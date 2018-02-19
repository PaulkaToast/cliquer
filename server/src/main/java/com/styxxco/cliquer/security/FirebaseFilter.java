package com.styxxco.cliquer.security;

import com.styxxco.cliquer.service.FirebaseService;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Log4j
public class FirebaseFilter extends OncePerRequestFilter {

    private static String HEADER_NAME = "X-Authorization-Firebase";

    private FirebaseService firebaseService;

    public FirebaseFilter(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String xAuth = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(xAuth)) {
            filterChain.doFilter(request, response);
        } else {
            try {
                FirebaseTokenHolder holder = firebaseService.parseToken(xAuth);
                String uid = holder.getUid();
                Collection<? extends GrantedAuthority> roles = firebaseService.getUserRoles(uid);
                if (roles == null) {
                    roles = firebaseService.getAnonRoles();
                }

                Authentication auth = new FirebaseAuthenticationToken(uid, holder, roles);
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println(SecurityContextHolder.getContext());

                filterChain.doFilter(request, response);
            } catch (FirebaseParser.FirebaseTokenInvalidException e) {
                log.info("Authentication error: User is no longer authenticated");
            }
        }
    }

}
