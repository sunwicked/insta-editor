package com.insta.editor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public abstract class BaseActivity extends Activity {

	Bitmap bitmap ;
	Bitmap circleBitmap ;
	Bitmap setImg;
	Bitmap bmp ;
	
 @Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	  Context mContext = this.getApplicationContext();
	  }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			System.gc();
			 if(bitmap!=null)
		  		  bitmap.recycle();
		  	  if(circleBitmap!=null)
		  		  circleBitmap.recycle();
		  	  if(setImg!=null)
		  		  setImg.recycle();
		  	  if(bmp!=null)
		  		  bmp.recycle();
		}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.rate_us:
				rateUs();
				return true;
			default:
				return false;
		}
	}

@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
}
	public boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivity = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {

            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null) {

                for (int i = 0; i < info.length; i++) {

                    Log.w("INTERNET:", String.valueOf(i));

                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                        Log.w("INTERNET:", "connected!");

                        return true;

                    }

                }

            }

        }

        return false;

    }
	
	
	private void rateUs() {
		
	    if (isNetworkAvailable(this)) {
			Uri uri = Uri.parse("market://details?id=" + getPackageName());
			Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
			try {
				startActivity(goToMarket);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, "Unable to Connect", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

}
