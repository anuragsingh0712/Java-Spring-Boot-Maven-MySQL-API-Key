package com.example.app.security;

import com.example.app.dto.apikey.ApiKeyResponse;
import com.example.app.dto.apikey.CreateApiKeyRequest;
import com.example.app.exception.ResourceNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

  private final ApiKeyRepository apiKeyRepository;
  private final SecureRandom secureRandom = new SecureRandom();

  public ApiKeyService(ApiKeyRepository apiKeyRepository) {
    this.apiKeyRepository = apiKeyRepository;
  }

  public ApiKeyResponse create(CreateApiKeyRequest request) {
    String rawKey = generateRawKey();
    String hash = sha256Hex(rawKey);

    ApiKey apiKey =
        ApiKey.builder()
            .keyHash(hash)
            .name(request.getName())
            .role(request.getRole() != null ? request.getRole() : ApiRole.SUPER_ADMIN)
            .status(ApiKeyStatus.ACTIVE)
            .createdAt(Instant.now())
            .expiresAt(request.getExpiresAt())
            .build();

    ApiKey saved = apiKeyRepository.save(apiKey);
    ApiKeyResponse response = toResponse(saved);
    response.setApiKey(rawKey);
    return response;
  }

  public List<ApiKeyResponse> list() {
    return apiKeyRepository.findAll().stream().map(this::toResponse).toList();
  }

  public ApiKeyResponse revoke(Long id) {
    ApiKey apiKey =
        apiKeyRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("API key not found: " + id));
    apiKey.setStatus(ApiKeyStatus.REVOKED);
    return toResponse(apiKeyRepository.save(apiKey));
  }

  private String generateRawKey() {
    byte[] bytes = new byte[32];
    secureRandom.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private String sha256Hex(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : hashBytes) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 not available", e);
    }
  }

  private ApiKeyResponse toResponse(ApiKey apiKey) {
    return ApiKeyResponse.builder()
        .id(apiKey.getId())
        .name(apiKey.getName())
        .role(apiKey.getRole())
        .status(apiKey.getStatus())
        .createdAt(apiKey.getCreatedAt())
        .expiresAt(apiKey.getExpiresAt())
        .lastUsedAt(apiKey.getLastUsedAt())
        .build();
  }
}
