package com.mojodigi.selfiepro.collage;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.mojodigi.selfiepro.R;
import com.mojodigi.selfiepro.activity.EditImageActivity;
import com.mojodigi.selfiepro.adapter.CollageEditToolsAdapter;
import com.mojodigi.selfiepro.adapter.CollageRecycleAdapter;
import com.mojodigi.selfiepro.adapter.FrameRecycleAdapter;
import com.mojodigi.selfiepro.adapter.ToolsRecyclerAdapter;
import com.mojodigi.selfiepro.enums.CollageEditToolsType;
import com.mojodigi.selfiepro.enums.CollageFrameType;
import com.mojodigi.selfiepro.enums.FrameType;
import com.mojodigi.selfiepro.enums.ToolsRecyclerType;
import com.mojodigi.selfiepro.filterUtils.ThumbnailCallback;
import com.mojodigi.selfiepro.filterUtils.ThumbnailItem;
import com.mojodigi.selfiepro.filterUtils.ThumbnailsAdapter;
import com.mojodigi.selfiepro.filterUtils.ThumbnailsManager;
import com.mojodigi.selfiepro.interfaces.OnCollageEditTollsSelected;
import com.mojodigi.selfiepro.interfaces.OnCollageFrameSelected;
import com.mojodigi.selfiepro.interfaces.OnFrameSelected;
import com.mojodigi.selfiepro.interfaces.OnToolsRecyclerSelected;
import com.mojodigi.selfiepro.utils.Constants;
import com.mojodigi.selfiepro.utils.MyPreference;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.ViewType;


public class CollageSelectedActivity extends AppCompatActivity implements OnPhotoEditorListener, View.OnTouchListener, View.OnClickListener,

          OnCollageFrameSelected, OnCollageEditTollsSelected , SeekBar.OnSeekBarChangeListener, OnFrameSelected  , ThumbnailCallback /*, FilterListener*/ , OnToolsRecyclerSelected {

    private static final String TAG = CollageSelectedActivity.class.getSimpleName();


    private static final int PICK_CAMERA_REQUEST_ONE = 11;

    private static final int PICK_CAMERA_REQUEST_TWO = 22;

    private static final int PICK_GALLARY_REQUEST_ONE = 111;
    private static final int PICK_GALLARY_REQUEST_TWO = 222;
    private static final int PICK_GALLARY_REQUEST_THREE = 333;
    private static final int PICK_GALLARY_REQUEST_FOUR = 444;
    private static final int PICK_GALLARY_REQUEST_FIVE = 555;
    private static final int PICK_GALLARY_REQUEST_SIX = 666;

    // These matrices will be used to move and zoom image
    private Matrix  mMatrixShape1 , mMatrixShape2 , mMatrixShape3 , mMatrixShape4 , mMatrixShape5 , mMatrixShape6 ;

    private Matrix  mMatrixShape2_1 , mMatrixShape2_2;

    private Matrix  mMatrixSaved1 , mMatrixSaved2 , mMatrixSaved3 , mMatrixSaved4 , mMatrixSaved5 , mMatrixSaved6;
    private Matrix  mMatrixSaved2_1 , mMatrixSaved2_2;

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // Remember some things for zooming
    private PointF startPointF, midPointF;
    private float oldDist = 1f;

    private Bitmap mBitmap , mBitmap1 , mBitmap2 , mBitmap3 , mBitmap4 , mBitmap5 , mBitmap6 ;
    private Bitmap mBitmapRotate1, mBitmapRotate2, mBitmapRotate3 , mBitmapRotate4 , mBitmapRotate5 , mBitmapRotate6;

    private Bitmap  mSelectedImageBitMap;

    //SvgImageView mCollageFrameShape4 ;
    //SvgMaskedImageView mCollageFrameShape4 ;
    // private SVGImageView mCollageFrameShape4 ;

    private ImageView mCollageFrameShape4 ;
    private ImageView   mMainCollageFrameImage ,   mCollageFrameShape1, mCollageFrameShape2, mCollageFrameShape3,  /*mCollageFrameShape4 ,*/ mCollageFrameShape5 ,mCollageFrameShape6;


    private boolean isSelectedImage = false;
    private boolean isSelectedImage1 = false;
    private boolean isSelectedImage2 = false;
    private boolean isSelectedImage3 = false;
    private boolean isSelectedImage4 = false;
    private boolean isSelectedImage5 = false;
    private boolean isSelectedImage6 = false;

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


    private String mImageName ;

    float angle1 = 0 , angle2 = 0 , angle3 = 0 , angle4 = 0, angle5 = 0 , angle6 = 0 ;
    private RelativeLayout mCollageRLayout;

    private boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    private LinearLayout   mCollageEditBackLLayout, mCollageSaveLayout,  collageEffectsSeekbarLLayout , frameRecyclerLayout ;

    private MyPreference mMyPrecfence = null;


    private RecyclerView mCollageListRecycleView , mCollageFrameRecycleView;
    private RecyclerView mCollageEditToolsRecycleView  /*, collageFilterView */;


    private CollageRecycleAdapter mCollageRecycleAdapter;
    private FrameRecycleAdapter mFrameRecycleAdapter;
    private CollageEditToolsAdapter mCollageEditToolsAdapter;
    private ToolsRecyclerAdapter toolsRecyclerAdapter;

    private String mIntentType = "";
    private Context mContext ;
    private File mImageFile ;
    private boolean imageSelectedOrNot = false;
    private boolean isSelectedEffects = false;

    private SeekBar collageBrightness , collageContrast;

    private ImageView selectedImageView;

    private static final int CROP_IMAGE = 001;

    private   FrameLayout frameShape1 , frameShape2 , frameShape3 ,frameShape4, frameShape5, frameShape6 ;

    private Bitmap selectedBitmap  , selectedBitmap1 , selectedBitmap2 ,selectedBitmap3 ,selectedBitmap4 ,selectedBitmap5 ,selectedBitmap6;

    private RecyclerView imageEffectsThumbnails , toolsRecyclerView;

    public static CollageSelectedActivity instance;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collage_selected_image);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        instance = this;

        if(mContext==null) {
            mContext = CollageSelectedActivity.this;
        }
        if (mMyPrecfence == null) {
            mMyPrecfence = MyPreference.getMyPreferenceInstance(mContext);
        }

        try {
            mMyPrecfence.saveString(Constants.COLLAGE_ACTIVITY , "true");
            mIntentType = mMyPrecfence.getString(Constants.INTENT_TYPE);
        } catch (Exception ex) {
            ex.getStackTrace();
        }

        //mMainCollageFrameImage = (ImageView) findViewById(R.id.idMainCollageFrameImage);
//        if (mIntentType.equalsIgnoreCase("IntentCollage")) {
//         mMainCollageFrameImage.setBackgroundResource(R.drawable.collage2_1);
//         }

        initView();
    }




    @Override
    protected void onResume() {
        super.onResume();

        //collageBrightness.setVisibility(View.GONE);
        //collageContrast.setVisibility(View.GONE);

        collageEffectsSeekbarLLayout.setVisibility(View.GONE);
        mCollageFrameRecycleView.setVisibility(View.GONE);
        mCollageListRecycleView.setVisibility(View.GONE);
        imageEffectsThumbnails.setVisibility(View.GONE);
        frameRecyclerLayout.setVisibility(View.GONE);
        toolsRecyclerView.setVisibility(View.GONE);

    }



    private void initView() {

        mMatrixShape2_1 = new Matrix();
        mMatrixShape2_2 = new Matrix();

        mMatrixShape1 = new Matrix();
        mMatrixShape2 = new Matrix();
        mMatrixShape3 = new Matrix();
        mMatrixShape4 = new Matrix();
        mMatrixShape5 = new Matrix();
        mMatrixShape6 = new Matrix();

        mMatrixSaved2_1 = new Matrix();
        mMatrixSaved2_2 = new Matrix();

        mMatrixSaved1 = new Matrix();
        mMatrixSaved2 = new Matrix();
        mMatrixSaved3 = new Matrix();
        mMatrixSaved4 = new Matrix();
        mMatrixSaved5 = new Matrix();
        mMatrixSaved6 = new Matrix();

        startPointF = new PointF();
        midPointF = new PointF();

        imageEffectsThumbnails = (RecyclerView) findViewById(R.id.imageEffectsThumbnails);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        imageEffectsThumbnails.setLayoutManager(layoutManager);
        imageEffectsThumbnails.setHasFixedSize(true);


        mCollageRecycleAdapter = new CollageRecycleAdapter(this);
        mFrameRecycleAdapter = new FrameRecycleAdapter(this);
        mCollageEditToolsAdapter = new CollageEditToolsAdapter(this);
        toolsRecyclerAdapter = new ToolsRecyclerAdapter(this);

        mCollageRLayout = (RelativeLayout) findViewById(R.id.idCollageRLayout);

        mMainCollageFrameImage = (ImageView) findViewById(R.id.idMainCollageFrameImage);

        frameShape1 = (FrameLayout) findViewById(R.id.frameShape1);
        frameShape1.setOnClickListener(this);

        mCollageFrameShape1 = (ImageView) findViewById(R.id.idCollageFrameShape1);
        mCollageFrameShape1.setOnClickListener(this);
        selectedImageView = mCollageFrameShape1;





        mCollageEditBackLLayout = (LinearLayout) findViewById(R.id.idCollageEditBackLLayout);
        mCollageSaveLayout = (LinearLayout) findViewById(R.id.idCollageSaveLayout);
        frameRecyclerLayout = (LinearLayout) findViewById(R.id.frameRecyclerLayout);
        frameRecyclerLayout.setVisibility(View.GONE);

        mCollageEditBackLLayout.setOnClickListener(this);
        mCollageSaveLayout.setOnClickListener(this);

        collageEffectsSeekbarLLayout = (LinearLayout) findViewById(R.id.collageEffectsSeekbarLLayout);
        collageEffectsSeekbarLLayout.setOnClickListener(this);

        collageBrightness = (SeekBar)findViewById(R.id.collageBrightness);
        collageContrast = (SeekBar)findViewById(R.id.collageContrast);
        collageBrightness.setOnSeekBarChangeListener(this);
        collageContrast.setOnSeekBarChangeListener(this);

        mCollageListRecycleView = (RecyclerView) findViewById(R.id.idCollageListRecycleView);
        mCollageFrameRecycleView = (RecyclerView) findViewById(R.id.idCollageFrameRecycleView);
        mCollageListRecycleView.setVisibility(View.GONE);
        mCollageFrameRecycleView.setVisibility(View.GONE);

        LinearLayoutManager mCollageListLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mCollageListRecycleView.setLayoutManager(mCollageListLManager);
        mCollageListRecycleView.setAdapter(mCollageRecycleAdapter);


        LinearLayoutManager mCollageFrameLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mCollageFrameRecycleView.setLayoutManager(mCollageFrameLManager);
        mCollageFrameRecycleView.setAdapter(mFrameRecycleAdapter);

        mCollageEditToolsRecycleView = (RecyclerView) findViewById(R.id.idCollageEditToolsRecycleView);
        //mCollageEditToolsRecycleView.setVisibility(View.VISIBLE);
        LinearLayoutManager mCollageEditToolsLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mCollageEditToolsRecycleView.setLayoutManager(mCollageEditToolsLManager);
        mCollageEditToolsRecycleView.setAdapter(mCollageEditToolsAdapter);

        //collageFilterView = (RecyclerView) findViewById(R.id.collageFilterView);
        //collageFilterView.setVisibility(View.GONE);

        toolsRecyclerView = (RecyclerView) findViewById(R.id.toolsRecyclerView);
        toolsRecyclerView.setVisibility(View.GONE);
        LinearLayoutManager mtoolsRecyclerAdapterLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        toolsRecyclerView.setLayoutManager(mtoolsRecyclerAdapterLManager);
        toolsRecyclerView.setAdapter(toolsRecyclerAdapter);



    }

    private void initView2() {
        frameShape2 = (FrameLayout) findViewById(R.id.frameShape2);
        frameShape2.setOnClickListener(this);
        mCollageFrameShape2 = (ImageView) findViewById(R.id.idCollageFrameShape2);
        mCollageFrameShape2.setOnClickListener(this);
    }
    private void initView3() {
        frameShape3 = (FrameLayout) findViewById(R.id.frameShape3);
        frameShape3.setOnClickListener(this);
        mCollageFrameShape3 = (ImageView) findViewById(R.id.idCollageFrameShape3);
        mCollageFrameShape3.setOnClickListener(this);
    }


    private void initView4() {

        frameShape4 = (FrameLayout) findViewById(R.id.frameShape4);
        frameShape4.setOnClickListener(this);
        mCollageFrameShape4 = (ImageView) findViewById(R.id.idCollageFrameShape4);
        mCollageFrameShape4.setOnClickListener(this);
    }


    private void initView5() {
        frameShape5 = (FrameLayout) findViewById(R.id.frameShape5);
        frameShape5.setOnClickListener(this);
        mCollageFrameShape5 = (ImageView) findViewById(R.id.idCollageFrameShape5);
        mCollageFrameShape5.setOnClickListener(this);
    }
    private void initView6() {
        frameShape6 = (FrameLayout) findViewById(R.id.frameShape6);
        frameShape6.setOnClickListener(this);
        mCollageFrameShape6 = (ImageView) findViewById(R.id.idCollageFrameShape6);
        mCollageFrameShape6.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.idCollageSaveLayout:
                captureImage();
                break;

            case R.id.frameShape1:
                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                mCollageListRecycleView.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                imageEffectsThumbnails.setVisibility(View.VISIBLE);
                bindDataToAdapter();
                break;
            case R.id.frameShape2:
                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                mCollageListRecycleView.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                imageEffectsThumbnails.setVisibility(View.VISIBLE);
                bindDataToAdapter();
                break;
            case R.id.frameShape3:
                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                mCollageListRecycleView.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                imageEffectsThumbnails.setVisibility(View.VISIBLE);
                bindDataToAdapter();
                break;

            case R.id.frameShape4:
                bindDataToAdapter();
                break;
            case R.id.frameShape5:
                bindDataToAdapter();
                break;
            case R.id.frameShape6:
                bindDataToAdapter();
                break;

            case R.id.idCollageFrameShape1:
                openGallery(PICK_GALLARY_REQUEST_ONE);
                break;

            case R.id.idCollageFrameShape2:
                openGallery(PICK_GALLARY_REQUEST_TWO);
                break;

            case R.id.idCollageFrameShape3:
                openGallery(PICK_GALLARY_REQUEST_THREE);
                break;

            case R.id.idCollageFrameShape4:
                openGallery(PICK_GALLARY_REQUEST_FOUR);
                break;
            case R.id.idCollageFrameShape5:
                openGallery(PICK_GALLARY_REQUEST_FIVE);
                break;
            case R.id.idCollageFrameShape6:
                openGallery(PICK_GALLARY_REQUEST_SIX);
                break;
            case R.id.idCollageEditBackLLayout:
                show_alert_back("Exit", "Are you sure you want to exit Editor ?");
                break;
        }
    }




    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case CROP_IMAGE:
                if (resultCode == RESULT_OK && null != data) {
                    Bitmap mBitmap = null;

                    Uri selectedImageUri = data.getData();

                   /* try {
                        mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    if(selectedImageUri==null) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            mBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                        } else {
                            mBitmap = (Bitmap) data.getExtras().get("data");
                        }
                    }
                    else if(selectedImageUri !=null && Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
                    {
                        mBitmap = (Bitmap) data.getExtras().get("data");
                    }
                    else {

                        try {
                            mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Matrix mat = new Matrix();
                    if (mBitmap != null) {
                        Bitmap mCropedBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
                                mBitmap.getWidth(), mBitmap.getHeight(), mat, true);

                        if (isSelectedImage1) {
                            mCollageFrameShape1.setImageBitmap(mCropedBitmap);
                            //mCollageFrameShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                        }
                        else if (isSelectedImage2) {
                            mCollageFrameShape2.setImageBitmap(mCropedBitmap);
                            // mCollageFrameShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                        } else if (isSelectedImage3) {
                            mCollageFrameShape3.setImageBitmap(mCropedBitmap);
                            // mCollageFrameShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                        }
                        else if (isSelectedImage4) {
                            mCollageFrameShape4.setImageBitmap(mCropedBitmap);
                            //mCollageFrameShape4.setBackgroundColor(Color.parseColor("#ffffff"));
                        }
                        else if (isSelectedImage5) {
                            mCollageFrameShape5.setImageBitmap(mCropedBitmap);
                            //mCollageFrameShape5.setBackgroundColor(Color.parseColor("#ffffff"));
                        }
                        else if (isSelectedImage6) {
                            mCollageFrameShape6.setImageBitmap(mCropedBitmap);
                            // mCollageFrameShape6.setBackgroundColor(Color.parseColor("#ffffff"));
                        }
                        //mCollageFrameShape1.setOnTouchListener(this);
                    }
                }
                break;

            case PICK_GALLARY_REQUEST_ONE:
                // mCollageFrameShape1.setScaleType(ImageView.ScaleType.FIT_XY);
                selectedImageView = mCollageFrameShape1;
                mCollageFrameShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                if (resultCode == RESULT_OK && null != data) {
                    mBitmap1 = null;

                    Uri selectedImageUri = data.getData();
                    try {
                        mBitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //mBitmap1 = BitmapFactory.decodeFile(picturePath);

                    Matrix mat = new Matrix();
                    if (mBitmap1 != null) {
                        mBitmapRotate1 = Bitmap.createBitmap(mBitmap1, 0, 0,
                                mBitmap1.getWidth(), mBitmap1.getHeight(), mat, true);
                        if( mBitmapRotate1 != null) {
                            //Bitmap myBitmap = generateCircularBitmap(mBitmapRotate1);
                            //mCollageFrameShape1.setImageBitmap(myBitmap);
                            mCollageFrameShape1.setImageBitmap(mBitmapRotate1);
                           // mCollageFrameShape1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            selectedBitmap1 = ((BitmapDrawable)mCollageFrameShape1.getDrawable()).getBitmap();

                        }

                        isSelectedImage1 = true;
                        imageSelectedOrNot = true;

                        mCollageFrameShape1.setOnTouchListener(this);

                        //bindDataToAdapter();
                    }
                }
                break;


            case PICK_GALLARY_REQUEST_TWO:
                selectedImageView = mCollageFrameShape2;
                //mCollageFrameShape2.setScaleType(ImageView.ScaleType.MATRIX);
                mCollageFrameShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                if (resultCode == RESULT_OK && null != data) {
                    mBitmap2 = null;
                    Uri selectedImageUri = data.getData();

                    try {
                        mBitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Matrix mat = new Matrix();

                    mBitmapRotate2 = Bitmap.createBitmap(mBitmap2, 0, 0,
                            mBitmap2.getWidth(), mBitmap2.getHeight(), mat, true);
                    if( mBitmapRotate2 != null) {
                        mCollageFrameShape2.setImageBitmap(mBitmapRotate2);
                        //mCollageFrameShape2.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        selectedBitmap2 = ((BitmapDrawable)mCollageFrameShape2.getDrawable()).getBitmap();
                    }
                    isSelectedImage2 = true;
                    imageSelectedOrNot = true;
                    mCollageFrameShape2.setOnTouchListener(this);
                    //bindDataToAdapter();
                }
                break;

            case PICK_GALLARY_REQUEST_THREE:
                selectedImageView = mCollageFrameShape3;
                //mCollageFrameShape3.setScaleType(ImageView.ScaleType.MATRIX);
                mCollageFrameShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                if (resultCode == RESULT_OK && null != data) {
                    mBitmap3 = null;
                    Uri selectedImageUri = data.getData();

                    try {
                        mBitmap3 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Matrix mat = new Matrix();

                    mBitmapRotate3 = Bitmap.createBitmap(mBitmap3, 0, 0,
                            mBitmap3.getWidth(), mBitmap3.getHeight(), mat, true);
                    if( mBitmapRotate3 != null) {
                        mCollageFrameShape3.setImageBitmap(mBitmapRotate3);
                        //mCollageFrameShape3.setScaleType(ImageView.ScaleType.FIT_XY);
                        selectedBitmap3 = ((BitmapDrawable)mCollageFrameShape3.getDrawable()).getBitmap();
                    }

                    isSelectedImage3 = true;
                    imageSelectedOrNot = true;
                    mCollageFrameShape3.setOnTouchListener(this);

                    //bindDataToAdapter();

                }
                break;



            case PICK_GALLARY_REQUEST_FOUR:
                selectedImageView = mCollageFrameShape4;
                //mCollageFrameShape4.setScaleType(ImageView.ScaleType.FIT_XY);
                mCollageFrameShape4.setBackgroundColor(Color.parseColor("#ffffff"));
                if (resultCode == RESULT_OK && null != data) {
                    mBitmap4 = null;
                    Uri selectedImageUri = data.getData();

                    try {
                        mBitmap4 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Matrix mat = new Matrix();

                    mBitmapRotate4 = Bitmap.createBitmap(mBitmap4, 0, 0,
                            mBitmap4.getWidth(), mBitmap4.getHeight(), mat, true);
                    if( mBitmapRotate4 != null) {
                        mCollageFrameShape4.setImageBitmap(mBitmapRotate4);
                        //mCollageFrameShape4.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        selectedBitmap4 = ((BitmapDrawable)mCollageFrameShape4.getDrawable()).getBitmap();
                    }

                    isSelectedImage4 = true;
                    imageSelectedOrNot = true;
                    mCollageFrameShape4.setOnTouchListener(this);

                    //bindDataToAdapter();

                }
                break;



            case PICK_GALLARY_REQUEST_FIVE:
                selectedImageView = mCollageFrameShape5;
                // mCollageFrameShape5.setScaleType(ImageView.ScaleType.MATRIX);
                mCollageFrameShape5.setBackgroundColor(Color.parseColor("#ffffff"));
                if (resultCode == RESULT_OK && null != data) {
                    mBitmap5 = null;
                    Uri selectedImageUri = data.getData();

                    try {
                        mBitmap5 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Matrix mat = new Matrix();

                    mBitmapRotate5 = Bitmap.createBitmap(mBitmap5, 0, 0,
                            mBitmap5.getWidth(), mBitmap5.getHeight(), mat, true);
                    if( mBitmapRotate5 != null) {
                        mCollageFrameShape5.setImageBitmap(mBitmapRotate5);
                        // mCollageFrameShape5.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        selectedBitmap5 = ((BitmapDrawable)mCollageFrameShape5.getDrawable()).getBitmap();
                    }
                    isSelectedImage5 = true;
                    imageSelectedOrNot = true;
                    mCollageFrameShape5.setOnTouchListener(this);

                    // bindDataToAdapter();

                }
                break;



            case PICK_GALLARY_REQUEST_SIX:
                selectedImageView = mCollageFrameShape6;
                //mCollageFrameShape6.setScaleType(ImageView.ScaleType.MATRIX);
                mCollageFrameShape6.setBackgroundColor(Color.parseColor("#ffffff"));
                if (resultCode == RESULT_OK && null != data) {
                    mBitmap6 = null;
                    Uri selectedImageUri = data.getData();

                    try {
                        mBitmap6 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Matrix mat = new Matrix();
                    mBitmapRotate6 = Bitmap.createBitmap(mBitmap6, 0, 0,
                            mBitmap6.getWidth(), mBitmap6.getHeight(), mat, true);
                    if( mBitmapRotate6 != null) {
                        mCollageFrameShape6.setImageBitmap(mBitmapRotate6);
                        //  mCollageFrameShape6.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        selectedBitmap6 = ((BitmapDrawable)mCollageFrameShape6.getDrawable()).getBitmap();
                    }
                    isSelectedImage6 = true;
                    imageSelectedOrNot = true;
                    mCollageFrameShape6.setOnTouchListener(this);

                    //bindDataToAdapter();
                }
                break;


        }
    }





    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        //SvgImageView svgView = (SvgImageView) v;

        if (view == mCollageFrameShape1) {

            isSelectedImage = true;

            if(isSelectedImage1){ mCollageFrameShape1.setScaleType(ImageView.ScaleType.MATRIX); }

            selectedImageView = mCollageFrameShape1 ;
            selectedBitmap = ((BitmapDrawable)mCollageFrameShape1.getDrawable()).getBitmap();

            if(frameShape1!=null)
                frameShape1.setBackgroundColor(getResources().getColor(R.color.color_shape_selected));
            if(frameShape2!=null)
                frameShape2.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape3!=null)
                frameShape3.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape4!=null)
                frameShape4.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape5!=null)
                frameShape5.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape6!=null)
                frameShape6.setBackgroundColor(getResources().getColor(R.color.white));


//            if(isSelectedEffects) {
//                bindDataToAdapter();
//            }

            isSelectedImage1 = true;
            isSelectedImage2 = false;
            isSelectedImage3 = false;
            isSelectedImage4 = false;
            isSelectedImage5 = false;
            isSelectedImage6 = false;


//            if(bitmap!=null) {
//                Constants.imageBitmap = bitmap;
//                Constants.imageView = selectedImageView;
//                bindDataToAdapter(bitmap, selectedImageView);
//            }

            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    mMatrixSaved1.set(mMatrixShape1);
                    startPointF.set(event.getX(), event.getY());
                    mode = DRAG;
                    break;


                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        mMatrixSaved1.set(mMatrixShape1);
                        midPoint(midPointF, event);
                        mode = ZOOM;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        // ...
                        mMatrixShape1.set(mMatrixSaved1);
                        mMatrixShape1.postTranslate(event.getX() - startPointF.x, event.getY()
                                - startPointF.y);

                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            mMatrixShape1.set(mMatrixSaved1);
                            float scale = newDist / oldDist;
                            mMatrixShape1.postScale(scale, scale, midPointF.x, midPointF.y);
                        }
                    }
                    break;
            }
            view.setImageMatrix(mMatrixShape1);


        } else if (view == mCollageFrameShape2) {
            isSelectedImage=true;

            if(isSelectedImage2){
                mCollageFrameShape2.setScaleType(ImageView.ScaleType.MATRIX);}
            selectedImageView = mCollageFrameShape2 ;
            selectedBitmap = ((BitmapDrawable)mCollageFrameShape2.getDrawable()).getBitmap();


            if(frameShape1!=null)
                frameShape1.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape2!=null)
                frameShape2.setBackgroundColor(getResources().getColor(R.color.color_shape_selected));
            if(frameShape3!=null)
                frameShape3.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape4!=null)
                frameShape4.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape5!=null)
                frameShape5.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape6!=null)
                frameShape6.setBackgroundColor(getResources().getColor(R.color.white));

//            if(isSelectedEffects) {
//                bindDataToAdapter();
//            }

            isSelectedImage1 = false;
            isSelectedImage2 = true;
            isSelectedImage3 = false;
            isSelectedImage4 = false;
            isSelectedImage5 = false;
            isSelectedImage6 = false;

//            Bitmap bitmap = ((BitmapDrawable)selectedImageView.getDrawable()).getBitmap();
//            if(bitmap!=null) {
//                Constants.imageBitmap = bitmap;
//                Constants.imageView = selectedImageView;
//                bindDataToAdapter(bitmap, selectedImageView);
//            }

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mMatrixSaved2.set(mMatrixShape2);

                    startPointF.set(event.getX(), event.getY());
                    Log.d(TAG, "mode=DRAG");
                    mode = DRAG;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    Log.d(TAG, "oldDist=" + oldDist);
                    if (oldDist > 10f) {
                        mMatrixSaved2.set(mMatrixShape2);
                        midPoint(midPointF, event);
                        mode = ZOOM;
                        Log.d(TAG, "mode=ZOOM");
                    }
                    break;

                case MotionEvent.ACTION_UP:


                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    Log.d(TAG, "mode=NONE");
                    break;


                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        // ...
                        mMatrixShape2.set(mMatrixSaved2);
                        mMatrixShape2.postTranslate(event.getX() - startPointF.x, event.getY()
                                - startPointF.y);

                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        Log.d(TAG, "newDist=" + newDist);
                        if (newDist > 10f) {
                            mMatrixShape2.set(mMatrixSaved2);
                            float scale = newDist / oldDist;
                            mMatrixShape2.postScale(scale, scale, midPointF.x, midPointF.y);
                        }
                    }
                    break;
            }

            view.setImageMatrix(mMatrixShape2);

        }


        else if (view == mCollageFrameShape3) {
            isSelectedImage=true;

            if(isSelectedImage3){
                mCollageFrameShape3.setScaleType(ImageView.ScaleType.MATRIX);}
            selectedImageView = mCollageFrameShape3 ;
            selectedBitmap = ((BitmapDrawable)mCollageFrameShape3.getDrawable()).getBitmap();



            if(frameShape1!=null)
                frameShape1.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape2!=null)
                frameShape2.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape3!=null)
                frameShape3.setBackgroundColor(getResources().getColor(R.color.color_shape_selected));
            if(frameShape4!=null)
                frameShape4.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape5!=null)
                frameShape5.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape6!=null)
                frameShape6.setBackgroundColor(getResources().getColor(R.color.white));

            //bindDataToAdapter( );

            isSelectedImage1 = false;
            isSelectedImage2 = false;
            isSelectedImage3 = true;
            isSelectedImage4 = false;
            isSelectedImage5 = false;
            isSelectedImage6 = false;

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mMatrixSaved3.set(mMatrixShape3);
                    startPointF.set(event.getX(), event.getY());
                    mode = DRAG;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        mMatrixSaved3.set(mMatrixShape3);
                        midPoint(midPointF, event);
                        mode = ZOOM;
                    }
                    break;

                case MotionEvent.ACTION_UP:

                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        // ...
                        mMatrixShape3.set(mMatrixSaved3);
                        mMatrixShape3.postTranslate(event.getX() - startPointF.x, event.getY()
                                - startPointF.y);

                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            mMatrixShape3.set(mMatrixSaved3);
                            float scale = newDist / oldDist;
                            mMatrixShape3.postScale(scale, scale, midPointF.x, midPointF.y);
                        }
                    }
                    break;
            }

            view.setImageMatrix(mMatrixShape3);

        }

        else if (view == mCollageFrameShape4) {
            isSelectedImage=true;

            if(isSelectedImage4){
                mCollageFrameShape4.setScaleType(ImageView.ScaleType.MATRIX);}
            selectedImageView = mCollageFrameShape4 ;
            selectedBitmap = ((BitmapDrawable)mCollageFrameShape4.getDrawable()).getBitmap();

            if(frameShape1!=null)
                frameShape1.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape2!=null)
                frameShape2.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape3!=null)
                frameShape3.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape4!=null)
                frameShape4.setBackgroundColor(getResources().getColor(R.color.color_shape_selected));
            if(frameShape5!=null)
                frameShape5.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape6!=null)
                frameShape6.setBackgroundColor(getResources().getColor(R.color.white));

            //bindDataToAdapter( );

            isSelectedImage1 = false;
            isSelectedImage2 = false;
            isSelectedImage3 = false;
            isSelectedImage4 = true;
            isSelectedImage5 = false;
            isSelectedImage6 = false;

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mMatrixSaved4.set(mMatrixShape4);
                    startPointF.set(event.getX(), event.getY());
                    mode = DRAG;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        mMatrixSaved4.set(mMatrixShape4);
                        midPoint(midPointF, event);
                        mode = ZOOM;
                    }
                    break;

                case MotionEvent.ACTION_UP:

                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        mMatrixShape4.set(mMatrixSaved4);
                        mMatrixShape4.postTranslate(event.getX() - startPointF.x, event.getY()
                                - startPointF.y);

                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            mMatrixShape4.set(mMatrixSaved4);
                            float scale = newDist / oldDist;
                            mMatrixShape4.postScale(scale, scale, midPointF.x, midPointF.y);
                        }
                    }
                    break;
            }

            view.setImageMatrix(mMatrixShape4);

        }

        else if (view == mCollageFrameShape5) {
            isSelectedImage=true;
            if(isSelectedImage5){
                mCollageFrameShape5.setScaleType(ImageView.ScaleType.MATRIX);}
            selectedImageView = mCollageFrameShape5 ;
            selectedBitmap = ((BitmapDrawable)mCollageFrameShape5.getDrawable()).getBitmap();

            if(frameShape1!=null)
                frameShape1.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape2!=null)
                frameShape2.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape3!=null)
                frameShape3.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape4!=null)
                frameShape4.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape5!=null)
                frameShape5.setBackgroundColor(getResources().getColor(R.color.color_shape_selected));
            if(frameShape6!=null)
                frameShape6.setBackgroundColor(getResources().getColor(R.color.white));

            //bindDataToAdapter( );

            isSelectedImage1 = false;
            isSelectedImage2 = false;
            isSelectedImage3 = false;
            isSelectedImage4 = false;
            isSelectedImage5 = true;
            isSelectedImage6 = false;

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mMatrixSaved5.set(mMatrixShape5);
                    startPointF.set(event.getX(), event.getY());
                    mode = DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        mMatrixSaved5.set(mMatrixShape5);
                        midPoint(midPointF, event);
                        mode = ZOOM;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        // ...
                        mMatrixShape5.set(mMatrixSaved5);
                        mMatrixShape5.postTranslate(event.getX() - startPointF.x, event.getY()
                                - startPointF.y);

                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            mMatrixShape5.set(mMatrixSaved5);
                            float scale = newDist / oldDist;
                            mMatrixShape5.postScale(scale, scale, midPointF.x, midPointF.y);
                        }
                    }
                    break;
            }

            view.setImageMatrix(mMatrixShape5);

        }

        else if (view == mCollageFrameShape6) {
            isSelectedImage=true;
            if(isSelectedImage6){
                mCollageFrameShape6.setScaleType(ImageView.ScaleType.MATRIX);}
            selectedImageView = mCollageFrameShape6 ;
            selectedBitmap = ((BitmapDrawable)mCollageFrameShape6.getDrawable()).getBitmap();

            if(frameShape1!=null)
                frameShape1.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape2!=null)
                frameShape2.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape3!=null)
                frameShape3.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape4!=null)
                frameShape4.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape5!=null)
                frameShape5.setBackgroundColor(getResources().getColor(R.color.white));
            if(frameShape6!=null)
                frameShape6.setBackgroundColor(getResources().getColor(R.color.color_shape_selected));


            // bindDataToAdapter( );

            isSelectedImage1 = false;
            isSelectedImage2 = false;
            isSelectedImage3 = false;
            isSelectedImage4 = false;
            isSelectedImage5 = false;
            isSelectedImage6 = true;



            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mMatrixSaved6.set(mMatrixShape6);
                    startPointF.set(event.getX(), event.getY());
                    mode = DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        mMatrixSaved6.set(mMatrixShape6);
                        midPoint(midPointF, event);
                        mode = ZOOM;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {

                        mMatrixShape6.set(mMatrixSaved6);
                        mMatrixShape6.postTranslate(event.getX() - startPointF.x, event.getY()
                                - startPointF.y);

                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            mMatrixShape6.set(mMatrixSaved6);
                            float scale = newDist / oldDist;
                            mMatrixShape6.postScale(scale, scale, midPointF.x, midPointF.y);
                        }
                    }
                    break;
            }
            view.setImageMatrix(mMatrixShape6);
        }
        return true;
    }


    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);

    }

    /**
     * Calculate the midPointF point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }





    /********************** EditTolls  /  onCollageEditTollsSelected *******************************/
    @Override
    public void onCollageEditTollsSelected(CollageEditToolsType collageEditToolsType) {
        switch (collageEditToolsType) {

            case COLLAGE:
                isSelectedEffects = false ;

//                imageSelectedOrNot =  imageSelectedOrNot();
//                if(!imageSelectedOrNot){
//                    sb_brightness.setVisibility(View.GONE);
//                    mCollageFrameRecycleView.setVisibility(View.GONE);
//                    mCollageListRecycleView.setVisibility(View.GONE);
//                    imageEffectsThumbnails.setVisibility(View.GONE);
//                    frameRecyclerLayout.setVisibility(View.GONE);
//                    toolsRecyclerView.setVisibility(View.GONE);
//
//                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);

                frameRecyclerLayout.setVisibility(View.VISIBLE);
                mCollageListRecycleView.setVisibility(View.VISIBLE);

                break;


            case TOOLS:
                isSelectedEffects = false ;

                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                    //sb_brightness.setVisibility(View.GONE);
                    mCollageFrameRecycleView.setVisibility(View.GONE);
                    mCollageListRecycleView.setVisibility(View.GONE);
                    imageEffectsThumbnails.setVisibility(View.GONE);
                    frameRecyclerLayout.setVisibility(View.GONE);
                    toolsRecyclerView.setVisibility(View.GONE);

                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mCollageListRecycleView.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.VISIBLE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);

                break;

            case EFFECTS:
                isSelectedEffects = true ;

                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                    //sb_brightness.setVisibility(View.GONE);
                    mCollageFrameRecycleView.setVisibility(View.GONE);
                    mCollageListRecycleView.setVisibility(View.GONE);
                    toolsRecyclerView.setVisibility(View.GONE);
                    imageEffectsThumbnails.setVisibility(View.GONE);
                    frameRecyclerLayout.setVisibility(View.GONE);
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }


                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                mCollageListRecycleView.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                imageEffectsThumbnails.setVisibility(View.VISIBLE);
//                LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//                collageFilterView.setLayoutManager(llmFilters);
//                collageFilterView.setAdapter(mFilterViewAdapter);

                //  showFilter(true);


                imageSelectedOrNot =  imageSelectedOrNot();

//                if(!imageSelectedOrNot){
//                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if (isSelectedImage1) {
                    Bitmap bitmap = ((BitmapDrawable)mCollageFrameShape1.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        //cropImageUri(getImageUri(mContext, bitmap));
                        Constants.imageBitmap = bitmap;
                        Constants.imageView = mCollageFrameShape1;
                        //bindDataToAdapter(bitmap , mCollageFrameShape1);
                        bindDataToAdapter( );
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage2) {
                    Bitmap bitmap = ((BitmapDrawable) mCollageFrameShape2.getDrawable()).getBitmap();
                    if (bitmap != null)
                    {
                        Constants.imageBitmap = bitmap;
                        Constants.imageView = mCollageFrameShape2;
                        //cropImageUri(getImageUri(mContext, bitmap));
                        //bindDataToAdapter(bitmap , mCollageFrameShape2);
                        bindDataToAdapter( );
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage3) {
                    Bitmap bitmap = ((BitmapDrawable)mCollageFrameShape3.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        Constants.imageBitmap = bitmap;
                        Constants.imageView = mCollageFrameShape3;
                        //cropImageUri(getImageUri(mContext, bitmap));
                        //bindDataToAdapter(bitmap , mCollageFrameShape3);
                        bindDataToAdapter( );
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage4) {
                    Bitmap bitmap = ((BitmapDrawable) mCollageFrameShape4.getDrawable()).getBitmap();
                    if (bitmap != null){
                        Constants.imageBitmap = bitmap;
                        Constants.imageView = mCollageFrameShape4;
                        //cropImageUri(getImageUri(mContext, bitmap));
                        // bindDataToAdapter(bitmap , mCollageFrameShape4);
                        bindDataToAdapter( );
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }

                else if (isSelectedImage5) {
                    Bitmap bitmap = ((BitmapDrawable) mCollageFrameShape5.getDrawable()).getBitmap();
                    if (bitmap != null){
                        Constants.imageBitmap = bitmap;
                        Constants.imageView = mCollageFrameShape5;
                        //cropImageUri(getImageUri(mContext, bitmap));
                        //bindDataToAdapter(bitmap , mCollageFrameShape5);
                        bindDataToAdapter( );
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage6) {
                    Bitmap bitmap = ((BitmapDrawable)mCollageFrameShape6.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        Constants.imageBitmap = bitmap;
                        Constants.imageView = mCollageFrameShape6;
                        // cropImageUri(getImageUri(mContext, bitmap));
                        //bindDataToAdapter(bitmap , mCollageFrameShape6);
                        bindDataToAdapter( );
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case FRAME:
                isSelectedEffects = false ;

                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                    //sb_brightness.setVisibility(View.GONE);
                    mCollageFrameRecycleView.setVisibility(View.GONE);
                    mCollageListRecycleView.setVisibility(View.GONE);
                    toolsRecyclerView.setVisibility(View.GONE);
                    imageEffectsThumbnails.setVisibility(View.GONE);
                    frameRecyclerLayout.setVisibility(View.GONE);
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // collageFilterView.setVisibility(View.GONE);
                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.GONE);
                mCollageListRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);

                mCollageFrameRecycleView.setVisibility(View.VISIBLE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);

                break;

            case CROP:
                isSelectedEffects = false ;
                toolsRecyclerView.setVisibility(View.GONE);
                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageListRecycleView.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);

                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isSelectedImage1) {
                    Bitmap bitmap = ((BitmapDrawable)mCollageFrameShape1.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage2) {
                    Bitmap bitmap = ((BitmapDrawable) mCollageFrameShape2.getDrawable()).getBitmap();
                    if (bitmap != null)
                    {
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage3) {
                    Bitmap bitmap = ((BitmapDrawable)mCollageFrameShape3.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage4) {
                    Bitmap bitmap = ((BitmapDrawable) mCollageFrameShape4.getDrawable()).getBitmap();
                    if (bitmap != null){
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }

                else if (isSelectedImage5) {
                    Bitmap bitmap = ((BitmapDrawable) mCollageFrameShape5.getDrawable()).getBitmap();
                    if (bitmap != null){
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage6) {
                    Bitmap bitmap = ((BitmapDrawable)mCollageFrameShape6.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case ADJUST:
                isSelectedEffects = false ;

                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    toolsRecyclerView.setVisibility(View.GONE);
                    mCollageListRecycleView.setVisibility(View.GONE);
                    mCollageFrameRecycleView.setVisibility(View.GONE);
                    imageEffectsThumbnails.setVisibility(View.GONE);
                    collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                    //sb_brightness.setVisibility(View.GONE);
                    frameRecyclerLayout.setVisibility(View.GONE);
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                toolsRecyclerView.setVisibility(View.GONE);
                mCollageListRecycleView.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.VISIBLE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                collageEffectsSeekbarLLayout.setVisibility(View.VISIBLE);

                break;



            case FLIP:

                isSelectedEffects = false ;

                toolsRecyclerView.setVisibility(View.GONE);
                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageListRecycleView.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);

                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isSelectedImage1) {
                    mMatrixShape1.postScale(-1.0f, 1.0f, mCollageFrameShape1.getWidth() / 2f,
                            mCollageFrameShape1.getHeight() / 2f);
                    mCollageFrameShape1.setImageMatrix(mMatrixShape1);

                }
                else if (isSelectedImage2) {
                    mMatrixShape2.postScale(-1.0f, 1.0f, mCollageFrameShape2.getWidth() / 2f,
                            mCollageFrameShape2.getHeight() / 2f);

                    mCollageFrameShape2.setImageMatrix(mMatrixShape2);

                } else if (isSelectedImage3) {
                    mMatrixShape3.postScale(-1.0f, 1.0f, mCollageFrameShape3.getWidth() / 2f,
                            mCollageFrameShape3.getHeight() / 2f);
                    mCollageFrameShape3.setImageMatrix(mMatrixShape3);
                }
                else if (isSelectedImage4) {
                    mMatrixShape4.postScale(-1.0f, 1.0f, mCollageFrameShape4.getWidth() / 2f,
                            mCollageFrameShape4.getHeight() / 2f);
                    mCollageFrameShape4.setImageMatrix(mMatrixShape4);
                }
                else if (isSelectedImage5) {
                    mMatrixShape5.postScale(-1.0f, 1.0f, mCollageFrameShape5.getWidth() / 2f,
                            mCollageFrameShape5.getHeight() / 2f);
                    mCollageFrameShape5.setImageMatrix(mMatrixShape5);
                }
                else if (isSelectedImage6) {
                    mMatrixShape6.postScale(-1.0f, 1.0f, mCollageFrameShape6.getWidth() / 2f,
                            mCollageFrameShape6.getHeight() / 2f);
                    mCollageFrameShape6.setImageMatrix(mMatrixShape6);
                }else if (!imageSelectedOrNot) {
                    Toast.makeText(this , "Sorry, Image not selected.", Toast.LENGTH_SHORT).show();

                }


                break;





            case SAVE:
                // isSelectedEffects = true ;

                toolsRecyclerView.setVisibility(View.GONE);
                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageListRecycleView.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.GONE);

                //String sCollageFileName = captureImage();

                captureImage();
                break;





            case RT_LEFT:

                toolsRecyclerView.setVisibility(View.GONE);
                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageListRecycleView.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);

                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isSelectedImage1) {

                    mMatrixShape1.postRotate(-12, mCollageFrameShape1.getMeasuredWidth() / 2,
                            mCollageFrameShape1.getMeasuredHeight() / 2);
                    mCollageFrameShape1.setImageMatrix(mMatrixShape1);

                } else if (isSelectedImage2) {
                    mMatrixShape2.postRotate(-12, mCollageFrameShape2.getMeasuredWidth() / 2,
                            mCollageFrameShape2.getMeasuredHeight() / 2);
                    mCollageFrameShape2.setImageMatrix(mMatrixShape2);
                } else if (isSelectedImage3) {
                    mMatrixShape3.postRotate(-12, mCollageFrameShape3.getMeasuredWidth() / 2,
                            mCollageFrameShape3.getMeasuredHeight() / 2);
                    mCollageFrameShape3.setImageMatrix(mMatrixShape3);
                }

                else if (isSelectedImage4) {
                    mMatrixShape4.postRotate(-12, mCollageFrameShape4.getMeasuredWidth() / 2,
                            mCollageFrameShape4.getMeasuredHeight() / 2);
                    mCollageFrameShape4.setImageMatrix(mMatrixShape4);
                }

                else if (isSelectedImage5) {
                    mMatrixShape5.postRotate(-12, mCollageFrameShape5.getMeasuredWidth() / 2,
                            mCollageFrameShape5.getMeasuredHeight() / 2);
                    mCollageFrameShape5.setImageMatrix(mMatrixShape5);
                }
                else if (isSelectedImage6) {
                    mMatrixShape6.postRotate(-12, mCollageFrameShape6.getMeasuredWidth() / 2,
                            mCollageFrameShape6.getMeasuredHeight() / 2);
                    mCollageFrameShape6.setImageMatrix(mMatrixShape6);
                }
                break;

            case RT_RIGHT:

                toolsRecyclerView.setVisibility(View.GONE);
                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageListRecycleView.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);


                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isSelectedImage1) {
                    mMatrixShape1.postRotate(12, mCollageFrameShape1.getMeasuredWidth() / 2,
                            mCollageFrameShape1.getMeasuredHeight() / 2);
                    mCollageFrameShape1.setImageMatrix(mMatrixShape1);
                } else if (isSelectedImage2) {
                    mMatrixShape2.postRotate(12, mCollageFrameShape2.getMeasuredWidth() / 2,
                            mCollageFrameShape2.getMeasuredHeight() / 2);
                    mCollageFrameShape2.setImageMatrix(mMatrixShape2);
                } else if (isSelectedImage3) {
                    mMatrixShape3.postRotate(12, mCollageFrameShape3.getMeasuredWidth() / 2,
                            mCollageFrameShape3.getMeasuredHeight() / 2);
                    mCollageFrameShape3.setImageMatrix(mMatrixShape3);
                }
                else if (isSelectedImage4) {
                    mMatrixShape4.postRotate(12, mCollageFrameShape4.getMeasuredWidth() / 2,
                            mCollageFrameShape4.getMeasuredHeight() / 2);
                    mCollageFrameShape4.setImageMatrix(mMatrixShape4);
                }
                else if (isSelectedImage5) {
                    mMatrixShape5.postRotate(12, mCollageFrameShape5.getMeasuredWidth() / 2,
                            mCollageFrameShape5.getMeasuredHeight() / 2);
                    mCollageFrameShape5.setImageMatrix(mMatrixShape5);
                }
                else if (isSelectedImage6) {
                    mMatrixShape6.postRotate(12, mCollageFrameShape6.getMeasuredWidth() / 2,
                            mCollageFrameShape6.getMeasuredHeight() / 2);
                    mCollageFrameShape6.setImageMatrix(mMatrixShape6);
                }
                break;


            case GALLERY:

                toolsRecyclerView.setVisibility(View.GONE);
                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                // sb_brightness.setVisibility(View.GONE);
                mCollageListRecycleView.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);


                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isSelectedImage1) {
                    openGallery(PICK_GALLARY_REQUEST_ONE);

                } else if (isSelectedImage2) {
                    openGallery(PICK_GALLARY_REQUEST_TWO);

                } else if (isSelectedImage3) {
                    openGallery(PICK_GALLARY_REQUEST_THREE);

                }
                else if (isSelectedImage4) {
                    openGallery(PICK_GALLARY_REQUEST_FOUR);
                }
                else if (isSelectedImage5) {
                    openGallery(PICK_GALLARY_REQUEST_FIVE);
                }
                else if (isSelectedImage6) {
                    openGallery(PICK_GALLARY_REQUEST_SIX);
                }
                break;

            case EDIT:

                if(frameShape1!=null)
                    frameShape1.setBackgroundColor(getResources().getColor(R.color.white));
                if(frameShape2!=null)
                    frameShape2.setBackgroundColor(getResources().getColor(R.color.white));
                if(frameShape3!=null)
                    frameShape3.setBackgroundColor(getResources().getColor(R.color.white));
                if(frameShape4!=null)
                    frameShape4.setBackgroundColor(getResources().getColor(R.color.white));
                if(frameShape5!=null)
                    frameShape5.setBackgroundColor(getResources().getColor(R.color.white));
                if(frameShape6!=null)
                    frameShape6.setBackgroundColor(getResources().getColor(R.color.white));


                // captureImage();

                editImage();

                mMyPrecfence.saveString(Constants.INTENT_TYPE, "CollageSelectedImage");
                try {
                    Uri uriCollageSelectedImage = Uri.fromFile(mImageFile);
                    Intent intentCollageSelectedImage = new Intent(CollageSelectedActivity.this, EditImageActivity.class);
                    intentCollageSelectedImage.putExtra(Constants.URI_COLLAGE_SELECTED_IMAGE, uriCollageSelectedImage);

                    startActivity(intentCollageSelectedImage);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }




    /*************************Frame Selection***********************************************/
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCollageFrameSelected(int collageFrameID, CollageFrameType collageFrameType) {

        //mMainCollageFrameImage = (ImageView) findViewById(R.id.idMainCollageFrameImage);
        // mMainCollageFrameImage.setBackgroundResource(collageFrameID);
        //mCollageEditToolsCloseLLayout.setVisibility(View.VISIBLE);


        mCollageListRecycleView.setVisibility(View.VISIBLE);
        mCollageEditToolsRecycleView.setVisibility(View.VISIBLE);

        //frameRecyclerLayout.setVisibility(View.VISIBLE);

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
                    Toast.makeText(this , "You have already selected." , Toast.LENGTH_SHORT ).show();
                    return;
                }

                setContentView(R.layout.activity_collage_selected_image);

                initView();

                Matrix mat = new Matrix();
                if (mBitmap1 != null && mBitmapRotate1!=null) {
                    mBitmapRotate1 = Bitmap.createBitmap(mBitmap1, 0, 0,
                            mBitmap1.getWidth(), mBitmap1.getHeight(), mat, true);
                    mCollageFrameShape1.setImageBitmap(mBitmapRotate1);
                    mCollageFrameShape1.setOnTouchListener(this);
                    if(isSelectedImage1) {
                        mCollageFrameShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                    }
                }
                if (mBitmap2 != null && mBitmapRotate2!=null) {
                    mBitmapRotate2 = Bitmap.createBitmap(mBitmap2, 0, 0,
                            mBitmap2.getWidth(), mBitmap2.getHeight(), mat, true);
                    mCollageFrameShape2.setImageBitmap(mBitmapRotate2);
                    mCollageFrameShape2.setOnTouchListener(this);
                    if(isSelectedImage2) {
                        mCollageFrameShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                    }
                }

                isSelectedFrame1_0 = true;


                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                mCollageListRecycleView.setVisibility(View.VISIBLE);

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
                    Toast.makeText(this , "You have already selected." , Toast.LENGTH_SHORT ).show();
                    return;
                }

                setContentView(R.layout.activity_collage_2_1);

                initView();
                initView2();


                //mMainCollageFrameImage.setBackgroundResource(collageFrameID);

                Matrix mat21 = new Matrix();
                if (mBitmap1 != null && mBitmapRotate1!=null) {
                    mBitmapRotate1 = Bitmap.createBitmap(mBitmap1, 0, 0,
                            mBitmap1.getWidth(), mBitmap1.getHeight(), mat21, true);
                    mCollageFrameShape1.setImageBitmap(mBitmapRotate1);
                    mCollageFrameShape1.setOnTouchListener(this);

                    mCollageFrameShape1.setBackgroundColor(Color.parseColor("#ffffff"));


                }
                if (mBitmap2 != null && mBitmapRotate2!=null) {
                    mBitmapRotate2 = Bitmap.createBitmap(mBitmap2, 0, 0,
                            mBitmap2.getWidth(), mBitmap2.getHeight(), mat21, true);
                    mCollageFrameShape2.setImageBitmap(mBitmapRotate2);
                    mCollageFrameShape2.setOnTouchListener(this);
                    mCollageFrameShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

//                if(mBitmapRotate1!=null){
//                    mCollageFrameShape1.setImageBitmap(mBitmapRotate1);
//                }

//                if(mBitmapRotate2!=null){
//                    mCollageFrameShape2.setImageBitmap(mBitmapRotate2);
//                }

                isSelectedFrame2_1 = true;

                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                mCollageListRecycleView.setVisibility(View.VISIBLE);

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
                    Toast.makeText(this , "You have already selected." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_2_2);
                initView();
                initView2();


                // mMainCollageFrameImage.setBackgroundResource(collageFrameID);

                Matrix mat22 = new Matrix();
                if (mBitmap1 != null && mBitmapRotate1!=null) {
                    mBitmapRotate1 = Bitmap.createBitmap(mBitmap1, 0, 0,
                            mBitmap1.getWidth(), mBitmap1.getHeight(), mat22, true);
                    mCollageFrameShape1.setImageBitmap(mBitmapRotate1);
                    mCollageFrameShape1.setOnTouchListener(this);
                    mCollageFrameShape1.setBackgroundColor(Color.parseColor("#ffffff"));

                }
                if (mBitmap2 != null && mBitmapRotate2!=null) {
                    mBitmapRotate2 = Bitmap.createBitmap(mBitmap2, 0, 0,
                            mBitmap2.getWidth(), mBitmap2.getHeight(), mat22, true);
                    mCollageFrameShape2.setImageBitmap(mBitmapRotate2);
                    mCollageFrameShape2.setOnTouchListener(this);
                    mCollageFrameShape2.setBackgroundColor(Color.parseColor("#ffffff"));

                }

//                if(mBitmapRotate1!=null){
//                    mCollageFrameShape1.setImageBitmap(mBitmapRotate1);
//                }
//                if(mBitmapRotate2!=null){
//                    mCollageFrameShape2.setImageBitmap(mBitmapRotate2);
//                }

                isSelectedFrame2_2 = true;

                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                mCollageListRecycleView.setVisibility(View.VISIBLE);

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
                    Toast.makeText(this , "You have already selected." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_3_1);
                initView();
                initView2();
                initView3();

                //mMainCollageFrameImage.setBackgroundResource(collageFrameID);

                Matrix mat31 = new Matrix();
                if (mBitmap1 != null && mBitmapRotate1!=null) {
                    mBitmapRotate1 = Bitmap.createBitmap(mBitmap1, 0, 0,
                            mBitmap1.getWidth(), mBitmap1.getHeight(), mat31, true);
                    mCollageFrameShape1.setImageBitmap(mBitmapRotate1);
                    mCollageFrameShape1.setOnTouchListener(this);
                    mCollageFrameShape1.setBackgroundColor(Color.parseColor("#ffffff"));

                }

                if (mBitmap2 != null && mBitmapRotate2!=null) {
                    mBitmapRotate2 = Bitmap.createBitmap(mBitmap2, 0, 0,
                            mBitmap2.getWidth(), mBitmap2.getHeight(), mat31, true);
                    mCollageFrameShape2.setImageBitmap(mBitmapRotate2);
                    mCollageFrameShape2.setOnTouchListener(this);
                    mCollageFrameShape2.setBackgroundColor(Color.parseColor("#ffffff"));

                }

                if (mBitmap3 != null && mBitmapRotate3!=null) {
                    mBitmapRotate3 = Bitmap.createBitmap(mBitmap3, 0, 0,
                            mBitmap3.getWidth(), mBitmap3.getHeight(), mat31, true);
                    mCollageFrameShape3.setImageBitmap(mBitmapRotate3);
                    mCollageFrameShape3.setOnTouchListener(this);
                    mCollageFrameShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                }


                isSelectedFrame3_1 = true;

                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                mCollageListRecycleView.setVisibility(View.VISIBLE);

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
                    Toast.makeText(this , "You have already selected." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_3_2);
                initView();
                initView2();
                initView3();


                //mMainCollageFrameImage.setBackgroundResource(collageFrameID);


                Matrix mat32 = new Matrix();
                if (mBitmap1 != null && mBitmapRotate1!=null) {
                    mBitmapRotate1 = Bitmap.createBitmap(mBitmap1, 0, 0,
                            mBitmap1.getWidth(), mBitmap1.getHeight(), mat32, true);
                    mCollageFrameShape1.setImageBitmap(mBitmapRotate1);
                    mCollageFrameShape1.setOnTouchListener(this);
                    mCollageFrameShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (mBitmap2 != null && mBitmapRotate2!=null) {
                    mBitmapRotate2 = Bitmap.createBitmap(mBitmap2, 0, 0,
                            mBitmap2.getWidth(), mBitmap2.getHeight(), mat32, true);
                    mCollageFrameShape2.setImageBitmap(mBitmapRotate2);
                    mCollageFrameShape2.setOnTouchListener(this);
                    mCollageFrameShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (mBitmap3 != null && mBitmapRotate3!=null) {
                    mBitmapRotate3 = Bitmap.createBitmap(mBitmap3, 0, 0,
                            mBitmap3.getWidth(), mBitmap3.getHeight(), mat32, true);
                    mCollageFrameShape3.setImageBitmap(mBitmapRotate3);
                    mCollageFrameShape3.setOnTouchListener(this);
                    mCollageFrameShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                }


                isSelectedFrame3_2 = true;

                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                mCollageListRecycleView.setVisibility(View.VISIBLE);

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
                    Toast.makeText(this , "You have already selected." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_3_3);
                initView();
                initView2();
                initView3();


                //mMainCollageFrameImage.setBackgroundResource(collageFrameID);

                Matrix mat33 = new Matrix();
                if (mBitmap1 != null && mBitmapRotate1!=null) {
                    mBitmapRotate1 = Bitmap.createBitmap(mBitmap1, 0, 0,
                            mBitmap1.getWidth(), mBitmap1.getHeight(), mat33, true);
                    mCollageFrameShape1.setImageBitmap(mBitmapRotate1);
                    mCollageFrameShape1.setOnTouchListener(this);
                    mCollageFrameShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (mBitmap2 != null && mBitmapRotate2!=null) {
                    mBitmapRotate2 = Bitmap.createBitmap(mBitmap2, 0, 0,
                            mBitmap2.getWidth(), mBitmap2.getHeight(), mat33, true);
                    mCollageFrameShape2.setImageBitmap(mBitmapRotate2);
                    mCollageFrameShape2.setOnTouchListener(this);
                    mCollageFrameShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (mBitmap3 != null && mBitmapRotate3!=null) {
                    mBitmapRotate3 = Bitmap.createBitmap(mBitmap3, 0, 0,
                            mBitmap3.getWidth(), mBitmap3.getHeight(), mat33, true);
                    mCollageFrameShape3.setImageBitmap(mBitmapRotate3);
                    mCollageFrameShape3.setOnTouchListener(this);
                    mCollageFrameShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                isSelectedFrame3_3 = true;

                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                mCollageListRecycleView.setVisibility(View.VISIBLE);

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
                    Toast.makeText(this , "You have already selected." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_4_1);
                initView();
                initView2();
                initView3();
                initView4();


                //mMainCollageFrameImage.setBackgroundResource(collageFrameID);

                Matrix mat41 = new Matrix();
                if (mBitmap1 != null && mBitmapRotate1!=null) {
                    mBitmapRotate1 = Bitmap.createBitmap(mBitmap1, 0, 0,
                            mBitmap1.getWidth(), mBitmap1.getHeight(), mat41, true);
                    mCollageFrameShape1.setImageBitmap(mBitmapRotate1);
                    mCollageFrameShape1.setOnTouchListener(this);
                    mCollageFrameShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (mBitmap2 != null && mBitmapRotate2!=null) {
                    mBitmapRotate2 = Bitmap.createBitmap(mBitmap2, 0, 0,
                            mBitmap2.getWidth(), mBitmap2.getHeight(), mat41, true);
                    mCollageFrameShape2.setImageBitmap(mBitmapRotate2);
                    mCollageFrameShape2.setOnTouchListener(this);
                    mCollageFrameShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (mBitmap3 != null && mBitmapRotate3!=null) {
                    mBitmapRotate3 = Bitmap.createBitmap(mBitmap3, 0, 0,
                            mBitmap3.getWidth(), mBitmap3.getHeight(), mat41, true);
                    mCollageFrameShape3.setImageBitmap(mBitmapRotate3);
                    mCollageFrameShape3.setOnTouchListener(this);
                    mCollageFrameShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                if (mBitmap4 != null && mBitmapRotate4!=null) {
                    mBitmapRotate3 = Bitmap.createBitmap(mBitmap4, 0, 0,
                            mBitmap4.getWidth(), mBitmap4.getHeight(), mat41, true);
                    mCollageFrameShape4.setImageBitmap(mBitmapRotate4);
                    mCollageFrameShape4.setOnTouchListener(this);
                    mCollageFrameShape4.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                isSelectedFrame4_1 = true;

                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                // sb_brightness.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                mCollageListRecycleView.setVisibility(View.VISIBLE);

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
                    Toast.makeText(this , "You have already selected." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_4_2);
                initView();
                initView2();
                initView3();
                initView4();


                //mMainCollageFrameImage.setBackgroundResource(collageFrameID);

                Matrix mat42 = new Matrix();
                if (mBitmap1 != null && mBitmapRotate1!=null) {
                    mBitmapRotate1 = Bitmap.createBitmap(mBitmap1, 0, 0,
                            mBitmap1.getWidth(), mBitmap1.getHeight(), mat42, true);
                    mCollageFrameShape1.setImageBitmap(mBitmapRotate1);
                    mCollageFrameShape1.setOnTouchListener(this);
                    mCollageFrameShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (mBitmap2 != null && mBitmapRotate2!=null) {
                    mBitmapRotate2 = Bitmap.createBitmap(mBitmap2, 0, 0,
                            mBitmap2.getWidth(), mBitmap2.getHeight(), mat42, true);
                    mCollageFrameShape2.setImageBitmap(mBitmapRotate2);
                    mCollageFrameShape2.setOnTouchListener(this);
                    mCollageFrameShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (mBitmap3 != null && mBitmapRotate3!=null) {
                    mBitmapRotate3 = Bitmap.createBitmap(mBitmap3, 0, 0,
                            mBitmap3.getWidth(), mBitmap3.getHeight(), mat42, true);
                    mCollageFrameShape3.setImageBitmap(mBitmapRotate3);
                    mCollageFrameShape3.setOnTouchListener(this);
                    if(isSelectedImage3) {
                        mCollageFrameShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                    }
                }
                if (mBitmap4 != null && mBitmapRotate4!=null) {
                    mBitmapRotate3 = Bitmap.createBitmap(mBitmap4, 0, 0,
                            mBitmap4.getWidth(), mBitmap4.getHeight(), mat42, true);
                    mCollageFrameShape4.setImageBitmap(mBitmapRotate4);
                    mCollageFrameShape4.setOnTouchListener(this);
                    mCollageFrameShape4.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                isSelectedFrame4_2 = true;

                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                mCollageListRecycleView.setVisibility(View.VISIBLE);

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
                    Toast.makeText(this , "You have already selected." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_4_3);
                initView();
                initView2();
                initView3();
                initView4();


                // mMainCollageFrameImage.setBackgroundResource(collageFrameID);
                //mCollageFrameShape4.setBackgroundResource(R.drawable.circle_img);

                Matrix mat43 = new Matrix();
                if (mBitmap1 != null && mBitmapRotate1!=null) {
                    mBitmapRotate1 = Bitmap.createBitmap(mBitmap1, 0, 0,
                            mBitmap1.getWidth(), mBitmap1.getHeight(), mat43, true);
                    mCollageFrameShape1.setImageBitmap(mBitmapRotate1);
                    mCollageFrameShape1.setOnTouchListener(this);
                    mCollageFrameShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (mBitmap2 != null && mBitmapRotate2!=null) {
                    mBitmapRotate2 = Bitmap.createBitmap(mBitmap2, 0, 0,
                            mBitmap2.getWidth(), mBitmap2.getHeight(), mat43, true);
                    mCollageFrameShape2.setImageBitmap(mBitmapRotate2);
                    mCollageFrameShape2.setOnTouchListener(this);
                    mCollageFrameShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (mBitmap3 != null && mBitmapRotate3!=null) {
                    mBitmapRotate3 = Bitmap.createBitmap(mBitmap3, 0, 0,
                            mBitmap3.getWidth(), mBitmap3.getHeight(), mat43, true);
                    mCollageFrameShape3.setImageBitmap(mBitmapRotate3);
                    mCollageFrameShape3.setOnTouchListener(this);
                    mCollageFrameShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                if (mBitmap4 != null && mBitmapRotate4!=null) {
                    mBitmapRotate3 = Bitmap.createBitmap(mBitmap4, 0, 0,
                            mBitmap4.getWidth(), mBitmap4.getHeight(), mat43, true);
                    mCollageFrameShape4.setImageBitmap(mBitmapRotate4);
                    mCollageFrameShape4.setOnTouchListener(this);
                    mCollageFrameShape4.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                isSelectedFrame4_3 = true;

                //sb_brightness.setVisibility(View.GONE);
                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                mCollageListRecycleView.setVisibility(View.VISIBLE);


                break;

            case FOUR_FOUR:

                isSelectedFrame1_0 = false;
                isSelectedFrame2_1 = false;
                isSelectedFrame2_2 = false;
                isSelectedFrame3_1 = false;
                isSelectedFrame3_2 = false;
                isSelectedFrame3_3 = false;
                isSelectedFrame4_1 = false;
                isSelectedFrame4_2 = false;
                isSelectedFrame4_3 = false;
                //isSelectedFrame4_4 = false;
                isSelectedFrame6_1 = false;
                if(isSelectedFrame4_4){
                    Toast.makeText(this , "You have already selected." , Toast.LENGTH_SHORT ).show();
                    return;
                }
                setContentView(R.layout.activity_collage_4_4);
                initView();
                initView2();
                initView3();
                initView4();



                //mMainCollageFrameImage.setBackgroundResource(collageFrameID);

                Matrix mat44 = new Matrix();
                if (mBitmap1 != null && mBitmapRotate1!=null) {
                    mBitmapRotate1 = Bitmap.createBitmap(mBitmap1, 0, 0,
                            mBitmap1.getWidth(), mBitmap1.getHeight(), mat44, true);
                    mCollageFrameShape1.setImageBitmap(mBitmapRotate1);
                    mCollageFrameShape1.setOnTouchListener(this);
                    mCollageFrameShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (mBitmap2 != null && mBitmapRotate2!=null) {
                    mBitmapRotate2 = Bitmap.createBitmap(mBitmap2, 0, 0,
                            mBitmap2.getWidth(), mBitmap2.getHeight(), mat44, true);
                    mCollageFrameShape2.setImageBitmap(mBitmapRotate2);
                    mCollageFrameShape2.setOnTouchListener(this);
                    mCollageFrameShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (mBitmap3 != null && mBitmapRotate3!=null) {
                    mBitmapRotate3 = Bitmap.createBitmap(mBitmap3, 0, 0,
                            mBitmap3.getWidth(), mBitmap3.getHeight(), mat44, true);
                    mCollageFrameShape3.setImageBitmap(mBitmapRotate3);
                    mCollageFrameShape3.setOnTouchListener(this);
                    mCollageFrameShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                if (mBitmap4 != null && mBitmapRotate4!=null) {
                    mBitmapRotate3 = Bitmap.createBitmap(mBitmap4, 0, 0,
                            mBitmap4.getWidth(), mBitmap4.getHeight(), mat44, true);
                    mCollageFrameShape4.setImageBitmap(mBitmapRotate4);
                    mCollageFrameShape4.setOnTouchListener(this);
                    mCollageFrameShape4.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                isSelectedFrame4_4 = true;

                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                //sb_brightness.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                mCollageListRecycleView.setVisibility(View.VISIBLE);

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
                    Toast.makeText(this , "You have already selected." , Toast.LENGTH_SHORT ).show();
                    return;
                }

                setContentView(R.layout.activity_collage_6_1);
                initView();
                initView2();
                initView3();
                initView4();
                initView5();
                initView6();




                //mMainCollageFrameImage.setBackgroundResource(collageFrameID);
                Matrix mat61 = new Matrix();
                if (mBitmap1 != null && mBitmapRotate1!=null) {
                    mBitmapRotate1 = Bitmap.createBitmap(mBitmap1, 0, 0,
                            mBitmap1.getWidth(), mBitmap1.getHeight(), mat61, true);
                    mCollageFrameShape1.setImageBitmap(mBitmapRotate1);
                    mCollageFrameShape1.setOnTouchListener(this);
                    mCollageFrameShape1.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (mBitmap2 != null && mBitmapRotate2!=null) {
                    mBitmapRotate2 = Bitmap.createBitmap(mBitmap2, 0, 0,
                            mBitmap2.getWidth(), mBitmap2.getHeight(), mat61, true);
                    mCollageFrameShape2.setImageBitmap(mBitmapRotate2);
                    mCollageFrameShape2.setOnTouchListener(this);
                    mCollageFrameShape2.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if (mBitmap3 != null && mBitmapRotate3!=null) {
                    mBitmapRotate3 = Bitmap.createBitmap(mBitmap3, 0, 0,
                            mBitmap3.getWidth(), mBitmap3.getHeight(), mat61, true);
                    mCollageFrameShape3.setImageBitmap(mBitmapRotate3);
                    mCollageFrameShape3.setOnTouchListener(this);
                    mCollageFrameShape3.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                if (mBitmap4 != null && mBitmapRotate4!=null) {
                    mBitmapRotate3 = Bitmap.createBitmap(mBitmap4, 0, 0,
                            mBitmap4.getWidth(), mBitmap4.getHeight(), mat61, true);
                    mCollageFrameShape4.setImageBitmap(mBitmapRotate4);
                    mCollageFrameShape4.setOnTouchListener(this);
                    mCollageFrameShape4.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                if (mBitmap5 != null && mBitmapRotate5!=null) {
                    mBitmapRotate5 = Bitmap.createBitmap(mBitmap5, 0, 0,
                            mBitmap5.getWidth(), mBitmap5.getHeight(), mat61, true);
                    mCollageFrameShape5.setImageBitmap(mBitmapRotate5);
                    mCollageFrameShape5.setOnTouchListener(this);
                    mCollageFrameShape5.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                if (mBitmap6 != null && mBitmapRotate6!=null) {
                    mBitmapRotate6 = Bitmap.createBitmap(mBitmap6, 0, 0,
                            mBitmap6.getWidth(), mBitmap6.getHeight(), mat61, true);
                    mCollageFrameShape6.setImageBitmap(mBitmapRotate6);
                    mCollageFrameShape6.setOnTouchListener(this);
                    mCollageFrameShape6.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                isSelectedFrame6_1 = true;

                collageEffectsSeekbarLLayout.setVisibility(View.GONE);
                // sb_brightness.setVisibility(View.GONE);
                mCollageFrameRecycleView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                frameRecyclerLayout.setVisibility(View.VISIBLE);
                mCollageListRecycleView.setVisibility(View.VISIBLE);


                break;
            default:
                //setContentView(R.layout.activity_collage_selected_image);
                break;
        }

    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

        //imageSelectedOrNot =  imageSelectedOrNot();
//        if(!isSelectedImage){
//            Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (selectedImageView != null) {
            if (seekBar.getId() == R.id.collageBrightness) {
                increaseBrightness(selectedImageView, progress);
            }
            if (seekBar.getId() == R.id.collageContrast) {
                increaseContrast(selectedImageView, progress);
            }

        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void increaseBrightness(ImageView mImageView, int progressValue){
        mImageView.setColorFilter(ColorFilterGenerator.adjustBrightness(progressValue));
    }
    private void increaseContrast(ImageView mImageView, int progressValue){
        mImageView.setColorFilter(ColorFilterGenerator.adjustContrast(progressValue));
    }


    /**************************Frame**********************************/
    @Override
    public void onFrameSelected(int frameID, FrameType frameType) {

        switch (frameType) {

            case F_NONE:
                mMainCollageFrameImage.setBackgroundResource(R.color.white);
                //mCollageRLayout.setBackgroundResource(R.color.white);
                break;

            case F_ONE:

                mMainCollageFrameImage.setBackgroundResource(R.drawable.frame_1);
                //mCollageRLayout.setBackgroundResource(R.drawable.frame_1);
                break;

            case F_TWO:
                mMainCollageFrameImage.setBackgroundResource(R.drawable.frame_2);
                //mCollageRLayout.setBackgroundResource(R.drawable.frame_2);
                break;

            case F_THREE:
                mMainCollageFrameImage.setBackgroundResource(R.drawable.frame_3);
                //mCollageRLayout.setBackgroundResource(R.drawable.frame_3);
                break;

            case F_FOUR:
                mMainCollageFrameImage.setBackgroundResource(R.drawable.frame_4);
                // mCollageRLayout.setBackgroundResource(R.drawable.frame_4);
                break;

            case F_FIVE:
                mMainCollageFrameImage.setBackgroundResource(R.drawable.frame_5);
                // mCollageRLayout.setBackgroundResource(R.drawable.frame_5);
                break;

            case F_SIX:
                mMainCollageFrameImage.setBackgroundResource(R.drawable.frame_6);
                //mCollageRLayout.setBackgroundResource(R.drawable.frame_6);
                break;
            case F_SEVEN:
                mMainCollageFrameImage.setBackgroundResource(R.drawable.frame_7);
                //mCollageRLayout.setBackgroundResource(R.drawable.frame_7);
                break;
            case F_EIGHT:
                mMainCollageFrameImage.setBackgroundResource(R.drawable.frame_8);
                //mCollageRLayout.setBackgroundResource(R.drawable.frame_8);
                break;
            case F_NINE:
                mMainCollageFrameImage.setBackgroundResource(R.drawable.frame_9);
                //mCollageRLayout.setBackgroundResource(R.drawable.frame_9);
                break;
            case F_TEN:
                mMainCollageFrameImage.setBackgroundResource(R.drawable.frame_12);
                //mCollageRLayout.setBackgroundResource(R.drawable.frame_10);
                break;
            case F_ELEVEN:
                mMainCollageFrameImage.setBackgroundResource(R.drawable.frame_11);
                //mCollageRLayout.setBackgroundResource(R.drawable.frame_11);
                break;
            case F_TWELVE:
                mMainCollageFrameImage.setBackgroundResource(R.drawable.frame_12);
                // mCollageRLayout.setBackgroundResource(R.drawable.frame_12);
                break;
        }

    }



    @Override
    public void onEditTextChangeListener(View rootView, String text, int colorCode) {

    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {

    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {

    }




    /**************** Tools  / ToolsRecycler*****************/
    @Override
    public void onToolsRecyclerSelected(ToolsRecyclerType toolsRecyclerType) {
        switch (toolsRecyclerType) {

            case CROP:
                isSelectedEffects = false ;

//                sb_brightness.setVisibility(View.GONE);
//                mCollageListRecycleView.setVisibility(View.GONE);
//                mCollageFrameRecycleView.setVisibility(View.GONE);
//                frameRecyclerLayout.setVisibility(View.GONE);
//                imageEffectsThumbnails.setVisibility(View.GONE);

                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isSelectedImage1) {
                    Bitmap bitmap = ((BitmapDrawable)mCollageFrameShape1.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage2) {
                    Bitmap bitmap = ((BitmapDrawable) mCollageFrameShape2.getDrawable()).getBitmap();
                    if (bitmap != null)
                    {
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage3) {
                    Bitmap bitmap = ((BitmapDrawable)mCollageFrameShape3.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage4) {
                    Bitmap bitmap = ((BitmapDrawable) mCollageFrameShape4.getDrawable()).getBitmap();
                    if (bitmap != null){
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }

                else if (isSelectedImage5) {
                    Bitmap bitmap = ((BitmapDrawable) mCollageFrameShape5.getDrawable()).getBitmap();
                    if (bitmap != null){
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (isSelectedImage6) {
                    Bitmap bitmap = ((BitmapDrawable)mCollageFrameShape6.getDrawable()).getBitmap();
                    if(bitmap!=null) {
                        cropImageUri(getImageUri(mContext, bitmap));
                    }else if(bitmap==null) {
                        Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case ROTATION:



                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isSelectedImage1) {
                    angle1 = angle1 + 10;
                    mCollageFrameShape1.setRotation(angle1);
                } else if (isSelectedImage2) {
                    angle2 = angle2 + 10;
                    mCollageFrameShape2.setRotation(angle2);
                } else if (isSelectedImage3) {
                    angle3 = angle3 + 10;
                    mCollageFrameShape3.setRotation(angle3);

                }

                else if (isSelectedImage4) {
                    angle4 = angle4 + 10;
                    mCollageFrameShape4.setRotation(angle4);
                }

                else if (isSelectedImage5) {
                    angle5 = angle5 + 10;
                    mCollageFrameShape5.setRotation(angle5);
                }
                else if (isSelectedImage6) {
                    angle6 = angle6 + 10;
                    mCollageFrameShape5.setRotation(angle6);
                }


                break;


            case RT_LEFT:
//                sb_brightness.setVisibility(View.GONE);
//                mCollageListRecycleView.setVisibility(View.GONE);
//                mCollageFrameRecycleView.setVisibility(View.GONE);
//                frameRecyclerLayout.setVisibility(View.GONE);
//                imageEffectsThumbnails.setVisibility(View.GONE);

                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isSelectedImage1) {

                    mMatrixShape1.postRotate(-12, mCollageFrameShape1.getMeasuredWidth() / 2,
                            mCollageFrameShape1.getMeasuredHeight() / 2);
                    mCollageFrameShape1.setImageMatrix(mMatrixShape1);

                } else if (isSelectedImage2) {
                    mMatrixShape2.postRotate(-12, mCollageFrameShape2.getMeasuredWidth() / 2,
                            mCollageFrameShape2.getMeasuredHeight() / 2);
                    mCollageFrameShape2.setImageMatrix(mMatrixShape2);
                } else if (isSelectedImage3) {
                    mMatrixShape3.postRotate(-12, mCollageFrameShape3.getMeasuredWidth() / 2,
                            mCollageFrameShape3.getMeasuredHeight() / 2);
                    mCollageFrameShape3.setImageMatrix(mMatrixShape3);
                }

                else if (isSelectedImage4) {
                    mMatrixShape4.postRotate(-12, mCollageFrameShape4.getMeasuredWidth() / 2,
                            mCollageFrameShape4.getMeasuredHeight() / 2);
                    mCollageFrameShape4.setImageMatrix(mMatrixShape4);
                }

                else if (isSelectedImage5) {
                    mMatrixShape5.postRotate(-12, mCollageFrameShape5.getMeasuredWidth() / 2,
                            mCollageFrameShape5.getMeasuredHeight() / 2);
                    mCollageFrameShape5.setImageMatrix(mMatrixShape5);
                }
                else if (isSelectedImage6) {
                    mMatrixShape6.postRotate(-12, mCollageFrameShape6.getMeasuredWidth() / 2,
                            mCollageFrameShape6.getMeasuredHeight() / 2);
                    mCollageFrameShape6.setImageMatrix(mMatrixShape6);
                }
                break;

            case RT_RIGHT:
//                sb_brightness.setVisibility(View.GONE);
//                mCollageListRecycleView.setVisibility(View.GONE);
//                mCollageFrameRecycleView.setVisibility(View.GONE);
//                frameRecyclerLayout.setVisibility(View.GONE);
//                imageEffectsThumbnails.setVisibility(View.GONE);


                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isSelectedImage1) {
                    mMatrixShape1.postRotate(12, mCollageFrameShape1.getMeasuredWidth() / 2,
                            mCollageFrameShape1.getMeasuredHeight() / 2);
                    mCollageFrameShape1.setImageMatrix(mMatrixShape1);
                } else if (isSelectedImage2) {
                    mMatrixShape2.postRotate(12, mCollageFrameShape2.getMeasuredWidth() / 2,
                            mCollageFrameShape2.getMeasuredHeight() / 2);
                    mCollageFrameShape2.setImageMatrix(mMatrixShape2);
                } else if (isSelectedImage3) {
                    mMatrixShape3.postRotate(12, mCollageFrameShape3.getMeasuredWidth() / 2,
                            mCollageFrameShape3.getMeasuredHeight() / 2);
                    mCollageFrameShape3.setImageMatrix(mMatrixShape3);
                }
                else if (isSelectedImage4) {
                    mMatrixShape4.postRotate(12, mCollageFrameShape4.getMeasuredWidth() / 2,
                            mCollageFrameShape4.getMeasuredHeight() / 2);
                    mCollageFrameShape4.setImageMatrix(mMatrixShape4);
                }
                else if (isSelectedImage5) {
                    mMatrixShape5.postRotate(12, mCollageFrameShape5.getMeasuredWidth() / 2,
                            mCollageFrameShape5.getMeasuredHeight() / 2);
                    mCollageFrameShape5.setImageMatrix(mMatrixShape5);
                }
                else if (isSelectedImage6) {
                    mMatrixShape6.postRotate(12, mCollageFrameShape6.getMeasuredWidth() / 2,
                            mCollageFrameShape6.getMeasuredHeight() / 2);
                    mCollageFrameShape6.setImageMatrix(mMatrixShape6);
                }
                break;

            case FLIP:

                isSelectedEffects = false ;

//                sb_brightness.setVisibility(View.GONE);
//                mCollageListRecycleView.setVisibility(View.GONE);
//                mCollageFrameRecycleView.setVisibility(View.GONE);
//                frameRecyclerLayout.setVisibility(View.GONE);
//                imageEffectsThumbnails.setVisibility(View.GONE);

                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please select your image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isSelectedImage1) {
                    mMatrixShape1.postScale(-1.0f, 1.0f, mCollageFrameShape1.getWidth() / 2f,
                            mCollageFrameShape1.getHeight() / 2f);
                    mCollageFrameShape1.setImageMatrix(mMatrixShape1);

                }
                else if (isSelectedImage2) {
                    mMatrixShape2.postScale(-1.0f, 1.0f, mCollageFrameShape2.getWidth() / 2f,
                            mCollageFrameShape2.getHeight() / 2f);

                    mCollageFrameShape2.setImageMatrix(mMatrixShape2);

                } else if (isSelectedImage3) {
                    mMatrixShape3.postScale(-1.0f, 1.0f, mCollageFrameShape3.getWidth() / 2f,
                            mCollageFrameShape3.getHeight() / 2f);
                    mCollageFrameShape3.setImageMatrix(mMatrixShape3);
                }
                else if (isSelectedImage4) {
                    mMatrixShape4.postScale(-1.0f, 1.0f, mCollageFrameShape4.getWidth() / 2f,
                            mCollageFrameShape4.getHeight() / 2f);
                    mCollageFrameShape4.setImageMatrix(mMatrixShape4);
                }
                else if (isSelectedImage5) {
                    mMatrixShape5.postScale(-1.0f, 1.0f, mCollageFrameShape5.getWidth() / 2f,
                            mCollageFrameShape5.getHeight() / 2f);
                    mCollageFrameShape5.setImageMatrix(mMatrixShape5);
                }
                else if (isSelectedImage6) {
                    mMatrixShape6.postScale(-1.0f, 1.0f, mCollageFrameShape6.getWidth() / 2f,
                            mCollageFrameShape6.getHeight() / 2f);
                    mCollageFrameShape6.setImageMatrix(mMatrixShape6);
                }else if (!imageSelectedOrNot) {
                    Toast.makeText(this , "Sorry, Image not selected.", Toast.LENGTH_SHORT).show();

                }

                break;
        }
    }



    /**********************Effector / Effects ***************/
    //private void bindDataToAdapter(Bitmap bitmap , final ImageView imageView) {
    private void bindDataToAdapter( ) {
        final Context context = this.getApplication();
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {

                if ( mCollageFrameShape1.getWidth()==0 ||  mCollageFrameShape1.getHeight()==0 ) {

                    return;
                }

                //Bitmap thumbImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.photo), 640, 640, false);
                //Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                Bitmap thumbImage = null;
                //Bitmap thumbImage = Bitmap.createScaledBitmap(selectedBitmap,  selectedImageView.getWidth(), selectedImageView.getHeight() , false);
                if (isSelectedImage1) {
                    thumbImage = Bitmap.createScaledBitmap(selectedBitmap1,  mCollageFrameShape1.getWidth(), mCollageFrameShape1.getHeight() , false);
                }
                else if (isSelectedImage2) {
                    thumbImage = Bitmap.createScaledBitmap(selectedBitmap2,  mCollageFrameShape2.getWidth(), mCollageFrameShape2.getHeight() , false);
                }
                else if (isSelectedImage3) {
                    thumbImage = Bitmap.createScaledBitmap(selectedBitmap3,  mCollageFrameShape3.getWidth(), mCollageFrameShape3.getHeight() , false);
                }else if (isSelectedImage4) {
                    thumbImage = Bitmap.createScaledBitmap(selectedBitmap4,  mCollageFrameShape4.getWidth(), mCollageFrameShape4.getHeight() , false);
                }
                else if (isSelectedImage5) {
                    thumbImage = Bitmap.createScaledBitmap(selectedBitmap5,  mCollageFrameShape5.getWidth(), mCollageFrameShape5.getHeight() , false);
                }
                else if (isSelectedImage6) {
                    thumbImage = Bitmap.createScaledBitmap(selectedBitmap6,  mCollageFrameShape6.getWidth(), mCollageFrameShape6.getHeight() , false);
                }

                //Bitmap thumbImage =imageBitmap;
                //imageBitmapTosave = mBitmap1;

                ThumbnailItem t1 = new ThumbnailItem();
                ThumbnailItem t2 = new ThumbnailItem();
                ThumbnailItem t3 = new ThumbnailItem();
                ThumbnailItem t4 = new ThumbnailItem();
                ThumbnailItem t5 = new ThumbnailItem();
                ThumbnailItem t6 = new ThumbnailItem();
                ThumbnailItem t7 = new ThumbnailItem();
                ThumbnailItem t8 = new ThumbnailItem();
//                ThumbnailItem t9 = new ThumbnailItem();
//                ThumbnailItem t10 = new ThumbnailItem();


                t1.image = thumbImage;
                t2.image = thumbImage;
                t3.image = thumbImage;
                t4.image = thumbImage;
                t5.image = thumbImage;
                t6.image = thumbImage;
                t7.image = thumbImage;
                t8.image = thumbImage;
//                t9.image = thumbImage;
//                t10.image = thumbImage;


                ThumbnailsManager.clearThumbs();
                ThumbnailsManager.addThumb(t1); // Original Image

                t2.filter = SampleFilters.getStarLitFilter();
                ThumbnailsManager.addThumb(t2);

                t3.filter = SampleFilters.getBlueMessFilter();
                ThumbnailsManager.addThumb(t3);

                t4.filter = SampleFilters.getAweStruckVibeFilter();
                ThumbnailsManager.addThumb(t4);

                t5.filter = SampleFilters.getLimeStutterFilter();
                ThumbnailsManager.addThumb(t5);

                t6.filter = SampleFilters.getNightWhisperFilter();
                ThumbnailsManager.addThumb(t6);

                t7.filter = SampleFilters.getStarLitFilter();
                ThumbnailsManager.addThumb(t7);

                t8.filter = SampleFilters.getBlueMessFilter();
                ThumbnailsManager.addThumb(t8);

//                t9.filter = SampleFilters.getAweStruckVibeFilter();
//                ThumbnailsManager.addThumb(t9);
//
//                t10.filter = SampleFilters.getLimeStutterFilter();
//                ThumbnailsManager.addThumb(t10);

                List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(context);

                ThumbnailsAdapter adapter = new ThumbnailsAdapter(thumbs, (ThumbnailCallback) instance);
                imageEffectsThumbnails.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };
        handler.post(r);
    }


    @Override
    public void onThumbnailClick(Filter filter) {

        //Bitmap bitmap = ((BitmapDrawable) selectedImageView.getDrawable()).getBitmap();
        //setOnThumbnailClick(filter ,  Constants.imageView,   Constants.imageBitmap);
        //setOnThumbnailClick(filter , selectedImageView,  bitmap);

        if (isSelectedImage1) {
            mCollageFrameShape1.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(selectedBitmap1, mCollageFrameShape1.getWidth(), mCollageFrameShape1.getHeight() ,false )));
        }
        else if (isSelectedImage2) {
            mCollageFrameShape2.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(selectedBitmap2, mCollageFrameShape2.getWidth(), mCollageFrameShape2.getHeight() ,false )));
        }
        else if (isSelectedImage3) {
            mCollageFrameShape3.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(selectedBitmap3, mCollageFrameShape3.getWidth(), mCollageFrameShape3.getHeight() ,false )));
        }else if (isSelectedImage4) {
            mCollageFrameShape4.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(selectedBitmap4, mCollageFrameShape4.getWidth(), mCollageFrameShape4.getHeight() ,false )));
        }
        else if (isSelectedImage5) {
            mCollageFrameShape5.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(selectedBitmap5, mCollageFrameShape5.getWidth(), mCollageFrameShape5.getHeight() ,false )));
        }
        else if (isSelectedImage6) {
            mCollageFrameShape6.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(selectedBitmap6, mCollageFrameShape6.getWidth(), mCollageFrameShape6.getHeight() ,false )));
        }

        // selectedImageView.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(selectedBitmap, selectedImageView.getWidth(), selectedImageView.getHeight() ,false )));

    }

    private void setOnThumbnailClick(Filter filter , ImageView imageView ,   Bitmap bitmap) {
        imageView.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(bitmap, imageView.getWidth(),imageView.getHeight() ,false )));

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
        }else   {
            return imageSelectedOrNot = false;
        }
    }


    public static Bitmap generateCircularBitmap(Bitmap input) {

        final int width = input.getWidth();
        final int height = input.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle(
                (float) (width / 2)
                , (float) (height / 2)
                , (float) Math.min(width, (height / 2))
                , Path.Direction.CCW
        );

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(input, 0, 0, null);
        return outputBitmap;
    }

    public void openGallery(int PICK_GALLARY_REQUEST) {

        if (isKitKat) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select picture"), PICK_GALLARY_REQUEST);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select picture"), PICK_GALLARY_REQUEST);
        }
    }



    private String captureImage() {

        OutputStream output;
        Calendar cal = Calendar.getInstance();

        Bitmap bitmap = Bitmap.createBitmap(mCollageRLayout.getWidth(), mCollageRLayout.getHeight(), Bitmap.Config.ARGB_8888);

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, mCollageRLayout.getWidth(), mCollageRLayout.getHeight());

        Canvas mBitCanvas = new Canvas(bitmap);
        mCollageRLayout.draw(mBitCanvas);

        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath() + "/SelfiePro/");
        dir.mkdirs();

        mImageName = "SelfiePro" + cal.getTimeInMillis() + ".png";

        // Create a name for the saved image
        mImageFile = new File(dir, mImageName);
        runMediaScan( mContext, mImageFile );

        // Show a toast message on successful save
        Toast.makeText(CollageSelectedActivity.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

        try {
            output = new FileOutputStream(mImageFile);
            // Compress into png format image from 0% - 100%
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mImageName;
    }

    private String editImage() {

        OutputStream output;
        Calendar cal = Calendar.getInstance();

        Bitmap bitmap = Bitmap.createBitmap(mCollageRLayout.getWidth(), mCollageRLayout.getHeight(), Bitmap.Config.ARGB_8888);

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, mCollageRLayout.getWidth(), mCollageRLayout.getHeight());

        Canvas mBitCanvas = new Canvas(bitmap);
        mCollageRLayout.draw(mBitCanvas);

        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath() + "/SelfiePro/");
        dir.mkdirs();

        mImageName = "SelfiePro" + cal.getTimeInMillis() + ".png";

        // Create a name for the saved image
        mImageFile = new File(dir, mImageName);
        //runMediaScan( mContext, mImageFile );

        // Show a toast message on successful save
        //  Toast.makeText(CollageSelectedActivity.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

        try {
            output = new FileOutputStream(mImageFile);
            // Compress into png format image from 0% - 100%
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mImageName;
    }



    public void runMediaScan(Context context, File fileName) {
        MediaScannerConnection.scanFile(
                context, new String[]{fileName.getPath()}, null,
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                        Log.e("acn ","connected");
                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("scan " , "completed" );
                    }
                });
    }


    protected void cropImageUri(Uri picUri) {
        try {
            //Intent intent = new Intent("com.android.camera.action.CROP");
            Intent intent = new Intent("com.android.camera.action.CROP",android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(picUri, "image/*");

            intent.putExtra("crop", "true");
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 3);
            intent.putExtra("aspectY", 4);
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("return-data", true);

            startActivityForResult(intent, CROP_IMAGE);

        } catch (ActivityNotFoundException e) {
            Log.e("", "Your device doesn't support the crop action!");
        }

    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }



    public void show_alert_back(String title, String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CollageSelectedActivity.this);
        // set title
        alertDialogBuilder.setTitle(title);
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(true)
                .setIcon(R.drawable.ic_launcher).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close
                mMyPrecfence.saveString(Constants.COLLAGE_ACTIVITY , "false");
                // current activity
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
            b.setTextColor(getResources().getColor(R.color.colorAccent));
        if (b1 != null)
            b1.setTextColor(getResources().getColor(R.color.colorAccent));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //show_alert_back("Exit", "Are you sure you want to exit Editor ?");

        mMyPrecfence.saveString(Constants.COLLAGE_ACTIVITY , "false");
        // current activity
        finish();

        return super.onKeyDown(keyCode, event);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        show_alert_back("Exit", "Are you sure you want to exit Editor ?");
    }

}
