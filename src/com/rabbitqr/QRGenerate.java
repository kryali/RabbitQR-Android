package com.rabbitqr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class QRGenerate extends Activity {

	private Handler main;
	private String receiverPhone;
	private String payerPhone;
	private String amount; 
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_screen);
        Bundle extras = this.getIntent().getExtras();

        // Grab current phone number
        TelephonyManager phoneManager = (TelephonyManager) getSystemService("phone");
        payerPhone = phoneManager.getLine1Number();
        receiverPhone = (String)extras.get("com.rabbitqr.receiverPhone");
        //Toast.makeText(this, payerPhone + "->" + receiverPhone, Toast.LENGTH_SHORT).show();

        main = new Handler();        
        
        ImageButton payBtn = (ImageButton) findViewById(R.id.payButton);
        payBtn.setOnClickListener(new OnClickListener() {
			
        EditText amountText = (EditText) findViewById(R.id.edittext);
        	
			public void onClick(View v) {
				// TODO Auto-generated method stub
				amount= amountText.getText().toString();
				if(amount.contentEquals("")){
					Toast.makeText(QRGenerate.this, "Please enter in a valid dollar amount", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(QRGenerate.this, "Sending $" + amount + " to "+ receiverPhone + "..." 
						, Toast.LENGTH_SHORT).show();
					makePayment.start();	
				}
				
			}
		});
        
    }
    
    private void sendTransaction(){
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://rabbitqr.com:81/pay");
		try{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("payerPhone", payerPhone));
			nameValuePairs.add(new BasicNameValuePair("receiverPhone", receiverPhone));
			nameValuePairs.add(new BasicNameValuePair("amount", amount));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = client.execute(post);
	        } catch (Exception e) {
			//Toast.makeText(QRGenerate.this, e.toString(), Toast.LENGTH_LONG);
	        }
    }
    
    private Thread makePayment = new Thread(){
    	public void run(){
    		sendTransaction();
    		main.post(postFinished);
    	}	
    };
    
    public void print(){
		Toast.makeText(QRGenerate.this, "Payment sent! The recepient should get a confimation soon.", Toast.LENGTH_LONG).show();
    }
    
    private Runnable postFinished = new Runnable(){
    	public void run(){
    		print();
    	}
    };
    
    
    
}
	