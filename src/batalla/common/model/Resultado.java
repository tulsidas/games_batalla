package batalla.common.model;

import org.apache.mina.common.ByteBuffer;

public enum Resultado {
   AGUA, TOCADO, HUNDIDO;

   public static void writeTo(Resultado res, ByteBuffer buff) {
      if (res == AGUA) {
         buff.put((byte) 0x01);
      }
      else if (res == TOCADO) {
         buff.put((byte) 0x02);
      }
      else {
         buff.put((byte) 0x03);
      }
   }

   public static Resultado readFrom(ByteBuffer buff) {
      byte b = buff.get();
      if (b == 0x01) {
         return AGUA;
      }
      else if (b == 0x02) {
         return TOCADO;
      }
      else {
         return HUNDIDO;
      }
   }
}