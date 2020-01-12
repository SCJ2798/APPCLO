package com.project.appclo.dataentryapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.grpc.internal.zzeo;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class secondPage extends AppCompatActivity {

    // radio-btn-set Catagory - ID - 2001

    Product product;
    EntryOperator operator;

    FirebaseFirestore firestore;
    StorageReference stoRef;

    RadioGroup rdGroup_SizeType;

    Spinner sizeCatSp , sizeSp;
    Spinner brandSp;

    TextView textPrQty;

    String Resul;

    ArrayList<String> arraySize,arraySizeCat,arrayBrand,arrayCat,arrayPrCat,arrayFeatureType;

    ArrayList<Integer> arrayPrCatRbId,arrayFeatureSpId;

    ArrayList<ArrayList> arrayFeaArray;



    LinearLayout linearLay;


    Boolean checkedCat = false;
    Boolean checkedSizeType = false;
    Boolean checkedQty = false;

    AlertDialog dialogPreview;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_page);

        product = new Product();
        operator = new EntryOperator();

         firestore = FirebaseFirestore.getInstance();
         stoRef = FirebaseStorage.getInstance().getReference();


//       ---------------- product details ----------------------------------

        product.setName(getIntent().getStringExtra("pr-name"));
        product.setPrice(getIntent().getStringExtra("pr-price"));
        product.setBarcode(getIntent().getStringExtra("pr-barcode"));
        product.setColour(getIntent().getStringExtra("pr-color"));
        product.setWho(getIntent().getStringExtra("pr-who"));

        product.setFrontImg((Bitmap) getIntent().getParcelableExtra("frontImage"));
        product.setBackImg((Bitmap) getIntent().getParcelableExtra("backImage"));

//        product.setWho("women");

//        ----------------------------------------------------------------------


//        ---------------- operator details ----------------------------------------

        operator.setFirstName(getIntent().getStringExtra("en-firstName"));
        operator.setLastName(getIntent().getStringExtra("en-lastName"));
        operator.setShop(getIntent().getStringExtra("en-shop"));
        operator.setLocation(getIntent().getStringExtra("en-location"));
        operator.setEmail(getIntent().getStringExtra("email"));
        operator.setUserEmpId(getIntent().getStringExtra("empID"));
        operator.setUserId(getIntent().getStringExtra("ID"));

//        Toast.makeText(this, product.getName()+" - "+product.getPrice()+" - "+product.getBarcode()+" - "+product.getColour()+" - "+product.getWho(),Toast.LENGTH_SHORT).show();

//        ---------------------------------------------------------------------------------------


//      Toast.makeText(this, product.getName(), Toast.LENGTH_SHORT).show();

        textPrQty = (TextView) findViewById(R.id.text_qty);

        arrayPrCat = new ArrayList<>();
        arrayPrCatRbId = new ArrayList<>();

        arraySize = new ArrayList<>();
        arraySizeCat = new ArrayList<>();
        arrayBrand = new ArrayList<>();
        arrayCat = new ArrayList<>();

        arrayFeatureSpId = new ArrayList<>();

        //--------------------------------------------------------------------------------------------------

        TextView textBarcode  = (TextView) findViewById(R.id.text_product_barcode);
        textBarcode.setText(product.getBarcode());

        TextView textProductName = (TextView) findViewById(R.id.text_product_name);
        textProductName.setText(product.getName());

        TextView textPrice = (TextView) findViewById(R.id.text_product_price);
        textPrice.setText(product.getPrice());

       test();



    }

    @Override
    protected void onStart() {
        super.onStart();




//        --------------------------------------------------------------------------------------------------------
    }

    public void checkSize(View view){
        int id = rdGroup_SizeType.getCheckedRadioButtonId();

        switch (id){

            case R.id.rb_st_size: // standard size radio button

                checkedSizeType = true;

                String typeS = "Standard Size";
                product.setSizeType(typeS);
                Toast.makeText(this, typeS, Toast.LENGTH_SHORT).show();
                break;

            case R.id.rb_plus_size: // plus size radio button

                checkedSizeType = true;

                String typeP = "Plus Size";
                product.setSizeType(typeP);
                Toast.makeText(this, typeP, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void sizeSpinner(){

        rdGroup_SizeType = (RadioGroup) findViewById(R.id.rg_size_type); // initialize size type radio group....

        sizeCatSp = (Spinner) findViewById(R.id.sp_size_cat);
        sizeSp = (Spinner) findViewById(R.id.sp_sizes);


        SpinnerAdapter SizeCatAdpter = new myAdapter(arraySizeCat,secondPage.this);
        sizeCatSp.setAdapter(SizeCatAdpter);



        sizeCatSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String ad = arraySizeCat.get(position);
                product.setSizeCatagory(ad);
                Toast.makeText(secondPage.this, ad, Toast.LENGTH_SHORT).show();

                final ProgressDialog progressDialog = new ProgressDialog(secondPage.this);
                progressDialog.setMessage("Loading data .....");
                progressDialog.show();

               if(ad != "Select One") {

                   firestore.collection("size").document(ad).get()
                           .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                   if (task.isSuccessful()) {
                                       DocumentSnapshot doc = task.getResult();

                                       arraySize.clear();
                                       arraySize.add("Select One");
                                       for (int a = 0; a < doc.getData().size(); a++) {
                                           String t = Integer.toString(a);
                                           String size = doc.getString(t);
                                           arraySize.add(size);
                                       }

                                       progressDialog.dismiss();
                                   }

                                   SpinnerAdapter sizeAdapter = new myAdapter(arraySize,secondPage.this);
                                   sizeSp.setAdapter(sizeAdapter);
                               }
                           });
               }else{
                   progressDialog.dismiss();
               }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sizeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ad = arraySize.get(position);

                HashMap<String,String> hasSize = new HashMap<>();

                hasSize.put(product.getSizeCatagory(),ad);

                product.setHashSize(hasSize);

                Toast.makeText(secondPage.this, ad, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }



        });



    }

    private void brand(){

        brandSp = (Spinner) findViewById(R.id.sp_brand);

        SpinnerAdapter brandSpAdapter = new myAdapter(arrayBrand,secondPage.this);
        brandSp.setAdapter(brandSpAdapter);

        brandSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ad = arrayBrand.get(position);
                product.setBrand(ad);//set product brand;
                Toast.makeText(secondPage.this, ad, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void AddNewBrand(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(secondPage.this);

        View viewDialog = getLayoutInflater().inflate(R.layout.add_new_brand_lay,null);

        final EditText BrandName = (EditText) viewDialog.findViewById(R.id.enter_new_brand);

        Button btnAddBrand = (Button)viewDialog.findViewById(R.id.submit_new_brand);

        builder.setView(viewDialog);

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnAddBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = new ProgressDialog(secondPage.this);
                progressDialog.setTitle("Add New Brand");
                progressDialog.setMessage("Adding new brand");
                progressDialog.show();

                AddNewBrandFB(BrandName.getText().toString(),progressDialog);
                dialog.dismiss();
            }
        });
    }

    private void AddNewBrandFB(final String newBr , final ProgressDialog prog){

        if(!newBr.isEmpty()){

            final DocumentReference docR = firestore.collection("brand").document(product.getWho());

            docR.get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(secondPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    prog.dismiss();
                }
            }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            DocumentSnapshot docSn = task.getResult();

                            if(docSn.exists()){

                                Boolean has = false;

                                for (int a = 0; a < docSn.getData().size(); a++){
                                    String t = Integer.toString(a);

                                    String lowExBrand = docSn.getString(t).toLowerCase();
                                    String lowNewBrand = newBr.toLowerCase();

                                    if(lowExBrand.matches(lowNewBrand)){
                                        Toast.makeText(secondPage.this, "Error occurring", Toast.LENGTH_SHORT).show();
                                        has = true;
                                        break;
                                    }

                                }

                                if(has){
                                    Toast.makeText(secondPage.this, "this brand already exists in System", Toast.LENGTH_SHORT).show();
                                    prog.dismiss();

                                }else {

                                    String t = Integer.toString(docSn.getData().size());

                                    HashMap<String, Object> hashBrand = new HashMap<>();
                                    hashBrand.put(t, newBr);

                                    docR.update(hashBrand).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(secondPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            prog.dismiss();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(secondPage.this, "This brand added into System Successfully", Toast.LENGTH_SHORT).show();
                                            prog.dismiss();
                                        }
                                    });

                                }


                            }else{
                                Toast.makeText(secondPage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                prog.dismiss();
                            }

                        }
                }
            });

        }else{
            Toast.makeText(this, "Please enter brand name ", Toast.LENGTH_SHORT).show();
            prog.dismiss();
        }

    }

    public void updateProInfo(View view){

        if(checkedCat) {

                     AlertDialog.Builder builder = new AlertDialog.Builder(secondPage.this);

            View viewDialog = getLayoutInflater().inflate(R.layout.update_product_info_lay, null);

            Spinner spSelCat = (Spinner) viewDialog.findViewById(R.id.sp_select_cat);

            SpinnerAdapter spAdapter = new myAdapter(arrayFeatureType, secondPage.this);
            spSelCat.setAdapter(spAdapter);

            final EditText GetDataCat = (EditText) viewDialog.findViewById(R.id.add_data_cat);

            Button AddBtn = (Button) viewDialog.findViewById(R.id.btn_update_pro_Info);

            builder.setView(viewDialog);

            final AlertDialog dialog = builder.create();
            dialog.show();

            final String[] relCat = new String[1];

            spSelCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                     relCat[0] = arrayFeatureType.get(parent.getSelectedItemPosition());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            AddBtn.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {

                    ProgressDialog progressDialog = new ProgressDialog(secondPage.this);
                    progressDialog.setTitle("Update");
                    progressDialog.setMessage("Updating Data");
                    progressDialog.show();

                    String relaCat = relCat[0];
                    String relCatData = GetDataCat.getText().toString();

                    addDataC(relaCat,relCatData,progressDialog);

                    Toast.makeText(secondPage.this, "Category - "+relaCat+"   Data"+relCatData, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            }); // Add button listener.......

        }else{
            Toast.makeText(this, "Please click on relevant set category radio button", Toast.LENGTH_SHORT).show();
        }

    }

    private void addDataC(String catName, final String data, final ProgressDialog pro){

        if(!catName.isEmpty() && !data.isEmpty()){

            final DocumentReference docRef = firestore.collection("feature").document(product.getWho()).collection(product.getCatagory()).document(catName);

            docRef.get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pro.dismiss();
                    Toast.makeText(secondPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if(task.isSuccessful()){

                                    DocumentSnapshot docSnp = task.getResult();

                                    if(docSnp.exists()){

                                        Boolean hasData = false;

                                        for(int a = 0; a < docSnp.getData().size(); a++){

                                            String t = Integer.toString(a);

                                            String lowExData = docSnp.getString(t).toString().toLowerCase();
                                            String lowNewData = data.toLowerCase();

                                            //Toast.makeText(secondPage.this, lowExData+" / "+lowNewData, Toast.LENGTH_SHORT).show();

                                            if(lowExData.matches(lowNewData)){
                                                hasData = true;
                                                Toast.makeText(secondPage.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                                                break;
                                            }

                                        }


                                        if(hasData){
                                            pro.dismiss();
                                            Toast.makeText(secondPage.this, "This point already added into System.", Toast.LENGTH_SHORT).show();

                                        }else {

//                                            Toast.makeText(secondPage.this, "Done", Toast.LENGTH_SHORT).show();

                                            HashMap<String, Object> hash = new HashMap<>();
                                            String size = Integer.toString(docSnp.getData().size());
                                            hash.put(size, data);

                                            docRef.update(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    pro.dismiss();
                                                    Toast.makeText(secondPage.this, "Adding is Successfully", Toast.LENGTH_SHORT).show();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    pro.dismiss();
                                                    Toast.makeText(secondPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }


                                    }else{
                                        pro.dismiss();
                                        Toast.makeText(secondPage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }


                                }else{
                                    pro.dismiss();
                                    Toast.makeText(secondPage.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }

                        }
                    }); //----- M

        }else{
            pro.dismiss();
            Toast.makeText(this, "Please enter name of data", Toast.LENGTH_SHORT).show();
        }

    }

    public void AddNewCat(View view){

        AlertDialog.Builder builderC = new AlertDialog.Builder(secondPage.this);

        View vC = getLayoutInflater().inflate(R.layout.add_new_category_lay,null);

        final EditText GetNewCategory = (EditText) vC.findViewById(R.id.enter_new_category);
        final EditText GetData = (EditText) vC.findViewById(R.id.enter_new_category_data);
        Button btnSubmitnewCat = (Button) vC.findViewById(R.id.submit_new_category);

        builderC.setView(vC);
        final AlertDialog dialogC = builderC.create();
        dialogC.show();

        btnSubmitnewCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog prod = new ProgressDialog(secondPage.this);
                prod.setTitle("Add new Category");
                prod.setMessage("Adding details");
                prod.show();

                String newCategory = GetNewCategory.getText().toString();
                String newGetData = GetData.getText().toString();

                if( !newCategory.isEmpty() && !newGetData.isEmpty()) {

                    addNewCatFB(newCategory,newGetData,prod);
                    onRestart();

                    dialogC.dismiss();

                }else{

                    prod.dismiss();
                    Toast.makeText(secondPage.this, "please enter Category name ", Toast.LENGTH_SHORT).show();
                    //dialogC.dismiss();
                }
            }
        });




    } // add new category dialog box

    private void addNewCatFB(final String newcat, final String newData, final ProgressDialog pro){

      final CollectionReference Collref = firestore.collection("feature").document(product.getWho()).collection(product.getCatagory());
            Collref.get()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           pro.dismiss();
                            Toast.makeText(secondPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    QuerySnapshot qyr = task.getResult();

                    Boolean had = false;

                    String lowCat = newcat.toLowerCase();

                    for(int t = 0; t < qyr.getDocuments().size(); t++){

                      String lowDocName = qyr.getDocuments().get(t).getId().toString().toLowerCase();

                        if(lowDocName.matches(lowCat)){
                          had = true;
                            Toast.makeText(secondPage.this, "Error occurring", Toast.LENGTH_SHORT).show();
                          break;
                        }

//                        Toast.makeText(secondPage.this, lowDocName+" / "+lowCat +"  - "+ "Had - "+had.toString(), Toast.LENGTH_SHORT).show();

                    }

                    if(!had){

                        Toast.makeText(secondPage.this, "Document not exists.....", Toast.LENGTH_SHORT).show();

                        HashMap<String,Object> hash = new HashMap<>();
                        hash.put("0","None");
                        hash.put("1",newData);

                        Collref.document(lowCat).set(hash).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pro.dismiss();
                                Toast.makeText(secondPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pro.dismiss();
                                Toast.makeText(secondPage.this, "Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });



                    }else{

                        pro.dismiss();
                        Toast.makeText(secondPage.this, "Document exists...", Toast.LENGTH_SHORT).show();
                    }

                }
            });

    }


    public void enterQty(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View builderView = getLayoutInflater().inflate(R.layout.enter_details_dialog,null);

        TextView topicDialogBox = (TextView) builderView.findViewById(R.id.dialog_topic);
        final EditText editEnterDetails = (EditText) builderView.findViewById(R.id.edt_enter_details);
        editEnterDetails.setInputType(InputType.TYPE_CLASS_NUMBER);
        Button btnSubmit = (Button) builderView.findViewById(R.id.btn_submit_proDet);

        builder.setView(builderView);

        final AlertDialog dialog =  builder.create();
        dialog.show();

        topicDialogBox.setText("Enter Product Qty");
        editEnterDetails.setHint("Enter product qty here");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Resul = editEnterDetails.getText().toString();

                    Toast.makeText(secondPage.this, Resul, Toast.LENGTH_SHORT).show();
                    textPrQty.setText(Resul);
                    product.setQty(Resul);

                    checkedQty = true;

                dialog.dismiss();
            }
        });
    }

    private void test(){

        final ProgressDialog proDialog = new ProgressDialog(secondPage.this);
        proDialog.setMessage("Loading data......");
        proDialog.show();

//        --------------------------------  get data from firestore for size ---------------------------------------

        final CollectionReference colRef = firestore.collection("size");

               colRef.get() .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                   QuerySnapshot query = task.getResult();

                   for(int a = 0; a < query.getDocuments().size();a++){
                       String docName = query.getDocuments().get(a).getId();
                       arraySizeCat.add(docName);

                   }

                    //---------- size spinner ---------------------------
                }

                sizeSpinner();
            }
        });


//      -------------------------------- get data from firestore for set catagory ------------------------------------

       DocumentReference docRef =  firestore.collection("feature-type").document(product.getWho());

       docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()){

                                for (int a = 0; a < doc.getData().size(); a++){

                                    String t = Integer.toString(a);
                                    arrayPrCat.add(doc.getString(t).toString());
                                }

                               setCatagory(); //------------ set catagory ---------------------

                            }
                        }
                    }
                });

//       ------------------------ Brand ------------------------------------------------

       firestore.collection("brand").document(product.getWho()).get().
               addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()){
                                for(int a = 0; a < doc.getData().size(); a++){
                                    String t = Integer.toString(a);
                                    arrayBrand.add(doc.getString(t));
                                }

                                brand();
                                proDialog.dismiss();
                            }
                        }else{
                            Toast.makeText(secondPage.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }


                   }
               });



    }

    private void setCatagory(){

        RadioGroup rdGroup = (RadioGroup) findViewById(R.id.rd_group_set_catagory); // initialize set-catagory - set Catagory


//---------------------------- For - Loop of create  Radio Button --------------------------------------

        for(int a = 0; a < arrayPrCat.size(); a++ ){


//            Toast.makeText(this, "Array Size - "+arrayPrCat.size(), Toast.LENGTH_SHORT).show();

            int id = a + 2001; // create id - st - 2001

            RadioButton rdBtn = new RadioButton(secondPage.this); // create radio button;
            rdBtn.setText(arrayPrCat.get(a)); // set text of Radio button
            rdBtn.setTextSize(20);// set text size
            rdBtn.setLeft(20);
            rdBtn.setPadding(10,20,10,20);

            rdBtn.setId(id); // set ID of radio button
            arrayPrCatRbId.add(id); // add id in to arrayPrCatId;

            rdGroup.addView(rdBtn); // add radio button in to Radio Group;

        }
//        ----------------------------------------------------------------------------------------

//         ----------------------- For Loop of create Radio button to Listener -----------------------------------

        for(int a = 0; a < arrayPrCatRbId.size(); a++){

            final RadioButton rbClick = findViewById(arrayPrCatRbId.get(a));// radio Button Initialize

//            ------------- click listener radio button ----------------
            rbClick.setOnClickListener(new View.OnClickListener() {



                   @Override
                   public void onClick(View v) {

                       final ProgressDialog progressDialog = new ProgressDialog(secondPage.this);
                       progressDialog.setMessage("Loading data......");
                       progressDialog.show();

                       checkedCat = true;

                         if(arrayFeatureType != null){
                             arrayFeatureType.clear(); // clear arrayFeaturetype
                         }

//                       Toast.makeText(secondPage.this, rbClick.getText(), Toast.LENGTH_SHORT).show();

                       product.setCatagory(rbClick.getText().toString());

                      final CollectionReference collRef = firestore.collection("feature").document(product.getWho()).collection(rbClick.getText().toString());

//                         ----------------------------------- get data for product info --------------------------------------
                         collRef.get()
                               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                   @Override
                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                       if(task.isSuccessful()){

                                           QuerySnapshot query = task.getResult();

                                           if(arrayFeatureType != null){
                                               linearLay.removeAllViews(); // clear all views in linear lay
                                           }

                                           arrayFeatureType = new ArrayList<>(); // create arrayFeatureType
                                           arrayFeaArray = new ArrayList<>(); // create arrayFeaArray


                                           for (int a = 0; a < query.getDocuments().size(); a++){

                                               DocumentSnapshot  docSn = query.getDocuments().get(a);  // document  docSn

                                               String doc = docSn.getId(); // get document name as String type....

                                               ArrayList<String> arrayList = new ArrayList<>(); //create arrayList for store features that are in Spinner

                                               for(int b =0; b < docSn.getData().size(); b++){

                                                   String t = Integer.toString(b); // convert integer to String
                                                   String fea = docSn.getString(t); // get document data
                                                   arrayList.add(fea); // add document data into arrayList

                                               }

                                               arrayFeaArray.add(arrayList); // add arrayList into arrayFesArray
                                               arrayFeatureType.add(doc); // add doc into arrayFeatureType

                                               product.setProFeatureType(arrayFeatureType);


                                           }
                                       }

                                       setProductInfo(); // go to setProductInfo

                                       progressDialog.dismiss(); // dismiss progress dialalog;

//                                       Toast.makeText(secondPage.this, arrayFeaArray.toString(), Toast.LENGTH_SHORT).show();

                                   }
                               });



                   }

               });
        }

//        -------------------------------------------------------------------------------------------------------

    }

    private void setProductInfo(){


     linearLay = (LinearLayout) findViewById(R.id.pro_info_lay); //initialize layout...

        ArrayList<HashMap> arrayHash = new ArrayList<>();

        for(int a = 0; a < arrayFeatureType.size(); a++){

            LinearLayout layout = new LinearLayout(secondPage.this); // create Linear layout.....
            layout.setOrientation(LinearLayout.VERTICAL); //
            layout.setDividerPadding(10);

            TextView textFeature = new TextView(secondPage.this); // create textview
            textFeature.setTextSize(18);
            textFeature.setPadding(10,10,10,10);
            textFeature.setTop(10);
            textFeature.setText(arrayFeatureType.get(a)); // set text



//            ---------------- create spinner -----------------------------------
            final Spinner spinner = new Spinner(secondPage.this);

//            Toast.makeText(this, arrayFeaArray.toString(), Toast.LENGTH_SHORT).show();

            SpinnerAdapter adapter = new myAdapter(arrayFeaArray.get(a),secondPage.this);

            spinner.setAdapter(adapter);
            spinner.setLeft(20);
            spinner.setMinimumWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            spinner.setGravity(Gravity.RIGHT);

            layout.addView(textFeature);
            layout.addView(spinner);

            linearLay.addView(layout);

            final int b = a;

            final HashMap<String,String> hashMap = new HashMap<>(); // create hash map

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ArrayList<String> reArray = arrayFeaArray.get(b);
                    String msg = reArray.get(parent.getSelectedItemPosition());

                    hashMap.put(arrayFeatureType.get(b),msg); // set hash map
                    Toast.makeText(secondPage.this, msg, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            arrayHash.add(hashMap);

        }

        product.setProFeature(arrayHash);


    }

    public void finishPro(View view){
        int Id = view.getId();

        switch (Id){
            case R.id.btn_add:
                if(checkActivity()) {
                    productReview();
                }else{
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_cancel:
                Toast.makeText(this, product.getProFeature().toString()+"  "+product.getHashSize().toString()+" --- "+product.getSizeCatagory() + " >> "+product.getProFeatureType().toString(), Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_sum_done:
                productAddFB();
                Toast.makeText(this, "Done..!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_sum_cancel:
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                dialogPreview.dismiss();
                break;
        }
    }

    private void productReview(){

        AlertDialog.Builder builderAl = new AlertDialog.Builder(secondPage.this);
        View viewSum = getLayoutInflater().inflate(R.layout.layout_product_summary,null);

//--------------------------------- front - image -view ------------------------------

        ImageView frontImageView = (ImageView) viewSum.findViewById(R.id.front_img_view_sum);
        frontImageView.setImageBitmap(product.getFrontImg());

        //--------------------------------- back - image -view ------------------------------

        ImageView backImageView = (ImageView) viewSum.findViewById(R.id.back_img_view_sum);
        backImageView.setImageBitmap(product.getBackImg());


        //        ----------------------------------  Barcode ------------------------------------------------

        TextView barcodeView = (TextView) viewSum.findViewById(R.id.text_barcode_sum);
        barcodeView.setText(product.getBarcode());


//        --------------------------------- Product - Qty --------------------------------------------

        TextView Qtyview = (TextView) viewSum.findViewById(R.id.text_product_qty_sum);
        Qtyview.setText(product.getQty());


//        ----------------------------------- Product - Name -----------------------------------------

        TextView productNameView = (TextView) viewSum.findViewById(R.id.text_product_name_sum);
        productNameView.setText(product.getName());


//       ------------------------------------ Product - Price -----------------------------------------

        TextView productPriceView = (TextView) viewSum.findViewById(R.id.text_product_price_sum);
        productPriceView.setText(product.getPrice());


//        ------------------------------ Product - Size type --------------------------------------

        TextView productSizeType = (TextView) viewSum.findViewById(R.id.pro_size_type_sum);
        productSizeType.setText(product.getSizeType());

//       ------------------------------- Product -  Size Category ---------------------------------

        TextView productSizeCat = (TextView) viewSum.findViewById(R.id.text_size_catagory_sum);
        productSizeCat.setText(product.getSizeCatagory());

//       ------------------------------- Product - Size ---------------------------------------

        TextView productSize = (TextView) viewSum.findViewById(R.id.text_product_size_sum);
        String size = product.getHashSize().get(product.getSizeCatagory()).toString();
        productSize.setText(size);


//       ------------------------------ Product - colour ---------------------------------------

        TextView productColor = (TextView) viewSum.findViewById(R.id.text_product_color_sum);
        productColor.setText(product.getColour());


//        ---------------------------- Product - Brand -----------------------------------------
        TextView productBrand = (TextView) viewSum.findViewById(R.id.text_product_brand_sum);
        productBrand.setText(product.getBrand());


        TextView productCatagory = (TextView) viewSum.findViewById(R.id.text_product_cat_sum);
        productCatagory.setText(product.getCatagory());

        LinearLayout proFeaLay = (LinearLayout) viewSum.findViewById(R.id.pro_info_detail_info);

        for(int s =0; s < product.getProFeatureType().size(); s++){

            LinearLayout linearlaySum = new LinearLayout(viewSum.getContext());
            linearlaySum.setMinimumWidth(proFeaLay.getWidth());
            linearlaySum.setOrientation(LinearLayout.VERTICAL);

            TextView textFeaTopic = new TextView(viewSum.getContext());
            textFeaTopic.setText(product.getProFeatureType().get(s));
            textFeaTopic.setWidth(100);
            textFeaTopic.setTextSize(20);
            textFeaTopic.setPadding(10,10,10,10);
            textFeaTopic.setGravity(Gravity.LEFT);

            TextView textFeature = new TextView(viewSum.getContext());
            textFeature.setWidth(100);
            textFeature.setTextSize(25);
            textFeature.setTextColor(Color.BLUE);
            textFeature.setPadding(10,10,10,10);
            textFeature.setGravity(Gravity.LEFT);

            HashMap<String,String> hashMap = product.getProFeature().get(s);
            String feat = hashMap.get(product.getProFeatureType().get(s));
            textFeature.setText(feat);

            linearlaySum.addView(textFeaTopic);
            linearlaySum.addView(textFeature);

            proFeaLay.addView(linearlaySum);
        }

        builderAl.setView(viewSum);
        dialogPreview = builderAl.create();
        dialogPreview.show();

    }

    private void productAddFB(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy ,  HH : mm : ss a");
        String date = dateFormat.format(new Date());

        HashMap<String,Object> productHash = new HashMap<>();
        productHash.put("barcode",product.getBarcode());
        productHash.put("name",product.getName());
        productHash.put("price",product.getPrice());
        productHash.put("colour",product.getColour());
        productHash.put("who",product.getWho());
        productHash.put("category",product.getCatagory());

        productHash.put("sizeType",product.getSizeType());
        productHash.put("sizeCategory",product.getSizeCatagory());
        productHash.put("Size",product.getHashSize());
        productHash.put("brand",product.getBrand());
        productHash.put("qty",product.getQty());

        productHash.put("feature-type",product.getProFeatureType());
        productHash.put("feature",product.getProFeature());

        productHash.put("shop",operator.getShop());
        productHash.put("location",operator.getLocation());
        productHash.put("Date",date);

        HashMap<String,String> EnOpt = new HashMap();
        EnOpt.put("firstName",operator.getFirstName());
        EnOpt.put("lastName",operator.getLastName());
        EnOpt.put("Id",operator.getUserId());
        EnOpt.put("EmpID",operator.getUserEmpId());
        EnOpt.put("email",operator.getEmail());

        productHash.put("Operator",EnOpt);


        firestore.collection("Product").add(productHash).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(secondPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {

//                ------------------ upload - front image -------------------------

                if(product.getFrontImg() != null) {

                    StorageReference frontImagRef = stoRef.child("Images").child(documentReference.getId()).child("frontImage.jpg");

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    product.getFrontImg().compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] dataFront = bos.toByteArray();

                    frontImagRef.putBytes(dataFront)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(secondPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    HashMap<String,Object> FrontImgHash = new HashMap<>();
                                    FrontImgHash.put("front-Image",taskSnapshot.getDownloadUrl().toString());

                                   documentReference.update(FrontImgHash).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(secondPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });

                                    Toast.makeText(secondPage.this, "Front Image upload into System Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    Toast.makeText(secondPage.this, " Front image not upload", Toast.LENGTH_SHORT).show();
                }

//                ------------------ upload - back image -------------------------

                if(product.getBackImg() != null) {

                    StorageReference frontImagRef = stoRef.child("Images").child(documentReference.getId()).child("backImage.jpg");

                    ByteArrayOutputStream bosBack = new ByteArrayOutputStream();
                    product.getBackImg().compress(Bitmap.CompressFormat.JPEG, 100, bosBack);
                    byte[] dataFront = bosBack.toByteArray();

                    frontImagRef.putBytes(dataFront)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(secondPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    HashMap<String,Object> BackImgHash = new HashMap<>();
                                    BackImgHash.put("back-Image",taskSnapshot.getDownloadUrl().toString());

                                    documentReference.update(BackImgHash).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(secondPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });

                                    Toast.makeText(secondPage.this, "Back Image upload into System Successfully", Toast.LENGTH_SHORT).show();

                                }
                            });
                }else{
                    Toast.makeText(secondPage.this, " Back Image not upload", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(secondPage.this, "This product is added into system successfully", Toast.LENGTH_SHORT).show();

                dialogPreview.dismiss();

            }

        });

    }

    private boolean checkActivity() {

        if(product.getQty() == "" || product.getQty() == null){
            checkedQty = false;
        }

        if(checkedSizeType && checkedCat && checkedQty){
            return true;
        }else if(!checkedCat){
            Toast.makeText(this, "Click on one of the set category radio button ", Toast.LENGTH_SHORT).show();
        }else if(!checkedSizeType){
            Toast.makeText(this, "Click on Standard size or plus size radio button", Toast.LENGTH_SHORT).show();
        }else if(! checkedQty){
            Toast.makeText(this, "please Set Qty", Toast.LENGTH_SHORT).show();
        }

        return false;

    }

//    private boolean checkInfo(){
//
//
//    }




}
