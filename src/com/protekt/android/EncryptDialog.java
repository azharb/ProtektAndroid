package com.protekt.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

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
    private String password;
    private String domain;

    public EncryptDialog(Context c) {
        this.c = c;
        this.domain = "";
        this.password = "";
    }

    public AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Protekt");

        LinearLayout linearView= new LinearLayout(c);
        linearView.setOrientation(LinearLayout.VERTICAL);

        final EditText domainInput = new EditText(c);
        domainInput.setHint("Domain name e.g. google.com");
        linearView.addView(domainInput);

        final EditText passInput = new EditText(c);
        passInput.setHint("Domain name e.g. google.com");
        linearView.addView(passInput);

        builder.setView(linearView);

        builder.setPositiveButton("Hash", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Do something
                domain = domainInput.getText().toString();
                password = passInput.getText().toString();
                System.out.println(encryptNode());
                dialog.dismiss();
            }});
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return alert;
    }

    private String encryptNode() {
        String hashed = "";
        String toHash = password + domain.toLowerCase();
        SHA3.DigestSHA3 md = new SHA3.DigestSHA3(256); //same as DigestSHA3 md = new SHA3.Digest256();
        try {
            md.update(toHash.getBytes("UTF-8"));
            byte[] digest = md.digest();
            hashed = org.bouncycastle.util.encoders.Hex.toHexString(digest);
            System.out.println(hashed);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



        return reBuildHash(hashed);
    }

    private String getEventText(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }

    private String reBuildHash(String hashed) {
        char[] hashblock = new char []{
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '@', '!',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'

        };

        char[] specialChar = new char[] { '@', '%', '+', '\\', '/', '\'', '!', '#', '$', '^', '?', ':', '.', '(', ')', ']', '[', '~', '-'};

        int count = 0;
        String newHash = "";
        for (int i = 0; i < hashed.length(); i++)
        {
            count += Integer.parseInt(String.valueOf(hashed.charAt(i)), 16);
            if((i+1)%4 == 0)
            {
                newHash += hashblock[count];
                count = 0;
            }

        }

        BigInteger hashInt = new BigInteger(hashed, 16);
        int specialLen = specialChar.length;
        int modVal = hashInt.mod(BigInteger.valueOf(specialLen)).intValue();
        char trailingSplChar = specialChar[modVal];
        newHash += trailingSplChar;

        return newHash;

    }

}
