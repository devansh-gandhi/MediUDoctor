package com.example.devanshgandhi.mediu_doctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DoctorRegister extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email, firstname, lastname, password;
    private Button register;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        register= (Button)findViewById(R.id.register);
        email= (EditText)findViewById(R.id.etEmail);
        firstname= (EditText)findViewById(R.id.etFirstName);
        lastname= (EditText)findViewById(R.id.etLastName);
        password= (EditText)findViewById(R.id.etPassword);

        if(mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), Home.class));
        }


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getfirstname = firstname.getText().toString().trim();
                String getlastname = lastname.getText().toString().trim();
                String getemail = email.getText().toString().trim();
                String getpassword = password.getText().toString().trim();

                if(!TextUtils.isEmpty(getfirstname) || !TextUtils.isEmpty(getlastname) || !TextUtils.isEmpty(getemail) || !TextUtils.isEmpty(getpassword))
                {
                    mProgressDialog.setTitle("Registering User");
                    mProgressDialog.setMessage("Please wait while we check your credentials");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    callregister(getfirstname, getlastname, getemail,getpassword);
                }
                else {
                    Toast.makeText(DoctorRegister.this, "Please fill all the details.",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });
    }



    private void callregister(final String firstname, final String lastname, final String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            mProgressDialog.dismiss();
                            FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
                            String uid= current_user.getUid();

//
//
//                            FirebaseOptions options = new FirebaseOptions.Builder()
//                                    .setApplicationId("1:530266078999:android:481c4ecf3253701e") // Required for Analytics.
//                                    .setApiKey("AIzaSyBRxOyIj5dJkKgAVPXRLYFkdZwh2Xxq51k") // Required for Auth.
//                                    .setDatabaseUrl("https://project-1765055333176374514.firebaseio.com/") // Required for RTDB.
//                                    .build();
//                            FirebaseApp.initializeApp(this /* Context */, options, "secondary");
//
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Doctor_Users").child(uid);
                            HashMap<String, String> userMap= new HashMap<>();
                            userMap.put("firstname",firstname);
                            userMap.put("lastname", lastname);
                            userMap.put("age", "");
                            userMap.put("image", "default");
                            userMap.put("thumbnail", "default");
                            userMap.put("dob", "");
                            userMap.put("address", "");
                            userMap.put("gender", "");
                            userMap.put("mobile", "");
                            userMap.put("email", email);

                            mDatabase.setValue(userMap);



                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(DoctorRegister.this, "Authentication Success. Please Login",
                                    Toast.LENGTH_SHORT).show();
                            userProfile();
                            startActivity(new Intent(getApplicationContext(), Profile.class));

                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(DoctorRegister.this, "Authentication failed. Please try again later",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void userProfile(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!= null)
        {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(firstname.getText().toString().trim())
//.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg")) // here you can set image link also.
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TESTING", "User profile updated.");
                            }
                        }
                    });
        }
    }

    public void opensigninpage(View view){
        Intent intent = new Intent(this, DoctorLogin.class);
        startActivity(intent);

    }


}
