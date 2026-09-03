package com.example.app.dto.apikey;

import com.example.app.security.ApiRole;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateApiKeyRequest {

  @NotBlank(message = "name is required")
  private String name;

  /**
   * Optional. Defaults to SUPER_ADMIN when omitted so that the very first key minted for a fresh
   * environment has full access to bootstrap the system.
   */
  private ApiRole role;

  private Instant expiresAt;
}
