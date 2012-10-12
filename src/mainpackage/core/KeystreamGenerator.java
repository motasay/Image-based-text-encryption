package mainpackage.core;

import java.util.Arrays;

class KeystreamGenerator {

   public static byte[] generateKeystream(final byte[] imageVector, final int keySize, final int numOfKeys, int numOfThreads) throws Exception {

      byte[] keystream = null;
      try {
         keystream = new byte[numOfKeys * (keySize / 8)];
      } catch (OutOfMemoryError e) {
         throw new Exception("Couldn't allocate memory for the keystream. Try increasing the maximum heap size (with the JVM argument and -Xmx) or process the text in smaller chuncks.");
      }

      if (numOfThreads == 1) {
         // Generate the first key.
         byte[] aKey = generateKey(imageVector, keySize);
         // Add it to the keystream
         System.arraycopy(aKey, 0, keystream, 0, aKey.length);
         // Generate the remaining keys
         for (int i = 1; i < numOfKeys; i++) {
            shiftBitsRight(imageVector, 1);
            aKey = generateKey(imageVector, keySize);
            System.arraycopy(aKey, 0, keystream, i * aKey.length, aKey.length);
         }
      }

      else {
         if (numOfThreads > numOfKeys) {
            numOfThreads = numOfKeys; // each key can be generated by exactly one thread
         }

         // Each Thread will copy the imageVector, so make sure the number of threads
         // is small enough to be able to make copies without generating memory errors

         // collect garbage to make accurate estimates
         System.gc();

         Runtime runtime = Runtime.getRuntime();
         long usedMemory = runtime.totalMemory() - runtime.freeMemory();
         long availableMemory = runtime.maxMemory() - usedMemory;
         int maxNumOfThreads  = (int) (availableMemory / imageVector.length + 1);
         if (numOfThreads > maxNumOfThreads) {
            System.out.println("Changing num of thread from " + numOfThreads + " to " + maxNumOfThreads);
            numOfThreads = maxNumOfThreads;
         }

         Thread[] threads = new Thread[numOfThreads];

         int remainingWork = numOfKeys;
         int endRange = -1;
         for (int i = 0; i < numOfThreads; i++) {
            // num of threads that haven't started = numOfThreads - i
            int amountOfWork = remainingWork / (numOfThreads - i);
            remainingWork -= amountOfWork;
            int startRange = endRange + 1;
            endRange   = startRange + amountOfWork - 1;
            byte[] imgVector = null;
            if (i + 1 != numOfThreads) {
               try {
                  imgVector = Arrays.copyOf(imageVector, imageVector.length);
               } catch (OutOfMemoryError e) {
                  throw new Exception("Couldn't allocate memory for a copy of the image vector.\nTry increasing the maximum heap size (with the JVM arguments -Xms and -Xmx) or use a smaller image.");
               }
            } else {
               imgVector = imageVector;
            }
            threads[i] = new Thread(new KeyBuilder(keystream, imgVector, keySize, startRange, endRange));
            threads[i].start();
         }

         // wait for the threads
         try {
            for (int i = 0; i < numOfThreads; i++) {
               threads[i].join();
            }
         } catch (InterruptedException ex) {
            System.err.println(ex);
         }
      }
      
      return keystream;
   }

   private static byte[] generateKey(final byte[] imageVector, final int keySize) throws Exception {

      // 1. hash imageVector
      final byte[] hash = HashGenerator.getHash(keySize, imageVector);

      // 2. XOR imageVector with the hash
      XOR(hash, imageVector);

      return hash;
   }

   private static void XOR(byte[] hash, byte[] imageVector) {

      for (int i = 0, h = 0; i < imageVector.length; i++, h++) {
         if (h == hash.length)
            h = 0;
         hash[h] = (byte) (hash[h] ^ imageVector[i]);
      }

   }

   private static void shiftBitsRight(byte[] bytes, final int numOfShifts) {
      
      if (numOfShifts == 0)
         return;

      final int MAX_BIT_SHIFTS = 8;
      if (numOfShifts >= MAX_BIT_SHIFTS)
         rotateArray(bytes, numOfShifts / MAX_BIT_SHIFTS);

      if (numOfShifts >= MAX_BIT_SHIFTS && numOfShifts % MAX_BIT_SHIFTS == 0)
         // rotating was enough, no bitwise shift needed
         return;

      final int RIGHT_SHIFTS = numOfShifts % MAX_BIT_SHIFTS;
      final int LEFT_SHIFTS = MAX_BIT_SHIFTS - RIGHT_SHIFTS;
      // bytes[1] = bytes[0] << 7 | bytes[1] >> 1

      // 1. make the last bit in the last byte the first bit in the first byte
      byte previousByte = bytes[0]; // keep the byte before modification
      bytes[0] = (byte) ((bytes[bytes.length-1] << LEFT_SHIFTS) | ((bytes[0] & 0xff) >> RIGHT_SHIFTS));

      // 2. shift the remaining bits
      byte tmp;
      for (int i = 1; i < bytes.length; i++) {
         tmp = bytes[i];
         bytes[i] = (byte) ((previousByte << LEFT_SHIFTS) | ((bytes[i] & 0xff) >> RIGHT_SHIFTS));
         previousByte = tmp;
      }
   }

   // Source: Jon Skeet http://stackoverflow.com/a/9635461/408286
   private static void rotateArray(byte[] bytes, int numOfRotations) {
      int stepSize = (numOfRotations > bytes.length ? numOfRotations % bytes.length : numOfRotations);
      if (stepSize == 0)
         return;
      else if (stepSize < 0)
         stepSize = bytes.length - (-1*stepSize); // convert it to right rotation

      byte[] tmp = new byte[stepSize];
      // Copy the last stepSize elements of bytes into tmp
      System.arraycopy(bytes, bytes.length - stepSize, tmp, 0, stepSize);
      // Move the elements of bytes stepSize to the right
      System.arraycopy(bytes, 0, bytes, stepSize, bytes.length - stepSize);
      // Copy the elements of tmp back into the first stepSize elements of bytes
      System.arraycopy(tmp, 0, bytes, 0, stepSize);
   }

   private static class KeyBuilder implements Runnable {

      private byte[] keystream;
      private byte[] imageVector;
      private int startKeyIndex, endKeyIndex, keySize;

      KeyBuilder(byte[] keystream, byte[] vector, int keySize, int startIndex, int endIndex) {
         this.keystream = keystream;
         this.imageVector = vector;
         this.keySize = keySize;
         this.startKeyIndex = startIndex;
         this.endKeyIndex = endIndex;
      }

      @Override
      public void run() {
         try {
            shiftBitsRight(imageVector, startKeyIndex);
            byte[] aKey = generateKey(imageVector, keySize);
            int bytesPerKey = aKey.length;
            System.arraycopy(aKey, 0, keystream, startKeyIndex*bytesPerKey, bytesPerKey);
            for (int keyNum = startKeyIndex + 1; keyNum <= endKeyIndex; keyNum++) {
               shiftBitsRight(imageVector, 1);
               aKey = generateKey(imageVector, keySize);
               System.arraycopy(aKey, 0, keystream, keyNum * bytesPerKey, bytesPerKey);
            }
         } catch (Exception e) {
            System.err.println(e);
         }
      }
   }
}
