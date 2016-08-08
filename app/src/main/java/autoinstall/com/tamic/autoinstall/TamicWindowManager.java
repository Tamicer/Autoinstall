package autoinstall.com.tamic.autoinstall;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.view.ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

/**
 * WindowManager.
 * Created By liuyongkui
 * @data 15-8-11.
 */
public class TamicWindowManager {
	
	private WindowManager wdm;
	private double time;
	private View mView;
	private WindowManager.LayoutParams params;
	private Timer timer;
	private static TamicWindowManager mWindowManager;
	  
	/**
	 * BdToastCustom constucts
	 * @param context   context
	 * @param text      text
	 * @param time      time
	 */
	private TamicWindowManager(Context context, String text, double time){
		wdm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		timer = new Timer();

		// mView = LayoutInflater.from(context).inflate(R.layout.activity_loading, null);
		mView = new TamcWaitingView(context);
		Toast toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setMargin(0, 0);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(mView);
		toast.setText(text);

		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.MATCH_PARENT;
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.format = PixelFormat.TRANSLUCENT;
		params.windowAnimations = toast.getView().getAnimation().INFINITE;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.setTitle("Toast");
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER;
		params.y = -30;

		this.time = time;
	}

	/**
	 * BdWindowManager constucts.
	 * @param context    context
	 * @param text       text
	 */
	private TamicWindowManager(Context context, String text){
		wdm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		mView = new TamcWaitingView(context);
		((TamcWaitingView)mView).getmText().setText(text);
		params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;
		params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		params.height = ViewGroup.LayoutParams.MATCH_PARENT;
		params.gravity = Gravity.CENTER;
		params.y = -30;

	}
	  
	  /**
	   * makeText.
	 * @param context     context
	 * @param text        text
	 * @param time        time
	 * @return BdToastCustom
	 */
	public static TamicWindowManager makeText(Context context, String text, double time) {

		mWindowManager = new TamicWindowManager(context, text, time);
	    
		return mWindowManager;
	  }

	/**
	 * makeText.
	 * @param context   context
	 * @param text      text
	 * @return WatingWiew
	 */
	public static TamicWindowManager makeWatingWiew(Context context, String text) {

		mWindowManager = new TamicWindowManager(context, text);

		return mWindowManager;
	}
	  
	  /**
	 * show toast.
	 */
	public void show() {
	    wdm.addView(mView, params);
		if (time <= 0) {

			((TamcWaitingView)mView).startAnimation();

		}

		if (timer != null) {
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					wdm.removeView(mView);
				}
			}, (long)(time * 1000));
		}

	  }
	  
	  /**
	 * cancel tosat.
	 */
	  public void cancel() {
		  
		  if (mView != null) {
			  if (wdm != null) {
				  wdm.removeView(mView);
				  mView = null;
			  }
			  
			  if (timer != null) {
				  timer.cancel();
			  }
		  }
	  }

	/**
	 *  dismiss
	 */
	 public static void dismiss() {
		if(mWindowManager != null) {
			mWindowManager.cancel();
			mWindowManager = null;
		 }
	  }
	  
	  
}
