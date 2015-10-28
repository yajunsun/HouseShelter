package com.hiibox.houseshelter.net;

import java.util.ArrayList;
import java.util.List;

  
public class MembersInfoResult {

	public int totalMembers = -1;              
	public int currIndex = -1;           

	public String cardNum="";

	public String nickname="";

	public byte[] url=null;

	public int status=0;
	
	public static List<MembersInfoResult> membersList = null;           
	
	public static List<MembersInfoResult> getMembersList() {
		return membersList;
	}

	public static int parseMembersInfo(Frame f) {
		if (null == membersList) {
			membersList = new ArrayList<MembersInfoResult>();
		}
		MembersInfoResult result = null;
		ArrayList<byte[]> arrData = FrameTools.split(f.aryData, '\t');
		int len = arrData.size();
		if (len < 2) {
			return -1;
		}
		result = new MembersInfoResult();
		result.totalMembers = Integer.parseInt(FrameTools.getFrameData(arrData
				.get(1)));
		result.currIndex = Integer.parseInt(FrameTools.getFrameData(arrData
				.get(2)));
		
		result.cardNum = FrameTools.getFrameData(arrData.get(3));
		result.nickname = FrameTools.getFrameData(arrData.get(4));
		result.status = Integer.parseInt(FrameTools.getFrameData(arrData.get(5)));
		if (len > 6) {
			result.url = arrData.get(6);

		}
		membersList.add(result);
		                                 
		                              
		                                                                                                                       
		return (result.totalMembers - result.currIndex);
	}
	
	public static MembersInfoResult parse(Frame f) {
		MembersInfoResult result = null;
		ArrayList<byte[]> arrData = FrameTools.split(f.aryData, '\t');
		int len = arrData.size();
		if (len < 3) {
			return result;
		}
		result = new MembersInfoResult();
		result.totalMembers = Integer.parseInt(FrameTools.getFrameData(arrData.get(1)));
		result.currIndex = Integer.parseInt(FrameTools.getFrameData(arrData.get(2)));
		result.cardNum = FrameTools.getFrameData(arrData.get(3));
		result.nickname = FrameTools.getFrameData(arrData.get(4));
		result.status = Integer.parseInt(FrameTools.getFrameData(arrData.get(5)));
		if (len > 6) {                                                                                        
          
			result.url = arrData.get(6);
		}

		return result;
	}
	public static MembersInfoResult parse2(Frame f) {
		MembersInfoResult result = null;
		String arrData[]=f.strData.split("\t");
		int len = arrData.length;
		if (len < 2) {
			return result;
		}
		result = new MembersInfoResult();
		result.totalMembers = Integer.parseInt(arrData[0]);
		result.currIndex = Integer.parseInt(arrData[1]);
		result.cardNum = arrData[2];
		result.nickname = arrData[3];
		result.status = Integer.parseInt(arrData[4]);
		if (len > 5) {                                   
                                              
                                                                                 
          
			result.url =arrData[5].getBytes();
		}

		return result;
	}
}
