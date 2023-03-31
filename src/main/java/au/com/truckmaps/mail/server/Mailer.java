package au.com.truckmaps.mail.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

/**
 * This class will send email to the recepients selected for the contact us page
 * of react page.
 *
 * @author Parbati Budhathoki
 * @Created On Mar 24, 2023 10:21:25 AM
 */
public class Mailer {

    private static final Logger LOG = LogManager.getLogger(Mailer.class);
    static final Properties PROP = AppConfiguration.getSectionProperties("mail_");

    public static String sendEmail(Request req, Response res) throws Exception {
        LOG.info("Request body: {}", req.body());

        //if request body is not empty then extract email contact object
        try {
            Contact contact = new Gson().fromJson(req.body(), Contact.class);
            LOG.info("contact object: {}", contact);
            if (contact == null || Utils.isNullOrEmpty(contact.getEmail()) || Utils.isNullOrEmpty(contact.getName())
                    || Utils.isNullOrEmpty(contact.getPhone()) || Utils.isNullOrEmpty(contact.getMessage())) {
                LOG.info("Required parameters for email are missing.");
                res.status(400);
                return "Required Parameter Missing";
            }

            String templateName = "truckmaps.ftl";
            String folder = PROP.getProperty("mail_template_path");
            try {
                Message message = getMessage();
                message.setRecipient(Message.RecipientType.TO, new InternetAddress("parbatibudhathoki22@gmail.com", "paru"));

                // Free marker Template
                Configuration cfg = new Configuration();
                cfg.setDirectoryForTemplateLoading(new File(folder));
                cfg.setDefaultEncoding("UTF-8");
                cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
                Template template = cfg.getTemplate(templateName);
                Map paramMap = new HashMap();
                paramMap.put("name", contact.getName());
                paramMap.put("email", contact.getEmail());
                paramMap.put("phone", contact.getPhone());
                paramMap.put("message", contact.getMessage());
                paramMap.put("date", String.valueOf(Calendar.getInstance().getTime()));
                Writer out = new StringWriter();
                template.process(paramMap, out);
                BodyPart body = new MimeBodyPart();
                body.setContent(out.toString(), "text/html");
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(body);
                message.setSubject("[TruckMaps] You've got a new mail.");
                message.setContent(multipart);
                Transport.send(message);
                LOG.info("The e-mail was sent successfully");
            } catch (TemplateException | IOException | MessagingException e) {
                throw new Exception("Error occur while sending Email: " + e.getMessage());
            }
            return "sent";
        } catch (JsonSyntaxException ex) {
            LOG.error("Error while extracting the object: ", ex.getMessage());
            res.status(500);
            return "error";
        }

    }

    public static Message getMessage() {
        Message message = null;
        try {
            String confSender = PROP.getProperty("mail_sender_name");
            String confEmail = PROP.getProperty("mail_sender_email");
            final String confPass = PROP.getProperty("mail_sender_pass");
            final String confUserName = PROP.getProperty("mail_sender_username");

            Properties SESSION_PROP = new Properties();
            SESSION_PROP.put("mail.smtp.host", PROP.getProperty("mail_smtp_server"));
            SESSION_PROP.put("mail.smtp.port", PROP.getProperty("mail_smtp_port"));
            SESSION_PROP.put("mail.smtp.auth", PROP.getProperty("mail_smtp_auth"));

            if (PROP.getProperty("mail_smtp_auth").equals("true") && PROP.getProperty("mail_smtp_trusttype").equals("tls")) {
                SESSION_PROP.put("mail.smtp.starttls.enable", true);
            } else if (PROP.getProperty("mail_smtp_auth").equals("true") && PROP.getProperty("mail_smtp_trusttype").equals("ssl")) {
                SESSION_PROP.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            }

            Session session;
            if (SESSION_PROP.get("mail.smtp.auth").equals("true")) {
                Authenticator auth = new Authenticator() {
                    @Override
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(confUserName, confPass);
                    }
                };
                session = Session.getInstance(SESSION_PROP, auth);
            } else {
                session = Session.getInstance(SESSION_PROP);
            }
            message = new MimeMessage(session);
            try {
                message.setFrom(new InternetAddress(confEmail, confSender));
            } catch (UnsupportedEncodingException | MessagingException ex) {
                LOG.error(ex.getMessage());
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        return message;

    }
}
