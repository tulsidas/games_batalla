package batalla.common.messages.client;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;

import batalla.common.ifaz.SaloonHandler;
import batalla.common.messages.BatallaClientGameMessage;
import batalla.common.model.Celda;

public class DisparoMessage extends BatallaClientGameMessage {

   private Celda celda;

   public DisparoMessage() {
   }

   public DisparoMessage(Celda celda) {
      this.celda = celda;
   }

   @Override
   public void execute(IoSession session, SaloonHandler salon) {
      salon.disparo(session, celda);
   }

   @Override
   public String toString() {
      return "Disparo " + celda;
   }

   @Override
   public void decode(ByteBuffer buff) {
      celda = Celda.readFrom(buff);
   }

   @Override
   public void encodeContent(ByteBuffer buff) {
      Celda.writeTo(celda, buff);
   }

   @Override
   public int getContentLength() {
      // byte de la celda
      return 1;
   }

   @Override
   public byte getMessageId() {
      return (byte) 0x80;
   }
}
