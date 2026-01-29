/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * 'Software'), to deal in the Software without restriction, including
 *  * without limitation the rights to use, copy, modify, merge, publish,
 *  * distribute, sublicense, and/or sell copies of the Software, and to
 *  * permit persons to whom the Software is furnished to do so, subject to
 *  * the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be
 *  * included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package io.github.xiaomisum.ryze.protocol.email;

import io.github.xiaomisum.ryze.protocol.email.config.EMailConfigureItem;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Properties;

public class EmailSender {

    public static void send(EMailConfigureItem config, SampleResult result) {
        result.sampleStart();
        try {
            // 设置邮件属性
            var props = createMailProperties(config);
            // 创建邮件会话
            var session = createSession(props, config);
            // 创建邮件消息
            var message = createMessage(session, config);
            // 发送邮件
            Transport.send(message);
        } catch (MessagingException e) {
            result.setThrowable(e);
            throw new RuntimeException("邮件发送失败: " + e.getMessage(), e);
        } catch (Exception e) {
            result.setThrowable(e);
            throw new RuntimeException("邮件发送过程中发生未知错误: " + e.getMessage(), e);
        } finally {
            result.sampleEnd();
        }
    }

    /**
     * 创建邮件会话属性
     *
     * @param config 邮件配置
     * @return 邮件会话属性
     */
    private static Properties createMailProperties(EMailConfigureItem config) {
        var props = new Properties();
        // 基础SMTP配置
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", config.getHost());
        props.put("mail.smtp.port", config.getPort(EMailConstantsInterface.DEFAULT_PORT));
        // SSL配置
        if (Objects.nonNull(config.getUseSSL()) && config.getUseSSL()) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.port", config.getPort(EMailConstantsInterface.DEFAULT_PORT));
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
        }
        // STARTTLS配置
        if (Objects.nonNull(config.getUseStarttls()) && config.getUseStarttls()) {
            props.put("mail.smtp.starttls.enable", "true");
        }
        return props;
    }

    /**
     * 创建邮件会话
     *
     * @param props  邮件属性
     * @param config 邮件配置
     * @return 邮件会话
     */
    private static Session createSession(Properties props, EMailConfigureItem config) {
        return Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getUsername(), config.getPassword());
            }
        });
    }

    /**
     * 创建邮件消息
     *
     * @param session 邮件会话
     * @param config  邮件配置
     * @return 邮件消息
     * @throws MessagingException 邮件处理异常
     */
    private static Message createMessage(Session session, EMailConfigureItem config) throws MessagingException {
        var message = new MimeMessage(session);
        // 设置发件人
        message.setFrom(new InternetAddress(config.getUsername()));
        // 设置收件人
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.getTo()));
        // 设置主题
        message.setSubject(config.getTitle(), "UTF-8");
        // 设置内容
        setMessageContent(message, config);
        return message;
    }

    /**
     * 设置邮件内容
     *
     * @param message 邮件消息
     * @param config  邮件配置
     * @throws MessagingException 邮件处理异常
     */
    private static void setMessageContent(MimeMessage message, EMailConfigureItem config) throws MessagingException {
        String content = StringUtils.trim(config.getContent());
        if (isHtmlContent(content)) {
            // HTML邮件内容
            MimeMultipart multipart = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);
        } else {
            // 纯文本邮件内容
            message.setText(content, "UTF-8");
        }
    }

    /**
     * 判断内容是否为HTML格式
     *
     * @param content 邮件内容
     * @return 如果是HTML格式返回true，否则返回false
     */
    private static boolean isHtmlContent(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        // 简单检查是否包含HTML标签
        String trimmed = content.trim();
        return trimmed.startsWith("<") && trimmed.endsWith(">");
    }

}