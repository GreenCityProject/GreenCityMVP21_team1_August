package greencity.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String content) {
        MimeMessage message = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            this.mailSender.send(message);
        } catch (MailParseException e) {
            System.err.println("Error parsing email: " + e.getMessage());
        }
        catch (MailAuthenticationException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        catch (MailSendException e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
        catch (MessagingException e) {
            System.err.println("Messaging error: " + e.getMessage());
        }
        catch (MailException e) {
            System.err.println("Mail exception occurred: " + e.getMessage());
        }
    }
}