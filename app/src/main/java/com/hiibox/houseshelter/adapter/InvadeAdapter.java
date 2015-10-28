package com.hiibox.houseshelter.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.zgan.youbao.R;
import com.hiibox.houseshelter.net.InvadePhotoResult.PhotoInfo;
import com.hiibox.houseshelter.util.ScreenUtil;

    
  
  
  
  
  
  
  
public class InvadeAdapter extends BaseAdapter {

    private Activity activity = null;
    private Context context = null;
    private FinalBitmap finalBitmap = null;
    private List<PhotoInfo> list = null;
    
    public InvadeAdapter(Activity activity, Context context, FinalBitmap finalBitmap) {
        super();
        this.activity = activity;
        this.context = context;
        this.finalBitmap = finalBitmap;
    }
    
    public void setList(List<PhotoInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhotoInfo info = (PhotoInfo) getItem(position);
        ViewHolder holder = new ViewHolder();
        if (null == convertView) {
            convertView = View.inflate(context, R.layout.lv_item_invade_layout, null);
        }
        holder.dateTV = (TextView) convertView.findViewById(R.id.invade_time_tv);
        holder.invadeTypeTV = (TextView) convertView.findViewById(R.id.invade_type_tv);
        holder.invadeIV = (ImageView) convertView.findViewById(R.id.invade_iv);
        
        int screenHeight = ScreenUtil.getScreenHeight(activity);
        if (screenHeight > 854 && screenHeight <= 1280) {
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 200);
            params.leftMargin = 10;
            params.rightMargin = 20;
            params.topMargin = 20;
            params.bottomMargin = 20;
            holder.invadeIV.setLayoutParams(params);
        }
        String[] time = info.getDate().split(" ");
        if (time.length < 2) {
            holder.dateTV.setText(info.getDate());
        } else {
            holder.dateTV.setText(time[0]+"\n"+time[1]);
        }
        finalBitmap.display(holder.invadeIV, info.getUrl());
      
        return convertView;
    }
    
    class ViewHolder {
        TextView dateTV;
        TextView invadeTypeTV;
        ImageView invadeIV;
    }

}
