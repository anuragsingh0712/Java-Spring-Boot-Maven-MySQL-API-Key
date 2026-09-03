package com.example.app.security;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "api_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKey {

  @Id
  private String id;

  @Indexed(unique = true)
  private String keyHash;

  private String name;

  private ApiRole role;

  private ApiKeyStatus status;

  private Instant createdAt;

  private Instant expiresAt;

  private Instant lastUsedAt;
}
