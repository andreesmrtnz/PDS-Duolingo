package modelo;

import modelo.Bloque;
import modelo.BloqueDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

/**
 * Clase de prueba para el DTO BloqueDTO.
 * Se prueba la conversión desde el objeto de dominio Bloque a DTO y viceversa.
 */
public class BloqueDTOTest {

    @Test
    public void testBloqueDTODesdeBloque() {
        // Se crea un Bloque con lista de preguntas vacía
        Bloque bloque = new Bloque("Título DTO", "Descripción DTO");
        bloque.setId(1L);

        // Se crea el DTO a partir del objeto Bloque
        BloqueDTO bloqueDTO = new BloqueDTO(bloque);

        // Verifica que los atributos se hayan mapeado correctamente
        assertEquals(bloque.getId(), bloqueDTO.getId());
        assertEquals(bloque.getTitulo(), bloqueDTO.getTitulo());
        assertEquals(bloque.getDescripcion(), bloqueDTO.getDescripcion());
        assertNotNull(bloqueDTO.getPreguntas());
        assertTrue(bloqueDTO.getPreguntas().isEmpty());
    }

    @Test
    public void testToBloqueObjeto() {
        // Se crea un Bloque con datos de prueba y lista de preguntas vacía
        Bloque bloque = new Bloque("Título conversión", "Descripción conversión");
        bloque.setId(2L);
        // Se crea el DTO a partir del Bloque
        BloqueDTO bloqueDTO = new BloqueDTO(bloque);

        // Se convierte el DTO de vuelta a un objeto de dominio Bloque
        Bloque convertido = bloqueDTO.toBloqueObjeto();

        // Se verifican que los atributos se hayan transferido correctamente
        assertEquals(bloqueDTO.getId(), convertido.getId());
        assertEquals(bloqueDTO.getTitulo(), convertido.getTitulo());
        assertEquals(bloqueDTO.getDescripcion(), convertido.getDescripcion());
        // Debido a que la lista de preguntas era vacía, se espera que la lista resultante también lo sea
        assertNotNull(convertido.getPreguntas());
        assertTrue(convertido.getPreguntas().isEmpty());
    }
}
