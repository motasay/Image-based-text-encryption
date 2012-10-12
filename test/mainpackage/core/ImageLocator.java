package mainpackage.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageLocator {

   private static String projectPath = null;

   static URL getURLForImage(String imageName) throws MalformedURLException {
      if (projectPath == null)
         setProjectPath();
      File imageFile = new File(projectPath + "/test/mainpackage/images/" + imageName);
      return imageFile.toURI().toURL();
   }

   static File getFileForImage(String imageName) {
      if (projectPath == null)
         setProjectPath();
      return new File(projectPath + "/test/mainpackage/images/" + imageName);
   }

   static String getPathForImage(String imageName) {
      if (projectPath == null)
         setProjectPath();
      return projectPath + "/test/mainpackage/images/" + imageName;
   }

   private static void setProjectPath() {
      try {
         projectPath = new File(".").getCanonicalPath();
      } catch (IOException ex) {
         Logger.getLogger(ImageLocator.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
