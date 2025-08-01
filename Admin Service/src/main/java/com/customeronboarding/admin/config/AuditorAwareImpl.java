package com.customeronboarding.admin.config;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Replace with Spring Security logic when available
        return Optional.of("admin"); // or "system" / dynamic user ID later
    }
}
