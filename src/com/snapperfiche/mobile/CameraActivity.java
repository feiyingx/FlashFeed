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
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snapperfiche.mobile.custom.TriToggleButton;

public class CameraActivity extends Activity {
	private final String DEBUGTAG = "flashfeed.camera";
	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	boolean mPreviewRunning = false;
	private Camera camera = null;
	private LayoutInflater mInflater = null;
	private ImageView cameraCursor;
	private TriToggleButton timerButton;
	private Button snapButton;
	LocationManager locMgr;
	String locProvider;
	private int mOrientation;
	OrientationEventListener mOrientationEventListener;
	private String mFlashMode;
	private int mTimerDuration;
	private Handler mHandler = new Handler();
	private long mStartTime;
	private boolean isTimerRunning = false;
	private boolean isTakingPicture = false;
	private boolean isAutoFocusing = false;
	private float topOverlayRatio;
	private float cameraBorderRatio;
	private int cameraSideBorderLength;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_surface);
		preview = (SurfaceView) findViewById(R.id.camera_surface);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		initializeScreenBrightness();

		// //// Orientation changes //////
		mOrientationEventListener = new OrientationEventListener(this,
				SensorManager.SENSOR_DELAY_NORMAL) {

			@Override
			public void onOrientationChanged(int orientation) {
				// TODO Auto-generated method stub
				setDisplayOrientation(orientation);
			}
		};

		if (mOrientationEventListener.canDetectOrientation()) {
			mOrientationEventListener.enable();
		} else {
			finish();
		}
		// ///////////////////////////////

		// //// Location updates ////////
		locProvider = LocationManager.NETWORK_PROVIDER;
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		locMgr.requestLocationUpdates(locProvider, 0, 0, onLocationChange);

		mOrientation = getWindowManager().getDefaultDisplay().getOrientation();

		mInflater = LayoutInflater.from(this);
		View overView = mInflater.inflate(R.layout.ui_camera_overlay, null);
		this.addContentView(overView, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setCameraOverlay();
	}

	private void setCameraOverlay() {
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		
		Log.e(DEBUGTAG, String.format("(display) w: %d, h: %d", width, height));

		LinearLayout overlayLayout = (LinearLayout) findViewById(R.id.ll_camera_overlay);
		RelativeLayout top = (RelativeLayout) findViewById(R.id.camera_top);
		//RelativeLayout bottom = (RelativeLayout) findViewById(R.id.camera_bottom);
		RelativeLayout cameraOverlay = (RelativeLayout) findViewById(R.id.cameraview_overlay);
		cameraCursor = (ImageView) findViewById(R.id.img_camera_cursor);
		TextView sideBorder = (TextView) findViewById(R.id.camera_side_border);
		cameraSideBorderLength = sideBorder.getLayoutParams().width;
		
		int topOverlayHeight = top.getLayoutParams().height + cameraSideBorderLength;
		topOverlayRatio = (float) topOverlayHeight / height;
		cameraBorderRatio = (float) cameraSideBorderLength / width;
		int cameraDimension = width - (cameraSideBorderLength*2);

		overlayLayout.setOrientation(LinearLayout.VERTICAL);
		/*LinearLayout.LayoutParams sideLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, sideLengths);
		LinearLayout.LayoutParams cameraLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, width);*/
		LinearLayout.LayoutParams cameraLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, cameraDimension);
		//top.setLayoutParams(sideLayoutParams);
		cameraOverlay.setLayoutParams(cameraLayoutParams);
		//bottom.setLayoutParams(sideLayoutParams);
		
		// Click camera screen to focus
		cameraOverlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(CameraActivity.this, "camera overlay click", Toast.LENGTH_LONG);
				focusCamera();
			}
		});
		
		// Flash Button
		/*final TriToggleButton flashButton = (TriToggleButton) findViewById(R.id.ttbFlash);
		//set default image
		flashButton.setText("flash auto");
		flashButton.setBackgroundResource(R.drawable.icon);*/
		mFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
		/*flashButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int state = flashButton.getState();
				Camera.Parameters parameters = camera.getParameters();
				try {
					switch (state) {
					case 0:
						flashButton.setText("flash auto");
						flashButton.setBackgroundResource(R.drawable.icon);
						mFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
						break;
					case 1:
						flashButton.setText("flash on");
						flashButton.setBackgroundResource(R.drawable.sample_icon_on);
						mFlashMode = Camera.Parameters.FLASH_MODE_ON;
						break;
					case 2:
						flashButton.setText("flash off");
						flashButton.setBackgroundResource(R.drawable.sample_icon_off);
						mFlashMode = Camera.Parameters.FLASH_MODE_OFF;
						break;
					default:
						break;
					}
					parameters.setFlashMode(mFlashMode);
					camera.setParameters(parameters);
				} catch (Exception e) {
					Log.e(DEBUGTAG,
							"ERROR:setCameraOverlay:flashButton:onClick()::  "
									+ e.getMessage());
				}
			}
		});
		
		// Timer Button
		timerButton = (TriToggleButton) findViewById(R.id.ttbTimer);
		timerButton.setText("timer off");
		timerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int state = timerButton.getState();
				try {
					switch (state) {
					case 0:
						timerButton.setText("timer: off");
						mTimerDuration = 0;
						break;
					case 1:
						timerButton.setText("timer: 2 sec");
						mTimerDuration = 2000;
						break;
					case 2:
						timerButton.setText("timer: 5 sec");
						mTimerDuration = 5000;
						break;
					default:
						break;
					}
				} catch (Exception e) {
					Log.e(DEBUGTAG,
							"ERROR:setCameraOverlay:flashButton:onClick()::  "
									+ e.getMessage());
				}
			}
		});*/

		// Snap Button
		snapButton = (Button) findViewById(R.id.btnSnap);
		snapButton.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(CameraActivity.this, "snap long click", Toast.LENGTH_LONG);
				focusCamera();
				return false;
			}
		});
		snapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cameraClick();
			}

		});
	}
	
	private void focusCamera() {
		if (!isTakingPicture && !isAutoFocusing)
		{
			//make sure only one autofocus is happening at once
			isAutoFocusing = true;
			// set flash mode to off before auto focusing
			Camera.Parameters parameters = camera.getParameters();
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			camera.setParameters(parameters);
			
			//show camera focus cursor
			cameraCursor.setVisibility(View.VISIBLE);
		
			// start auto focus
			camera.autoFocus(new AutoFocusCallback() {
				public void onAutoFocus(boolean arg0, Camera arg1) {
					//hide camera focus cursor
					cameraCursor.setVisibility(View.INVISIBLE);
					
					// reset the flash mode back to original
					Camera.Parameters parameters = camera.getParameters();
					parameters.setFlashMode(mFlashMode);
					camera.setParameters(parameters);
					
					isAutoFocusing = false;
				};
			});
		}
	}
	
	private void cameraClick() {
		
		if (isTimerRunning) 
		{
			mHandler.removeCallbacks(mUpdateTimeTask);
			isTimerRunning = false;
			isTakingPicture = false;
			snapButton.setText(R.string.btn_snap);
			timerButton.setEnabled(true);
		} 
		else 
		{
			isTakingPicture = true;
			if (mTimerDuration > 0) {
				isTimerRunning = true;
				timerButton.setEnabled(false);
				
				mStartTime = System.currentTimeMillis();
				mHandler.removeCallbacks(mUpdateTimeTask);
				mHandler.post(mUpdateTimeTask);
				
			} else {
				takePicture();
				snapButton.setEnabled(false);
			}
		}
	}
	
	public void initializeScreenBrightness() {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		winParams.screenBrightness = 0.7f;
		win.setAttributes(winParams);
	}
	
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			final long start = mStartTime;
			long millis = System.currentTimeMillis() - start;
			int seconds = (int) (millis / 1000);
			int timerDurationSeconds = mTimerDuration / 1000;
			
			if (seconds < timerDurationSeconds) {
				int countdown = (mTimerDuration/1000) - seconds;
				snapButton.setText("" + countdown);
				mHandler.post(this);
			} else if (seconds == timerDurationSeconds) {
				snapButton.setText(R.string.btn_snap);
				mHandler.removeCallbacks(this);
				isTimerRunning = false;
				takePicture();
				snapButton.setEnabled(false);
			} else {
				mHandler.removeCallbacks(this);
				isTimerRunning = false;
			}
		}
	};

	private void setDisplayOrientation(int orientation) {
		// absolute cases
		if ((orientation >= 0 && orientation < 30) || orientation >= 330) {
			mOrientation = Surface.ROTATION_0;
		} else if (orientation >= 60 && orientation < 120) {
			mOrientation = Surface.ROTATION_270;
		} else if (orientation >= 150 && orientation < 210) {
			mOrientation = Surface.ROTATION_180;
		} else if (orientation >= 240 && orientation < 300) {
			mOrientation = Surface.ROTATION_90;
		}
	}

	private LocationListener onLocationChange = new LocationListener() {
		public void onLocationChanged(Location location) {
			// required for interface
			Geocoder gc = new Geocoder(CameraActivity.this);
			try {
				List<Address> addresses = gc.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1);
				Address addr = null;
				if (addresses != null) {
					addr = addresses.get(0);
					if (addr != null) {
						TextView txtLocation = (TextView) findViewById(R.id.txtLocation);
						txtLocation.setText(String.format("%s, %s", addr.getLocality(), addr.getAdminArea()));
					}
				}
			} catch (Exception ex) {
				
			}
		}

		public void onProviderDisabled(String provider) {
			// required for interface
		}

		public void onProviderEnabled(String provider) {
			// required for interface
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// required for interface
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_CAMERA) {
			cameraClick();
			return (true);
		}
		return (super.onKeyDown(keyCode, event));
	}

	private void takePicture() {
		camera.takePicture(null, null, photoCallback);
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		
		public void surfaceCreated(SurfaceHolder holder) {
			Log.e("flashfeed.camera", "surfaceCreated");
			camera = Camera.open();
			try {
				camera.setDisplayOrientation(90);
				camera.setPreviewDisplay(previewHolder);
			} catch (Throwable t) {
				Log.e("Photographer", "Exception in setPreviewDisplay()", t);
				Toast.makeText(CameraActivity.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Log.e("flashfeed.camera", "surfaceChanged");
			Log.e("flashfeed.camera", "surfaceChanged: {width=" + width
					+ ", height=" + height + "}");

			if (mPreviewRunning) {
				camera.stopPreview();
				mPreviewRunning = false;
			}

			/*
			 * Toast .makeText(CameraActivity.this,
			 * String.format("(surfaceChanged) width: %d, height: %d", width,
			 * height), Toast.LENGTH_LONG) .show();
			 */

			Camera.Parameters parameters = camera.getParameters();

			List<Size> supportedPreviewSizes = parameters
					.getSupportedPreviewSizes();
			List<Size> supportedPictureSizes = parameters
					.getSupportedPictureSizes();

			Size optimizedPreviewSize, optimizedPictureSize;

			optimizedPreviewSize = getOptimalPreviewSize(supportedPreviewSizes, height, width);
			optimizedPictureSize = getOptimalPictureSize(supportedPictureSizes, height, width);

			Log.e("camera_test", String.format(
					"preview_width: %d, preview_height: %d",
					optimizedPreviewSize.width, optimizedPreviewSize.height));
			/*
			 * Toast .makeText(CameraActivity.this,
			 * String.format("preview_width: %d, preview_height: %d",
			 * optimizedPreviewSize.width, optimizedPreviewSize.height),
			 * Toast.LENGTH_LONG) .show();
			 */

			parameters.setPreviewSize(optimizedPreviewSize.width, optimizedPreviewSize.height);
			parameters.setPictureSize(optimizedPictureSize.width, optimizedPictureSize.height);
			parameters.setPictureFormat(PixelFormat.JPEG);
			parameters.setRotation(90);

			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

			camera.setParameters(parameters);
			camera.startPreview();
			mPreviewRunning = true;
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.e("camera_test", "surfaceDestroyed");
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	};
	Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			mOrientationEventListener.disable();
			showPicture(data);
		}
	};

	void showPicture(byte[] data) {
		if (data != null) {
			Bitmap picture = BitmapFactory
					.decodeByteArray(data, 0, data.length);

			Log.e("flashfeed.camera", String.format("width: %d, height: %d",
					picture.getWidth(), picture.getHeight()));

			int width = picture.getWidth();
			int height = picture.getHeight();

			boolean isOutputPortrait = width < height;

			int targetDim = 600;
			//int scaleToDim = targetDim + (cameraSideBorderLength*2);
			int scaleToDim = (int)(targetDim / (1-2*cameraBorderRatio));
			int longDim = 0, shortDim = 0;
			int x = 0, y = 0;

			if (isOutputPortrait) {
				longDim = height;
				shortDim = width;
			} else {
				longDim = width;
				shortDim = height;
			}

			float scaleFactor = (float) scaleToDim / shortDim;
			int scaledLongDim = (int) (longDim * scaleFactor);
			int offset = (int) (scaledLongDim * topOverlayRatio);
			
			if (mOrientation == Surface.ROTATION_0) {
				x = (int) (cameraBorderRatio * scaleToDim);
				y = offset;
			} 
			else if (mOrientation == Surface.ROTATION_90) {
				x = offset;
				y = (int) (cameraBorderRatio * scaleToDim);
			}
			else if (mOrientation == Surface.ROTATION_180) {
				x = (int) (cameraBorderRatio * scaleToDim);
				y = scaledLongDim - targetDim - offset;
			}
			else if (mOrientation == Surface.ROTATION_270) {
				x = scaledLongDim - targetDim - offset;;
				y = (int) (cameraBorderRatio * scaleToDim);
			}

			Log.e("flashfeed.camera", String.format(
					"(showPicture) w: %d, h: %d, x: %d", width, height,
					(scaledLongDim - scaleToDim) / 2));
			Log.e("flashfeed.camera", String.format(
					"(scaled) scaleFactor: %f, scaledHeight %f, (int): %d",
					scaleFactor, (width * scaleFactor),
					(int) (width * scaleFactor)));
			Log.e("flashfeed.camera", "saving bitmaps");
			/*Log.e("flashfeed.camera", String.format(
					"(x,y)=(%d,%d), targetHeight=%d", 0,
					(scaledLongDim - targetDim) / 2, targetDim));*/
			Log.e("flashfeed.camera", String.format(
					"(x,y)=(%d,%d), targetHeight=%d", x, y, scaleToDim));

			Matrix matrix = new Matrix();
			matrix.preScale(scaleFactor, scaleFactor);
			if (mOrientation == Surface.ROTATION_0) {
				if (!isOutputPortrait)
					matrix.postRotate(90);
			} else if (mOrientation == Surface.ROTATION_90) {
				if (isOutputPortrait)
					matrix.postRotate(270);
			} else if (mOrientation == Surface.ROTATION_180) {
				if (isOutputPortrait)
					matrix.postRotate(180);
				else
					matrix.postRotate(270);
			} else if (mOrientation == Surface.ROTATION_270) {
				if (isOutputPortrait)
					matrix.postRotate(90);
				else
					matrix.postRotate(180);
			}
			
			//scale and rotate (if needed) image first
			Bitmap targetBitmap = Bitmap.createBitmap(picture, 0, 0, width, height, matrix, true);
			//cut out the rest of the image making image a square
			Bitmap finalBitmap = Bitmap.createBitmap(targetBitmap, x, y, targetDim, targetDim);

			Address addr = getAddress();
			/*String currentAddr = "";
			if (addr != null) {
				currentAddr = String
						.format("Address: %s\nLocality: %s\nAdmin: %s\nPostal: %s\nCountry: %s\nFeature: %s\nPremises: %s",
								addr.getAddressLine(0), addr.getLocality(),
								addr.getAdminArea(), addr.getPostalCode(),
								addr.getCountryCode(), addr.getFeatureName(),
								addr.getPremises());
			}*/

			File myDir = new File("/sdcard/fichey_images");
			myDir.mkdirs();
			String username = "user1";

			SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyyHHmmss");
			String fname = username + formatter.format(new Date()) + ".jpg";

			File file = new File(myDir, fname);
			if (file.exists())
				file.delete();
			String fullpath = file.getAbsolutePath();
			FileOutputStream fos = null;
			
			try {				
				fos = new FileOutputStream(file);
				finalBitmap.compress(CompressFormat.JPEG, 75, fos);

				fos.close();
			} catch (Throwable ex) {
				
			}
			picture.recycle();
			targetBitmap.recycle();
			finalBitmap.recycle();

			/* bundle photo info and send to the confirm activity */
			Bundle bundle = new Bundle();
			if (fullpath != null) {
				bundle.putString("fullpathSkewed", fullpath);
				bundle.putParcelable("photoAddress", addr);
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
		/*Toast.makeText(CameraActivity.this,
		 "orientation change",Toast.LENGTH_LONG).show();*/

		//setCameraOverlay();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		/*Toast.makeText(CameraActivity.this, "onResume", Toast.LENGTH_LONG)
				.show();*/
		if (mOrientationEventListener.canDetectOrientation()) {
			mOrientationEventListener.enable();
		}
		locMgr.requestLocationUpdates(locProvider, 0, 0, onLocationChange);
		// mSensorMgr.registerListener(mSensorEventListener, mAccelerometer,
		// SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		/*Toast.makeText(CameraActivity.this, "onPause", Toast.LENGTH_LONG)
				.show();*/
		mOrientationEventListener.disable();
		locMgr.removeUpdates(onLocationChange);
		mHandler.removeCallbacks(mUpdateTimeTask);
		// mSensorMgr.unregisterListener(mSensorEventListener);
	}

	protected void onDestroy() {
		super.onDestroy();
		/*Toast.makeText(CameraActivity.this, "onDestroy", Toast.LENGTH_LONG)
				.show();*/
		mOrientationEventListener.disable();
		locMgr.removeUpdates(onLocationChange);
		mHandler.removeCallbacks(mUpdateTimeTask);
		mPreviewRunning = false;
	}

	/*******************************************************************/
	/** Helper functions **/
	/*******************************************************************/
	
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
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		// Try to find a size that matches the aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
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

		// If cannot find a match for the target ratio, take the closest height
		// match
		if (optimalSize == null) {
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
		Location loc = locMgr.getLastKnownLocation(locProvider);

		if (loc == null) {
			Toast.makeText(this, "No location available", Toast.LENGTH_SHORT)
					.show();
			return null;
		} else {
			Log.e("flashfeed.camera",
					String.format("L:%f,%f", loc.getLatitude(),
							loc.getLongitude()));
			Geocoder gc = new Geocoder(this);
			try {
				List<Address> addresses = gc.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
				Address addr = null;
				if (addresses != null) {
					addr = addresses.get(0);
					if (addr != null) {
						/*Toast.makeText(
								this,
								String.format(
										"Address: %s\nLocality: %s\nAdmin: %s\nPostal: %s\nCountry: %s\nFeature: %s\nPremises: %s",
										addr.getAddressLine(0),
										addr.getLocality(),
										addr.getAdminArea(),
										addr.getPostalCode(),
										addr.getCountryCode(),
										addr.getFeatureName(),
										addr.getPremises()), Toast.LENGTH_LONG)
								.show();*/
						return addr;
					}
				}
				/*Toast.makeText(this, String.format("No address found"),
						Toast.LENGTH_LONG).show();*/
				return null;
			} catch (IOException ex) {
				Log.e("flashfeed.camera", "error finding list of addresses");
				return null;
			}
		}
	}
}
