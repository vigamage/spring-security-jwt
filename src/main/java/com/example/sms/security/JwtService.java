package com.example.sms.security;

import com.example.sms.domain.operator.Operator;
import com.example.sms.exception.SmsException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Component
//@PropertySource("classpath:jwtconfig.yaml")
// PropertySource does not work with yaml by default - https://www.baeldung.com/spring-yaml-propertysource
@PropertySource("classpath:jwt.properties")
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.tokenIssuer}")
    private String tokenIssuer;

    @Value("${jwt.tokenExpirationTime}")
    private Long tokenExpirationTime;

    @Value("${jwt.tokenSignInKey}")
    private String tokenSignInKey;

    @Value("${jwt.refreshTokenExpirationTime}")
    private Long refreshTokenExpirationTime;

    /**
     * Create a jwt token and return
     *
     * @param operator
     * @return
     */
    public String createAccessJwtToken(Operator operator) {

        if (!StringUtils.hasLength(operator.getUserId()))
            throw new IllegalArgumentException("Cannot create JWT Token without user id");

        if (operator.getAuthorities() == null || operator.getAuthorities().isEmpty())
            throw new AuthenticationServiceException("User doesn't have any privileges");

        LocalDateTime currentTime = LocalDateTime.now();

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        List<String> roles = operator.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(tokenSignInKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        String jws = Jwts.builder()
                .setIssuer(tokenIssuer)
                .setSubject(operator.getUsername())
                .claim("roles", roles)
                .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(currentTime
                        .plusMinutes(tokenExpirationTime)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(
                        signingKey,
                        SignatureAlgorithm.HS256
                )
                .compact();

        return jws;
    }

    public String createRefreshToken(Operator operator) {
        if (!StringUtils.hasLength(operator.getUserId())) {
            throw new IllegalArgumentException("Cannot create JWT Token without user id");
        }

        LocalDateTime currentTime = LocalDateTime.now();

        Claims claims = Jwts.claims().subject(operator.getUsername()).build();

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(tokenSignInKey);
        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        String jws = Jwts.builder()
                .setIssuer(tokenIssuer)
                .setSubject(operator.getUserId())
                .addClaims(claims)
                .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(currentTime
                        .plusMinutes(tokenExpirationTime)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(
                        signingKey,
                        SignatureAlgorithm.HS256
                )
                .compact();

        return jws;

    }

    public Jws<Claims> getClaims(final String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try {
//            PublicKey publicKey = getPublicKey();
            JwtParser jwtParser = Jwts.parser().setSigningKey(tokenSignInKey).build();
            return jwtParser.parseSignedClaims(token);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
            log.error("Invalid JWT token ", ex);
            throw new BadCredentialsException("Invalid JWT token: ", ex);
        } catch (ExpiredJwtException expiredEx) {
            log.error("JWT Token is expired", expiredEx);
            throw expiredEx;
        }
    }


    private PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyBytes = Files.readAllBytes(Path.of("private.pem"));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);
        return privateKey;
    }

    private PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyBytes = Files.readAllBytes(Path.of("public.pem"));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(publicKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(spec);
        return publicKey;
    }

    public String getUsername(String token) {
        try {
            Jws<Claims> claims = getClaims(token);
            return claims.getPayload().getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = getUsername(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    private boolean isTokenExpired(String token) {
        try {
            Jws<Claims> claims = getClaims(token);
            Date expiration = claims.getPayload().getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
