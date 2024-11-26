// Aplicação do filtro de autenticação do token JWT
package com.example.api2024.configCors;

import com.example.api2024.service.AdmDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthToken extends OncePerRequestFilter{


    @Autowired
    private JwtGenerate jwtGenerate;

    @Autowired
    private AdmDetailsService admDetailsService;

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Obtém o token JWT da requisição
            String jwt = getToken(request);

            // Mostra o token no console (para depuração)
            if (jwt != null) {
                System.out.println("Token recebido no AuthToken filter: " + jwt);
            }

            // Valida o token e configura a autenticação no contexto de segurança
            if (jwt != null && jwtGenerate.JwtToken(jwt)) {
                String email = jwtGenerate.UsernameToken(jwt);
                UserDetails userDetails = admDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            System.out.println("Erro no filtro de autenticação: " + e.getMessage());
        }

        // Continua a cadeia de filtros
        filterChain.doFilter(request, response);
    }


    private String getToken(HttpServletRequest request) {
        String headerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(headerToken) && headerToken.startsWith("Bearer ")) {
            return headerToken.replace("Bearer ", "");
        }
        return null;
    }
}
