package org.alejandro.vaca.persona.repository;

import org.alejandro.vaca.persona.model.PersonaModel;
import org.springframework.stereotype.Repository;
import java.util.List;

@SuppressWarnings
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

@Repository
public class PersonaRepository {
    private static final String COLLECTION = "personas";
    public final Firestore firestore;

    public PersonaRepository(Firestore fire) {
        this.firestore = fire;
    }

    /*
     * Dentro de la lógica de negocios del programa, se calculará el IMC de la
     * persona, su RFC sin homoclave y su CURP, en el caso de este último, podrás
     * crear valores
     * aleatorios en
     * los dígitos 18 y 19 para completar la estructura.
     */

    // Inician metodos de IMC
    public void calcularIMC() {

    }

    public void obtenerIMCPorId() {

    }

    public void obtenerIMCPorCURP() {

    }

    public void guardarIMC(Double imc) {

    }

    // Termina metodos de IMC
    // Inician metodos de Rfc
    public void CrearRfc() {

    }

    public void getRfcPorId() {

    }

    public void getRfcPorCurp() {

    }

    public void deleteRfcPorId() {

    }
    public void deleteRfcPorCurp() {

    }
    public void updateRfcPorCurp() {

    }
    public void updateRfcPorNombreYFechaNac(String nombreCompleto, String fechaNac){
        
    }

    // Terminan metodos de Rfc
    // Inician metodos para Curp
    public void crearCurp() {

    }

    public void getCurpPorId() {

    }

    public void getCurpPorNombreYFechaNac(String nombreCompleto, String fechaNac) {

    }
    public void actualizarCurpPorId(){

    }
    public void actualizarCurpPorNombreYFechaNac(String nombreCompleto, String fechaNac){

    }

    // Terminan metodos para Curp
    // Metodos para persona
    public void guardarPersona(PersonaModel personaModel) {

    }
    public void getPersonaPorCurp(String curp){

    }
    public void getPersonaPorRfc(String rfc){

    }
    public List<PersonaModel> getTodaPersonaEnDB(){

    }
}
