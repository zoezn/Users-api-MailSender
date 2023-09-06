package com.ecran.users.events;

import com.ecran.users.entity.UserEntity;
import com.ecran.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

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
        String confirmationUrl = event.getAppUrl() + "/users/confirm?token=" + user.getUserId();

        SimpleMailMessage email = new SimpleMailMessage();
//        MimeMessage msg= new MimeMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
//        email.setContent()
//        email.setContentType()
        email.setText("Muchas gracias por registrarte en Ecran.lat! Por favor, verifica tu mail." + "\r\n" + "http://localhost:8082" + confirmationUrl);
//        email.setText("<html><body><h1>¡Hola!</h1><p>Este es un correo con formato HTML.</p></body></html>" + "http://localhost:8082" + confirmationUrl);
        mailSender.send(email);
    }
}
