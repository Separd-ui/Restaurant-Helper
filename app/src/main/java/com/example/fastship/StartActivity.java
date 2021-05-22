package com.example.fastship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button b_driver,b_customer;
        b_driver=findViewById(R.id.b_driver);
        b_customer=findViewById(R.id.b_customer);
        b_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(StartActivity.this,LoginActivity.class);
                i.putExtra(Constans.START_ID,1);
                startActivity(i);
            }
        });
        b_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(StartActivity.this,LoginActivity.class);
                i.putExtra(Constans.START_ID,2);
                startActivity(i);
            }
        });
    }

}