package za.ac.cput.carrental.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    // --- login fields ---
    private String email;
    // Stores the password HASH. WRITE_ONLY: accepted in requests but never sent back in responses.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String role;   // "CUSTOMER" or "ADMIN"
    private String region;

    protected Member(){

    }


    private Member(Builder builder) {
        this.memberId = builder.memberId;
        this.name = builder.name;
        this.licenseNumber = builder.licenseNumber;
        this.phoneNumber = builder.phoneNumber;
        this.accountStatus =  builder.accountStatus;
        this.email = builder.email;
        this.password = builder.password;
        this.role = builder.role;
        this.region = builder.region;
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

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getRegion() {
        return region;
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId='" + memberId + '\'' +
                ", name='" + name + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", accountStatus='" + accountStatus + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", region='" + region + '\'' +
                '}';
    }

    public static class Builder{
        private String memberId;
        private String name;
        private String licenseNumber;
        private String phoneNumber;
        private String accountStatus;
        private String email;
        private String password;
        private String role;
        private String region;

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
        public Builder setEmail(String email){
            this.email = email;
            return this;
        }
        // Pass an already-hashed password (see PasswordUtil.hash).
        public Builder setPassword(String password){
            this.password = password;
            return this;
        }
        public Builder setRole(String role){
            this.role = role;
            return this;
        }
        public Builder setRegion(String region){
            this.region = region;
            return this;
        }

        public Builder copy(Member member) {
            this.memberId = member.getMemberId();
            this.name = member.getName();
            this.licenseNumber = member.getLicenseNumber();
            this.phoneNumber = member.getPhoneNumber();
            this.accountStatus = member.getAccountStatus();
            this.email = member.getEmail();
            this.password = member.getPassword();
            this.role = member.getRole();
            this.region = member.getRegion();
            return this;
        }

        public Member build() {return new Member(this);
        }
    }
}