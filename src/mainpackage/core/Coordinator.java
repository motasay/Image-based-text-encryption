package mainpackage.core;

import java.io.File;

/*
 * The steps that will be performed by this class in both encryption and decryption:
 *    1. Getting the input text bytes
 *    2. Loading the image
 *    3. Building the image vector
 *    4. Generating the keystream
 *    5. Encryption / Decryption
 *    6. Converting the encrypted/decrypted bytes to a string
 */
public class Coordinator {

   private static Timer timer;
   private static int taskNumber;
   
   public static String encrypt(String plainText, File imageFile, int keySize, int numOfThreads, CoreObserver observer) throws Exception {
      timer = new Timer();
      taskNumber = 0;

      boolean thereIsObserver = observer != null;

      // 1. Getting the input text bytes
      timer.start();
      byte[] plainTextBytes = plainText.getBytes("UTF-8");
      timer.end();
      if (thereIsObserver) observer.done(taskNumber++, timer.getLastTime());

      // 2, 3, 4 and 5
      byte[] ciphered = reverse(plainTextBytes, imageFile, keySize, numOfThreads, observer);

      // 6. Converting the bytes to a string
      timer.start();
      String hex = Util.getHex(ciphered);
      timer.end();
      if (thereIsObserver) {
         observer.done(taskNumber++, timer.getLastTime());
         observer.done(timer.toString());
      }

      return hex;
   }

   public static String decrypt(String cipheredText, File imageFile, int keySize, int numOfThreads, CoreObserver observer) throws Exception {
      timer = new Timer();
      taskNumber = 0;

      boolean thereIsObserver = observer != null;

      // 1. Getting the input text bytes
      timer.start();
      byte[] cipheredBytes = Util.getBytes(cipheredText);
      timer.end();
      if (thereIsObserver) observer.done(taskNumber++, timer.getLastTime());

      // Steps 2, 3, 4, 5
      byte[] original = reverse(cipheredBytes, imageFile, keySize, numOfThreads, observer);

      // 6. Converting the bytes to a string
      timer.start();
      String plainText = new String(original, "UTF-8");
      timer.end();
      if (thereIsObserver) {
         observer.done(taskNumber++, timer.getLastTime());
         observer.done(timer.toString());
      }
      
      return plainText;
   }

   private static byte[] reverse(byte[] inputTextBytes, File imageFile, int keySize, int numOfThreads, CoreObserver observer) throws Exception {
      // inputTextBytes might be plain or cipher text.
      boolean thereIsObserver = observer != null;

      // 2. Loading the image
      timer.start();
      java.awt.image.BufferedImage image = BufferedImageLoader.getBufferedImage(imageFile);
      timer.end();
      if (thereIsObserver) observer.done(taskNumber++, timer.getLastTime());

      // 3. Building the image vector
      timer.start();
      byte[] imageVector = ImageVectorBuilder.getImageVector(image, numOfThreads);
      timer.end();
      if (thereIsObserver) observer.done(taskNumber++, timer.getLastTime());

      image.flush();
      image = null;
      
      // 4. Generating the keystream
      // 4.1 Calculate the number of keys needed
      long maxKeys = imageVector.length * 8L;
      int keySizeInBytes = keySize / 8;
      int numOfKeysNeeded = inputTextBytes.length / keySizeInBytes;
      if (inputTextBytes.length % keySizeInBytes != 0)
         numOfKeysNeeded++; // we need one more key for the remainder
      if (numOfKeysNeeded > maxKeys) {
         // No need to regenerate keys that have been generated before
         numOfKeysNeeded = (int) maxKeys;
      }
      // 4.2
      timer.start();
      byte[] keystream = KeystreamGenerator.generateKeystream(imageVector, keySize, numOfKeysNeeded, numOfThreads);
      timer.end();
      if (thereIsObserver) observer.done(taskNumber++, timer.getLastTime());

      
//      Util.saveToFile(keystream, numOfKeysNeeded);


      // 5. Encryption / Decryption
      timer.start();
      for (int i = 0, k = 0; i < inputTextBytes.length; i++, k++) {
         if (k == keystream.length)
            k = 0; // end of keystream. Restart.
         inputTextBytes[i] = (byte) (keystream[k] ^ inputTextBytes[i]);
      }
      timer.end();
      if (thereIsObserver) observer.done(taskNumber++, timer.getLastTime());

      return inputTextBytes;
   }
}
