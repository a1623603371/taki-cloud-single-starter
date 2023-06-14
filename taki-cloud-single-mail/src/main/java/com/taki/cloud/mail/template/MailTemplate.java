package com.taki.cloud.mail.template;

import javax.mail.MessagingException;

/**
 * @ClassName MailTemplate
 * @Description 邮件通用操作模板
 * @Author Long
 * @Date 2023/6/14 18:34
 * @Version 1.0
 */
public interface MailTemplate {
    /*** 
     * @description: 发送文本文件
     * @param to
     * @param  subject
     * @param   content
     * @param   cc
     * @return  void
     * @author Long
     * @date: 2023/6/14 18:35
     */ 
    void sendSimpleMail(String  to,String  subject,String content,String ...cc);

    /***
     * @description: 发送html文本
     * @param to 收件地址
     * @param  subject 邮件主题
     * @param   content 邮件内容
     * @param   cc 抄送地址
     * @return  void
     * @author Long
     * @date: 2023/6/14 18:35
     */
    void sendHtmlMail(String  to,String  subject,String content,String ...cc) throws MessagingException;


    /***
     * @description: 发送附带文件
     * @param to
     * @param subject
     * @param content
     * @param filePath 文件路径
     * @param cc
     * @return  void
     * @author Long
     * @date: 2023/6/14 18:40
     */
    void sendAttachmentsMail(String  to,String  subject,String content,String filePath,String ...cc) throws MessagingException;


    /***
     * @description: 发送静态资源邮件
     * @param to
     * @param subject
     * @param  content
     * @param  rscPath
     * @param  rscId
     * @param  cc
     * @return  void
     * @author Long
     * @date: 2023/6/14 18:42
     */
    void sendResourceMail(String to,String subject,String content,String rscPath,String rscId,String ... cc) throws MessagingException;
}
