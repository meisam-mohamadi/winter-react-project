package com.api.eshop.controller;

import com.api.eshop.domain.Users;
import com.api.eshop.payload.LoginRequest;
import com.api.eshop.service.utilities.ErrorsMaps;
import com.api.eshop.service.utilities.FileStorageService;
import com.api.eshop.security.JwtTokenProvider;
import com.api.eshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private ErrorsMaps errorsMaps;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private FileStorageService fileStorageService;




    @GetMapping("setVerificationCode/{mobileNumber}")
    @CrossOrigin("*")
    public ResponseEntity getVerificationCode(@PathVariable String mobileNumber) {

        Map<String , String> result = service.getVerificationCode(mobileNumber);


        if (result.get("status") != "ok") {
            return new ResponseEntity(new HashMap() {{

                put("message", "token is not correct");
            }}, HttpStatus.OK);
        }

        return new ResponseEntity(new HashMap() {{
            put("message", "success");
            put("user", result);
        }}, HttpStatus.OK);

    }





    @GetMapping("token/{token}")
    @CrossOrigin("*")
    public ResponseEntity getUserByToken(@PathVariable String token) {

        Users result = service.getUserByToken(token);

        if (result == null) {
            return new ResponseEntity(new HashMap() {{
                put("message", "token is not correct");
            }}, HttpStatus.OK);
        }

        return new ResponseEntity(new HashMap() {{
            put("message", "success");
            put("user", result);
        }}, HttpStatus.OK);

    }



    @PostMapping("login")
    @CrossOrigin("*")
    public ResponseEntity authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult, HttpServletRequest request) {

        return new ResponseEntity(service.login(loginRequest), HttpStatus.OK);

    }



    @PostMapping("isTokenValid")
    @CrossOrigin("*")
    public ResponseEntity isTokenValid(@RequestBody Map<String, String> token) {
        return new ResponseEntity(service.checkUsersToken(token.get("token")), HttpStatus.OK);
    }

    @GetMapping
    @RequestMapping("getVerificationCode/{number}")
    public String getCodeWitMobileNumber(@PathVariable String number) {
        return service.getCodeWitMobileNumber(number);
    }



    @PutMapping(value = "update/{token}" , consumes = "multipart/form-data")
    @CrossOrigin("*")
    public ResponseEntity<Users> updateUser(@RequestParam("user") Users user , @RequestParam("file") MultipartFile file, @PathVariable String token) {

        fileStorageService.storeFile(file);
        Users result = service.updateUser(user, token);
        if (result == null) {
            return new ResponseEntity(new HashMap<String, String>() {{
                put("message", "token is not correct");
            }}, HttpStatus.OK);
        }

        return new ResponseEntity(new HashMap<String, Object>() {{
            put("message", "ok");
            put("user", result);
        }}, HttpStatus.OK);
    }






}
