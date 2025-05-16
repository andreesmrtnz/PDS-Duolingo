// EstadisticaTest.java
package modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class EstadisticaTest {
    @Test
    void testConstructorAndGetters() {
        Estadistica stats = new Estadistica(1L, 3600L, 5, 3);
        
        assertEquals(1L, stats.getId());
        assertEquals(3600L, stats.getTiempoTotalUso());
        assertEquals(5, stats.getMejorRacha());
        assertEquals(3, stats.getDiasConsecutivos());
    }
}