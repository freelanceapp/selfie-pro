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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.mojodigi.selfiepro.adjust.AdjustToolsAdapter;
import com.mojodigi.selfiepro.adjust.AdjustToolsType;
import com.mojodigi.selfiepro.adjust.OnAdjustTollsSelected;
import com.mojodigi.selfiepro.filters.EditImageListener;
import com.mojodigi.selfiepro.filters.FiltersListFragment;
import com.mojodigi.selfiepro.filters.FiltersPagerAdapter;
import com.mojodigi.selfiepro.utils.Constants;
import com.mojodigi.selfiepro.utils.CustomProgressDialog;
import com.mojodigi.selfiepro.utils.MyPreference;
import com.mojodigi.selfiepro.utils.Utilities;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.geometry.Point;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ColorOverlaySubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;
import com.zomato.photofilters.imageprocessors.subfilters.ToneCurveSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.VignetteSubfilter;

import net.alhazmy13.imagefilter.ImageFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class CameraActivity  extends AppCompatActivity implements OnAdjustTollsSelected,EditImageListener,FiltersListFragment.FiltersListFragmentListener,

        View.OnClickListener,  OnCameraEditTollsSelected, SeekBar.OnSeekBarChangeListener , OnCameraSubTollsSelected
{

    private static final String TAG = CameraActivity.class.getSimpleName();
    private Context mContext = null ;

    private static final int REQUEST_TYPE_CAMERA = 101;



    private ImageView  undoCameraImageView , redoCameraImageView;

    private PhotoView cameraSelectedImageShape /*, adjustCameraPhotoView*/;

    private TextView   undoCameraTextView , redoCameraTextView;

    private RelativeLayout cameraSelectedImageRLayout      ;
    private LinearLayout backCameraLLayout, undoCameraImageLayout , redoCameraImageLayout , discardCameraLayout,
            mCameraSaveLayout , cameraAdjustSeekbarLLayout , cameraSubToolsLayout ;

    private MyPreference mMyPrecfence = null;



    private RecyclerView cameraSubToolsRecycler , cameraEditToolsRecycleView  , cameraAdjustToolsRecycler ;

    private CameraEditToolsAdapter mCameraEditToolsAdapter;
    private CameraSubToolsAdapter cameraSubToolsAdapter;
    private AdjustToolsAdapter adjustToolsAdapter;

    private SeekBar cameraBrightnessSeekBar, cameraContrastSeekBar , cameraSaturationSeekBar ,cameraSharpnessSeekBar
            , cameraVignetteSeekBar , cameraShadowSeekBar , cameraHighlightSeekBar
            , cameraTempSeekBar , cameraTintSeekBar ,  cameraDenoiseSeekBar , cameraCurveSeekBar, cameraColorBalanceSeekBar;


    public static CameraActivity instance;
    static { System.loadLibrary("NativeImageProcessor"); }




    private String pictureFilePath;


    private float angleRotation = 0;

    private LinearLayout  mCameraImageLLayout , cameraEffectsLLayout ,blankCameraLayoutTop, cameraBlankLayoutBottom ;

    private int currentShowingIndex = 0;
    private Uri  mainPathUri  ;

    private ArrayList<Uri> pathForTempList  ;

   //  private int progressBright , progressContrast;

    private Bitmap originalImage , firstImage , initialBitmap;
    private Bitmap filteredImage;
    private FiltersListFragment filtersListFragment;
    private ViewPager filters_viewpager ;
    private Bitmap cropedBitmap ;

    private SharedPreferenceUtil addprefs;
    private View adContainer;



    Bitmap finalImage;
    // modified image values
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;


    private TextView adjustClose  ,  adjustDone;
    private LinearLayoutManager mAdjustToolsAdapterLManager;

    public static final double PI = 3.14159d;
    public static final double FULL_CIRCLE_DEGREE = 360d;
    public static final double HALF_CIRCLE_DEGREE = 180d;
    public static final double RANGE = 256d;

    final static int KERNAL_WIDTH = 3;
    final static int KERNAL_HEIGHT = 3;

    int[][] SharpConfig ={
            {0, -1, 0},
            {-1, 5, -1},
            {0, -1, 0}
    };
    double[][] SharpConfig2 = new double[][] {
            { 0 , -2    , 0  },
            { -2, 11, -2 },
            { 0 , -2    , 0  }
    };


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (instance == null) {
            instance = CameraActivity.this;
        }
        if (mContext == null) {
            mContext = CameraActivity.this;
        }



        Constants.isAdjustCameraImage =false;
        Constants.isImageCroped =false;
        Constants.editImageUri = "false";
        Constants.isAdjustSelected = false;



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


        cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
        cameraEditToolsRecycleView.setVisibility(View.VISIBLE);

        cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
        cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
        cameraSubToolsLayout.setVisibility(View.GONE);
        cameraSubToolsRecycler.setVisibility(View.GONE);
        cameraAdjustToolsRecycler.setVisibility(View.GONE);
        cameraEffectsLLayout.setVisibility(View.GONE);


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
                //Constants.cameraOrignalBitmap = originalImage;
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
        Constants.isAdjustSelected = false;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        adContainer = findViewById(R.id.adMobView);
        cameraSelectedImageRLayout = (RelativeLayout) findViewById(R.id.cameraSelectedImageRLayout);
        cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

        cameraSelectedImageShape = (PhotoView) findViewById(R.id.cameraSelectedImageShape);
        cameraSelectedImageShape.setLayoutParams(
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ) );
        cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Constants.cameraOrignalBitmap = Constants.capturedImageBitmap;
        cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);
        cameraSelectedImageShape.setColorFilter(Utilities.setBrightness(110));
        cameraSelectedImageShape.setOnClickListener(this);
        cameraSelectedImageShape.setRotation(0);
        fixAntiAlias(cameraSelectedImageShape);

        backCameraLLayout = (LinearLayout) findViewById(R.id.backCameraLLayout);
        backCameraLLayout.setOnClickListener(this);

        mCameraImageLLayout = (LinearLayout) findViewById(R.id.idCameraImageLLayout);
        cameraEffectsLLayout = (LinearLayout) findViewById(R.id.cameraEffectsLLayout);

        blankCameraLayoutTop = (LinearLayout) findViewById(R.id.blankCameraLayoutTop);
        blankCameraLayoutTop.setVisibility(View.VISIBLE);
        blankCameraLayoutTop.setOnClickListener(this);
        cameraBlankLayoutBottom = (LinearLayout) findViewById(R.id.cameraBlankLayoutBottom);
        cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
        cameraBlankLayoutBottom.setOnClickListener(this);

        mCameraEditToolsAdapter = new CameraEditToolsAdapter(this);
        cameraSubToolsAdapter = new CameraSubToolsAdapter(this);



        undoCameraImageLayout = (LinearLayout) findViewById(R.id.undoCameraImageLayout);
        undoCameraImageLayout.setOnClickListener(this);
        undoCameraImageView = (ImageView) findViewById(R.id.undoCameraImageView);
        undoCameraTextView = (TextView) findViewById(R.id.undoCameraTextView);

        redoCameraImageLayout = (LinearLayout) findViewById(R.id.redoCameraImageLayout);
        redoCameraImageLayout.setOnClickListener(this);

        redoCameraImageView = (ImageView) findViewById(R.id.redoCameraImageView);
        redoCameraTextView = (TextView) findViewById(R.id.redoCameraTextView);

        discardCameraLayout = (LinearLayout) findViewById(R.id.discardCameraLayout);
        discardCameraLayout.setOnClickListener(this);
        mCameraSaveLayout = (LinearLayout) findViewById(R.id.idCameraSaveLayout);
        mCameraSaveLayout.setOnClickListener(this);

        cameraSubToolsLayout = (LinearLayout) findViewById(R.id.cameraSubToolsLayout);
        cameraSubToolsLayout.setVisibility(View.GONE);

        cameraAdjustSeekbarLLayout = (LinearLayout) findViewById(R.id.cameraAdjustSeekbarLLayout);
        cameraAdjustSeekbarLLayout.setOnClickListener(this);

        cameraEditToolsRecycleView = (RecyclerView) findViewById(R.id.cameraEditToolsRecycleView);
        cameraEditToolsRecycleView.setVisibility(View.VISIBLE);

        LinearLayoutManager mCollageEditToolsLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        cameraEditToolsRecycleView.setLayoutManager(mCollageEditToolsLManager);
        cameraEditToolsRecycleView.setAdapter(mCameraEditToolsAdapter);

        cameraSubToolsRecycler = (RecyclerView) findViewById(R.id.cameraSubToolsRecycler);

        LinearLayoutManager mcameraSubToolsAdapterLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        cameraSubToolsRecycler.setLayoutManager(mcameraSubToolsAdapterLManager);
        cameraSubToolsRecycler.setAdapter(cameraSubToolsAdapter);

        cameraAdjustToolsRecycler = (RecyclerView) findViewById(R.id.cameraAdjustToolsRecycler);
        cameraAdjustToolsRecycler.setVisibility(View.GONE);
        adjustToolsAdapter = new AdjustToolsAdapter(this);
        mAdjustToolsAdapterLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        cameraAdjustToolsRecycler.setLayoutManager(mAdjustToolsAdapterLManager);
        cameraAdjustToolsRecycler.setAdapter(adjustToolsAdapter);

        addToUndoReodList();
        hideShowUndoRedo();

        cameraBrightnessSeekBar = (SeekBar) findViewById(R.id.cameraBrightnessSeekBar);
        cameraBrightnessSeekBar.setVisibility(View.VISIBLE);
        cameraBrightnessSeekBar.setOnSeekBarChangeListener(this);

        cameraContrastSeekBar = (SeekBar) findViewById(R.id.cameraContrastSeekBar);
        cameraContrastSeekBar.setOnSeekBarChangeListener(this);

        cameraSaturationSeekBar = (SeekBar) findViewById(R.id.cameraSaturationSeekBar);
        cameraSaturationSeekBar.setOnSeekBarChangeListener(this);

        cameraSharpnessSeekBar = (SeekBar) findViewById(R.id.cameraSharpnessSeekBar);
        cameraSharpnessSeekBar.setOnSeekBarChangeListener(this);

        cameraVignetteSeekBar = (SeekBar) findViewById(R.id.cameraVignetteSeekBar);
        cameraVignetteSeekBar.setOnSeekBarChangeListener(this);

        cameraShadowSeekBar = (SeekBar) findViewById(R.id.cameraShadowSeekBar);
        cameraShadowSeekBar.setOnSeekBarChangeListener(this);

        cameraHighlightSeekBar = (SeekBar) findViewById(R.id.cameraHighlightSeekBar);
        cameraHighlightSeekBar.setOnSeekBarChangeListener(this);

        cameraTempSeekBar = (SeekBar) findViewById(R.id.cameraTempSeekBar);
        cameraTempSeekBar.setOnSeekBarChangeListener(this);

        cameraTintSeekBar = (SeekBar) findViewById(R.id.cameraTintSeekBar);
        cameraTintSeekBar.setOnSeekBarChangeListener(this);

        cameraDenoiseSeekBar = (SeekBar) findViewById(R.id.cameraDenoiseSeekBar);
        cameraDenoiseSeekBar.setOnSeekBarChangeListener(this);

        cameraCurveSeekBar = (SeekBar) findViewById(R.id.cameraCurveSeekBar);
        cameraCurveSeekBar.setOnSeekBarChangeListener(this);

        cameraColorBalanceSeekBar = (SeekBar) findViewById(R.id.cameraColorBalanceSeekBar);
        cameraColorBalanceSeekBar.setOnSeekBarChangeListener(this);


        adjustClose = (TextView) findViewById(R.id.adjustClose);
        adjustClose.setOnClickListener(this);

        adjustDone = (TextView) findViewById(R.id.adjustDone);
        adjustDone.setOnClickListener(this);

    }


    /**onClick*/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backCameraLLayout:
                Constants.isAdjustSelected = false;
                Constants.isAdjustCameraImage = false;

                if(Constants.isAdjustCameraImage){
                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                    cameraEditToolsRecycleView.setVisibility(View.VISIBLE);
                    cameraBlankLayoutBottom.setVisibility(View.VISIBLE);

                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    cameraAdjustToolsRecycler.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);

                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;
                }

                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                cameraEditToolsRecycleView.setVisibility(View.VISIBLE);
                cameraBlankLayoutBottom.setVisibility(View.VISIBLE);

                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraAdjustToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);

                show_alert_back("Exit", "Are you sure you want to exit Editor ?");
                break;


            case R.id.undoCameraImageLayout:
                Constants.isAdjustCameraImage = false;
                Constants.isAdjustSelected = false;

                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                cameraEditToolsRecycleView.setVisibility(View.VISIBLE);
                cameraBlankLayoutBottom.setVisibility(View.VISIBLE);

                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraAdjustToolsRecycler.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);

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
                Constants.isAdjustSelected = false;


                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                cameraEditToolsRecycleView.setVisibility(View.VISIBLE);
                cameraBlankLayoutBottom.setVisibility(View.VISIBLE);

                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraAdjustToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);


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
                Constants.isAdjustCameraImage = false;
                Constants.isAdjustSelected = false;

                if(Constants.isAdjustCameraImage){

                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                    cameraEditToolsRecycleView.setVisibility(View.VISIBLE);
                    cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    cameraAdjustToolsRecycler.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);

                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;
                }

                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                cameraEditToolsRecycleView.setVisibility(View.VISIBLE);
                cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraAdjustToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);


                cameraSelectedImageShape.setLayoutParams( new android.widget.RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );
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

/*****************************Display Interestial Adds****************************/
                if(addprefs!=null)
                    dispInterestialAdds();

                break;

            case R.id.blankCameraLayoutTop:
                if(Constants.isAdjustCameraImage){
                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                    cameraEditToolsRecycleView.setVisibility(View.VISIBLE);
                    cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                    blankCameraLayoutTop.setVisibility(View.VISIBLE);

                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    cameraAdjustToolsRecycler.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);

                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;
                }

                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                cameraEditToolsRecycleView.setVisibility(View.VISIBLE);
                cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                blankCameraLayoutTop.setVisibility(View.VISIBLE);

                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraAdjustToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);

                break;

            case R.id.cameraBlankLayoutBottom:
                if(Constants.isAdjustCameraImage){
                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);
                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                    cameraEditToolsRecycleView.setVisibility(View.VISIBLE);
                    blankCameraLayoutTop.setVisibility(View.VISIBLE);
                    cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    cameraAdjustToolsRecycler.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;
                }
                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                cameraEditToolsRecycleView.setVisibility(View.VISIBLE);
                blankCameraLayoutTop.setVisibility(View.VISIBLE);
                cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraAdjustToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                break;


            case R.id.adjustClose:
                Constants.isAdjustSelected = false;
                if (Constants.isAdjustCameraImage) {
                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                    cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                    cameraEditToolsRecycleView.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    cameraAdjustToolsRecycler.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    Constants.isAdjustCameraImage = false;
                }
                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                cameraEditToolsRecycleView.setVisibility(View.VISIBLE);
                cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraAdjustToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);

                break;


            case R.id.adjustDone:
                Constants.isAdjustSelected = false;
                if (Constants.isAdjustCameraImage) {
                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                    cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                    cameraEditToolsRecycleView.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    cameraAdjustToolsRecycler.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    Constants.isAdjustCameraImage = false;
                }

                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                cameraEditToolsRecycleView.setVisibility(View.VISIBLE);
                cameraBlankLayoutBottom.setVisibility(View.VISIBLE);

                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraAdjustToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);

                /*save*/
                cameraSelectedImageShape.setLayoutParams(
                        new android.widget.RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

                if (cameraSelectedImageShape.getRotation() > 0) {
                    Bitmap bitmap = createBitmapFromLayout(cameraSelectedImageRLayout);
                    Constants.capturedImageBitmap = bitmap;
                    cameraSelectedImageShape.setImageBitmap(bitmap);
                } else if (cameraSelectedImageShape.getRotation() == 0) {
                    Bitmap bitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();
                    Constants.capturedImageBitmap = bitmap;
                    cameraSelectedImageShape.setImageBitmap(bitmap);
                }

                cameraSelectedImageShape.setLayoutParams(
                        new android.widget.RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                        ));
                cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
        }
    }

    /****************onSeekbar Start**********************/
    @Override
    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
         //Bitmap finalImage = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();
        cameraSelectedImageShape.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        cameraSelectedImageShape.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        cameraSelectedImageShape.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onColorOverlaySubFilter(int depth, float red, float green, float blue) {
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ColorOverlaySubFilter(depth , red , green , blue));
        cameraSelectedImageShape.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onVignetteSubfilter(Context context, int alpha) {
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new VignetteSubfilter(context , alpha));
        cameraSelectedImageShape.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));

    }

    @Override
    public void onToneCurveSubFilter( int  redKnotsX, int  redKnotsY, int  greenKnotsX, int  greenKnotsY, int blueKnotsX , int blueKnotsY) {
        Filter myFilter = new Filter();
        Point[] rgbKnots;
        rgbKnots = new Point[3];

        rgbKnots[0] = new Point(redKnotsX, redKnotsY);
        rgbKnots[1] = new Point(175-greenKnotsX, 139-greenKnotsY);
        rgbKnots[2] = new Point(255-blueKnotsX, 255-blueKnotsY);

        myFilter.addSubFilter(new ToneCurveSubFilter(rgbKnots, null, null, null));
        cameraSelectedImageShape.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }



    /************************Brightness / Contrast Start*******************/

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

        if (seekBar.getId() == R.id.cameraBrightnessSeekBar) {
            int progressBrightness = cameraBrightnessSeekBar.getProgress();
            onBrightnessChanged(progressBrightness-100);
            //progressBright = progressBrightness;
        }

        if (seekBar.getId() == R.id.cameraContrastSeekBar) {
            int progressContrast = cameraContrastSeekBar.getProgress();
            progressContrast += 10;
            float floatVal = .10f * progressContrast;
            onContrastChanged(floatVal);
        }
        if (seekBar.getId() == R.id.cameraSaturationSeekBar) {
            // saturation values are b/w 0.0f - 3.0f
            int progressSaturation = cameraSaturationSeekBar.getProgress();
            float floatVal = .10f * progressSaturation;
            onSaturationChanged(floatVal);
        }

        if (seekBar.getId() == R.id.cameraVignetteSeekBar) {
            int progressVignette = cameraVignetteSeekBar.getProgress();
            onVignetteSubfilter(this , progressVignette);
        }
        if (seekBar.getId() == R.id.cameraShadowSeekBar) {
            int progressShadow = cameraShadowSeekBar.getProgress();
            onVignetteSubfilter(this , progressShadow*3);
        }
        if (seekBar.getId() == R.id.cameraHighlightSeekBar) {
            int progressHighlight = (int)cameraHighlightSeekBar.getProgress()/2;
            onBrightnessChanged(progressHighlight);
        }
        if (seekBar.getId() == R.id.cameraTempSeekBar) {

//            onColorOverlaySubFilter(depth , red ,green, blue );
            if (cameraTempSeekBar.getProgress() > 0 && cameraTempSeekBar.getProgress() < 30){
                int progressTempSeek = cameraTempSeekBar.getProgress() ;
                float red = .02f * progressTempSeek;
                float green = .01f * progressTempSeek;
                float blue = .01f * progressTempSeek;
                onColorOverlaySubFilter(progressTempSeek, red, green, blue);
            }
            if (cameraTempSeekBar.getProgress() > 30 && cameraTempSeekBar.getProgress() < 65){
                int progressTempSeek = cameraTempSeekBar.getProgress() ;
                float red = .01f * progressTempSeek;
                float green = .02f * progressTempSeek;
                float blue = .01f * progressTempSeek;
                onColorOverlaySubFilter(progressTempSeek, red, green, blue);
            }
            if (cameraTempSeekBar.getProgress() > 65 && cameraTempSeekBar.getProgress() <=100){
                int progressTempSeek = cameraTempSeekBar.getProgress() ;
                float red = .01f * progressTempSeek;
                float green = .01f * progressTempSeek;
                float blue = .02f * progressTempSeek;
                onColorOverlaySubFilter(progressTempSeek, red, green, blue);
            }
        }
        if (seekBar.getId() == R.id.cameraDenoiseSeekBar) {
            //CustomProgressDialog.show(cameraActivity.this, "Processing...");
        }
        if (seekBar.getId() == R.id.cameraTintSeekBar) {
            int progressTintSeek = cameraTintSeekBar.getProgress() ;
            cameraSelectedImageShape.setImageBitmap(applyTintEffect(finalImage.copy(Bitmap.Config.ARGB_8888, true), progressTintSeek*5));
        }

        if (seekBar.getId() == R.id.cameraCurveSeekBar) {
            int  progressCurve = cameraCurveSeekBar.getProgress();
            //onToneCurveSubFilter(progressCurve, progressCurve, progressCurve*5,progressCurve*9, progressCurve*15 , progressCurve*25);
            onToneCurveSubFilter(progressCurve, progressCurve, progressCurve,progressCurve, progressCurve , progressCurve);
        }
        if (seekBar.getId() == R.id.cameraColorBalanceSeekBar) {
            if (cameraColorBalanceSeekBar.getProgress() > 0 && cameraColorBalanceSeekBar.getProgress() < 30){
                int progressColorBalance = cameraColorBalanceSeekBar.getProgress() ;
                //int depth = progress ;
                float red = .01f * progressColorBalance;
                float green = .3f * progressColorBalance;
                float blue = .01f * progressColorBalance;

                onColorOverlaySubFilter(progressColorBalance, red, green, blue);
            }
            if (cameraColorBalanceSeekBar.getProgress() > 30 && cameraColorBalanceSeekBar.getProgress() < 65){
                int progressColorBalance = cameraColorBalanceSeekBar.getProgress() ;
                float red = .01f * progressColorBalance;
                float green = .01f * progressColorBalance;
                float blue = .03f * progressColorBalance;

                onColorOverlaySubFilter(progressColorBalance, red, green, blue);
            }
            if (cameraColorBalanceSeekBar.getProgress() > 65 && cameraColorBalanceSeekBar.getProgress() <=100){
                int progressColorBalance = cameraColorBalanceSeekBar.getProgress() ;
                float red = .03f * progressColorBalance;
                float green = .01f * progressColorBalance;
                float blue = .01f * progressColorBalance;

                onColorOverlaySubFilter(progressColorBalance, red, green, blue);
            }
        }


    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //onEditCompleted();

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //onEditCompleted();

        if (seekBar.getId() == R.id.cameraDenoiseSeekBar) {
            CustomProgressDialog.show(CameraActivity.this, "Processing...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cameraSelectedImageShape.setImageBitmap(onNoise(finalImage.copy(Bitmap.Config.ARGB_8888, true)));

                    cameraSelectedImageShape.setImageBitmap(onAdjustFinalImage());

                    CustomProgressDialog.dismiss();
                }
            }, 1000);
        }

        if (seekBar.getId() == R.id.cameraSharpnessSeekBar) {
            cameraSelectedImageShape.setImageBitmap(ImageFilter.applyFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true), ImageFilter.Filter.SHARPEN));
        }

    }

    /************************Brightness / Contrast End*******************/


    /****************onSeekbar End**********************/

    @Override
    public void onEditStarted() {

    }
    @Override
    public void onEditCompleted() {
        //Bitmap mFilteredImage = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();
        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalImage = myFilter.processFilter(bitmap);
        Constants.capturedImageBitmap = finalImage;
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

    /*****************************Display Interestial Adds****************************/
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
                            Constants.cameraOrignalBitmap = Constants.capturedImageBitmap;
                            cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);
                            //cameraSelectedImageShape.setColorFilter(Utilities.setBrightness(110));
                            cameraSelectedImageShape.setRotation(0);
                            fixAntiAlias(cameraSelectedImageShape);


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

    /****************AdjustTools Start**********************/
    @Override
    public void onAdjustTollsSelected(AdjustToolsType cameraAdjustToolsType) {

        switch (cameraAdjustToolsType) {

            case RESET:

                addToUndoReodList();

                cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                cameraEditToolsRecycleView.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.GONE);
                cameraBrightnessSeekBar.setVisibility(View.GONE);
                cameraContrastSeekBar.setVisibility(View.GONE);
                cameraSaturationSeekBar.setVisibility(View.GONE);
                cameraSharpnessSeekBar.setVisibility(View.GONE);
                cameraVignetteSeekBar.setVisibility(View.GONE);
                cameraShadowSeekBar.setVisibility(View.GONE);
                cameraHighlightSeekBar.setVisibility(View.GONE);
                cameraTempSeekBar.setVisibility(View.GONE);
                cameraTintSeekBar.setVisibility(View.GONE);
                cameraDenoiseSeekBar.setVisibility(View.GONE);
                cameraCurveSeekBar.setVisibility(View.GONE);
                cameraColorBalanceSeekBar.setVisibility(View.GONE);

                cameraAdjustToolsRecycler.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);

                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);

                cameraSelectedImageShape.setImageBitmap(Constants.cameraOrignalBitmap);
                cameraSelectedImageShape.setRotation(0);
                fixAntiAlias(cameraSelectedImageShape);
                break;


            case BRIGHTNESS:
                addToUndoReodList();
                cameraBlankLayoutBottom.setVisibility(View.GONE);
                cameraEditToolsRecycleView.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.VISIBLE);

                cameraBrightnessSeekBar.setVisibility(View.VISIBLE);
                cameraContrastSeekBar.setVisibility(View.GONE);
                cameraSaturationSeekBar.setVisibility(View.GONE);
                cameraSharpnessSeekBar.setVisibility(View.GONE);
                cameraVignetteSeekBar.setVisibility(View.GONE);
                cameraShadowSeekBar.setVisibility(View.GONE);
                cameraHighlightSeekBar.setVisibility(View.GONE);
                cameraTempSeekBar.setVisibility(View.GONE);
                cameraTintSeekBar.setVisibility(View.GONE);
                cameraDenoiseSeekBar.setVisibility(View.GONE);
                cameraCurveSeekBar.setVisibility(View.GONE);
                cameraColorBalanceSeekBar.setVisibility(View.GONE);

                cameraAdjustToolsRecycler.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                finalImage = onAdjustFinalImage();

                // keeping brightness value b/w -100 / +100
                cameraBrightnessSeekBar.setMax(200);
                cameraBrightnessSeekBar.setProgress(100);
                break;

            case CONTRAST:
                addToUndoReodList();
                // keeping contrast value b/w 1.0 - 3.0
                cameraContrastSeekBar.setMax(30);
                cameraContrastSeekBar.setProgress(0);

                cameraBlankLayoutBottom.setVisibility(View.GONE);
                cameraEditToolsRecycleView.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.VISIBLE);

                cameraBrightnessSeekBar.setVisibility(View.GONE);
                cameraContrastSeekBar.setVisibility(View.VISIBLE);
                cameraSaturationSeekBar.setVisibility(View.GONE);
                cameraSharpnessSeekBar.setVisibility(View.GONE);
                cameraVignetteSeekBar.setVisibility(View.GONE);
                cameraShadowSeekBar.setVisibility(View.GONE);
                cameraHighlightSeekBar.setVisibility(View.GONE);
                cameraTempSeekBar.setVisibility(View.GONE);
                cameraTintSeekBar.setVisibility(View.GONE);
                cameraDenoiseSeekBar.setVisibility(View.GONE);
                cameraCurveSeekBar.setVisibility(View.GONE);
                cameraColorBalanceSeekBar.setVisibility(View.GONE);

                cameraAdjustToolsRecycler.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                finalImage = onAdjustFinalImage();

                break;
            case SATURATION:
                addToUndoReodList();
                // keeping saturation value b/w 0.0 - 3.0
                cameraSaturationSeekBar.setMax(30);
                cameraSaturationSeekBar.setProgress(10);

                cameraBlankLayoutBottom.setVisibility(View.GONE);
                cameraEditToolsRecycleView.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.VISIBLE);

                cameraBrightnessSeekBar.setVisibility(View.GONE);
                cameraContrastSeekBar.setVisibility(View.GONE);
                cameraSaturationSeekBar.setVisibility(View.VISIBLE);
                cameraSharpnessSeekBar.setVisibility(View.GONE);
                cameraVignetteSeekBar.setVisibility(View.GONE);
                cameraShadowSeekBar.setVisibility(View.GONE);
                cameraHighlightSeekBar.setVisibility(View.GONE);
                cameraTempSeekBar.setVisibility(View.GONE);
                cameraTintSeekBar.setVisibility(View.GONE);
                cameraDenoiseSeekBar.setVisibility(View.GONE);
                cameraCurveSeekBar.setVisibility(View.GONE);
                cameraColorBalanceSeekBar.setVisibility(View.GONE);

                cameraAdjustToolsRecycler.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                finalImage = onAdjustFinalImage();
                break;
            case SHARPNESS:
                addToUndoReodList();
                cameraBlankLayoutBottom.setVisibility(View.GONE);
                cameraEditToolsRecycleView.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.VISIBLE);

                cameraBrightnessSeekBar.setVisibility(View.GONE);
                cameraContrastSeekBar.setVisibility(View.GONE);
                cameraSaturationSeekBar.setVisibility(View.GONE);
                cameraSharpnessSeekBar.setVisibility(View.VISIBLE);
                cameraVignetteSeekBar.setVisibility(View.GONE);
                cameraShadowSeekBar.setVisibility(View.GONE);
                cameraHighlightSeekBar.setVisibility(View.GONE);
                cameraTempSeekBar.setVisibility(View.GONE);
                cameraTintSeekBar.setVisibility(View.GONE);
                cameraDenoiseSeekBar.setVisibility(View.GONE);
                cameraCurveSeekBar.setVisibility(View.GONE);
                cameraColorBalanceSeekBar.setVisibility(View.GONE);

                cameraAdjustToolsRecycler.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                finalImage = onAdjustFinalImage();

                break;
            case VIGNETTE:
                addToUndoReodList();
                cameraBlankLayoutBottom.setVisibility(View.GONE);
                cameraEditToolsRecycleView.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.VISIBLE);

                cameraBrightnessSeekBar.setVisibility(View.GONE);
                cameraContrastSeekBar.setVisibility(View.GONE);
                cameraSaturationSeekBar.setVisibility(View.GONE);
                cameraSharpnessSeekBar.setVisibility(View.GONE);
                cameraVignetteSeekBar.setVisibility(View.VISIBLE);
                cameraShadowSeekBar.setVisibility(View.GONE);
                cameraHighlightSeekBar.setVisibility(View.GONE);
                cameraTempSeekBar.setVisibility(View.GONE);
                cameraTintSeekBar.setVisibility(View.GONE);
                cameraDenoiseSeekBar.setVisibility(View.GONE);
                cameraCurveSeekBar.setVisibility(View.GONE);
                cameraColorBalanceSeekBar.setVisibility(View.GONE);

                cameraAdjustToolsRecycler.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                finalImage = onAdjustFinalImage();
                break;


            case SHADOW:
                addToUndoReodList();
                cameraShadowSeekBar.setMax(80);
                //cameraShadowSeekBar.setMax(0);
                cameraBlankLayoutBottom.setVisibility(View.GONE);
                cameraEditToolsRecycleView.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.VISIBLE);

                cameraBrightnessSeekBar.setVisibility(View.GONE);
                cameraContrastSeekBar.setVisibility(View.GONE);
                cameraSaturationSeekBar.setVisibility(View.GONE);
                cameraSharpnessSeekBar.setVisibility(View.GONE);
                cameraVignetteSeekBar.setVisibility(View.GONE);
                cameraShadowSeekBar.setVisibility(View.VISIBLE);
                cameraHighlightSeekBar.setVisibility(View.GONE);
                cameraTempSeekBar.setVisibility(View.GONE);
                cameraTintSeekBar.setVisibility(View.GONE);
                cameraDenoiseSeekBar.setVisibility(View.GONE);
                cameraCurveSeekBar.setVisibility(View.GONE);
                cameraColorBalanceSeekBar.setVisibility(View.GONE);

                cameraAdjustToolsRecycler.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                finalImage = onAdjustFinalImage();

                break;
            case HIGH_LIGHT:
                addToUndoReodList();
                cameraBlankLayoutBottom.setVisibility(View.GONE);
                cameraEditToolsRecycleView.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.VISIBLE);

                cameraBrightnessSeekBar.setVisibility(View.GONE);
                cameraContrastSeekBar.setVisibility(View.GONE);
                cameraSaturationSeekBar.setVisibility(View.GONE);
                cameraSharpnessSeekBar.setVisibility(View.GONE);
                cameraVignetteSeekBar.setVisibility(View.GONE);
                cameraShadowSeekBar.setVisibility(View.GONE);
                cameraHighlightSeekBar.setVisibility(View.VISIBLE);
                cameraTempSeekBar.setVisibility(View.GONE);
                cameraTintSeekBar.setVisibility(View.GONE);
                cameraDenoiseSeekBar.setVisibility(View.GONE);
                cameraCurveSeekBar.setVisibility(View.GONE);
                cameraColorBalanceSeekBar.setVisibility(View.GONE);

                cameraAdjustToolsRecycler.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                finalImage = onAdjustFinalImage();
                break;

            case TEMP:
                addToUndoReodList();
                cameraTempSeekBar.setMax(100);
                //cameraTempSeekBar.setProgress(0);

                cameraBlankLayoutBottom.setVisibility(View.GONE);
                cameraEditToolsRecycleView.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.VISIBLE);

                cameraBrightnessSeekBar.setVisibility(View.GONE);
                cameraContrastSeekBar.setVisibility(View.GONE);
                cameraSaturationSeekBar.setVisibility(View.GONE);
                cameraSharpnessSeekBar.setVisibility(View.GONE);
                cameraVignetteSeekBar.setVisibility(View.GONE);
                cameraShadowSeekBar.setVisibility(View.GONE);
                cameraHighlightSeekBar.setVisibility(View.GONE);
                cameraTempSeekBar.setVisibility(View.VISIBLE);
                cameraTintSeekBar.setVisibility(View.GONE);
                cameraDenoiseSeekBar.setVisibility(View.GONE);
                cameraCurveSeekBar.setVisibility(View.GONE);
                cameraColorBalanceSeekBar.setVisibility(View.GONE);

                cameraAdjustToolsRecycler.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                finalImage = onAdjustFinalImage();
                break;
            case TINT:
                addToUndoReodList();
                cameraBlankLayoutBottom.setVisibility(View.GONE);
                cameraEditToolsRecycleView.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.VISIBLE);

                cameraBrightnessSeekBar.setVisibility(View.GONE);
                cameraContrastSeekBar.setVisibility(View.GONE);
                cameraSaturationSeekBar.setVisibility(View.GONE);
                cameraSharpnessSeekBar.setVisibility(View.GONE);
                cameraVignetteSeekBar.setVisibility(View.GONE);
                cameraShadowSeekBar.setVisibility(View.GONE);
                cameraHighlightSeekBar.setVisibility(View.GONE);
                cameraTempSeekBar.setVisibility(View.GONE);
                cameraTintSeekBar.setVisibility(View.VISIBLE);
                cameraDenoiseSeekBar.setVisibility(View.GONE);
                cameraCurveSeekBar.setVisibility(View.GONE);
                cameraColorBalanceSeekBar.setVisibility(View.GONE);

                cameraAdjustToolsRecycler.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                finalImage = onAdjustFinalImage();
                break;
            case DENOISE:
                addToUndoReodList();
                cameraBlankLayoutBottom.setVisibility(View.GONE);
                cameraEditToolsRecycleView.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.VISIBLE);

                cameraBrightnessSeekBar.setVisibility(View.GONE);
                cameraContrastSeekBar.setVisibility(View.GONE);
                cameraSaturationSeekBar.setVisibility(View.GONE);
                cameraSharpnessSeekBar.setVisibility(View.GONE);
                cameraVignetteSeekBar.setVisibility(View.GONE);
                cameraShadowSeekBar.setVisibility(View.GONE);
                cameraHighlightSeekBar.setVisibility(View.GONE);
                cameraTempSeekBar.setVisibility(View.GONE);
                cameraTintSeekBar.setVisibility(View.GONE);
                cameraDenoiseSeekBar.setVisibility(View.VISIBLE);
                cameraCurveSeekBar.setVisibility(View.GONE);
                cameraColorBalanceSeekBar.setVisibility(View.GONE);

                cameraAdjustToolsRecycler.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                finalImage = onAdjustFinalImage();

                break;
            case CURVE:
                addToUndoReodList();
                cameraBlankLayoutBottom.setVisibility(View.GONE);
                cameraEditToolsRecycleView.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.VISIBLE);

                cameraBrightnessSeekBar.setVisibility(View.GONE);
                cameraContrastSeekBar.setVisibility(View.GONE);
                cameraSaturationSeekBar.setVisibility(View.GONE);
                cameraSharpnessSeekBar.setVisibility(View.GONE);
                cameraVignetteSeekBar.setVisibility(View.GONE);
                cameraShadowSeekBar.setVisibility(View.GONE);
                cameraHighlightSeekBar.setVisibility(View.GONE);
                cameraTempSeekBar.setVisibility(View.GONE);
                cameraTintSeekBar.setVisibility(View.GONE);
                cameraDenoiseSeekBar.setVisibility(View.GONE);
                cameraCurveSeekBar.setVisibility(View.VISIBLE);
                cameraColorBalanceSeekBar.setVisibility(View.GONE);

                cameraAdjustToolsRecycler.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                finalImage = onAdjustFinalImage();
                break;
            case COLOR_BALANCE:
                addToUndoReodList();
                cameraColorBalanceSeekBar.setMax(100);
                //cameraColorBalanceSeekBar.setProgress(0);

                cameraBlankLayoutBottom.setVisibility(View.GONE);
                cameraEditToolsRecycleView.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.VISIBLE);

                cameraBrightnessSeekBar.setVisibility(View.GONE);
                cameraContrastSeekBar.setVisibility(View.GONE);
                cameraSaturationSeekBar.setVisibility(View.GONE);
                cameraSharpnessSeekBar.setVisibility(View.GONE);
                cameraVignetteSeekBar.setVisibility(View.GONE);
                cameraShadowSeekBar.setVisibility(View.GONE);
                cameraHighlightSeekBar.setVisibility(View.GONE);
                cameraTempSeekBar.setVisibility(View.GONE);
                cameraTintSeekBar.setVisibility(View.GONE);
                cameraDenoiseSeekBar.setVisibility(View.GONE);
                cameraCurveSeekBar.setVisibility(View.GONE);

                cameraColorBalanceSeekBar.setVisibility(View.VISIBLE);
                cameraAdjustToolsRecycler.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                finalImage = onAdjustFinalImage();
                break;
        }
    }

    /****************AdjustTools End**********************/
    /**************Select Tolls  **********************/
    @Override
    public void onCameraEditTollsSelected(CameraEditToolsType cameraEditToolsType) {
        switch (cameraEditToolsType) {

            case CAMERA:

                if(Constants.isAdjustCameraImage){
                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);
                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);

                    cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);

                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;

                }

                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
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
                    //Constants.capturedImageBitmap = createBitmapFromLayout(adjustCameraPhotoRLayout);
                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                    cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;
                }
                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                Constants.capturedImageBitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();
                if(cameraSubToolsRecycler.getVisibility() == View.VISIBLE ){
                    cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                }else {
                    cameraBlankLayoutBottom.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.VISIBLE);
                    cameraSubToolsRecycler.setVisibility(View.VISIBLE);
                }
                break;

            case EFFECTS:

                if(Constants.isAdjustCameraImage){
                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                    blankCameraLayoutTop.setVisibility(View.VISIBLE);
                    cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;
                }

                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                //Bitmap  bitmap  = createBitmapFromLayout(cameraSelectedImageRLayout);
                Constants.capturedImageBitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();

                setFiltersImage();

                if(cameraEffectsLLayout.getVisibility() == View.VISIBLE ){
                    cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                }else {
                    cameraBlankLayoutBottom.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.VISIBLE);
                    cameraEffectsLLayout.setVisibility(View.VISIBLE);
                }
                break;

            case ADJUST:
                Constants.isAdjustSelected = true;
                cameraAdjustToolsRecycler = (RecyclerView) findViewById(R.id.cameraAdjustToolsRecycler);
                cameraAdjustToolsRecycler.setVisibility(View.GONE);
                adjustToolsAdapter = new AdjustToolsAdapter(this);
                mAdjustToolsAdapterLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                cameraAdjustToolsRecycler.setLayoutManager(mAdjustToolsAdapterLManager);
                cameraAdjustToolsRecycler.setAdapter(adjustToolsAdapter);
                cameraBlankLayoutBottom.setVisibility(View.GONE);
                cameraEditToolsRecycleView.setVisibility(View.GONE);
                cameraSubToolsLayout.setVisibility(View.VISIBLE);
                cameraBrightnessSeekBar.setVisibility(View.VISIBLE);
                cameraContrastSeekBar.setVisibility(View.GONE);
                cameraSaturationSeekBar.setVisibility(View.GONE);
                cameraSharpnessSeekBar.setVisibility(View.GONE);
                cameraVignetteSeekBar.setVisibility(View.GONE);
                cameraShadowSeekBar.setVisibility(View.GONE);
                cameraHighlightSeekBar.setVisibility(View.GONE);
                cameraTempSeekBar.setVisibility(View.GONE);
                cameraTintSeekBar.setVisibility(View.GONE);
                cameraDenoiseSeekBar.setVisibility(View.GONE);
                cameraCurveSeekBar.setVisibility(View.GONE);
                cameraColorBalanceSeekBar.setVisibility(View.GONE);
                cameraSubToolsRecycler.setVisibility(View.GONE);
                cameraEffectsLLayout.setVisibility(View.GONE);
                cameraAdjustToolsRecycler.setVisibility(View.VISIBLE);
                cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);



                if (!Constants.isAdjustCameraImage) {
                    cameraSelectedImageShape.setLayoutParams(
                            new android.widget.RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            ) );
                    cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    addToUndoReodList();

                    Constants.capturedImageBitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();

                    finalImage =  Constants.capturedImageBitmap;
                    Constants.cameraOrignalBitmap = Constants.capturedImageBitmap;

                    Constants.isAdjustCameraImage = true;

                    cameraSelectedImageShape.setLayoutParams(
                            new android.widget.RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                            ) );
                    cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }

                // keeping brightness value b/w -100 / +100
                cameraBrightnessSeekBar.setMax(200);
                cameraBrightnessSeekBar.setProgress(100);

//                cameraEffectsLLayout.setVisibility(View.GONE);
//                cameraSubToolsRecycler.setVisibility(View.GONE);
//                //Bitmap  bitmap  = createBitmapFromLayout(cameraSelectedImageRLayout);
//                Constants.capturedImageBitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();

//                if(cameraAdjustSeekbarLLayout.getVisibility() == View.VISIBLE ){
//                    cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
//                    cameraSubToolsLayout.setVisibility(View.GONE);
//                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
//                }else {
//                    cameraBlankLayoutBottom.setVisibility(View.GONE);
//                    cameraSubToolsLayout.setVisibility(View.VISIBLE);
//                    cameraAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
//                }


                break;


            case EDIT:

                if(Constants.isAdjustCameraImage){

                    //2608  Constants.capturedImageBitmap = createBitmapFromLayout(adjustCameraPhotoRLayout);

                    cameraSelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                    cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
                    cameraAdjustSeekbarLLayout.setVisibility(View.GONE);
                    cameraEffectsLLayout.setVisibility(View.GONE);
                    cameraSubToolsLayout.setVisibility(View.GONE);
                    cameraSubToolsRecycler.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustCameraImage=false;
                }

                cameraSelectedImageRLayout.setVisibility(View.VISIBLE);
                cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
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
                    //Constants.cameraEditBitmapHeight  = cameraSelectedImageShape.getHeight();
                    //Constants.cameraEditBitmapWidth  = cameraSelectedImageShape.getWidth();

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
                    //Constants.capturedImageBitmap =  createBitmapFromLayout(mcameraRLayout);

                    Constants.cameraEditBitmap = Constants.capturedImageBitmap;
                    //Bitmap layoutBitmap = createBitmapFromLayout(mcameraRLayout);
                    Constants.cameraEditBitmapHeight  = Constants.cameraEditBitmap.getHeight();
                    Constants.cameraEditBitmapWidth  = Constants.cameraEditBitmap.getWidth();

                    //Bitmap layoutBitmap = createBitmapFromLayout(cameraSelectedImageShape);
//                    Bitmap layoutBitmap = createBitmapFromLayout(mcameraRLayout);
//                    Constants.cameraEditBitmapHeight  = layoutBitmap.getHeight();
//                    Constants.cameraEditBitmapWidth  = layoutBitmap.getWidth();

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

                cameraBlankLayoutBottom.setVisibility(View.VISIBLE);
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

//            increaseBrightness(cameraSelectedImageShape, 0);
//            increaseContrast(cameraSelectedImageShape, 0);
//            cameraBrightness.setProgress(0);
//            cameraContrast.setProgress(0);

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

//            increaseBrightness(cameraSelectedImageShape, 0);
//            increaseContrast(cameraSelectedImageShape, 0);
//            cameraBrightness.setProgress(0);
//            cameraContrast.setProgress(0);

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

    public Bitmap onAdjustFinalImage(){
        cameraSelectedImageShape.setLayoutParams(
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
        cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Bitmap bitmap = ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();

        cameraSelectedImageShape.setLayoutParams(
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));
        cameraSelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return bitmap;
    }
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

    public Bitmap applyTintEffect(Bitmap src, int degree) {
        // get source image size
        int width = src.getWidth();
        int height = src.getHeight();

        int[] pix = new int[width * height];
        // get pixel array from source
        src.getPixels(pix, 0, width, 0, 0, width, height);

        int RY, GY, BY, RYY, GYY, BYY, R, G, B, Y;
        double angle = (PI * (double)degree) / HALF_CIRCLE_DEGREE;

        int S = (int)(RANGE * Math.sin(angle));
        int C = (int)(RANGE * Math.cos(angle));

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int r = ( pix[index] >> 16 ) & 0xff;
                int g = ( pix[index] >> 8 ) & 0xff;
                int b = pix[index] & 0xff;
                RY = ( 70 * r - 59 * g - 11 * b ) / 100;
                GY = (-30 * r + 41 * g - 11 * b ) / 100;
                BY = (-30 * r - 59 * g + 89 * b ) / 100;
                Y  = ( 30 * r + 59 * g + 11 * b ) / 100;
                RYY = ( S * BY + C * RY ) / 256;
                BYY = ( C * BY - S * RY ) / 256;
                GYY = (-51 * RYY - 19 * BYY ) / 100;
                R = Y + RYY;
                R = ( R < 0 ) ? 0 : (( R > 255 ) ? 255 : R );
                G = Y + GYY;
                G = ( G < 0 ) ? 0 : (( G > 255 ) ? 255 : G );
                B = Y + BYY;
                B = ( B < 0 ) ? 0 : (( B > 255 ) ? 255 : B );
                pix[index] = 0xff000000 | (R << 16) | (G << 8 ) | B;
            }
        // output bitmap
        Bitmap outBitmap = Bitmap.createBitmap(width, height, src.getConfig());
        outBitmap.setPixels(pix, 0, width, 0, 0, width, height);

        pix = null;

        return outBitmap;
    }

    public static Bitmap onNoise(Bitmap source) {
        final int COLOR_MAX = 0xFF;

        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        // a random object
        Random random = new Random();

        int index = 0;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get random color
                int randColor = Color.rgb(random.nextInt(COLOR_MAX),
                        random.nextInt(COLOR_MAX), random.nextInt(COLOR_MAX));
                // OR
                pixels[index] |= randColor;
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, source.getConfig());
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);

        source.recycle();
        source = null;

        return bmOut;
    }

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

