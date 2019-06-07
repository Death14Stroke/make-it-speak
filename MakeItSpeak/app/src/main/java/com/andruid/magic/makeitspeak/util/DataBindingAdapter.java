package com.andruid.magic.makeitspeak.util;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import java.text.DateFormat;

public class DataBindingAdapter {
    @BindingAdapter({"msToDate"})
    public static void msToDate(TextView textView, long ms){
        DateFormat dateFormat = DateFormat.getDateInstance();
        String str = dateFormat.format(ms);
        textView.setText(str);
    }
}