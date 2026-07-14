/*MemberService.java
Author: Stephanie Tola Oluwafemi Lewu
Student Number: 230211216
07 July 2026
 */

package za.ac.cput.carrental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.carrental.domain.Member;
import za.ac.cput.carrental.factory.MemberFactory;
import za.ac.cput.carrental.repository.MemberRepository;

import java.util.List;

@Service
public class MemberService implements IMemberService {
@Autowired
    private  MemberRepository member;

    @Override
    public Member craete(Member member) {
        return this.member.save(member);
    }

    @Override
    public Member read(String id) {
        return this.member.findById(id).orElse(null);
    }

    @Override
    public Member update(Member member) {
        return this.member.save(member);
    }

    @Override
    public boolean delete(String id) {
        this.member.deleteById(id);
        return true;
    }
}



