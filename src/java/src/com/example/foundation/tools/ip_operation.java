package com.example.foundation.tools;
/**
 * 该类用来对IP进行各种类型的转换，由于link对象得到的net_block信息可能为dec或String格式的，我们负责提供转换方法。
 * @author pool_little
 *
 */
public class ip_operation {
	public ip_operation() {
		// TODO Auto-generated constructor stub
	}
	//输入long返回点分十进制的IP
	public StringBuilder Get_ipSTR(long IP){
		StringBuilder ipsrc = new StringBuilder(15);
		for(int i = 0 ; i < 4 ; i ++){
			ipsrc.insert(0, Long.toString(IP & 0xff));
			if(i < 3)
				ipsrc.insert(0, ".");
			IP = IP >> 8;
		}
		return ipsrc;
	}
	//输入点分十进制返回long类型的IP
	public long Get_ipDEC(String IP){
		int[] ip = new int[4];
		long result = 0;
		for(int i = 0 ; i < 4 ; i ++){
			ip[i] = Integer.parseInt(IP.split("\\.")[i]);
		}
		for(int i = 3 ; i >= 0 ; i --){
			result += ip[i]*Math.pow(256, 3-i);
		}
		return result;
		
	}
	
}
