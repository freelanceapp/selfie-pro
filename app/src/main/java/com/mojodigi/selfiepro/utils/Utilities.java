package com.mojodigi.selfiepro.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mojodigi.selfiepro.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utilities {

    private Context mContext;
    private static Utilities mUtilInstance = null;
    static int count = 0;

    private static boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;


    private Utilities() {
    }

    private Utilities(Context context) {
        this.mContext = context;
    }

    public static Utilities getUtilInstance(Context context) {

        if (mUtilInstance == null) {
            mUtilInstance = new Utilities(context);
        }
        return mUtilInstance;
    }




    public static String getStringFromXml(Context mContext,int resId)
    {
        return mContext.getResources().getString(resId);
    }

    private void redirectToactivity(Class targetClass)
    {
        Intent i = new Intent(mContext, targetClass);
        mContext.startActivity(i);
    }


    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int pxFromDp(final Context context, final int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public double parseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch(Exception e) {
                return -1;
            }
        }
        else return 0;
    }


    public void showSnackBarLong(String message, View view) {
        if (view != null && !TextUtils.isEmpty(message)) {
            Snackbar mSnackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            mSnackbar.setActionTextColor(Color.blue(R.color.white));
            mSnackbar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.color_theme));
            mSnackbar.show();
        }
    }


    public void showSnackBarShort(String message, View view) {
        if (view != null && !TextUtils.isEmpty(message)) {
            Snackbar mSnackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
            mSnackbar.setActionTextColor(Color.blue(R.color.white));
            mSnackbar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.color_theme));
            mSnackbar.show();

        }
    }


    public void showToastShort(Context context, String message) {
        if (context != null && !TextUtils.isEmpty(message)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void showToastLong(Context context, String message) {
        if (context != null && !TextUtils.isEmpty(message)) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isWiFiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

            return true;

        } else {
            return false;
        }
    }




//    public static ProgressDialog generateProgressDialog(Context context, boolean cancelable) {
//        ProgressDialog progressDialog = new ProgressDialog(context, R.style.ProgressTheme);
//        progressDialog.setMessage(context.getString(R.string.Loading));
//        progressDialog.setCancelable(cancelable);
//        return progressDialog;
//    }




    public static int dpToPx(Context context, int dp) {
        return Math.round(dp * getPixelScaleFactor(context));
    }

    public static int pxToDp(Context context, int px) {
        return Math.round(px / getPixelScaleFactor(context));
    }

    private static float getPixelScaleFactor(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT;
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();
        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are  > 0
        final int width = !drawable.getBounds().isEmpty() ? drawable.getBounds().width() : drawable.getIntrinsicWidth();
        final int height = !drawable.getBounds().isEmpty() ? drawable.getBounds().height() : drawable.getIntrinsicHeight();
        // Now we check we are > 0
        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public static boolean isEmpty(String target) {
        if (target.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSame(String target, String newtar) {
        if (target.equals(newtar)) {
            return true;
        } else {
            return false;
        }
    }



    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }


    @SuppressLint("ObsoleteSdkInt")
    public static void fixAntiAlias(View viewAntiAlias) {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            viewAntiAlias.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (Build.VERSION.SDK_INT > 10) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
            viewAntiAlias.setLayerType(View.LAYER_TYPE_SOFTWARE, p);
            ((View) viewAntiAlias.getParent()).setLayerType(View.LAYER_TYPE_SOFTWARE, p);
        }
    }

    public static Bitmap bitmapFromImageView(ImageView view){
        Bitmap bitmap = ((BitmapDrawable)view.getDrawable()).getBitmap();
        return bitmap;
    }

    //Method to do flip action
    public static Bitmap flipImage(Bitmap image_bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        Bitmap flipped_bitmap = Bitmap.createBitmap(image_bitmap, 0, 0, image_bitmap.getWidth(), image_bitmap.getHeight(), matrix, true);

        return flipped_bitmap;
    }



//    private String saveImage() {
//
//        OutputStream output;
//        Calendar cal = Calendar.getInstance();
//
//        Bitmap bitmap = Bitmap.createBitmap(mGalleryRLayout.getWidth(), mGalleryRLayout.getHeight(), Bitmap.Config.ARGB_8888);
//
//        bitmap = ThumbnailUtils.extractThumbnail(bitmap, mGalleryRLayout.getWidth(), mGalleryRLayout.getHeight());
//
//        Canvas mBitCanvas = new Canvas(bitmap);
//        mGalleryRLayout.draw(mBitCanvas);
//
//        // Find the SD Card path
//        File filepath = Environment.getExternalStorageDirectory();
//
//        // Create a new folder in SD Card
//        File dir = new File(filepath.getAbsolutePath() + "/SelfiePro/");
//        dir.mkdirs();
//
//        mImageName = "SelfiePro" + cal.getTimeInMillis() + ".png";
//
//        // Create a name for the saved image
//        mImageFile = new File(dir, mImageName);
//        runMediaScan(mContext, mImageFile);
//        // Show a toast message on successful save
//        Toast.makeText(GalleryActivity.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();
//
//        try {
//            output = new FileOutputStream(mImageFile);
//            // Compress into png format image from 0% - 100%
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
//            output.flush();
//            output.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return mImageName;
//    }


    public static String saveSelfieProImage(Context context , Bitmap finalBitmap) {

        File myDir = new File(Constants.appfolder);
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "SelfiePro_"+ timeStamp +".jpg";

        File file = new File(myDir, fname);
        runMediaScan(context, file);

        if (file.exists()) file.delete();

        Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT).show();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.e("Temp Image Path " , file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public static void runMediaScan(Context context, File fileName) {
        MediaScannerConnection.scanFile(
                context, new String[]{fileName.getPath()}, null,
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                        Log.e("acn ", "connected");
                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("scan ", "completed");
                    }
                });
    }



    public static String saveBitmap_Temp(Bitmap finalBitmap) {

        File myDir = new File(Constants.tempfolder);
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = count+"_"+ timeStamp +".jpg";

        File file = new File(myDir, fname);

        if (file.exists()) file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        count = count+1;

        //Log.e("Temp Image Path " , file.getAbsolutePath());

        return file.getAbsolutePath();
    }


    public static Bitmap getBitmapFromView(View view) {
        Bitmap b = Bitmap.createBitmap( view.getLayoutParams().width, view.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        view.draw(canvas);
        return b;
    }

    public static void openGallery(Activity activity , int REQUEST_TYPE) {

        if (isKitKat) {
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            }
            assert intent != null;
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            activity.startActivityForResult(Intent.createChooser(intent, "Select picture"), REQUEST_TYPE);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(Intent.createChooser(intent, "Select picture"), REQUEST_TYPE);
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


    public static Bitmap createBitmapFromLayout(View view){
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, view.getWidth(), view.getHeight());
        Canvas mBitCanvas = new Canvas(bitmap);
        view.draw(mBitCanvas);
        return bitmap;
    }


    public static Bitmap createBitmapFromView(View v) {
        v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return bitmap;
    }


//    public static void openGallery(Activity activity , int REQUEST_TYPE) {
//
//        if (isKitKat) {
//            Intent intent = new Intent(android.content.Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);
//            intent.setType("image/*");
//            activity.startActivityForResult(android.content.Intent.createChooser(intent, "Select picture"), REQUEST_TYPE);
//        } else {
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
//            activity.startActivityForResult(android.content.Intent.createChooser(intent, "Select picture"), REQUEST_TYPE);
//        }
//    }



    public static PorterDuffColorFilter setBrightness(int progress) {
        if (progress >= 100)
        {
            int value = (int) (progress-100) * 255 / 100;

            return new PorterDuffColorFilter(Color.argb(value, 255, 255, 255), PorterDuff.Mode.SRC_OVER);

        }
        else
        {
            int value = (int) (100-progress) * 255 / 100;
            return new PorterDuffColorFilter(Color.argb(value, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);


        }
    }







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
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = mContext.getContentResolver().query(contentUri, null, null, null, null);
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

}
