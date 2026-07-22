/*MemberServiceTest.java
Author: Stephanie Tola Oluwafemi Lewu
Student Number: 230211216
20 July 2026
 */

package za.ac.cput.carrental.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.carrental.domain.Member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MemberFactoryTest {

    @Test
    void testCreateMemberBooking_Success(){
        Member member = MemberFactory.createMember(
                "A001",
                "Stephanie Lewu",
                "STOL230211",
                "0665119940",
                "ACTIVE"
        );
        assertNotNull(member);
        assertNotNull("A001",member.getMemberId());
        assertEquals("Stephanie Lewu",member.getName());
        assertEquals("STOL230211" ,member.getLicenseNumber());
        assertEquals("0665119940" ,member.getPhoneNumber());
        assertEquals("ACTIVE" ,member.getAccountStatus());
    }

    @Test
    void testCreateMember_NotNull(){
        Member member = MemberFactory.createMember(
                "A001",
                "Stephanie Lewu",
                "STOL230211",
                "0665119940",
                "ACTIVE"
        );

        assertNotNull(member);
    }
}
