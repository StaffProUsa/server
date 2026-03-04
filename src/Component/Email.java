package Component;

import java.io.FileReader;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import org.json.JSONArray;
import Servisofts.SConfig;
import org.json.JSONObject;
import java.util.Properties;
import javax.mail.Transport;
import org.json.JSONException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import javax.mail.internet.InternetAddress;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class Email extends Thread {

    private String host;
    private String port;
    private String user;
    private String pass;

    private JSONObject data;
    private JSONArray mailTo;
    private JSONObject params;

    public Email(JSONArray mailTo, JSONObject data, JSONObject params) {
        this.data = data;
        this.params = params;
        JSONObject mail_server = SConfig.getJSON("mail_server");
        this.mailTo = mailTo;
        this.host = mail_server.getString("host");
        this.port = mail_server.getInt("port") + "";
        this.user = mail_server.getString("email");
        this.pass = mail_server.getString("password");
        this.start();
    }

    @Override
    public void run() {
        try {
            Properties props = new Properties();
            props.setProperty("mail.smtp.host", this.host);
            props.setProperty("mail.smtp.port", this.port);
            props.setProperty("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Email.this.user, Email.this.pass);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.user, "Staff Pro USA", "UTF-8"));
            message.setReplyTo(new InternetAddress[]{new InternetAddress(this.user)});
            
            for (int i = 0; i < this.mailTo.length(); i++) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(this.mailTo.getString(i)));
            }
            
            message.setSubject(this.data.getString("subject"), "UTF-8");
            
            // Headers adicionales para evitar spam
            message.addHeader("X-Mailer", "Staff Pro USA Mail Service");
            message.addHeader("X-Priority", "1");
            message.setHeader("Priority", "urgent");
            message.addHeader("Importance", "high");
            message.addHeader("List-Unsubscribe", "<mailto:" + this.user + ">");
            
            // Crear contenido multipart (texto plano + HTML)
            Multipart multipart = new MimeMultipart("alternative");
            
            String htmlContent = getHtml(this.data.getString("path"), this.params);
            
            // Parte de texto plano (extraído del HTML)
            MimeBodyPart textPart = new MimeBodyPart();
            String plainText = htmlContent.replaceAll("<[^>]*>", "").replaceAll("\\s+", " ").trim();
            textPart.setText(plainText, "UTF-8");
            
            // Parte HTML
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
            
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(htmlPart);
            
            message.setContent(multipart);
            
            Transport.send(message);
            System.out.println("Correo enviado a " + this.mailTo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String getHtml(String path, JSONObject params) throws JSONException {
        String cuerpo = "";
        try {
            FileReader file;
            file = new FileReader(path, StandardCharsets.UTF_8);
            int valor = file.read();
            String configJson = "";
            while (valor != -1) {
                configJson = String.valueOf(((char) valor));
                cuerpo = cuerpo + configJson;
                valor = file.read();
            }
            file.close();
            if(params != null){
                for (String key : params.keySet()) {
                    cuerpo = cuerpo.replace("{" + key + "}", params.getString(key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cuerpo;
    }
}
