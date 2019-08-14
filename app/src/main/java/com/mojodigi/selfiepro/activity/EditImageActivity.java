package com.mojodigi.selfiepro.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mojodigi.selfiepro.AddsUtility.AddConstants;
import com.mojodigi.selfiepro.AddsUtility.AddMobUtils;
import com.mojodigi.selfiepro.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.selfiepro.R;
import com.mojodigi.selfiepro.adapter.StickersListRecyclerAdapter;
import com.mojodigi.selfiepro.base.BaseActivity;
import com.mojodigi.selfiepro.collage.CollageActivity;
import com.mojodigi.selfiepro.enums.StickersRecyclerType;
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
import com.mojodigi.selfiepro.interfaces.FilterListener;
import com.mojodigi.selfiepro.interfaces.OnItemSelected;
import com.mojodigi.selfiepro.interfaces.OnStickersRecyclerSelected;
import com.mojodigi.selfiepro.tools.EditingToolsAdapter;
import com.mojodigi.selfiepro.tools.ToolType;
import com.mojodigi.selfiepro.utils.Constants;
import com.mojodigi.selfiepro.utils.MyPreference;
import com.mojodigi.selfiepro.utils.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.ViewType;


public class EditImageActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener, PropertiesFragment.Properties, EmojiFragment.EmojiListener,
        StickerFragment.StickerListener, StickerOneFragment.StickerOneListener,  StickerTwoFragment.StickerTwoListener,
        StickerThreeFragment.StickerThreeListener,StickerFourFragment.StickerFourListener, StickerFiveFragment.StickerFiveListener ,
        StickerSixFragment.StickerSixListener , StickerSevenFragment.StickerSevenListener , StickerEightFragment.StickerEightListener
        , StickerNineFragment.StickerNineListener, OnItemSelected, FilterListener  , OnStickersRecyclerSelected   {

    private static final String TAG = EditImageActivity.class.getSimpleName();


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


    private RecyclerView mRvTools, /*mRvFilters ,*/ stickersListRecycleView;

    private EditingToolsAdapter mEditingToolsAdapter  ;

    private StickersListRecyclerAdapter stickersListRecyclerAdapter  ;


    //private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet  ;
    //private boolean mIsFilterVisible;

    private LinearLayout editImageBackLLayout , editIamgeUndoLayout , editImageRedoLayout , editIamgeSaveLayout , doneEditIamgeLayout ;

    private ImageView  undoEditImageView, redoEditImageView;
    private TextView undoEditTextView, redoEditTextView;

    private MyPreference mMyPrecfence = null;

    private String mIntentType = "";

    //private RelativeLayout mEditMainRLayout  ;
    private LinearLayout blankEditImageLayoutTop , blankEditImageLayoutBottom  , editImageSubRecyclerLayout /*, editImageLLayout*/  ;


    private SharedPreferenceUtil addprefs;
    private View adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(mContext==null){
            mContext =  EditImageActivity.this;
        }

        makeFullScreen();

        setContentView(R.layout.activity_edit_image);

        initViews();

        addprefs = new SharedPreferenceUtil(mContext);
        AddMobUtils adutil = new AddMobUtils();

        if(AddConstants.checkIsOnline(mContext) && adContainer !=null && addprefs !=null)
        {
            String AddPrioverId=addprefs.getStringValue(AddConstants.ADD_PROVIDER_ID, AddConstants.NOT_FOUND);
            if(AddPrioverId.equalsIgnoreCase(AddConstants.FaceBookAddProividerId))
            {
                adutil.dispFacebookBannerAdd(mContext,addprefs , EditImageActivity.this);
            }
        }
        else {
            Log.e("","");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        blankEditImageLayoutTop.setVisibility(View.VISIBLE);
        blankEditImageLayoutBottom.setVisibility(View.VISIBLE);
        editImageSubRecyclerLayout.setVisibility(View.GONE);

    }



    private void initViews() {

        mConstraintSet = new ConstraintSet();

        adContainer = findViewById(R.id.adMobView);

        mEditingToolsAdapter = new EditingToolsAdapter(this);

        stickersListRecyclerAdapter = new StickersListRecyclerAdapter(this);

        if (mMyPrecfence == null) {
            mMyPrecfence = MyPreference.getMyPreferenceInstance(EditImageActivity.this);
        }

        try{
            mMyPrecfence.saveString(Constants.COLLAGE_ACTIVITY , "false");
            mIntentType = mMyPrecfence.getString(Constants.INTENT_TYPE);
        }catch (Exception ex){
            ex.getStackTrace();
        }

        undoEditImageView = findViewById(R.id.undoEditImageView);
        undoEditTextView = findViewById(R.id.undoEditTextView);

        redoEditImageView = findViewById(R.id.redoEditImageView);
        redoEditTextView = findViewById(R.id.redoEditTextView);

        //mEditMainRLayout = findViewById(R.id.idEditMainRLayout);

        //editImageLLayout = findViewById(R.id.editImageLLayout);

        blankEditImageLayoutTop = findViewById(R.id.blankEditImageLayoutTop);
        blankEditImageLayoutTop.setOnClickListener(this);

        blankEditImageLayoutBottom = findViewById(R.id.blankEditImageLayoutBottom);
        blankEditImageLayoutBottom.setOnClickListener(this);

        editImageSubRecyclerLayout = findViewById(R.id.editImageSubRecyclerLayout);

        mPhotoEditorView = findViewById(R.id.photoEditorView);

        mRvTools = findViewById(R.id.rvConstraintTools);

        stickersListRecycleView = findViewById(R.id.stickersListRecycleView);
        //mRvFilters = findViewById(R.id.rvFilterView);

        //mRootView = findViewById(R.id.rootView);

        editImageBackLLayout = findViewById(R.id.editImageBackLLayout);
        editImageBackLLayout.setOnClickListener(this);

        editIamgeUndoLayout = findViewById(R.id.editIamgeUndoLayout);
        editIamgeUndoLayout.setOnClickListener(this);

        editImageRedoLayout = findViewById(R.id.editImageRedoLayout);
        editImageRedoLayout.setOnClickListener(this);

        editIamgeSaveLayout = findViewById(R.id.editIamgeSaveLayout);
        editIamgeSaveLayout.setOnClickListener(this);

        doneEditIamgeLayout = findViewById(R.id.doneEditIamgeLayout);
        doneEditIamgeLayout.setOnClickListener(this);

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


        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView).setPinchTextScalable(true).build();

        // build photo editor sdk
        mPhotoEditor.setOnPhotoEditorListener(this);

        if (mIntentType.equalsIgnoreCase(Constants.INTENT_TYPE_CAMERA)) {
            Constants.done_Edited_ImageType_Collage = "false";
            mPhotoEditor.clearAllViews();
            Uri uriCameraImage = null;
            Intent extrasIntent = getIntent();
            if (extrasIntent != null) {
                uriCameraImage = extrasIntent.getParcelableExtra(Constants.URI_CAMERA);
                try {
                    //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriCameraImage);
                    Bitmap bitmap = Constants.cameraEditBitmap;

                    RelativeLayout.LayoutParams imgSrcParam = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//ss
                    mPhotoEditorView.setLayoutParams(imgSrcParam);
                    //mPhotoEditorView.getSource().setImageURI(uriCameraImage);
                    mPhotoEditorView.getSource().setImageBitmap(bitmap);
                }
                catch ( Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (mIntentType.equalsIgnoreCase(Constants.INTENT_TYPE_GALLERY)) {
            Constants.done_Edited_ImageType_Collage = "false";
            mPhotoEditor.clearAllViews();
            Uri uriGalleryImage = null;
            Intent extrasIntent = getIntent();
            if (extrasIntent != null) {
                uriGalleryImage = extrasIntent.getParcelableExtra(Constants.URI_GALLERY);
                try {
                     //Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriGalleryImage);
                    Bitmap bitmap = Constants.galleryEditBitmap;

//                    Log.e("bitmap.getWidth()" , bitmap.getWidth()+"");
//                    Log.e("bitmap.getHeight()" , bitmap.getHeight()+"");
//                    Log.e("imgSrcParam.width" , imgSrcParam.width+"");
//                    Log.e("imgSrcParam.height" , imgSrcParam.height+"");

                    // bitmap.setWidth(bitmap.getWidth()+100);

//                    RelativeLayout.LayoutParams imgSrcParam = new RelativeLayout.LayoutParams(
//                            bitmap.getWidth(), bitmap.getHeight());//12_55

//                    RelativeLayout.LayoutParams imgSrcParam = new RelativeLayout.LayoutParams(
//                            Constants.galleryEditBitmapWidth, Constants.galleryEditBitmapHeight);

//                    RelativeLayout.LayoutParams imgSrcParam = new RelativeLayout.LayoutParams(
//                            getScreenWidth(), Constants.galleryEditBitmapHeight);




//                    RelativeLayout.LayoutParams imgSrcParam = new RelativeLayout.LayoutParams(
//                            Constants.galleryEditBitmapWidth, Constants.galleryEditBitmapHeight);

//                    RelativeLayout.LayoutParams imgSrcParam = new RelativeLayout.LayoutParams(
//                            bitmap.getWidth()-30, bitmap.getHeight()-40);

//                    RelativeLayout.LayoutParams imgSrcParam = new RelativeLayout.LayoutParams(
//                            bitmap.getWidth()-40, ViewGroup.LayoutParams.WRAP_CONTENT);//ss

                    RelativeLayout.LayoutParams imgSrcParam = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//ss

//                    Log.e("bitmap.getWidth()" , bitmap.getWidth()+"");
//                    Log.e("bitmap.getHeight()" , bitmap.getHeight()+"");
//                    Log.e("imgSrcParam.width" , imgSrcParam.width+"");
//                    Log.e("imgSrcParam.height" , imgSrcParam.height+"");

//                     RelativeLayout.LayoutParams imgSrcParamGallery = new RelativeLayout.LayoutParams(
//                             imgSrcParam.width, imgSrcParam.height);

                     mPhotoEditorView.setLayoutParams(imgSrcParam);
                    //mPhotoEditorView.setLayoutParams(imgSrcParamGallery);

                    mPhotoEditorView.getSource().setImageBitmap(bitmap);
                   // mPhotoEditorView.getSource().setImageURI(uriGalleryImage);

                }
                catch ( Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (mIntentType.equalsIgnoreCase(Constants.INTENT_TYPE_COLLAGE)) {
            Constants.done_Edited_ImageType_Collage = "true";
            mPhotoEditor.clearAllViews();
            Uri uriCollageImage = null;
            Intent extrasIntent = getIntent();
            if (extrasIntent != null) {
                uriCollageImage = extrasIntent.getParcelableExtra(Constants.URI_COLLAGE);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriCollageImage);
                    RelativeLayout.LayoutParams imgSrcParam = new RelativeLayout.LayoutParams(
                            bitmap.getWidth(), bitmap.getHeight());
                    mPhotoEditorView.setLayoutParams(imgSrcParam);
                    mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    //mPhotoEditorView.getSource().setImageURI(uriCollageImage);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch ( Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        onUndoRedo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.blankEditImageLayoutTop:
                blankEditImageLayoutTop.setVisibility(View.VISIBLE);
                blankEditImageLayoutBottom.setVisibility(View.VISIBLE);
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                break;

            case R.id.blankEditImageLayoutBottom:
                blankEditImageLayoutTop.setVisibility(View.VISIBLE);
                blankEditImageLayoutBottom.setVisibility(View.VISIBLE);
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                break;

            case R.id.editImageBackLLayout:
                blankEditImageLayoutTop.setVisibility(View.VISIBLE);
                blankEditImageLayoutBottom.setVisibility(View.VISIBLE);
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                onBackPressed();
                break;

            case R.id.editIamgeUndoLayout:
                blankEditImageLayoutBottom.setVisibility(View.VISIBLE);
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                mPhotoEditor.undo();
                boolean undo = mPhotoEditor.isUndoEmpty();
                Log.e("Undo " , undo+"");
                if(undo){
                    hideUndo();
                }else {
                    showUndo();
                }
                boolean redo = mPhotoEditor.isRedoEmpty();
                Log.e("Redo " , redo+"");
                if(redo){
                    hideRedo();
                }else {
                    showRedo();
                }
                break;

            case R.id.editImageRedoLayout:
                blankEditImageLayoutBottom.setVisibility(View.VISIBLE);
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                mPhotoEditor.redo();
                boolean redoo = mPhotoEditor.isRedoEmpty();
                Log.e("Redoo " , redoo+"");
                if(redoo){
                    hideRedo();
                }else {
                    showRedo();
                }
                boolean undoo = mPhotoEditor.isUndoEmpty();
                Log.e("Undoo " , undoo+"");
                if(undoo){
                    hideUndo();
                }else {
                    showUndo();
                }
                break;

            case R.id.doneEditIamgeLayout:
                //imgPhotoEditorClose.setVisibility(View.INVISIBLE);
                blankEditImageLayoutBottom.setVisibility(View.VISIBLE);
                editImageSubRecyclerLayout.setVisibility(View.GONE);

                Constants.editImageUri = "true";

                if(Constants.done_Edited_ImageType_Collage.equalsIgnoreCase("true")){
                    try {
                        Bitmap mBitmap = Utilities.createBitmapFromLayout(mPhotoEditorView);
                        String imagePath =  Utilities.saveBitmap_Temp(mBitmap);
                        //Uri uriImage = Uri.fromFile(new File(imagePath));
                        Constants.imageUriOnBack = imagePath;
                        File imgFile = new File(imagePath);
                        if (imgFile.exists()) {
                            Constants.imageUri = Uri.fromFile(imgFile);
                        }

                        Intent intentCollageImage = new Intent(EditImageActivity.this, CollageActivity.class);
                        intentCollageImage.putExtra(Constants.URI_COLLAGE_EDIT_IMAGE, Constants.imageUri);
                        startActivity(intentCollageImage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        //Bitmap mBitmap = Utilities.createBitmapFromLayout(mEditMainRLayout);
                        Bitmap bitmap = Utilities.createBitmapFromLayout(mPhotoEditorView);
                        String imagePath =  Utilities.saveBitmap_Temp(bitmap);
                        Constants.imageUriOnBack = imagePath;
                        File imgFile = new File(imagePath);
                        if (imgFile.exists()) {
                            Constants.imageUri = Uri.fromFile(imgFile);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }



                finish();

                break;
        }
    }


 private void onUndoRedo(){
     boolean undo = mPhotoEditor.isUndoEmpty();
     Log.e("Undo " , undo+"");
     if(undo){
         hideUndo();
     }else {
         showUndo();
     }

     boolean redo = mPhotoEditor.isRedoEmpty();
     Log.e("Redo " , redo+"");
     if(redo){
         hideRedo();
     }else {
         showRedo();
     }
 }

    private void showUndo() {
        undoEditImageView.setImageResource(R.drawable.ic_undo);
        undoEditTextView.setTextColor(getResources().getColor(R.color.color_icon));
    }
    private void hideUndo() {
        undoEditImageView.setImageResource(R.drawable.ic_undo_none);
        undoEditTextView.setTextColor(getResources().getColor(R.color.undo_redo_none));
    }
    private void showRedo() {
        redoEditImageView.setImageResource(R.drawable.ic_redo);
        redoEditTextView.setTextColor(getResources().getColor(R.color.color_icon));
    }
    private void hideRedo() {
        redoEditImageView.setImageResource(R.drawable.ic_redo_none);
        redoEditTextView.setTextColor(getResources().getColor(R.color.undo_redo_none));
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }





    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {

            case TEXT:
                blankEditImageLayoutBottom.setVisibility(View.VISIBLE);
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                TextEditorFragment textEditorDialogFragment = TextEditorFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        mPhotoEditor.addText(inputText, colorCode);
                        onUndoRedo();
                    }
                });

                break;

            case STICKERS:
                if(editImageSubRecyclerLayout.getVisibility() == View.VISIBLE ){
                    blankEditImageLayoutBottom.setVisibility(View.VISIBLE);
                    editImageSubRecyclerLayout.setVisibility(View.GONE);
                }else {
                    blankEditImageLayoutBottom.setVisibility(View.GONE);
                    editImageSubRecyclerLayout.setVisibility(View.VISIBLE);
                }
                break;

            case BRUSH:
                blankEditImageLayoutBottom.setVisibility(View.VISIBLE);
                editImageSubRecyclerLayout.setVisibility(View.GONE);

                if(mPropertiesFragment.isAdded())
                {
                    return;
                }
                 mPhotoEditor.setBrushDrawingMode(true);

                mPropertiesFragment.show(getSupportFragmentManager(), mPropertiesFragment.getTag());


                break;

            case ERASER:
                blankEditImageLayoutBottom.setVisibility(View.VISIBLE);
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                mPhotoEditor.brushEraser();
                break;


            case EMOJI:
                blankEditImageLayoutBottom.setVisibility(View.VISIBLE);
                editImageSubRecyclerLayout.setVisibility(View.GONE);
                if(mEmojiFragment.isAdded())
                {
                    return;
                }
                mEmojiFragment.show(getSupportFragmentManager(), mEmojiFragment.getTag());
                break;
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
                onUndoRedo();
            }
        });
    }



    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

//    @Override
//    public void onRemoveViewListener(int numberOfAddedViews) {
//        Log.d(TAG, "onRemoveViewListener() called with: numberOfAddedViews = [" + numberOfAddedViews + "]");
//    }

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

        onUndoRedo();
    }



    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
        }
    }


    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
    }


    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        onUndoRedo();
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        if(bitmap!=null)
            mPhotoEditor.addImage(bitmap);
        onUndoRedo();
    }


    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
        onUndoRedo();
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

        onUndoRedo();
    }

    public void show_alert_back(String title, String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( EditImageActivity.this);
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

                Constants.editImageUri = "true";

                if(Constants.done_Edited_ImageType_Collage.equalsIgnoreCase("true")){
                    //backCollageEditImage();
                    try {
                        Bitmap mBitmap = Utilities.createBitmapFromLayout(mPhotoEditorView);
                        String imagePath =  Utilities.saveBitmap_Temp(mBitmap);
                        //Uri uriImage = Uri.fromFile(new File(imagePath));
                        Constants.imageUriOnBack = imagePath;
                        File imgFile = new File(imagePath);
                        if (imgFile.exists()) {
                            Constants.imageUri = Uri.fromFile(imgFile);
                        }

                        Intent intentCollageImage = new Intent(EditImageActivity.this, CollageActivity.class);
                        intentCollageImage.putExtra(Constants.URI_COLLAGE_EDIT_IMAGE, Constants.imageUri);
                        //intentCollageImage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        intentCollageImage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intentCollageImage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        Bitmap bitmap = Utilities.createBitmapFromLayout(mPhotoEditorView);
                        String imagePath =  Utilities.saveBitmap_Temp(bitmap);
                        Constants.imageUriOnBack = imagePath;
                        File imgFile = new File(imagePath);
                        if (imgFile.exists()) {
                            Constants.imageUri = Uri.fromFile(imgFile);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (!mPhotoEditor.isCacheEmpty() || mPhotoEditor.isCacheEmpty()) {
            show_alert_back("Exit", "You want to save changes ?");
        } else {
            super.onBackPressed();
        }
    }
}




