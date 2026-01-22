package com.eva.backend.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.eva.backend.model.User;
import com.eva.backend.model.UserAdditionalData;
import com.eva.backend.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserAdditionalDataController {

    @Autowired 
    private UserService userService;

    @Autowired
    private RequestUtils requestUtils;

    @PostMapping("/addData")
    public ResponseEntity<?> registerAdditionalData(@RequestBody Map<String, Object> body, HttpServletRequest request){
        String token = requestUtils.getTokenFromRequest(request, "jwt");
        User user = userService.findByToken(token);

        UserAdditionalData addData = new UserAdditionalData();
        addData.setAcceptContact((boolean) body.get("acceptContact"));
        addData.setAcceptMap((boolean) body.get("acceptMap"));
        addData.setBirthday(LocalDate.parse((String) body.get("birthday")));
        addData.setGender((String) body.get("gender"));
        addData.setJob((String) body.get("job"));
        addData.setSpecializedTopics((String) body.get("specializedTopics"));
        addData.setOtherSpecialization((String) body.get("otherSpecialization"));
        addData.setTeacherBehaviour((String) body.get("teacherBehaviour"));
        addData.setFreeField((String) body.get("freeField"));

        user.setAdditionalData(addData);
        userService.saveUpdatedUser(user);
        return ResponseEntity.ok(addData);
    }
 
}
