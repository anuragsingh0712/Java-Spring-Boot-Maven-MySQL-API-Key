package com.example.app.service;

import com.example.app.dto.member.MemberRequest;
import com.example.app.dto.member.MemberResponse;
import com.example.app.entity.Branch;
import com.example.app.entity.Member;
import com.example.app.entity.MemberStatus;
import com.example.app.exception.BusinessRuleException;
import com.example.app.exception.ConflictException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final BranchService branchService;

  public MemberService(MemberRepository memberRepository, BranchService branchService) {
    this.memberRepository = memberRepository;
    this.branchService = branchService;
  }

  public MemberResponse create(MemberRequest request) {
    memberRepository
        .findByEmail(request.getEmail())
        .ifPresent(
            m -> {
              throw new ConflictException(
                  "A member with this email already exists: " + request.getEmail());
            });
    Branch branch = branchService.findOrThrow(request.getBranchId());
    Member member =
        Member.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .dateOfBirth(request.getDateOfBirth())
            .address(request.getAddress())
            .emergencyContactName(request.getEmergencyContactName())
            .emergencyContactPhone(request.getEmergencyContactPhone())
            .branchId(branch.getId())
            .status(request.getStatus())
            .build();
    return toResponse(memberRepository.save(member));
  }

  public Page<MemberResponse> list(Pageable pageable) {
    return memberRepository.findAll(pageable).map(this::toResponse);
  }

  public MemberResponse get(String id) {
    return toResponse(findOrThrow(id));
  }

  public MemberResponse update(String id, MemberRequest request) {
    Member member = findOrThrow(id);
    memberRepository
        .findByEmail(request.getEmail())
        .ifPresent(
            existing -> {
              if (!existing.getId().equals(id)) {
                throw new ConflictException(
                    "A member with this email already exists: " + request.getEmail());
              }
            });
    Branch branch = branchService.findOrThrow(request.getBranchId());
    member.setFirstName(request.getFirstName());
    member.setLastName(request.getLastName());
    member.setEmail(request.getEmail());
    member.setPhone(request.getPhone());
    member.setDateOfBirth(request.getDateOfBirth());
    member.setAddress(request.getAddress());
    member.setEmergencyContactName(request.getEmergencyContactName());
    member.setEmergencyContactPhone(request.getEmergencyContactPhone());
    member.setBranchId(branch.getId());
    member.setStatus(request.getStatus());
    return toResponse(memberRepository.save(member));
  }

  public void delete(String id) {
    memberRepository.delete(findOrThrow(id));
  }

  public Member findOrThrow(String id) {
    return memberRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
  }

  /**
   * Members that are BLOCKED can never access gym services. Members that are SUSPENDED or EXPIRED
   * cannot use services that require an active membership.
   */
  public Member getServiceEligibleMember(String id) {
    Member member = findOrThrow(id);
    if (member.getStatus() == MemberStatus.BLOCKED) {
      throw new BusinessRuleException("Member is blocked and cannot access gym services: " + id);
    }
    if (member.getStatus() == MemberStatus.SUSPENDED
        || member.getStatus() == MemberStatus.EXPIRED
        || member.getStatus() == MemberStatus.INACTIVE) {
      throw new BusinessRuleException(
          "Member status "
              + member.getStatus()
              + " cannot use services requiring an active membership: "
              + id);
    }
    return member;
  }

  private MemberResponse toResponse(Member member) {
    String branchName = branchService.findOrThrow(member.getBranchId()).getName();
    return MemberResponse.builder()
        .id(member.getId())
        .firstName(member.getFirstName())
        .lastName(member.getLastName())
        .email(member.getEmail())
        .phone(member.getPhone())
        .dateOfBirth(member.getDateOfBirth())
        .address(member.getAddress())
        .emergencyContactName(member.getEmergencyContactName())
        .emergencyContactPhone(member.getEmergencyContactPhone())
        .branchId(member.getBranchId())
        .branchName(branchName)
        .status(member.getStatus())
        .build();
  }
}
