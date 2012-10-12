package mainpackage.core;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author almutasemalsayed
 */
public class CoordinatorTest {

   /**
    * Test case 1:
    * Goal: Ensure that we can retrieve the plaintext from the ciphertext
    * Input: Plaintext: "Hello, world!", Image: "1024X1024.jpg", keySize: 128, numOfThreads: 2.
    * Operation: Encrypt plainText to get cipherText, then decrypt cipherText to get decryptedText.
    * Expected result: decryptedText equals plainText
    */
   @Test
   public void testCase1() throws Exception {
      System.out.println("testCase1");
      String plainText = "Hello, world!";
      int keySize = 128;
      int numberOfThreads = 2;
      File imageFile = new File(ImageLocator.getPathForImage("1024X1024.jpg"));

      String cipherText = Coordinator.encrypt(plainText, imageFile, keySize, numberOfThreads, null);
      String decryptedText = Coordinator.decrypt(cipherText, imageFile, keySize, numberOfThreads, null);

      assertTrue(decryptedText.equalsIgnoreCase(plainText));
   }

   /**
    * Test case 2:
    * Goal: Ensure that the chosen key size does affect the result
    * Input: Plaintext: "Hello world!", Image: "1024X1024.jpg", keySize: 128, numOfThreads: 2.
    * Operation: Encrypt plainText to get cipherText, then decrypt cipherText with keySize 512 to get decryptedText.
    * Expected result: decryptedText does not equal plainText
    */
   @Test
   public void testCase2() throws Exception {
      System.out.println("testCase2");
      String plainText = "Hello, world!";
      int keySize = 128;
      int numberOfThreads = 2;
      File imageFile = new File(ImageLocator.getPathForImage("1024X1024.jpg"));

      String cipherText = Coordinator.encrypt(plainText, imageFile, keySize, numberOfThreads, null);
      keySize = 512;
      String decryptedText = Coordinator.decrypt(cipherText, imageFile, keySize, numberOfThreads, null);

      assertFalse(decryptedText.equalsIgnoreCase(plainText));
   }

   /**
    * Test case 3:
    * Goal: Ensure that using a different image than that used in the encryption does not give the plaintext
    * Input: Plaintext: "Hello world!", Image: "1024X1024.jpg", keySize: 128, numOfThreads: 2.
    * Operation: Encrypt plainText to get cipherText, then decrypt cipherText with image "1024X1024-1px.jpg" to get decryptedText.
    * Expected result: decryptedText does not equal plainText
    */
   @Test
   public void testCase3() throws Exception {
      System.out.println("testCase3");
      String plainText = "Hello, world!";
      int keySize = 128;
      int numberOfThreads = 2;
      File imageFile = new File(ImageLocator.getPathForImage("1024X1024.jpg"));

      String cipherText = Coordinator.encrypt(plainText, imageFile, keySize, numberOfThreads, null);
      imageFile = new File(ImageLocator.getPathForImage("1024X1024-1px-diff.jpg"));
      String decryptedText = Coordinator.decrypt(cipherText, imageFile, keySize, numberOfThreads, null);

      assertFalse(decryptedText.equalsIgnoreCase(plainText));
   }

   /**
    * Test case 4:
    * Goal: Ensure that encrypting the same plaintext using the same parameters always gives the same ciphertext
    * Input: Plaintext: "Hello world!", Image: "1024X1024.jpg", keySize: 128, numOfThreads: 2.
    * Operation: Encrypt plainText to get cipherText, then encrypt plainText again to get cipherText2.
    * Expected result: cipherText equals cipherText2
    */
   @Test
   public void testCase4() throws Exception {
      System.out.println("testCase4");
      String plainText = "Hello, world!";
      int keySize = 128;
      int numberOfThreads = 2;
      File imageFile = new File(ImageLocator.getPathForImage("1024X1024.jpg"));

      String cipherText = Coordinator.encrypt(plainText, imageFile, keySize, numberOfThreads, null);

      for (int i = 0; i < 10; i++) {
         String cipherText2 = Coordinator.encrypt(plainText, imageFile, keySize, numberOfThreads, null);
         assertTrue(cipherText2.equalsIgnoreCase(cipherText));
      }
   }

   /**
    * Test case 5:
    * Goal: Ensure that decrypting the same ciphertext using the same parameters always returns the same plaintext
    * Input: cipherText: "ff005da0cb", Image: "1024X1024.jpg", keySize: 128, numOfThreads: 2.
    * Operation: decrypt cipherText to get plainText, then decrypt cipherText again to get plainText2.
    * Expected result: plainText equals plainText2
    */
   @Test
   public void testCase5() throws Exception {
      System.out.println("testCase5");
      String cipherText = "ff005da0cb";
      int keySize = 128;
      int numberOfThreads = 2;
      File imageFile = new File(ImageLocator.getPathForImage("1024X1024.jpg"));

      String plainText = Coordinator.decrypt(cipherText, imageFile, keySize, numberOfThreads, null);

      for (int i = 0; i < 10; i++) {
         String plainText2 = Coordinator.decrypt(cipherText, imageFile, keySize, numberOfThreads, null);
         assertTrue(plainText2.equalsIgnoreCase(plainText));
      }
   }

   /**
    * Test case 6:
    * Goal: Ensure that the chosen number of threads does not affect the result
    * Input: Plaintext: "Hello world!", Image: "1024X1024.jpg", keySize: 256, numOfThreads: 1.
    * Operation: Encrypt plainText to get cipherText, then decrypt cipherText with 100 threads again to get cipherText2.
    * Expected result: cipherText2 equals cipherText
    */
   @Test
   public void testCase6() throws Exception {
      System.out.println("testCase6");
      String plainText = "Hello, world!";
      int keySize = 256;
      int numberOfThreads = 1;
      File imageFile = new File(ImageLocator.getPathForImage("1024X1024.jpg"));

      String cipherText = Coordinator.encrypt(plainText, imageFile, keySize, numberOfThreads, null);
      numberOfThreads = 100;
      String decryptedText = Coordinator.decrypt(cipherText, imageFile, keySize, numberOfThreads, null);

      assertTrue(decryptedText.equalsIgnoreCase(plainText));
   }

   /**
    * Test case 7:
    * Goal: Ensure that encrypting the same plaintext with different images gives different ciphertext
    * Input: Plaintext: "Hello world!", Image1: "1024X1024.jpg", Image2: "1024X1024-1px.jpg", keySize: 512, numOfThreads: 2.
    * Operation: encrypt plainText with image1 to get cipherText1, encrypt plainText with image2 to get cipherText2
    * Expected result: cipherText1 does not equal cipherText2
    */
   @Test
   public void testCase7() throws Exception {
      System.out.println("testCase7");
      String plainText = "Hello, world!";
      int keySize = 512;
      int numberOfThreads = 1;

      File image1 = new File(ImageLocator.getPathForImage("1024X1024.jpg"));
      File image2 = new File(ImageLocator.getPathForImage("1024X1024-1px-diff.jpg"));

      String cipherText1 = Coordinator.encrypt(plainText, image1, keySize, numberOfThreads, null);
      String cipherText2 = Coordinator.encrypt(plainText, image2, keySize, numberOfThreads, null);

      assertFalse(cipherText1.equalsIgnoreCase(cipherText2));
   }

   /**
    * Used to ensure that optimization does not affect the logic.
    */
   @Test
   public void encryptDecrypt1() throws Exception {
      System.out.println("encryptDecrypt1");
      String plainText = "Hello, world!";
      String imageFile = ImageLocator.getPathForImage("1024X1024.jpg");

      File image = new File(imageFile);
      int keySize = 128;
      int numberOfThreads = 1;
      String expectedResult = "FEDFCB0C38B3D22165DE179227";

      String result = Coordinator.encrypt(plainText, image, keySize, numberOfThreads, null);
      assertTrue(expectedResult.equalsIgnoreCase(result));

      result = Coordinator.decrypt(result, image, keySize, numberOfThreads, null);
      assertTrue(result.equalsIgnoreCase(plainText));
   }

   /**
    * Used to ensure that optimization does not affect the logic.
    */
   @Test
   public void encryptDecrypt2() throws Exception {
      System.out.println("encryptDecrypt2");
      String plainText = "abcdefghijklmnopqrstuvwxyzABCDEF";
      String imageFile = ImageLocator.getPathForImage("1024X1024.jpg");

      File image = new File(imageFile);
      int keySize = 128;
      int numberOfThreads = 2;

      String expectedResult = "D7D8C40432F9953E63C6109A6B59A88A0DC1F545F0F3FE9250181E84ACF558F1";

      String result = Coordinator.encrypt(plainText, image, keySize, numberOfThreads, null);
      assertTrue(expectedResult.equalsIgnoreCase(result));

      result = Coordinator.decrypt(result, image, keySize, numberOfThreads, null);
      assertTrue(result.equalsIgnoreCase(plainText));
   }
}
