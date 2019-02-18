package com.mobile.order.listener;

import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDigitsInputFilter implements InputFilter {

    Pattern mPattern;

    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        String s = Html.toHtml(dest).replaceAll("\\<.*?>","").replaceAll("\n","");
        Matcher matcher = mPattern.matcher(dest);
        if (!matcher.matches())
            return "";
        try {
            if(Double.parseDouble(s)<9999.99 && s.contains(".")) {
                return null;
            }else if ((Double.parseDouble(s)<1000 && !s.contains("."))||source.equals(".")) {
                return null;
            }else {
                return "";
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
