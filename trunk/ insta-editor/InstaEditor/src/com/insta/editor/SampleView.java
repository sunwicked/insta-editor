package com.insta.editor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SampleView extends ImageView 
{
    private Paint mPaint;
    private Paint mPaint2;

    private Bitmap mBitmap, copyBmp;
    ColorMatrix cm,ocm,adjMat, effMat;
    ColorMatrixColorFilter cmf,ocmf;
    boolean old = false;

    private float r_lum = 0.212671F;
    private float g_lum = 0.715160F;
    private float b_lum = 0.072169F;

    Context mContext;
    float[] mMat = new float[] {
                    0,0,0,0,0,
                    0,0,0,0,0,
                    0,0,0,0,0,
                    0,0,0,0,0
    };
    
    float[] brightMat = new float[] {
            1,0,0,0,0,
            0,1,0,0,0,
            0,0,1,0,0,
            0,0,0,1,0
    };
    
    float[] contMat = new float[] {
            1,0,0,0,0,
            0,1,0,0,0,
            0,0,1,0,0,
            0,0,0,1,0
    };
    
    float[] satMat = new float[] {
            1,0,0,0,0,
            0,1,0,0,0,
            0,0,1,0,0,
            0,0,0,1,0
    };
    
    ColorMatrix sCm, cCm, bCm;
    
    TileMode[] shaderStyle =
    {
            TileMode.MIRROR,
            TileMode.REPEAT,
            TileMode.CLAMP
    };

    boolean isShaderOn = false;
    //boolean isAdjustOn = false;
    private int a = 1, r = 1, g = 1, b = 1;
    private int aOffset = 0, rOffset = 0, gOffset = 0, bOffset = 0;
    
    //Bitmap bm;
    //Canvas mCanvas = new Canvas();
    
    public SampleView(Context context) {
        super(context);
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        cm = new ColorMatrix();
        cmf = new ColorMatrixColorFilter(cm);
        
        sCm = new ColorMatrix();
        cCm = new ColorMatrix();
        bCm = new ColorMatrix();
        
        adjMat = new ColorMatrix();
        effMat = new ColorMatrix();
        
        //ocm = new ColorMatrix();
        setDrawingCacheEnabled(true);
    }  

    public SampleView (Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        cm = new ColorMatrix();
        cmf = new ColorMatrixColorFilter(cm);
        
        sCm = new ColorMatrix();
        cCm = new ColorMatrix();
        bCm = new ColorMatrix();
        //ocm = new ColorMatrix();
        //ocmf = new ColorMatrixColorFilter(ocm);
        setDrawingCacheEnabled(true);
    }

    public SampleView (Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        cm = new ColorMatrix();
        cmf = new ColorMatrixColorFilter(cm);
        
        sCm = new ColorMatrix();
        cCm = new ColorMatrix();
        bCm = new ColorMatrix();
        //ocm = new ColorMatrix();
        //ocmf = new ColorMatrixColorFilter(ocm);
        setDrawingCacheEnabled(true);
    }

    public void setNormal()
    {
        isShaderOn = false;
        mMat = new float[] {
                1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 0, 1, 0 };
        
        cm.set(mMat);
        cmf = new ColorMatrixColorFilter(cm);
        invalidate();
    }

    public void setPolaroid()
    {
        isShaderOn = false;
        mMat = new float[] {
                1.438f, -0.062f, -0.062f, 0, 10,
                -0.122f, 1.378f, -0.122f, 0, 15,
                -0.016f, -0.016f, 1.483f, 0, 8,
                0, 0, 0, 1, 0 };
        cm.set(mMat);
        cmf = new ColorMatrixColorFilter(cm);

        invalidate();
    }

    public void setGrayScale()
    {
        isShaderOn = false;
        mMat = new float[] {
                r_lum, g_lum, b_lum, 0, 10,
                r_lum, g_lum, b_lum, 0, 10,
                r_lum, g_lum, b_lum, 0, 10,
                0, 0, 0, 1, 0 };
        cm.set(mMat);

        cmf = new ColorMatrixColorFilter(cm);
        invalidate();
    }

    public void setInvert()
    {
        isShaderOn = false;
        mMat = new float[] {
                -1, 0, 0, 0, 255,
                0, -1, 0, 0, 255,
                0, 0, -1, 0, 255,
                0, 0, 0, 1, 0 };
        cm.set(mMat);
        cmf = new ColorMatrixColorFilter(cm);
        invalidate();
    }

    public void setSepia()
    {
        isShaderOn = false;
        /*mMat = new float[] {
                0.393f, 0.349f, 0.272f, 0, 50,
                0.769f, 0.686f, 0.534f, 0, -50,
                0.189f, 0.168f, 0.131f, 0, 50,
                0, 0, 0, 1, 0 };*/
        mMat = new float[] {
                0.3588f, 0.7044f, 0.1368f, 0, 0,
                0.2990f, 0.5870f, 0.1140f, 0, 0,
                0.2392f, 0.4696f, 0.0912f ,0, 0,
                0,0,0,1,0
        };
        
        cm.set(mMat);
        cmf = new ColorMatrixColorFilter(cm);
        invalidate();
    }

    public void setBGR()
    {
        isShaderOn = false;
        mMat = new float[] {
                0, 0, 1, 0, 0,
                0, 1, 0, 0, 0,
                1, 0, 0, 0, 0,
                0, 0, 0, 1, 0 };
        cm.set(mMat);
        cmf = new ColorMatrixColorFilter(cm);
        invalidate();
    }

    public void setRed()
    {
        isShaderOn = false;
        cm.set(new float[] {
                1, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 1, 0 });
        cmf = new ColorMatrixColorFilter(cm);
        invalidate();
    }

    public void setGreen()
    {
        isShaderOn = false;
        cm.set(new float[] {
                0, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 1, 0 });
        cmf = new ColorMatrixColorFilter(cm);
        invalidate();
    }

    public void setBlue()
    {
        isShaderOn = false;
        cm.set(new float[] {
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 0, 1, 50 });
        cmf = new ColorMatrixColorFilter(cm);
        invalidate();
    }

    public void setBlaze(int amount)
    {
        isShaderOn = false;
        float s = ((float)amount)/100f;
        float is = 1-s;
        
        float ir_lum = is * r_lum;
        float ig_lum = is * g_lum;
        float ib_lum = is * b_lum;
        
        cm.set(new float[] {
                ir_lum+s, ig_lum, ib_lum, 0, 20,
                ir_lum, ig_lum+s, ib_lum, 0, -30,
                ir_lum, ig_lum, ib_lum+s, 0, -100,
                0, 0, 0, 1, 0 });
        cmf = new ColorMatrixColorFilter(cm);
        invalidate();
    }
    
    public void setCold()
    {
        isShaderOn = false;
        cm.set(new float[] {
                1, 0, 0, 0, -80,
                0, 1, 0, 0, -20,
                0, 0, 1, 0, 0,
                0, 0, 0, 1, 0 });
        cmf = new ColorMatrixColorFilter(cm);
        invalidate();
    }
    
    public void lomoEffectYellow()
    {
        isShaderOn = true;
        shader((float)mBitmap.getWidth()/2, (float)mBitmap.getHeight()/2, (float)mBitmap.getHeight()/2+50, 0x10fcfc0f, 0xef000000);
    }

    public void lomoEffectRed()
    {
        isShaderOn = true;
        shader((float)mBitmap.getWidth()/2, (float)mBitmap.getHeight()/2, (float)mBitmap.getHeight()/2+50, 0x10ec0f0f, 0xef000000);
    }
    
    public void lomoEffectGreen()
    {
        isShaderOn = true;
        shader((float)mBitmap.getWidth()/2, (float)mBitmap.getHeight()/2, (float)mBitmap.getHeight()/2+50, 0x100fec0f, 0xff000000);
    }
    
    public void lomoEffectBlue()
    {
        isShaderOn = true;
        shader((float)mBitmap.getWidth()/2, (float)mBitmap.getHeight()/2, (float)mBitmap.getHeight()/2+50, 0x100f0fec, 0xff000000);
    }
    
    public void lomoEffectGray()
    {
        isShaderOn = true;
        shader((float)mBitmap.getWidth()/2, (float)mBitmap.getHeight()/2, (float)mBitmap.getHeight()/2+50, 0x10aeaeae, 0xff000000);
    }

    public void lomoEffectWhite()
    {
        isShaderOn = true;
        shader((float)mBitmap.getWidth()/2, (float)mBitmap.getHeight()/2, (float)mBitmap.getHeight()/2+50, 0x00000000, 0xff000000);
    }
    
    public void shader(float x, float y, float r, int startColor, int endColor)
    {
        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_OVER));
        mPaint2.setShader(new RadialGradient(x, y, r, startColor, endColor, shaderStyle[2]));
        //paint.setMaskFilter(new BlurMaskFilter(50F, blurStyles[2]));
        invalidate();
    }

    public void setSaturation(int amount)
    {
        isShaderOn = false;
        //isAdjustOn = true;
        float s = ((float)amount)/100f;
        float is = 1-s;
        
        float ir_lum = is * r_lum;
        float ig_lum = is * g_lum;
        float ib_lum = is * b_lum;

        satMat = new float[] {
                ir_lum+s, ig_lum, ib_lum, 0, 0,
                ir_lum, ig_lum+s, ib_lum, 0, 0,
                ir_lum, ig_lum, ib_lum+s, 0, 0,
                0, 0, 0, 1, 0 };
        sCm.set(satMat);
        /*for(int i=0;i<mMat.length;i++)
        {
            satMat[i] = satMat[i] + brightMat[i] + contMat[i];
            satMat[i] = clamp(satMat[i]);
        }*/
        
        ColorMatrix tempCM = new ColorMatrix();
        tempCM.set(contMat);
        ColorMatrix tempBM = new ColorMatrix();
        tempBM.set(brightMat);
        tempCM.setConcat(tempCM, tempBM);
        
        sCm.setConcat(tempCM, sCm);
        cmf = new ColorMatrixColorFilter(sCm);
        /*
        Paint p = new Paint();
        p.setColorFilter(ocmf);
        mCanvas.drawBitmap(mBitmap, 0, 0, p);*/
        invalidate();
    }
    
    public void setContrast(float contrast)
    {
        isShaderOn = false;
        float scale = contrast + 1.f;
        float translate = (-.5f * scale + .5f) * 255.f;
        
        contMat = new float[] {
                scale, 0, 0, 0, translate,
                0, scale, 0, 0, translate,
                0, 0, scale, 0, translate,
                0, 0, 0, 1, 0 };
        cCm.set(contMat);
        /*for(int i=0;i<mMat.length;i++)
        {
            contMat[i] = contMat[i] + brightMat[i] + satMat[i];
            contMat[i] = clamp(contMat[i]);
        }*/
        ColorMatrix tempSM = new ColorMatrix();
        tempSM.set(satMat);
        ColorMatrix tempBM = new ColorMatrix();
        tempBM.set(brightMat);
        tempSM.setConcat(tempSM, tempBM);
        
        cCm.setConcat(tempSM, cCm);
        cmf = new ColorMatrixColorFilter(cCm);
        invalidate();
    }
    
    
    public void setBrightness(int translate)
    {
        isShaderOn = false;

        brightMat = new float[] {
                1, 0, 0, 0, translate,
                0, 1, 0, 0, translate,
                0, 0, 1, 0, translate,
                0, 0, 0, 1, 0 };
        
        bCm.set(brightMat);
        
        ColorMatrix tempSM = new ColorMatrix();
        tempSM.set(satMat);
        ColorMatrix tempCM = new ColorMatrix();
        tempCM.set(contMat);
        tempSM.setConcat(tempSM, tempCM);
        
        bCm.setConcat(tempSM, bCm);
        cmf = new ColorMatrixColorFilter(bCm);
        invalidate();
    }

    @Override 
    protected void onDraw(Canvas canvas)
    {
        Paint paint = mPaint;
        paint.setColorFilter(cmf);
        if(mBitmap != null)
        {
            int offsetW = 0;
            int offsetH = 0;
            if(EditActivity.offsetW > 0)
            {
                //offsetW = EditActivity.offsetW;
            }
            if(EditActivity.offsetH > 0)
            {
                //offsetH = EditActivity.offsetH;
            }
            if(isShaderOn)
            {
                canvas.drawBitmap(mBitmap, offsetW, offsetH, null);
                canvas.drawRect(offsetW, offsetH, mBitmap.getWidth(), mBitmap.getHeight(), mPaint2);
            }
            else
            {
                canvas.drawBitmap(mBitmap, offsetW, offsetH, paint); 
            }
        }
    }

    @Override
    public void setImageBitmap (Bitmap bmp)
    {
        mBitmap = bmp;
        //tempBmp = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap.getConfig());
        //mCanvas = new Canvas(mBitmap);
        super.setImageBitmap(bmp);
    }

    public Bitmap getBitmap()
    {
        return mBitmap;
    }
    
    public void onUndo()
    {
        
    }
    
    public void saveBitmap(String path)
    {
        new SaveBitmap(path).execute();
    }

    class SaveBitmap extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pd;
        String path;

        public SaveBitmap (String path)
        {
            this.path = path;
        }

        @Override
        protected void onPreExecute ( )
        {
            pd = new ProgressDialog(mContext);
            pd.setMessage("Saving Image...");
            pd.show();
        }

        @Override
        protected Void doInBackground (Void... params)
        {
            try
            {
                FileOutputStream fout = new FileOutputStream(path);
                getDrawingCache().compress(CompressFormat.JPEG, 100, fout);
                /*fout = new FileOutputStream("/mnt/sdcard/myPic.jpg");
                tempBmp.compress(CompressFormat.JPEG, 100, fout);*/
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result)
        {
            pd.dismiss();
        }

    }
    
    public float clamp(float c) {
        if (c > 255)
            return 255;
        return c;
    }
}
