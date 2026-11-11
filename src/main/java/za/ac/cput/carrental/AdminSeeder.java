package za.ac.cput.carrental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import za.ac.cput.carrental.domain.Member;
import za.ac.cput.carrental.repository.MemberRepository;
import za.ac.cput.carrental.util.PasswordUtil;

/** Creates the admin account on startup if it doesn't exist yet. */
@Component
public class AdminSeeder implements CommandLineRunner {

    public static final String ADMIN_EMAIL = "admin@carrental.co.za";
    public static final String ADMIN_PASSWORD = "Admin@123";   // change this after your first login

    @Autowired
    private MemberRepository members;

    @Override
    public void run(String... args) {
        if (members.findByEmailIgnoreCase(ADMIN_EMAIL).isEmpty()) {
            members.save(new Member.Builder()
                    .setMemberId("MEM-ADMIN")
                    .setName("Admin")
                    .setLicenseNumber("N/A")
                    .setPhoneNumber("0000000000")
                    .setRegion("Cape Town")
                    .setEmail(ADMIN_EMAIL)
                    .setPassword(PasswordUtil.hash(ADMIN_PASSWORD))
                    .setRole("ADMIN")
                    .setAccountStatus("Active")
                    .build());
        }
    }
}
