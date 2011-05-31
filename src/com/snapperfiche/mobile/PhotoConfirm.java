package com.snapperfiche.mobile;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.snapperfiche.code.Enumerations.BasicStatus;
import com.snapperfiche.webservices.PostService;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoConfirm extends Activity {
	String cameraImageUrl;
	
	Button btnTags;
	EditText etxtCaption;
	TextView txtLocation;
	TextView txtAM_PM;
	TextView txtTime;
	TextView txtDate;
	Address addr;
	Date date;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photoconfirm);
        
        //find controls
        ImageView imgPreview = (ImageView) findViewById(R.id.picPreview);
        btnTags = (Button) findViewById(R.id.btn_photo_confirm_tags);
        etxtCaption = (EditText) findViewById(R.id.etxt_photo_confirm_caption);
        txtLocation = (TextView) findViewById(R.id.txt_photo_confirm_location);
        txtAM_PM = (TextView) findViewById(R.id.txt_photo_confirm_ampm);
        txtTime = (TextView) findViewById(R.id.txt_photo_confirm_time);
        txtDate = (TextView) findViewById(R.id.txt_photo_confirm_date);
        
        Bundle cameraBundle = this.getIntent().getExtras();
        String imgUrl = (String) cameraBundle.get("fullpathSkewed");
        addr = (Address) cameraBundle.get("photoAddress");
        //String username = (String) cameraBundle.get("username");
        Uri imgUri = Uri.parse(imgUrl);
        cameraImageUrl = imgUrl;
        imgPreview.setImageURI(imgUri);
        
        //TODO check for addr null
        txtLocation.setText(addr.getLocality() + "," + addr.getAdminArea());
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a | MM.dd.yyyy");
        date = new Date();
        txtTime.setText(dateFormat.format(date));
        
        /*
        File pic = new File(imgUrl);
        
        pic = pic.getAbsoluteFile();
        boolean exists = pic.exists();
        String fileName = pic.getName();
        String fileString = pic.toString();
        
        //instantiate basic http params
        //HttpParams postParameters = new BasicHttpParams();
        
        
        HttpClient client = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        //String postUrl = "http://10.0.2.2:3000/posts";
        String postUrl = "http://192.168.1.4:3000/posts";
        HttpPost post = new HttpPost(postUrl);
        
        FileBody bin = new FileBody(pic, "image/jpeg");
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        //ContentBody cb = bin;
        reqEntity.addPart("photo", bin);
        
		try {
			StringBody sbody, sbody2, sbody3;
			sbody = new StringBody("3");
			sbody2 = new StringBody("hello fichey fichey");
			reqEntity.addPart("user_id", sbody);
	        reqEntity.addPart("caption", sbody2);
	        reqEntity.addPart("post_type", new StringBody("default"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        //postParameters.setParameter("user_id", 1);
        //postParameters.setParameter("caption", "test caption");
        //post.setParams()
        post.setEntity(reqEntity);
        
        ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("user_id", "1"));
        postParams.add(new BasicNameValuePair("caption", "from baby droid"));
        
        try {
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParams);
        	//ContentBody body = new BasicContentBody();
        	
			//post.setEntity(formEntity);
			//postParameters.setParameter("post", postParams);
			//post.setParams(postParameters);
			//reqEntity.addPart("formData", formEntity);
			//reqEntity.
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        try {
			HttpResponse response = client.execute(post, localContext);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        
		Button btnTagFriend = (Button) findViewById(R.id.btnTagFriend);
		btnTagFriend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				BasicStatus status = PostService.Post(etxtCaption.getText().toString(), cameraImageUrl, addr, null, null, false);
				
				Intent i = new Intent(v.getContext(), StatusFeed.class);
				i.putExtra("reloadFeed", true);
				startActivity(i);
				
			}
        	
        });
    }
}

