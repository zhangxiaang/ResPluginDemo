package aaron.boy.simpledynamicloaddemo;

import android.app.Application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Author：Aaron on 2016/9/1 16:19
 * Email：zhangxiang5@le.com
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            InputStream inputStream = getResources().getAssets().open("plugin.apk");
//            InputStream inputStream = getResources().getAssets().open("signed_plugins.apk");
            File apkFile = new File(getFilesDir(), "plugin.apk");//加载到应用内部空间
//            File apkFile = new File(getFilesDir(), "signed_plugins.apk");//加载到应用内部空间

            OutputStream out = new FileOutputStream(apkFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            inputStream.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] paths = new String[2];

        paths[1] = getPackageResourcePath();
//        paths[0] = getFilesDir() + File.separator + "signed_plugins.apk";
        paths[0] = getFilesDir() + File.separator + "plugin.apk";

        //如何给context对象重新设置合并后的res对象呢?
        //还只是资源的合并
        ReflectAccelerator.mergeResources(this, paths);
    }
}
