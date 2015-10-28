package com.hiibox.houseshelter.activity;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.adapter.ImprintingAdapter;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.AlarmRecordsResult;
import com.hiibox.houseshelter.net.DefenceRecordsResult;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.net.MembersInfoResult;
import com.hiibox.houseshelter.net.OutRecordsResult;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.view.PullToRefreshView;
import com.hiibox.houseshelter.view.PullToRefreshView.OnFooterRefreshListener;
import com.hiibox.houseshelter.view.PullToRefreshView.OnHeaderRefreshListener;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.jtws.ZganJTWSServiceTools;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;

/**
 * @Description 印记
 * @Author wangjiang
 * @Create Date 2013-10-26 下午3:57:30
 * @Modified By
 * @Modified Date
 * @Modified Description
 */
public class ImprintingActivity extends ShaerlocActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener, OnDateSetListener,
		OnScrollListener {

	@ViewInject(id = R.id.back_iv, click = "onClick")
	ImageView backIV;
	@ViewInject(id = R.id.pull_to_refresh_view)
	PullToRefreshView refreshView;
	@ViewInject(id = R.id.records_lv, itemClick = "onItemClick")
	ListView recordsLV;
	@ViewInject(id = R.id.access_records_tv, click = "onClick")
	TextView accessTV;
	@ViewInject(id = R.id.defence_records_tv, click = "onClick")
	TextView defenceTV;
	@ViewInject(id = R.id.warning_records_tv, click = "onClick")
	TextView warningTV;
	@ViewInject(id = R.id.date_tv, click = "onClick")
	TextView dateTV;
	@ViewInject(id = R.id.type_tv, click = "onClick")
	TextView typeTV;
	@ViewInject(id = R.id.root_layout)
	RelativeLayout rootLayout;
	@ViewInject(id = R.id.progress_bar)
	ProgressBar bar;

	private ImprintingAdapter adapter = null;
	private List<Map<String, String>> accessList = null;
	private List<Map<String, String>> defenceList = null;
	private List<Map<String, String>> warningList = null;
	private int index = 0; // 0:出入记录; 1:安防记录; 2:报警记录
	private DatePickerDialog dateDialog = null;
	private String queryType1, queryType2, queryType3, queryTime1, queryTime2,
			queryTime3;
	private int queryIndex = 0;
	private String currDate = null;
	private AlarmRecordsResult alarmRecordsResult = null;
	private OutRecordsResult outRecordsResult = null;
	private DefenceRecordsResult defenceRecordsResult = null;

	private int intPageNo = 0;
	private int intLastPageNo = 0;
	private int pageType = 0x01;

	String RfidCard = null;
	String QueryTime = null;

	private boolean bottomtatues = false;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Frame frame = (Frame) msg.obj;
			
			if (defenceList != null && bottomtatues==false) {
				defenceList.clear();
			}
			if (accessList != null && bottomtatues==false) {
				accessList.clear();
			}
			if (warningList != null && bottomtatues==false) {
				warningList.clear();
			}
			
			if (null == frame) {
				return;
			}

			int subCmd = frame.subCmd;

			if (msg.what == 1) {

				if (frame.strData.equals("1")) {

					bar.setVisibility(View.GONE);
					return;
				}
				String datString[] = frame.strData.split("\t");

				if (subCmd == 68) { // 出入记录查询
					if (Integer.parseInt(datString[0]) > 0) {
						int rc = outRecordsResult.getResult(frame);
						if (rc > 0) {

							accessList = outRecordsResult.getList();
							if (null != accessList && accessList.size() > 0) {
								adapter.setList(accessList, 0);
							} else {								
									adapter.setList(accessList, 0);
								
							}
							bar.setVisibility(View.GONE);
						} else {
							if (intPageNo == 0) {

							}
							intPageNo = intLastPageNo;
						}
					} else {
						
						adapter.setList(accessList, 0);
						

						bar.setVisibility(View.GONE);
						if (intPageNo == 0) {

							initPageData();

						}
						intPageNo = intLastPageNo;

					}

				} else if (subCmd == 69) { // 布防记录查询

					if (Integer.parseInt(datString[0]) > 0) {
						int rc = defenceRecordsResult.getResult(frame);

						if (rc > 0) {
							defenceList = defenceRecordsResult.getList();
							if (null != defenceList && defenceList.size() > 0) {
								adapter.setList(defenceList, 1);
							}
							bar.setVisibility(View.GONE);
						} else {
							
								adapter.setList(defenceList, 1);

							

							if (intPageNo == 0) {
							}
							intPageNo = intLastPageNo;
						}

					} else {
						
							adapter.setList(defenceList, 1);

						
						bar.setVisibility(View.GONE);
						if (intPageNo == 0) {
							initPageData();
						}
						intPageNo = intLastPageNo;
					}

				} else if (subCmd == 70) { // 报警记录查询
					if (Integer.parseInt(datString[0]) > 0) {

						int rc = alarmRecordsResult.getAlarmRecords(frame);

						if (rc > 0) {
							warningList = alarmRecordsResult.getAlarmList();

							if (null != warningList && warningList.size() > 0) {
								adapter.setList(warningList, 2);
							} else {

							}
							bar.setVisibility(View.GONE);
						} else {
							
								adapter.setList(warningList, 2);

							

							if (intPageNo == 0) {

							}
							intPageNo = intLastPageNo;

						}
					} else {
						
						adapter.setList(warningList, 2);

						

						bar.setVisibility(View.GONE);
						if (intPageNo == 0) {
							initPageData();
						}
						intPageNo = intLastPageNo;

					}

				}
				bottomtatues = false;
			} else { // 超时
				MessageUtil.alertMessage(mContext, R.string.network_timeout);
				bar.setVisibility(View.GONE);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imprinting_layout);
		queryIndex = getIntent().getIntExtra("queryIndex", 0);

		refreshView.setHeadRefresh(false);
		refreshView.setFooterRefresh(false);

		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		currDate = getFormatDate(year, month, day);
		dateTV.setText(currDate);
		dateDialog = new DatePickerDialog(this, this, year, month, day);

		adapter = new ImprintingAdapter(mContext, finalBitmap);
		recordsLV.setAdapter(adapter);
		recordsLV.setOnScrollListener(this);
		refreshView.setOnHeaderRefreshListener(this);
		refreshView.setOnFooterRefreshListener(this);

		initPageData();

		if (queryIndex == 1) { // 温度报警

		} else if (queryIndex == 2) { // 入侵报警
			index = 2;
			pageType = 0x11;
			// sendRequest(0x11, null, null,intPageNo);
			warningTV.setBackgroundResource(R.drawable.bg_warning_selected);
			accessTV.setBackgroundDrawable(null);
			defenceTV.setBackgroundDrawable(null);
		}

		sendRequest(pageType, null, null, intPageNo);

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 滚动时一直回调，直 到停止滚动时才停止回调。单击时回调一次。
		// firstVisibleItem：当前能看见的第一个列表项ID（从0开始）
		// visibleItemCount：当前能看见的列表项个数（小半个也算）
		// totalItemCount：列表项共数
		// TODO Auto-generated method stub

		if (firstVisibleItem + visibleItemCount == totalItemCount
				&& totalItemCount > 0) {

			bottomtatues = true;
		}
	}

	// 初始化数据
	private void initPageData() {
		intLastPageNo = 0;
		intPageNo = 0;

		RfidCard = null;
		QueryTime = null;

		alarmRecordsResult = new AlarmRecordsResult();
		outRecordsResult = new OutRecordsResult();
		defenceRecordsResult = new DefenceRecordsResult();

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && bottomtatues) {
			// 当滑到底部时自动加载

			intLastPageNo = intPageNo;
			intPageNo++;

			sendRequest(pageType, RfidCard, QueryTime, intPageNo);

			
		}
	}

	private String getFormatDate(int year, int month, int day) {
		StringBuilder sb = new StringBuilder();
		sb.append(year).append("/");
		if (month < 9) {
			sb.append("0");
		}
		sb.append((month + 1)).append("/");
		if (day < 10) {
			sb.append("0");
		}
		sb.append(day);
		return sb.toString();
	}

	/**
	 * 记录查询请求
	 * 
	 * @param queryCode
	 *            请求码
	 * @param rfidCard
	 * @param date
	 */
	private void sendRequest(int queryCode, String rfidCard, String date,
			int intPage) {
		if (!ZganJTWSServiceTools.isConnect) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			return;
		}
		switch (queryCode) {
		case 0x01: // 查询全部出入记录
			//outRecordsResult.clearList();
			ZganJTWSService.toGetServerData(
					68,
					new String[] { ZganLoginService.getUserName(), "0",
							Integer.toString(intPage) }, handler, 2);
			break;
		case 0x02: // 查询指定人的出入记录
			//outRecordsResult.clearList();
			ZganJTWSService.toGetServerData(68,
					new String[] { ZganLoginService.getUserName(), "1",
							rfidCard, Integer.toString(intPage) }, handler, 2);

			break;
		case 0x03: // 查询指定时间段的出入记录
			//outRecordsResult.clearList();
			ZganJTWSService.toGetServerData(68,
					new String[] { ZganLoginService.getUserName(), "2", date,
							Integer.toString(intPage) }, handler, 2);

			break;
		case 0x04: // 查询指定人在指定时间段的出入记录
			//outRecordsResult.clearList();
			ZganJTWSService.toGetServerData(68,
					new String[] { ZganLoginService.getUserName(), "3",
							rfidCard, date, Integer.toString(intPage) },
					handler, 2);

			break;
		case 0x05: // 查询全部布防撤防记录
			//defenceRecordsResult.clearList();
			ZganJTWSService.toGetServerData(
					69,
					new String[] { ZganLoginService.getUserName(), "0",
							Integer.toString(intPage) }, handler, 2);

			break;
		case 0x06: // 查询特定日期布防撤防记录
			//defenceRecordsResult.clearList();
			ZganJTWSService.toGetServerData(69,
					new String[] { ZganLoginService.getUserName(), "1", date,
							Integer.toString(intPage) }, handler, 2);

			break;
		case 0x07: // 查询全部布防记录
			//defenceRecordsResult.clearList();
			ZganJTWSService.toGetServerData(69,
					new String[] { ZganLoginService.getUserName(), "2", "1",
							Integer.toString(intPage) }, handler, 2);

			break;
		case 0x08: // 查询全部撤防记录
			//defenceRecordsResult.clearList();
			ZganJTWSService.toGetServerData(69,
					new String[] { ZganLoginService.getUserName(), "3", "2",
							Integer.toString(intPage) }, handler, 2);

			break;
		case 0x09: // 查询全部报警记录
			//alarmRecordsResult.clearList();

			ZganJTWSService.toGetServerData(
					70,
					new String[] { ZganLoginService.getUserName(), "0",
							Integer.toString(intPage) }, handler, 2);

			break;

		case 0x11: // 查询入侵报警记录(不能查询特定日期记录)
			//alarmRecordsResult.clearList();

			ZganJTWSService.toGetServerData(
					70,
					new String[] { ZganLoginService.getUserName(), "2",
							Integer.toString(intPage) }, handler, 2);

			break;
		case 0x12: // 查询特定日期的报警记录
			//alarmRecordsResult.clearList();

			ZganJTWSService.toGetServerData(70,
					new String[] { ZganLoginService.getUserName(), "3", date,
							Integer.toString(intPage) }, handler, 2);

			break;
		case 0x13: // 指定时间布防查询
			//defenceRecordsResult.clearList();
			ZganJTWSService.toGetServerData(69,
					new String[] { ZganLoginService.getUserName(), "4", date,
							Integer.toString(intPage) }, handler, 2);

			break;
		case 0x14: // 指定时间撤防查询
			//defenceRecordsResult.clearList();
			ZganJTWSService.toGetServerData(69,
					new String[] { ZganLoginService.getUserName(), "5", date,
							Integer.toString(intPage) }, handler, 2);

			break;
		default:
			break;
		}
		bar.setVisibility(View.VISIBLE);
	}

	public void onClick(View v) {
		int vId = v.getId();
		Intent intent = new Intent();

		initPageData();

		switch (vId) {
		case R.id.back_iv:
			//modified by yajunsun 20160929
			//startActivity(new Intent(mContext, HomepageActivity.class));
			MianActivity.getScreenManager().exitActivity(mActivity);
			
			break;
		case R.id.access_records_tv:
			index = 0;
			if (TextUtils.isEmpty(queryType1)) {
				typeTV.setText(getString(R.string.all));
			} else {
				typeTV.setText(queryType1);
			}
			if (TextUtils.isEmpty(queryTime1)) {
				dateTV.setText(currDate);
			} else {
				dateTV.setText(queryTime1);
			}
			accessTV.setBackgroundResource(R.drawable.bg_access_selected);
			defenceTV.setBackgroundDrawable(null);
			warningTV.setBackgroundDrawable(null);
			pageType = 0x01;
			sendRequest(pageType, null, null, intPageNo);
			break;
		case R.id.defence_records_tv:
			index = 1;
			if (TextUtils.isEmpty(queryType2)) {
				typeTV.setText(getString(R.string.all));
			} else {
				typeTV.setText(queryType2);
			}
			if (TextUtils.isEmpty(queryTime2)) {
				dateTV.setText(currDate);
			} else {
				dateTV.setText(queryTime2);
			}
			defenceTV.setBackgroundResource(R.drawable.bg_defence_selected);
			accessTV.setBackgroundDrawable(null);
			warningTV.setBackgroundDrawable(null);
			pageType = 0x05;
			sendRequest(pageType, null, null, intPageNo);
			break;
		case R.id.warning_records_tv:
			index = 2;
			if (TextUtils.isEmpty(queryType3)) {
				typeTV.setText(getString(R.string.all));
			} else {
				typeTV.setText(queryType3);
			}
			if (TextUtils.isEmpty(queryTime3)) {
				dateTV.setText(currDate);
			} else {
				dateTV.setText(queryTime3);
			}
			warningTV.setBackgroundResource(R.drawable.bg_warning_selected);
			accessTV.setBackgroundDrawable(null);
			defenceTV.setBackgroundDrawable(null);
			pageType = 0x09;
			sendRequest(pageType, null, null, intPageNo);
			break;
		case R.id.date_tv:

			dateDialog.show();

			break;
		case R.id.type_tv:
			Intent typeIntent = new Intent(mActivity,
					FamilyMembersActivity.class);
			typeIntent.putExtra("selectedIndex", index);
			startActivityForResult(typeIntent, 0x201);
			break;
		default:
			break;
		}

		if (intent.getClass() != null) {
			startActivity(intent);
			MianActivity.getScreenManager().exitActivity(mActivity);
		}
	}

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (index == 2) {
			Intent intent = new Intent();
			Map<String, String> map = warningList.get(position);
			String filedId = map.get("filedId");
			// 3入侵报警 16:温度报警
			if (map.get("warningType").equals("16")) { // 温度异常
				intent.setClass(this, TemperatureAbnormalActivity.class);
				intent.putExtra("time", map.get("time"));
			} else { // 入侵
				intent.setClass(this, InvadeActivity.class);
			}

			intent.putExtra("filedId", filedId);
			startActivity(intent);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0x201 && resultCode == RESULT_OK) {
			String member = data.getStringExtra("member");

			if (!TextUtils.isEmpty(member)) {
				typeTV.setText(member);
				if (index == 0) {
					queryType1 = member;
					queryAccessRecords(member, dateTV.getText().toString());
				} else if (index == 1) {
					queryType2 = member;
					queryDefenceRecords(member, dateTV.getText().toString());

				} else if (index == 2) {
					queryType3 = member;

					if (member.equals(getString(R.string.temperature_abnormal))) {
						pageType = 0x10;
					} else if (member.equals(getString(R.string.invade))) {
						pageType = 0x11;
					} else if (member.equals(getString(R.string.all))) {
						pageType = 0x09;

					}

					sendRequest(pageType, null, null, 0);
				}
			}
		}
	}

	/**
	 * 出入记录查询（子类查询）
	 * 
	 * @param member
	 * @param time
	 */
	private void queryAccessRecords(String member, String time) {

		String tp = getString(R.string.all);
		String dt = currDate;

		List<MembersInfoResult> membersList = HomepageActivity.membersList;
		if (null != membersList) {
			int len = membersList.size();
			for (int i = 0; i < len; i++) {
				if (membersList.get(i).nickname.trim().equals(member)) {
					RfidCard = membersList.get(i).cardNum;
					break;
				}
			}
		}

		// initPageData();

		if (tp.equals(member) && dt.equals(time)) {

		} else if (tp.equals(member) && !dt.equals(time)) {
			QueryTime = time;
			pageType = 0x03;
		} else if (!tp.equals(member) && dt.equals(time)) {
			if (null != RfidCard) {
				// sendRequest(0x02, RfidCard, null,nub2);

				pageType = 0x02;
			}
		} else if (!tp.equals(member) && !dt.equals(time)) {
			// sendRequest(0x04, RfidCard, time,nub4);

			QueryTime = time;
			pageType = 0x04;
		}

		sendRequest(pageType, RfidCard, QueryTime, intPageNo);
	}

	/**
	 * 安防记录查询（子类查询）
	 * 
	 * @param DefencetType
	 * @param time
	 */
	private void queryDefenceRecords(String defenceType, String time) {

		String tp = getString(R.string.all);
		String dt = currDate;

		initPageData();

		if (tp.equals(defenceType) && dt.equals(time)) {
			pageType = 0x05;
		} else if (tp.equals(defenceType) && !dt.equals(time)) {
			pageType = 0x06;
			QueryTime = time;

		} else if (!tp.equals(defenceType) && dt.equals(time)) {
			if (defenceType.equals(getString(R.string.defence))) {
				pageType = 0x07;

			} else if (defenceType.equals(getString(R.string.cancel_defence))) {
				pageType = 0x08;

			}
		} else if (!tp.equals(defenceType) && !dt.equals(time)) {
			if (defenceType.equals(getString(R.string.defence))) {
				pageType = 0x13;
				QueryTime = time;

			} else if (defenceType.equals(getString(R.string.cancel_defence))) {

				pageType = 0x14;
				QueryTime = time;
			}
		}

		sendRequest(pageType, null, QueryTime, intPageNo);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		refreshView.onHeaderRefreshComplete();
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		refreshView.onFooterRefreshComplete();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//modified by yajunsun 20150929
			//startActivity(new Intent(mContext, HomepageActivity.class));
			MianActivity.getScreenManager().exitActivity(mActivity);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		String date = getFormatDate(year, monthOfYear, dayOfMonth);
		dateTV.setText(date);
		if (index == 0) {
			queryTime1 = date;
			queryAccessRecords(typeTV.getText().toString(), date);
		} else if (index == 1) {
			queryTime2 = date;
			queryDefenceRecords(typeTV.getText().toString(), date);
		} else if (index == 2) {
			// queryTime3 = date;

			// 报警记录条件查询
			initPageData();
			pageType = 0x12;
			QueryTime = date;
			sendRequest(pageType, null, QueryTime, intPageNo);

		}
	}

}
