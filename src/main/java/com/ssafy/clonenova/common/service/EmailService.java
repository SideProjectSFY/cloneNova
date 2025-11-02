package com.ssafy.clonenova.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 이메일 발송 서비스
 * 
 * 인증 코드, 비밀번호 재설정 등 이메일 관련 기능 처리
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * 비밀번호 재설정 링크 이메일 발송
     * 
     * @param to 받는 사람 이메일 주소
     * @param token 비밀번호 재설정 토큰 (UUID)
     */
    public void sendPasswordResetLink(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("[CloneNova] 비밀번호 재설정");
            
            String htmlContent = buildPasswordResetEmail(token);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("비밀번호 재설정 링크 이메일 발송 완료: {}", to);

        } catch (MessagingException e) {
            log.error("이메일 발송 실패: {}", to, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * 비밀번호 재설정 링크 이메일 HTML 템플릿
     * 
     * @param token 비밀번호 재설정 토큰 (UUID)
     * @return HTML 콘텐츠
     */
    private String buildPasswordResetEmail(String token) {
        // TODO: 실제 프론트엔드 URL로 변경 필요
        String resetUrl = "http://localhost:3000/reset-password?token=" + token;
        
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }\n" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }\n" +
                "        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }\n" +
                "        .content { padding: 20px; background-color: #f9f9f9; }\n" +
                "        .button { display: inline-block; padding: 12px 30px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }\n" +
                "        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }\n" +
                "        .warning { color: #ff6b6b; font-size: 14px; margin-top: 20px; }\n" +
                "        .link { color: #666; word-break: break-all; margin-top: 20px; padding: 10px; background-color: #fff; border: 1px solid #ddd; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class='container'>\n" +
                "        <div class='header'>\n" +
                "            <h1>CloneNova 비밀번호 재설정</h1>\n" +
                "        </div>\n" +
                "        <div class='content'>\n" +
                "            <p>안녕하세요,</p>\n" +
                "            <p>비밀번호 재설정을 요청하셨습니다. 아래 버튼을 클릭하여 비밀번호를 재설정하세요.</p>\n" +
                "            <div style='text-align: center; margin: 30px 0;'>\n" +
                "                <a href='" + resetUrl + "' class='button'>비밀번호 재설정</a>\n" +
                "            </div>\n" +
                "            <p>또는 아래 링크를 복사하여 브라우저에 붙여넣으세요:</p>\n" +
                "            <div class='link'>" + resetUrl + "</div>\n" +
                "            <p>링크는 <strong>30분간</strong> 유효합니다.</p>\n" +
                "            <div class='warning'>\n" +
                "                <p>⚠️ 본인이 요청하지 않은 경우, 이 이메일을 무시해주세요.</p>\n" +
                "                <p>보안을 위해 이 링크를 다른 사람과 공유하지 마세요.</p>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div class='footer'>\n" +
                "            <p>© 2025 CloneNova. All rights reserved.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
