package com.github.guignol.intenthook;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class IntentHookActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // アニメーションを無効化
        overridePendingTransition(0, 0);

        setContentView(R.layout.intetn_hook);
        findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Uri data = getIntent().getData();
        final Intent forward = new Intent();
        forward.setData(data);
        forward.setAction(Intent.ACTION_VIEW);
        forward.addCategory(Intent.CATEGORY_DEFAULT);
        forward.addCategory(Intent.CATEGORY_BROWSABLE);

        final String thisAppPackageName = getPackageName();
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(forward, PackageManager.MATCH_DEFAULT_ONLY);

        final List<AppInfo> appsList = new ArrayList<>();
        for (final ResolveInfo resolveInfo : resolveInfos) {
            final String packageName = resolveInfo.activityInfo.packageName;
            // 自分を除外する
            if (thisAppPackageName.equals(packageName)) {
                continue;
            }
            appsList.add(new AppInfo() {
                @Override
                public CharSequence loadLabel(PackageManager pm) {
                    return resolveInfo.loadLabel(pm);
                }

                @Override
                public Drawable loadIcon(PackageManager pm) {
                    return resolveInfo.loadIcon(pm);
                }

                @Override
                public String packageName() {
                    return packageName;
                }
            });
        }
        appsList.add(new AppInfo() {
            @Override
            public CharSequence loadLabel(PackageManager pm) {
                return "このアプリで常にフックする設定を解除する";
            }

            @Override
            public Drawable loadIcon(PackageManager pm) {
                return null;
            }

            @Override
            public String packageName() {
                return thisAppPackageName;
            }
        });

        final ListView targetList = (ListView) findViewById(R.id.target_list);
        final AppIconAdapter targetAdapter = new AppIconAdapter(this, appsList);
        targetList.setAdapter(targetAdapter);
        targetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AppInfo itemClicked = targetAdapter.getItem(position);
                if (thisAppPackageName.equals(itemClicked.packageName())) {
                    // このアプリで常に開く設定を初期化する
                    packageManager.clearPackagePreferredActivities(thisAppPackageName);
                } else {
                    // 選んだアプリにインテントを転送する
                    forward.setPackage(itemClicked.packageName());
                    startActivity(forward);
                }
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        // アニメーションを無効化
        overridePendingTransition(0, 0);
    }
}
