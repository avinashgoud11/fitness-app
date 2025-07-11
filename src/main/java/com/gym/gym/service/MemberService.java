package com.gym.gym.service;

import com.gym.gym.model.Member;
import com.gym.gym.model.MembershipType;
import com.gym.gym.repository.MemberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.gym.gym.model.Role;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new member
     * @param member Member object containing registration details
     * @return Registered member with encoded password
     */
    public Member registerMember(Member member) {
        // Check if email already exists
        if (memberRepository.findByUser_Email(member.getUser().getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Encode password before saving
        member.getUser().setPassword(passwordEncoder.encode(member.getUser().getPassword()));
        
        // Set default values
        member.setActive(true);
        member.setMembershipStartDate(java.time.LocalDateTime.now());
        
    // SET DEFAULT ROLE
    member.getUser().setRole(Role.ROLE_MEMBER); // Set role as enum Role.USER
    
        return memberRepository.save(member);
    }

    /**
     * Authenticate a member
     * @param email Member's email
     * @param password Member's password
     * @return Authenticated member if credentials are valid
     */
    public Member authenticateMember(String email, String password) {
        Optional<Member> memberOpt = memberRepository.findByUser_Email(email);
        
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            if (passwordEncoder.matches(password, member.getUser().getPassword())) {
                return member;
            }
        }
        
        throw new RuntimeException("Invalid email or password");
    }

    /**
     * Get member by ID
     * @param id Member ID
     * @return Member if found
     */
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    /**
     * Get member by email
     * @param email Member's email
     * @return Member if found
     */
    public Member getMemberByEmail(String email) {
        return memberRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    /**
     * Update member details
     * @param id Member ID
     * @param updatedMember Updated member details
     * @return Updated member
     */
    public Member updateMember(Long id, Member updatedMember) {
        Member existingMember = getMemberById(id);
        
        // Update fields
        existingMember.getUser().setFirstName(updatedMember.getUser().getFirstName());
        existingMember.getUser().setLastName(updatedMember.getUser().getLastName());
        existingMember.setPhoneNumber(updatedMember.getPhoneNumber());
        existingMember.setAddress(updatedMember.getAddress());
        
        // Update password if provided
        if (updatedMember.getUser().getPassword() != null && !updatedMember.getUser().getPassword().isEmpty()) {
            existingMember.getUser().setPassword(passwordEncoder.encode(updatedMember.getUser().getPassword()));
        }
        
        return memberRepository.save(existingMember);
    }

    /**
     * Update member's plan
     * @param id Member ID
     * @param plan New plan
     * @return Updated member
     */
    public Member updatePlan(Long id, String plan) {
        Member member = getMemberById(id);
        member.setMembershipType(MembershipType.valueOf(plan));
        return memberRepository.save(member);
    }

    /**
     * Get all members
     * @return List of all members
     */
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    /**
     * Deactivate a member
     * @param id Member ID
     * @return Deactivated member
     */
    public Member deactivateMember(Long id) {
        Member member = getMemberById(id);
        member.setActive(false);
        return memberRepository.save(member);
    }

    /**
     * Reactivate a member
     * @param id Member ID
     * @return Reactivated member
     */
    public Member reactivateMember(Long id) {
        Member member = getMemberById(id);
        member.setActive(true);
        return memberRepository.save(member);
    }

    /**
     * Reset member's password
     * @param email Member's email
     * @param newPassword New password
     * @return Member with updated password
     */
    public Member resetPassword(String email, String newPassword) {
        Member member = getMemberByEmail(email);
        member.getUser().setPassword(passwordEncoder.encode(newPassword));
        return memberRepository.save(member);
    }

    /**
     * Check if member has access to a specific feature based on their plan
     * @param memberId Member ID
     * @param feature Feature to check access for
     * @return true if member has access, false otherwise
     */
    public boolean hasAccessToFeature(Long memberId, String feature) {
        Member member = getMemberById(memberId);
        String plan = member.getMembershipType().toString().toLowerCase();
        
        switch (feature.toLowerCase()) {
            case "classes":
                return true; // All plans have access to classes
            case "tracker":
                return plan.equals("standard") || plan.equals("premium");
            case "personal_trainer":
                return plan.equals("standard") || plan.equals("premium");
            case "spa":
                return plan.equals("premium");
            default:
                return false;
        }
    }

    public Member updateMedicalConditions(Long id, String medicalConditions) {
        Member member = getMemberById(id);
        member.setMedicalConditions(medicalConditions);
        return memberRepository.save(member);
    }

    public Member updateFitnessGoals(Long id, String fitnessGoals) {
        Member member = getMemberById(id);
        member.setFitnessGoals(fitnessGoals);
        return memberRepository.save(member);
    }

    public boolean isMembershipActive(Long id) {
        Member member = getMemberById(id);
        return member.isActive();
    }

    public List<Member> searchMembers(String name) {
        return memberRepository.findByUser_FirstNameContainingOrUser_LastNameContaining(name, name);
    }

    public List<Member> getMembersByType(String type) {
        return memberRepository.findByMembershipType(MembershipType.valueOf(type));
    }

public void updateplan(Long memberId, String membershipType) {
    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found with ID: " + memberId));
    // Validate membership type
    member.setMembershipType(MembershipType.valueOf(membershipType.toUpperCase()));
    memberRepository.save(member);
}
}