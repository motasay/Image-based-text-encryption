package mainpackage.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

class HashGenerator {

   private static final String PROVIDER_SUN   = "SUN";
   private static final String PROVIDER_APPLE = "Apple";
   private static final String SHA1   = "SHA-1";
   private static final String SHA2   = "SHA-256";
   private static final String SHA512 = "SHA-512";

   static byte[] getHash(int keySize, byte[] bytes) throws Exception {
      assert (keySize == 128 || keySize == 256 || keySize == 512);

      switch (keySize) {
         case 128:
            return get128BitHash(bytes);
         case 256:
            return get256BitHash(bytes);
         case 512:
            return get512BitHash(bytes);
         default:
            throw new Exception("Key size not supported: " + keySize);
      }
   }
   
   private static byte[] get128BitHash(byte[] bytes) throws Exception {
      final byte[] hash160 = get160BitHash(bytes);
      final byte[] hash128 = new byte[16]; // truncate it to 16 bytes
      System.arraycopy(hash160, 0, hash128, 0, hash128.length);
      return hash128;
   }

   private static byte[] get160BitHash(byte[] bytes) throws Exception {
      MessageDigest md = null;
      try {
         md = MessageDigest.getInstance(SHA1, PROVIDER_APPLE);
      } catch (NoSuchProviderException ex) {
         try {
            md = MessageDigest.getInstance(SHA1, PROVIDER_SUN);
         }  catch (NoSuchProviderException e) {
            md = MessageDigest.getInstance(SHA1);
         }
      }  catch (NoSuchAlgorithmException ex) {
         throw new Exception("NoSuchAlgorithmException: Could not locate the SHA-1 algorithm to generate a 128-bit key.");
      }
      return md.digest(bytes);
   }

   private static byte[] get256BitHash(byte[] bytes) throws Exception {
      MessageDigest md = null;
      try {
         md = MessageDigest.getInstance(SHA2, PROVIDER_SUN);
      } catch (NoSuchAlgorithmException ex) {
         throw new Exception("NoSuchAlgorithmException: Could not locate the SHA-2 algorithm to generate a 128-bit key.");
      }
      return md.digest(bytes);
   }

   private static byte[] get512BitHash(byte[] bytes) throws Exception {
      MessageDigest md = null;
      try {
         md = MessageDigest.getInstance(SHA512, PROVIDER_SUN);
      } catch (NoSuchAlgorithmException ex) {
         throw new Exception("NoSuchAlgorithmException: Could not locate the SHA-512 algorithm to generate a 128-bit key.");
      }
      return md.digest(bytes);
   }
}
