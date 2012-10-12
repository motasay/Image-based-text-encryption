package mainpackage.core;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

class ImageVectorBuilder {

   static byte[] getImageVector(BufferedImage img, int numOfThreads) throws Exception {
      byte[] imageVector;
      try {
         imageVector = new byte[img.getWidth() * img.getHeight()];
      } catch (OutOfMemoryError e) {
         throw new Exception("Couldn't allocate memory for the image vector. Try increasing the maximum heap size (with the JVM argument and -Xmx) or use a smaller image.");
      }

      if (numOfThreads == 1) {
         buildImageVector(imageVector, img);
      }
      else {
         // a thread can work on at least 1 pixel
         if (numOfThreads > imageVector.length)
            numOfThreads = imageVector.length;
         buildImageVector(imageVector, img, numOfThreads);
      }
      
      return imageVector;
   }

   private static void buildImageVector(byte[] imageVector, BufferedImage image) throws Exception {
      /*
       * Supported types:
       *   TYPE_3BYTE_BGR
       *   TYPE_4BYTE_ABGR
       *   TYPE_4BYTE_ABGR_PRE
       *   TYPE_BYTE_INDEXED
       *   TYPE_BYTE_GRAY
       *   TYPE_INT_ARGB
       *   TYPE_INT_ARGB_PRE
       *   TYPE_INT_BGR
       *   TYPE_INT_RGB
       */
      int imageType = image.getType();

      if (imageType == BufferedImage.TYPE_4BYTE_ABGR || imageType == BufferedImage.TYPE_4BYTE_ABGR_PRE) {
         byte[] imgPixels = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();
         workForFourBytes(imageVector, imgPixels, 0, imageVector.length - 1);
      }
      
      else if (imageType == BufferedImage.TYPE_3BYTE_BGR) {
         byte[] imgPixels = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();
         workForThreeBytes(imageVector, imgPixels, 0, imageVector.length - 1);
      }
      
      else if (imageType == BufferedImage.TYPE_BYTE_INDEXED) {
         // TYPE_BYTE
         byte[] imgPixels = getIndexedRGBPixels(image);
         if (image.getColorModel().hasAlpha())
            workForFourBytes(imageVector, imgPixels, 0, imageVector.length - 1);
         else
            workForThreeBytes(imageVector, imgPixels, 0, imageVector.length - 1);
      }

      else if (imageType == BufferedImage.TYPE_BYTE_GRAY) {
         // TYPE_BYTE
         byte[] imgPixels = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();
         workForOneByte(imageVector, imgPixels, 0, imageVector.length - 1);
      }

      else if (imageType == BufferedImage.TYPE_INT_ARGB || imageType == BufferedImage.TYPE_INT_ARGB_PRE) {
         int[] imagePixels = ((java.awt.image.DataBufferInt) image.getRaster().getDataBuffer()).getData();
         workForIntWithAlpha(imageVector, imagePixels, 0, imageVector.length - 1);
      }

      else if (imageType == BufferedImage.TYPE_INT_BGR || imageType == BufferedImage.TYPE_INT_RGB) {
         int[] imagePixels = ((java.awt.image.DataBufferInt) image.getRaster().getDataBuffer()).getData();
         workForIntWithoutAlpha(imageVector, imagePixels, 0, imageVector.length - 1);
      }
      
      else {
         // image type not supported
         String typeName = null;
         switch (imageType) {
            case BufferedImage.TYPE_BYTE_BINARY:    typeName = "TYPE_BYTE_BINARY"; break;
            case BufferedImage.TYPE_USHORT_555_RGB: typeName = "TYPE_USHORT_555_RGB"; break;
            case BufferedImage.TYPE_USHORT_565_RGB: typeName = "TYPE_USHORT_565_RGB"; break;
            case BufferedImage.TYPE_USHORT_GRAY:    typeName = "TYPE_USHORT_GRAY"; break;
            case BufferedImage.TYPE_CUSTOM:         typeName = "TYPE_CUSTOM"; break;
            default:                                typeName = "UNDEFINED";
         }
         if (imageType == BufferedImage.TYPE_CUSTOM)
            throw new Exception("The selected image cannot be decoded. Please select a different image.");
         else
            throw new Exception("Image type \"" + typeName + " ("+imageType+")\" is not supported.");
      }
   }

   private static void buildImageVector(byte[] imageVector, BufferedImage image, int numOfThreads) throws Exception {

      Thread[] threads = new Thread[numOfThreads];
      
      int imageType = image.getType();
      if (imageType == BufferedImage.TYPE_3BYTE_BGR
              || imageType == BufferedImage.TYPE_4BYTE_ABGR
              || imageType == BufferedImage.TYPE_4BYTE_ABGR_PRE
              || imageType == BufferedImage.TYPE_BYTE_INDEXED) {

         byte[] imgPixels = null;
         if (imageType == BufferedImage.TYPE_BYTE_INDEXED)
            imgPixels = getIndexedRGBPixels(image);
         else
            imgPixels = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();

         boolean hasAlpha = image.getColorModel().hasAlpha();
         int remainingWork = imageVector.length;
         int endRange = -1;
         for (int i = 0; i < numOfThreads; i++) {
            int amountOfWork = remainingWork / (numOfThreads - i);
            remainingWork -= amountOfWork;
            int startRange = endRange + 1;
            endRange   = startRange + amountOfWork - 1;
            threads[i] = new Thread(new PixelTransformer(imgPixels, startRange, endRange, imageVector, hasAlpha));
            threads[i].start();
         }
      }

      else if (imageType == BufferedImage.TYPE_BYTE_GRAY) {
         byte[] imgPixels = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();
         int remainingWork = imageVector.length;
         int endRange = -1;
         for (int i = 0; i < numOfThreads; i++) {
            int amountOfWork = remainingWork / (numOfThreads - i);
            remainingWork -= amountOfWork;
            int startRange = endRange + 1;
            endRange   = startRange + amountOfWork - 1;
            threads[i] = new Thread(new PixelTransformer(imgPixels, startRange, endRange, imageVector));
            threads[i].start();
         }
      }
      else if (imageType == BufferedImage.TYPE_INT_ARGB
              || imageType == BufferedImage.TYPE_INT_ARGB_PRE
              || imageType == BufferedImage.TYPE_INT_BGR
              || imageType == BufferedImage.TYPE_INT_RGB ) {

         int[] imgPixels = ((java.awt.image.DataBufferInt) image.getRaster().getDataBuffer()).getData();
         boolean hasAlpha = imageType == BufferedImage.TYPE_INT_ARGB || imageType == BufferedImage.TYPE_INT_ARGB_PRE;
         int remainingWork = imageVector.length;
         int endRange = -1;
         for (int i = 0; i < numOfThreads; i++) {
            int amountOfWork = remainingWork / (numOfThreads - i);
            remainingWork -= amountOfWork;
            int startRange = endRange + 1;
            endRange   = startRange + amountOfWork - 1;
            threads[i] = new Thread(new PixelTransformer(imgPixels, startRange, endRange, imageVector, hasAlpha));
            threads[i].start();
         }
      }
      else {
         // image type not supported
         String typeName = null;
         switch (imageType) {
            case BufferedImage.TYPE_BYTE_BINARY:    typeName = "TYPE_BYTE_BINARY"; break;
            case BufferedImage.TYPE_USHORT_555_RGB: typeName = "TYPE_USHORT_555_RGB"; break;
            case BufferedImage.TYPE_USHORT_565_RGB: typeName = "TYPE_USHORT_565_RGB"; break;
            case BufferedImage.TYPE_USHORT_GRAY:    typeName = "TYPE_USHORT_GRAY"; break;
            case BufferedImage.TYPE_CUSTOM:         typeName = "TYPE_CUSTOM"; break;
            default:                          typeName = "UNDEFINED";
         }
         if (imageType == BufferedImage.TYPE_CUSTOM)
            throw new Exception("The selected image cannot be decoded. Please select a different image.");
         else
            throw new Exception("Image type \"" + typeName + " ("+imageType+")\" is not supported.");
      }

      // wait for the threads
      try {
         for (int i = 0; i < numOfThreads; i++) {
            threads[i].join();
         }
      } catch (InterruptedException ex) {
         System.out.println(ex);
      }
   }

   private static void workForOneByte(byte[] imageVector, byte[] imagePixels, int startPixel, int endPixel) {
      final int pixelLength = 1;
      int bufferPixel = startPixel;
      final int max = endPixel * pixelLength;
      for (int pixel = startPixel * pixelLength; pixel <= max; pixel += pixelLength) {
         // red, green and blue has the same value, and alpha is 255
         // so to get the sum we multiply the value by 3
         int rgbSum = (imagePixels[pixel] & 0xff) * 3;

         imageVector[bufferPixel++] = (byte) (rgbSum % 255);
      }
   }
   
   private static void workForThreeBytes(byte[] imageVector, byte[] imagePixels, int startPixel, int endPixel) {
      final int pixelLength = 3;
      int bufferPixel = startPixel;
      final int max = endPixel * pixelLength;
      for (int pixel = startPixel * pixelLength; pixel <= max; pixel += pixelLength) {
         int rgbSum = 0;
         for (int i = 0; i < pixelLength; i++) {
            rgbSum += ((int) imagePixels[pixel + i] & 0xff);
         }
         imageVector[bufferPixel++] = (byte) (rgbSum % 255);
      }
   }

   private static void workForFourBytes(byte[] imageVector, byte[] imagePixels, int startPixel, int endPixel) {
      final int pixelLength = 4;
      int bufferPixel = startPixel;
      final int max = endPixel * pixelLength;
      for (int pixel = startPixel * pixelLength; pixel <= max; pixel += pixelLength) {
         int alpha = (int) imagePixels[pixel] & 0xff;
         if (alpha == 0) {
            alpha = 255; // avoid division by 0!
         }
         int rgbSum = 0;
         for (int i = 1; i < pixelLength; i++) { // start from 1 to skip the alpha value
            rgbSum += ((int) imagePixels[pixel + i] & 0xff);
         }

         imageVector[bufferPixel++] = (byte) (rgbSum % alpha);
      }
   }

   private static void workForIntWithAlpha(byte[] imageVector, int[] imagePixels, int startPixel, int endPixel) {
      final int pixelLength = 1;
      int bufferPixel = startPixel;
      final int max = endPixel * pixelLength;
      for (int pixel = startPixel * pixelLength; pixel <= max; pixel += pixelLength) {
         int argb = imagePixels[pixel];
         int alpha = (argb >> 24) & 0xff;
         int red = (argb >> 16) & 0xff;
         int green = (argb >> 8) & 0xff;
         int blue = argb & 0xff;

         if (alpha == 0) {
            alpha = 255; // avoid devision by 0
         }
         int rgbSum = red + green + blue;

         imageVector[bufferPixel] = (byte) (rgbSum % alpha);
         bufferPixel++;
      }
   }

   private static void workForIntWithoutAlpha(byte[] imageVector, int[] imagePixels, int startPixel, int endPixel) {
      final int pixelLength = 1;
      int bufferPixel = startPixel;
      final int max = endPixel * pixelLength;
      final int alpha = 255;
      for (int pixel = startPixel * pixelLength; pixel <= max; pixel += pixelLength) {
         int rgb = imagePixels[pixel];
         int red = rgb >> 16 & 0xff;
         int green = rgb >> 8 & 0xff;
         int blue = rgb & 0xff;

         int rgbSum = red + green + blue;

         imageVector[bufferPixel] = (byte) (rgbSum % alpha);
         bufferPixel++;
      }
   }

   private static byte[] getIndexedRGBPixels(BufferedImage img) {
      byte[] indices = ((java.awt.image.DataBufferByte) img.getRaster().getDataBuffer()).getData();
      int numOfPixels = indices.length;
      ColorModel model = img.getColorModel();
      byte[] rgbs = null;

      if (model.hasAlpha()) {
         rgbs = new byte[numOfPixels * 4];
         int i = 0;
         for (int j = 0; j < numOfPixels; j++) {
            int argb = model.getRGB(indices[j]);
            rgbs[i++] = (byte) ((argb >> 24) & 0xff);
            rgbs[i++] = (byte) ((argb) & 0xff);
            rgbs[i++] = (byte) ((argb >> 8) & 0xff);
            rgbs[i++] = (byte) ((argb >> 16) & 0xff);
         }
      } else {
         rgbs = new byte[numOfPixels * 3];
         int i = 0;
         for (int j = 0; j < numOfPixels; j++) {
            int argb = model.getRGB(indices[j]);
            rgbs[i++] = (byte) ((argb) & 0xff);
            rgbs[i++] = (byte) ((argb >> 8) & 0xff);
            rgbs[i++] = (byte) ((argb >> 16) & 0xff);
         }
      }

      return rgbs;
   }

   private static class PixelTransformer implements Runnable {

      private static enum Type {ONE_BYTE, THREE_BYTES, FOUR_BYTES, INT_ALPHA, INT_NO_ALPHA}

      private byte[] imageVector; // always used regardless of the type and filled with the final values
      private byte[] imageBytePixels; // used for types ONE_BYTE, THREE_BYTES, FOUR_BYTES
      private int[] imageIntPixels; // used for INT_ALPHA and INT_NO_ALPHA
      private int startPixel; // always used regardless of the type
      private int endPixel;   // always used regardless of the type
      private Type type;      // always used

      private PixelTransformer(byte[] imagePixels, int startPixel, int endPixel, byte[] imageVector, boolean hasAlpha) {
         if (hasAlpha)
            this.type = Type.FOUR_BYTES;
         else
            this.type = Type.THREE_BYTES;
         this.imageBytePixels = imagePixels;
         this.startPixel  = startPixel;
         this.endPixel    = endPixel;
         this.imageVector      = imageVector;
      }

      private PixelTransformer(byte[] imagePixels, int startPixel, int endPixel, byte[] imageVector) {
         this.type = Type.ONE_BYTE;
         this.imageBytePixels = imagePixels;
         this.startPixel  = startPixel;
         this.endPixel    = endPixel;
         this.imageVector      = imageVector;
      }

      private PixelTransformer(int[] imagePixels, int startPixel, int endPixel, byte[] imageVector, boolean hasAlpha) {
         if (hasAlpha)
            this.type = Type.INT_ALPHA;
         else
            this.type = Type.INT_NO_ALPHA;
         this.imageIntPixels = imagePixels;
         this.startPixel     = startPixel;
         this.endPixel       = endPixel;
         this.imageVector         = imageVector;
      }

      @Override
      public void run() {
         switch(type) {
            case ONE_BYTE: {
               workForOneByte(imageVector, imageBytePixels, startPixel, endPixel);
               break;
            }
            case THREE_BYTES: {
               workForThreeBytes(imageVector, imageBytePixels, startPixel, endPixel);
               break;
            }
            case FOUR_BYTES: {
               workForFourBytes(imageVector, imageBytePixels, startPixel, endPixel);
               break;
            }
            case INT_ALPHA: {
               workForIntWithAlpha(imageVector, imageIntPixels, startPixel, endPixel);
               break;
            }
            case INT_NO_ALPHA: {
               workForIntWithoutAlpha(imageVector, imageIntPixels, startPixel, endPixel);
               break;
            }
         }
      }

   }
}