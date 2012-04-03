package batalla.server;

import java.util.ArrayList;

import server.AbstractSaloon;
import server.ServerSessionHandler;
import batalla.common.messages.BatallaProtocolDecoder;

public class BatallaSessionHandler extends ServerSessionHandler {

   public BatallaSessionHandler() {
      super(new BatallaProtocolDecoder());

      salones = new ArrayList<AbstractSaloon>(3);
      salones.add(new BatallaSaloon(0, this));
      salones.add(new BatallaSaloon(1, this));
      salones.add(new BatallaSaloon(2, this));
   }

   @Override
   protected int getCodigoJuego() {
      // batalla = 2 para la base
      return 2;
   }
}
