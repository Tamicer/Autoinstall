package autoinstall.com.tamic.autoinstall;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.IntentFilter;


import android.text.TextUtils;

import java.util.Iterator;
import java.util.List;

/**  Created by liyongkui on 15/9/13. */
public class TamicInstallService extends AccessibilityService {

    /** INVOKE_TYPE */
    private static int INVOKE_TYPE = 0;
    /** INSTALL */
    public static final int TYPE_INSTALL_APP = 1;
    /** UNINSTALL */
    public static final int TYPE_UNINSTALL_APP = 2;
    /** REASON_KEY */
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    /** RECENT_APPS */
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    /** HOME_KEY */
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    /** _LOCK */
    private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
    /** ASSIST */
    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";
    /** isAoutOpenApp */
    private boolean isAutoRunning = false;
    /** tag */
    public static String LOG_TAG = "AccessibilityService";

    @Override
    protected void onServiceConnected() {
        // TODO Auto-generated method stub
        super.onServiceConnected();
        registerFilter();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (homecastReceiver != null) {
            unregisterReceiver(homecastReceiver);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            if (event.getSource() == null) {
                Log.d(LOG_TAG, "the source = null");
                return;
            }
            switch (INVOKE_TYPE) {
                case TYPE_INSTALL_APP:
                    try {
                        processAccessibilityEvent(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case TYPE_UNINSTALL_APP:
                     //processUninstallApplication(event);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected boolean onKeyEvent(KeyEvent aKeyEvent) {
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * extractNode.
     * @param aAccessibilityEvent    aAccessibilityEvent
     * @param aNodeClassName         aNodeClassName
     * @param aNodeText              aNodeText
     * @return                       AccessibilityNodeInfo
     */
    private AccessibilityNodeInfo extractNode(AccessibilityEvent aAccessibilityEvent, String aNodeClassName, String aNodeText) {
        List<AccessibilityNodeInfo> extractList = null;
        AccessibilityNodeInfo targetNode = null;
        AccessibilityNodeInfo rootNode;
        if((aAccessibilityEvent == null) || (aAccessibilityEvent.getSource() == null)) {
            rootNode = this.getRootInActiveWindow();
            if(rootNode != null) {
                extractList = rootNode.findAccessibilityNodeInfosByText(aNodeText);
            }
        } else {
            extractList = aAccessibilityEvent.getSource().findAccessibilityNodeInfosByText(aNodeText);
        }

        if ((extractList == null) || extractList.isEmpty()) {
            targetNode = null;
        } else {
            Iterator<AccessibilityNodeInfo> it = extractList.iterator();
            AccessibilityNodeInfo tempNode = null;
            while (it.hasNext()) {
                tempNode = it.next();

                if (!tempNode.getClassName().equals(aNodeClassName)) {
                    continue;
                }

                String nodeName = (tempNode.getText() == null) ? "" : tempNode.getText().toString();

                if (!nodeName.endsWith(aNodeText)) {
                    continue;
                }

                targetNode = tempNode;
                break;
            }
        }

        return targetNode;
    }

    /**
     *  performClickAction
     * @param aAccessibilityNodeInfo  aAccessibilityNodeInfo
     * @return
     */
    private boolean performClickAction(AccessibilityNodeInfo aAccessibilityNodeInfo) {
        int targetAction = AccessibilityNodeInfo.ACTION_CLICK;
        if ((aAccessibilityNodeInfo != null)  && aAccessibilityNodeInfo.isEnabled() && aAccessibilityNodeInfo.isClickable()
                && aAccessibilityNodeInfo.performAction(targetAction)) {
            return true;
        }

        return false;
    }

    private void handleAlertDialog(AccessibilityEvent aAccessibilityEvent, String aClassName, String aNodeText) {
        if (!aAccessibilityEvent.getText().toString().contains(this.getString(R.string.str_accessibility_error))) {
            AccessibilityNodeInfo accessibilityNodeInfo = extractNode(aAccessibilityEvent, "android.widget.Button", this.getString(R.string.btn_accessibility_ok));
            if (accessibilityNodeInfo != null) {
                performClickAction(accessibilityNodeInfo);
            }
        }
    }


    /**
     * containNodeWithText.
     * @param aAccessibilityEvent aAccessibilityEvent
     * @param aText               aText
     * @return                    Text
     */
    private boolean containNodeWithText(AccessibilityEvent aAccessibilityEvent, String aText) {
        List<AccessibilityNodeInfo> extractList = null;
        if ((aAccessibilityEvent != null) && (aAccessibilityEvent.getSource()) != null) {
            extractList = aAccessibilityEvent.getSource().findAccessibilityNodeInfosByText(aText);
        } else {
            AccessibilityNodeInfo rootNode = this.getRootInActiveWindow();
            if (rootNode != null) {
                extractList = rootNode.findAccessibilityNodeInfosByText(aText);
            }
        }

        return !(extractList == null || extractList.isEmpty());

    }

    /**
     * containUninstallInfo
     * @param aAccessibilityEvent aAccessibilityEvent
     * @param aNodeText           aNodeText
     * @return                    installInfos
     */
    private boolean containUninstallInfo(AccessibilityEvent aAccessibilityEvent, String aClassName, String aNodeText) {
        return containNodeWithText(null, this.getString(R.string.str_accessibility_uninstalled)) || containNodeWithText(null, this.getString(R.string.str_accessibility_uninstalled2))
                || containNodeWithText(null, this.getString(R.string.str_accessibility_uninstalled3));
    }


    /**
     * InstalledFinish
     * @param aAccessibilityEvent aAccessibilityEvent
     * @param aClassName          aClassName
     * @param aNodeText           aNodeText
     * @return                    isInstalledFinish
     */
    private boolean isInstalledFinish(AccessibilityEvent aAccessibilityEvent, String aClassName, String aNodeText) {
        return aNodeText.equalsIgnoreCase(this.getString(R.string.str_accessibility_installed))
                || aNodeText.equalsIgnoreCase(this.getString(R.string.str_accessibility_installed2))
                || aNodeText.contains(this.getString(R.string.str_accessibility_installed3))
                || aNodeText.contains(this.getString(R.string.btn_accessibility_open))
                || aNodeText.contains(this.getString(R.string.btn_accessibility_run))
                || containNodeWithText(null, this.getString(R.string.str_accessibility_installed))
                || containNodeWithText(null, this.getString(R.string.str_accessibility_installed2))
                || containNodeWithText(null, this.getString(R.string.str_accessibility_installed3));
    }

    /**
     * performClickActionWithFindNode.
     * @param aAccessibilityNodeInfo  aAccessibilityNodeInfo
     * @param aClassName              aClassName
     * @param aNodeTxt                aNodeTxt
     * @param isGlobal                isGlobal
     * @return                        true
     */
    private boolean performClickActionWithFindNode(AccessibilityNodeInfo aAccessibilityNodeInfo, String aClassName, String aNodeTxt, boolean isGlobal) {
        if(aAccessibilityNodeInfo == null) {
            return false;
        } else {
            List<AccessibilityNodeInfo> targetList = aAccessibilityNodeInfo.findAccessibilityNodeInfosByText(aNodeTxt);
            if (targetList != null) {
                for (AccessibilityNodeInfo targetNode : targetList) {
                    if (aClassName != null) {
                        String targetClassName = targetNode.getClassName() == null ? "" : targetNode.getClassName().toString();
                        if (!aClassName.equals(targetClassName)) {
                            continue;
                        }
                    }

                    String targetNodeText = targetNode.getText() == null ? "" : targetNode.getText().toString();
                    if (!aNodeTxt.equals(targetNodeText)) {
                        continue;
                    }

                    if (isGlobal && !isAutoRunning) {
                        performGlobalAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    } else {
                        performClickAction(targetNode);
                    }
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * performCloseInstalledUI.
     * @param aAccessibilityEvent   aAccessibilityEvent
     * @return                      boolean
     */
    private boolean performCloseInstalledUI(AccessibilityEvent aAccessibilityEvent) {
        AccessibilityNodeInfo targetNode = (aAccessibilityEvent != null) && (aAccessibilityEvent.getSource() != null)
                ? aAccessibilityEvent.getSource() : getRootInActiveWindow();
        if (performClickActionWithFindNode(targetNode, null, this.getString(R.string.btn_accessibility_ok), false)) {
            return true;
        }

        if (performClickActionWithFindNode(targetNode, null, this.getString(R.string.btn_accessibility_done), false)) {
            reset();
            return true;
        }

        if (performClickActionWithFindNode(targetNode, null, this.getString(R.string.btn_accessibility_complete), false)) {
            reset();
            return true;
        }

        if (performClickActionWithFindNode(targetNode, null, this.getString(R.string.btn_accessibility_know), false)) {
            return true;
        }

        if (performClickActionWithFindNode(targetNode, null, this.getString(R.string.btn_accessibility_run), true)) {
            reset();
            return true;
        }

        if (performClickActionWithFindNode(targetNode, null, this.getString(R.string.btn_accessibility_open), true)) {
            reset();
            return true;
        }
        return false;
    }

    /**
     * closeInstalledUI.
     * @param aAccessibilityEvent aAccessibilityEvent
     */
    private void closeInstalledUI(AccessibilityEvent aAccessibilityEvent) {
        performCloseInstalledUI(aAccessibilityEvent);
    }

    /**
     * isUninstallUI
     * @param aAccessibilityEvent     aAccessibilityEvent
     * @param aClassName              aClassName
     * @param aNodeText               aNodeText
     * @return                        isUninstallUI
     */
    private boolean isUninstallUI(AccessibilityEvent aAccessibilityEvent, String aClassName, String aNodeText) {
        return aClassName.equalsIgnoreCase("com.android.packageinstaller.UninstallerActivity")
                || aNodeText.contains(this.getString(R.string.str_accessibility_uninstall));
    }

    /**
     * InstallUI.
     * @param aAccessibilityEvent     aAccessibilityEvent
     * @param aClassName              aClassName
     * @param aNodeText               aNodeText
     * @return                        isInstallUI
     */
    private boolean isInstallUI(AccessibilityEvent  aAccessibilityEvent, String aClassName, String aNodeText) {
        return aClassName.equalsIgnoreCase("com.android.packageinstaller.PackageInstallerActivity")
                || aNodeText.contains(this.getString(R.string.btn_accessibility_install));
    }

    /**
     * reset Type
     */
    public static void reset(){
        INVOKE_TYPE = 0;
    }

    /**
     * setInvokeType
     * @param aType type
     */
    public static void setInvokeType(int aType){
        INVOKE_TYPE = aType;
    }

    private int checkSmartBar = 0; //not yet

    private boolean hasSmartBar() {
        if (checkSmartBar == 0) {
            try {
                if (Build.class.getMethod("hasSmartBar") != null) {
                    checkSmartBar = 1;
                } else {
                    checkSmartBar = -1;
                }
            } catch(Exception e) {
                checkSmartBar = -1;
            }
        }

       return (checkSmartBar == 1);
    }

    /**
     * performAutoInstall
     * @param aAccessibilityEvent     aAccessibilityEvent
     * @param aClassName              aClassName
     */
    private void performAutoInstall(AccessibilityEvent aAccessibilityEvent, String aClassName) {
        AccessibilityNodeInfo targetNode = extractNode(aAccessibilityEvent, aClassName,
                this.getString(R.string.btn_accessibility_install));
        Log.e("test", "target node 1 " + targetNode);
        if (targetNode != null) {
            performClickAction(targetNode);
            return;
        }

        targetNode = extractNode(aAccessibilityEvent, aClassName,
                this.getString(R.string.btn_accessibility_allow_once));
        Log.e("test", "target node 2 " + targetNode);
        if (targetNode != null) {
            performClickAction(targetNode);
            return;
        }

        targetNode = extractNode(aAccessibilityEvent, aClassName,
                this.getString(R.string.btn_accessibility_next));
        Log.e("test", "target node 3 " + targetNode);
        if(targetNode != null) {
            performClickAction(targetNode);
            autoInstall(aAccessibilityEvent);
        }
    }

    /**
     * autoInstall
     * @param aAccessibilityEvent   aAccessibilityEvent
     */
    private void autoInstall(AccessibilityEvent aAccessibilityEvent) {
        String targetClassName = hasSmartBar() ? "android.widget.TextView" : "android.widget.Button";
        performAutoInstall(aAccessibilityEvent, targetClassName);
    }

    /**
     * processAccessibilityEvent.
     * @param aAccessibilityEvent   aAccessibilityEvent
     */
    private void processAccessibilityEvent(AccessibilityEvent aAccessibilityEvent) {
        if(aAccessibilityEvent.getSource() != null) {
            String packageName = aAccessibilityEvent.getPackageName().toString();
            String className = aAccessibilityEvent.getClassName().toString();
            String nodeText = aAccessibilityEvent.getSource().getText() == null ? "" : aAccessibilityEvent.getSource().getText().toString().trim();

            if(packageName.equals("com.android.packageinstaller")) {
                if(className.equalsIgnoreCase("android.app.AlertDialog")) {
                   // handleAlertDialog(aAccessibilityEvent, className, nodeText); //should for uninstall
                    Log.e("test", "onAccessibilityEvent alert dialog");
                    return;
                }

                if(containUninstallInfo(aAccessibilityEvent, className, nodeText)) {
                    //skip
                    Log.e("test", "onAccessibilityEvent uninstall 1");
                    return;
                }

                if(isInstalledFinish(aAccessibilityEvent, className, nodeText)) {
                    closeInstalledUI(null);
                    return;
                }

                if(isUninstallUI(aAccessibilityEvent, className, nodeText)) {
                    //skip
                    Log.e("test", "onAccessibilityEvent uninstall 2");
                    return;
                }

                if(isInstallUI(aAccessibilityEvent, className, nodeText)) {
                    Log.e("test", "onAccessibilityEvent install");
                    autoInstall(null);
                    return;
                }

                if (!containNodeWithText(null, this.getString(R.string.str_accessibility_install_blocked))) {
                    Log.e("test", "onAccessibilityEvent not contain block");
                    return;
                }

                autoInstall(null);
                return;
            }

            if(packageName.equals("com.lenovo.safecenter")) {
                processAccessibilityEventForLenvo(aAccessibilityEvent, className, nodeText);
            }
        }
    }

    /**
     * registerReceiver.
     */
    private void registerFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //filter.addDataScheme("package");
        //regist for broadcasts of interest
        registerReceiver(homecastReceiver, filter);
    }

    /**
     *  EventForLenvo
     * @param aAccessibilityEvent  aAccessibilityEvent
     * @param aClassName           ClassName
     * @param aNodeText            aNodeText
     */
    private void processAccessibilityEventForLenvo(AccessibilityEvent aAccessibilityEvent, String aClassName,
                                                   String aNodeText) {
        checkSmartBar = -1;
        if (aNodeText.contains(this.getString(R.string.str_accessibility_installed3))) { //auto close installed
            closeInstalledUI(null);
        } else if (aNodeText.contains(this.getString(R.string.str_accessibility_uninstalled3))) {
            return;
            //skip
        } else if (aClassName.equals("com.lenovo.safecenter.defense.fragment.install.UninstallerActivity")) {
            //skip
        }
        else {//auto install
            performAutoInstall(null, "android.widget.TextView");
        }
    }

    /**
     * BroadcastReceiver
     */
    private BroadcastReceiver homecastReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            if (context == null || intent == null) {
                return;
            }
            String action = intent.getAction();
            if (TextUtils.equals(action, Intent.ACTION_SCREEN_ON)) {
                Log.d(LOG_TAG, "ACTION_SCREEN_ON");
            }

            if (TextUtils.equals(action, Intent.ACTION_SCREEN_OFF)) {
                Log.d(LOG_TAG, "ACTION_SCREEN_OFF)");
            }

            if (TextUtils.equals(action, Intent.ACTION_PACKAGE_ADDED)) {
                Log.d(LOG_TAG, "PackAge install");
            }

            if (TextUtils.equals(action, Intent.ACTION_INSTALL_PACKAGE)) {

                Log.d(LOG_TAG, "ACTION_INSTALL_PACKAGE");

            }

            if (TextUtils.equals(action, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {

                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

                if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                    // 长按Home键 或者 activity切换键
                    Log.d(LOG_TAG, "home  long onclick");
                    TamicWindowManager.dismiss();

                } else if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    // 短按Home键
                    Log.d(LOG_TAG, "home onclick");
                    TamicWindowManager.dismiss();


                } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                    // 锁屏
                    TamicWindowManager.dismiss();

                } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                    // samsung 长按Home键
                    TamicWindowManager.dismiss();

                }

            }



        }
    };


    /**
     * isAccessibilitySettingsOn
     */
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + TamicInstallService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(LOG_TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(LOG_TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(LOG_TAG, "***ACCESSIBILITY IS ENABLED*** -");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(LOG_TAG, " accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(LOG_TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(LOG_TAG, "---ACCESSIBILITY IS DISABLED--");
        }

        return false;
    }

}
