package com.example.app.dto.apikey;

import com.example.app.security.ApiKeyStatus;
import com.example.app.security.ApiRole;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKeyResponse {

  private String id;
  private String name;
  private ApiRole role;
  private ApiKeyStatus status;
  private Instant createdAt;
  private Instant expiresAt;
  private Instant lastUsedAt;

  /** Only populated once, at creation time. Never returned by list/get endpoints. */
  private String apiKey;
}
