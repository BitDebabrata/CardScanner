package com.cardScanner.Parser;

import com.google.firebase.codelab.mlkit.LaunchMlKitActivity;

import java.util.regex.Pattern;

public class CvvParser implements ParserChain {
    LaunchMlKitActivity activity;
    ParserChain nextChain;
    Pattern expiryDatePattern = Pattern.compile("[0-9]{3}");

    public CvvParser(){
        this.nextChain = null;
    }

    @Override
    public void parse(LaunchMlKitActivity activity, String detectedText) {

        if(expiryDatePattern.matcher(detectedText).matches()){
            activity.cvv = detectedText;
        }
    }
}
