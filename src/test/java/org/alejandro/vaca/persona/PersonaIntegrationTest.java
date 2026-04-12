package org.alejandro.vaca.persona;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.alejandro.vaca.persona.model.PersonaModel;
import org.alejandro.vaca.persona.repository.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PersonaIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private Firestore firestore;

        @MockitoBean
        private PersonaRepository personaRepository;

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
                when(collectionReference.whereEqualTo(anyString(), anyString())).thenReturn(query);
                when(query.whereEqualTo(anyString(), anyString())).thenReturn(query);
                when(query.get()).thenReturn(querySnapshotFuture);

                when(personaRepository.getPersonaPorCurp(anyString())).thenReturn(Optional.empty());
                when(personaRepository.getPersonaPorRFC(anyString())).thenReturn(Optional.empty());
                when(personaRepository.getPersonasPorNombre(anyString(), anyString(), anyString()))
                                .thenReturn(Collections.emptyList());

                WriteResult wr = Mockito.mock(WriteResult.class);
                when(documentReference.set(any())).thenReturn(ApiFutures.immediateFuture(wr));
                when(documentReference.delete()).thenReturn(ApiFutures.immediateFuture(wr));

                try {
                        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
                        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());
                } catch (InterruptedException | ExecutionException e) {
                }
        }

        @Test
        @DisplayName("Obtiene una persona por su ID")
        void obtenerPersonaPorID() throws Exception {
                String id = "1212123";
                PersonaModel persona = new PersonaModel(id, "alejandro", "pintor", "vaca", "12/07/2002", "masculino",
                                "mexicano", 1.80, 73.1, "5512345678", "alejandro@gmail.com", "PIVE", "PIVE0120712",
                                22.56);

                when(personaRepository.obtenerPorId(id)).thenReturn(Optional.of(persona));

                mockMvc.perform(get(BASE_URL + "/id/{id}", id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombre").value("alejandro"));
        }

        @Test
        @DisplayName("Metodo que despliega todas las personas que se tienen en la base de datos")
        void getPersonasPorNombreTest() throws Exception {
                mockMvc.perform(get(BASE_URL)
                                .param("nombre", "alejandro")
                                .param("apellidoP", "Pintor")
                                .param("apellidoM", "Vaca"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("Obtiene a una persona mediante su Curp")
        void obtenerPersonaPorCurp() throws Exception {
                String curp = "PIVE021712";
                PersonaModel persona = new PersonaModel("121", "alejandro", "pintor", "vaca", "12/07/2002", "masculino",
                                "mexicano", 1.80, 73.1, "5512345678", "alejandro@gmail.com", curp, "RFC123", 22.56);

                when(personaRepository.getPersonaPorCurp(curp)).thenReturn(Optional.of(persona));

                mockMvc.perform(get(BASE_URL + "/curp/{curp}", curp))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.curp").value(curp));
        }

        @Test
        @DisplayName("Obtiene una persona mediante su RFC")
        void obtenerPersonaPorRfc() throws Exception {
                String rfc = "PIVE0120712";
                PersonaModel persona = new PersonaModel("ID-123", "alejandro", "pintor", "vaca", "12/07/2002",
                                "masculino", "mexicano", 1.80, 73.1, "5512345678", "alejandro@gmail.com", "CURP", rfc,
                                22.56);

                when(personaRepository.getPersonaPorRFC(rfc)).thenReturn(Optional.of(persona));

                mockMvc.perform(get(BASE_URL + "/rfc/{rfc}", rfc))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.rfc").value(rfc));
        }

        @Test
        @DisplayName("Devuelve error 400 cuando el ID buscado no existe")
        void obtenerPersonaPorIdInexistente() throws Exception {
                String idInexistente = "no-existe-123";
                when(personaRepository.obtenerPorId(idInexistente)).thenReturn(Optional.empty());

                mockMvc.perform(get(BASE_URL + "/id/{id}", idInexistente))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Registra una nueva persona exitosamente")
        void registrarPersonaTest() throws Exception {
                when(personaRepository.guardar(any(PersonaModel.class))).thenAnswer(invocation -> {
                        PersonaModel p = invocation.getArgument(0);
                        return new PersonaModel("nuevo-id-123", p.nombre(), p.apellidoPaterno(), p.apellidoMaterno(),
                                        p.fechaDeNacimiento(), p.genero(), p.estatusMigratorio(), p.estatura(),
                                        p.peso(), p.telefono(), p.email(), p.curp(), p.rfc(), p.imc());
                });

                mockMvc.perform(post(BASE_URL)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content("{\"nombre\":\"Alejandro\",\"apellidoPaterno\":\"Pintor\",\"apellidoMaterno\":\"Vaca\",\"fechaDeNacimiento\":\"12/07/2002\",\"genero\":\"masculino\",\"estatusMigratorio\":\"mexicano\",\"estatura\":1.80,\"peso\":73.1,\"telefono\":\"5512345678\",\"email\":\"alejandro@gmail.com\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.imc").value(22.56));
        }

        @Test
        @DisplayName("Actualiza una persona localizándola por su CURP")
        void actualizarPersonaPorCurpTest() throws Exception {
                String curp = "PIVE020712HDFRRR01";
                PersonaModel existente = new PersonaModel("id-456", "alejandro", "p", "v", "12/07/2002", "masculino",
                                "mexicano", 1.80, 75.0, "5512345678", "v@g.com", curp, "RFC", 23.15);

                when(personaRepository.getPersonaPorCurp(curp)).thenReturn(Optional.of(existente));
                when(personaRepository.actualizarPersonaPorId(anyString(), any(PersonaModel.class)))
                                .thenAnswer(inv -> inv.getArgument(1));

                mockMvc.perform(put(BASE_URL + "/curp/{curp}", curp)
                                .contentType("application/json")
                                .content("{\"nombre\":\"Alejandro\",\"apellidoPaterno\":\"Pintor\",\"apellidoMaterno\":\"Vaca\",\"fechaDeNacimiento\":\"12/07/2002\",\"genero\":\"masculino\",\"estatusMigratorio\":\"mexicano\",\"estatura\":1.80,\"peso\":75.0,\"telefono\":\"5512345678\",\"email\":\"actualizado@gmail.com\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("actualizado@gmail.com"));
        }

        @Test
        @DisplayName("Actualiza una persona localizándola por su RFC")
        void actualizarPersonaPorRfcTest() throws Exception {
                String rfc = "PIVE0120712";
                PersonaModel existente = new PersonaModel("id-789", "alejandro", "p", "v", "12/07/2002", "masculino",
                                "mexicano", 1.80, 80.0, "5512345678", "v@g.com", "CURP", rfc, 24.69);

                when(personaRepository.getPersonaPorRFC(rfc)).thenReturn(Optional.of(existente));
                when(personaRepository.actualizarPersonaPorId(anyString(), any(PersonaModel.class)))
                                .thenAnswer(inv -> inv.getArgument(1));

                mockMvc.perform(put(BASE_URL + "/rfc/{rfc}", rfc)
                                .contentType("application/json")
                                .content("{\"nombre\":\"Alejandro\",\"apellidoPaterno\":\"Pintor\",\"apellidoMaterno\":\"Vaca\",\"fechaDeNacimiento\":\"12/07/2002\",\"genero\":\"masculino\",\"estatusMigratorio\":\"mexicano\",\"estatura\":1.80,\"peso\":80.0,\"telefono\":\"5512345678\",\"email\":\"nuevo@gmail.com\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("nuevo@gmail.com"));
        }

        @Test
        @DisplayName("Elimina una persona por su ID")
        void eliminarPersonaPorIdTest() throws Exception {
                when(personaRepository.deletePersonaPorId("ID-BORRAR")).thenReturn(true);
                mockMvc.perform(delete(BASE_URL + "/id/ID-BORRAR")).andExpect(status().isOk())
                                .andExpect(jsonPath("$").value(true));
        }

        @Test
        @DisplayName("Elimina una persona por su CURP")
        void eliminarPersonaPorCurpTest() throws Exception {
                String curp = "PIVE021712";
                mockMvc.perform(delete(BASE_URL + "/curp/{curp}", curp)).andExpect(status().isOk());
        }

        @ParameterizedTest
        @CsvSource({
                        "70.0, 1.75, 22.86",
                        "85.5, 1.80, 26.39",
                        "50.0, 1.60, 19.53"
        })
        @DisplayName("Integración: Registro y cálculo de IMC parametrizado")
        void registroParametrizadoTest(Double peso, Double estatura, Double imcEsperado) throws Exception {
                when(personaRepository.guardar(any(PersonaModel.class))).thenAnswer(i -> i.getArgument(0));

                String json = String.format("""
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

                mockMvc.perform(post(BASE_URL).contentType("application/json").content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.imc").value(imcEsperado));
        }

        @RepeatedTest(10)
        @DisplayName("Integracion: Generación repetida de CURP")
        void registroRepetidoCurpTest() throws Exception {
                when(personaRepository.guardar(any(PersonaModel.class))).thenAnswer(i -> i.getArgument(0));

                mockMvc.perform(post(BASE_URL)
                                .contentType("application/json")
                                .content("""
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
                                                """))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.curp").exists())
                                .andExpect(jsonPath("$.curp").value(org.hamcrest.Matchers.hasLength(16)));
        }

        @Test
        @DisplayName("Integracion: Error al registrar menor de 16 años")
        void registroFallaMenorEdadTest() throws Exception {
                mockMvc.perform(post(BASE_URL).contentType("application/json").content(
                                "{\"nombre\":\"Niño\",\"apellidoPaterno\":\"P\",\"apellidoMaterno\":\"L\",\"fechaDeNacimiento\":\"01/01/2020\",\"genero\":\"masculino\",\"estatusMigratorio\":\"mexicano\",\"estatura\":1.20,\"peso\":30.0,\"telefono\":\"5500000000\",\"email\":\"n@g.com\"}"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Integracion: Error de validación Jakarta (Teléfono inválido)")
        void registroFallaTelefonoTest() throws Exception {
                mockMvc.perform(post(BASE_URL).contentType("application/json").content(
                                "{\"nombre\":\"Err\",\"apellidoPaterno\":\"P\",\"apellidoMaterno\":\"L\",\"fechaDeNacimiento\":\"01/01/2000\",\"genero\":\"masculino\",\"estatusMigratorio\":\"mexicano\",\"estatura\":1.70,\"peso\":70.0,\"telefono\":\"44123\",\"email\":\"e@g.com\"}"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Integracion: Error de validación en Género (No permitido)")
        void registroFallaGeneroTest() throws Exception {
                mockMvc.perform(post(BASE_URL).contentType("application/json").content(
                                "{\"nombre\":\"Err\",\"apellidoPaterno\":\"P\",\"apellidoMaterno\":\"L\",\"fechaDeNacimiento\":\"01/01/2000\",\"genero\":\"desconocido\",\"estatusMigratorio\":\"mexicano\",\"estatura\":1.70,\"peso\":70.0,\"telefono\":\"5512345678\",\"email\":\"e@g.com\"}"))
                                .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @CsvSource({ "55123, t@g.com", "4412345678, t@g.com", "5512345678, malo" })
        @DisplayName("Integración: Validación parametrizada de datos de contacto inválidos")
        void validacionContactoParametrizadaTest(String tel, String mail) throws Exception {
                String json = String.format(
                                "{\"nombre\":\"Err\",\"apellidoPaterno\":\"P\",\"apellidoMaterno\":\"L\",\"fechaDeNacimiento\":\"01/01/2000\",\"genero\":\"masculino\",\"estatusMigratorio\":\"mexicano\",\"estatura\":1.70,\"peso\":70.0,\"telefono\":\"%s\",\"email\":\"%s\"}",
                                tel, mail);
                mockMvc.perform(post(BASE_URL).contentType("application/json").content(json))
                                .andExpect(status().isBadRequest());
        }

        @RepeatedTest(5)
        @DisplayName("Integración: Validación de formato Regex en dígitos aleatorios de CURP")
        void validacionRegexCurpAleatoriaTest() throws Exception {
                when(personaRepository.guardar(any(PersonaModel.class))).thenAnswer(i -> i.getArgument(0));

                mockMvc.perform(post(BASE_URL)
                                .contentType("application/json")
                                .content("""
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
                                                """))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.curp").value(
                                                org.hamcrest.Matchers.matchesPattern("^[A-Z]{4}\\d{6}[A-Z0-9]{6}$")));
        }
}