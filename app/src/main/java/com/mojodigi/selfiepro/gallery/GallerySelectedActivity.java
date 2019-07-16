package com.mojodigi.selfiepro.gallery;

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
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.ViewType;

public class GallerySelectedActivity extends AppCompatActivity implements OnPhotoEditorListener, View.OnTouchListener, View.OnClickListener,

        OnGalleryEditTollsSelected, SeekBar.OnSeekBarChangeListener, /*OnFrameSelected ,*/ ThumbnailCallback , OnToolsRecyclerSelected {

    private static final String TAG = GallerySelectedActivity.class.getSimpleName();

    private static final int PICK_GALLARY_REQUEST = 222;

    //These matrices will be used to move and zoom image
    private Matrix mMatrixShape   ;
    private Matrix  mMatrixSaved ;





    private Bitmap  mSelectedImageBitMap = null ;

    private Bitmap mBitmapRotate = null ;

    private ImageView    gallerySelectedImageShape ;

    private String mImageName ;

    private RelativeLayout mGalleryRLayout;

    private boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    private LinearLayout mGalleryEditBackLLayout, mGallerySaveLayout , galleryEffectsSeekbarLLayout , galleryRecyclerLayout ;

    private MyPreference mMyPrecfence = null;

    private RecyclerView mGalleryListRecycleView , mGalleryFrameRecycleView;

    private RecyclerView galleryEditToolsRecycleView , galleryFilterView;

    private GalleryEditToolsAdapter mGalleryEditToolsAdapter;
    private ToolsRecyclerAdapter toolsRecyclerAdapter;

    private String mIntentType = "";
    private Context mContext ;
    private  File mImageFile;

    private boolean imageSelectedOrNot = false;
    private boolean isSelectedImage = true;

    private SeekBar galleryBrightness , galleryContrast  ;
    private static final int CROP_IMAGE = 001;
    public static GallerySelectedActivity instance;
    private RecyclerView imageEffectsThumbnails , toolsRecyclerView;
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private  FrameLayout galleryframeShape;

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // Remember some things for zooming
    private PointF startPointF, midPointF;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery_selected);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        instance = this;

        if(mContext==null) {
            mContext = GallerySelectedActivity.this;
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

        initView();

        if (mIntentType.equalsIgnoreCase("IntentGallery")) {
            Uri myUri = null;
            Intent extrasIntent = getIntent();
            if (extrasIntent != null) {
                myUri = extrasIntent.getParcelableExtra("URI_PICK_GALLARY");
                //gallerySelectedImageShape.setScaleType(ImageView.ScaleType.FIT_START);
                gallerySelectedImageShape.setImageURI(myUri);
                //gallerySelectedImageShape.setRotation(1);
                //galleryframeShape.setBackgroundColor(getResources().getColor(R.color.white));

                imageSelectedOrNot = true;

            }
            try {
                mSelectedImageBitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), myUri);

                Matrix mat = new Matrix();
                if (mSelectedImageBitMap != null) {
                    mBitmapRotate = Bitmap.createBitmap(mSelectedImageBitMap, 0,0,
                            mSelectedImageBitMap.getWidth(), mSelectedImageBitMap.getHeight(), mat, true);
                 }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        mMatrixShape = new Matrix();

        mMatrixSaved = new Matrix();

        startPointF = new PointF();
        midPointF = new PointF();


        //FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(700, 900);
        layoutParams.leftMargin = -10;
        layoutParams.topMargin = -10;
        layoutParams.bottomMargin = -10;
        layoutParams.rightMargin = -10;


        imageEffectsThumbnails = (RecyclerView) findViewById(R.id.imageEffectsThumbnails);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        imageEffectsThumbnails.setLayoutManager(layoutManager);
        imageEffectsThumbnails.setHasFixedSize(true);

        mGalleryRLayout = (RelativeLayout) findViewById(R.id.idGalleryRLayout);
        galleryframeShape = (FrameLayout) findViewById(R.id.galleryframeShape);

        gallerySelectedImageShape = (ImageView) findViewById(R.id.gallerySelectedImageShape);
        gallerySelectedImageShape.setLayoutParams(layoutParams);
       // gallerySelectedImageShape.setOnClickListener(this);
        gallerySelectedImageShape.setOnTouchListener(this);

        mGalleryEditToolsAdapter = new GalleryEditToolsAdapter(this);
        toolsRecyclerAdapter = new ToolsRecyclerAdapter(this);



        mGalleryEditBackLLayout = (LinearLayout) findViewById(R.id.idGalleryEditBackLLayout);
        mGallerySaveLayout = (LinearLayout) findViewById(R.id.idGallerySaveLayout);


        galleryRecyclerLayout = (LinearLayout) findViewById(R.id.galleryRecyclerLayout);
        galleryRecyclerLayout.setVisibility(View.GONE);

        galleryEffectsSeekbarLLayout = (LinearLayout) findViewById(R.id.galleryEffectsSeekbarLLayout);
        galleryEffectsSeekbarLLayout.setOnClickListener(this);
        mGalleryEditBackLLayout.setOnClickListener(this);
        mGallerySaveLayout.setOnClickListener(this);

        galleryBrightness = (SeekBar)findViewById(R.id.galleryBrightness);
        galleryBrightness.setOnSeekBarChangeListener(this);

        galleryContrast = (SeekBar)findViewById(R.id.galleryContrast);
        galleryContrast.setOnSeekBarChangeListener(this);

        mGalleryListRecycleView = (RecyclerView) findViewById(R.id.idGalleryListRecycleView);
        mGalleryFrameRecycleView = (RecyclerView) findViewById(R.id.idGalleryFrameRecycleView);
        mGalleryListRecycleView.setVisibility(View.GONE);
        mGalleryFrameRecycleView.setVisibility(View.GONE);


        galleryEditToolsRecycleView = (RecyclerView) findViewById(R.id.galleryEditToolsRecycleView);
        galleryEditToolsRecycleView.setVisibility(View.VISIBLE);

        LinearLayoutManager mCollageEditToolsLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        galleryEditToolsRecycleView.setLayoutManager(mCollageEditToolsLManager);
        galleryEditToolsRecycleView.setAdapter(mGalleryEditToolsAdapter);


        galleryFilterView = (RecyclerView) findViewById(R.id.galleryFilterView);
        galleryFilterView.setVisibility(View.GONE);


        toolsRecyclerView = (RecyclerView) findViewById(R.id.toolsRecyclerView);
        toolsRecyclerView.setVisibility(View.GONE);


        LinearLayoutManager mtoolsRecyclerAdapterLManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        toolsRecyclerView.setLayoutManager(mtoolsRecyclerAdapterLManager);
        toolsRecyclerView.setAdapter(toolsRecyclerAdapter);

        //bindDataToAdapter();

    }


    @Override
    protected void onResume() {
        super.onResume();
        galleryEffectsSeekbarLLayout.setVisibility(View.GONE);
        //galleryBrightness.setVisibility(View.GONE);
        mGalleryFrameRecycleView.setVisibility(View.GONE);
        mGalleryListRecycleView.setVisibility(View.GONE);
        imageEffectsThumbnails.setVisibility(View.GONE);
        galleryRecyclerLayout.setVisibility(View.GONE);
        toolsRecyclerView.setVisibility(View.GONE);

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final ImageView view = (ImageView) v;

        if (view == gallerySelectedImageShape) {
            gallerySelectedImageShape.setScaleType(ImageView.ScaleType.MATRIX);
            //selectedImageView = gallerySelectedImageShape ;
            isSelectedImage = true;

            if (galleryframeShape != null)
                //galleryframeShape.setBackgroundColor(getResources().getColor(R.color.white));
            //gallerySelectedImageShape.setBackgroundColor(Color.parseColor("#ffffff"));

             ((BitmapDrawable) view.getDrawable()).setAntiAlias(true);
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

//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        ImageView view = (ImageView) v;
//        //SvgImageView svgView = (SvgImageView) v;
//        if (view == gallerySelectedImageShape) {
//            gallerySelectedImageShape.setScaleType(ImageView.ScaleType.MATRIX);
//            //selectedImageView = gallerySelectedImageShape ;
//            isSelectedImage = true;
//            if(galleryframeShape!=null)
//                galleryframeShape.setBackgroundColor(getResources().getColor(R.color.color_shape_selected));
//
//            switch (event.getAction() & MotionEvent.ACTION_MASK) {
//
//                case MotionEvent.ACTION_DOWN:
//                    mMatrixSaved.set(mMatrixShape);
//                    startPointF.set(event.getX(), event.getY());
//                    mode = DRAG;
//                    break;
//
//
//                case MotionEvent.ACTION_POINTER_DOWN:
//                    oldDist = spacing(event);
//                    if (oldDist > 10f) {
//                        mMatrixSaved.set(mMatrixShape);
//                        midPoint(midPointF, event);
//                        mode = ZOOM;
//                    }
//                    break;
//
//                case MotionEvent.ACTION_UP:
//
//
//                case MotionEvent.ACTION_POINTER_UP:
//                    mode = NONE;
//                    break;
//
//
//                case MotionEvent.ACTION_MOVE:
//                    if (mode == DRAG) {
//                        mMatrixShape.set(mMatrixSaved);
//                        // mMatrixShape.postTranslate(gallerySelectedImageShape.getWidth()/2, gallerySelectedImageShape.getHeight()/2);
//                        mMatrixShape.postTranslate(event.getX() - startPointF.x, event.getY()
//                                - startPointF.y);
//
//                    } else if (mode == ZOOM) {
//                        float newDist = spacing(event);
//                        if (newDist > 10f) {
//                            mMatrixShape.set(mMatrixSaved);
//                            float scale = newDist / oldDist;
//                            //mMatrixShape.postScale(scale, scale,gallerySelectedImageShape.getWidth()/2, gallerySelectedImageShape.getHeight()/2);
//                            mMatrixShape.postScale(scale, scale, midPointF.x, midPointF.y);
//                        }
//                    }
//                    break;
//            }
//            view.setImageMatrix(mMatrixShape);
//        }
//        return true;
//    }
//
//    /**
//     * Determine the space between the first two fingers
//     */
//    private float spacing(MotionEvent event) {
//        float x = event.getX(0) - event.getX(1);
//        float y = event.getY(0) - event.getY(1);
//        return (float) Math.sqrt(x * x + y * y);
//    }
//
//    /**
//     * Calculate the midPointF point of the first two fingers
//     */
//    private void midPoint(PointF point, MotionEvent event) {
//        float x = event.getX(0) + event.getX(1);
//        float y = event.getY(0) + event.getY(1);
//        point.set(x / 2, y / 2);
//    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.idCollageSaveLayout:
                saveImage();
                break;

            case R.id.gallerySelectedImageShape:

                openGallery(PICK_GALLARY_REQUEST);

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

                        if (isSelectedImage) {
                            gallerySelectedImageShape.setImageBitmap(mCropedBitmap);
                        }
                    }
                }
                break;

            case PICK_GALLARY_REQUEST:

                if (resultCode == RESULT_OK && null != data) {
                    mSelectedImageBitMap = null;
                    Uri selectedImageUri = data.getData();

                    if (selectedImageUri!=null) {
                        gallerySelectedImageShape.setImageURI(selectedImageUri);
                        gallerySelectedImageShape.setBackgroundColor(Color.parseColor("#ffffff"));

                        try {
                            mSelectedImageBitMap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            Matrix mat = new Matrix();
                            if (mSelectedImageBitMap != null) {
                                mBitmapRotate = Bitmap.createBitmap(mSelectedImageBitMap, 0,0,
                                        mSelectedImageBitMap.getWidth(), mSelectedImageBitMap.getHeight(), mat, true);
                            }
                            imageSelectedOrNot = true;
                            //gallerySelectedImageShape.setOnTouchListener(this);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }







    /**************Select Tolls  **********************/

    @Override
    public void onToolsRecyclerSelected(ToolsRecyclerType toolsRecyclerType) {
        switch (toolsRecyclerType) {
            case CROP:
                mGalleryListRecycleView.setVisibility(View.GONE);
                galleryFilterView.setVisibility(View.GONE);
                galleryEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mGalleryFrameRecycleView.setVisibility(View.GONE);
                galleryRecyclerLayout.setVisibility(View.VISIBLE);
                toolsRecyclerView.setVisibility(View.VISIBLE);


                Bitmap bitmap = ((BitmapDrawable)gallerySelectedImageShape.getDrawable()).getBitmap();
                if(bitmap!=null) {
                    cropImageUri(getImageUri(mContext, bitmap));
                }else if(bitmap==null) {
                    Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                }
                break;

            case ROTATION:
                mGalleryListRecycleView.setVisibility(View.GONE);
                galleryFilterView.setVisibility(View.GONE);
                galleryEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mGalleryFrameRecycleView.setVisibility(View.GONE);
                galleryRecyclerLayout.setVisibility(View.VISIBLE);
                toolsRecyclerView.setVisibility(View.VISIBLE);

                angle = angle + 10;
                gallerySelectedImageShape.setRotation(angle);
                break;

            case RT_LEFT:
                mGalleryListRecycleView.setVisibility(View.GONE);
                galleryFilterView.setVisibility(View.GONE);
                galleryEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mGalleryFrameRecycleView.setVisibility(View.GONE);
                galleryRecyclerLayout.setVisibility(View.VISIBLE);
                toolsRecyclerView.setVisibility(View.VISIBLE);



                mMatrixShape.postRotate(-12, gallerySelectedImageShape.getMeasuredWidth() / 2,
                        gallerySelectedImageShape.getMeasuredHeight() / 2);
                gallerySelectedImageShape.setImageMatrix(mMatrixShape);
                break;

            case RT_RIGHT:
                mGalleryListRecycleView.setVisibility(View.GONE);
                galleryFilterView.setVisibility(View.GONE);
                galleryEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mGalleryFrameRecycleView.setVisibility(View.GONE);
                galleryRecyclerLayout.setVisibility(View.VISIBLE);
                toolsRecyclerView.setVisibility(View.VISIBLE);

                mMatrixShape.postRotate(12, gallerySelectedImageShape.getMeasuredWidth() / 2,
                        gallerySelectedImageShape.getMeasuredHeight() / 2);
                gallerySelectedImageShape.setImageMatrix(mMatrixShape);

                break;

            case FLIP:

                mGalleryListRecycleView.setVisibility(View.GONE);
                galleryFilterView.setVisibility(View.GONE);
                galleryEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mGalleryFrameRecycleView.setVisibility(View.GONE);

                galleryRecyclerLayout.setVisibility(View.VISIBLE);
                toolsRecyclerView.setVisibility(View.VISIBLE);

                mMatrixShape.postScale(-1.0f, 1.0f, gallerySelectedImageShape.getWidth() / 2f,
                        gallerySelectedImageShape.getHeight() / 2f);
                gallerySelectedImageShape.setImageMatrix(mMatrixShape);
                break;


        }
    }

    @Override
    public void onGalleryEditTollsSelected(GalleryEditToolsType galleryEditToolsType) {
        switch (galleryEditToolsType) {

            case TOOLS:
                gallerySelectedImageShape.setScaleType(ImageView.ScaleType.MATRIX);
                mGalleryListRecycleView.setVisibility(View.GONE);
                galleryFilterView.setVisibility(View.GONE);
                galleryEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mGalleryFrameRecycleView.setVisibility(View.GONE);
                galleryRecyclerLayout.setVisibility(View.VISIBLE);
                toolsRecyclerView.setVisibility(View.VISIBLE);
                break;


            case EFFECTS:

                bindDataToAdapter();

                mGalleryFrameRecycleView.setVisibility(View.GONE);
                mGalleryListRecycleView.setVisibility(View.GONE);
                galleryEffectsSeekbarLLayout.setVisibility(View.GONE);
                galleryFilterView.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                galleryRecyclerLayout.setVisibility(View.VISIBLE);
                imageEffectsThumbnails.setVisibility(View.VISIBLE);

                break;

            case FRAME:
                mGalleryListRecycleView.setVisibility(View.GONE);
                galleryFilterView.setVisibility(View.GONE);
                galleryEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                galleryRecyclerLayout.setVisibility(View.VISIBLE);
                mGalleryFrameRecycleView.setVisibility(View.VISIBLE);
                break;



            case ADJUST:
                mGalleryFrameRecycleView.setVisibility(View.GONE);
                mGalleryListRecycleView.setVisibility(View.GONE);
                galleryFilterView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);
                galleryRecyclerLayout.setVisibility(View.VISIBLE);
                galleryEffectsSeekbarLLayout.setVisibility(View.VISIBLE);
                break;


            case SAVE:
                mGalleryFrameRecycleView.setVisibility(View.GONE);
                mGalleryListRecycleView.setVisibility(View.GONE);
                galleryFilterView.setVisibility(View.GONE);
                galleryEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                galleryRecyclerLayout.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);

                saveImage();
                break;

            case GALLERY:

                mGalleryFrameRecycleView.setVisibility(View.GONE);
                mGalleryListRecycleView.setVisibility(View.GONE);
                galleryFilterView.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                galleryRecyclerLayout.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);

                imageSelectedOrNot =  imageSelectedOrNot();
                if(!imageSelectedOrNot){
                    Toast.makeText(this , "Please selected image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isSelectedImage) {
                    openGallery(PICK_GALLARY_REQUEST);
                }
                break;


            case EDIT:

                if(galleryframeShape!=null)
                    galleryframeShape.setBackgroundColor(getResources().getColor(R.color.white));


                mGalleryListRecycleView.setVisibility(View.GONE);
                galleryFilterView.setVisibility(View.GONE);
                //galleryBrightness.setVisibility(View.GONE);
                galleryEffectsSeekbarLLayout.setVisibility(View.GONE);
                imageEffectsThumbnails.setVisibility(View.GONE);
                mGalleryFrameRecycleView.setVisibility(View.GONE);
                galleryRecyclerLayout.setVisibility(View.GONE);
                toolsRecyclerView.setVisibility(View.GONE);


                editImage();

                mMyPrecfence.saveString(Constants.INTENT_TYPE, "CollageSelectedImage");
                //mMyPrecfence.saveString(Constants.INTENT_TYPE, "IntentGallery");
                try {

//                    Bitmap bitmap = Bitmap.createBitmap(mGalleryRLayout.getWidth(), mGalleryRLayout.getHeight(), Bitmap.Config.ARGB_8888);
//                    bitmap = ThumbnailUtils.extractThumbnail(bitmap, mGalleryRLayout.getWidth(), mGalleryRLayout.getHeight());

                    Uri uriCollageSelectedImage = Uri.fromFile(mImageFile);
                    Intent intentCollageSelectedImage = new Intent(GallerySelectedActivity.this, EditImageActivity.class);
                    intentCollageSelectedImage.putExtra(Constants.URI_COLLAGE_SELECTED_IMAGE, uriCollageSelectedImage);

                    startActivity(intentCollageSelectedImage);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }



    private void bindDataToAdapter() {
        final Context context = this.getApplication();
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                //Bitmap thumbImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.photo), 640, 640, false);
                Bitmap thumbImage = Bitmap.createScaledBitmap(mSelectedImageBitMap,  gallerySelectedImageShape.getWidth(),gallerySelectedImageShape.getHeight() , false);
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


                //

                //

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
        gallerySelectedImageShape.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(mSelectedImageBitMap, gallerySelectedImageShape.getWidth(),gallerySelectedImageShape.getHeight() ,false )));
        //imageBitmapTosave = filter.processFilter(Bitmap.createScaledBitmap(mSelectedImageBitMap, 640,640 ,false ));

    }


    private String saveImage() {

        OutputStream output;
        Calendar cal = Calendar.getInstance();

        Bitmap bitmap = Bitmap.createBitmap(mGalleryRLayout.getWidth(), mGalleryRLayout.getHeight(), Bitmap.Config.ARGB_8888);

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, mGalleryRLayout.getWidth(), mGalleryRLayout.getHeight());

        Canvas mBitCanvas = new Canvas(bitmap);
        mGalleryRLayout.draw(mBitCanvas);

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
        Toast.makeText(GallerySelectedActivity.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

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

        Bitmap bitmap = Bitmap.createBitmap(mGalleryRLayout.getWidth(), mGalleryRLayout.getHeight(), Bitmap.Config.ARGB_8888);
        //Bitmap bitmap = Bitmap.createBitmap(gallerySelectedImageShape.getWidth(), gallerySelectedImageShape.getHeight(), Bitmap.Config.ARGB_8888);

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, mGalleryRLayout.getWidth(), mGalleryRLayout.getHeight());
        // bitmap = ThumbnailUtils.extractThumbnail(bitmap, gallerySelectedImageShape.getWidth(), gallerySelectedImageShape.getHeight());

        Canvas mBitCanvas = new Canvas(bitmap);
       mGalleryRLayout.draw(mBitCanvas);
        // gallerySelectedImageShape.draw(mBitCanvas);

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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GallerySelectedActivity.this);
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
        if (gallerySelectedImageShape != null) {
            if (seekBar.getId() == R.id.galleryBrightness) {
                increaseBrightness(gallerySelectedImageShape, progress);
            }
            if (seekBar.getId() == R.id.galleryContrast) {
                increaseContrast(gallerySelectedImageShape, progress);
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
    private void increaseSaturation(ImageView mImageView, int progressValue){
        mImageView.setColorFilter(ColorFilterGenerator.adjustSaturation(progressValue));
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




    //@Override
    // public void onFilterSelected(PhotoFilter photoFilter) {
    // mPhotoEditor.setFilterEffect(photoFilter);

//        if (isSelectedImage) {
//            Toast.makeText(this , "isSelectedImage selected.", Toast.LENGTH_SHORT).show();
//        }


    //  }

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
