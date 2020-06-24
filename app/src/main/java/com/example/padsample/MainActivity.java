package com.example.padsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.play.core.assetpacks.AssetPackLocation;
import com.google.android.play.core.assetpacks.AssetPackManager;
import com.google.android.play.core.assetpacks.AssetPackManagerFactory;
import com.google.android.play.core.assetpacks.AssetPackState;
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener;
import com.google.android.play.core.assetpacks.AssetPackStates;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    AssetPackManager assetPackManager;

    String assetPackName = "fast_follow_asset_pack";
    String assetPackName1 = "on_demand_asset_pack";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // install-time 可以直接用 AssetManager 来访问，跟主工程中的Assets目录使用一样
        AssetManager assetManager = this.getAssets();
        try {
            InputStream is = assetManager.open("install_time2.txt");
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            Log.d("puzzle", new String(buffer));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assetPackManager = AssetPackManagerFactory.getInstance(this.getApplicationContext());
        assetPackManager.getPackStates(Collections.singletonList(assetPackName))
                .addOnCompleteListener(new OnCompleteListener<AssetPackStates>() {
                    @Override
                    public void onComplete(Task<AssetPackStates> task) {
                        AssetPackStates assetPackStates;
                        try {
                            assetPackStates = task.getResult();
                            AssetPackState assetPackState =
                                    assetPackStates.packStates().get(assetPackName);

                            Log.d("puzzle", "status: " + assetPackState.status() +
                                    ", name: " + assetPackState.name() +
                                    ", errorCode: " + assetPackState.errorCode() +
                                    ", bytesDownloaded: " + assetPackState.bytesDownloaded() +
                                    ", totalBytesToDownload: " + assetPackState.totalBytesToDownload() +
                                    ", transferProgressPercentage: " + assetPackState.transferProgressPercentage());
                        }catch (Exception e){
                            Log.d("MainActivity", e.getMessage());
                        }
                    }
                });

        setContentView(R.layout.activity_main);
        findViewById(R.id.text).setOnClickListener(this);
        assetPackManager.registerListener(mAssetPackStateUpdateListener);

        List<String> list = new ArrayList();
        list.add(assetPackName);
        list.add(assetPackName1);
        assetPackManager.fetch(list);
    }

    AssetPackStateUpdateListener mAssetPackStateUpdateListener = new AssetPackStateUpdateListener() {
        @Override
        public void onStateUpdate(AssetPackState state) {
            Log.d("puzzle", "mAssetPackStateUpdateListener onStateUpdate state: " + state.status());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        assetPackManager.unregisterListener(mAssetPackStateUpdateListener);
    }

    // fast-follow 和 on-demand 需要通过AssetPackLocation获取到assetpack的路径，在根据绝对路径来读取资源
    private void getAbsoluteAssetPath(String assetPack, String relativeAssetPath) {
        AssetPackLocation assetPackPath = assetPackManager.getPackLocation(assetPack);
        Log.d("puzzle", "invoke getAbsoluteAssetPath");

        String assetsFolderPath = assetPackPath.assetsPath();
        // equivalent to: FilenameUtils.concat(assetPackPath.path(), "assets");

        File file = new File(assetsFolderPath + relativeAssetPath);
        if (file.isFile()) {
            try {
                InputStream is = new FileInputStream(file);
                int length = is.available();
                byte[] buffer = new byte[length];
                is.read(buffer);
                Log.d("puzzle", new String(buffer));
            }
            catch (Exception e) {
               e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        getAbsoluteAssetPath("fast_follow_asset_pack", "/fast_follow/fast_follow1.txt");
        getAbsoluteAssetPath("on_demand_asset_pack", "/on_demand2.txt");
    }
}