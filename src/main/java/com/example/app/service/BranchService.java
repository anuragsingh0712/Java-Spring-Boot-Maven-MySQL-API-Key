package com.example.app.service;

import com.example.app.dto.branch.BranchRequest;
import com.example.app.dto.branch.BranchResponse;
import com.example.app.entity.Branch;
import com.example.app.entity.Gym;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.BranchRepository;
import com.example.app.repository.GymRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BranchService {

  private final BranchRepository branchRepository;
  private final GymRepository gymRepository;

  public BranchService(BranchRepository branchRepository, GymRepository gymRepository) {
    this.branchRepository = branchRepository;
    this.gymRepository = gymRepository;
  }

  public BranchResponse create(BranchRequest request) {
    Gym gym =
        gymRepository
            .findById(request.getGymId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Gym not found: " + request.getGymId()));
    Branch branch =
        Branch.builder()
            .gymId(gym.getId())
            .name(request.getName())
            .address(request.getAddress())
            .city(request.getCity())
            .state(request.getState())
            .country(request.getCountry())
            .openingTime(request.getOpeningTime())
            .closingTime(request.getClosingTime())
            .capacity(request.getCapacity())
            .facilities(request.getFacilities())
            .managerName(request.getManagerName())
            .managerContact(request.getManagerContact())
            .status(request.getStatus())
            .build();
    return toResponse(branchRepository.save(branch));
  }

  public Page<BranchResponse> list(Pageable pageable) {
    return branchRepository.findAll(pageable).map(this::toResponse);
  }

  public BranchResponse get(String id) {
    return toResponse(findOrThrow(id));
  }

  public BranchResponse update(String id, BranchRequest request) {
    Branch branch = findOrThrow(id);
    Gym gym =
        gymRepository
            .findById(request.getGymId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Gym not found: " + request.getGymId()));
    branch.setGymId(gym.getId());
    branch.setName(request.getName());
    branch.setAddress(request.getAddress());
    branch.setCity(request.getCity());
    branch.setState(request.getState());
    branch.setCountry(request.getCountry());
    branch.setOpeningTime(request.getOpeningTime());
    branch.setClosingTime(request.getClosingTime());
    branch.setCapacity(request.getCapacity());
    branch.setFacilities(request.getFacilities());
    branch.setManagerName(request.getManagerName());
    branch.setManagerContact(request.getManagerContact());
    branch.setStatus(request.getStatus());
    return toResponse(branchRepository.save(branch));
  }

  public void delete(String id) {
    branchRepository.delete(findOrThrow(id));
  }

  public Branch findOrThrow(String id) {
    return branchRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + id));
  }

  private BranchResponse toResponse(Branch branch) {
    String gymName =
        gymRepository.findById(branch.getGymId()).map(Gym::getName).orElse(null);
    return BranchResponse.builder()
        .id(branch.getId())
        .gymId(branch.getGymId())
        .gymName(gymName)
        .name(branch.getName())
        .address(branch.getAddress())
        .city(branch.getCity())
        .state(branch.getState())
        .country(branch.getCountry())
        .openingTime(branch.getOpeningTime())
        .closingTime(branch.getClosingTime())
        .capacity(branch.getCapacity())
        .facilities(branch.getFacilities())
        .managerName(branch.getManagerName())
        .managerContact(branch.getManagerContact())
        .status(branch.getStatus())
        .build();
  }
}
