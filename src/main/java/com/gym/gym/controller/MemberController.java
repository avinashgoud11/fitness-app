package com.gym.gym.controller;
import com.gym.gym.model.Member;
import com.gym.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController{

    @Autowired
    private MemberService memberService;

    /**
     * Register a new member
     * @param member Member details
     * @return Created member
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerMember(@RequestBody Member member, BindingResult result) {
        if(result.hasErrors()){
            return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
        }
        try {
            Member registeredMember = memberService.registerMember(member);
            return ResponseEntity.ok(registeredMember);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get all members
     * @return List of all members
     */
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    /**
     * Get member by ID
     * @param id Member ID
     * @return Member if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        try {
            Member member = memberService.getMemberById(id);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
@GetMapping("/by-email") // This maps to GET /api/members/by-email?email=...
    @PreAuthorize("hasRole('ADMIN')") // It's generally a good security practice to restrict email searches to admins
    public ResponseEntity<Member> getMemberByEmail(@RequestParam String email) {
        Member member = memberService.getMemberByEmail(email); // Call your service method
        return ResponseEntity.ok(member);
    }
    /**
     * Update member details
     * @param id Member ID
     * @param updatedMember Updated member details
     * @return Updated member
     */
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(
            @PathVariable Long id,
            @RequestBody Member updatedMember) {
        try {
            Member member = memberService.updateMember(id, updatedMember);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update member's membership type
     * @param id Member ID
     * @param membershipType New membership type
     * @return Updated member
     */
    @PutMapping("/{id}/{membershipType}")
    public ResponseEntity<?> updateMembershipType(
            @PathVariable Long id,
            @PathVariable String membershipType) {
        try {
        memberService.updatePlan(id, membershipType);
            return ResponseEntity.ok(membershipType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Faile to update membership type: " + e.getMessage());
        }
    }

    /**
     * Deactivate member
     * @param id Member ID
     * @return Deactivated member
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Member> deactivateMember(@PathVariable Long id) {
        try {
            Member member = memberService.deactivateMember(id);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Reactivate member
     * @param id Member ID
     * @return Reactivated member
     */
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<Member> reactivateMember(@PathVariable Long id) {
        try {
            Member member = memberService.reactivateMember(id);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update member's medical conditions
     * @param id Member ID
     * @param medicalConditions New medical conditions
     * @return Updated member
     */
    @PutMapping("/{id}/medical-conditions")
    public ResponseEntity<Member> updateMedicalConditions(
            @PathVariable Long id,
            @RequestBody String medicalConditions) {
        try {
            Member member = memberService.updateMedicalConditions(id, medicalConditions);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update member's fitness goals
     * @param id Member ID
     * @param fitnessGoals New fitness goals
     * @return Updated member
     */
    @PutMapping("/{id}/fitness-goals")
    public ResponseEntity<Member> updateFitnessGoals(
            @PathVariable Long id,
            @RequestBody String fitnessGoals) {
        try {
            Member member = memberService.updateFitnessGoals(id, fitnessGoals);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get member's membership status
     * @param id Member ID
     * @return Membership status
     */
    @GetMapping("/{id}/membership-status")
    public ResponseEntity<Boolean> getMembershipStatus(@PathVariable Long id) {
        try {
            boolean isActive = memberService.isMembershipActive(id);
            return ResponseEntity.ok(isActive);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Search members by name
     * @param name Name to search for
     * @return List of matching members
     */
    @GetMapping("/search")
    public ResponseEntity<List<Member>> searchMembers(@RequestParam String name) {
        List<Member> members = memberService.searchMembers(name);
        return ResponseEntity.ok(members);
    }

    /**
     * Get members by membership type
     * @param type Membership type
     * @return List of members with specified type
     */
    @GetMapping("/by-type")
    public ResponseEntity<List<Member>> getMembersByType(@RequestParam String type) {
        List<Member> members = memberService.getMembersByType(type);
        return ResponseEntity.ok(members);
    }
} 
