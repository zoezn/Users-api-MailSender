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
                    "<body> <div> Muchas gracias por registrarse en nuestra página. Por favor, verifique su email: </p> <a href='" + confirmationUrl + "'>Verificar</a></div> </body>";

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
