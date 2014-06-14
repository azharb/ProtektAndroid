package com.protekt.android;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
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
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        final int eventType = accessibilityEvent.getEventType();
        String eventText = null;
        switch(eventType) {
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                AccessibilityNodeInfo nodeInfo = accessibilityEvent.getSource();

                if (nodeInfo != null && nodeInfo.isPassword()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Protekt");
                    builder.setMessage("Let's hash your pash");
                    builder.setPositiveButton("Hash", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Do something
                            dialog.dismiss();
                        }});
                        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        alert.show();

                    System.out.println("IsPassword? " + nodeInfo.isPassword());
                    String password = "password" + "google.com";
                    SHA3.DigestSHA3 md = new SHA3.DigestSHA3(256); //same as DigestSHA3 md = new SHA3.Digest256();
                    try {
                        md.update(password.getBytes("UTF-8"));
                        byte[] digest = md.digest();
                        String hashed = org.bouncycastle.util.encoders.Hex.toHexString(digest);
                        System.out.println(hashed);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                    Toast toShow = Toast.makeText(this, "To do: show protekt dialog here", Toast.LENGTH_SHORT);
                    toShow.setGravity(Gravity.CENTER, 0, 0);
                    toShow.show();
                }
                break;
        }
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
