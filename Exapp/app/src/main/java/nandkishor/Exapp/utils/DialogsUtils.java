package nandkishor.Exapp.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import java.util.List;

import rx.functions.Func1;

public class DialogsUtils {

    public static ProgressDialog showProgressDialog(Activity activity, String message){
        ProgressDialog mDialog = new ProgressDialog(activity);
        mDialog.setMessage(message);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(false);
        mDialog.show();
        return mDialog;
    }
}
