package wfc;

import java.util.HashSet;
import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class keyListener implements KeyListener{
   HashSet<Integer> keysHeld = new HashSet<>();
   keyListener(JFrame frame){
      frame.addKeyListener(this);
   }
   @Override
   public void keyPressed(KeyEvent e){
      keysHeld.add(e.getKeyCode());
   }
   @Override
   public void keyReleased(KeyEvent e){
      keysHeld.remove(e.getKeyCode());
   }
   @Override
   public void keyTyped(KeyEvent e){}
   public boolean keyDown(int key){
      return keysHeld.contains(key);
   }
}