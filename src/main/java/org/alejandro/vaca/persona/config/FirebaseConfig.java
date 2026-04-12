package org.alejandro.vaca.persona.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    // Spring buscará la variable FIREBASE_CREDENTIALS, si no existe, usará "null"
    @Value("${FIREBASE_CREDENTIALS:null}")
    private String firebaseJson;

    @Bean
    public Firestore firestore() {
        try {
            InputStream serviceAccount;

            if (firebaseJson != null && !firebaseJson.equals("null") && !firebaseJson.isBlank()) {
                System.out.println("✅ LOG: Iniciando con VARIABLE DE ENTORNO");
                serviceAccount = new ByteArrayInputStream(firebaseJson.getBytes(StandardCharsets.UTF_8));
            }else {
                System.out.println("⚠️ Variable no encontrada. Intentando cargar Secret File externo...");
                // Render pone los Secret Files en la raíz del contenedor
                java.io.File file = new java.io.File("serviceAccountKey.json");
                if (file.exists()) {
                    serviceAccount = new java.io.FileInputStream(file);
                } else {
                    // Si no es un Secret File, intenta buscarlo como recurso interno (IDE)
                    serviceAccount = new ClassPathResource("serviceAccountKey.json").getInputStream();
                }
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