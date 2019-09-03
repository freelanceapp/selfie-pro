/*
package com.mojodigi.selfiepro.adjust;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.content.ContextCompat;
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
import com.mojodigi.selfiepro.bitmap.BitmapProcessing;
import com.mojodigi.selfiepro.filters.EditImageListener;
import com.mojodigi.selfiepro.filters.FiltersListFragment;
import com.mojodigi.selfiepro.filters.FiltersPagerAdapter;
import com.mojodigi.selfiepro.gallery.GalleryEditToolsAdapter;
import com.mojodigi.selfiepro.gallery.GalleryEditToolsType;
import com.mojodigi.selfiepro.gallery.GallerySubToolsAdapter;
import com.mojodigi.selfiepro.gallery.GallerySubToolsType;
import com.mojodigi.selfiepro.gallery.OnGalleryEditTollsSelected;
import com.mojodigi.selfiepro.gallery.OnGallerySubToolsSelected;
import com.mojodigi.selfiepro.utils.Constants;
import com.mojodigi.selfiepro.utils.CustomProgressDialog;
import com.mojodigi.selfiepro.utils.MyPreference;
import com.mojodigi.selfiepro.utils.Utilities;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class AdjustDemoActivity extends AppCompatActivity implements OnGalleryAdjustTollsSelected, EditImageListener, FiltersListFragment.FiltersListFragmentListener,  */
/*View.OnTouchListener,*//*
 View.OnClickListener,

        OnGalleryEditTollsSelected, SeekBar.OnSeekBarChangeListener, OnGallerySubToolsSelected {

    private static final String TAG = AdjustDemoActivity.class.getSimpleName();

    private Context mContext;
    private static final int GALLERY_REQUEST_TYPE = 222;

    private TextView undoGalleryTextView, redoGalleryTextView;
    private ImageView undoGalleryImageView, redoGalleryImageView;

    private PhotoView gallerySelectedImageShape*/
/*, adjustGalleryPhotoView*//*
;

    private RelativeLayout gallerySelectedImageRLayout */
/*, adjustGalleryRLayout, adjustGalleryPhotoRLayout*//*
;

    private LinearLayout backGalleryLLayout, blankGalleryLayoutTop, galleryEffectsLLayout, saveGalleryLayout,
            undoGalleryImageLayout, redoGalleryImageLayout, galleryAdjustSeekbarLLayout, gallerySubToolsLayout,
            blankGalleryLayoutBottom */
/*, adjustImageDoneLLayout*//*
, galleryBootomLayout;

    private MyPreference mMyPrecfence = null;

    private RecyclerView gallerySubToolsRecycler, galleryEditToolsRecycleView ,galleryAdjustToolsRecycler;

    private GalleryEditToolsAdapter mGalleryEditToolsAdapter;
    private GallerySubToolsAdapter gallerySubToolsAdapter;
    private AdjustToolsAdapter adjustToolsAdapter;

    private SeekBar galleryBrightnessSeekBar, galleryContrastSeekBar , gallerySaturationSeekBar ,gallerySharpnessSeekBar
            , galleryVignetteSeekBar , galleryShadowSeekBar , galleryHighlightSeekBar
            , galleryTempSeekBar , galleryTintSeekBar;

    public static AdjustDemoActivity instance;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private int currentShowingIndex = 0;
    private Uri mainPathUri;
    private ArrayList<Uri> pathForTempList;

    private int progressBright, progressContrast;

    */
/*******************************************************//*


    float angleRotation = 0;
    private Bitmap originalImage, filteredImage, firstImage;
    private FiltersListFragment filtersListFragment;
    private ViewPager filters_viewpager;
    private Bitmap cropedBitmap;



    private SharedPreferenceUtil addprefs;
    private View adContainer;

    Bitmap finalImage;
    // modified image values
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;


    private TextView adjustClose  ,  adjustDone;
    private LinearLayoutManager mAdjustToolsAdapterLManager;


    private String selected_effect;
    private Bitmap last_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_adjust_demo);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Constants.isAdjustGalleryImage = false;
        Constants.isImageCroped = false;
        Constants.editImageUri = "false";
        instance = AdjustDemoActivity.this;

        if (mContext == null) {
            mContext = AdjustDemoActivity.this;
        }
        if (mMyPrecfence == null) {
            mMyPrecfence = MyPreference.getMyPreferenceInstance(mContext);
        }

        pathForTempList = new ArrayList<Uri>();
        pathForTempList.clear();
        currentShowingIndex = 0;
        deleteTempExtraFile();

        getIntentUri();

        initView();

        filters_viewpager = (ViewPager) findViewById(R.id.filters_viewpager);
        ;
        setupViewPager(filters_viewpager);
        loadImageFilters();


        addprefs = new SharedPreferenceUtil(mContext);
        AddMobUtils adutil = new AddMobUtils();

        if (AddConstants.checkIsOnline(mContext) && adContainer != null && addprefs != null) {
            String AddPrioverId = addprefs.getStringValue(AddConstants.ADD_PROVIDER_ID, AddConstants.NOT_FOUND);
            if (AddPrioverId.equalsIgnoreCase(AddConstants.FaceBookAddProividerId)) {
                adutil.dispFacebookBannerAdd(mContext, addprefs, AdjustDemoActivity.this);
            }
        } else {
            Log.e("", "");
        }
    }


    public void getIntentUri() {
        Uri getedIntentUri = null;
        Intent extrasIntent = getIntent();
        if (extrasIntent != null) {
            getedIntentUri = extrasIntent.getParcelableExtra(Constants.URI_GALLERY);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), getedIntentUri);
                firstImage = bitmap;

                Constants.capturedImageBitmap = bitmap;
                setFiltersImage();

                //Uri compressedUri = Uri.parse(compressImage(getedIntentUri.toString()));
                //Uri compressedUri = Uri.parse(compressImage(getImageUri(mContext, originalImage).toString()));
                Uri compressedUri = Uri.parse(compressImage(getImageUri(mContext, bitmap).toString()));
                File imgFile = new File(compressImage(compressedUri.toString()));
                Uri compressedUri2;
                Bitmap compressedBitmap;
                if (imgFile.exists()) {
                    compressedUri2 = Uri.fromFile(imgFile);
                    compressedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), compressedUri2);
                    Constants.capturedImageBitmap = compressedBitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        //adjustGalleryRLayout.setVisibility(View.GONE);
        gallerySelectedImageRLayout.setVisibility(View.VISIBLE);
        galleryEditToolsRecycleView.setVisibility(View.VISIBLE);
        blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
        galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
        gallerySubToolsLayout.setVisibility(View.GONE);
        gallerySubToolsRecycler.setVisibility(View.GONE);
        galleryAdjustToolsRecycler.setVisibility(View.GONE);
        galleryEffectsLLayout.setVisibility(View.GONE);

        gallerySelectedImageShape.setLayoutParams(
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));

        gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (Constants.editImageUri.equalsIgnoreCase("true")) {
            CustomProgressDialog.show(mContext, getResources().getString(R.string.loading_msg));
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Constants.imageUri);
                Constants.capturedImageBitmap = bitmap;

                setFiltersImage();

                gallerySelectedImageShape.setLayoutParams(
                        new android.widget.RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                        ));

                gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

                gallerySelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);
                gallerySelectedImageShape.setRotation(0);
                fixAntiAlias(gallerySelectedImageShape);

                //galleryBrightnessSeekBar.setProgress(0);
                //galleryContrastSeekBar.setProgress(0);

                addToUndoReodList();
                hideShowUndoRedo();
                CustomProgressDialog.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Constants.editImageUri = "false";
        Constants.isAdjustGalleryImage = false;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        adContainer = findViewById(R.id.adMobView);

        gallerySelectedImageRLayout = (RelativeLayout) findViewById(R.id.gallerySelectedImageRLayout);
        gallerySelectedImageRLayout.setVisibility(View.VISIBLE);

//                adjustGalleryRLayout = (RelativeLayout) findViewById(R.id.adjustGalleryRLayout);
//                adjustGalleryRLayout.setVisibility(View.GONE);
//                adjustGalleryPhotoRLayout = (RelativeLayout) findViewById(R.id.adjustGalleryPhotoRLayout);
//                adjustGalleryPhotoView = (PhotoView) findViewById(R.id.adjustGalleryPhotoView);
//                adjustGalleryPhotoView.setRotation(0);
//                fixAntiAlias(adjustGalleryPhotoView);


        gallerySelectedImageShape = (PhotoView) findViewById(R.id.gallerySelectedImageShape);
        gallerySelectedImageShape.setLayoutParams(
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));

        gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
        gallerySelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);
        gallerySelectedImageShape.setOnClickListener(this);
        gallerySelectedImageShape.setRotation(0);
        fixAntiAlias(gallerySelectedImageShape);

        galleryEffectsLLayout = (LinearLayout) findViewById(R.id.galleryEffectsLLayout);


        mGalleryEditToolsAdapter = new GalleryEditToolsAdapter(this);
        gallerySubToolsAdapter = new GallerySubToolsAdapter(this);



        backGalleryLLayout = (LinearLayout) findViewById(R.id.backGalleryLLayout);
        blankGalleryLayoutTop = (LinearLayout) findViewById(R.id.blankGalleryLayoutTop);
        blankGalleryLayoutBottom = (LinearLayout) findViewById(R.id.blankGalleryLayoutBottom);
        //adjustImageDoneLLayout = (LinearLayout) findViewById(R.id.adjustImageDoneLLayout);

        backGalleryLLayout.setOnClickListener(this);
        blankGalleryLayoutTop.setOnClickListener(this);
        blankGalleryLayoutBottom.setOnClickListener(this);
        //adjustImageDoneLLayout.setOnClickListener(this);

        undoGalleryImageLayout = (LinearLayout) findViewById(R.id.undoGalleryImageLayout);
        undoGalleryImageLayout.setOnClickListener(this);
        undoGalleryImageView = (ImageView) findViewById(R.id.undoGalleryImageView);
        undoGalleryTextView = (TextView) findViewById(R.id.undoGalleryTextView);

        redoGalleryImageLayout = (LinearLayout) findViewById(R.id.redoGalleryImageLayout);
        redoGalleryImageLayout.setOnClickListener(this);
        redoGalleryImageView = (ImageView) findViewById(R.id.redoGalleryImageView);
        redoGalleryTextView = (TextView) findViewById(R.id.redoGalleryTextView);

        saveGalleryLayout = (LinearLayout) findViewById(R.id.saveGalleryLayout);
        saveGalleryLayout.setOnClickListener(this);

        blankGalleryLayoutBottom = (LinearLayout) findViewById(R.id.blankGalleryLayoutBottom);
        blankGalleryLayoutBottom.setVisibility(View.VISIBLE);

        //galleryBootomLayout = (LinearLayout) findViewById(R.id.galleryBootomLayout);
        //galleryBootomLayout.setVisibility(View.VISIBLE);


        gallerySubToolsLayout = (LinearLayout) findViewById(R.id.gallerySubToolsLayout);
        gallerySubToolsLayout.setVisibility(View.GONE);

        galleryAdjustSeekbarLLayout = (LinearLayout) findViewById(R.id.galleryAdjustSeekbarLLayout);
        galleryAdjustSeekbarLLayout.setOnClickListener(this);

        galleryEditToolsRecycleView = (RecyclerView) findViewById(R.id.galleryEditToolsRecycleView);
        galleryEditToolsRecycleView.setVisibility(View.VISIBLE);

        LinearLayoutManager mCollageEditToolsLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        galleryEditToolsRecycleView.setLayoutManager(mCollageEditToolsLManager);
        galleryEditToolsRecycleView.setAdapter(mGalleryEditToolsAdapter);


        gallerySubToolsRecycler = (RecyclerView) findViewById(R.id.gallerySubToolsRecycler);
        gallerySubToolsRecycler.setVisibility(View.GONE);

        LinearLayoutManager mGallerySubToolsAdapterLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        gallerySubToolsRecycler.setLayoutManager(mGallerySubToolsAdapterLManager);
        gallerySubToolsRecycler.setAdapter(gallerySubToolsAdapter);

        galleryAdjustToolsRecycler = (RecyclerView) findViewById(R.id.galleryAdjustToolsRecycler);
        galleryAdjustToolsRecycler.setVisibility(View.GONE);
        adjustToolsAdapter = new AdjustToolsAdapter(this);
        mAdjustToolsAdapterLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        galleryAdjustToolsRecycler.setLayoutManager(mAdjustToolsAdapterLManager);
        galleryAdjustToolsRecycler.setAdapter(adjustToolsAdapter);

        addToUndoReodList();
        hideShowUndoRedo();

        galleryBrightnessSeekBar = (SeekBar) findViewById(R.id.galleryBrightnessSeekBar);
        galleryBrightnessSeekBar.setVisibility(View.VISIBLE);
        galleryBrightnessSeekBar.setOnSeekBarChangeListener(this);

        galleryContrastSeekBar = (SeekBar) findViewById(R.id.galleryContrastSeekBar);
        galleryContrastSeekBar.setOnSeekBarChangeListener(this);

        gallerySaturationSeekBar = (SeekBar) findViewById(R.id.gallerySaturationSeekBar);
        gallerySaturationSeekBar.setOnSeekBarChangeListener(this);

        gallerySharpnessSeekBar = (SeekBar) findViewById(R.id.gallerySharpnessSeekBar);
        gallerySharpnessSeekBar.setOnSeekBarChangeListener(this);

        galleryVignetteSeekBar = (SeekBar) findViewById(R.id.galleryVignetteSeekBar);
        galleryVignetteSeekBar.setOnSeekBarChangeListener(this);

        galleryShadowSeekBar = (SeekBar) findViewById(R.id.galleryShadowSeekBar);
        galleryShadowSeekBar.setOnSeekBarChangeListener(this);

        galleryHighlightSeekBar = (SeekBar) findViewById(R.id.galleryHighlightSeekBar);
        galleryHighlightSeekBar.setOnSeekBarChangeListener(this);

        galleryTempSeekBar = (SeekBar) findViewById(R.id.galleryTempSeekBar);
        galleryTempSeekBar.setOnSeekBarChangeListener(this);

        galleryTintSeekBar = (SeekBar) findViewById(R.id.galleryTintSeekBar);
        galleryTintSeekBar.setOnSeekBarChangeListener(this);



        adjustClose = (TextView) findViewById(R.id.adjustClose);
        adjustClose.setOnClickListener(this);

        adjustDone = (TextView) findViewById(R.id.adjustDone);
        adjustDone.setOnClickListener(this);

    }

    @Override
    public void onBrightnessChanged(final int brightness) {

        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        //Bitmap finalImage = ((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap();
        gallerySelectedImageShape.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        //Bitmap finalImage = ((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap();
        gallerySelectedImageShape.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void ColorOverlaySubFilter(int depth, float red, float green, float blue) {

    }

    @Override
    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        gallerySelectedImageShape.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        // once the editing is done i.e seekbar is drag is completed,
        // apply the values on to filtered image
        //Bitmap mFilteredImage = ((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap();

        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        // myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalImage = myFilter.processFilter(bitmap);

        Constants.capturedImageBitmap = finalImage;
        //resetControls();

    }

    */
/**
     * Resets image edit controls to normal when new filter
     * is selected
     *//*

    private void resetControls() {
        if(!Constants.isAdjustGalleryImage) {
            galleryBrightnessSeekBar.setProgress(100);
            galleryContrastSeekBar.setProgress(0);
        }
        brightnessFinal = 0;
        // saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }


    private void loadImageFilters() {
        originalImage = Constants.capturedImageBitmap;
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        //finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        //gallerySelectedImageShape.setImageBitmap(originalImage);
    }

    public void setFiltersImage() {
        originalImage = Constants.capturedImageBitmap;
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        //finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        filters_viewpager = (ViewPager) findViewById(R.id.filters_viewpager);
        setupViewPager(filters_viewpager);
    }

    private void setupViewPager(ViewPager viewPager) {
        FiltersPagerAdapter adapter = new FiltersPagerAdapter(getSupportFragmentManager());
        // adding filter list fragment
        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);
        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        viewPager.setAdapter(adapter);
    }



    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls
        //resetControls();

        CustomProgressDialog.show(mContext, getResources().getString(R.string.loading_msg));
        originalImage = Constants.capturedImageBitmap;
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        gallerySelectedImageShape.setImageBitmap(filter.processFilter(filteredImage));
        addToUndoReodList();
        CustomProgressDialog.dismiss();
    }

    */
/**onClick*//*

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.backGalleryLLayout:

                if (Constants.isAdjustGalleryImage) {

                    //Constants.capturedImageBitmap = createBitmapFromLayout(adjustGalleryPhotoRLayout);
                    gallerySelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    //adjustGalleryRLayout.setVisibility(View.GONE);
                    gallerySelectedImageRLayout.setVisibility(View.VISIBLE);
                    galleryEditToolsRecycleView.setVisibility(View.VISIBLE);
                    blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                    galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                    gallerySubToolsLayout.setVisibility(View.GONE);
                    gallerySubToolsRecycler.setVisibility(View.GONE);
                    galleryAdjustToolsRecycler.setVisibility(View.GONE);
                    galleryEffectsLLayout.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustGalleryImage = false;
                }

                // adjustGalleryRLayout.setVisibility(View.GONE);
                gallerySelectedImageRLayout.setVisibility(View.VISIBLE);
                galleryEditToolsRecycleView.setVisibility(View.VISIBLE);

                blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                gallerySubToolsLayout.setVisibility(View.GONE);
                gallerySubToolsRecycler.setVisibility(View.GONE);
                galleryAdjustToolsRecycler.setVisibility(View.GONE);
                galleryEffectsLLayout.setVisibility(View.GONE);
                show_alert_back("Exit", "You want to save changes ?");
                break;

            case R.id.undoGalleryImageLayout:
                Constants.isAdjustGalleryImage = false;

                // adjustGalleryRLayout.setVisibility(View.GONE);
                gallerySelectedImageRLayout.setVisibility(View.VISIBLE);
                galleryEditToolsRecycleView.setVisibility(View.VISIBLE);
                blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                gallerySubToolsLayout.setVisibility(View.GONE);
                gallerySubToolsRecycler.setVisibility(View.GONE);
                galleryAdjustToolsRecycler.setVisibility(View.GONE);
                galleryEffectsLLayout.setVisibility(View.GONE);
                Log.e("Undo List Size", pathForTempList.size() + "");
                Log.e("Undo Index ", currentShowingIndex + "");

                if (currentShowingIndex > pathForTempList.size()) {
                    currentShowingIndex = currentShowingIndex - 1;
                    return;
                }
                hideShowUndoRedo();
                if (currentShowingIndex >= 1) {
                    onUndoPressed();
                } else {
                    Log.e("", "");
                }

                break;

            case R.id.redoGalleryImageLayout:
                Constants.isAdjustGalleryImage = false;

                //adjustGalleryRLayout.setVisibility(View.GONE);
                gallerySelectedImageRLayout.setVisibility(View.VISIBLE);
                galleryEditToolsRecycleView.setVisibility(View.VISIBLE);
                blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                gallerySubToolsLayout.setVisibility(View.GONE);
                gallerySubToolsRecycler.setVisibility(View.GONE);
                galleryAdjustToolsRecycler.setVisibility(View.GONE);
                galleryEffectsLLayout.setVisibility(View.GONE);
                Log.e("Redo List Size", pathForTempList.size() + "");
                Log.e("Redo Index ", currentShowingIndex + "");
                if (currentShowingIndex > pathForTempList.size()) {
                    currentShowingIndex = currentShowingIndex - 1;
                    return;
                }
                hideShowUndoRedo();
                if (currentShowingIndex < pathForTempList.size() - 1) {
                    onRedoPressed();
                } else {
                    Log.e("", "");
                }

                break;

            case R.id.saveGalleryLayout:

//                                if (Constants.isAdjustGalleryImage) {
//
//                                        //Constants.capturedImageBitmap = createBitmapFromLayout(adjustGalleryPhotoRLayout);
//
//                                        //gallerySelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);
//
//                                        //adjustGalleryRLayout.setVisibility(View.GONE);
//                                        gallerySelectedImageRLayout.setVisibility(View.VISIBLE);
//                                        galleryEditToolsRecycleView.setVisibility(View.VISIBLE);
//                                        blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
//                                        galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
//                                        gallerySubToolsLayout.setVisibility(View.GONE);
//                                        gallerySubToolsRecycler.setVisibility(View.GONE);
//                                        galleryEffectsLLayout.setVisibility(View.GONE);
//                                        addToUndoReodList();
//                                        Constants.isAdjustGalleryImage = false;
//                                }

                //adjustGalleryRLayout.setVisibility(View.GONE);
                gallerySelectedImageRLayout.setVisibility(View.VISIBLE);
                galleryEditToolsRecycleView.setVisibility(View.VISIBLE);
                blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                gallerySubToolsLayout.setVisibility(View.GONE);
                gallerySubToolsRecycler.setVisibility(View.GONE);
                galleryAdjustToolsRecycler.setVisibility(View.GONE);
                galleryEffectsLLayout.setVisibility(View.GONE);

                gallerySelectedImageShape.setLayoutParams(
                        new android.widget.RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

                if (gallerySelectedImageShape.getRotation() > 0) {
                    Bitmap bitmap = createBitmapFromLayout(gallerySelectedImageRLayout);
                    Utilities.saveSelfieProImage(mContext, bitmap);
                } else if (gallerySelectedImageShape.getRotation() == 0) {
                    Bitmap bitmap = ((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap();
                    Utilities.saveSelfieProImage(mContext, bitmap);
                }

                gallerySelectedImageShape.setLayoutParams(
                        new android.widget.RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                        ));
                gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

                if(addprefs!=null)
                    dispInterestialAdds();

                break;

            case R.id.blankGalleryLayoutTop:

                if (Constants.isAdjustGalleryImage) {
                    //Constants.capturedImageBitmap = createBitmapFromLayout(adjustGalleryPhotoRLayout);
                    gallerySelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    // adjustGalleryRLayout.setVisibility(View.GONE);
                    gallerySelectedImageRLayout.setVisibility(View.VISIBLE);
                    galleryEditToolsRecycleView.setVisibility(View.VISIBLE);
                    blankGalleryLayoutBottom.setVisibility(View.VISIBLE);

                    galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                    gallerySubToolsLayout.setVisibility(View.GONE);
                    gallerySubToolsRecycler.setVisibility(View.GONE);
                    galleryAdjustToolsRecycler.setVisibility(View.GONE);
                    galleryEffectsLLayout.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustGalleryImage = false;
                }

                //adjustGalleryRLayout.setVisibility(View.GONE);
                gallerySelectedImageRLayout.setVisibility(View.VISIBLE);
                galleryEditToolsRecycleView.setVisibility(View.VISIBLE);
                blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                gallerySubToolsLayout.setVisibility(View.GONE);
                gallerySubToolsRecycler.setVisibility(View.GONE);
                galleryAdjustToolsRecycler.setVisibility(View.GONE);
                galleryEffectsLLayout.setVisibility(View.GONE);

                break;

            case R.id.blankGalleryLayoutBottom:

                if (Constants.isAdjustGalleryImage) {

                    //Constants.capturedImageBitmap = createBitmapFromLayout(adjustGalleryPhotoRLayout);
                    //gallerySelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    // adjustGalleryRLayout.setVisibility(View.GONE);
                    gallerySelectedImageRLayout.setVisibility(View.VISIBLE);
                    galleryEditToolsRecycleView.setVisibility(View.VISIBLE);

                    blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                    galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                    gallerySubToolsLayout.setVisibility(View.GONE);
                    gallerySubToolsRecycler.setVisibility(View.GONE);
                    galleryAdjustToolsRecycler.setVisibility(View.GONE);
                    galleryEffectsLLayout.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustGalleryImage = false;
                }


                //adjustGalleryRLayout.setVisibility(View.GONE);
                gallerySelectedImageRLayout.setVisibility(View.VISIBLE);
                galleryEditToolsRecycleView.setVisibility(View.VISIBLE);
                blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                gallerySubToolsLayout.setVisibility(View.GONE);
                gallerySubToolsRecycler.setVisibility(View.GONE);
                galleryAdjustToolsRecycler.setVisibility(View.GONE);
                galleryEffectsLLayout.setVisibility(View.GONE);
                break;

            case R.id.adjustClose:
                if (Constants.isAdjustGalleryImage) {

                    gallerySelectedImageRLayout.setVisibility(View.VISIBLE);
                    blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                    galleryEditToolsRecycleView.setVisibility(View.VISIBLE);

                    galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                    gallerySubToolsLayout.setVisibility(View.GONE);
                    gallerySubToolsRecycler.setVisibility(View.GONE);
                    galleryAdjustToolsRecycler.setVisibility(View.GONE);
                    galleryEffectsLLayout.setVisibility(View.GONE);
                    Constants.isAdjustGalleryImage = false;
//                                        galleryBrightnessSeekBar.setVisibility(View.VISIBLE);
//                                        galleryContrastSeekBar.setVisibility(View.GONE);
                }

                gallerySelectedImageRLayout.setVisibility(View.VISIBLE);
                galleryEditToolsRecycleView.setVisibility(View.VISIBLE);
                blankGalleryLayoutBottom.setVisibility(View.VISIBLE);

                galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                gallerySubToolsLayout.setVisibility(View.GONE);
                gallerySubToolsRecycler.setVisibility(View.GONE);
                galleryAdjustToolsRecycler.setVisibility(View.GONE);
                galleryEffectsLLayout.setVisibility(View.GONE);

//                                galleryBrightnessSeekBar.setVisibility(View.VISIBLE);
//                                galleryContrastSeekBar.setVisibility(View.GONE);

                break;

            case R.id.adjustDone:

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


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GALLERY_REQUEST_TYPE:
                if (resultCode == RESULT_OK && null != data) {
                    CustomProgressDialog.show(mContext, getResources().getString(R.string.loading_msg));
                    Uri getUri = data.getData();
                    Bitmap bitmap = null;
                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), getUri);

                        Uri compressedUri = Uri.parse(compressImage(getImageUri(mContext, bitmap).toString()));
                        File imgFile = new File(compressImage(compressedUri.toString()));
                        Uri compressedUri2;
                        Bitmap compressedBitmap;
                        if (imgFile.exists()) {
                            compressedUri2 = Uri.fromFile(imgFile);
                            compressedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), compressedUri2);
                            Constants.capturedImageBitmap = compressedBitmap;
                        }
                        setFiltersImage();
                        gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        gallerySelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);
                        gallerySelectedImageShape.setRotation(0);
                        fixAntiAlias(gallerySelectedImageShape);


                        //  galleryBrightnessSeekBar.setProgress(0);
                        // galleryContrastSeekBar.setProgress(0);

                        deleteTempExtraFile();
                        addToUndoReodList();
                        hideShowUndoRedo();
                        Constants.isImageCroped = false;
                        Constants.isAdjustGalleryImage = false;

                        CustomProgressDialog.dismiss();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    //Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("Please try ", "again.");
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
                //Log.e(TAG, "Crop error: " + cropError);
                setResultCancelled();
                break;
            default:
                setResultCancelled();
        }
    }


    */
/****************AdjustTools Start**********************//*

    @Override
    public void onGalleryAdjustTollsSelected(GalleryAdjustToolsType galleryAdjustToolsType) {

                */
/*galleryAdjustToolsRecycler.setVisibility(View.GONE);
                galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                galleryEditToolsRecycleView.setVisibility(View.VISIBLE);*//*


        switch (galleryAdjustToolsType) {

            case BRIGHTNESS:
                blankGalleryLayoutBottom.setVisibility(View.GONE);
                galleryEditToolsRecycleView.setVisibility(View.GONE);
                gallerySubToolsLayout.setVisibility(View.VISIBLE);
                galleryContrastSeekBar.setVisibility(View.GONE);
                gallerySaturationSeekBar.setVisibility(View.GONE);
                galleryTempSeekBar.setVisibility(View.GONE);
                galleryBrightnessSeekBar.setVisibility(View.VISIBLE);
                galleryAdjustToolsRecycler.setVisibility(View.VISIBLE);
                galleryAdjustSeekbarLLayout.setVisibility(View.VISIBLE);

                break;

            case CONTRAST:
                blankGalleryLayoutBottom.setVisibility(View.GONE);
                galleryEditToolsRecycleView.setVisibility(View.GONE);
                gallerySubToolsLayout.setVisibility(View.VISIBLE);
                galleryBrightnessSeekBar.setVisibility(View.GONE);
                gallerySaturationSeekBar.setVisibility(View.GONE);
                galleryTempSeekBar.setVisibility(View.GONE);
                galleryContrastSeekBar.setVisibility(View.VISIBLE);
                galleryAdjustToolsRecycler.setVisibility(View.VISIBLE);
                galleryAdjustSeekbarLLayout.setVisibility(View.VISIBLE);


                break;
            case SATURATION:
                blankGalleryLayoutBottom.setVisibility(View.GONE);
                galleryEditToolsRecycleView.setVisibility(View.GONE);
                gallerySubToolsLayout.setVisibility(View.VISIBLE);
                galleryBrightnessSeekBar.setVisibility(View.GONE);
                galleryContrastSeekBar.setVisibility(View.GONE);
                galleryTempSeekBar.setVisibility(View.GONE);
                gallerySaturationSeekBar.setVisibility(View.VISIBLE);
                galleryAdjustToolsRecycler.setVisibility(View.VISIBLE);
                galleryAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                break;
            case SHARPNESS:

                break;
            case VIGNETTE:

                break;
            case SHADOW:

                break;
            case HIGH_LIGHT:

                break;

            case TEMP:
                blankGalleryLayoutBottom.setVisibility(View.GONE);
                galleryEditToolsRecycleView.setVisibility(View.GONE);
                gallerySubToolsLayout.setVisibility(View.VISIBLE);
                galleryBrightnessSeekBar.setVisibility(View.GONE);
                galleryContrastSeekBar.setVisibility(View.GONE);
                gallerySaturationSeekBar.setVisibility(View.GONE);
                galleryTempSeekBar.setVisibility(View.VISIBLE);
                galleryAdjustToolsRecycler.setVisibility(View.VISIBLE);
                galleryAdjustSeekbarLLayout.setVisibility(View.VISIBLE);


                break;
            case TINT:

                break;
            case DENOISE:

                break;
            case CURVE:

                break;
            case COLOR_BALANCE:

                break;

        }
    }

    */
/****************AdjustTools End**********************//*


    */
/****************EditTolls Start**********************//*

    @Override
    public void onGalleryEditTollsSelected(GalleryEditToolsType galleryEditToolsType) {
        switch (galleryEditToolsType) {

            case GALLERY:

                if (Constants.isAdjustGalleryImage) {

                    //Constants.capturedImageBitmap = createBitmapFromLayout(adjustGalleryPhotoRLayout);
                    gallerySelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    //adjustGalleryRLayout.setVisibility(View.GONE);
                    gallerySelectedImageRLayout.setVisibility(View.VISIBLE);

                    blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                    galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                    gallerySubToolsLayout.setVisibility(View.GONE);
                    gallerySubToolsRecycler.setVisibility(View.GONE);
                    galleryAdjustToolsRecycler.setVisibility(View.GONE);
                    galleryEffectsLLayout.setVisibility(View.GONE);

                    addToUndoReodList();
                    Constants.isAdjustGalleryImage = false;

                }

                //adjustGalleryRLayout.setVisibility(View.GONE);
                gallerySelectedImageRLayout.setVisibility(View.VISIBLE);

                blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                gallerySubToolsLayout.setVisibility(View.GONE);
                gallerySubToolsRecycler.setVisibility(View.GONE);
                galleryAdjustToolsRecycler.setVisibility(View.GONE);
                galleryEffectsLLayout.setVisibility(View.GONE);

                Utilities.openGallery(AdjustDemoActivity.this, GALLERY_REQUEST_TYPE);

                deleteTempExtraFile();

                break;


            case TOOLS:

                if (Constants.isAdjustGalleryImage) {
                    //Constants.capturedImageBitmap = createBitmapFromLayout(adjustGalleryPhotoRLayout);
                    gallerySelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    //adjustGalleryRLayout.setVisibility(View.GONE);
                    gallerySelectedImageRLayout.setVisibility(View.VISIBLE);

                    blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                    galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                    gallerySubToolsLayout.setVisibility(View.GONE);
                    gallerySubToolsRecycler.setVisibility(View.GONE);
                    galleryAdjustToolsRecycler.setVisibility(View.GONE);
                    galleryEffectsLLayout.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustGalleryImage = false;
                }

                // adjustGalleryRLayout.setVisibility(View.GONE);
                gallerySelectedImageRLayout.setVisibility(View.VISIBLE);

                galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                galleryEffectsLLayout.setVisibility(View.GONE);
                galleryAdjustToolsRecycler.setVisibility(View.GONE);

                Constants.capturedImageBitmap = ((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap();

                if (gallerySubToolsRecycler.getVisibility() == View.VISIBLE) {
                    blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                    gallerySubToolsLayout.setVisibility(View.GONE);
                    gallerySubToolsRecycler.setVisibility(View.GONE);
                } else {
                    blankGalleryLayoutBottom.setVisibility(View.GONE);
                    gallerySubToolsLayout.setVisibility(View.VISIBLE);
                    gallerySubToolsRecycler.setVisibility(View.VISIBLE);
                }
                break;

            case EFFECTS:

                if (Constants.isAdjustGalleryImage) {

                    //Constants.capturedImageBitmap = createBitmapFromLayout(adjustGalleryPhotoRLayout);

                    gallerySelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);
                    gallerySelectedImageShape.setLayoutParams(
                            new android.widget.RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                            ));
                    gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    //adjustGalleryRLayout.setVisibility(View.GONE);
                    gallerySelectedImageRLayout.setVisibility(View.VISIBLE);

                    blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                    galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                    gallerySubToolsLayout.setVisibility(View.GONE);
                    gallerySubToolsRecycler.setVisibility(View.GONE);
                    galleryAdjustToolsRecycler.setVisibility(View.GONE);
                    galleryEffectsLLayout.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustGalleryImage = false;
                }


                //adjustGalleryRLayout.setVisibility(View.GONE);
                gallerySelectedImageRLayout.setVisibility(View.VISIBLE);

                galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                gallerySubToolsRecycler.setVisibility(View.GONE);
                galleryAdjustToolsRecycler.setVisibility(View.GONE);

                setFiltersImage();

                if (galleryEffectsLLayout.getVisibility() == View.VISIBLE) {
                    blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                    gallerySubToolsLayout.setVisibility(View.GONE);
                    galleryEffectsLLayout.setVisibility(View.GONE);
                } else {
                    blankGalleryLayoutBottom.setVisibility(View.GONE);
                    gallerySubToolsLayout.setVisibility(View.VISIBLE);
                    galleryEffectsLLayout.setVisibility(View.VISIBLE);
                }
                break;

            case ADJUST:

                galleryAdjustToolsRecycler.setVisibility(View.VISIBLE);
                galleryBrightnessSeekBar.setVisibility(View.VISIBLE);
                galleryContrastSeekBar.setVisibility(View.VISIBLE);
                galleryEditToolsRecycleView.setVisibility(View.GONE);

                last_bitmap = bitmap().copy(bitmap().getConfig(), false);

                if (!Constants.isAdjustGalleryImage) {
                    gallerySelectedImageShape.setLayoutParams(
                            new android.widget.RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            ));

                    gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    Constants.capturedImageBitmap = ((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap();
                    finalImage =  Constants.capturedImageBitmap;

                    // keeping brightness value b/w -100 / +100
                    //galleryBrightnessSeekBar.setMax(100);
                    galleryBrightnessSeekBar.setProgress(200);

                    // keeping contrast value b/w 1.0 - 3.0
                    galleryContrastSeekBar.setMax(20);
                    galleryContrastSeekBar.setProgress(0);

                    // keeping saturation value b/w 0.0 - 3.0
                    gallerySaturationSeekBar.setMax(30);
                    gallerySaturationSeekBar.setProgress(10);


                    //galleryTempSeekBar.setMax(400);
                    //galleryTempSeekBar.setProgress(200);

////                    Constants.galleryAdjustImageWidth =  Constants.capturedImageBitmap.getWidth();
////                    Constants.galleryAdjustImageHeight =  Constants.capturedImageBitmap.getHeight() ;
//
//
//                                        adjustGalleryPhotoView.setLayoutParams(
//                                                new android.widget.RelativeLayout.LayoutParams(
//                                                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                                                        ViewGroup.LayoutParams.WRAP_CONTENT
//                                                ));
//
//                                        adjustGalleryPhotoView.setScaleType(ImageView.ScaleType.FIT_XY);
//                                        adjustGalleryPhotoView.setImageBitmap(Constants.capturedImageBitmap);
//                                        adjustGalleryPhotoView.setRotation(0);
//                                        fixAntiAlias(adjustGalleryPhotoView);
//
//                                        gallerySelectedImageRLayout.setVisibility(View.GONE);
//                                        adjustGalleryRLayout.setVisibility(View.VISIBLE);

                    Constants.isAdjustGalleryImage = true;

                    gallerySelectedImageShape.setLayoutParams(
                            new android.widget.RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                            ));
                    gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }


                gallerySubToolsRecycler.setVisibility(View.GONE);
                //galleryAdjustToolsRecycler.setVisibility(View.GONE);
                galleryEffectsLLayout.setVisibility(View.GONE);

                if (galleryAdjustToolsRecycler.getVisibility() == View.VISIBLE) {
                    blankGalleryLayoutBottom.setVisibility(View.GONE);
                    gallerySubToolsLayout.setVisibility(View.VISIBLE);
                    galleryAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                } else {
                    gallerySubToolsLayout.setVisibility(View.GONE);
                    galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                    blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                }

                                */
/*if (galleryAdjustSeekbarLLayout.getVisibility() == View.VISIBLE) {
                                        blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                                        gallerySubToolsLayout.setVisibility(View.GONE);
                                        galleryAdjustToolsRecycler.setVisibility(View.GONE);
                                        galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                                } else {
                                        blankGalleryLayoutBottom.setVisibility(View.GONE);
                                        gallerySubToolsLayout.setVisibility(View.VISIBLE);
                                        galleryAdjustToolsRecycler.setVisibility(View.VISIBLE);
                                         galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                                }*//*


                break;


            case EDIT:



                if (Constants.isAdjustGalleryImage) {

                    // Constants.capturedImageBitmap = createBitmapFromLayout(adjustGalleryPhotoRLayout);

                    gallerySelectedImageShape.setImageBitmap(Constants.capturedImageBitmap);

                    //adjustGalleryRLayout.setVisibility(View.GONE);
                    gallerySelectedImageRLayout.setVisibility(View.VISIBLE);

                    blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                    galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                    gallerySubToolsLayout.setVisibility(View.GONE);
                    gallerySubToolsRecycler.setVisibility(View.GONE);
                    galleryEffectsLLayout.setVisibility(View.GONE);
                    addToUndoReodList();
                    Constants.isAdjustGalleryImage = false;

                }

                //adjustGalleryRLayout.setVisibility(View.GONE);
                gallerySelectedImageRLayout.setVisibility(View.VISIBLE);

                blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                gallerySubToolsLayout.setVisibility(View.GONE);
                gallerySubToolsRecycler.setVisibility(View.GONE);
                galleryEffectsLLayout.setVisibility(View.GONE);

                gallerySelectedImageShape.setLayoutParams(
                        new android.widget.RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

                Constants.capturedImageBitmap = ((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap();
                mMyPrecfence.saveString(Constants.INTENT_TYPE, Constants.INTENT_TYPE_GALLERY);
                try {

                    Bitmap mBitmap = createBitmapFromLayout(gallerySelectedImageShape);
                    //ImageView   End
                    //Constants.galleryEditBitmapHeight  = gallerySelectedImageShape.getHeight();
                    //Constants.galleryEditBitmapWidth  = gallerySelectedImageShape.getWidth();

                    String imagePath = Utilities.saveBitmap_Temp(mBitmap);
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        Constants.imageUri = Uri.fromFile(imgFile);
                    }

                    Constants.done_Edited_ImageType_Collage = "false";
                    Intent intentGalleryImage = new Intent(AdjustDemoActivity.this, EditImageActivity.class);
                    intentGalleryImage.putExtra(Constants.URI_GALLERY, Constants.imageUri);
                    startActivity(intentGalleryImage);

                    gallerySelectedImageShape.setLayoutParams(
                            new android.widget.RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            ));

                    gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    Constants.capturedImageBitmap = ((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap();
                    //Constants.capturedImageBitmap =  createBitmapFromLayout(gallerySelectedImageRLayout);

                    Constants.galleryEditBitmap = Constants.capturedImageBitmap;
                    //Bitmap layoutBitmap = createBitmapFromLayout(gallerySelectedImageRLayout);
                    Constants.galleryEditBitmapHeight = Constants.galleryEditBitmap.getHeight();
                    Constants.galleryEditBitmapWidth = Constants.galleryEditBitmap.getWidth();

                    //Bitmap layoutBitmap = createBitmapFromLayout(gallerySelectedImageShape);
//                    Bitmap layoutBitmap = createBitmapFromLayout(gallerySelectedImageRLayout);
//                    Constants.galleryEditBitmapHeight  = layoutBitmap.getHeight();
//                    Constants.galleryEditBitmapWidth  = layoutBitmap.getWidth();

                    gallerySelectedImageShape.setLayoutParams(
                            new android.widget.RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                            ));

                    gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    */
/****************EditTolls End**********************//*


    */
/**************SubTolls  Start**********************//*

    @Override
    public void onGallerySubToolsSelected(GallerySubToolsType gallerySubToolsType) {
        switch (gallerySubToolsType) {

            case GALLERY_CROP:

                blankGalleryLayoutBottom.setVisibility(View.VISIBLE);
                galleryAdjustSeekbarLLayout.setVisibility(View.GONE);
                gallerySubToolsLayout.setVisibility(View.GONE);
                gallerySubToolsRecycler.setVisibility(View.GONE);
                galleryEffectsLLayout.setVisibility(View.GONE);

                Bitmap bitmap = ((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap();
                //Bitmap bitmap =  createBitmapFromLayout(galleryFrameShape);
                Constants.capturedImageBitmap = bitmap;
                if (bitmap != null) {
                    cropImageUri(getImageUri(mContext, bitmap));
                } else {
                    //Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("you have not", " selected image.");
                }
                break;

            case GALLERY_RT_LEFT:
                angleRotation = angleRotation - 10;
                gallerySelectedImageShape.setRotation(angleRotation);
                break;

            case GALLERY_RT_RIGHT:
                angleRotation = angleRotation + 10;
                gallerySelectedImageShape.setRotation(angleRotation);
                break;

            case GALLERY_RT_NONE:
                angleRotation = 0;
                if (gallerySelectedImageShape != null) {
                    gallerySelectedImageShape.setRotation(0);
                }
                fixAntiAlias(gallerySelectedImageShape);
                break;

            case GALLERY_FLIP:
                gallerySelectedImageShape.setImageBitmap(flipImage(((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap()));
                addToUndoReodList();
                Constants.capturedImageBitmap = ((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap();
                ;
                setFiltersImage();
                break;
        }
    }
    */
/**************SubTolls  End**********************//*


    */
/****************Crop Start**********************//*

    private void handleUCropResult(Intent data) {
        if (data == null) {
            setResultCancelled();
            return;
        }
        final Uri resultUri = UCrop.getOutput(data);
        okResult(resultUri);
    }


    private void okResult(Uri imagePath) {
        if (imagePath != null) {
            Constants.imageUri = imagePath;
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagePath);
                Constants.capturedImageBitmap = bitmap;
                cropedBitmap = bitmap;

                Constants.galleryEditBitmapWidth = cropedBitmap.getWidth();
                Constants.galleryEditBitmapHeight = cropedBitmap.getHeight();

                setFiltersImage();

                gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
                gallerySelectedImageShape.setImageBitmap(bitmap);
                //gallerySelectedImageShape.setRotation(0);

                fixAntiAlias(gallerySelectedImageShape);
                addToUndoReodList();
                hideShowUndoRedo();

                Constants.isImageCroped = true;
                Constants.isAdjustGalleryImage = false;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setResultCancelled() {
        Log.e("Crop Result", " Cancelled ");
    }

    */
/****************Crop End**********************//*



    */
/*********Seekbar*******onSeekbar Start**********************//*

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //onEditCompleted();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

         if (seekBar.getId() == R.id.galleryBrightnessSeekBar) {

            // applyTempSelectedEffect();
//
//            //Bitmap  bitmap = BitmapProcessing.brightness(Constants.capturedImageBitmap, progress);
//
//            //gallerySelectedImageShape.setImageBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true));
//
//            //Bitmap  bitmap = BitmapProcessing.brightness(Constants.galleryCapturedBitmap, galleryBrightnessSeekBar.getProgress() - 200);
//
//
////            onBrightnessChanged(progress-100);
////            // increaseBrightness(adjustGalleryPhotoView, progress);
////            progressBright = progress;
//
//
         }
        if (seekBar.getId() == R.id.galleryContrastSeekBar) {
            progress += 10;
            float floatVal = .10f * progress;
            //float floatVal =  progress;
            onContrastChanged(floatVal);
            //increaseContrast(adjustGalleryPhotoView, progress);
            progressContrast = progress;
        }

        if (seekBar.getId() == R.id.gallerySaturationSeekBar) {
            // converting int value to float
            // saturation values are b/w 0.0f - 3.0f
            float floatVal = .10f * progress;
            onSaturationChanged(floatVal);
            //increaseContrast(adjustGalleryPhotoView, progress);
            progressContrast = progress;
        }

//        if (seekBar.getId() == R.id.galleryTempSeekBar) {
//            int r = progress;
//            int g = (int).10f*progress;
//            int n =(int).20f*progress;
//
//            onTemp(gallerySelectedImageShape, r , g , n);
//        }



    }



    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {




        if (seekBar.getId() == R.id.galleryBrightnessSeekBar) {

            //Bitmap  bitmap = BitmapProcessing.brightness(Constants.capturedImageBitmap, galleryBrightnessSeekBar.getProgress()-100);
            //new ApplyEffects(bitmap()).execute("brightness");

            //gallerySelectedImageShape.setImageBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true));

//            if (galleryBrightnessSeekBar.getProgress() == 200) {
//
//                selected_effect = "";
//                galleryBrightnessSeekBar.setOnSeekBarChangeListener(null);
//
//            } else {
//                selected_effect = selected_effect + "_" + galleryBrightnessSeekBar.getProgress();			}
//
//
          applyTempSelectedEffect();

            Toaster.make(getApplicationContext(), "Stop Tracking Touch");
        }

        //onEditCompleted();

    }


//
//    private void increaseBrightness(ImageView mImageView, int progressValue) {
//        mImageView.setColorFilter(ColorFilterGenerator.adjustBrightness(progressValue));
//    }
//
//    private void increaseContrast(ImageView mImageView, int progressValue) {
//        mImageView.setColorFilter(ColorFilterGenerator.adjustContrast(progressValue));
//    }
//
//    private void onTemp(ImageView mImageView, int r, int g, int b) {
//        mImageView.setColorFilter(ColorFilterGenerator.adjustTemperature(r , g , b));
//    }

    */
/****************onSeekbar End**********************//*



    */
/*New*//*




    private void applyTempSelectedEffect() {
       applyEffect(selected_effect, false, true);

//        Bitmap input_bitmap = bitmap();
//        Bitmap bitmap;
//        bitmap = input_bitmap.copy(input_bitmap.getConfig(), true);
//        input_bitmap.recycle();
//        input_bitmap = null;
//
//        Bitmap mBitmap = BitmapProcessing.brightness(bitmap, galleryBrightnessSeekBar.getProgress() - 100);
//        try {
//            if (mBitmap != null) {
//                gallerySelectedImageShape.setImageBitmap(mBitmap);
//            } else {
//                Toaster.make(getApplicationContext(), "R.string.error_img_not_found");
//                //backToMain();
//            }
//        } catch (Exception e) {
//            Toaster.make(getApplicationContext(), "R.string.error_img_not_found");
//            //backToMain();
//        }
//        mBitmap.recycle();
//        mBitmap = null;
    }


    private void applyEffect(String effect, boolean set, boolean apply) {
//        if (set) {
//            //effects.add(effect);
//            try {
//                last_bitmap.recycle();
//                last_bitmap = null;
//            } catch(Exception e) {}
//        } else {
//            setImage(last_bitmap.copy(last_bitmap.getConfig(), true));
//        }
        if (apply) {
            new ApplyEffects(bitmap()).execute("brightness");
        }
    }


    private class ApplyEffects extends AsyncTask<String, Void, Bitmap> {
        Bitmap bitmap;

        public ApplyEffects(Bitmap input_bitmap) {
            bitmap = input_bitmap.copy(input_bitmap.getConfig(), true);
            input_bitmap.recycle();
            input_bitmap = null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                gallerySelectedImageShape.setImageBitmap(bitmap);
            } catch (Exception e) {}

        }

        @Override
        protected Bitmap doInBackground(String... input_effects) {
            for (String effect : input_effects) {

//                if (effect.startsWith("brightness") && effect.contains("_")) {
//
//                    String[] properties = effect.split("_");
//                    effect = properties[0];
//                    galleryBrightnessSeekBar.setProgress(Integer.parseInt(properties[1]));
//                }
                // Apply Filters
                if (effect.equals("brightness")) {
                    try{
                        bitmap = BitmapProcessing.brightness(bitmap, galleryBrightnessSeekBar.getProgress() - 100);
                    } catch (Exception e) {
                        Toaster.make(getApplicationContext(), "R.string.error_apply_effect");
                    }
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
                setImage(bitmap);
        }
    }


    private Bitmap bitmap() {
        try {
            return ((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap();
        } catch (Exception e) {
            return null;
        }
    }
    private void setImage(Bitmap bitmap) {
        //hideLoading();
        try {
            if (bitmap != null) {
                gallerySelectedImageShape.setImageBitmap(bitmap);
            } else {
                Toaster.make(getApplicationContext(), "R.string.error_img_not_found");
                //backToMain();
            }
        } catch (Exception e) {
            Toaster.make(getApplicationContext(), "R.string.error_img_not_found");
            //backToMain();
        }
    }

    */
/*****************Undo/Redo Start****************//*

    private void addToUndoReodList() {

        hideShowUndoRedo();

        Bitmap mainBitmap = ((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap();

        String imagePath = Utilities.saveBitmap_Temp(mainBitmap);
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Constants.imageUri = Uri.fromFile(imgFile);
            mainPathUri = Constants.imageUri;
            //Log.e("added Undo/Reod Uri ", Constants.imageUri+"");
        }

        pathForTempList.add(mainPathUri);
        currentShowingIndex = currentShowingIndex + 1;

        //Log.e("added List Size", pathForTempList.size()+"");
        //Log.e("Index " ,  currentShowingIndex+"");
        hideShowUndoRedo();
    }


    private void onUndoPressed() {
        String mUri = getUndoPath();
        File imgFile = new File(mUri);
        if (imgFile.exists()) {
            Constants.imageUri = Uri.fromFile(imgFile);
            mainPathUri = Constants.imageUri;
        }
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mainPathUri);
            Constants.capturedImageBitmap = bitmap;

            gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_CENTER);
            gallerySelectedImageShape.setImageBitmap(bitmap);
            //gallerySelectedImageShape.setRotation(0);
            fixAntiAlias(gallerySelectedImageShape);

            hideShowUndoRedo();

            //increaseBrightness(gallerySelectedImageShape, 0);
            //increaseContrast(gallerySelectedImageShape, 0);

            galleryBrightnessSeekBar.setProgress(0);
            galleryContrastSeekBar.setProgress(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("Set Undo Uri   ", Constants.imageUri + "");
    }

    private String getUndoPath() {
        if (currentShowingIndex >= 1 && currentShowingIndex != pathForTempList.size()) {
            currentShowingIndex = currentShowingIndex - 1;
        } else if (currentShowingIndex > 1 && currentShowingIndex == pathForTempList.size()) {
            currentShowingIndex = currentShowingIndex - 2;
        } else {
            currentShowingIndex = 0;
        }
        // Log.e("getUndoPath ", currentShowingIndex + "");
        return pathForTempList.get(currentShowingIndex).getPath();
    }

    private void onRedoPressed() {
        String mUri = getRedoPath();
        File imgFile = new File(mUri);
        if (imgFile.exists()) {
            Constants.imageUri = Uri.fromFile(imgFile);
            mainPathUri = Constants.imageUri;
        }
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mainPathUri);
            Constants.capturedImageBitmap = bitmap;

            gallerySelectedImageShape.setImageBitmap(bitmap);
            //gallerySelectedImageShape.setRotation(0);
            fixAntiAlias(gallerySelectedImageShape);
            hideShowUndoRedo();
            //increaseBrightness(gallerySelectedImageShape, 0);
            //increaseContrast(gallerySelectedImageShape, 0);
            galleryBrightnessSeekBar.setProgress(0);
            galleryContrastSeekBar.setProgress(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private String getRedoPath() {
        currentShowingIndex = currentShowingIndex + 1;
        //  Log.e("getRedoPath ", currentShowingIndex + "");
        return pathForTempList.get(currentShowingIndex).getPath();
    }

    private void hideShowUndoRedo() {
        Log.e("Hide Show Size ", pathForTempList.size() + "");
        Log.e("Hide Show Index ", currentShowingIndex + "");
        if (pathForTempList.size() <= 1) {
            hideUndo();
            hideRedo();
        }
        if (pathForTempList.size() > 1 && currentShowingIndex == 0) {
            hideUndo();
            showRedo();
        }
        if (pathForTempList.size() > 1 && currentShowingIndex != 0 && currentShowingIndex != pathForTempList.size() - 1) {
            showUndo();
            showRedo();
        }
        if (pathForTempList.size() > 1 && currentShowingIndex == pathForTempList.size() - 1) {
            showUndo();
            hideRedo();
        }
        if (pathForTempList.size() > 1 && currentShowingIndex == pathForTempList.size()) {
            showUndo();
            hideRedo();
        }
        if (pathForTempList.size() > 1 && currentShowingIndex > pathForTempList.size()) {
            showUndo();
            hideRedo();
        }
    }


    private void showUndo() {
        undoGalleryImageView.setImageResource(R.drawable.ic_undo);
        undoGalleryTextView.setTextColor(getResources().getColor(R.color.color_icon));
    }

    private void hideUndo() {
        undoGalleryImageView.setImageResource(R.drawable.ic_undo_none);
        undoGalleryTextView.setTextColor(getResources().getColor(R.color.undo_redo_none));
    }

    private void showRedo() {
        redoGalleryImageView.setImageResource(R.drawable.ic_redo);
        redoGalleryTextView.setTextColor(getResources().getColor(R.color.color_icon));
    }

    private void hideRedo() {
        redoGalleryImageView.setImageResource(R.drawable.ic_redo_none);
        redoGalleryTextView.setTextColor(getResources().getColor(R.color.undo_redo_none));
    }

    */
/*****************Undo/Redo End****************//*


//Method to do flip action
    public Bitmap flipImage(Bitmap image_bitmap) {
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

    public Bitmap createBitmapFromLayout(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, view.getWidth(), view.getHeight());
        Canvas mBitCanvas = new Canvas(bitmap);
        view.draw(mBitCanvas);
        return bitmap;
    }

    */
/*************************Crop Image Start**********************************//*

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

    */
/*************************Crop Image End**********************************//*


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

    public void show_alert_back(String title, String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdjustDemoActivity.this);
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
                //Bitmap bitmap = ((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap();
                Bitmap bitmap = createBitmapFromLayout(gallerySelectedImageRLayout);
                Utilities.saveSelfieProImage(mContext, bitmap);
                finish();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();

        Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button b1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        if (b != null)
            b.setTextColor(getResources().getColor(R.color.green_color_picker));
        if (b1 != null)
            b1.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void deleteTempExtraFile() {
        try {
            File directory = new File(Constants.tempfolder);
            File[] fList = directory.listFiles();
            Log.e("Files are ", fList + "");
            for (File file : fList) {
                if (file.isFile() && file.exists()) {
                    file.delete();
                } else {
                    Log.e("Files are ", " not available.");
                }
            }
            pathForTempList.clear();
            currentShowingIndex = 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private int getScreenWidth() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }

    private int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.heightPixels;
        return width;
    }


    */
/*************Compress Image Start************//*


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
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Gallery/Images");
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

    */
/*************Compress Image End************//*



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
        // super.onBackPressed();
        show_alert_back("Exit", "You want to save changes ?");
        return;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteTempExtraFile();
    }


}



*/
