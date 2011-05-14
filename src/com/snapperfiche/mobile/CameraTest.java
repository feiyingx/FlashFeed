package com.snapperfiche.mobile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
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
	LocationManager locMgr;
	String locProvider;
	private int orientation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_test);
		preview=(SurfaceView)findViewById(R.id.camera_test);
		previewHolder=preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		locProvider = LocationManager.NETWORK_PROVIDER;
		locMgr = (LocationManager)getSystemService(LOCATION_SERVICE);
		locMgr.requestLocationUpdates(locProvider, 0, 0, onLocationChange);
		
		Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        //int orientation = display.getOrientation();
        orientation = getResources().getConfiguration().orientation;
		
		mInflater = LayoutInflater.from(this);
        View overView = mInflater.inflate(R.layout.camera_test_overlay, null);
        this.addContentView(overView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        LinearLayout overlayLayout = (LinearLayout) findViewById(R.id.ll_camera_overlay);
        TextView top = (TextView) findViewById(R.id.camera_top);
        TextView bottom = (TextView) findViewById(R.id.camera_bottom);
        TextView cameraOverlay = (TextView) findViewById(R.id.cameraview_overlay);
        
        Log.e("flashfeed.camera", "width: " + (width-height)/2);
        
        
        int sideLengths = (int)(Math.ceil((double)(Math.abs(width-height))/2));
        
        Toast
		.makeText(CameraTest.this, String.format("width: %d, height: %d, orientation: %d", width, height, orientation),
				Toast.LENGTH_LONG)
				.show();
        
        /*LinearLayout.LayoutParams sideLayoutParams = new LinearLayout.LayoutParams(sideLengths, LayoutParams.FILL_PARENT);
        LinearLayout.LayoutParams cameraLayoutParams = new LinearLayout.LayoutParams(height, LayoutParams.FILL_PARENT);
        top.setLayoutParams(sideLayoutParams);
        cameraOverlay.setLayoutParams(cameraLayoutParams);
        bottom.setLayoutParams(sideLayoutParams);*/
        
        overlayLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams sideLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, sideLengths);
        LinearLayout.LayoutParams cameraLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, width);
        top.setLayoutParams(sideLayoutParams);
        cameraOverlay.setLayoutParams(cameraLayoutParams);
        bottom.setLayoutParams(sideLayoutParams);
        
        
        
        //top.setLayoutParams(new LayoutParams((width-height)/2, height));
        //top.setLayoutParams(new LayoutParams(200, 200));
        //takePictureBtn.setWidth(300);
        //takePictureBtn.setLayoutParams(new LayoutParams(300, LayoutParams.FILL_PARENT));
	}
	
	public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
			case Surface.ROTATION_0: degrees = 0; break;
			case Surface.ROTATION_90: degrees = 90; break;
			case Surface.ROTATION_180: degrees = 180; break;
			case Surface.ROTATION_270: degrees = 270; break;
		}
		
		//int result = (orientation - degrees + 360) % 360;
		//camera.setDisplayOrientation(result);
	}
	
	private LocationListener onLocationChange=new LocationListener() {
		public void onLocationChanged(Location location) {
			//required for interface
			//Toast.makeText(CameraTest.this, "Location Change", Toast.LENGTH_SHORT).show();
			//Toast.makeText(CameraTest.this, String.format("Lat: %f, Long: %f", location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();
		}
		public void onProviderDisabled(String provider) {
			//required for interface
		}
		public void onProviderEnabled(String provider) {
			//required for interface
			Toast.makeText(CameraTest.this, "Location Provider Enabled", Toast.LENGTH_SHORT).show();
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {
			//required for interface
		}
	};

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
			Log.e("flashfeed.camera", "surfaceCreated");
			/*Toast
			.makeText(CameraTest.this, String.format("width: %d, height: %d", holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height()),
					Toast.LENGTH_LONG)
					.show();*/
			
			//takePictureBtn.setHeight(holder.getSurfaceFrame().height());
			//takePictureBtn.
			
			camera=Camera.open();
			try {
				camera.setDisplayOrientation(90);
				
				/*Camera.Parameters p = camera.getParameters();
				p.setRotation(90);
				camera.setParameters(p);*/
				
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
			Log.e("flashfeed.camera", "surfaceChanged");
			Log.e("flashfeed.camera", "surfaceChanged: {width=" + width + ", height=" + height + "}");
			Toast
			.makeText(CameraTest.this, String.format("width: %d, height: %d", width, height),
					Toast.LENGTH_LONG)
					.show();
							
			Camera.Parameters parameters=camera.getParameters();
			
			Toast
			.makeText(CameraTest.this, String.format("(Picture size) width: %d, height: %d", parameters.getPictureSize().width, parameters.getPictureSize().height),
					Toast.LENGTH_LONG)
					.show();
						
			int orientation = getResources().getConfiguration().orientation;
			
			List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
			List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
			
			/*Size optimizedPreviewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);
			Size optimizedPictureSize = getOptimalPictureSize(supportedPictureSizes, width, height);*/
			Size optimizedPreviewSize = getOptimalPreviewSize(supportedPreviewSizes, height, width);
			Size optimizedPictureSize = getOptimalPictureSize(supportedPictureSizes, height, width);
			Log.e("camera_test", String.format("preview_width: %d, preview_height: %d", optimizedPreviewSize.width, optimizedPreviewSize.height));
			Toast
			.makeText(CameraTest.this, String.format("preview_width: %d, preview_height: %d", optimizedPreviewSize.width, optimizedPreviewSize.height),
					Toast.LENGTH_LONG)
					.show();
			parameters.setPreviewSize(optimizedPreviewSize.width, optimizedPreviewSize.height);
			parameters.setPictureSize(optimizedPictureSize.width, optimizedPictureSize.height);
			/*parameters.setRotation(90);
			parameters.setPreviewSize(optimizedPreviewSize.height, optimizedPreviewSize.width);
			parameters.setPictureSize(optimizedPictureSize.height, optimizedPictureSize.width);*/
			parameters.setPictureFormat(PixelFormat.JPEG);
			parameters.setRotation(90);
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
							
			/*int width = picture.getWidth();
			int height = picture.getHeight();*/
			int width = picture.getHeight();
			int height = picture.getWidth();
			int offset = Math.abs((width-height))/2;
			
			int targetHeight = 600;
			double scaleFactor = (double)targetHeight / height;
			int targetWidth = (int)(width*scaleFactor);
			
			Log.e("flashfeed.camera", String.format("(showPicture) w: %d, h: %d, x: %d", width, height, offset));
			Log.e("flashfeed.camera", String.format("(scaled) scaleFactor: %f, scaledHeight %f, (int): %d", scaleFactor, (width*scaleFactor), (int)(width*scaleFactor)));
			/*Toast
			.makeText(CameraTest.this, String.format("w: %d, h: %d", picture.getWidth(), picture.getHeight()),
					Toast.LENGTH_LONG)
					.show();*/
			
			int orientation1 = getResources().getConfiguration().orientation;
			int orientation2 = getWindowManager().getDefaultDisplay().getOrientation();
			Toast
			.makeText(CameraTest.this, String.format("orientation1: %d, orientation2: %d", orientation1, orientation2),
					Toast.LENGTH_LONG)
					.show();
			
			//test			
			//Bitmap bmScaled = Bitmap.createScaledBitmap(picture, targetWidth, targetHeight, false);			
			//Bitmap bmSkewed = Bitmap.createBitmap(bmScaled, (targetWidth-targetHeight)/2, 0, targetHeight, targetHeight);
			
			Bitmap bmScaled = Bitmap.createScaledBitmap(picture, targetHeight, targetWidth, false);
			Bitmap bmSkewed = Bitmap.createBitmap(bmScaled, 0, (targetWidth-targetHeight)/2, targetHeight, targetHeight);
			
			ImageView view = new ImageView(this);
			//view.setImageBitmap(picture);
			view.setImageBitmap(bmSkewed);
			this.setContentView(view);
			
			//getLocation();
			Address addr = getAddress();
			String currentAddr = "";
			if (addr != null) {
				currentAddr = String.format("Address: %s\nLocality: %s\nAdmin: %s\nPostal: %s\nCountry: %s\nFeature: %s\nPremises: %s", 
					addr.getAddressLine(0), 
					addr.getLocality(),
					addr.getAdminArea(),
					addr.getPostalCode(),
					addr.getCountryCode(),
					addr.getFeatureName(),
					addr.getPremises());
			}
			
			File myDir = new File("/sdcard/fichey_images");
			myDir.mkdirs();
			String username = "user1";
			
			SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyyHHmmss");
			String fname = username + formatter.format(new Date()) + ".jpg";
			String fnameSkewed = username + formatter.format(new Date()) + "_skewed.jpg";
			String fnameLocation = username + formatter.format(new Date()) + "_location.txt";
			
			File file = new File(myDir, fname);			
			if(file.exists()) file.delete();
			String fullpath = file.getAbsolutePath();
			FileOutputStream fos = null;
			
			File fileSkewed = new File(myDir, fnameSkewed);
			if(fileSkewed.exists()) fileSkewed.delete();
			String fullpathSkewed = fileSkewed.getAbsolutePath();
			
			File locationInfo = new File(myDir, fnameLocation);
			if (locationInfo.exists()) locationInfo.delete();
			String fullpathLocation = locationInfo.getAbsolutePath();
			try{
				fos = new FileOutputStream(file);
				picture.compress(CompressFormat.JPEG, 100, fos);
				
				fos = new FileOutputStream(fileSkewed);
				bmSkewed.compress(CompressFormat.JPEG, 100, fos);
				
				fos = new FileOutputStream(locationInfo);
				fos.write(currentAddr.getBytes());
				
				fos.close();
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
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Toast
		.makeText(CameraTest.this, "orientation change",
				Toast.LENGTH_LONG)
				.show();
	}
	
	/*private void onDestroy() {
		super.onDestroy();
		
		locMgr.removeUpdates(onLocationChange);
	}*/
	
	protected void onDestroy() {
		super.onDestroy();
		
		locMgr.removeUpdates(onLocationChange);
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
			if (isPreview) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			} else {
				optimalSize = size;
				break;
			}	
		}
		
		// If cannot find a match for the target ratio, take the closest height match
		if (optimalSize == null ) {
			Log.e("flashfeed.camera", "Camera aspect ratio not found");
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
	
	private Address getAddress() {
		Location loc =locMgr.getLastKnownLocation(locProvider);
		
		if (loc == null) {
			Toast.makeText(this, "No location available", Toast.LENGTH_SHORT).show();
			return null;
		}
		else {
			Log.e("flashfeed.camera", String.format("L:%f,%f", loc.getLatitude(), loc.getLongitude()));
			//Toast.makeText(this, String.format("L:%f,%f", loc.getLatitude(), loc.getLongitude()), Toast.LENGTH_LONG).show();
			Geocoder gc = new Geocoder(this);
			try {
				List<Address> addresses = gc.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
				Address addr = null;
				if (addresses != null) {
					addr = addresses.get(0);
					if (addr != null) {
						Toast.makeText(this, String.format("Address: %s\nLocality: %s\nAdmin: %s\nPostal: %s\nCountry: %s\nFeature: %s\nPremises: %s", 
								addr.getAddressLine(0), 
								addr.getLocality(),
								addr.getAdminArea(),
								addr.getPostalCode(),
								addr.getCountryCode(),
								addr.getFeatureName(),
								addr.getPremises()
								), Toast.LENGTH_LONG).show();
						return addr;
					}
				}
				Toast.makeText(this, String.format("No address found"), Toast.LENGTH_LONG).show();
				return null;
			} catch (IOException ex) {
				Log.e("flashfeed.camera", "error finding list of addresses");
				return null;
			}
		}
	}
}
