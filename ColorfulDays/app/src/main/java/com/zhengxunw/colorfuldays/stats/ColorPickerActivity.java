package com.zhengxunw.colorfuldays.stats;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

public class ColorPickerActivity extends AppCompatActivity implements ColorPickerDialogListener {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowCustom(true)
                .setAllowPresets(false)
                .setColor(Color.BLACK)
                .show(ColorPickerActivity.this);
    }


    @Override
    public void onDialogDismissed(int dialogId) {
        onBackPressed();
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        Toast.makeText(getApplicationContext(), "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();
    }
}
