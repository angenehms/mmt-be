package ssafy.mmt.common.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// 순수 자바 클래스라 따로 어노테이션을 붙이지 않는다!
public class JWTUtil {

    private static final SecretKey secretKey;
    private static final Long accessTokenExpiresIn; // 엑세스 토큰 생명주기
    private static final Long refreshTokenExpiresIn; // 리프레시 토큰 생명주기

    // static 필드 - 필드변수가 static 이라 기본 생성자로는 만들 수 없어서 static 영역 생성자로 만듬
    static  {
        String secretKeyString = "himynameiskimjihunmyyoutubechann"; // 32자리의 시크릿키
        secretKey = new SecretKeySpec(secretKeyString.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());

        accessTokenExpiresIn = 3600L * 1000; // 1시간
        refreshTokenExpiresIn = 604800L * 1000; // 7일
    }

    // *** isValid 로 검증한 이후에는 내부 페이로드에 담긴 데이터 값 파싱 ***
    // JWTFilter 에서 시큐리티 컨텍스트를 만들어줘야하는데 그때 필요한 "sub" 와 "role" 값 만들어서 String 으로 리턴할 수 있도록 만들자

    // JWT 클레임 username 파싱 - 바디 내부의 username 파싱
    public static String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("sub", String.class);
    }

    // JWT 클레임 role 파싱 - 바디 내부의 role 값 파싱
    public static String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public static Long getMemberId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("memberId", Long.class);
    }

    // *** isValid 로 검증한 이후에는 내부 페이로드에 담긴 데이터 값 파싱 ***

    // JWT 유효 여부 (위조, 시간, Access/Refresh 여부) - 우리가 만든 JWT 인지 검증 및 유효시간 검증
    public static Boolean isValid(String token, Boolean isAccess) {
        try { // try 로 진행하는 이유 : 파싱하는 과정에서 시간이 다되었다면 자동으로 exception 이 던져지기 때문
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey) // 파서로 시크릿키 검증
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (type == null) return false;

            if (isAccess && !type.equals("access")) return false;
            if (!isAccess && !type.equals("refresh")) return false;

            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // JWT(Access/Refresh) 생성
    // isAccess 는 이 JWT 가 엑세스인지 리프레시 토큰인지 구분
    public static String createJWT(String username, String role, Long memberId, Boolean isAccess) {

        long now = System.currentTimeMillis(); // 현재 시각에
        long expiry = isAccess ? accessTokenExpiresIn : refreshTokenExpiresIn;
        String type = isAccess ? "access" : "refresh";

        return Jwts.builder()
                // 내부 페이로드 구성
                .claim("sub", username)
                .claim("role", role)
                .claim("memberId", memberId) // 여기에 memberId 담음
                .claim("type", type) // 엑세스인지, 리프레시인지
                .issuedAt(new Date(now)) // JWT 발급시간
                .expiration(new Date(now + expiry)) // JWT 생명주기
                .signWith(secretKey) // 비밀키로 시그니처를 만드는 메서드
                .compact();
    }

}
