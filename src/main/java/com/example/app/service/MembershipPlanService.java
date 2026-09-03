package com.example.app.service;

import com.example.app.dto.membership.MembershipPlanRequest;
import com.example.app.dto.membership.MembershipPlanResponse;
import com.example.app.entity.MembershipPlan;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.MembershipPlanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MembershipPlanService {

  private final MembershipPlanRepository membershipPlanRepository;

  public MembershipPlanService(MembershipPlanRepository membershipPlanRepository) {
    this.membershipPlanRepository = membershipPlanRepository;
  }

  @Transactional
  public MembershipPlanResponse create(MembershipPlanRequest request) {
    MembershipPlan plan =
        MembershipPlan.builder()
            .name(request.getName())
            .type(request.getType())
            .durationDays(request.getDurationDays())
            .price(request.getPrice())
            .description(request.getDescription())
            .active(request.getActive() != null ? request.getActive() : Boolean.TRUE)
            .build();
    return toResponse(membershipPlanRepository.save(plan));
  }

  public Page<MembershipPlanResponse> list(Pageable pageable) {
    return membershipPlanRepository.findAll(pageable).map(this::toResponse);
  }

  public MembershipPlanResponse get(Long id) {
    return toResponse(findOrThrow(id));
  }

  @Transactional
  public MembershipPlanResponse update(Long id, MembershipPlanRequest request) {
    MembershipPlan plan = findOrThrow(id);
    plan.setName(request.getName());
    plan.setType(request.getType());
    plan.setDurationDays(request.getDurationDays());
    plan.setPrice(request.getPrice());
    plan.setDescription(request.getDescription());
    if (request.getActive() != null) {
      plan.setActive(request.getActive());
    }
    return toResponse(membershipPlanRepository.save(plan));
  }

  @Transactional
  public void delete(Long id) {
    membershipPlanRepository.delete(findOrThrow(id));
  }

  public MembershipPlan findOrThrow(Long id) {
    return membershipPlanRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found: " + id));
  }

  private MembershipPlanResponse toResponse(MembershipPlan plan) {
    return MembershipPlanResponse.builder()
        .id(plan.getId())
        .name(plan.getName())
        .type(plan.getType())
        .durationDays(plan.getDurationDays())
        .price(plan.getPrice())
        .description(plan.getDescription())
        .active(plan.getActive())
        .build();
  }
}
