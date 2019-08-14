package com.mojodigi.selfiepro.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mojodigi.selfiepro.AddsUtility.AddConstants;
import com.mojodigi.selfiepro.AddsUtility.AddMobUtils;
import com.mojodigi.selfiepro.AddsUtility.JsonParser;
import com.mojodigi.selfiepro.AddsUtility.OkhttpMethods;
import com.mojodigi.selfiepro.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.selfiepro.R;
import com.mojodigi.selfiepro.camera.CameraActivity;
import com.mojodigi.selfiepro.collage.CollageActivity;
import com.mojodigi.selfiepro.gallery.GalleryActivity;
import com.mojodigi.selfiepro.permission.PermissionsActivity;
import com.mojodigi.selfiepro.utils.Constants;
import com.mojodigi.selfiepro.utils.MyPreference;
import com.mojodigi.selfiepro.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeActivity extends PermissionsActivity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private Context mContext ;

    private ImageView cameraImgView  , galleryImgView , collageImgView  ;

    private static final int CAMERA_REQUEST_TYPE = 1;
    private static final int GALLERY_REQUEST_TYPE = 2;
    private static final int TEST_LAYOUT_REQUEST_TYPE = 3;
    private static final int TEST_IMAGEVIEW_REQUEST_TYPE = 4;
    private static final int REQUEST_PERMISSIONS = 20;

    private MyPreference mMyPrecfence = null;

    private String pictureFilePath ;

    BroadcastReceiver internetChangerReceiver;
    private SharedPreferenceUtil addprefs; ;
    private View adContainer;
    private RelativeLayout addhoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Constants.editImageUri = "false";

        if(mContext == null) {
            mContext =  HomeActivity.this ;
        }
        if(addprefs==null) {
            addprefs = new SharedPreferenceUtil(mContext);
        }

        if(mMyPrecfence == null) {
            mMyPrecfence = MyPreference.getMyPreferenceInstance(HomeActivity.this);
        }
        if (!checkPermission()) {
            HomeActivity.super.requestAppPermissions(new String[]{ Manifest.permission.CAMERA,  Manifest.permission.WRITE_EXTERNAL_STORAGE }, R.string.runtime_permissions_txt  , REQUEST_PERMISSIONS);
        } else {
            initView();
        }


        if(mContext!=null)
        {
            addprefs = new SharedPreferenceUtil(mContext);
            addhoster=findViewById(R.id.addhoster);
            adContainer = findViewById(R.id.adMobView);

        }


        // this broadcast  will  listen the  internet state change for sendig request  when internet becomes available
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        internetChangerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean isNetworkAvailable = AddConstants.checkIsOnline(mContext);

                //  Toast.makeText(context, "isNetworkAvailable-->" + isNetworkAvailable, Toast.LENGTH_SHORT).show();

                Log.d("isNetworkAvailable", "" + isNetworkAvailable);
                if (isNetworkAvailable) {
                    long ms1=System.currentTimeMillis();
                    System.out.print("Milliseconds-->>"+ms1);
                    Log.e("Milliseconds before-->>", ""+ms1);
                    new WebCall().execute();

                } else {
                    if (  addprefs != null) {
                        AddMobUtils util = new AddMobUtils();

                        //util.showInterstitial(addprefs,HomeActivity.this, null);
                        //util.displayRewaredVideoAdd(addprefs, mContext, null);
                    }
                }
            }
        };
        registerReceiver(internetChangerReceiver, intentFilter);
        // this broadcast  will  listen the  internet state change for sendig request  when internet becomes available
        deleteTempExtraFile();
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
                    mMyPrecfence.saveString(Constants.INTENT_TYPE, Constants.INTENT_TYPE_CAMERA);
                if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                    sendTakePictureIntent();
                }
                break;

            case R.id.galleryImgView:
                if(mMyPrecfence!=null)
                    mMyPrecfence.saveString(Constants.INTENT_TYPE, Constants.INTENT_TYPE_GALLERY);
                Utilities.openGallery(HomeActivity.this, GALLERY_REQUEST_TYPE);
                break;

            case R.id.collageImgView:

                Constants.isImageCroped1=false;
                Constants.isImageCroped2=false;
                Constants.isImageCroped3=false;
                Constants.isImageCroped4=false;
                Constants.isImageCroped5=false;
                Constants.isImageCroped6=false;


                if(mMyPrecfence!=null)
                    mMyPrecfence.saveString(Constants.INTENT_TYPE, Constants.INTENT_TYPE_GALLERY);
                Intent collageIntent = new Intent( HomeActivity.this , CollageActivity.class);
                startActivity(collageIntent);
                break;

        }
    }

    public class WebCall extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... strings) {
            String versioName="0";
            int versionCode=0;
            try {
                versioName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                versionCode = getPackageManager().getPackageInfo(getPackageName(),0 ).versionCode;

                Log.d("currentVersion", "" + versioName);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                // handel any other exception
            }

            try {
                JSONObject requestObj= AddConstants.prepareAddJsonRequest(mContext, AddConstants.VENDOR_ID , versioName ,versionCode );

                return OkhttpMethods.CallApi(mContext,AddConstants.API_URL,requestObj.toString());
            } catch (IOException e) {
                e.printStackTrace();
                return ""+e.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("JsonResponse", s);
            long ms2=System.currentTimeMillis();
            System.out.print("Milliseconds after-->>"+ms2);
            Log.e("Milliseconds after-->>", ""+ms2);
            if (addprefs != null)
            {
                int responseCode=addprefs.getIntValue(AddConstants.API_RESPONSE_CODE, 0);

                if (s != null  && responseCode==200 ) {
                    try {
                        JSONObject mainJson = new JSONObject(s);
                        if (mainJson.has("status")) {
                            String status = JsonParser.getkeyValue_Str(mainJson, "status");

                            String newVersion=JsonParser.getkeyValue_Str(mainJson,"appVersion");
                            addprefs.setValue(AddConstants.APP_VERSION, newVersion);
                            //addprefs.setValue(AddConstants.APP_VERSION, "1.29");


                            if (status.equalsIgnoreCase("true")) {

                                String adShow = JsonParser.getkeyValue_Str(mainJson, "AdShow");

                                if (adShow.equalsIgnoreCase("true")) {
                                    if (mainJson.has("data")) {
                                        JSONObject dataJson = mainJson.getJSONObject("data");
                                        AddMobUtils util = new AddMobUtils();
                                        String show_Add = JsonParser.getkeyValue_Str(mainJson, "AdShow");

                                        String adProviderId =JsonParser.getkeyValue_Str(dataJson, "adProviderId");
                                        String adProviderName = JsonParser.getkeyValue_Str(dataJson, "adProviderName");


//                                        String appId_PublisherId = JsonParser.getkeyValue_Str(dataJson, "appId_PublisherId");
//                                        String bannerAdId = JsonParser.getkeyValue_Str(dataJson, "bannerAdId");
//                                        String interstitialAdId = JsonParser.getkeyValue_Str(dataJson, "interstitialAdId");
//                                        String videoAdId = JsonParser.getkeyValue_Str(dataJson, "videoAdId");

                                        String appId_PublisherId = "0";//testID
                                        String bannerAdId = "712543632524943_712544799191493"; //testId
                                        String interstitialAdId = "712543632524943_712548422524464";//testId
                                        String videoAdId = "0";//testId


                                        Log.d("AddiDs", adProviderName + " ==" + appId_PublisherId + "==" + bannerAdId + "==" + interstitialAdId + "==" + videoAdId);


                                        //check for true value above in code so  can put true directly;
                                        try {
                                            addprefs.setValue(AddConstants.SHOW_ADD, Boolean.parseBoolean(show_Add));
                                        }catch (Exception e)
                                        {
                                            // IN CASE OF EXCEPTION CONSIDER  FALSE AS THE VALUE WILL NOT BE TRUE,FALSE.
                                            addprefs.setValue(AddConstants.SHOW_ADD, false);
                                        }
                                        //addprefs.setValue(AddConstants.APP_VERSION, newVersion);
                                        //addprefs.setValue(AddConstants.APP_VERSION, "1.22");
                                        addprefs.setValue(AddConstants.ADD_PROVIDER_ID, adProviderId);

                                        addprefs.setValue(AddConstants.APP_ID, appId_PublisherId);
                                        addprefs.setValue(AddConstants.BANNER_ADD_ID, bannerAdId);
                                        addprefs.setValue(AddConstants.INTERESTIAL_ADD_ID, interstitialAdId);
                                        addprefs.setValue(AddConstants.VIDEO_ADD_ID, videoAdId);

                                        if (adProviderId.equalsIgnoreCase(AddConstants.InMobiProvideId))
                                        {
                                            // inmobi adds not being implemented in this version
                                            // inmobi adds not being implemented in this version
                                        }
                                        else  if(adProviderId.equalsIgnoreCase(AddConstants.FaceBookAddProividerId))
                                        {
                                            //util.dispFacebookBannerAdd(mContext, addprefs,MainActivity.this);
                                        }
                                    } else {
                                        String message = JsonParser.getkeyValue_Str(mainJson, "message");
                                        Log.d("message", "" + message);
                                    }
                                } else {
                                    String message = JsonParser.getkeyValue_Str(mainJson, "message");

                                    Log.d("message", "" + message);
                                }
                            }
                            //call dispUpdateDialog
                            //dispUpdateDialog();
                        }
                    } catch (JSONException e) {
                        Log.d("jsonParse", "error while parsing json -->" + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Log.d("jsonParse", "error while parsing json -->");
                }
            }
        }
    }

    private void dispUpdateDialog() {
        try {
            String currentVersion = "0";
            String newVersion="0";
            if(addprefs!=null)
                newVersion=addprefs.getStringValue(AddConstants.APP_VERSION, AddConstants.NOT_FOUND);

            try {
                currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                Log.d("currentVersion", "" + currentVersion);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (Float.parseFloat(newVersion) > Float.parseFloat(currentVersion) && !newVersion.equalsIgnoreCase("0"))

            {
                if (mContext != null) {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialog_version_update);
                    long time = addprefs.getLongValue("displayedTime", 0);
                    long diff=86400000; // one day
                    //long diff=60000; // one minute;

                    if (time < System.currentTimeMillis() - diff) {
                        dialog.show();
                        addprefs.setValue("displayedTime", System.currentTimeMillis());
                    }


                    TextView later = dialog.findViewById(R.id.idDialogLater);
                    TextView updateNow = dialog.findViewById(R.id.idDialogUpdateNow);
                    TextView idVersionDetailsText = dialog.findViewById(R.id.idVersionDetailsText);
                    TextView idAppVersionText = dialog.findViewById(R.id.idAppVersionText);
                    TextView idVersionTitleText = dialog.findViewById(R.id.idVersionTitleText);


//                    idVersionTitleText.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
//                    idVersionDetailsText.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
//                    idAppVersionText.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
//                    later.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
//                    updateNow.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));

                    idAppVersionText.setText(newVersion);


                    later.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog.dismiss();
                        }
                    });


                    updateNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final String appPackageName = getPackageName(); // package name of the app
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                            dialog.dismiss();
                        }
                    });
                }
            }
        }
        catch (Exception e)
        {

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case CAMERA_REQUEST_TYPE:
                    Constants.isImageCroped =false;
                    File imgFile = new  File(pictureFilePath);
                    Log.e("Home File Path" , pictureFilePath);
                    if(imgFile.exists()) {
                        Constants.imageUri = Uri.fromFile(imgFile);
                        Constants.cameraImageUri = imgFile.getAbsolutePath();
                        Intent intentPhotoCamera  = new Intent(HomeActivity.this, CameraActivity.class);
                        intentPhotoCamera.putExtra(Constants.URI_CAMERA,  Constants.imageUri);
                        intentPhotoCamera.putExtra(Constants.PATH_CAMERA,  imgFile.getAbsolutePath());
                        startActivity(intentPhotoCamera);
                    }
                    break;

                case GALLERY_REQUEST_TYPE:
                    Constants.isImageCroped =false;
                    try {
                        Uri uri = data.getData();
                        Constants.imageUri = uri;
                        Intent intentPhotoGallary  = new Intent(HomeActivity.this,  GalleryActivity.class);
                        intentPhotoGallary.putExtra(Constants.URI_GALLERY,  Constants.imageUri);
                        intentPhotoGallary.putExtra(Constants.PATH_CAMERA,  uri.getPath());
                        startActivity(intentPhotoGallary);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
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

        }catch (Exception ex)
        {
            ex.printStackTrace();
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
                        "com.mojodigi.selfiepro.provider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_TYPE);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(internetChangerReceiver !=null)
            unregisterReceiver(internetChangerReceiver);

    }
}
