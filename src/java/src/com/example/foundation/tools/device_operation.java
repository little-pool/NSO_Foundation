package com.example.foundation.tools;

import java.net.InterfaceAddress;
import org.apache.log4j.chainsaw.Main;
import com.tailf.navu.NavuContainer;
import com.tailf.navu.NavuException;
import com.tailf.navu.NavuList;
import com.tailf.navu.NavuNode;


public class device_operation {
	
	public NavuContainer devices;
	
	public device_operation(NavuContainer devices) {
		// TODO Auto-generated constructor stub
		this.devices = devices;
	}
	/**
	 * return "cisco-ios-xr-id:cisco-ios-xr" or "cisco-ios-xr-id:cisco-ios-xr"
	 * @param deviceName
	 * @return
	 * @throws NavuException
	 */
	public String get_deviceNed(String deviceName) throws NavuException{
		//好麻烦
		NavuList device = devices.list("device");
		NavuContainer targetDevice = device.elem(deviceName);
		NavuContainer deviceType = targetDevice.container("device-type");
		return deviceType.container("cli").leaf("ned-id").valueAsString();
	}
	
	public String get_deviceIP(String deviceName,String intType,String intID) throws NavuException{
		NavuList device = devices.list("device");
		NavuContainer targetDevice = device.elem(deviceName);
		NavuContainer config = targetDevice.container("config");
		NavuContainer interface_ = config.container("ios:interface");
		NavuList int_type = interface_.list(intType);
		NavuContainer int_id = int_type.elem(intID);
		NavuContainer ip = int_id.container("address");
		NavuContainer primary = ip.container("primary");
		return primary.leaf("address").valueAsString();
		
	}
}
