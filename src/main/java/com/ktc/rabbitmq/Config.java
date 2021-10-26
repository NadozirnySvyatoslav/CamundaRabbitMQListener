package com.ktc.rabbitmq;


import org.apache.commons.configuration2.XMLConfiguration ;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Exception;
import java.io.File;

class Config{

    private static Config inst=new Config();
    XMLConfiguration config;

    public static HashMap<String,String> getServices(){
    	if (inst==null) inst=new Config();
	    HashMap<String,String> list=new HashMap<String,String>();
	    int i=0;
	    while(true){
	        if ((inst.config.getString("services.service("+i+").rmq_queue") ==null) ||
		        (inst.config.getString("services.service("+i+").process_key")==null) || i>65535 ) break;
    	    list.put(inst.config.getString("services.service("+i+").rmq_queue"),
	    	inst.config.getString("services.service("+i+").process_key"));
	        i++;
    	}
	    return list;
    }
    public static XMLConfiguration config(){
    	if (inst==null) inst=new Config();
	    return inst.config;
    }
    private Config(){
    	try{
	        Configurations configs = new Configurations();
	        String home=System.getProperty("catalina.home");
    	    File configDir = new File(System.getProperty("catalina.base"), "conf");
    	    File configFile = new File(configDir,"rmq-listener.xml");
    	    config = configs.xml(configFile);
	    }
    	catch(ConfigurationException e){
	        e.printStackTrace();
    	}
    }
}