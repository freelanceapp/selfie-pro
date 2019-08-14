package com.mojodigi.selfiepro.camera;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.mojodigi.selfiepro.AddsUtility.AddConstants;
import com.mojodigi.selfiepro.AddsUtility.AddMobUtils;
import com.mojodigi.selfiepro.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.selfiepro.R;
import com.mojodigi.selfiepro.activity.EditImageActivity;
import com.mojodigi.selfiepro.filters.FiltersListFragment;
import com.mojodigi.selfiepro.filters.FiltersPagerAdapter;
import com.mojodigi.selfiepro.utils.ColorFilterGenerator;
import com.mojodigi.selfiepro.utils.Constants;
import com.mojodigi.selfiepro.utils.CustomProgressDialog;
import com.mojodigi.selfiepro.utils.MyPreference;
import com.mojodigi.selfiepro.utils.Utilities;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class CameraActivity  extends AppCompatActivity implements FiltersListFragment.FiltersListFragmentListener, /*View.OnTouchListener,*/
        View.OnClickListener,  OnCameraEditTollsSelected, SeekBar.OnSeekBarChangeListener , OnCameraSubTollsSelected
{

    private Context mContext = null ;
    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final int REQUEST_TYPE_CAMERA = 101;

    static { System.loadLibrary("NativeImageProcessor"); }

    private ImageView    undoCameraImageView , redoCameraImageView;

    private PhotoView cameraSelectedImageShape , adjustCameraPhotoView;

    private TextView   undoCameraTextView , redoCameraTextView;

    private RelativeLayout cameraSelectedImageRLayout  , adjustCameraRLayout , adjustCameraPhotoRLayout ;
    private LinearLayout mCameraEditBackLLayout, undoCameraImageLayout , redoCameraImageLayout , discardCameraLayout, mCameraSaveLayout , cameraAdjustSeekbarLLayout , cameraSubToolsLayout ;
    private MyPreference mMyPrecfence = null;



    private RecyclerView cameraEditToolsRecycleView ;
    private CameraEditToolsAdapter mCameraEditToolsAdapter;
    private CameraSubToolsAdapter cameraSubToolsAdapter;

    private SeekBar cameraBrightness , cameraContrast ;

    public static CameraActivity instance;
    private RecyclerView  cameraSubToolsRecycler;



    private String pictureFilePath;


    private float angleRotation = 0;

    private LinearLayout  mCameraImageLLayout , cameraEffectsLLayout ,blankCameraLayoutTop, blankCameraLayoutBottom ;

    private int currentShowingIndex = 0;
    private Uri  mainPathUri  ;

    private ArrayList<Uri> pathForTempList  ;

    private int progressBright , progressContrast;

    private Bitmap originalImage , firstImage , initialBitmap;
    private Bitmap filteredImage;
    private FiltersListFragment filtersListFragment;
    private ViewPager filters_viewpager ;
    private Bitmap cropedBitmap ;

    private SharedPreferenceUtil addprefs;
    private View adContainer;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Constants.isAdjustCameraImage =false;
        Constants.isImageCroped =false;
        Constants.editImageUri = "false";

        instance = this;

        if (mContext == null) {
            mContext = CameraActivity.this;
        }
        if (mMyPrecfence == null) {
            mMyPrecfence = MyPreference.getMyPreferenceInstance(mContext);
        }

        pathForTempList = new ArrayList<Uri>();
        pathForTempList.clear();
        currentShowingIndex=0;
        deleteTempExtraFile();

        getIntentUri();

        initView();


        filters_viewpager = (ViewPager)findViewById(R.id.filters_viewpager);;
        setupViewPager(filters_viewpager);
        loadImageFilters();


        addprefs = new SharedPreferenceUtil(mContext);
        AddMobUtils adutil = new AddMobUtils();

        if(AddConstants.checkIsOnline(mContext) && adContainer !=null && addprefs !=null)
        {
            String AddPrioverId=addprefs.getStringValue(AddConstants.ADD_PROVIDER_ID, AddConstants.NOT_FOUND);
            if(AddPrioverId.equalsIgnoreCase(AddConstants.FaceBookAddProividerId))
            {
                adutil.dispFacebookBannerAdd(mContext,addprefs , CameraActivity.this);
            }
        }
        else {
            Log.e("","");
        }
    }

    private void getIntentUri() {
        Uri getedIntentUri = null;
        Intent extrasIntent = getIntent();
        if (extrasIntent != null) {
            getedIntentUri = extrasIntent.getParcelableExtra(Constants.URI_CAMERA);

            try {
                Bitmap  bitmap   = MediaStore.Images.Media.getBitmap(this.getContentResolver(), getedIntentUri);
                firstImage = bitmap;
                Constants.capturedImageBitmap = bitmap;
                setFiltersImage();

                Uri compressedUri = Uri.parse(compressImage(getedIntentUri.toString()));
                File imgFile = new  File(compressImage(compressedUri.toString()));
                Uri compressedUri2 ;
                 Bitmap compressedBitmap ;
                if(imgFile.exists()) {
                      compressedUri2 = Uri.fromFile(imgFile);
                    //Log.e("Camera File Uri " , compressedUri2+"");
                    compressedBitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), compressedUri2);
                    Constants.capturedImageBitmap = compressedBitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch ( Exception ex) {
                ex.printStackTrace();
            }
            //Log.e("On Create List " ,  pathForTempList.size()+"");
            //Log.e("On Create Index " ,  currentShowingIndex+"");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();


        adjustCameraRLayout.setVisibility(View.GONE);
        cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

        blankCameraLayoutBottom.setVisibility(View.VISIBLE);
        cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
        cameraEffectsLLayout.setVisibility(View.GONE);
        cameraSubToolsLayout.setVisibility(View.GONE);
        cameraSubToolsRecycler.setVisibility(View.GONE);

        cameraSelectedImageShape.setLayoutParams(
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ) );
        cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if(Constants.editImageUri.equalsIgnoreCase("true")) {
            CustomProgressDialog.show(mContext,getResources().getString(R.string.loading_msg));
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Constants.imageUri);
                Constants.capturedImageBitmap = bitmap;
                setFiltersImage();

                cameraSelectedImageShape.setLayoutParams(
                        new android.widget.RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                        ) );

                cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

                cameraSelectedImageShape.setImageBitmap(originalImage);

                cameraSelectedImageShape.setRotation(0);
                fixAntiAlias(cameraSelectedImageShape);

                //cameraBrightness.setProgress(0);
                //cameraContrast.setProgress(0);

                addToUndoReodList();
                hideShowUndoRedo();

                CustomProgressDialog.dismiss();

            } catch (IOException e) {
                e.printStackTrace();
            }catch ( Exception ex) {
                ex.printStackTrace();
            }
        }
        Constants.editImageUri = "false";
        Constants.isAdjustCameraImage =false;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        adContainer = findViewById(R.id.adMobView);
        cameraSelectedImageRLayout = (RelativeLayout) findViewById(R.id.cameraSelectedImageRLayout);
        cameraSelectedImageRLayout.setVisibility(View.VISIBLE);


        adjustCameraRLayout = (RelativeLayout) findViewById(R.id.adjustCameraRLayout);
        adjustCameraRLayout.setVisibility(View.GONE);
        adjustCameraPhotoRLayout = (RelativeLayout) findViewById(R.id.adjustCameraPhotoRLayout);
        adjustCameraPhotoView = (PhotoView) findViewById(R.id.adjustCameraPhotoView);
        adjustCameraPhotoView.setRotation(0);
        fixAntiAlias(adjustCameraPhotoView);

        cameraSelectedImageShape = (PhotoView) findViewById(R.id.cameraSelectedImageShape);
        cameraSelectedImageShape.setLayoutParams(
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ) );
        cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

        cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);
        //cameraSelectedImageShape.setColorFilter(Utilities.setBrightness(110));
        cameraSelectedImageShape.setOnClickListener(this);
        cameraSelectedImageShape.setRotation(0);
        fixAntiAlias(cameraSelectedImageShape);


        mCameraImageLLayout = (LinearLayout) findViewById(R.id.idCameraImageLLayout);
        cameraEffectsLLayout = (LinearLayout) findViewById(R.id.cameraEffectsLLayout);

        blankCameraLayoutTop = (LinearLayout) findViewById(R.id.blankCameraLayoutTop);
        blankCameraLayoutTop.setVisibility(View.VISIBLE);
        blankCameraLayoutTop.setOnClickListener(this);
        blankCameraLayoutBottom = (LinearLayout) findViewById(R.id.blankCameraLayoutBottom);
        blankCameraLayoutBottom.setVisibility(View.VISIBLE);
        blankCameraLayoutBottom.setOnClickListener(this);

        mCameraEditToolsAdapter = new CameraEditToolsAdapter(this);
        cameraSubToolsAdapter = new CameraSubToolsAdapter(this);

        mCameraEditBackLLayout = (LinearLayout) findViewById(R.id.idCameraEditBackLLayout);

        undoCameraImageLayout = (LinearLayout) findViewById(R.id.undoCameraImageLayout);
        undoCameraImageView = (ImageView) findViewById(R.id.undoCameraImageView);
        undoCameraTextView = (TextView) findViewById(R.id.undoCameraTextView);

        redoCameraImageLayout = (LinearLayout) findViewById(R.id.redoCameraImageLayout);
        redoCameraImageView = (ImageView) findViewById(R.id.redoCameraImageView);
        redoCameraTextView = (TextView) findViewById(R.id.redoCameraTextView);

        discardCameraLayout = (LinearLayout) findViewById(R.id.discardCameraLayout);

        mCameraSaveLayout = (LinearLayout) findViewById(R.id.idCameraSaveLayout);
        cameraSubToolsLayout = (LinearLayout) findViewById(R.id.cameraSubToolsLayout);

        cameraAdjustSeekbarLLayout = (LinearLayout) findViewById(R.id.cameraAdjustSeekbarLLayout);
        cameraAdjustSeekbarLLayout.setOnClickListener(this);
        mCameraEditBackLLayout.setOnClickListener(this);
        mCameraSaveLayout.setOnClickListener(this);
        undoCameraImageLayout.setOnClickListener(this);
        redoCameraImageLayout.setOnClickListener(this);
        discardCameraLayout.setOnClickListener(this);

        cameraBrightness = (SeekBar)findViewById(R.id.cameraBrightness);
        cameraBrightness.setOnSeekBarChangeListener(this);

        cameraContrast = (SeekBar)findViewById(R.id.cameraContrast);
        cameraContrast.setOnSeekBarChangeListener(this);

        cameraEditToolsRecycleView = (RecyclerView) findViewById(R.id.cameraEditToolsRecycleView);
        cameraEditToolsRecycleView.setVisibility(View.VISIBLE);

        LinearLayoutManager mCollageEditToolsLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        cameraEditToolsRecycleView.setLayoutManager(mCollageEditToolsLManager);
        cameraEditToolsRecycleView.setAdapter(mCameraEditToolsAdapter);

        cameraSubToolsRecycler = (RecyclerView) findViewById(R.id.cameraSubToolsRecycler);

        LinearLayoutManager mcameraSubToolsAdapterLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        cameraSubToolsRecycler.setLayoutManager(mcameraSubToolsAdapterLManager);
        cameraSubToolsRecycler.setAdapter(cameraSubToolsAdapter);

        addToUndoReodList();
        hideShowUndoRedo();

    }




    private void loadImageFilters( ) {
        originalImage = Constants.capturedImageBitmap ;
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        //finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    public void setFiltersImage() {
        originalImage = Constants.capturedImageBitmap;
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        //finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        filters_viewpager =(ViewPager) findViewById(R.id.filters_viewpager);
        setupViewPager(filters_viewpager);
    }

    private void setupViewPager(ViewPager viewPager) {
        FiltersPagerAdapter adapter = new FiltersPagerAdapter(getSupportFragmentManager());
        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);
        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onFilterSelected(Filter filter) {
        CustomProgressDialog.show(mContext,getResources().getString(R.string.loading_msg));
        originalImage = Constants.capturedImageBitmap;
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        //finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
        cameraSelectedImageShape.setImageBitmap(filter.processFilter(filteredImage));
        addToUndoReodList();
        CustomProgressDialog.dismiss();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.idCameraEditBackLLayout:

                if(Constants.isAdjustCameraImage){
                    Constants.capturedImageBitmap = createBitmapFromLayout(adjustCameraPhotoRLayout);
                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    adjustCameraRLayout.setVisibility(View.GONE);
                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

                    blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;
                }

                adjustCameraRLayout.setVisibility(View.GONE);
                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

                blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                show_alert_back("Exit", "Are you sure you want to exit Editor ?");
                break;


            case R.id.undoCameraImageLayout:
                Constants.isAdjustCameraImage = false;

                adjustCameraRLayout.setVisibility(View.GONE);
                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

                blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);

                Log.e("Undo List Size", pathForTempList.size()+"");
                Log.e("Undo Index " ,  currentShowingIndex+"");

                if(currentShowingIndex>pathForTempList.size()){
                    currentShowingIndex = currentShowingIndex-1;
                    return;
                }

                hideShowUndoRedo();
                if(currentShowingIndex >=1) {
                    onUndoPressed( );
                }else {Log.e("" ,"");}

                break;



            case R.id.redoCameraImageLayout:
                Constants.isAdjustCameraImage = false;
                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                adjustCameraRLayout.setVisibility(View.GONE);

                blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);

                Log.e("Redo List Size", pathForTempList.size()+"");
                Log.e("Redo Index " ,  currentShowingIndex+"");
                if(currentShowingIndex>pathForTempList.size()){
                    currentShowingIndex = currentShowingIndex-1;
                    return;
                }
                hideShowUndoRedo();
                if(currentShowingIndex < pathForTempList.size()-1) {
                    onRedoPressed();
                }else {Log.e("" ,"");}

                break;

            case R.id.idCameraSaveLayout:

                if(Constants.isAdjustCameraImage){

                    Constants.capturedImageBitmap = createBitmapFromLayout(adjustCameraPhotoRLayout);

                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    adjustCameraRLayout.setVisibility(View.GONE);
                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

                    blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;
                }

                adjustCameraRLayout.setVisibility(View.GONE);
                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

                blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);


                cameraSelectedImageShape.setLayoutParams(
                        new android.widget.RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ) );
                cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

                if(cameraSelectedImageShape.getRotation()>0){
                    Bitmap bitmap = createBitmapFromLayout(cameraSelectedImageRLayout);
                    Utilities.saveSelfieProImage(mContext , bitmap);
                }else if(cameraSelectedImageShape.getRotation()==0) {
                   Bitmap bitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();
                   Utilities.saveSelfieProImage(mContext , bitmap);
                }

                cameraSelectedImageShape.setLayoutParams(
                        new android.widget.RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                        ) );
                cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

                if(addprefs!=null)
                    dispInterestialAdds();

                break;

            case R.id.blankCameraLayoutTop:
                if(Constants.isAdjustCameraImage){
                    Constants.capturedImageBitmap = createBitmapFromLayout(adjustCameraPhotoRLayout);
                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    adjustCameraRLayout.setVisibility(View.GONE);
                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

                    blankCameraLayoutTop.setVisibility(View.VISIBLE);
                    blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);

                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;
                }

                adjustCameraRLayout.setVisibility(View.GONE);
                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

                blankCameraLayoutTop.setVisibility(View.VISIBLE);
                blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                break;

            case R.id.blankCameraLayoutBottom:
                if(Constants.isAdjustCameraImage){
                    Constants.capturedImageBitmap = createBitmapFromLayout(adjustCameraPhotoRLayout);
                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    adjustCameraRLayout.setVisibility(View.GONE);
                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

                    blankCameraLayoutTop.setVisibility(View.VISIBLE);
                    blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;
                }

                adjustCameraRLayout.setVisibility(View.GONE);
                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

                blankCameraLayoutTop.setVisibility(View.VISIBLE);
                blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                break;
        }
    }

    private void dispInterestialAdds() {
        AddMobUtils addutil = new AddMobUtils();
        if (AddConstants.checkIsOnline(mContext) && addprefs != null) {

            String addPrioverId = addprefs.getStringValue(AddConstants.ADD_PROVIDER_ID, AddConstants.NOT_FOUND);
            if (addPrioverId.equalsIgnoreCase(AddConstants.FaceBookAddProividerId)) {
                addutil.dispFacebookInterestialAdds(mContext, addprefs);
            }
        } else
            Log.e("", "");
    }

    /**************************onActivityResult Start************************************/
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_TYPE_CAMERA:
                if (resultCode == RESULT_OK) {
                    File imgFile = new File(pictureFilePath);
                    if (imgFile.exists()) {
                        Constants.imageUri = Uri.fromFile(imgFile);
                        //Uri gotedUri = Uri.fromFile(imgFile);
                        //Constants.capturedImagePath = imgFile.getAbsolutePath();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Constants.imageUri);
                            Constants.capturedImageBitmap = bitmap;
                            setFiltersImage();
                            Uri compressedUri = Uri.parse(compressImage(Constants.imageUri.toString()));
                            File compressFile = new  File(compressImage(compressedUri.toString()));
                            Uri compressedUri2 ;
                            Bitmap compressedBitmap ;
                            if(compressFile.exists()) {
                                compressedUri2 = Uri.fromFile(compressFile);
                                //Log.e("Camera File Uri " , compressedUri2+"");
                                compressedBitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), compressedUri2);
                                Constants.capturedImageBitmap = compressedBitmap;
                            }

                            cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);
                            //cameraSelectedImageShape.setColorFilter(Utilities.setBrightness(110));
                            cameraSelectedImageShape.setRotation(0);
                            fixAntiAlias(cameraSelectedImageShape);


                            cameraBrightness.setProgress(0);
                            cameraContrast.setProgress(0);

                            deleteTempExtraFile();
                            addToUndoReodList();
                            hideShowUndoRedo();
                            Constants.isImageCroped = false;
                            Constants.isAdjustCameraImage=false;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }else   {
                    //Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("you have not" , " clicked image.");
                }
                break;


            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    handleUCropResult(data);
                } else {
                    setResultCancelled();
                }
                break;

            case UCrop.RESULT_ERROR:
                //final Throwable cropError = UCrop.getError(data);
                // Log.e(TAG, "Crop error: " + cropError);
                setResultCancelled();
                break;
            default:
                setResultCancelled();

        }
    }



    private void handleUCropResult(Intent data) {
        if (data == null) {
            //Toast.makeText(this, "data", Toast.LENGTH_SHORT).show();
            setResultCancelled();
            return;
        }
        final Uri resultUri = UCrop.getOutput(data);
        okResult(resultUri);
        //Toast.makeText(this, "handleUCropResult", Toast.LENGTH_SHORT).show();
    }

    private void okResult(Uri imagePath) {
        if (imagePath != null) {
            Constants.imageUri = imagePath;
            //Constants.capturedImagePath = imagePath.getPath();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Constants.imageUri);
                Constants.capturedImageBitmap = bitmap;
                cropedBitmap = bitmap;

                Constants.cameraEditBitmapWidth =  cropedBitmap.getWidth();
                Constants.cameraEditBitmapHeight =  cropedBitmap.getHeight();

                setFiltersImage();

                cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
                cameraSelectedImageShape.setImageBitmap(bitmap);
                //cameraSelectedImageShape.setImageURI(Constants.imageUri);
                //cameraSelectedImageShape.setRotation(0);
                fixAntiAlias(cameraSelectedImageShape);
                addToUndoReodList();
                hideShowUndoRedo();

                Constants.isImageCroped = true;
                Constants.isAdjustCameraImage=false;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void setResultCancelled() {
        Log.e("Crop Result" , " Cancelled ");
    }

    /**************Select Tolls  **********************/
    @Override
    public void onCameraEditTollsSelected(CameraEditToolsType cameraEditToolsType) {
        switch (cameraEditToolsType) {

            case CAMERA:

                if(Constants.isAdjustCameraImage){
                    Constants.capturedImageBitmap = createBitmapFromLayout(adjustCameraPhotoRLayout);
                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    adjustCameraRLayout.setVisibility(View.GONE);
                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

                    blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;

                }

                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                adjustCameraRLayout.setVisibility(View.GONE);


                blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);

                if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                    sendTakePictureIntent();
                }
                deleteTempExtraFile();

                break;

            case TOOLS:
                if(Constants.isAdjustCameraImage){
                    Constants.capturedImageBitmap = createBitmapFromLayout(adjustCameraPhotoRLayout);
                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    adjustCameraRLayout.setVisibility(View.GONE);
                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

                    blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;

                }

                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                adjustCameraRLayout.setVisibility(View.GONE);

                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                Constants.capturedImageBitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();
                if(cameraSubToolsRecycler.getVisibility() == View.VISIBLE ){
                    blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                }else {
                    blankCameraLayoutBottom.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.VISIBLE);
                    cameraSubToolsRecycler.setVisibility(View.VISIBLE);
                }
                break;

            case EFFECTS:

                if(Constants.isAdjustCameraImage){
                    Constants.capturedImageBitmap = createBitmapFromLayout(adjustCameraPhotoRLayout);
                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    adjustCameraRLayout.setVisibility(View.GONE);
                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

                    blankCameraLayoutTop.setVisibility(View.VISIBLE);
                    blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;
                }

                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                adjustCameraRLayout.setVisibility(View.GONE);

                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                //Bitmap  bitmap  = createBitmapFromLayout(cameraSelectedImageRLayout);
                Constants.capturedImageBitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();

                setFiltersImage();

                if(cameraEffectsLLayout.getVisibility() == View.VISIBLE ){
                    blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                }else {
                    blankCameraLayoutBottom.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.VISIBLE);
                    cameraEffectsLLayout.setVisibility(View.VISIBLE);
                }
                break;

            case ADJUST:

                if (!Constants.isAdjustCameraImage) {
                    cameraSelectedImageShape.setLayoutParams(
                            new android.widget.RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            ) );
                    cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    Constants.capturedImageBitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();

                    adjustCameraPhotoView.setLayoutParams(
                            new android.widget.RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            ) );
                    adjustCameraPhotoView.setScaleType(ImageView.ScaleType.FIT_XY);

                    adjustCameraPhotoView.setImageBitmap(Constants.capturedImageBitmap);
                    adjustCameraPhotoView.setRotation(0);
                    fixAntiAlias(adjustCameraPhotoView);

                    cameraSelectedImageRLayout.setVisibility(View.GONE);
                    adjustCameraRLayout.setVisibility(View.VISIBLE);

                    Constants.isAdjustCameraImage = true;

                    cameraSelectedImageShape.setLayoutParams(
                            new android.widget.RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                            ) );
                    cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }


                cameraEffectsLLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                //Bitmap  bitmap  = createBitmapFromLayout(cameraSelectedImageRLayout);
                Constants.capturedImageBitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();
                //addToUndoReodList();
                if(cameraAdjustSeekbarLLayout.getVisibility() == View.VISIBLE ){
                    blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                }else {
                    blankCameraLayoutBottom.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                }
                break;


            case EDIT:

                if(Constants.isAdjustCameraImage){

                    Constants.capturedImageBitmap = createBitmapFromLayout(adjustCameraPhotoRLayout);

                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    adjustCameraRLayout.setVisibility(View.GONE);
                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

                    blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;

                }

                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                adjustCameraRLayout.setVisibility(View.GONE);


                blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);

                cameraSelectedImageShape.setLayoutParams(
                        new android.widget.RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ) );
                cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

                Constants.capturedImageBitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();
                //Log.e("Width ", cameraSelectedImageShape.getWidth() + "");
                //Log.e("Height ", cameraSelectedImageShape.getHeight() + "");

                mMyPrecfence.saveString(Constants.INTENT_TYPE, Constants.INTENT_TYPE_CAMERA);
                try {
                    //Bitmap mBitmap = createBitmapFromLayout(cameraSelectedImageRLayout);

                    Bitmap mBitmap = createBitmapFromLayout(cameraSelectedImageShape);
                    //ImageView End
                    //Constants.galleryEditBitmapHeight  = cameraSelectedImageShape.getHeight();
                    //Constants.galleryEditBitmapWidth  = cameraSelectedImageShape.getWidth();

                    String imagePath =  Utilities.saveBitmap_Temp(mBitmap);
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        Constants.imageUri = Uri.fromFile(imgFile);
                    }

                    Constants.done_Edited_ImageType_Collage ="false";
                    Intent intentCameraSelectedImage = new Intent(CameraActivity.this, EditImageActivity.class);
                    intentCameraSelectedImage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intentCameraSelectedImage.putExtra(Constants.URI_CAMERA, Constants.imageUri);
                    startActivity(intentCameraSelectedImage);

                    cameraSelectedImageShape.setLayoutParams(
                            new android.widget.RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            ) );

                    cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);



                    Constants.capturedImageBitmap = ((BitmapDrawable)cameraSelectedImageShape.getDrawable()).getBitmap();
                    //Constants.capturedImageBitmap =  createBitmapFromLayout(mGalleryRLayout);

                    Constants.cameraEditBitmap = Constants.capturedImageBitmap;
                    //Bitmap layoutBitmap = createBitmapFromLayout(mGalleryRLayout);
                    Constants.cameraEditBitmapHeight  = Constants.cameraEditBitmap.getHeight();
                    Constants.cameraEditBitmapWidth  = Constants.cameraEditBitmap.getWidth();

                    //Bitmap layoutBitmap = createBitmapFromLayout(cameraSelectedImageShape);
//                    Bitmap layoutBitmap = createBitmapFromLayout(mGalleryRLayout);
//                    Constants.galleryEditBitmapHeight  = layoutBitmap.getHeight();
//                    Constants.galleryEditBitmapWidth  = layoutBitmap.getWidth();

                    cameraSelectedImageShape.setLayoutParams(
                            new android.widget.RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                            ) );
                    cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    /**************Select Sub-Tolls  **********************/

    @Override
    public void onCameraSubTollsSelected(CameraSubToolsType cameraSubToolsType) {
        switch (cameraSubToolsType) {

            case CAMERA_CROP:

                blankCameraLayoutBottom.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);

                //Bitmap bitmap = createBitmapFromLayout(cameraSelectedImageRLayout);
                Bitmap bitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();
                Constants.capturedImageBitmap = bitmap ;

                if(bitmap!=null  ) {
                    cropImageUri(getImageUri(mContext, bitmap));
                }else  {
                    //Toast.makeText(this , "Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("you have not" , " clicked image.");
                }
                break;

            case CAMERA_RT_LEFT:
                angleRotation = angleRotation - 10;
                cameraSelectedImageShape.setRotation(angleRotation);
                break;

            case CAMERA_RT_RIGHT:
                angleRotation = angleRotation + 10;
                cameraSelectedImageShape.setRotation(angleRotation);
                break;

            case CAMERA_RT_NONE:
                angleRotation=0;
                if (cameraSelectedImageShape != null) {
                    cameraSelectedImageShape.setRotation(0);
                }
                fixAntiAlias(cameraSelectedImageShape);

                break;

            case CAMERA_FLIP:

                cameraSelectedImageShape.setImageBitmap(flipImage(((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap()));
                addToUndoReodList();
                //Bitmap flipBitmap = createBitmapFromLayout(cameraSelectedImageRLayout);
                Constants.capturedImageBitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();
                setFiltersImage();
                break;
        }
    }


    /************************Brightness / Contrast Start*******************/
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if (adjustCameraPhotoView != null) {
            if (seekBar.getId() == R.id.cameraBrightness) {
                increaseBrightness(adjustCameraPhotoView, progress);
                progressBright = progress;
            }
            if (seekBar.getId() == R.id.cameraContrast) {
                increaseContrast(adjustCameraPhotoView, progress);
                progressContrast = progress;

            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //addToUndoReodList();
    }

    private void increaseBrightness(ImageView mImageView, int progressValue){
        mImageView.setColorFilter(ColorFilterGenerator.adjustBrightness(progressValue));
    }
    private void increaseContrast(ImageView mImageView, int progressValue){
        mImageView.setColorFilter(ColorFilterGenerator.adjustContrast(progressValue));
    }


    /************************Brightness / Contrast End*******************/


    /*****************Undo/Redo Start****************/

    private void addToUndoReodList(){

        hideShowUndoRedo();

        Bitmap mainBitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();

        String imagePath =  Utilities.saveBitmap_Temp(mainBitmap);
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Constants.imageUri = Uri.fromFile(imgFile);
            mainPathUri = Constants.imageUri ;
            //Log.e("added Undo/Reod Uri ", Constants.imageUri+"");
        }

        pathForTempList.add(mainPathUri);
        currentShowingIndex = currentShowingIndex+1;

        //Log.e("added List Size", pathForTempList.size()+"");
        //Log.e("Index " ,  currentShowingIndex+"");

        hideShowUndoRedo();
    }

    private void onUndoPressed( ) {
        String mUri = getUndoPath();
        File imgFile = new File(mUri);
        if (imgFile.exists()) {
            Constants.imageUri = Uri.fromFile(imgFile);
            mainPathUri = Constants.imageUri ;
        }
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mainPathUri);
            Constants.capturedImageBitmap = bitmap;

            cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
            cameraSelectedImageShape.setImageBitmap(bitmap);
            cameraSelectedImageShape.setRotation(0);
            fixAntiAlias(cameraSelectedImageShape);

            hideShowUndoRedo();

            increaseBrightness(cameraSelectedImageShape, 0);
            increaseContrast(cameraSelectedImageShape, 0);
            cameraBrightness.setProgress(0);
            cameraContrast.setProgress(0);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private String getUndoPath() {
        if(currentShowingIndex >=1 && currentShowingIndex!=pathForTempList.size()) {
            currentShowingIndex = currentShowingIndex - 1;
        }
        else if(currentShowingIndex > 1 && currentShowingIndex==pathForTempList.size() ){
            currentShowingIndex = currentShowingIndex - 2;}
        else {
            currentShowingIndex =0;
        }
        // Log.e("getUndoPath ", currentShowingIndex + "");
        return pathForTempList.get(currentShowingIndex).getPath();
    }

    private void onRedoPressed( ) {
        String   mUri = getRedoPath();
        File imgFile = new File(mUri);
        if (imgFile.exists()) {
            Constants.imageUri = Uri.fromFile(imgFile);
            mainPathUri = Constants.imageUri ;
        }
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mainPathUri);
            Constants.capturedImageBitmap = bitmap;

            cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
            cameraSelectedImageShape.setImageBitmap(bitmap);
            cameraSelectedImageShape.setRotation(0);
            fixAntiAlias(cameraSelectedImageShape);

            hideShowUndoRedo();

            increaseBrightness(cameraSelectedImageShape, 0);
            increaseContrast(cameraSelectedImageShape, 0);
            cameraBrightness.setProgress(0);
            cameraContrast.setProgress(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getRedoPath() {
        currentShowingIndex  = currentShowingIndex+1 ;
        //  Log.e("getRedoPath ", currentShowingIndex + "");
        return pathForTempList.get(currentShowingIndex).getPath();
    }

    private void hideShowUndoRedo(){
        Log.e("Hide Show Size ", pathForTempList.size() + "");
        Log.e("Hide Show Index ", currentShowingIndex + "");
        if(pathForTempList.size()<=1)
        {
            hideUndo();
            hideRedo();
        }
        if(pathForTempList.size()>1 &&  currentShowingIndex==0 )
        {
            hideUndo();
            showRedo();
        }
        if(pathForTempList.size()>1 && currentShowingIndex!=0 && currentShowingIndex!=pathForTempList.size()-1 )
        {
            showUndo();
            showRedo();
        }
        if(pathForTempList.size()>1 && currentShowingIndex==pathForTempList.size()-1){
            showUndo();
            hideRedo();
        }
        if(pathForTempList.size()>1 && currentShowingIndex==pathForTempList.size()){
            showUndo();
            hideRedo();
        }
        if(pathForTempList.size()>1 && currentShowingIndex>pathForTempList.size()){
            showUndo();
            hideRedo();
        }


    }


    private void showUndo() {
        undoCameraImageView.setImageResource(R.drawable.ic_undo);
        undoCameraTextView.setTextColor(getResources().getColor(R.color.color_icon));
    }
    private void hideUndo() {
        undoCameraImageView.setImageResource(R.drawable.ic_undo_none);
        undoCameraTextView.setTextColor(getResources().getColor(R.color.undo_redo_none));
    }
    private void showRedo() {
        redoCameraImageView.setImageResource(R.drawable.ic_redo);
        redoCameraTextView.setTextColor(getResources().getColor(R.color.color_icon));
    }
    private void hideRedo() {
        redoCameraImageView.setImageResource(R.drawable.ic_redo_none);
        redoCameraTextView.setTextColor(getResources().getColor(R.color.undo_redo_none));
    }

    /*****************Undo/Redo End****************/


    //Method to do flip action
    public  Bitmap flipImage(Bitmap image_bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        Bitmap flipped_bitmap = Bitmap.createBitmap(image_bitmap, 0, 0, image_bitmap.getWidth(), image_bitmap.getHeight(), matrix, true);

        return flipped_bitmap;
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public Bitmap createBitmapFromLayout(View view){
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, view.getWidth(), view.getHeight());
        Canvas mBitCanvas = new Canvas(bitmap);
        view.draw(mBitCanvas);
        return bitmap;
    }

    private void sendTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra( MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File pictureFile = null;
            try {
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,getPackageName() +".provider",  pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_TYPE_CAMERA);
            }
        }
    }

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName,  ".jpg",    storageDir );
        // Save a file: path for use with ACTION_VIEW intents
        pictureFilePath = image.getAbsolutePath();
        return image;
    }




    /*************************Crop Image Start**********************************/
    private void cropImageUri(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), queryName(getContentResolver(), sourceUri)));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(100);

        // applying UI theme
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary));

        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .start(this);
    }


    private static String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }
    /*************************Crop Image End**********************************/


    public void show_alert_back(String title, String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CameraActivity.this);
        // set title
        alertDialogBuilder.setTitle(title);
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(true)
                .setIcon(R.mipmap.ic_launcher_round).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //Bitmap bitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();
                Bitmap bitmap = createBitmapFromLayout(cameraSelectedImageRLayout);
                Utilities.saveSelfieProImage(mContext , bitmap);

                finish();
            }
        });



        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        try {
            alertDialog.show();
        } catch (Exception e) {
            alertDialog.dismiss();
        }

        Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button b1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        if (b != null)
            b.setTextColor(getResources().getColor(R.color.green_color_picker));
        if (b1 != null)
            b1.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void deleteTempExtraFile()
    {
        try {
            File directory = new File(Constants.tempfolder);
            File[] fList = directory.listFiles();
            Log.e("Files are ", fList+"");
            for (File file : fList) {
                if (file.isFile() && file.exists()) {
                    file.delete();
                }
                else{
                    Log.e("Files are ", " not available.");
                }
            }
            pathForTempList.clear();
            currentShowingIndex=0;
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }



    @SuppressLint("ObsoleteSdkInt")
    public void fixAntiAlias(View viewAntiAlias) {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            viewAntiAlias.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (Build.VERSION.SDK_INT > 10) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
            viewAntiAlias.setLayerType(View.LAYER_TYPE_SOFTWARE, p);
            ((View) viewAntiAlias.getParent()).setLayerType(View.LAYER_TYPE_SOFTWARE, p);
        }
    }


    /*************Compress Image Start************/
    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
    /*************Compress Image End************/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //finish();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        show_alert_back("Exit", "You want to save changes ?");
        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        deleteTempExtraFile();
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private  int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return  width;
    }

    private  int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.heightPixels;
        return  width;
    }

}

