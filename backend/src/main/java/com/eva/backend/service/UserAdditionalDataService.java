package com.eva.backend.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.eva.backend.model.UserAdditionalData;
import com.eva.backend.repository.UserAdditionalDataRepository;

public class UserAdditionalDataService {
    @Autowired
    private UserAdditionalDataRepository addRepository;

    public UserAdditionalData save(UserAdditionalData addData){
        return addRepository.save(addData);
    }
}
