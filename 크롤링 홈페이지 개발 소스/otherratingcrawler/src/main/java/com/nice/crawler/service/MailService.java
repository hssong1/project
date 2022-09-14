package com.nice.crawler.service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nice.crawler.dto.Mail;
import com.nice.crawler.gather.common.MailAuth;
import com.nice.crawler.mapper.UserMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailService {
	
	private final UserService userService;

    public void sendMail(Mail mail) throws Exception {
        System.out.println("========== 메일전송 ============");
        System.out.println("mail.getAddress().toString() = " + mail.getEmail().toString());
        
        final String bodyEncoding = "UTF-8"; //콘텐츠 인코딩
        
        String subject = mail.getTitle();
        String empNo = mail.getCompanynumber();
        
        final String address = mail.getEmail();
        
        // 메일에 출력할 텍스트
        StringBuffer sb = new StringBuffer();
        sb.append("<h3>안녕하세요</h3>");
        sb.append("<h4>테스트용 메일입니다.</h4>");
        sb.append("<h4>KIS : "+ mail.getKisCount() + "개 KR : " + mail.getKrCount() + "개 SCRI : " + mail.getScriCount()+"개 총 : " + mail.getTotalCount() +"개 </h4>");
        String html = sb.toString();
        
        // 메일 옵션 설정
        Properties props = System.getProperties();    
        props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.host", "smtp.naver.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.naver.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        Authenticator auth = new MailAuth();

        Session session = Session.getInstance(props, auth);
         
        MimeMessage msg = new MimeMessage(session);
	    
	    try {
            msg.setSentDate(new Date());

            msg.setFrom(new InternetAddress("aginy01@naver.com", "SYSTEM"));

            InternetAddress to = new InternetAddress(address);         

            msg.setRecipient(Message.RecipientType.TO, to);            

            msg.setSubject(subject, "UTF-8");
            
            Multipart mParts = new MimeMultipart();
            
            // 메일 콘텐츠 설정 - 파일
            MimeBodyPart mFilePart = new MimeBodyPart();
            String attachedFiles = mail.getFileLocation();
            FileDataSource fds = new FileDataSource(attachedFiles);
            mFilePart.setDataHandler(new DataHandler(fds));
            mFilePart.setFileName(fds.getName());
            mParts.addBodyPart(mFilePart);
            
            // 메일 콘텐츠 - 내용
            MimeBodyPart mTextPart = new MimeBodyPart();
            mTextPart.setText(html, bodyEncoding, "html");
            mParts.addBodyPart(mTextPart);
            
            // 메일 콘텐츠 설정
            msg.setContent(mParts);

            Transport.send(msg);
            
            LocalDateTime now = LocalDateTime.now();
            
            String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            
            // 전송시간 저장
            if(userService.getUser(empNo) == null) {
            	System.out.println("메일 주소가 없습니다.");
            }else {          	
            	Map<String, Object> request = new HashMap<>();
            	request.put("empNo", empNo);
            	request.put("emailDtme", formatedNow);
    			
    			int updateUserCount = userService.updateUser(request);
            }
            System.out.println("메일 전송시간 : " + formatedNow);
            System.out.println("========== 메일전송완료 ============");
	    }catch(AddressException ae){
	    	System.out.println("AddressException : " + ae.getMessage()); 
	    }catch(MessagingException me) {            
            System.out.println("MessagingException : " + me.getMessage());
        } catch(UnsupportedEncodingException e) {
            System.out.println("UnsupportedEncodingException : " + e.getMessage());
        }
    }
}
