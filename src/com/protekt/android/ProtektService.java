package com.protekt.android;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.app.Service;
import android.content.*;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by abandeali on 5/31/14.
 */
public class ProtektService extends AccessibilityService {

    private AccessibilityNodeInfo nodeInternal;
    private Context c;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        final int eventType = accessibilityEvent.getEventType();
        String eventText = null;
        switch(eventType) {
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                AccessibilityNodeInfo nodeInfo = accessibilityEvent.getSource();

                if (nodeInfo != null && nodeInfo.isPassword()) {

                    //inputData(this, "abc", nodeInfo);

                    EncryptDialog eDialog = new EncryptDialog(this);
                    eDialog.createDialog().show();

                    IntentFilter passwordIntent = new IntentFilter();
                    passwordIntent.addAction("com.protekt.android.setPassword");

                    nodeInternal = nodeInfo;
                    c = this;
                    registerReceiver(receiver, passwordIntent);

                }
                break;
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("com.protekt.android.setPassword")){
                String cipher = intent.getStringExtra("cipher");
                System.out.println("Password: "+cipher);

                inputData(c, cipher, nodeInternal);
                nodeInternal.recycle();

            }
            unregisterReceiver(this);
        }
    };

    public void inputData(Context c, String data, AccessibilityNodeInfo source) {
        String lastClip;
        ClipboardManager clipboard = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
        try {

            lastClip = clipboard.getPrimaryClip().getItemAt(0).coerceToText(c)
                    .toString();
        } catch (Exception e) {
            lastClip = "";
        }
        Log.d("THE NODE INFO", source.toString());

        ClipData clip = ClipData.newPlainText("protektPass", data);
        clipboard.setPrimaryClip(clip);

        Log.d("SENDING DATA", Boolean.toString(source.refresh()));
        Log.d("SENDING DATA", Boolean.toString(source
                .performAction(AccessibilityNodeInfo.ACTION_PASTE)));
        clip = ClipData.newPlainText("protektPass", lastClip);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public void onInterrupt() {
        System.out.println("onInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        System.out.println("onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
        info.notificationTimeout = 100;
        info.feedbackType = AccessibilityEvent.TYPES_ALL_MASK;
        setServiceInfo(info);
    }

}
