package batalla.common.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TableroTest {

    Tablero t;

    @Before
    public void setUp() {
        t = new Tablero();
    }

    @Test
    public void testPuedePoner() {
        // // vacio
        // assertTrue(t.puedePoner(Barco.BOTE, new Celda(1), true));
        // assertTrue(t.puedePoner(Barco.BOTE, new Celda(9), true));
        // assertFalse(t.puedePoner(Barco.BOTE, new Celda(10), true));
        //
        // // pongo portaaviones
        // t.add(Barco.PORTAAVIONES, new Celda(3), true);
        //
        // assertTrue(t.puedePoner(Barco.BOTE, new Celda(1), true));
        // assertFalse(t.puedePoner(Barco.BOTE, new Celda(2), true));
        // assertFalse(t.puedePoner(Barco.BOTE, new Celda(3), true));
        // assertFalse(t.puedePoner(Barco.BOTE, new Celda(4), true));
        // assertFalse(t.puedePoner(Barco.BOTE, new Celda(5), true));
        // assertFalse(t.puedePoner(Barco.BOTE, new Celda(6), true));
        // assertFalse(t.puedePoner(Barco.BOTE, new Celda(7), true));
        // assertFalse(t.puedePoner(Barco.BOTE, new Celda(8), true));
        // assertTrue(t.puedePoner(Barco.BOTE, new Celda(9), true));
        // assertFalse(t.puedePoner(Barco.BOTE, new Celda(10), true));
    }

    @Test
    public void testGetFila() {
        for (int f = 1; f < 11; f++) {
            for (int c = 1; c < 11; c++) {
                assertEquals(f, new Celda(f, c).getFila());
            }
        }
    }

    @Test
    public void testGetColumna() {
        for (int f = 1; f < 11; f++)
            for (int c = 1; c < 11; c++)
                assertEquals(c, new Celda(f, c).getColumna());
    }
}
