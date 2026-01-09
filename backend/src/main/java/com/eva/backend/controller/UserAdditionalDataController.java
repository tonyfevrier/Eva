package com.eva.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.eva.backend.model.User;
import com.eva.backend.model.UserAdditionalData;
import com.eva.backend.service.UserAdditionalDataService;
import com.eva.backend.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserAdditionalDataController {
    @Autowired
    private UserAdditionalDataService addService;

    @Autowired 
    private UserService userService;

    @PostMapping("/addData")
    public ResponseEntity<?> registerAdditionalData(@RequestBody Map<String, Object> body, HttpServletRequest request){
        String token = getTokenFromRequest(request, "jwt");
        User user = userService.findByToken(token);

        UserAdditionalData addData = new UserAdditionalData();
        addData.setUser(user);
        addData.setAffiliation((String) body.get("affiliation"));
        addData.setAcceptContact((boolean) body.get("acceptContact"));
        addData.setAcceptMap((boolean) body.get("acceptMap"));
        addData.setStreet((String) body.get("street"));
        addData.setPostcode((String) body.get("postcode"));
        addData.setTown((String) body.get("town"));
        addData.setPhone((String) body.get("phone"));
        addService.save(addData);
        return ResponseEntity.ok(addData);
    }

    private String getTokenFromRequest(HttpServletRequest request, String tokenName){
        if (request.getCookies() != null){
            for (Cookie cookie : request.getCookies()) {
                if (tokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }
}
