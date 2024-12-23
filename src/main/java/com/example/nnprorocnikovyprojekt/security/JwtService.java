package com.example.nnprorocnikovyprojekt.security;

import com.example.nnprorocnikovyprojekt.entity.AuthToken;
import com.example.nnprorocnikovyprojekt.entity.User;
import com.example.nnprorocnikovyprojekt.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String SECRET;

    @Value("${application.security.jwt.expiration}")
    private Long TOKEN_EXPIRATION;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    public boolean isTokenValid(String plainTextToken, User user){
        AuthToken activeAuthToken = user.getActiveAuthToken();
        if(activeAuthToken == null) return false;
        return passwordEncoder.matches(plainTextToken, activeAuthToken.getJwtHash());
    }

    public AuthToken generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return createToken(claims, user);
    }

    private AuthToken createToken(Map<String, Object> claims, User user) {
        Date expirationDate = new Date(System.currentTimeMillis() + TOKEN_EXPIRATION);
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        AuthToken authToken = new AuthToken(user, passwordEncoder.encode(token), true, token);
        userService.addAuthTokenAndInvalidateOthers(user, authToken);
        return authToken;
    }

    // Get the signing key for JWT token
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract the username from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract the expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract a claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token is expired
    public Boolean isTokenExpiredOrNull(String token) {
        if(token == null) return true;
        return extractExpiration(token).before(new Date());
    }

    // Validate the token against user details and expiration
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpiredOrNull(token) && isTokenValid(token, (User)userDetails));
    }
}
