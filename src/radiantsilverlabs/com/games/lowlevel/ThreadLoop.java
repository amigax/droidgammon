/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package radiantsilverlabs.com.games.lowlevel;


import radiantsilverlabs.com.games.androidspecifics.DrawView;

/**
 *
 * @author Gaz
 */
class ThreadLoop implements Runnable {

   


      boolean isRunning = true;
      public void run() {
        long cycleTime = System.currentTimeMillis();
        while(isRunning) {
           // System.out.println("repaint.");
            //DrawView.customcanvas.//Main.canvas.paint();
            cycleTime = cycleTime + Main.FRAME_DELAY;
            long difference = cycleTime - System.currentTimeMillis();
            try {
             Thread.sleep(Math.max(0, difference));
            }
            catch(InterruptedException e) {
             e.printStackTrace();
            }
         }
      }
 }




