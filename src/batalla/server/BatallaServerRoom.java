package batalla.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.mina.common.IoSession;

import server.TwoPlayersServerRoom;
import batalla.common.messages.server.DisparoEnemigoMessage;
import batalla.common.messages.server.DisparoResultMessage;
import batalla.common.messages.server.FinJuegoMessage;
import batalla.common.messages.server.NewGameMessage;
import batalla.common.messages.server.StartGameMessage;
import batalla.common.model.Barco;
import batalla.common.model.BatallaRoom;
import batalla.common.model.Celda;
import batalla.common.model.Posicion;
import batalla.common.model.Resultado;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import common.messages.server.UpdatedPointsMessage;
import common.model.AbstractRoom;

public class BatallaServerRoom extends TwoPlayersServerRoom {

    // los barcos y las celdas que ocupa
    private Map<Barco, List<Celda>> barcos1, barcos2;

    private Map<Barco, Posicion> posiciones1, posiciones2;

    public BatallaServerRoom(BatallaSaloon saloon, IoSession session, int puntos) {
        super(saloon, session, puntos);
    }

    @Override
    public AbstractRoom createRoom() {
        return new BatallaRoom(getId(), puntosApostados, getUsers());
    }

    @Override
    public void startGame() {
    }

    public void setPosicionesBarcos(IoSession session,
            Map<Barco, Posicion> barcos) {

        HashMap<Barco, List<Celda>> mapa = Maps.newHashMap();

        for (Map.Entry<Barco, Posicion> entry : barcos.entrySet()) {
            List<Celda> celdas = Lists.newArrayList();

            Celda cel = entry.getValue().getCelda();
            boolean horiz = entry.getValue().isHorizontal();

            for (int i = 0; i < entry.getKey().getLargo(); i++) {
                celdas.add(cel);

                cel = horiz ? cel.der() : cel.aba();
            }

            mapa.put(entry.getKey(), celdas);
        }

        if (session == player1) {
            barcos1 = mapa;
            posiciones1 = barcos;
        }
        else {
            barcos2 = mapa;
            posiciones2 = barcos;
        }

        if (barcos1 != null && barcos2 != null) {
            setEnJuego(true);

            player1.write(new StartGameMessage(true));
            player2.write(new StartGameMessage(false));
        }
    }

    public void disparo(IoSession session, Celda celda) {
        IoSession atacante, atacado;
        Map<Barco, List<Celda>> barcos;
        Map<Barco, Posicion> posiciones;

        if (session == player1) {
            atacante = player1;
            atacado = player2;
            barcos = barcos2;
            posiciones = posiciones2;
        }
        else {
            atacante = player2;
            atacado = player1;
            barcos = barcos1;
            posiciones = posiciones1;
        }

        Resultado res = Resultado.AGUA;
        DisparoResultMessage drm = new DisparoResultMessage(res);

        Iterator<Map.Entry<Barco, List<Celda>>> it = barcos.entrySet()
                .iterator();
        while (it.hasNext()) {
            Map.Entry<Barco, List<Celda>> entry = it.next();

            List<Celda> celdas = entry.getValue();
            if (celdas.contains(celda)) {
                celdas.remove(celda);

                if (celdas.size() == 0) {
                    res = Resultado.HUNDIDO;
                    Barco b = entry.getKey();
                    drm = new DisparoResultMessage(res, b, posiciones.get(b));

                    // saco este barco
                    it.remove();
                }
                else {
                    res = Resultado.TOCADO;
                    drm = new DisparoResultMessage(res);
                }

                break;
            }
        }

        // si rompi todo
        if (barcos.size() == 0) {
            // gameover

            setEnJuego(false);

            atacante.write(new FinJuegoMessage(true));
            atacado.write(new FinJuegoMessage(false));

            atacante.write(drm);
            atacado.write(new DisparoEnemigoMessage(celda, res));

            // transfiero puntos
            int newPoints[] = saloon.transferPoints(atacante, atacado,
                    puntosApostados);

            // mando puntos (si siguen conectados)
            if (atacante != null) {
                atacante.write(new UpdatedPointsMessage(newPoints[0]));
            }
            if (atacado != null) {
                atacado.write(new UpdatedPointsMessage(newPoints[1]));
            }
        }
        else {
            atacante.write(drm);
            atacado.write(new DisparoEnemigoMessage(celda, res));
        }
    }

    @Override
    public void startNuevoJuego() {
        barcos1 = null;
        barcos2 = null;
        posiciones1 = null;
        posiciones2 = null;

        player1.write(new NewGameMessage());
        player2.write(new NewGameMessage());
    }

    @Override
    public boolean isGameOn() {
        return barcos1 != null && barcos2 != null;
    }
}