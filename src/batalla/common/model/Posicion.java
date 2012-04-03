package batalla.common.model;

import org.apache.mina.common.ByteBuffer;

import common.messages.TaringaProtocolEncoder;

public class Posicion {

   private Celda celda;

   private boolean horizontal;

   public Posicion(Celda celda, boolean horizontal) {
      this.celda = celda;
      this.horizontal = horizontal;
   }

   public Celda getCelda() {
      return celda;
   }

   public boolean isHorizontal() {
      return horizontal;
   }

   public void setCelda(Celda celda) {
      this.celda = celda;
   }

   public void setHorizontal(boolean horizontal) {
      this.horizontal = horizontal;
   }

   @Override
   public String toString() {
      return celda + (horizontal ? " horizontal" : " vertical");
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((celda == null) ? 0 : celda.hashCode());
      result = prime * result + (horizontal ? 1231 : 1237);
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Posicion other = (Posicion) obj;
      if (celda == null) {
         if (other.celda != null) {
            return false;
         }
      }
      else if (!celda.equals(other.celda)) {
         return false;
      }
      if (horizontal != other.horizontal) {
         return false;
      }
      return true;
   }

   public static void writeTo(Posicion pos, ByteBuffer buff) {
      Celda.writeTo(pos.celda, buff);
      buff.put(pos.horizontal ? TaringaProtocolEncoder.TRUE
            : TaringaProtocolEncoder.FALSE);
   }

   public static Posicion readFrom(ByteBuffer buff) {
      return new Posicion(Celda.readFrom(buff),
            buff.get() == TaringaProtocolEncoder.TRUE);
   }

}
