package com.mojodigi.selfiepro.collage;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.mojodigi.selfiepro.AddsUtility.AddConstants;
import com.mojodigi.selfiepro.AddsUtility.AddMobUtils;
import com.mojodigi.selfiepro.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.selfiepro.R;
import com.mojodigi.selfiepro.activity.EditImageActivity;
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
import java.util.Random;

public class CollageActivity extends AppCompatActivity implements OnAdjustTollsSelected, EditImageListener,  FiltersListFragment.FiltersListFragmentListener, View.OnClickListener,

        OnCollageLayoutSelected, OnCollageEditTollsSelected , SeekBar.OnSeekBarChangeListener, OnCollageSubToolsSelected {

    private static final String TAG = CollageActivity.class.getSimpleName();

    private Context mContext ;

    private static final int PICK_GALLARY_REQUEST_ONE = 111;
    private static final int PICK_GALLARY_REQUEST_TWO = 222;
    private static final int PICK_GALLARY_REQUEST_THREE = 333;
    private static final int PICK_GALLARY_REQUEST_FOUR = 444;
    private static final int PICK_GALLARY_REQUEST_FIVE = 555;
    private static final int PICK_GALLARY_REQUEST_SIX = 666;

    private boolean isSelectedImage = false;
    private boolean isSelectedImage1 = false;
    private boolean isSelectedImage2 = false;
    private boolean isSelectedImage3 = false;
    private boolean isSelectedImage4 = false;
    private boolean isSelectedImage5 = false;
    private boolean isSelectedImage6 = false;

    private boolean isGalleryPickedImage1 = false;
    private boolean isGalleryPickedImage2 = false;
    private boolean isGalleryPickedImage3 = false;
    private boolean isGalleryPickedImage4 = false;
    private boolean isGalleryPickedImage5 = false;
    private boolean isGalleryPickedImage6 = false;

    private boolean isEmptyImage1 = false;
    private boolean isEmptyImage2 = false;
    private boolean isEmptyImage3 = false;
    private boolean isEmptyImage4 = false;
    private boolean isEmptyImage5 = false;
    private boolean isEmptyImage6 = false;

    boolean isSelectedFrame1_0 = true;
    boolean isSelectedFrame2_1 = false;
    boolean isSelectedFrame2_2 = false;
    boolean isSelectedFrame3_1 = false;
    boolean isSelectedFrame3_2 = false;
    boolean isSelectedFrame3_3 = false;
    boolean isSelectedFrame4_1 = false;
    boolean isSelectedFrame4_2 = false;
    boolean isSelectedFrame4_3 = false;
    boolean isSelectedFrame4_4 = false;
    boolean isSelectedFrame6_1 = false;

    private float angleRotation1 = 0, angleRotation2 = 0, angleRotation3 = 0, angleRotation4 = 0, angleRotation5 = 0, angleRotation6 = 0;

    private RelativeLayout mCollageRLayout;

    private LinearLayout backArrowCollageLLayout,  mCollageLLayout, collageEffectsLLayout   , collageBlankLayoutTop, saveCollageLLayout, collageAdjustSeekbarLLayout, collageBlankLayoutBottom, collageSubToolsLayout;

    private MyPreference mMyPrecfence = null;

    private RecyclerView collageEditToolsRecycleView ,collageLayoutsRecycler  ,collageAdjustToolsRecycler, collageSubToolsRecycler;


    private CollageLayoutsAdapter  mCollageLayoutsAdapter ;
    private CollageEditToolsAdapter mCollageEditToolsAdapter;
    private CollageSubToolsAdapter   collageSubToolsAdapter  ;
    private CollageAdjustToolsAdapter   adjustToolsAdapter  ;


    private boolean imageSelectedOrNot = false;
    private boolean isSelectedEffects = false;

    private SeekBar collageBrightnessSeekBar, collageContrastSeekBar , collageSaturationSeekBar ,collageSharpnessSeekBar
            , collageVignetteSeekBar , collageShadowSeekBar , collageHighlightSeekBar
            , collageTempSeekBar , collageTintSeekBar ,  collageDenoiseSeekBar , collageCurveSeekBar, collageColorBalanceSeekBar;


    private FrameLayout collageFrameShape1, collageFrameShape2, collageFrameShape3, collageFrameShape4, collageFrameShape5, collageFrameShape6;

    private Uri selectedImageUri1, selectedImageUri2, selectedImageUri3, selectedImageUri4, selectedImageUri5, selectedImageUri6;

    private PhotoView selectedImageView , collageImageShape1, collageImageShape2, collageImageShape3, collageImageShape4, collageImageShape5, collageImageShape6;

    private Bitmap cropedBitmap ;
    private TextView adjustClose , adjustDone ;

    public static CollageActivity instance;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private Bitmap originalImage;
    private Bitmap filteredImage;
    private FiltersListFragment filtersListFragment;
    private ViewPager filters_viewpager;

    private SharedPreferenceUtil addprefs;
    private View adContainer;

    public static final double PI = 3.14159d;
    public static final double FULL_CIRCLE_DEGREE = 360d;
    public static final double HALF_CIRCLE_DEGREE = 180d;
    public static final double RANGE = 256d;

    final static int KERNAL_WIDTH = 3;
    final static int KERNAL_HEIGHT = 3;

    private Bitmap finalImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(instance==null) {
            instance = CollageActivity.this;
        }
        if (mContext == null) {
            mContext = CollageActivity.this;
        }

        Constants.isImageCroped1=false;
        Constants.isImageCroped2=false;
        Constants.isImageCroped3=false;
        Constants.isImageCroped4=false;
        Constants.isImageCroped5=false;
        Constants.isImageCroped6=false;

        Constants.isAdjustSelected = false;

        if (mMyPrecfence == null) {
            mMyPrecfence = MyPreference.getMyPreferenceInstance(mContext);
        }

        initView();


        addprefs = new SharedPreferenceUtil(mContext);
        AddMobUtils adutil = new AddMobUtils();

        if(AddConstants.checkIsOnline(mContext) && adContainer !=null && addprefs !=null)
        {
            String AddPrioverId=addprefs.getStringValue(AddConstants.ADD_PROVIDER_ID, AddConstants.NOT_FOUND);
            if(AddPrioverId.equalsIgnoreCase(AddConstants.FaceBookAddProividerId))
            {
                adutil.dispFacebookBannerAdd(mContext,addprefs , CollageActivity.this);
            }
        }
        else {
            Log.e("","");
        }
    }



    @Override
    protected void onResume() {
        super.onResume();

        collageBlankLayoutBottom.setVisibility(View.VISIBLE);
        collageBlankLayoutTop.setVisibility(View.VISIBLE);
        collageEditToolsRecycleView.setVisibility(View.VISIBLE);
        collageAdjustSeekbarLLayout.setVisibility(View.GONE);
        collageLayoutsRecycler.setVisibility(View.GONE);
        collageSubToolsRecycler.setVisibility(View.GONE);
        collageSubToolsLayout.setVisibility(View.GONE);
        collageEffectsLLayout.setVisibility(View.GONE);
        collageAdjustToolsRecycler.setVisibility(View.GONE);



        Constants.isAdjustSelected = false;

        if(Constants.editImageUri.equalsIgnoreCase("true")) {
            CustomProgressDialog.show(mContext,getResources().getString(R.string.loading_msg));
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Constants.imageUri);
                Constants.capturedImageBitmap = bitmap;
                Constants.capturedImageBitmap1 = bitmap;
                Constants.collageOrignalBitmap1 = bitmap;

                loadImageFilters();

            } catch (IOException e) {
                e.printStackTrace();
            }


            collageImageShape1 = (PhotoView) findViewById(R.id.collageImageShape1);
            collageImageShape1.setBackgroundColor(getResources().getColor(R.color.white));
            collageImageShape1.setScaleType(ImageView.ScaleType.FIT_CENTER);
            selectedImageUri1 = Constants.imageUri;
            collageImageShape1.setImageURI(Constants.imageUri);
            collageImageShape1.setRotation(0);
            fixAntiAlias(collageImageShape1);

            //collageBrightness.setProgress(0);
            //collageBrightness.setProgress(0);

            isSelectedImage1 = true;
            isGalleryPickedImage1 = true;
            imageSelectedOrNot = true;

            collageImageShape1.setOnClickListener(this);
            collageImageShape1.setClickable(false);
            CustomProgressDialog.dismiss();

        }
        Constants.editImageUri = "false";
    }




    public void loadImageFilters() {
        filters_viewpager =(ViewPager) findViewById(R.id.filters_viewpager);
        setupViewPager(filters_viewpager);

        if (isSelectedImage1) {
            originalImage = Constants.capturedImageBitmap1;
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        }
        else if (isSelectedImage2) {
            originalImage = Constants.capturedImageBitmap2;
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        }
        else if (isSelectedImage3) {
            originalImage = Constants.capturedImageBitmap3;
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        }
        else if (isSelectedImage4) {
            originalImage = Constants.capturedImageBitmap4;
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        }
        else if (isSelectedImage5) {
            originalImage = Constants.capturedImageBitmap5;
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        }
        else if (isSelectedImage6) {
            originalImage = Constants.capturedImageBitmap6;
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        }
        filters_viewpager =(ViewPager) findViewById(R.id.filters_viewpager);
        setupViewPager(filters_viewpager);
    }

    // adding filter list fragment
    private void setupViewPager(ViewPager viewPager) {
        FiltersPagerAdapter adapter = new FiltersPagerAdapter(getSupportFragmentManager());
        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);
        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        viewPager.setAdapter(adapter);
    }

    // applying the selected filter
    @Override
    public void onFilterSelected(Filter filter) {
        CustomProgressDialog.show(mContext,getResources().getString(R.string.loading_msg));
        if (isSelectedImage1) {
            originalImage = Constants.capturedImageBitmap1;
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            //finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
            collageImageShape1.setImageBitmap(filter.processFilter(filteredImage));
        }
        else if (isSelectedImage2) {
            originalImage = Constants.capturedImageBitmap2;
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            //finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
            collageImageShape2.setImageBitmap(filter.processFilter(filteredImage));
        }
        else if (isSelectedImage3) {
            originalImage = Constants.capturedImageBitmap3;
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            //finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
            collageImageShape3.setImageBitmap(filter.processFilter(filteredImage));
        }
        else if (isSelectedImage4) {
            originalImage = Constants.capturedImageBitmap4;
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            //finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
            collageImageShape4.setImageBitmap(filter.processFilter(filteredImage));
        }
        else if (isSelectedImage5) {
            originalImage = Constants.capturedImageBitmap5;
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            //finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
            collageImageShape5.setImageBitmap(filter.processFilter(filteredImage));
        }
        else if (isSelectedImage6) {
            originalImage = Constants.capturedImageBitmap6;
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            //finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
            collageImageShape6.setImageBitmap(filter.processFilter(filteredImage));
        }
        CustomProgressDialog.dismiss();
    }



    private void initView() {

        adContainer = findViewById(R.id.adMobView);

        mCollageLayoutsAdapter  = new CollageLayoutsAdapter (this);
        mCollageEditToolsAdapter = new CollageEditToolsAdapter(this);
        collageSubToolsAdapter   = new CollageSubToolsAdapter  (this);

        mCollageRLayout = (RelativeLayout) findViewById(R.id.idCollageRLayout);
        mCollageLLayout = (LinearLayout) findViewById(R.id.idCollageLLayout);
        mCollageLLayout.setOnClickListener(this);
        collageEffectsLLayout   = (LinearLayout) findViewById(R.id.collegeEffectsLLayout);

        collageFrameShape1 = (FrameLayout) findViewById(R.id.collageFrameShape1);
        collageImageShape1 = (PhotoView) findViewById(R.id.collageImageShape1);
        collageImageShape1.setScaleType(ImageView.ScaleType.CENTER);
        collageImageShape1.setOnClickListener(this);

        selectedImageView = collageImageShape1;

        backArrowCollageLLayout = (LinearLayout) findViewById(R.id.backArrowCollageLLayout);
        saveCollageLLayout = (LinearLayout) findViewById(R.id.saveCollageLLayout);

        backArrowCollageLLayout.setOnClickListener(this);
        saveCollageLLayout.setOnClickListener(this);

        collageBlankLayoutTop = (LinearLayout) findViewById(R.id.blankCollageLayoutTop);
        collageBlankLayoutTop.setOnClickListener(this);
        collageBlankLayoutTop.setVisibility(View.VISIBLE);
        collageBlankLayoutBottom = (LinearLayout) findViewById(R.id.blankCollageLayoutBottom);
        collageBlankLayoutBottom.setOnClickListener(this);
        collageBlankLayoutBottom.setVisibility(View.VISIBLE);

        collageAdjustSeekbarLLayout = (LinearLayout) findViewById(R.id.collageAdjustSeekbarLLayout);
        collageAdjustSeekbarLLayout.setOnClickListener(this);

        collageSubToolsLayout = (LinearLayout) findViewById(R.id.collageSubToolsLayout);
        collageSubToolsLayout.setVisibility(View.VISIBLE);

        collageLayoutsRecycler = (RecyclerView) findViewById(R.id.collageLayoutsRecycler);
        collageLayoutsRecycler.setVisibility(View.VISIBLE);

        LinearLayoutManager mCollageListLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        collageLayoutsRecycler.setLayoutManager(mCollageListLManager);
        collageLayoutsRecycler.setAdapter(mCollageLayoutsAdapter );

        collageEditToolsRecycleView = (RecyclerView) findViewById(R.id.idCollageEditToolsRecycleView);
        LinearLayoutManager mCollageEditToolsLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        collageEditToolsRecycleView.setLayoutManager(mCollageEditToolsLManager);
        collageEditToolsRecycleView.setAdapter(mCollageEditToolsAdapter);

        collageSubToolsRecycler = (RecyclerView) findViewById(R.id.collageSubToolsRecycler);
        collageSubToolsRecycler.setVisibility(View.GONE);
        LinearLayoutManager collageSubToolsAdapterLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        collageSubToolsRecycler.setLayoutManager(collageSubToolsAdapterLManager);
        collageSubToolsRecycler.setAdapter(collageSubToolsAdapter  );

        collageAdjustToolsRecycler = (RecyclerView) findViewById(R.id.collageAdjustToolsRecycler);
        collageAdjustToolsRecycler.setVisibility(View.GONE);
        adjustToolsAdapter = new CollageAdjustToolsAdapter(this);
        LinearLayoutManager mAdjustToolsAdapterLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        collageAdjustToolsRecycler.setLayoutManager(mAdjustToolsAdapterLManager);
        collageAdjustToolsRecycler.setAdapter(adjustToolsAdapter);

        collageBrightnessSeekBar = (SeekBar) findViewById(R.id.collageBrightnessSeekBar);
        collageBrightnessSeekBar.setOnSeekBarChangeListener(this);

        collageContrastSeekBar = (SeekBar) findViewById(R.id.collageContrastSeekBar);
        collageContrastSeekBar.setOnSeekBarChangeListener(this);

        collageSaturationSeekBar = (SeekBar) findViewById(R.id.collageSaturationSeekBar);
        collageSaturationSeekBar.setOnSeekBarChangeListener(this);

        collageSharpnessSeekBar = (SeekBar) findViewById(R.id.collageSharpnessSeekBar);
        collageSharpnessSeekBar.setOnSeekBarChangeListener(this);

        collageVignetteSeekBar = (SeekBar) findViewById(R.id.collageVignetteSeekBar);
        collageVignetteSeekBar.setOnSeekBarChangeListener(this);

        collageShadowSeekBar = (SeekBar) findViewById(R.id.collageShadowSeekBar);
        collageShadowSeekBar.setOnSeekBarChangeListener(this);

        collageHighlightSeekBar = (SeekBar) findViewById(R.id.collageHighlightSeekBar);
        collageHighlightSeekBar.setOnSeekBarChangeListener(this);

        collageTempSeekBar = (SeekBar) findViewById(R.id.collageTempSeekBar);
        collageTempSeekBar.setOnSeekBarChangeListener(this);

        collageTintSeekBar = (SeekBar) findViewById(R.id.collageTintSeekBar);
        collageTintSeekBar.setOnSeekBarChangeListener(this);

        collageDenoiseSeekBar = (SeekBar) findViewById(R.id.collageDenoiseSeekBar);
        collageDenoiseSeekBar.setOnSeekBarChangeListener(this);

        collageCurveSeekBar = (SeekBar) findViewById(R.id.collageCurveSeekBar);
        collageCurveSeekBar.setOnSeekBarChangeListener(this);

        collageColorBalanceSeekBar = (SeekBar) findViewById(R.id.collageColorBalanceSeekBar);
        collageColorBalanceSeekBar.setOnSeekBarChangeListener(this);

        adjustClose = (TextView) findViewById(R.id.adjustClose);
        adjustClose.setOnClickListener(this);

        adjustDone = (TextView) findViewById(R.id.adjustDone);
        adjustDone.setOnClickListener(this);



    }

    private void initView2() {
        collageFrameShape1 = (FrameLayout) findViewById(R.id.collageFrameShape1);
        collageImageShape1 = (PhotoView) findViewById(R.id.collageImageShape1);
        collageImageShape1.setScaleType(ImageView.ScaleType.CENTER);
        collageImageShape1.setOnClickListener(this);

        collageFrameShape2 = (FrameLayout) findViewById(R.id.collageFrameShape2);
        collageImageShape2 = (PhotoView) findViewById(R.id.collageImageShape2);
        collageImageShape2.setScaleType(ImageView.ScaleType.CENTER);
        collageImageShape2.setOnClickListener(this);
    }


    private void initView3() {
        collageFrameShape3 = (FrameLayout) findViewById(R.id.collageFrameShape3);
        collageImageShape3 = (PhotoView) findViewById(R.id.collageImageShape3);
        collageImageShape3.setScaleType(ImageView.ScaleType.CENTER);
        collageImageShape3.setOnClickListener(this);
    }


    private void initView4() {
        collageFrameShape4 = (FrameLayout) findViewById(R.id.collageFrameShape4);
        collageImageShape4 = (PhotoView) findViewById(R.id.collageImageShape4);
        collageImageShape4.setScaleType(ImageView.ScaleType.CENTER);
        collageImageShape4.setOnClickListener(this);
    }


    private void initView5() {
        collageFrameShape5 = (FrameLayout) findViewById(R.id.collageFrameShape5);
        collageImageShape5 = (PhotoView) findViewById(R.id.collageImageShape5);
        collageImageShape5.setScaleType(ImageView.ScaleType.CENTER);
        collageImageShape5.setOnClickListener(this);
    }

    private void initView6() {
        collageFrameShape6 = (FrameLayout) findViewById(R.id.collageFrameShape6);
        collageImageShape6 = (PhotoView) findViewById(R.id.collageImageShape6);
        collageImageShape6.setScaleType(ImageView.ScaleType.CENTER);
        collageImageShape6.setOnClickListener(this);
    }


    /**onClick*/
    @Override
    public void onClick(View view) {

//        collageBlankLayoutTop.setVisibility(View.VISIBLE);
//        collageBlankLayoutBottom.setVisibility(View.VISIBLE);
//        collageEditToolsRecycleView.setVisibility(View.VISIBLE);
//        collageAdjustSeekbarLLayout.setVisibility(View.GONE);
//        collageLayoutsRecycler.setVisibility(View.GONE);
//        collageSubToolsRecycler.setVisibility(View.GONE);
//        collageSubToolsLayout.setVisibility(View.GONE);
//        collageEffectsLLayout  .setVisibility(View.GONE);
//        collageAdjustToolsRecycler.setVisibility(View.GONE);


        switch (view.getId()) {

            case R.id.backArrowCollageLLayout:

                collageBlankLayoutTop.setVisibility(View.VISIBLE);
                collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                collageEditToolsRecycleView.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageAdjustToolsRecycler.setVisibility(View.GONE);
                Constants.isAdjustSelected = false;
                if(collageFrameShape1!=null)
                    collageFrameShape1.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape2!=null)
                    collageFrameShape2.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape3!=null)
                    collageFrameShape3.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape4!=null)
                    collageFrameShape4.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape5!=null)
                    collageFrameShape5.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape6!=null)
                    collageFrameShape6.setBackgroundColor(getResources().getColor(R.color.white));

                show_alert_back("Exit", "Are you sure you want to exit Editor ?");
                break;

            case R.id.blankCollageLayoutTop:
                Constants.isAdjustSelected = false;

                collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                collageBlankLayoutTop.setVisibility(View.VISIBLE);
                collageEditToolsRecycleView.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageAdjustToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);
                collageEffectsLLayout.setVisibility(View.GONE);

                if(collageFrameShape1!=null)
                    collageFrameShape1.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape2!=null)
                    collageFrameShape2.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape3!=null)
                    collageFrameShape3.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape4!=null)
                    collageFrameShape4.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape5!=null)
                    collageFrameShape5.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape6!=null)
                    collageFrameShape6.setBackgroundColor(getResources().getColor(R.color.white));
                break;

            case R.id.blankCollageLayoutBottom:
                collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                collageBlankLayoutTop.setVisibility(View.VISIBLE);
                collageEditToolsRecycleView.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.GONE);
                collageEffectsLLayout.setVisibility(View.GONE);
                collageAdjustToolsRecycler.setVisibility(View.GONE);
                Constants.isAdjustSelected = false;

                if(collageFrameShape1!=null)
                    collageFrameShape1.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape2!=null)
                    collageFrameShape2.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape3!=null)
                    collageFrameShape3.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape4!=null)
                    collageFrameShape4.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape5!=null)
                    collageFrameShape5.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape6!=null)
                    collageFrameShape6.setBackgroundColor(getResources().getColor(R.color.white));


                break;




            case R.id.saveCollageLLayout:
                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    collageBlankLayoutTop.setVisibility(View.VISIBLE);
                    collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                    collageEditToolsRecycleView.setVisibility(View.VISIBLE);
                    collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                    collageLayoutsRecycler.setVisibility(View.GONE);
                    collageSubToolsRecycler.setVisibility(View.GONE);
                    collageSubToolsLayout.setVisibility(View.GONE);
                    collageEffectsLLayout  .setVisibility(View.GONE);
                    collageAdjustToolsRecycler.setVisibility(View.GONE);
                    Constants.isAdjustSelected = false;
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    collageBlankLayoutTop.setVisibility(View.VISIBLE);
                    collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                    collageEditToolsRecycleView.setVisibility(View.VISIBLE);
                    collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                    collageLayoutsRecycler.setVisibility(View.GONE);
                    collageSubToolsRecycler.setVisibility(View.GONE);
                    collageSubToolsLayout.setVisibility(View.GONE);
                    collageEffectsLLayout  .setVisibility(View.GONE);
                    collageAdjustToolsRecycler.setVisibility(View.GONE);
                    Constants.isAdjustSelected = false;
                }


                if(collageFrameShape1!=null)
                    collageFrameShape1.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape2!=null)
                    collageFrameShape2.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape3!=null)
                    collageFrameShape3.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape4!=null)
                    collageFrameShape4.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape5!=null)
                    collageFrameShape5.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape6!=null)
                    collageFrameShape6.setBackgroundColor(getResources().getColor(R.color.white));

                Bitmap mBitmap = Utilities.createBitmapFromLayout(mCollageRLayout);
                Utilities.saveSelfieProImage(mContext , mBitmap);

                if(addprefs!=null)
                    dispInterestialAdds();
                break;

            case R.id.adjustClose:
                Constants.isAdjustSelected = false;

                collageBlankLayoutTop.setVisibility(View.VISIBLE);
                collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                collageEditToolsRecycleView.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageAdjustToolsRecycler.setVisibility(View.GONE);


                break;


            case R.id.adjustDone:
                Constants.isAdjustSelected = false;

                collageBlankLayoutTop.setVisibility(View.VISIBLE);
                collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                collageEditToolsRecycleView.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageAdjustToolsRecycler.setVisibility(View.GONE);

                 Bitmap bitmap = ((BitmapDrawable) selectedImageView.getDrawable()).getBitmap();
                 Constants.capturedImageBitmap = bitmap;
                 selectedImageView.setImageBitmap(bitmap);

                break;


            case R.id.collageImageShape1:
                isSelectedImage=true;
                selectedImageView = collageImageShape1 ;
                Constants.capturedImageBitmap = Constants.capturedImageBitmap1 ;

                if(collageFrameShape1!=null)
                    collageFrameShape1.setBackground(getResources().getDrawable(R.drawable.border_selectedlayout));
                if(collageFrameShape2!=null)
                    collageFrameShape2.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape3!=null)
                    collageFrameShape3.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape4!=null)
                    collageFrameShape4.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape5!=null)
                    collageFrameShape5.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape6!=null)
                    collageFrameShape6.setBackgroundColor(getResources().getColor(R.color.white));

                isSelectedImage1 = true;
                isSelectedImage2 = false;
                isSelectedImage3 = false;
                isSelectedImage4 = false;
                isSelectedImage5 = false;
                isSelectedImage6 = false;

                if(isGalleryPickedImage1){
                    return;
                }
                Utilities.openGallery(this, PICK_GALLARY_REQUEST_ONE);

                break;

            case R.id.collageImageShape2:

                isSelectedImage=true;
                selectedImageView = collageImageShape2 ;
                Constants.capturedImageBitmap = Constants.capturedImageBitmap2 ;

                if(collageFrameShape1!=null)
                    collageFrameShape1.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape2!=null)
                    collageFrameShape2.setBackground(getResources().getDrawable(R.drawable.border_selectedlayout));
                if(collageFrameShape3!=null)
                    collageFrameShape3.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape4!=null)
                    collageFrameShape4.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape5!=null)
                    collageFrameShape5.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape6!=null)
                    collageFrameShape6.setBackgroundColor(getResources().getColor(R.color.white));

                isSelectedImage1 = false;
                isSelectedImage2 = true;
                isSelectedImage3 = false;
                isSelectedImage4 = false;
                isSelectedImage5 = false;
                isSelectedImage6 = false;

                if(isGalleryPickedImage2){
                    return;
                }
                Utilities.openGallery(this, PICK_GALLARY_REQUEST_TWO);
                break;

            case R.id.collageImageShape3:
                isSelectedImage=true;
                selectedImageView = collageImageShape3 ;
                Constants.capturedImageBitmap = Constants.capturedImageBitmap3 ;

                if(collageFrameShape1!=null)
                    collageFrameShape1.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape2!=null)
                    collageFrameShape2.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape3!=null)
                    collageFrameShape3.setBackground(getResources().getDrawable(R.drawable.border_selectedlayout));
                if(collageFrameShape4!=null)
                    collageFrameShape4.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape5!=null)
                    collageFrameShape5.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape6!=null)
                    collageFrameShape6.setBackgroundColor(getResources().getColor(R.color.white));

                isSelectedImage1 = false;
                isSelectedImage2 = false;
                isSelectedImage3 = true;
                isSelectedImage4 = false;
                isSelectedImage5 = false;
                isSelectedImage6 = false;

                if(isGalleryPickedImage3){
                    return;
                }
                Utilities.openGallery(this, PICK_GALLARY_REQUEST_THREE);
                break;

            case R.id.collageImageShape4:

                isSelectedImage = true;
                selectedImageView = collageImageShape4;
                Constants.capturedImageBitmap = Constants.capturedImageBitmap4;

                if (collageFrameShape1 != null)
                    collageFrameShape1.setBackgroundColor(getResources().getColor(R.color.white));
                if (collageFrameShape2 != null)
                    collageFrameShape2.setBackgroundColor(getResources().getColor(R.color.white));
                if (collageFrameShape3 != null)
                    collageFrameShape3.setBackgroundColor(getResources().getColor(R.color.white));
                if (collageFrameShape4 != null)
                    collageFrameShape4.setBackground(getResources().getDrawable(R.drawable.border_selectedlayout));
                if (collageFrameShape5 != null)
                    collageFrameShape5.setBackgroundColor(getResources().getColor(R.color.white));
                if (collageFrameShape6 != null)
                    collageFrameShape6.setBackgroundColor(getResources().getColor(R.color.white));

                isSelectedImage1 = false;
                isSelectedImage2 = false;
                isSelectedImage3 = false;
                isSelectedImage4 = true;
                isSelectedImage5 = false;
                isSelectedImage6 = false;
                if(isGalleryPickedImage4){
                    return;
                }
                Utilities.openGallery(this, PICK_GALLARY_REQUEST_FOUR);
                break;
            case R.id.collageImageShape5:

                isSelectedImage = true;
                selectedImageView = collageImageShape5;
                Constants.capturedImageBitmap = Constants.capturedImageBitmap5;

                if (collageFrameShape1 != null)
                    collageFrameShape1.setBackgroundColor(getResources().getColor(R.color.white));
                if (collageFrameShape2 != null)
                    collageFrameShape2.setBackgroundColor(getResources().getColor(R.color.white));
                if (collageFrameShape3 != null)
                    collageFrameShape3.setBackgroundColor(getResources().getColor(R.color.white));
                if (collageFrameShape4 != null)
                    collageFrameShape4.setBackgroundColor(getResources().getColor(R.color.white));
                if (collageFrameShape5 != null)
                    collageFrameShape5.setBackground(getResources().getDrawable(R.drawable.border_selectedlayout));
                if (collageFrameShape6 != null)
                    collageFrameShape6.setBackgroundColor(getResources().getColor(R.color.white));

                isSelectedImage1 = false;
                isSelectedImage2 = false;
                isSelectedImage3 = false;
                isSelectedImage4 = false;
                isSelectedImage5 = true;
                isSelectedImage6 = false;

                if(isGalleryPickedImage5){
                    return;
                }
                Utilities.openGallery(this, PICK_GALLARY_REQUEST_FIVE);
                break;

            case R.id.collageImageShape6:

                isSelectedImage = true;
                selectedImageView = collageImageShape6;
                Constants.capturedImageBitmap = Constants.capturedImageBitmap6;

                if (collageFrameShape1 != null)
                    collageFrameShape1.setBackgroundColor(getResources().getColor(R.color.white));
                if (collageFrameShape2 != null)
                    collageFrameShape2.setBackgroundColor(getResources().getColor(R.color.white));
                if (collageFrameShape3 != null)
                    collageFrameShape3.setBackgroundColor(getResources().getColor(R.color.white));
                if (collageFrameShape4 != null)
                    collageFrameShape4.setBackgroundColor(getResources().getColor(R.color.white));
                if (collageFrameShape5 != null)
                    collageFrameShape5.setBackgroundColor(getResources().getColor(R.color.white));
                if (collageFrameShape6 != null)
                    collageFrameShape6.setBackground(getResources().getDrawable(R.drawable.border_selectedlayout));

                isSelectedImage1 = false;
                isSelectedImage2 = false;
                isSelectedImage3 = false;
                isSelectedImage4 = false;
                isSelectedImage5 = false;
                isSelectedImage6 = true;

                if(isGalleryPickedImage6){
                    return;
                }
                Utilities.openGallery(this, PICK_GALLARY_REQUEST_SIX);
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

            case PICK_GALLARY_REQUEST_ONE:
                selectedImageView = collageImageShape1;
                if (resultCode == RESULT_OK && null != data) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        selectedImageUri1 = selectedImageUri;
                        try {
                            Constants.capturedImageBitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri1);
                            //selectedBitmap1= mBitmap1 ;
                            //Constants.capturedImageBitmap = Constants.capturedImageBitmap1;
                            //Constants.capturedImageBitmap1 = selectedBitmap1;

                            Uri compressedUri = Uri.parse(compressImage(getImageUri(mContext, Constants.capturedImageBitmap1).toString()));
                            File imgFile = new  File(compressImage(compressedUri.toString()));

                            Uri compressedUri2 ;
                            Bitmap compressedBitmap ;
                            if(imgFile.exists()) {
                                compressedUri2 = Uri.fromFile(imgFile);
                                //Log.e("Camera File Uri " , compressedUri2+"");
                                compressedBitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), compressedUri2);
                                Constants.capturedImageBitmap = compressedBitmap;
                                Constants.capturedImageBitmap1 = compressedBitmap;
                            }

                            loadImageFilters();

                            collageImageShape1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            //collageImageShape1.setImageURI(selectedImageUri1);
                            Constants.collageOrignalBitmap1 = Constants.capturedImageBitmap1;
                            collageImageShape1.setImageBitmap(Constants.capturedImageBitmap1);
                            collageImageShape1.setRotation(0);
                            fixAntiAlias(collageImageShape1);
                            collageImageShape1.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

                            collageImageShape1.setOnClickListener(this);
                            collageImageShape1.setClickable(false);

                            isSelectedImage1 = true;
                            isGalleryPickedImage1 = true;
                            isEmptyImage1 = true;
                            imageSelectedOrNot = true;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //bindDataToAdapter();
                }else   {
                    Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                }
                break;

            case PICK_GALLARY_REQUEST_TWO:
                selectedImageView = collageImageShape2;

                if (resultCode == RESULT_OK && null != data) {
                    //mBitmap2 = null;
                    Uri selectedImageUri = data.getData();

                    if (selectedImageUri != null) {
                        selectedImageUri2 = selectedImageUri;
                        try {
                            Constants.capturedImageBitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri2);

                            //selectedBitmap2= mBitmap2;
                            //Constants.capturedImageBitmap = Constants.capturedImageBitmap2;
                            //Constants.capturedImageBitmap2 = selectedBitmap2;

                            Uri compressedUri = Uri.parse(compressImage(getImageUri(mContext, Constants.capturedImageBitmap2).toString()));
                            File imgFile = new  File(compressImage(compressedUri.toString()));
                            Uri compressedUri2 ;
                            Bitmap compressedBitmap ;
                            if(imgFile.exists()) {
                                compressedUri2 = Uri.fromFile(imgFile);
                                //Log.e("Camera File Uri " , compressedUri2+"");
                                compressedBitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), compressedUri2);
                                Constants.capturedImageBitmap = compressedBitmap;
                                Constants.capturedImageBitmap2 = compressedBitmap;
                            }

                            loadImageFilters();

                            collageImageShape2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            //collageImageShape2.setImageURI(selectedImageUri2);
                            Constants.collageOrignalBitmap2 = Constants.capturedImageBitmap2;
                            collageImageShape2.setImageBitmap(Constants.capturedImageBitmap2);
                            collageImageShape2.setRotation(0);
                            fixAntiAlias(collageImageShape2);
                            collageImageShape2.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

                            collageImageShape2.setOnClickListener(this);
                            collageImageShape2.setClickable(false);

                            isSelectedImage2 = true;
                            isGalleryPickedImage2 = true;
                            isEmptyImage2 = true;
                            imageSelectedOrNot = true;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else   {
                    Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                }

                break;

            case PICK_GALLARY_REQUEST_THREE:
                selectedImageView = collageImageShape3;
                //collageImageShape3.setScaleType(ImageView.ScaleType.MATRIX);
                if (resultCode == RESULT_OK && null != data) {
                    //mBitmap3 = null;
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        selectedImageUri3 = selectedImageUri;
                        try {
                            Constants.capturedImageBitmap3 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri3);

                            //selectedBitmap3 = mBitmap3;
                            //Constants.capturedImageBitmap = Constants.capturedImageBitmap3;
                            //Constants.capturedImageBitmap3 = selectedBitmap3;

                            Uri compressedUri = Uri.parse(compressImage(getImageUri(mContext, Constants.capturedImageBitmap3).toString()));
                            File imgFile = new  File(compressImage(compressedUri.toString()));
                            Uri compressedUri2 ;
                            Bitmap compressedBitmap ;
                            if(imgFile.exists()) {
                                compressedUri2 = Uri.fromFile(imgFile);
                                //Log.e("Camera File Uri " , compressedUri2+"");
                                compressedBitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), compressedUri2);
                                Constants.capturedImageBitmap = compressedBitmap;
                                Constants.capturedImageBitmap3 = compressedBitmap;
                            }

                            loadImageFilters();

                            collageImageShape3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            //collageImageShape3.setImageURI(selectedImageUri3);
                            Constants.collageOrignalBitmap3 = Constants.capturedImageBitmap3;
                            collageImageShape3.setImageBitmap(Constants.capturedImageBitmap3);
                            collageImageShape3.setRotation(0);
                            fixAntiAlias(collageImageShape3);
                            //selectedBitmap3 = Utilities.bitmapFromImageView(collageImageShape3);
                            collageImageShape3.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                            collageImageShape3.setOnClickListener(this);
                            collageImageShape3.setClickable(false);

                            isSelectedImage3 = true;
                            isGalleryPickedImage3 = true;
                            isEmptyImage3 = true;
                            imageSelectedOrNot = true;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else   {
                    Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                }
                break;

            case PICK_GALLARY_REQUEST_FOUR:
                selectedImageView = collageImageShape4;
                //collageImageShape4.setScaleType(ImageView.ScaleType.FIT_XY);
                if (resultCode == RESULT_OK && null != data) {
                    //mBitmap4 = null;
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        selectedImageUri4 = selectedImageUri;
                        try {
                            Constants.capturedImageBitmap4 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri4);
                            //selectedBitmap4 = mBitmap4;
                            //Constants.capturedImageBitmap = Constants.capturedImageBitmap4;
                            //Constants.capturedImageBitmap4 = selectedBitmap4;

                            Uri compressedUri = Uri.parse(compressImage(getImageUri(mContext, Constants.capturedImageBitmap4).toString()));
                            File imgFile = new  File(compressImage(compressedUri.toString()));
                            Uri compressedUri2 ;
                            Bitmap compressedBitmap ;
                            if(imgFile.exists()) {
                                compressedUri2 = Uri.fromFile(imgFile);
                                //Log.e("Camera File Uri " , compressedUri2+"");
                                compressedBitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), compressedUri2);
                                Constants.capturedImageBitmap = compressedBitmap;
                                Constants.capturedImageBitmap4 = compressedBitmap;
                            }

                            loadImageFilters();

                            collageImageShape4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            Constants.collageOrignalBitmap4 = Constants.capturedImageBitmap4;
                            collageImageShape4.setImageBitmap(Constants.capturedImageBitmap4);
                            //collageImageShape4.setImageURI(selectedImageUri4);
                            collageImageShape4.setRotation(0);
                            fixAntiAlias(collageImageShape4);
                            collageImageShape4.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                            collageImageShape4.setOnClickListener(this);
                            collageImageShape4.setClickable(false);

                            isSelectedImage4 = true;
                            isGalleryPickedImage4 = true;
                            isEmptyImage4 = true;
                            imageSelectedOrNot = true;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else   {
                    Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                }
                break;

            case PICK_GALLARY_REQUEST_FIVE:
                selectedImageView = collageImageShape5;
                // collageImageShape5.setScaleType(ImageView.ScaleType.MATRIX);
                if (resultCode == RESULT_OK && null != data) {
                    //mBitmap5 = null;
                    Uri selectedImageUri = data.getData();

                    if (selectedImageUri != null) {
                        selectedImageUri5 = selectedImageUri;
                        try {
                            Constants.capturedImageBitmap5 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri5);
                            //selectedBitmap5 = mBitmap5;
                            //Constants.capturedImageBitmap = Constants.capturedImageBitmap5;
                            //Constants.capturedImageBitmap5 = selectedBitmap5;

                            Uri compressedUri = Uri.parse(compressImage(getImageUri(mContext, Constants.capturedImageBitmap5).toString()));
                            File imgFile = new  File(compressImage(compressedUri.toString()));
                            Uri compressedUri2 ;
                            Bitmap compressedBitmap ;
                            if(imgFile.exists()) {
                                compressedUri2 = Uri.fromFile(imgFile);
                                //Log.e("Camera File Uri " , compressedUri2+"");
                                compressedBitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), compressedUri2);
                                Constants.capturedImageBitmap = compressedBitmap;
                                Constants.capturedImageBitmap5 = compressedBitmap;
                            }

                            loadImageFilters();

                            collageImageShape5.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            //collageImageShape5.setImageURI(selectedImageUri5);
                            Constants.collageOrignalBitmap5 = Constants.capturedImageBitmap5;
                            collageImageShape5.setImageBitmap(Constants.capturedImageBitmap5);
                            collageImageShape5.setRotation(0);
                            fixAntiAlias(collageImageShape5);
                            //selectedBitmap5 = Utilities.bitmapFromImageView(collageImageShape5);
                            collageImageShape5.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                            collageImageShape5.setOnClickListener(this);
                            collageImageShape5.setClickable(false);

                            isSelectedImage5 = true;
                            isGalleryPickedImage5 = true;
                            isEmptyImage5 = true;
                            imageSelectedOrNot = true;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else   {
                    Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                }


                break;

            case PICK_GALLARY_REQUEST_SIX:
                selectedImageView = collageImageShape6;
                //collageImageShape6.setScaleType(ImageView.ScaleType.MATRIX);

                if (resultCode == RESULT_OK && null != data) {
                    // mBitmap6 = null;
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        selectedImageUri6 = selectedImageUri;
                        try {
                            Constants.capturedImageBitmap6 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri6);
                            // selectedBitmap6 = mBitmap6;
                            //Constants.capturedImageBitmap = Constants.capturedImageBitmap6;
                            //Constants.capturedImageBitmap6 = selectedBitmap6;

                            Uri compressedUri = Uri.parse(compressImage(getImageUri(mContext, Constants.capturedImageBitmap6).toString()));
                            File imgFile = new  File(compressImage(compressedUri.toString()));
                            Uri compressedUri2 ;
                            Bitmap compressedBitmap ;
                            if(imgFile.exists()) {
                                compressedUri2 = Uri.fromFile(imgFile);
                                //Log.e("Camera File Uri " , compressedUri2+"");
                                compressedBitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), compressedUri2);
                                Constants.capturedImageBitmap = compressedBitmap;
                                Constants.capturedImageBitmap6 = compressedBitmap;
                            }

                            loadImageFilters();

                            collageImageShape6.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            //collageImageShape6.setImageURI(selectedImageUri6);
                            Constants.collageOrignalBitmap6 = Constants.capturedImageBitmap6;
                            collageImageShape6.setImageBitmap(Constants.capturedImageBitmap6);
                            collageImageShape6.setRotation(0);
                            fixAntiAlias(collageImageShape6);
                            //selectedBitmap6 = Utilities.bitmapFromImageView(collageImageShape6);
                            collageImageShape6.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                            collageImageShape6.setOnClickListener(this);
                            collageImageShape6.setClickable(false);

                            isSelectedImage6 = true;
                            isGalleryPickedImage6 = true;
                            isEmptyImage6 = true;
                            imageSelectedOrNot = true;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }else   {
                    Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
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

            if (isSelectedImage1) {
                selectedImageUri1 = Constants.imageUri;
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri1);
                    //mSelectedImageBitMap = bitmap;
                    //selectedBitmap1  = bitmap;
                    Constants.capturedImageBitmap = bitmap;
                    Constants.capturedImageBitmap1 = bitmap;

                    loadImageFilters();

                    collageImageShape1 = (PhotoView) findViewById(R.id.collageImageShape1);
                    collageImageShape1.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                    collageImageShape1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    //collageImageShape1.setImageURI(selectedImageUri1);
                    collageImageShape1.setImageBitmap(Constants.capturedImageBitmap1);
                    collageImageShape1.setRotation(0);
                    fixAntiAlias(collageImageShape1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else if (isSelectedImage2) {
                selectedImageUri2 = Constants.imageUri;
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri2);
                    //mSelectedImageBitMap = bitmap;
                    //selectedBitmap2  = bitmap;
                    Constants.capturedImageBitmap = bitmap;
                    Constants.capturedImageBitmap2 = bitmap;
                    loadImageFilters();
                    collageImageShape2 = (PhotoView) findViewById(R.id.collageImageShape2);
                    collageImageShape2.setBackgroundColor(getResources().getColor(R.color.white));
                    collageImageShape2.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    //collageImageShape2.setImageURI(selectedImageUri2);
                    collageImageShape2.setImageBitmap(Constants.capturedImageBitmap2);
                    collageImageShape2.setRotation(0);
                    fixAntiAlias(collageImageShape2);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (isSelectedImage3) {

                selectedImageUri3 = Constants.imageUri;
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri3);
                    //mSelectedImageBitMap = bitmap;
                    // selectedBitmap3  = bitmap;
                    Constants.capturedImageBitmap = bitmap;
                    Constants.capturedImageBitmap3 = bitmap;
                    loadImageFilters();

                    collageImageShape3 = (PhotoView) findViewById(R.id.collageImageShape3);
                    collageImageShape3.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                    collageImageShape3.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    collageImageShape3.setImageURI(selectedImageUri3);
                    collageImageShape3.setRotation(0);
                    fixAntiAlias(collageImageShape3);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if (isSelectedImage4) {
                selectedImageUri4 = Constants.imageUri;
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri4);
                    //mSelectedImageBitMap = bitmap;
                    //selectedBitmap4  = bitmap;
                    Constants.capturedImageBitmap = bitmap;
                    Constants.capturedImageBitmap4 = bitmap;
                    loadImageFilters();

                    collageImageShape4 = (PhotoView) findViewById(R.id.collageImageShape4);
                    collageImageShape4.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                    collageImageShape4.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    collageImageShape4.setImageURI(selectedImageUri4);
                    collageImageShape4.setRotation(0);
                    fixAntiAlias(collageImageShape4);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if (isSelectedImage5) {
                selectedImageUri5 = Constants.imageUri;
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri5);
                    //mSelectedImageBitMap = bitmap;
                    // selectedBitmap5  = bitmap;
                    Constants.capturedImageBitmap = bitmap;
                    Constants.capturedImageBitmap5 = bitmap;
                    loadImageFilters();

                    collageImageShape5 = (PhotoView) findViewById(R.id.collageImageShape5);
                    collageImageShape5.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                    collageImageShape5.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    collageImageShape5.setImageURI(selectedImageUri5);
                    collageImageShape5.setRotation(0);
                    fixAntiAlias(collageImageShape5);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //mSelectedImageBitMap = Utilities.bitmapFromImageView(collageImageShape5);
                //collageImageShape5.setImageBitmap(mCropedBitmap);
                //collageImageShape5.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            else if (isSelectedImage6) {
                selectedImageUri6 = Constants.imageUri;
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri6);
                    //mSelectedImageBitMap = bitmap;
                    // selectedBitmap6  = bitmap;
                    Constants.capturedImageBitmap = bitmap;
                    Constants.capturedImageBitmap6 = bitmap;

                    loadImageFilters();

                    collageImageShape6 = (PhotoView) findViewById(R.id.collageImageShape6);
                    collageImageShape6.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                    collageImageShape6.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    collageImageShape6.setImageURI(selectedImageUri6);
                    collageImageShape6.setRotation(0);
                    fixAntiAlias(collageImageShape6);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //mSelectedImageBitMap = Utilities.bitmapFromImageView(collageImageShape6);
                //collageImageShape6.setImageBitmap(mCropedBitmap);
                // collageImageShape6.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }
    }

    private void setResultCancelled() {
        Log.e("Crop Result" , " Cancelled ");
    }



    /********************** EditTolls  /  onCollageEditTollsSelected *******************************/
    @Override
    public void onCollageEditTollsSelected(CollageEditToolsType collageEditToolsType) {
        switch (collageEditToolsType) {
            case COLLAGE:
                isSelectedEffects = false ;

                collageEditToolsRecycleView.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);

                if (collageLayoutsRecycler.getVisibility() == View.VISIBLE) {
                    collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                    collageSubToolsLayout.setVisibility(View.GONE);
                    collageLayoutsRecycler.setVisibility(View.GONE);
                } else {
                    collageBlankLayoutBottom.setVisibility(View.GONE);
                    collageSubToolsLayout.setVisibility(View.VISIBLE);
                    collageLayoutsRecycler.setVisibility(View.VISIBLE);
                }
                break;

            case TOOLS:
                isSelectedEffects = false ;
                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                    collageEditToolsRecycleView.setVisibility(View.VISIBLE);
                    collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                    collageLayoutsRecycler.setVisibility(View.GONE);
                    collageEffectsLLayout  .setVisibility(View.GONE);
                    collageSubToolsLayout.setVisibility(View.GONE);
                    collageSubToolsRecycler.setVisibility(View.GONE);
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                collageEditToolsRecycleView.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);

                if (collageSubToolsRecycler.getVisibility() == View.VISIBLE) {
                    collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                    collageSubToolsLayout.setVisibility(View.GONE);
                    collageSubToolsRecycler.setVisibility(View.GONE);
                } else {
                    collageBlankLayoutBottom.setVisibility(View.GONE);
                    collageSubToolsLayout.setVisibility(View.VISIBLE);
                    collageSubToolsRecycler.setVisibility(View.VISIBLE);
                }



                break;


            case EFFECTS:
                isSelectedEffects = true ;

                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                    collageEditToolsRecycleView.setVisibility(View.VISIBLE);
                    collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                    collageLayoutsRecycler.setVisibility(View.GONE);
                    collageSubToolsRecycler.setVisibility(View.GONE);
                    collageSubToolsLayout.setVisibility(View.GONE);
                    collageEffectsLLayout  .setVisibility(View.GONE);
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if(!isSelectedImage){
//                    collageBlankLayoutBottom.setVisibility(View.VISIBLE);
//                    collageAdjustSeekbarLLayout.setVisibility(View.GONE);
//                    collageLayoutsRecycler.setVisibility(View.GONE);
//                    collageSubToolsRecycler.setVisibility(View.GONE);
//                    collageSubToolsLayout.setVisibility(View.GONE);
//                    collageEffectsLLayout  .setVisibility(View.GONE);
//                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                collageEditToolsRecycleView.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);

                if (collageEffectsLLayout  .getVisibility() == View.VISIBLE) {
                    collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                    collageSubToolsLayout.setVisibility(View.GONE);
                    collageEffectsLLayout  .setVisibility(View.GONE);
                } else {
                    collageBlankLayoutBottom.setVisibility(View.GONE);
                    collageSubToolsLayout.setVisibility(View.VISIBLE);
                    collageEffectsLLayout  .setVisibility(View.VISIBLE);
                }

                imageSelectedOrNot =  imageSelectedOrNot();
                if (isSelectedImage1) {
                    Bitmap bitmap = ((BitmapDrawable)collageImageShape1.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        Constants.imageBitmap = bitmap;
                        Constants.imageView = collageImageShape1;
                        Constants.capturedImageBitmap  = bitmap;
                        Constants.capturedImageBitmap1  = bitmap;
                        loadImageFilters();
                    }else   {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage2) {
                    Bitmap bitmap = ((BitmapDrawable) collageImageShape2.getDrawable()).getBitmap();
                    if (bitmap != null)
                    {
                        Constants.imageBitmap = bitmap;
                        Constants.imageView = collageImageShape2;
                        Constants.capturedImageBitmap  = bitmap;
                        Constants.capturedImageBitmap2  = bitmap;
                        loadImageFilters();
                    }else  {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage3) {
                    Bitmap bitmap = ((BitmapDrawable)collageImageShape3.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        Constants.imageBitmap = bitmap;
                        Constants.imageView = collageImageShape3;
                        Constants.capturedImageBitmap  = bitmap;
                        Constants.capturedImageBitmap3  = bitmap;
                        loadImageFilters();
                    }else   {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage4) {
                    Bitmap bitmap = ((BitmapDrawable) collageImageShape4.getDrawable()).getBitmap();
                    if (bitmap != null){
                        Constants.imageBitmap = bitmap;
                        Constants.imageView = collageImageShape4;
                        Constants.capturedImageBitmap  = bitmap;
                        Constants.capturedImageBitmap4  = bitmap;
                        loadImageFilters();
                    }else {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage5) {
                    Bitmap bitmap = ((BitmapDrawable) collageImageShape5.getDrawable()).getBitmap();
                    if (bitmap != null){
                        Constants.imageBitmap = bitmap;
                        Constants.imageView = collageImageShape5;
                        Constants.capturedImageBitmap  = bitmap;
                        Constants.capturedImageBitmap5  = bitmap;
                        loadImageFilters();
                    }else   {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage6) {
                    Bitmap bitmap = ((BitmapDrawable)collageImageShape6.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        Constants.imageBitmap = bitmap;
                        Constants.imageView = collageImageShape6;
                        Constants.capturedImageBitmap  = bitmap;
                        Constants.capturedImageBitmap6  = bitmap;
                        loadImageFilters();
                    }else  {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;


            case ADJUST:
                Constants.isAdjustSelected = true;

                collageAdjustToolsRecycler = (RecyclerView) findViewById(R.id.collageAdjustToolsRecycler);
                collageAdjustToolsRecycler.setVisibility(View.GONE);
                adjustToolsAdapter = new CollageAdjustToolsAdapter(this);
                LinearLayoutManager mAdjustToolsAdapterLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                collageAdjustToolsRecycler.setLayoutManager(mAdjustToolsAdapterLManager);
                collageAdjustToolsRecycler.setAdapter(adjustToolsAdapter);

                isSelectedEffects = false ;
                imageSelectedOrNot =  imageSelectedOrNot();

                if(!imageSelectedOrNot){
                    collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                    collageEditToolsRecycleView.setVisibility(View.VISIBLE);
                    collageSubToolsRecycler.setVisibility(View.GONE);
                    collageLayoutsRecycler.setVisibility(View.GONE);
                    collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                    collageSubToolsLayout.setVisibility(View.GONE);
                    collageEffectsLLayout  .setVisibility(View.GONE);
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }


                finalImage = onAdjustFinalImage();

                collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.GONE);
                collageEditToolsRecycleView.setVisibility(View.GONE);
                collageBrightnessSeekBar.setVisibility(View.GONE);
                collageContrastSeekBar.setVisibility(View.GONE);
                collageSaturationSeekBar.setVisibility(View.GONE);
                collageSharpnessSeekBar.setVisibility(View.GONE);
                collageVignetteSeekBar.setVisibility(View.GONE);
                collageShadowSeekBar.setVisibility(View.GONE);
                collageHighlightSeekBar.setVisibility(View.GONE);
                collageTempSeekBar.setVisibility(View.GONE);
                collageTintSeekBar.setVisibility(View.GONE);
                collageDenoiseSeekBar.setVisibility(View.GONE);
                collageCurveSeekBar.setVisibility(View.GONE);
                collageColorBalanceSeekBar.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);
//                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);
//                collageContrastSeekBar.setVisibility(View.GONE);
//                collageBrightnessSeekBar.setVisibility(View.GONE);
//                collageEditToolsRecycleView.setVisibility(View.GONE);
//                collageSubToolsRecycler.setVisibility(View.GONE);
//                collageLayoutsRecycler.setVisibility(View.GONE);
//                collageEffectsLLayout.setVisibility(View.GONE);
//                collageEffectsLLayout.setVisibility(View.GONE);
//                collageAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
//
//                if (collageAdjustSeekbarLLayout.getVisibility() == View.VISIBLE) {
//                    collageBlankLayoutBottom.setVisibility(View.VISIBLE);
//                    collageSubToolsLayout.setVisibility(View.GONE);
//                    collageAdjustSeekbarLLayout.setVisibility(View.GONE);
//                } else {
//                    collageBlankLayoutBottom.setVisibility(View.GONE);
//                    collageSubToolsLayout.setVisibility(View.VISIBLE);
//                    collageAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
//                }
                break;


            case EDIT:
                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                    collageEditToolsRecycleView.setVisibility(View.VISIBLE);
                    collageSubToolsRecycler.setVisibility(View.GONE);
                    collageLayoutsRecycler.setVisibility(View.GONE);
                    collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                    collageSubToolsLayout.setVisibility(View.GONE);
                    collageEffectsLLayout  .setVisibility(View.GONE);
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                collageAdjustToolsRecycler.setVisibility(View.GONE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);

                if(collageFrameShape1!=null)
                    collageFrameShape1.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape2!=null)
                    collageFrameShape2.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape3!=null)
                    collageFrameShape3.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape4!=null)
                    collageFrameShape4.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape5!=null)
                    collageFrameShape5.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape6!=null)
                    collageFrameShape6.setBackgroundColor(getResources().getColor(R.color.white));

                mMyPrecfence.saveString(Constants.INTENT_TYPE, Constants.INTENT_TYPE_COLLAGE);
                try {
                    Bitmap mBitmap = Utilities.createBitmapFromLayout(mCollageRLayout);
                    String imagePath =  Utilities.saveBitmap_Temp(mBitmap);
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        Constants.imageUri = Uri.fromFile(imageFile);
                    }
                    Intent intentCollageSelectedImage = new Intent(CollageActivity.this, EditImageActivity.class);
                    intentCollageSelectedImage.putExtra(Constants.URI_COLLAGE, Constants.imageUri);
                    //intentCollageSelectedImage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(intentCollageSelectedImage);
                    //finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }






    /*************************onFrame / onCollageLayout Selection***********************************************/
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCollageLayoutSelected(int collageFrameID, CollageLayoutsType collageFrameType) {

        collageLayoutsRecycler.setVisibility(View.VISIBLE);
        collageEditToolsRecycleView.setVisibility(View.VISIBLE);

        switch (collageFrameType) {

            case ONE_ZERO:
                isSelectedFrame2_1 = false;
                isSelectedFrame2_2 = false;
                isSelectedFrame3_1 = false;
                isSelectedFrame3_2 = false;
                isSelectedFrame3_3 = false;
                isSelectedFrame4_1 = false;
                isSelectedFrame4_2 = false;
                isSelectedFrame4_3 = false;
                isSelectedFrame4_4 = false;
                isSelectedFrame6_1 = false;

                if(isSelectedFrame1_0){
                    //Toast.makeText(this , "You have already selected." , Toast.LENGTH_SHORT ).show();
                    Toast.makeText(this , "This is your current selection." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage);
                initView();

                if (selectedImageUri1 != null) {
                    //Constants.imageUri = selectedImageUri1 ;
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri1);

//                        // mSelectedImageBitMap = bitmap;
//                        // selectedBitmap1  = bitmap;
//                        Constants.capturedImageBitmap = bitmap;
//                        Constants.capturedImageBitmap1 = bitmap;
//                        //loadImageFilters();

                        Constants.capturedImageBitmap = Constants.capturedImageBitmap1;

                        collageImageShape1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        //collageImageShape1.setImageURI(selectedImageUri1);
                        collageImageShape1.setImageBitmap(Constants.capturedImageBitmap1);
                        collageImageShape1.setRotation(0);
                        fixAntiAlias(collageImageShape1);
                        collageImageShape1.setOnClickListener(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage1) {
                if(isEmptyImage1) {
                    collageImageShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                isSelectedFrame1_0 = true;
                isSelectedImage=false;

                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);
                collageLayoutsRecycler.setVisibility(View.VISIBLE);

                break;

            case TWO_ONE:

                isSelectedFrame1_0 = false;
                isSelectedFrame2_2 = false;
                isSelectedFrame3_1 = false;
                isSelectedFrame3_2 = false;
                isSelectedFrame3_3 = false;
                isSelectedFrame4_1 = false;
                isSelectedFrame4_2 = false;
                isSelectedFrame4_3 = false;
                isSelectedFrame4_4 = false;
                isSelectedFrame6_1 = false;

                if(isSelectedFrame2_1){
                    Toast.makeText(this , "This is your current selection." , Toast.LENGTH_SHORT ).show();
                    return;
                }

                setContentView(R.layout.activity_collage_2_1);

                initView();
                initView2();

                if (selectedImageUri1 != null) {
                    //Constants.imageUri = selectedImageUri1 ;
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri1);
                        // mSelectedImageBitMap = bitmap;
                        // selectedBitmap1  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap1 ;
                        //Constants.capturedImageBitmap1 = bitmap;
                        //loadImageFilters();

                        collageImageShape1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        //collageImageShape1.setImageBitmap(selectedBitmap1);
                        collageImageShape1.setImageURI(selectedImageUri1);
                        collageImageShape1.setImageBitmap(Constants.capturedImageBitmap1);
                        collageImageShape1.setRotation(0);
                        fixAntiAlias(collageImageShape1);
                        collageImageShape1.setOnClickListener(this);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //if(isSelectedImage1) {
                if(isEmptyImage1) {
                    collageImageShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (selectedImageUri2 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri2);
                        // mSelectedImageBitMap = bitmap;
                        // selectedBitmap2  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap2 ;
                        //Constants.capturedImageBitmap2 = bitmap;
                        //loadImageFilters();

                        collageImageShape2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        //collageImageShape2.setImageBitmap(selectedBitmap2);
                        collageImageShape2.setImageURI(selectedImageUri2);
                        collageImageShape2.setImageBitmap(Constants.capturedImageBitmap2);
                        collageImageShape2.setRotation(0);
                        fixAntiAlias(collageImageShape2);
                        //selectedBitmap1 = Utilities.bitmapFromImageView(collageImageShape2);
                        //selectedBitmap2 = ((BitmapDrawable) collageImageShape2.getDrawable()).getBitmap();
                        collageImageShape2.setOnClickListener(this);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                //if(isSelectedImage2) {
                if(isEmptyImage2) {
                    collageImageShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                isSelectedFrame2_1 = true;
                isSelectedImage=false;

                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);
                collageLayoutsRecycler.setVisibility(View.VISIBLE);

                break;

            case TWO_TWO:

                isSelectedFrame1_0 = false;
                isSelectedFrame2_1 = false;
                // isSelectedFrame2_2 = false;
                isSelectedFrame3_1 = false;
                isSelectedFrame3_2 = false;
                isSelectedFrame3_3 = false;
                isSelectedFrame4_1 = false;
                isSelectedFrame4_2 = false;
                isSelectedFrame4_3 = false;
                isSelectedFrame4_4 = false;
                isSelectedFrame6_1 = false;

                if(isSelectedFrame2_2){
                    Toast.makeText(this , "This is your current selection." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_2_2);
                initView();
                initView2();
                if (selectedImageUri1 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri1);
                        // mSelectedImageBitMap = bitmap;
                        // selectedBitmap1  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap1;
                        //Constants.capturedImageBitmap1 = bitmap;
                        //loadImageFilters();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    collageImageShape1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    //collageImageShape1.setImageURI(selectedImageUri1);
                    collageImageShape1.setImageBitmap(Constants.capturedImageBitmap1);
                    collageImageShape1.setRotation(0);
                    fixAntiAlias(collageImageShape1);

                    collageImageShape1.setOnClickListener(this);
                }
                // if(isSelectedImage1) {
                if(isEmptyImage1) {
                    collageImageShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                if (selectedImageUri2 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri2);
                        //mSelectedImageBitMap = bitmap;
                        // selectedBitmap2  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap2 ;
                        //Constants.capturedImageBitmap2 = bitmap;
                        //loadImageFilters();

                        collageImageShape2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        //collageImageShape2.setImageURI(selectedImageUri2);
                        collageImageShape2.setImageBitmap(Constants.capturedImageBitmap2);
                        collageImageShape2.setRotation(0);
                        fixAntiAlias(collageImageShape2);
                        collageImageShape2.setOnClickListener(this);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                //if(isSelectedImage2) {
                if(isEmptyImage2) {
                    collageImageShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                isSelectedFrame2_2 = true;
                isSelectedImage=false;

                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);
                collageLayoutsRecycler.setVisibility(View.VISIBLE);

                break;

            case THREE_ONE:

                isSelectedFrame1_0 = false;
                isSelectedFrame2_1 = false;
                isSelectedFrame2_2 = false;
                //isSelectedFrame3_1 = false;
                isSelectedFrame3_2 = false;
                isSelectedFrame3_3 = false;
                isSelectedFrame4_1 = false;
                isSelectedFrame4_2 = false;
                isSelectedFrame4_3 = false;
                isSelectedFrame4_4 = false;
                isSelectedFrame6_1 = false;
                if(isSelectedFrame3_1){
                    Toast.makeText(this , "This is your current selection." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_3_1);
                initView();
                initView2();
                initView3();

                if (selectedImageUri1 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri1);
                        // mSelectedImageBitMap = bitmap;
                        // selectedBitmap1  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap1;
                        //Constants.capturedImageBitmap1 = bitmap;
                        //loadImageFilters();
                        collageImageShape1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        //collageImageShape1.setImageURI(selectedImageUri1);
                        collageImageShape1.setImageBitmap(Constants.capturedImageBitmap1);
                        collageImageShape1.setRotation(0);
                        fixAntiAlias(collageImageShape1);
                        collageImageShape1.setOnClickListener(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage1) {
                if(isEmptyImage1) {
                    collageImageShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                if (selectedImageUri2 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri2);
                        //mSelectedImageBitMap = bitmap;
                        // selectedBitmap2  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap2;
                        //Constants.capturedImageBitmap2 = bitmap;
                        //loadImageFilters();
                        collageImageShape2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape2.setImageBitmap(Constants.capturedImageBitmap2);
                        collageImageShape2.setRotation(0);
                        fixAntiAlias(collageImageShape2);
                        collageImageShape2.setOnClickListener(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage2) {
                if(isEmptyImage2) {
                    collageImageShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (selectedImageUri3 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri3);
                        //mSelectedImageBitMap = bitmap;
                        //  selectedBitmap3  = bitmap;
                        Constants.capturedImageBitmap =  Constants.capturedImageBitmap3;
                        //Constants.capturedImageBitmap3 = bitmap;

                        collageImageShape3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape3.setImageBitmap( Constants.capturedImageBitmap3);
                        collageImageShape3.setRotation(0);
                        fixAntiAlias(collageImageShape3);
                        collageImageShape3.setOnClickListener(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
                //if(isSelectedImage3) {
                if(isEmptyImage3) {
                    collageImageShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                isSelectedFrame3_1 = true;
                isSelectedImage=false;
                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);
                collageLayoutsRecycler.setVisibility(View.VISIBLE);

                break;


            case THREE_TWO:

                isSelectedFrame1_0 = false;
                isSelectedFrame2_1 = false;
                isSelectedFrame2_2 = false;
                isSelectedFrame3_1 = false;
                //isSelectedFrame3_2 = false;
                isSelectedFrame3_3 = false;
                isSelectedFrame4_1 = false;
                isSelectedFrame4_2 = false;
                isSelectedFrame4_3 = false;
                isSelectedFrame4_4 = false;
                isSelectedFrame6_1 = false;
                if(isSelectedFrame3_2){
                    Toast.makeText(this , "This is your current selection." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_3_2);
                initView();
                initView2();
                initView3();

                if (selectedImageUri1 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri1);
                        //mSelectedImageBitMap = bitmap;
                        // selectedBitmap1  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap1;
                        //Constants.capturedImageBitmap1 = bitmap;

                        collageImageShape1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape1.setImageBitmap(Constants.capturedImageBitmap1);
                        collageImageShape1.setRotation(0);
                        fixAntiAlias(collageImageShape1);
                        //selectedBitmap1 = Utilities.bitmapFromImageView(collageImageShape1);
                        //selectedBitmap1 = ((BitmapDrawable) collageImageShape1.getDrawable()).getBitmap();
                        collageImageShape1.setOnClickListener(this);
                    } catch ( Exception e) {
                        e.printStackTrace();
                    }


                }
                //if(isSelectedImage1) {
                if(isEmptyImage1) {
                    collageImageShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                if (selectedImageUri2 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri2);
                        //mSelectedImageBitMap = bitmap;
                        //  selectedBitmap2  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap2;
                        //Constants.capturedImageBitmap2 = bitmap;

                        collageImageShape2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape2.setImageBitmap(Constants.capturedImageBitmap2);
                        collageImageShape2.setRotation(0);
                        fixAntiAlias(collageImageShape2);
                        //selectedBitmap2 = Utilities.bitmapFromImageView(collageImageShape2);
                        //selectedBitmap2 = ((BitmapDrawable) collageImageShape2.getDrawable()).getBitmap();
                        collageImageShape2.setOnClickListener(this);
                    } catch ( Exception e) {
                        e.printStackTrace();
                    }


                }
                // if(isSelectedImage2) {
                if(isEmptyImage2) {
                    collageImageShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (selectedImageUri3 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri3);
                        //mSelectedImageBitMap = bitmap;
                        //  selectedBitmap3  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap3;
                        //Constants.capturedImageBitmap3 = bitmap;

                        collageImageShape3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape3.setImageBitmap(Constants.capturedImageBitmap3);
                        collageImageShape3.setRotation(0);
                        fixAntiAlias(collageImageShape3);
                        collageImageShape3.setOnClickListener(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                //if(isSelectedImage3) {
                if(isEmptyImage3) {
                    collageImageShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                isSelectedFrame3_2 = true;
                isSelectedImage=false;

                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);
                collageLayoutsRecycler.setVisibility(View.VISIBLE);

                break;


            case THREE_THREE:

                isSelectedFrame1_0 = false;
                isSelectedFrame2_1 = false;
                isSelectedFrame2_2 = false;
                isSelectedFrame3_1 = false;
                isSelectedFrame3_2 = false;
                //isSelectedFrame3_3 = false;
                isSelectedFrame4_1 = false;
                isSelectedFrame4_2 = false;
                isSelectedFrame4_3 = false;
                isSelectedFrame4_4 = false;
                isSelectedFrame6_1 = false;

                if(isSelectedFrame3_3){
                    Toast.makeText(this , "This is your current selection." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_3_3);
                initView();
                initView2();
                initView3();

                if (selectedImageUri1 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri1);
                        //mSelectedImageBitMap = bitmap;
                        //  selectedBitmap1  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap1;
                        //Constants.capturedImageBitmap1 = bitmap;

                        collageImageShape1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape1.setImageBitmap(Constants.capturedImageBitmap1);
                        collageImageShape1.setRotation(0);
                        fixAntiAlias(collageImageShape1);
                        //selectedBitmap1 = Utilities.bitmapFromImageView(collageImageShape1);
                        //selectedBitmap1 = ((BitmapDrawable) collageImageShape1.getDrawable()).getBitmap();
                        collageImageShape1.setOnClickListener(this);

                    } catch ( Exception e) {
                        e.printStackTrace();
                    }


                }
                //if(isSelectedImage1) {
                if(isEmptyImage1) {
                    collageImageShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                if (selectedImageUri2 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri2);
                        //mSelectedImageBitMap = bitmap;
                        //  selectedBitmap2  = bitmap;
                        Constants.capturedImageBitmap =  Constants.capturedImageBitmap2;
                        //Constants.capturedImageBitmap2 = bitmap;

                        collageImageShape2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape2.setImageBitmap( Constants.capturedImageBitmap2);
                        collageImageShape2.setRotation(0);
                        fixAntiAlias(collageImageShape2);
                        //selectedBitmap2 = Utilities.bitmapFromImageView(collageImageShape2);
                        //selectedBitmap2 = ((BitmapDrawable) collageImageShape2.getDrawable()).getBitmap();
                        collageImageShape2.setOnClickListener(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage2) {
                if(isEmptyImage2) {
                    collageImageShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (selectedImageUri3 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri3);
                        // mSelectedImageBitMap = bitmap;
                        //  selectedBitmap3  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap3;
                        //Constants.capturedImageBitmap3 = bitmap;
                        collageImageShape3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape3.setImageBitmap(Constants.capturedImageBitmap3);
                        collageImageShape3.setRotation(0);
                        fixAntiAlias(collageImageShape3);
                        //selectedBitmap3 = Utilities.bitmapFromImageView(collageImageShape3);
                        //selectedBitmap3  = ((BitmapDrawable) collageImageShape3.getDrawable()).getBitmap();
                        collageImageShape3.setOnClickListener(this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage3) {
                if(isEmptyImage3) {
                    collageImageShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                isSelectedFrame3_3 = true;
                isSelectedImage=false;
                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);

                collageSubToolsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);
                collageLayoutsRecycler.setVisibility(View.VISIBLE);

                break;

            case FOUR_ONE:

                isSelectedFrame1_0 = false;
                isSelectedFrame2_1 = false;
                isSelectedFrame2_2 = false;
                isSelectedFrame3_1 = false;
                isSelectedFrame3_2 = false;
                isSelectedFrame3_3 = false;
                //isSelectedFrame4_1 = false;
                isSelectedFrame4_2 = false;
                isSelectedFrame4_3 = false;
                isSelectedFrame4_4 = false;
                isSelectedFrame6_1 = false;

                if(isSelectedFrame4_1){
                    Toast.makeText(this , "This is your current selection." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_4_1);
                initView();
                initView2();
                initView3();
                initView4();

                if (selectedImageUri1 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri1);
                        //mSelectedImageBitMap = bitmap;
                        //  selectedBitmap1  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap1;
                        //Constants.capturedImageBitmap1 = bitmap;

                        collageImageShape1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape1.setImageBitmap(Constants.capturedImageBitmap1);
                        collageImageShape1.setRotation(0);
                        fixAntiAlias(collageImageShape1);
                        //selectedBitmap1 = Utilities.bitmapFromImageView(collageImageShape1);
                        //selectedBitmap1 = ((BitmapDrawable) collageImageShape1.getDrawable()).getBitmap();
                        collageImageShape1.setOnClickListener(this);

                    } catch ( Exception e) {
                        e.printStackTrace();
                    }


                }
                //if(isSelectedImage1) {
                if(isEmptyImage1) {
                    collageImageShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                if (selectedImageUri2 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri2);
                        //mSelectedImageBitMap = bitmap;
                        //  selectedBitmap2  = bitmap;
                        Constants.capturedImageBitmap =  Constants.capturedImageBitmap2;
                        //Constants.capturedImageBitmap2 = bitmap;

                        collageImageShape2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape2.setImageBitmap( Constants.capturedImageBitmap2);
                        collageImageShape2.setRotation(0);
                        fixAntiAlias(collageImageShape2);
                        //selectedBitmap2 = Utilities.bitmapFromImageView(collageImageShape2);
                        //selectedBitmap2 = ((BitmapDrawable) collageImageShape2.getDrawable()).getBitmap();
                        collageImageShape2.setOnClickListener(this);
                    } catch ( Exception e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage2) {
                if(isEmptyImage2) {
                    collageImageShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (selectedImageUri3 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri3);
                        // mSelectedImageBitMap = bitmap;
                        //  selectedBitmap3  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap3;
                        //Constants.capturedImageBitmap3 = bitmap;
                        collageImageShape3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape3.setImageBitmap(Constants.capturedImageBitmap3);
                        collageImageShape3.setRotation(0);
                        fixAntiAlias(collageImageShape3);
                        //selectedBitmap3 = Utilities.bitmapFromImageView(collageImageShape3);
                        //selectedBitmap3  = ((BitmapDrawable) collageImageShape3.getDrawable()).getBitmap();
                        collageImageShape3.setOnClickListener(this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage3) {
                if(isEmptyImage3) {
                    collageImageShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (selectedImageUri4 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri4);
                        // mSelectedImageBitMap = bitmap;
                        //  selectedBitmap4  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap4;
                        //Constants.capturedImageBitmap4 = bitmap;

                        collageImageShape4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape4.setImageBitmap(Constants.capturedImageBitmap4);
                        collageImageShape4.setRotation(0);
                        fixAntiAlias(collageImageShape4);
                        //selectedBitmap4 = Utilities.bitmapFromImageView(collageImageShape4);
                        //selectedBitmap4 = ((BitmapDrawable) collageImageShape4.getDrawable()).getBitmap();
                        collageImageShape4.setOnClickListener(this);

                    } catch ( Exception e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage4) {
                if(isEmptyImage4) {
                    collageImageShape4.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                isSelectedFrame4_1 = true;
                isSelectedImage=false;
                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);
                collageLayoutsRecycler.setVisibility(View.VISIBLE);

                break;

            case FOUR_TWO:

                isSelectedFrame1_0 = false;
                isSelectedFrame2_1 = false;
                isSelectedFrame2_2 = false;
                isSelectedFrame3_1 = false;
                isSelectedFrame3_2 = false;
                isSelectedFrame3_3 = false;
                isSelectedFrame4_1 = false;
                //isSelectedFrame4_2 = false;
                isSelectedFrame4_3 = false;
                isSelectedFrame4_4 = false;
                isSelectedFrame6_1 = false;
                if(isSelectedFrame4_2){
                    Toast.makeText(this , "This is your current selection." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_4_2);
                initView();
                initView2();
                initView3();
                initView4();

                if (selectedImageUri1 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri1);
                        //mSelectedImageBitMap = bitmap;
                        //  selectedBitmap1  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap1;
                        //Constants.capturedImageBitmap1 = bitmap;

                        collageImageShape1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape1.setImageBitmap(Constants.capturedImageBitmap1);
                        collageImageShape1.setRotation(0);
                        fixAntiAlias(collageImageShape1);
                        //selectedBitmap1 = Utilities.bitmapFromImageView(collageImageShape1);
                        //selectedBitmap1 = ((BitmapDrawable) collageImageShape1.getDrawable()).getBitmap();
                        collageImageShape1.setOnClickListener(this);

                    } catch ( Exception e) {
                        e.printStackTrace();
                    }


                }
                //if(isSelectedImage1) {
                if(isEmptyImage1) {
                    collageImageShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                if (selectedImageUri2 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri2);
                        //mSelectedImageBitMap = bitmap;
                        //  selectedBitmap2  = bitmap;
                        Constants.capturedImageBitmap =  Constants.capturedImageBitmap2;
                        //Constants.capturedImageBitmap2 = bitmap;

                        collageImageShape2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape2.setImageBitmap( Constants.capturedImageBitmap2);
                        collageImageShape2.setRotation(0);
                        fixAntiAlias(collageImageShape2);
                        //selectedBitmap2 = Utilities.bitmapFromImageView(collageImageShape2);
                        //selectedBitmap2 = ((BitmapDrawable) collageImageShape2.getDrawable()).getBitmap();
                        collageImageShape2.setOnClickListener(this);
                    } catch ( Exception e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage2) {
                if(isEmptyImage2) {
                    collageImageShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (selectedImageUri3 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri3);
                        // mSelectedImageBitMap = bitmap;
                        //  selectedBitmap3  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap3;
                        //Constants.capturedImageBitmap3 = bitmap;
                        collageImageShape3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape3.setImageBitmap(Constants.capturedImageBitmap3);
                        collageImageShape3.setRotation(0);
                        fixAntiAlias(collageImageShape3);
                        //selectedBitmap3 = Utilities.bitmapFromImageView(collageImageShape3);
                        //selectedBitmap3  = ((BitmapDrawable) collageImageShape3.getDrawable()).getBitmap();
                        collageImageShape3.setOnClickListener(this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage3) {
                if(isEmptyImage3) {
                    collageImageShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (selectedImageUri4 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri4);
                        // mSelectedImageBitMap = bitmap;
                        //  selectedBitmap4  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap4;
                        //Constants.capturedImageBitmap4 = bitmap;

                        collageImageShape4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape4.setImageBitmap(Constants.capturedImageBitmap4);
                        collageImageShape4.setRotation(0);
                        fixAntiAlias(collageImageShape4);
                        //selectedBitmap4 = Utilities.bitmapFromImageView(collageImageShape4);
                        //selectedBitmap4 = ((BitmapDrawable) collageImageShape4.getDrawable()).getBitmap();
                        collageImageShape4.setOnClickListener(this);

                    } catch ( Exception e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage4) {
                if(isEmptyImage4) {
                    collageImageShape4.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                isSelectedFrame4_2 = true;
                isSelectedImage=false;
                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);
                collageLayoutsRecycler.setVisibility(View.VISIBLE);

                break;

            case FOUR_THREE:

                isSelectedFrame1_0 = false;
                isSelectedFrame2_1 = false;
                isSelectedFrame2_2 = false;
                isSelectedFrame3_1 = false;
                isSelectedFrame3_2 = false;
                isSelectedFrame3_3 = false;
                isSelectedFrame4_1 = false;
                isSelectedFrame4_2 = false;
                //isSelectedFrame4_3 = false;
                isSelectedFrame4_4 = false;
                isSelectedFrame6_1 = false;
                if(isSelectedFrame4_3){
                    Toast.makeText(this , "This is your current selection." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_4_3);
                initView();
                initView2();
                initView3();
                initView4();

                if (selectedImageUri1 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri1);
                        //mSelectedImageBitMap = bitmap;
                        //  selectedBitmap1  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap1;
                        //Constants.capturedImageBitmap1 = bitmap;

                        collageImageShape1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape1.setImageBitmap(Constants.capturedImageBitmap1);
                        collageImageShape1.setRotation(0);
                        fixAntiAlias(collageImageShape1);
                        //selectedBitmap1 = Utilities.bitmapFromImageView(collageImageShape1);
                        //selectedBitmap1 = ((BitmapDrawable) collageImageShape1.getDrawable()).getBitmap();
                        collageImageShape1.setOnClickListener(this);

                    } catch ( Exception e) {
                        e.printStackTrace();
                    }


                }
                //if(isSelectedImage1) {
                if(isEmptyImage1) {
                    collageImageShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                if (selectedImageUri2 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri2);
                        //mSelectedImageBitMap = bitmap;
                        //  selectedBitmap2  = bitmap;
                        Constants.capturedImageBitmap =  Constants.capturedImageBitmap2;
                        //Constants.capturedImageBitmap2 = bitmap;

                        collageImageShape2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape2.setImageBitmap( Constants.capturedImageBitmap2);
                        collageImageShape2.setRotation(0);
                        fixAntiAlias(collageImageShape2);
                        //selectedBitmap2 = Utilities.bitmapFromImageView(collageImageShape2);
                        //selectedBitmap2 = ((BitmapDrawable) collageImageShape2.getDrawable()).getBitmap();
                        collageImageShape2.setOnClickListener(this);
                    } catch ( Exception e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage2) {
                if(isEmptyImage2) {
                    collageImageShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (selectedImageUri3 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri3);
                        // mSelectedImageBitMap = bitmap;
                        //  selectedBitmap3  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap3;
                        //Constants.capturedImageBitmap3 = bitmap;
                        collageImageShape3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape3.setImageBitmap(Constants.capturedImageBitmap3);
                        collageImageShape3.setRotation(0);
                        fixAntiAlias(collageImageShape3);
                        //selectedBitmap3 = Utilities.bitmapFromImageView(collageImageShape3);
                        //selectedBitmap3  = ((BitmapDrawable) collageImageShape3.getDrawable()).getBitmap();
                        collageImageShape3.setOnClickListener(this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage3) {
                if(isEmptyImage3) {
                    collageImageShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (selectedImageUri4 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri4);
                        // mSelectedImageBitMap = bitmap;
                        //  selectedBitmap4  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap4;
                        //Constants.capturedImageBitmap4 = bitmap;

                        collageImageShape4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape4.setImageBitmap(Constants.capturedImageBitmap4);
                        collageImageShape4.setRotation(0);
                        fixAntiAlias(collageImageShape4);
                        //selectedBitmap4 = Utilities.bitmapFromImageView(collageImageShape4);
                        //selectedBitmap4 = ((BitmapDrawable) collageImageShape4.getDrawable()).getBitmap();
                        collageImageShape4.setOnClickListener(this);

                    } catch ( Exception e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage4) {
                if(isEmptyImage4) {
                    collageImageShape4.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                isSelectedFrame4_4 = true;
                isSelectedImage=false;
                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);
                collageLayoutsRecycler.setVisibility(View.VISIBLE);
                break;

            case SIX_ONE:

                isSelectedFrame1_0 = false;
                isSelectedFrame2_1 = false;
                isSelectedFrame2_2 = false;
                isSelectedFrame3_1 = false;
                isSelectedFrame3_2 = false;
                isSelectedFrame3_3 = false;
                isSelectedFrame4_1 = false;
                isSelectedFrame4_2 = false;
                isSelectedFrame4_3 = false;
                isSelectedFrame4_4 = false;
                //isSelectedFrame6_1 = false;
                if(isSelectedFrame6_1){
                    Toast.makeText(this , "This is your current selection." , Toast.LENGTH_SHORT ).show();
                    return;
                }

                setContentView(R.layout.activity_collage_6_1);
                initView();
                initView2();
                initView3();
                initView4();
                initView5();
                initView6();


                if (selectedImageUri1 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri1);
                        //mSelectedImageBitMap = bitmap;
                        //  selectedBitmap1  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap1;
                        //Constants.capturedImageBitmap1 = bitmap;

                        collageImageShape1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape1.setImageBitmap(Constants.capturedImageBitmap1);
                        collageImageShape1.setRotation(0);
                        fixAntiAlias(collageImageShape1);
                        //selectedBitmap1 = Utilities.bitmapFromImageView(collageImageShape1);
                        //selectedBitmap1 = ((BitmapDrawable) collageImageShape1.getDrawable()).getBitmap();
                        collageImageShape1.setOnClickListener(this);

                    } catch ( Exception e) {
                        e.printStackTrace();
                    }


                }
                //if(isSelectedImage1) {
                if(isEmptyImage1) {
                    collageImageShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                if (selectedImageUri2 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri2);
                        //mSelectedImageBitMap = bitmap;
                        //  selectedBitmap2  = bitmap;
                        Constants.capturedImageBitmap =  Constants.capturedImageBitmap2;
                        //Constants.capturedImageBitmap2 = bitmap;

                        collageImageShape2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape2.setImageBitmap( Constants.capturedImageBitmap2);
                        collageImageShape2.setRotation(0);
                        fixAntiAlias(collageImageShape2);
                        //selectedBitmap2 = Utilities.bitmapFromImageView(collageImageShape2);
                        //selectedBitmap2 = ((BitmapDrawable) collageImageShape2.getDrawable()).getBitmap();
                        collageImageShape2.setOnClickListener(this);
                    } catch ( Exception e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage2) {
                if(isEmptyImage2) {
                    collageImageShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (selectedImageUri3 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri3);
                        // mSelectedImageBitMap = bitmap;
                        //  selectedBitmap3  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap3;
                        //Constants.capturedImageBitmap3 = bitmap;
                        collageImageShape3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape3.setImageBitmap(Constants.capturedImageBitmap3);
                        collageImageShape3.setRotation(0);
                        fixAntiAlias(collageImageShape3);
                        //selectedBitmap3  = ((BitmapDrawable) collageImageShape3.getDrawable()).getBitmap();
                        collageImageShape3.setOnClickListener(this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage3) {
                if(isEmptyImage3) {
                    collageImageShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (selectedImageUri4 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri4);
                        // mSelectedImageBitMap = bitmap;
                        //  selectedBitmap4  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap4;
                        //Constants.capturedImageBitmap4 = bitmap;

                        collageImageShape4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape4.setImageBitmap(Constants.capturedImageBitmap4);
                        collageImageShape4.setRotation(0);
                        fixAntiAlias(collageImageShape4);
                        //selectedBitmap4 = Utilities.bitmapFromImageView(collageImageShape4);
                        //selectedBitmap4 = ((BitmapDrawable) collageImageShape4.getDrawable()).getBitmap();
                        collageImageShape4.setOnClickListener(this);

                    } catch ( Exception e) {
                        e.printStackTrace();
                    }
                }
                //if(isSelectedImage4) {
                if(isEmptyImage4) {
                    collageImageShape4.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (selectedImageUri5 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri5);
                        //mSelectedImageBitMap = bitmap;
                        // selectedBitmap5  = bitmap;
                        Constants.capturedImageBitmap = Constants.capturedImageBitmap5;
                        //Constants.capturedImageBitmap5 = bitmap;

                        collageImageShape5.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape5.setImageBitmap(Constants.capturedImageBitmap5);
                        collageImageShape5.setRotation(0);
                        fixAntiAlias(collageImageShape5);
                        //selectedBitmap5 = Utilities.bitmapFromImageView(collageImageShape5);
                        //selectedBitmap5 = ((BitmapDrawable) collageImageShape5.getDrawable()).getBitmap();
                        collageImageShape5.setOnClickListener(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //if(isSelectedImage5) {
                if(isEmptyImage5) {
                    collageImageShape5.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (selectedImageUri6 != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  selectedImageUri6);
                        //mSelectedImageBitMap = bitmap;
                        // selectedBitmap6  = bitmap;
                        Constants.capturedImageBitmap =  Constants.capturedImageBitmap6;
                        //Constants.capturedImageBitmap6 = bitmap;

                        collageImageShape6.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        collageImageShape6.setImageBitmap( Constants.capturedImageBitmap6);
                        collageImageShape6.setRotation(0);
                        fixAntiAlias(collageImageShape6);
                        //selectedBitmap6 = Utilities.bitmapFromImageView(collageImageShape6);
                        //selectedBitmap6 = ((BitmapDrawable) collageImageShape6.getDrawable()).getBitmap();
                        collageImageShape6.setOnClickListener(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // if(isSelectedImage6) {
                if(isEmptyImage6) {
                    collageImageShape6.setBackgroundColor(Color.parseColor("#ffffff"));
                }


                isSelectedFrame6_1 = true;
                isSelectedImage=false;
                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);
                collageLayoutsRecycler.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {


        if (seekBar.getId() == R.id.collageBrightnessSeekBar) {
            int progressBrightness = collageBrightnessSeekBar.getProgress();
            onBrightnessChanged(progressBrightness-100);

        }

        if (seekBar.getId() == R.id.collageContrastSeekBar) {
            int progressContrast = collageContrastSeekBar.getProgress();
            progressContrast += 10;
            float floatVal = .10f * progressContrast;
            onContrastChanged(floatVal);
        }

        if (seekBar.getId() == R.id.collageSaturationSeekBar) {
            // saturation values are b/w 0.0f - 3.0f
            int progressSaturation = collageSaturationSeekBar.getProgress();
            float floatVal = .10f * progressSaturation;
            onSaturationChanged(floatVal);
        }

        if (seekBar.getId() == R.id.collageVignetteSeekBar) {
            int progressVignette = collageVignetteSeekBar.getProgress();
            onVignetteSubfilter(this , progressVignette);
        }
        if (seekBar.getId() == R.id.collageShadowSeekBar) {
            int progressShadow = collageShadowSeekBar.getProgress();
            onVignetteSubfilter(this , progressShadow*3);
        }
        if (seekBar.getId() == R.id.collageHighlightSeekBar) {
            int progressHighlight = (int)collageHighlightSeekBar.getProgress()/2;
            onBrightnessChanged(progressHighlight);
        }
        if (seekBar.getId() == R.id.collageTempSeekBar) {

            if (collageTempSeekBar.getProgress() > 0 && collageTempSeekBar.getProgress() < 30){
                int progressTempSeek = collageTempSeekBar.getProgress() ;
                float red = .02f * progressTempSeek;
                float green = .01f * progressTempSeek;
                float blue = .01f * progressTempSeek;
                onColorOverlaySubFilter(progressTempSeek, red, green, blue);
            }
            if (collageTempSeekBar.getProgress() > 30 && collageTempSeekBar.getProgress() < 65){
                int progressTempSeek = collageTempSeekBar.getProgress() ;
                float red = .01f * progressTempSeek;
                float green = .02f * progressTempSeek;
                float blue = .01f * progressTempSeek;
                onColorOverlaySubFilter(progressTempSeek, red, green, blue);
            }
            if (collageTempSeekBar.getProgress() > 65 && collageTempSeekBar.getProgress() <=100){
                int progressTempSeek = collageTempSeekBar.getProgress() ;
                float red = .01f * progressTempSeek;
                float green = .01f * progressTempSeek;
                float blue = .02f * progressTempSeek;
                onColorOverlaySubFilter(progressTempSeek, red, green, blue);
            }
        }
        if (seekBar.getId() == R.id.collageDenoiseSeekBar) {
            //CustomProgressDialog.show(collageActivity.this, "Processing...");
        }
        if (seekBar.getId() == R.id.collageTintSeekBar) {
            int progressTintSeek = collageTintSeekBar.getProgress() ;
            selectedImageView.setImageBitmap(applyTintEffect(finalImage.copy(Bitmap.Config.ARGB_8888, true), progressTintSeek*5));
        }

        if (seekBar.getId() == R.id.collageCurveSeekBar) {
            int  progressCurve = collageCurveSeekBar.getProgress();
            onToneCurveSubFilter(progressCurve, progressCurve, progressCurve,progressCurve, progressCurve , progressCurve);
        }
        if (seekBar.getId() == R.id.collageColorBalanceSeekBar) {
            if (collageColorBalanceSeekBar.getProgress() > 0 && collageColorBalanceSeekBar.getProgress() < 30){
                int progressColorBalance = collageColorBalanceSeekBar.getProgress() ;
                //int depth = progress ;
                float red = .01f * progressColorBalance;
                float green = .3f * progressColorBalance;
                float blue = .01f * progressColorBalance;

                onColorOverlaySubFilter(progressColorBalance, red, green, blue);
            }
            if (collageColorBalanceSeekBar.getProgress() > 30 && collageColorBalanceSeekBar.getProgress() < 65){
                int progressColorBalance = collageColorBalanceSeekBar.getProgress() ;
                float red = .01f * progressColorBalance;
                float green = .01f * progressColorBalance;
                float blue = .03f * progressColorBalance;

                onColorOverlaySubFilter(progressColorBalance, red, green, blue);
            }
            if (collageColorBalanceSeekBar.getProgress() > 65 && collageColorBalanceSeekBar.getProgress() <=100){
                int progressColorBalance = collageColorBalanceSeekBar.getProgress() ;
                float red = .03f * progressColorBalance;
                float green = .01f * progressColorBalance;
                float blue = .01f * progressColorBalance;

                onColorOverlaySubFilter(progressColorBalance, red, green, blue);
            }
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.collageDenoiseSeekBar) {
            CustomProgressDialog.show(CollageActivity.this, "Processing...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    selectedImageView.setImageBitmap(onNoise(finalImage.copy(Bitmap.Config.ARGB_8888, true)));

                    selectedImageView.setImageBitmap(onAdjustFinalImage());

                    CustomProgressDialog.dismiss();
                }
            }, 1000);
        }

        if (seekBar.getId() == R.id.collageSharpnessSeekBar) {
            selectedImageView.setImageBitmap(ImageFilter.applyFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true), ImageFilter.Filter.SHARPEN));
        }

    }




    /**************** Tools  / ToolsRecycler*****************/
    @Override
    public void onCollageSubToolsSelected(CollageSubToolsType toolsRecyclerType) {
        switch (toolsRecyclerType) {
            case CROP:

                isSelectedEffects = false ;
                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);

                if (isSelectedImage1) {
                    Bitmap bitmap = ((BitmapDrawable)collageImageShape1.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else  {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage2) {
                    Bitmap bitmap = ((BitmapDrawable) collageImageShape2.getDrawable()).getBitmap();
                    if (bitmap != null)
                    {
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else   {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage3) {
                    Bitmap bitmap = ((BitmapDrawable)collageImageShape3.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else   {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage4) {
                    Bitmap bitmap = ((BitmapDrawable) collageImageShape4.getDrawable()).getBitmap();
                    if (bitmap != null){
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else  {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage5) {
                    Bitmap bitmap = ((BitmapDrawable) collageImageShape5.getDrawable()).getBitmap();
                    if (bitmap != null){
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else   {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage6) {
                    Bitmap bitmap = ((BitmapDrawable)collageImageShape6.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else  {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case RT_LEFT:
                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isSelectedImage1) {
                    angleRotation1 = angleRotation1 - 10;
                    collageImageShape1.setRotation(angleRotation1);
                } else if (isSelectedImage2) {
                    angleRotation2 = angleRotation2 - 10;
                    collageImageShape2.setRotation(angleRotation2);
                }
                else if (isSelectedImage3) {
                    angleRotation3 = angleRotation3 - 10;
                    collageImageShape3.setRotation(angleRotation3);
                }else if (isSelectedImage4) {
                    angleRotation4 = angleRotation4 - 10;
                    collageImageShape4.setRotation(angleRotation4);
                }else if (isSelectedImage5) {
                    angleRotation5 = angleRotation5 - 10;
                    collageImageShape5.setRotation(angleRotation5);
                }else if (isSelectedImage6) {
                    angleRotation6 = angleRotation6 - 10;
                    collageImageShape6.setRotation(angleRotation6);
                }
                break;



            case RT_RIGHT:

                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isSelectedImage1) {
                    angleRotation1 = angleRotation1 + 10;
                    collageImageShape1.setRotation(angleRotation1);
                } else if (isSelectedImage2) {
                    angleRotation2 = angleRotation2 + 10;
                    collageImageShape2.setRotation(angleRotation2);
                }
                else if (isSelectedImage3) {
                    angleRotation3 = angleRotation3 + 10;
                    collageImageShape3.setRotation(angleRotation3);
                }else if (isSelectedImage4) {
                    angleRotation4 = angleRotation4 + 10;
                    collageImageShape4.setRotation(angleRotation4);
                }else if (isSelectedImage5) {
                    angleRotation5 = angleRotation5 + 10;
                    collageImageShape5.setRotation(angleRotation5);
                }else if (isSelectedImage6) {
                    angleRotation6 = angleRotation6 + 10;
                    collageImageShape6.setRotation(angleRotation6);
                }

                break;

            case RT_NONE:
                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isSelectedImage1) {
                    angleRotation1=0;
                    collageImageShape1.setRotation(0);
                }else if (isSelectedImage2) {
                    angleRotation2=0;
                    collageImageShape2.setRotation(0);
                }
                else if (isSelectedImage3) {
                    angleRotation3=0;
                    collageImageShape3.setRotation(0);
                }else if (isSelectedImage4) {
                    angleRotation4=0;
                    collageImageShape4.setRotation(0);
                }else if (isSelectedImage5) {
                    angleRotation5=0;
                    collageImageShape5.setRotation(0);
                }else if (isSelectedImage6) {
                    angleRotation6=0;
                    collageImageShape5.setRotation(0);
                }
                break;

            case FLIP:
                isSelectedEffects = false ;
                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isSelectedImage1) {
                    //collageImageShape1.setImageBitmap(Utilities.flipImage(Utilities.bitmapFromImageView(collageImageShape1)));
                    collageImageShape1.setImageBitmap(Utilities.flipImage(((BitmapDrawable) collageImageShape1.getDrawable()).getBitmap()));
                    //Bitmap flipBitmap = Utilities.bitmapFromImageView(collageImageShape1);
                    Bitmap flipBitmap = ((BitmapDrawable) collageImageShape1.getDrawable()).getBitmap();
                    Constants.capturedImageBitmap = flipBitmap;
                    Constants.capturedImageBitmap1 = flipBitmap;
                    loadImageFilters();
                }
                else if (isSelectedImage2) {
                    //collageImageShape2.setImageBitmap(Utilities.flipImage(Utilities.bitmapFromImageView(collageImageShape2)));
                    collageImageShape2.setImageBitmap(Utilities.flipImage(((BitmapDrawable) collageImageShape2.getDrawable()).getBitmap()));
                    Bitmap flipBitmap = ((BitmapDrawable) collageImageShape2.getDrawable()).getBitmap();
                    Constants.capturedImageBitmap = flipBitmap;
                    Constants.capturedImageBitmap2 = flipBitmap;
                    loadImageFilters();
                }
                else if (isSelectedImage3) {
                    collageImageShape3.setImageBitmap(Utilities.flipImage(((BitmapDrawable) collageImageShape3.getDrawable()).getBitmap()));
                    Bitmap flipBitmap = ((BitmapDrawable) collageImageShape3.getDrawable()).getBitmap();
                    Constants.capturedImageBitmap = flipBitmap;
                    Constants.capturedImageBitmap3 = flipBitmap;
                    loadImageFilters();
                }
                else if (isSelectedImage4) {
                    collageImageShape4.setImageBitmap(Utilities.flipImage(((BitmapDrawable) collageImageShape4.getDrawable()).getBitmap()));
                    Bitmap flipBitmap = ((BitmapDrawable) collageImageShape4.getDrawable()).getBitmap();
                    Constants.capturedImageBitmap = flipBitmap;
                    Constants.capturedImageBitmap4 = flipBitmap;

                    loadImageFilters();
                }
                else if (isSelectedImage5) {
                    collageImageShape5.setImageBitmap(Utilities.flipImage(((BitmapDrawable) collageImageShape5.getDrawable()).getBitmap()));
                    Bitmap flipBitmap = ((BitmapDrawable) collageImageShape5.getDrawable()).getBitmap();
                    Constants.capturedImageBitmap = flipBitmap;
                    Constants.capturedImageBitmap5 = flipBitmap;
                    loadImageFilters();
                }
                else if (isSelectedImage6) {
                    collageImageShape6.setImageBitmap(Utilities.flipImage(((BitmapDrawable) collageImageShape6.getDrawable()).getBitmap()));
                    Bitmap flipBitmap = ((BitmapDrawable) collageImageShape6.getDrawable()).getBitmap();
                    Constants.capturedImageBitmap = flipBitmap;
                    Constants.capturedImageBitmap6 = flipBitmap;
                    loadImageFilters();
                }else if (!imageSelectedOrNot) {
                    Toast.makeText(this , "Sorry, Image not selected.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    /************************All methods************************/
    private boolean imageSelectedOrNot(){
        imageSelectedOrNot = false;
        if (isSelectedImage1) {
            return imageSelectedOrNot = true;
        }
        else if (isSelectedImage2) {
            return imageSelectedOrNot = true;
        }
        else if (isSelectedImage3) {
            return imageSelectedOrNot = true;
        }
        else if (isSelectedImage4) {
            return imageSelectedOrNot = true;
        }
        else if (isSelectedImage5) {
            return imageSelectedOrNot = true;
        }
        else if (isSelectedImage6) {
            return imageSelectedOrNot = true;
        }
        else   {
            return imageSelectedOrNot = false;
        }
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


    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @SuppressLint("ObsoleteSdkInt")
    public  void fixAntiAlias(View viewAntiAlias) {
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CollageActivity.this);
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

                if(collageFrameShape1!=null)
                    collageFrameShape1.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape2!=null)
                    collageFrameShape2.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape3!=null)
                    collageFrameShape3.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape4!=null)
                    collageFrameShape4.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape5!=null)
                    collageFrameShape5.setBackgroundColor(getResources().getColor(R.color.white));
                if(collageFrameShape6!=null)
                    collageFrameShape6.setBackgroundColor(getResources().getColor(R.color.white));

                Bitmap mBitmap = Utilities.createBitmapFromLayout(mCollageRLayout);
                Utilities.saveSelfieProImage(mContext , mBitmap);

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
    /*************Compress Image End************/


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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
    public void onAdjustTollsSelected(AdjustToolsType adjustToolsType) {
        switch (adjustToolsType) {

            case RESET:

                collageBlankLayoutBottom.setVisibility(View.VISIBLE);
                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);

                collageSubToolsLayout.setVisibility(View.GONE);
                collageEditToolsRecycleView.setVisibility(View.GONE);
                collageBrightnessSeekBar.setVisibility(View.GONE);
                collageContrastSeekBar.setVisibility(View.GONE);
                collageSaturationSeekBar.setVisibility(View.GONE);
                collageSharpnessSeekBar.setVisibility(View.GONE);
                collageVignetteSeekBar.setVisibility(View.GONE);
                collageShadowSeekBar.setVisibility(View.GONE);
                collageHighlightSeekBar.setVisibility(View.GONE);
                collageTempSeekBar.setVisibility(View.GONE);
                collageTintSeekBar.setVisibility(View.GONE);
                collageDenoiseSeekBar.setVisibility(View.GONE);
                collageCurveSeekBar.setVisibility(View.GONE);
                collageColorBalanceSeekBar.setVisibility(View.GONE);
                collageAdjustSeekbarLLayout.setVisibility(View.GONE);

                collageEffectsLLayout  .setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);

                isSelectedEffects = false ;
                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isSelectedImage1) {
                    collageImageShape1.setImageBitmap(Constants.collageOrignalBitmap1);
                    Bitmap flipBitmap = ((BitmapDrawable) collageImageShape1.getDrawable()).getBitmap();
                    Constants.capturedImageBitmap = flipBitmap;
                    Constants.capturedImageBitmap1 = flipBitmap;
                    loadImageFilters();
                }
                else if (isSelectedImage2) {
                    collageImageShape2.setImageBitmap(Constants.collageOrignalBitmap2);
                    Bitmap flipBitmap = ((BitmapDrawable) collageImageShape2.getDrawable()).getBitmap();
                    Constants.capturedImageBitmap = flipBitmap;
                    Constants.capturedImageBitmap2 = flipBitmap;
                    loadImageFilters();
                }
                else if (isSelectedImage3) {
                    collageImageShape3.setImageBitmap(Constants.collageOrignalBitmap3);
                    Bitmap flipBitmap = ((BitmapDrawable) collageImageShape3.getDrawable()).getBitmap();
                    Constants.capturedImageBitmap = flipBitmap;
                    Constants.capturedImageBitmap3 = flipBitmap;
                    loadImageFilters();
                }
                else if (isSelectedImage4) {
                    collageImageShape4.setImageBitmap(Constants.collageOrignalBitmap4);
                    Bitmap flipBitmap = ((BitmapDrawable) collageImageShape4.getDrawable()).getBitmap();
                    Constants.capturedImageBitmap = flipBitmap;
                    Constants.capturedImageBitmap4 = flipBitmap;
                    loadImageFilters();
                }
                else if (isSelectedImage5) {
                    collageImageShape5.setImageBitmap(Constants.collageOrignalBitmap5);
                    Bitmap flipBitmap = ((BitmapDrawable) collageImageShape5.getDrawable()).getBitmap();
                    Constants.capturedImageBitmap = flipBitmap;
                    Constants.capturedImageBitmap5 = flipBitmap;
                    loadImageFilters();
                }
                else if (isSelectedImage6) {
                    collageImageShape6.setImageBitmap(Constants.collageOrignalBitmap6);
                    Bitmap flipBitmap = ((BitmapDrawable) collageImageShape6.getDrawable()).getBitmap();
                    Constants.capturedImageBitmap = flipBitmap;
                    Constants.capturedImageBitmap6 = flipBitmap;
                    loadImageFilters();
                }else if (!imageSelectedOrNot) {
                    Toast.makeText(this , "Sorry, Image not selected.", Toast.LENGTH_SHORT).show();
                }

                break;


            case BRIGHTNESS:

                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageEditToolsRecycleView.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);
                collageBrightnessSeekBar.setVisibility(View.VISIBLE);
                collageContrastSeekBar.setVisibility(View.GONE);
                collageSaturationSeekBar.setVisibility(View.GONE);
                collageSharpnessSeekBar.setVisibility(View.GONE);
                collageVignetteSeekBar.setVisibility(View.GONE);
                collageShadowSeekBar.setVisibility(View.GONE);
                collageHighlightSeekBar.setVisibility(View.GONE);
                collageTempSeekBar.setVisibility(View.GONE);
                collageTintSeekBar.setVisibility(View.GONE);
                collageDenoiseSeekBar.setVisibility(View.GONE);
                collageCurveSeekBar.setVisibility(View.GONE);
                collageColorBalanceSeekBar.setVisibility(View.GONE);

                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.VISIBLE);

                collageEffectsLLayout  .setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);


                finalImage = onAdjustFinalImage();

                 // keeping brightness value b/w -100 / +100
                collageBrightnessSeekBar.setMax(200);
                collageBrightnessSeekBar.setProgress(100);
                break;

            case CONTRAST:

                // keeping contrast value b/w 1.0 - 3.0
                collageContrastSeekBar.setMax(30);
                collageContrastSeekBar.setProgress(0);

                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageEditToolsRecycleView.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);

                collageBrightnessSeekBar.setVisibility(View.GONE);
                collageContrastSeekBar.setVisibility(View.VISIBLE);
                collageSaturationSeekBar.setVisibility(View.GONE);
                collageSharpnessSeekBar.setVisibility(View.GONE);
                collageVignetteSeekBar.setVisibility(View.GONE);
                collageShadowSeekBar.setVisibility(View.GONE);
                collageHighlightSeekBar.setVisibility(View.GONE);
                collageTempSeekBar.setVisibility(View.GONE);
                collageTintSeekBar.setVisibility(View.GONE);
                collageDenoiseSeekBar.setVisibility(View.GONE);
                collageCurveSeekBar.setVisibility(View.GONE);
                collageColorBalanceSeekBar.setVisibility(View.GONE);

                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);

                finalImage = onAdjustFinalImage();

                break;
            case SATURATION:

                // keeping saturation value b/w 0.0 - 3.0
                collageSaturationSeekBar.setMax(30);
                collageSaturationSeekBar.setProgress(10);

                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageEditToolsRecycleView.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);

                collageBrightnessSeekBar.setVisibility(View.GONE);
                collageContrastSeekBar.setVisibility(View.GONE);
                collageSaturationSeekBar.setVisibility(View.VISIBLE);
                collageSharpnessSeekBar.setVisibility(View.GONE);
                collageVignetteSeekBar.setVisibility(View.GONE);
                collageShadowSeekBar.setVisibility(View.GONE);
                collageHighlightSeekBar.setVisibility(View.GONE);
                collageTempSeekBar.setVisibility(View.GONE);
                collageTintSeekBar.setVisibility(View.GONE);
                collageDenoiseSeekBar.setVisibility(View.GONE);
                collageCurveSeekBar.setVisibility(View.GONE);
                collageColorBalanceSeekBar.setVisibility(View.GONE);

                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);

                 finalImage = onAdjustFinalImage();
                break;
            case SHARPNESS:

                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageEditToolsRecycleView.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);

                collageBrightnessSeekBar.setVisibility(View.GONE);
                collageContrastSeekBar.setVisibility(View.GONE);
                collageSaturationSeekBar.setVisibility(View.GONE);
                collageSharpnessSeekBar.setVisibility(View.VISIBLE);
                collageVignetteSeekBar.setVisibility(View.GONE);
                collageShadowSeekBar.setVisibility(View.GONE);
                collageHighlightSeekBar.setVisibility(View.GONE);
                collageTempSeekBar.setVisibility(View.GONE);
                collageTintSeekBar.setVisibility(View.GONE);
                collageDenoiseSeekBar.setVisibility(View.GONE);
                collageCurveSeekBar.setVisibility(View.GONE);
                collageColorBalanceSeekBar.setVisibility(View.GONE);

                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);

                finalImage = onAdjustFinalImage();
                break;


            case VIGNETTE:

                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageEditToolsRecycleView.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);

                collageBrightnessSeekBar.setVisibility(View.GONE);
                collageContrastSeekBar.setVisibility(View.GONE);
                collageSaturationSeekBar.setVisibility(View.GONE);
                collageSharpnessSeekBar.setVisibility(View.GONE);
                collageVignetteSeekBar.setVisibility(View.VISIBLE);
                collageShadowSeekBar.setVisibility(View.GONE);
                collageHighlightSeekBar.setVisibility(View.GONE);
                collageTempSeekBar.setVisibility(View.GONE);
                collageTintSeekBar.setVisibility(View.GONE);
                collageDenoiseSeekBar.setVisibility(View.GONE);
                collageCurveSeekBar.setVisibility(View.GONE);
                collageColorBalanceSeekBar.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);

                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                 finalImage = onAdjustFinalImage();
                break;


            case SHADOW:

                collageShadowSeekBar.setMax(80);
                //collageShadowSeekBar.setMax(0);
                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageEditToolsRecycleView.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);

                collageBrightnessSeekBar.setVisibility(View.GONE);
                collageContrastSeekBar.setVisibility(View.GONE);
                collageSaturationSeekBar.setVisibility(View.GONE);
                collageSharpnessSeekBar.setVisibility(View.GONE);
                collageVignetteSeekBar.setVisibility(View.GONE);
                collageShadowSeekBar.setVisibility(View.VISIBLE);
                collageHighlightSeekBar.setVisibility(View.GONE);
                collageTempSeekBar.setVisibility(View.GONE);
                collageTintSeekBar.setVisibility(View.GONE);
                collageDenoiseSeekBar.setVisibility(View.GONE);
                collageCurveSeekBar.setVisibility(View.GONE);
                collageColorBalanceSeekBar.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);

                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.VISIBLE);

                 finalImage = onAdjustFinalImage();

                break;
            case HIGH_LIGHT:

                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageEditToolsRecycleView.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);

                collageBrightnessSeekBar.setVisibility(View.GONE);
                collageContrastSeekBar.setVisibility(View.GONE);
                collageSaturationSeekBar.setVisibility(View.GONE);
                collageSharpnessSeekBar.setVisibility(View.GONE);
                collageVignetteSeekBar.setVisibility(View.GONE);
                collageShadowSeekBar.setVisibility(View.GONE);
                collageHighlightSeekBar.setVisibility(View.VISIBLE);
                collageTempSeekBar.setVisibility(View.GONE);
                collageTintSeekBar.setVisibility(View.GONE);
                collageDenoiseSeekBar.setVisibility(View.GONE);
                collageCurveSeekBar.setVisibility(View.GONE);
                collageColorBalanceSeekBar.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);

                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.VISIBLE);

                finalImage = onAdjustFinalImage();
                break;

            case TEMP:
                collageTempSeekBar.setMax(100);
                 collageTempSeekBar.setProgress(0);

                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageEditToolsRecycleView.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);

                collageBrightnessSeekBar.setVisibility(View.GONE);
                collageContrastSeekBar.setVisibility(View.GONE);
                collageSaturationSeekBar.setVisibility(View.GONE);
                collageSharpnessSeekBar.setVisibility(View.GONE);
                collageVignetteSeekBar.setVisibility(View.GONE);
                collageShadowSeekBar.setVisibility(View.GONE);
                collageHighlightSeekBar.setVisibility(View.GONE);
                collageTempSeekBar.setVisibility(View.VISIBLE);
                collageTintSeekBar.setVisibility(View.GONE);
                collageDenoiseSeekBar.setVisibility(View.GONE);
                collageCurveSeekBar.setVisibility(View.GONE);
                collageColorBalanceSeekBar.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);

                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.VISIBLE);

                 finalImage = onAdjustFinalImage();
                break;


            case TINT:

                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageEditToolsRecycleView.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);

                collageBrightnessSeekBar.setVisibility(View.GONE);
                collageContrastSeekBar.setVisibility(View.GONE);
                collageSaturationSeekBar.setVisibility(View.GONE);
                collageSharpnessSeekBar.setVisibility(View.GONE);
                collageVignetteSeekBar.setVisibility(View.GONE);
                collageShadowSeekBar.setVisibility(View.GONE);
                collageHighlightSeekBar.setVisibility(View.GONE);
                collageTempSeekBar.setVisibility(View.GONE);
                collageTintSeekBar.setVisibility(View.VISIBLE);
                collageDenoiseSeekBar.setVisibility(View.GONE);
                collageCurveSeekBar.setVisibility(View.GONE);
                collageColorBalanceSeekBar.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);

                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.VISIBLE);

                finalImage = onAdjustFinalImage();
                break;
            case DENOISE:

                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageEditToolsRecycleView.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);

                collageBrightnessSeekBar.setVisibility(View.GONE);
                collageContrastSeekBar.setVisibility(View.GONE);
                collageSaturationSeekBar.setVisibility(View.GONE);
                collageSharpnessSeekBar.setVisibility(View.GONE);
                collageVignetteSeekBar.setVisibility(View.GONE);
                collageShadowSeekBar.setVisibility(View.GONE);
                collageHighlightSeekBar.setVisibility(View.GONE);
                collageTempSeekBar.setVisibility(View.GONE);
                collageTintSeekBar.setVisibility(View.GONE);
                collageDenoiseSeekBar.setVisibility(View.VISIBLE);
                collageCurveSeekBar.setVisibility(View.GONE);
                collageColorBalanceSeekBar.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);

                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.VISIBLE);

                finalImage = onAdjustFinalImage();

                break;
            case CURVE:

                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageEditToolsRecycleView.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);

                collageBrightnessSeekBar.setVisibility(View.GONE);
                collageContrastSeekBar.setVisibility(View.GONE);
                collageSaturationSeekBar.setVisibility(View.GONE);
                collageSharpnessSeekBar.setVisibility(View.GONE);
                collageVignetteSeekBar.setVisibility(View.GONE);
                collageShadowSeekBar.setVisibility(View.GONE);
                collageHighlightSeekBar.setVisibility(View.GONE);
                collageTempSeekBar.setVisibility(View.GONE);
                collageTintSeekBar.setVisibility(View.GONE);
                collageDenoiseSeekBar.setVisibility(View.GONE);
                collageCurveSeekBar.setVisibility(View.VISIBLE);
                collageColorBalanceSeekBar.setVisibility(View.GONE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);

                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                 finalImage = onAdjustFinalImage();
                break;
            case COLOR_BALANCE:
                collageColorBalanceSeekBar.setMax(100);
                //collageColorBalanceSeekBar.setProgress(0);
                collageBlankLayoutBottom.setVisibility(View.GONE);
                collageEditToolsRecycleView.setVisibility(View.GONE);
                collageSubToolsLayout.setVisibility(View.VISIBLE);

                collageBrightnessSeekBar.setVisibility(View.GONE);
                collageContrastSeekBar.setVisibility(View.GONE);
                collageSaturationSeekBar.setVisibility(View.GONE);
                collageSharpnessSeekBar.setVisibility(View.GONE);
                collageVignetteSeekBar.setVisibility(View.GONE);
                collageShadowSeekBar.setVisibility(View.GONE);
                collageHighlightSeekBar.setVisibility(View.GONE);
                collageTempSeekBar.setVisibility(View.GONE);
                collageTintSeekBar.setVisibility(View.GONE);
                collageDenoiseSeekBar.setVisibility(View.GONE);
                collageCurveSeekBar.setVisibility(View.GONE);
                collageColorBalanceSeekBar.setVisibility(View.VISIBLE);
                collageEffectsLLayout  .setVisibility(View.GONE);
                collageSubToolsRecycler.setVisibility(View.GONE);
                collageLayoutsRecycler.setVisibility(View.GONE);

                collageAdjustToolsRecycler.setVisibility(View.VISIBLE);
                collageAdjustSeekbarLLayout.setVisibility(View.VISIBLE);
                 finalImage = onAdjustFinalImage();
                break;
        }
    }

    @Override
    public void onBrightnessChanged(final int brightness) {
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        //Bitmap finalImage = ((BitmapDrawable) gallerySelectedImageShape.getDrawable()).getBitmap();


        selectedImageView.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(final float contrast) {
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        selectedImageView.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(final float saturation) {
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        selectedImageView.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onColorOverlaySubFilter(int depth, float red, float green, float blue) {
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ColorOverlaySubFilter(depth , red , green , blue));
        selectedImageView.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onVignetteSubfilter(Context context, int alpha) {
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new VignetteSubfilter(context , alpha));
        selectedImageView.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));

    }

    @Override
    public void onToneCurveSubFilter( int  redKnotsX, int  redKnotsY, int  greenKnotsX, int  greenKnotsY, int blueKnotsX , int blueKnotsY) {
        Filter myFilter = new Filter();
        Point[] rgbKnots;
        rgbKnots = new Point[3];

        rgbKnots[0] = new Point(redKnotsX, redKnotsY);
        rgbKnots[1] = new Point(175-greenKnotsX, 139-greenKnotsY);
        rgbKnots[2] = new Point(255-blueKnotsX, 255-blueKnotsY);

//        rgbKnots[0] = new Point(0, 0);
//        rgbKnots[1] = new Point(175, 139);
//        rgbKnots[2] = new Point(255, 255);

        myFilter.addSubFilter(new ToneCurveSubFilter(rgbKnots, null, null, null));
        selectedImageView.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {

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

    public Bitmap onAdjustFinalImage(){
        Bitmap bitmap = ((BitmapDrawable) selectedImageView.getDrawable()).getBitmap();
        return bitmap;
    }
}




