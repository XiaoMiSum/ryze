package io.github.xiaomisum.ryze.protocol.email;


import io.github.xiaomisum.ryze.protocol.email.config.EMailConfigureItem;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.util.Properties;

public class EmailSender {

    public static void send(EMailConfigureItem config, SampleResult result) {
        result.sampleStart();
        var props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", config.getUsername()); // 启用TLS加密
        props.put("mail.smtp.host", config.getHost());
        props.put("mail.smtp.port", config.getPort(EMailConstantsInterface.DEFAULT_PORT));

        // 2. 创建Session会话
        var session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getHost(), config.getPassword());
            }
        });
        try {
            // 3. 编写邮件内容
            var message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getUsername()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.getTo()));
            message.setSubject(config.getTitle());
            var content = StringUtils.trim(config.getContent());
            if (Strings.CS.startsWith(content, "<") && Strings.CS.endsWith(content, ">")) {
                var htmlPart = new MimeBodyPart();
                htmlPart.setContent(content, "text/html; charset=utf-8");
                message.setContent(new MimeMultipart(htmlPart));
            } else {
                message.setText(content);
            }
            // 4. 发送邮件
            Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            result.sampleEnd();
        }
    }
}