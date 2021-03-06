//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : See51
//  @ File Name : DeviceList.java
//  @ Date : 2012-5-30
//  @ Author : Eric Guo <gjl@my51c.com>
//
//

package com.hiibox.houseshelter.device;

import java.net.SocketAddress;
import java.security.acl.Group;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import android.bluetooth.BluetoothClass.Device;
import android.util.Log;

/**
 * 设备列表类
 * 
 * @author guo
 * 
 */
public class DeviceList implements Iterable<Device>
{

	private Map<String, Device> devices;
	private Map<String, Group> groups;
	private Group parent_group = null; // 列表父分组的ID，默认为null，即就是根分组
	private String grandParent_group = null; // 列表爷分组的ID，默认为null，即就是根分组
	private int version;
	private int server_version;

	/**
	 * 构造函数
	 */
	public DeviceList()
	{

		// 使用同步的map
		devices = Collections.synchronizedMap(new HashMap<String, Device>());
		groups = Collections.synchronizedMap(new HashMap<String, Group>());
		this.server_version = 0;
	}

	

	/**
	 * 
	 * @param devInfo
	 * @param Addr
	 */
	public void onReceivedMessage(DeviceLocalInfo devInfo, SocketAddress Addr)
	{
		String devId = devInfo.getCamSerial();
		Log.d("DeviceList", "onReceivedMessage " + devId);
		Device dev = devices.get(devId);
		if (dev == null) // 新设备
		{
			dev = new Device();
			add(devId, dev);
		}

	}

	/**
	 * 获取设备对象
	 * 
	 * @param deviceId
	 *            设备ID
	 * @return 设备对象
	 */
	public Device getDevice(String deviceId)
	{
		return this.devices.get(deviceId);
	}

	/**
	 * 获取组对象
	 * 
	 * @param groupId
	 *            组ID
	 * @return 组对象
	 */
	public Group getGroup(String groupId)
	{
		return this.groups.get(groupId);
	}

	/**
	 * 清除设备列表
	 */
	public void clear()
	{
		synchronized (this)
		{
			this.devices.clear();
			this.groups.clear();
		}

	}

	/**
	 * 获取设备列表版本号
	 * 
	 * @return 设备列表版本号
	 */
	public int getVersion()
	{
		return version;
	}

	/**
	 * 设置设备列表版本号
	 * 
	 * @param version
	 *            设备列表版本号
	 */
	public void setVersion(int version)
	{
		this.version = version;
	}



	public Group getParent_group()
	{
		return parent_group;
	}

	public void setParent_group(Group parent_group)
	{
		this.parent_group = parent_group;
	}

	public String getGrandParent_group()
	{
		return grandParent_group;
	}

	public void setGrandParent_group(String grandParent_group)
	{
		this.grandParent_group = grandParent_group;
	}

	public int getServerVersion()
	{
		return server_version;
	}

	public void setServeVersion(int server_version)
	{
		this.server_version = server_version;
	}

	public void updateSuccess()
	{
		this.version = this.server_version;
	}

	/**
	 * 向列表中添加设备
	 * 
	 * @param devId
	 * @param dev
	 */
	public void add(String devId, Device dev)
	{
		synchronized (this)
		{
			devices.put(devId, dev);
		}
	}

	/**
	 * 向列表中添加组
	 * 
	 * @param groId
	 * @param gro
	 */
	public void addGroups(String groId, Group gro)
	{
		synchronized (this)
		{
			groups.put(groId, gro);
		}
	}

	/**
	 * 获取设备数量
	 * 
	 * @return 设备数量
	 */
	public int getDeviceCount()
	{
		return this.devices.size();
	}

	/**
	 * 获取组数量
	 * 
	 * @return 组数量
	 */
	public int getGroupCount()
	{
		return this.groups.size();
	}

	/**
	 * 列表游标
	 */
	@Override
	public Iterator<Device> iterator()
	{
		return new DeviceIterator();
	}

	private class DeviceIterator implements Iterator<Device>
	{

		private Iterator<Map.Entry<String, Device>> iterator;

		public DeviceIterator()
		{
			this.iterator = devices.entrySet().iterator();
		}

		// 判断是否还有下一个元素，如果迭代到最后一个元素就返回false
		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		// 返回当前元素数据，并递增下标
		public Device next()
		{
			Map.Entry<String, Device> entry;
			entry = (Map.Entry<String, Device>) iterator.next();
			return (Device) entry.getValue();

		}

		// 这里不支持，抛出不支持操作异常
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * 组列表游标
	 */
	public Iterator<Group> getIteratorGroup()
	{
		return new GroupIterator();
	}

	private class GroupIterator implements Iterator<Group>
	{

		private Iterator<Map.Entry<String, Group>> iterator;

		public GroupIterator()
		{
			this.iterator = groups.entrySet().iterator();
		}

		// 判断是否还有下一个元素，如果迭代到最后一个元素就返回false
		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		// 返回当前元素数据，并递增下标
		public Group next()
		{
			Map.Entry<String, Group> entry = (Map.Entry<String, Group>) iterator.next();
			return (Group) entry.getValue();
		}

		// 这里不支持，抛出不支持操作异常
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
}
