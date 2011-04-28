package com.snapperfiche.mobile;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CameraTest extends Activity {
	private SurfaceView preview=null;
	private SurfaceHolder previewHolder=null;
	private Camera camera=null;
	private LayoutInflater mInflater = null;
	Button takePictureBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_test);
		preview=(SurfaceView)findViewById(R.id.camera_test);
		previewHolder=preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		mInflater = LayoutInflater.from(this);
        View overView = mInflater.inflate(R.layout.camera_test_overlay, null);
        this.addContentView(overView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        TextView top = (TextView) findViewById(R.id.camera_top);
        TextView bottom = (TextView) findViewById(R.id.camera_bottom);
        TextView cameraOverlay = (TextView) findViewById(R.id.cameraview_overlay);
        
        Log.e("camera_test", "width: " + (width-height)/2);
        
        
        int sideLengths = (int)(Math.ceil((double)(width-height)/2));
        
        Toast
		.makeText(CameraTest.this, String.format("width: %d, height: %d, sideLengths: %d", width, height, sideLengths),
				Toast.LENGTH_LONG)
				.show();
        
        LinearLayout.LayoutParams sideLayoutParams = new LinearLayout.LayoutParams(sideLengths, LayoutParams.FILL_PARENT);
        LinearLayout.LayoutParams cameraLayoutParams = new LinearLayout.LayoutParams(height, LayoutParams.FILL_PARENT);
        top.setLayoutParams(sideLayoutParams);
        cameraOverlay.setLayoutParams(cameraLayoutParams);
        bottom.setLayoutParams(sideLayoutParams);
        //top.setLayoutParams(new LayoutParams((width-height)/2, height));
        //top.setLayoutParams(new LayoutParams(200, 200));
        //takePictureBtn.setWidth(300);
        //takePictureBtn.setLayoutParams(new LayoutParams(300, LayoutParams.FILL_PARENT));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode==KeyEvent.KEYCODE_CAMERA ||
				keyCode==KeyEvent.KEYCODE_SEARCH) {
			takePicture();
			return(true);
		}
		return(super.onKeyDown(keyCode, event));
	}

	private void takePicture() {
		camera.takePicture(null, null, photoCallback);
	}
	SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			Log.e("camera_test", "surfaceCreated");
			/*Toast
			.makeText(CameraTest.this, String.format("width: %d, height: %d", holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height()),
					Toast.LENGTH_LONG)
					.show();*/
			
			//takePictureBtn.setHeight(holder.getSurfaceFrame().height());
			//takePictureBtn.
			
			camera=Camera.open();
			try {
				camera.setPreviewDisplay(previewHolder);
			}
			catch (Throwable t) {
				Log.e("Photographer",
						"Exception in setPreviewDisplay()", t);
				Toast
				.makeText(CameraTest.this, t.getMessage(),
						Toast.LENGTH_LONG)
						.show();
			}
		}
		public void surfaceChanged(SurfaceHolder holder,
				int format, int width,
				int height) {
			Log.e("camera_test", "surfaceChanged");
			Log.e("camera_test", "surfaceCreated: {width=" + width + ", height=" + height + "}");
			/*Toast
			.makeText(CameraTest.this, String.format("width: %d, height: %d", width, height),
					Toast.LENGTH_LONG)
					.show();*/
							
			Camera.Parameters parameters=camera.getParameters();
			
			/*Toast
			.makeText(CameraTest.this, String.format("(Picture size) width: %d, height: %d", parameters.getPictureSize().width, parameters.getPictureSize().height),
					Toast.LENGTH_LONG)
					.show();*/
						
			List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
			List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
			Size optimizedPreviewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);
			Size optimizedPictureSize = getOptimalPictureSize(supportedPictureSizes, width, height);
			/*Log.e("camera_test", String.format("preview_width: %d, preview_height: %d", optimizedPreviewSize.width, optimizedPreviewSize.height));*/
			Toast
			.makeText(CameraTest.this, String.format("preview_width: %d, preview_height: %d", optimizedPreviewSize.width, optimizedPreviewSize.height),
					Toast.LENGTH_LONG)
					.show();
			parameters.setPreviewSize(optimizedPreviewSize.width, optimizedPreviewSize.height);
			parameters.setPictureSize(optimizedPictureSize.width, optimizedPictureSize.height);
			parameters.setPictureFormat(PixelFormat.JPEG);
			camera.setParameters(parameters);
			camera.startPreview();
		}
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.e("camera_test", "surfaceDestroyed");
			camera.stopPreview();
			camera.release();
			camera=null;
		}
	};
	Camera.PictureCallback photoCallback=new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			// do something with the photo JPEG (data[]) here!
			showPicture(data);
			//camera.startPreview();
		}
	};
	
	void showPicture(byte[] data) {
		if (data != null) {
			Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
							
			int width = picture.getWidth();
			int height = picture.getHeight();
			int xCoord = (width-height)/2;
			
			Log.e("flashfeed: CameraTest(showPicture)", String.format("w: %d, h: %d, x: %d", width, height, xCoord));
			Toast
			.makeText(CameraTest.this, String.format("w: %d, h: %d", picture.getWidth(), picture.getHeight()),
					Toast.LENGTH_LONG)
					.show();
			
			//test
			Bitmap bmSkewed = Bitmap.createBitmap(picture, (width-height)/2, 0, height, height);
			//Bitmap bmScaled = Bitmap.createScaledBitmap(bmSkewed, 600, 600, false);
			
			ImageView view = new ImageView(this);
			//view.setImageBitmap(picture);
			view.setImageBitmap(bmSkewed);
			this.setContentView(view);
			
			File myDir = new File("/sdcard/fichey_images");
			myDir.mkdirs();
			String username = "user1";
			
			SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyyHHmmss");
			String fname = username + formatter.format(new Date()) + ".jpg";
			String fnameSkewed = username + formatter.format(new Date()) + "_skewed.jpg";
			
			File file = new File(myDir, fname);			
			if(file.exists()) file.delete();
			String fullpath = file.getAbsolutePath();
			FileOutputStream fos = null;
			
			File fileSkewed = new File(myDir, fnameSkewed);
			if(fileSkewed.exists()) fileSkewed.delete();
			String fullpathSkewed = fileSkewed.getAbsolutePath();
			try{
				fos = new FileOutputStream(file);
				picture.compress(CompressFormat.JPEG, 100, fos);
				
				fos = new FileOutputStream(fileSkewed);
				bmSkewed.compress(CompressFormat.JPEG, 100, fos);
				/*Toast
				.makeText(CameraTest.this, String.format("Picture saved: %s", fullpath),
						Toast.LENGTH_LONG)
						.show();*/
				
			}catch(Throwable ex){
				
			}
			//String url = Images.Media.insertImage(getContentResolver(), bm, "test", null);
			//picture.recycle();
			
			/* bundle photo info and send to the confirm activity */
			Bundle bundle = new Bundle();
			if(fullpath != null){
				bundle.putString("fullpathSkewed", fullpath);
				Intent mIntent = new Intent(this, PhotoConfirm.class);
				mIntent.putExtras(bundle);
				startActivity(mIntent);
			}
		}		
	}
	
	Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		return getOptimalSize(sizes, w, h, true);
	}
	
	Size getOptimalPictureSize(List<Size> sizes, int w, int h) {
		return getOptimalSize(sizes, w, h, false);
	}
	
	Size getOptimalSize(List<Size> sizes, int w, int h, boolean isPreview) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		int targetHeight = h;
		if (sizes == null) return null;
		
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;		
		
		// Try to find a size that matches the aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			//if (isPreview) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			/*} else {
				optimalSize = size;
				break;
			}	*/
		}
		
		// If cannot find a match for the target ratio, take the closest height match
		if (optimalSize == null ) {
			Log.e("flashfeed camera", "Camera aspect ratio not found");
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
			
		return optimalSize;
	}	
}
