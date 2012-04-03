package batalla.common.messages.server;

import batalla.common.ifaz.GameHandler;
import batalla.common.ifaz.GameMessage;

import common.messages.FixedLengthMessageAdapter;

public class NewGameMessage extends FixedLengthMessageAdapter implements
      GameMessage {

   public void execute(GameHandler game) {
      game.newGame();
   }

   @Override
   public byte getMessageId() {
      return (byte) 0x84;
   }

   @Override
   public int getContentLength() {
      return 0;
   }
}
