package com.example.testiconcount;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    static String TAG = "MainActivity";
    private EditText et_number;
    private Button btnSet;

//    android.os.Build.MANUFACTURER .contains("samsung")
//    android.os.Build.MANUFACTURER .contains("Sony")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_number = (EditText) findViewById(R.id.unreadnumber);
        btnSet = (Button) findViewById(R.id.btnSet);

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setBadge(MainActivity.this,
                        Integer.parseInt(et_number.getText().toString()));
                Toast.makeText(MainActivity.this, "please see badge on your application in launcher", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static void setBadge(Context context, int count) {

        Log.d(TAG, "android.os.Build.MANUFACTURER=" +android.os.Build.MANUFACTURER);

        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Log.d(TAG, "launcherClassName="+launcherClassName);
        String MANUFACTURER = android.os.Build.MANUFACTURER;
        if (MANUFACTURER != null && MANUFACTURER.toLowerCase().contains("samsung")) {
            // Samsung OK
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", count);//<= 0 will remove badge
            intent.putExtra("badge_count_package_name",context.getPackageName());
            intent.putExtra("badge_count_class_name", launcherClassName);
            context.sendBroadcast(intent);

        } else if (MANUFACTURER != null && MANUFACTURER.toLowerCase().contains("sony")) {
            // Sony OK
            Intent intent = new Intent();
            intent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
            intent.putExtra( "com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", launcherClassName);
            if (count <= 0) {
                intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", false);
            } else {
                intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", true);
                intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", count + ""); // String
            }
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());
            context.sendBroadcast(intent);

        } else {
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", count);
            intent.putExtra("badge_count_package_name",context.getPackageName());
            intent.putExtra("badge_count_class_name", launcherClassName);
            context.sendBroadcast(intent);
        }

    }

    //索尼的badge显示方法
    private void showBadgeOnSony(String packageName, String launcherClassName) {
        if (packageName == null || launcherClassName == null)
            return;
        Intent intent = new Intent();
        intent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", launcherClassName);
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", true);//if we want remove the badge just set value false
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", "99");
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", packageName);
        sendBroadcast(intent);
    }


    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }
}
