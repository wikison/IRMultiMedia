package inroids.common;

/**
 * 设置开关机时间
 * 
 * @author yf-lc Time Format : "yyyyMMddHHmmss"
 */
public abstract class PIC {

    private static final String sTag = "IRLibrary";

//    // 获取PIC的当前时间
//    public static String getPicCurrentTime(picService PicService) {
//        String result = "";
//        StringBuffer sb = new StringBuffer();
//        int tag = PicService.GetPICCurrentTime(sb);
//        result = tag == 0 ? sb.toString() : "";
//        return result;
//    }
//
//    // 获取PIC当前的开机时间
//    public static String getPicStartUpTime(picService PicService) {
//        String result = "";
//        StringBuffer sb = new StringBuffer();
//        int tag = PicService.GetPICStartUpTime(sb);
//        result = tag == 0 ? sb.toString() : "";
//        return result;
//    }
//
//    // 获取PIC当前的关机时间
//    public static String getPicShutDownTime(picService PicService) {
//        String result = "";
//        StringBuffer sb = new StringBuffer();
//        int tag = PicService.GetPICShutDownTime(sb);
//        result = tag == 0 ? sb.toString() : "";
//        return result;
//    }
//
//    // 设置PIC的开机时间
//    public static int startUp(picService PicService, String eTime) {
//        int hint = -1;
//        hint = PicService.SetPICStartUpTime(eTime);
//        return hint;
//    }
//
//    // 设置PIC的关机时间
//    public static int shutDown(picService PicService, String eTime) {
//        int hint = -1;
//        hint = PicService.SetPICShutDownTime(eTime);
//        return hint;
//    }
//
//    public static void shutDownAlarm(Context context, long intervalTime) {
//        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent("com.android.settings.action.REQUEST_POWER_OFF");
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        am.set(AlarmManager.RTC, intervalTime, pendingIntent);
//    }
//
//    // 关闭主机
//    public static boolean shutDown(Context context, Shell shRoot, boolean isCommand) {
//        try {
//            if (isCommand) {
//                // Use command
//                if (shRoot.getRoot().isRootShell()) {
//                    Command cmdT = shRoot.execute("reboot -p");
//                    if (cmdT.iExitStatus == 0) {
//                        return true;
//                    } else {
//                        return false;
//                    }
//
//                } else {
//                    MyLog.e(sTag, "PIC.shutDown:" + "Is't Root permission");
//                    return false;
//                }
//            } else {
//                Intent intent = new Intent(Intent.ACTION_SHUTDOWN);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//                return true;
//            }
//        } catch (Exception e) {
//            MyLog.e(sTag, "PIC.shutDown:" + e.toString());
//        }
//        return false;
//    }

}
