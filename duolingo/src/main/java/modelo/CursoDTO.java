package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase DTO (Data Transfer Object) para la carga y guardado de cursos desde/hacia
 * archivos JSON o YAML.
 */
public class CursoDTO {
    private Long id;
    private String titulo;
    private String dominio;
    private Long idCreador; // Solo guardamos el ID del creador
    private List<BloqueDTO> bloques;
    private int posicionActual;
    private String tipoEstrategia; // String que identifica el tipo de estrategia
    
    // Constructor vacío necesario para Jackson
    public CursoDTO() {
    }
    
    // Constructor para crear DTO desde un objeto Curso
    public CursoDTO(Curso curso) {
        this.id = curso.getId();
        this.titulo = curso.getTitulo();
        this.dominio = curso.getDominio();
        this.idCreador = curso.getCreador().getId();
        this.posicionActual = curso.getPosicionActual();
        
        // Convertir estrategia a su nombre
        this.tipoEstrategia = curso.getEstrategia().getClass().getSimpleName();
        
        // Convertir bloques a DTOs
        this.bloques = convertirBloquesADTO(curso.getBloques());
    }
    
    private List<BloqueDTO> convertirBloquesADTO(List<Bloque> bloques) {
        List<BloqueDTO> bloquesDTO = new ArrayList<>();
        for (Bloque bloque : bloques) {
            bloquesDTO.add(new BloqueDTO(bloque));
        }
        return bloquesDTO;
    }
    
    // Getters y setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDominio() {
        return dominio;
    }
    
    public void setDominio(String dominio) {
        this.dominio = dominio;
    }
    
    public Long getIdCreador() {
        return idCreador;
    }
    
    public void setIdCreador(Long idCreador) {
        this.idCreador = idCreador;
    }
    
    public List<BloqueDTO> getBloques() {
        return bloques;
    }
    
    public void setBloques(List<BloqueDTO> bloques) {
        this.bloques = bloques;
    }
    
    public int getPosicionActual() {
        return posicionActual;
    }
    
    public void setPosicionActual(int posicionActual) {
        this.posicionActual = posicionActual;
    }
    
    public String getTipoEstrategia() {
        return tipoEstrategia;
    }
    
    public void setTipoEstrategia(String tipoEstrategia) {
        this.tipoEstrategia = tipoEstrategia;
    }
    
    // Método para obtener los bloques en formato de objetos de dominio
    public List<Bloque> getBloquesDominio() {
        // Implementación pendiente
        return null;
    }
    
    // Método para obtener la estrategia en formato de objeto de dominio
    public Estrategia getEstrategia() {
        // Implementación pendiente - crear estrategia según el tipo
        return null;
    }
}