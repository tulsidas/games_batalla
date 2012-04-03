package batalla.common.messages.server;

import org.apache.mina.common.ByteBuffer;

import batalla.common.ifaz.GameHandler;
import batalla.common.ifaz.GameMessage;
import batalla.common.model.Barco;
import batalla.common.model.Posicion;
import batalla.common.model.Resultado;

import common.messages.TaringaProtocolEncoder;
import common.messages.VariableLengthMessageAdapter;

public class DisparoResultMessage extends
/* FixedLengthMessageAdapter */VariableLengthMessageAdapter implements
        GameMessage {

    private Resultado res;

    private Barco barco;

    private Posicion pos;

    public DisparoResultMessage() {
    }

    public DisparoResultMessage(Resultado res) {
        this.res = res;
    }

    public DisparoResultMessage(Resultado res, Barco barco, Posicion pos) {
        this.res = res;
        this.barco = barco;
        this.pos = pos;
    }

    public void execute(GameHandler game) {
        if (res == Resultado.AGUA) {
            game.agua();
        }
        else if (res == Resultado.TOCADO) {
            game.tocado();
        }
        else if (res == Resultado.HUNDIDO) {
            game.hundido(barco, pos);
        }
    }

    @Override
    public void decode(ByteBuffer buff) {
        res = Resultado.readFrom(buff);

        if (buff.get() == TaringaProtocolEncoder.NON_NULL) {
            barco = Barco.readFrom(buff);
        }

        if (buff.get() == TaringaProtocolEncoder.NON_NULL) {
            pos = Posicion.readFrom(buff);
        }
    }

    @Override
    public byte getMessageId() {
        return (byte) 0x82;
    }

    @Override
    public ByteBuffer encodedContent() {
        ByteBuffer buff = ByteBuffer.allocate(5);

        Resultado.writeTo(res, buff);

        if (barco == null) {
            buff.put(TaringaProtocolEncoder.NULL);
        }
        else {
            buff.put(TaringaProtocolEncoder.NON_NULL);
            Barco.writeTo(barco, buff);
        }

        if (pos == null) {
            buff.put(TaringaProtocolEncoder.NULL);
        }
        else {
            buff.put(TaringaProtocolEncoder.NON_NULL);
            Posicion.writeTo(pos, buff);
        }

        return buff.flip();
    }
}