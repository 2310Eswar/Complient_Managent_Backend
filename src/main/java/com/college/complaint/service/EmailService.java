package com.college.complaint.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.mail.from:Campus Resolv Support <no-reply@campusresolv.com>}")
    private String fromEmail;

    public void sendPasswordResetOtp(String toEmail, String otp, int expiryMinutes) {
        // Always log OTP for development and audit fallback
        logger.info("----------------------------------------------------------------");
        logger.info("🔑 [PASSWORD RESET OTP] For: {} | OTP Code: {} (Valid for {} mins)", toEmail, otp, expiryMinutes);
        logger.info("----------------------------------------------------------------");

        if (mailSender == null) {
            logger.warn("JavaMailSender bean not configured. OTP printed to console above.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Campus Resolv - Password Reset Verification Code");

            String htmlContent = buildOtpHtmlEmail(toEmail, otp, expiryMinutes);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Successfully sent OTP email to {}", toEmail);
        } catch (Exception ex) {
            logger.error("Failed to deliver OTP email via SMTP to {}: {}. OTP can be retrieved from server console above.", 
                    toEmail, ex.getMessage());
            // Do not rethrow so development/demo is not blocked if user hasn't set real SMTP credentials yet
        }
    }

    private String buildOtpHtmlEmail(String email, String otp, int expiryMinutes) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Password Reset OTP</title>
            </head>
            <body style="margin: 0; padding: 0; background-color: #f1f5f9; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; color: #1e293b;">
              <table role="presentation" width="100%%" border="0" cellspacing="0" cellpadding="0" style="background-color: #f1f5f9; padding: 40px 10px;">
                <tr>
                  <td align="center">
                    <table role="presentation" width="100%%" border="0" cellspacing="0" cellpadding="0" style="max-width: 520px; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.07); border: 1px solid #e2e8f0;">
                      <!-- Header -->
                      <tr>
                        <td style="background: linear-gradient(135deg, #4f46e5 0%%, #7c3aed 100%%); padding: 32px 24px; text-align: center;">
                          <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: 800; letter-spacing: -0.5px;">Campus Resolv</h1>
                          <p style="color: #e0e7ff; margin: 6px 0 0 0; font-size: 13px; font-weight: 500;">Campus Complaint &amp; Resolution Portal</p>
                        </td>
                      </tr>
                      
                      <!-- Body Content -->
                      <tr>
                        <td style="padding: 36px 32px;">
                          <h2 style="margin: 0 0 12px 0; color: #0f172a; font-size: 19px; font-weight: 700;">Password Reset Request</h2>
                          <p style="margin: 0 0 20px 0; color: #475569; font-size: 14px; line-height: 1.6;">
                            We received a request to reset your password for account <strong style="color: #1e293b;">%s</strong>. Use the verification code below to complete the reset process:
                          </p>

                          <!-- OTP Display Card -->
                          <div style="background-color: #f8fafc; border: 2px dashed #cbd5e1; border-radius: 12px; padding: 24px 16px; text-align: center; margin: 24px 0;">
                            <span style="font-family: 'Courier New', Courier, monospace; font-size: 34px; font-weight: 800; letter-spacing: 8px; color: #4f46e5;">%s</span>
                            <p style="margin: 8px 0 0 0; color: #64748b; font-size: 12px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px;">
                              Valid for %d minutes only
                            </p>
                          </div>

                          <p style="margin: 0 0 24px 0; color: #64748b; font-size: 13px; line-height: 1.5;">
                            If you did not request a password reset, you can safely ignore this email. Your password will remain unchanged.
                          </p>

                          <div style="border-top: 1px solid #f1f5f9; padding-top: 20px; font-size: 12px; color: #94a3b8; text-align: center;">
                            For campus security, never share your verification code or password with anyone.
                          </div>
                        </td>
                      </tr>

                      <!-- Footer -->
                      <tr>
                        <td style="background-color: #f8fafc; border-top: 1px solid #e2e8f0; padding: 18px 24px; text-align: center; font-size: 12px; color: #94a3b8;">
                          &copy; 2026 Campus Resolv Management System. All rights reserved.
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(email, otp, expiryMinutes);
    }
}
