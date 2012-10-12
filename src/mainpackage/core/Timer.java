package mainpackage.core;

class Timer {

   private long startTime;
   private long endTime;
   private long totalTime;

   public Timer() {
      totalTime = 0L;
      this.reset();
   }

   public void start() {
      this.reset();
      startTime = System.nanoTime();
   }

   public void end() {
      endTime = System.nanoTime();
      this.calculateTotal();
   }

   public String getLastTime() {
      long nanoSecs  = (endTime - startTime);
      return Util.convertToString(nanoSecs);
   }

   @Override
   public String toString() {
      return Util.convertToString(totalTime);
   }

   private void calculateTotal() {
      totalTime = totalTime + (endTime - startTime);
   }

   private void reset() {
      startTime = 0;
      endTime   = 0;
   }
}
