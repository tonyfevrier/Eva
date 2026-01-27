package com.eva.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eva.backend.model.Institution;
import com.eva.backend.repository.InstitutionRepository;

@Service
public class InstitutionService {
    @Autowired
    private InstitutionRepository institutionRepository;

    public Institution save(Institution institution){
        return institutionRepository.save(institution);
    }

    public Optional<Institution> findById(Long id){
        return institutionRepository.findById(id);
    }

    public void update(Institution institutionToUpdate, Institution newInstitution){
        String name = newInstitution.getName();
        if (name != null && !name.isEmpty()){
            institutionToUpdate.setName(name);
        }

        String town = newInstitution.getTown();
        if (town != null && !town.isEmpty()){
            institutionToUpdate.setTown(town);
        }

        String contactMail = newInstitution.getContactMail();
        if (contactMail != null && !contactMail.isEmpty()){
            institutionToUpdate.setContactMail(contactMail);
        }

        String category = newInstitution.getCategory();
        if (category != null && !category.isEmpty()){
            institutionToUpdate.setCategory(category);
        }

        Integer studentsNumber = newInstitution.getStudentsNumber();
        if (studentsNumber != null){
            institutionToUpdate.setStudentsNumber(studentsNumber);
        }

        String socialStatus = newInstitution.getSocialStatus();
        if (socialStatus != null && !socialStatus.isEmpty()){
            institutionToUpdate.setSocialStatus(socialStatus);
        }

        String institutionSpecifities = newInstitution.getInstitutionSpecifities();
        if (institutionSpecifities != null){
            institutionToUpdate.setInstitutionSpecifities(institutionSpecifities);
        }

        String studentsSpecificities = newInstitution.getStudentsSpecificities();
        if (studentsSpecificities != null){
            institutionToUpdate.setStudentsSpecificities(studentsSpecificities);
        }

        String teachersSpecificities = newInstitution.getTeachersSpecificities();
        if (teachersSpecificities != null){
            institutionToUpdate.setTeachersSpecificities(teachersSpecificities);
        }

        institutionRepository.save(institutionToUpdate);
    }
}
