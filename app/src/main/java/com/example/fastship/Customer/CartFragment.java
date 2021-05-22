package com.example.fastship.Customer;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastship.Adapters.CartAdapter;
import com.example.fastship.Adapters.SpinnerAdapter;
import com.example.fastship.DialogOrder;
import com.example.fastship.Models.Cart;
import com.example.fastship.Models.Dish;
import com.example.fastship.Models.Order;
import com.example.fastship.R;
import com.example.fastship.getOrderDet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CartFragment extends Fragment implements getOrderDet {
    private RecyclerView recyclerView;
    private List<Cart> cartList;
    private TextView text_price;
    private Button b_order;
    private CartAdapter adapter;
    private int total_price;
    private LinearLayout layout,layout_empty;
    private int numberOrder;
    private int count=0;
    private Spinner spinner;
    private DialogOrder dialogOrder;
    private SpinnerAdapter spinnerAdapter;
    private String[] payList={"Наличными","Картой"};
    private int[] images={R.drawable.money,R.drawable.credit};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_cart, container, false);
        init(view);
        return view;
    }
    private void init(View view)
    {
        layout_empty=view.findViewById(R.id.layout_empty);
        layout=view.findViewById(R.id.cart_layout);
        recyclerView=view.findViewById(R.id.cart_rec);
        cartList=new ArrayList<>();
        text_price=view.findViewById(R.id.cart_price);
        b_order=view.findViewById(R.id.cart_order);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new CartAdapter(getContext(),cartList,false);
        recyclerView.setAdapter(adapter);

        spinner=view.findViewById(R.id.spinner);
        spinnerAdapter=new SpinnerAdapter(getContext(),R.layout.spinner_item,payList,images);
        spinner.setAdapter(spinnerAdapter);
        spinner.setPromptId(R.string.payment);



        b_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOrder=new DialogOrder(getContext(),CartFragment.this);
                dialogOrder.show();
            }
        });

        getNumberOrder();
        getCart();
        getTotalPrice();

    }
    private void clearCart()
    {
        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getUid());
        databaseReference1.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    DatabaseReference databaseReference2=FirebaseDatabase.getInstance().getReference("Number");
                    databaseReference2.setValue(numberOrder+1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                dialogOrder.dismiss();
                                Toast.makeText(getContext(), "Заказ был успешно опубликован", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }
    private void addOrderToCheck()
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Check").child(String.valueOf(numberOrder));
        if(count<cartList.size())
        {
            databaseReference.child(cartList.get(count).getName()).setValue(cartList.get(count)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        count++;
                        addOrderToCheck();
                    }
                }
            });
        }
        else
        {
            count=0;
            clearCart();
        }
    }
    private void getNumberOrder()
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Number");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                numberOrder=snapshot.getValue(int.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getCart()
    {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    cartList.add(ds.getValue(Cart.class));
                }
                if(cartList.size()>0){
                    layout.setVisibility(View.VISIBLE);
                    layout_empty.setVisibility(View.GONE);
                }
                else
                {
                    layout_empty.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getTotalPrice()
    {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total_price=0;
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    Cart cart=ds.getValue(Cart.class);
                    total_price+=Integer.parseInt(cart.getTotal());
                }
                text_price.setText(String.valueOf(total_price));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void getOrderDetail(String tel, String address,String notice) {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Order");
        Order order=new Order();
        order.setCustomer(FirebaseAuth.getInstance().getUid());
        order.setData(System.currentTimeMillis());
        order.setNumber(String.valueOf(numberOrder));
        order.setStatus(0);
        order.setNotice(notice);
        order.setAddress(address);
        order.setTel(tel);
        order.setRating(-1.0f);
        order.setPayment(spinner.getSelectedItem().toString());
        order.setTotal(String.valueOf(total_price));
        databaseReference.child(String.valueOf(numberOrder)).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    addOrderToCheck();
                }
                else
                    Toast.makeText(getContext(), "Что-то пошло не так...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}