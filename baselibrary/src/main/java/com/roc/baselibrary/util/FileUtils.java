package com.roc.baselibrary.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Create by roc
 * 2019-10-26
 */
public class FileUtils {

    /**
     * 格式化文件大小
     *
     * @param byteNum -
     * @return -
     */
    public static String byte2FitMemorySize(final long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < 1024) {
            return String.format(Locale.getDefault(), "%.2f B ", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format(Locale.getDefault(), "%.2f KB ", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format(Locale.getDefault(), "%.2f MB ", (double) byteNum / 1048576);
        } else {
            return String.format(Locale.getDefault(), "%.2f GB ", (double) byteNum / 1073741824);
        }
    }


    /**
     * 保存文件
     *
     * @param is   -
     * @param file 目标文件
     * @throws IOException -
     */
    public static void convertInputStreamToFileCommonWay(InputStream is, File file) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }

    /**
     * 获得应用内部存储路径
     *
     * @return
     */
    public static String getFilePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {//如果外部储存可用
            return context.getExternalFilesDir(null).getPath();//获得外部存储路径,默认路径为 /storage/emulated/0/Android/data/包名/files/
        } else {
            return context.getFilesDir().getPath();//直接存在/data/data里，非root手机是看不到的
        }
    }


}
