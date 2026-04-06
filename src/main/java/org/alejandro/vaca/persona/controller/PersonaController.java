@RestController
@RequestMapping("/personas/personas-api")
public class PersonaController{
    private final PersonaService personaService;
    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }
    // Todos los metodos GET
    @GetMapping
    public List<PersonaModel> listarPersonas() {
        return personaService.listarPersonas();
    }
    @GetMapping("/id/{id}")
    public PersonaModel obtenerLibroPorId(@PathVariable String id) {
        return personaService.buscarPersonaPorId(id);
    }
    @GetMapping("/curp/{curp}")
    public PersonaModel obtenerPersonaPorCurp(@PathVariable String curp) {
        return personaService.buscarPersonaPorCurp(curp);
    }
    @GetMapping("/rfc/{rfc}")
    public PersonaModel obtenerPersonaPorRfc(@PathVariable String rfc) {
        return personaService.buscarPersonaPorRfc(rfc);
    }
    // El unico metodo POST
    @PostMapping
    public PersonaModel registrarPersona(@RequestBody PersonaModel persona) {
        return personaService.registrarPersona(persona);
    }
    // Todos los metodos PUT para actualizar los datos de la persona
    @PutMapping("/id/{id}")
    public PersonaModel actualizarPersona(@PathVariable String id, @RequestBody PersonaModel persona) {
        return personaService.actualizarPersona(persona);
    }
    @PutMapping("/curp/{curp}")
    public PersonaModel actualizarPersonaPorCurp(@PathVariable String curp, @RequestBody PersonaModel persona) {
        return personaService.actualizarPersonaPorCurp(curp, persona);
    }
    @PutMapping("/rfc/{rfc}")
    public PersonaModel actualizarPersonaPorRfc(@PathVariable String rfc, @RequestBody PersonaModel persona) {
        return personaService.actualizarPersonaPorRfc(rfc, persona);
    }
    // Todos los metodos DELETE para eliminar los datos de la persona
    @DeleteMapping("/id/{id}")
    public boolean eliminarPersona(@PathVariable String id) {
        return personaService.eliminarPersona(id);
    }
    @DeleteMapping("/curp/{curp}")
    public boolean eliminarPersonaPorCurp(@PathVariable String curp) {
        return personaService.eliminarPersonaPorCurp(curp);
    }
    @DeleteMapping("/rfc/{rfc}")
    public boolean eliminarPersonaPorRfc(@PathVariable String rfc) {
        return personaService.eliminarPersonaPorRfc(rfc);
    }
}