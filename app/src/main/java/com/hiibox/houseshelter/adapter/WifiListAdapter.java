package com.hiibox.houseshelter.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zgan.youbao.R;
import com.tutk.IOTC.AVIOCTRLDEFs;

public class WifiListAdapter extends BaseAdapter {

	private Context context = null;
	private List<AVIOCTRLDEFs.SWifiAp> list = null;
	private String[] arySSID = null;
	
	public WifiListAdapter(Context context, String[] arySSID) {
		super();
		this.context = context;
		this.arySSID = arySSID;
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
		String str = arySSID[position];
		ViewHolder holder = new ViewHolder();
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(R.layout.popupwindow_drop_down_item_layout, null);
		}
		holder.itemTV = (TextView) convertView.findViewById(R.id.popupwindown_item_tv);
		holder.itemTV.setText(str);
		return convertView;
	}
	
	class ViewHolder {
		TextView itemTV;
	}

}
