package training.spring.filter;

import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import training.spring.security.IdToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    private static final String AUTHENTICATE_REQUEST_URI = "/authenticate";

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("AuthenticationFilter.doFilterInternal()");
        if (!AUTHENTICATE_REQUEST_URI.equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER_NAME);
        if (authHeader == null || !authHeader.startsWith(AUTHORIZATION_HEADER_PREFIX)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authorization header is missing");
            return;
        }

        String authHeaderUser;
        try {
            authHeaderUser = IdToken.of(
                authHeader.substring(AUTHORIZATION_HEADER_PREFIX.length())
            ).getUser();
        } catch (JwtException ex) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Access token is invalid");
            return;
        }

        if (authHeaderUser == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Access token is invalid");
            return;
        }

        UserDetails user = userDetailsService.loadUserByUsername(authHeaderUser);

        if (user == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Access token is invalid");
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
