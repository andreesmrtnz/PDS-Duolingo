package modelo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CursoParser {
    private static RepositorioUsuarios repoUsuarios = RepositorioUsuarios.getUnicaInstancia();
    
    /**
     * Carga un curso desde un archivo (JSON o YAML) y lo convierte a un objeto Curso
     */
    public static Curso cargarCurso(String rutaArchivo, Usuario creador) throws Exception {
        ObjectMapper mapper;
        
        // Determinar el tipo de archivo por su extensión
        if (rutaArchivo.toLowerCase().endsWith(".json")) {
            mapper = new ObjectMapper();
        } else if (rutaArchivo.toLowerCase().endsWith(".yaml") || rutaArchivo.toLowerCase().endsWith(".yml")) {
            mapper = new ObjectMapper(new YAMLFactory());
        } else {
            throw new IllegalArgumentException("Formato de archivo no soportado. Use .json o .yaml");
        }
        
        CursoDTO cursoDTO = mapper.readValue(new File(rutaArchivo), CursoDTO.class);
        return convertirDTOaCurso(cursoDTO, creador);
    }
    
    /**
     * Guarda un curso en un archivo (JSON por defecto)
     */
    public static void guardarCurso(Curso curso, String rutaArchivo) throws Exception {
        ObjectMapper mapper;
        
        // Determinar el tipo de archivo por su extensión
        if (rutaArchivo.toLowerCase().endsWith(".yaml") || rutaArchivo.toLowerCase().endsWith(".yml")) {
            mapper = new ObjectMapper(new YAMLFactory());
        } else {
            // Por defecto usamos JSON
            if (!rutaArchivo.toLowerCase().endsWith(".json")) {
                rutaArchivo += ".json";
            }
            mapper = new ObjectMapper();
        }
        
        CursoDTO cursoDTO = new CursoDTO(curso);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(rutaArchivo), cursoDTO);
    }

    /**
     * Convierte un DTO a un objeto Curso
     */
    static Curso convertirDTOaCurso(CursoDTO dto, Usuario creador) {
        // Convertir bloques
        List<Bloque> bloques = new ArrayList<>();
        if (dto.getBloques() != null) {
            for (BloqueDTO bloqueDTO : dto.getBloques()) {
                bloques.add(bloqueDTO.toBloqueObjeto());
            }
        }
        
        
        return new Curso(
            dto.getId(),
            dto.getTitulo(),
            dto.getDominio(),
            creador,  // Usamos el creador proporcionado
            bloques,
            dto.getPosicionActual()
        );
    }
    
    /**
     * Crea una estrategia según su tipo
     */
    private static Estrategia crearEstrategia(String tipoEstrategia) {
        // Aquí se crearían las estrategias según su tipo
        // Como no vemos la definición de Estrategia en el código compartido,
        // esto es un placeholder
        return new EstrategiaBasica();  // Asumimos que hay una implementación básica
    }
}