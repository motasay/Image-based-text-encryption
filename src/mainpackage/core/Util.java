package mainpackage.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class Util {

   private static final String[] hexArray = {
      "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F",
      "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D", "1E", "1F",
      "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F",
      "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E", "3F",
      "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D", "4E", "4F",
      "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F",
      "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6A", "6B", "6C", "6D", "6E", "6F",
      "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E", "7F",
      "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "8A", "8B", "8C", "8D", "8E", "8F",
      "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9A", "9B", "9C", "9D", "9E", "9F",
      "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB", "AC", "AD", "AE", "AF",
      "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF",
      "C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB", "CC", "CD", "CE", "CF",
      "D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF",
      "E0", "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED", "EE", "EF",
      "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF"
   };
   static String getHex(byte[] bytes) {
      StringBuilder hexString = new StringBuilder(bytes.length * 2);
      for (int i = 0; i < bytes.length; i++) {
         hexString.append(hexArray[bytes[i] & 0xff]);
      }
      return hexString.toString();
   }

   // Source: Dave L. http://stackoverflow.com/a/140861/408286
   static byte[] getBytes(String hex) {
      if (hex == null || hex.isEmpty()) {
         return null;
      }
      int len = hex.length();
      byte[] data = new byte[len / 2];
      for (int i = 0; i < len; i += 2) {
         data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                 + Character.digit(hex.charAt(i + 1), 16));
      }
      return data;
   }
   
   public static String getFileName(String path) {
      int index = path.lastIndexOf('/');
      if (index == -1) {
         return null;
      }
      return path.substring(index + 1);
   }

   public static String getExtension(File f) {
      String result = null, path = f.getPath();
      if (path != null) {
         int i = path.lastIndexOf(".");
         if (i != -1) {
            result = path.substring(i+1);
         }
      }
      return result;
   }

   static void saveToFile(byte[] bytes, int numberOfLines) {
      int bytesPerLine = bytes.length / numberOfLines;
      FileWriter fileWriter = null;
      try {
         fileWriter = new FileWriter("keys.txt");
         BufferedWriter buffer = new BufferedWriter(fileWriter);
         for (int i = 0; i < numberOfLines; i++) {
            buffer.write(getHex(Arrays.copyOfRange(bytes, i * bytesPerLine, (i * bytesPerLine) + bytesPerLine)) + "\n");
         }
         buffer.close();
      } catch (IOException ex) {
         System.out.println(ex.getMessage());
      } finally {
         try {
            fileWriter.close();
         } catch (IOException ex) {
            System.out.println(ex.getMessage());
         }
      }
   }

   static String convertToString(long nanoSecs) {
      int hours      = (int) (nanoSecs / 3600000000000.0);
      int minutes    = (int) (nanoSecs / 60000000000.0) % 60;
      int seconds    = (int) (nanoSecs / 1000000000.0) % 60;
      int millisecs  = (int) (nanoSecs / 1000000.0) % 1000;

      return getString(hours, minutes, seconds, millisecs, nanoSecs);
   }

   private static String getString(int hours, int minutes, int seconds, int millisecs, long nanoSecs) {
      if (hours == 0 && minutes == 0 && seconds == 0 && millisecs == 0)
         return nanoSecs + "ns";
      else if(hours == 0 && minutes == 0 && seconds == 0) {
         if (nanoSecs % 1000000 == 0)
            return millisecs + "ms";
         long decimal = Math.round((nanoSecs % 1000000) * 1.0 / 100000.0);
         if (decimal >= 5)
            return (millisecs + 1) + "ms";
         else
            return millisecs + "ms";
      }
      else if (hours == 0 && minutes == 0 && millisecs == 0)
         return seconds + "s";
      else if (hours == 0 && seconds == 0 && millisecs == 0)
         return minutes + "min";
      else if (minutes == 0 && seconds == 0 && millisecs == 0)
         return hours + "h";
      else if (hours == 0 && minutes == 0)
         return seconds + "s " + millisecs + "ms";
      else if (hours == 0 && seconds == 0)
         return minutes + "min " + millisecs + "ms";
      else if (hours == 0 && millisecs == 0)
         return minutes + "min " + seconds + "s";
      else if (minutes == 0 && seconds == 0)
         return hours + "h " + millisecs + "ms";
      else if (minutes == 0 && millisecs == 0)
         return hours + "h " + seconds + "s";
      else if (seconds == 0 && millisecs == 0)
         return hours + "h " + minutes + "min";
      else if (hours == 0)
         return minutes + "min " + seconds + "s " + millisecs + "ms";
      else if (minutes == 0)
         return hours + "h " + seconds + "s " + millisecs + "ms";
      else if (seconds == 0)
         return hours + "h " + minutes + "min " + millisecs + "ms";
      else if (millisecs == 0)
         return hours + "h " + minutes + "min " + seconds + "s";

      return hours + "h " + minutes + "min " + seconds + "s " + millisecs + "ms";
   }

   public static void saveToFile(String text) {

      FileWriter fileWriter = null;
      try {
         fileWriter = new FileWriter("text.txt");
         BufferedWriter buffer = new BufferedWriter(fileWriter);
         buffer.write(text);
         buffer.close();
      } catch (IOException ex) {
         System.out.println(ex.getMessage());
      } finally {
         try {
            fileWriter.close();
         } catch (IOException ex) {
            System.out.println(ex.getMessage());
         }
      }
   }
}
