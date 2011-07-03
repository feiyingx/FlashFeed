package com.snapperfiche.mobile;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapperfiche.code.Enumerations.BasicStatus;
import com.snapperfiche.webservices.PostService;
import com.snapperfiche.webservices.SimpleCache;

public class PhotoConfirm extends Activity {
	String cameraImageUrl;
	
	ImageButton btnTags;
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
        ImageView imgPreview = (ImageView) findViewById(R.id.img_preview);
        btnTags = (ImageButton) findViewById(R.id.btn_tags);
        etxtCaption = (EditText) findViewById(R.id.edit_caption);
        txtLocation = (TextView) findViewById(R.id.text_location);
        //txtAM_PM = (TextView) findViewById(R.id.txt_photo_confirm_ampm);
        txtTime = (TextView) findViewById(R.id.text_timeanddate);
        //txtDate = (TextView) findViewById(R.id.txt_photo_confirm_date);
        
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
        
		Button btnTagFriend = (Button) findViewById(R.id.btn_share);
		btnTagFriend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				BasicStatus status = PostService.Post(etxtCaption.getText().toString(), cameraImageUrl, addr, null, null, false);
				SimpleCache.remove(PostService.getCacheKey_GetGlobalFeed(0));
				Intent i = new Intent(v.getContext(), StatusFeedActivity.class);
				i.putExtra("reloadFeed", true);
				startActivity(i);
				
			}
        	
        });
    }
}

