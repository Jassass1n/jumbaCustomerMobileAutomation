package helpers;

import config.EmailConfigManager;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.util.Properties;

public class EmailReportSender {

    public static void sendReportEmail(String reportZipPath) {
        String enabled = EmailConfigManager.get("email.enabled");
        if (!Boolean.parseBoolean(enabled.trim())) {
            System.out.println("🚫 Email sending disabled via config. Skipping send.");
            return;
        }
        final String senderEmail = EmailConfigManager.get("email.sender");
        final String senderPassword = EmailConfigManager.get("email.password");
        final String recipients = EmailConfigManager.get("email.recipients");
        final String subject = EmailConfigManager.get("email.subject");
        final String body = EmailConfigManager.get("email.body");

        Properties props = new Properties();
        props.put("mail.smtp.auth", EmailConfigManager.get("email.auth"));
        props.put("mail.smtp.starttls.enable", EmailConfigManager.get("email.starttls"));
        props.put("mail.smtp.host", EmailConfigManager.get("email.host"));
        props.put("mail.smtp.port", EmailConfigManager.get("email.port"));

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));

            String[] recipientArray = recipients.split(",");
            InternetAddress[] recipientAddresses = new InternetAddress[recipientArray.length];
            for (int i = 0; i < recipientArray.length; i++) {
                recipientAddresses[i] = new InternetAddress(recipientArray[i].trim());
            }
            message.setRecipients(Message.RecipientType.TO, recipientAddresses);

            message.setSubject(subject);

            // Body
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);

            // Attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(reportZipPath);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("Allure-Report.zip");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("✅ Allure report emailed successfully.");
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}