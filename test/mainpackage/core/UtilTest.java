package mainpackage.core;

import java.util.Arrays;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author almutasemalsayed
 */
public class UtilTest {

   @Test
   public void testGetFileName1() {
      System.out.println("getFileName1");
      String path = "/a/a/a/abcd.exe";
      String expResult = "abcd.exe";
      String result = Util.getFileName(path);
      assertEquals(expResult, result);
   }

   @Test
   public void testGetFileName2() {
      System.out.println("getFileName2");
      String path = "/a/a/a/abcd";
      String expResult = "abcd";
      String result = Util.getFileName(path);
      assertEquals(expResult, result);
   }

   @Test
   public void testGetExtensionFromFile1() {
      System.out.println("getExtensionFromFile1");
      File f = ImageLocator.getFileForImage("395X455.jpeg");
      String expResult = "jpeg";
      String result = Util.getExtension(f);
      assertEquals(expResult, result);
   }

   @Test
   public void testGetExtensionFromFile2() {
      System.out.println("getExtensionFromFile2");
      File f = ImageLocator.getFileForImage("rose.gif");
      String expResult = "gif";
      String result = Util.getExtension(f);
      assertEquals(expResult, result);
   }

   @Test
   public void testGetExtensionFromFile3() {
      System.out.println("getExtensionFromFile3");
      File f = ImageLocator.getFileForImage("395X455");
      String result = Util.getExtension(f);
      assertNull(result);
   }

   /**
    * Test of getHex method, of class Util.
    */
   @Test
   public void testGetHex() {
      System.out.println("getHex");
      byte[] raw = {0, -1, 1};
      String expResult = "00FF01";
      String result = Util.getHex(raw);
      assertEquals(expResult, result);
   }

   /**
    * Test of getBytes method, of class Util.
    */
   @Test
   public void testGetBytes() {
      System.out.println("getBytes");
      String hex = "00FF01";
      byte[] expected = {0, -1, 1};
      byte[] result = Util.getBytes(hex);
      assertTrue("Expected: " + Arrays.toString(expected) + "\nFound: " + Arrays.toString(result), Arrays.equals(expected, result));
   }


   @Test
   public void testConvertToString_long1() {
      System.out.println("testConvertToString_long1");
      long nanoSecs = 100009000000L;
      String expResult = "1min 40s 9ms";
      String result = Util.convertToString(nanoSecs);
      assertEquals(expResult, result);
   }

   @Test
   public void testConvertToString_long2() {
      System.out.println("testConvertToString_long2");
      long nanoSecs = 360032488000000L;
      String expResult = "100h 32s 488ms";
      String result = Util.convertToString(nanoSecs);
      assertEquals(expResult, result);
   }

   @Test
   public void testConvertToString_long3() {
      System.out.println("testConvertToString_long3");
      long nanoSecs = 488000000L;
      String expResult = "488ms";
      String result = Util.convertToString(nanoSecs);
      assertEquals(expResult, result);
   }

   @Test
   public void testConvertToString_long4() {
      System.out.println("testConvertToString_long4");
      long nanoSecs = 400000L;
      String expResult = "400000ns";
      String result = Util.convertToString(nanoSecs);
      assertEquals(expResult, result);
   }

   @Test
   public void testConvertToString_long5() {
      System.out.println("testConvertToString_long5");
      long nanoSecs = 2000000000L;
      String expResult = "2s";
      String result = Util.convertToString(nanoSecs);
      assertEquals(expResult, result);
   }

   @Test
   public void testConvertToString_long6() {
      System.out.println("testConvertToString_long6");
      long nanoSecs = 120000000000L;
      String expResult = "2min";
      String result = Util.convertToString(nanoSecs);
      assertEquals(expResult, result);
   }

   @Test
   public void testConvertToString_long7() {
      System.out.println("testConvertToString_long7");
      long nanoSecs = Long.MAX_VALUE;
      String expResult = "2562047h 47min 7s 647ms"; // 2562047h 47min 7s 647ms
      String result = Util.convertToString(nanoSecs);
      assertEquals(expResult, result);
   }

   @Test
   public void testConvertToString_long8() {
      System.out.println("testConvertToString_long8");
      long nanoSecs = Integer.MAX_VALUE;
      String expResult = "2s 147ms";
      String result = Util.convertToString(nanoSecs);
      assertEquals(expResult, result);
   }

   @Test
   public void testConvertToString_long9() {
      System.out.println("testConvertToString_long9");
      long nanoSecs = 9973376;
      String expResult = "10ms";
      String result = Util.convertToString(nanoSecs);
      assertEquals(expResult, result);
   }

   @Test
   public void testConvertToString_long10() {
      System.out.println("testConvertToString_long10");
      long nanoSecs = 9873376;
      String expResult = "10ms";
      String result = Util.convertToString(nanoSecs);
      assertEquals(expResult, result);
   }

   @Test
   public void testConvertToString_long11() {
      System.out.println("testConvertToString_long11");
      long nanoSecs = 9453376;
      String expResult = "10ms";
      String result = Util.convertToString(nanoSecs);
      assertEquals(expResult, result);
   }

   @Test
   public void testConvertToString_long12() {
      System.out.println("testConvertToString_long12");
      long nanoSecs = 9443376;
      String expResult = "9ms";
      String result = Util.convertToString(nanoSecs);
      assertEquals(expResult, result);
   }

   @Test
   public void testConvertToString_long13() {
      System.out.println("testConvertToString_long13");
      long nanoSecs = 9445376;
      String expResult = "9ms";
      String result = Util.convertToString(nanoSecs);
      assertEquals(expResult, result);
   }
}