package com.example.fastship.Customer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fastship.Adapters.OrderAdapter;
import com.example.fastship.Models.Order;
import com.example.fastship.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView=view.findViewById(R.id.his_rec_view);
        orderList=new ArrayList<>();
        adapter=new OrderAdapter(orderList,getContext(),false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        getOrders();

        return view;
    }
    private void getOrders()
    {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Order");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    Order order=ds.getValue(Order.class);
                    if(order.getCustomer().equals(FirebaseAuth.getInstance().getUid()))
                        orderList.add(order);
                }
                Collections.reverse(orderList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}