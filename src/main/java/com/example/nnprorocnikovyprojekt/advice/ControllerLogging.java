package com.example.nnprorocnikovyprojekt.advice;

import com.example.nnprorocnikovyprojekt.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Arrays;

@Aspect
@Component
public class ControllerLogging {
    private static final Logger logger = LoggerFactory.getLogger(ControllerLogging.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Pointcut("execution(* com.example.nnprorocnikovyprojekt.controllers..*(..)) && " +
            "!execution(* com.example.nnprorocnikovyprojekt.controllers.UserController.login(..)) && " +
            "!execution(* com.example.nnprorocnikovyprojekt.controllers.UserController.verify2Fa(..)) && " +
            "!execution(* com.example.nnprorocnikovyprojekt.controllers.UserController.register(..)) && " +
            "!execution(* com.example.nnprorocnikovyprojekt.controllers.UserController.resetPassword(..)) && " +
            "!execution(* com.example.nnprorocnikovyprojekt.controllers.UserController.newPassword(..)) && " +
            "!execution(* com.example.nnprorocnikovyprojekt.controllers.UserController.updateUser(..))")
    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void logAfterControllerMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        if(args != null) {
            args = filterPrincipal(args);
        }
        String username = null;

        try {
            username = userService.getUserFromContext().getUsername();
        } catch (Exception e) {
            username = "username NOT in context";
        }

        try {
            String argsSerialized = objectMapper.writeValueAsString(args);
            logger.info("User {} executed controller method: {} with arguments: {}", username, methodName, argsSerialized);
        } catch (Exception ex){
            logger.warn("Failed to serialize arguments: {}", args);
            logger.info("User {} executed controller method: {} with arguments: {}", username, methodName, args);
        }
    }

    private Object[] filterPrincipal(Object[] args) {
        return Arrays.stream(args)
                .filter(arg -> !(arg instanceof Principal))
                .toArray();
    }
}
