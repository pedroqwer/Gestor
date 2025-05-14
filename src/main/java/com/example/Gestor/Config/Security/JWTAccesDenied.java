package com.example.Gestor.Config.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JWTAccesDenied implements AccessDeniedHandler
{
    private final Logger logger = LoggerFactory.getLogger(JWTAccesDenied.class);

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException
    {
        //Se ejecuta cuando no está autorizado mediante roles.
        logger.error("Unauthorized error: {}", accessDeniedException.getMessage());

        throw new AccessDeniedException(accessDeniedException.getMessage());
    }
}
