package com.knziha.ODPlayer;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.knziha.ODPlayer.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MediaInfoAdapter extends BaseAdapter implements View.OnClickListener, DialogInterface.OnDismissListener {
    HashMap directory;
    VICMainAppOptions opt;
    ArrayList arr;
    final Context mContext;
    final String[] dscp;
    public int windowwidth;

    public MediaInfoAdapter(Context _mContext, HashMap _directory, VICMainAppOptions _opt, ArrayList _arr, String[] _dscp){
        directory=_directory;
        opt=_opt;
        mContext=_mContext;
        arr=_arr;
        dscp=_dscp;
    }


    public void invalidate(HashMap _directory, VICMainAppOptions _opt, ArrayList _arr) {
        directory=_directory;
        opt=_opt;
        arr=_arr;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return arr==null?0:arr.size();
    }

    @Nullable
    @Override
    public CharSequence getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position,  View convertView,
                        ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.simple_column_litem, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
            convertView.getBackground().setColorFilter(new ColorMatrixColorFilter(Toastable_Activity.NEGATIVE));
            convertView.getBackground().setAlpha(180);
            vh.tv.setTextColor(Color.WHITE);
        }else
            vh= (ViewHolder) convertView.getTag();

        if(position<=2){
            if(position==2){
                if(directory.containsKey("fform")){
                    String SizeAndFormat = (String) directory.get("sizandform");
                    if(SizeAndFormat==null){
                        SizeAndFormat = getSpannedString((String) arr.get(2), (String) directory.get("fform"), vh.tv.getPaint());
                        directory.put("sizandform", SizeAndFormat);
                    }
                    vh.tv.setText(SizeAndFormat);
                }else
                    vh.tv.setText((String) arr.get(2));
                return convertView;
            }
            vh.tv.setText(dscp[position] + " : "+String.valueOf(arr.get(position)));
        }else{
            int typeIdx = (int) arr.get(position);
            String message="";
            switch (typeIdx){
                case 5:
                    if(directory.containsKey("dscp")){
                        message+=directory.get("dscp");
                    }
                    if(directory.containsKey("cmt")){
                        message+=directory.get("cmt");
                    }
                break;
                case 3:
                    String DurationAndFileBitrate = (String) directory.get("durandfrate");
                    if(DurationAndFileBitrate==null){
                        DurationAndFileBitrate = getSpannedString((String) directory.get("ftime"), (String) directory.get("frate"), vh.tv.getPaint());
                        directory.put("durandfrate", DurationAndFileBitrate);
                    }
                    vh.tv.setText(DurationAndFileBitrate);
                return convertView;
                default:
                message=String.valueOf(typeIdx);
            }
            vh.tv.setText(dscp[typeIdx+3] + " : "+message);
        }


        return convertView;
    }

    private String getSpannedString(String A, String B, TextPaint paint) {
        String filerate = (String) directory.get("frate");
        float len1 = paint.measureText(A);
        float len2 = paint.measureText(filerate);
        int chajia = (int) ((windowwidth - (len1 + len2)) / (paint.measureText(" ")))-1;
        //CMN.Log(chajia,"chajiachajiachajia", windowwidth,len1,len2);
        StringBuilder sbb = new StringBuilder(10);
        for (int i = 0; i < chajia; i++) {
            sbb.append(" ");
        }
        return A+sbb.toString()+B;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.check1:
            break;
            case R.id.check2:
            break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    class ViewHolder{
        ViewHolder(View v){
            tv=v.findViewById(android.R.id.text1);
        }
        TextView tv;
    }


}
