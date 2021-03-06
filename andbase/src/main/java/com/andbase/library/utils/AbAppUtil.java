package com.andbase.library.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.LauncherActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.VideoView;

import com.andbase.library.cache.AbDiskCache;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;


/**
 * Copyright ymbok.com
 * Author ???????????????
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info APP?????????
 */
public class AbAppUtil {

	/**
	 * ?????????????????????.
	 *
	 * @param context the context
	 * @param apKFile apk??????
	 * @param authority
	 */
	public static void installApk(Context context, File apKFile,String authority) {
		if (context == null || apKFile == null) {
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		//?????????????????????7.0??????
		if (Build.VERSION.SDK_INT >= 24) {
			Uri apkUri = FileProvider.getUriForFile(context,authority,apKFile);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
		} else {
			intent.setDataAndType(Uri.fromFile(apKFile), "application/vnd.android.package-archive");
		}
		context.startActivity(intent);
	}
	
	/**
	 * ????????????.
	 *
	 * @param context the context
	 * @param packageName ??????
	 */
	public static void uninstallApk(Context context,String packageName) {
		Intent intent = new Intent(Intent.ACTION_DELETE);
		Uri packageURI = Uri.parse("package:" + packageName);
		intent.setData(packageURI);
		context.startActivity(intent);
	}


	/**
	 * ??????????????????????????????.
	 *
	 * @param context the context
	 * @param className ????????????????????? "com.xxx.xx..XXXService"
	 * @return true ????????? false ????????????
	 */
	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
		Iterator<RunningServiceInfo> l = servicesList.iterator();
		while (l.hasNext()) {
			RunningServiceInfo si = (RunningServiceInfo) l.next();
			if (className.equals(si.service.getClassName())) {
				isRunning = true;
			}
		}
		return isRunning;
	}

	/**
	 * ????????????.
	 *
	 * @param context the context
	 * @param className the class name
	 * @return true, if successful
	 */
	public static boolean stopRunningService(Context context, String className) {
		Intent intent_service = null;
		boolean ret = false;
		try {
			intent_service = new Intent(context, Class.forName(className));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (intent_service != null) {
			ret = context.stopService(intent_service);
		}
		return ret;
	}

	/**
	 * ?????????????????????APP
	 * @param context the context
	 */
	public static boolean isInstallApk(Context context,String packageName) {
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		if (packageInfos != null) {
			for (int i = 0; i < packageInfos.size(); i++) {
				String packageInfo = packageInfos.get(i).packageName;
				if (packageInfo.equals(packageName)) {
					return true;
				}
			}
		}
		return false;
	}
	

	/** 
	 * Gets the number of cores available in this device, across all processors. 
	 * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu" 
	 * @return The number of cores, or 1 if failed to get result 
	 */ 
	public static int getNumCores() { 
		try { 
			//Get directory containing CPU info 
			File dir = new File("/sys/devices/system/cpu/"); 
			//Filter to only list the devices we care about 
			File[] files = dir.listFiles(new FileFilter(){

				@Override
				public boolean accept(File pathname) {
					//Check if filename is "cpu", followed by a single digit number 
					if(Pattern.matches("cpu[0-9]", pathname.getName())) { 
					   return true; 
				    } 
				    return false; 
				}
				
			}); 
			//Return the number of cores (virtual CPU devices) 
			return files.length; 
		} catch(Exception e) { 
			e.printStackTrace();
			return 1; 
		} 
	}
	
	
	/**
	 * ????????????????????????.
	 *
	 * @param context the context
	 * @return true, if is network available
	 */
	@RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
	public static boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	/**
	 * Gps????????????
	 * ??????<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />??????
	 *
	 * @param context the context
	 * @return true, if is gps enabled
	 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
	    return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}


	/**
	 * ????????????????????????.
	 * @param context the context
	 * @return string
	 */
	@RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
	public static String isNetworkType(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null){
			if(activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE){
				return "mobile";
			}else if(activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return "wifi";
			}
		}
		return  "unknow";
	}
	
	/**
	 * ???????????????.
	 *
	 * @param context the context
	 * @param dbName the db name
	 * @param rawRes the raw res
	 * @return true, if successful
	 */
    public static boolean importDatabase(Context context,String dbName,int rawRes) {
		int buffer_size = 1024;
		InputStream is = null;
		FileOutputStream fos = null;
		boolean flag = false;
		
		try {
			String dbPath = "/data/data/"+context.getPackageName()+"/databases/"+dbName; 
			File dbfile = new File(dbPath);
			//?????????????????????????????????????????????????????????????????????????????????????????????
			if (!dbfile.exists()) {
				//?????????????????????
				if(!dbfile.getParentFile().exists()){
					dbfile.getParentFile().mkdirs();
				}
				dbfile.createNewFile();
				is = context.getResources().openRawResource(rawRes); 
				fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[buffer_size];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
				   fos.write(buffer, 0, count);
				}
				fos.flush();
			}
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (Exception e) {
				}
			}
			if(is!=null){
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
		return flag;
	}
    
    /**
     * ???????????????????????????.
     *
     * @param context the context
     * @return mDisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources mResources;
        if (context == null){
            mResources = Resources.getSystem();
            
        }else{
            mResources = context.getResources();
        }
        //DisplayMetrics{density=1.5, width=480, height=854, scaledDensity=1.5, xdpi=160.421, ydpi=159.497}
        //DisplayMetrics{density=2.0, width=720, height=1280, scaledDensity=2.0, xdpi=160.42105, ydpi=160.15764}
        DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
        return mDisplayMetrics;
    }
    
    /**
     * ????????????.
     * @param context the context
     */
    public static void showInputMethod(Context context){
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    
    /**
     * ??????????????????.
     * @param context the context
     */
    public static void closeInputMethod(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager)context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && ((Activity)context).getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(((Activity)context).getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    
    /**
     * ???????????????.
     * @param context the context
     */
    public static PackageInfo getPackageInfo(Context context) {
    	PackageInfo info = null;
	    try {
	        String packageName = context.getPackageName();
	        info = context.getPackageManager().getPackageInfo(packageName,PackageManager.GET_ACTIVITIES);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return info;
    }
    
    /**
     * 
     * ?????????????????????????????????.
     * @param context
     * @param processName
     * @return
     */
    public static ApplicationInfo getApplicationInfo(Context context,String processName) {
        if (processName == null) {
            return null;
        }
    	
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : appList) {
        	if (processName.equals(appInfo.processName)) {
                return appInfo;
            }
        }
        return null;
    }
    
    /**
     * 
     * kill??????.
     * @param context
     * @param pid
     */
	@RequiresPermission(Manifest.permission.KILL_BACKGROUND_PROCESSES)
    public static void killProcesses(Context context,int pid,String processName) {
    	/*String cmd = "kill -9 "+pid;
    	Process process = null;
	    DataOutputStream os = null;
    	try {
			process = Runtime.getRuntime().exec("su"); 
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	AbLogUtil.d(AbAppUtil.class, "#kill -9 "+pid);*/
    	
    	ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    	String packageName = null;
    	try {
    		if(processName.indexOf(":")==-1){
    			packageName = processName;
		    }else{
		    	packageName = processName.split(":")[0];
		    }
    		
			activityManager.killBackgroundProcesses(packageName);
			
			//
			Method forceStopPackage = activityManager.getClass().getDeclaredMethod("forceStopPackage", String.class);
			forceStopPackage.setAccessible(true);
			forceStopPackage.invoke(activityManager, packageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
	
	/**
	 * ?????????????????????????????? Root??????????????????????????????(??????ROOT??????)
	 * @return ???????????????/?????????Root??????
	 */
	public static boolean getRootPermission(Context context) {
		String path = context.getPackageCodePath();  
	    return getRootPermission(path);
	}
	
	/**
	 * ??????????????????
	 * @return ????????????
	 */
	public static boolean getRootPermission(String path) {
		Process process = null;
		DataOutputStream os = null;
		try {
			File  file = new File(path);
			if(!file.exists()){
				return false;
			}
			String cmd = "chmod 777 " + path;
			// ?????????root??????
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

    /**
     * ??????????????????.
     * @param context
     * @return
     */
	public static long getAvailMemory(Context context){  
        //??????android????????????????????????  
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
        MemoryInfo memoryInfo = new MemoryInfo();  
        activityManager.getMemoryInfo(memoryInfo);  
        //???????????????????????? ,?????????????????????????????????  
        return memoryInfo.availMem;  
    }  
	
	/**
	 * ?????????.
	 * @param context
	 * @return
	 */
	public static long getTotalMemory(Context context){  
		//????????????????????????  
        String file = "/proc/meminfo";
        String memInfo;  
        String[] strs;  
        long memory = 0;  
          
        try{  
            FileReader fileReader = new FileReader(file);  
            BufferedReader bufferedReader = new BufferedReader(fileReader,8192);  
            //??????meminfo?????????????????????????????? 
            memInfo = bufferedReader.readLine(); 
            strs = memInfo.split("\\s+");  
            for(String str:strs){  
                AbLogUtil.d(AbAppUtil.class,str+"\t");  
            }  
            //??????????????????????????????KB  
            memory = Integer.valueOf(strs[1]).intValue()*1024;
            bufferedReader.close();  
        }catch(Exception e){  
            e.printStackTrace();
        }  
        //Byte??????KB???MB
        return memory;  
    }

	/**
	 * 
	 * ??????IMSI.
	 * @return
	 */
	@RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
	public static String getIMSI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getSubscriberId() == null) {
			return null;
		} else {
			return telephonyManager.getSubscriberId();
		}
	}

	/**
	 * ??????IMEI.
	 * @return
	 */
	@RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
	public static String getIMEI(Context context) {
		String deviceId = null;
		try{
			if (Build.VERSION.SDK_INT >= 29) {
				//??????Android_ID
				deviceId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
			}else{
				TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				deviceId = telephonyManager.getDeviceId();
			}
		}catch(Exception e){
		}

		if (AbStrUtil.isEmpty(deviceId)) {
			deviceId = android.os.Build.SERIAL;
		}
		if (AbStrUtil.isEmpty(deviceId)) {
			deviceId = AbSharedUtil.getString(context, "deviceId", "");
			if(AbStrUtil.isEmpty(deviceId)){
				deviceId = UUID.randomUUID().toString();
				AbSharedUtil.putString(context,"deviceId", deviceId);
			}
		}
        return deviceId;
	}


	/**
	 * ????????????????????????.
	 * @param context the context
	 * @return string
	 */
	@RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public static String getNetworkType(Context context) {

        // Wifi
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                return "WIFI";
            }
        }

        // Mobile network
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        int networkType = telephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
        }

        return "UNKNOW";
    }

	@RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
	public static String getCarrier(Context context) {
		// ????????????????????????????????????Mobile Network Code???MNC????????????????????????????????????Mobile Country Code???MCC??????????????????MCC /
		// MNC???????????????, ??????46000???????????????MCC???????????????MNC ???????????????????????????
		String carrier = "";
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String IMSI = telephonyManager.getSubscriberId();
		if(IMSI!=null){
			// IMSI?????????3???460???????????????????????????2???00 ???????????????
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
				carrier = "????????????";
			} else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
				carrier = "????????????";
			} else if (IMSI.startsWith("46003") || IMSI.startsWith("46005")) {
				carrier = "????????????";
			}
		}

		return carrier;
	}


	/**
	 * ????????????
	 * @return
	 */
	@RequiresPermission(allOf = {
			android.Manifest.permission.READ_PHONE_STATE,
			android.Manifest.permission.READ_SMS,
			android.Manifest.permission.READ_PHONE_NUMBERS
	})
	public static String getPhoneNumber(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getLine1Number() == null || telephonyManager.getLine1Number().length() < 11) {
			return null;
		} else {
			return telephonyManager.getLine1Number();
		}
	}
	
	/**
	 * 
	 * ??????QQ???.
	 * @return
	 */
	public static String getQQNumber(Context context) {
		String path = "/data/data/com.tencent.mobileqq/shared_prefs/Last_Login.xml";
		getRootPermission(context);
		File file = new File(path);
		getRootPermission(path);
		boolean flag = file.canRead();
		String qq = null;
		if(flag){
			try {
				FileInputStream is = new FileInputStream(file);
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(is, "UTF-8");
				int event = parser.getEventType();
				while (event != XmlPullParser.END_DOCUMENT) {

					switch (event) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if ("map".equals(parser.getName())) {
						}
						if ("string".equals(parser.getName())) {
							String uin = parser.getAttributeValue(null, "name");
							if (uin.equals("uin")) {
								qq = parser.nextText();
								return qq;
							}
						}
						break;
					case XmlPullParser.END_TAG:
						break;
					}
					event = parser.next();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
		
	/**
	 * 
	 * ??????WEIXIN???.
	 * @return
	 */
	public static String getWeiXinNumber(Context context) {
		String path = "/data/data/com.tencent.mm/shared_prefs/com.tencent.mm_preferences.xml";
		getRootPermission(context);
		File file = new File(path);
		getRootPermission(path);
		boolean flag = file.canRead();
		String weixin = null;
		if(flag){
			try {
				FileInputStream is = new FileInputStream(file);
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(is, "UTF-8");
				int event = parser.getEventType();
				while (event != XmlPullParser.END_DOCUMENT) {

					switch (event) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if ("map".equals(parser.getName())) {
						}
						if ("string".equals(parser.getName())) {
							String nameString = parser.getAttributeValue(null, "name");
							if (nameString.equals("login_user_name")) {
								weixin = parser.nextText();
								return weixin;
							}
						}
						break;
					case XmlPullParser.END_TAG:
						break;
					}
					event = parser.next();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * ????????????????????????
	 * @param tel
     */
	public static void showActionCall(Context context,String tel){
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + tel));
		context.startActivity(intent);
	}

	/**
	 * ???????????????????????????????????????
	 * @param grantResults
	 * @return
	 */
	public static boolean hasAllPermissionsGranted(int[] grantResults) {
		for (int grantResult : grantResults) {
			if (grantResult == PackageManager.PERMISSION_DENIED) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ??????????????????????????????
	 * @param context
	 * @param permission
	 * @return
	 */
	public static boolean hasPermission(Context context, String permission){

		int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
		if(permissionCheck == PackageManager.PERMISSION_GRANTED){
			return true;
		}
		return false;
	}

	/**
	 * ??????????????????
	 * @param activity
	 * @param requestCode
	 */
	public static boolean requestBlueToothPermission(Activity activity, int requestCode) {
		if (Build.VERSION.SDK_INT >= 23) {
			return requestPermissions(activity,new String []{
					Manifest.permission.BLUETOOTH,
					Manifest.permission.BLUETOOTH_ADMIN,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.READ_PHONE_STATE

			}, requestCode);
		}else{
            return true;
        }

	}

	/**
	 * ??????SD?????????
	 * @param activity
	 * @param requestCode
	 */
	public static boolean requestSDCardPermission(Activity activity, int requestCode) {
		if (Build.VERSION.SDK_INT >= 23) {
			return requestPermissions(activity,new String []{
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.READ_EXTERNAL_STORAGE

			}, requestCode);
		}else{
            return true;
        }

	}

	/**
	 * ?????????????????????
	 * @param activity
	 * @param requestCode
     *
	 */
	public static boolean requestCameraPermission(Activity activity, int requestCode) {
		if (Build.VERSION.SDK_INT >= 23) {
			return requestPermissions(activity,new String []{
					Manifest.permission.CAMERA

			}, requestCode);
		}else{
            return true;
        }

	}


	/**
	 * ???????????? ???6.0????????????????????????
	 * @param activity
	 * @param permissions
	 * SD??? Manifest.permission.WRITE_EXTERNAL_STORAGE
	 * ????????? Manifest.permission.CAMERA
	 * ?????? Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE
	 * @param requestCode
	 *
	 */
	/*
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
			//OK
		} else {
			Toast.makeText(this, "???????????????????????????????????????\"??????\"??????????????????????????????", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + getPackageName()));
			startActivity(intent);
			finish();
		}
	}*/
	public static boolean requestPermissions(Activity activity, String[] permissions, int requestCode){

		List<String> lackedPermission = new ArrayList<String>();
		for(String reqPermission :permissions) {
			if (!hasPermission(activity, reqPermission)) {
				lackedPermission.add(reqPermission);
			}
		}
		if (lackedPermission.size() == 0) {
			return true;
		} else {
			String[] requestPermissions = new String[lackedPermission.size()];
			lackedPermission.toArray(requestPermissions);
			ActivityCompat.requestPermissions(activity,requestPermissions, requestCode);
		}
		return false;
	}

	/**
	 * ??????????????????
	 * @return
     */
	public static int getSDKVersion() {
		int sdkVersion;
		try {
			sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (Exception e) {
			sdkVersion = 0;
		}
		return sdkVersion;
	}

	/**
	 * ??????????????????
	 * @param context
	 */
	public static void clearAllCache(Context context){
		try {
			PackageInfo info = AbAppUtil.getPackageInfo(context);
			File cacheDir = null;
			if (!AbFileUtil.isCanUseSD()) {
				cacheDir = new File(context.getCacheDir(), info.packageName);
			} else {
				cacheDir = new File(AbFileUtil.getCacheDownloadDir(context));
			}
			File[] files = cacheDir.listFiles();
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
			AbLogUtil.d(AbDiskCache.class,"??????????????????.");
		}catch(Exception e){
			e.printStackTrace();
			AbLogUtil.d(AbDiskCache.class,"??????????????????.");
		}

	}

	/**
	 * ???????????????????????????
	 * @param context
	 */
	public static void clearAllDownload(Context context){
		try {
			PackageInfo info = AbAppUtil.getPackageInfo(context);
			File downloadDir = null;
			if (!AbFileUtil.isCanUseSD()) {
				downloadDir = new File(context.getCacheDir(), info.packageName);
			} else {
				downloadDir = new File(AbFileUtil.getDownloadRootDir(context));
			}
			File[] files = downloadDir.listFiles();
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
			AbLogUtil.d(AbDiskCache.class,"??????????????????.");
		}catch(Exception e){
			e.printStackTrace();
			AbLogUtil.d(AbDiskCache.class,"??????????????????.");
		}

	}

	/**
	 * ????????????????????????
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * ?????????????????????
	 * @param activity
	 * @param color
	 * @param isLight
	 */
	public static void setWindowStatusBarColor(Activity activity, int color,boolean isLight) {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Window window = activity.getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.setStatusBarColor(color);
				//???????????????
				window.setNavigationBarColor(color);
				//android6.0?????????????????????????????????????????????????????????
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					if(isLight){
						//????????????
						window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
					}else{
						//????????????
						window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ?????????????????????
	 * @param dialog
	 * @param color
	 * @param isLight
	 */
	public static void setWindowStatusBarColor(Dialog dialog, int color,boolean isLight) {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Window window = dialog.getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.setStatusBarColor(color);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					if(isLight){
						//????????????
						window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
					}else{
						//????????????
						window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * ????????????????????? ??? ????????????
	 * @param activity
	 */
	public static void setWindowStatusBarTransparent(Activity activity,boolean whiteTextColor) {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Window window = activity.getWindow();
				//???????????????
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

					// ???????????????
					window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
					if(whiteTextColor){
						//????????????
						window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
								| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
					}else{
						//????????????
						window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
								| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
					}
					window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
					window.setStatusBarColor(Color.TRANSPARENT);


					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						//??????????????????????????????????????? ????????????????????????
						try {
							Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
							Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
							field.setAccessible(true);
							//????????????
							field.setInt(window.getDecorView(), Color.TRANSPARENT);
						} catch (Exception e) {
							//e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ????????????????????? + ??????
	 * @param activity ???????????????activity
	 * @param toolbar
	 * @return
	 */
	public static void setWindowStatusBarTransparent(@NonNull Activity activity,Toolbar toolbar) {
		setWindowStatusBarTransparent(activity,true);
		if (toolbar != null) {
			ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
			layoutParams.setMargins(
					layoutParams.leftMargin,
					layoutParams.topMargin + AbAppUtil.getStatusBarHeight(activity),
					layoutParams.rightMargin,
					layoutParams.bottomMargin);
		}
		return;
	}

	/**
	 * ??????????????????
	 * @param context
	 * @param mode
	 */
	public static void setNightMode(Context context, boolean mode) {
		if (mode) {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		} else {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		}
	}

	/**
	 * ????????????????????????????????????
	 * @param context
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public static boolean isNotificationEnabled(Context context) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			//8.0????????????
			if (((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).getImportance() == NotificationManager.IMPORTANCE_NONE) {
				return false;
			}
		}

		String CHECK_OP_NO_THROW = "checkOpNoThrow";
		String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

		AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
		ApplicationInfo appInfo = context.getApplicationInfo();
		String pkg = context.getApplicationContext().getPackageName();
		int uid = appInfo.uid;
		Class appOpsClass = null;
		try {
			appOpsClass = Class.forName(AppOpsManager.class.getName());
			Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
					String.class);
			Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

			int value = (Integer) opPostNotificationValue.get(Integer.class);
			return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ????????????APP
	 * @param context
	 */
	public static void startApp(Context context){
		Intent mStartActivity = new Intent(context, LauncherActivity.class);
		int mPendingIntentId = 123456;
		PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, mPendingIntent);
	}

	/**
	 * ??????????????????
	 * @param videoView
	 * @param open
	 */
	public static void toogleVideoVolume(VideoView videoView, boolean open) {
		try {
			Class<?> forName = Class.forName("android.widget.VideoView");
			Field field = forName.getDeclaredField("mMediaPlayer");
			field.setAccessible(true);
			MediaPlayer mediaPlayer = (MediaPlayer) field.get(videoView);
			if(mediaPlayer!=null){
				if(open){
					mediaPlayer.setVolume(0.5f, 0.5f);
				}else{
					mediaPlayer.setVolume(0, 0);
				}
			}
		} catch (Exception e) {
		}
	}
}
