package com.transaction.core.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @description: config
 * @author: yhl
 * @create: 2019-08-12
 */
public class ZhaobiConfig {
    private static final String PROPERTIES_DEFAULT = "zhaobiConfig.properties";
    public static Double usdt;
    public static Integer sleepTime;
    public static Double totalFee;
    public static Properties properties;

    static{
        init();
    }

    /**
     * 初始化
     */
    private static void init() {
        properties = new Properties();
        try{
            InputStream inputStream = ZhaobiConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_DEFAULT);
//          properties.load(inputStream);
//          inputStream.close();
            //解决中文乱码，采取reader把inputStream转换成reader用字符流来读取中文
            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
            properties.load(bf);
            usdt = Double.valueOf(properties.getProperty("usdt"));
            sleepTime = Integer.parseInt(properties.getProperty("sleepTime"));
            totalFee = Double.valueOf(properties.getProperty("totalFee"));
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
