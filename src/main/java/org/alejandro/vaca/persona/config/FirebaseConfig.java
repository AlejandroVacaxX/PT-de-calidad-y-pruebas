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
            } else {
                System.out.println("⚠️ Variable no encontrada. Buscando archivo físico...");
                
                // Lista de rutas posibles donde Render/Docker pueden guardar el archivo
                String[] posiblesRutas = {
                    "serviceAccountKey.json",                  // Raíz del contenedor
                    "/app/serviceAccountKey.json",             // Ruta estándar de Render
                    "/app/src/main/resources/serviceAccountKey.json",
                    "etc/secrets/serviceAccountKey.json"       // A veces Render los monta aquí
                };
            
                InputStream is = null;
                for (String ruta : posiblesRutas) {
                    java.io.File file = new java.io.File(ruta);
                    if (file.exists()) {
                        System.out.println("✅ ¡ARCHIVO ENCONTRADO EN: " + ruta + "!");
                        is = new java.io.FileInputStream(file);
                        break;
                    }
                }
            
                if (is == null) {
                    System.out.println("❌ No se encontró archivo físico. Intentando ClassPathResource...");
                    is = new ClassPathResource("serviceAccountKey.json").getInputStream();
                }
                serviceAccount = is;
            }

            return FirestoreClient.getFirestore();

        } catch (Exception e) {
            System.err.println("¡¡ERROR CRÍTICO EN FIREBASE!!: " + e.getMessage());
            throw new IllegalStateException("Falló la configuración de Firebase", e);
        }
    }
}