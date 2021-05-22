package com.example.fastship.Cook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fastship.Adapters.SpinnerAdapter;
import com.example.fastship.Models.Dish;
import com.example.fastship.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewDishActivity extends AppCompatActivity {
    private TextInputEditText ed_name,ed_desc,ed_price,ed_time;
    private FloatingActionButton fb_save;
    private CircleImageView image;
    private Spinner spinner;
    private SpinnerAdapter spinnerAdapter;
    private String imageId;
    private Uri imageUri;
    private static  final int PERMESSION_REQ =10,IMAGE_REQ=11,CAMERA_REQ=12;
    private int[] images={R.drawable.pizza_menu,R.drawable.salad_menu,R.drawable.hot_menu,R.drawable.cake_menu,R.drawable.drink_menu};
    private String[] title_category={"Пиццы","Салаты","Горячее","Десерты","Напитки"};

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dish);

        ed_desc=findViewById(R.id.ed_desc);
        ed_name=findViewById(R.id.ed_name);
        ed_time=findViewById(R.id.ed_time);
        ed_price=findViewById(R.id.ed_price);
        fb_save=findViewById(R.id.dish_save);
        image=findViewById(R.id.dish_image);
        spinner=findViewById(R.id.spinner_category);
        spinnerAdapter=new SpinnerAdapter(this,R.layout.spinner_item,title_category,images);
        spinner.setAdapter(spinnerAdapter);
        imageId="empty";

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });
        fb_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(ed_name.getText().toString()) && !TextUtils.isEmpty(ed_desc.getText().toString())
                && !TextUtils.isEmpty(ed_price.getText().toString()) && !TextUtils.isEmpty(ed_time.getText().toString())
                && !imageId.equals("empty"))
                {
                    uploadImage();
                }
                else {
                    Toast.makeText(NewDishActivity.this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void uploadImage()
    {
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Идет загрузка...");
        progressDialog.show();

        Bitmap bitmap=null;
        try {
            bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageId));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();

        StorageReference storageReference= FirebaseStorage.getInstance().getReference("menu").child(ed_name.getText().toString()+"_Image");
        UploadTask uploadTask=storageReference.putBytes(bytes);
        Task<Uri> task=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful())
                {
                    imageId=task.getResult().toString();
                    saveInf();
                }
                else {
                    Toast.makeText(NewDishActivity.this, "Не удалось загрузить фото...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void saveInf()
    {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Menu").child(spinner.getSelectedItem().toString()).
                child(ed_name.getText().toString());
        Dish dish=new Dish();
        dish.setDesc(ed_desc.getText().toString());
        dish.setImageId(imageId);
        dish.setName(ed_name.getText().toString());
        dish.setTime(ed_time.getText().toString());
        dish.setPrice(ed_price.getText().toString());
        dish.setCategory(spinner.getSelectedItem().toString());
        dish.setSearch(ed_name.getText().toString().toLowerCase());
        databaseReference.setValue(dish).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    finish();

                }
                else
                {
                    Toast.makeText(NewDishActivity.this, "Что-то пошло не так", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            {
                shopPopupMenu();
            }
            else {
                if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    Toast.makeText(this, "Вы должны одобрить разрешения для доступа к камере и галерее.", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMESSION_REQ);
            }
        }
        else
        {
            shopPopupMenu();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode== PERMESSION_REQ)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                shopPopupMenu();
            }
            else
            {
                Toast.makeText(this, "Резрешение отклонено...", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openCamera()
    {

        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"New Picture");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"From camera");
        imageUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(i,CAMERA_REQ);
    }
    private void getImageFromGallery()
    {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(i,IMAGE_REQ);
    }
    private void shopPopupMenu()
    {
        PopupMenu popupMenu=new PopupMenu(this,image);
        popupMenu.inflate(R.menu.image_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.camera)
                {
                    openCamera();
                }
                if(item.getItemId()==R.id.gallery)
                {
                    getImageFromGallery();
                }

                return true;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true);
        }
        popupMenu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && data!=null && requestCode==IMAGE_REQ)
        {
            imageId=data.getData().toString();
            image.setImageURI(data.getData());
        }
        if(RESULT_OK == resultCode && requestCode==CAMERA_REQ)
        {
            image.setImageURI(imageUri);
            imageId=imageUri.toString();
        }
    }
}