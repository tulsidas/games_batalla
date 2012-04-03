package batalla.common.messages.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;

import batalla.common.ifaz.ClientGameMessage;
import batalla.common.ifaz.SaloonHandler;
import batalla.common.messages.BatallaClientGameMessage;
import batalla.common.model.Barco;
import batalla.common.model.Posicion;

public class PosicionesBarcoMessage extends BatallaClientGameMessage implements
      ClientGameMessage {

   private Map<Barco, Posicion> map;

   public PosicionesBarcoMessage() {
   }

   public PosicionesBarcoMessage(Map<Barco, Posicion> map) {
      this.map = map;
   }

   public void execute(IoSession session, SaloonHandler salon) {
      salon.setPosicionesBarcos(session, map);
   }

   @Override
   public String toString() {
      return "PosicionesBarcoMessage";
   }

   public int getContentLength() {
      // 5 barcos, 5 posiciones = 5*1 + 5*2 = 15
      return 15;
   }

   @Override
   public void decode(ByteBuffer buff) {
      map = new HashMap<Barco, Posicion>();
      // FIXME hardcodeado el 5
      for (int i = 0; i < 5; i++) {
         Barco b = Barco.readFrom(buff);
         Posicion p = Posicion.readFrom(buff);

         map.put(b, p);
      }
   }

   @Override
   protected void encodeContent(ByteBuffer buff) {
      for (Map.Entry<Barco, Posicion> entry : map.entrySet()) {
         Barco.writeTo(entry.getKey(), buff);
         Posicion.writeTo(entry.getValue(), buff);
      }
   }

   @Override
   public byte getMessageId() {
      return (byte) 0x85;
   }
}
