package com.example.devanshgandhi.mediu_doctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

// table name == Doctor_Users

//  StorageReference filepath = mImageStorage.child("doctor_profileimage").child(current_user_id+".jpg");
//final StorageReference thumbnail_filepath = mImageStorage.child("doctor_profileimage").child("thumbs").child(current_user_id+".jpg");


public class Profile extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;
    private Button mSaveButton;
    private CircleImageView image_profile;
    private ProgressDialog mProgressDialog;
    private EditText mFirstname, mLastname, mMobile, mAge, mDob, mEmail, mAddress;
    private RadioGroup rg_gender;
    private RadioButton radioButton, radiobutton_male, radiobutton_female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Please wait while we load your profile");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mFirstname = (EditText)findViewById(R.id.etFirstname);
        mLastname = (EditText)findViewById(R.id.etLastname);
        mAge = (EditText)findViewById(R.id.etAge);
        mMobile = (EditText)findViewById(R.id.etMobile);
        mEmail = (EditText)findViewById(R.id.etEmail);
        mDob = (EditText)findViewById(R.id.etDob);
        mAddress = (EditText)findViewById(R.id.etAddress);
        mSaveButton = (Button)findViewById(R.id.saveButton);
        image_profile = (CircleImageView) findViewById(R.id.image_profile);
        rg_gender = (RadioGroup)findViewById(R.id.radiogroup_gender);
        radiobutton_male = (RadioButton)findViewById(R.id.radiobutton_male);
        radiobutton_female = (RadioButton)findViewById(R.id.radiobutton_female);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mImageStorage = FirebaseStorage.getInstance().getReference();

        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Doctor_Users").child(current_uid);


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstname= dataSnapshot.child("firstname").getValue().toString();
                String lastname= dataSnapshot.child("lastname").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String mobile = dataSnapshot.child("mobile").getValue().toString();
                String dob = dataSnapshot.child("dob").getValue().toString();
                String address = dataSnapshot.child("address").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String age = dataSnapshot.child("age").getValue().toString();
                String gender = dataSnapshot.child("gender").getValue().toString();
                String thumbnail = dataSnapshot.child("thumbnail").getValue().toString();

                mFirstname.setText(firstname);
                mLastname.setText(lastname);
                mEmail.setText(email);
                mMobile.setText(mobile);
                mDob.setText(dob);
                mAddress.setText(address);
                mAge.setText(age);
                if(gender.equals("Male")){
                    radiobutton_male.setChecked(true);
                }else if(gender.equals("Female")){
                    radiobutton_female.setChecked(true);
                }

                if(!image.equals("default")) {
                    Picasso.with(Profile.this).load(thumbnail).placeholder(R.drawable.default_avatar).into(image_profile);
                }

                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.setTitle("Saving Data");
                mProgressDialog.setMessage("Please wait while we save the changes");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                String firstname=  mFirstname.getEditableText().toString();
                String lastname = mLastname.getEditableText().toString();
                String email = mEmail.getEditableText().toString();
                String mobile = mMobile.getEditableText().toString();
                String dob = mDob.getEditableText().toString();
                String address = mAddress.getEditableText().toString();
                String age = mAge.getEditableText().toString();
                radioButton = findViewById(rg_gender.getCheckedRadioButtonId());
                String gender= radioButton.getText().toString();

                mUserDatabase.child("firstname").setValue(firstname);
                mUserDatabase.child("lastname").setValue(lastname);
                mUserDatabase.child("email").setValue(email);
                mUserDatabase.child("mobile").setValue(mobile);
                mUserDatabase.child("dob").setValue(dob);
                mUserDatabase.child("address").setValue(address);
                mUserDatabase.child("gender").setValue(gender);
                mUserDatabase.child("age").setValue(age).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgressDialog.dismiss();
                        }
                        else {
                            mProgressDialog.dismiss();
                            Toast.makeText(Profile.this, "Error in saving data.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });


        image_profile.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {

//                Intent intent = new Intent();
//                intent.setAction(android.content.Intent.ACTION_VIEW);
//                intent.setType("image/*");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);

                PopupMenu popupMenu= new PopupMenu(Profile.this, image_profile);
                popupMenu.getMenuInflater().inflate(R.menu.popup_profile_image, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.viewimage:
                                //Code to run when the Create Order item is clicked
                                startActivity(new Intent(getApplicationContext(), ProfilePicture.class));
                                return true;
                            case R.id.changeimage:
                                // Code to run when the settings item is clicked
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setAspectRatio(1,1)
                                        .start(Profile.this);
                                return true;
                        }
                        return true;
                    }
                });
                popupMenu.show();
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .setAspectRatio(1,1)
//                        .start(PatientProfile.this);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setTitle("Uploading");
                mProgressDialog.setMessage("Please wait while we upload your image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                Uri resultUri = result.getUri();

                File thumb_filepath = new File(resultUri.getPath());

                String current_user_id = mCurrentUser.getUid();

                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(150)
                        .setMaxHeight(150)
                        .setQuality(50)
                        .compressToBitmap(thumb_filepath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_bytes = baos.toByteArray();

                StorageReference filepath = mImageStorage.child("doctor_profileimage").child(current_user_id+".jpg");
                final StorageReference thumbnail_filepath = mImageStorage.child("doctor_profileimage").child("thumbs").child(current_user_id+".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            final String image_download_url= task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumbnail_filepath.putBytes(thumb_bytes);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_download_url = thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()){

                                        Map update_hashmap = new HashMap<>();
                                        update_hashmap.put("image", image_download_url);
                                        update_hashmap.put("thumbnail", thumb_download_url);

                                        mUserDatabase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(Profile.this, "Upload successful.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else
                                    {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(Profile.this, "Error in uploading the thumbnail.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });




                        }else {
                            mProgressDialog.dismiss();
                            Toast.makeText(Profile.this, "Error in uploading the image.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
