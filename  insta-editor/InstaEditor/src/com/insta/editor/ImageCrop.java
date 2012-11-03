package com.insta.editor;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;


public class ImageCrop 
{
    private int aspectX, aspectY;
    private int outputX, outputY;
    private boolean returnData = false;
    private boolean circleCrop = false;
    private boolean scale = true;
    private boolean faceDetection = true;
    protected int PHOTO_PICKED;
    private Activity actContext;

    private String filePath;

    public ImageCrop(Activity context, int key)
    {
        actContext   = context;
        PHOTO_PICKED = key;
    }

    
    public void setOutputDimension(int aspX, int aspY, int outX, int outY)
    {
        aspectX = aspX;
        aspectY = aspY;
        outputX = outX;
        outputY = outY;
    }
    
    public void setOutputFormat(boolean retData, boolean cirCrop, boolean scale)
    {
        returnData = retData;
        circleCrop = cirCrop;
        this.scale = scale;
    }
    
    
    public void getCroppedImagePath(String path)
    {
        filePath = path;
        try 
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
            intent.putExtra("outputX", outputX);    
            intent.putExtra("outputY", outputY);
            intent.putExtra("scale", scale);
            intent.putExtra("return-data", returnData);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection",!faceDetection);
            if (circleCrop)
            {
                intent.putExtra("circleCrop", true);
            }
            
            actContext.startActivityForResult(intent, PHOTO_PICKED);
        }
        catch(ActivityNotFoundException e)
        {
            e.printStackTrace();
            //Toast.makeText(actContext, "no app to handle crop", Toast.LENGTH_LONG).show();
        }

    }

    private Uri getTempUri()
    {
        return Uri.fromFile(getTempFile());
    }

    
    private File getTempFile()
    {
        if (isSDCARDMounted())
        {
            File f = new File(filePath);
            try
            {
                f.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                //Toast.makeText(actContext, "I/O exception", Toast.LENGTH_LONG).show();
            }
            return f;
        } 
        else 
        {
            return null;
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
