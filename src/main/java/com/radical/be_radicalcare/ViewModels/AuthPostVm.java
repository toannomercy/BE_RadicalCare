package com.radical.be_radicalcare.ViewModels;

import lombok.Builder;

@Builder
public record AuthPostVm(
        String idToken
) {
}
