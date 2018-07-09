package com.saltedfish.floatview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Toast;


import java.lang.reflect.Field;


/**
 * Project:  SaltedFishFloatView <br/>
 * Package:  com.saltedfish.floatview <br/>
 * ClassName:  SaltedFishFloatView <br/>
 * Description:  浮窗基类 <br/>
 * Date:  2018/05/29  14:41 <br/>
 * <p>
 * Author  LuoHao<br/>
 * Version 1.0<br/>
 * since JDK 1.6<br/>
 * <p>
 */
public abstract class SaltedFishFloatView {
    protected int mTouchSlop;

    protected float mPositionX = 0;
    protected float mPositionY = 0;

    protected View mContentView;

    protected WindowManager mWindowManager;
    protected WindowManager.LayoutParams mParams;

    protected int mWidth;
    protected int mHeight;

    public abstract View getContentView();

    protected Activity mActivity;

    protected boolean hasAttach = false;
    /**
     * 换肤
     */
    public void changeSkin(){
        if(null == mContentView)
            return;
    }

    /**
     * 加入到界面
     * @param activity
     * @throws Exception
     */
    public void onAttach(Activity activity) throws Exception {
        this.mActivity = activity;
        mContentView = getContentView();
        if (null == mContentView)
            return;
        mTouchSlop = ViewConfiguration.get(mContentView.getContext()).getScaledTouchSlop();
        mWidth = mContentView.getContext().getResources().getDisplayMetrics().widthPixels;
        mHeight =  mContentView.getContext().getResources().getDisplayMetrics().heightPixels;
        reflectionResource(mContentView);
    }

    /**
     * 反射资源 这里7.1以下用TOAST 不用权限，7.1以上走正常浮窗生成
     * @param contentView
     * @throws Exception
     */
    private void reflectionResource(View contentView) throws Exception {
        Toast toast = new Toast(mActivity);
        toast.setView(contentView);
        Field mTNField = Toast.class.getDeclaredField("mTN");
        mTNField.setAccessible(true);
        Object mTN = mTNField.get(toast);

        final View mView = toast.getView();
        Context context = mView.getContext().getApplicationContext();
        if (context == null) {
            context = mView.getContext();
        }
        final WindowManager mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Field mParamsField = mTN.getClass().getDeclaredField("mParams");
        mParamsField.setAccessible(true);
        WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) mParamsField.get(mTN);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.N_MR1){
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        this. mWindowManager = mWM;
        this. mParams = mParams;
        if (mView.getParent() != null) {
            mWM.removeView(mView);
        }
        mContentView.setOnTouchListener(mDefaultTouchListener);
        final int gravity = Gravity.LEFT | Gravity.TOP;
        mParams.gravity = gravity;
        mParams.verticalWeight = 1.0f;
        mParams.x = (int) mPositionX;
        mParams.y = (int) mPositionY;

        addView();
        hasAttach = true;
    }

    public void hide() {
        try {
            if (null == mContentView || !hasAttach)
                return;
            mContentView.setEnabled(false);
            mParams.alpha = 0f;
            mWindowManager.updateViewLayout(mContentView, mParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show() {
        try {
            if (null == mContentView || !hasAttach )
                return;
            mContentView.setEnabled(true);
            mParams.alpha = 1.0f;
            mWindowManager.updateViewLayout(mContentView, mParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addView(){
        mWindowManager.addView(mContentView, mParams);
    }

    /**
     *  触摸事件
     * @param v
     * @param motionEvent
     * @return
     */
    protected abstract boolean onTouchEvent(View v, MotionEvent motionEvent);


    private View.OnTouchListener mDefaultTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            return onTouchEvent(v, motionEvent);
        }
    };

    public void onDetach(){

        if(null == mWindowManager || !hasAttach) {
            return;
        }
        mWindowManager.removeView(mContentView);
        hasAttach = false;

    }

    protected  int getNavHeight(){
        Resources resources = mActivity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
