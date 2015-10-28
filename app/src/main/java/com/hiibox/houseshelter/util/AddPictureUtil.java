package com.hiibox.houseshelter.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;

import com.zgan.youbao.R;

public class AddPictureUtil {

	private Context context;
	
	public AddPictureUtil(Context ctx) {
		super();
		this.context = ctx;
	}

	public void add() {
		new AlertDialog.Builder(context)
		.setTitle(R.string.please_select_item)
		.setItems(new String[]{""}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						Intent takePhotosIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						((Activity) context).startActivityForResult(takePhotosIntent, 0x101);
						break;
					case 1:
						Intent selectLocalPicture = new Intent(Intent.ACTION_GET_CONTENT);
						selectLocalPicture.setType("image/*");
						((Activity) context).startActivityForResult(selectLocalPicture, 0x102);
						break;
					default:
						break;
				}
			}
		}).show();
	}
	
	public void alertDialog() {
		Dialog dialog = new AlertDialog.Builder(context)
		.setTitle("��ʾ")
		.setMessage("��ѡ��Ĳ�����Ч��ͼƬ")
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		}).create();
		dialog.show();
	}
}
