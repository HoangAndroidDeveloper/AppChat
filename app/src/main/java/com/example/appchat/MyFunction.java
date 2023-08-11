package com.example.appchat;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.view.Window;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MyFunction {
    public static long CreateId ()
    {
        return new Date().getTime();
    }
    public static String VerificationEmail(String email) // gửi code về email
    {
        Random random = new Random();
        String code = random.nextInt(899999)+100000+"";
        String EmailSender = "trinhviethoang307@gmail.com";
        String password = "ehphxgljxybnsvvk", host = "smtp.gmail.com";
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host",host);
        properties.put("mail.smtp.port","587");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.auth","true");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailSender,password);
            }
        });
        Message mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.addRecipients(Message.RecipientType.TO,  InternetAddress.parse(email));
            mimeMessage.setFrom(new InternetAddress(email));
            mimeMessage.setSubject("Mã code xác thực tài khoản của bạn được gửi từ App chat");
            mimeMessage.setText("Mã xác thực: "+code);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {


                    }
                }
            });
            thread.start();
        } catch (MessagingException e) {

        }
        return code;

    }




}
