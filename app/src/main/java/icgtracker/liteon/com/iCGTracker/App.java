package icgtracker.liteon.com.iCGTracker;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class App extends Application implements  Application.ActivityLifecycleCallbacks {

    private static Context mContext;
    private static AppCompatActivity mActivity;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mContext = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        registerActivityLifecycleCallbacks(this);
    }

    public static Context getContext(){
        return mContext;
    }

    public static AppCompatActivity getCurrentActivity() {
        return mActivity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        mActivity = (AppCompatActivity) activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
