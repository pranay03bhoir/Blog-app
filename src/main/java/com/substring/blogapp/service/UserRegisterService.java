package com.substring.blogapp.service;

import com.substring.blogapp.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface UserRegisterService {

    UserDto registerUser(UserDto userDto);
}
