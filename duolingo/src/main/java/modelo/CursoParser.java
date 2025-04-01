package modelo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CursoParser {

    /**
     * Carga un curso desde un archivo JSON.
     * 
     * @param rutaArchivo La ruta al archivo JSON
     * @param usuarioActual El usuario actual que está cargando el curso
     * @return Un objeto Curso con los datos cargados
     * @throws IOException Si ocurre un error al leer el archivo
     */
    public static Curso cargarCurso(String rutaArchivo, Usuario usuarioActual) throws IOException {
        try {
            // Configurar ObjectMapper para ignorar propiedades desconocidas
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
            // Cargar el DTO desde el archivo
            CursoDTO cursoDTO = mapper.readValue(new File(rutaArchivo), CursoDTO.class);
            
            // Convertir el DTO a un objeto de dominio
            List<Bloque> bloques = new ArrayList<>();
            
            // Convertir los BloqueDTO a objetos Bloque
            if (cursoDTO.getBloques() != null) {
                for (BloqueDTO bloqueDTO : cursoDTO.getBloques()) {
                    List<Pregunta> preguntas = new ArrayList<>();
                    
                    // Convertir las PreguntaDTO a objetos Pregunta específicos
                    if (bloqueDTO.getPreguntas() != null) {
                        for (PreguntaDTO preguntaDTO : bloqueDTO.getPreguntas()) {
                            // Aquí utilizamos el método toPreguntaObjeto que se encargará de
                            // crear la instancia específica del tipo correcto de pregunta
                            Pregunta pregunta = preguntaDTO.toPreguntaObjeto();
                            
                            // Establecer estados adicionales como las selecciones del usuario
                            if (preguntaDTO.getSeleccionUsuario() != null) {
                                pregunta.setSeleccionUsuario(preguntaDTO.getSeleccionUsuario());
                            }
                            
                            if (preguntaDTO.getSeleccionesEmparejamiento() != null) {
                                pregunta.setSeleccionesEmparejamiento(preguntaDTO.getSeleccionesEmparejamiento());
                            }
                            
                            if (preguntaDTO.getOrdenSeleccionado() != null) {
                                pregunta.setOrdenSeleccionado(preguntaDTO.getOrdenSeleccionado());
                            }
                            
                            preguntas.add(pregunta);
                        }
                    }
                    
                    // Crear el bloque con las preguntas convertidas
                    Bloque bloque = new Bloque(bloqueDTO.getId(), bloqueDTO.getTitulo(), 
                                              bloqueDTO.getDescripcion(), preguntas);
                    bloques.add(bloque);
                }
            }
            
            // Obtener el creador del curso - si no existe, usar el usuario actual
            Usuario creador = usuarioActual;
            if (cursoDTO.getIdCreador() != null) {
                // Aquí deberías obtener el usuario de la base de datos si es necesario
                // creador = usuarioDAO.obtenerPorId(cursoDTO.getIdCreador());
            }
            
            // Crear el curso con los datos convertidos
            Curso curso = new Curso(
                cursoDTO.getId(),
                cursoDTO.getTitulo(),
                cursoDTO.getDominio(),
                creador,
                bloques,
                cursoDTO.getPosicionActual()
            );
            
            // Establecer correctamente las relaciones bidireccionales
            for (Bloque bloque : curso.getBloques()) {
                bloque.setCurso(curso);
                
                for (Pregunta pregunta : bloque.getPreguntas()) {
                    pregunta.setBloque(bloque);
                }
            }
            
            return curso;
        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error al procesar los datos del curso: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Error al procesar los datos del curso", e);
        }
    }
    
    /**
     * Guarda un curso en un archivo JSON.
     * 
     * @param curso El curso a guardar
     * @param rutaArchivo La ruta donde se guardará el archivo
     * @throws IOException Si ocurre un error al escribir el archivo
     */
    public static void guardarCurso(Curso curso, String rutaArchivo) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            // Convertir el curso a DTO
            CursoDTO cursoDTO = new CursoDTO(curso);
            
            // Guardar el DTO en el archivo
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(rutaArchivo), cursoDTO);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo JSON: " + e.getMessage());
            throw e;
        }
    }
}