package com.example.foundation.tools;
import org.apache.log4j.chainsaw.Main;

import com.tailf.navu.NavuContainer;
import com.tailf.navu.NavuException;
import com.tailf.navu.NavuList;
import com.tailf.navu.NavuNode;

import sun.print.resources.serviceui;

public class device_operation {
	public device_operation() {
		// TODO Auto-generated constructor stub
	}
	public NavuContainer devices;
	
	public String get_deviceNed(String deviceName) throws NavuException{
		//好麻烦
		NavuList device = devices.list("device");
		NavuContainer targetDevice = device.elem(deviceName);
		NavuContainer deviceType = targetDevice.container("device-type");
		return deviceType.container("cli").leaf("ned-id").valueAsString();
		
	}
}
