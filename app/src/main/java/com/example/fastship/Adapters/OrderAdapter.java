package com.example.fastship.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastship.Constans;
import com.example.fastship.Dialog_order_status;
import com.example.fastship.Models.Order;
import com.example.fastship.OrderDetail;
import com.example.fastship.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolderData> {
    private List<Order> orderList;
    private Context context;
    private Boolean isCook;
    private Dialog_order_status dialog_order_status;

    public OrderAdapter(List<Order> orderList, Context context,Boolean isCook) {
        this.orderList = orderList;
        this.context = context;
        this.isCook=isCook;
    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.order_item,parent,false);
        return new ViewHolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderData holder, int position) {

        holder.setData(orderList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, OrderDetail.class);
                i.putExtra(Constans.KEY,orderList.get(position).getNumber());
                i.putExtra(Constans.isCook,isCook);
                context.startActivity(i);

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(isCook)
                {
                    showPopupMenu(holder.itemView,orderList.get(position).getNumber(),orderList.get(position).getStatus());
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolderData extends RecyclerView.ViewHolder {
        TextView status,number,total;
        public ViewHolderData(@NonNull View itemView) {
            super(itemView);
            status=itemView.findViewById(R.id.order_status);
            number=itemView.findViewById(R.id.order_number);
            total=itemView.findViewById(R.id.order_total);
        }
        private void setData(Order order)
        {
            switch (order.getStatus())
            {
                case 0:
                    status.setText("выполняется");
                    break;
                case 1:
                    status.setText("в пути");
                    break;
                case 2:
                    status.setText("доставлено");
                    break;
            }
            number.setText("#"+order.getNumber());
            total.setText(String.valueOf(order.getTotal()));
        }
    }
    private void showPopupMenu(View view,String key,int status)
    {
        PopupMenu popupMenu=new PopupMenu(context,view);
        popupMenu.inflate(R.menu.order_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.update_status)
                {
                    if(status!=2)
                    {
                        dialog_order_status=new Dialog_order_status(context,key,status);
                        dialog_order_status.show();
                    }
                    else
                    {
                        AlertDialog();
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
    private void AlertDialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage("Заказ был доставлен!Вы не можете больше обновлять статус...");
        builder.setTitle("Статус");
        builder.setNeutralButton("Понятно", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

}
