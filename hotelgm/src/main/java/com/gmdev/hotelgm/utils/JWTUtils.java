package com.gmdev.hotelgm.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

/*

    this code is used for creating secret key for signing JWT tokens, which is used to identify users.
    securely in web app--for login systems
    expiry date --> How long key is valid.

    What is JWT
        --A JSON Web token is compact, URL-Safe Way to represent claims between two parties.
        --Authenticate a user(prove they're logged in)
        --Authorize access to resources(check their role/permissions)
     JWT Has 3 part
        --Base64Url-Header.{Base64Url-payload},{Base64Url-Signature}
     Header Describes
        --Algorith "HS256" Type
     Payload(Claims)
        --Your Data
        --subject : 'mane.ganesh.pc@gmail.com'
        --issuedAt
        --expiry
      Signature
        --HMAC-SHA256
        --ensures the token wasn't tampered with.
    Signing a JWT
        --
 */
@Service
public class JWTUtils {
    private static final long EXPIRATION_TIME=1000*60*60*24*7;//for 7 Days
    private SecretKey key;
    public JWTUtils(){
        String secretString = "5548589548959689965889658996333581855886278963214785236983256325896322593217823687554455464647423421121221445585681248459476752";
        byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");

    }

    public String generateToken(UserDetails userDetails){

        return Jwts.builder().subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(key).compact();
    }

    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims,T> claimsTFunction){
        return claimsTFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
    }

    public boolean isValidToken(String token,UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token){
        return extractClaims(token,Claims::getExpiration).before(new Date());
    }


}
