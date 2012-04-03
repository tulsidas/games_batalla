package batalla.common.messages.server;

import org.apache.mina.common.ByteBuffer;

import batalla.common.ifaz.GameHandler;
import batalla.common.ifaz.GameMessage;
import batalla.common.model.Celda;
import batalla.common.model.Resultado;

import common.messages.FixedLengthMessageAdapter;

public class DisparoEnemigoMessage extends FixedLengthMessageAdapter implements
      GameMessage {

   protected Celda cell;

   protected Resultado res;

   public DisparoEnemigoMessage() {
   }

   public DisparoEnemigoMessage(Celda cell, Resultado res) {
      this.cell = cell;
      this.res = res;
   }

   public void execute(GameHandler game) {
      game.disparoEnemigo(cell, res);
   }

   @Override
   public void decode(ByteBuffer buff) {
      cell = Celda.readFrom(buff);
      res = Resultado.readFrom(buff);
   }

   @Override
   protected void encodeContent(ByteBuffer buff) {
      Celda.writeTo(cell, buff);
      Resultado.writeTo(res, buff);
   }

   @Override
   public int getContentLength() {
      return 2;
   }

   @Override
   public byte getMessageId() {
      return (byte) 0x81;
   }
}
