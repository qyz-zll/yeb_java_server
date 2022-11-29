package com.zll.server.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {
    //用户名的key
    private static final String CLAIM_KEY_USERNAME = "sub";
    //jwt的创建时间的key
    private static final String CLAIM_KEY_CREATED = "created";
    //jwt的密钥
    @Value("${jwt.secret}")
    private String secret;
    //jwt的失效时间
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 根据用户信息生成token
     *
     * @param userDetails
     * @return
     */
    Jedis jedis = new Jedis("localhost",6379);
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        System.out.println("开始");
        System.out.println(generateToken(claims));

        return generateToken(claims);
    }

    /**
     * 根据荷载生成jwt token
     *
     * @param claims
     * @return
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 生成token失效时间
     *
     * @return
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * 从token中获取登陆用户名
     *
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getclaimsfromtoken(token);
            username = claims.getSubject();
            System.out.println(username + "：用户名");
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 从token中获取荷载
     *
     * @param token
     * @return
     */
    private Claims getclaimsfromtoken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
            System.out.println(claims);
        }
        return claims;
    }



    /**
     * 判断token是否失效
     *
     * @param token
     * @return
     */
    private boolean isTokenExpired(String token) {
        Date expireDate = getExpiredDateFromToken(token);
        return expireDate.before(new Date());
    }
    /**
     * 从token中获取过期时间
     *
     * @param token
     * @return
     */
    private Date getExpiredDateFromToken(String token) {
        System.out.println(token + "这是过期的时间");
        Claims claims = getclaimsfromtoken(token);
        return claims.getExpiration();
    }


    /**
     * 判断token是否可以被刷新
     *
     * @param token
     * @return
     */
    public boolean canRefresh(String token) {
        return !isTokenExpired(token);
    }

    /**
     * 刷新token
     *
     * @param token
     * @return
     */
    public String refreshToken(String token) {
        Claims claims = getclaimsfromtoken(token);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);

    }
    /**
     * 验证token是否有效
     *
     * @param token
     * @param userDetails
     * @return
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        System.out.println(userDetails + "用户信息");
        String tokens = jedis.get(userDetails.getUsername());
        System.out.println("redis 存储的字符串为: "+ jedis.get(userDetails.getUsername()));
        String username = getUsernameFromToken(tokens);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(tokens);
    }

}
