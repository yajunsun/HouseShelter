package com.hiibox.houseshelter.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ��ȡ��ǰ�û��豸��Ϣ
 * @Description 
 * @Author wangjiang
 * @Create Date 2013-12-17 ����3:44:49
 * @Modified By 
 * @Modified Date 
 * @Modified Description
 */
public class DeviceInfoResult {

	private int count = 0; // 设备个数
    private String defaultDevCode = null; // 默认设备号
    private List<Map<String, Object>> deviceList = null;
    
    public DeviceInfoResult(String devicCode) {
        super();
        this.defaultDevCode = devicCode;
        deviceList = new ArrayList<Map<String,Object>>();
    }

    public int getCount() {
        return count;
    }
    
    public List<Map<String, Object>> getDeviceList() {
        return deviceList;
    }
    
    public void praseDeviceInfo(Frame f) {
        if (null == f) {
            return;
        }
        ArrayList<byte[]> arrData = FrameTools.split(f.aryData, '\t');
        if (arrData.size() < 2) {
            return;
        }
        count = Integer.parseInt(FrameTools.getFrameData(arrData.get(1)));

        for (int i = 2; i < arrData.size(); i ++) {
            String[] records = FrameTools.getFrameData(arrData.get(i)).split("\n");
            if (records.length != 4) {
                continue;
            }

            String deviceCode = records[0];
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("address", records[1]);
            map.put("deviceNumber", deviceCode);
            if (null != defaultDevCode) {
                if (deviceCode.equals(defaultDevCode)) {
                    map.put("defaultAddr", true);
                } else {
                    map.put("defaultAddr", false);
                }
            } else {
                map.put("defaultAddr", false);
            }
            deviceList.add(map);
        }
    }
}
