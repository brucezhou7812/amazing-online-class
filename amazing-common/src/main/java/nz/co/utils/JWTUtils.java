package nz.co.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import nz.co.constant.ConstantOnlineClass;
import nz.co.model.UserLoginModel;

import java.util.Date;

@Slf4j
public class JWTUtils {

    private static final String SUBJECT = "online-class";
    private static final String SECRET = "online-class-bruce";
    private static final String TOKEN_PREFIX = "online-class";
    public static String generateToken(UserLoginModel userLoginModel){
        if(userLoginModel == null){
            throw new NullPointerException("userLoginModel is null");
        }
        String token = Jwts.builder().setSubject(SUBJECT)
                .claim("head_img",userLoginModel.getHeadImg())
                .claim("id",userLoginModel.getId())
                .claim("mail",userLoginModel.getMail())
                .claim("name",userLoginModel.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ConstantOnlineClass.EXPIRE_TIME_FOR_ACCESS_TOKEN))
                .signWith(SignatureAlgorithm.HS256,SECRET)
                .compact();
        return TOKEN_PREFIX+token;
    }

    public static Claims checkToken(String token){
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
            return claims;
        }catch (Exception e){
            log.info("Token parse failed");
            return null;
        }
    }

    public static String refreshToken(String token){
        Claims claims = checkToken(token);
        return TOKEN_PREFIX+Jwts.builder().setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ ConstantOnlineClass.EXPIRE_TIME_FOR_ACCESS_TOKEN))
                .signWith(SignatureAlgorithm.HS256,SECRET)
                .compact();
    }
}
