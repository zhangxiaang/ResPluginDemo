package aaron.boy.simpledynamicloaddemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

/**
 * Author：Aaron on 2016/8/1 11:36
 * Email：zhangxiang5@le.com
 */
public class ExternalDexResLoader {
    public static final String TAG = "ExternalDexResLoader";

    public static AssetManager createExternalAssetManager(String dexPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            try {
                AssetManager.class.getDeclaredMethod("addAssetPath", String.class).invoke(
                        assetManager, dexPath);
            } catch (Throwable th) {
                Log.e(TAG, th.getMessage());
                th.printStackTrace();
            }
            return assetManager;//这个assetManager是加载了外部的dex资源
        } catch (Throwable th) {
            Log.e(TAG, th.getMessage());
            th.printStackTrace();
        }
        return null;
    }

    public static Resources getExternalDexResource(Context context,AssetManager mAssetManager) {
        Resources mResources;
        Resources superRes = context.getResources();
        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),
                superRes.getConfiguration());
        return mResources;
    }
}