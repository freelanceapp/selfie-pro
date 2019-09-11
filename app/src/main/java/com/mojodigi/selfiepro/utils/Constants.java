package com.mojodigi.selfiepro.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

public class Constants {

    public static Bitmap faceStickerSelected = null;
    public static String imageUriOnBack = null;

    public static String capturedImagePath = "";

    public static int galleryAdjustImageWidth = 0;
    public static int galleryAdjustImageHeight = 0;

    public static int galleryEditBitmapHeight = 0;
    public static int galleryEditBitmapWidth = 0;

    public static int cameraEditBitmapHeight = 0;
    public static int cameraEditBitmapWidth = 0;

    public static Bitmap cameraCapturedBitmap = null;
    public static Bitmap cameraEditBitmap = null;
    public static Bitmap cameraCompressedBitmap = null;

    public static Bitmap galleryCapturedBitmap = null;
    public static Bitmap galleryEditBitmap = null;

    public static Bitmap capturedImageBitmap = null;
    public static Bitmap capturedImageBitmap1 = null;
    public static Bitmap capturedImageBitmap2 = null;
    public static Bitmap capturedImageBitmap3 = null;
    public static Bitmap capturedImageBitmap4 = null;
    public static Bitmap capturedImageBitmap5 = null;
    public static Bitmap capturedImageBitmap6 = null;

    public static Bitmap galleryOrignalBitmap = null;
    public static Bitmap cameraOrignalBitmap = null;
    public static Bitmap collageOrignalBitmap1 = null;
    public static Bitmap collageOrignalBitmap2 = null;
    public static Bitmap collageOrignalBitmap3 = null;
    public static Bitmap collageOrignalBitmap4 = null;
    public static Bitmap collageOrignalBitmap5 = null;
    public static Bitmap collageOrignalBitmap6 = null;


    public static String cameraImageUri = "";
    public static String galleryImageUri = "";

    public static Uri imageOrignalUri  = null;
    public static Uri imageUri  = null;

    public static boolean isAllCollageAdjust  =  false ;
    public static boolean isAdjustGalleryImage  =  false ;
    public static boolean isAdjustCameraImage  =  false ;

    public static boolean isImageCroped  =  false ;
    public static boolean isImageCroped1  =  false ;
    public static boolean isImageCroped2  =  false ;
    public static boolean isImageCroped3  =  false ;
    public static boolean isImageCroped4  =  false ;
    public static boolean isImageCroped5  =  false ;
    public static boolean isImageCroped6 =  false ;

    public static String editImageUri  = "false";

    public static String done_Edited_ImageType_Collage  = "false";

    public static Bitmap imageBitmap = null;

    public static ImageView imageView = null;

    public static final String appfolder = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SelfiePro";

    public static final String  tempfolder  = Environment.getExternalStorageDirectory().getAbsolutePath()+"/temp";

    public static final String  BITMAP_PICK_CAMERA  = "BITMAP_PICK_CAMERA";
    public static final String  BITMAP_PICK_GALLERY  = "BITMAP_PICK_GALLERY";

    public static final String  PATH_CAMERA  = "PATH_CAMERA";
    public static final String  PATH_GALLERY  = "PATH_GALLERY";
    public static final String  PATH_COLLAGE  = "PATH_COLLAGE";

    public static final String  URI_CAMERA  = "URI_CAMERA";
    public static final String  URI_GALLERY  = "URI_GALLERY";
    public static final String  URI_COLLAGE  = "URI_COLLAGE";



    public static final String  COLLAGE_ACTIVITY  = "CollageActivity";
    public static final String  INTENT_TYPE  = "IntentType";
    public static final String  INTENT_TYPE_CAMERA  = "CameraSelectedImage";
    public static final String  INTENT_TYPE_GALLERY  = "GallerySelectedImage";
    public static final String  INTENT_TYPE_COLLAGE  = "CollageSelectedImage";


    public static final String  URI_COLLAGE_EDIT_IMAGE  = "URI_COLLAGE_EDIT_IMAGE";


    public static boolean isSetCollageLayout1 =  false ;

    public static boolean isAdjustSelected =  false ;



}
