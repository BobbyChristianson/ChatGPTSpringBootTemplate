package sandbox.SpringBootTemplateChatGPT.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    private static final String API_KEY = System.getenv("OPENAI_KEY"); // Retrieve API key from environment variable

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);
        return headers;
    }
}
