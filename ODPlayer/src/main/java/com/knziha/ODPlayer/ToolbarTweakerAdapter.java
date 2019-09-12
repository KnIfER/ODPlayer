package com.knziha.ODPlayer;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.knziha.filepicker.widget.CircleCheckBox;

public class ToolbarTweakerAdapter extends BaseAdapter implements View.OnClickListener, DialogInterface.OnDismissListener {
    final Toolbar toolbar;
    final VICMainAppOptions opt;
    final Context mContext;
    public ToolbarTweakerAdapter(Context _mContext, Toolbar _toolbar, VICMainAppOptions _opt){
        toolbar=_toolbar;
        opt=_opt;
        mContext=_mContext;
    }

    @Override
    public int getCount() {
        return toolbar.getMenu().size();
    }

    @Nullable
    @Override
    public CharSequence getItem(int position) {
        String text=toolbar.getMenu().getItem(position).getTitle().toString().replace("√", "");
        return text;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.circle_checker_item, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
            convertView.getBackground().setColorFilter(new ColorMatrixColorFilter(Toastable_Activity.NEGATIVE));
            convertView.getBackground().setAlpha(128);
            vh.tv.setTextColor(Color.WHITE);
            vh.ck.addStateWithDrawable(mContext.getResources().getDrawable(R.drawable.ic_screen_rotation_black_24dp).mutate());
            vh.ck.drawIconForEmptyState =false;
            vh.ck.setOnClickListener(this);
            vh.ck2.setProgress(1);
            vh.ck2.drawInnerForEmptyState=true;
            vh.ck2.setOnClickListener(this);
        }else
            vh= (ViewHolder) convertView.getTag();
        MenuItem tI = toolbar.getMenu().getItem(position);
        vh.tv.setText(tI.getTitle().toString().replace("√",""));
        Drawable d = tI.getIcon();
        vh.ck.setChecked(1,false);
        if(d!=null) {
            vh.ck2.setDrawable(0, d.getConstantState().newDrawable());
            vh.ck2.drawIconForEmptyState =true;
        }else {
            vh.ck2.drawIconForEmptyState = false;
        }
        vh.ck.invalidate();
        vh.ck2.invalidate();
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.check1:
                CircleCheckBox ck = (CircleCheckBox) v;
                ck.setProgress(0);
                ck.iterate();
            break;
            case R.id.check2:
                ck = (CircleCheckBox) v;
                ck.setProgress(0);
                ck.addAnim(true);
            break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }


    class ViewHolder{
        ViewHolder(View v){
            ck=v.findViewById(R.id.check1);
            ck2=v.findViewById(R.id.check2);
            tv=v.findViewById(android.R.id.text1);
        }
        CircleCheckBox ck;
        CircleCheckBox ck2;
        TextView tv;
    }


}
