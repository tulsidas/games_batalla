package batalla.common.model;

import org.apache.mina.common.ByteBuffer;

public enum Barco {
    BOTE(2), SUBMARINO(3), CRUCERO(4), ACORAZADO(5), PORTAAVIONES(6);

    private int largo;

    private Barco(int largo) {
        this.largo = largo;
    }

    public int getLargo() {
        return largo;
    }

    public static void writeTo(Barco barco, ByteBuffer buff) {
        buff.put((byte) barco.getLargo());
    }

    public static Barco readFrom(ByteBuffer buff) {
        byte b = buff.get();
        for (Barco barco : Barco.values()) {
            if ((byte) barco.getLargo() == b) {
                return barco;
            }
        }

        return null;
    }
}
