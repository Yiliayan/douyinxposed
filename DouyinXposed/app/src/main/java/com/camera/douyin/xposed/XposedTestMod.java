package com.camera.douyin.xposed;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class XposedTestMod implements IXposedHookLoadPackage {

    public static final String TAG="VirtualXposed";
    String targetPackageName = "com.ss.android.ugc.aweme";
    String targetClasssName = "com.ss.android.ugc.aweme.shortvideo.edit.VEVideoPublishEditActivity";
    String targetMethodName = "onCreate";
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(targetPackageName))
            return;
        Log.d(TAG,"handleLoadPackage: 进入包啦");
        Log.d(TAG, "handleLoadPackage: app="+lpparam.packageName);
        XposedHelpers.findAndHookMethod(targetClasssName,
                lpparam.classLoader, targetMethodName, Bundle.class,new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("VirtualXposed开始劫持了~");
                        Log.d(TAG, "beforeHookedMethod: param="+param);
                        Activity att = (Activity) param.thisObject;
                        Context context = att.getApplicationContext();
                        String[] files = context.fileList();
                        Log.d(TAG, "beforeHookedMethod: filesnum="+files.length);
                        for (int i =0; i<files.length;i++) {
                            Log.d(TAG, "beforeHookedMethod: files[i]="+files[i]);
                        }
                        String filePath="/data/data/com.ss.android.ugc.aweme/files";
                        File file = new File(filePath);
                        File[] files2=file.listFiles();
                        String fileName="";
                        if (files2 == null){Log.e("error","空目录");}
                        for(int i =0;i<files2.length;i++){
                            filePath=files2[i].getAbsolutePath();
                            if (filePath.contains("concat-v")) {
                                Log.d(TAG, "beforeHookedMethod: filePath="+filePath);
                                fileName = files2[i].getName()+".mp4";
                                file = new File(filePath);
                                InputStream in = null;
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                try {
                                    in =  new FileInputStream(file);
                                    Log.d(TAG, "beforeHookedMethod: in="+in);
                                    int len=0;
                                    byte[] buffer = new byte[2*1024*1024];
                                    while((len=in.read(buffer))!=-1){
                                        outStream.write(buffer, 0, len);//
                                    }
                                    Log.d(TAG, "beforeHookedMethod: outStream="+outStream);
                                    byte[] content_byte = outStream.toByteArray();
                                    boolean result = SdcardSave.saveToSdcard("CameraData/douyin/"+fileName,content_byte);
                                    Log.d(TAG, "beforeHookedMethod: content_byte="+content_byte+" save to sdcard "+result);
                                    String content = new String(content_byte);
                                } catch(Exception e){
                                    e.printStackTrace();
                                    Log.d(TAG, "beforeHookedMethod: 复制文件失败");
                                }
                            }
                        }
                    }
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                         XposedBridge.log("VirtualXposed劫持结束了");
                        super.afterHookedMethod(param);
                    }
                });
    }
}