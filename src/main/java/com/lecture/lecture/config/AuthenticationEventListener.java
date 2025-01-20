package com.lecture.lecture.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationEventListener.class);

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent successEvent) {
            Authentication auth = successEvent.getAuthentication();
            logger.info("로그인 성공: 사용자={}, 시간={}", auth.getName(), new java.util.Date());
        } else if (event instanceof AuthenticationFailureBadCredentialsEvent failureEvent) {
            Authentication auth = failureEvent.getAuthentication();
            logger.warn("로그인 실패: 사용자={}, 시간={}, 이유={}", auth.getName(), new java.util.Date(), failureEvent.getException().getMessage());
        }
      
    }
}