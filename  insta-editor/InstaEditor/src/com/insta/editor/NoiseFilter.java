package com.insta.editor;

import java.util.Random;

import android.graphics.Bitmap;


public class NoiseFilter {

    /*
     * Gaussian distribution for the noise
     */
    public static final int GAUSSIAN = 1;

    /*
     * Uniform distribution for the noise
     */
    public static final int UNIFORM = 1;

    /*
     * Amount of noise for each pixel
     */
    private int amount;

    /*
     * Mode of distribution 
     */
    private int distribution = UNIFORM;

    /*
     * For gray-scale image 
     */
    private boolean monochrome = false;

    /*
     * 
     */
    private float density = 1;

    /*
     * Random number generator
     */
    private Random ran = new Random();

    public NoiseFilter()
    {

    }

    /**
     * 
     * @brief setAmount method
     * 
     * @param amount
     * 
     * @detail sets the amount of noise for each pixel
     */
    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    /**
     * 
     * @brief getAmount method
     * 
     * @return
     * 
     * @detail returns the amount of noise
     */
    public int getAmount()
    {
        return amount;
    }

    /**
     * 
     * @brief setDistribution method
     * 
     * @param distribution
     * 
     * @detail sets the distribution of the noise
     */
    public void setDistribution(int distribution)
    {
        this.distribution = distribution;
    }

    /**
     * 
     * @brief getDistribution method
     * 
     * @return
     * 
     * @detail returns the distribution of the noise
     */
    public int getDistribution()
    {
        return distribution;
    }

    /**
     * 
     * @brief setMonochrome method
     * 
     * @param monochrome
     * 
     * @detail
     */
    public void setMonochrome(boolean monochrome)
    {
        this.monochrome = monochrome;
    }

    /**
     * 
     * @brief getMonochrome method
     * 
     * @return
     * 
     * @detail
     */
    public boolean getMonochrome()
    {
        return monochrome;
    }

    /**
     * 
     * @brief setDensity method
     * 
     * @param density
     * 
     * @detail sets the density of the noise
     */
    public void setDensity( float density )
    {
        this.density = density;
    }

    /**
     * 
     * @brief getDensity method
     * 
     * @return
     * 
     * @detail returns the density of the noise
     */
    public float getDensity()
    {
        return density;
    }


    private int randomValue(int x)
    {
        x += ((distribution == UNIFORM) ? ran.nextGaussian() : (2*ran.nextFloat() -1)) * amount;

        if(x < 0)
            x = 0;
        else if(x > 255)
            x = 255;

        return x;
    }

    public Bitmap addNoise(Bitmap srcBmp)
    {
        int width = srcBmp.getWidth();
        int height = srcBmp.getHeight();
        int inPixels[] = new int[width * height];;
        int outPixels[] = new int[width * height];
        int a = 0, r = 0, g = 0, b = 0, rgb;
        Bitmap dstBmp = null;

        try
        {
            srcBmp.getPixels(inPixels, 0, width, 0, 0, width, height);
        }
        catch(IllegalArgumentException e)
        {
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
        }

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                rgb = inPixels[y*width + x];
                a = (rgb >> 24) & 0xff;
                r = (rgb >> 16) & 0xff;
                g = (rgb >>  8)  & 0xff;
                b =  rgb        & 0xff;

                if ( ran.nextFloat() <= density ) {
                    if(monochrome)
                    {
                        int n = (int)(((distribution == GAUSSIAN ? ran.nextGaussian() : 2*ran.nextFloat() - 1)) * amount);
                        r = clamp(r+n);
                        g = clamp(g+n);
                        b = clamp(b+n);
                    }
                    else
                    {
                        r = randomValue(r);
                        g = randomValue(g);
                        b = randomValue(b);
                    }
                }
                outPixels[y*width + x] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }

        dstBmp = Bitmap.createBitmap(outPixels, width, height, srcBmp.getConfig());

        return dstBmp;
    }

    /**
     * Clamp a value to the range 0..255
     */
    public int clamp(int c)
    {
        if (c < 0)
            return 0;
        if (c > 255)
            return 255;
        return c;
    }
}


