/*MemberService.java
Author: Stephanie Tola Oluwafemi Lewu
Student Number: 230211216
07 July 2026
 */

package za.ac.cput.carrental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.carrental.domain.Member;
import za.ac.cput.carrental.repository.MemberRepository;
import za.ac.cput.carrental.util.PasswordUtil;

import java.util.List;
import java.util.UUID;

@Service
public class MemberService implements IMemberService {
    @Autowired
    private  MemberRepository member;

    @Override
    public Member create(Member member) {
        return this.member.save(member);
    }

    @Override
    public Member read(String id) {
        return this.member.findById(id).orElse(null);
    }

    @Override
    public Member update(Member incoming) {
        // Never let a plain update wipe the stored login details.
        Member existing = incoming.getMemberId() == null ? null : this.member.findById(incoming.getMemberId()).orElse(null);
        if (existing != null) {
            incoming = new Member.Builder().copy(incoming)
                    .setPassword(existing.getPassword())
                    .setEmail(incoming.getEmail() != null ? incoming.getEmail() : existing.getEmail())
                    .setRole(existing.getRole())
                    .setRegion(incoming.getRegion() != null ? incoming.getRegion() : existing.getRegion())
                    .build();
        }
        return this.member.save(incoming);
    }

    @Override
    public boolean delete(String id) {
        this.member.deleteById(id);
        return true;
    }

    @Override
    public List<Member> getAll() {
        return this.member.findAll();
    }

    // ---------- Login / registration ----------

    /** Creates a CUSTOMER account. Throws IllegalArgumentException (bad input) or IllegalStateException (email taken). */
    public Member register(Member req) {
        String email = req.getEmail() == null ? "" : req.getEmail().trim().toLowerCase();
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Enter a valid email address");
        }
        if (req.getPassword() == null || req.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        validateProfile(req);
        if (this.member.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalStateException("That email is already registered");
        }

        Member created = new Member.Builder()
                .setMemberId("MEM-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .setName(req.getName().trim())
                .setLicenseNumber(req.getLicenseNumber())
                .setPhoneNumber(req.getPhoneNumber())
                .setRegion(req.getRegion().trim())
                .setEmail(email)
                .setPassword(PasswordUtil.hash(req.getPassword()))
                .setRole("CUSTOMER")   // role is always set here, never taken from the request
                .setAccountStatus("Active Member")
                .build();
        return this.member.save(created);
    }

    /** Returns the member when the email and password match, otherwise null. */
    public Member login(String email, String password) {
        if (email == null || password == null) return null;
        Member found = this.member.findByEmailIgnoreCase(email.trim()).orElse(null);
        if (found == null || !PasswordUtil.matches(password, found.getPassword())) return null;
        return found;
    }

    /** Updates name, licence, phone and region only. Email, password and role are kept. */
    public Member updateProfile(String id, Member changes) {
        Member existing = this.member.findById(id).orElse(null);
        if (existing == null) return null;
        validateProfile(changes);
        Member updated = new Member.Builder().copy(existing)
                .setName(changes.getName().trim())
                .setLicenseNumber(changes.getLicenseNumber())
                .setPhoneNumber(changes.getPhoneNumber())
                .setRegion(changes.getRegion().trim())
                .build();
        return this.member.save(updated);
    }

    private void validateProfile(Member m) {
        if (m.getName() == null || m.getName().isBlank()) {
            throw new IllegalArgumentException("Enter your full name");
        }
        if (m.getLicenseNumber() == null || !m.getLicenseNumber().matches("^CPT-\\d{4}$")) {
            throw new IllegalArgumentException("Licence must be CPT- followed by 4 digits");
        }
        if (m.getPhoneNumber() == null || !m.getPhoneNumber().matches("^\\d{10}$")) {
            throw new IllegalArgumentException("Phone number must be exactly 10 digits");
        }
        if (m.getRegion() == null || m.getRegion().isBlank()) {
            throw new IllegalArgumentException("Enter your region");
        }
    }
}