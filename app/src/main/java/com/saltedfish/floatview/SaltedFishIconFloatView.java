package com.saltedfish.floatview;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import static com.saltedfish.floatview.LogManager.logE;
import static com.saltedfish.floatview.LogManager.logI;

/**
 * Project:  SaltedFishIconFloatView <br/>
 * Package:  com.saltedfish.floatview <br/>
 * ClassName:  SaltedFishIconFloatView <br/>
 * Description:  TODO <br/>
 * Date:  2018/05/29  14:41 <br/>
 * <p>
 * Author  LuoHao<br/>
 * Version 1.0<br/>
 * since JDK 1.6<br/>
 * <p>
 */
public class SaltedFishIconFloatView extends SaltedFishFloatView {

    private static SaltedFishIconFloatView mInstance = null;

    private SaltedFishCornerFloatView mCornerFlowView;

    private boolean hasHideByUser = false;

    private int mOffsetX;
    private int mOffsetY;

    private SaltedFishIconFloatView() {
    }


    public static SaltedFishIconFloatView getInstance() {
        if (null == mInstance) {
            mInstance = new SaltedFishIconFloatView();
        }
        return mInstance;
    }



    @Override
    public void changeSkin() {
        super.changeSkin();
        if (null == mCornerFlowView)
            return;
        mCornerFlowView.changeSkin();
    }

    /**
     * 添加到界面
     */
    @Override
    public void onAttach(Activity activity) {
        doAttach(activity);
    }

    private void doAttach(Activity activity) {
        if (hasAttach)
            return;
        try {
            mCornerFlowView = SaltedFishCornerFloatView.getInstance();
            mCornerFlowView.onAttach(activity);
            //x,y的偏移量
            mOffsetX = 52;
            mOffsetY = 144;
            //x,y的起始位置确定
            mPositionY = 248;
            Drawable drawable = activity.getResources().getDrawable(R.mipmap.ic_saltedfish_robot_bg);
            mPositionX = activity.getResources().getDisplayMetrics().widthPixels - mOffsetX - drawable.getIntrinsicWidth() / 2;
            super.onAttach(activity);
            changeSkin();
        } catch (Exception e) {
            logE("======================SaltedFishIconFloatView doAttach=========================" + e.toString());
        }
    }

    @Override
    public View getContentView() {
        FrameLayout container = new FrameLayout(mActivity);
        ImageView icon = new ImageView(mActivity);
        container.setBackgroundResource(R.mipmap.ic_saltedfish_robot_bg);
        icon.setImageResource(R.mipmap.ic_saltedfish_robot);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = 22;
        layoutParams.gravity = Gravity.LEFT | Gravity.CENTER;
        layoutParams.rightMargin = margin;
        layoutParams.leftMargin = margin;
        container.addView(icon, layoutParams);
        return container;
    }

    private float mCountX;
    private float mCountY;
    private long mLastDownTime;

    @Override
    protected boolean onTouchEvent(View v, MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mPositionX = motionEvent.getRawX();
                mPositionY = motionEvent.getRawY();
                mCountX = 0f;
                mCountY = 0f;
                mLastDownTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                try {
                    float deltaX = motionEvent.getRawX() - mPositionX;
                    float deltaY = motionEvent.getRawY() - mPositionY;
                    mCountX += deltaX;
                    mCountY += deltaY;
                    mParams.x += deltaX;
                    mParams.y += deltaY;
                    mWindowManager.updateViewLayout(mContentView, mParams);
                    mPositionX = motionEvent.getRawX();
                    mPositionY = motionEvent.getRawY();

                    if (mCountX > mTouchSlop || mCountY > mTouchSlop) {
//                        animateCornerView();
                        mCornerFlowView.showOutAnimation();
                    }

                    //TODO  处理移动到角落的逻辑
                    mCornerFlowView.scale(isCornerRect(), 1.0f, 1.2f);
                } catch (Exception e) {
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                handleIcon();
                if (mCountX < mTouchSlop && mCountY < mTouchSlop && System.currentTimeMillis() - mLastDownTime <= 200) {
                    mContentView.callOnClick();
                    return true;
                }
                hideCorner();
                break;
        }
        return true;
    }


    private void handleIcon() {
        yOffset = -1;
        if (mParams.y < 0) {
            yOffset = mStatusBarHeight;
        }
        if (mParams.y > mHeight - mOffsetY - mContentView.getHeight() * 2) {
            yOffset = mHeight - mOffsetY - mContentView.getHeight() * 2;
        }
        xOffset = mParams.x;
        final int yPosition = mParams.y;

        final float xBoundValue = xOffset < mWidth / 2 ? -mOffsetX : mWidth - mOffsetX - mContentView.getWidth() / 2;
        ValueAnimator va = ValueAnimator.ofFloat(xOffset, xBoundValue);
        int mDuration = 250;
        va.setDuration(mDuration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            public void onAnimationUpdate(ValueAnimator animation) {
                float animationValue = Math.round((float) animation.getAnimatedValue());
                float scale = (xBoundValue - animationValue) / (xBoundValue - xOffset);
                logI("=================animationValue=================" + animationValue);
                mParams.x = (int) animationValue;
                if (-1 != yOffset) {
                    logI("=================animationValue scale=================" + scale);
                    mParams.y = (int) (yPosition - ((yPosition - yOffset) * (1.0f - scale)));
                }
                mWindowManager.updateViewLayout(mContentView, mParams);
                if (animationValue == xBoundValue) {
                    int gravity = xOffset < mWidth / 2 ? Gravity.RIGHT | Gravity.CENTER : Gravity.LEFT | Gravity.CENTER;
                    ImageView icon = (ImageView) ((ViewGroup) mContentView).getChildAt(0);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) icon.getLayoutParams();
                    layoutParams.gravity = gravity;
                    icon.setLayoutParams(layoutParams);
                }
            }
        });
        va.start();

    }


    private Drawable mCornerDrawable;
    private int mCornerWidth = 0;
    private int mCornerStartX = 0;
    private int mCornerStartY = 0;
    private int mStatusBarHeight = 0;

    private boolean isCornerRect() {
        checkResource();

        Rect cornerRect = new Rect(mCornerStartX, mCornerStartY, mCornerStartX + mCornerWidth, mCornerStartY + mCornerWidth);
        return cornerRect.contains(mParams.x, mParams.y);
    }

    private void checkResource() {
        if (0 == mStatusBarHeight) {
            Rect rectangle = new Rect();
            Window window = mActivity.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
            mStatusBarHeight = rectangle.top;
        }
        mCornerDrawable = mCornerFlowView.getBackground();
        mCornerWidth = mCornerDrawable.getIntrinsicWidth();
        mCornerStartX = mActivity.getResources().getDisplayMetrics().widthPixels - mCornerWidth;
        mCornerStartY = mActivity.getResources().getDisplayMetrics().heightPixels - mCornerWidth;
    }


    private void hideCorner() {
        if (isCornerRect()) {
            hasHideByUser = true;
            hide();
        }
        mCornerFlowView.showInAnimation();
    }

    private int xOffset = 0, yOffset = 0;

}
