package com.example.jobmatch;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private EditText nameField, phoneField,xpField;
    private Button backButton,confirmButton;
    private ImageView profileImage;



    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore DB = FirebaseFirestore.getInstance();

    private DocumentReference userDB;

    private String userId, name, phone, profileImageUrl,userType,xp;

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
        xpField = findViewById(R.id.xp);
        confirmButton = findViewById(R.id.confirm);
        profileImage = findViewById(R.id.profilePic);
        userType = getIntent().getStringExtra(GlobalVerbs.USER_TYPE);
        switch (userType){
            case GlobalVerbs.EMPLOYEE:
                xpField.setHint("What is your experience ?");
                break;
            case GlobalVerbs.EMPLOYER:
                xpField.setHint("is any experience needed for the job you offer? describe");
                break;

        }
        userId = mAuth.getCurrentUser().getUid();
        userDB=DB.collection(GlobalVerbs.USERS_COLLECTION).document(userId);
    }

    private void getUserInfo() {
        userDB.addSnapshotListener((doc, error) -> {
            Map<String,Object> userInfo = (Map<String, Object>) doc.getData().get(GlobalVerbs.USERS_USER);
            Users tempUser = new Users(userInfo);
                Log.i("snap","name");
                name = tempUser.getUserName();
                Log.i("snap",name);
                nameField.setText(name);
                phone = tempUser.getPhone();
                phoneField.setText(phone);
                xp = tempUser.getExperience();
                xpField.setText(xp);
                profileImageUrl = tempUser.getProfileImageUrl();
                Glide.with(getApplication()).load(profileImageUrl).into(profileImage);



        });
    }

    public void listeners(){
        confirmButton.setOnClickListener(v -> {
            if(nameField!=null&&phoneField!=null)
                saveUserInfo();
        });

        profileImage.setOnClickListener(v -> {
            //Intent intent = new Intent(Intent.ACTION_PICK);
           // intent.setType("image/*");
          //  mGetContent.launch("image/*")
            //ImagePicker
           // ImagePicker.Companion.with(SettingsActivity.this).start()
            chooseProfilePicture();
        });

        backButton.setOnClickListener(v -> finish());

    }

    private void chooseProfilePicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.chose_profile_pic_dialog,null);
        builder.setCancelable(false);
        builder.setView(dialogView);

        AlertDialog alertDialogProfilePic =builder.create();
        alertDialogProfilePic.show();

        ImageView Camera = dialogView.findViewById(R.id.choseCamera);
        ImageView Gallery = dialogView.findViewById(R.id.choseGallery);

        Camera.setOnClickListener(v -> {
            //open camera
            openCamera();
            alertDialogProfilePic.cancel();

        });

        Gallery.setOnClickListener(v -> {
            startGallery.launch("image/*");
            alertDialogProfilePic.cancel();
        });



    }
    Uri cam_uri;
    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        cam_uri = SettingsActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri);

        //startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE); // OLD WAY
        startCamera.launch(cameraIntent);                // VERY NEW WAY


    }



    ActivityResultLauncher<Intent> startCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        // There are no request codes
                        resultUri = cam_uri;
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
                            uploadTask.addOnFailureListener(e -> Log.i("banana",e.toString()));
                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                                if(taskSnapshot.getMetadata() != null){
                                    if(taskSnapshot.getMetadata().getReference()!=null){
                                        Task<Uri> result1 = taskSnapshot.getStorage().getDownloadUrl();
                                        result1.addOnSuccessListener(uri -> profileImageUrl= uri.toString());
                                    }
                                }
                            });

                        } else {
                            finish();
                        }

                    }
                }
            });

    ActivityResultLauncher<String> startGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {

            resultUri = result;
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
                    uploadTask.addOnFailureListener(e -> Log.i("banana",e.toString()));
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        if(taskSnapshot.getMetadata() != null){
                            if(taskSnapshot.getMetadata().getReference()!=null){
                                Task<Uri> result1 = taskSnapshot.getStorage().getDownloadUrl();
                                result1.addOnSuccessListener(uri -> profileImageUrl= uri.toString());
                            }
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
        xp = xpField.getText().toString();
        Users user = new Users(name,phone,15,profileImageUrl,userType,xp);
        Map<String,Object> userInfo = new HashMap<>();
        userInfo.put("user",user);
        userDB.set(userInfo);
        finish();
    }
}