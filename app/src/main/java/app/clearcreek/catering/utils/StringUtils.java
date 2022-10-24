package app.clearcreek.catering.utils;

import android.text.Spanned;

import androidx.core.text.HtmlCompat;

import java.text.NumberFormat;
import java.util.Locale;

public class StringUtils {
    public static Spanned parseHtml(String htmlString) {
        return HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT);
    }

    public static String formatCurrency(double amount) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return numberFormat.format(amount);
    }
}
