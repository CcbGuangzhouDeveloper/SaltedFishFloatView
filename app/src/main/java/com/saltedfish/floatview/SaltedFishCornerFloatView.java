package com.saltedfish.floatview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import static com.saltedfish.floatview.LogManager.logE;
import static com.saltedfish.floatview.LogManager.logI;


/**
 * Project:  SaltedFishFloatView <br/>
 * Package:  com.saltedfish.floatview <br/>
 * ClassName:  SaltedFishCornerFloatView <br/>
 * Date:  2018/06/08  16:49 <br/>
 * <p>
 * Author  LuoHao<br/>
 * Version 1.0<br/>
 * since JDK 1.6<br/>
 * <p>
 */
public class SaltedFishCornerFloatView extends SaltedFishFloatView {
    private static SaltedFishCornerFloatView mInstance = null;


    private SaltedFishCornerFloatView(){
    }

    public static SaltedFishCornerFloatView getInstance() {
        if(null == mInstance){
            mInstance = new SaltedFishCornerFloatView();
        }
        return mInstance;
    }
    private FrameLayout mCornerFrameLayout;
    @Override
    public View getContentView() {
        FrameLayout parent = new FrameLayout(mActivity);
        mCornerFrameLayout = new FrameLayout(mActivity);
        mCornerFrameLayout.setBackgroundResource(R.mipmap.ic_saltedfish_bg_flowview_corner);
        FrameLayout.LayoutParams LayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams.gravity = Gravity.RIGHT|Gravity.BOTTOM;

        ImageView hideFlowViewView = new ImageView(mActivity);
        hideFlowViewView.setImageResource(R.mipmap.ic_saltedfish_hide_flowviewr);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        mCornerFrameLayout.addView(hideFlowViewView,layoutParams);
        parent.addView(mCornerFrameLayout,LayoutParams);

        return parent;
    }

    public Drawable getBackground(){
        return  mCornerFrameLayout.getBackground();
    }

    private AnimatorSet mOutAnimator;
    private volatile  boolean mHasEnd = false;
    public  synchronized void showOutAnimation(){

        if(null != mOutAnimator && mOutAnimator.isRunning() || mHasEnd)
            return;
        mOutAnimator = new AnimatorSet();
        Drawable drawable = getBackground();
        mPositionY =   mActivity.getResources().getDisplayMetrics().heightPixels - drawable.getIntrinsicWidth();
        mPositionX =  mActivity.getResources().getDisplayMetrics().widthPixels  - drawable.getIntrinsicWidth();
        mParams.x = (int) mPositionX;
        mParams.y = (int) mPositionY;
        mWindowManager.updateViewLayout(mContentView,mParams);
        if(!isInitFirst){
            updatePosition(mActivity.getResources().getDisplayMetrics().widthPixels - drawable.getIntrinsicWidth() *1.2f,
                    mActivity.getResources().getDisplayMetrics().heightPixels  - drawable.getIntrinsicWidth() *1.2f,1.0f);
        }
        if(isInitFirst){
            updateViewLayout(1.2f);
            mPositionY =   mActivity.getResources().getDisplayMetrics().heightPixels - drawable.getIntrinsicWidth()*1.2f;
            mPositionX =  mActivity.getResources().getDisplayMetrics().widthPixels  - drawable.getIntrinsicWidth()*1.2f;
            updatePosition(mPositionX,mPositionY,1.0f);

            isInitFirst = false;
        }
        ObjectAnimator  x = ObjectAnimator.ofFloat(mCornerFrameLayout,"translationX",drawable.getIntrinsicWidth(),0);
        ObjectAnimator y = ObjectAnimator.ofFloat(mCornerFrameLayout,"translationY",drawable.getIntrinsicWidth(),0);
        ObjectAnimator a = ObjectAnimator.ofFloat(mCornerFrameLayout,"alpha",0,1.0f);
        mOutAnimator.play(x).with(y).with(a);
        mOutAnimator.setDuration(300);
        mOutAnimator.start();
        mOutAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHasEnd = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private AnimatorSet mInAnimator;
    public synchronized void showInAnimation(){
        if(null  != mOutAnimator &&  mOutAnimator.isRunning()){
            mOutAnimator.cancel();
        }
        if(null != mInAnimator && mInAnimator.isRunning() || !mHasEnd)
            return;

        mInAnimator = new AnimatorSet();
        Drawable drawable = getBackground();
        ObjectAnimator  x = ObjectAnimator.ofFloat(mCornerFrameLayout,"translationX",0,drawable.getIntrinsicWidth());
        ObjectAnimator y = ObjectAnimator.ofFloat(mCornerFrameLayout,"translationY",0,drawable.getIntrinsicWidth());
        ObjectAnimator a = ObjectAnimator.ofFloat(mCornerFrameLayout,"alpha",1.0f,0);
        mInAnimator.play(x).with(y).with(a);
        mInAnimator.setDuration(300);
        mInAnimator.start();
        mInAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHasEnd = false;
                mPositionY =   mActivity.getResources().getDisplayMetrics().heightPixels;
                mPositionX =  mActivity.getResources().getDisplayMetrics().widthPixels;
                mParams.x = (int) mPositionX;
                mParams.y = (int) mPositionY;
                mWindowManager.updateViewLayout(mContentView,mParams);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public boolean isInitFirst = true;
    public boolean beBigger = true;
    public void scale(boolean isCornerRect, float smallScale, float bigScale){
        if(isCornerRect){
            if (beBigger){
                mCornerFrameLayout.animate().scaleY(bigScale);
                mCornerFrameLayout.animate().scaleX(bigScale);
                beBigger = false;
            }

        }else{
            if(!beBigger){
                mCornerFrameLayout.animate().scaleY(smallScale);
                mCornerFrameLayout.animate().scaleX(smallScale);
                beBigger = true;
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        if(hasAttach)
            return;
        try {
            //x,y的起始位置确定
            mPositionY =   activity.getResources().getDisplayMetrics().heightPixels;
            mPositionX =  activity.getResources().getDisplayMetrics().widthPixels;
            super.onAttach(activity);
            changeSkin();
        } catch (Exception e) {
            logE("=======================SaltedFishCornerFloatView  onAttach======================="+e.toString());
        }
    }

    public  void  updatePosition(float x , float y,float alpha ){
        mParams.x = (int) x;
        mParams.y = (int) y;
        mParams.alpha = alpha;
        mWindowManager.updateViewLayout(mContentView,mParams);
        logI("=================updatePosition=================="+mParams.x+"******************"+mParams.y);
    }

    @Override
    protected boolean onTouchEvent(View v, MotionEvent motionEvent) {
        return false;
    }


    public void updateViewLayout(float scale){
        int height = mContentView.getHeight();
        int width = mContentView.getWidth();

        mParams.height = (int) (height * scale);
        mParams.width = (int) (width * scale);
        mContentView.setLayoutParams(mParams);
        mWindowManager.updateViewLayout(mContentView,mParams);
    }
}