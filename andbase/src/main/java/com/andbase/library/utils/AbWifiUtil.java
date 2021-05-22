package com.andbase.library.utils;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import androidx.annotation.RequiresPermission;
import android.telephony.TelephonyManager;

public class AbWifiUtil {
	
	
	/**
	 * 打开wifi.
	 * @param context
	 * @param enabled
	 * @return
	 */
	public static void setWifiEnabled(Context context,boolean enabled){
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if(enabled){
			wifiManager.setWifiEnabled(true);
		}else{
			wifiManager.setWifiEnabled(false);
		}
	}
	
	/**
	 * 
	 * 是否有网络连接.
	 * @param context
	 * @return
	 */
	@RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
	public static boolean isConnectivity(Context context) {
		
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return ((connectivityManager.getActiveNetworkInfo() != null && connectivityManager
				.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || telephonyManager
				.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}
	
	/**
	 * 判断当前网络是否是wifi网络.
	 *
	 * @param context the context
	 * @return boolean
	 */
	@RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
	public static boolean isWifiConnectivity(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 得到所有的WiFi列表.
	 * @param context
	 * @return
	 */
	public static List<ScanResult> getScanResults(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> list = null;
		//开始扫描WiFi
		boolean f = wifiManager.startScan();
		if(!f){
			getScanResults(context);
		}else{
			// 得到扫描结果
			list = wifiManager.getScanResults();
		}
		
		return list;
	}

	/**
	 * 
	 * 根据SSID过滤扫描结果.
	 * @param context
	 * @param bssid
	 * @return
	 */
	public static ScanResult getScanResultsByBSSID(Context context,String bssid) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		ScanResult scanResult = null;
		//开始扫描WiFi
		boolean f = wifiManager.startScan();
		if(!f){
			getScanResultsByBSSID(context,bssid);
		}
		// 得到扫描结果
		List<ScanResult> list = wifiManager.getScanResults();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				// 得到扫描结果
				scanResult = list.get(i);
				if (scanResult.BSSID.equals(bssid)) {
					break;
				}
			}
		}
		return scanResult;
	}

	/**
	 *
	 * 获取连接的WIFI信息.
	 * @param context
	 * @return
	 */
	public static WifiInfo getConnectionInfo(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo;
	}

	/**
	 *
	 * 获取mac地址.
	 * @param context
	 * @return
	 */
	public static String getMac(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (info.getMacAddress() == null) {
			return null;
		} else {
			return info.getMacAddress();
		}
	}

	/**
	 *
	 * 获取SSID地址.
	 * @param context
	 * @return
	 */
	public static String getSSID(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (info.getSSID() == null) {
			return null;
		} else {
			return info.getSSID();
		}
	}
	
	
}
