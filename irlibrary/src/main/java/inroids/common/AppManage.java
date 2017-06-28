/*
 * Application Manage
 * 
 * Created by sealy on 2013-07-01. 
 * Copyright 2013 Sealy, Inc. All rights reserved.
 */

package inroids.common;

import java.io.File;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

/**
 * User information manage
 * 
 * @author Sealy
 */
public abstract class AppManage {
    private static final String sTag = "IRLibrary";

    /**
     * start new APP
     * 
     * @param context
     *            a context
     * @param sPackage
     *            a APP package name
     * @param sActivity
     *            a APP activity name
     */
    public static boolean startApp(Context context, Shell shRoot, String sPackage, String sActivity) {
        return startApp(context, shRoot, sPackage, sActivity, false);
    }

    /**
     * start new APP
     * 
     * @param context
     *            a context
     * @param sPackage
     *            a APP package name
     * @param sActivity
     *            a APP activity name
     * @param isCommand
     *            Whether to use the command
     */
    public static boolean startApp(Context context, Shell shRoot, String sPackage, String sActivity, boolean isCommand) {
        try {
            if (checkAppActivityState(context, sPackage) == -1) {// Check the package is running
                if (isCommand) {// Use command
                    if (shRoot.getRoot().isRootShell()) {
                        Command cmdT = shRoot.execute("am start -n " + sPackage + File.separator + sActivity);
                        if (cmdT.iExitStatus == 0) {
                            return true;
                        } else {
                            return false;
                        }

                    } else {
                        MyLog.e(sTag, "AppManage.startApp:" + "Is't Root permission");
                        return false;
                    }
                } else {
                    ComponentName comp = new ComponentName(sPackage, sActivity);
                    Intent newIntent = new Intent();
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    newIntent.setComponent(comp);
                    context.startActivity(newIntent);
                    return true;
                }
            } else {
                if (checkAppActivityState(context, sPackage) > 0) {
                    moveTaskToFront(context, sPackage);
                } else {
                    MyLog.e(sTag, "AppManage.startApp:" + "the package is running");
                    return false;
                }
            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.startApp:" + e.toString());
        }
        return false;
    }

    /**
     * start new APP
     * 
     * @param context
     *            a context
     * @param sPackage
     *            a APP package name
     * @param sActivity
     *            a APP activity name
     */
    public static boolean startService(Context context, Shell shRoot, String sPackage, String sService) {
        return startService(context, shRoot, sPackage, sService, false);
    }

    /**
     * start new APP
     * 
     * @param context
     *            a context
     * @param sPackage
     *            a APP package name
     * @param sActivity
     *            a APP activity name
     * @param isCommand
     *            Whether to use the command
     */
    public static boolean startService(Context context, Shell shRoot, String sPackage, String sService,
        boolean isCommand) {
        try {
            if (checkAppServiceState(context, sPackage, sService) == -1) {// Check the package is running
                if (isCommand) {// Use command
                    if (shRoot.getRoot().isRootShell()) {
                        Command cmdT = shRoot.execute("am startservice -n " + sPackage + File.separator + sService);
                        if (cmdT.iExitStatus == 0) {
                            return true;
                        }
                        return false;

                    } else {
                        MyLog.e(sTag, "AppManage.startService:" + "Is't Root permission");
                        return false;
                    }
                } else {
                    ComponentName comp = new ComponentName(sPackage, sService);
                    Intent newIntent = new Intent();
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    newIntent.setComponent(comp);
                    context.startService(newIntent);
                    return true;
                }
            } else {
                MyLog.e(sTag, "AppManage.startService:" + "the package is running");
                return false;
            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.startService:" + e.toString());
        }
        return false;
    }

    /**
     * stop APP
     * 
     * @param context
     *            a context
     * @param sPackage
     *            a APP package name
     */
    public static void stopApp(Context context, Shell shRoot, String sPackage) {
        try {
            // Check Package is running
            if (checkAppState(context, sPackage) >= 0) {
                if (shRoot.getRoot().isRootShell()) {
                    shRoot.execute("am force-stop " + sPackage, "am kill " + sPackage);

                }
            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.stopApp:" + e.toString());
        }
    }

    /**
     * check program is run
     * 
     * @param context
     *            a Context.
     * @param sPackage
     *            run program package name.
     * @return true or false.
     */
    public static int checkAppState(Context context, String sPackage) {
        int iResult = -1;
        int i = -1;
        try {
            ActivityManager actTemp = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningAppProcessInfo> lstRun = actTemp.getRunningAppProcesses();
            for (RunningAppProcessInfo actRun : lstRun) {
                i++;
                if (actRun.processName.equals(sPackage)) {
                    iResult = i;
                    break;
                }
            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.isAppStarted:" + e.toString());
        }
        return iResult;
    }

    /**
     * check program is run
     * 
     * @param context
     *            a Context.
     * @param sPackage
     *            run program package name.
     * @return true or false.
     */
    public static int checkAppActivityState(Context context, String sPackage, String sActivity) {
        int iResult = -1;
        int i = -1;
        try {
            ActivityManager actTemp = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = actTemp.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : runningTasks) {
                i++;
                if (taskInfo.baseActivity.getPackageName().equals(sPackage)
                    && taskInfo.baseActivity.getClassName().equals(sActivity)) {
                    iResult = i;
                    break;
                }
            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.checkAppActivtyIsRun:" + e.toString());
        }
        return iResult;
    }

    /**
     * check program is run
     * 
     * @param context
     *            a Context.
     * @param sPackage
     *            run program package name.
     * @return true or false.
     */
    public static int checkAppActivityState(Context context, String sPackage) {
        int iResult = -1;
        int i = -1;
        try {
            ActivityManager actTemp = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = actTemp.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : runningTasks) {
                i++;
                if (taskInfo.baseActivity.getPackageName().equals(sPackage)) {
                    iResult = i;
                    break;
                }
            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.checkAppActivtyIsRun:" + e.toString());
        }
        return iResult;
    }

    /**
     * check program is run
     * 
     * @param context
     *            a Context.
     * @param sPackage
     *            run program package name.
     * @return true or false.
     */
    public static boolean moveTaskToFront(Context context, String sPackage) {
        boolean isResult = false;
        try {
            ActivityManager actTemp = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = actTemp.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : runningTasks) {
                if (taskInfo.baseActivity.getPackageName().equals(sPackage)) {
                    // System.out.println("sdafasdf:"+taskInfo.id);
                    actTemp.moveTaskToFront(taskInfo.id, 0);
                    isResult = true;
                    break;
                }
            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.checkAppActivtyIsRun:" + e.toString());
        }
        return isResult;
    }

    /**
     * check program is run
     * 
     * @param context
     *            a Context.
     * @param sPackage
     *            run program package name.
     * @return true or false.
     */
    public static int checkAppServiceState(Context context, String sPackage, String sService) {
        int iResult = -1;
        int i = -1;
        try {
            ActivityManager actTemp = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> runningTasks = actTemp.getRunningServices(100);
            for (ActivityManager.RunningServiceInfo taskInfo : runningTasks) {
                i++;
                if (taskInfo.process.equals(sPackage) && taskInfo.service.equals(sService)) {
                    iResult = i;
                    break;
                }
            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.checkAppServiceIsRun:" + e.toString());
        }
        return iResult;
    }

    /**
     * install a APP
     * 
     * @param appPath
     *            run program package name.
     */
    public static boolean installApp(Shell shRoot, String appPath) {
        try {
            if (shRoot.getRoot().isRootShell()) {
                Command cmdT = shRoot.execute("pm install -r " + appPath);
                if (cmdT.iExitStatus == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.installApp:" + e.toString());
        }
        return false;
    }

    /**
     * UnInstall APP
     * 
     * @return true or false.
     */
    public static boolean uninstallApp(Shell shRoot, String appName, boolean keepData) {
        try {
            if (shRoot.getRoot().isRootShell()) {
                String strT = "pm uninstall -k " + appName;
                if (!keepData)
                    strT = "pm uninstall " + appName;
                Command cmdT = shRoot.execute(strT);
                if (cmdT.iExitStatus == 0) {
                    return true;
                }

            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.uninstallApp:" + e.toString());
        }
        return false;
    }

    /**
     * reboot device
     */
    public static void reboot(Shell shRoot) {
        try {
            if (shRoot.getRoot().isRootShell()) {
                shRoot.execute("reboot");
            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.reboot:" + e.toString());
        }
    }

    /**
     * check Package Is Exists
     * 
     * @return true or false.
     */
    public static boolean isExistsPackage(Context context, String sPackage) {
        boolean isR = false;
        try {
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> infoList = pm.getInstalledPackages(PackageManager.GET_SERVICES);
            for (PackageInfo info : infoList) {
                if (sPackage.equals(info.packageName)) {
                    isR = true;
                    break;
                }
            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.isExistsPackage:" + e.toString());
        }
        return isR;
    }

    public static String getPackageInfo(Context context, String appPath) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(appPath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                // String appName = pm.getApplicationLabel(appInfo).toString();
                // String sPackage = appInfo.sPackage; //得到安装包名称

                // System.out.println("appName:"+appName +" sPackage:"+sPackage);
                return appInfo.packageName;
                // String version=info.versionName; //得到版本信息
            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.getPackageInfo:" + e.toString());
        }
        return null;
    }

    public static String getAppVersionName(Context context) {
        String result = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            // 当前版本的版本名称
            result = info.versionName;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static String getAppStartActivity(Context context) {
        String result = "";
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            List<ResolveInfo> rInfos = context.getPackageManager().queryIntentActivities(intent, 0);
            // 获得应用的第一个activity
            for (ResolveInfo rInfo : rInfos) {
                if (rInfo.activityInfo.packageName.equalsIgnoreCase(context.getPackageName())) {
                    result = rInfo.activityInfo.name;
                    break;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static String getAppStartActivity(Context context, String appPath) {
        String result = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pInfo = pm.getPackageArchiveInfo(appPath, PackageManager.GET_ACTIVITIES);
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (pInfo.activities.length > 0)
                result = pInfo.activities[0].name;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static int getAppCode(Context context) {
        int result = 0;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            // 当前版本的版本号
            result = info.versionCode;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static int getAppFileCode(Context context, String appPath) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(appPath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                // ApplicationInfo appInfo = info.applicationInfo;
                return info.versionCode;
            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.getPackageInfo:" + e.toString());
        }
        return 0;
    }

    public static boolean installStartApp(Shell shRoot, String appPath, String sPackage, String sActivity) {
        try {
            if (shRoot.getRoot().isRootShell()) {
                Command cmdT =
                    shRoot.execute("pm install -r " + appPath + "&&am start -n " + sPackage + File.separator
                        + sActivity);
                if (cmdT.iExitStatus == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            MyLog.e(sTag, "AppManage.installApp:" + e.toString());
        }
        return false;

    }
}
