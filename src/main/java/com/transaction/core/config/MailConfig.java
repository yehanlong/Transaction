package com.transaction.core.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
/**
 * @description: mail配置
 * @author: yhl
 * @create: 2019-08-12
 */


public class MailConfig {
    private static final String PROPERTIES_DEFAULT = "mailConfig.properties";
    public static String host;
    public static Integer port;
    public static String userName;
    public static String passWord;
    public static String emailForm;
    public static String timeout;
    public static String personal;
    public static String html;
    public static String subject;
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
            InputStream inputStream = MailConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_DEFAULT);
//          properties.load(inputStream);
//          inputStream.close();
            //解决中文乱码，采取reader把inputStream转换成reader用字符流来读取中文
            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
            properties.load(bf);
//            host = properties.getProperty("mailHost");
//            port = Integer.parseInt(properties.getProperty("mailPort"));
//            userName = properties.getProperty("mailUsername");
//            passWord = properties.getProperty("mailPassword");
//            emailForm = properties.getProperty("mailFrom");
//            timeout = properties.getProperty("mailTimeout");
//            personal = properties.getProperty("personal");
//            html = properties.getProperty("html");
//            subject = properties.getProperty("subject");


            host = "smtp.163.com";
            port = 25;
            userName = "15958028967@163.com";
            passWord = "abc123";
            emailForm = "15958028967@163.com";
            timeout = "200";
            personal = "transaction";
            html ="";
            subject =  "transaction";
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}

