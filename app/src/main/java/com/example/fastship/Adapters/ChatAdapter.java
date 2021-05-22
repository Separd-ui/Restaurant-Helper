package com.example.fastship.Adapters;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastship.Models.Chat;
import com.example.fastship.Models.User;
import com.example.fastship.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolderData> {
    private List<Chat> chats;
    private Context context;
    private final int TYPE_LEFT=1;
    private final int TYPE_RIGHT=2;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth auth;
    private EditText ed_mes;
    private ImageButton b_send,b_update;
    private String key;

    public ChatAdapter(List<Chat> chats, Context context, EditText ed_mes, ImageButton b_send, ImageButton b_update) {
        this.chats = chats;
        this.context = context;
        this.ed_mes=ed_mes;
        this.b_send=b_send;
        this.b_update=b_update;
        firebaseDatabase=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==TYPE_LEFT)
            view= LayoutInflater.from(context).inflate(R.layout.chat_left,parent,false);
        else
            view=LayoutInflater.from(context).inflate(R.layout.chat_right,parent,false);
        return new ViewHolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderData holder, int position) {
        holder.setData(chats.get(position));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(chats.get(position).getSender().equals(auth.getUid()))
                {
                    key=chats.get(position).getKey();
                    showPopupMenu(holder.itemView,chats.get(position).getMessage());

                    return true;
                }
                return false;
            }
        });
        b_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference=firebaseDatabase.getReference("Chat").child(key);
                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("message",ed_mes.getText().toString());
                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Редактирование прошло успешно!", Toast.LENGTH_SHORT).show();
                            b_update.setVisibility(View.GONE);
                            b_send.setVisibility(View.VISIBLE);
                            ed_mes.setText("");
                        }
                        else {
                            Toast.makeText(context, "Не получилось отредактировать сообщениею...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chats.get(position).getSender().equals(auth.getUid()))
            return TYPE_RIGHT;
        else
            return TYPE_LEFT;

    }

    public class ViewHolderData extends RecyclerView.ViewHolder {
        private TextView right_name,right_time,right_mes,left_name,left_mes,left_time;
        private View seen;
        public ViewHolderData(@NonNull View itemView) {
            super(itemView);
            right_time=itemView.findViewById(R.id.right_time);
            right_mes=itemView.findViewById(R.id.right_mes);
            left_name=itemView.findViewById(R.id.left_name);
            left_mes=itemView.findViewById(R.id.left_mes);
            left_time=itemView.findViewById(R.id.left_time);
            seen=itemView.findViewById(R.id.right_seen);
        }
        private void setData(Chat chat)
        {
            if(chat.getSender().equals(auth.getUid()))
            {
                if(!chat.isSeen())
                    seen.setVisibility(View.VISIBLE);
                else
                    seen.setVisibility(View.GONE);
                right_mes.setText(chat.getMessage());
                right_time.setText(DateFormat.format("hh:mm",chat.getTime()));
            }
            else
            {
                getUsername(left_name,chat.getSender());
                left_mes.setText(chat.getMessage());
                left_time.setText(DateFormat.format("hh:mm",chat.getTime()));
            }
        }
    }
    private void showPopupMenu(View view,String message)
    {
        PopupMenu popupMenu=new PopupMenu(context,view);
        popupMenu.inflate(R.menu.chat_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.mes_delete)
                {
                    DatabaseReference databaseReference=firebaseDatabase.getReference("Chat").child(key);
                    databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(context, "Сообщение было успешно удалено!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(context, "Не получилось удалить сообщение...", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                }
                if(item.getItemId()==R.id.mes_edit)
                {
                    ed_mes.setText(message);
                    b_send.setVisibility(View.GONE);
                    b_update.setVisibility(View.VISIBLE);

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
    private void getUsername(TextView username,String ui){
        DatabaseReference databaseReference=firebaseDatabase.getReference("Users").child("cook").child(ui);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
