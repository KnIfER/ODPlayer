package com.knziha.equalizer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.knziha.ODPlayer.R;

public class PresetsCustomAdapter extends BaseAdapter implements  OnClickListener{
    public ArrayList<JSONObject> filtered_disk_presets = new ArrayList<>(5);
    public ArrayList<Integer> filtered_disk_presets_tracer = new ArrayList<>(5);
    LayoutInflater inflater;
	Context _context;
	public ArrayList<String> _list;
	public String[] _list_pre_empt;
	
	@Override//pre empt onClick
	public void onClick(View v) {
		//CMN.show(System.currentTimeMillis()+"");
		int pos = (int) v.getTag();
		switch(pos) {
			case 0:
                oiclMy.saveCurrent();
			break;
			case 1:
				oiclMy.saveCurrentAs();
			break;
			case 2:
                oiclMy.populateFurther();
			break;
		}
	}

	public interface OnPopulaterFuther {
		public void populateFurther();

        public void saveCurrentAs();

		public void saveCurrent();
	}

    OnPopulaterFuther oiclMy;
	
	public PresetsCustomAdapter(Context mContext, ArrayList<String> shareAppInfos) {
		 _context = mContext;
		 _list= shareAppInfos;
		 inflater = LayoutInflater.from(mContext);
		 _list_pre_empt = _context.getResources().getStringArray(R.array.share_pre_empt);
	}
	
	public int number_margin=3;
	public int getCount() {return _list.size()+filtered_disk_presets.size()+number_margin;}
	  
    public Object getItem(int pos) {
    	if(pos<number_margin)
    		return null;
        if(pos<number_margin+filtered_disk_presets.size())
            return filtered_disk_presets.get(pos-number_margin);
    	return _list.get(pos-number_margin-filtered_disk_presets.size());
	}

	  public View getView ( int position, View convertView, ViewGroup parent ) {
          TextView tvName;
		  if (convertView == null) {
			  convertView = inflater.inflate(  R.layout.simple_column_litem, null );
              tvName = (TextView) convertView.findViewById(android.R.id.text1);
              tvName.setFocusable(false);
              tvName.setClickable(false);
              tvName.setTextIsSelectable(false);
		  }else
              tvName = (TextView) convertView.findViewById(android.R.id.text1);
		  //convertView.findViewById(R.id.indicator).setVisibility(View.INVISIBLE);
		  if(position<number_margin) {
	          tvName.setText(_list_pre_empt[position]);
	          convertView.setTag(position);
	          convertView.setOnClickListener(this);
			  convertView.setClickable(true);
              tvName.setTextColor(Color.WHITE);
              convertView.setBackgroundResource(R.drawable.listviewselector0);
			  return convertView;
		  }if(position<number_margin+filtered_disk_presets.size()) {
              convertView.setOnClickListener(null);
              convertView.setClickable(false);
              JSONObject infoI = (JSONObject) getItem(position);
              tvName.setText(infoI.getString("pname"));
              tvName.setTextColor(Color.WHITE);
              convertView.setBackgroundResource(R.drawable.listviewselector0);
              return convertView;
		  }else {
              convertView.setOnClickListener(null);
              convertView.setClickable(false);
              String infoI = (String) getItem(position);
              tvName.setText(infoI);
              tvName.setTextColor(Color.WHITE);
              convertView.setBackgroundResource(R.drawable.listviewselector0);
              return convertView;
          }
    }

	@Override
	public int getItemViewType(int arg0) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
		// TODO Auto-generated method stub
	
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		 
		return true;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void setListMargin(int i) {
		number_margin = i;
		//notifyDataSetChanged();this is not working here,why?
	}

	public void setPopulaterFutherListener(OnPopulaterFuther onItemClickListenermy) {
		oiclMy=onItemClickListenermy;
	}

	

}
