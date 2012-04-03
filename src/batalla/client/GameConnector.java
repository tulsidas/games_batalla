package batalla.client;

import org.apache.mina.common.IoSession;

import batalla.common.ifaz.GameHandler;
import batalla.common.ifaz.GameMessage;
import batalla.common.messages.BatallaProtocolDecoder;
import batalla.common.model.Barco;
import batalla.common.model.Celda;
import batalla.common.model.Posicion;
import batalla.common.model.Resultado;
import client.AbstractGameConnector;

public class GameConnector extends AbstractGameConnector implements GameHandler {

    public GameConnector(String host, int port, int salon, String user,
            String pass, long version) {
        super(host, port, salon, user, pass, version,
                new BatallaProtocolDecoder());
    }

    @Override
    public void messageReceived(IoSession sess, Object message) {
        super.messageReceived(sess, message);

        if (message instanceof GameMessage && gameHandler != null) {
            ((GameMessage) message).execute(this);
        }
    }

    // /////////////
    // GameHandler
    // /////////////
    public void disparoEnemigo(Celda cell, Resultado res) {
        if (gameHandler != null) {
            ((GameHandler) gameHandler).disparoEnemigo(cell, res);
        }
    }

    public void agua() {
        if (gameHandler != null) {
            ((GameHandler) gameHandler).agua();
        }
    }

    public void tocado() {
        if (gameHandler != null) {
            ((GameHandler) gameHandler).tocado();
        }
    }

    public void hundido(Barco barco, Posicion pos) {
        if (gameHandler != null) {
            ((GameHandler) gameHandler).hundido(barco, pos);
        }
    }
}
