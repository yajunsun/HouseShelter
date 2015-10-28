package com.hiibox.houseshelter.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
    
  
  
  
public class AlarmRecordsResult {

	private List<Map<String, String>> alarmList = null;
	
	private int currRecords = 0;
	
	public List<Map<String, String>> getAlarmList() {

		return alarmList;
	}
	
	public void clearList() {
		if (null != alarmList && alarmList.size() > 0) {
			currRecords = 0;
			alarmList.clear();
		}
	}
	
	    
  
  
  
  
  
	public int getAlarmRecords(Frame f) {
		if (null == alarmList) {
			alarmList = new ArrayList<Map<String,String>>();
		}
		ArrayList<byte[]> arrData = FrameTools.split(f.aryData, '\t');
		if(arrData.size() < 2) {
			return -1;
		}
		int totalRecords = Integer.parseInt(FrameTools.getFrameData(arrData.get(0)));
		

		if (totalRecords == 0) {
			return 0;
		}
                                                                               
		for (int m = 3; m < arrData.size(); m ++) {
			String[] records = FrameTools.getFrameData(arrData.get(m)).split(",");
			Map<String,String> map = new HashMap<String, String>();
			map.put("filedId", records[0]);
			map.put("warningType", records[1]);
			map.put("time", records[2]);
			if (records.length == 4) {
				map.put("temperature", records[3]);
			} else {
				map.put("temperature", "");
			}
			alarmList.add(map);
		}
		return totalRecords;
	}

}
