package atos.knowledgelab.capture.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

	String from;
	String username;
	String password;
	String to;
	
	public MailSender(String from, String username, String password) {
		//configure mailer
		this.username = username;
		this.password = password;
		this.from = from;
		
		
	}
	
	
	public void sendMail(String recipient, String subject, String content) {
		
		Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");


        try {
        	
        	Session session = Session.getInstance(props,
        			new javax.mail.Authenticator() {
        				protected PasswordAuthentication getPasswordAuthentication() {
        					return new PasswordAuthentication(username, password);
        				}
        	});
            MimeMessage message = new MimeMessage(session);
        	
            message.setFrom(new InternetAddress(from));
            
            
            String[] recipients = recipient.split(";");
            for (String r : recipients) {
            	InternetAddress toAddress = new InternetAddress(r);
            	message.addRecipient(Message.RecipientType.TO, toAddress);            	
            }

            message.setSubject(subject);
            message.setText(content);
            
            Transport transport = session.getTransport("smtp");
            //transport.connect(host, from, password);
            transport.send(message);

            //transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }
        catch (AddressException ae) {
            ae.printStackTrace();
        }
        catch (MessagingException me) {
            me.printStackTrace();
        }
		
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
