package com.mojodigi.selfiepro.filters;

import android.content.Context;

public interface EditImageListener {

    void onBrightnessChanged(int brightness);

   void onSaturationChanged(float saturation);

    void onContrastChanged(float contrast);

    void onColorOverlaySubFilter(int depth, float red, float green, float blue);

    void onVignetteSubfilter(Context context, int alpha);

    void onToneCurveSubFilter( int  redKnotsX, int  redKnotsY, int  greenKnotsX, int  greenKnotsY, int blueKnotsX , int blueKnotsY) ;


    void onEditStarted();

    void onEditCompleted();
}
