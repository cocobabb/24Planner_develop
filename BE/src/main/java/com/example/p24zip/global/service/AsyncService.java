package com.example.p24zip.global.service;

import com.example.p24zip.global.exception.CustomException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.example.p24zip.global.exception.CustomErrorCode;

@Service
@AllArgsConstructor
@Slf4j
public class AsyncService {

    private final JavaMailSender mailSender; // 메일 보내는 객체


    @Async("customExecutor")

    public void sendMail(String to, String subject, String htmlContent, String fromName,
        String mailAddress) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(mailAddress, fromName));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("[이메일 작성 오류] username = {}", to, e);
            System.out.println("회원가입-이메일 작성 오류: " + e.getMessage());
            throw new CustomException(CustomErrorCode.EMAIL_SEND_FAIL);

        } catch (MailException e) {
            // 메일 서버 오류 (SMTP 접속 실패, 인증 실패 등)
            log.error("[메일 전송 실패] username = {}", to, e);
            System.out.println("회원가입-메일 전송 실패: " + e.getMessage());
            throw new CustomException(CustomErrorCode.EMAIL_SEND_FAIL);

        } catch (Exception e) {
            // 그 외 예상치 못한 오류
            log.error("[알 수 없는 메일 전송 오류] username = {}", to, e);
            System.out.println("회원가입-알 수 없는 메일 전송 오류: " + e.getMessage());
            throw new CustomException(CustomErrorCode.EMAIL_SEND_FAIL);
        }
    }


    public void sendSignupEmail(String to, int code, String mailAddress) {
        String subject = "이사모음.zip 회원가입 인증 메일입니다.";
        String html = String.format("<h1>인증 코드는 %d입니다.</h1>", code);
        sendMail(to, subject, html, "이사모음.zip", mailAddress);
    }

    public void sendFindPassword(String to, String token, String origin, String mailAddress) {
        String subject = "이사모음.zip 비밀번호 인증 메일입니다.";
        String link = String.format("%s/newpassword?query=%s", origin, token);
        String html = "<h1>해당 링크로 접속 후 비밀번호를 변경해 주세요. 이용시간은 10분 입니다.</h1><p>" + link + "</p>";
        sendMail(to, subject, html, "이사모음.zip", mailAddress);
    }


}
