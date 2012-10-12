package mainpackage.core;

public interface CoreObserver {
   
   public void done(int taskNumber, String time);

   public void done(String time);
}
