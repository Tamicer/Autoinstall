/**
 * Filename:    BdViewHelper.java
 * Description:
 * Copyright:   Baidu MIC Copyright(c)2013
 * @author:     Rambow
 * @version:    1.0
 * Create at:   May 22, 2013 1:50:58 PM
 * 
 * Modification History:
 * Date         Author      Version     Description
 * ------------------------------------------------------------------
 * May 22, 2013    Rambow      1.0         1.0 Version
 */
package autoinstall.com.tamic.autoinstall;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

/**
 * View的工具类
 */
public final class ViewUtils {
	/** 默认状态栏的高度, dip */
	public static final int DEFAULT_STATUSBAR_HEIGHT = 24;
	/** 缓存图片 */
	private static Bitmap sCache = null;
	/**Constructor*/
	private ViewUtils() {

	}

	/**
	 * 强制对View及其子视图进行递归刷新
	 * 
	 * @param aView
	 *            需要重绘的View(也有可能是ViewGroup)
	 */
	public static void forceInvalidateView(View aView) {
		if (aView instanceof ViewGroup) {
			ViewGroup childGroup = (ViewGroup) aView;
			int childCnt = childGroup.getChildCount();
			for (int i = 0; i < childCnt; i++) {
				View childView = childGroup.getChildAt(i);
				forceInvalidateView(childView);
			}
		}
		if (aView != null) {
			invalidate(aView);
		}
	}

	/**
	 * 获得status bar的高度
	 * 
	 * @param aActivity
	 *            activity
	 * @return status bar高度
	 */
	public static int getStatusbarHeight(Activity aActivity) {
		int statusbarHeight;
		try {
			Rect frame = new Rect();
			aActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			statusbarHeight = frame.top;
		} catch (Exception e) {
			final DisplayMetrics dm = aActivity.getResources().getDisplayMetrics();
			statusbarHeight = (int) (DEFAULT_STATUSBAR_HEIGHT * dm.density);
		}
		return statusbarHeight;
	}

	/**
	 * 从parent中移出指定child
	 * 
	 * @param aChild
	 *            child
	 */
	public static void removeFromParent(View aChild) {
		if (aChild != null) {
			View parent = (View) aChild.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(aChild);
			}
		}
	}

	/**
	 * layout指定的view
	 * @param aView view
	 */
	public static void requestLayout(View aView) {
		if (aView != null) {
			aView.requestLayout();
		}
	}

    /**
     * 强行递归layout
     * @param aView 根View
     */
    public static void requestLayoutAllRecurse(View aView) {
        if (aView != null && aView instanceof ViewGroup) {
            int cnt = ((ViewGroup) aView).getChildCount();
            for (int i = 0; i < cnt; i++) {
                View child = ((ViewGroup) aView).getChildAt(i);
                if (child != null && child instanceof ViewGroup) {
                    requestLayoutAllRecurse(child);
                } else if (child != null) {
                    child.requestLayout();
                }

            }
        }
    }

    /**
     * 把invalidate换成这个方法，是为了便于增加打印堆栈，方便找出哪里触发了界面更新
     * @param aView view
     */
    public static void invalidate(View aView) {
        if (aView != null) {
            aView.invalidate();
        }
    }

    /**
     * 把postInvalidate换成这个方法
     * @param aView view
     */
    public static void postInvalidate(View aView) {
        if (aView != null) {
            aView.postInvalidate();
        }
    }

	/**
	 * 强制对View及其子视图进行递归刷新
	 * 
	 * @param aView view
	 */
	public static void forceChildrenInvalidateRecursively(View aView) {
		if (aView instanceof ViewGroup) {
			ViewGroup childGroup = (ViewGroup) aView;
			int childCnt = childGroup.getChildCount();
			for (int i = 0; i < childCnt; i++) {
				View childView = childGroup.getChildAt(i);
				forceChildrenInvalidateRecursively(childView);
			}
		}
		if (aView != null) {
			invalidate(aView);
		}
	}

	/**
	 * 当前是否是横屏
	 *
	 * @param aContext
	 *            context
	 * @return true表示是横屏
	 */
	public static boolean isLandscape(Context aContext) {
		//因为小说会强制竖屏，所以这个判断不能添加前半句
		return (aContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
	}

	/**
	 * 得到屏幕的高度
	 *
	 * @param aContext
	 *            context
	 * @return screen height
	 */
	public static int getScreenHeight(Context aContext) {
		DisplayMetrics dm = aContext.getResources().getDisplayMetrics();
		int screenHeight;
		if (isLandscape(aContext)) {
			screenHeight = Math.min(dm.widthPixels, dm.heightPixels);
		} else {
			screenHeight = Math.max(dm.widthPixels, dm.heightPixels);
		}
		return screenHeight;
	}

	/**
	 * dip to pix
	 *
	 * @param aValue
	 *            dip
	 * @param aDensity
	 *            density
	 * @return pix
	 */
	public static int dip2pix(float aValue, float aDensity) {
		return Math.round(aValue * aDensity);
	}
	/**
	 * pix to dip
	 *
	 * @param aValue
	 *            pix
	 * @param aDensity
	 *            density
	 * @return dip
	 */
	public static int pix2dip(float aValue, float aDensity) {
		return Math.round(aValue / aDensity);
	}

	/**
	 * pix to dip
	 *
	 * @param aValue
	 *            pix
	 * @return dip
	 */
	public static float pix2dip(float aValue) {
		return Math.round(aValue / 1.5); //SUPPRESS CHECKSTYLE
	}

}
