package com.hiibox.houseshelter.device;


public class Schedule implements Cloneable{
	private byte bStatus;	        ///< schedule status ( 0:disable 1:录像 2:报警时录像 }
	private byte nDay;		        ///< schedule day of week (1:Mon 2:Tue 3:Wed 4:Thr 5:Fri 6:Sat 7:Sun 8:Everyday 9:Working day)
	private byte nStartHour;	    ///< Hour from 0 to 23.
	private byte nStartMin;	    ///< Minute from 0 to 59.
	private byte nStartnSec;	    ///< Second from 0 to 59.
	private byte nDurationHour;	///< Hour from 0 to 23.
	private byte nDurationMin;	    ///< Minute from 0 to 59.
	private byte nDurationSec;	    ///< Second from 0 to 59.
	
	public Schedule(byte[] buf){
		bStatus = buf[0];
		nDay = buf[1];
		nStartHour = buf[2];
		nStartMin = buf[3];
		nStartnSec = buf[4];
		nDurationHour = buf[5];
		nDurationMin = buf[6];
		nDurationSec = buf[7];
	}
	
	public Schedule() {
		// TODO Auto-generated constructor stub
		bStatus = 0;
		nDay = 0;
		nStartHour = 0;
		nStartMin = 0;
		nStartnSec = 0;
		nDurationHour = 0;
		nDurationMin = 0;
		nDurationSec = 0;
	}

	public byte[] getScheduleBuffer(){
		byte buf[] = new byte[8];
		buf[0] = bStatus;
		buf[1] = nDay;
		buf[2] = nStartHour;
		buf[3] = nStartMin;
		buf[4] = nStartnSec;
		buf[5] = nDurationHour;
		buf[6] = nDurationMin;
		buf[7] = nDurationSec;		
		return buf;
	}
	
	@Override
	public Object clone(){
		Schedule o = null;
		try {
			o = (Schedule) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;		
	}
	/**
	 * @return the bStatus
	 */
	public byte getbStatus() {
		return bStatus;
	}
	/**
	 * @param bStatus the bStatus to set
	 */
	public void setbStatus(byte bStatus) {
		this.bStatus = bStatus;
	}
	/**
	 * @return the nDay
	 */
	public byte getnDay() {
		return nDay;
	}
	/**
	 * @param nDay the nDay to set
	 */
	public void setnDay(byte nDay) {
		this.nDay = nDay;
	}
	/**
	 * @return the nStartHour
	 */
	public byte getnStartHour() {
		return nStartHour;
	}
	/**
	 * @param nStartHour the nStartHour to set
	 */
	public void setnStartHour(byte nStartHour) {
		this.nStartHour = nStartHour;
	}
	/**
	 * @return the nStartMin
	 */
	public byte getnStartMin() {
		return nStartMin;
	}
	/**
	 * @param nStartMin the nStartMin to set
	 */
	public void setnStartMin(byte nStartMin) {
		this.nStartMin = nStartMin;
	}
	/**
	 * @return the nStartnSec
	 */
	public byte getnStartnSec() {
		return nStartnSec;
	}
	/**
	 * @param nStartnSec the nStartnSec to set
	 */
	public void setnStartnSec(byte nStartnSec) {
		this.nStartnSec = nStartnSec;
	}
	/**
	 * @return the nDurationHour
	 */
	public byte getnDurationHour() {
		return nDurationHour;
	}
	/**
	 * @param nDurationHour the nDurationHour to set
	 */
	public void setnDurationHour(byte nDurationHour) {
		this.nDurationHour = nDurationHour;
	}
	/**
	 * @return the nDurationMin
	 */
	public byte getnDurationMin() {
		return nDurationMin;
	}
	/**
	 * @param nDurationMin the nDurationMin to set
	 */
	public void setnDurationMin(byte nDurationMin) {
		this.nDurationMin = nDurationMin;
	}
	/**
	 * @return the nDurationSec
	 */
	public byte getnDurationSec() {
		return nDurationSec;
	}
	/**
	 * @param nDurationSec the nDurationSec to set
	 */
	public void setnDurationSec(byte nDurationSec) {
		this.nDurationSec = nDurationSec;
	}
}
