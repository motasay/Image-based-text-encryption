package mainpackage.core;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author almutasemalsayed
 */
public class ImageVectorBuilderTest {

   @Test
   public void testGetImageVector1() throws Exception {
      String imgName = "1X1_white_full-alpha.png";
      int numOfThreads = 1;
      byte[] expResult = new byte[] {0};

      BufferedImage image = BufferedImageLoader.getBufferedImage(ImageLocator.getFileForImage(imgName));
      byte[] result = null;
      try {
         result = ImageVectorBuilder.getImageVector(image, numOfThreads);
      } catch (Exception ex) {
         Logger.getLogger(ImageVectorBuilderTest.class.getName()).log(Level.SEVERE, null, ex);
      }
      assertTrue(Arrays.equals(expResult, result));
   }

   @Test
   public void testGetImageVector2() throws Exception {
      String imgName = "1X1_white_full-alpha.png";
      int numOfThreads = 2;
      byte[] expResult = new byte[] {0};
      BufferedImage image = BufferedImageLoader.getBufferedImage(ImageLocator.getFileForImage(imgName));
      byte[] result = null;
      try {
         result = ImageVectorBuilder.getImageVector(image, numOfThreads);
      } catch (Exception ex) {
         Logger.getLogger(ImageVectorBuilderTest.class.getName()).log(Level.SEVERE, null, ex);
      }
      assertTrue(Arrays.equals(expResult, result));
   }

   @Test
   public void testGetImageVector3() throws Exception {
      String imgName = "3X3_white_full-alpha.png";
      int numOfThreads = 1;
      byte[] expResult = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0};

      BufferedImage image = BufferedImageLoader.getBufferedImage(ImageLocator.getFileForImage(imgName));

      byte[] result = null;
      try {
         result = ImageVectorBuilder.getImageVector(image, numOfThreads);
      } catch (Exception ex) {
         Logger.getLogger(ImageVectorBuilderTest.class.getName()).log(Level.SEVERE, null, ex);
      }
      assertTrue(Arrays.equals(expResult, result));
   }

   @Test
   public void testGetImageVector4() throws Exception {
      String imgName = "3X3_white_full-alpha.png";
      int numOfThreads = 3;
      byte[] expResult = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0};

      BufferedImage image = BufferedImageLoader.getBufferedImage(ImageLocator.getFileForImage(imgName));

      byte[] result = null;
      try {
         result = ImageVectorBuilder.getImageVector(image, numOfThreads);
      } catch (Exception ex) {
         Logger.getLogger(ImageVectorBuilderTest.class.getName()).log(Level.SEVERE, null, ex);
      }
      assertTrue(Arrays.equals(expResult, result));
   }

   @Test
   public void testGetImageVector5() throws Exception {
      String imgName = "3X3.png";
      int numOfThreads = 1;
      byte[] expResult = new byte[] {41, 2, 0, 8, 2, 2, 0, 10, 41};

      BufferedImage image = BufferedImageLoader.getBufferedImage(ImageLocator.getFileForImage(imgName));

      byte[] result = null;
      try {
         result = ImageVectorBuilder.getImageVector(image, numOfThreads);
      } catch (Exception ex) {
         Logger.getLogger(ImageVectorBuilderTest.class.getName()).log(Level.SEVERE, null, ex);
      }
      assertTrue(Arrays.equals(expResult, result));
   }

   @Test
   public void testGetImageVector6() throws Exception {
      String imgName = "3X3.png";
      int numOfThreads = 2;
      byte[] expResult = new byte[] {41, 2, 0, 8, 2, 2, 0, 10, 41};

      BufferedImage image = BufferedImageLoader.getBufferedImage(ImageLocator.getFileForImage(imgName));

      byte[] result = null;
      try {
         result = ImageVectorBuilder.getImageVector(image, numOfThreads);
      } catch (Exception ex) {
         Logger.getLogger(ImageVectorBuilderTest.class.getName()).log(Level.SEVERE, null, ex);
      }
      assertTrue(Arrays.equals(expResult, result));
   }

   @Test
   public void testGetImageVector7() throws Exception {
      String imgName = "3X3-1px.png";
      int numOfThreads = 1;
      byte[] expResult = new byte[] {41, 2, 0, 8, 2, 2, 0, 10, 42};

      BufferedImage image = BufferedImageLoader.getBufferedImage(ImageLocator.getFileForImage(imgName));

      byte[] result = null;
      try {
         result = ImageVectorBuilder.getImageVector(image, numOfThreads);
      } catch (Exception ex) {
         Logger.getLogger(ImageVectorBuilderTest.class.getName()).log(Level.SEVERE, null, ex);
      }
      assertTrue(Arrays.equals(expResult, result));
   }

   @Test
   public void testGetImageVector8() throws Exception {
      String imgName = "3X3-1px.png";
      int numOfThreads = 3;
      byte[] expResult = new byte[] {41, 2, 0, 8, 2, 2, 0, 10, 42};

      BufferedImage image = BufferedImageLoader.getBufferedImage(ImageLocator.getFileForImage(imgName));

      byte[] result = null;
      try {
         result = ImageVectorBuilder.getImageVector(image, numOfThreads);
      } catch (Exception ex) {
         Logger.getLogger(ImageVectorBuilderTest.class.getName()).log(Level.SEVERE, null, ex);
      }
      assertTrue(Arrays.equals(expResult, result));
   }

   @Test
   public void testGetImageVector9() throws Exception {
      String imgName = "4X4_half-alpha.png";
      int numOfThreads = 1;
      byte[] expResult = new byte[] {41, 3, 3, 0, 9, 3, 3, 1, 3, 11, 41, 0, 9, 46, 0, 52};

      BufferedImage image = BufferedImageLoader.getBufferedImage(ImageLocator.getFileForImage(imgName));

      byte[] result = null;
      try {
         result = ImageVectorBuilder.getImageVector(image, numOfThreads);
      } catch (Exception ex) {
         Logger.getLogger(ImageVectorBuilderTest.class.getName()).log(Level.SEVERE, null, ex);
      }
      assertTrue(Arrays.equals(expResult, result));
   }

   @Test
   public void testGetImageVector10() throws Exception {

      String imgName = "mixed-alpha.png";
      int numOfThreads = 1;
      byte[] expResult = new byte[] {38, 38, 38, 38,38, 38, 38, 38,38, 38, 38, 38,38, 38, 38, 38};

      BufferedImage image = BufferedImageLoader.getBufferedImage(ImageLocator.getFileForImage(imgName));

      byte[] result = null;
      try {
         result = ImageVectorBuilder.getImageVector(image, numOfThreads);
      } catch (Exception ex) {
         Logger.getLogger(ImageVectorBuilderTest.class.getName()).log(Level.SEVERE, null, ex);
      }
      assertTrue(Arrays.equals(expResult, result));
   }

   @Test
   public void testGetImageVector11() throws Exception {

      String imgName = "mixed-alpha2.png";
      int numOfThreads = 1;
      byte[] expResult = new byte[] {38, 38, 38, 38,38, 38, 38, 38,38, 38, 38, 38,13, 13, 13, 13};

      BufferedImage image = BufferedImageLoader.getBufferedImage(ImageLocator.getFileForImage(imgName));
      byte[] result = null;
      try {
         result = ImageVectorBuilder.getImageVector(image, numOfThreads);
      } catch (Exception ex) {
         Logger.getLogger(ImageVectorBuilderTest.class.getName()).log(Level.SEVERE, null, ex);
      }
      assertTrue(Arrays.equals(expResult, result));
   }

   @Test
   public void testGetImageVector12() throws Exception {

      String imgName = "mixed-alpha2.png";
      int numOfThreads = 3;
      byte[] expResult = new byte[] {38, 38, 38, 38,38, 38, 38, 38,38, 38, 38, 38,13, 13, 13, 13};

      BufferedImage image = BufferedImageLoader.getBufferedImage(ImageLocator.getFileForImage(imgName));

      byte[] result = null;
      try {
         result = ImageVectorBuilder.getImageVector(image, numOfThreads);
      } catch (Exception ex) {
         Logger.getLogger(ImageVectorBuilderTest.class.getName()).log(Level.SEVERE, null, ex);
      }
      assertTrue(Arrays.equals(expResult, result));
   }

}