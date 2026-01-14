package com.eva.backend.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eva.backend.model.User;
import com.eva.backend.model.UserAdditionalData;
import com.eva.backend.repository.UserAdditionalDataRepository;

@Service
public class UserAdditionalDataService {
    @Autowired
    private UserAdditionalDataRepository addRepository;

    public UserAdditionalData save(UserAdditionalData addData){
        return addRepository.save(addData);
    }

    public Optional<UserAdditionalData> findByUser(User user){
        return addRepository.findById(user.getId());
    }

    public void update(Map<String, Object> body, UserAdditionalData additionalData){
        String affiliation = (String) body.get("affiliation");
        if (affiliation != null){
            additionalData.setAffiliation(affiliation);
        }

        Boolean acceptMap = (Boolean) body.get("acceptMap");
        if (acceptMap != null){
            additionalData.setAcceptMap(acceptMap);
        }

        Boolean acceptContact = (Boolean) body.get("acceptContact");
        if (acceptContact != null){
            additionalData.setAcceptContact(acceptContact);
        }

        String street = (String) body.get("street");
        if (street != null){
            additionalData.setStreet(street);
        }

        String postcode = (String) body.get("postcode");
        if (postcode != null){
            additionalData.setPostcode(postcode);
        }

        String town = (String) body.get("town");
        if (town != null){
            additionalData.setTown(town);
        }

        String phone = (String) body.get("phone");
        if (phone != null){
            additionalData.setPhone(phone);
        }

        save(additionalData);
    }
}
