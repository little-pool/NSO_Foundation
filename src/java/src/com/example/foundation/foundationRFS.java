package com.example.foundation;

import com.example.foundation.link.link;
import com.example.foundation.namespaces.*;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
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

    @ServiceCallback(servicePoint="foundation-servicepoint",
        callType=ServiceCBType.CREATE)
    public Properties create(ServiceContext context,
                             NavuNode service,
                             NavuNode ncsRoot,
                             Properties opaque)
                             throws ConfException {
    	
        NavuContainer link_service = service.container("link_service");
        NavuList link_part = link_service.list("link_part");
        System.out.print("over");
        for(NavuContainer part_info : link_part){
        	System.out.println(part_info.leaf("part_name").valueAsString()+"   ");
        	int i = 0;
        	//外层map，用于存放所有的link对象。
        	HashMap<Integer, link> links= new HashMap<>();
        	//外层map，用于存放给所有link对象分配的netblock地址的十进制数据的。
        	HashMap<Integer, Long>netBlock_all = new HashMap<>();
        	//单个part全局地址资源取值
    		NavuContainer netblock = part_info.container("netblock");
    		NavuLeaf netaddress = netblock.leaf("netaddress");
    		NavuLeaf netmask = netblock.leaf("netmask");
    		//得到存储所有/30网段的map
        	netblock_operation netBLK = new netblock_operation(netaddress.valueAsString(), Integer.parseInt(netmask.valueAsString()));
        	netBLK.Get_link_netblock_all();
        	netBlock_all = netBLK.getNetblock_map();
        	/**
        	 * 利用link对象将yang中所有的链路相关参数取出存入link对象中，之后使用。
        	 */
        	for(NavuContainer link_info : part_info.list("link")){
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
        		des_Vars.putQuoted("NETMASK", link_netmask);
        		myTemplate.apply(service, des_Vars);
        		
        	}
        }
        return opaque;
    }


    /**
     * Init method for selftest action
     */
    @ActionCallback(callPoint="foundation-self-test", callType=ActionCBType.INIT)
    public void init(DpActionTrans trans) throws DpCallbackException {
    }

    /**
     * Selftest action implementation for service
     */
    @ActionCallback(callPoint="foundation-self-test", callType=ActionCBType.ACTION)
    public ConfXMLParam[] selftest(DpActionTrans trans, ConfTag name,
                                   ConfObject[] kp, ConfXMLParam[] params)
    throws DpCallbackException {
        try {
            // Refer to the service yang model prefix
            String nsPrefix = "foundation";
            // Get the service instance key
            String str = ((ConfKey)kp[0]).toString();

          return new ConfXMLParam[] {
              new ConfXMLParamValue(nsPrefix, "success", new ConfBool(true)),
              new ConfXMLParamValue(nsPrefix, "message", new ConfBuf(str))};

        } catch (Exception e) {
            throw new DpCallbackException("self-test failed", e);
        }
    }
}
