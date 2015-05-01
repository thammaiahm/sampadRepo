/**
 * 
 */
package com.mot.upd.pcba.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import org.apache.log4j.Logger;


/**
 * @author JKXH84
 *
 */
public class InitProperty {

	protected static final String SQL_PROPERTIES_FILE = "pcbasql.properties";
	private static final Logger log = Logger.getLogger(InitProperty.class);

public static PropertyResourceBundle getProperty(String propertyType) 
{
	
		PropertyResourceBundle bundle = null;	
		try{
		ClassLoader loader = InitProperty.class.getClassLoader();
		System.out.println("Loading SQL File : "+ApplicationProperties.getInstance().getAppProperty(SQL_PROPERTIES_FILE));
		InputStream input = loader.getResourceAsStream(ApplicationProperties.getInstance().getAppProperty(propertyType));
		bundle = new PropertyResourceBundle(input);
		}
		catch(IOException e){
			System.out.println("Exception in loading SQL File : "+e.getMessage());
			log.error(e);
		}

 return bundle;
}



}

