package com.google.firebase.codelab.mlkit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.stripe.android.view.CardInputWidget;

import io.fabric.sdk.android.Fabric;
import java.util.Timer;
import java.util.TimerTask;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class ScanCardActivity extends Activity implements View.OnClickListener{
    private static final int REQUEST_SCAN = 100;
    private static final int REQUEST_AUTOTEST = 200;

    Button saveButton  ;
    EditText zipCode ;
    CardInputWidget cardInputWidget ;
    Button scanCardButton ;

    //----------
    private final static int MESSAGE_UPDATE_TEXT_CHILD_THREAD =1;
    private final static int MESSAGE_UPDATE_TEXT_CHILD_THREAD2 =2;
    private Handler updateUIHandler = null;
    //-----------
volatile boolean isCardDetected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_scan_card);

        saveButton = findViewById(R.id.saveButton);
        zipCode = findViewById(R.id.zipCode);
        cardInputWidget = findViewById(R.id.cardInputWidget);
        scanCardButton = findViewById(R.id.scanCardButton);
        //      saveButton.setOnClickListener(this);
        scanCardButton.setOnClickListener(this);
    }
/*
        final Timer timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        //captureCameraImage.peformClick();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!isCardDetected ){
                                    Intent intent = new Intent(ScanCardActivity.this,LaunchMlKitActivity.class);
                                    *//*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);*//*
                                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivityForResult(intent,1);

                                }
                            }
                        });

                    }
                }, 20000   );*/




    @Override
    public void onClick(View view) {
       if(view.equals(scanCardButton)) {

           Intent intent = new Intent(ScanCardActivity.this, LaunchMlKitActivity.class);
           startActivityForResult(intent, 1);
       }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_SCAN || requestCode == REQUEST_AUTOTEST) && data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            isCardDetected=true;
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {

                io.card.payment.CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                setCardWidget(scanResult);
                Log.d("Scan Result", scanResult.cardNumber);

            }
        } else if(data!=null && data.hasExtra("mlKitData") ){
            if(resultCode==RESULT_OK) {
                isCardDetected = true;
                setCardWidget(data);
            }else if(resultCode == RESULT_CANCELED){

                callCardIO();

            }
        }
    }
    public void setCardWidget(CreditCard data){
        cardInputWidget.setCardNumber(data.cardNumber);
        if(data.expiryMonth!=0 && data.expiryYear!=0) {
            cardInputWidget.setExpiryDate(data.expiryMonth, data.expiryYear);
        }

    }

    public void setCardWidget(Intent data){
        cardInputWidget.setCardNumber(data.getStringExtra("cardNumber"));
        if(data.getStringExtra("expiryMonth")!=null){
            //cardInputWidget.setExpiryDate(data.getIntExtra("expiryMonth",00),data.getIntExtra("expiryYear",00));
            cardInputWidget.setExpiryDate(Integer.parseInt(data.getStringExtra("expiryMonth")),
                    Integer.parseInt(data.getStringExtra("expiryYear")));
        }
        if(data.getStringExtra("cvv")!=null){
            cardInputWidget.setCvcCode(data.getStringExtra("cvv"));
        }
    }
    public void callCardIO(){
        clearUIElements();
        Intent intent = new Intent(ScanCardActivity.this, CardIOActivity.class)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true)
                .putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true)
                .putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE, true)
                .putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION,true)
                .putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO,true)
                .putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY,true);
        this.startActivityForResult(intent, REQUEST_SCAN);
    }
    public void clearUIElements(){
        cardInputWidget.clear();
    }
}