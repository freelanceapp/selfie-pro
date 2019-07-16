package com.mojodigi.selfiepro.camera;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.mojodigi.selfiepro.R;
import com.mojodigi.selfiepro.activity.EditImageActivity;
import com.mojodigi.selfiepro.adapter.ToolsRecyclerAdapter;
import com.mojodigi.selfiepro.collage.ColorFilterGenerator;
import com.mojodigi.selfiepro.enums.ToolsRecyclerType;
import com.mojodigi.selfiepro.filterUtils.ThumbnailCallback;
import com.mojodigi.selfiepro.filterUtils.ThumbnailItem;
import com.mojodigi.selfiepro.filterUtils.ThumbnailsAdapter;
import com.mojodigi.selfiepro.filterUtils.ThumbnailsManager;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.ViewType;


public class CameraActivity  extends AppCompatActivity implements OnPhotoEditorListener, View.OnTouchListener,
        View.OnClickListener, OnCameraEditTollsSelected, SeekBar.OnSeekBarChangeListener,  ThumbnailCallback, OnToolsRecyclerSelected
{
    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final int PICK_CAMERA_REQUEST = 1;
    private Bitmap mSelectedImageBitMap = null  ;
    private ImageView selectedImageView ,  cameraSelectedImageShape ;
    private String mImageName ;
    private RelativeLayout mCameraRLayout;
    private boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    private LinearLayout mCameraEditBackLLayout, cameraDiscardLayout , /*cameraRedoRotationLayout,*/  mCameraSaveLayout , cameraEffectsSeekbarLLayout , cameraRecyclerLayout ;
    private MyPreference mMyPrecfence = null;
    private RecyclerView mCameraListRecycleView , mCameraFrameRecycleView;
    private RecyclerView cameraEditToolsRecycleView , cameraFilterView;
    private CameraEditToolsAdapter mCameraEditToolsAdapter;
    private ToolsRecyclerAdapter toolsRecyclerAdapter;

    private String mIntentType = "";
    private Context mContext = null ;
    private File mImageFile = null;

    private boolean imageSelectedOrNot = false;
    private boolean isSelectedImage = true;

    private SeekBar cameraBrightness , cameraContrast ;
    private static final int CROP_IMAGE = 2;

    public static CameraActivity instance;
    private RecyclerView imageEffectsThumbnails , toolsRecyclerView;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private String pictureFilePath;

    private FrameLayout cameraFrameShape;

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // Remember some things for zooming
    private float oldDist = 1f;

    /***********New************/
    float scalediff;
    private float d = 0f;
    private float newRot = 0f;

    FrameLayout.LayoutParams parms;
    int startwidth;
    int startheight;
    float dx = 0, dy = 0, x = 0, y = 0;
    float angle = 0;

    FrameLayout.LayoutParams  frameLayoutParams ;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        instance = this;

        if (mContext == null) {
            mContext = CameraActivity.this;
        }
        if (mMyPrecfence == null) {
            mMyPrecfence = MyPreference.getMyPreferenceInstance(mContext);
        }

        try {
            mMyPrecfence.saveString(Constants.COLLAGE_ACTIVITY, "true");
            mIntentType = mMyPrecfence.getString(Constants.INTENT_TYPE);
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        cameraEffectsSeekbarLLayout.setVisibility(View.GONE);
        mCameraFrameRecycleView.setVisibility(View.GONE);
        mCameraListRecycleView.setVisibility(View.GONE);
        imageEffectsThumbnails.setVisibility(View.GONE);
        cameraRecyclerLayout.setVisibility(View.GONE);
        toolsRecyclerView.setVisibility(View.GONE);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

       frameLayoutParams = new FrameLayout.LayoutParams(700, 900 );
    //frameLayoutParams = new FrameLayout.LayoutParams(700, 900 , Gravity.CENTER);
     //FrameLayout.LayoutParams  frameLayoutParams = new FrameLayout.LayoutParams(700, 900 , Gravity.CENTER);

        // FrameLayout.LayoutParams  layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

//        layoutParams.leftMargin = -50;
//        layoutParams.topMargin = -50;
//        layoutParams.bottomMargin = -150;
//        layoutParams.rightMargin = -150;

        cameraSelectedImageShape = (ImageView) findViewById(R.id.cameraSelectedImageShape);
        //cameraSelectedImageShape.setPadding(-20, -20, -20, -20);
        //cameraSelectedImageShape.setBackground(getResources().getDrawable(R.drawable.white_image_bg));

        cameraSelectedImageShape.setLayoutParams(frameLayoutParams);
        cameraSelectedImageShape.setOnTouchListener(this);


        imageEffectsThumbnails = (RecyclerView) findViewById(R.id.imageEffectsThumbnails);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        imageEffectsThumbnails.setLayoutManager(layoutManager);
        imageEffectsThumbnails.setHasFixedSize(true);

        mCameraRLayout = (RelativeLayout) findViewById(R.id.idCameraRLayout);
        cameraFrameShape = (FrameLayout) findViewById(R.id.cameraFrameShape);

        mCameraEditToolsAdapter = new CameraEditToolsAdapter(this);
        toolsRecyclerAdapter = new ToolsRecyclerAdapter(this);


        mCameraEditBackLLayout = (LinearLayout) findViewById(R.id.idCameraEditBackLLayout);
        cameraDiscardLayout = (LinearLayout) findViewById(R.id.cameraDiscardLayout);
        //cameraRedoRotationLayout = (LinearLayout) findViewById(R.id.cameraRedoRotationLayout);
        mCameraSaveLayout = (LinearLayout) findViewById(R.id.idCameraSaveLayout);


        cameraRecyclerLayout = (LinearLayout) findViewById(R.id.cameraRecyclerLayout);
        cameraRecyclerLayout.setVisibility(View.GONE);

        cameraEffectsSeekbarLLayout = (LinearLayout) findViewById(R.id.cameraEffectsSeekbarLLayout);
        cameraEffectsSeekbarLLayout.setOnClickListener(this);
        mCameraEditBackLLayout.setOnClickListener(this);
        cameraDiscardLayout.setOnClickListener(this);
        //cameraRedoRotationLayout.setOnClickListener(this);
        mCameraSaveLayout.setOnClickListener(this);

        cameraBrightness = (SeekBar)findViewById(R.id.cameraBrightness);
        cameraBrightness.setOnSeekBarChangeListener(this);

        cameraContrast = (SeekBar)findViewById(R.id.cameraContrast);
        cameraContrast.setOnSeekBarChangeListener(this);

        mCameraListRecycleView = (RecyclerView) findViewById(R.id.idCameraListRecycleView);
        mCameraFrameRecycleView = (RecyclerView) findViewById(R.id.idCameraFrameRecycleView);
        mCameraListRecycleView.setVisibility(View.GONE);
        mCameraFrameRecycleView.setVisibility(View.GONE);


        cameraEditToolsRecycleView = (RecyclerView) findViewById(R.id.cameraEditToolsRecycleView);
        cameraEditToolsRecycleView.setVisibility(View.VISIBLE);

        LinearLayoutManager mCollageEditToolsLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        cameraEditToolsRecycleView.setLayoutManager(mCollageEditToolsLManager);
        cameraEditToolsRecycleView.setAdapter(mCameraEditToolsAdapter);


        cameraFilterView = (RecyclerView) findViewById(R.id.cameraFilterView);
        cameraFilterView.setVisibility(View.GONE);


        toolsRecyclerView = (RecyclerView) findViewById(R.id.toolsRecyclerView);
        toolsRecyclerView.setVisibility(View.GONE);


        LinearLayoutManager mtoolsRecyclerAdapterLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        toolsRecyclerView.setLayoutManager(mtoolsRecyclerAdapterLManager);
        toolsRecyclerView.setAdapter(toolsRecyclerAdapter);



        /**************************************Get Image******************************/
        if (mIntentType.equalsIgnoreCase("IntentCamera")) {
            Uri myUri = null;
            Intent extrasIntent = getIntent();
            if (extrasIntent != null) {
                myUri = extrasIntent.getParcelableExtra("BITMAP_PICK_CAMERA");

                cameraSelectedImageShape.setImageURI(myUri);
                //angle = angle + 1;

                cameraSelectedImageShape.setRotation(1);

                fixAntiAlias(cameraSelectedImageShape);


                // cameraSelectedImageShape.setBackgroundColor(Color.parseColor("#ffffff"));
                //cameraSelectedImageShape.setBackgroundColor(getResources().getColor(R.color.white));
                //cameraSelectedImageShape.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //cameraSelectedImageShape.setRotation(0);

                //cameraSelectedImageShape.setImageBitmap(flipImage(((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap()));
                //cameraSelectedImageShape.setImageBitmap(flipImage(((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap()));

                imageSelectedOrNot = true;
            }
            try {
                mSelectedImageBitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), myUri);
                //cameraSelectedImageShape.setImageBitmap(mSelectedImageBitMap);
//                Matrix mat = new Matrix();
//                if (mSelectedImageBitMap != null) {
//                    mBitmapRotate = Bitmap.createBitmap(mSelectedImageBitMap, 0, 0,
//                            mSelectedImageBitMap.getWidth(), mSelectedImageBitMap.getHeight(), mat, true);
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.idCameraSaveLayout:
                saveImage();
                break;

            case R.id.cameraDiscardLayout:
                //cameraSelectedImageShape.setLayoutParams(new FrameLayout.LayoutParams(700, 900, Gravity.CENTER));
                //frameLayoutParams = new FrameLayout.LayoutParams(700, 900 , Gravity.CENTER);
                cameraSelectedImageShape.setRotation(1);
                fixAntiAlias(cameraSelectedImageShape);
                break;

//            case R.id.cameraRedoRotationLayout:
//                cameraSelectedImageShape.setRotation(angle);
//                fixAntiAlias(cameraSelectedImageShape);
//                break;

            case R.id.idCameraEditBackLLayout:
                //Toast.makeText(mContext, "Camera Selected Image Shape" , Toast.LENGTH_SHORT).show();
               show_alert_back("Exit", "Are you sure you want to exit Editor ?");
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final ImageView view = (ImageView) v;

        if (view == cameraSelectedImageShape) {
            //cameraSelectedImageShape.setScaleType(ImageView.ScaleType.MATRIX);
            //selectedImageView = cameraSelectedImageShape ;
            isSelectedImage = true;

            //if(cameraSelectedImageShape!=null)
            // cameraSelectedImageShape.setBackgroundColor(getResources().getColor(R.color.white));
            //cameraSelectedImageShape.setBackground(getResources().getDrawable(R.drawable.white_image_bg));

           // ((BitmapDrawable) view.getDrawable()).setAntiAlias(true);

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:

                    parms = (FrameLayout.LayoutParams) view.getLayoutParams();
                    startwidth = parms.width;
                    startheight = parms.height;
                    dx = event.getRawX() - parms.leftMargin;
                    dy = event.getRawY() - parms.topMargin;
                    mode = DRAG;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        mode = ZOOM;
                    }

                    d = rotation(event);

                    break;
                case MotionEvent.ACTION_UP:

                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {

                        x = event.getRawX();
                        y = event.getRawY();

                        parms.leftMargin = (int) (x - dx);
                        parms.topMargin = (int) (y - dy);

                        parms.rightMargin = 0;
                        parms.bottomMargin = 0;
                        parms.rightMargin = parms.leftMargin + (5 * parms.width);
                        parms.bottomMargin = parms.topMargin + (10 * parms.height);

                        view.setLayoutParams(parms);

                    } else if (mode == ZOOM) {

                        if (event.getPointerCount() == 2) {

                            newRot = rotation(event);
                            float r = newRot - d;
                            angle = r;

                            x = event.getRawX();
                            y = event.getRawY();

                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                float scale = newDist / oldDist * view.getScaleX();
                                if (scale > 0.6) {
                                    scalediff = scale;
                                    view.setScaleX(scale);
                                    view.setScaleY(scale);

                                }
                            }

                            view.animate().rotationBy(angle).setDuration(0).setInterpolator(new LinearInterpolator()).start();

                            x = event.getRawX();
                            y = event.getRawY();

                            parms.leftMargin = (int) ((x - dx) + scalediff);
                            parms.topMargin = (int) ((y - dy) + scalediff);

                            parms.rightMargin = 0;
                            parms.bottomMargin = 0;
                            parms.rightMargin = parms.leftMargin + (5 * parms.width);
                            parms.bottomMargin = parms.topMargin + (10 * parms.height);

                            view.setLayoutParams(parms);


                        }
                    }
                    break;
            }
        }
        return true;

    }


    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void fixAntiAlias(View viewAntiAlias) {
        if (Build.VERSION.SDK_INT > 10) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
            viewAntiAlias.setLayerType(View.LAYER_TYPE_SOFTWARE, p);
            ((View) viewAntiAlias.getParent()).setLayerType(View.LAYER_TYPE_SOFTWARE, p);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK  ) {
            switch (requestCode) {
                case PICK_CAMERA_REQUEST:

                    File imgFile = new File(pictureFilePath);
                    if (imgFile.exists()) {
                        cameraSelectedImageShape.setImageURI(Uri.fromFile(imgFile));
                        cameraSelectedImageShape.setRotation(1);
                        fixAntiAlias(cameraSelectedImageShape);

                        try {
                            mSelectedImageBitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(imgFile));
//                            Matrix mat = new Matrix();
//                            if (mSelectedImageBitMap != null) {
//                                mBitmapRotate = Bitmap.createBitmap(mSelectedImageBitMap, 0,0,
//                                        mSelectedImageBitMap.getWidth(), mSelectedImageBitMap.getHeight(), mat, true);
//                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    break;


                case CROP_IMAGE:
                    Bitmap mBitmap = null;
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri == null) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            mBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                        } else {
                            mBitmap = (Bitmap) data.getExtras().get("data");
                        }
                    } else if (selectedImageUri != null && Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                        mBitmap = (Bitmap) data.getExtras().get("data");
                    } else {
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

                        if (isSelectedImage) {
                            cameraSelectedImageShape.setImageBitmap(mCropedBitmap);
                            //angle = angle + 1;
                            //cameraSelectedImageShape.setRotation(angle);
                            fixAntiAlias(cameraSelectedImageShape);
                        }
                    }

                    break;


            }
        }
    }





    /**************Select Tolls  **********************/
    @Override
    public void onCameraEditTollsSelected(CameraEditToolsType cameraEditToolsType) {
        switch (cameraEditToolsType) {
            case CAMERA:

                mCameraListRecycleView.setVisibility(View.GONE);
                cameraFilterView.setVisibility(View.GONE);
                cameraEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mCameraFrameRecycleView.setVisibility(View.GONE);
                cameraRecyclerLayout.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);

                if(mMyPrecfence!=null)
                    mMyPrecfence.saveString(Constants.INTENT_TYPE, "IntentCamera");
                if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                    sendTakePictureIntent();
                }

                break;

            case TOOLS:
                //cameraSelectedImageShape.setScaleType(ImageView.ScaleType.MATRIX);
                mCameraListRecycleView.setVisibility(View.GONE);
                cameraFilterView.setVisibility(View.GONE);
                cameraEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mCameraFrameRecycleView.setVisibility(View.GONE);
                cameraRecyclerLayout.setVisibility(View.VISIBLE);
                toolsRecyclerView.setVisibility(View.VISIBLE);
                break;


            case EFFECTS:

                bindDataToAdapter();

                mCameraFrameRecycleView.setVisibility(View.GONE);
                mCameraListRecycleView.setVisibility(View.GONE);
                cameraEffectsSeekbarLLayout.setVisibility(View.GONE);
                cameraFilterView.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                cameraRecyclerLayout.setVisibility(View.VISIBLE);
                imageEffectsThumbnails.setVisibility(View.VISIBLE);

                 angle = angle + 1;
                cameraSelectedImageShape.setRotation(1);
                fixAntiAlias(cameraSelectedImageShape);

                break;


            case ADJUST:
                mCameraFrameRecycleView.setVisibility(View.GONE);
                mCameraListRecycleView.setVisibility(View.GONE);
                cameraFilterView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                cameraRecyclerLayout.setVisibility(View.VISIBLE);
                cameraEffectsSeekbarLLayout.setVisibility(View.VISIBLE);
                break;


            case SAVE:
                mCameraFrameRecycleView.setVisibility(View.GONE);
                mCameraListRecycleView.setVisibility(View.GONE);
                cameraFilterView.setVisibility(View.GONE);
                cameraEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                cameraRecyclerLayout.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);

                saveImage();
                break;



            case EDIT:

                mCameraListRecycleView.setVisibility(View.GONE);
                cameraFilterView.setVisibility(View.GONE);
                cameraEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mCameraFrameRecycleView.setVisibility(View.GONE);
                cameraRecyclerLayout.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);


                editImage();

                mMyPrecfence.saveString(Constants.INTENT_TYPE, "CameraSelectedImage");
                try {

//                    Bitmap bitmap = Bitmap.createBitmap(mCameraRLayout.getWidth(), mCameraRLayout.getHeight(), Bitmap.Config.ARGB_8888);
//                    bitmap = ThumbnailUtils.extractThumbnail(bitmap, mCameraRLayout.getWidth(), mCameraRLayout.getHeight());

                    Uri uriCameraSelectedImage = Uri.fromFile(mImageFile);
                    Intent intentCameraSelectedImage = new Intent(CameraActivity.this, EditImageActivity.class);
                    intentCameraSelectedImage.putExtra(Constants.URI_COLLAGE_SELECTED_IMAGE, uriCameraSelectedImage);
                    startActivity(intentCameraSelectedImage);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }

    /**************Select Sub-Tolls  **********************/
    @Override
    public void onToolsRecyclerSelected(ToolsRecyclerType toolsRecyclerType) {
        switch (toolsRecyclerType) {

            case CROP:
                mCameraListRecycleView.setVisibility(View.GONE);
                cameraFilterView.setVisibility(View.GONE);
                cameraEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mCameraFrameRecycleView.setVisibility(View.GONE);
                cameraRecyclerLayout.setVisibility(View.VISIBLE);
                toolsRecyclerView.setVisibility(View.VISIBLE);

                Bitmap bitmap = ((BitmapDrawable)cameraSelectedImageShape.getDrawable()).getBitmap();
                if(bitmap!=null) {
                    cropImageUri(getImageUri(mContext, bitmap));
                }else if(bitmap==null) {
                    Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                }

                break;

            case ROTATION:
                mCameraListRecycleView.setVisibility(View.GONE);
                cameraFilterView.setVisibility(View.GONE);
                cameraEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mCameraFrameRecycleView.setVisibility(View.GONE);
                cameraRecyclerLayout.setVisibility(View.VISIBLE);
                toolsRecyclerView.setVisibility(View.VISIBLE);
                angle = angle - 10;
                cameraSelectedImageShape.setRotation(angle);
                break;

            case RT_LEFT:
                mCameraListRecycleView.setVisibility(View.GONE);
                cameraFilterView.setVisibility(View.GONE);
                cameraEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mCameraFrameRecycleView.setVisibility(View.GONE);
                cameraRecyclerLayout.setVisibility(View.VISIBLE);
                toolsRecyclerView.setVisibility(View.VISIBLE);

                angle = angle - 10;
                cameraSelectedImageShape.setRotation(angle);


//                mSelectedImageBitMap.postRotate(-12, cameraSelectedImageShape.getMeasuredWidth() / 2,
//                        cameraSelectedImageShape.getMeasuredHeight() / 2);
//                cameraSelectedImageShape.setImageMatrix(mMatrixShape);

                break;

            case RT_RIGHT:
                mCameraListRecycleView.setVisibility(View.GONE);
                cameraFilterView.setVisibility(View.GONE);
                cameraEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mCameraFrameRecycleView.setVisibility(View.GONE);
                cameraRecyclerLayout.setVisibility(View.VISIBLE);
                toolsRecyclerView.setVisibility(View.VISIBLE);

                angle = angle + 10;
                cameraSelectedImageShape.setRotation(angle);

//                mMatrixShape.postRotate(12, cameraSelectedImageShape.getMeasuredWidth() / 2,
//                        cameraSelectedImageShape.getMeasuredHeight() / 2);
//                cameraSelectedImageShape.setImageMatrix(mMatrixShape);

                break;

            case FLIP:

                mCameraListRecycleView.setVisibility(View.GONE);
                cameraFilterView.setVisibility(View.GONE);
                cameraEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mCameraFrameRecycleView.setVisibility(View.GONE);

                cameraRecyclerLayout.setVisibility(View.VISIBLE);
                toolsRecyclerView.setVisibility(View.VISIBLE);

                cameraSelectedImageShape.setImageBitmap(flipImage(((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap()));

//                Matrix matrix = new Matrix();
//                //matrix.postScale(-1.0f, 1.0f);
//                matrix.postScale(-1.0f, 1.0f, cameraSelectedImageShape.getWidth() / 2f,
//                        cameraSelectedImageShape.getHeight() / 2f);
//                Bitmap  flipImageBitMap = Bitmap.createBitmap(mSelectedImageBitMap, 0, 0, mSelectedImageBitMap.getWidth(), mSelectedImageBitMap.getHeight(), matrix, true);
//                cameraSelectedImageShape.setImageBitmap(flipImageBitMap);
//                mMatrixShape.postScale(-1.0f, 1.0f, cameraSelectedImageShape.getWidth() / 2f,
//                        cameraSelectedImageShape.getHeight() / 2f);
//                cameraSelectedImageShape.setImageMatrix(mMatrixShape);

                break;


        }
    }





    private void sendTakePictureIntent() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra( MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File pictureFile = null;
            try {
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                Toast.makeText(mContext,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mContext,
                        "com.mojodigi.selfiepro.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(cameraIntent, PICK_CAMERA_REQUEST);
            }
        }
    }

    private File getPictureFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        pictureFilePath = image.getAbsolutePath();
        return image;

    }


    private void bindDataToAdapter() {

        final Context context = this.getApplication();
        Handler handler = new Handler();

        Runnable r = new Runnable() {
            public void run() {
                Bitmap bitmapDrawable =  ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();

                //Bitmap thumbImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.photo), 640, 640, false);
                //Bitmap thumbImage = Bitmap.createScaledBitmap(bitmapDrawable,  bitmapDrawable.getWidth(),bitmapDrawable.getHeight() , false);

                //Bitmap thumbImage = Bitmap.createScaledBitmap(mSelectedImageBitMap,  700, 900, false);

             Bitmap thumbImage = Bitmap.createScaledBitmap(mSelectedImageBitMap,  cameraSelectedImageShape.getWidth(), cameraSelectedImageShape.getHeight() , false);
                //  Bitmap thumbImage = Bitmap.createScaledBitmap(mSelectedImageBitMap,  700, 900 , false);

                //Bitmap thumbImage =imageBitmap;
                //imageBitmapTosave = mSelectedImageBitMap;

                ThumbnailItem t1 = new ThumbnailItem();
                ThumbnailItem t2 = new ThumbnailItem();
                ThumbnailItem t3 = new ThumbnailItem();
                ThumbnailItem t4 = new ThumbnailItem();
                ThumbnailItem t5 = new ThumbnailItem();
                ThumbnailItem t6 = new ThumbnailItem();
                ThumbnailItem t7 = new ThumbnailItem();
                ThumbnailItem t8 = new ThumbnailItem();


                t1.image = thumbImage;
                t2.image = thumbImage;
                t3.image = thumbImage;
                t4.image = thumbImage;
                t5.image = thumbImage;
                t6.image = thumbImage;
                t7.image = thumbImage;
                t8.image = thumbImage;


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
        Bitmap bitmapDrawable =  ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();
        //cameraSelectedImageShape.setImageBitmap(filter.processFilter(((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap()));
        //cameraSelectedImageShape.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(bitmapDrawable, 700,900 ,false )));

        cameraSelectedImageShape.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(mSelectedImageBitMap, cameraSelectedImageShape.getWidth(), cameraSelectedImageShape.getHeight() ,false )));

        //cameraSelectedImageShape.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(mSelectedImageBitMap, 700, 900 ,false )));
        //imageBitmapTosave = filter.processFilter(Bitmap.createScaledBitmap(mSelectedImageBitMap, 700,900 ,false ));


//          Bitmap bitmapDrawable =  ((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap();
//        cameraSelectedImageShape.setImageBitmap(filter.processFilter(((BitmapDrawable) cameraSelectedImageShape.getDrawable()).getBitmap()));
//         cameraSelectedImageShape.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(bitmapDrawable, bitmapDrawable.getWidth(),bitmapDrawable.getHeight() ,false )));
//        //imageBitmapTosave = filter.processFilter(Bitmap.createScaledBitmap(mSelectedImageBitMap, 640,640 ,false ));





    }


    private String saveImage() {

        OutputStream output;
        Calendar cal = Calendar.getInstance();

        Bitmap bitmap = Bitmap.createBitmap(mCameraRLayout.getWidth(), mCameraRLayout.getHeight(), Bitmap.Config.ARGB_8888);

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, mCameraRLayout.getWidth(), mCameraRLayout.getHeight());

        Canvas mBitCanvas = new Canvas(bitmap);
        mCameraRLayout.draw(mBitCanvas);

        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath() + "/SelfiePro/");
        dir.mkdirs();

        mImageName = "SelfiePro" + cal.getTimeInMillis() + ".png";

        // Create a name for the saved image
        mImageFile = new File(dir, mImageName);
        runMediaScan( mContext, mImageFile);
        // Show a toast message on successful save
        Toast.makeText(CameraActivity.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

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

        Bitmap bitmap = Bitmap.createBitmap(mCameraRLayout.getWidth(), mCameraRLayout.getHeight(), Bitmap.Config.ARGB_8888);
        //Bitmap bitmap = Bitmap.createBitmap(cameraSelectedImageShape.getWidth(), cameraSelectedImageShape.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, mCameraRLayout.getWidth(), mCameraRLayout.getHeight());
        // bitmap = ThumbnailUtils.extractThumbnail(bitmap, cameraSelectedImageShape.getWidth(), cameraSelectedImageShape.getHeight());
        Canvas mBitCanvas = new Canvas(bitmap);
        mCameraRLayout.draw(mBitCanvas);
        //cameraSelectedImageShape.draw(mBitCanvas);

        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath() + "/SelfiePro/");
        dir.mkdirs();

        mImageName = "SelfiePro" + cal.getTimeInMillis() + ".png";

        // Create a name for the saved image
        mImageFile = new File(dir, mImageName);
        // runMediaScan( mContext, mImageFile);
        // Show a toast message on successful save
        //Toast.makeText(GallerySelectedActivity.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

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


    //Method to do flip action
    private Bitmap flipImage(Bitmap image_bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        Bitmap flipped_bitmap = Bitmap.createBitmap(image_bitmap, 0, 0, image_bitmap.getWidth(), image_bitmap.getHeight(), matrix, true);
        return flipped_bitmap;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //show_alert_back("Exit", "Are you sure you want to exit Editor ?");

        mMyPrecfence.saveString(Constants.COLLAGE_ACTIVITY , "false");
        // current activity
        finish();

        return super.onKeyDown(keyCode, event);

    }










    protected void cropImageUri(Uri picUri) {
        try {
            // Intent intent = new Intent("com.android.camera.action.CROP");
            Intent intent = new Intent("com.android.camera.action.CROP" , android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CameraActivity.this);
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if (cameraSelectedImageShape != null) {
            if (seekBar.getId() == R.id.cameraBrightness) {
                increaseBrightness(cameraSelectedImageShape, progress);
            }
            if (seekBar.getId() == R.id.cameraContrast) {
                increaseContrast(cameraSelectedImageShape, progress);
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






    private boolean imageSelectedOrNot(){
        imageSelectedOrNot = false;
        if (isSelectedImage) {
            return imageSelectedOrNot = true;
        }
        else   {
            return imageSelectedOrNot = false;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        show_alert_back("Exit", "Are you sure you want to exit Editor ?");
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
            Intent intent = new Intent(android.content.Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(android.content.Intent.createChooser(intent, "Select picture"), PICK_GALLARY_REQUEST);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
            startActivityForResult(android.content.Intent.createChooser(intent, "Select picture"), PICK_GALLARY_REQUEST);
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


}
