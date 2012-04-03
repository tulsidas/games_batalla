package batalla.common.messages;

import batalla.common.messages.client.DisparoMessage;
import batalla.common.messages.client.PosicionesBarcoMessage;
import batalla.common.messages.server.DisparoEnemigoMessage;
import batalla.common.messages.server.DisparoResultMessage;
import batalla.common.messages.server.FinJuegoMessage;
import batalla.common.messages.server.NewGameMessage;
import batalla.common.messages.server.StartGameMessage;

import common.messages.TaringaProtocolDecoder;

public class BatallaProtocolDecoder extends TaringaProtocolDecoder {

   public BatallaProtocolDecoder() {
      classes.put(new DisparoMessage().getMessageId(), DisparoMessage.class);
      classes.put(new DisparoEnemigoMessage().getMessageId(),
            DisparoEnemigoMessage.class);
      classes.put(new DisparoResultMessage().getMessageId(),
            DisparoResultMessage.class);
      classes.put(new FinJuegoMessage().getMessageId(), FinJuegoMessage.class);
      classes.put(new NewGameMessage().getMessageId(), NewGameMessage.class);
      classes.put(new PosicionesBarcoMessage().getMessageId(),
            PosicionesBarcoMessage.class);
      classes.put(new StartGameMessage().getMessageId(),
            StartGameMessage.class);
      
   }
}
