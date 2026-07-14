package za.ac.cput.carrental.domain;

import jakarta.persistence.*;

//Stephanie Tola Oluwafemi Lewu
// Student Number: 230211216
// 21 June 2026

@Entity
@Table(name = "members")
public class Member {

    @Id
    @Column(name = "member_id")
    private String memberId;
    private String name;
    private String licenseNumber;
    private String phoneNumber;
    private String accountStatus;

    protected Member(){

    }


    private Member(Builder builder) {
        this.memberId = builder.memberId;
        this.name = builder.name;
        this.licenseNumber = builder.licenseNumber;
        this.phoneNumber = builder.phoneNumber;
        this.accountStatus =  builder.accountStatus;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId='" + memberId + '\'' +
                ", name='" + name + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", accountStatus='" + accountStatus + '\'' +
                '}';
    }

    public static class Builder{
        private String memberId;
        private String name;
        private String licenseNumber;
        private String phoneNumber;
        private String accountStatus;

        public Builder setMemberId(String memberId){
            this.memberId = memberId;
            return this;
        }
        public Builder setName(String name){
            this.name = name;
            return this;
        }
        public Builder setLicenseNumber(String licenseNumber){
            this.licenseNumber = licenseNumber;
            return this;
        }
        public Builder setPhoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
            return this;
        }
        public Builder setAccountStatus(String accountStatus){
            this.accountStatus = accountStatus;
            return this;
        }

        public Builder copy(Member member) {
            this.memberId = member.getMemberId();
            this.name = member.getName();
            this.licenseNumber = member.getLicenseNumber();
            this.phoneNumber = member.getPhoneNumber();
            this.accountStatus = member.getAccountStatus();
            return this;
        }

        public Member build() {return new Member(this);
        }
    }
}