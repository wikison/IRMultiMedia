package com.mokid.server;

import android.util.Log;

public final class picService {// extends IpicService.stub{

    // PIC 通讯控制命令
    private static final byte CMD_SET_CURRUNT_TIME = (byte)0xA1;

    private static final byte CMD_SET_SHUTDOWM_TIME = (byte)0xA2;

    private static final byte CMD_SET_START_TIME = (byte)0xA3;

    private static final byte CMD_RTC_CHECK = (byte)0xAB;

    private static final byte CMD_GET_CURRUNT_TIME = (byte)0xE1;

    private static final byte CMD_GET_SHUTDOWM_TIME = (byte)0xE2;

    private static final byte CMD_GET_START_TIME = (byte)0xE3;

    private static final byte CMD_RESET_MACHINE = (byte)0xE4;

    private static final byte CMD_LIGHT_ON = (byte)0xC0;

    private static final byte COM_LICHT_OFF = (byte)0xC1;

    private static final byte CMD_LIGHT_ON_STEADY = (byte)0xC2;

    // private static final byte CMD_RTC_CHECK = (byte)0xEB;
    private static final byte CMD_SET_GPIO_OUTPUT = (byte)0xA4;

    private static final byte CMD_SET_GPIO_INPUT = (byte)0xC4;

    public final int GPIOD4 = 114;

    static {

        System.load("/system/lib/libmokoid_runtime.so");
    }

    public picService() {

        Log.i("picService", "Go to get Pic Stub");
        if (PIC_Init() < 0) {
            Log.i("picService", "PIC_Init Open file faild!.....");
            
        } else {
            Log.i("picService", "PIC_Init Open file succeed!");
        }

    }

    /*************************************
     * int SetPICDateTime（String dateTime） 功能：设置PIC的当前时间 输入：日期时间： dateTime 20130608082255 返回：0表示设置成功，-1表示失败
     *****************************************/
    public int SetPICDateTime(String dateTime) {

        return PicSendDateTime(CMD_SET_CURRUNT_TIME, dateTime.getBytes(), dateTime.length());
    }

    /*************************************
     * int SetPICShutDownTime（String dateTime） 功能：设置PIC的关机时间 输入：日期时间： dateTime 20130608082255 返回：0表示设置成功，-1表示失败
     *****************************************/

    public int SetPICShutDownTime(String dateTime) {

        return PicSendDateTime(CMD_SET_SHUTDOWM_TIME, dateTime.getBytes(), dateTime.length());
    }

    /*************************************
     * int SetPICStartUpTime（String dateTime） 功能：设置PIC的自动启动时间 输入：日期时间： dateTime 20130608082255 返回：0表示设置成功，-1表示失败
     *****************************************/
    public int SetPICStartUpTime(String dateTime) {

        return PicSendDateTime(CMD_SET_START_TIME, dateTime.getBytes(), dateTime.length());
    }

    /*************************************
     * int GetPICStartUpTime(StringBuffer dateTime) 功能：查询PIC的自动启动时间 输出：dateTime 20130608082255 返回：0表示查询成功，-1表示失败
     *****************************************/
    public int GetPICStartUpTime(StringBuffer dateTime) {
        String str;
        str = PicReadDateTime(CMD_GET_START_TIME);
        if (str.equalsIgnoreCase("error")) {
            dateTime.append("");
            return -1;
        }
        dateTime.append(str);
        return 0;
    }

    /*************************************
     * int GetPICShutDownTime (StringBuffer dateTime) 功能：查询PIC的自动关机时间 输出：dateTime 20130608082255 返回：0表示查询成功，-1表示失败
     *****************************************/
    public int GetPICShutDownTime(StringBuffer dateTime) {
        String str;
        str = PicReadDateTime(CMD_GET_SHUTDOWM_TIME);
        if (str.equalsIgnoreCase("error")) {
            dateTime.append("");
            return -1;
        }
        dateTime.append(str);
        return 0;
    }

    /*************************************
     * int GetPICCurrentTime (StringBuffer dateTime) 功能：查询PIC的当前PIC的时间 输出：dateTime 20130608082255 返回：0表示查询成功，-1表示失败
     *****************************************/
    public int GetPICCurrentTime(StringBuffer dateTime) {
        String str;
        str = PicReadDateTime(CMD_GET_CURRUNT_TIME);
        if (str.equalsIgnoreCase("error")) {
            dateTime.append("");
            return -1;
        }
        dateTime.append(str);
        return 0;
    }

    /*************************************
     * int WatchDog 功能：发送看门狗命令 输出： 返回：0表示查询成功，-1表示失败
     *****************************************/
    public int WatchDog() {
        String str;
        str = PicWatchDog(CMD_RESET_MACHINE);
        if (str.equalsIgnoreCase("error")) {
            return -1;
        }
        return 0;
    }

    /*************************************
     * int RTCEncryptionCheck 功能：获取RTC加密值 输出： 返回：0表示查询成功，-1表示失败
     *****************************************/
    public int RTCEncryptionCheck(byte[] data, byte[] EncrypteData) {
        return GetRTCEncryption(CMD_RTC_CHECK, data, EncrypteData);
    }

    public int Contrl_Light_ON() {
        String str;
        str = PicWatchDog(CMD_LIGHT_ON);
        if (str.equalsIgnoreCase("error")) {
            return -1;
        }
        return 0;
    }

    /*************************************
     * int SetGPIOOutput（GPIO，byte flag） 功能：设置GPIO为输出 输出：flag 1为高，0为低 返回：0表示查询成功，-1表示失败
     *****************************************/
    public int SetGPIOOutput(int GPIO, byte Status) {
        int iRet = -1;
        byte[] bflag = new byte[1];
        bflag[0] = Status;
        if (GPIO == GPIOD4) {
            iRet = PicCmdNormalSend(CMD_SET_GPIO_OUTPUT, bflag, 1);
        }
        return iRet;
    }

    /*************************************
     * int GetGPIOStatus(int GPIO) 功能：设置GPIO为输入获取GPIO状态 输出： 返回：0表GPIO为低，1表示GPIO为高，-1表示失败
     *****************************************/
    public int SetGPIOInputToGetGPIOStatus(int GPIO) {
        int Status = -1;
        byte[] RcvData = new byte[100];
        if (GPIO == GPIOD4) {
            Status = PicCmdNormalRead(CMD_SET_GPIO_INPUT, RcvData, 100);
        }
        return Status;

    }

    /*************************************
     * int CloseScreen() 功能：关闭休眠 输出： 返回：0表示查询成功，-1表示失败
     *****************************************/
    int CloseScreen() {
        return -1;
    }

    /*************************************
     * int OpenScreen() 功能：系统休眠 输出： 返回：0表示查询成功，-1表示失败
     *****************************************/
    int OpenScreen() {
        return -1;
    }

    public int Contrl_Light_ON_STEADY() {
        String str;
        str = PicWatchDog(CMD_LIGHT_ON_STEADY);
        if (str.equalsIgnoreCase("error")) {
            return -1;
        }
        return 0;
    }

    public int Contrl_Light_OFF() {
        String str;
        str = PicWatchDog(COM_LICHT_OFF);
        if (str.equalsIgnoreCase("error")) {
            return -1;
        }
        return 0;
    }

    public void PIC_close() {
        close();
    }

    private native static int GetRTCEncryption(byte CMD, byte[] data, byte[] EncrypteData);

    private native static int PicSendDateTime(byte CMD, byte[] Data, int lenth);

    private native static String PicReadDateTime(byte CMD);

    private native static String PicWatchDog(byte CMD);

    private native static int PicCmdNormalSend(byte CMD, byte[] data, int leanth);

    private native static int PicCmdNormalRead(byte CMD, byte[] rcvData, int sendlength);

    private native static int PIC_Init();

    private native static void close();
}