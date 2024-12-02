package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Constants.Provider;
import com.radical.be_radicalcare.Constants.RoleType;
import com.radical.be_radicalcare.Entities.User;
import com.radical.be_radicalcare.Repositories.IRoleRepository;
import com.radical.be_radicalcare.Repositories.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;

    @Autowired
    public OAuth2UserService(IUserRepository userRepository, IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String uuid = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String fullName = oAuth2User.getAttribute("name");

        User user = userRepository.findByUsername(uuid);
        if (user == null) {
            user = new User();
            user.setUsername(uuid);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setProvider(Provider.GOOGLE);
            user.setRoles(Set.of(roleRepository.findRoleById(RoleType.USER.value)));
            user.setPassword("N/A");

            userRepository.save(user);
        } else {
            log.info("User already exists with UUID: {}", uuid);
            log.info("Full Name: {}", fullName);
        }

        return oAuth2User;
    }

}

