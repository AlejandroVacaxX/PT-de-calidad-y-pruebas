package org.alejandro.vaca.persona.config;

import java.io.FileInputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            if (FirebaseApp.getApps().isEmpty()) {
                FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);

            }
            return FirestoreClient.getFirestore();

        } catch (Exception e) {
            // ¡Esto imprimirá la verdadera causa del problema en la consola!
            System.err.println("¡¡ERROR CRÍTICO AL CONECTAR CON FIREBASE!! -> " + e.getMessage());
            throw new IllegalStateException("Falló la configuración de Firebase", e);
        }
    }
}