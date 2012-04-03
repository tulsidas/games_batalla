package batalla.client;

import pulpcore.image.CoreImage;
import pulpcore.sprite.ImageSprite;
import batalla.common.model.Barco;
import batalla.common.model.Celda;
import batalla.common.model.Posicion;

public class BarcoSprite extends ImageSprite {

   private Barco barco;

   private Posicion pos;

   private CoreImage[] imgs;

   private static final int HOTSPOT = 18;

   public BarcoSprite(Barco barco, Posicion pos) {
      super((CoreImage) null, 0, 0);

      this.barco = barco;
      this.pos = pos;
      this.imgs = getImages(barco);

      imgs[0].setHotspot(HOTSPOT, HOTSPOT);
      imgs[1].setHotspot(HOTSPOT, HOTSPOT);

      setPixelLevelChecks(false);

      updatePos();
   }

   /**
    * Actualiza x, y, angle en base a la Posicion
    */
   private void updatePos() {
      x.set(pos.getCelda().getColumna() * TableroGroup.CELDA_X - HOTSPOT);
      y.set(pos.getCelda().getFila() * TableroGroup.CELDA_Y - HOTSPOT);
      if (pos.isHorizontal()) {
         setImage(imgs[0]);
      }
      else {
         setImage(imgs[1]);
      }

      width.set(getImage().getWidth());
      height.set(getImage().getHeight());
   }

   public Celda getCelda() {
      return pos.getCelda();
   }

   public boolean isHorizontal() {
      return pos.isHorizontal();
   }

   public Barco getBarco() {
      return barco;
   }

   public void setCelda(Celda celda) {
      pos.setCelda(celda);
      updatePos();
   }

   public void setHorizontal(boolean horizontal) {
      pos.setHorizontal(horizontal);
      updatePos();
   }

   @Override
   public String toString() {
      return barco + " " + pos;
   }

   public static CoreImage[] getImages(Barco barco) {
      if (barco == Barco.BOTE) {
         return new CoreImage[] { CoreImage.load("imgs/barco2.png"),
               CoreImage.load("imgs/barco2v.png") };
      }
      else if (barco == Barco.SUBMARINO) {
         return new CoreImage[] { CoreImage.load("imgs/barco3.png"),
               CoreImage.load("imgs/barco3v.png") };
      }
      else if (barco == Barco.CRUCERO) {
         return new CoreImage[] { CoreImage.load("imgs/barco4.png"),
               CoreImage.load("imgs/barco4v.png") };
      }
      else if (barco == Barco.ACORAZADO) {
         return new CoreImage[] { CoreImage.load("imgs/barco5.png"),
               CoreImage.load("imgs/barco5v.png") };
      }
      else { // Barco.PORTAAVIONES
         return new CoreImage[] { CoreImage.load("imgs/barco6.png"),
               CoreImage.load("imgs/barco6v.png") };
      }
   }

}
