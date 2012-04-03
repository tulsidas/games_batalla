package batalla.server;

import java.util.Map;

import org.apache.mina.common.IoSession;

import server.AbstractSaloon;
import batalla.common.ifaz.SaloonHandler;
import batalla.common.model.Barco;
import batalla.common.model.Celda;
import batalla.common.model.Posicion;

import common.ifaz.POSTHandler;

public class BatallaSaloon extends AbstractSaloon implements SaloonHandler {

    public BatallaSaloon(int id, POSTHandler poster) {
        super(id, poster);
    }

    public void setPosicionesBarcos(IoSession session, Map<Barco, Posicion> map) {
        getRoom(session).setPosicionesBarcos(session, map);
    }

    @Override
    protected BatallaServerRoom getRoom(IoSession session) {
        return (BatallaServerRoom) super.getRoom(session);
    }

    public void disparo(IoSession session, Celda celda) {
        getRoom(session).disparo(session, celda);
    }

    public void createRoom(IoSession session, int puntos) {
        createRoom(session, puntos,
                new BatallaServerRoom(this, session, puntos));
    }
}
