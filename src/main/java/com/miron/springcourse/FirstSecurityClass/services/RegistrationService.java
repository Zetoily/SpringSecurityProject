package com.miron.springcourse.FirstSecurityClass.services;

import com.miron.springcourse.FirstSecurityClass.models.Person;
import com.miron.springcourse.FirstSecurityClass.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public void register(Person person){
       person.setPassword(passwordEncoder.encode(person.getPassword()));
       person.setRole("ROLE_USER");
       peopleRepository.save(person);
    }
}
