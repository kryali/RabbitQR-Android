package com.rabbitqr;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;



public class rabbitqr extends Activity {
    /** Called when the activity is first created. */
	public String phoneNumber;
	private Handler main;
	public ImageView i;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelephonyManager phoneManager = (TelephonyManager) getSystemService("phone");
        phoneNumber = phoneManager.getLine1Number();
		
        setContentView(R.layout.main);
        ImageButton button = (ImageButton) findViewById(R.id.scanButton);
        
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	startScan();
            }
        });
        
        // Set up QRCodeFetcher
         i = (ImageView)findViewById(R.id.image);
         grabImage();

         //fetchQRCode.start();
         //Toast.makeText(rabbitqr.this, phoneNumber, Toast.LENGTH_LONG).show();
 		
    }
    
    public void grabImage(){

		String imageurl = "http://chart.apis.google.com/chart?cht=qr&chs=300x300&chl=" + phoneNumber;
		//Toast.makeText(rabbitqr.this, imageurl, Toast.LENGTH_LONG).show();
        
		try {
      	  Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageurl).getContent());
      	  i.setImageBitmap(bitmap); 
      } catch (MalformedURLException e) {
      	  e.printStackTrace();
     	} catch (IOException e) {
      	  e.printStackTrace();
      }   
    }

    private Thread fetchQRCode = new Thread(){
    	public void run(){
    		//grabImage();
      
    	}	
    };
    
    public void startScan(){
    	IntentIntegrator.initiateScan(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	
    	IntentResult scanResult = 
    		IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
    	if( scanResult != null){
    		String contents = scanResult.getContents()+ " " + phoneNumber; 
            if (contents == null) { 
              Log.d("SCAN", "no scan results"); 
              return; 
            } 
            Log.d("SCAN", "Got it!"); 
            finishActivity(IntentIntegrator.REQUEST_CODE); 
            String format = scanResult.getFormatName(); 
            
            if(scanResult.getContents()==null){
            		Toast.makeText(rabbitqr.this, "We screwed up, working on it!", Toast.LENGTH_SHORT).show();
            		return;
            }

			Intent payIntent = new Intent(rabbitqr.this, QRGenerate.class);
			payIntent.putExtra("com.rabbitqr.receiverPhone", scanResult.getContents());
			startActivity(payIntent);
            
    	}
    }
}