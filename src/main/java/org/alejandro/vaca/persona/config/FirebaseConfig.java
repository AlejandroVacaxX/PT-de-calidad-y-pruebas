

package org.alejandro.vaca.persona.config;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore firestore() {
        try {
            InputStream serviceAccount;
            String firebaseEnv = System.getenv("FIREBASE_CREDENTIALS");

            if (firebaseEnv != null && !firebaseEnv.isBlank()) {
                System.out.println("Cargando Firebase desde Variable de Entorno...");
                serviceAccount = new ByteArrayInputStream(firebaseEnv.getBytes(StandardCharsets.UTF_8));
            } else {
            
                System.out.println("Cargando Firebase desde archivo local...");
                serviceAccount = new ClassPathResource("serviceAccountKey.json").getInputStream();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            return FirestoreClient.getFirestore();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Falló la configuración de Firebase", e);
        }
    }
}