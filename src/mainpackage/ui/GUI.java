package mainpackage.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.Document;
import mainpackage.core.Coordinator;
import mainpackage.core.CoreObserver;
import mainpackage.core.Util;



public class GUI extends JFrame {

   
   /**
    * Main actions
    */
   private void encrypt() {
      disableInteraction();
      resetTimes();

      cipherTextArea.setText("");
      final String inputText = plainTextArea.getText();
      final int keySize      = Integer.parseInt((String)keySizeComboBox.getSelectedItem());
      final int numOfThreads = Integer.parseInt(numberOfThreadsField.getText());
      final File imageFile   = lastChosenImage;
      timeMeasurementsTable.setRowSelectionInterval(0, 0);
      new Thread(new Runnable() {
         @Override
         public void run() {
            String output = null;
            String problem = null;
            try {
               output = Coordinator.encrypt(inputText, imageFile, keySize, numOfThreads, new CoreObserverImpl());
            } catch (Exception ex) {
               resetTimes();
               problem = ex.getMessage();
            }
            if (output != null) {
//               Util.saveToFile(output);
               cipherTextArea.setText(output);
            } else if (problem != null) {
               displayError(problem, "Error");
            } else {
               displayError("Unknown Error.", "Error");
            }

            enableInteraction();
         }

      }).start();
   }

   private void decrypt() {
      disableInteraction();
      resetTimes();

      plainTextArea.setText("");
      final String inputText = cipherTextArea.getText();
      final int keySize      = Integer.parseInt((String)keySizeComboBox.getSelectedItem());
      final int numOfThreads = Integer.parseInt(numberOfThreadsField.getText());
      final File imageFile   = lastChosenImage;
      timeMeasurementsTable.setRowSelectionInterval(0, 0);
      
      new Thread(new Runnable() {
         @Override
         public void run() {
            String output = null;
            String problem = null;
            try {
               output = Coordinator.decrypt(inputText, imageFile, keySize, numOfThreads, new CoreObserverImpl());
            } catch (Exception ex) {
               resetTimes();
               problem = ex.getMessage();
            }
            if (output != null) {
//               Util.saveToFile(output);
               plainTextArea.setText(output);
            } else if (problem != null) {
               displayError(problem, "Error");
            } else {
               displayError("Unknown Error.", "Error");
            }

            enableInteraction();
         }

      }).start();
   }

   private void openFileChooser() {

       String projectPath = null;
       try {
            projectPath = new File(".").getCanonicalPath();
       } catch (IOException ex) {
          // that's fine, set the current directory to null
       }

       if (projectPath != null)
       {
          File file = new File(projectPath);
          fileChooser.setCurrentDirectory(file);
       }
       else
       {
          fileChooser.setCurrentDirectory(null);
       }
       int returnVal = fileChooser.showOpenDialog(this);
       if (returnVal == JFileChooser.APPROVE_OPTION) {
          lastChosenImage = fileChooser.getSelectedFile();
          // get the image pixels and then set the icon
          new Thread(new Runnable() {
            @Override
            public void run() {
               setImagePixels(fileChooser.getSelectedFile());
               setThumbImageIcon(numberOfPixels, fileChooser.getSelectedFile());
            }
          }).start();
       }
   }


   
   /*
    *   Event handlers / Utility classes
    */
   // To listen for:
   // - selectImageButton
   // - encryptButton
   // - decryptButton
   // - keySizeComboBox
   private class MyActionsListener implements ActionListener {

      @Override
      public void actionPerformed(ActionEvent a) {
         if (a.getSource().equals(selectImageButton)) {
            openFileChooser();
         }
         else if(a.getSource().equals(encryptButton)) {
            if (inputsAreValid(ENCRYPT)) {
               encrypt();
            }
         }
         else if(a.getSource().equals(decryptButton)) {
            if (inputsAreValid(DECRYPT)) {
               decrypt();
            }
         }
         else if(a.getSource().equals(keySizeComboBox)) {
            calculateKeySizeInBytes();
            calculatePeriod();
            calculateNumOfKeysNeeded();
            calculateNumOfThreadsForKeystreamGeneration();
         }
      }
   }

   // To listen for changes in:
   //  - plainTextArea
   //  - cipherTextArea
   //  - numOfThreadsField
   private class TextListener implements DocumentListener {

      @Override
      public void changedUpdate(DocumentEvent e) {
         if (e.getDocument().equals(plainTextArea.getDocument())) {
            textAreaChanged(e.getDocument());
         }
         else if (e.getDocument().equals(cipherTextArea.getDocument())) {
            textAreaChanged(e.getDocument());
         }
         else
            numberOfThreadsFieldChanged();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
         if (e.getDocument().equals(plainTextArea.getDocument())) {
            textAreaChanged(e.getDocument());
         }
         else if (e.getDocument().equals(cipherTextArea.getDocument())) {
            textAreaChanged(e.getDocument());
         }
         else
            numberOfThreadsFieldChanged();
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
         if (e.getDocument().equals(plainTextArea.getDocument())) {
            textAreaChanged(e.getDocument());
         }
         else if (e.getDocument().equals(cipherTextArea.getDocument())) {
            textAreaChanged(e.getDocument());
         }
         else
            numberOfThreadsFieldChanged();
      }

      private void textAreaChanged(Document d) {
         if (d.equals(plainTextArea.getDocument()) && !plainTextArea.getText().isEmpty() && cipherTextArea.isEnabled()) {
            SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                  cipherTextArea.setText("");
               }
            });
         } else if (d.equals(cipherTextArea.getDocument()) && !cipherTextArea.getText().isEmpty() && plainTextArea.isEnabled()) {
            SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                  plainTextArea.setText("");
               }
            });
         }

         if (plainTextArea.isEnabled() || cipherTextArea.isEnabled()) {
            calculateTextBytesLength();
            calculateNumOfKeysNeeded();
            calculateNumOfThreadsForKeystreamGeneration();
         }
      }

      private void numberOfThreadsFieldChanged() {
         calculateNumOfThreadsForImageVectorBuilding();
         calculateNumOfThreadsForKeystreamGeneration();
      }
   }

   // To get time measurements from Coordinator
   private class CoreObserverImpl implements CoreObserver {

      @Override
      public void done(int taskNumber, String time) {
         setTimeForRow(taskNumber, time);
      }

      @Override
      public void done(String time) {
         setTimeTotal(time);
      }

   }

   

   /**
    * Misc. Helpers
    */
   private boolean inputsAreValid(String task) {
      // If an image has not been selected and the input was empty
      // notify then return false
      boolean taskIsEncrypt = task.equalsIgnoreCase(ENCRYPT);
      boolean textIsEmpty = (taskIsEncrypt ? plainTextArea.getText().isEmpty() : cipherTextArea.getText().isEmpty());
      boolean imageHasBeenSelected = lastChosenImage != null;

      // Check text and image
      if (!imageHasBeenSelected && textIsEmpty) {
         String message = "Please insert some text and select an image.";
         displayError(message, "Invalid Input");
         return false;
      }

      // Check image only
      if (!imageHasBeenSelected) {
         String aTask = "";
         if (taskIsEncrypt)
            aTask = "encrypting";
         else
            aTask = "decrypting";
         String message = "Please select an image to be used for " + aTask + " the text.";
         displayError(message, "Invalid Input");
         return false;
      }

      // Check text only
      if (textIsEmpty) {
         String message = "Please insert some text to " + task + ".";
         displayError(message, "Invalid Input");
         return false;
      }

      // Validate number of threads
      try {
          int numOfThreads = Integer.parseInt(numberOfThreadsField.getText());
          if (numOfThreads <= 0) {
             String message = "The number of threads is invalid. Please enter a number greater than 0.";
             displayError(message, "Invalid Input");
             return false;
          }
       } catch (NumberFormatException ex) {
          String message = "The number of threads is invalid. Please only enter numbers greater than 0, without any characters.";
          displayError(message, "Invalid Input");
          return false;
       }

      // In case of decryption: check that the input is a sequence of hex characters.
      // and the number of the characters is a multiple of 2.
      if (!taskIsEncrypt) {
         String input = cipherTextArea.getText();
         if (!input.matches("[0-9A-Fa-f]+")) {
            String message = "The system can only decrypt hexadecimal values (Characters between A-F and numbers).";
            displayError(message, "Invalid Input");
            return false;
         }
         else if(input.length() % 2 != 0) {
            String message = "The number of hexadecimal values must be a multiple of 2.";
            displayError(message, "Invalid Input");
            return false;
         }
      }
      
      return true;
   }

   private void disableInteraction() {
      selectImageButton.setEnabled(false);
      encryptButton.setEnabled(false);
      decryptButton.setEnabled(false);
      keySizeComboBox.setEnabled(false);
      numberOfThreadsField.setEnabled(false);
      plainTextArea.setEnabled(false);
      cipherTextArea.setEnabled(false);
   }

   private void enableInteraction() {
      selectImageButton.setEnabled(true);
      encryptButton.setEnabled(true);
      decryptButton.setEnabled(true);
      keySizeComboBox.setEnabled(true);
      numberOfThreadsField.setEnabled(true);
      plainTextArea.setEnabled(true);
      cipherTextArea.setEnabled(true);
   }

   private void setImagePixels(final File file) {
      String suffix = Util.getExtension(file);
      if (suffix != null) {
         Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
         if (iter.hasNext()) {
            ImageReader reader = iter.next();
            try {
               ImageInputStream stream = new FileImageInputStream(file);
               reader.setInput(stream);
               int width = reader.getWidth(reader.getMinIndex());
               int height = reader.getHeight(reader.getMinIndex());
               numberOfPixels = width * height;
               calculatePeriod();
               calculateNumOfThreadsForImageVectorBuilding();
               calculateNumOfThreadsForKeystreamGeneration();
            } catch (IOException e) {
               numberOfPixels = -1;
            } finally {
               reader.dispose();
            }
         }
         else {
            numberOfPixels = -1; // couldn't find a reader for the suffix
            updateFactsTable();
         }
      }
      else {
         numberOfPixels = -1; // it has no suffix
         updateFactsTable();
      }
   }

   private void setThumbImageIcon(final int numberOfPixels, final File file) {
      java.awt.EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            imageThumbnail.setText("");
            imageThumbnail.setIcon(null);
            try {
               ImageIcon icon = (ImageIcon) getImageIcon(numberOfPixels, file);
               if (icon != null) {
                  imageThumbnail.setIcon(icon);
               } else {
                  imageThumbnail.setText(Util.getFileName(file.getAbsolutePath()));
               }
            } catch (IOException ex) {
               imageThumbnail.setText(Util.getFileName(file.getAbsolutePath()));
            }
            imageThumbnail.setVisible(true);
         }
      });
   }

   private Icon getImageIcon(int numOfPixels, File file) throws IOException {

      if (numOfPixels < 5000000) { // Average loading time  : 380ms
         BufferedImage image = ImageIO.read(file);
         if (image == null || image.getType() == BufferedImage.TYPE_CUSTOM)
            return null; // unsopprted format
         BufferedImage resizedImage = new BufferedImage(imageThumbnail.getWidth(), imageThumbnail.getHeight(), image.getType());
         Graphics2D g = resizedImage.createGraphics();
         g.drawImage(image, 0, 0, imageThumbnail.getWidth(), imageThumbnail.getHeight(), null);
         g.dispose();

         return new ImageIcon(resizedImage);
      }

      return null;
   }

   private void displayError(String message, String title) {
      Toolkit.getDefaultToolkit().beep();
      JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
   }




   /**
    * Table setters
    */
   private void updateFactsTable() {
      DecimalFormat format = new DecimalFormat("###,###,###,###");
      if (numberOfPixels == -1) {
         factsTable.getModel().setValueAt("N/A", 0, 1);
         factsTable.getModel().setValueAt("N/A", 3, 1);
         factsTable.getModel().setValueAt("N/A", 4, 1);
         factsTable.getModel().setValueAt("N/A", 5, 1);
      }
      else {
         factsTable.getModel().setValueAt(format.format(numberOfPixels), 0, 1);
         factsTable.getModel().setValueAt(format.format(keystreamPeriod), 3, 1);
         factsTable.getModel().setValueAt(format.format(numOfThreadsForImageVectorBuilding), 4, 1);
         factsTable.getModel().setValueAt(format.format(numOfThreadsForKeystreamGeneration), 5, 1);
      }
      factsTable.getModel().setValueAt(format.format(textBytesLength), 1, 1);
      factsTable.getModel().setValueAt(format.format(numOfKeysNeeded), 2, 1);
   }

   private void setTimeForRow(final int row, final String time) {
      java.awt.EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            timeMeasurementsTable.getModel().setValueAt(time, row, 1);
            timeMeasurementsTable.setRowSelectionInterval(row+1, row+1);
         }
      });
   }

   private void setTimeTotal(final String time) {
      java.awt.EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            timeMeasurementsTable.getModel().setValueAt(time, getDefaultValuesForTimeMeasurementsTable().length-1, 1);
         }
      });
   }

   private void resetTimes() {
      java.awt.EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            String[][] times = getDefaultValuesForTimeMeasurementsTable();
            for (int i = 0; i < times.length; i++) {
               timeMeasurementsTable.getModel().setValueAt("", i, 1);
            }
            timeMeasurementsTable.clearSelection();
         }
      });
   }



   /**
    * Facts calculations methods
    */
   private void calculateTextBytesLength() {
      if (cipherTextArea.getText().isEmpty())
         textBytesLength = plainTextArea.getText().getBytes().length;
      else if (plainTextArea.getText().isEmpty())
         textBytesLength = cipherTextArea.getText().getBytes().length / 2;
      else
         textBytesLength = 0;
      updateFactsTable();
   }

   private void calculateNumOfKeysNeeded() {
      numOfKeysNeeded = textBytesLength / keySizeInBytes;
      if (textBytesLength % keySizeInBytes != 0)
         numOfKeysNeeded++;
      updateFactsTable();
   }

   private void calculatePeriod() {
//      if (numberOfPixels == 0)
//         keystreamPeriod = 0L;
//      else
      keystreamPeriod = (8L * keySizeInBytes) * numberOfPixels;
      updateFactsTable();
   }

   private void calculateNumOfThreadsForImageVectorBuilding() {
      numOfThreadsForImageVectorBuilding = 0;
      try {
         numOfThreadsForImageVectorBuilding = Integer.parseInt(numberOfThreadsField.getText());
         if (numOfThreadsForImageVectorBuilding > numberOfPixels)
            numOfThreadsForImageVectorBuilding = numberOfPixels;
      }
      catch (NumberFormatException ex) {
      }
      updateFactsTable();
   }

   private void calculateNumOfThreadsForKeystreamGeneration() {
      numOfThreadsForKeystreamGeneration = 0;
      try {
         numOfThreadsForKeystreamGeneration = Integer.parseInt(numberOfThreadsField.getText());
         if (numOfThreadsForKeystreamGeneration > numOfKeysNeeded)
            numOfThreadsForKeystreamGeneration = numOfKeysNeeded;

         if (numberOfPixels > 0) {
            System.gc();
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory() + 104857600; // add 100 MB for other JVM allocations
            long availableMemory = runtime.maxMemory() - usedMemory;
            int maxNumOfThreads = (int) (availableMemory / numberOfPixels);
            if (numOfThreadsForKeystreamGeneration > maxNumOfThreads) {
               numOfThreadsForKeystreamGeneration = maxNumOfThreads;
            }
         }
         
      } catch (NumberFormatException ex) {
      }
      updateFactsTable();
   }

   private void calculateKeySizeInBytes() {
      keySizeInBytes = Integer.parseInt((String)keySizeComboBox.getSelectedItem()) / 8;
      updateFactsTable();
   }

   // Calculations vars
   private int numberOfPixels;
   private int textBytesLength;
   private int numOfKeysNeeded;
   private long keystreamPeriod;
   private int numOfThreadsForImageVectorBuilding;
   private int numOfThreadsForKeystreamGeneration;
   private int keySizeInBytes;

   private File lastChosenImage;
   private static final String ENCRYPT = "encrypt";
   private static final String DECRYPT = "decrypt";

   private static final boolean IS_MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));

   // GUI components
   private static final String[] KEY_SIZES = {"128", "256", "512"};
   private JFileChooser fileChooser;
   private Container cp;
   private JLabel imageThumbnail;
   private JButton selectImageButton;
   private JButton encryptButton;
   private JButton decryptButton;
   private JComboBox keySizeComboBox;
   private JTextField numberOfThreadsField;
   private JTextArea plainTextArea;
   private JTextArea cipherTextArea;
   private JTable factsTable;
   private JTable timeMeasurementsTable;
   private static final String[] FACTS_TABLE_COLUMN_TITLES = {"", ""};
   private static final String[] TIME_MEASUREMENTS_TABLE_COLUMN_TITLES = {"Task", "Time"};
   private String[][] getDefaultValuesForFactsTable() {
      return new String[][]{
                 {"Number of pixels", "0"},
                 {"Number of text bytes", "0"},
                 {"Number of keys needed to encrypt the text", "0"},
                 {"Keystream period (in bytes)", "0"},
                 {"Number of threads to build the image vector", numberOfThreadsField.getText()},
                 {"Number of threads to generate the keys", numberOfThreadsField.getText()}
              };
   }
   private String[][] getDefaultValuesForTimeMeasurementsTable() {
      return new String[][]{
                 {"Getting the input text bytes", ""},
                 {"Loading the image", ""},
                 {"Building the image vector", ""},
                 {"Generating the keystream", ""},
                 {"Encryption / Decryption", ""},
                 {"Converting the bytes to a string", ""},
                 {"Total time", ""},
              };
   }



   /**
    * Initialization methods
    */
   public GUI() {
      // Make the enter key functioinal
      // Source: http://stackoverflow.com/a/440445/408286
      InputMap im = (InputMap) UIManager.getDefaults().get("Button.focusInputMap");
      Object pressedAction = im.get(KeyStroke.getKeyStroke("pressed SPACE"));
      Object releasedAction = im.get(KeyStroke.getKeyStroke("released SPACE"));
      im.put(KeyStroke.getKeyStroke("pressed ENTER"), pressedAction);
      im.put(KeyStroke.getKeyStroke("released ENTER"), releasedAction);

      setSize(1000, 760);
      setTitle("Image-based Encryption System");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setFocusable(true);

      initGUIComponents();
      registerListeners();
      setLocationRelativeTo(cp);
      initIVars();
//      if (IS_MAC_OS_X)
         getRootPane().putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
   }

   private void initIVars() {
      numberOfPixels = 0;
      textBytesLength = 0;
      numOfKeysNeeded = 0;
      keystreamPeriod = 0L;
      numOfThreadsForImageVectorBuilding = 0;
      numOfThreadsForKeystreamGeneration = 0;
      calculateKeySizeInBytes();
   }

   private void initGUIComponents() {
      cp = getContentPane();

      fileChooser = new JFileChooser();
      fileChooser.setApproveButtonText("Choose");
      fileChooser.setDialogTitle("Choose an image");
      fileChooser.setFileFilter(new ImageFilter());
      
      JPanel topPanel = new JPanel() {
         @Override
         public Border getBorder() {
            Window window = SwingUtilities.getWindowAncestor(this);
            return window != null && window.isFocused()
                    ? BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(64, 64, 64))
                    : BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(135, 135, 135));
         }
      };
      topPanel.setOpaque(false);
      topPanel.setLayout(null);
      topPanel.setSize(this.getWidth(), 100);

      Font sansSerif16 = new Font("SansSerif", Font.BOLD, 16);
      Font sansSerif14 = new Font("SansSerif", Font.PLAIN, 14);
      Font sansSerif13 = new Font("SansSerif", Font.PLAIN, 13);

      JPanel tmpPanel = new JPanel();
      tmpPanel.setLayout(null);
      tmpPanel.setOpaque(false);
      tmpPanel.setBounds(0, 0, 300, topPanel.getHeight());
      selectImageButton = new JButton("Select Image");
      selectImageButton.setBounds(30, 30, 110, 40);
      selectImageButton.setAlignmentX(CENTER_ALIGNMENT);
      selectImageButton.setFont(sansSerif13);
      tmpPanel.add(selectImageButton);
      imageThumbnail = new JLabel("", SwingConstants.CENTER);
      imageThumbnail.setFont(new Font("SansSerif", Font.PLAIN, 10));
      imageThumbnail.setBounds(160, 5, 90, 90);
      imageThumbnail.setAlignmentX(CENTER_ALIGNMENT);
      imageThumbnail.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED), new EtchedBorder(EtchedBorder.LOWERED)));
      imageThumbnail.setVisible(false);
      tmpPanel.add(imageThumbnail);

      topPanel.add(tmpPanel);
      int currentX = tmpPanel.getX() + tmpPanel.getWidth();

      tmpPanel = new JPanel();
      tmpPanel.setOpaque(false);
      tmpPanel.setLayout(null);
      tmpPanel.setBounds(currentX, 5, 400, topPanel.getHeight() - 10);
      encryptButton = new JButton("Encrypt");
      encryptButton.setBounds(40, 15, 140, 60);
      encryptButton.setAlignmentY(CENTER_ALIGNMENT);
      encryptButton.setOpaque(false);
      encryptButton.setFont(sansSerif16);
      tmpPanel.add(encryptButton);
      decryptButton = new JButton("Decrypt");
      decryptButton.setBounds(220, 15, 140, 60);
      decryptButton.setAlignmentY(CENTER_ALIGNMENT);
      decryptButton.setOpaque(false);
      decryptButton.setFont(sansSerif16);
      tmpPanel.add(decryptButton);
      tmpPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.LIGHT_GRAY));

      topPanel.add(tmpPanel);
      currentX = tmpPanel.getX() + tmpPanel.getWidth();

      tmpPanel = new JPanel();
      tmpPanel.setLayout(null);
      tmpPanel.setOpaque(false);
      tmpPanel.setBounds(currentX, 0, 300, 100);

      JLabel aLabel = new MyLabel("Key size in bits:");
      aLabel.setBounds(50, 10, 130, 30);
      aLabel.setBackground(Color.red);
      aLabel.setAlignmentX(CENTER_ALIGNMENT);
      aLabel.setAlignmentY(CENTER_ALIGNMENT);
      aLabel.setOpaque(false);
      aLabel.setFont(sansSerif14);
      keySizeComboBox = new JComboBox(KEY_SIZES);
      keySizeComboBox.setBounds(200,10,80, 30);
      keySizeComboBox.setAlignmentX(CENTER_ALIGNMENT);
      keySizeComboBox.setBackground(new Color(255, 255, 255));
      tmpPanel.add(aLabel);
      tmpPanel.add(keySizeComboBox);

      aLabel = new MyLabel("Number of Threads:");
      aLabel.setBounds(50,60,140, 30);
      aLabel.setAlignmentX(CENTER_ALIGNMENT);
      aLabel.setAlignmentY(CENTER_ALIGNMENT);
      aLabel.setOpaque(false);
      aLabel.setFont(sansSerif14);
      numberOfThreadsField = new JTextField("2", 99);
      numberOfThreadsField.setBounds(200,60,80, 30);
      numberOfThreadsField.setAlignmentX(CENTER_ALIGNMENT);
      tmpPanel.add(aLabel);
      tmpPanel.add(numberOfThreadsField);

      topPanel.add(tmpPanel);
      cp.setLayout(null);
      cp.add(topPanel);

      Font serif20 = new Font("Serif", Font.BOLD, 20);

      JPanel middlePane = new JPanel();
      middlePane.setLayout(null);
      middlePane.setBounds(0, topPanel.getHeight(), this.getWidth(), 320);
      aLabel = new MyLabel("Plaintext",SwingConstants.CENTER);
      aLabel.setBounds(180, 0, 140, 40);
      aLabel.setAlignmentX(CENTER_ALIGNMENT);
      aLabel.setAlignmentY(CENTER_ALIGNMENT);
      aLabel.setFont(serif20);
      middlePane.add(aLabel);

      aLabel = new MyLabel("Ciphertext",SwingConstants.CENTER);
      aLabel.setBounds(640, 0, 220, 40);
      aLabel.setAlignmentX(CENTER_ALIGNMENT);
      aLabel.setAlignmentY(CENTER_ALIGNMENT);
      aLabel.setFont(serif20);
      middlePane.add(aLabel);

      RoundedBorder border = new RoundedBorder();

      plainTextArea = new JTextArea();
      plainTextArea.setLineWrap(true);
      plainTextArea.setBorder(new EmptyBorder(5, 5, 5, 5));
      JScrollPane scrollPane = new JScrollPane(plainTextArea);
      scrollPane.setBorder(new CompoundBorder(border, new EtchedBorder(EtchedBorder.RAISED)));
      scrollPane.setOpaque(false);
      scrollPane.setBounds(30, 40, 440, 260);
      middlePane.add(scrollPane);

      cipherTextArea = new JTextArea();
      cipherTextArea.setLineWrap(true);
      cipherTextArea.setBorder(new EmptyBorder(5, 5, 5, 5));
      scrollPane = new JScrollPane(cipherTextArea);
      scrollPane.setBorder(new CompoundBorder(border, new EtchedBorder(EtchedBorder.RAISED)));
      scrollPane.setOpaque(false);
      scrollPane.setBounds(530, 40, 440, 260);
      middlePane.add(scrollPane);

      cp.add(middlePane);

      JPanel bottomPane = new JPanel();
      bottomPane.setLayout(null);
      bottomPane.setBounds(0, topPanel.getHeight()+middlePane.getHeight(), 1000, 440);
      bottomPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(64, 64, 64)));

      aLabel = new MyLabel("Facts",SwingConstants.CENTER);
      aLabel.setBounds(180, 0, 140, 40);
      aLabel.setAlignmentX(CENTER_ALIGNMENT);
      aLabel.setAlignmentY(CENTER_ALIGNMENT);
      aLabel.setFont(serif20);
      bottomPane.add(aLabel);

      aLabel = new MyLabel("Time Measurements",SwingConstants.CENTER);
      aLabel.setBounds(640, 0, 220, 40);
      aLabel.setAlignmentX(CENTER_ALIGNMENT);
      aLabel.setAlignmentY(CENTER_ALIGNMENT);
      aLabel.setFont(serif20);
      bottomPane.add(aLabel);

      String[][] factsTableRows = getDefaultValuesForFactsTable();
      factsTable = new JTable(factsTableRows, FACTS_TABLE_COLUMN_TITLES) {
         public boolean isCellEditable(int rowIndex, int colIndex) {
            return false;
         }
      };
      factsTable.getColumnModel().getColumn(0).setPreferredWidth(250);
      DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
      centerRenderer.setHorizontalAlignment(JLabel.CENTER);
      factsTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
      factsTable.setRowSelectionAllowed(true);
      factsTable.setShowHorizontalLines(false);
      factsTable.setShowVerticalLines(false);
      factsTable.setShowGrid(false);
      factsTable.setRowHeight(235/factsTableRows.length);
      factsTable.setIntercellSpacing(new Dimension(0, 0));
      if (IS_MAC_OS_X)
         factsTable.setSelectionBackground(Color.DARK_GRAY);
      else
         factsTable.setSelectionBackground(Color.LIGHT_GRAY);
      scrollPane = new JScrollPane(factsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scrollPane.setBounds(30, 40, 440, 260);
      scrollPane.setBorder(new CompoundBorder(border, new EtchedBorder(EtchedBorder.RAISED)));
      scrollPane.setOpaque(false);
      bottomPane.add(scrollPane);

      String[][] timeMeasurementsTableRows = getDefaultValuesForTimeMeasurementsTable();
      timeMeasurementsTable = new JTable(timeMeasurementsTableRows, TIME_MEASUREMENTS_TABLE_COLUMN_TITLES)  {
         public boolean isCellEditable(int rowIndex, int colIndex) {
            return false;
         }
      };
      timeMeasurementsTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
      timeMeasurementsTable.setRowSelectionAllowed(true);
      timeMeasurementsTable.setShowHorizontalLines(false);
      timeMeasurementsTable.setShowVerticalLines(true);
      timeMeasurementsTable.setRowHeight(220/timeMeasurementsTableRows.length);
      timeMeasurementsTable.setGridColor(Color.LIGHT_GRAY);
      if (IS_MAC_OS_X)
         timeMeasurementsTable.setSelectionBackground(Color.DARK_GRAY);
      else
         timeMeasurementsTable.setSelectionBackground(Color.LIGHT_GRAY);
      scrollPane = new JScrollPane(timeMeasurementsTable);
      scrollPane.setBounds(530, 40, 440, 260);
      scrollPane.setBorder(new CompoundBorder(border, new EtchedBorder(EtchedBorder.RAISED)));
      scrollPane.setOpaque(false);
      bottomPane.add(scrollPane);

      cp.add(bottomPane);

      getRootPane().setDefaultButton(selectImageButton);
      selectImageButton.requestFocusInWindow();
   }

   private void registerListeners() {

      selectImageButton.addActionListener(new MyActionsListener());
      imageThumbnail.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            selectImageButton.doClick();
         }
      });
      encryptButton.addActionListener(new MyActionsListener());
      decryptButton.addActionListener(new MyActionsListener());

      keySizeComboBox.addActionListener(new MyActionsListener());

      plainTextArea.getDocument().addDocumentListener(new TextListener());
      cipherTextArea.getDocument().addDocumentListener(new TextListener());
      numberOfThreadsField.getDocument().addDocumentListener(new TextListener());

      plainTextArea.addMouseListener(new MouseListener() {

         public void mouseClicked(MouseEvent me) {
            if ((me.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
               // right click
               if (!plainTextArea.getText().isEmpty()) {
                  // display a copy option
                  JPopupMenu popup = new JPopupMenu();
                  JMenuItem copyItem = new JMenuItem("Copy");
                  copyItem.addActionListener(new ActionListener() {
                     @Override
                     public void actionPerformed(ActionEvent e) {
                        Toolkit toolkit = Toolkit.getDefaultToolkit();
                        Clipboard clipboard = toolkit.getSystemClipboard();
                        StringSelection strSel = new StringSelection(plainTextArea.getText());
                        clipboard.setContents(strSel, null);
                     }
                  });
                  popup.add(copyItem);
                  JMenuItem clear = new JMenuItem("Clear");
                  clear.addActionListener(new ActionListener() {
                     @Override
                     public void actionPerformed(ActionEvent e) {
                        plainTextArea.setText("");
                     }
                  });
                  popup.add(clear);
                  popup.show(plainTextArea, me.getX(), me.getY());
               }
            }
         }

         public void mousePressed(MouseEvent e) {}
         public void mouseReleased(MouseEvent e) {}
         public void mouseEntered(MouseEvent e) {}
         public void mouseExited(MouseEvent e) {}

      });
      cipherTextArea.addMouseListener(new MouseListener() {

         public void mouseClicked(MouseEvent me) {
            if ((me.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
               // right click
               if (!cipherTextArea.getText().isEmpty()) {
                  // display a copy option
                  JPopupMenu popup = new JPopupMenu();
                  JMenuItem copyItem = new JMenuItem("Copy");
                  copyItem.addActionListener(new ActionListener() {
                     @Override
                     public void actionPerformed(ActionEvent e) {
                        Toolkit toolkit = Toolkit.getDefaultToolkit();
                        Clipboard clipboard = toolkit.getSystemClipboard();
                        StringSelection strSel = new StringSelection(cipherTextArea.getText());
                        clipboard.setContents(strSel, null);
                     }
                  });
                  popup.add(copyItem);
                  JMenuItem clear = new JMenuItem("Clear");
                  clear.addActionListener(new ActionListener() {
                     @Override
                     public void actionPerformed(ActionEvent e) {
                        cipherTextArea.setText("");
                     }
                  });
                  popup.add(clear);
                  popup.show(cipherTextArea, me.getX(), me.getY());
               }
            }
         }

         public void mousePressed(MouseEvent e) {}
         public void mouseReleased(MouseEvent e) {}
         public void mouseEntered(MouseEvent e) {}
         public void mouseExited(MouseEvent e) {}

      });

   }
}
