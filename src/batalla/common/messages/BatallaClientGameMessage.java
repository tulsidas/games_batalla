package batalla.common.messages;

import org.apache.mina.common.IoSession;

import batalla.common.ifaz.ClientGameMessage;
import batalla.common.ifaz.SaloonHandler;

import common.ifaz.BasicServerHandler;
import common.messages.FixedLengthMessageAdapter;

public abstract class BatallaClientGameMessage extends
      FixedLengthMessageAdapter implements ClientGameMessage {

   public abstract void execute(IoSession session, SaloonHandler salon);

   public void execute(IoSession session, BasicServerHandler serverHandler) {
      execute(session, (SaloonHandler) serverHandler);
   }
}