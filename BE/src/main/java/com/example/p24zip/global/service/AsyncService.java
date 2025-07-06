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

@Service
@AllArgsConstructor
@Slf4j
public class AsyncService {

    private final JavaMailSender mailSender; // 메일 보내는 객체


    @Async("customExecutor")
    public void sendSignupEmail(String username, int codeNum, String mailAddress) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(mailAddress, "이사모음.zip"));
            helper.setTo(username);
            helper.setSubject("이사모음.zip 회원가입 인증 메일입니다.");
            String text = String.format("<h1>인증 코드는 %d입니다.</h1>", codeNum);
            helper.setText(text, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("[회원가입-이메일 작성 오류] username = {}", username, e);
            System.out.println("회원가입-이메일 작성 오류: " + e.getMessage());
            throw new CustomException("EMAIL_SEND_FAIL", "메일 전송 중 오류가 발생했습니다.");

        } catch (MailException e) {
            // 메일 서버 오류 (SMTP 접속 실패, 인증 실패 등)
            log.error("[회원가입-메일 전송 실패] username = {}, codeNum = {}", username, codeNum, e);
            System.out.println("회원가입-메일 전송 실패: " + e.getMessage());
            throw new CustomException("EMAIL_SEND_FAIL", "메일 전송 중 오류가 발생했습니다.");

        } catch (Exception e) {
            // 그 외 예상치 못한 오류
            log.error("[회원가입-알 수 없는 메일 전송 오류] username = {}", username, e);
            System.out.println("회원가입-알 수 없는 메일 전송 오류: " + e.getMessage());
            throw new CustomException("EMAIL_SEND_FAIL", "메일 전송 중 오류가 발생했습니다.");
        }

    }

    @Async("customExecutor")
    public void sendFindPassword(String username, String tempJwt, String mailAddress,
        String origin) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(mailAddress, "이사모음.zip"));
            helper.setTo(username);
            helper.setSubject("이사모음.zip 비밀번호 인증 메일입니다.");
            String text = String.format(
                "<h1>해당 링크로 접속 후 비밀번호를 변경해 주세요. 이용시간은 10분 입니다. 이용시간이 끝나거나 비밀번호 변경 후에는 해당 링크를 이용할 수 없습니다.</h1><p>%s/newpassword?query=%s</p>",
                origin, tempJwt);
            helper.setText(text, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("[비밀번호 찾기-이메일 작성 오류] username = {}", username, e);
            System.out.println("비밀번호 찾기-이메일 작성 오류: " + e.getMessage());
            throw new CustomException("EMAIL_SEND_FAIL", "메일 전송 중 오류가 발생했습니다.");

        } catch (MailException e) {
            // 메일 서버 오류 (SMTP 접속 실패, 인증 실패 등)
            log.error("[비밀번호 찾기-메일 전송 실패] username = {}", username, e);
            System.out.println("비밀번호 찾기-메일 전송 실패: " + e.getMessage());
            throw new CustomException("EMAIL_SEND_FAIL", "메일 전송 중 오류가 발생했습니다.");

        } catch (Exception e) {
            // 그 외 예상치 못한 오류
            log.error("[비밀번호 찾기-알 수 없는 메일 전송 오류] username = {}", username, e);
            System.out.println("비밀번호 찾기-알 수 없는 메일 전송 오류: " + e.getMessage());
            throw new CustomException("EMAIL_SEND_FAIL", "메일 전송 중 오류가 발생했습니다.");
        }
    }

}
