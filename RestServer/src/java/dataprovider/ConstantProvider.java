/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataprovider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConstantProvider {

    private static ConstantProvider provider = null;
    private Properties properties;

    private ConstantProvider(){
        properties = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(new File("C:\\Users\\Jan\\Documents\\school\\mobiele\\project\\RestServer\\constants.property"));
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            
        }
    }

    public static ConstantProvider getInstance(){
        if ( provider == null ) {
            provider = new ConstantProvider();
        }
        return provider;
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }
}

