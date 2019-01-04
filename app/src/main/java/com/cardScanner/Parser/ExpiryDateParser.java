package com.cardScanner.Parser;

import com.google.firebase.codelab.mlkit.LaunchMlKitActivity;

import java.util.regex.Pattern;

public class ExpiryDateParser implements ParserChain {


    LaunchMlKitActivity activity;
    ParserChain nextChain;
    Pattern expiryDatePattern = Pattern.compile("[0-9]{2}/[0-9]{2}");

    public ExpiryDateParser(){
        this.nextChain = new ExpiryMulitpleDateParser();
    }



    @Override
    public void parse(LaunchMlKitActivity activity, String detectedText) {
        this.activity = activity;
        if(expiryDatePattern.matcher(detectedText).matches()){
            activity.setExpiryDateDetected(true);
            setExpiryDate(detectedText);
        }else{
            this.nextChain.parse(activity,detectedText);
        }
    }

    public void setExpiryDate(String detectedText){
        activity.expiryMonth = detectedText.split("/")[0];
        activity.expiryYear = detectedText.split("/")[1];
    }
}
