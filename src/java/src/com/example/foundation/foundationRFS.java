package com.example.foundation;

import com.example.foundation.link.link;
import com.example.foundation.namespaces.*;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.swing.text.AbstractDocument.LeafElement;
import com.tailf.conf.*;
import com.tailf.navu.*;
import com.tailf.ncs.ns.Ncs;
import com.tailf.dp.*;
import com.tailf.dp.annotations.*;
import com.tailf.dp.proto.*;
import com.tailf.dp.services.*;
import com.tailf.ncs.template.Template;
import com.tailf.ncs.template.TemplateVariables;
import com.example.foundation.link.*;
import com.example.foundation.tools.*;

public class foundationRFS {

    /**
     * Create callback method.
     * This method is called when a service instance committed due to a create
     * or update event.
     *
     * This method returns a opaque as a Properties object that can be null.
     * If not null it is stored persistently by Ncs.
     * This object is then delivered as argument to new calls of the create
     * method for this service (fastmap algorithm).
     * This way the user can store and later modify persistent data outside
     * the service model that might be needed.
     *
     * @param context - The current ServiceContext object
     * @param service - The NavuNode references the service node.
     * @param ncsRoot - This NavuNode references the ncs root.
     * @param opaque  - Parameter contains a Properties object.
     *                  This object may be used to transfer
     *                  additional information between consecutive
     *                  calls to the create callback.  It is always
     *                  null in the first call. I.e. when the service
     *                  is first created.
     * @return Properties the returning opaque instance
     * @throws ConfException
     */
    @ServiceCallback(servicePoint="link_service-servicepoint",
        callType=ServiceCBType.CREATE)
    public Properties create_LinkService(ServiceContext context,
                             NavuNode service,
                             NavuNode ncsRoot,
                             Properties opaque)
                             throws ConfException {
    	//link_service部分
    	NavuLeaf part_name = service.leaf("part_name");
    	NavuLeaf link_num = service.leaf("link_num");
    	NavuContainer netblock = service.container("netblock");
    	NavuList link = service.list("link");

    	System.out.println(part_name.valueAsString()+"   ");
    	int i = 0;
    	//外层map，用于存放所有的link对象。
    	HashMap<Integer, link> links= new HashMap<>();
    	//外层map，用于存放给所有link对象分配的netblock地址的十进制数据的。
    	HashMap<Integer, Long> netBlock_all = new HashMap<>();
    	//单个part全局地址资源取值
		NavuLeaf netaddress = netblock.leaf("netaddress");
		NavuLeaf netmask = netblock.leaf("netmask");
		//得到存储所有/30网段的map
    	netblock_operation netBLK = new netblock_operation(netaddress.valueAsString(), Integer.parseInt(netmask.valueAsString()));
    	netBLK.Get_link_netblock_all();
    	netBlock_all = netBLK.getNetblock_map();
    	for(NavuContainer link_info : link){
    		//设备及接口资源取值
    		NavuContainer s_node = link_info.container("s_node");
    		NavuLeaf s_device = s_node.leaf("device");
    		NavuLeaf s_int_type = s_node.container("interface").leaf("int_type");
    		NavuLeaf s_int_id = s_node.container("interface").leaf("int_id");
    		NavuContainer d_node = link_info.container("d_node");
    		NavuLeaf d_device = d_node.leaf("device");
    		NavuLeaf d_int_type = d_node.container("interface").leaf("int_type");
    		NavuLeaf d_int_id = d_node.container("interface").leaf("int_id");

    		//生成一个link对象存储当前遍历的节点
    		link link_tmp = new link(i, s_device.valueAsString(), s_int_type.valueAsString(), s_int_id.valueAsString(), d_device.valueAsString(), d_int_type.valueAsString(), d_int_id.valueAsString());
    		//将link存入map中
    		links.put(i, link_tmp);
        	//控制linkmap的索引，使所有的link均以递增的索引被添加进map中。
        	i ++;
    	}
    	/**
		 * 用存储netblock_dec的map一次赋值links中的每一个link对象中的net_block属性
		 */
    	for(int j = 0 ; j < links.size() ; j ++){
    		links.get(j).setNet_block(netBlock_all.get(j));
    		//输出验证link对象的netblock数据已经输入
    		System.out.print(links.get(j).getNet_block());
    		System.out.print("\n");
    	}
    	/**
    	 * 利用ip_operation对象来处理所有link对象中的netblock，生成对应的IP赋值给各个link对象自己的s_IP&d_IP
    	 */
    	for(int j = 0 ; j < links.size() ; j ++){
    		ip_operation ipOPE = new ip_operation();
    		links.get(j).setS_IP(ipOPE.Get_ipSTR(links.get(j).getNet_block()+1).toString());
    		System.out.print("sourceIP:");
    		System.out.print(links.get(j).getS_IP());
    		System.out.print("\n");
    		links.get(j).setD_IP(ipOPE.Get_ipSTR(links.get(j).getNet_block()+2).toString());
    		System.out.print("destinationIP:");
    		System.out.print(links.get(j).getD_IP());
    		System.out.print("\n");
    	}
    	/**
    	 * 每个link对象应用模板一次赋值
    	 */
    	for(int j = 0 ; j < links.size() ; j ++){

    		Template myTemplate = new Template(context, "interfaces");
    		String link_netmask = links.get(j).getNetmask();
    		//创建device_operator对象来判断设备的nedType
    		device_operation device_tmp = new device_operation(ncsRoot.container("devices"));

    		//source device deploy
    		TemplateVariables source_Vars = new TemplateVariables();
    		String s_device_name = links.get(j).getS_device();
    		String s_interface_type = links.get(j).getS_interface_type();
    		String s_interface_id = links.get(j).getS_interface_id();
    		String s_ipv4_address = links.get(j).getS_IP();
    		source_Vars.putQuoted("DEVICE_NAME", s_device_name);
    		source_Vars.putQuoted("INT_TYPE", s_interface_type);
    		source_Vars.putQuoted("INTER_ID", s_interface_id);
    		source_Vars.putQuoted("IPV4_ADDRESS", s_ipv4_address);
    		System.out.print("#");
    		System.out.print(device_tmp.get_deviceNed(s_device_name));
    		System.out.print("#\n");
    		if (device_tmp.get_deviceNed(s_device_name).equals("cisco-ios-xr-id:cisco-ios-xr"))
    			source_Vars.putQuoted("NETMASK", "30");
    		else
    			source_Vars.putQuoted("NETMASK", link_netmask);
    		myTemplate.apply(service, source_Vars);

    		//des device deploy
    		TemplateVariables des_Vars = new TemplateVariables();
    		String d_device_name = links.get(j).getD_device();
    		String d_interface_type = links.get(j).getD_interface_type();
    		String d_interface_id = links.get(j).getD_interface_id();
    		String d_ipv4_address = links.get(j).getD_IP();
    		des_Vars.putQuoted("DEVICE_NAME", d_device_name);
    		des_Vars.putQuoted("INT_TYPE", d_interface_type);
    		des_Vars.putQuoted("INTER_ID", d_interface_id);
    		des_Vars.putQuoted("IPV4_ADDRESS", d_ipv4_address);
    		System.out.print("#");
    		System.out.print(device_tmp.get_deviceNed(d_device_name));
    		System.out.print("#\n");
    		if (device_tmp.get_deviceNed(d_device_name).equals("cisco-ios-xr-id:cisco-ios-xr"))
    			des_Vars.putQuoted("NETMASK", "30");
    		else
    			des_Vars.putQuoted("NETMASK", link_netmask);
    		myTemplate.apply(service, des_Vars);

    	}
        return opaque;
    }

    @ServiceCallback(servicePoint="IGP_service-servicepoint",callType=ServiceCBType.CREATE)
    public Properties create_IGPService(
    		ServiceContext context,
            NavuNode service,
            NavuNode ncsRoot,
            Properties opaque)throws ConfException{

    	Template myTemplate_IOS = new Template(context, "IGP_ospf_IOS");
    	Template myTemplate_XR = new Template(context, "IGP_ospf_XR");
    	TemplateVariables ospf = new TemplateVariables();
    	/**
    	 * 取值&赋值部分
    	 */
    	//ospf 进程ID取值
    	NavuLeaf process_id = service.leaf("process_id");
    	//ospf 进程ID赋值
    	ospf.putQuoted("PROCESS_ID", process_id.valueAsString());
    	System.out.print(process_id.valueAsString());
    	//...

    	//重分部部分取值
    	NavuContainer route_import = service.container("route_import");
    	NavuLeaf route_type = route_import.leaf("route_type");
    	NavuLeaf red_process_id = route_import.leaf("red_process_id");
    	NavuLeaf metric_type = route_import.leaf("metric_type");
    	String mt_type = "2";
    	if(metric_type.valueAsString().equals("type-one")){
    		mt_type = "1";
    	}
    	NavuLeaf metric_value = route_import.leaf("metric_value");
    	//重分部部分赋值
    	if(!route_type.valueAsString().equals("none")){
    		ospf.putQuoted("RED", "TRUE");
    		if(route_type.valueAsString().equals("static")){
    			ospf.putQuoted("RED_PROTOCOL", route_type.valueAsString());
    			ospf.putQuoted("RED_METRIC_VALUE", metric_value.valueAsString());
    			ospf.putQuoted("RED_METRIC_TYPE", mt_type);
    		}
    		else {
				ospf.putQuoted("RED_PROTOCOL", route_type.valueAsString());
				ospf.putQuoted("RED_PROCESS_ID", red_process_id.valueAsString());
				ospf.putQuoted("RED_METRIC_VALUE", metric_value.valueAsString());
				ospf.putQuoted("RED_METRIC_TYPE", mt_type);
			}
    	}
    	//...

    	//area部分取值
    	NavuContainer areas = service.container("areas");
    	NavuList area = areas.list("area");
    	for(NavuContainer area_info : area){
    		NavuLeaf area_id = area_info.leaf("area_id");
    		NavuLeaf area_type = area_info.leaf("area_type");
    		NavuLeaf totally = area_info.leaf("totally");
    		System.out.printf("%s\n%s\n",area_type.valueAsString(),totally.valueAsString());

    		//area部分赋值
    		ospf.putQuoted("AREA_ID", area_id.valueAsString());
    		ospf.putQuoted("AREA_TYPE", area_type.valueAsString());
    		if(!area_type.valueAsString().equals("normal")){
    			System.out.printf("%s\n%s\n%s\n",area_id.valueAsString(),area_type.valueAsString(),totally.valueAsString());
    			if(totally.valueAsString().equals("TRUE"))
    				ospf.putQuoted("TOTALLY", "true");
    			else
    				ospf.putQuoted("TOTALLY", "false");
    		}
    		else{
    			ospf.putQuoted("TOTALLY", "false");
    		}

    		//...

    		//device部分
    		NavuContainer devices = area_info.container("devices");
    		NavuList device = devices.list("device");
    		for(NavuContainer device_info : device){
    			NavuLeaf device_id = device_info.leaf("device_id");
    			NavuLeaf router_id = device_info.leaf("router_id");
    			ospf.putQuoted("DEVICE_NAME", device_id.valueAsString());
    			ospf.putQuoted("ROUTER_ID", router_id.valueAsString());
    			//...

    			//interface部分
    			NavuContainer interfaces = device_info.container("interfaces");
    			NavuList interface_ = interfaces.list("interface");
    			for(NavuContainer interface_info : interface_){
    				NavuLeaf int_type = interface_info.leaf("int_type");
    				NavuLeaf int_id = interface_info.leaf("int_id");
    				NavuLeaf network_type = interface_info.leaf("network_type");
    				//在宣告部分IOS与XR有所不同
    				device_operation device_tmp = new device_operation(ncsRoot.container("devices"));
    				if(device_tmp.get_deviceNed(device_id.valueAsString()).equals("cisco-ios-xr-id:cisco-ios-xr")){
    					//接口宣告部分赋值
    					ospf.putQuoted("INT_TYPE", int_type.valueAsString());
    					ospf.putQuoted("INT_ID", int_id.valueAsString());
    					//接口网络类型部分赋值
    					ospf.putQuoted("NET_TYPE", network_type.valueAsString());

    					myTemplate_XR.apply(service, ospf);

    				}
    				else if (device_tmp.get_deviceNed(device_id.valueAsString()).equals("ios-id:cisco-ios")) {
    					//接口宣告部分赋值
    					ospf.putQuoted("INT_IP", device_tmp.get_deviceIP(device_id.valueAsString(), int_type.valueAsString(), int_id.valueAsString()));
    					ospf.putQuoted("WILD_CARD", "0.0.0.0");
    					ospf.putQuoted("TAR_AREA", area_id.valueAsString());
    					//接口网络类型部分赋值
    					ospf.putQuoted("INT_TYPE", int_type.valueAsString());
    					ospf.putQuoted("INT_ID", int_id.valueAsString());
    					ospf.putQuoted("NET_TYPE", network_type.valueAsString());

    					myTemplate_IOS.apply(service, ospf);
					}
    				//...
    			}
    		}
    	}
    	return opaque;
    }
}
