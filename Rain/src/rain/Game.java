/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rain;

import graphics.Screen;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 * @author AnhTu
 */
public class Game extends Canvas implements Runnable {
    private static final long serialVersionUID = 1L;
    
    public static int width = 300;
    public static int height = width/16*9;
    public static int scale = 3;

    private Thread thread;
    private boolean running = false;
    private JFrame frame;
    
    private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData(); 

    private Screen screen;
    
    public Game() {
        Dimension size = new Dimension(width * scale, height * scale);
        this.setPreferredSize(size);
        
        frame = new JFrame();
        screen = new Screen(Game.width, Game.height);
    }
    
    
    public synchronized void startThread() {
        running = true;
        thread = new Thread(this, "Display");
        thread.start();
    }

    public synchronized void stopThread() {
        try {
            thread.join();      //wait until thread finish its task
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double ns = 1000000000.0 / 60.0;
        double delta = 0;
        int frames = 0;     //how many frames computer will render
        int updates = 0;
        
        while(running) {
            long now = System.nanoTime();
            delta += (now-lastTime)/ns;
            lastTime = now;
            while(delta >= 1) {
                update();   //handle all the logic, running 60times/second
                updates++;
                delta--;
            }
            render();   //display image on screen
            frames++;
            
            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frame.setTitle("Rain | "+updates + " ups, " + frames + " fps");
                updates = 0; frames = 0;
            }
        }
        
        stopThread();
    }
    
    public static void main(String[] args) {
        Game game = new Game();
        game.frame.setResizable(false);
        game.frame.setTitle("Rain");
        game.frame.add(game);
        game.frame.pack();
        game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.frame.setLocationRelativeTo(null);
        game.frame.setVisible(true);
        
        game.startThread();
    }

    public void update() {
        
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if(bs == null) {
            createBufferStrategy(3);    //3 = space of programm,...
            System.out.println("bs == null");
            return;
        }
        
        screen.clear();
        screen.render();
        
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = screen.pixels[i];
        }
        //có thể dùng: System.arraycopy(screen.pixels, 0, pixels, 0, pixels.length);
        
        Graphics g = bs.getDrawGraphics();
        
        ///do all the graphics in here////////
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        /////end of do all the graphics///////
        g.dispose();
        bs.show();
        
    }
    
}
