package training.spring.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;

public class IdToken {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String of(UserDetails user) {
        return Jwts.builder().setSubject(user.getUsername()).signWith(key).compact();
    }
}
