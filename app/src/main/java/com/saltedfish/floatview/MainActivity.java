package com.saltedfish.floatview;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (hasAppPermission()) {
            SaltedFishIconFloatView.getInstance().onAttach(this);
        } else {
            requestPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SaltedFishIconFloatView.getInstance().show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SaltedFishIconFloatView.getInstance().hide();
    }

    final static int PERMISSION_REQUEST_OVERLAY_CODE = 0xFF99;

    private boolean hasAppPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) ? Settings.canDrawOverlays(this) : true;
    }

    private void requestPermission() {
        Uri packageURI = Uri.parse("package:" + getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, packageURI);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startActivityForResult(intent, PERMISSION_REQUEST_OVERLAY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PERMISSION_REQUEST_OVERLAY_CODE || !checkFloatPermission(this)) {
            Toast.makeText(this, "has no permission", Toast.LENGTH_SHORT).show();
            return;
        }
        SaltedFishIconFloatView.getInstance().onAttach(this);
    }

    /**
     * a bug in oreo
     *
     * @param context
     * @return
     * @see <a href="https://issuetracker.google.com/issues/66072795">issue</a>
     */
    private boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            return Settings.canDrawOverlays(context);
        } else {
            if (Settings.canDrawOverlays(context)) return true;
            AppOpsManager appOpsMgr = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), getPackageName());
            LogManager.logI("android:system_alert_window: mode=" + mode);
            return AppOpsManager.MODE_ERRORED != mode;
        }
    }
}
