package com.example.p24zip.global.config;


import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class GoogleCredentialsConfig {

    /**
     * Firebase Admin SDK(json file)의 비공개 키를 참조하여 Firebase와의 Authentication & Authorization
     **/
    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        return GoogleCredentials
            .fromStream(
                new ClassPathResource("firebase/firebase_service_key.json").getInputStream())
            .createScoped(
                Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
    }

}
