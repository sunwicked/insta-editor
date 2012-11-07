package com.insta.editor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MyGalleryAdapter extends BaseAdapter{

    /*Integer[] mImageIdsEffects1 = 
    {
            R.drawable.effect_bgr,
            R.drawable.effect_sepia,
            R.drawable.effect_inv,
            R.drawable.effect_pol,
            R.drawable.effect_bw,
            R.drawable.effect_red,
            R.drawable.effect_green,
            R.drawable.effect_blue,
            R.drawable.yellow,
            R.drawable.red,
            R.drawable.green,
            R.drawable.blue,
            R.drawable.gray,
            R.drawable.white
    };*/

    Integer[] mImageIdsEffects =
    {
            R.drawable.sunflower_bgr,
            R.drawable.sunflower_sepia,
            R.drawable.sunflower_invert,
            R.drawable.sunflower_polaroid,
            R.drawable.sunflower_gray,
            R.drawable.sunflower_lomo1,
            R.drawable.sunflower_lomo,
            R.drawable.sunflower_lomo1,
            R.drawable.sunflower_grain,
            R.drawable.sunflower_hot,
            R.drawable.sunflower_cold, 
    };
    
    Integer[] mImageIdsAdjustments = 
    {
            R.drawable.sunflower_sat,
            R.drawable.sunflower_cont,
            R.drawable.sunflower_bright,
            R.drawable.sunflower_grain
    };

    Integer[] mTextureIds =
    {
            R.drawable.sunflower_sat,
            R.drawable.sunflower_cont,
            R.drawable.sunflower_bright,
            R.drawable.sunflower_hot,
            R.drawable.sunflower_cold,
            R.drawable.more
    };
    
    String[] mEffectsTxt = 
    {
            "BGR", "Sepia", "Invert", "Polaroid",
            "Gray", "LomoYo", "LomoGr", "LomoWht",
            "Grain", "Warm", "Cold"            
    };
    
    String[] mAdjTxt = 
    {
            "Saturation","Contrast","Brightness",
            "Grain"
    };
    
    Context context;
    LayoutInflater inflater;

    Transformation transform;
    
    public MyGalleryAdapter(Context context, Transformation transform)
    {
        this.context = context;
        this.transform = transform;
        inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount()
    {
        if(transform == Transformation.Effects)
            return mImageIdsEffects.length;
        else if(transform == Transformation.Adjustments)
            return mImageIdsAdjustments.length;
        else
            return mTextureIds.length;
    }

    @Override
    public Object getItem (int position)
    {
        if(transform == Transformation.Effects)
            return mImageIdsEffects[position];
        else if(transform == Transformation.Adjustments)
            return mImageIdsAdjustments[position];
        else
            return mTextureIds[position];
    }

    @Override
    public long getItemId (int position)
    {
        return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if(convertView == null)
        {
            holder       = new ViewHolder();
            convertView  = inflater.inflate(R.layout.gallery_item, null);
            holder.image = (ImageView)convertView.findViewById(R.id.gImage);
            holder.txt   = (TextView)convertView.findViewById(R.id.gText);
            holder.rel   = (RelativeLayout)convertView.findViewById(R.id.galItemRoot);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        if(transform == Transformation.Effects) { 
            holder.image.setImageResource(mImageIdsEffects[position]);
            holder.txt.setText(mEffectsTxt[position]);
        }else if(transform == Transformation.Adjustments){
            holder.image.setImageResource(mImageIdsAdjustments[position]);
            holder.txt.setText(mAdjTxt[position]);
        }else {
            holder.image.setImageResource(mTextureIds[position]);
        }
        
        if(EditActivity.selected == position)
        {
            //holder.rel.setBackgroundResource(R.drawable.selection_back);
        }
        {
            //holder.rel.setBackgroundColor(R.drawable.button_blue);
        }
        return convertView;
    }

    class ViewHolder
    {
        ImageView image;
        TextView txt;
        RelativeLayout rel;
    }

}
