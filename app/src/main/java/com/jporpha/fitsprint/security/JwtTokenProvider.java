package com.jporpha.fitsprint.security;

import com.jporpha.fitsprint.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TEAM_ID = "teamId";

    private final JwtProperties properties;
    private final SecretKey key;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(properties.getSecret()));
    }

    public String generateToken(AuthenticatedUser user) {
        Instant now = Instant.now();
        var builder = Jwts.builder()
                .subject(String.valueOf(user.userId()))
                .claim(CLAIM_EMAIL, user.email())
                .claim(CLAIM_ROLE, user.role().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(properties.getExpirationDays(), ChronoUnit.DAYS)))
                .signWith(key);
        if (user.teamId() != null) {
            builder.claim(CLAIM_TEAM_ID, user.teamId());
        }
        return builder.compact();
    }

    public Optional<AuthenticatedUser> parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = Long.valueOf(claims.getSubject());
            String email = claims.get(CLAIM_EMAIL, String.class);
            Role role = Role.valueOf(claims.get(CLAIM_ROLE, String.class));
            Number teamIdClaim = claims.get(CLAIM_TEAM_ID, Number.class);
            Long teamId = teamIdClaim == null ? null : teamIdClaim.longValue();

            return Optional.of(new AuthenticatedUser(userId, email, role, teamId));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
