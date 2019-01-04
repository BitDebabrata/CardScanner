package com.cardScanner.Parser;

import com.google.firebase.codelab.mlkit.LaunchMlKitActivity;

import java.util.regex.Pattern;

public class ExpiryMulitpleDateParser implements ParserChain {

    LaunchMlKitActivity activity;
    Pattern expiryDatePatternCompbined = Pattern.compile("[0-9]{2}/[0-9]{4}/[0-9]{2}");
    ParserChain nextLink;

    public ExpiryMulitpleDateParser(){

        nextLink = new CvvParser();
    }

    @Override
    public void parse(LaunchMlKitActivity activity, String detectedText) {

        this.activity = activity;
        if(expiryDatePatternCompbined.matcher(detectedText).matches()){
            activity.setExpiryDateDetected(true);
            parseExpiry(detectedText);

        }
        this.nextLink.parse(activity,detectedText);
    }

    public void parseExpiry(String rawText) {

        Integer date[] = new Integer[4];
        date[0] = Integer.parseInt(rawText.split("/")[0]);
        date[1] = Integer.parseInt(rawText.split("/")[1].substring(0, 2));
        date[2] = Integer.parseInt(rawText.split("/")[1].substring(2));
        date[3] = Integer.parseInt(rawText.split("/")[2]);
        if (date[1] > date[3]) {
            activity.expiryMonth = date[0]+"";
            activity.expiryMonth = date[1]+"";
        } else if (date[1] < date[3]) {
            activity.expiryMonth = date[2]+"";
            activity.expiryYear = date[3]+"";
        } else {
            if (date[0] < date[2]) {
                activity.expiryMonth = date[2]+"";
                activity.expiryYear = date[3]+"";
            } else {
                activity.expiryMonth = date[0]+"";
                activity.expiryYear = date[1]+"";
            }
        }
    }
}
