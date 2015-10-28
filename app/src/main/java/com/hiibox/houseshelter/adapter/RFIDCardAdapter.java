package com.hiibox.houseshelter.adapter;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgan.jtws.ZganJTWSService;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;
import com.hiibox.houseshelter.activity.ManageAddressActivity;
import com.hiibox.houseshelter.activity.ManageRFIDCardActivity;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.net.FrameTools;
import com.hiibox.houseshelter.net.MembersInfoResult;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.view.ChangeRFidNameDialog;
import com.tencent.mm.sdk.platformtools.BackwardSupportUtil.BitmapFactory;

  
public class RFIDCardAdapter extends BaseAdapter {

    private Context context = null;
    @SuppressWarnings("unused")
    private FinalBitmap finalBitmap = null;
    private List<MembersInfoResult> list = null;
    private String deleteMemberStr = null;
    private ProgressDialog loginDialog = null;
    private int flag=-1;
    
    public RFIDCardAdapter(Context context, FinalBitmap finalBitmap) {
        super();
        this.context = context;
        this.finalBitmap = finalBitmap;
        deleteMemberStr = context.getResources().getString(R.string.delete_member);
        loginDialog = new ProgressDialog(context);
        loginDialog.setCancelable(true);
        loginDialog.setCanceledOnTouchOutside(false);
        loginDialog.setMessage(context.getResources().getString(R.string.deleting_member));
    }

    public void setList(List<MembersInfoResult> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
    	MembersInfoResult map = (MembersInfoResult) getItem(position);
        ViewHolder holder = new ViewHolder();
        if (null == convertView) {
            convertView = View.inflate(context, R.layout.lv_item_rfid_card_layout, null);
        }
        holder.nameTV = (TextView) convertView.findViewById(R.id.member_name_tv);
        holder.codeTV = (TextView) convertView.findViewById(R.id.rfid_card_code_tv);
        holder.portraitIV = (ImageView) convertView.findViewById(R.id.head_portrait_iv);
        holder.deleteIV = (ImageView) convertView.findViewById(R.id.delete_iv);
        
        final String name = map.nickname;
        final String cardNum = map.cardNum;
        holder.nameTV.setText(name);
        holder.codeTV.setText(cardNum);
        if (map.url.length>7) {        	
        	try {
        		URL url = new URL(FrameTools.getFrameData(map.url)); 
				holder.portraitIV.setImageBitmap(BitmapFactory.decodeStream(url.openStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
                                                                          
        holder.nameTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 Dialog dialog = new AlertDialog.Builder(context)
				 	.setMessage("是否修改随身保名称？")
	                .setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.dismiss();
	                        Intent intent=new Intent(context,ChangeRFidNameDialog.class);
	                        intent.putExtra("RFID",cardNum);

	                        context.startActivity(intent);
	                    }
	                })
	                .setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.dismiss();
	                    }
	                })
	                .create();
	                dialog.show();
			}
		});
        holder.deleteIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new AlertDialog.Builder(context)
                .setMessage(deleteMemberStr + name)
                .setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        flag=position;
                        sendOrder(cardNum, position);
                    }
                })
                .setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
                dialog.show();
            }
        });
        
        return convertView;
    }
    
    /**
	 * 发送请求到队列
	 */
    private void sendOrder(final String cardNum, final int position) {
    	loginDialog.show();
    	
    	//注销卡片
    	ZganJTWSService.toGetServerData(06,new String[]{ZganLoginService.getUserName(),cardNum}, handler);
    }

    class ViewHolder {
        TextView nameTV;
        TextView codeTV;
        ImageView portraitIV;
        ImageView deleteIV;
    }
    
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if(msg.what==1){
				Frame frame = (Frame) msg.obj;
				
				if (frame.subCmd == 06) {					
					String src=frame.strData;
					
					if(src!=null && !src.equals("") && src.equals("0")){
						MessageUtil.alertMessage(context, R.string.operate_success);
						list.remove(flag);
						loginDialog.dismiss();
						notifyDataSetChanged();
					}else{
						MessageUtil.alertMessage(context, R.string.operate_failed);
						loginDialog.dismiss();

					}
				}
			}
		}
    };

}
