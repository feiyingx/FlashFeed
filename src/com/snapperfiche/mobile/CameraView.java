package com.snapperfiche.mobile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.*;
import org.apache.http.impl.client.DefaultHttpClient;

public class CameraView extends Activity implements SurfaceHolder.Callback {
	private static final String TAG = "flashfeed.camera";
	private LayoutInflater mInflater = null;
	byte[] tempData;
	SurfaceView mSurfaceView;
	SurfaceHolder mSurfaceHolder;
	Camera mCamera;
	boolean mPreviewRunning = false;
	Button takePictureBtn;
	private static int CAMERA_RESULT = 1225;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //use the built in camera activity
        /*
        Intent nativeCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator, "test.jpg");
        Uri outputFileUri = Uri.fromFile(file);
        nativeCamera.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        this.startActivityForResult(nativeCamera, CAMERA_RESULT);
		*/
        
        
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.camera_surface);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        
        mInflater = LayoutInflater.from(this);
        View overView = mInflater.inflate(R.layout.camera_overlay, null);
        this.addContentView(overView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        takePictureBtn = (Button) findViewById(R.id.takePictureBtn);
        
        takePictureBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.takePicture(mShutterCallback, mPictureCallback, mjpeg);
			}
        	
        });
        
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == CAMERA_RESULT){
			if(resultCode == Activity.RESULT_OK){
				Bundle b = data.getExtras();
				Uri imgUri = (Uri) b.get(MediaStore.EXTRA_OUTPUT);
				
				Bitmap bm = (Bitmap) b.get("data");
				InputStream inputStream = null;
				try {
					inputStream = getContentResolver().openInputStream(imgUri);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bitmap bm2 = BitmapFactory.decodeStream(inputStream);
				if(bm2 != null){
					File myDir = new File("/sdcard/fichey_images");
					myDir.mkdirs();
					
					SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyyHHmmss");
					String fname = formatter.format(new Date()) + ".jpg";
					
					File file = new File(myDir, fname);
					if(file.exists()) file.delete();
					String fullpath = file.getAbsolutePath();
					FileOutputStream fos = null;
					try{
						fos = new FileOutputStream(file);
						bm2.compress(CompressFormat.JPEG, 100, fos);
						
					}catch(Throwable ex){
						
					}
					//String url = Images.Media.insertImage(getContentResolver(), bm, "test", null);
					bm2.recycle();
					Bundle bundle = new Bundle();
					if(fullpath != null){
						bundle.putString("fullpath", fullpath);
						Intent mIntent = new Intent(this, PhotoConfirm.class);
						mIntent.putExtras(bundle);
						startActivity(mIntent);
						//setResult(RESULT_OK, mIntent);
					}else{
						Toast.makeText(this, "Doh, your picture can not be saved", Toast.LENGTH_SHORT).show();
					}
					finish();
				}
				
			}else if(resultCode == Activity.RESULT_CANCELED){
				
			}
		}
	}
	
	ShutterCallback mShutterCallback = new ShutterCallback(){
		public void onShutter(){
			
		}
	};
	PictureCallback mPictureCallback = new PictureCallback(){
		public void onPictureTaken(byte[] data, Camera c){
			
		}
	};
	PictureCallback mjpeg = new PictureCallback(){
		public void onPictureTaken(byte[] data, Camera c){
			if(data != null){
				tempData = data;
				done();
			}
		}
	};
	void done(){
		Bitmap bm = BitmapFactory.decodeByteArray(tempData, 0, tempData.length);
		bm = Bitmap.createScaledBitmap(bm, (int)(0.18 * bm.getHeight()), (int) (0.18 * bm.getWidth()), true);
		File myDir = new File("/sdcard/fichey_images");
		myDir.mkdirs();
		String username = "user1";
		
		SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyyHHmmss");
		String fname = username + formatter.format(new Date()) + ".jpg";
		
		File file = new File(myDir, fname);
		if(file.exists()) file.delete();
		String fullpath = file.getAbsolutePath();
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(file);
			bm.compress(CompressFormat.JPEG, 100, fos);
			
		}catch(Throwable ex){
			
		}
		//String url = Images.Media.insertImage(getContentResolver(), bm, "test", null);
		bm.recycle();
		Bundle bundle = new Bundle();
		if(fullpath != null){
			bundle.putString("fullpath", fullpath);
			bundle.putString("username", "user1");
			Intent mIntent = new Intent(this, PhotoConfirm.class);
			mIntent.putExtras(bundle);
			startActivity(mIntent);
			//setResult(RESULT_OK, mIntent);
		}else{
			Toast.makeText(this, "Doh, your picture can not be saved", Toast.LENGTH_SHORT).show();
		}
		finish();
	}
	
	private void uploadFile(Bitmap bm){
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("10.0.2.2/posts/new");
		//MultipartEntity entity = new MultipartEntity();
		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// TODO Auto-generated method stub
		Log.e(TAG, "surfaceChanged");
		try{
			if(mPreviewRunning){
				mCamera.stopPreview();
				mPreviewRunning = false;
			}
			
			Camera.Parameters params = mCamera.getParameters();
			params.setPreviewSize(w, (int)(0.8*h));
			params.setJpegQuality(100);
			
			mCamera.setParameters(params);
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
			mPreviewRunning = true;
		}catch(Exception e){
			Log.d("", e.toString());
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		Log.e(TAG, "surfaceCreated");
		mCamera = Camera.open();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		Log.e(TAG, "surfaceDestroyed");
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
		mCamera = null;
	}
}
