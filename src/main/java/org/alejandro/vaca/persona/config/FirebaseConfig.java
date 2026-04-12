package org.alejandro.vaca.persona.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore firestore() {
        try {
            InputStream serviceAccount;
            
            // RUTA ABSOLUTA DE RENDER PARA DOCKER
            File renderSecret = new File("/etc/secrets/serviceAccountKey.json");

            if (renderSecret.exists()) {
                System.out.println("✅ LOG: ¡Bóveda de Render encontrada! Cargando credenciales...");
                serviceAccount = new FileInputStream(renderSecret);
            } else {
                System.out.println("⚠️ LOG: No se encontró /etc/secrets/. Usando archivo local (IDE)...");
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