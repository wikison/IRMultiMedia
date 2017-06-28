package inroids.common;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.format.Formatter;

public class Hardware {
	private static final String sTag="IRLibrary";
	/**
	 * is Tablet computer
	 * @return true or false
	 */
	public static boolean isTablet(Context context) {
		try {
			return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
		} catch (Exception e) {
			MyLog.e(sTag, "Hardware.isTablet:"+e.toString());
		}
		return false;
	}

	/**
	 * get CPU serial
	 * @return CPU Serial(16) or "0000000000000000"
	 */
	public static String getCPUSerial() {
		String str = "", strCPU = "", cpuAddress = "0000000000000000";
		try {
			//read CPU information
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			//Search CPU Serial
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {//Search Serial row
					if (str.indexOf("Serial") > -1) {
						strCPU = str.substring(str.indexOf(":") + 1,str.length());
						cpuAddress = strCPU.trim();
						break;
						}
				}else{break;}
			}
		} catch (IOException e) {
			MyLog.e(sTag, "Hardware.getCPUSerial:"+e.toString());
		}
		return cpuAddress;
	}

	/**
	 * Get ID of the device
	 * @return the string, or null:the ID of the Device.
	 * @param context using a context for this function.
	 */
  	public static String getDeviceID(Context context){
  		try{
  			return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
   		}catch (Exception e) {
   			MyLog.e(sTag, "Hardware.getDeviceID:"+e.toString());
  		}
  		return null;
  	}
  	
  	
  	/**
	 * Get SN of the device
	 * @return the string, or null:the SN of the Device.
	 * @param context using a context for this function.
	 * @param aKey The Prefix string of the SN. 
	 */
  	public static String getSN(Context context){
  		try{
  			String strA=getDeviceID(context);
  			//strA
  			String strT="IR"+strA.toUpperCase()+"0000000000000000";
  			if (strT!=null)
  				return strT.substring(0, 4)+"-"+strT.substring(4, 8)+"-"+strT.substring(8, 12)+"-"+strT.substring(12, 16);
  		}catch (Exception e) {
  			MyLog.e(sTag, "Hardware.getWifiMac:"+e.toString());
  		}
  		return null;
  	}
  	
  	/**
	 * Get MAC address of the wireless card
	 * @return the string, or null.the MAC address of the wireless card
	 * @exception permissions add permissions in the 'Androidmanifest.xml' file.
	 * @exception code {@code<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />}
	 */
  	@SuppressLint("DefaultLocale") 
  	public static String getWifiMac(Context context){
  		try{
  			WifiManager wifT = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
  	        WifiInfo infT = wifT.getConnectionInfo();
  	        return infT.getMacAddress().toUpperCase();
  		}catch (Exception e) {
  			MyLog.e(sTag, "Hardware.getWifiMac:"+e.toString());
        }
  		return null;
  	}
  	
  	/**
	 * Get IP address of the wireless card
	 * @return the string, or null.the MAC address of the wireless card
	 * @exception permissions add permissions in the 'Androidmanifest.xml' file.
	 * @exception code {@code<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />}
	 */
  	public static String  getWifiIp(Context context){  
  		try {
	        WifiManager wifi_service = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
	        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();  
	        return Formatter.formatIpAddress(dhcpInfo.ipAddress);  
	  	}catch (Exception e) {
				MyLog.e(sTag, "Hardware.getWifiMac:"+e.toString());
	    }
			return null;
  	}
  	/**
	 * Get Gateway address of the wireless card
	 * @return the string, or null.the MAC address of the wireless card
	 * @exception permissions add permissions in the 'Androidmanifest.xml' file.
	 * @exception code {@code<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />}
	 */
  	public static String  getWifiGateway(Context context){
  		try {
	  		WifiManager wifi_service = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
	        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();  
	        return Formatter.formatIpAddress(dhcpInfo.gateway);  
	  	}catch (Exception e) {
				MyLog.e(sTag, "Hardware.getWifiGateway:"+e.toString());
	    }
			return null;
  	}
  	/**
	 * Get MAC address of the Ethernet card
	 * @return the MAC address of the Ethernet card:Returns the string, or null. 
	 * @exception permissions add permissions in the 'Androidmanifest.xml' file.
	 * @exception code {@code<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}
	 */
  	public static String getEthernetMac(){
  		try {
  			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
  				NetworkInterface intf = en.nextElement();
  				if (intf.getName().toLowerCase().equals("eth0")) {
  				
  					byte[] bytmac = intf.getHardwareAddress();
  		      		StringBuffer stbT = new StringBuffer();
  		      		for(int intI=0;intI<bytmac.length;intI++){
  		  	    		if(intI!=0){stbT.append(":");}
  		  	    		String strT = Integer.toHexString(bytmac[intI] & 0xFF);
  		  	    		stbT.append(strT.length()==1?0+strT:strT);
  		  	    	}
  		      		return stbT.toString().toUpperCase();
  				}
  			}
      		
      	} catch (Exception e) {
      		MyLog.e(sTag, "Hardware.getEthernetMac:"+e.toString());
        }
  		return null;
  	}
  	
  	/**
	 * Get MAC address of the Ethernet card
	 * @return the MAC address of the Ethernet card:Returns the string, or null. 
	 * @exception permissions add permissions in the 'Androidmanifest.xml' file.
	 * @exception code {@code<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}
	 */
  	public static String getEthernetIp(){
  		try {
  			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
  				NetworkInterface intf = en.nextElement();
  				//|| intf.getName().toLowerCase().equals("wlan0")
  				if (intf.getName().toLowerCase().equals("eth0") ) {
  					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
  						InetAddress inetAddress = enumIpAddr.nextElement();
  						 if (!inetAddress.isLoopbackAddress()) {
  							 String ipaddress = inetAddress.getHostAddress().toString();
  							 if(!ipaddress.contains("::")){
  				  		        return ipaddress;
  				  		       }
  						 }
  					}
  				}
  			}
  		} catch (Exception e) {
  			MyLog.e(sTag, "Hardware.getEthernetIp:"+e.toString());
        }
  		return null;
  	}
}
