package com.example.app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

  private static final String HEADER_NAME = "X-API-Key";

  private final ApiKeyRepository apiKeyRepository;

  public ApiKeyFilter(ApiKeyRepository apiKeyRepository) {
    this.apiKeyRepository = apiKeyRepository;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String rawKey = request.getHeader(HEADER_NAME);

    if (rawKey != null && !rawKey.isBlank()) {
      try {
        String hash = sha256Hex(rawKey);
        Optional<ApiKey> apiKeyOpt = apiKeyRepository.findByKeyHash(hash);

        if (apiKeyOpt.isPresent()) {
          ApiKey apiKey = apiKeyOpt.get();
          boolean expired =
              apiKey.getExpiresAt() != null && apiKey.getExpiresAt().isBefore(Instant.now());

          if (expired && apiKey.getStatus() == ApiKeyStatus.ACTIVE) {
            apiKey.setStatus(ApiKeyStatus.EXPIRED);
            apiKeyRepository.save(apiKey);
          }

          if (apiKey.getStatus() == ApiKeyStatus.ACTIVE && !expired) {
            apiKey.setLastUsedAt(Instant.now());
            apiKeyRepository.save(apiKey);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    apiKey.getName(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + apiKey.getRole().name())));
            authentication.setDetails(apiKey.getId());
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        }
      } catch (NoSuchAlgorithmException e) {
        // fall through - no authentication set, request will be rejected downstream
      }
    }

    filterChain.doFilter(request, response);
  }

  private String sha256Hex(String value) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hashBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
    StringBuilder sb = new StringBuilder();
    for (byte b : hashBytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}
