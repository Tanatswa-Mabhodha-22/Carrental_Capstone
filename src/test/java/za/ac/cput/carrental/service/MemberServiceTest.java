package za.ac.cput.carrental.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.carrental.domain.Member;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    private static Member member;

    @BeforeAll
    static void setUp() {
        member = new Member.Builder()
                .setMemberId("A001")
                .setName("Stephanie Lewu")
                .setLicenseNumber("AIJ44667899")
                .setPhoneNumber("0665119940")
                .setAccountStatus("Active")
                .build();
    }

    @Test
    @Order(1)
    void create() {
        Member created = memberService.create(member);
        assertNotNull(created);
        assertEquals(member.getMemberId(), created.getMemberId());
        System.out.println("Created: " + created);
    }

    @Test
    @Order(2)
    void read() {
        Member read = memberService.read(member.getMemberId());
        assertNotNull(read);
        assertEquals(member.getMemberId(), read.getMemberId());
        System.out.println("Read: " + read);
    }

    @Test
    @Order(3)
    void update() {
        Member updated = new Member.Builder()
                .copy(member)
                .setAccountStatus("Inactive")
                .build();
        Member result = memberService.update(updated);
        assertNotNull(result);
        assertEquals("Inactive", result.getAccountStatus());
        System.out.println("Updated: " + result);
    }

    @Test
    @Order(4)
    void getAll() {
        List<Member> members = memberService.getAll();
        assertNotNull(members);
        assertFalse(members.isEmpty());
        System.out.println("All members: " + members);
    }

    @Test
    @Order(5)
    void delete() {
        boolean deleted = memberService.delete(member.getMemberId());
        assertTrue(deleted);
        System.out.println("Deleted: " + deleted);
    }
}

