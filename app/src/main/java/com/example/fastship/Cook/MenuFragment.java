package com.example.fastship.Cook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.example.fastship.Adapters.MenuAdapter;
import com.example.fastship.Constans;
import com.example.fastship.Models.Dish;
import com.example.fastship.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class MenuFragment extends Fragment {
    private MenuAdapter adapter,hitAdapter;
    private List<Dish> dishList,hitList;
    private List<String> keyHits;
    private Boolean isCook;
    private EditText ed_search;
    private String key_search="Пиццы";
    private ImageButton b_filter;
    private TextView title;
    private int tr,count=0;
    private DatabaseReference databaseReference;
    private String[] menuArray={"Пиццы","Салаты","Горячее","Десерты","Напитки"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_menu, container, false);
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        isCook=sharedPreferences.getBoolean(Constans.isCook,true);

        RecyclerView rec_view_hit=view.findViewById(R.id.menu_rec_hit);
        RecyclerView recyclerView=view.findViewById(R.id.rec_view_filter);
        //FloatingActionButton floatingActionButton=view.findViewById(R.id.cook_add);
        ed_search=view.findViewById(R.id.ed_search);
        b_filter=view.findViewById(R.id.b_filter);
        title=view.findViewById(R.id.menu_fragment_name);
        databaseReference=FirebaseDatabase.getInstance().getReference("Menu");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rec_view_hit.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));

        dishList=new ArrayList<>();
        hitList=new ArrayList<>();
        keyHits=new ArrayList<>();


        hitAdapter=new MenuAdapter(getContext(),hitList,isCook,true);
        adapter=new MenuAdapter(getContext(),dishList,isCook,false);

        rec_view_hit.setAdapter(hitAdapter);
        recyclerView.setAdapter(adapter);


        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchForDish(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        b_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });
        /*if (isCook)
        {
            floatingActionButton.show();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState==RecyclerView.SCROLL_STATE_IDLE)
                        floatingActionButton.show();
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if(dy>0 || dy<0 && floatingActionButton.isShown())
                        floatingActionButton.hide();
                }
            });
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(),NewDishActivity.class));
                }
            });
        }*/



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        tr=1;
        ed_search.setText("");
        title.setText("Пиццы");
        key_search="Пиццы";
        getRestaurantMenu("Пиццы");
        getHits();
    }

    private void getHits()
    {
        keyHits.clear();
        hitList.clear();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Menu").child("Хиты");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                keyHits.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                    keyHits.add(ds.getKey());
                getHitsFromMenu();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void getHitsFromMenu()
    {
        Log.d("MyLog","Size"+count);
        if(count<menuArray.length)
        {
            databaseReference.child(menuArray[count]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        Dish dish=ds.getValue(Dish.class);
                        for(String title:keyHits)
                            if(title.equals(dish.getName()))
                                hitList.add(dish);
                    }
                    count++;
                    getHitsFromMenu();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
        else
        {
            count=0;
            hitAdapter.notifyDataSetChanged();
        }
    }
    private void getRestaurantMenu(String key)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Menu").child(key);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dishList.clear();
                        for(DataSnapshot ds:snapshot.getChildren())
                        {
                            Dish dish=ds.getValue(Dish.class);
                            dishList.add(dish);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).start();
    }
    private void showPopupMenu()
    {
        PopupMenu popupMenu=new PopupMenu(getContext(),b_filter);
        popupMenu.inflate(R.menu.menu_filter);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.fil_pizza)
                {
                    if(tr!=1)
                    {
                        ed_search.setText("");
                        title.setText("Пиццы");
                        key_search="Пиццы";
                        getRestaurantMenu("Пиццы");
                        tr=1;
                    }
                    return true;
                }
                if(item.getItemId()==R.id.fil_salad)
                {
                    if(tr!=2)
                    {
                        ed_search.setText("");
                        title.setText("Салаты");
                        key_search="Салаты";
                        getRestaurantMenu("Салаты");
                        tr=2;
                    }
                    return true;
                }
                if(item.getItemId()==R.id.fil_drinks)
                {
                    if(tr!=3)
                    {
                        ed_search.setText("");
                        title.setText("Напитки");
                        tr=3;
                        key_search="Напитки";
                        getRestaurantMenu("Напитки");
                    }
                    return true;
                }
                if(item.getItemId()==R.id.fil_hot)
                {
                    if(tr!=4)
                    {
                        ed_search.setText("");
                        title.setText("Горячее");
                        key_search="Горячее";
                        tr=4;
                        getRestaurantMenu("Горячее");
                    }
                    return true;
                }
                if(item.getItemId()==R.id.fil_dessert)
                {
                    if(tr!=5)
                    {
                        ed_search.setText("");
                        title.setText("Десерты");
                        tr=5;
                        key_search="Десерты";
                        getRestaurantMenu("Десерты");
                    }
                    return true;
                }
                return false;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true);
        }
        popupMenu.show();
    }

    private void searchForDish(String text)
    {
        Query query=FirebaseDatabase.getInstance().getReference("Menu").child(key_search);
        query.orderByChild("search").startAt(text).endAt(text+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dishList.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    dishList.add(ds.getValue(Dish.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


}