package za.ac.cput.carrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.carrental.domain.Member;
import za.ac.cput.carrental.service.MemberService;

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
}