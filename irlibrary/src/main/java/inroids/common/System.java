//System function
//Created by sealy on 2012-12-01.  
//Copyright 2012 Sealy, Inc. All rights reserved.

package inroids.common;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;

/**
 * system public function
 * 
 * @author Sealy
 */
public abstract class System {
    private static final String sTag = "IRLibrary";

    /**
     * hint information with toast
     * 
     * @param context
     *            using a context for this function
     * @param sHint
     *            The new value for the preference.
     * @param iGravity
     *            Toast position
     */
    public static void hintWithToast(Context context, String sHint, int iGravity) {
        android.widget.Toast toast = android.widget.Toast.makeText(context, sHint, android.widget.Toast.LENGTH_LONG);
        toast.setGravity(iGravity, 0, 0);
        toast.show();
    }

    /**
     * hint information with toast
     * 
     * @param context
     *            using a context for this function
     * @param sHint
     *            The new value for the preference.
     */
    public static void hintWithToast(Context context, String sHint) {
        android.widget.Toast.makeText(context, sHint, android.widget.Toast.LENGTH_LONG).show();
    }

    /**
     * check value is't null
     * 
     * @param sValue
     *            a check string
     * @return boolean if value is't null and value.length()>0 return true else false.
     */
    public static boolean isNotNullString(String sValue) {
        return sValue != null && sValue.length() > 0;
    }

    /**
     * Start Thread
     * 
     * @exception startThread
     *                When network download file
     */
    /*
     * public static void startThread(){ try{ StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
     * .detectDiskReads() .detectDiskWrites() .detectNetwork() .penaltyMyLog() .build());
     * 
     * StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder() .detectLeakedSqlLiteObjects()
     * .detectLeakedClosableObjects() .penaltyMyLog() .penaltyDeath() .build()); }catch (Exception e) { MyLog.e(sTag,
     * "System.startThread:"+e.toString()); } }
     */

    /**
     * get ROOT Path
     * 
     * @param isCheckSDCard
     *            is check SDCard folder
     * @return String or null
     */
    // <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    public static String getRootPath(boolean isCheckSDCard) {
        String sRoot = "/mnt";
        try {
            if (FileManage.isExistsFolder(sRoot + "/flash")) {
                sRoot = sRoot + "/flash";
            } else {
                if (isCheckSDCard) {
                    boolean isSDCardExists = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
                    if (isSDCardExists) {
                        sRoot = Environment.getExternalStorageDirectory().getPath();
                    } else {
                        sRoot = Environment.getDataDirectory().getPath();
                    }
                } else {
                    sRoot = "/mnt/sdcard";
                }
            }
            String sTemp = sRoot + "/inroids";
            FileManage.createFolder(sTemp);
            return sTemp;
        } catch (Exception e) {
            MyLog.e(sTag, "System.getRootPath:" + e.toString());
        }
        return sRoot;
    }

    public static boolean isBox(String rootPath) {
        return rootPath.equals("/mnt/flash/inroids");
    }

    /**
     * get Main Path
     * 
     * @param sRootPath
     *            root path
     * @param sKey
     *            create folder in Root path
     * @return String or null
     */
    public static String getMainPath(String sRootPath, String sKey) {
        try {
            if (isNotNullString(sRootPath) && isNotNullString(sKey)) {
                String sTemp = sRootPath + File.separator + sKey.toLowerCase();
                FileManage.createFolder(sTemp);
                return sTemp;
            }
        } catch (Exception e) {
            MyLog.e(sTag, "System.getMainPath:" + e.toString());
        }
        return null;
    }

    /**
     * get res Path
     * 
     * @param sMainPath
     *            Main Path
     * @return String or null
     */
    public static String getResPath(String sMainPath) {
        try {
            if (isNotNullString(sMainPath)) {
                String sTemp = sMainPath + "/res";
                FileManage.createFolder(sTemp); // Resource Path
                return sTemp;
            }
        } catch (Exception e) {
            MyLog.e(sTag, "System.getResPath:" + e.toString());
        }
        return null;
    }

    /**
     * getApkPath
     * 
     * @return the string, or null.
     * @param context
     *            using a context for this function.
     */
    public static String getApkPath(Context context) {
        try {
            return context.getApplicationContext().getFilesDir().getAbsolutePath();
        } catch (Exception e) {
            MyLog.e(sTag, "System.getApkPath:" + e.toString());
        }
        return null;
    }

    /**
     * getApkPath
     * 
     * @return the string, or null.
     * @param context
     *            using a context for this function.
     */
    public static void setAllScreen(Activity context) {
        try {
            context.requestWindowFeature(Window.FEATURE_NO_TITLE); // hide Title
            context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// set All Screen
        } catch (Exception e) {
            MyLog.e(sTag, "System.setAllScreen:" + e.toString());
        }
    }

    /**
     * get Display Width
     * 
     * @param context
     *            using a context for this function
     * @param display
     *            a default display
     * @return string Return run package from inroids.irs file.
     */
    public static int getDisplayWidth(Context context, String sDisplay) {
        if (isNotNullString(sDisplay)) {
            int iStart = sDisplay.indexOf("x");
            if (iStart >= 0) {
                return Integer.valueOf(sDisplay.substring(0, iStart));
            }
        }
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * get Display Height
     * 
     * @param context
     *            using a context for this function
     * @param display
     *            a default display
     * @return string Return run package from inroids.irs file.
     */
    public static int getDisplayHeight(Context context, String sDisplay) {
        if (isNotNullString(sDisplay)) {
            int iStart = sDisplay.indexOf("x");
            if (iStart >= 0) {
                return Integer.valueOf(sDisplay.substring(iStart + 1));
            }
        }
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * update System time
     * 
     * @param sValue
     *            a DateTime String JSON String
     * @param sShell
     *            a root Shell
     * @return boolean if value is't null and value.length()>0 return true else false.
     */
    public static boolean updateSystemTime(Shell sShell, String sValue) {
        if (isNotNullString(sValue)) {
            String strKey = "\"t\":\"";
            if (sValue.contains(strKey)) {
                int iStart = sValue.indexOf(strKey);
                int iEnd = sValue.indexOf("\"", iStart + strKey.length());
                if (iStart >= 0 && iEnd >= 0) {
                    String sTime = sValue.substring(iStart + strKey.length(), iEnd);
                    if (isNotNullString(sTime)) {
                        DateTime.updateSystemTime(sShell, sTime);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * get String from Setting(inroids.irs)
     * 
     * @param sFile
     *            a file path
     * @param sKey
     *            a key of value
     * @return string Return run package from inroids.irs file.
     */
    public static String getStringFromSetting(String sFile, String sKey) {
        if (inroids.common.FileManage.isExistsFile(sFile)) {
            try {
                String strT = inroids.common.FileManage.getStringFromFile(sFile);
                if (strT != null && strT.length() > 0) {
                    String sTemp = "\"" + sKey + "\":";
                    int iStart = strT.indexOf(sTemp);
                    if (iStart >= 0) {
                        int iEnd = strT.indexOf("\"", iStart + sTemp.length() + 1);
                        if (iEnd >= 0) {
                            return strT.substring(iStart + sTemp.length() + 1, iEnd);
                        }
                    }
                }
            } catch (Exception e) {
                MyLog.e(sTag, "System.getStringFromSetting:" + e.toString());
            }
        }
        return null;
    }

    /**
     * set String to Setting(inroids.irs)
     * 
     * @param sFile
     *            a file path
     * @param sKey
     *            a key of value
     * @param sValue
     *            save a value to setting file
     * @return boolean if value is't null and value.length()>0 return true else false.
     */
    public static boolean setStringFromSetting(String sFile, String sKey, String sValue) {
        if (inroids.common.FileManage.isExistsFile(sFile)) {// file is exists
            try {
                String strT = inroids.common.FileManage.getStringFromFile(sFile);// get string from file
                if (strT != null && strT.length() > 0) {
                    String sTemp = "\"" + sKey + "\":";
                    int iStart = strT.indexOf(sTemp);
                    if (iStart >= 0) {
                        int iEnd = strT.indexOf("\"", iStart + sTemp.length() + 1);
                        if (iEnd >= 0) {
                            String str1 = "\"" + sKey + "\":\"" + sValue + "\"";
                            String str2 = FileManage.getStringFromFile(sFile);
                            String str3 =
                                str2.replace(sTemp + "\"" + strT.substring(iStart + sTemp.length() + 1, iEnd) + "\"",
                                    str1);
                            FileManage.saveStringToFile(sFile, str3);
                        } else {
                            String str1 = "{\"" + sKey + "\":\"" + sValue + "\",";
                            String str2 = FileManage.getStringFromFile(sFile);
                            String str3 = str2.replace("{", str1);
                            FileManage.saveStringToFile(sFile, str3);
                        }
                    } else {
                        String str1 = "{\"" + sKey + "\":\"" + sValue + "\",";
                        String str2 = FileManage.getStringFromFile(sFile);
                        String str3 = str2.replace("{", str1);
                        FileManage.saveStringToFile(sFile, str3);
                    }
                } else {
                    // String str1="{\""+sKey+"\":\""+sValue+"\",";
                    // String str2=FileManage.getStringFromFile(sFile);
                    // String str3=str2.replace("{", str1);
                    // FileManage.saveStringToFile( sFile, str3);
                    String str1 = "{\"" + sKey + "\":\"" + sValue + "\"}";
                    FileManage.saveStringToFile(sFile, str1);
                }

            } catch (Exception e) {
                MyLog.e(sTag, "System.setStringFromSetting:" + e.toString());
                return false;
            }
        } else {// file is not exists
            String strT = "{\"" + sKey + "\":\"" + sValue + "\"}";
            FileManage.saveStringToFile(sFile, strT);
        }
        return true;
    }
}
