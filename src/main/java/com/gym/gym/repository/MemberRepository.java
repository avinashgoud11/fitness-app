package com.gym.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gym.gym.model.Member;
import com.gym.gym.model.MembershipType;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByActiveTrue();
    List<Member> findByMembershipTypeAndActiveTrue(String membershipType);
    Optional<Member> findByUser_Email(String email);
    List<Member> findByUser_FirstNameContainingOrUser_LastNameContaining(String firstName, String lastName);
    List<Member> findByMembershipType(MembershipType membershipType);
} 