package mainpackage.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

class BufferedImageLoader {

   static BufferedImage getBufferedImage(File aFile) throws Exception {
      BufferedImage result = null;

      try {
         result = ImageIO.read(aFile);
      } catch (IOException ex) {
         result = null;
      } catch (OutOfMemoryError e) {
         throw new Exception("The image size is too big for the available memory. Try increasing the maximum heap size (with the JVM arguments -Xms and -Xmx) or use a smaller image.");
      }

      if (result == null) {
         // fallback to the Java Advanced Imaging API
         PlanarImage pi = null;
         try {
            pi = JAI.create("fileload", aFile.getPath());
         } catch (OutOfMemoryError e) {
            throw new Exception("The image size is too big for the available memory. Try increasing the maximum heap size (with the JVM arguments -Xms and -Xmx) or use a smaller image.");
         }

         if (pi != null)
            result = pi.getAsBufferedImage();

         if (result == null)
            throw new Exception("Couldn't read the image at "+ Util.getFileName(aFile.getPath()));
      }

      return result;
   }
}
