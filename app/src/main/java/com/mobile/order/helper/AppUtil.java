package com.mobile.order.helper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.order.R;
import com.mobile.order.model.SalesFilter;

import org.greenrobot.greendao.annotation.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class AppUtil {

    /**
     * Variable to hold the toast object
     */
    private static Toast toast;
    /**
     * Method to show toasts for long duration.
     * This method allows user to override the existing toasts.
     *
     * @param context Context in which the longToast has to be shown.
     * @param msg     Text which has to be toasted.
     */
    public static void longToast(@NotNull Context context, @NotNull String msg) {
        toast(context, msg, Toast.LENGTH_LONG);
    }

    /**
     * Method to show toasts for short duration.
     * This method allows user to override the existing toasts.
     *
     * @param context Context in which the longToast has to be shown.
     * @param msg     Text which has to be toasted.
     */
    public static void shortToast(@NotNull Context context, @NotNull String msg) {
        toast(context, msg, Toast.LENGTH_SHORT);
    }

    /**
     * Method to show toasts.
     * This method allows user to override the existing toasts.
     *
     * @param context         Context in which the toast has to be shown.
     * @param msg             Text which has to be toasted.
     * @param displayDuration Display duration indicating how long the toast has to be shown.
     */
    private static void toast(Context context, String msg, int displayDuration) {

        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), msg, displayDuration);
            toast.show();
        } else {
            toast.setDuration(displayDuration);
            toast.setText(msg);
            toast.show();
        }
    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }
    public static SpannableString applyFontStyle(String s, Typeface font) {
        SpannableString spannableString = new SpannableString(s);
        spannableString.setSpan(new CustomTypefaceSpan(font), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @NonNull
    public static View.OnClickListener addCalendarDialog(final EditText calText, final Activity activity) {

        final FirestoreUtil firestoreUtil=new FirestoreUtil();
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date userDate  = new Date();
                final DatePickerDialog picker;
                String selectedDate = calText.getText().toString();
                if(!selectedDate.isEmpty()){
                    try {
                        userDate = firestoreUtil.filterDateFormat.parse(selectedDate);
                    }
                    catch (ParseException pe){
                    }
                }
                final Calendar cldr = Calendar.getInstance();
                cldr.setTime(userDate);
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                calText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            calText.setText("");
                        }
                    }
                });
                picker.show();
            }
        };
    }
    public static String getShortMonth(int month){
        switch(month){
            case 1: return "JAN";
            case 2: return "FEB";
            case 3: return "MAR";
            case 4: return "APR";
            case 5: return "MAY";
            case 6: return "JUN";
            case 7: return "JUL";
            case 8: return "AUG";
            case 9: return "SEP";
            case 10: return "OCT";
            case 11: return "NOV";
            case 12: return "DEV";
        }
        return "";
    }
    public static String getShortMonthFromMonthAndYear(String combine){
        String sep = "-";
        String[] words = combine.split(sep);
        String shortMonth=getShortMonth(Integer.valueOf(words[0]));

        return shortMonth+sep+words[1];
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static SalesFilter getSalesFilters(Spinner customer, Spinner salesPerson, EditText fromDate, EditText toDate, Spinner settledStatus, int defaultEarlyDays) {
        SalesFilter aFilter = new SalesFilter();
        FirestoreUtil util=new FirestoreUtil();
        aFilter.setCustomerName(customer!=null && customer.getSelectedItemPosition()!=0 ?customer.getSelectedItem().toString():"");
        aFilter.setSalesPersonId(salesPerson!=null && salesPerson.getSelectedItemPosition()!=0 ?salesPerson.getSelectedItem().toString():"");
        aFilter.setFromDate(null);

        if(null!=settledStatus){
            aFilter.setFullySettled(null);
            if(settledStatus.getSelectedItemPosition()==1){
                aFilter.setFullySettled(true);
            }
            if(settledStatus.getSelectedItemPosition()==2){
                aFilter.setFullySettled(false);
            }
        }

        String selectedToDate = null!=toDate?toDate.getText().toString():null;
        aFilter.setToDate(new Date());
        //Override default if user would have selected
        if(null!=selectedToDate && !selectedToDate.isEmpty()){
            try {
                aFilter.setToDate(util.filterDateFormat.parse(selectedToDate));
            }
            catch (ParseException pe){
            }
        }

        String selectedFromDate = null!=fromDate?fromDate.getText().toString():null;
        if(null!=selectedFromDate && !selectedFromDate.isEmpty()){
            try {
                aFilter.setFromDate(util.filterDateFormat.parse(selectedFromDate));
            }
            catch (ParseException pe){
            }
        }
        else{
            Calendar cal = Calendar.getInstance();
            if(defaultEarlyDays!=0){
                //Default 90 days before from today
                cal.setTime(aFilter.getToDate());
                cal.add(Calendar.DATE, defaultEarlyDays);
                aFilter.setFromDate(cal.getTime());
            }
            else{
                // Set it to today's date and mark time with 0s
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                aFilter.setFromDate(cal.getTime());
            }

        }
        return aFilter;
    }
    /**
     * Method used to check network available or not
     *
     * @param context Context in which the conversion will happen.
     * @return Boolean
     */
    public static boolean isNetworkAvailable(@NotNull Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        return activeNetwork != null && activeNetwork.isAvailable();
    }
    /**
     * Show network offline status from activity
     * Ref: https://stackoverflow.com/questions/4486034/get-root-view-from-current-activity/5069354#5069354
     *
     * @param activity Current activity
     */
    public static void showOffline(@NotNull Activity activity) {
        View view = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);

        showOffline(view);
    }

    /**
     * Show network offline status
     *
     * @param view Current focused view
     */
    public static void showOffline(@NotNull View view) {
        Snackbar snackbar = Snackbar
                .make(view, view.getContext().getString(R.string.network_offline), Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.RED);
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(FontHelper.getFont(Fonts.MULI_SEMI_BOLD));
        snackbar.show();
    }
    /**
     * Hide keyboard manually
     */
    public static void hideKeyboard(Activity activity, EditText textInput) {
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(textInput.getWindowToken(), 0);
        }
    }
}
