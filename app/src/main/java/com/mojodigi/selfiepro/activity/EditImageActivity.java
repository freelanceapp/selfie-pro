package com.mojodigi.selfiepro.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mojodigi.selfiepro.R;
import com.mojodigi.selfiepro.adapter.BackgroundAdapter;
import com.mojodigi.selfiepro.adapter.FrameRecycleAdapter;
import com.mojodigi.selfiepro.adapter.StickersListRecyclerAdapter;
import com.mojodigi.selfiepro.base.BaseActivity;
import com.mojodigi.selfiepro.enums.BackgroundType;
import com.mojodigi.selfiepro.enums.FrameType;
import com.mojodigi.selfiepro.enums.StickersRecyclerType;
import com.mojodigi.selfiepro.filters.FilterListener;
import com.mojodigi.selfiepro.filters.FilterViewAdapter;
import com.mojodigi.selfiepro.fragment.EmojiFragment;
import com.mojodigi.selfiepro.fragment.PropertiesFragment;
import com.mojodigi.selfiepro.fragment.StickerEightFragment;
import com.mojodigi.selfiepro.fragment.StickerFiveFragment;
import com.mojodigi.selfiepro.fragment.StickerFourFragment;
import com.mojodigi.selfiepro.fragment.StickerFragment;
import com.mojodigi.selfiepro.fragment.StickerNineFragment;
import com.mojodigi.selfiepro.fragment.StickerOneFragment;
import com.mojodigi.selfiepro.fragment.StickerSevenFragment;
import com.mojodigi.selfiepro.fragment.StickerSixFragment;
import com.mojodigi.selfiepro.fragment.StickerThreeFragment;
import com.mojodigi.selfiepro.fragment.StickerTwoFragment;
import com.mojodigi.selfiepro.fragment.TextEditorFragment;
import com.mojodigi.selfiepro.interfaces.BackgroundListener;
import com.mojodigi.selfiepro.interfaces.OnFrameSelected;
import com.mojodigi.selfiepro.interfaces.OnItemSelected;
import com.mojodigi.selfiepro.interfaces.OnStickersRecyclerSelected;
import com.mojodigi.selfiepro.tools.EditingToolsAdapter;
import com.mojodigi.selfiepro.tools.ToolType;
import com.mojodigi.selfiepro.utils.Constants;
import com.mojodigi.selfiepro.utils.MyPreference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.ViewType;


public class EditImageActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener, PropertiesFragment.Properties, EmojiFragment.EmojiListener,
        StickerFragment.StickerListener,
        StickerOneFragment.StickerOneListener,  StickerTwoFragment.StickerTwoListener,
        StickerThreeFragment.StickerThreeListener,StickerFourFragment.StickerFourListener, StickerFiveFragment.StickerFiveListener ,
        StickerSixFragment.StickerSixListener , StickerSevenFragment.StickerSevenListener , StickerEightFragment.StickerEightListener
         , StickerNineFragment.StickerNineListener,
        OnItemSelected, FilterListener  , OnFrameSelected , BackgroundListener , OnStickersRecyclerSelected /* , View.OnTouchListener*/ {

    private static final String TAG = EditImageActivity.class.getSimpleName();


    private static final int CROP_IMAGE = 0;
    private static final int PICK_CAMERA_REQUEST = 1;
    private static final int PICK_GALLARY_REQUEST = 2;

    private Context mContext;
    private PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;

    private PropertiesFragment mPropertiesFragment;
    private EmojiFragment mEmojiFragment;
    private StickerFragment mStickerFragment;
    private StickerOneFragment mStickerOneFragment;
    private StickerTwoFragment mStickerTwoFragment;
    private StickerThreeFragment mStickerThreeFragment;
    private StickerFourFragment mStickerFourFragment;
    private StickerFiveFragment mStickerFiveFragment;
    private StickerSixFragment mStickerSixFragment;
    private StickerSevenFragment mStickerSevenFragment;
    private StickerEightFragment mStickerEightFragment;
    private StickerNineFragment mStickerNineFragment;

    // private TextView mTxtCurrentTool;

    private Typeface mWonderFont;

    private RecyclerView mRvTools, mRvFilters , stickersListRecycleView;

    private EditingToolsAdapter mEditingToolsAdapter  ;
    private FilterViewAdapter mFilterViewAdapter  ;
    private StickersListRecyclerAdapter stickersListRecyclerAdapter  ;
    private FrameRecycleAdapter mFrameRecycleAdapter  ;
    private BackgroundAdapter mBackgroundAdapter  ;

    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet  ;
    private boolean mIsFilterVisible;

    private ImageView   mMainFrameImageView  /*, backEditImage,  imgUndo, imgRedo, imgCamera, imgGallery,  imgClose ,imgSave */ ;

    private LinearLayout editImageBackLLayout , editIamgeUndoLayout , editImageRedoLayout , editIamgeSaveLayout ;

    private boolean isKitKat ;

    private MyPreference mMyPrecfence = null;

    private String mIntentType = "";
    private Typeface mEmojiTypeFace ;

    private Bitmap mSelectedCropImage = null ;
    private RelativeLayout mEditMainRLayout  ;
    private LinearLayout editImageSubRecyclerLayout  ;

    private File mImageFile;
    private String mImageName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(mContext==null){
            mContext =  EditImageActivity.this;
        }

        makeFullScreen();

        setContentView(R.layout.activity_edit_image);

        initViews();
    }


    private void initViews() {

        isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        mConstraintSet = new ConstraintSet();

        mEditingToolsAdapter = new EditingToolsAdapter(this);
        mFilterViewAdapter = new FilterViewAdapter(this);
        stickersListRecyclerAdapter = new StickersListRecyclerAdapter(this);
        mFrameRecycleAdapter = new FrameRecycleAdapter(this);
        mBackgroundAdapter = new BackgroundAdapter(this);


        if (mMyPrecfence == null) {
            mMyPrecfence = MyPreference.getMyPreferenceInstance(EditImageActivity.this);
        }

        try{
            mMyPrecfence.saveString(Constants.COLLAGE_ACTIVITY , "false");
            mIntentType = mMyPrecfence.getString(Constants.INTENT_TYPE);
        }catch (Exception ex){
            ex.getStackTrace();
        }



        mEditMainRLayout = findViewById(R.id.idEditMainRLayout);
        editImageSubRecyclerLayout = findViewById(R.id.editImageSubRecyclerLayout);

        mPhotoEditorView = findViewById(R.id.photoEditorView);


        //mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRvTools = findViewById(R.id.rvConstraintTools);

        stickersListRecycleView = findViewById(R.id.stickersListRecycleView);
        mRvFilters = findViewById(R.id.rvFilterView);

        mRootView = findViewById(R.id.rootView);

        editImageBackLLayout = findViewById(R.id.editImageBackLLayout);
        editImageBackLLayout.setOnClickListener(this);

        editIamgeUndoLayout = findViewById(R.id.editIamgeUndoLayout);
        editIamgeUndoLayout.setOnClickListener(this);

        editImageRedoLayout = findViewById(R.id.editImageRedoLayout);
        editImageRedoLayout.setOnClickListener(this);

//        imgSave = findViewById(R.id.imgSave);
//        imgSave.setOnClickListener(this);

        editIamgeSaveLayout = findViewById(R.id.editIamgeSaveLayout);
        editIamgeSaveLayout.setOnClickListener(this);


        mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");

        mPropertiesFragment = new PropertiesFragment();
        mEmojiFragment = new EmojiFragment();
        mStickerFragment = new StickerFragment();
        mStickerOneFragment = new StickerOneFragment();
        mStickerTwoFragment = new StickerTwoFragment();
        mStickerThreeFragment = new StickerThreeFragment();
        mStickerFourFragment = new StickerFourFragment();
        mStickerFiveFragment = new  StickerFiveFragment();
        mStickerSixFragment = new  StickerSixFragment();
        mStickerSevenFragment = new  StickerSevenFragment();
        mStickerEightFragment = new StickerEightFragment();
        mStickerNineFragment = new StickerNineFragment();

        /*Interface Listener*/
        mStickerFragment.setStickerListener(this);
        mStickerOneFragment.setStickerOneListener(this);
        mStickerTwoFragment.setStickerTwoListener(this);
        mStickerThreeFragment.setStickerThreeListener(this);
        mStickerFourFragment.setStickerFourListener(this);
        mStickerFiveFragment.setStickerFiveListener(this);
        mStickerSixFragment.setStickerSixListener(this);
        mStickerSevenFragment.setStickerSevenListener(this);
        mStickerEightFragment.setStickerEightListener(this);
        mStickerNineFragment.setStickerNineListener(this);

        mEmojiFragment.setEmojiListener(this);
        mPropertiesFragment.setPropertiesChangeListener(this);

        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager llmStickers = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        stickersListRecycleView.setLayoutManager(llmStickers);
        stickersListRecycleView.setAdapter(stickersListRecyclerAdapter);


//        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        mRvFilters.setLayoutManager(llmFilters);
//        mRvFilters.setAdapter(mFilterViewAdapter);


        //Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        //mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

        //Set Image Dynamically
        // mPhotoEditorView.getSource().setImageResource(R.drawable.color_palette);

//        if (mIntentType.equalsIgnoreCase("IntentCamera")) {
//            mPhotoEditor.clearAllViews();
//            Intent extrasIntentCamera = getIntent();
//            if (extrasIntentCamera != null) {
//                Bitmap photoCameraBitmap = (Bitmap) this.getIntent().getParcelableExtra("BITMAP_PICK_CAMERA");
//                mPhotoEditorView.getSource().setImageBitmap(photoCameraBitmap);
//            }
//        }

        if (mIntentType.equalsIgnoreCase("CameraSelectedImage")) {
            mPhotoEditor.clearAllViews();
            Uri mUriCollageSelectedImage = null;
            Intent extrasIntent = getIntent();
            if (extrasIntent != null) {
                mUriCollageSelectedImage = extrasIntent.getParcelableExtra(Constants.URI_COLLAGE_SELECTED_IMAGE);
            }
            try {
                Bitmap bitmapPhotoGallary = MediaStore.Images.Media.getBitmap(getContentResolver(), mUriCollageSelectedImage);
                mPhotoEditorView.getSource().setImageBitmap(bitmapPhotoGallary);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        if (mIntentType.equalsIgnoreCase("IntentGallery")) {
//            mPhotoEditor.clearAllViews();
//            Uri myUri = null;
//            Intent extrasIntent = getIntent();
//            if (extrasIntent != null) {
//                myUri = extrasIntent.getParcelableExtra("URI_PICK_GALLARY");
//            }
//            try {
//                Bitmap bitmapPhotoGallary = MediaStore.Images.Media.getBitmap(getContentResolver(), myUri);
//                mPhotoEditorView.getSource().setImageBitmap(bitmapPhotoGallary);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        if (mIntentType.equalsIgnoreCase("GallerySelectedImage")) {
            mPhotoEditor.clearAllViews();
            Uri mUriCollageSelectedImage = null;
            Intent extrasIntent = getIntent();
            if (extrasIntent != null) {
                mUriCollageSelectedImage = extrasIntent.getParcelableExtra(Constants.URI_COLLAGE_SELECTED_IMAGE);
            }
            try {
                Bitmap bitmapPhotoGallary = MediaStore.Images.Media.getBitmap(getContentResolver(), mUriCollageSelectedImage);
                mPhotoEditorView.getSource().setImageBitmap(bitmapPhotoGallary);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (mIntentType.equalsIgnoreCase("CollageSelectedImage")) {
            mPhotoEditor.clearAllViews();
            Uri mUriCollageSelectedImage = null;
            Intent extrasIntent = getIntent();
            if (extrasIntent != null) {
                mUriCollageSelectedImage = extrasIntent.getParcelableExtra(Constants.URI_COLLAGE_SELECTED_IMAGE);
            }
            try {
                Bitmap bitmapPhotoGallary = MediaStore.Images.Media.getBitmap(getContentResolver(), mUriCollageSelectedImage);
                mPhotoEditorView.getSource().setImageBitmap(bitmapPhotoGallary);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mMainFrameImageView = (ImageView) findViewById(R.id.idMainFrameImageView);
    }






    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.editImageBackLLayout:
                onBackPressed();
                break;

            case R.id.editIamgeUndoLayout:
                mPhotoEditor.undo();
                break;

            //case R.id.imgRedo:
            case R.id.editImageRedoLayout:
                mPhotoEditor.redo();
                break;

            //case R.id.imgSave:
            case R.id.editIamgeSaveLayout:
                //saveImage();

                captureImage();

                break;

        }
    }







    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {

            case CAMERA:
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, PICK_CAMERA_REQUEST);
                break;

            case GALLERY:
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                openGallery();
                break;

            case EFFECTS:
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                // mTxtCurrentTool.setText(R.string.label_filter);
                LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                mRvFilters.setLayoutManager(llmFilters);
                mRvFilters.setAdapter(mFilterViewAdapter);
                showFilter(true);
                break;


            case FRAME:
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                LinearLayoutManager llmFramess = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                mRvFilters.setLayoutManager(llmFramess);
                mRvFilters.setAdapter(mFrameRecycleAdapter);
                showFilter(true);
                break;

            case BACKGROUND:
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                LinearLayoutManager llmBackground = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                mRvFilters.setLayoutManager(llmBackground);
                mRvFilters.setAdapter(mBackgroundAdapter);
                showFilter(true);
                break;


            case CROP:
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                // Bitmap bitmap = mSelectedCropImage;
                Bitmap bitmap = ((BitmapDrawable)mPhotoEditorView.getSource().getDrawable()).getBitmap();
                if(bitmap!=null) {
                    cropImageUri(getImageUri(mContext, bitmap));
                }else if(bitmap==null) {
                    Toast.makeText(this , "You have not selected image.", Toast.LENGTH_SHORT).show();
                }
                break;

            case BRUSH:
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                if(mPropertiesFragment.isAdded())
                {
                    return;
                }
                mPhotoEditor.setBrushDrawingMode(true);
                //mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesFragment.show(getSupportFragmentManager(), mPropertiesFragment.getTag());
                break;


            case TEXT:
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                TextEditorFragment textEditorDialogFragment = TextEditorFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        mPhotoEditor.addText(inputText, colorCode);
                        //mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;

            case ERASER:
                editImageSubRecyclerLayout.setVisibility(View.GONE);

                mPhotoEditor.brushEraser();
                // mTxtCurrentTool.setText(R.string.label_eraser);
                break;


            case EMOJI:
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                if(mEmojiFragment.isAdded())
                {
                    return;
                }
                mEmojiFragment.show(getSupportFragmentManager(), mEmojiFragment.getTag());
                break;

            case STICKERS:

                editImageSubRecyclerLayout.setVisibility(View.VISIBLE);


//                if(mStickerFragment.isAdded())
//                {
//                    return;
//                }
//                mStickerFragment.show(getSupportFragmentManager(), mStickerFragment.getTag());



                break;

        }
    }






    @Override
    public void onFrameSelected(int frameID, FrameType frameType) {
        switch (frameType) {
            case F_NONE:
                mMainFrameImageView.setBackgroundResource(frameID);
                break;
            case F_ONE:
                mMainFrameImageView.setBackgroundResource(R.drawable.frame_1);
                break;
            case F_TWO:
                mMainFrameImageView.setBackgroundResource(R.drawable.frame_2);
                break;
            case F_THREE:
                mMainFrameImageView.setBackgroundResource(R.drawable.frame_3);

                break;
            case F_FOUR:
                mMainFrameImageView.setBackgroundResource(R.drawable.frame_4);
                break;
            case F_FIVE:
                mMainFrameImageView.setBackgroundResource(R.drawable.frame_5);
                break;
            case F_SIX:
                mMainFrameImageView.setBackgroundResource(R.drawable.frame_6);
                break;
            case F_SEVEN:
                mMainFrameImageView.setBackgroundResource(R.drawable.frame_7);
                break;
            case F_EIGHT:
                mMainFrameImageView.setBackgroundResource(R.drawable.frame_8);
                break;
            case F_NINE:
                mMainFrameImageView.setBackgroundResource(R.drawable.frame_9);
                break;

            case F_TEN:
                mMainFrameImageView.setBackgroundResource(R.drawable.frame_10);
                break;

            case F_ELEVEN:
                mMainFrameImageView.setBackgroundResource(R.drawable.frame_11);
                break;

            case F_TWELVE:
                mMainFrameImageView.setBackgroundResource(R.drawable.frame_12);
                //Toast.makeText(this , "Frame 0." , Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /********************************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case CROP_IMAGE:
                    try {
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;


                case PICK_CAMERA_REQUEST:
                    mPhotoEditor.clearAllViews();

                    Bitmap cameraBitmap = (Bitmap) data.getExtras().get("data");
                    mSelectedCropImage = cameraBitmap ;
                    mPhotoEditorView.getSource().setImageBitmap(cameraBitmap);
                    break;

                case PICK_GALLARY_REQUEST:
                    try {
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap galleryBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        mSelectedCropImage = galleryBitmap ;
                        mPhotoEditorView.getSource().setImageBitmap(galleryBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    }


    /***********************************************************************************************/

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorFragment textEditorDialogFragment =
                TextEditorFragment.show(this, text, colorCode);

        textEditorDialogFragment.setOnTextEditorListener(new TextEditorFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                mPhotoEditor.editText(rootView, inputText, colorCode);
                //mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }



    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }


    /******************************************************************************************************/


    @SuppressLint("MissingPermission")
    private void saveImage() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            showLoading("Saving...");

            Calendar cal = Calendar.getInstance();
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SelfiePro/"
                    + File.separator + "SelfiePro"+cal.getTimeInMillis()+ ".png");
            try {
                file.createNewFile();

                SaveSettings saveSettings = new SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build();

                mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {

                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        hideLoading();
                        //showSnackbar("Image Saved Successfully");

                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));

                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hideLoading();
                        showSnackbar("Failed to save Image");
                    }
                });



            } catch (IOException e) {
                e.printStackTrace();
                hideLoading();
                showSnackbar(e.getMessage());
            }
        }
    }




    private String captureImage() {

        OutputStream output;
        Calendar cal = Calendar.getInstance();

        Bitmap bitmap = Bitmap.createBitmap(mEditMainRLayout.getWidth(), mEditMainRLayout.getHeight(), Bitmap.Config.ARGB_8888);

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, mEditMainRLayout.getWidth(), mEditMainRLayout.getHeight());

        Canvas mBitCanvas = new Canvas(bitmap);
        mEditMainRLayout.draw(mBitCanvas);

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
        Toast.makeText(EditImageActivity.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

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



    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
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
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
        //main_img.setBrushColor(colorCode);
        //mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
        // mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
        //mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        //mPhotoEditor.addEmoji(emojiUnicode);
        //main_img.addEmoji(emojiUnicode);
        mPhotoEditor.addEmoji(mEmojiTypeFace, emojiUnicode);
        //mTxtCurrentTool.setText(R.string.label_emoji);
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        if(bitmap!=null)
        mPhotoEditor.addImage(bitmap);
        //mTxtCurrentTool.setText(R.string.label_sticker);
    }


    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit").setIcon(R.drawable.ic_launcher).setMessage("Are you want to exit without saving image ?");

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();

    }


    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }


    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }


    public void openGallery() {

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

    protected void cropImageUri(Uri picUri) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
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


    @Override
    public void onBackgroundSelected(int backgroundID , BackgroundType backgroundType) {
        switch (backgroundType) {

            case NONE:
                mPhotoEditorView.getSource().setBackgroundResource(R.color.white);
                break;

            case ONE:
                mPhotoEditorView.getSource().setBackgroundResource(R.drawable.bg_01);
                break;

            case TWO:
                mPhotoEditorView.getSource().setBackgroundResource(R.drawable.bg_02);
                break;

            case THREE:
                mPhotoEditorView.getSource().setBackgroundResource(R.drawable.bg_03);
                break;

            case FOUR:
                mPhotoEditorView.getSource().setBackgroundResource(R.drawable.bg_04);
                break;

            case FIVE:
                mPhotoEditorView.getSource().setBackgroundResource(R.drawable.bg_05);
                break;

            case SIX:
                mPhotoEditorView.getSource().setBackgroundResource(R.drawable.bg_06);
                break;

            case SEVEN:
                mPhotoEditorView.getSource().setBackgroundResource(R.drawable.bg_07);
                break;

            case EIGHT:
                mPhotoEditorView.getSource().setBackgroundResource(R.drawable.bg_08);
                break;

            case NINE:
                mPhotoEditorView.getSource().setBackgroundResource(R.drawable.bg_09);
                break;

            case TEN:
                mPhotoEditorView.getSource().setBackgroundResource(R.drawable.bg_10);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false);
            //mTxtCurrentTool.setText(R.string.app_name);
        } else if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStickersRecyclerSelected(StickersRecyclerType stickersType) {
        switch (stickersType) {

            case STICKER_ONE:
                if(mStickerOneFragment.isAdded())
                {
                    return;
                }
                mStickerOneFragment.show(getSupportFragmentManager(), mStickerOneFragment.getTag());
                break;
            case STICKER_TWO:
                if(mStickerTwoFragment.isAdded())
                {
                    return;
                }
                mStickerTwoFragment.show(getSupportFragmentManager(), mStickerTwoFragment.getTag());
                break;
            case STICKER_THREE:
                if(mStickerThreeFragment.isAdded())
                {
                    return;
                }
                mStickerThreeFragment.show(getSupportFragmentManager(), mStickerThreeFragment.getTag());
                break;
            case STICKER_FOUR:
                if(mStickerFourFragment.isAdded())
                {
                    return;
                }
                mStickerFourFragment.show(getSupportFragmentManager(), mStickerFourFragment.getTag());
                break;
            case STICKER_FIVE:
                if(mStickerFiveFragment.isAdded())
                {
                    return;
                }
                mStickerFiveFragment.show(getSupportFragmentManager(), mStickerFiveFragment.getTag());
                break;

            case STICKER_SIX:
                if(mStickerSixFragment.isAdded())
                {
                    return;
                }
                mStickerSixFragment.show(getSupportFragmentManager(), mStickerSixFragment.getTag());
                break;
            case STICKER_SEVEN:
                if(mStickerSevenFragment.isAdded())
                {
                    return;
                }
                mStickerSevenFragment.show(getSupportFragmentManager(), mStickerSevenFragment.getTag());
                break;

            case STICKER_EIGHT:
                if(mStickerEightFragment.isAdded())
                {
                    return;
                }
                mStickerEightFragment.show(getSupportFragmentManager(), mStickerEightFragment.getTag());
                break;

            case STICKER_NINE:
                if(mStickerNineFragment.isAdded())
                {
                    return;
                }
                mStickerNineFragment.show(getSupportFragmentManager(), mStickerNineFragment.getTag());
                break;


        }
    }

    @Override
    public void onStickerOneClick(Bitmap bitmap) {
        if(bitmap!=null)
        mPhotoEditor.addImage(bitmap);
    }
}
