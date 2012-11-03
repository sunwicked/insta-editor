package com.insta.editor;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class AppPreference {
    
    private static final String APP_SHARED_PREFS = "com.insta.editor"; //  Name of the file -.xml
    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;
    private static int valueCount;
    private static int savedCount = 0;
    private static final int MAX_COUNT = 4;
    private final String SAVE_VAL ="save value";
    
    //private ArrayList<String> pathList;
    
    
    public AppPreference(Context _context) {
        appSharedPrefs = _context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        prefsEditor = appSharedPrefs.edit();
        valueCount = appSharedPrefs.getInt(SAVE_VAL, 0);
       // pathList = new ArrayList<String>();
    }

    public void savePath(String path) {
        if(valueCount >= MAX_COUNT)
        {
            valueCount = valueCount % MAX_COUNT;
        }
        prefsEditor.putString(Integer.toString(valueCount), path);
        prefsEditor.commit();
        valueCount++;
        if(savedCount < MAX_COUNT) {
            savedCount = valueCount;
            saveValue();
        }
        
    }

    public String getPath(int key) {
        return appSharedPrefs.getString(Integer.toString(key), "");
    }
    
    public ArrayList<String> getPathList() {
        ArrayList<String> pathList = new ArrayList<String>();
        for(int i=0; i<MAX_COUNT; i++)
        {
            String path = appSharedPrefs.getString(Integer.toString(i), "");
            if(path!=""){
            	if(!pathList.contains(path))
                pathList.add(path);
            }
        }
        return pathList;
    }
 
    public void saveValue() {
        prefsEditor.putInt(SAVE_VAL, savedCount);
        prefsEditor.commit();
    }
    
}
