package org.alejandro.vaca.persona.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore firestore() {
        try {
            InputStream serviceAccount;
            String firebaseEnv = System.getenv("FIREBASE_CREDENTIALS");

            if (firebaseEnv != null && !firebaseEnv.isBlank()) {
                System.out.println("✅ LOG: Decodificando credenciales Base64 desde Variable de Entorno...");
                // Magia: Convertimos el Base64 de vuelta a un archivo en memoria
                byte[] decodedBytes = Base64.getDecoder().decode(firebaseEnv.trim());
                serviceAccount = new ByteArrayInputStream(decodedBytes);
            } else {
                System.out.println("⚠️ LOG: Variable vacía. Usando archivo local (para pruebas en el IDE)...");
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
            System.err.println("¡¡ERROR CRÍTICO EN FIREBASE!!: " + e.getMessage());
            throw new IllegalStateException("Falló la configuración de Firebase", e);
        }
    }
}