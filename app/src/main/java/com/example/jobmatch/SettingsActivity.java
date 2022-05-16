package com.example.jobmatch;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private EditText nameField, phoneField;
    private Button backButton,confirmButton;
    private ImageView profileImage;



    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore DB = FirebaseFirestore.getInstance();

    private DocumentReference userDB;

    private String userId, name, phone, profileImageUrl,userType;

    private Uri resultUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        listeners();
        getUserInfo();
    }

    public void init(){
        nameField =  findViewById(R.id.name);
        phoneField = findViewById(R.id.phone);
        backButton = findViewById(R.id.back);
        confirmButton = findViewById(R.id.confirm);
        profileImage = findViewById(R.id.profilePic);



        userId = mAuth.getCurrentUser().getUid();
        Log.i("banana",""+userId);


        userDB=DB.collection(GlobalVerbs.USERS_COLLECTION).document(userId);


    }

    private void getUserInfo() {
        userDB.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot doc, @Nullable FirebaseFirestoreException error) {
                     if(doc.exists()){
                         if(doc.get("name")!=null){
                             Log.i("snap","name");
                             name = doc.getString("name");
                             Log.i("snap",name);
                             nameField.setText(name);
                         }
                         if(doc.get("phone")!=null){
                             phone = doc.getString("phone");
                             phoneField.setText(phone);
                         }
                         if(doc.get("profileImageUrl")!=null){
                             profileImageUrl = doc.getString("profileImageUrl");
                             Glide.with(getApplication()).load(profileImageUrl).into(profileImage);


                         }
                     }
            }
        });
    }

    public void listeners(){
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Intent.ACTION_PICK);
               // intent.setType("image/*");
                mGetContent.launch("image/*");


            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });

    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {

                final Uri imageUri = result;
                resultUri = imageUri;
                profileImage.setImageURI(resultUri);
                if (resultUri != null) {
                    StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
                    byte[] pic =  baos.toByteArray();
                    UploadTask uploadTask = filepath.putBytes(pic);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("banana",e.toString());
                        }
                    });
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if(taskSnapshot.getMetadata() != null){
                                if(taskSnapshot.getMetadata().getReference()!=null){
                                    Task<Uri>  result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            Map<String,Object> userInfo = new HashMap<>();
                                            userInfo.put(GlobalVerbs.PROFILE_IMAGE_URL, imageUrl.toString());
                                            userDB.update(userInfo);

                                            return;
                                        }
                                    });
                                }
                            }
                            //String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();


                        }
                    });

                } else {
                    finish();
                }



            }

    });

    private void saveUserInfo() {
        name = nameField.getText().toString();
        phone = phoneField.getText().toString();

        Map<String,Object> userInfo = new HashMap<>();
        userInfo.put("name",name);
        userInfo.put("phone",phone);
        userDB.update(userInfo);
    }
    /*


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            profileImage.setImageURI(resultUri);
            if (resultUri != null) {
                StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
                byte[] pic =  baos.toByteArray();
                UploadTask uploadTask = filepath.putBytes(pic);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("banana",e.toString());
                    }
                });
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                        Map<String,Object> userInfo = new HashMap<>();
                        userInfo.put("profileImageUrl", downloadUrl.toString());
                        userDB.update(userInfo);
                        finish();
                        return;
                    }
                });

            } else {
                finish();
            }



        }
    }*/
}