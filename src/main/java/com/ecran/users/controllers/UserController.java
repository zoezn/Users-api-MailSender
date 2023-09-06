package com.ecran.users.controllers;

import com.ecran.users.entity.UserDto;
import com.ecran.users.entity.UserEntity;
import com.ecran.users.entity.VerificationToken;
import com.ecran.users.events.OnRegistrationCompleteEvent;
import com.ecran.users.model.CreateUserRequestModel;
import com.ecran.users.model.CreateUserResponseModel;
import com.ecran.users.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/users")
public class UserController {

    //    @Autowired
//    private Environment env;
    @Autowired
    private MessageSource messages;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    UserService usersService;

    @PostMapping(
//            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
//            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<CreateUserResponseModel> createUser(@RequestBody CreateUserRequestModel userDetails, HttpServletRequest request) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        userDto.setEnabled(false);
        String appUrl = request.getContextPath();
        UserEntity userEvent = modelMapper.map(userDetails, UserEntity.class);

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(userEvent,
                request.getLocale(), appUrl));

        UserDto createdUser = usersService.createUser(userDto);


        CreateUserResponseModel returnValue = modelMapper.map(createdUser, CreateUserResponseModel.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }


    @GetMapping("/regitrationConfirm")
    public String confirmRegistration
            (WebRequest request, Model model, @RequestParam("token") String token) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Locale locale = request.getLocale();

        VerificationToken verificationToken = usersService.getVerificationToken(token);
        if (verificationToken == null) {
            String message = messages.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        UserEntity user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String messageValue = messages.getMessage("auth.message.expired", null, locale);
            model.addAttribute("message", messageValue);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        user.setEnabled(true);
//        usersService.createUser(user);
        usersService.createUser(modelMapper.map(user, UserDto.class));
        return "redirect:/login.html?lang=" + request.getLocale().getLanguage();
    }
}
