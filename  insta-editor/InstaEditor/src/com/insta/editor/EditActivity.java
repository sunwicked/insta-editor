package com.insta.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class EditActivity extends Activity implements OnItemClickListener, OnSeekBarChangeListener{

    private static final int PIC_CROP = 0;
    private Gallery mGallery;
    private MyGalleryAdapter mAdapter;
    private SampleView mainImage;
    private RelativeLayout rel, seekRel;
    private SeekBar satSeekCont, contSeekCont, brightSeekCont;
    private ImageView effectImage;
    private int MAX_HEIGHT;
    private int MAX_WIDTH;
    private int degrees = 0;

    public static int offsetW;
    public static int offsetH;
    public static int selected = -1;

    private String path;
    //new file path for gallery saved image
    private File filePath;
    private int modeOfCapture;
    private boolean isSaved = false;

    enum seekIdEnum{
        saturation,
        contrast,
        brightness
    };

    private seekIdEnum seekId;
    private Transformation transform;

    int[] colorsHue =
    {
        0xFFFF0000,
        0XFFFF00FF,
        0XFF0000FF,
        0XFF00FFFF,
        0XFF00FF00,
        0XFFFFFF00,
        0xFFFF0000
    };
    
    int[] colorsSat = 
    {
            0xFFAEAEAE,
            0xFFFFFF00,
            0xFFFFA500   
    };
    
    int[] colorsBright = 
    {
         0xFF000000,
         0xFFFCFCFC,
         0xFFFFFFFF
    };
    
    int[] colorsContrast = 
    {
        0xFFFFFFFF,
        0xFFAEAEAE,
        0xFF000000
    };
    
    float unit = 255/40;
    
    int satProg = 100, contProg, brightProg;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /*
         * calculating the device dimensions
         */
        Display d     = getWindowManager().getDefaultDisplay();
        int displayWidth  = d.getWidth();
        int displayHeight = d.getHeight();
        MAX_WIDTH  = displayWidth - 20;
        float f = displayHeight/1.5f;
        MAX_HEIGHT = Math.round(f) - 20;

        /*
         * extracting the cropped image path
         */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        path   = bundle.getString("path");
        modeOfCapture = bundle.getInt("mode");

        setContentView(R.layout.activity_main);
        findMyViews();
        setListeners();

        //extracting the image from the path above
        new ExtractBitmap(path).execute();

        /*RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(MAX_WIDTH, MAX_HEIGHT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainImage.setLayoutParams(lp);*/
        transform = Transformation.Effects;
        mAdapter = new MyGalleryAdapter(this, transform);
        mGallery.setAdapter(mAdapter);
        mGallery.setSelection(1);

        ShapeDrawable sd = new ShapeDrawable();
        sd.setShape(new Shape() {
            
            @Override
            public void draw (Canvas canvas, Paint paint)
            {
               /* int x1 = (int) satSeekControl.getLeft();
                int y1 = (int) satSeekControl.getTop();
                
                int x2 = satSeekControl.getRight();
                int y2 = satSeekControl.getBottom();*/
                
                paint.setStyle(Style.FILL_AND_STROKE);
                paint.setStrokeWidth(40);
                
                if(seekId == seekIdEnum.saturation)
                {
                    paint.setShader(new LinearGradient(0,40, satSeekCont.getWidth(),
                            40, colorsSat, null, TileMode.CLAMP));
                    canvas.drawLine(0,-15, satSeekCont.getWidth(),-15, paint);
                }
                else if(seekId == seekIdEnum.contrast)
                {
                    paint.setShader(new LinearGradient(0,40, contSeekCont.getWidth(),
                            40, colorsContrast, null, TileMode.CLAMP));
                    canvas.drawLine(0,-15, contSeekCont.getWidth(),-15, paint);
                }
                else if(seekId == seekIdEnum.brightness)
                {
                    paint.setShader(new LinearGradient(0,40, brightSeekCont.getWidth(),
                            40, colorsBright, null, TileMode.CLAMP));
                    canvas.drawLine(0,-15, brightSeekCont.getWidth(),-15, paint);
                }
                
                
            }
        });
        
        satSeekCont.setProgressDrawable(sd);
        contSeekCont.setProgressDrawable(sd);
        brightSeekCont.setProgressDrawable(sd);
    }

    public void findMyViews()
    {
        mGallery    = (Gallery)findViewById(R.id.gallery);
        mainImage   = (SampleView)findViewById(R.id.mainImage);
        rel         = (RelativeLayout)findViewById(R.id.picLayout);
        seekRel     = (RelativeLayout)findViewById(R.id.seekRel);
        satSeekCont = (SeekBar)findViewById(R.id.satSeekCont);
        contSeekCont = (SeekBar)findViewById(R.id.contSeekCont);
        brightSeekCont = (SeekBar)findViewById(R.id.brightSeekCont);
        effectImage       = (ImageView)findViewById(R.id.effectImage);  
    }

    private void setListeners()
    {
        mGallery.setOnItemClickListener(this);
        satSeekCont.setOnSeekBarChangeListener(this);
        contSeekCont.setOnSeekBarChangeListener(this);
        brightSeekCont.setOnSeekBarChangeListener(this);
    }

    public void initSeekBar()
    {
        switch(seekId)
        {
            case saturation:
                contSeekCont.setVisibility(View.GONE);
                brightSeekCont.setVisibility(View.GONE);
                satSeekCont.setVisibility(View.VISIBLE);
                
                satSeekCont.setProgress(satProg);
                satSeekCont.setMax(400);
                //seekControl.incrementProgressBy(satProg);
                
                //satSeekCont.invalidate();
                break;
            case contrast:
                brightSeekCont.setVisibility(View.GONE);
                satSeekCont.setVisibility(View.GONE);
                contSeekCont.setVisibility(View.VISIBLE);
                
                contSeekCont.setProgress(contProg);
                contSeekCont.setMax(50);
                //seekControl.incrementProgressBy(contProg);
                //contSeekCont.invalidate();
                break;
            case brightness:
                contSeekCont.setVisibility(View.GONE);
                satSeekCont.setVisibility(View.GONE);
                brightSeekCont.setVisibility(View.VISIBLE);
                
                brightSeekCont.setProgress(brightProg);
                brightSeekCont.setMax(100);
                //seekControl.incrementProgressBy(brightProg);
                
                break;
        }

    }

    public void closeSeekControl(View v)
    {
        mGallery.setVisibility(View.VISIBLE);
        seekRel.setVisibility(View.GONE);
    }

    /*****************Seek bar methods*****************************/
    @Override  
    public void onProgressChanged (SeekBar seekBar, int progress,
            boolean fromUser)
    {
        switch(seekId)
        {
            case saturation:
                satProg = progress;
                mainImage.setSaturation(progress);
                break;
            case contrast:
                contProg = progress;
                float prog = (float)progress/10f;
                mainImage.setContrast(prog);
                break;
            case brightness:
                brightProg = progress;
                mainImage.setBrightness(progress-50);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch (SeekBar seekBar)
    {
    }

    @Override
    public void onStopTrackingTouch (SeekBar seekBar)
    {
    }
    /********************************************************/

    /*****************Gallery methods*****************************/
    @Override
    public void onItemClick (AdapterView<?> parent, View v, int position, long Id)
    {
        selected = position;
        //view.findViewById(R.id.galItemRoot).setBackgroundResource(R.drawable.selection_back);
        mAdapter.notifyDataSetChanged();
        if(transform == Transformation.Effects)
        {
            switch(position)
            {
                case 0:
                    mainImage.setBGR();
                    break;
                case 1:
                    mainImage.setSepia();
                    break;
                case 2:
                    mainImage.setInvert();
                    break;
                case 3:
                    mainImage.setPolaroid();
                    break;
                case 4:
                    mainImage.setGrayScale();
                    break;
                case 5:
                    mainImage.lomoEffectYellow();
                    break;
                case 6:
                    mainImage.lomoEffectGray();
                    new ApplyFilter(10).execute();
                    break;
                case 7:
                    mainImage.lomoEffectWhite();
                    new ApplyFilter(10).execute();
                    break;
                case 8:
                    new ApplyFilter(10).execute();
                    break;
                case 9:
                    mainImage.setBlaze(110);
                    break;
                case 10:
                    mainImage.setCold();
                    break;
            }
        }
        else
        {
            Integer rId = (Integer) mAdapter.getItem(position);
            switch(position)
            {
                case 0:
                    seekId = seekIdEnum.saturation;
                    mGallery.setVisibility(View.GONE);
                    seekRel.setVisibility(View.VISIBLE);
                    initSeekBar();
                    effectImage.setBackgroundResource(rId);
                    break;
                case 1:
                    seekId = seekIdEnum.contrast;
                    mGallery.setVisibility(View.GONE);
                    seekRel.setVisibility(View.VISIBLE);
                    initSeekBar();
                    effectImage.setBackgroundResource(rId);
                    break;
                case 2:
                    seekId = seekIdEnum.brightness;
                    mGallery.setVisibility(View.GONE);
                    seekRel.setVisibility(View.VISIBLE);
                    initSeekBar();
                    effectImage.setBackgroundResource(rId);
                    break;
                case 3:
                    new ApplyFilter(50).execute();
                    break;
            }
        }

    }

    /*************************************************************/

    /**
     * 
     * @brief onRevert method
     * 
     * @param v
     * 
     * @detail removes all the image transformations
     */
    public void onRevert(View v)
    {
        mainImage.setNormal();
        selected = -1;
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 
     * @brief onRotate method
     * 
     * @param v
     * 
     * @detail rotates the image view
     */
    public void onRotate(View v)
    {
        RotateAnimation anim = new RotateAnimation(degrees, degrees+90, mainImage.getRight()/2, mainImage.getBottom()/2);
        anim.setDuration(500);
        anim.setFillEnabled(true);
        anim.setFillAfter(true);
        degrees += 90;
        degrees %= 360;
        mainImage.startAnimation(anim);
    }


    /**
     * 
     * @brief onSave method
     * 
     * @param v
     * 
     * @detail saves the bitmap
     */
    public void onSave(View v)
    {
        isSaved = true;
        //Toast.makeText(this, "saving...", Toast.LENGTH_SHORT).show();
        FileOutputStream fout = null;
        FileInputStream fin = null;

        switch(modeOfCapture)
        {
            //camera capture
            case 0:
                mainImage.saveBitmap(path);
                break;
                //gallery pick
            case 1:
                filePath = CaptureActivity.getOutputMediaFile(CaptureActivity.MEDIA_TYPE_IMAGE);
                try
                {
                    //copy the image data from the temporary file to a
                    //permanent file
                    fout = new FileOutputStream(filePath);
                    fin = new FileInputStream(path);

                    byte buffer[] = new byte[fin.available()];
                    while(fin.read(buffer) != -1)
                        fout.write(buffer);

                } catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                try
                {
                    if(fout != null)
                        fout.close();
                    if(fin != null)
                        fin.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                mainImage.saveBitmap(filePath.getAbsolutePath());
                break;
        }
    }

    public void onShare(View v)
    {
        Intent shareIntent;
        switch(modeOfCapture)
        {
            case 0:
                if(!isSaved)
                {
                    onSave(null);
                }
                shareIntent =  new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file://" + path));
                startActivity(Intent.createChooser(shareIntent, "Share image using"));
                break;
            case 1:
                if(!isSaved)
                {
                    onSave(null);
                }
                shareIntent =  new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file://" + filePath.getAbsolutePath()));
                startActivity(Intent.createChooser(shareIntent, "Share image using"));
                break;
        }

    }

    class ApplyFilter extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pd;
        Bitmap grainBmp;
        int amount;
        
        public ApplyFilter(int amount)
        {
            this.amount = amount;
        }
        
        @Override
        protected void onPreExecute ( )
        {
            pd = new ProgressDialog(EditActivity.this);
            pd.setMessage("Applying Effect...");
            pd.show();
        }

        @Override
        protected Void doInBackground (Void... params)
        {
            NoiseFilter NF = new NoiseFilter();
            NF.setAmount(amount);
            grainBmp = NF.addNoise(mainImage.getBitmap());
            return null;
        }

        @Override
        protected void onPostExecute (Void result)
        {
            mainImage.setImageBitmap(grainBmp);
            pd.dismiss();
        }

    }

    class ExtractBitmap extends AsyncTask<Void, Void, Void>
    {

        File pathF;
        String pathToFile;
        Bitmap bmp;
        ProgressDialog pd;

        public ExtractBitmap (String path)
        {
            //String sdPath = Environment.getExternalStorageDirectory().toString();
            pathToFile = path;
            pathF = new File(pathToFile);
        }

        @Override
        protected void onPreExecute ( )
        {
            pd = new ProgressDialog(EditActivity.this);
            pd.setMessage("Loading Image...");
            pd.show();
        }

        @Override
        protected Void doInBackground (Void... arg0)
        {
            bmp = extractBitmap(pathToFile);
            return null;
        }

        @Override
        protected void onPostExecute (Void result)
        {
            offsetW = (MAX_WIDTH - 20)- bmp.getWidth();
            offsetW /= 2;

            offsetH = (MAX_HEIGHT - 20)- bmp.getHeight();
            offsetH /= 2;

            mainImage.setImageBitmap(bmp);  
            pd.dismiss();
            //bmp.recycle();
            //bmp = null;
        }

    }

    public Bitmap extractBitmap(String pathToFile)
    {
        BitmapFactory.Options boundsOp = new BitmapFactory.Options();
        boundsOp.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToFile, boundsOp);

        if(boundsOp.outWidth == -1) 
        {
            Log.i("Error", "error");   
        }

        int width = boundsOp.outWidth;
        int height = boundsOp.outHeight;

        int inSampleSize = 1;

        int temp = Math.max(width, height);
        while(temp > 2*MAX_WIDTH)
        {
            inSampleSize *= 2;
            temp /= 2;
        }

        BitmapFactory.Options resample = new BitmapFactory.Options();
        resample.inSampleSize = inSampleSize;

        Bitmap bmp = BitmapFactory.decodeFile(pathToFile, resample);
        bmp = Bitmap.createScaledBitmap(bmp, MAX_WIDTH, MAX_HEIGHT, true);

        return bmp;
    }

    public void onEffect(View v)
    {
        /*if(transform != Transformation.Effects)
        {*/
            selected = -1;
            transform = Transformation.Effects;
            mGallery.setVisibility(View.VISIBLE);
            seekRel.setVisibility(View.GONE);
            mAdapter = new MyGalleryAdapter(this, transform);
            mGallery.setAdapter(mAdapter);
            mGallery.setSelection(1);
        //}
    }

    public void onAdjust(View v)
    {
        /*if(transform != Transformation.Adjustments)
        {*/
            selected = -1;
            transform = Transformation.Adjustments;
            mGallery.setVisibility(View.VISIBLE);
            seekRel.setVisibility(View.GONE);
            mAdapter = new MyGalleryAdapter(this, transform);
            mGallery.setAdapter(mAdapter);
            mGallery.setSelection(1);
        //}
    }

    public void onUndo(View v)
    {

    }

    @Override
    public void onBackPressed ( )
    {

        AlertDialog.Builder alertbox = new AlertDialog.Builder(EditActivity.this);

        alertbox.setMessage("  All Unsaved Changes Will Be Lost"); // Message to be displayed

        alertbox.setPositiveButton("Continue",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                EditActivity.super.onBackPressed();
            }

        });

        alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }

        });

        // show the alert box will be swapped by other code later
        alertbox.show();

    }

}
