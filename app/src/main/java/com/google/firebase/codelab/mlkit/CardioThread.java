package com.google.firebase.codelab.mlkit;

public class CardioThread extends Thread {
    @Override
    public void run() {
        super.run();
        /*runOnUiThread(new Runnable() {
            public void run() {
                //Whatever task you wish to perform
                //For eg. textView.setText("SOME TEXT")
            }
        });*/

    }

    /*AsyncLoader.loadOnUIThread(new AsyncLoader.CompletionBlock()
    {
        @Override
        public void run(Exception exception)
        {


          //  if (bmp != null)
            {
               // if (cached)
                {
                    //Timber.i("Image Loaded and Cached " + resId);
                    //stringWeakReferenceHashMap.put(resId + "", new WeakReference<Bitmap>(bmp));
                }
               // imageView.setImageBitmap(bmp);
            }

        }
    });*/
    public void runOnUIThread(){

    }
}
