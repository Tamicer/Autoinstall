package autoinstall.com.tamic.autoinstall;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**  Created by liyongkui on 15/9/13.*/
public class MainActivity extends Activity {
    /** WindowManager */
    private WindowManager mWinManager;
    /** WindowManager.LayoutParams */
    private WindowManager.LayoutParams mWinManagerParams;
    /** PackageChangedMonitor */
    private BroadcastReceiver mPackageChangedMonitor;
    /** isInstall */
    private boolean  isInstall = false;
    /** WaitView */
    private TamcWaitingView wait = null;
    /** target apk name */
    private String apkName = "UCBrowser.apk";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        FrameLayout rootView = new FrameLayout(this);

        setContentView(rootView);

        RelativeLayout contentView = new RelativeLayout(this);
        rootView.addView(contentView);

        int bId = 0x0001;
        Button installButton = new Button(this);
        installButton.setId(bId);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, bId);

        contentView.addView(installButton, layoutParams);

        installButton.setText("Install apk");
        installButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TamicInstallService.isAccessibilitySettingsOn(MainActivity.this)) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                    return;
                }
                TamicInstallService.setInvokeType(TamicInstallService.TYPE_INSTALL_APP);

                installApk();


            }
        });

        mPackageChangedMonitor = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(Intent.ACTION_PACKAGE_ADDED)
                        || action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                    if (isInstall) {
                        isInstall = false;

                        if (wait != null) {
                            mWinManager.removeView(wait);
                            wait = null;
                        }
                        try {
                            Thread.sleep(1300);
                            TamicWindowManager.dismiss();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        IntentFilter filterPackage = new IntentFilter();
        filterPackage.addAction(Intent.ACTION_PACKAGE_ADDED);
        filterPackage.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filterPackage.addDataScheme("package");
        registerReceiver(mPackageChangedMonitor, filterPackage);
    }



    /**
     * install Apk
     */
    private void installApk() {
        isInstall = true;
        String path = Environment.getExternalStorageDirectory().getPath() + "";
       // File localApkFile = /*new File(path + File.separator  + apkName);*/

        boolean isCopy = copyApkFromAssets(this, apkName, path + File.separator  + apkName);

        File localApkFile = new File(path + File.separator  + apkName);

        if ( !isCopy &&!localApkFile.exists()) {
            Toast.makeText(MainActivity.this, "apk file is not exists!", Toast.LENGTH_SHORT).show();
            //TamicWindowManager.makeWatingWiew(MainActivity.this, "Loading").cancel();

           // throw new RuntimeException("file is not exists!");
            return;
        }

        TamicWindowManager.makeWatingWiew(MainActivity.this, "安装中...").show();

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile( localApkFile), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    public boolean copyApkFromAssets(Context context, String fileName, String path) {
        boolean copyIsFinish = false;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyIsFinish;
    }


}
