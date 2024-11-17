package com.radical.be_radicalcare.ViewModels;

import com.radical.be_radicalcare.Entities.User;
import lombok.Builder;

@Builder
public record UserGetVm(
        String id,
        String username,
        String email,
        String phone,
        String gender,
        String provider,
        Boolean accountNonExpired,
        Boolean accountNonLocked,
        Boolean credentialsNonExpired,
        Boolean enabled) {

    public static UserGetVm fromEntity(User user) {
        return UserGetVm.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender())
                .provider(user.getProvider().toString())
                .accountNonExpired(user.getAccountNonExpired())
                .accountNonLocked(user.getAccountNonLocked())
                .credentialsNonExpired(user.getCredentialsNonExpired())
                .enabled(user.getEnabled())
                .build();
    }
}
