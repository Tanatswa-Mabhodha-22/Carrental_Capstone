/*MemberServiceTest.java
Author: Stephanie Tola Oluwafemi Lewu
Student Number: 230211216
20 July 2026
 */

package za.ac.cput.carrental.factory;



import za.ac.cput.carrental.domain.Member;
import za.ac.cput.carrental.util.Helper;

public class MemberFactory {
   public static Member createMember(String memberId,
                                     String name,
                                     String licenseNumber,
                                     String phoneNumber,
                                     String accountStatus){
       return new Member.Builder()
               .setMemberId(memberId)
               .setName(name)
               .setLicenseNumber(licenseNumber)
               .setPhoneNumber(phoneNumber)
               .setAccountStatus(accountStatus)
               .build();
   }
}
