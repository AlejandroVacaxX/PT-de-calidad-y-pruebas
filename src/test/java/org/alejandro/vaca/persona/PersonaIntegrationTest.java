package org.alejandro.vaca.persona;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
                } catch (InterruptedException | ExecutionException e) {
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

                mockMvc.perform(get(BASE_URL)
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

        @Test
        @DisplayName("Devuelve error 400 cuando el ID buscado no existe")
        void obtenerPersonaPorIdInexistente() throws Exception {
                String idInexistente = "no-existe-123";

                DocumentSnapshot doc = Mockito.mock(DocumentSnapshot.class);
                when(doc.exists()).thenReturn(false);
                when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(doc));

                mockMvc.perform(get(BASE_URL + "/id/{id}", idInexistente))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("El Id " + idInexistente + " No Existe"));
        }

        @Test
        @DisplayName("Actualiza una persona localizándola por su CURP")
        void actualizarPersonaPorCurpTest() throws Exception {
                String curp = "PIVE020712HDFRRR01";
                String idInterno = "id-doc-456";
                String jsonUpdate = """
                                {
                                    "nombre": "Alejandro",
                                    "apellidoPaterno": "Pintor",
                                    "apellidoMaterno": "Vaca",
                                    "fechaDeNacimiento": "12/07/2002",
                                    "genero": "masculino",
                                    "estatusMigratorio": "mexicano",
                                    "estatura": 1.80,
                                    "peso": 75.0,
                                    "telefono": "5512345678",
                                    "email": "actualizado@gmail.com"
                                }
                                """;

                QuerySnapshot qs = Mockito.mock(QuerySnapshot.class);
                QueryDocumentSnapshot qds = Mockito.mock(QueryDocumentSnapshot.class);
                when(qs.getDocuments()).thenReturn(List.of(qds));
                when(qds.getId()).thenReturn(idInterno);
                when(query.get()).thenReturn(ApiFutures.immediateFuture(qs));

                DocumentSnapshot doc = Mockito.mock(DocumentSnapshot.class);
                when(doc.exists()).thenReturn(true);
                when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(doc));

                com.google.cloud.firestore.WriteResult wr = Mockito.mock(com.google.cloud.firestore.WriteResult.class);
                when(documentReference.set(Mockito.anyMap())).thenReturn(ApiFutures.immediateFuture(wr));

                mockMvc.perform(put(BASE_URL + "/curp/{curp}", curp)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(jsonUpdate))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(idInterno))
                                .andExpect(jsonPath("$.email").value("actualizado@gmail.com"));
        }


        @Test
        @DisplayName("Registra una nueva persona exitosamente")
        void registrarPersonaTest() throws Exception {
                String personaJson = """
                                {
                                    "nombre": "Alejandro",
                                    "apellidoPaterno": "Pintor",
                                    "apellidoMaterno": "Vaca",
                                    "fechaDeNacimiento": "12/07/2002",
                                    "genero": "masculino",
                                    "estatusMigratorio": "mexicano",
                                    "estatura": 1.80,
                                    "peso": 73.1,
                                    "telefono": "5512345678",
                                    "email": "alejandro@gmail.com"
                                }
                                """;

                com.google.cloud.firestore.WriteResult writeResult = Mockito
                                .mock(com.google.cloud.firestore.WriteResult.class);
                when(documentReference.set(Mockito.anyMap())).thenReturn(ApiFutures.immediateFuture(writeResult));
                when(documentReference.getId()).thenReturn("nuevo-id-123");

                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(BASE_URL)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(personaJson))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("nuevo-id-123"))
                                .andExpect(jsonPath("$.nombre").value("alejandro"))
                                .andExpect(jsonPath("$.imc").value(22.56));
        }

        @Test
        @DisplayName("Obtiene una persona mediante su RFC")
        void obtenerPersonaPorRfc() throws Exception {
                String rfc = "PIVE0120712";

                QuerySnapshot qs = Mockito.mock(QuerySnapshot.class);
                QueryDocumentSnapshot qds = Mockito.mock(QueryDocumentSnapshot.class);

                when(qs.getDocuments()).thenReturn(List.of(qds));
                when(qds.getId()).thenReturn("ID-123");
                when(qds.getString("nombre")).thenReturn("alejandro");
                when(qds.getString("rfc")).thenReturn(rfc);

                when(query.get()).thenReturn(ApiFutures.immediateFuture(qs));

                mockMvc.perform(get(BASE_URL + "/rfc/{rfc}", rfc))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.rfc").value(rfc))
                                .andExpect(jsonPath("$.nombre").value("alejandro"));
        }

        @Test
        @DisplayName("Actualiza una persona localizandola por su RFC")
        void actualizarPersonaPorRfcTest() throws Exception {
                String rfc = "PIVE0120712";
                String idInterno = "id-doc-rfc-789";
                String jsonUpdate = """
                                {
                                    "nombre": "Alejandro",
                                    "apellidoPaterno": "Pintor",
                                    "apellidoMaterno": "Vaca",
                                    "fechaDeNacimiento": "12/07/2002",
                                    "genero": "masculino",
                                    "estatusMigratorio": "mexicano",
                                    "estatura": 1.80,
                                    "peso": 80.0,
                                    "telefono": "5512345678",
                                    "email": "nuevo@gmail.com"
                                }
                                """;

                QuerySnapshot qs = Mockito.mock(QuerySnapshot.class);
                QueryDocumentSnapshot qds = Mockito.mock(QueryDocumentSnapshot.class);
                when(qs.getDocuments()).thenReturn(List.of(qds));
                when(qds.getId()).thenReturn(idInterno);
                when(query.get()).thenReturn(ApiFutures.immediateFuture(qs));

                // Mock de existencia y guardado
                DocumentSnapshot doc = Mockito.mock(DocumentSnapshot.class);
                when(doc.exists()).thenReturn(true);
                when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(doc));
                com.google.cloud.firestore.WriteResult wr = Mockito.mock(com.google.cloud.firestore.WriteResult.class);
                when(documentReference.set(Mockito.anyMap())).thenReturn(ApiFutures.immediateFuture(wr));

                mockMvc.perform(put(BASE_URL + "/rfc/{rfc}", rfc)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(jsonUpdate))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("nuevo@gmail.com"));
        }

        @Test
        @DisplayName("Elimina una persona por su ID")
        void eliminarPersonaPorIdTest() throws Exception {
                String id = "ID-BORRAR";

                com.google.cloud.firestore.WriteResult wr = Mockito.mock(com.google.cloud.firestore.WriteResult.class);
                when(documentReference.delete()).thenReturn(ApiFutures.immediateFuture(wr));

                mockMvc.perform(delete(BASE_URL + "/id/{id}", id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").value(true));
        }

        @Test
        @DisplayName("Elimina una persona por su CURP")
        void eliminarPersonaPorCurpTest() throws Exception {
                String curp = "PIVE021712";
                String idADelete = "id-curp-999";

                QuerySnapshot qs = Mockito.mock(QuerySnapshot.class);
                QueryDocumentSnapshot qds = Mockito.mock(QueryDocumentSnapshot.class);
                when(qs.getDocuments()).thenReturn(List.of(qds));
                when(qds.getId()).thenReturn(idADelete);
                when(query.get()).thenReturn(ApiFutures.immediateFuture(qs));

                com.google.cloud.firestore.WriteResult wr = Mockito.mock(com.google.cloud.firestore.WriteResult.class);
                when(documentReference.delete()).thenReturn(ApiFutures.immediateFuture(wr));

                mockMvc.perform(delete(BASE_URL + "/curp/{curp}", curp))
                                .andExpect(status().isOk());
        }

        @ParameterizedTest
        @CsvSource({
                        "70.0, 1.75, 22.86",
                        "85.5, 1.80, 26.39",
                        "50.0, 1.60, 19.53"
        })
        @DisplayName("Integración: Registro y cálculo de IMC parametrizado")
        void registroParametrizadoTest(Double peso, Double estatura, Double imcEsperado) throws Exception {
                String personaJson = String.format(java.util.Locale.US,"""
                                {
                                    "nombre": "Prueba",
                                    "apellidoPaterno": "Test",
                                    "apellidoMaterno": "Unit",
                                    "fechaDeNacimiento": "10/10/1990",
                                    "genero": "masculino",
                                    "estatusMigratorio": "mexicano",
                                    "estatura": %.2f,
                                    "peso": %.2f,
                                    "telefono": "5511223344",
                                    "email": "test@uam.mx"
                                }
                                """, estatura, peso);

                com.google.cloud.firestore.WriteResult writeResult = Mockito
                                .mock(com.google.cloud.firestore.WriteResult.class);
                when(documentReference.set(Mockito.anyMap())).thenReturn(ApiFutures.immediateFuture(writeResult));
                when(documentReference.getId()).thenReturn("id-param");

                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(BASE_URL)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(personaJson))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.imc").value(imcEsperado));
        }

        @RepeatedTest(3)
        @DisplayName("Integracion: Generación repetida de CURP")
        void registroRepetidoCurpTest() throws Exception {
                String personaJson = """
                                {
                                    "nombre": "Edgar",
                                    "apellidoPaterno": "Vaca",
                                    "apellidoMaterno": "Alejandro",
                                    "fechaDeNacimiento": "15/05/1998",
                                    "genero": "masculino",
                                    "estatusMigratorio": "mexicano",
                                    "estatura": 1.75,
                                    "peso": 70.0,
                                    "telefono": "5566778899",
                                    "email": "edgar@uam.mx"
                                }
                                """;

                com.google.cloud.firestore.WriteResult writeResult = Mockito
                                .mock(com.google.cloud.firestore.WriteResult.class);
                when(documentReference.set(Mockito.anyMap())).thenReturn(ApiFutures.immediateFuture(writeResult));
                when(documentReference.getId()).thenReturn("id-repetido");

                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(BASE_URL)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(personaJson))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.curp").exists())
                                .andExpect(jsonPath("$.curp").value(org.hamcrest.Matchers.hasLength(16)));
        }

        @Test
        @DisplayName("Integracion: Error de validación Jakarta (Teléfono inválido)")
        void registroFallaTelefonoTest() throws Exception {
                String personaInvalida = """
                                {
                                    "nombre": "Error",
                                    "apellidoPaterno": "Test",
                                    "apellidoMaterno": "Malo",
                                    "fechaDeNacimiento": "01/01/2000",
                                    "genero": "masculino",
                                    "estatusMigratorio": "mexicano",
                                    "estatura": 1.70,
                                    "peso": 70.0,
                                    "telefono": "4412345678",
                                    "email": "malo@gmail.com"
                                }
                                """;

                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(BASE_URL)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(personaInvalida))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").exists());
        }

        

        @Test
        @DisplayName("Integracion: Error de validación en Género (No permitido)")
        void registroFallaGeneroTest() throws Exception {
                String personaInvalida = """
                                {
                                    "nombre": "Prueba",
                                    "apellidoPaterno": "Test",
                                    "apellidoMaterno": "Malo",
                                    "fechaDeNacimiento": "01/01/2000",
                                    "genero": "Desconocido",
                                    "estatusMigratorio": "mexicano",
                                    "estatura": 1.70,
                                    "peso": 70.0,
                                    "telefono": "5512345678",
                                    "email": "malo@gmail.com"
                                }
                                """;

                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(BASE_URL)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(personaInvalida))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Integracion: Error al registrar menor de 16 años (Regla RFC Punto V)")
        void registroFallaMenorEdadTest() throws Exception {
                String personaJovenJson = """
                                {
                                    "nombre": "Juanito",
                                    "apellidoPaterno": "Perez",
                                    "apellidoMaterno": "Lopez",
                                    "fechaDeNacimiento": "01/01/2020",
                                    "genero": "masculino",
                                    "estatusMigratorio": "mexicano",
                                    "estatura": 1.20,
                                    "peso": 30.0,
                                    "telefono": "5500000000",
                                    "email": "nino@gmail.com"
                                }
                                """;

                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(BASE_URL)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(personaJovenJson))
                                .andExpect(status().isInternalServerError());
        }

        @ParameterizedTest
        @CsvSource({
                        "JUAN, PEREZ, LOPEZ, 01/01/1990, PELJ900101",
                        "MARIA, GARCIA, RUIZ, 15/03/1985, GARM850315",
                        "ana, delgado, mora, 20/12/1995, DEMA951220"
        })
        @DisplayName("Integracion: Validación parametrizada de generación de RFC")
        void registroRfcParametrizadoTest(String nom, String pat, String mat, String fecha, String rfcEsperado)
                        throws Exception {
                String personaJson = String.format("""
                                {
                                    "nombre": "%s",
                                    "apellidoPaterno": "%s",
                                    "apellidoMaterno": "%s",
                                    "fechaDeNacimiento": "%s",
                                    "genero": "femenino",
                                    "estatusMigratorio": "mexicano",
                                    "estatura": 1.65,
                                    "peso": 60.0,
                                    "telefono": "5500000000",
                                    "email": "test.param@uam.mx"
                                }
                                """, nom, pat, mat, fecha);

                com.google.cloud.firestore.WriteResult writeResult = Mockito
                                .mock(com.google.cloud.firestore.WriteResult.class);
                when(documentReference.set(Mockito.anyMap())).thenReturn(ApiFutures.immediateFuture(writeResult));
                when(documentReference.getId()).thenReturn("id-rfc-param");

                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(BASE_URL)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(personaJson))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.rfc").value(rfcEsperado));
        }

        @RepeatedTest(5)
        @DisplayName("Integracion: Verificacion de estabilidad en datos aleatorios de CURP")
        void registroCurpEstabilidadTest() throws Exception {
                String personaJson = """
                                {
                                    "nombre": "Edgar",
                                    "apellidoPaterno": "Vaca",
                                    "apellidoMaterno": "Alejandro",
                                    "fechaDeNacimiento": "10/10/2000",
                                    "genero": "masculino",
                                    "estatusMigratorio": "mexicano",
                                    "estatura": 1.75,
                                    "peso": 80.0,
                                    "telefono": "5511223344",
                                    "email": "edgar.test@uam.mx"
                                }
                                """;

                com.google.cloud.firestore.WriteResult writeResult = Mockito
                                .mock(com.google.cloud.firestore.WriteResult.class);
                when(documentReference.set(Mockito.anyMap())).thenReturn(ApiFutures.immediateFuture(writeResult));
                when(documentReference.getId()).thenReturn("id-repetido-curp");

                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(BASE_URL)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(personaJson))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.curp").exists())
                                .andExpect(jsonPath("$.curp").value(org.hamcrest.Matchers.hasLength(16)));
        }

        @ParameterizedTest
        @CsvSource({
                        "55123, test@gmail.com",
                        "4412345678, test@gmail.com",
                        "5512345678, correo-invalido",
                        "A123456789, test@gmail.com"
        })
        @DisplayName("Integración: Validación parametrizada de datos de contacto inválidos")
        void validacionContactoParametrizadaTest(String tel, String mail) throws Exception {
                String personaInvalida = String.format("""
                                {
                                    "nombre": "Falla",
                                    "apellidoPaterno": "Test",
                                    "apellidoMaterno": "Jakarta",
                                    "fechaDeNacimiento": "01/01/2000",
                                    "genero": "masculino",
                                    "estatusMigratorio": "mexicano",
                                    "estatura": 1.70,
                                    "peso": 70.0,
                                    "telefono": "%s",
                                    "email": "%s"
                                }
                                """, tel, mail);

                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(BASE_URL)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(personaInvalida))
                                .andExpect(status().isBadRequest());
        }

      


    
    @Test
    @DisplayName("Elimina una persona localizandola por su RFC")
    void eliminarPersonaPorRfcTest() throws Exception {
        String rfc = "PIVE020712";
        String idADelete = "id-rfc-999";

        QuerySnapshot qs = Mockito.mock(QuerySnapshot.class);
        QueryDocumentSnapshot qds = Mockito.mock(QueryDocumentSnapshot.class);
        when(qs.getDocuments()).thenReturn(List.of(qds));
        when(qds.getId()).thenReturn(idADelete);
        when(query.get()).thenReturn(ApiFutures.immediateFuture(qs));

        com.google.cloud.firestore.WriteResult wr = Mockito.mock(com.google.cloud.firestore.WriteResult.class);
        when(documentReference.delete()).thenReturn(ApiFutures.immediateFuture(wr));

        mockMvc.perform(delete(BASE_URL + "/rfc/{rfc}", rfc))
                .andExpect(status().isOk());
    }




    @Test
    @DisplayName("Integracion: Actualizar persona por RFC")
    void actualizarPorRfcTest() throws Exception {
        String rfc = "PIVA020712";
        String idInterno = "doc-id-789";
        String jsonUpdate = """
                {
                    "nombre": "Alejandro",
                    "apellidoPaterno": "Vaca",
                    "apellidoMaterno": "Pintor",
                    "fechaDeNacimiento": "12/07/2002",
                    "genero": "masculino",
                    "estatusMigratorio": "mexicano",
                    "estatura": 1.80,
                    "peso": 80.0,
                    "telefono": "5512345678",
                    "email": "nuevo@uam.mx"
                }
                """;

        QuerySnapshot qs = Mockito.mock(QuerySnapshot.class);
        QueryDocumentSnapshot qds = Mockito.mock(QueryDocumentSnapshot.class);
        when(qs.getDocuments()).thenReturn(List.of(qds));
        when(qds.getId()).thenReturn(idInterno);
        when(query.get()).thenReturn(ApiFutures.immediateFuture(qs));

        // Mock de guardado
        DocumentSnapshot doc = Mockito.mock(DocumentSnapshot.class);
        when(doc.exists()).thenReturn(true);
        when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(doc));
        com.google.cloud.firestore.WriteResult wr = Mockito.mock(com.google.cloud.firestore.WriteResult.class);
        when(documentReference.set(Mockito.anyMap())).thenReturn(ApiFutures.immediateFuture(wr));

        mockMvc.perform(put(BASE_URL + "/rfc/{rfc}", rfc)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(jsonUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("nuevo@uam.mx"));
    }


    @RepeatedTest(10)
    @DisplayName("Integración: Validación de formato Regex en dígitos aleatorios de CURP")
    void validacionRegexCurpAleatoriaTest() throws Exception {
        String personaJson = """
                {
                    "nombre": "Alejandro",
                    "apellidoPaterno": "Pintor",
                    "apellidoMaterno": "Vaca",
                    "fechaDeNacimiento": "15/05/1998",
                    "genero": "masculino",
                    "estatusMigratorio": "mexicano",
                    "estatura": 1.75,
                    "peso": 70.0,
                    "telefono": "5566778899",
                    "email": "edgar@uam.mx"
                }
                """;

        com.google.cloud.firestore.WriteResult writeResult = Mockito.mock(com.google.cloud.firestore.WriteResult.class);
        when(documentReference.set(Mockito.anyMap())).thenReturn(ApiFutures.immediateFuture(writeResult));
        when(documentReference.getId()).thenReturn("id-curp-regex");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(BASE_URL)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(personaJson))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.curp").value(org.hamcrest.Matchers.matchesPattern("^[A-Z]{4}\\d{6}[A-Z0-9]{6}$")));
    }
}
