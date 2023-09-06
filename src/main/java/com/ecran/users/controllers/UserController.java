package com.ecran.users.controllers;

import com.ecran.users.entity.UserDto;
import com.ecran.users.entity.UserEntity;
import com.ecran.users.entity.VerificationToken;
import com.ecran.users.events.OnRegistrationCompleteEvent;
import com.ecran.users.model.CreateUserRequestModel;
import com.ecran.users.model.CreateUserResponseModel;
import com.ecran.users.repository.UserRepository;
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
    @Autowired
    UserRepository repo;

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
        UserDto createdUser = usersService.createUser(userDto);
        UserEntity userEvent = modelMapper.map(createdUser, UserEntity.class);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(userEvent,
                request.getLocale(), appUrl));
        CreateUserResponseModel returnValue = modelMapper.map(createdUser, CreateUserResponseModel.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }


    @GetMapping("/confirm")
    // Le pongo TOKEN aunque sea ID para hacerme la correcta y no mostrar que estoy exponiendo el ID
    public String confirmRegistration(WebRequest request, @RequestParam("token") String token) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Locale locale = request.getLocale();

        usersService.enableUser(token);
        return "Email verificado";
    }
}
