//HIDE
//OUT canvas.png
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.RescaleOp;
import java.awt.Color;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Canvas
{
    private static Canvas canvas = new Canvas();

    private ArrayList<Shape> shapes = new ArrayList<Shape>();
    private BufferedImage background;
    private JFrame frame;
    private CanvasComponent component;

    private static Color bgColor = Color.WHITE;

    private static final int MIN_SIZE = 100;
    private static final int MARGIN = 0;
    private static final int LOCATION_OFFSET = 120;

    class CanvasComponent extends JComponent
    {
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.setColor(bgColor);
            g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
            g.setColor(java.awt.Color.BLACK);
            if (background != null)
            {
                g.drawImage(background, 0, 0, null);
            }               
            for (Shape s : new ArrayList<Shape>(shapes))
            {
                Graphics2D g2 = (Graphics2D) g.create();
                s.paintShape(g2);
                g2.dispose();
            }
        }

        public Dimension getPreferredSize()
        {
            int maxx = MIN_SIZE;
            int maxy = MIN_SIZE;
            if (this.isPreferredSizeSet()) {
                maxx = (int) super.getPreferredSize().getWidth();
                maxy = (int) super.getPreferredSize().getHeight();
            }
            if (background != null) {
                maxx = Math.max(maxx, background.getWidth());
                maxy = Math.max(maxx, background.getHeight());
            }
            for (Shape s : shapes)
            {
                maxx = (int) Math.max(maxx, s.getX() + s.getWidth());
                maxy = (int) Math.max(maxy, s.getY() + s.getHeight());
            }
            return new Dimension(maxx + MARGIN, maxy + MARGIN);
        }
    }

    private Canvas()
    {
        super();
        component = new CanvasComponent();
        component.setLayout(null);

        if (System.getProperty("com.horstmann.codecheck") == null)
        {
            frame = new JFrame();
            if (!System.getProperty("java.class.path").contains("bluej"))
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(component);
            frame.pack();
            frame.setLocation(LOCATION_OFFSET, LOCATION_OFFSET);
            frame.setVisible(true);
        }
        else
        {
            final String SAVEFILE ="canvas.png";
            final Thread currentThread = Thread.currentThread();
            Thread watcherThread = new Thread() 
                {
                    public void run()
                    {
                        try
                        {
                            final int DELAY = 10;
                            
                            while (currentThread.getState() != State.TERMINATED)
                            {
                                Thread.sleep(DELAY);
                            }
                            saveToDisk(SAVEFILE);
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                };
            watcherThread.start();
        }
    }

    public static Canvas getInstance()
    {
        canvas.frame.setVisible(true); // In case it was closed in BlueJ
        return canvas;
    }

    public CanvasComponent getCanvasComponent()
    {
        return component;
    }

    public void show(Shape s)
    {
        if (!shapes.contains(s))
        {
            shapes.add(s);
        }
        repaint();
    }

    public int getWidth()
    {
        return component.getWidth();
    }

    public int getHeight()
    {
        return component.getHeight();
    }

    public void setPreferredSize(int width, int height)
    {
        //frame.setSize(width, height);
        component.setSize(width, height);
        //frame.setPreferredSize(new Dimension(width, height));
        component.setPreferredSize(new Dimension(width, height));
        repaint();
    }

    public void repaint()
    {
        if (frame == null) return;
        Dimension dim = component.getPreferredSize();
        if (dim.getWidth() > component.getWidth()
                || dim.getHeight() > component.getHeight())
        {
            frame.pack();
            frame.repaint();
        }
        else
        {
            frame.repaint();
        }
    }

    /**
     * Pauses so that the user can see the picture before it is transformed.
     */
    public static void pause()
    {
        JFrame frame = getInstance().frame;
        if (frame == null) return;
        JOptionPane.showMessageDialog(frame, "Click Ok to continue");
    }

    /**
     * Takes a snapshot of the screen, fades it, and sets it as the background.
     */
    public static void snapshot()
    {
        Dimension dim = getInstance().component.getPreferredSize();
        java.awt.Rectangle rect = new java.awt.Rectangle(0, 0, dim.width, dim.height);
        BufferedImage image = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, rect.width, rect.height);
        g.setColor(java.awt.Color.BLACK);
        getInstance().component.paintComponent(g);
        float factor = 0.8f;
        float base = 255f * (1f - factor);
        RescaleOp op = new RescaleOp(factor, base, null);
        BufferedImage filteredImage
           = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        op.filter(image, filteredImage);
        getInstance().background = filteredImage;
        getInstance().component.repaint();
    }

    public void saveToDisk(String fileName)
    {
        Dimension dim = component.getPreferredSize();
    	java.awt.Rectangle rect = new java.awt.Rectangle(0, 0, dim.width, dim.height);
    	BufferedImage image = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(bgColor);
        g.fill(rect);
        g.setColor(java.awt.Color.BLACK);
        component.paintComponent(g);
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        try
        {
            ImageIO.write(image, extension, new File(fileName));
        } 
        catch(IOException e)
        {
            System.err.println("Was unable to save the image to " + fileName);
        }
    	g.dispose();    	
    }
    public void setBackground(Color bgColor)
    {
        this.bgColor = bgColor;
        frame.repaint();
    }
}
