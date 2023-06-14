package com.taki.cloud.mail.template;

import com.taki.cloud.mail.properties.TakiMailProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.ObjectUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @ClassName MailTemplateImpl
 * @Description TODO
 * @Author Long
 * @Date 2023/6/14 18:42
 * @Version 1.0
 */
@AllArgsConstructor
@Slf4j
public class MailTemplateImpl implements  MailTemplate{

    private  final JavaMailSender mailSender;

    private final MailProperties mailProperties;

    @Override
    public void sendSimpleMail(String to, String subject, String content, String... cc) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage() ;

        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);

        if (!ObjectUtils.isEmpty(cc)){
            simpleMailMessage.setCc(cc);
        }
        mailSender.send(simpleMailMessage);
    }

    @Override
    public void sendHtmlMail(String to, String subject, String content, String... cc) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = buildHelper(to,subject,content,mimeMessage,cc);
        mailSender.send(mimeMessage);


    }
    /***
     * @description:
     * @param to
     * @param subject
     * @param content
     * @param cc
     * @return  org.springframework.mail.javamail.MimeMessageHelper
     * @author Long
     * @date: 2023/6/14 18:47
     */
    private MimeMessageHelper buildHelper(String to, String subject, String content,MimeMessage mimeMessage, String[] cc) throws MessagingException {

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);

        helper.setFrom(mailProperties.getUsername());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content,true);
        if (!ObjectUtils.isEmpty(cc)){
            helper.setCc(cc);
        }

        return helper;
    }

    @Override
    public void sendAttachmentsMail(String to, String subject, String content, String filePath, String... cc) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = buildHelper(to,subject,content,mimeMessage,cc);
        FileSystemResource resource = new FileSystemResource(new File(filePath));
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
        helper.addAttachment(fileName,resource);
        mailSender.send(mimeMessage);

    }

    @Override
    public void sendResourceMail(String to, String subject, String content, String rscPath, String rscId, String ... cc) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = buildHelper(to,subject,content,mimeMessage,cc);
        FileSystemResource resource = new FileSystemResource(new File(rscPath));
        helper.addInline(rscId,resource);
        mailSender.send(mimeMessage);
    }
}
