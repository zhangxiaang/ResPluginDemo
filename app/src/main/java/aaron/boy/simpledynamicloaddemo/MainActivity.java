package aaron.boy.simpledynamicloaddemo;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ryg.dynamicload.internal.DLIntent;
import com.ryg.dynamicload.internal.DLPluginManager;

import java.io.File;

import dalvik.system.DexClassLoader;

//需要明白的是  被调用的apk必须受某种规范的约束 否则几乎是不可能随意去加载任意一个apk的
public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    private AssetManager mAssetManager;
    private Resources mResources;
    private HelloWorld helloWorld;
    private File file1;
    private DexClassLoader dexClassLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //找到plugin.apk 并且加载进来
//        file1 = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "tests.apk");
//        final File optimizedDexOutputPath = getDir("libs", Context.MODE_PRIVATE);
//
//        dexClassLoader = new DexClassLoader(file1.getAbsolutePath(),
//                optimizedDexOutputPath.getAbsolutePath(), null, getClassLoader());
//
//        Class libProviderClass = null;
//
//        try {
//            libProviderClass = dexClassLoader.loadClass("aaron.boy.simpledynamicloaddemo.Test");
//            Log.e(TAG, libProviderClass.getClass().getName());
//            helloWorld = (HelloWorld) libProviderClass.newInstance();
//            Log.e(TAG, helloWorld.hello("Aaron"));//helloWorld是一个壳儿
//
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//            Log.e(TAG, e.getMessage());
//            e.printStackTrace();
//        }
//
//        //如果在这里 即 插件还没加载的地方去执行获取本地的信息呢？
//
//        TextView textView = (TextView) MainActivity.this.findViewById(R.id.show);
//        int res = R.string.app_name;
//        textView.setText(getResources().getString(res));
        Toast.makeText(this, getResources().getString(R.string.app_name), Toast.LENGTH_LONG).show();
        Log.e(TAG, getResources().getString(R.string.app_name) + ":0x" + Integer.toHexString(R.string.app_name));
//        Toast.makeText(MainActivity.this, getResources().getString(Integer.parseInt("7e060012", 16)), Toast.LENGTH_SHORT).show();

    }

    public void loadsJar(View view) {
//        loadResources();
    }

//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    protected void loadResources() {
//        //单纯的去读取资源而已
//        //如何获取到外部dex的assetsManager
//        mAssetManager = ExternalDexResLoader.createExternalAssetManager(file1.getAbsolutePath());
//
//        mResources = ExternalDexResLoader.getExternalDexResource(getApplicationContext(), mAssetManager);
//        //之前没有加载jar成功是因为没有打包res文件...只是纯粹的class文件 所以无法加载成功...
//
//        int resultId0 = mResources.getIdentifier("app_names", "string", "aaron.boy.simpledynamicloaddemo");
//        int resultId = R.string.app_name;
//        int resultId2 = mResources.getIdentifier("dynamicload", "string", "aaron.boy.simpledynamicloaddemo");
//
////        Log.e(TAG, "APP_NAME" + mResources.getString(resultId));
//        Log.e(TAG, "APP_NAME0" + mResources.getString(resultId0));
//        Log.e(TAG, "dynamicload" + mResources.getString(resultId2));
//
//        //以上的app_name产生了id冲突 然而在插件中的mResources不为空  所以需要修改下resource.arsc资源表
//        //以上是加载了value资源 不管是采用mResource还是getResources()得到的都是mResource对象
//        Log.e(TAG, resultId + "" + "resultId:" + getResources().getString(resultId));
//        Log.e(TAG, resultId0 + "" + "resultId0:" + mResources.getString(resultId0));
//        Log.e(TAG, resultId2 + "" + "resource form:" + mResources.getString(resultId2));
//        //这样是获取到dex包里面资源的id值
//        int layoutId = mResources.getIdentifier("test", "layout", "aaron.boy.simpledynamicloaddemo");
//        if (layoutId == 0) {
//            Log.e(TAG, "sorry..can't find that");
//        } else
//            Log.e(TAG, layoutId + "");
//
//        int layoutId2 = R.layout.activity_main;
//        if (layoutId2 == 0) {
//            Log.e(TAG, "sorry..can't find layoutId2");
//        } else
//            Log.e(TAG, layoutId2 + "layoutId2");
//
//        TextView textView = (TextView) MainActivity.this.findViewById(R.id.show);
//        int res = R.string.app_name;
//        textView.setText(getResources().getString(res));
//        //那如果需要加载layout资源呢?
//        //需要动态的判断context的来源是宿主的还是插件的  如果用宿主的context去inflate插件的就有问题了
////        LayoutInflater.from(getApplicationContext()).inflate(layoutId, null);
//    }

    public void loadsActivity(View view) {
        //todo 这个DLPluginManager代码组织的很赞 可以学习下
        DLPluginManager pluginManager = DLPluginManager.getInstance(this);
        pluginManager.loadApk(file1.getAbsolutePath());
        pluginManager.startPluginActivity(this, new DLIntent("aaron.boy.simpledynamicloaddemo", "aaron.boy.simpledynamicloaddemo.PluginBActivity"));
        //原来是因为我的这个pluginManager并没有加载apk  导致apk 中的activity无法被反射到
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void loadPlugin(View view) {
//        Log.e(TAG, "Host:0x" + Integer.toHexString(getResources().getIdentifier("activity_main", "layout", getPackageName())));
        Log.e(TAG, getResources().getString(R.string.app_name) + " hex:" + Integer.toHexString(R.string.app_name));
        Toast.makeText(MainActivity.this, getResources().getString(Integer.parseInt("7e060012", 16)),Toast.LENGTH_SHORT).show();
    }
}
