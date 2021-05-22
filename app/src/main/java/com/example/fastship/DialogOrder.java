package com.example.fastship;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputEditText;

public class DialogOrder extends Dialog {
    private getOrderDet getOrderDet;
    private Button b_order;
    private Context context;
    private TextInputEditText ed_tel,ed_address,ed_notice;
    private SharedPreferences sharedPreferences;
    public DialogOrder(@NonNull Context context,getOrderDet getOrderDet) {
        super(context);
        this.getOrderDet=getOrderDet;
        this.context=context;
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_order);

        b_order=findViewById(R.id.d_order);
        ed_address=findViewById(R.id.d_address);
        ed_tel=findViewById(R.id.d_tel);
        ed_notice=findViewById(R.id.d_notice);

        if(sharedPreferences.getBoolean("set_switch",false))
        {
            ed_tel.setText(sharedPreferences.getString("set_tel","00000"));
            ed_address.setText(sharedPreferences.getString("set_address","empty"));
        }

        b_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(ed_address.getText().toString()) && !TextUtils.isEmpty(ed_tel.getText().toString()))
                {
                    if(!TextUtils.isEmpty(ed_notice.getText().toString()))
                    {
                        getOrderDet.getOrderDetail(ed_tel.getText().toString(),ed_address.getText().toString(),ed_notice.getText().toString());
                    }
                    else {
                        getOrderDet.getOrderDetail(ed_tel.getText().toString(),ed_address.getText().toString(),"empty");
                    }
                }
                else
                {
                    Toast.makeText(context, "Все поля должны быть заполнены...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
