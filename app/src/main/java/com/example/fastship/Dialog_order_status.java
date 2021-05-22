package com.example.fastship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fastship.Adapters.SpinnerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Dialog_order_status extends Dialog {
    private Context context;
    private Spinner spinner;
    private SpinnerAdapter spinnerAdapter;
    private Button button;
    private String key;
    private int status;
    private String[] title={"Выполняется","В пути","Доставлено"};
    private int[] images={R.drawable.prepare,R.drawable.food_delivery,R.drawable.received};

    public Dialog_order_status(@NonNull Context context,String key,int status) {
        super(context);
        this.key=key;
        this.context=context;
        this.status=status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_order_status);

        button=findViewById(R.id.b_update);
        spinner=findViewById(R.id.spinner2);
        spinner.setPromptId(R.string.spinner);
        spinnerAdapter=new SpinnerAdapter(context,R.layout.spinner_item,title,images);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(status);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Order").child(key);
                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("status",spinner.getSelectedItemPosition());
                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(context, "Операция прошла успешно", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                });
            }
        });



    }
}