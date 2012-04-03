package batalla.common.ifaz;

import java.util.Map;

import org.apache.mina.common.IoSession;

import batalla.common.model.Barco;
import batalla.common.model.Celda;
import batalla.common.model.Posicion;

public interface SaloonHandler {

    public abstract void setPosicionesBarcos(IoSession session,
            Map<Barco, Posicion> map);

    public abstract void disparo(IoSession session, Celda celda);

}