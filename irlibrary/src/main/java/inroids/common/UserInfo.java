/*
 * Get or Save setting information to SharedPreference
 * 
 * Created by sealy on 2013-07-01. 
 * Copyright 2013 Sealy, Inc. All rights reserved.
 */

package inroids.common;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * User information manage
 * 
 * @author Sealy
 */
public abstract class UserInfo {
	public static final String xmlFileName = "user_Info";
	private static final String sTag = "IRLibrary";
	private static final int[] MODES = new int[] { Activity.MODE_PRIVATE,// 默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中，可以使用Activity.MODE_APPEND
			Activity.MODE_WORLD_READABLE,// 表示当前文件可以被其他应用读取，
			Activity.MODE_WORLD_WRITEABLE,// 表示当前文件可以被其他应用写入；
											// 如果希望文件被其他应用读和写，可以传入:Activity.MODE_WORLD_READABLE+Activity.MODE_WORLD_WRITEABLE
			Activity.MODE_APPEND // 该模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件
	};

	/**
	 * Save String value to preference,xmlFileName="user_Info"
	 * 
	 * @param context
	 *            using a context for this function
	 * @param sKey
	 *            The name of the preference to modify.
	 * @param sValue
	 *            The new value for the preference.
	 */
	public static boolean setString(Context context, String sKey, String sValue) {
		return setString(context, xmlFileName, sKey, sValue);
	}

	/**
	 * Save String value to preference
	 * 
	 * @param context
	 *            using a context for this function
	 * @param xmlFileName
	 *            The Name of the XML file
	 * @param sKey
	 *            The name of the preference to modify.
	 * @param sValue
	 *            The new value for the preference.
	 */
	public static boolean setString(Context context, String xmlFileName,
			String sKey, String sValue) {
		try {
			SharedPreferences settings = context.getSharedPreferences(
					xmlFileName, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(sKey, sValue);
			return editor.commit();
		} catch (Exception e) {
			MyLog.e(sTag, "UserInfo.setString:" + e.toString());
		}
		return false;
	}

	/**
	 * delete setting information with "sKey",xmlFileName="user_Info"
	 * 
	 * @param context
	 *            using a context for this function
	 * @param sKey
	 *            The name of the preference to modify.
	 */
	public static boolean delSettings(Context context, String sKey) {
		return delSettings(context, xmlFileName, sKey);
	}

	/**
	 * delete setting information with "sKey"
	 * 
	 * @param context
	 *            using a context for this function
	 * @param xmlFileName
	 *            The Name of the XML file
	 * @param sKey
	 *            The name of the preference to modify.
	 */
	public static boolean delSettings(Context context, String xmlFileName,
			String sKey) {
		try {
			SharedPreferences settings = context.getSharedPreferences(
					xmlFileName, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.remove(sKey);
			return editor.commit();
		} catch (Exception e) {
			MyLog.e(sTag, "UserInfo.delSettings:" + e.toString());
		}
		return false;
	}

	/**
	 * Get All to Map<String,?>
	 * 
	 * @param context
	 *            using a context for this function
	 */
	public static Map<String, ?> getAll(Context context) {
		return getAll(context, xmlFileName);
	}

	/**
	 * Get All to Map<String,?>
	 * 
	 * @param context
	 *            using a context for this function
	 * @param xmlFileName
	 *            The Name of the XML file
	 * @param sKey
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference.
	 */
	public static Map<String, ?> getAll(Context context, String xmlFileName) {
		try {
			SharedPreferences settings = context.getSharedPreferences(
					xmlFileName, 0);
			return settings.getAll();
		} catch (Exception e) {
			MyLog.e(sTag, "UserInfo.getAll:" + e.toString());
		}
		return null;
	}

	/**
	 * Save integer value to preference,xmlFileName="user_Info"
	 * 
	 * @param context
	 *            using a context for this function
	 * @param sKey
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference.
	 */
	public static boolean setInt(Context context, String sKey, int value) {
		return setInt(context, xmlFileName, sKey, value);
	}

	/**
	 * Save integer value to preference
	 * 
	 * @param context
	 *            using a context for this function
	 * @param xmlFileName
	 *            The Name of the XML file
	 * @param sKey
	 *            The name of the preference to modify.
	 * @param iValue
	 *            The new value for the preference.
	 */
	public static boolean setInt(Context context, String xmlFileName,
			String sKey, int iValue) {
		try {
			SharedPreferences settings = context.getSharedPreferences(
					xmlFileName, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(sKey, iValue);
			return editor.commit();
		} catch (Exception e) {
			MyLog.e(sTag, "UserInfo.setInt:" + e.toString());
		}
		return false;
	}

	/**
	 * Save long value to preference,xmlFileName="user_Info"
	 * 
	 * @param context
	 *            using a context for this function
	 * @param sKey
	 *            The name of the preference to modify.
	 * @param iValue
	 *            The new value for the preference.
	 */
	public static boolean setLong(Context context, String sKey, long iValue) {
		return setLong(context, xmlFileName, sKey, iValue);
	}

	/**
	 * Save long value to preference
	 * 
	 * @param context
	 *            using a context for this function
	 * @param xmlFileName
	 *            The Name of the XML file
	 * @param sKey
	 *            The name of the preference to modify.
	 * @param iValue
	 *            The new value for the preference.
	 */
	public static boolean setLong(Context context, String xmlFileName,
			String sKey, long iValue) {
		try {
			SharedPreferences settings = context.getSharedPreferences(
					xmlFileName, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putLong(sKey, iValue);
			return editor.commit();
		} catch (Exception e) {
			MyLog.e(sTag, "UserInfo.setLong:" + e.toString());
		}
		return false;
	}

	/**
	 * Save float value to preference,xmlFileName="user_Info"
	 * 
	 * @param context
	 *            using a context for this function
	 * @param sKey
	 *            The name of the preference to modify.
	 * @param fValue
	 *            The new value for the preference.
	 */
	public static boolean setFloat(Context context, String sKey, float fValue) {
		return setFloat(context, xmlFileName, sKey, fValue);
	}

	/**
	 * Save float value to preference
	 * 
	 * @param context
	 *            using a context for this function
	 * @param xmlFileName
	 *            The Name of the XML file
	 * @param sKey
	 *            The name of the preference to modify.
	 * @param fValue
	 *            The new value for the preference.
	 */
	public static boolean setFloat(Context context, String xmlFileName,
			String sKey, float fValue) {
		try {
			SharedPreferences settings = context.getSharedPreferences(
					xmlFileName, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putFloat(sKey, fValue);
			return editor.commit();
		} catch (Exception e) {
			MyLog.e(sTag, "UserInfo.setFloat:" + e.toString());
		}
		return false;
	}

	/**
	 * Save boolean value to preference,xmlFileName="user_Info"
	 * 
	 * @param context
	 *            using a context for this function
	 * @param sKey
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference.
	 */
	public static boolean setBoolean(Context context, String sKey,
			boolean isValue) {
		return setBoolean(context, xmlFileName, sKey, isValue);
	}

	/**
	 * Save boolean value to preference
	 * 
	 * @param context
	 *            using a context for this function
	 * @param xmlFileName
	 *            The Name of the XML file
	 * @param sKey
	 *            The name of the preference to modify.
	 * @param isValue
	 *            The new value for the preference.
	 */
	public static boolean setBoolean(Context context, String xmlFileName,
			String sKey, boolean isValue) {
		try {
			SharedPreferences settings = context.getSharedPreferences(
					xmlFileName, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(sKey, isValue);
			return editor.commit();
		} catch (Exception e) {
			MyLog.e(sTag, "UserInfo.setBoolean:" + e.toString());
		}
		return false;
	}

	/**
	 * Get string value from preference,xmlFileName="user_Info"
	 * 
	 * @param context
	 *            using a context for this function
	 * @param sKey
	 *            The name of the preference to retrieve.
	 * @return the value of string:Returns the preference value if it exists, or
	 *         null.
	 */
	public static String getString(Context context, String sKey) {
		return getString(context, xmlFileName, sKey);
	}

	/**
	 * Get string value from preference
	 * 
	 * @param context
	 *            using a context for this function
	 * @param xmlFileName
	 *            The Name of the XML file
	 * @param sKey
	 *            The name of the preference to retrieve.
	 * @return the value of string:Returns the preference value if it exists, or
	 *         null.
	 */
	public static String getString(Context context, String xmlFileName,
			String sKey) {
		try {
			SharedPreferences settings = context.getSharedPreferences(
					xmlFileName, 0);
			return settings.getString(sKey, null);
		} catch (Exception e) {
			MyLog.e(sTag, "UserInfo.getString:" + e.toString());
		}
		return null;
	}

	/**
	 * Get integer value from preference,xmlFileName="user_Info"
	 * 
	 * @param context
	 *            using a context for this function
	 * @param sKey
	 *            The name of the preference to retrieve.
	 * @return the value of integer:Returns the preference value if it exists,
	 *         or 0.
	 */
	public static int getInt(Context context, String sKey) {
		return getInt(context, xmlFileName, sKey);
	}

	/**
	 * Get integer value from preference
	 * 
	 * @param context
	 *            using a context for this function
	 * @param xmlFileName
	 *            The Name of the XML file
	 * @param sKey
	 *            The name of the preference to retrieve.
	 * @return the value of integer:Returns the preference value if it exists,
	 *         or 0.
	 */
	public static int getInt(Context context, String xmlFileName, String sKey) {
		try {
			SharedPreferences settings = context.getSharedPreferences(
					xmlFileName, 0);
			return settings.getInt(sKey, 0);
		} catch (Exception e) {
			MyLog.e(sTag, "UserInfo.getInt:" + e.toString());
		}
		return 0;
	}

	/**
	 * Get long value from preference,xmlFileName="user_Info"
	 * 
	 * @param context
	 *            using a context for this function
	 * @param sKey
	 *            The name of the preference to retrieve.
	 * @return the value of long:Returns the preference value if it exists, or
	 *         0.
	 */
	public static long getLong(Context context, String sKey) {
		return getLong(context, xmlFileName, sKey);
	}

	/**
	 * Get long value from preference
	 * 
	 * @param context
	 *            using a context for this function
	 * @param xmlFileName
	 *            The Name of the XML file
	 * @param sKey
	 *            The name of the preference to retrieve.
	 * @return the value of long:Returns the preference value if it exists, or
	 *         0.
	 */
	public static long getLong(Context context, String xmlFileName, String sKey) {
		try {
			SharedPreferences settings = context.getSharedPreferences(
					xmlFileName, 0);
			return settings.getLong(sKey, 0);
		} catch (Exception e) {
			MyLog.e(sTag, "UserInfo.getLong:" + e.toString());
		}
		return 0;
	}

	/**
	 * Get float value from preference,xmlFileName="user_Info"
	 * 
	 * @param context
	 *            using a context for this function
	 * @param sKey
	 *            The name of the preference to retrieve.
	 * @return the value of float:Returns the preference value if it exists, or
	 *         0.
	 */
	public static float getFloat(Context context, String sKey) {
		return getFloat(context, xmlFileName, sKey);
	}

	/**
	 * Get float value from preference
	 * 
	 * @param context
	 *            using a context for this function
	 * @param xmlFileName
	 *            The Name of the XML file
	 * @param sKey
	 *            The name of the preference to retrieve.
	 * @return the value of float:Returns the preference value if it exists, or
	 *         0.
	 */
	public static float getFloat(Context context, String xmlFileName,
			String sKey) {
		try {
			SharedPreferences settings = context.getSharedPreferences(
					xmlFileName, 0);
			return settings.getFloat(sKey, 0);
		} catch (Exception e) {
			MyLog.e(sTag, "UserInfo.getFloat:" + e.toString());
		}
		return 0;
	}

	/**
	 * Get boolean value from preference,xmlFileName="user_Info"
	 * 
	 * @param context
	 *            using a context for this function
	 * @param sKey
	 *            The name of the preference to retrieve.
	 * @return the value of boolean:Returns the preference value if it exists,
	 *         or false.
	 */
	public static boolean getBoolean(Context context, String sKey) {
		return getBoolean(context, xmlFileName, sKey);
	}

	/**
	 * Get boolean value from preference
	 * 
	 * @param context
	 *            using a context for this function
	 * @param xmlFileName
	 *            The Name of the XML file
	 * @param sKey
	 *            The name of the preference to retrieve.
	 * @return the value of boolean:Returns the preference value if it exists,
	 *         or false.
	 */
	public static boolean getBoolean(Context context, String xmlFileName,
			String sKey) {
		try {
			SharedPreferences settings = context.getSharedPreferences(
					xmlFileName, 0);
			return settings.getBoolean(sKey, false);
		} catch (Exception e) {
			MyLog.e(sTag, "UserInfo.getBoolean:" + e.toString());
		}
		return false;
	}

}
