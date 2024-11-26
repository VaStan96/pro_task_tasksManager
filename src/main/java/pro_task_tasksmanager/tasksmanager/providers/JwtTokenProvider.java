package pro_task_tasksmanager.tasksmanager.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret; // JWT-Key von .properties

    Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

    //Parsing
    public String getUserNameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token) //parse
                .getBody();
        return claims.getSubject(); // token -> "sub"
    }

    //Validate
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true; //valid
        } catch (JwtException | IllegalArgumentException e) {
            return false; //invalid
        }
    }
}
