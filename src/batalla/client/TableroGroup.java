package batalla.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pulpcore.Input;
import pulpcore.image.AnimatedImage;
import pulpcore.image.CoreImage;
import pulpcore.sprite.Group;
import pulpcore.sprite.ImageSprite;
import batalla.common.model.Barco;
import batalla.common.model.Celda;
import batalla.common.model.Posicion;
import batalla.common.model.Tablero;

public class TableroGroup extends Group {

    private BarcoSprite movingShip;

    private List<BarcoSprite> barcos;

    private Tablero tablero;

    private boolean enJuego = false;

    private CoreImage agua;

    // si es mi tablero o el del oponente
    private boolean propio;

    // donde se cliqueo el mouse
    private int deltaX, deltaY;

    public static final float CELDA_X = 22.5f, CELDA_Y = 20f;

    public TableroGroup(int x, int y, int width, int height, boolean propio) {
        super(x, y, width, height);

        this.propio = propio;
        barcos = new ArrayList<BarcoSprite>(5);

        agua = CoreImage.load("imgs/agua.png");

        reset();
    }

    void reset() {
        removeAll();
        barcos.clear();

        tablero = new Tablero();

        if (propio) {
            for (Map.Entry<Barco, Posicion> entry : tablero.getBarcos()
                    .entrySet()) {
                BarcoSprite barco = new BarcoSprite(entry.getKey(), entry
                        .getValue());

                add(barco);
                barcos.add(barco);
            }
        }
    }

    public Celda getCelda(int viewX, int viewY) {
        int x = (int) getLocalX(viewX, viewY);
        int y = (int) getLocalY(viewX, viewY);

        // sumo uno porque empiezan en 1 y los pixels en 0
        int columna = 1 + (int) (x / CELDA_X);
        int fila = 1 + (int) (y / CELDA_Y);

        if (fila < 0 || columna < 0 || fila > 10 || columna > 10) {
            return null;
        }
        else {
            return new Celda(fila, columna);
        }
    }

    /**
     * @param celda
     *            la celda a marcar como tocado
     * @return el barco que fue tocado
     */
    public Barco tocado(Celda celda) {
        AnimatedImage fuego = new AnimatedImage(CoreImage
                .load("imgs/fuego.png"), 3, 1);
        fuego.setFrameDuration(250, true);
        fuego.start();

        add(new ImageSprite(fuego, (celda.getColumna() - 1) * CELDA_X + 5,
                (celda.getFila() - 1) * CELDA_Y - 5));

        return tablero.getBarco(celda);
    }

    public void agua(Celda celda) {
        add(new ImageSprite(agua, (celda.getColumna() - 1) * CELDA_X, (celda
                .getFila() - 1)
                * CELDA_Y));
    }

    @Override
    public void update(int elapsedTime) {
        super.update(elapsedTime);

        if (!enJuego) {
            int viewX = Input.getMouseX();
            int viewY = Input.getMouseY();

            Input.setCursor(isMouseOver() ? Input.CURSOR_HAND
                    : Input.CURSOR_DEFAULT);

            if (Input.isMouseDown() && movingShip != null
                    && contains(viewX, viewY)) {
                // veo si esta en una celda valida dentro de los limites
                Celda celda = getCelda(viewX - deltaX, viewY - deltaY);

                if (tablero.entraBarco(movingShip.getBarco(), celda, movingShip
                        .isHorizontal())) {
                    movingShip.setCelda(celda);
                }
            }

            // drop del barco
            if (Input.isMouseReleased() && movingShip != null) {
                // actualizo tablero
                tablero.move(movingShip.getBarco(), movingShip.getCelda(),
                        movingShip.isHorizontal());

                // dejo de mover
                movingShip = null;
            }

            for (BarcoSprite barco : barcos) {
                if (barco.isMouseDoubleClicked()) {

                    // si no entra cambiandole la orientacion, lo muevo hasta
                    // que entre
                    if (!tablero.entraBarco(barco.getBarco(), barco.getCelda(),
                            !barco.isHorizontal())) {
                        do {
                            barco.setCelda(barco.isHorizontal() ? barco
                                    .getCelda().arr() : barco.getCelda().izq());
                        }
                        while (!tablero.entraBarco(barco.getBarco(), barco
                                .getCelda(), !barco.isHorizontal()));
                    }

                    // actualizo sprite
                    barco.setHorizontal(!barco.isHorizontal());

                    // actualizo tablero
                    tablero.move(barco.getBarco(), barco.getCelda(), barco
                            .isHorizontal());
                }
                else if (barco.isMousePressed()) {
                    movingShip = barco;

                    deltaX = (int) (getLocalX(viewX, viewY) - barco.x
                            .getAsInt());
                    deltaY = (int) (getLocalY(viewX, viewY) - barco.y
                            .getAsInt());
                }
            }
        }
    }

    public boolean isValid() {
        return tablero.isValid();
    }

    public Map<Barco, Posicion> getBarcos() {
        return tablero.getBarcos();
    }

    public void setEnJuego(boolean enJuego) {
        this.enJuego = enJuego;
    }
}