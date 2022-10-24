package app.clearcreek.catering.utils;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.annotation.StringRes;

public class UiUtils {
    public static ProgressDialog getProgress(Context context, @StringRes int resource) {
        return getProgress(context, context.getString(resource));
    }

    public static ProgressDialog getProgress(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        return progressDialog;
    }
}
