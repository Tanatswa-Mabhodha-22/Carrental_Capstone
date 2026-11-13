package za.ac.cput.carrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.carrental.domain.Member;
import za.ac.cput.carrental.security.TokenService;
import za.ac.cput.carrental.service.MemberService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    /** What login and register send back: the member plus a login token. */
    public record AuthResponse(String token, Member member) {
    }

    @Autowired
    private MemberService memberService;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public Member create(@RequestBody Member member) {
        return this.memberService.create(member);
    }

    @GetMapping("/{id}")
    public Member read(@PathVariable String id) {
        return this.memberService.read(id);
    }

    @PutMapping
    public Member update(@RequestBody Member member) {
        return this.memberService.update(member);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        this.memberService.delete(id);
    }

    @GetMapping
    public List<Member> getAll() {
        return this.memberService.getAll();
    }

    // ---------- Login / registration ----------

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Member member) {
        try {
            Member created = this.memberService.register(member);
            return ResponseEntity.ok(new AuthResponse(this.tokenService.create(created.getMemberId(), created.getRole()), created));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        Member member = this.memberService.login(body.get("email"), body.get("password"));
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong email or password");
        }
        if (MemberService.isBlocked(member)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Your account has been blocked. Please contact the admin.");
        }
        return ResponseEntity.ok(new AuthResponse(this.tokenService.create(member.getMemberId(), member.getRole()), member));
    }

    // Admin: block or unblock a customer (a blocked customer can't log in)
    @PutMapping("/{id}/block")
    public ResponseEntity<?> block(@PathVariable String id) {
        return setBlocked(id, true);
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<?> unblock(@PathVariable String id) {
        return setBlocked(id, false);
    }

    private ResponseEntity<?> setBlocked(String id, boolean blocked) {
        try {
            Member updated = this.memberService.setBlocked(id, blocked);
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Logged-in customer changes their own password
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            this.memberService.changePassword(id, body.get("currentPassword"), body.get("newPassword"));
            return ResponseEntity.ok("Password changed");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Forgot password: email + licence + phone must match the account
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        try {
            this.memberService.resetPassword(body.get("email"), body.get("licenseNumber"), body.get("phoneNumber"), body.get("newPassword"));
            return ResponseEntity.ok("Password changed");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable String id, @RequestBody Member changes) {
        try {
            Member updated = this.memberService.updateProfile(id, changes);
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}