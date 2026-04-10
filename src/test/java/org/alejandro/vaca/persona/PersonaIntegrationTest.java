package org.alejandro.vaca.persona;

import java.util.Collections;
import java.util.List;

import org.alejandro.vaca.persona.model.PersonaModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PersonaIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Firestore firestore;

    private CollectionReference collectionReference;
    private DocumentReference documentReference;
    private ApiFuture<QuerySnapshot> querySnapshotFuture;
    private QuerySnapshot querySnapshot;
    private Query query;

    private static final String BASE_URL = "/personas/personas-api";

    @SuppressWarnings({ "unchecked" })
    @BeforeEach
    void setUp() {
        collectionReference = Mockito.mock(CollectionReference.class);
        documentReference = Mockito.mock(DocumentReference.class);
        querySnapshotFuture = Mockito.mock(ApiFuture.class);
        querySnapshot = Mockito.mock(QuerySnapshot.class);
        query = Mockito.mock(Query.class);

        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document()).thenReturn(documentReference);
        when(collectionReference.document(anyString())).thenReturn(documentReference);
        when(collectionReference.get()).thenReturn(querySnapshotFuture);
        
        // Mock chainable whereEqualTo
        when(collectionReference.whereEqualTo(anyString(), anyString())).thenReturn(query);
        when(query.whereEqualTo(anyString(), anyString())).thenReturn(query);
        when(query.get()).thenReturn(querySnapshotFuture);

        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());
        try {
            when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Obtiene una persona por su ID")
    void obtenerPersonaPorID() throws Exception {
        String id = "1212123";
        
        DocumentSnapshot doc = Mockito.mock(DocumentSnapshot.class);
        when(doc.exists()).thenReturn(true);
        when(doc.getId()).thenReturn(id);
        
        
        when(doc.getString("nombre")).thenReturn("alejandro");
        when(doc.getString("apellidoPaterno")).thenReturn("Pintor");
        when(doc.getString("apellidoMaterno")).thenReturn("Vaca");
        when(doc.getString("fechaDeNacimiento")).thenReturn("12/07/2002");
        when(doc.getString("genero")).thenReturn("masculino");
        when(doc.getString("estatusMigratorio")).thenReturn("mexicano");
        when(doc.getDouble("estatura")).thenReturn(1.80);
        when(doc.getDouble("peso")).thenReturn(73.1);
        when(doc.getString("telefono")).thenReturn("5512345678");
        when(doc.getString("email")).thenReturn("Alejandro@gmail.com");
        when(doc.getString("curp")).thenReturn("PIVE");
        when(doc.getString("rfc")).thenReturn("PIVE0120712");
        when(doc.getDouble("imc")).thenReturn(23.1);

        when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(doc));

        mockMvc.perform(get(BASE_URL + "/id/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("alejandro"))
                .andExpect(jsonPath("$.curp").value("PIVE"));
    }

    @Test
    @DisplayName("Metodo que despliega todas las personas que se tienen en la base de datos")
    void getPersonasPorNombreTest() throws Exception {
        String nombre = "alejandro";
        String apellidoP = "Pintor";
        String apellidoM = "Vaca";
		
        mockMvc.perform(get(BASE_URL + "/nombreTodos/{nombre}", nombre)
                .param("nombre", nombre)
                .param("apellidoP", apellidoP)
                .param("apellidoM", apellidoM))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Obtiene a una persona mediante su Curp")
    void obtenerPersonaPorCurp() throws Exception {
        String curp = "PIVE021712";
        
        QuerySnapshot qs = Mockito.mock(QuerySnapshot.class);
        QueryDocumentSnapshot qds = Mockito.mock(QueryDocumentSnapshot.class);
        
        when(qs.getDocuments()).thenReturn(List.of(qds));
        when(qds.getId()).thenReturn("12121");
        when(qds.getString("nombre")).thenReturn("alejandro");
        when(qds.getString("apellidoPaterno")).thenReturn("Pintor");
        when(qds.getString("apellidoMaterno")).thenReturn("Vaca");
        when(qds.getString("fechaDeNacimiento")).thenReturn("12/07/2002");
        when(qds.getString("genero")).thenReturn("masculino");
        when(qds.getString("estatusMigratorio")).thenReturn("mexicano");
        when(qds.getDouble("estatura")).thenReturn(1.80);
        when(qds.getDouble("peso")).thenReturn(73.1);
        when(qds.getString("telefono")).thenReturn("5512345678");
        when(qds.getString("email")).thenReturn("Alejandro@gmail.com");
        when(qds.getString("curp")).thenReturn(curp);
        when(qds.getString("rfc")).thenReturn("PIVE0120712");
        when(qds.getDouble("imc")).thenReturn(23.1);

        when(query.get()).thenReturn(ApiFutures.immediateFuture(qs));

        mockMvc.perform(get(BASE_URL + "/curp/{curp}", curp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.curp").value(curp))
                .andExpect(jsonPath("$.nombre").value("alejandro"));
    }
}
