package batalla.common.model;

import org.apache.mina.common.ByteBuffer;

public class Celda {

   private int pos;

   public Celda(int fila, int columna) {
      this((fila - 1) * 10 + columna);
   }

   public Celda(int pos) {
      this.pos = pos;
   }

   // celda: nro entre 1 y 100
   // 01-10: fila 1, col a-j
   // 11-20: fila 2, col a-j
   // 21-30: fila 3, col a-j
   // 31-40: fila 4, col a-j
   // 41-50: fila 5, col a-j
   // 51-60: fila 6, col a-j
   // 61-70: fila 7, col a-j
   // 71-80: fila 8, col a-j
   // 81-90: fila 9, col a-j
   // 91-100: fila 10, col a-j
   public int getFila() {
      return (int) Math.ceil(pos / 10f);
   }

   public int getColumna() {
      return pos % 10 == 0 ? 10 : pos % 10;
   }

   public Celda izq() {
      if (getColumna() > 1) {
         return new Celda(pos - 1);
      }
      return null;
   }

   public Celda der() {
      if (getColumna() < 10) {
         return new Celda(pos + 1);
      }
      return null;
   }

   public Celda arr() {
      if (getFila() > 1) {
         return new Celda(pos - 10);
      }
      return null;
   }

   public Celda aba() {
      if (getFila() < 10) {
         return new Celda(pos + 10);
      }
      return null;
   }

   @Override
   public String toString() {
      return "(" + getFila() + "," + getColumna() + ")"; // "[" + pos + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + pos;
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
      final Celda other = (Celda) obj;
      if (pos != other.pos) {
         return false;
      }
      return true;
   }

   public static void writeTo(Celda c, ByteBuffer buff) {
      buff.put((byte) c.pos);
   }

   public static Celda readFrom(ByteBuffer buff) {
      return new Celda(buff.get());
   }
}
