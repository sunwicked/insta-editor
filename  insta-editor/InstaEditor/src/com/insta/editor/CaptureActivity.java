package com.insta.editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CaptureActivity extends BaseActivity {

	Constants ids = new Constants();

	public static final int MEDIA_TYPE_IMAGE = 1;

	/**
	 * this variable stores the integer value of camera that is sent via intent
	 * for startActivityForResult()
	 */
	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	/**
	 * this variable stores the integer value of gallery that is sent via intent
	 * for startActivityForResult()
	 */
	public static final int SELECT_PICTURE = 200;

	/**
	 * this variable stores the file uri of the image picked-up from gallery
	 */
	private Uri fileUri;

	public String picturePath;
	// //////////////////////////////////
	public String savedPaths[] = new String[1];
	public int savedIndex = 0;
	public static String tempImagePath;
	// //////////////////////////////////

	private int MAX_HEIGHT;
	private int MAX_WIDTH;
	private static final int PIC_CROP = 0;

	private ImageCrop mImageCrop;

	private int modeOfCapture;

	ImageView smImage;
	ImageView smImage2;
	ImageView smImage3;
	ImageView smImage4;
	TextView fontSet;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Display d = getWindowManager().getDefaultDisplay();
		int displayWidth = d.getWidth();
		int displayHeight = d.getHeight();

		MAX_WIDTH = displayWidth - 20;
		float f = displayHeight / 1.5f;
		MAX_HEIGHT = Math.round(f) - 20;

		mImageCrop = new ImageCrop(this, PIC_CROP);
		mImageCrop.setOutputDimension(1, 1, MAX_WIDTH, MAX_HEIGHT);
		mImageCrop.setOutputFormat(false, false, true);

		setContentView(R.layout.circle);

		smImage = (ImageView) findViewById(R.id.imageView1);
		smImage2 = (ImageView) findViewById(R.id.imageView2);
		smImage3 = (ImageView) findViewById(R.id.imageView3);
		smImage4 = (ImageView) findViewById(R.id.imageView6);
		fontSet = (TextView) findViewById(R.id.textView1);
		Typeface type = Typeface.createFromAsset(getAssets(), "Sachiko.ttf");
		fontSet.setTypeface(type);
		pathList = new ArrayList<String>();
		getPath = new AppPreference(this);

		smFunction();

		folderExist();

	}

	/**
	 * 
	 * @brief getOutputMediaFileUri method
	 * 
	 * @param type
	 * @return
	 * 
	 * @detail Create a file Uri for saving an image or video
	 */

	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	public void camPik(View v) {
		modeOfCapture = 0;
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to
		// save the image
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
		// name

		// start the image capture Intent
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	public void galleryPik(View v) {
		modeOfCapture = 1;
		// creating a temporary image file
		String pathExt = Environment.getExternalStorageDirectory().toString();
		File resolveMeSDCard = new File(pathExt
				+ "/Pictures/instaEditor/tempImage.jpg");
		if (!resolveMeSDCard.exists()) {
			try {
				resolveMeSDCard.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		tempImagePath = resolveMeSDCard.getAbsolutePath();

		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_PICK);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				SELECT_PICTURE);
	}

	public void folderExist() {

		String pathExt = Environment.getExternalStorageDirectory().toString();
		File resolveMeSDCard = new File(pathExt + "/Pictures/instaEditor");
		if (!resolveMeSDCard.exists()) {
			resolveMeSDCard.mkdirs();
		}

	}

	public static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {

			mediaStorageDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					"instaEditor");
		} else {
			mediaStorageDir = new File(
					Environment.getExternalStorageDirectory(), "instaEditor");
		}

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("instaEditor", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	/*
	 * onStartActivity for camera intent and browsing images from gallery
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		/*
		 * setting images from camera click intent
		 */
		try {
			switch (requestCode) {
			case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:

				if (resultCode == RESULT_OK) {
					crop(fileUri);
					getPath.savePath(fileUri.getEncodedPath());
				} else if (resultCode == RESULT_CANCELED) {
					// User cancelled the image capture
				} else {
					// Image capture failed, advise user
				}
				break;

			case PIC_CROP:
				if (resultCode == RESULT_OK) {
					launchEditScreen(fileUri.getPath());
				} else if (resultCode == RESULT_CANCELED) {
					// User cancelled the image capture
				} else {
					// Image capture failed, advise user
				}
				break;
			case SELECT_PICTURE:
				if (resultCode == RESULT_OK) {
					Uri selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					picturePath = cursor.getString(columnIndex);

					getPath.savePath(picturePath);

					Bitmap bmp = extractBitmap(picturePath);
					FileOutputStream fout = new FileOutputStream(tempImagePath);
					bmp.compress(CompressFormat.JPEG, 100, fout);

					bmp.recycle();
					bmp = null;

					fileUri = Uri.parse("file://" + tempImagePath);

					crop(fileUri);

					cursor.close();
				} else if (resultCode == RESULT_CANCELED) {
					// User cancelled the image capture
				} else {
					// Image capture failed, advise user
				}
				break;

			}

		} catch (Exception e) {
			Toast.makeText(CaptureActivity.this, "Unable to load image !!",
					Toast.LENGTH_SHORT).show();
		}

	}

	/*
	 * Method for launching next screen putting in the path from gallery
	 */

	private void launchEditScreen(String path) {
		Intent launchEdit = new Intent(CaptureActivity.this, EditActivity.class);
		Bundle pathBundle = new Bundle();
		pathBundle.putString("path", path);
		pathBundle.putInt("mode", modeOfCapture);
		launchEdit.putExtras(pathBundle);
		Log.d("path", path);
		startActivity(launchEdit);

	}

	public void crop(Uri picUri) {

		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		// indicate image type and Uri
		cropIntent.setDataAndType(picUri, "image/*");
		// set crop properties
		cropIntent.putExtra("crop", "true");
		// indicate aspect of desired crop
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		// indicate output X and Y
		cropIntent.putExtra("outputX", MAX_WIDTH);
		cropIntent.putExtra("outputY", MAX_HEIGHT);
		// retrieve data on return
		cropIntent.putExtra("return-data", false);
		cropIntent.putExtra("scale", true);
		cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
		cropIntent.putExtra("outputFormat",
				Bitmap.CompressFormat.JPEG.toString());

		// start the activity - we handle returning in onActivityResult
		startActivityForResult(cropIntent, PIC_CROP);
	}

	public Bitmap extractBitmap(String pathToFile) {
		BitmapFactory.Options boundsOp = new BitmapFactory.Options();

		boundsOp.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathToFile, boundsOp);

		if (boundsOp.outWidth == -1) {
			Log.i("Error", "error");
		}

		int width = boundsOp.outWidth;
		int height = boundsOp.outHeight;

		int inSampleSize = 1;

		int temp = Math.max(width, height);

		while (temp > 2 * MAX_WIDTH) {
			inSampleSize *= 2;
			temp /= 2;
		}

		BitmapFactory.Options resample = new BitmapFactory.Options();
		resample.inSampleSize = inSampleSize;

		bmp = BitmapFactory.decodeFile(pathToFile);
		bmp = Bitmap.createScaledBitmap(bmp, MAX_WIDTH, MAX_HEIGHT, true);

		return bmp;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();

		if (tempImagePath != null) {
			File f = new File(tempImagePath);
			if (f.exists()) {
				f.delete();
			}
		}

	}

	AppPreference getPath;
	ArrayList<String> pathList;

	private void smFunction() {

		String path;

		pathList = getPath.getPathList();
		if (!pathList.isEmpty()) {
			if (pathList.size() > 0) {
				path = pathList.get(0);
				setImg = generateBitmap(path, 1);
			} else
				setImg = generateBitmap(" ", 0);
			// setImg=setImg.createScaledBitmap(setImg, 400, 400, true);
			smImage.setImageBitmap(setImg);
			if (pathList.size() > 1) {
				path = pathList.get(1);
				setImg = generateBitmap(path, 1);
			} else
				setImg = generateBitmap(" ", 0);
			// setImg=setImg.createScaledBitmap(setImg, 300, 300, true);
			smImage2.setImageBitmap(setImg);
			if (pathList.size() > 2) {
				path = pathList.get(2);
				setImg = generateBitmap(path, 1);
			} else
				setImg = generateBitmap(" ", 0);
			// setImg=setImg.createScaledBitmap(setImg, 250, 250, true);
			smImage3.setImageBitmap(setImg);
			if (pathList.size() > 3) {
				path = pathList.get(3);
				setImg = generateBitmap(path, 1);
			} else
				setImg = generateBitmap(" ", 0);
			// setImg=setImg.createScaledBitmap(setImg, 350, 350, true);
			smImage4.setImageBitmap(setImg);
		} else {
			setImg = generateBitmap(" ", 0);
			smImage.setImageBitmap(setImg);
			smImage3.setImageBitmap(setImg);
			smImage4.setImageBitmap(setImg);
			smImage2.setImageBitmap(setImg);
		}
	}

	private Bitmap generateBitmap(String path, int option) {
		// TODO Auto-generated method stub

		if (option == 0) {
			bitmap = BitmapFactory.decodeResource(this.getResources(),
					R.drawable.s1);
			bitmap = bitmap.createScaledBitmap(bitmap, 400, 400, true);
		} else {
			// bitmap = extractBitmap(path);
			bitmap = BitmapFactory.decodeFile(path);
			bitmap = bitmap.createScaledBitmap(bitmap, 400, 400, true);
		}
		circleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_4444);

		BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
				TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setShader(shader);

		Canvas c = new Canvas(circleBitmap);
		c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
				bitmap.getWidth() / 2, paint);
		return circleBitmap;
	}

	public void sendPic(View v) {

		switch (v.getId()) {
		case R.id.imageView1:
			path(0);

			break;
		case R.id.imageView2:
			path(1);

			break;
		case R.id.imageView3:
			path(2);
			break;
		case R.id.imageView6:
			path(3);
			break;

		default:
			break;
		}

	}

	public void path(int i) {
		String pathExt = Environment.getExternalStorageDirectory().toString();
		File resolveMeSDCard = new File(pathExt
				+ "/Pictures/instaEditor/tempImage.jpg");
		if (!resolveMeSDCard.exists()) {
			try {
				resolveMeSDCard.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		tempImagePath = resolveMeSDCard.getAbsolutePath();

		int chk = -1;
		if (!pathList.isEmpty()) {
			if (pathList.size() > i) {
				Bitmap bmp = extractBitmap(pathList.get(i));
				FileOutputStream fout = null;
				try {
					fout = new FileOutputStream(tempImagePath);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bmp.compress(CompressFormat.JPEG, 100, fout);

				bmp.recycle();
				bmp = null;

				fileUri = Uri.parse("file://" + tempImagePath);

				crop(fileUri);
				chk=0;
            
			}
			 
		} if(chk!=0) {
			File file = new File(pathExt+"/Pictures/instaEditor", "dog.png");
			if(isSDCARDMounted()){
			if (!file.exists()) {
				bmp = BitmapFactory.decodeResource(getResources(),
						R.drawable.s1);

				try {

			    	FileOutputStream outStream = new FileOutputStream(file);
					bmp.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
					outStream.flush();
					outStream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					}
			
			fileUri=Uri.parse("file://" +  file.getAbsolutePath());
				crop(fileUri);
		}
			else
				Toast.makeText(this	, "sdcardMounted", Toast.LENGTH_LONG).show();
		}
	}
	 private boolean isSDCARDMounted()
	    {
	        String status = Environment.getExternalStorageState();

	        if (status.equals(Environment.MEDIA_MOUNTED))
	            return true;
	        
	        return false;
	    }
}
