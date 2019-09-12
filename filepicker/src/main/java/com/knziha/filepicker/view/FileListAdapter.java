/*
 * Copyright (C) 2019 KnIfER
 * Copyright (C) 2016 Angad Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.knziha.filepicker.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.knziha.filepicker.R;
import com.knziha.filepicker.controller.NotifyItemChecked;
import com.knziha.filepicker.model.AudioCover;
import com.knziha.filepicker.model.DialogConfigs;
import com.knziha.filepicker.model.DialogProperties;
import com.knziha.filepicker.model.FilePickerOptions;
import com.knziha.filepicker.model.MarkedItemList;
import com.knziha.filepicker.model.MyRequestListener;
import com.knziha.filepicker.model.PatternHolder;
import com.knziha.filepicker.utils.ExtensionHelper;
import com.knziha.filepicker.widget.MaterialCheckbox;
import com.knziha.filepicker.widget.OnCheckedChangeListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;

import mp4meta.utils.CMN;

/* <p>
 * Created by Angad Singh on 09-07-2016.
 * </p>
 */

/**
 * Adapter Class that extends {@link BaseAdapter} that is
 * used to populate {@link ListView} with file info.
 */
public class FileListAdapter extends ArrayAdapter<FileListItem>{
    private final FilePickerOptions opt;
    private ArrayList<FileListItem> listItem;
    private Context context;
    private DialogProperties properties;
    private NotifyItemChecked notifyItemChecked;
    public boolean bIsSelecting=true;
    SimpleDateFormat timemachine;
    public SparseArray<Object> PrevewsPool = new SparseArray<>();
    final private PatternHolder ph;

    public FileListAdapter(ArrayList<FileListItem> listItem, Context context, DialogProperties properties,PatternHolder inph, FilePickerOptions inopt) {
        super(context,R.layout.dialog_file_list_item,R.id.fname,listItem);
        this.listItem = listItem;
        this.context = context;
        this.properties = properties;
        if(properties.selection_mode== DialogConfigs.SINGLE_MULTI_MODE) {
            properties.selection_mode = DialogConfigs.MULTI_MODE;
            bIsSelecting =false;
        }
        timemachine = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        ph=inph;
        opt = inopt;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public FileListItem getItem(int i) {
        return listItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    @SuppressWarnings("deprecation")
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.dialog_file_list_item, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
            holder.fmark.setOnCheckedChangedListener(mItemCheckedListener);
        }
        else{
            holder = (ViewHolder)view.getTag();
        }
        final FileListItem item = listItem.get(i);

        holder.fmark.setTag(i);
        //item.size

        //if (MarkedItemList.hasItem(item.getLocation())) {
        //    Animation animation = AnimationUtils.loadAnimation(context,R.anim.marked_item_animation);
       //     view.setAnimation(animation);
       // }
        //else {
       //     Animation animation = AnimationUtils.loadAnimation(context,R.anim.unmarked_item_animation);
       //     view.setAnimation(animation);
       // }
        if(bIsSelecting)
            holder.fmark.setInflated(true);
        else
            holder.fmark.setInflated(false);


        if(item.directory>2) {//==3
            holder.type_icon.setImageResource(R.mipmap.ic_type_folder);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorPrimary,context.getTheme()));
            else
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
            if(i==0) {
                if (item.time == -2) {
                    holder.type.subText = null;
                    holder.type.setText(R.string.label_root_directory);
                } else if (item.time == -1) {
                    holder.type.subText = (getCount()-1)+" 项";
                    String parentName = item.getLocation().substring(item.getLocation().lastIndexOf("/") + 1);
                    holder.type.setText(context.getString(R.string.label_parent_directory)+(parentName.length()==0?parentName:(" - " + parentName)));
                }
                holder.fmark.setVisibility(View.INVISIBLE);
            }
        }else if(item.directory==2) {//==2
            holder.type.subText = null;
            holder.type.setText(R.string.local_storage);
            holder.type_icon.setImageResource(R.drawable.ic_sd_storage_black_24dp);
            holder.type_icon.setColorFilter(0xFF2b4381);
            if(properties.locked && properties.selection_type == DialogConfigs.DIR_SELECT)
                holder.fmark.setVisibility(View.INVISIBLE);
            else
                holder.fmark.setVisibility(View.VISIBLE);
        }else if (item.directory==1) {
            holder.type_icon.setImageResource(R.mipmap.ic_type_folder);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorPrimary,context.getTheme()));
            else
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
            if(!properties.locked&&(properties.selection_type==DialogConfigs.FILE_SELECT))
                holder.fmark.setVisibility(View.INVISIBLE);
            else
                holder.fmark.setVisibility(View.VISIBLE);
            Date date = new Date(item.getTime());
            holder.type.subText = timemachine.format(date);
            if(item.size==-1) {
                File currentFile = new File(item.location);
                String[] list = currentFile.list();
                if(list==null)
                    item.size=-2;
                else
                    item.size=list.length;
            }
            holder.type.setText(item.size<0?null:item.size+" 项");
        }else{
            holder.type_icon.setImageResource(R.mipmap.ic_type_file);
            if(item.directory==0){//==0 this is a file
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorAccent,context.getTheme()));
                else
                    holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                Date date = new Date(item.getTime());
                holder.type.subText = timemachine.format(date);
                if(item.size==-1) {
                    File currentFile = new File(item.location);
                    item.size=currentFile.length();
                }
            }else{//==-1
                holder.type_icon.setColorFilter(0xFF3F51B5);
                holder.type.subText = getContext().getResources().getText(R.string.asset).toString();
            }
            if(properties.locked && properties.selection_type == DialogConfigs.DIR_SELECT)
                holder.fmark.setVisibility(View.INVISIBLE);
            else
                holder.fmark.setVisibility(View.VISIBLE);
            holder.type.setText(mp4meta.utils.CMN.formatSize(item.size));
            //holder.type.setText(Formatter.formatFileSize(getContext(), item.size));
        }

        holder.type_icon.getLayoutParams().height = -1;
        holder.type_icon.getLayoutParams().width  = -2;
        int suffix_idx;String file_suffix="";
        if((suffix_idx=item.getFilename().lastIndexOf("."))!=-1){file_suffix=item.getFilename().substring(suffix_idx).toLowerCase();}
        holder.name.setText(item.filename);
        if(properties.isDark) {
        	holder.name.setTextColor(Color.WHITE);
        	holder.type.setTextColor(0x8aFFFFFF);
        	
        }else {
        	holder.name.setTextColor(Color.BLACK);
        	holder.type.setTextColor(0x8a000000);
        }

        if(i>0){
            if (holder.fmark.getVisibility() == View.VISIBLE) {
                if (MarkedItemList.hasItem(item.location)) {
                    holder.fmark.setChecked(true);
                } else {
                    holder.fmark.setChecked(false);
                }
            }
        }
        decorate_by_keys(holder.name);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        float density = dm.density;
		boolean b2 = false;
        if(opt.getEnableTumbnails() && !item.isDirectory() && (ExtensionHelper.FOOTAGE.contains(file_suffix)||ExtensionHelper.PHOTO.contains(file_suffix)) || (b2=ExtensionHelper.SOUNDS.contains(file_suffix))) {
			//int targetDimension = (int) (150 * density);
			int targetDimension = (int) (opt.getListIconSize() * 1.f / 16 * Math.min(dm.widthPixels, dm.heightPixels));
			decorate_by_dimensions(holder.type_icon, targetDimension,
					opt.getAutoThumbsHeight() ? LayoutParams.WRAP_CONTENT : targetDimension);
			//decorate_by_dimensions(holder.type_icon, targetDimension, LayoutParams.MATCH_PARENT);
			Priority priority = Priority.HIGH;
			RequestOptions options = new RequestOptions()
					.signature(new ObjectKey(item.time))//+"|"+item.size
					.format(DecodeFormat.PREFER_ARGB_8888)//DecodeFormat.PREFER_ARGB_8888
					.priority(priority)
					.skipMemoryCache(false)
					.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
					//.onlyRetrieveFromCache(true)
					.fitCenter()
					.override(360, Target.SIZE_ORIGINAL);
			holder.type_icon.setColorFilter(null);
			holder.type_icon.setTag(R.id.home, false);
			RequestManager IncanOpen = Glide.with(getContext().getApplicationContext());
			(b2?IncanOpen.load(new AudioCover(item.location)):
					IncanOpen.load(item.location))
					.apply(options)
					.format(DecodeFormat.PREFER_RGB_565)
					.listener(myreqL2.setCrop(opt.getCropTumbnails()))
					.into(holder.type_icon)
			;
        }else {
			holder.type_icon.setTag(R.id.home, null);
			decorate_by_dimensions(holder.type_icon, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			holder.type_icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		}
        return view;
    }

    private void decorate_by_dimensions(View v, int w, int h) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        boolean needSet=false;
        if(lp.width!=w){lp.width = w;needSet=true;}
        if(lp.height!=h){lp.height = h;needSet=true;}
        //if(needSet)v.setLayoutParams(lp);
    }

    MyRequestListener myreqL2 = new MyRequestListener<Drawable>();

    private void decorate_by_keys(TextView name) {
        if(ph.pattern!=null){
            Matcher m = ph.pattern.matcher(name.getText().toString());
            SpannableStringBuilder spannable = null;
            while(m.find()){
                if(spannable==null)spannable = new SpannableStringBuilder(name.getText());
                spannable.setSpan(new ForegroundColorSpan(Color.RED),m.start(), m.end(),SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            if(spannable!=null) name.setText(spannable);
        }else if(ph.text !=null){
            String tofind = name.getText().toString();
            int index=-ph.text.length();
            SpannableStringBuilder spannable = null;
            while((index=tofind.indexOf(ph.text, index+ph.text.length()))!=-1){
                if(spannable==null)spannable = new SpannableStringBuilder(name.getText());
                spannable.setSpan(new ForegroundColorSpan(Color.RED),index, index+ph.text.length(),SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            if(spannable!=null) name.setText(spannable);
        }
    }

    public int[] lastCheckedPos = new int[]{-1,-1};
    public int lastCheckedPosIdx=-1;
    private OnCheckedChangeListener mItemCheckedListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(MaterialCheckbox checkbox, boolean isChecked, boolean isRisingEdge) {
            //CMNF.Log("onCheckedChangedonCheckedChanged", isChecked, isRisingEdge);
            int position = (Integer) checkbox.getTag();
            if(lastCheckedPosIdx==-1 || lastCheckedPos[lastCheckedPosIdx]!=position){
                lastCheckedPosIdx=(lastCheckedPosIdx+1)%2;
                lastCheckedPos[lastCheckedPosIdx] = position;
            }
            final FileListItem item = listItem.get(position);
            item.setMarked(isChecked);
            if (item.isMarked()) {
                if(!properties.locked || properties.selection_mode == DialogConfigs.MULTI_MODE) {//多选模式，或者解锁
                    MarkedItemList.addSelectedItem(item);
                }
                else {
                    MarkedItemList.addSingleFile(item);
                }
            }
            else {
                MarkedItemList.removeSelectedItem(item.getLocation());
            }
            if(isRisingEdge){
                bIsSelecting = true;
                notifyDataSetChanged();
            }
            notifyItemChecked.notifyCheckBoxIsClicked(item, isChecked);
        }
    };

    class ViewHolder
    {   ImageView type_icon;
        TextView name;
        FileInfoTextView type;
        MaterialCheckbox fmark;

        ViewHolder(View itemView) {
            name= itemView.findViewById(R.id.fname);
            type= itemView.findViewById(R.id.ftype);
            type_icon= itemView.findViewById(R.id.image_type);
            fmark= itemView.findViewById(R.id.file_mark);
        }
    }

    public void setNotifyItemCheckedListener(NotifyItemChecked notifyItemChecked) {
        this.notifyItemChecked = notifyItemChecked;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(!bIsSelecting){
            lastCheckedPos[0]=lastCheckedPos[1]=-1;
        }
    }
}
