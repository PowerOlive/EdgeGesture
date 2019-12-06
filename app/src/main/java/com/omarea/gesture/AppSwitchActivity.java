package com.omarea.gesture;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

public class AppSwitchActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            overridePendingTransition(0, 0);
            Intent currentIntent = getIntent();
            int animation = SpfConfig.HOME_ANIMATION_DEFAULT;
            if (currentIntent.hasExtra("animation")) {
                animation = currentIntent.getIntExtra("animation", SpfConfig.HOME_ANIMATION_DEFAULT);
            }

            if (currentIntent.hasExtra("next")) {
                String appPackageName = currentIntent.getStringExtra("next");
                Log.d("AppSwitchActivity", "next >> " + appPackageName);
                if (animation == SpfConfig.HOME_ANIMATION_CUSTOM) {
                    switchApp(appPackageName, R.anim.activity_open_enter_2, R.anim.activity_open_exit_2);
                } else {
                    switchApp(appPackageName, R.anim.activity_open_enter, R.anim.activity_open_exit);
                }
            } else if (currentIntent.hasExtra("prev")) {
                String appPackageName = currentIntent.getStringExtra("prev");
                Log.d("AppSwitchActivity", "prev << " + appPackageName);
                if (animation == SpfConfig.HOME_ANIMATION_CUSTOM) {
                    switchApp(appPackageName, R.anim.activity_close_enter_2, R.anim.activity_close_exit_2);
                } else {
                    switchApp(appPackageName, R.anim.activity_close_enter, R.anim.activity_close_exit);
                }
            } else if (currentIntent.hasExtra("home")) {
                overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                if (animation == SpfConfig.HOME_ANIMATION_CUSTOM) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                }
                intent.addCategory(Intent.CATEGORY_HOME);
                if (animation == SpfConfig.HOME_ANIMATION_CUSTOM) {
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(this.getApplicationContext(), R.anim.activity_close_enter_2, R.anim.activity_close_exit_2);
                    overridePendingTransition(R.anim.home_enter, R.anim.app_exit);
                    startActivity(intent, activityOptions.toBundle());
                } else {
                    startActivity(intent);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ActivityOptions getActivityOptions() {
        ActivityOptions options = ActivityOptions.makeBasic();
        int freeform_stackId = 5;
        try {
            Method method = ActivityOptions.class.getMethod("setLaunchWindowingMode", int.class);
            method.invoke(options, freeform_stackId);
        } catch (Exception e) { /* Gracefully fail */ }

        return options;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startActivityAsFreeForm(Intent intent) {
        ActivityOptions activityOptions = getActivityOptions();
        int left = 50;
        int top = 100;
        int right = 800;
        int bottom = 1100;
        activityOptions.setLaunchBounds(new Rect(left,top,right,bottom));
        Bundle bundle = activityOptions.toBundle();
        startActivity(intent, bundle);
    }

    /*
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startActivityAsFreeForm(intent);
    } else {
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        pendingIntent.send();
    }
    */
    /*
    @Override
    public void startActivity(Intent intent) {
        try {
            PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Toast.makeText(this.getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    */

    private void switchApp(String appPackageName, int enterAnimation, int exitAnimation) {
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(this.getApplicationContext(), enterAnimation, exitAnimation);
        startActivity(getAppSwitchIntent(appPackageName), activityOptions.toBundle());
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    private Intent getAppSwitchIntent(String appPackageName) {
        Intent i = getPackageManager().getLaunchIntentForPackage(appPackageName);
        // i.setFlags((i.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) | Intent.FLAG_ACTIVITY_NEW_TASK);
        // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        // i.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        // i.setFlags(0x10200000);
        i.setFlags((i.getFlags() | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        // i.setFlags((i.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        // i.setAction(Intent.ACTION_MAIN);
        // i.addCategory(Intent.CATEGORY_LAUNCHER);
        return i;
    }
}
