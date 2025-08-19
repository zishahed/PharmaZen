package com.pharmazen.services;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Date;
import java.util.Properties;

public class EmailService {

    public static void sendOtp(String toEmail, String name, String otpCode) {
        final String from = "your mail";
        final String password = "password provided by GMAIL";

        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );
            message.setSubject("PharmaZen Access Verification - Your OTP Code");
            message.setText("Dear " + name + ",\n\n"
                    + "To complete your verification process and access PharmaZen, please use the following One-Time Password (OTP):\n\n"
                    + otpCode + "\n\n"
                    + "This OTP is valid for a limited time and should not be shared with anyone.\n\n"
                    + "If you did not initiate this request, please ignore this email.\n\n"
                    + "Best regards,\n"
                    + "The PharmaZen Team");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendEmailToPatient(String toEmail, String PatientName, String DoctorName, String genre, Date expiryDate) {
        final String from = "zishahed25@gmail.com";
        final String password = "vnzr prat gxbo rdvo";

        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );
            message.setSubject("PharmaZen: " + genre + " Medicine Authorization Approved");
            message.setText(
                    "Dear " + PatientName + ",\n\n" +
                            "This is an auto-generated email to inform you that Dr. " + DoctorName +
                            " has authorized a restricted medicine to support your treatment.\n\n" +
                            "You are now permitted to purchase " + genre + " type medicines from any Pharmaceuticals Limited through our shop until **" + expiryDate + "**.\n\n" +
                            "We wish you a speedy recovery.\n\n" +
                            "Best regards,\n" +
                            "The PharmaZen Team"
            );

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
