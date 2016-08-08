/** 
 * Filename:    BdBitmapUtils.java
 * Description:  
 * Copyright:   Baidu MIC Copyright(c)2011 
 * @author:     Jacob 
 * @version:    1.0
 * Create at:   2013-7-9 下午2:04:21
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-7-9     Jacob       1.0         1.0 Version 
 */
package autoinstall.com.tamic.autoinstall;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * bitmap工具类
 */
public final class BitmapUtils {

	/**
	 * Constructor
	 */
	private BitmapUtils() {

	}

	/**
	 * 获取资源图片
	 * 
	 * @param aPath
	 *            path
	 * @return bitmap
	 */
	public static Bitmap getBitmap(String aPath) {
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeFile(aPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bmp;
	}

	/**
	 * 保存图片到给定的路径中，PNG保存
	 * 
	 * @param aBmp
	 *            bmp
	 * @param aPath
	 *            path
	 * @return true表示保存成功
	 */
	public static boolean saveBitmap(Bitmap aBmp, String aPath) {
		if (aBmp == null || aPath == null) {
			return false;
		}
		FileOutputStream fos = null;
		ByteArrayOutputStream baos = null;
		boolean result;
		try {
			File file = new File(aPath);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			baos = new ByteArrayOutputStream();
			aBmp.compress(Bitmap.CompressFormat.PNG, 100, baos); //SUPPRESS CHECKSTYLE
			fos.write(baos.toByteArray());
			baos.flush();
			fos.flush();

			result = true;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			result = false;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 保存图片到给定的路径中，JPEG保存
	 * 
	 * @param aBmp
	 *            bmp
	 * @param aPath
	 *            path
	 * @return true表示保存成功
	 */
	public static boolean saveJPEGBitmap(Bitmap aBmp, String aPath) {
		if (aBmp == null || aPath == null) {
			return false;
		}

		FileOutputStream fos = null;
		ByteArrayOutputStream baos = null;
		boolean result = false;
		try {
			File file = new File(aPath);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			baos = new ByteArrayOutputStream();
			aBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); //SUPPRESS CHECKSTYLE
			fos.write(baos.toByteArray());
			baos.flush();
			fos.flush();

			result = true;
		} catch (Error e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 读取文件中位图文件
	 * 
	 * @param aFileName
	 *            文件名，包括路径
	 * @return 返回读取的bmp
	 */
	public static Bitmap readBitmapFile(String aFileName) {
		Bitmap bitmap = null;
		File file = new File(aFileName);
		try {
			FileInputStream fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 读取文件中位图文件
	 * 
	 * @param aFileName
	 *            文件名，包括路径
	 * @param aOptions
	 *            option
	 * @return 返回读取的bmp
	 */
	public static Bitmap readBitmapFile(String aFileName, Options aOptions) {
		Bitmap bitmap = null;
		File file = new File(aFileName);
		try {
			FileInputStream fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis, null, aOptions);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	/**
	 * Fills the bitmap's pixels of the specified Color to transparency.
	 * 
	 * @param aBitmap bitmap to process
	 * @param aColor color to fill
	 * @return bmp
	 */
	@SuppressLint("NewApi")
	public static Bitmap eraseBG(Bitmap aBitmap, int aColor) {
	    int width = aBitmap.getWidth();
	    int height = aBitmap.getHeight();
	    Bitmap b = aBitmap.copy(Config.ARGB_8888, true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
            b.setHasAlpha(true);
        }

	    int[] pixels = new int[width * height];
	    aBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

	    for (int i = 0; i < width * height; i++) {
	        if (pixels[i] == aColor) {
	            pixels[i] = 0;
	        }
	    }

	    b.setPixels(pixels, 0, width, 0, 0, width, height);

	    return b;
	}

	/**
	 * drawable转换成bitmap
	 * @param aDrawable drawable
	 * @return bitmap
	 */
	public static Bitmap drawableToBitmap(Drawable aDrawable) {
		// 取 drawable 的长宽  
		int w = aDrawable.getIntrinsicWidth();
		int h = aDrawable.getIntrinsicHeight();
	
		// 取 drawable 的颜色格式  
		Config config = aDrawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
				: Config.RGB_565;
		// 建立对应 bitmap  
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(w, h, config);
		} catch (OutOfMemoryError e) {
			//BdLog.w("bitmap outofmemory error");
		} catch (Exception e) {
			//BdLog.w("unknow exception");
		}
		// 建立对应 bitmap 的画布  
		Canvas canvas = new Canvas(bitmap);
		aDrawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中  
		aDrawable.draw(canvas);
		return bitmap;
	}

    /**
     * recycle a bitmap
     * @param aBitmap bitmap
     */
    public static void recycleBitmap(Bitmap aBitmap) {
        if (aBitmap != null) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                aBitmap.recycle();
            }
        }
    }
}
