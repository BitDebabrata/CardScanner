package com.cardScanner.Parser;

import com.google.firebase.codelab.mlkit.LaunchMlKitActivity;

import java.util.regex.Pattern;

public class CardNumberParser implements ParserChain {
    ParserChain nextChain;
    Pattern cardNumberPattern = Pattern.compile("[0-9]{16}");

    @Override
    public void parse(LaunchMlKitActivity activity, String detectedText) {

        if(cardNumberPattern.matcher(detectedText).matches()){
            activity.setCardNumberDetected(true);
            activity.cardNumber = detectedText;

        }
            this.nextChain.parse(activity, detectedText);

    }

    public CardNumberParser(){

        this.nextChain = new ExpiryDateParser();

    }
}
