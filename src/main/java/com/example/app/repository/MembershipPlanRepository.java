package com.example.app.repository;

import com.example.app.entity.MembershipPlan;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MembershipPlanRepository extends MongoRepository<MembershipPlan, String> {}
