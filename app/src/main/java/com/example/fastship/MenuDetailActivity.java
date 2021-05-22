package com.example.fastship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastship.Models.Cart;
import com.example.fastship.Models.Dish;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuDetailActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private TextView price,time,desc,name,t_count;
    private CircleImageView image;
    private String key,category;
    private int price_item;
    private int count=0;
    private Boolean isCook;
    private Button b_plus,b_minus,b_add_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        GetIntent();
        init();
        getInfoAboutDish();
    }
    private void init()
    {
        Toolbar toolbar=findViewById(R.id.det_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle("Информация о блюде");

        t_count=findViewById(R.id.det_count);
        price=findViewById(R.id.det_price);
        time=findViewById(R.id.det_time);
        linearLayout=findViewById(R.id.det_linearLayout);
        desc=findViewById(R.id.det_desc);
        name=findViewById(R.id.det_name);
        image=findViewById(R.id.det_image);
        b_minus=findViewById(R.id.b_minus);
        b_plus=findViewById(R.id.b_plus);
        b_add_cart=findViewById(R.id.det_add_cart);
        if(!isCook)
            linearLayout.setVisibility(View.VISIBLE);

        b_add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addToCart();
            }
        });
        b_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count>=0)
                {
                    count++;
                    t_count.setText(String.valueOf(count));
                }

            }
        });
        b_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count>0)
                {
                    count--;
                    t_count.setText(String.valueOf(count));
                }

            }
        });
    }
    private void GetIntent()
    {
        Intent i=getIntent();
        if(i!=null)
        {
            category=i.getStringExtra(Constans.CATEGORY);
            key=i.getStringExtra(Constans.KEY);
            isCook=i.getBooleanExtra(Constans.isCook,false);
        }
    }
    private void addToCart()
    {
        if(count>0)
        {
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getUid()).child(key);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        Cart cart=snapshot.getValue(Cart.class);
                        HashMap<String ,Object> hashMap=new HashMap<>();
                        hashMap.put("count",String.valueOf(Integer.parseInt(cart.getCount())+count));
                        hashMap.put("total",String.valueOf((Integer.parseInt(cart.getCount())+count)*price_item));
                        snapshot.getRef().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MenuDetailActivity.this, "Блюдо было успешно добавлено в корзину.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getUid());
                        Cart cart=new Cart();
                        cart.setName(key);
                        cart.setCategory(category);
                        cart.setPrice(String.valueOf(price_item));
                        cart.setTotal(String.valueOf(count*price_item));
                        cart.setCount(String.valueOf(count));
                        databaseReference1.child(key).setValue(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(MenuDetailActivity.this, "Блюдо было успешно добавлено в корзину.", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(MenuDetailActivity.this, "Что-то пошло не так...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else
        {
            Toast.makeText(this, "Вы не выбрали кол-во блюд...", Toast.LENGTH_SHORT).show();
        }
    }
    private void getInfoAboutDish()
    {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Menu").child(category).child(key);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Dish dish=snapshot.getValue(Dish.class);
                Picasso.get().load(dish.getImageId()).into(image);
                time.setText(dish.getTime()+" мин");
                price.setText(dish.getPrice()+" лей");
                name.setText(dish.getName());
                desc.setText(dish.getDesc());
                price_item=Integer.parseInt(dish.getPrice());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}