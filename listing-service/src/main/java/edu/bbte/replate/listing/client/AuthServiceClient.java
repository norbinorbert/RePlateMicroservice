package edu.bbte.replate.listing.client;

import edu.bbte.replate.shared.dto.internal.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
}

