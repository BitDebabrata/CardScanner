package com.google.firebase.codelab.mlkit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.cardScanner.Parser.DataExtractor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class LaunchMlKitActivity extends AppCompatActivity {
    private CameraKitView cameraKitView;
    TextView cardDataTextView;
    public volatile String cardNumber="";
    public volatile String expiryDate="";
    public volatile boolean isCardDetected;
    public volatile boolean timeExpired;
    public volatile String expiryMonth;
    public volatile String expiryYear;
    public ArrayList dateExpiry = new ArrayList();
    DataExtractor dataExtractor = new DataExtractor();
    public volatile String cvv;
    public volatile boolean cardNumberDetected;
    public volatile boolean expiryDateDetected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_camera);
        cameraKitView = findViewById(R.id.camera);
      //  captureCameraImage = findViewById(R.id.captureImage);
        cardDataTextView = findViewById(R.id.displayCardData);
    }


    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
        cameraKitView.setFocus(CameraKit.FOCUS_AUTO);
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        Log.d("Firebase-Scanner","Text-Found "+date);



            final Timer timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {

                        captureImage();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(isCardDetected || timeExpired){
                                    setResultAndFinish();
                                    timer.cancel();
                                }
                            }
                        });
                    }
                }, 5000,1000
        );
        final Timer timer2 = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        timeExpired = true;

                    }
                }, 20000
        );

    }

    Bitmap mSelectedImage;


public void autoClick(){
    try {
        for (int i = 0; i < 20; i++) {
            Thread.sleep(2);
            //captureCameraImage.performClick();
            captureImage();
        }
    }catch(InterruptedException ie){
        ie.getMessage();
    }
}
    public void scanCard(){
        while(true) {
            if (isCardDetected ) {
                break;
            }else{
                captureImage();
            }
        }
    }

    public void captureImage(){

        cameraKitView.captureImage(new CameraKitView.ImageCallback() {
        @Override
        public void onImage(CameraKitView cameraKitView, byte[] bytes) {
            mSelectedImage = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
            Log.d("","asfd");
            runTextRecognition();

        }

    });
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void runTextRecognition() {


        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {

                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
    }


    private void processTextRecognitionResult(FirebaseVisionText texts) {
        String detectedText="";

        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {

            return;
        }
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        Log.d("Firebase-Scanner","Text-Found "+texts.getText()+":= "+date);
        for (int i = 0; i < blocks.size(); i++) {
            /*Pattern cardNumberPattern = Pattern.compile("[0-9]{16}");
            Pattern expiryDatePattern = Pattern.compile("[0-9]{2}/[0-9]{2}");
            Pattern expiryDatePatternCompbined = Pattern.compile("[0-9]{2}/[0-9]{4}/[0-9]{2}");
*/
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();

            for (int j = 0; j < lines.size(); j++) {


                detectedText = lines.get(j).getText().replaceAll("\\s","");
                dataExtractor.extractData(this,detectedText);


                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {

                }
            }
        }
    }
    public void setResultAndFinish(){

        Intent returnIntent = new Intent();
        returnIntent.putExtra("mlKitData","mlKitData");
        if(isCardDetected && !timeExpired){
            returnIntent.putExtra("cardNumber",cardNumber);
            returnIntent.putExtra("expiryMonth",expiryMonth);
            returnIntent.putExtra("expiryYear",expiryYear);
            returnIntent.putExtra("cvv",cvv);
            setResult(RESULT_OK,returnIntent);
        }else{
            setResult(RESULT_CANCELED,returnIntent);

        }
        finish();
    }

    public void setCardDetected(){
        isCardDetected = cardNumberDetected && expiryDateDetected;
    }


    public void setCardNumberDetected(boolean cardNumberDetected){
        this.cardNumberDetected = cardNumberDetected;
        setCardDetected();
    }
    public void setExpiryDateDetected(boolean expiryDateDetected){
        this.expiryDateDetected = expiryDateDetected;
        setCardDetected();
    }
}