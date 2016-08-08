/*
 * *
 *  * Filename:    BdTucaoWaitView.java
 *  * Description:
 *  * Copyright:   Baidu MIC Copyright(c)2015
 *  * @author:     guokai01
 *  * @version:    1.0
 *  * Create at:   2015-04-11
 *  *
 *  * Modification History:
 *  * Date         Author      Version     Description
 *  * ------------------------------------------------------------------
 *  * 2015-04-11    guokai01      1.0         1.0 Version
 *
 */

package autoinstall.com.tamic.autoinstall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by skay on 15-8-11.
 */
public class TamcWaitingView extends FrameLayout {
    /** Layout */
    private LinearLayout mContentLayout;
    /** 图标 */
    private TamicLoanIcon mLoadingIcon;
    /** 文案 */
    protected TextView mText;
    /** */
    private static final int COLOR_BG = 0xffffffff;
    /** */
    private int mBgColor = COLOR_BG;
    /** */
    private boolean mIsBgColor = false;

    /**SDK最大版本号*/
    private static final int MAX_SDK_VERSION_CODE = 9;
    /**
     * Constructor
     *
     * @param context
     *            context
     */
    public TamcWaitingView(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context
     *            context
     * @param attrs
     *            attrs
     */
    public TamcWaitingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context
     *            context
     * @param attrs
     *            attrs
     * @param defStyle
     *            defStyle
     */
    public TamcWaitingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        float density = getResources().getDisplayMetrics().density;
        FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        contentParams.gravity = Gravity.CENTER;
        mContentLayout = new LinearLayout(context);
        mContentLayout.setOrientation(LinearLayout.VERTICAL);

        int iconSize = (int) (density * 55); //SUPPRESS CHECKSTYLE
        LinearLayout.LayoutParams loadingParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        loadingParams.gravity = Gravity.CENTER;
        mLoadingIcon = new TamicLoanIcon(context);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mText = new TextView(context);
        mText.setText("waitting..");
        mText.setTextColor(0xff898989); //SUPPRESS CHECKSTYLE
        mContentLayout.addView(mLoadingIcon, loadingParams);
        mContentLayout.addView(mText, textParams);

        addView(mContentLayout, contentParams);

    }

    public TextView getmText() {
        return mText;
    }

    public void setmText(TextView mText) {
        this.mText = mText;
    }

    /**
     * 开始动画
     */
    public void startAnimation() {
        if (mLoadingIcon != null) {
            mLoadingIcon.startAnimation();
        }
    }

    /**
     * 结束动画
     */
    public void stopAnimation() {
        if (mLoadingIcon != null) {
            mLoadingIcon.stopAnimation();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }

    /**
     * release
     */
    public void release() {
        removeAllViews();
        mLoadingIcon = null;
        mText = null;
        mContentLayout = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 旋转icon
     */
    public class TamicLoanIcon extends View {

        /** 默认转动角度 */
        public static final int DEFAULT_DEGREE = 20;

        /** 默认延迟时间 */
        public static final int DEFAULT_DELAY_TIME = 35;

        /** 动画开关 */
        private boolean mAnimating = false;

        /** icon */
        private Bitmap mIcon;

        /** mPaint */
        private Paint mPaint;

        /** 转角 */
        private int mIconDegree = 0;

        /***/
        private int mRotateDegree;

        /**
         * 开始动画
         */
        public void startAnimation() {
            setDataBeforeStartAni();
            setVisibility(VISIBLE);
            ViewUtils.invalidate(this);
        }

        /**
         * 开始动画前对变量初始化
         */
        public void setDataBeforeStartAni() {
            mIconDegree = 0;
            mAnimating = true;
        }

        /**
         * 停止动画
         */
        public void stopAnimation() {
            mAnimating = true;
            ViewUtils.invalidate(this);
        }

        /**
         * Constructor
         *
         * @param context
         *            context
         */
        public TamicLoanIcon(Context context) {
            this(context, null);
        }

        /**
         * Instantiates a new bd novel custom progress bar.
         *
         * @param context
         *            the context
         * @param attrs
         *            the attrs
         */
        public TamicLoanIcon(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        /**
         * Instantiates a new bd novel custom progress bar.
         *
         * @param context
         *            the context
         * @param attrs
         *            the attrs
         * @param defStyle
         *            the def style
         */
        public TamicLoanIcon(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            setWillNotDraw(false);
            mIcon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.loading);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            mRotateDegree = DEFAULT_DEGREE;
        }

        /**
         * 设置转动图标
         *
         * @param aResId
         *            aResId
         */
        public void setLoadingIcon(int aResId) {
            mIcon = BitmapFactory.decodeResource(getResources(), aResId);
        }

        /**
         * 获取图标高
         *
         * @return int
         */
        public int getIconHeight() {
            if (mIcon != null) {
                if (mIcon != null) {
                    return mIcon.getWidth();
                }
            }
            return 0;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            if (mIcon != null) {

                float x = (getWidth() - mIcon.getWidth()) >> 1;
                float y = (getHeight() - mIcon.getHeight()) >> 1;

                if (mAnimating) {
                    canvas.save();
                    canvas.rotate(mIconDegree, getWidth() >> 1, getHeight() >> 1);
                    canvas.drawBitmap(mIcon, x, y, mPaint);
                    canvas.restore();
                    mIconDegree += mRotateDegree;
                    if (mIconDegree >= 360) { //SUPPRESS CHECKSTYLE
                        mIconDegree = mIconDegree % 360; //SUPPRESS CHECKSTYLE
                    }
                    postInvalidateDelayed(DEFAULT_DELAY_TIME);
                } else {
                    canvas.drawBitmap(mIcon, x, y, mPaint);
                }
            }
        }

        /**
         * release
         */
        public void release() {
            if (mIcon != null && !mIcon.isRecycled()) {
                BitmapUtils.recycleBitmap(mIcon);
                mIcon = null;
            }
        }
    }
}
