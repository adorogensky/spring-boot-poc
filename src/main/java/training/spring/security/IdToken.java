package training.spring.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;

public class IdToken {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private String jws;

    private IdToken(String jws) {
        this.jws = jws;
    }

    public static IdToken of(String jws) {
        return new IdToken(jws);
    }

    public static IdToken of(UserDetails user) {
        return new IdToken(
            Jwts.builder().setSubject(
                user.getUsername()
            ).signWith(key).compact()
        );
    }

    public String getUser() throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(jws).getBody().getSubject();
    }

    @Override
    public String toString() {
        return jws;
    }
}
