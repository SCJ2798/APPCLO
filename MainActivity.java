package com.project.appclo.dataentryapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText loginId_input;
    EditText psw_input;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;
    String UID;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginId_input = (EditText) findViewById(R.id.input_loginID);
        psw_input = (EditText) findViewById(R.id.input_psw);

        mAuth = FirebaseAuth.getInstance(); // Authentication - Firebase

        firestore = FirebaseFirestore.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser user = mAuth.getCurrentUser(); // get user

//        ----------------- check user already registered -----------------------------
        if(user == null){
            Toast.makeText(this, "User is not registered", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "User already registered", Toast.LENGTH_SHORT).show();

            final String email = user.getEmail(); // get user email
            mAuth.signOut();
            loginId_input.setText(email);


//
////  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  Build dialog box for Enter password %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
////            --------------------------- get dialog layout -----------------------------------------------------------
//            View dialogView = getLayoutInflater().inflate(R.layout.dialog_password_enter,null);
//
//
////            ----------------------------------- bulid dialog box ----------------------------------------------------
//            builder.setView(dialogView);
//            alertDialog = builder.create();
//            alertDialog.show();
//
//            final EditText dialog_psw = (EditText) dialogView.findViewById(R.id.in_dialog_psw);     // get password from Enter button in Dialog box
//
//            final Button btn_dialog_enter = (Button) dialogView.findViewById(R.id.btn_dialog_enter);  // initialize Enter Buton in Dialogbox
//
//
//            btn_dialog_enter.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    mAuth.signOut(); // sign out user from system
//
////                   ------------------------ Progress bar ------------------------------------------------
//                    final ProgressDialog proDialog = new ProgressDialog(MainActivity.this);
//                    proDialog.setMessage("Loading....");
//                    proDialog.show();
////                  ----------------------------------------------------------------------------------------
//
//                    String passw = dialog_psw.getText().toString(); // get password
//
//                    Toast.makeText(MainActivity.this, "Email - "+email+" psw - "+passw, Toast.LENGTH_SHORT).show(); // Toast
//
//
////                  ####################################### SIGN IN ########################################################
//                   mAuth.signInWithEmailAndPassword(email,passw)
//                           .addOnFailureListener(MainActivity.this, new OnFailureListener() { // check failure in the System
//                       @Override
//                       public void onFailure(@NonNull Exception e) {
//                           Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();// Show error
//
//                       }
//                   }).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() { // Complete Listener
//                       @Override
//                       public void onComplete(@NonNull Task<AuthResult> task) {
//                           if(task.isComplete()){
//
//                               if(mAuth.getCurrentUser() != null){ // check current user....
//
//                                   proDialog.dismiss(); // dismiss progress Dialog
//
//                                   Toast.makeText(MainActivity.this, "Login Success -- UID", Toast.LENGTH_SHORT).show(); // Toast
//
////                                   ----------------  Start loginAct activity --------------------------------------------------
//                                   Intent intent = new Intent(MainActivity.this, loginAct.class);
//                                   startActivity(intent);
////                                   -------------------------------------------------------------------------------------------
//                               }
//                           }
//
//                       }
//                   });
//
//                }
//            });
        }
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    public void clickOn(View view){

        int id = view.getId(); // get id

        switch(id){

//            ----------------------  On click submit btn ------------------------
            case R.id.btn_submit:

                final ProgressDialog proDialog = new ProgressDialog(MainActivity.this);
                proDialog.setMessage("Loding Data.......");
                proDialog.show();

//            ----------------------- get login id and password ----------------------------------
                String email = loginId_input.getText().toString();
                String psw = psw_input.getText().toString();
//            ------------------------------------------------------------------------------------
//

//                ################################### SIGN IN ##########################################

                mAuth.signInWithEmailAndPassword(email,psw).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        proDialog.dismiss();

                        Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();// show error via Toast
                    }
                }).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isComplete()){ // if task completed

                            if(mAuth.getCurrentUser() != null) { // check current user

                                proDialog.dismiss();

//                                Toast.makeText(MainActivity.this, "Task is completed", Toast.LENGTH_SHORT).show();

                                String Uid = mAuth.getCurrentUser().getUid();// get UID

//                                Toast.makeText(MainActivity.this, Uid, Toast.LENGTH_SHORT).show();

//                                --------------------------- Start Activity ---------------------------------------------------------------------------
                                Intent intent = new Intent(MainActivity.this, dataEntryAct.class);
                                intent.putExtra("UID",Uid);
                                startActivity(intent);
//                                --------------------------------------------------------------------------------------------
                            }

                        }else{

                            proDialog.dismiss();

                            Toast.makeText(MainActivity.this, task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                break;
//      >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>^^^^^^^^^^^^^^^^^^^^^^^><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<





        }
    }



}
