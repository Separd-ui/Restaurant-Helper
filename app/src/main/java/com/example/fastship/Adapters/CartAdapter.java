package com.example.fastship.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastship.Models.Cart;
import com.example.fastship.Models.Dish;
import com.example.fastship.R;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolderData> {
    private Context context;
    private List<Cart> cartList;
    private int count_item;
    private Boolean isOrder;

    public CartAdapter(Context context, List<Cart> cartList,Boolean isOrder) {
        this.context = context;
        this.cartList = cartList;
        this.isOrder=isOrder;
    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(!isOrder)
            view= LayoutInflater.from(context).inflate(R.layout.cart_item,parent,false);
        else
            view=LayoutInflater.from(context).inflate(R.layout.order_det_item,parent,false);
        return new ViewHolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderData holder, int position) {
        holder.setData(cartList.get(position));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViewHolderData extends RecyclerView.ViewHolder {
        private ImageButton b_delete;
        private Button b_plus,b_minus;
        private TextView name, price_all,count,count_det,name_det,price_det;
        private CircleImageView image;
        public ViewHolderData(@NonNull View itemView) {
            super(itemView);
            count_det=itemView.findViewById(R.id.order_det_count);
            name_det=itemView.findViewById(R.id.order_det_name);
            price_det=itemView.findViewById(R.id.order_det_price);
            b_delete=itemView.findViewById(R.id.cart_b_delete);
            b_minus=itemView.findViewById(R.id.cart_b_minus);
            b_plus=itemView.findViewById(R.id.cart_b_plus);
            name=itemView.findViewById(R.id.cart_name);
            price_all =itemView.findViewById(R.id.cart_price_all);
            count=itemView.findViewById(R.id.cart_count);
            image=itemView.findViewById(R.id.cart_image);
        }
        private void setData(Cart cart)
        {
            if(!isOrder)
            {
                getInfoAboutOrder(image,cart.getName(),name,cart.getCategory());
                count.setText(cart.getCount());
                price_all.setText(cart.getTotal());
                b_plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateCart(cart.getName(),cart.getPrice(),String.valueOf(Integer.parseInt(cart.getCount())+1));
                    }
                });
                b_minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Integer.parseInt(cart.getCount())>1)
                        {
                            updateCart(cart.getName(),cart.getPrice(),String.valueOf(Integer.parseInt(cart.getCount())-1));
                        }
                        else
                        {
                            Toast.makeText(context, "Вы не можете заказать менее одного блюда.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                b_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getUid())
                                .child(cart.getName());
                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(context, "Блюдо было успешно удалено", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(context, "Что-то пошло не так...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
            else
            {
                name_det.setText(cart.getName());
                price_det.setText(cart.getTotal());
                count_det.setText(cart.getCount());
            }

        }
    }
    private void getInfoAboutOrder(CircleImageView image,String key,TextView name,String category)
    {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Menu").child(category).child(key);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Dish dish=snapshot.getValue(Dish.class);
                Picasso.get().load(dish.getImageId()).into(image);
                name.setText(dish.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void updateCart(String key,String price,String count)
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getUid())
                .child(key);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("count",count);
        hashMap.put("total",String.valueOf(Integer.parseInt(count)*Integer.parseInt(price)));
        databaseReference.updateChildren(hashMap);
    }

}
