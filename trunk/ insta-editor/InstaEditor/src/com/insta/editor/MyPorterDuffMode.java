package com.insta.editor;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class MyPorterDuffMode {

    public Bitmap applyOverlayMode(Bitmap srcBmp, Bitmap destBmp, PorterDuffMode mode)
    {
        int width = srcBmp.getWidth();
        int height = srcBmp.getHeight();
        int srcPixels[] = new int[width * height];;
        int destPixels[] = new int[width * height];
        int tempPixels[] = new int[width * height];
        int aS = 0, rS = 0, gS = 0, bS = 0;
        int rgbS = 0;
        int aD = 0, rD = 0, gD = 0, bD = 0;
        int rgbD = 0;

        try
        {
            srcBmp.getPixels(srcPixels, 0, width, 0, 0, width, height);
            destBmp.getPixels(destPixels, 0, width, 0, 0, width, height);
            srcBmp.recycle();
            destBmp.recycle();
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
                rgbS = srcPixels[y*width + x];
                aS = (rgbS >> 24) & 0xff;
                rS = (rgbS >> 16) & 0xff;
                gS = (rgbS >>  8) & 0xff;
                bS = (rgbS      ) & 0xff;

                rgbD = destPixels[y*width + x];
                aD = ((rgbD >> 24) & 0xff);
                rD = (rgbD >> 16) & 0xff;
                gD = (rgbD >>  8) & 0xff;
                bD = (rgbD      )  & 0xff;

                switch(mode)
                {
                    case OVERLAY:
                        rS = overlay_byte(rD, rS, aS, aD);
                        gS = overlay_byte(gD, gS, aS, aD);
                        bS = overlay_byte(bD, bS, aS, aD);
                        aS = aS + aD - Math.round((aS * aD)/255f);
                        break;
                    case ADD:
                        aS = clamp(aS + aD);
                        rS = clamp(rS + rD);
                        gS = clamp(gS + gD);
                        bS = clamp(bS + bD);
                        break;
                    case DODGE:
                        if(rgbD == 0)
                        {
                            tempPixels[y*width + x] = rgbS;
                            continue;
                        }
                        rS = colordodge_byte(rS, rD, aS, aD);
                        gS = colordodge_byte(gS, gD, aS, aD);
                        bS = colordodge_byte(bS, bD, aS, aD);
                        aS = aS + aD - Math.round((aS * aD)/255f);
                        break;
                    case BURN:
                        if(rgbD == 0)
                        {
                            tempPixels[y*width + x] = rgbS;
                            continue;
                        }
                        rS = colorburn_byte(rD, rS, aS, aD);
                        gS = colorburn_byte(gD, gS, aS, aD);
                        bS = colorburn_byte(bD, bS, aS, aD);
                        aS = aS + aD - Math.round((aS * aD)/255f);
                        break;
                    case HARDLIGHT:
                        rS = hardlight_byte(rD, rS, aS, aD);
                        gS = hardlight_byte(gD, gS, aS, aD);
                        bS = hardlight_byte(bD, bS, aS, aD);
                        aS = aS + aD - Math.round((aS * aD)/255f);
                        break;
                    case DIFFERENCE:
                        rS = difference_byte(rD, rS, aS, aD);
                        gS = difference_byte(gD, gS, aS, aD);
                        bS = difference_byte(bD, bS, aS, aD);
                        aS = aS + aD - Math.round((aS * aD)/255f);
                        break;
                }
                /*if(rgbD == 0)
                {
                    tempPixels[y*width + x] = rgbS;
                    continue;
                }*/

                //multiplication
                /*aS = Math.round((aS * aD)/255f);
                rS = Math.round((rS * rD)/255f);
                gS = Math.round((gS * gD)/255f);
                bS = Math.round((bS * bD)/255f);*/

                //addition
                /*aS = clamp(aS + aD);
                rS = clamp(rS + rD);
                gS = clamp(gS + gD);
                bS = clamp(bS + bD);*/

                //overlay
                //order of color arguments has been changed
                //reason unknown
                /*rS = overlay_byte(rD, rS, aS, aD);
                gS = overlay_byte(gD, gS, aS, aD);
                bS = overlay_byte(bD, bS, aS, aD);
                aS = aS + aD - Math.round((aS * aD)/255f);*/

                //color-dodge
                /*rS = colordodge_byte(rS, rD, aS, aD);
                gS = colordodge_byte(gS, gD, aS, aD);
                bS = colordodge_byte(bS, bD, aS, aD);
                aS = aS + aD - Math.round((aS * aD)/255f);

                rS = clamp_max(rS, aS);
                gS = clamp_max(gS, aS);
                bS = clamp_max(bS, aS);*/

                //hard-light
                /*rS = hardlight_byte(rD, rS, aS, aD);
                gS = hardlight_byte(gD, gS, aS, aD);
                bS = hardlight_byte(bD, bS, aS, aD);
                aS = aS + aD - Math.round((aS * aD)/255f);*/
                
                //difference
                /*rS = difference_byte(rD, rS, aS, aD);
                gS = difference_byte(gD, gS, aS, aD);
                bS = difference_byte(bD, bS, aS, aD);
                aS = aS + aD - Math.round((aS * aD)/255f);*/

                //color-burn
                /*rS = colorburn_byte(rD, rS, aS, aD);
                gS = colorburn_byte(gD, gS, aS, aD);
                bS = colorburn_byte(bD, bS, aS, aD);
                aS = aS + aD - Math.round((aS * aD)/255f);*/
                
                /*if(rS < 0.5)
                    rS = 2*rS * rD;
                else 
                    rS = 1 - ((1-rD) * (2-2*rS));

                if(gS < 0.5)
                    gS = 2*gS * gD;
                else
                    gS = 1 - ((1-gD) * (2-2*gS)); 

                if(bS < 0.5)
                    bS = 2*bS * bD;
                else
                    bS = 1 - ((1-bD) * (2-2*bS));*/
                /*aS *= 255;
                rS *= 255;
                gS *= 255;
                bS *= 255;*/

                tempPixels[y*width + x] = ((int)aS << 24) | ((int)rS << 16) | ((int)gS << 8) | (int)bS;
            }
        }

        return Bitmap.createBitmap(tempPixels, width, height, srcBmp.getConfig());
    }
    
    // kDifference_Mode
    int difference_byte(int sc, int dc, int sa, int da) {
        int tmp = Math.min(sc * da, dc * sa);
        return clamp_signed_byte(sc + dc - 2 * Math.round(tmp/255f));
    }
    
    // kHardLight_Mode
    int hardlight_byte(int sc, int dc, int sa, int da) {
        int rc;
        if (2 * sc <= sa) {
            rc = 2 * sc * dc;
        } else {
            rc = sa * da - 2 * (da - dc) * (sa - sc);
        }
        return clamp_div255round(rc + sc * (255 - da) + dc * (255 - sa));
    }

    // kColorDodge_Mode
    int colordodge_byte(int sc, int dc, int sa, int da) {
        int diff = sa - sc;
        int rc;
        if (0 == diff) {
            rc = sa * da + sc * (255 - da) + dc * (255 - sa);
            rc = Math.round(rc/255f);
        } else {
            int tmp = (dc * sa << 15) / (da * diff);
            rc = Math.round((sa * da)/255f) * tmp >> 15;
            // don't clamp here, since we'll do it in our modeproc
        }
        return rc;
    }

    // kColorBurn_Mode
    int colorburn_byte(int sc, int dc, int sa, int da) {
        int rc;
        if (dc == da && 0 == sc) {
            rc = sa * da + dc * (255 - sa);
        } else if (0 == sc) {
            return Math.round((dc * (255-sa))/255f);//SkAlphaMulAlpha(dc, 255 - sa);
        } else {
            int tmp = (sa * (da - dc) * 256) / (sc * da);
            if (tmp > 256) {
                tmp = 256;
            }
            int tmp2 = sa * da;
            rc = tmp2 - (tmp2 * tmp >> 8) + sc * (255 - da) + dc * (255 - sa);
        }
        return Math.round(rc/255f);
    }
    
    // kOverlay_Mode
    int overlay_byte(int sc, int dc, int sa, int da) {
        int tmp = sc * (255 - da) + dc * (255 - sa);
        int rc;
        if (2 * dc <= da) {
            rc = 2 * sc * dc;
        } else {
            rc = sa * da - 2 * (da - dc) * (sa - sc);
        }
        return clamp_div255round(rc + tmp);
    }
    
    int clamp_signed_byte(int n) {
        if (n < 0) {
            n = 0;
        } else if (n > 255) {
            n = 255;
        }
        return n;
    }

    int clamp_div255round(int prod) {
        if (prod <= 0) {
            return 0;
        } else if (prod >= 255*255) {
            return 255;
        } else {
            return Math.round((float)prod/255);
        }
    }

    int clamp_max(int value, int max) {
        if (value > max) {
            value = max;
        }
        return value;
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
