package com.knziha.ODPlayer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.knziha.filepicker.view.CMNF;
import com.knziha.text.SelectableTextView;

public class MenuAdapter extends BaseAdapter {
    final String[] menu_common;
    final String[] menu_selecting;
    private final int[] icons_common;
    private final int[] icons_selecting;
    private final int count_common;
    private final int count_selecting;
    SelectableTextView mWrappedSelectableText;
    Context cc;
    
    MenuAdapter(SelectableTextView inWrappedSelectableText, Context c){
        super();
        cc=c;
        mWrappedSelectableText=inWrappedSelectableText;
        menu_common = c.getResources().getStringArray(R.array.menu_textbook_common);
        menu_selecting = c.getResources().getStringArray(R.array.menu_textbook_selecting);
        //icons_common = c.getResources().getIntArray(R.array.icons_common);
        //icons_selecting = c.getResources().getIntArray(R.array.icons_selecting);
        icons_common = new int[]{
                R.drawable.abc_ic_menu_selectall_mtrl_alpha,
                R.drawable.ic_palette_black_24dp,
                R.drawable.ic_palette_black_24dp,
        };
        icons_selecting = new int[]{
                R.drawable.fp_copy,
                R.drawable.ic_send_black_24dp,
                R.drawable.abc_ic_search_api_material,
                R.drawable.ic_delete,
                R.drawable.ic_palette_black_24dp,
                R.drawable.ic_palette_black_24dp,
                R.mipmap.intervalsele,
                R.drawable.abc_ic_clear_material,
        };
        count_common = Math.min(icons_common.length, menu_common.length);
        count_selecting = Math.min(icons_selecting.length, menu_selecting.length);
        CMNF.Log(icons_common);
    }
    
    @Override
    public int getCount() {
        return mWrappedSelectableText.hasSelection()?count_selecting:count_common;
    }

    @Override
    public String getItem(int position) {
        return mWrappedSelectableText.hasSelection()?menu_selecting[position]:menu_common[position];
    }

    @Override
    public long getItemId(int position) {
        return getId(position);
    }

    public int getId(int position) {
        return mWrappedSelectableText.hasSelection()?icons_selecting[position]:icons_common[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        if(convertView instanceof TextView){
            tv= (TextView) convertView;
        }else{
            Context context;
            if(parent!=null) {cc=null;context=parent.getContext();}
            else context=cc;
            tv = new TextView(context);
            DisplayMetrics dm = parent.getContext().getResources().getDisplayMetrics();
            final int pad = (int) (10*dm.density);
            tv.setTextColor(Color.WHITE);
            tv.setBackgroundResource(R.drawable.listviewselector3);
            tv.setPadding(pad,pad,pad,pad);
            tv.setSingleLine(true);
            tv.setTextSize(15.5f);
            CMNF.Log(tv.getTextSize()/dm.density);
        }
        tv.setText(getItem(position));
        tv.setText("  "+tv.getText());
        TextPaint painter = tv.getPaint();
        final float fontHeight = painter.getFontMetrics().bottom - painter.getFontMetrics().top;
        Drawable d = parent.getContext().getResources().getDrawable(getId(position));
        d.setBounds(0,0, (int) fontHeight, (int) fontHeight);
        d.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        tv.setCompoundDrawables(d,null,null,null);
        return tv;
    }
}
