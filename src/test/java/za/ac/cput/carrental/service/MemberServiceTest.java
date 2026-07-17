/*MemberServiceTest.java
Author: Stephanie Tola Oluwafemi Lewu
Student Number: 230211216
07 July 2026
 */

package za.ac.cput.carrental.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.carrental.factory.MemberFactory;

import za.ac.cput.carrental.domain.Member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MemberServiceTest {
   private Member member;

   @BeforeEach
    void setup(){
       member = MemberFactory.createMember(
               "A001",
               "Stephanie Lewu",
               "AIJ44667899",
               "0665119940",
               "Active"
       );
   }

   @Test
    void testMemberIsNotNull(){
       assertNotNull(member);
   }

   @Test
    void testGetMemberId(){
       assertEquals("A001", member.getMemberId());
   }

   @Test
    void testGetName(){
       assertEquals("Stephanie Lewu", member.getName());
   }

    @Test
    void testGetLicenseNumber() {
        assertEquals("AIJ44667899", member.getLicenseNumber());
    }

    @Test
    void testGetPhoneNumber() {
        assertEquals("0665119940", member.getPhoneNumber());
    }

    @Test
    void testGetAccountStatus() {
        assertEquals("Active", member.getAccountStatus());
    }

    @Test
    void testToString() {
        assertNotNull(member.toString());
}
}
