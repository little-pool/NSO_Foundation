package com.example.foundation.tools;

import java.lang.Math;
import java.util.HashMap;

import org.apache.log4j.helpers.NullEnumeration;
/**
 * 该类主要任务：
 * 承载service中的netblock数据，将其处理成为一个可以给各个link对象分配的link_netblock，最后将所有的网段IP以dec的格式
 * 存储到map中返回给links对象使用
 * @author pool_little
 *
 */
public class netblock_operation {
	public netblock_operation(String IP , int NETMASK) {
		// TODO Auto-generated constructor stub
		this.ip_str = IP;
		this.netmask_int = NETMASK;
		this.block_num =  Math.pow(2, (32-netmask_int))/4;
		for(int i = 0 ; i < 4 ; i ++){
			this.ip[i] = Integer.parseInt(IP.split("\\.")[i]);
		}
	}
	private String ip_str;
	private int netmask_int;
	private int[] ip = new int[4];
	private double block_num;
	//用于存储该block所包含的所有网段IP的十进制数
	private HashMap<Integer, Long> netblock_map = new HashMap<>();
	
	//返回IP的十进制
	public long Get_decIP(){
		long decIP = 0;
		for(int i = 0 ; i < ip.length ; i ++){
			decIP += this.ip[3-i]*Math.pow(256, i);
		}
		return decIP;
	}
	
	//将所有的/30网段的IP十进制格式存入map中，并最终返回。
	public void Get_link_netblock_all(){
		long decIP = Get_decIP();
		double Num = block_num;
		for(int i = 0 ; i < Num ; i ++){
			netblock_map.put(i, decIP);
			decIP += 4;
		}
	}
	public String getIp_str() {
		return ip_str;
	}

	public void setIp_str(String ip_str) {
		this.ip_str = ip_str;
	}

	public int getNetmask_int() {
		return netmask_int;
	}

	public void setNetmask_int(int netmask_int) {
		this.netmask_int = netmask_int;
	}

	public int[] getIp() {
		return ip;
	}

	public void setIp(int[] ip) {
		this.ip = ip;
	}

	public double getBlock_num() {
		return block_num;
	}

	public void setBlock_num(double block_num) {
		this.block_num = block_num;
	}

	public HashMap<Integer, Long> getNetblock_map() {
		return netblock_map;
	}

	public void setNetblock_map(HashMap<Integer, Long> netblock_map) {
		this.netblock_map = netblock_map;
	}
	
}
