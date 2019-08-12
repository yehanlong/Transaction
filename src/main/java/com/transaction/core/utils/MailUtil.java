package com.transaction.core.utils;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.transaction.core.config.MailConfig;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
/**
 * @description: 发送邮件
 * @author: yhl
 * @create: 2019-08-12
 */


public class MailUtil {
    private static final String HOST = MailConfig.host;
    private static final Integer PORT = MailConfig.port;
    private static final String USERNAME = MailConfig.userName;
    private static final String PASSWORD = MailConfig.passWord;
    private static final String emailForm = MailConfig.emailForm;
    private static final String timeout = MailConfig.timeout;
    private static final String personal = MailConfig.personal;
    private static final String subject = MailConfig.subject;
    private static final String html = MailConfig.html;
    private static JavaMailSenderImpl mailSender = createMailSender();

    /**
     * 邮件发送器
     *
     * @return 配置好的工具
     */
    private static JavaMailSenderImpl createMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(HOST);
        sender.setPort(PORT);
        sender.setUsername(USERNAME);
        sender.setPassword(PASSWORD);
        sender.setDefaultEncoding("Utf-8");
        Properties p = new Properties();
        p.setProperty("mail.smtp.timeout", timeout);
        p.setProperty("mail.smtp.auth", "false");
        sender.setJavaMailProperties(p);
        return sender;
    }

    /**
     * 发送邮件
     *
     * @param to 接受人
     * @param //subject 主题
     * @param html 发送内容
     * @throws MessagingException 异常
     * @throws UnsupportedEncodingException 异常
     */
    public static void sendMail(String to, String html) throws MessagingException,UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 设置utf-8或GBK编码，否则邮件会有乱码
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        messageHelper.setFrom(emailForm, personal);
//        String[] a = {to,"15958028967@163.com"};
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(html, true);
        messageHelper.setCc(emailForm);
//      messageHelper.addAttachment("", new File(""));//附件
        mailSender.send(mimeMessage);
    }

    public static void sendEmains(String msg){
        try {
            sendMail("1536161955@qq.com",msg);
            sendMail("13588208796@163.com",msg);


        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public static String sendResultEmains(String exch, String Sys, int count, String type, double succUsdt, double thisMoney,
                                        double allMoney, double historyMoney){
        LocalDateTime localDateTime = LocalDateTime.now();
        String msg1 = "时间："+ localDateTime.toString() +"\n";
        String msg2 = "交易所："+ exch +"。\n";
        String msg3 = "交易对："+ Sys +"。\n";
        String msg4 = "交易方式："+ type +"。\n";
        String msg5 = "交易次数："+ count +"。\n";
        String msg6 = "成交usdt数量："+ succUsdt +"。\n";
        String msg7 = "此次总盈利USDT："+ thisMoney +"。\n";
        String msg8 = "此次总盈利RMB："+ thisMoney*7 +"。\n";
        String msg9 = "平均每笔交易盈利："+ succUsdt/Double.valueOf(count) +"。\n";
        String msg10 = "该交易对总盈利USDT："+ allMoney +"。\n";
        String msg11 = "该交易所总盈利USDT："+ historyMoney +"。\n";

        String msg = msg1+msg2+msg3+msg4+msg5+msg6+msg7+msg8+msg9+msg10;
        sendEmains(msg);
        return msg;
    }



}

