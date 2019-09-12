package com.mycompany.myapp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;

import com.knziha.ODPlayer.R;
import com.knziha.text.TextViewmy;
import com.knziha.ODPlayer.CMN;
import com.knziha.ODPlayer.Toastable_Activity;

import org.videolan.libvlc.LibVLC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


public class MainActivity extends Toastable_Activity
{
    static{
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ArrayList<String> options = new ArrayList<>();
        options.add("--vout=android-display");
        options.add("--aout=opensles");
        //options.add("-vvv");
        new LibVLC(this, options);

        long stst = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CMN.Log("before onCreate2 setContentView",System.currentTimeMillis()-stst);
        CMN.Log();

        if(false)
        try {
            stst = System.currentTimeMillis();
            //fo = new FileOutputStream(new File("/sdcard/PLOD/mLog.txt"));
            //scanFiles("/sdcard/");
            //fo.flush();

            fo = new FileOutputStream(new File("/sdcard/PLOD/mLog2.txt"));
            fo.flush();
            BufferedReader fi = new BufferedReader(new FileReader(new File("/sdcard/PLOD/mLog.txt")));
            String ln;
            ln=fi.readLine();
            String arr[] = ln.split("/");
            for(String sI:arr){
                fo.write(sI.getBytes());
                fo.write("/".getBytes());
                hs.add(sI);
            }
            fo.flush();


            CMN.Log( "asdsad",System.currentTimeMillis()-stst, hs.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextViewmy tv = findViewById(R.id.text1);
        String str = "";
        for (int i = 0; i < 1000; i++) {
            str+=i +i +"asdasdsadasdsada"+i +"dsadsad"+i+"\r\n";
        }
        tv.setText(str);
        tv.setTextIsSelectable(false);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv.setTextColor(Color.WHITE);
    }



    FileOutputStream fo;
    HashSet<String> hs = new HashSet<>();
    private void scanFiles(String f) {
        File p=new File(f);
        if(!p.isDirectory())
            return;
        //if(depth>0) {
            File[] list2 = p.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }});
            for(File pI:list2)
                scanFiles(pI.getAbsolutePath());
        //}
        if(list2!=null)
            for(File pI:list2){
                processDir(pI.getAbsolutePath());
            }
    }

    private void processDir(String absolutePath) {
       String[] arr =  absolutePath.split("/");
       for(String sI:arr){
           if(!hs.contains(sI)){
               try {
                   fo.write(sI.getBytes());
                   fo.write("/".getBytes());
                   hs.add(sI);
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }

    }




}


