package com.example.app.repository;

import com.example.app.entity.Member;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberRepository extends MongoRepository<Member, String> {

  Optional<Member> findByEmail(String email);
}
