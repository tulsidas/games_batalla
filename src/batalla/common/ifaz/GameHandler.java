package batalla.common.ifaz;

import batalla.common.model.Barco;
import batalla.common.model.Celda;
import batalla.common.model.Posicion;
import batalla.common.model.Resultado;

import common.ifaz.BasicGameHandler;

public interface GameHandler extends BasicGameHandler {

    void disparoEnemigo(Celda cell, Resultado res);

    // resultado disparos
    void agua();

    void tocado();

    void hundido(Barco barco, Posicion pos);
}
