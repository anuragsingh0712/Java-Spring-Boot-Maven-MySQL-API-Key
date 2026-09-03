package com.example.app.service;

import com.example.app.dto.member.MemberRequest;
import com.example.app.dto.member.MemberResponse;
import com.example.app.entity.Branch;
import com.example.app.entity.Member;
import com.example.app.entity.MemberStatus;
import com.example.app.exception.BusinessRuleException;
import com.example.app.exception.ConflictException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.BranchRepository;
import com.example.app.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;
  private final BranchRepository branchRepository;

  public MemberService(MemberRepository memberRepository, BranchRepository branchRepository) {
    this.memberRepository = memberRepository;
    this.branchRepository = branchRepository;
  }

  @Transactional
  public MemberResponse create(MemberRequest request) {
    memberRepository
        .findByEmail(request.getEmail())
        .ifPresent(
            m -> {
              throw new ConflictException(
                  "A member with this email already exists: " + request.getEmail());
            });
    Branch branch =
        branchRepository
            .findById(request.getBranchId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Branch not found: " + request.getBranchId()));
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
            .branch(branch)
            .status(request.getStatus())
            .build();
    return toResponse(memberRepository.save(member));
  }

  public Page<MemberResponse> list(Pageable pageable) {
    return memberRepository.findAll(pageable).map(this::toResponse);
  }

  public MemberResponse get(Long id) {
    return toResponse(findOrThrow(id));
  }

  @Transactional
  public MemberResponse update(Long id, MemberRequest request) {
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
    Branch branch =
        branchRepository
            .findById(request.getBranchId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Branch not found: " + request.getBranchId()));
    member.setFirstName(request.getFirstName());
    member.setLastName(request.getLastName());
    member.setEmail(request.getEmail());
    member.setPhone(request.getPhone());
    member.setDateOfBirth(request.getDateOfBirth());
    member.setAddress(request.getAddress());
    member.setEmergencyContactName(request.getEmergencyContactName());
    member.setEmergencyContactPhone(request.getEmergencyContactPhone());
    member.setBranch(branch);
    member.setStatus(request.getStatus());
    return toResponse(memberRepository.save(member));
  }

  @Transactional
  public void delete(Long id) {
    memberRepository.delete(findOrThrow(id));
  }

  public Member findOrThrow(Long id) {
    return memberRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
  }

  /**
   * Members that are BLOCKED can never access gym services. Members that are SUSPENDED or EXPIRED
   * cannot use services that require an active membership.
   */
  public Member getServiceEligibleMember(Long id) {
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
        .branchId(member.getBranch().getId())
        .branchName(member.getBranch().getName())
        .status(member.getStatus())
        .build();
  }
}
