package za.ac.cput.carrental.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import za.ac.cput.carrental.domain.Booking;
import za.ac.cput.carrental.domain.Member;
import za.ac.cput.carrental.service.BookingService;
import za.ac.cput.carrental.service.MemberService;

import java.io.IOException;

/**
 * Checks the login token on every request.
 *  - public:    browsing cars/garages, availability, login, register
 *  - customer:  own bookings, own profile, booking + paying
 *  - admin:     everything else
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String AUTH_MEMBER = "authMemberId";
    public static final String AUTH_ROLE = "authRole";

    @Autowired
    private TokenService tokens;
    @Autowired
    private BookingService bookings;
    @Autowired
    private MemberService members;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException {
        String method = req.getMethod().toUpperCase();
        String path = req.getRequestURI().replaceAll("/+$", "");

        if (method.equals("OPTIONS")) return true; // CORS pre-flight

        TokenService.Claims claims = null;
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            claims = tokens.verify(header.substring(7).trim());
        }
        if (claims != null) {
            req.setAttribute(AUTH_MEMBER, claims.memberId());
            req.setAttribute(AUTH_ROLE, claims.role());
        }

        if (isPublic(method, path)) return true;
        if (claims == null) return deny(res, 401, "Please log in again");
        if ("ADMIN".equals(claims.role())) return true;

        // A customer who was deleted or blocked loses access straight away, even with a valid token.
        Member me = members.read(claims.memberId());
        if (me == null) return deny(res, 401, "Please log in again");
        if (MemberService.isBlocked(me)) return deny(res, 403, "Your account has been blocked. Please contact the admin.");

        // ----- customer rules -----
        if (method.equals("POST") && path.equals("/booking")) return true;            // checked in the controller
        if (method.equals("PUT") && path.equals("/booking")) return true;             // extend: checked in the controller
        if (method.equals("GET") && path.equals("/booking/mine")) return true;
        if (method.equals("POST") && path.equals("/payment")) return true;            // checked in the controller
        if (method.equals("GET") && path.equals("/payment/mine")) return true;

        if (method.equals("PUT") && path.matches("/booking/[^/]+/cancel")) {
            Booking b = bookings.read(path.split("/")[2]);
            if (b != null && !claims.memberId().equals(b.getMemberId())) return deny(res, 403, "That isn't your booking");
            return true;
        }
        if ((method.equals("GET") && path.matches("/member/[^/]+"))
                || (method.equals("PUT") && path.matches("/member/[^/]+/(profile|password)"))) {
            if (!claims.memberId().equals(path.split("/")[2])) return deny(res, 403, "That isn't your account");
            return true;
        }

        return deny(res, 403, "Admins only");
    }

    private boolean isPublic(String method, String path) {
        if (path.equals("/error") || path.startsWith("/error/")) return true;
        if (method.equals("GET")) {
            if (path.equals("/car") || path.matches("/car/[^/]+")) return true;
            if (path.equals("/garage") || path.matches("/garage/[^/]+")) return true;
            if (path.equals("/booking/busy")) return true;
            if (path.startsWith("/uploads/cars/")) return true; // car photos
        }
        return method.equals("POST") && (path.equals("/member/login") || path.equals("/member/register") || path.equals("/member/forgot-password"));
    }

    private boolean deny(HttpServletResponse res, int status, String message) throws IOException {
        res.setStatus(status);
        res.setContentType("text/plain;charset=UTF-8");
        res.getWriter().write(message);
        return false;
    }
}