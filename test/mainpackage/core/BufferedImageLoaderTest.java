package mainpackage.core;

import java.awt.image.BufferedImage;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author almutasemalsayed
 */
public class BufferedImageLoaderTest {


   @Test
   public void testGetBufferedImage1() {
      System.out.println("getBufferedImage1");
      String imageName = "1X1_white_full-alpha.png";
      File aFile = ImageLocator.getFileForImage(imageName);

      BufferedImage result = null;
      try {
         result = BufferedImageLoader.getBufferedImage(aFile);
      } catch (Exception ex) {
         fail("An exception was thrown for the image " + imageName);
      }
      assertTrue(result != null);
   }

   @Test
   public void testGetBufferedImage2() {
      System.out.println("getBufferedImage2");
      String imageName = "395X455";
      File aFile = ImageLocator.getFileForImage(imageName);

      BufferedImage result = null;
      try {
         result = BufferedImageLoader.getBufferedImage(aFile);
      } catch (Exception ex) {
         fail("An exception was thrown for the image " + imageName);
      }
      assertTrue(result != null);
   }

   @Test
   public void testGetBufferedImage3() {
      System.out.println("getBufferedImage3");
      String imageName = "395X455.jpeg";
      File aFile = ImageLocator.getFileForImage(imageName);

      BufferedImage result = null;
      try {
         result = BufferedImageLoader.getBufferedImage(aFile);
      } catch (Exception ex) {
         fail("An exception was thrown for the image " + imageName);
      }
      assertTrue(result != null);
   }

   @Test
   public void testGetBufferedImage4() {
      System.out.println("getBufferedImage4");
      String imageName = "1024-1024-No-alpha.jpg";
      File aFile = ImageLocator.getFileForImage(imageName);

      BufferedImage result = null;
      try {
         result = BufferedImageLoader.getBufferedImage(aFile);
      } catch (Exception ex) {
         fail("An exception was thrown for the image " + imageName);
      }
      assertTrue(result != null);
   }

   @Test
   public void testGetBufferedImage5() {
      System.out.println("getBufferedImage5");
      String imageName = "rose.gif";
      File aFile = ImageLocator.getFileForImage(imageName);

      BufferedImage result = null;
      try {
         result = BufferedImageLoader.getBufferedImage(aFile);
      } catch (Exception ex) {
         fail("An exception was thrown for the image " + imageName);
      }
      assertTrue(result != null);
   }

   @Test
   public void testGetBufferedImage6() {
      System.out.println("getBufferedImage6");
      String imageName = "US-395X455.tif";
      File aFile = ImageLocator.getFileForImage(imageName);

      BufferedImage result = null;
      try {
         result = BufferedImageLoader.getBufferedImage(aFile);
      } catch (Exception ex) {
         System.out.println(ex);
         fail("An exception was thrown for the image " + imageName);
      }
      assertTrue(result != null);
   }

   @Test
   public void testGetBufferedImage7() {
      System.out.println("getBufferedImage7");
      String imageName = "395X455-grayscale.png";
      File aFile = ImageLocator.getFileForImage(imageName);

      BufferedImage result = null;
      try {
         result = BufferedImageLoader.getBufferedImage(aFile);
      } catch (Exception ex) {
         fail("An exception was thrown for the image " + imageName);
      }
      assertTrue(result != null);
   }

}