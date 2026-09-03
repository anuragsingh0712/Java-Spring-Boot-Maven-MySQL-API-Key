package com.example.app.repository;

import com.example.app.entity.Membership;
import com.example.app.entity.MembershipStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MembershipRepository extends MongoRepository<Membership, String> {

  List<Membership> findByMemberIdAndStatusIn(String memberId, List<MembershipStatus> statuses);

  Page<Membership> findByMemberId(String memberId, Pageable pageable);
}
