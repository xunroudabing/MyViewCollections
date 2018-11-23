package com.xunroudabing.myviewscollections.utils;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


public class SysUtils {
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}

	public static float getScreenDensity(Context context) {
		try {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager manager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			manager.getDefaultDisplay().getMetrics(dm);
			return dm.density;
		} catch (Exception ex) {

		}
		return 1.0f;
	}

	public static boolean isChinese(CharSequence sequence) {
		for (int i = 0; i < sequence.length(); i++) {
			char c = sequence.charAt(i);
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	public static int getLength(CharSequence sequence) {
		double count = 0;
		for (int i = 0; i < sequence.length(); i++) {
			char c = sequence.charAt(i);
			if (isChinese(c)) {
				count++;
			} else {
				count = count + 0.5;
			}
		}
		return (int) Math.round(count);
	}

	public static String getRealPathFromURI(Activity activity, Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.managedQuery(contentUri, proj, null, null,
				null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * 检查网络wifi 2G 3G网络
	 * 
	 * @return TODO
	 */
	public static boolean isOpenNet(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}

	/**
	 * 用来判断服务是否在运行.
	 * 
	 * @param mContext
	 *            上下文
	 * @param className
	 *            判断的服务名字
	 * @return isRunning ：true 在运行 、false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		// 默认标记：为false
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取正在运行的服务
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		// 如果没有，那么返回false
		if (!(serviceList.size() > 0)) {
			return false;
		}
		// 如果有，那么迭代List，判断是否有当前某个服务
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/**
	 * 判断程序是否在当前前台运行
	 * 
	 * @return true程序在当前前台运行、false的时候程序不在当前前台运行
	 */
	public static boolean isRunningForeground(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// The activity component at the top of the history stack of the task.
		// This is what the user is currently doing.
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		if (!TextUtils.isEmpty(currentPackageName)
				&& currentPackageName.equals(context.getPackageName())) {
			return true;
		}
		return false;
	}

	/**
	 * 程序是否在前台队列中运行
	 * 
	 * @param mContext
	 *            上下文
	 * @return true 标识是在队列里、false标识不在前台桟列
	 */
	public static boolean isAppOnForeground(Context mContext) {
		// Returns a list of application processes that are running on the
		// device
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = mContext.getApplicationContext().getPackageName();
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	public static String random(int length) {
		String seed = "1234567890abcdefghijklmnopkrstuvxwxyz";
		Random random = new Random();
		char[] ret = new char[length];
		for (int i = 0; i < length; i++) {
			char[] array = seed.toCharArray();
			int index = random.nextInt(array.length);
			char c = array[index];
			ret[i] = c;
		}
		return new String(ret);
	}

	public static String toUpper(String string) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (Character.isLowerCase(c)) {
				buffer.append(Character.toUpperCase(c));
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}
}
