package com.example.fastship.Cook;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.fastship.Adapters.OrderAdapter;
import com.example.fastship.Models.Order;
import com.example.fastship.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrdersFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private LinearLayout layout_done;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_orders, container, false);

        recyclerView=view.findViewById(R.id.order_rec_view);
        layout_done=view.findViewById(R.id.order_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList=new ArrayList<>();
        adapter=new OrderAdapter(orderList,getContext(),true);
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
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    Order order=ds.getValue(Order.class);
                    if(order.getStatus()!=2)
                        orderList.add(order);
                }
                if(orderList.size()>0)
                {
                    layout_done.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                else
                {
                    layout_done.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
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