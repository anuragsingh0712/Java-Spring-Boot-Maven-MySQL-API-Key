package com.example.app.repository;

import com.example.app.entity.Membership;
import com.example.app.entity.MembershipStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

  List<Membership> findByMemberIdAndStatusIn(Long memberId, List<MembershipStatus> statuses);

  Page<Membership> findByMemberId(Long memberId, Pageable pageable);
}
