package com.hiibox.houseshelter.adapter;

  
  
  
  
  
  
  
      
  
  
  
  
  
  
  

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

    
  
  
public abstract class AbstractWheelTextAdapter extends AAdapter {

	      
	public static final int TEXT_VIEW_ITEM_RESOURCE = -1;

	      
	protected static final int NO_RESOURCE = 0;

	      
	public static final int DEFAULT_TEXT_COLOR = 0xFF101010;

	      
	public static final int LABEL_COLOR = 0xFF700070;
	
	public static final int CONFIG_TEXT_COLOR = 0xFF6699ff;

	      
	public static final int DEFAULT_TEXT_SIZE = 24;

	                 
	private int textColor = DEFAULT_TEXT_COLOR;
	private int textSize = DEFAULT_TEXT_SIZE;
	
	private String textType = "";
	

	                   
	protected Context context;
	                   
	protected LayoutInflater inflater;

	                   
	protected int itemResourceId;
	protected int itemTextResourceId;

	                         
	protected int emptyItemResourceId;

	
	
	public String getTextType() {
		return textType;
	}

	public void setTextType(String textType) {
		this.textType = textType;
	}

	    
  
  
  
  
  
	protected AbstractWheelTextAdapter(Context context) {
		this(context, TEXT_VIEW_ITEM_RESOURCE);
	}

	    
  
  
  
  
  
  
  
  
	protected AbstractWheelTextAdapter(Context context, int itemResource) {
		this(context, itemResource, NO_RESOURCE);
	}

	    
  
  
  
  
  
  
  
  
  
  
	protected AbstractWheelTextAdapter(Context context, int itemResource,
			int itemTextResource) {
		this.context = context;
		itemResourceId = itemResource;
		itemTextResourceId = itemTextResource;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	    
  
  
  
  
	public int getTextColor() {
		return textColor;
	}

	    
  
  
  
  
  
	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	    
  
  
  
  
	public int getTextSize() {
		return textSize;
	}

	    
  
  
  
  
  
	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	    
  
  
  
  
	public int getItemResource() {
		return itemResourceId;
	}

	    
  
  
  
  
  
	public void setItemResource(int itemResourceId) {
		this.itemResourceId = itemResourceId;
	}

	    
  
  
  
  
	public int getItemTextResource() {
		return itemTextResourceId;
	}

	    
  
  
  
  
  
	public void setItemTextResource(int itemTextResourceId) {
		this.itemTextResourceId = itemTextResourceId;
	}

	    
  
  
  
  
	public int getEmptyItemResource() {
		return emptyItemResourceId;
	}

	    
  
  
  
  
  
	public void setEmptyItemResource(int emptyItemResourceId) {
		this.emptyItemResourceId = emptyItemResourceId;
	}

	    
  
  
  
  
  
  
	protected abstract CharSequence getItemText(int index);

	public View getItem(int index, View convertView, ViewGroup parent) {
		if (index >= 0 && index < getItemsCount()) {
			if (convertView == null) {
				convertView = getView(itemResourceId, parent);
			}
			TextView textView = getTextView(convertView, itemTextResourceId);
			textView.setPadding(0, 6, 0, 6);
			if (textView != null) {
				CharSequence text = getItemText(index);
				if (text == null) {
					text = "";
				}
				textView.setText(text);

				if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
					configureTextView(textView);
				}
			}
			return convertView;
		}
		return null;
	}

	@Override
	public View getEmptyItem(View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = getView(emptyItemResourceId, parent);
		}
		if (emptyItemResourceId == TEXT_VIEW_ITEM_RESOURCE
				&& convertView instanceof TextView) {
			configureTextView((TextView) convertView);
		}

		return convertView;
	}

	    
  
  
  
  
  
	protected void configureTextView(TextView view) {
		view.setTextColor(textColor);
		view.setGravity(Gravity.CENTER);
		view.setTextSize(textSize);
		view.setLines(1);
		view.setText(view.getText()+textType);
		view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
	}

	    
  
  
  
  
  
  
  
  
	private TextView getTextView(View view, int textResource) {
		TextView text = null;
		try {
			if (textResource == NO_RESOURCE && view instanceof TextView) {
				text = (TextView) view;
			} else if (textResource != NO_RESOURCE) {
				text = (TextView) view.findViewById(textResource);
			}
		} catch (ClassCastException e) {
			Log.e("AbstractWheelAdapter",
					"You must supply a resource ID for a TextView");
			throw new IllegalStateException(
					"AbstractWheelAdapter requires the resource ID to be a TextView",
					e);
		}

		return text;
	}

	    
  
  
  
  
  
  
	private View getView(int resource, ViewGroup parent) {
		switch (resource) {
		case NO_RESOURCE:
			return null;
		case TEXT_VIEW_ITEM_RESOURCE:
			return new TextView(context);
		default:
			return inflater.inflate(resource, parent, false);
		}
	}
}