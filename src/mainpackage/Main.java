package mainpackage;

import mainpackage.ui.GUI;

public class Main {

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) throws Exception {
      java.awt.EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            new GUI().setVisible(true);
         }
      });
   }
}
