package com.camera.douyin.xposed;

import java.io.File;
import java.io.FileOutputStream;

import android.os.Environment;

public class SdcardSave {

    public SdcardSave() {

    }

    /**
     *
     * @param fileName
     *              文件名
     * @param data
     *              写入的数据
     * @return
     */
    public static boolean saveToSdcard(String fileName,byte[] data) {
        boolean flag = false;
        File file = Environment.getExternalStorageDirectory();
        // 判断是否挂载 Sdcard 卡
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            FileOutputStream outputStream = null;
            try {
                outputStream=new FileOutputStream(new File(file, fileName));
                //outputStream=new FileOutputStream(new File(file, "VCG211152054190.jpg"));
                outputStream.write(data, 0, data.length);
                flag=true;
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }finally{
                if(outputStream != null)
                {
                    try {
                        outputStream.close();
                    } catch (Exception e2) {
                        // TODO: handle exception
                        e2.printStackTrace();
                    }
                }
            }
        }
        return flag;
    }
}
