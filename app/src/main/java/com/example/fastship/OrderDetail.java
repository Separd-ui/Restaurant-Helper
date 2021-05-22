package com.example.fastship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastship.Adapters.CartAdapter;
import com.example.fastship.Models.Cart;
import com.example.fastship.Models.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderDetail extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<Cart> cartList;
    private TextView data,status,tel,total,payment, address,notice,text_rating,rat_start;
    private String numberOrder;
    private RatingBar ratingBar;
    private Button b_rating;
    private LinearLayout rating_layout;
    private Boolean isCook;
    private Animation rating_anim;
    private SharedPreferences sharedPreferences;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        GetIntent();
        init();
        getOrder();
        getInfoAboutOrder();
    }
    private void init()
    {
        Toolbar toolbar=findViewById(R.id.det_order_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setTitle("Информация о заказе");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rat_start=findViewById(R.id.rat_text_start);
        rating_layout=findViewById(R.id.rating_layout);
        ratingBar=findViewById(R.id.ratingBar);
        text_rating=findViewById(R.id.rat_text);
        b_rating=findViewById(R.id.rat_b);
        data=findViewById(R.id.det_data);
        status=findViewById(R.id.det_status);
        tel=findViewById(R.id.det_tel);
        total=findViewById(R.id.det_total);
        payment=findViewById(R.id.det_payment);
        address=findViewById(R.id.det_adress);
        notice=findViewById(R.id.det_notice);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        rating_anim= AnimationUtils.loadAnimation(this,R.anim.rating_animation);

        recyclerView=findViewById(R.id.det_rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartList=new ArrayList<>();
        adapter=new CartAdapter(this,cartList,true);
        recyclerView.setAdapter(adapter);


        b_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Order").child(numberOrder);
                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("rating",ratingBar.getRating());
                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            ratingBar.setIsIndicator(true);
                            rat_start.setVisibility(View.GONE);
                            b_rating.setVisibility(View.GONE);
                            text_rating.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            Toast.makeText(OrderDetail.this, "Не получилось отправить...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
    private void GetIntent()
    {
        Intent i=getIntent();
        if(i!=null)
        {
            isCook=i.getBooleanExtra(Constans.isCook,false);
            numberOrder=i.getStringExtra(Constans.KEY);
        }
    }
    private void getOrder()
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Check").child(numberOrder);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    Cart cart=ds.getValue(Cart.class);
                    cartList.add(cart);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getInfoAboutOrder()
    {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Order").child(numberOrder);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order order=snapshot.getValue(Order.class);
                tel.setText(order.getTel());
                total.setText(order.getTotal());
                address.setText(order.getAddress());
                if(order.getStatus()==2 && !isCook){
                    if(sharedPreferences.getBoolean("switch_preference_anim",true))
                        rating_layout.startAnimation(rating_anim);
                    rating_layout.setVisibility(View.VISIBLE);
                    if(order.getRating()!=-1.0f)
                    {
                        rat_start.setVisibility(View.GONE);
                        b_rating.setVisibility(View.GONE);
                        ratingBar.setIsIndicator(true);
                        ratingBar.setRating(order.getRating());
                    }
                }
                if(order.getNotice().equals("empty"))
                    notice.setText("Пусто");
                else
                    notice.setText(order.getNotice());
                switch (order.getStatus())
                {
                    case 0:
                        status.setText("Выполняется");
                        break;
                    case 1:
                        status.setText("В пути");
                        break;
                    case 2:
                        status.setText("Доставлено");
                        break;
                }
                data.setText(DateFormat.format("mm.dd.yyyy| hh:mm",order.getData()));
                payment.setText(order.getPayment());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}