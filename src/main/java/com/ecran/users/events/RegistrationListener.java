package com.ecran.users.events;

import com.ecran.users.entity.UserEntity;
import com.ecran.users.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.Session;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    @Autowired
    private UserService service;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        UserEntity user = event.getUser();
        String recipientAddress = user.getEmail();
        String subject = "ECRAN: Verifique su mail";
//        Una barra mas delante de users
//        String confirmationUrl = event.getAppUrl() + "/users/confirm?token=" + user.getUserId();
        String confirmationUrl = "http://localhost:8082/users/confirm?token=" + user.getUserId();

        SimpleMailMessage email = new SimpleMailMessage();
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
//            String htmlMsg = "<a href='"+ confirmationUrl + "'> Hello World!</a>";
            String html =
//                    "<body style='background-color: #0f172a; margin: 0; height: 500px'> <div style = ' background: radial-gradient(122.17% 122.17% at 50% 100%,rgb(233, 213, 255) 0%,rgb(168, 85, 247) 22.35%,rgba(15, 23, 42, 0) 100%); width: 90%; margin-left: 5vw; padding-bottom: 60px; align-items: center; display: flex;  flex-direction: column; border-radius: 0px 0px 48px 48px; height: 350px'>   <p style='color: #e2e8f0; font-family: InterBold; text-align: center; font-size: 24px; font-weight: 400; padding: 45px; margin: 1.5em auto;'> Muchas gracias por registrarse en nuestra página. Por favor, verifique su email: </p> <a href='" + confirmationUrl + "' style='background-color: rgb(104, 60, 160); padding: 10px 20px; cursor: pointer; border-radius: 10px; text-decoration: none; color: #e2e8f0; font-family: InterBold; font-size: 26px;' >Verificar</a></div> </body>";
                    "<body style='background-color: #0f172a; margin: 0; height: 500px'> <table style = ' background: radial-gradient(122.17% 122.17% at 50% 100%,rgb(233, 213, 255) 0%,rgb(168, 85, 247) 22.35%,rgba(15, 23, 42, 0) 100%); width: 90%; margin-left: 5vw; padding-bottom: 60px; align-items: center;  border-radius: 0px 0px 48px 48px; height: 350px'><tr> <th><img src='https://ecran.s3.us-east-1.amazonaws.com/Logos/%C3%89CRAN.png?response-content-disposition=inline&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEMT%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXNhLWVhc3QtMSJIMEYCIQCzmkrRTneeogpu0i4QXcUsJbS053om2awSEArcLrWc%2BwIhAMyMMJJSJPIFyCaMPyoVdjFwTu4LZqm4c9SUxXCibC9OKoQDCJ3%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEQABoMMzAyODM2NDQxMDc4IgxhP%2FVk0OQf4ewdS8Eq2ALnfjsM0ucu%2BDeQ5Tet9MG3PB17ee0zcIDWRHA5Otjp0Dw%2F%2Fn%2B1%2BB%2FKboHRX8HG1AZ6fnCUZgXEJ%2Fp%2FldlPrUIGxzqcRKXghje0ECE8pnZouCbKgGVqI1zcgfGWOtKqz8Py0FXVvp3r1GntRNZzQRCeP16XN2vj%2FldTU0T3gxbipFyblE6lUP9PzUEaA5zYKtsnFgx%2FYzMFALZ%2FUFvM9zusL8gwkRB0Jvus4g%2FGVkdEWGMfpJ7zeIZTPCaylHvi7jBawnOSOmS9Lqf%2FONibXZT3Bdc7r05BlhI%2F1M6RPfXVoU4xVkg659dqKYannJtK5kEpamkJr6%2BG9lJiujftBiFepLRlPQm4RRb3RZoNh5KSD%2FppZGXIif947gTzHuYeVLE9%2BPimKnCfXIihR2eZ5dy3D9n99ri%2FVlo2NEG9gxM5vQsJreFcpx5Hzunx4mADedxOws3yuHDR5zCysuqnBjqyAtoV7p8QG7PCkzQ%2BX4y6UKw792qGr7PE%2BOtWEEvjNkKcdsUMmINnKldnTqxh144A%2BnRiF%2FAAXjUUNp4VKaZpss4YbVP6BanhQDW2xYMtYe35IkqyXDtKnx9bcQXQV41mQcGZHCdNsvIAlj5wQZLm9IFhI%2FkkQP9ebbHCbHADBEa3%2FiD9QqwgqB2zEndcnVzjdDAujganAcLb0ycyzLQ6IIG1E%2FPGQo420lrdsFDXAHYKpJkilvhRVe4vAf7yGM7tX9Yq1qJp7pqOov9xCy33WyqX3ZT2BiASrSOff9UzUDgBHyO5XlPy2%2FlPYGNcAoZJN0phwT1oIcsoic2VasL4UkKVlhMoY1u0QO9E5b2qUAsicbk1aHcEH1A5auWnY305ldJLimVsxIxZ3vSgDHCUN5ZAuQ%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20230908T035103Z&X-Amz-SignedHeaders=host&X-Amz-Expires=300&X-Amz-Credential=ASIAUNATVL73AVD5WZQW%2F20230908%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=b63249878b99f384f0f37aa103c8488bef5dc7ad3fde0cd4c1f34dc274b674b6' style='width:40%;  margin-top: 60px;'/></th> </tr><tr><td><p style='color: #e2e8f0; font-family: InterBold; text-align: center; font-size: 24px; font-weight: 400; padding: 45px; margin: 1.5em auto;'> Muchas gracias por registrarse en nuestra página. Por favor, verifique su email: </p></td></tr><tr><td style='text-align: center;'> <a href='" + confirmationUrl + "' style='background-color: rgb(104, 60, 160); padding: 10px 20px; cursor: pointer; border-radius: 10px; text-decoration: none; color: #e2e8f0; font-family: InterBold; font-size: 26px;' >Verificar</a></td></tr></table> </body>";

            mimeMessage.setContent(html, "text/html");
            /** Use this or below line **/
            helper.setText(html, true); // Use this or above line.
            helper.setTo(recipientAddress);
            helper.setSubject(subject);
            helper.setFrom("ecran.lat@gmail.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            System.out.println(ex);
        }

//        email.setTo(recipientAddress);
//        email.setSubject(subject);
//        email.setText("Muchas gracias por registrarte en Ecran.lat! Por favor, verifica tu mail." + "\r\n" + "http://localhost:8082" + confirmationUrl);//        email.setText("html><body><h1>¡Hola!</h1><p>Este es un correo con formato HTML.</p></body></html>" + "http://localhost:8082" + confirmationUrl);
//        mailSender.send(email);
    }
}
