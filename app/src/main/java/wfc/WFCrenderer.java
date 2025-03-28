package wfc;

import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
public class WFCrenderer extends JFrame{
   int[][] grid;
   Graphics g;
   keyListener kl;
   double xoffset = 0;
   double yoffset = 0;
   double zoom = 1;
    public WFCrenderer(int[][] grid){
        setVisible(true);
        this.setSize(800,800);
        this.setTitle("WFCrenderer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        g = this.getGraphics();
        this.grid = grid;
        kl = new keyListener(this);
        while (true) {
            try {Thread.sleep(16);} catch (Exception e) {}
            paint(g);
        }
    }
    Color[] colors = {Color.DARK_GRAY, Color.GREEN, Color.ORANGE, Color.CYAN, Color.LIGHT_GRAY, Color.RED};
    @Override
    public void paint(Graphics g){
        if (this.kl == null) {
            return;
        }
        if(kl.keyDown(KeyEvent.VK_W)){
            yoffset++;
         }
         if(kl.keyDown(KeyEvent.VK_S)){
            yoffset--;
         }
         if(kl.keyDown(KeyEvent.VK_A)){
            xoffset--;
         }
         if(kl.keyDown(KeyEvent.VK_D)){
            xoffset++;
         }
         if(kl.keyDown(KeyEvent.VK_E)){
            zoom*=1.01;
         }
         if(kl.keyDown(KeyEvent.VK_Q)){
            zoom*=0.99;
         }
         BufferedImage bi = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
         Graphics bg = bi.getGraphics();
         bg.setColor(Color.WHITE);
         bg.fillRect(0,0,800,800);
         bg.setColor(Color.BLACK);
         for(int y = 0; y < grid.length; y++){
            for(int x = 0; x < grid[y].length; x++){
               bg.setColor(colors[grid[x][y]]);
               bg.fillRect((int)((double)(x-xoffset*grid.length/100)*(720.0/grid.length)/zoom)+400,(int)((double)(y+yoffset*grid.length/100)*(720.0/grid.length)/zoom)+400,Math.max((int)(720.0/grid.length/zoom),1)+1,Math.max((int)(720.0/grid.length/zoom),1)+1);
            }
         }
         g.drawImage(bi,0,0,null);
    }
}
