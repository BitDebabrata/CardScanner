package com.cardScanner.Parser;

import com.google.firebase.codelab.mlkit.LaunchMlKitActivity;

public class DataExtractor {

    ParserChain parser;
    public void extractData(LaunchMlKitActivity activity,String detectedText){
        this.parser.parse(activity,detectedText);
    }
    public DataExtractor(){
        parser = new CardNumberParser();
    }
}