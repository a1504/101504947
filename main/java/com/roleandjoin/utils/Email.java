package com.roleandjoin.utils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Email {

	public static final String EMAIL_FROM = "swlozano@gmail.com";
	public static final short TYPE_MAIL_ACTIVATE_ACCOUNT = 1;
	public static final short TYPE_MAIL_NOTIFY_INVITATION = 2;
	public static String EMAIL_TEMPLATE_INVITATIONS;
	
	
	public static void sendEmail(String addressFrom,String nameFrom, String subject, String content, String... addressTo){
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(addressFrom,nameFrom));

			if (addressTo.length < 1) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						addressTo[0], nameFrom));
			} else {
				msg.addRecipients(Message.RecipientType.BCC,addRecipient(addressTo));
			}
			msg.setSubject(subject, "UTF-8");
			Multipart mp = new MimeMultipart();
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(content, "text/html");
			mp.addBodyPart(htmlPart);
			msg.setContent(mp);
			Transport.send(msg);
		} catch (AddressException e) {
			Logger.getLogger("").warning("@@@@@" + e.toString());
		} catch (MessagingException e) {
			Logger.getLogger("").warning("@@@@@" + e.toString());
		} catch (UnsupportedEncodingException e) {
			Logger.getLogger("").warning("@@@@@" + e.toString());
		}
	}
	
	public static void sendEmail(String name, String userName, short typeEmail,
			String... emailTo) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {
			String[] strContentParts = getEmailContent(typeEmail);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(EMAIL_FROM, name));

			if (emailTo.length < 1) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						emailTo[0], name));
			} else {
				msg.addRecipients(Message.RecipientType.BCC,addRecipient(emailTo));
			}

			msg.setSubject(strContentParts[0] + userName, "UTF-8");
			Multipart mp = new MimeMultipart();
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(strContentParts[1], "text/html");
			mp.addBodyPart(htmlPart);
			msg.setContent(mp);
			Transport.send(msg);
		} catch (AddressException e) {
			Logger.getLogger("").warning("@@@@@" + e.toString());
		} catch (MessagingException e) {
			Logger.getLogger("").warning("@@@@@" + e.toString());
		} catch (UnsupportedEncodingException e) {
			Logger.getLogger("").warning("@@@@@" + e.toString());
		}
	}

	private static InternetAddress[] addRecipient(String... emailTo) {
		try {
			InternetAddress[] x = new InternetAddress[emailTo.length];
			for (int i = 0; i < x.length; i++) {
				x[i] = new InternetAddress(emailTo[i],"");
			}
			return x;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static String[] getEmailContent(short type) {
		String[] strContentParts = { "Subject", "Content" };
		if (type == TYPE_MAIL_ACTIVATE_ACCOUNT) {
			strContentParts[0] = "Activate account ";
			strContentParts[1] = "Welcome to Role&Join.<br/><br/>Click here to activate account <a href='http://roleandjoin.appspot.com'>Double_Go</a>";
		}else if(type == TYPE_MAIL_NOTIFY_INVITATION){
			strContentParts[0] = "New Invitation";
			strContentParts[1] = "Alejandro Lozado te ha invitado a compartir en su canal";
		}
		return strContentParts;
	}
	
	public static String getIvitationTemplate(String personalName, String urlProfile, String channelName, String urlChannel){
		String html1 = "<html><head><title>Role&Join</title><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'></head><body><div><h2>Role&Join</h2>%s</div></body></html>";
		String html2 = "<h3><a href='%s'>%s</a> has invited you to join <a href='%s'>%s</a></h3><h3>Share with  <a href='%s'>%s</a> great moments, information, pictures and more things on <a href='%s'>%s</a></h3>";            
		html2 = String.format(html2, urlProfile,personalName,urlChannel,channelName, urlProfile,personalName,urlChannel,channelName);
		html1 =String.format(html1, html2);
		return html1;
	}
}
