package com.E1i3.NoExit.domain.common.service;

import com.E1i3.NoExit.domain.member.domain.Member;
import com.E1i3.NoExit.domain.member.domain.Role;
import com.E1i3.NoExit.domain.member.repository.MemberRepository;
import com.E1i3.NoExit.domain.owner.domain.Owner;
import com.E1i3.NoExit.domain.owner.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitialDataLoader implements CommandLineRunner {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Check and create OWNER account if not exis
        // ts
        if (ownerRepository.findByEmail("owner@test.com").isEmpty()) {
            Owner admin = Owner.builder()
                    .username("owner")
                    .email("owner@test.com")
                    .password(passwordEncoder.encode("12341234")) // Encode password
                    .storeName("Admin Store") // Assuming storeName is required for Owner
                    .phoneNumber("010-1234-5678") // Example phone number
                    .role(Role.OWNER) // Ensure Role is set appropriately
                    .build();
            ownerRepository.save(admin);
        }

        // Check and create USER account if not exists
        if (memberRepository.findByEmail("user@test.com").isEmpty()) {
            Member user = Member.builder()
                    .username("user")
                    .email("user@test.com")
                    .password(passwordEncoder.encode("12341234")) // Encode password
                    .role(Role.USER)
                    .phone_number("010-1234-5678") // Example phone number
                    .nickname("user") // Example nickname
                    .profileImage("defaultImageUrl") // Set default or placeholder image URL
                    .build();
            memberRepository.save(user);
        }
    }
}
