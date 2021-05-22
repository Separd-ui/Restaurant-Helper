package com.example.fastship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastship.Cook.CookActivity;
import com.example.fastship.Customer.CustomerActivity;
import com.example.fastship.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity implements GetName {
    private TextInputEditText ed_mail,ed_pas;
    private Button b_sign_in,b_sign_up;
    private FirebaseAuth auth;
    private TextView t_forget;
    private int index;
    private Toolbar toolbar;
    private TextWatcher textWatcher;
    private DialogLogin dialogLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }
    private void init()
    {
        textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                b_sign_in.setEnabled(!TextUtils.isEmpty(ed_mail.getText().toString().trim()) && !TextUtils.isEmpty(ed_pas.getText().toString().trim()));
                b_sign_up.setEnabled(!TextUtils.isEmpty(ed_mail.getText().toString().trim()) && !TextUtils.isEmpty(ed_pas.getText().toString().trim()));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        toolbar=findViewById(R.id.sign_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GetIntent();

        auth=FirebaseAuth.getInstance();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        t_forget=findViewById(R.id.t_forget);
        b_sign_in=findViewById(R.id.b_sign_in);
        b_sign_up=findViewById(R.id.b_sign_up);
        ed_mail=findViewById(R.id.ed_mail);
        ed_pas=findViewById(R.id.ed_pas);

        ed_mail.addTextChangedListener(textWatcher);
        ed_pas.addTextChangedListener(textWatcher);

        b_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(ed_mail.getText().toString()) && !TextUtils.isEmpty(ed_pas.getText().toString()))
                {
                    if(index==1)
                    {
                        dialogLogin=new DialogLogin(LoginActivity.this,LoginActivity.this,false);
                        dialogLogin.show();
                    }
                    else
                    {
                        sign_up(ed_mail.getText().toString(),ed_pas.getText().toString(),"empty");
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Заполните все поля...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(ed_mail.getText().toString()) && !TextUtils.isEmpty(ed_pas.getText().toString()))
                {
                    sign_in(ed_mail.getText().toString(),ed_pas.getText().toString());
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                }
            }
        });
        t_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLogin=new DialogLogin(LoginActivity.this,LoginActivity.this,true);
                dialogLogin.show();
            }
        });
    }
    private void GetIntent()
    {
        Intent i=getIntent();
        if(i!=null)
        {
            index=i.getIntExtra(Constans.START_ID,1);
            if(index==1)
            {
                toolbar.setTitle("Вход для повара");
            }
            else
            {
                toolbar.setTitle("Вход для заказчика");
            }
        }
    }


    private void restorePas(String mail)
    {
        FirebaseAuth.getInstance().sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, "Мы отправили вам письмо для восстановление пароля", Toast.LENGTH_SHORT).show();
                    dialogLogin.dismiss();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Не получилось отправить письмо...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sign_up(String mail,String pas,String username)
    {
        auth.createUserWithEmailAndPassword(mail,pas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
                    User user=new User();
                    user.setUi(auth.getUid());
                    user.setUsername(username);
                    if(index==1)
                    {
                        databaseReference.child("cook").child(auth.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(LoginActivity.this, "Вы вошли как:"+auth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, CookActivity.class));
                                    finish();
                                }
                                else
                                {
                                    b_sign_in.setEnabled(true);
                                    b_sign_up.setEnabled(true);
                                    Toast.makeText(LoginActivity.this, "Что-то пошло не так", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                    else
                    {
                        databaseReference.child("customers").child(auth.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(LoginActivity.this, "Вы вошли как:"+auth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, CustomerActivity.class));
                                    finish();
                                }
                                else
                                {
                                    b_sign_in.setEnabled(true);
                                    b_sign_up.setEnabled(true);
                                    Toast.makeText(LoginActivity.this, "Что-то пошло не так", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Пароль должен содержать не менее 6 символов", Toast.LENGTH_SHORT).show();
                    b_sign_in.setEnabled(true);
                    b_sign_up.setEnabled(true);
                }
            }
        });
    }
    private void sign_in(String mail,String pas)
    {
        auth.signInWithEmailAndPassword(mail,pas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    if (index==1)
                    {
                        checkAuthorization("customers",1);
                    }
                    else
                    {
                        checkAuthorization("cook",2);
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Вы ввели неверный e-mail или пароль", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void checkAuthorization(String key,int indexLogin)
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(key).child(FirebaseAuth.getInstance().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Toast.makeText(LoginActivity.this, "Вы не можете зайти под той же учётной записью в другую категорию", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(indexLogin==1)
                    {
                        Toast.makeText(LoginActivity.this, "Вы вошли как:"+auth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,CookActivity.class));
                        finish();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Вы вошли как:"+auth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,CustomerActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    @Override
    public void getUsername(String name,boolean isRestore) {
        dialogLogin.dismiss();
        if(isRestore)
        {
            restorePas(name);
        }
        else
        {
            b_sign_in.setEnabled(false);
            b_sign_up.setEnabled(false);
            sign_up(ed_mail.getText().toString(),ed_pas.getText().toString(),name);
        }

    }
}