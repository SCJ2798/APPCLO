package com.project.appclo.dataentryapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class barcode_scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(scannerView == null){
            scannerView = new ZXingScannerView(this);
        }

        scannerView.setResultHandler(this);
        scannerView.startCamera();

    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {

        String scanresult = result.getText();
        Toast.makeText(this, scanresult, Toast.LENGTH_SHORT).show();

       // entryOperator

        Intent intent = new Intent(barcode_scanner.this,dataEntryAct.class);
       // intent.putExtra("barcode",scanresult);
        startActivity(intent);

    }
}
