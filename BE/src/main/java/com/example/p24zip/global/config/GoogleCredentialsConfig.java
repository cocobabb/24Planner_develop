package com.example.p24zip.global.config;


import com.google.auth.oauth2.GoogleCredentials;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleCredentialsConfig {

    /**
     * Firebase Admin SDK(json file)의 비공개 키를 참조하여 Firebase와의 Authentication & Authorization
     **/
    @Bean
    public GoogleCredentials googleCredentials(
        @Value("${FIREBASE_SERVICE_JSON}") String firebaseKeyBase64) throws IOException {
        byte[] decoded = Base64.getDecoder().decode(firebaseKeyBase64);
        try (InputStream in = new ByteArrayInputStream(decoded)) {
            return GoogleCredentials.fromStream(in)
                .createScoped(
                    Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
        }
    }

}
