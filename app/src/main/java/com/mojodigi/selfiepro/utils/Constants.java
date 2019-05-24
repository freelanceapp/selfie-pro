package com.mojodigi.selfiepro.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.widget.ImageView;

public class Constants {

    public static Bitmap imageBitmap = null;

    public static ImageView imageView = null;

    public static final String  URI_COLLAGE_SELECTED_IMAGE  = "uriCollageSelectedImage";
    public static final String  COLLAGE_ACTIVITY  = "CollageActivity";
    public static final String  INTENT_TYPE  = "IntentType";

    public static Typeface typeFace_Anydore(Context ctx)
    {

        return Typeface.createFromAsset(ctx.getAssets(), "Anydore.otf");
    }
}
