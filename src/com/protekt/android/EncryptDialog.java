package com.protekt.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: abandeali
 * Date: 6/14/14
 * Time: 2:51 PM
 *
 * Class that is responsible for throwing the alert box
 * and encrypting given password
 */
public class EncryptDialog {

    private final Context c;
    private final AccessibilityNodeInfo nodeInfo;
    private String domain;

    public EncryptDialog(Context c, AccessibilityNodeInfo nodeInfo) {
        this.c = c;
        this.nodeInfo = nodeInfo;
        this.domain = "";
    }

    public AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
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

        return alert;
    }

    private String encryptNode() {
        String hashed = "";
        System.out.println("IsPassword? " + nodeInfo.isPassword());
        String password = nodeInfo.toString() + domain;
        SHA3.DigestSHA3 md = new SHA3.DigestSHA3(256); //same as DigestSHA3 md = new SHA3.Digest256();
        try {
            md.update(password.getBytes("UTF-8"));
            byte[] digest = md.digest();
            hashed = org.bouncycastle.util.encoders.Hex.toHexString(digest);
            System.out.println(hashed);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return hashed;
    }

}
