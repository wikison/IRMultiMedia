//Inroids Network
//Created by sealy on 2012-12-01.  
//Copyright 2012 Sealy, Inc. All rights reserved.
package inroids.common;

import android.content.Context;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Network Manage.
 *
 * @author Sealy
 */

public abstract class Network {

    private static final String sTag = "IRLibrary";

    /**
     * check Network Available
     *
     * @param context using a context for this function
     */
    //<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    public static boolean isNetworkAvailable(Context context) {
        try {
            android.net.ConnectivityManager cm = (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) {
                MyLog.w(sTag, "Network.isNetworkAvailable ConnectivityManager is null!");
            } else {
                android.net.NetworkInfo netInfo = cm.getActiveNetworkInfo();
                return netInfo != null && netInfo.isConnected();
            }

        } catch (Exception e) {
            MyLog.e(sTag, "Network.isNetworkAvailable:" + e.toString());
        }
        return false;
    }

    /**
     * GPS is Enabled
     *
     * @param context using a context for this function
     * @return true or false
     */
    public static boolean isGpsEnabled(Context context) {
        try {
            LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
            List<String> accessibleProviders = locationManager.getProviders(true);
            return accessibleProviders != null && accessibleProviders.size() > 0;
        } catch (Exception e) {
            MyLog.e(sTag, "Network.isGpsEnabled:" + e.toString());
        }
        return false;
    }

    /**
     * WIFI is Enabled
     *
     * @param context using a context for this function
     * @return true or false
     */
    public static boolean isWifiEnabled(Context context) {
        try {
            android.net.ConnectivityManager mgrConn = (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            TelephonyManager mgrTel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return ((mgrConn.getActiveNetworkInfo() != null && mgrConn.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.CONNECTED) || mgrTel.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
        } catch (Exception e) {
            MyLog.e(sTag, "Network.isWifiEnabled:" + e.toString());
        }
        return false;
    }

    /**
     * 3G is Enabled
     *
     * @param context using a context for this function
     * @return true or false
     */
    public static boolean is3GEnabled(Context context) {
        try {
            android.net.ConnectivityManager connectivityManager = (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetInfo != null && activeNetInfo.getType() == android.net.ConnectivityManager.TYPE_MOBILE;
        } catch (Exception e) {
            MyLog.e(sTag, "Network.is3GEnabled:" + e.toString());
        }
        return false;
    }

    /* @author sichard
    * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
            * @return
            */
    public static final boolean ping() {
        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 2 -w 100 " + ip);// ping网址2次
            // 读取ping的内容，可以不加
//            InputStream input = p.getInputStream();
//            BufferedReader in = new BufferedReader(new InputStreamReader(input));
//            StringBuffer stringBuffer = new StringBuffer();
//            String content = "";
//            while ((content = in.readLine()) != null) {
//                stringBuffer.append(content);
//            }
//            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }

    /**
     * Set the connection timeout(5s) and Socket timeout(5s), and Socket cache size
     *
     * @return BasicHttpParams.
     */
    public static BasicHttpParams setHttpParams() {
        try {
            BasicHttpParams httpParams = new BasicHttpParams();
            org.apache.http.params.HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
            org.apache.http.params.HttpConnectionParams.setSoTimeout(httpParams, 10000);
            org.apache.http.params.HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
            return httpParams;
        } catch (Exception e) {
            MyLog.e(sTag, "Network.setHttpParams:" + e.toString());
        }
        return null;
    }

    /**
     * get string from network(GET)
     *
     * @param sUrl a URL Address.
     * @return this string, or null.
     */
    public static String getString(String sUrl) {
        try {
            HttpGet httpGet = new HttpGet(sUrl);
            DefaultHttpClient httpClient = new DefaultHttpClient(setHttpParams());
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            } else {
                MyLog.w(sTag, "Network.getString:" + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            MyLog.e(sTag, "Network.getString:" + e.toString());
        }
        return null;
    }

    /**
     * get sFile from network(GET)
     *
     * @param sUrl  a URL Address.
     * @param sFile a sFile path.
     * @return true or false.
     */
    public static String getFile(String sUrl, String sFile) {
        if (FileManage.isExistsFile(sFile)) {
            FileManage.delFile(sFile);
        }
        InputStream inStream = null;
        FileOutputStream fOutput = null;
        try {
            HttpGet httpGet = new HttpGet(sUrl);
            DefaultHttpClient httpClient = new DefaultHttpClient(setHttpParams());
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                inStream = response.getEntity().getContent();
                fOutput = new FileOutputStream(sFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = inStream.read(buffer)) != -1) {
                    fOutput.write(buffer, 0, count);
                }
                fOutput.close();
                inStream.close();
                return "true";
            }
        } catch (SocketTimeoutException s) {
            MyLog.e(sTag, "Network.getFile:" + sUrl + " ----error");
        } catch (Exception e) {//Download failed
            if (fOutput != null) {
                try {
                    fOutput.close();
                } catch (IOException e1) {
                    MyLog.e(sTag, "Network.getFile:" + sUrl + " ----error:" + e1.toString());
                }
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e1) {
                    MyLog.e(sTag, "Network.getFile:" + sUrl + " ----error:" + e1.toString());
                }
            }
            MyLog.e(sTag, "Network.getFile:" + sUrl);
        }
        //Delete the incomplete sFile
        if (FileManage.isExistsFile(sFile)) {
            FileManage.delFile(sFile);
        }
        return null;
    }

    /**
     * get sFile from network(POST)
     *
     * @param sUrl  a URL Address.
     * @param sFile a sFile path.
     * @return this string, or null.
     */
    public static String postFile(String sUrl, String sFile) {
        if (FileManage.isExistsFile(sFile)) {
            FileManage.delFile(sFile);
        }
        InputStream inStream = null;
        FileOutputStream fOutput = null;
        try {
            HttpPost httpGet = new HttpPost(sUrl);
            DefaultHttpClient httpClient = new DefaultHttpClient(setHttpParams());
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                inStream = response.getEntity().getContent();
                fOutput = new FileOutputStream(sFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = inStream.read(buffer)) != -1) {
                    fOutput.write(buffer, 0, count);
                }
                fOutput.close();
                inStream.close();
                return "true";
            }
        } catch (Exception e) {//Download failed
            if (fOutput != null) {
                try {
                    fOutput.close();
                } catch (IOException e1) {
                    MyLog.e(sTag, "Network.postFile:" + e1.toString());
                }
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e1) {
                    MyLog.e(sTag, "Network.postFile:" + e1.toString());
                }
            }
            MyLog.e(sTag, "Network.postFile:" + e.toString());
        }
        //Delete the incomplete sFile
        if (FileManage.isExistsFile(sFile)) {
            FileManage.delFile(sFile);
        }
        return null;
    }


    /**
     * get string from network(POST)
     *
     * @param sUrl a URL Address.
     * @return this string, or null.
     */
    public static String postString(String sUrl, String... params) {
        try {
            HttpPost httpPost = new HttpPost(sUrl);
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            for (int i = 0; i < params.length; ) {
                if (i + 1 >= params.length)
                    break;
                if (params[i] != null && params[i + 1] != null)
                    list.add(new BasicNameValuePair(params[i], params[i + 1]));
                i = i + 2;
            }
            httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
            HttpResponse response = new DefaultHttpClient(setHttpParams()).execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            }
        } catch (Exception e) {
            MyLog.e(sTag, "Network.postString:" + e.toString());
        }
        return null;
    }

}
