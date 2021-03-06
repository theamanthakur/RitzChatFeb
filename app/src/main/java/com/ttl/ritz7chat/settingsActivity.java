package com.ttl.ritz7chat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class settingsActivity extends AppCompatActivity {
    CircleImageView imageProfile;
    EditText usernameEdt, statusEdt, userTaskedt;
    Button btnUpdate;
    String getCurrUserID;
    ProgressDialog dialog;
    Uri imageUri;
    Toolbar toolbar;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    private StorageReference profileImageRef;

    private static final int galleryPick = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        InitializeSettingFeilds();
        mAuth = FirebaseAuth.getInstance();
        getCurrUserID =  mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        profileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

//        usernameEdt.setVisibility(View.INVISIBLE);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });
        RetrieveUserInfo();
        
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,galleryPick);
            }
        });
    }
//--------On act result////////////


    //////////---------------------------
    private void RetrieveUserInfo() {

        databaseReference.child("UsersChat").child(getCurrUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.exists()) && (snapshot.hasChild("name")) && (snapshot.hasChild("image"))){

                    String getUsername = snapshot.child("name").getValue().toString();
                    String getTask = snapshot.child("task").getValue().toString();
                    String getStatus = snapshot.child("status").getValue().toString();
                    String getImage = snapshot.child("image").getValue().toString();

                    usernameEdt.setText(getUsername);
                    statusEdt.setText(getStatus);
                    userTaskedt.setText(getTask);
                    Picasso.get().load(getImage).into(imageProfile);
//                    Glide.with(settingsActivity.this).load(getImage).into(imageProfile);


                }else if((snapshot.exists()) && (snapshot.hasChild("name"))){
                    String getUsername = snapshot.child("name").getValue().toString();
                    String getTask = snapshot.child("task").getValue().toString();
                    String getStatus = snapshot.child("status").getValue().toString();
//                    String getImage = snapshot.child("image").getValue().toString();

                    usernameEdt.setText(getUsername);
                    statusEdt.setText(getStatus);
                    userTaskedt.setText(getTask);
                 //   Picasso.get().load(getImage).into(imageProfile);
                }else {
                    usernameEdt.setVisibility(View.VISIBLE);
                    Toast.makeText(settingsActivity.this, "Please Update Profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1,1)
//                    .start(this);
//            FilePathUri = data.getData();
//
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
//                imageProfile.setImageBitmap(bitmap);
//            }
//            catch (IOException e) {
//
//                e.printStackTrace();
//            }
//
//
//        }
//    }
//    public String GetFileExtension(Uri uri) {
//
//        ContentResolver contentResolver = getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
//
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==galleryPick && resultCode==RESULT_OK && data != null){
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                ///Loading bar\\\\\\\
                dialog.setTitle("Set Profile Image");
                dialog.setMessage("Please wait your image is uploading...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                ////----------------- end-------------////////////

                Uri resulturi = result.getUri();
                StorageReference filepath = profileImageRef.child(getCurrUserID + ".jpg");
//                filepath.putFile(resulturi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Uri> task) {
//                                if(task.isSuccessful()) {
//                                    Toast.makeText(settingsActivity.this, "Profile Pic Uploaded", Toast.LENGTH_SHORT).show();
//                                    final String downloaduri = taskSnapshot.getStorage().getDownloadUrl().toString();
//                                    databaseReference.child("UsersChat").child(getCurrUserID).child("image")
//                                            .setValue(downloaduri).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                Toast.makeText(settingsActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
//                                                dialog.dismiss();
//                                            } else {
//                                                String msg = task.getException().toString();
//                                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//                                                dialog.dismiss();
//                                            }
//                                        }
//                                    });
//                                }else{
//                                    String err = task.getException().toString();
//                            Toast.makeText(getApplicationContext(),err,Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//                });
//----------------------------******************---------------------------

                filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(settingsActivity.this, "Profile Pic Uploaded", Toast.LENGTH_SHORT).show();
                            final String downloaduri = task.getResult().getDownloadUrl().toString();
                            databaseReference.child("UsersChat").child(getCurrUserID).child("image")
                                    .setValue(downloaduri)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(settingsActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }else{
                                                String msg = task.getException().toString();
                                                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });

                        }else{
                            String err = task.getException().toString();
                            Toast.makeText(getApplicationContext(),err,Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }
    }

    private void updateSettings() {
        String setUsername = usernameEdt.getText().toString();
        String setStatus = statusEdt.getText().toString();
        String setTask = userTaskedt.getText().toString();


        if (TextUtils.isEmpty(setUsername)){
            usernameEdt.setError("Enter Valid Username");
            usernameEdt.requestFocus();
            return;
        }else if (TextUtils.isEmpty(setStatus)){
            statusEdt.setError("Enter Valid Status");
            statusEdt.requestFocus();
            return;
        }else if (TextUtils.isEmpty(setTask)){
            userTaskedt.setError("Enter Valid Status");
            userTaskedt.requestFocus();
            return;
        }

        HashMap<String, Object> profileMap = new HashMap<>();
        profileMap.put("uid",getCurrUserID);
        profileMap.put("name",setUsername);
        profileMap.put("status",setStatus);
        profileMap.put("task",setTask);

        databaseReference.child("UsersChat").child(getCurrUserID).updateChildren(profileMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            sendToMainActivity();
                            Toast.makeText(settingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        }else {
                            String msg = task.getException().toString();
                            Toast.makeText(settingsActivity.this, "Error" +msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void InitializeSettingFeilds() {
        userTaskedt = findViewById(R.id.set_usertask);
        usernameEdt = findViewById(R.id.set_username);
        statusEdt = findViewById(R.id.set_status);
        imageProfile = findViewById(R.id.profile_image);
        btnUpdate = findViewById(R.id.btnUpdate);
        dialog = new ProgressDialog(this);
        toolbar = findViewById(R.id.settings_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Settings");
    }
    private void sendToMainActivity() {
        Intent intent = new Intent(settingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(settingsActivity.this,MainActivity.class));
    }
}