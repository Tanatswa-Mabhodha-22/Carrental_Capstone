package za.ac.cput.carrental.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.carrental.domain.Member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MemberFactoryTest {

    @Test
    void testCreateMemberBooking_Success(){
        Member member = MemberFactory.createMember(
                "M001",
                "Stephanie Lewu",
                "STOL230211",
                "0665119940",
                "ACTIVE"
        );
        assertNotNull(member);
        assertEquals("M001", member.getMemberId());
        assertEquals("Stephanie Lewu",member.getName());
        assertEquals("STOL230211" ,member.getLicenseNumber());
        assertEquals("0665119940" ,member.getPhoneNumber());
        assertEquals("ACTIVE" ,member.getAccountStatus());
    }

    @Test
    void testCreateMember_NotNull(){
        Member member = MemberFactory.createMember(
                "M001",
                "Stephanie Lewu",
                "STOL230211",
                "0665119940",
                "ACTIVE"
        );

        assertNotNull(member);
    }
}
