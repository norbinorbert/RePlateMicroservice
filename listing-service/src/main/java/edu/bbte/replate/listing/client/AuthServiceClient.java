package edu.bbte.replate.listing.client;

import edu.bbte.replate.shared.dto.internal.TokenValidationResponseDto;
import edu.bbte.replate.shared.dto.internal.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AuthServiceClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${auth.service.url:http://auth-service}")
    private String authServiceUrl;

    public Long getUserIdByUsername(String username) {
        log.info("Fetching user id for username: {}", username);
        try {
            String url = authServiceUrl + "/auth/user/" + username;
            UserInfoDto response = restTemplate.getForObject(url, UserInfoDto.class);
            return response != null ? response.id() : null;
        } catch (Exception e) {
            log.error("Failed to fetch user with username: {}", username, e);
            return null;
        }
    }

    public TokenValidationResponseDto validateToken(String token) {
        log.info("Validating JWT token with auth service.");
        try {
            String url = authServiceUrl + "/auth/validate-token";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<TokenValidationResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    TokenValidationResponseDto.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Token validation response received.");
                return response.getBody();
            } else {
                log.warn("Failed to validate token, received status: {}", response.getStatusCode());
                return new TokenValidationResponseDto(null, null, null, false);
            }
        } catch (Exception e) {
            log.error("Failed to validate token with auth service: {}", e.getMessage(), e);
            return new TokenValidationResponseDto(null, null, null, false);
        }
    }
}

