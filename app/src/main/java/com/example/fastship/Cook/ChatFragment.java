package com.example.fastship.Cook;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.fastship.Adapters.ChatAdapter;
import com.example.fastship.Models.Chat;
import com.example.fastship.R;
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

public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Chat> chatList;
    private EditText ed_message;
    private ImageButton b_send,b_update;
    private ChatAdapter adapter;
    private DatabaseReference databaseReference;
    private ValueEventListener seenListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView=view.findViewById(R.id.chat_rec_view);
        chatList=new ArrayList<>();
        ed_message=view.findViewById(R.id.ed_message);
        b_send=view.findViewById(R.id.b_send);
        b_update=view.findViewById(R.id.b_update);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new ChatAdapter(chatList,getContext(),ed_message,b_send,b_update);
        recyclerView.setAdapter(adapter);

        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(ed_message.getText().toString()))
                {
                    sendMessage(view);
                }
                else
                {
                    Toast.makeText(getContext(), "Вы не можете отправить пустое сообщение...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getMessages();
        seenMessages();


        return view;
    }
    private void sendMessage(View view)
    {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Chat");
        Chat chat=new Chat();
        String key=databaseReference.push().getKey();
        chat.setKey(key);
        chat.setSeen(false);
        chat.setMessage(ed_message.getText().toString());
        chat.setSender(FirebaseAuth.getInstance().getUid());
        chat.setTime(System.currentTimeMillis());
        databaseReference.child(key).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ed_message.setText("");
                    InputMethodManager inputMethodManager=(InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
                else {
                    Toast.makeText(getContext(), "Что-то пошло не так...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void  getMessages()
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Chat");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    Chat chat=ds.getValue(Chat.class);
                    chatList.add(chat);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void seenMessages()
    {
        databaseReference=FirebaseDatabase.getInstance().getReference("Chat");
        seenListener=databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    Chat chat=ds.getValue(Chat.class);
                    if(!chat.getSender().equals(FirebaseAuth.getInstance().getUid()))
                    {
                        if(!chat.isSeen())
                        {
                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("seen",true);
                            ds.getRef().updateChildren(hashMap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseReference.removeEventListener(seenListener);
    }
}