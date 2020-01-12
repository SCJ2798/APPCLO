package com.project.appclo.dataentryapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class dataEntryAct extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    FirebaseFirestore firestore;

    String Uid;

    String opFirst,opLast,shop,Location,opId,opEmployeeId,Email;

    TextView txt_Opname,txt_shopname;

    TextView barcode_text;

    EditText inp_Barcode;

    ProgressDialog progressDialog;

    public EntryOperator entryOperator;

    AlertDialog dialogGetBar,dialogTypeBar,dialogcam;

    ZXingScannerView scannerView;

    Intent camIntent,galIntent;

    int reqCode;

    Bitmap front_img_bitmap , back_img_bitmap;

    ImageView imgFrontView,imgBackView;

//    --------------------- product details ------------

    TextView text_ProductName , text_productPrice;

    String Resul;

    Spinner colourSpinner;
    SpinnerAdapter adapter;

    RadioGroup rdGroup;

    ArrayList<String> arrayListColour;

   public Product product;

//------------------------------------------------------------------


//    View viewDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry);

        firestore = FirebaseFirestore.getInstance();
        Uid = getIntent().getStringExtra("UID");

        scannerView = new ZXingScannerView(this);

        product = new Product();

        entryOperator = new EntryOperator(); // create new entry-operator class


    }

    @Override
    protected void onStart() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data");
        progressDialog.show();

//        **************************************************************************************************

        txt_Opname = (TextView) findViewById(R.id.text_opName); // initialize operator name

        txt_shopname = (TextView) findViewById(R.id.text_shopName);// initialize shop name

        barcode_text = (TextView) findViewById(R.id.barcode_text_de);// initialize barcode enter field

        imgFrontView = (ImageView) findViewById(R.id.imgView_front_side);
        imgBackView  = (ImageView) findViewById(R.id.imgView_back_side);

        text_ProductName = (TextView) findViewById(R.id.text_product_name);
        text_productPrice = (TextView) findViewById(R.id.text_product_price);

        colourSpinner = (Spinner) findViewById(R.id.spinner_set_product_color);

        colourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String color = arrayListColour.get(position);
                product.setColour(color);
                Toast.makeText(dataEntryAct.this, color, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        ------------------------------------------------------------------------------------

        rdGroup = (RadioGroup) findViewById(R.id.rd_group_sel_gender);





//        ***************************************************************************************




//        Toast.makeText(this, "UID - "+Uid, Toast.LENGTH_SHORT).show();
        getUserData();

        getColorDetails();

        super.onStart();

    }


    private void getUserData(){
        //  ----------------------- get data from user ---------------------------------

        firestore.collection("Data-Entry-Operators").document(Uid).get()
                //----------------- failure listener ----------------------------
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss(); // close progress dialog....
                        Toast.makeText(dataEntryAct.this, e.getMessage(), Toast.LENGTH_SHORT).show(); // show error message...
                    }
                })
//                        ------------------------ complete listener --------------------------------
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){ // check task complete

                            DocumentSnapshot documentSnapshot = task.getResult();

                            if(documentSnapshot.exists()) { //check document exists

                                progressDialog.dismiss();

                                entryOperator.setFirstName(documentSnapshot.getString("first"));// Assign  First name
                                entryOperator.setLastName(documentSnapshot.getString("last"));// Assign  last name
                                entryOperator.setShop(documentSnapshot.getString("shop"));
                                entryOperator.setLocation(documentSnapshot.getString("location"));
                                entryOperator.setUserEmpId(documentSnapshot.getString("EmpId"));
                                entryOperator.setUserId(documentSnapshot.getString("ID"));
                                entryOperator.setEmail(documentSnapshot.getString("email"));


                                txt_Opname.setText(entryOperator.getFirstName());//get first name & set op name into text field.

                                String shop = entryOperator.getShop()+"-"+entryOperator.getLocation();
                                txt_shopname.setText(shop);

                            }


                        }
                    }
                });

    }




//    @@@@@@@@@@@@@@@@@@@@@@@@@@@@@ set - Barcode @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    public void clickBar(View view){

        int id = view.getId(); // get button ids

        switch (id){

//            $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ Barcode - Get button $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            case R.id.btn_get_barcode:

//                *************************  create 'What do you do' dialogbox  ******************************************

                AlertDialog.Builder builderGetBar = new AlertDialog.Builder(this);
                View viewDialog = getLayoutInflater().inflate(R.layout.dis_do_getbarcode,null); // set0 'what do you' layout
                builderGetBar.setView(viewDialog);
                dialogGetBar = builderGetBar.create();
                dialogGetBar.show();

//                ************************************************************************************
                break;


//            $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ Barcode - Scan button $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            case R.id.btn_scan_barcode:
                Toast.makeText(this, "Scan Barcode", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(dataEntryAct.this,barcode_scanner.class);
//                startActivity(intent);
                takeBarcode();
                dialogGetBar.dismiss();
                break;



//            $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ Barcode - Type button   $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            case R.id.btn_type_barcode:

                dialogGetBar.dismiss(); // dismiss 'what do you do' layout

//                ****************************** create 'Enter barcode' dialog box *************************

                AlertDialog.Builder builderType = new AlertDialog.Builder(this);
                View DialogEnterBar = getLayoutInflater().inflate(R.layout.layout_enter_barcode,null);

                inp_Barcode = (EditText) DialogEnterBar.findViewById(R.id.en_barcode);// initialize barcode enter field

                builderType.setView(DialogEnterBar);
                dialogTypeBar = builderType.create();
                dialogTypeBar.show();

//                ***************************************************************************************
                break;

//                $$$$$$$$$$$$$$$$$$$$$$$$$$$$ Barcode - submit button $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            case R.id.btn_submit_barcode:

                dialogTypeBar.dismiss(); // dismiss 'Enter-password' dialog box

                String barcode = inp_Barcode.getText().toString(); //get barcode

                product.setBarcode(barcode); //set barcode in to product class

                barcode_text.setText(barcode); // set barcode into text-barcode field

                Toast.makeText(this, barcode, Toast.LENGTH_SHORT).show();
        }
    }

    private void takeBarcode(){
        scannerView.setResultHandler(this);
        scannerView.startCamera();
        setContentView(scannerView);

    }

    @Override
    public void handleResult(Result result) {
            String re = result.getText();
        Toast.makeText(this, re, Toast.LENGTH_SHORT).show();
        scannerView.stopCamera();
        setContentView(R.layout.activity_data_entry);
        onStart();

        product.setBarcode(re); // set barcode in to product class

        barcode_text.setText(re);

    }

//    @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@



//  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   Set - Camera   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    public void clickImg(View view){
        int Id = view.getId(); // get Id

        switch(Id){
//            $$$$ Add Image Button - Front side ---
            case R.id.btn_set_front_img:
                displayCamDialog(); // show 'What do you do' dialogbox
                reqCode = 1001; // request code - front - side
                break;

//            $$$$ Add Image Button - Back Side ---
            case R.id.btn_set_back_img:
                displayCamDialog(); // show 'What do you do' dialog box
                reqCode = 2001;// request code - back side -
                break;

//            $$$$ Take photo - Button -- $$$$
            case R.id.btn_take_photo:
                dialogcam.dismiss(); // dismiss 'What do you do' dialog box
                takePhoto(); // take photo
                Toast.makeText(this, "take photo", Toast.LENGTH_SHORT).show();// toast...
                break;

//            $$$$ Import image from gallery --- $$$$
            case R.id.btn_import_photo:
                dialogcam.dismiss(); // dismiss 'dismiss 'What do you do' dialog box'
                openGal();
                Toast.makeText(this, "Import from Gallery", Toast.LENGTH_SHORT).show();
                break;

//            $$$$ Image view - front - side $$$$
            case R.id.imgView_front_side:
                if(front_img_bitmap != null) { // check
                    showImage(front_img_bitmap); // show image -- front image
                }else{
                    Toast.makeText(this, "take picture first...", Toast.LENGTH_SHORT).show();
                }
                break;

//             $$$$ Image view -- Back - side $$$$
            case R.id.imgView_back_side:
                if(back_img_bitmap != null){ //check
                    showImage(back_img_bitmap); //show image
                }else{
                    Toast.makeText(this, "take picture first...", Toast.LENGTH_SHORT).show();
                }

        }
    }

//    ----------------- Create 'What do you do'- Camera dialog box ---------------------------------------
    public void displayCamDialog(){
        AlertDialog.Builder camDialogBuilder = new AlertDialog.Builder(this); //builder - alert dialog
            View viewCamDialog = getLayoutInflater().inflate(R.layout.dis_do_getcamera, null);
            camDialogBuilder.setView(viewCamDialog);
            dialogcam = camDialogBuilder.create();
            dialogcam.show();
        }

//        ------------------  Show image ----------------------------
    public void showImage(Bitmap bitmap){

        AlertDialog.Builder camDialogBuilder = new AlertDialog.Builder(this);
        View viewCamDialog = getLayoutInflater().inflate(R.layout.show_image, null);
        camDialogBuilder.setView(viewCamDialog);
        AlertDialog alViewImg = camDialogBuilder.create();
        alViewImg.show();

        ImageView imageView = (ImageView) viewCamDialog.findViewById(R.id.show_img_view);
        imageView.setImageBitmap(bitmap);
    }

// -------------------- take photo ------------------------------
    private void takePhoto() {

            if (cameraHas(this)) { // check camera has
                    camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camIntent, reqCode);
            } else{
                Toast.makeText(this, "camera can't open", Toast.LENGTH_SHORT).show();
            }

    }

 //------------- camera has ---------------------------------
    private boolean cameraHas(Context context){

            if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                return true;
            }else{
                return  false;
            }

        }

 // ---------------------- Open gallery -----------------------------
    private void openGal(){
            galIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(galIntent,"Select Image from Gallery"),3101);
        }

    @Override
//    ----------------- On Activity Result --------------------------------------------
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // ---------------------- get Image from Camera for set into Front image View ----------------------

        if(requestCode == 1001 && resultCode == RESULT_OK){
            if(data != null){
                Bundle bundle = data.getExtras();
                front_img_bitmap =  bundle.getParcelable("data");
                imgFrontView.setImageBitmap(front_img_bitmap);
                product.setFrontImg(front_img_bitmap);

            }
        }

        // ---------------------- get Image from Camera for set into Back image View ----------------------

        if(requestCode == 2001 && resultCode == RESULT_OK){
            if(data != null){
                Bundle bundle = data.getExtras();
                back_img_bitmap = bundle.getParcelable("data");
                imgBackView.setImageBitmap(back_img_bitmap);
                product.setBackImg(back_img_bitmap);
            }
        }


        // ---------------------- get Image from gallery for set into Front image View ----------------------

        if(requestCode == 3101 && resultCode == RESULT_OK && reqCode == 1001){
            if(data != null){

                try {

                    Uri imgUri = data.getData();
                    InputStream inputStream = getContentResolver().openInputStream(imgUri);
                    front_img_bitmap = BitmapFactory.decodeStream(inputStream);
                    imgFrontView.setImageBitmap(front_img_bitmap);
                    product.setFrontImg(front_img_bitmap);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }

        // ---------------------- get Image from Camera for set into back image View ----------------------

        if(requestCode == 3101 && resultCode == RESULT_OK && reqCode == 2001){

            if(data != null){
                try {
                    Uri imgUri = data.getData();
                    InputStream inputStream = getContentResolver().openInputStream(imgUri);
                    back_img_bitmap = BitmapFactory.decodeStream(inputStream);
                    imgBackView.setImageBitmap(back_img_bitmap);
                    product.setBackImg(back_img_bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

//    private void setSize(ImageView imageView,Uri uri){
//
//        int targetW = imageView.getWidth();
//        int targetH = imageView.getHeight();
//
//        String path = uri.toString();
//
//        BitmapFactory.Options bmoOptions = new BitmapFactory.Options();
//        bmoOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(path,bmoOptions);
//
//        int photoW = bmoOptions.outWidth;
//        int photoH = bmoOptions.outHeight;
//
//        int scaleFactor = Math.min(photoW/targetW,photoH/targetH);
//
//        bmoOptions.inJustDecodeBounds = false;
//        bmoOptions.inSampleSize = scaleFactor;
//        bmoOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(path,bmoOptions);
//        imageView.setImageBitmap(bitmap);
//
//    }

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

//   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Set Product details @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    public void addDetail(View view){

        int id = view.getId();

        switch (id){

            case R.id.text_product_name:
                EnterValDialog("Enter Product Name","Enter product name here",text_ProductName,product,false);
                break;

            case R.id.text_product_price:
                EnterValDialog("Enter Product Price","Enter price here",text_productPrice,product,true);
                break;
        }

    }

    private void EnterValDialog(String msg, String hint , final TextView textView , final Product pro,Boolean inputtype){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View builderView = getLayoutInflater().inflate(R.layout.enter_details_dialog,null);

        TextView topicDialogBox = (TextView) builderView.findViewById(R.id.dialog_topic);
        final EditText editEnterDetails = (EditText) builderView.findViewById(R.id.edt_enter_details);

        if(inputtype){
            editEnterDetails.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        Button btnSubmit = (Button) builderView.findViewById(R.id.btn_submit_proDet);

        builder.setView(builderView);

        final AlertDialog dialog =  builder.create();
        dialog.show();

        topicDialogBox.setText(msg);
        editEnterDetails.setHint(hint);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resul = editEnterDetails.getText().toString();
                textView.setText(Resul);

               if(textView == text_ProductName){
                   pro.setName(Resul);
               }else if(textView == text_productPrice){
                   pro.setPrice(Resul);
               }

                dialog.dismiss();
            }
        });

    }

    private void getColorDetails(){

        arrayListColour = new ArrayList<>();

        firestore.collection("colour").document("colour-name").get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(dataEntryAct.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();

                            if (doc.exists()){

//                                Toast.makeText(dataEntryAct.this, doc.getReference().toString(), Toast.LENGTH_SHORT).show();

                                for(int z =1; z <= doc.getData().size(); z++){
                                    String y = Integer.toString(z);
                                    arrayListColour.add(doc.getString(y));
                                }

                                adapter = new myAdapter(arrayListColour,dataEntryAct.this);
                                colourSpinner.setAdapter(adapter);

//                                Toast.makeText(dataEntryAct.this, arrayListColour.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void checkedRB(View view){

        int rbId = rdGroup.getCheckedRadioButtonId();

       switch (rbId){
           case R.id.rd_btn_women:
               Toast.makeText(this, "Women", Toast.LENGTH_SHORT).show();
               product.setWho("women");
               gotoOtherPage();
               break;

           case R.id.rd_btn_men:
               Toast.makeText(this, "Men", Toast.LENGTH_SHORT).show();
               product.setWho("men");
               gotoOtherPage();
               break;

       }
    }

    private void gotoOtherPage(){

//        if(product.getName() == null || product.getBarcode() == null || product.getPrice() == null){
//
//            if(product.getName() == null){
//                Toast.makeText(this, "Enter product name", Toast.LENGTH_SHORT).show();
//            }else if(product.getPrice() == null){
//                Toast.makeText(this, "Enter product price", Toast.LENGTH_SHORT).show();
//            }else if(product.getBarcode() == null){
//                Toast.makeText(this, "Scan or Type barcode of product", Toast.LENGTH_SHORT).show();
//            }
//
//        }else {

            Intent intent = new Intent(dataEntryAct.this, secondPage.class);

//        ------------------------ product details -----------------------------------

            intent.putExtra("pr-name", product.getName());
            intent.putExtra("pr-price", product.getPrice());
            intent.putExtra("pr-barcode", product.getBarcode());
            intent.putExtra("pr-color", product.getColour());
            intent.putExtra("pr-who", product.getWho());

//        ----------------------- Entry Operator details ------------------------

            intent.putExtra("en-firstName", entryOperator.getFirstName());
            intent.putExtra("en-lastName", entryOperator.getLastName());
            intent.putExtra("en-shop", entryOperator.getShop());
            intent.putExtra("en-location", entryOperator.getLocation());
            intent.putExtra("email",entryOperator.getEmail());
            intent.putExtra("empID",entryOperator.getUserEmpId());
            intent.putExtra("ID",entryOperator.getUserId());

//        ------------------------------------------------------------------

            intent.putExtra("frontImage",product.getFrontImg());
            intent.putExtra("backImage",product.getBackImg());

            startActivity(intent);

//        }
    }



}

