package com.example.foundation.link;

import javax.management.loading.PrivateClassLoader;


public class link {
	public link(int linkID, String s_device, String s_interface_type, String s_interface_id,String d_device, String d_interface_type, String d_interface_id) {
		this.linkID = linkID;
		this.s_device = s_device;
		this.s_interface_id = s_interface_id;
		this.s_interface_type = s_interface_type;
		this.d_device = d_device;
		this.d_interface_id = d_interface_id;
		this.d_interface_type = d_interface_type;
	}

	private int linkID;
	private String s_device;
	private String s_interface_type;
	private String s_interface_id;
	private String s_IP;
	private String d_device;
	private String d_interface_type;
	private String d_interface_id;
	private String d_IP;
	private long net_block;
	private String netmask = "255.255.255.252";


	public String getNetmask() {
		return netmask;
	}
	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}


	public long getNet_block() {
		return net_block;
	}
	public void setNet_block(long net_block) {
		this.net_block = net_block;
	}
	public int getLinkID() {
		return linkID;
	}
	public void setLinkID(int linkID) {
		this.linkID = linkID;
	}
	public String getS_device() {
		return s_device;
	}
	public void setS_device(String s_device) {
		this.s_device = s_device;
	}
	public String getS_interface_type() {
		return s_interface_type;
	}
	public void setS_interface_type(String s_interface_type) {
		this.s_interface_type = s_interface_type;
	}
	public String getS_interface_id() {
		return s_interface_id;
	}
	public void setS_interface_id(String s_interface_id) {
		this.s_interface_id = s_interface_id;
	}
	public String getS_IP() {
		return s_IP;
	}
	public void setS_IP(String s_IP) {
		this.s_IP = s_IP;
	}
	public String getD_device() {
		return d_device;
	}
	public void setD_device(String d_device) {
		this.d_device = d_device;
	}
	public String getD_interface_type() {
		return d_interface_type;
	}
	public void setD_interface_type(String d_interface_type) {
		this.d_interface_type = d_interface_type;
	}
	public String getD_interface_id() {
		return d_interface_id;
	}
	public void setD_interface_id(String d_interface_id) {
		this.d_interface_id = d_interface_id;
	}
	public String getD_IP() {
		return d_IP;
	}
	public void setD_IP(String d_IP) {
		this.d_IP = d_IP;
	}




}
