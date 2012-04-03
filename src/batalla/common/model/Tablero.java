package batalla.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tablero {
    private Map<Barco, Posicion> barcos;

    public Tablero() {
        this.barcos = new HashMap<Barco, Posicion>();

        placeRandom();
    }

    private void placeRandom() {
        List<Barco> barquillos = new ArrayList<Barco>(5);
        barquillos.add(Barco.BOTE);
        barquillos.add(Barco.SUBMARINO);
        barquillos.add(Barco.CRUCERO);
        barquillos.add(Barco.ACORAZADO);
        barquillos.add(Barco.PORTAAVIONES);

        List<Celda> disponibles = getDisponibles();

        for (Barco barco : barquillos) {
            Celda c;
            boolean h;
            do {
                c = disponibles.get((int) (Math.random() * disponibles.size()));
                h = Math.random() < 0.5;
            }
            while (!puedePoner(disponibles, barco, c, h));

            move(barco, c, h);

            // marco celdas como no disponibles
            marcarCeldas(disponibles, c, barco.getLargo(), h);
        }
    }

    private void marcarCeldas(List<Celda> disponibles, Celda cell, int largo,
            boolean horiz) {
        if (horiz) {
            if (cell.izq() != null) {
                disponibles.remove(cell.izq());
                disponibles.remove(cell.izq().arr());
                disponibles.remove(cell.izq().aba());
            }
            for (int i = 0; i < largo; i++) {
                disponibles.remove(cell);
                disponibles.remove(cell.arr());
                disponibles.remove(cell.aba());

                cell = cell.der();
            }

            // la de la derecha
            if (cell != null) {
                disponibles.remove(cell);
                disponibles.remove(cell.arr());
                disponibles.remove(cell.aba());
            }
        }
        else { // vertical
            if (cell.arr() != null) {
                disponibles.remove(cell.arr());
                disponibles.remove(cell.arr().izq());
                disponibles.remove(cell.arr().der());
            }
            for (int i = 0; i < largo; i++) {
                disponibles.remove(cell);
                disponibles.remove(cell.izq());
                disponibles.remove(cell.der());

                cell = cell.aba();
            }

            // la de la derecha
            if (cell != null) {
                disponibles.remove(cell);
                disponibles.remove(cell.izq());
                disponibles.remove(cell.der());
            }
        }
    }

    /**
     * @return si los barcos estan bien puestos
     */
    public boolean isValid() {
        List<Celda> disponibles = getDisponibles();

        for (Map.Entry<Barco, Posicion> entry : barcos.entrySet()) {
            Barco barco = entry.getKey();
            Celda cell = entry.getValue().getCelda();
            boolean horiz = entry.getValue().isHorizontal();

            if (puedePoner(disponibles, barco, cell, horiz)) {
                // marco celdas como no disponibles
                marcarCeldas(disponibles, cell, barco.getLargo(), horiz);
            }
            else {
                return false;
            }
        }

        return true;
    }

    boolean puedePoner(List<Celda> disponibles, Barco b, Celda celda,
            boolean horizontal) {
        if (!entraBarco(b, celda, horizontal)) {
            return false;
        }

        // veo que no choque
        for (int i = 0; i < b.getLargo(); i++) {
            if (!disponibles.contains(celda)) {
                return false;
            }

            celda = horizontal ? celda.der() : celda.aba();
        }

        return true;
    }

    /**
     * @param b
     *            el barco
     * @param celda
     *            la celda donde evaluo
     * @param horizontal
     *            su alineamiento
     * @return si el barco entra en esta celda
     */
    public boolean entraBarco(Barco b, Celda celda, boolean horizontal) {
        if (celda == null || celda.getFila() < 0 || celda.getColumna() < 0) {
            return false;
        }

        if (horizontal) {
            if (b.getLargo() + celda.getColumna() > 11) {
                // se pasa
                return false;
            }
        }
        else { // vertical
            if (b.getLargo() + celda.getFila() > 11) {
                // se pasa
                return false;
            }
        }

        return true;
    }

    public void move(Barco b, Celda c, boolean h) {
        barcos.put(b, new Posicion(c, h));
    }

    public Map<Barco, Posicion> getBarcos() {
        return barcos;
    }

    /**
     * @return el barco que esta en la celda c o null
     */
    public Barco getBarco(Celda c) {
        for (Map.Entry<Barco, Posicion> entry : barcos.entrySet()) {
            Barco barco = entry.getKey();
            Celda cell = entry.getValue().getCelda();
            boolean horiz = entry.getValue().isHorizontal();

            for (int i = 0; i < barco.getLargo(); i++) {
                if (cell.equals(c)) {
                    return barco;
                }
                else {
                    cell = horiz ? cell.der() : cell.aba();
                }
            }
        }

        return null;
    }

    private List<Celda> getDisponibles() {
        List<Celda> disponibles = new ArrayList<Celda>(100);
        for (int f = 1; f <= 10; f++) {
            for (int c = 1; c <= 10; c++) {
                disponibles.add(new Celda(f, c));
            }
        }

        return disponibles;
    }
}
