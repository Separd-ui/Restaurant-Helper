package com.example.fastship.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastship.Constans;
import com.example.fastship.MenuDetailActivity;
import com.example.fastship.Models.Dish;
import com.example.fastship.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolderData> {
    private Context context;
    private List<Dish> dishList;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth auth;
    private Boolean isCook,isHorizontal;

    public MenuAdapter(Context context, List<Dish> dishList,Boolean isCook,Boolean isHorizontal) {
        this.context = context;
        this.dishList = dishList;
        this.isCook=isCook;
        this.isHorizontal=isHorizontal;
        auth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(!isHorizontal)
            view= LayoutInflater.from(context).inflate(R.layout.menu_item,parent,false);
        else
            view= LayoutInflater.from(context).inflate(R.layout.menu_item_horizontal,parent,false);
        return new ViewHolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderData holder, int position) {
        holder.setData(dishList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, MenuDetailActivity.class);
                i.putExtra(Constans.KEY,dishList.get(position).getName());
                i.putExtra(Constans.CATEGORY,dishList.get(position).getCategory());
                if(isCook)
                    i.putExtra(Constans.isCook,true);
                else
                    i.putExtra(Constans.isCook,false);
                context.startActivity(i);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    public class ViewHolderData extends RecyclerView.ViewHolder {
        ShapeableImageView image;
        CircleImageView circleImageView;
        TextView name,price,time,name_circle;
        public ViewHolderData(@NonNull View itemView) {
            super(itemView);
            name_circle=itemView.findViewById(R.id.menu_name_circle);
            circleImageView=itemView.findViewById(R.id.menu_circle);
            image=itemView.findViewById(R.id.menu_image);
            name=itemView.findViewById(R.id.menu_name);
            price=itemView.findViewById(R.id.menu_price);
            time=itemView.findViewById(R.id.menu_time);
        }
        private void setData(Dish dish)
        {
            if(!isHorizontal)
            {
                Picasso.get().load(dish.getImageId()).into(image);
                name.setText(dish.getName());
                price.setText(dish.getPrice()+" лей");
                time.setText(dish.getTime()+" мин");
            }
            else
            {
                Picasso.get().load(dish.getImageId()).into(circleImageView);
                name_circle.setText(dish.getName());
            }
        }

    }
}
