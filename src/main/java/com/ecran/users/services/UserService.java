package com.ecran.users.services;

import com.ecran.users.entity.UserDto;
import com.ecran.users.entity.UserEntity;
import com.ecran.users.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    UserRepository usersRepository;

    Environment environment;
    private final ModelMapper mapper;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserService(UserRepository usersRepository, Environment environment,  ModelMapper mapper) {
        this.usersRepository = usersRepository;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//		this.restTemplate = restTemplate;
//        this.moviesServiceClient = moviesServiceClient;
        this.environment = environment;
        this.mapper = mapper;
    }


//    @Override
    public UserDto createUser(UserDto userDetails) {
        // TODO Auto-generated method stub

        userDetails.setUserId(UUID.randomUUID().toString());
//        userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
        userDetails.setEncryptedPassword(userDetails.getPassword());
        userDetails.setEnabled(false);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);

        usersRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(userEntity, UserDto.class);

        return returnValue;
    }

    public UserEntity enableUser(String id) {
        UserEntity foundUser = usersRepository.findByUserId(id);
        foundUser.setEnabled(true);
        UserEntity saved =  usersRepository.save(foundUser);
        return saved;
    }

}
