package com.example.fastship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class DialogLogin extends Dialog {
    private TextInputEditText ed_name;
    private TextInputLayout ed;
    private Button b_save;
    private Context context;
    private boolean isRestore;

    private GetName getName;
    public DialogLogin(@NonNull Context context,GetName getName,boolean isRestore) {
        super(context);
        this.getName=getName;
        this.context=context;
        this.isRestore=isRestore;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_login);


        ed=findViewById(R.id.layout_3);
        b_save=findViewById(R.id.b_reg);
        ed_name=findViewById(R.id.ed_username);

        if(isRestore)
        {
            //ed.setHint("Введите почту для восстановления");
            b_save.setText("Отправить");
            ed_name.setHint("Введите почту для восстановления");
        }
        else
        {
            ed_name.setHint(R.string.username);
        }

        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(ed_name.getText().toString()))
                {
                    if(isRestore)
                    {
                        getName.getUsername(ed_name.getText().toString(),true);
                    }
                    else
                    {
                        getName.getUsername(ed_name.getText().toString(),false);
                    }
                }
                else
                {
                    Toast.makeText(context, "Поле должно быть заполнено...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}