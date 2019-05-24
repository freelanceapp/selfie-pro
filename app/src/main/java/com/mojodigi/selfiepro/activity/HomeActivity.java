package com.mojodigi.selfiepro.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.mojodigi.selfiepro.R;
import com.mojodigi.selfiepro.camera.CameraActivity;
import com.mojodigi.selfiepro.collage.CollageSelectedActivity;
import com.mojodigi.selfiepro.gallery.GallerySelectedActivity;
import com.mojodigi.selfiepro.permission.PermissionsActivity;
import com.mojodigi.selfiepro.utils.Constants;
import com.mojodigi.selfiepro.utils.MyPreference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeActivity extends PermissionsActivity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private Context  mContext;
    private ImageView cameraImgView  , galleryImgView , collageImgView;

    private static final int PICK_CAMERA_REQUEST = 1;
    private static final int PICK_GALLARY_REQUEST = 2;
    private static final int REQUEST_PERMISSIONS = 20;

    private boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ;
    private MyPreference mMyPrecfence = null;

    private String pictureFilePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(mContext == null) {
            mContext =  HomeActivity.this ;
        }

        if(mMyPrecfence == null) {
            mMyPrecfence = MyPreference.getMyPreferenceInstance(HomeActivity.this);
        }

        if (!checkPermission()) {
            HomeActivity.super.requestAppPermissions(new String[]{ Manifest.permission.CAMERA,  Manifest.permission.WRITE_EXTERNAL_STORAGE }, R.string.runtime_permissions_txt  , REQUEST_PERMISSIONS);
        } else {
            initView();
        }
    }


    private boolean checkPermission() {
        boolean permissionCheckStatus;

        int permissionCheckCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        int permissionCheckWrite = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheckCamera > -1) {
            if (permissionCheckWrite > -1) {
                permissionCheckStatus = true;
            } else {
                permissionCheckStatus = false;
            }
        } else {
            permissionCheckStatus = false;
        }
        return permissionCheckStatus;
    }


    @Override
    public void onPermissionsGranted(int requestCode) {
        initView();
        Log.e("Permissions " , "has been Received.");
        // Utilities.getUtilInstance(HomeActivity.this).showSnackBarLong("Permissions has been Received." ,  homeActivityLayout);
    }


    private void initView() {


        mMyPrecfence.saveString(Constants.COLLAGE_ACTIVITY , "false");

        cameraImgView = findViewById(R.id.cameraImgView);
        cameraImgView.setOnClickListener(this);

        galleryImgView = findViewById(R.id.galleryImgView);
        galleryImgView.setOnClickListener(this);

        collageImgView = findViewById(R.id.collageImgView);
        collageImgView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cameraImgView:
                if(mMyPrecfence!=null)

                 mMyPrecfence.saveString(Constants.INTENT_TYPE, "IntentCamera");

                 //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                 //startActivityForResult(cameraIntent, PICK_CAMERA_REQUEST);

//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);//Start intent with Action_Image_Capture
//                Uri fileUri = CameraUtils.getOutputMediaFileUri(mContext);//get fileUri from CameraUtils
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);//Send fileUri with intent
//                startActivityForResult(intent, PICK_CAMERA_REQUEST);//start activity for result with CAMERA_REQUEST_CODE

//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, PICK_CAMERA_REQUEST);
//                }

                if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                   sendTakePictureIntent();
                    // dispatchTakePictureIntent();
                }


                break;

            case R.id.galleryImgView:
                if(mMyPrecfence!=null)
                 mMyPrecfence.saveString(Constants.INTENT_TYPE, "IntentGallery");
                openGallery();
                break;

            case R.id.collageImgView:
                if(mMyPrecfence!=null)
                mMyPrecfence.saveString(Constants.INTENT_TYPE, "IntentCollage");

                Intent collageIntent = new Intent( HomeActivity.this , CollageSelectedActivity.class);
                startActivity(collageIntent);
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
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
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

//        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//        String pictureFile = "JPEG_" + timeStamp;
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
//        pictureFilePath = image.getAbsolutePath();
//        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case PICK_CAMERA_REQUEST:

                    File imgFile = new  File(pictureFilePath);
                    if(imgFile.exists())            {

                        Intent intentPhotoCamera  = new Intent(HomeActivity.this, CameraActivity.class);
                        intentPhotoCamera.putExtra("BITMAP_PICK_CAMERA", Uri.fromFile(imgFile));
                        startActivity(intentPhotoCamera);
                    }



                    break;

                case PICK_GALLARY_REQUEST:

                    try {
                        Uri uri = data.getData();
                        Intent intentPhotoGallary  = new Intent(HomeActivity.this,  GallerySelectedActivity.class);
                        intentPhotoGallary.putExtra("URI_PICK_GALLARY", uri);
                        startActivity(intentPhotoGallary);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

    public void openGallery() {

        if (isKitKat) {
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            }
            assert intent != null;
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

}
