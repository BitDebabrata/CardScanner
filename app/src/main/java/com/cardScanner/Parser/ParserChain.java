package com.cardScanner.Parser;

import com.google.firebase.codelab.mlkit.LaunchMlKitActivity;

public interface ParserChain {
    public void parse(LaunchMlKitActivity activity,String detectedText);

}
