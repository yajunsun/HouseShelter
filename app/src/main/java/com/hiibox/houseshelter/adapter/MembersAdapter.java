package com.hiibox.houseshelter.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zgan.youbao.R;
import com.hiibox.houseshelter.net.MembersInfoResult;

public class MembersAdapter extends BaseAdapter {

	private Context context = null;
	@SuppressWarnings("unused")
    private FinalBitmap finalBitmap = null;
	private List<MembersInfoResult> list = null;
	private Drawable maleDrawable = null;        
    private Drawable maleLightdDrawable = null;          
    private Drawable recorderDrawable = null;           
	
	public MembersAdapter(Context context, FinalBitmap finalBitmap,
			List<MembersInfoResult> list) {
		super();
		this.context = context;
		this.finalBitmap = finalBitmap;
		this.list = list;
		Resources res = context.getResources();
		maleDrawable = res.getDrawable(R.drawable.male_gray);
        maleDrawable.setBounds(0, 0, maleDrawable.getMinimumWidth(), maleDrawable.getMinimumHeight());
        maleLightdDrawable = res.getDrawable(R.drawable.male_light);
        maleLightdDrawable.setBounds(0, 0, maleLightdDrawable.getMinimumWidth(), maleLightdDrawable.getMinimumHeight());
        recorderDrawable = res.getDrawable(R.drawable.recorder_gray);
        recorderDrawable.setBounds(0, 0, recorderDrawable.getMinimumWidth(), recorderDrawable.getMinimumHeight());
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
                                                               
		MembersInfoResult info = (MembersInfoResult) getItem(position);
		int status = info.status;
		if (null == convertView) {
			convertView = View.inflate(context, R.layout.lv_item_family_members_layout, null);
		}
		TextView tv = new TextView(context);
		tv = (TextView) convertView.findViewById(R.id.member_tv);
                                                                                                 
                         
                            
                                   
                               
		tv.setText(info.nickname.trim());
		if (status == 0) {
			tv.setCompoundDrawables(null, maleDrawable, null, recorderDrawable);
		} else {
			tv.setCompoundDrawables(null, maleLightdDrawable, null, recorderDrawable);
		}
                                                               
		return convertView;
	}

}
