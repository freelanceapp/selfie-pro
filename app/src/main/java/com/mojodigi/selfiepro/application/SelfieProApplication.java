package com.mojodigi.selfiepro.application;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.ads.AudienceNetworkAds;
import com.mojodigi.selfiepro.AddsUtility.AddConstants;
import com.mojodigi.selfiepro.AddsUtility.SharedPreferenceUtil;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

public class SelfieProApplication extends android.support.multidex.MultiDexApplication {

    private static SelfieProApplication mSelfieProApplication;

    private static final String TAG = SelfieProApplication.class.getSimpleName();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mSelfieProApplication = SelfieProApplication.this;

        // MobileAds.initialize(this, "ca-app-pub-8509384168493764~9766841905"); //demo app id
        // MobileAds.initialize(this, getResources().getString(R.string.admob_app_id)); //actual app id

        SharedPreferenceUtil addPref=new SharedPreferenceUtil(getApplicationContext());
        String appId=addPref.getStringValue(AddConstants.APP_ID, AddConstants.NOT_FOUND);
        if(appId !=null && !appId.equalsIgnoreCase(AddConstants.NOT_FOUND) )

//            MobileAds.initialize(this, appId); //actual app id


         //for fb adds
        //https://developers.facebook.com/docs/audience-network/reference/android/com/facebook/ads/audiencenetworkads.html/
        AudienceNetworkAds.initialize(getApplicationContext());
        AudienceNetworkAds.isInAdsProcess(getApplicationContext());

       // AnalyticsTrackers.initialize(this);
       // AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);



        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("f1762b2c-3f84-4287-a112-856c0c4caed1").build();
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);

        // Automatic tracking of user activity.
        //YandexMetrica.enableActivityAutoTracking(this);


    }

    public static SelfieProApplication getSelfieProApplication() {
        return mSelfieProApplication;
    }

    public Context getContext() {
        return mSelfieProApplication.getContext();
    }

//    public synchronized Tracker getGoogleAnalyticsTracker() {
//        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
//        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
//    }


    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
//    public void trackScreenView(String screenName) {
//        Tracker t = getGoogleAnalyticsTracker();
//
//        // Set screen name.
//        t.setScreenName(screenName);
//
//        // Send a screen view.
//        t.send(new HitBuilders.ScreenViewBuilder().build());
//
//        GoogleAnalytics.getInstance(this).dispatchLocalHits();
//    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
//    public void trackException(Exception e) {
//        if (e != null) {
//            Tracker t = getGoogleAnalyticsTracker();
//
//            t.send(new HitBuilders.ExceptionBuilder()
//                    .setDescription(
//                            new StandardExceptionParser(this, null)
//                                    .getDescription(Thread.currentThread().getName(), e))
//                    .setFatal(false)
//                    .build()
//            );
//        }
//    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
//    public void trackEvent(String category, String action, String label) {
//        Tracker t = getGoogleAnalyticsTracker();
//
//        // Build and send an Event.
//        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
//    }


}
