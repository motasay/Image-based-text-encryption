package mainpackage.ui;

import java.io.File;
import javax.swing.filechooser.*;
import mainpackage.core.Util;

class ImageFilter extends FileFilter {

   public final static String jpeg = "jpeg";
   public final static String jpg = "jpg";
   public final static String gif = "gif";
   public final static String tiff = "tiff";
   public final static String tif = "tif";
   public final static String png = "png";
   public final static String bmp = "bmp";

   /**
    * Returns true if f is a directory or is a file with a "bmp",
    * "tiff", "tif", "gif", "jpeg", "jpg" or "png" extension, or a file
    * without an extension.
    * @param f
    * @return
    */
   @Override
   public boolean accept(File f) {

      if (f.isDirectory()) {
         return true;
      }

      String extension = Util.getExtension(f);
      if (extension != null) {
         if (extension.equals(bmp)
                 || extension.equals(tiff)
                 || extension.equals(tif)
                 || extension.equals(gif)
                 || extension.equals(jpeg)
                 || extension.equals(jpg)
                 || extension.equals(png)) {
            return true;
         }
         return false;
      }

      return true; // in case the image file has no extension
   }

   @Override
   public String getDescription() {
      return "Image file";
   }
}
