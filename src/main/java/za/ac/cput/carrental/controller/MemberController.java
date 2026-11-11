package za.ac.cput.carrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.carrental.domain.Member;
import za.ac.cput.carrental.service.MemberService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

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
            return ResponseEntity.ok(this.memberService.register(member));
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
        return ResponseEntity.ok(member);
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