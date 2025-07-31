package com.example.p24zip.global.service;

import com.example.p24zip.global.exception.ConnectMailException;
import com.example.p24zip.global.exception.CustomErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncService {

    private static final long MAIL_TIMEOUT_SECONDS = 2;

    private final JavaMailSender mailSender;

    // 메일 전송을 실행할 전용 Executor (강제 타임아웃 처리를 위해)
    @Qualifier("mailTimeoutExecutor")
    private final ExecutorService mailTimeoutExecutor;

    @Qualifier("customExecutor")
    public CompletableFuture<Object> sendEmail(String to, String subject,
        String htmlContent, String mailAddress) {
        System.out.println("현재 스레드: " + Thread.currentThread().getName());

        return CompletableFuture.supplyAsync(() -> {
            Future<?> future = mailTimeoutExecutor.submit(() -> {
                System.out.println("현재 스레드: " + Thread.currentThread().getName());

                try {
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                    helper.setFrom(new InternetAddress(mailAddress, "이사모음.zip"));
                    helper.setTo(to);
                    helper.setSubject(subject);
                    helper.setText(htmlContent, true);

                    mailSender.send(message);
                    log.info("메일 전송 성공: {}", to);

                } catch (MessagingException | MailException | UnsupportedEncodingException e) {
                    log.error("[메일 전송 실패] to = {}", to, e);
                    throw new ConnectMailException(CustomErrorCode.EMAIL_SEND_FAIL);
                }
            });

            try {
                // 타임아웃 제한 내에 완료되기를 기다림
                future.get(MAIL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                future.cancel(true); // 타임아웃 시 강제 인터럽트
                log.error("[메일 전송 타임아웃] {}초 초과: {}", MAIL_TIMEOUT_SECONDS, to);
                throw new ConnectMailException(CustomErrorCode.EMAIL_SEND_FAIL);
            } catch (ExecutionException e) {
                if (e.getCause() instanceof ConnectMailException ce) {
                    throw ce;
                }
                log.error("[메일 전송 중 예외 발생] username = {}", to, e);
                throw new ConnectMailException(CustomErrorCode.EMAIL_SEND_FAIL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("[메일 전송 인터럽트] {}", to, e);
                throw new ConnectMailException(CustomErrorCode.EMAIL_SEND_FAIL);
            }

            return null;
        });
    }

    @Qualifier("customExecutor")
    public CompletableFuture<Object> sendSignupEmail(String to, int code, String mailAddress) {
        String subject = "이사모음.zip 회원가입 인증 메일입니다.";
        String content = String.format("<h1>인증 코드는 %d입니다.</h1>", code);
        return sendEmail(to, subject, content, mailAddress);
    }

    @Qualifier("customExecutor")
    public CompletableFuture<Object> sendFindPassword(String to, String token, String origin,
        String mailAddress) {
        String subject = "이사모음.zip 비밀번호 인증 메일입니다.";
        String link = String.format("%s/newpassword?query=%s", origin, token);
        String content = String.format(
            "<h1>해당 링크로 접속 후 비밀번호를 변경해 주세요. 이용시간은 10분 입니다.</h1>\n <p>%s</p>",
            link);
        return sendEmail(to, subject, content, mailAddress);
    }

}
