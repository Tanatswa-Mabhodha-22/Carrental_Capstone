package za.ac.cput.carrental.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

/**
 * Creates and checks signed login tokens (no extra libraries needed).
 * Token = base64url(memberId|role|expiry) + "." + base64url(HMAC-SHA256 signature).
 *
 * The signing secret comes from app.auth.secret in application.properties.
 * If it is not set, a random one is made at startup (everyone is logged out when the backend restarts).
 */
@Component
public class TokenService {

    public record Claims(String memberId, String role) {
    }

    private static final long LIFETIME_SECONDS = 12 * 60 * 60; // 12 hours

    private final byte[] secret;

    public TokenService(@Value("${app.auth.secret:}") String configuredSecret) {
        if (configuredSecret == null || configuredSecret.isBlank()) {
            this.secret = new byte[32];
            new SecureRandom().nextBytes(this.secret);
        } else {
            this.secret = configuredSecret.getBytes(StandardCharsets.UTF_8);
        }
    }

    public String create(String memberId, String role) {
        long expires = Instant.now().getEpochSecond() + LIFETIME_SECONDS;
        String payload = memberId + "|" + (role == null ? "CUSTOMER" : role) + "|" + expires;
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return encoded + "." + sign(encoded);
    }

    /** Returns the claims when the token is genuine and not expired, otherwise null. */
    public Claims verify(String token) {
        if (token == null) return null;
        String[] parts = token.split("\\.");
        if (parts.length != 2) return null;
        byte[] expected = sign(parts[0]).getBytes(StandardCharsets.UTF_8);
        byte[] actual = parts[1].getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expected, actual)) return null;
        try {
            String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            String[] f = payload.split("\\|");
            if (f.length != 3) return null;
            if (Long.parseLong(f[2]) < Instant.now().getEpochSecond()) return null;
            return new Claims(f[0], f[1]);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Token signing failed", e);
        }
    }
}