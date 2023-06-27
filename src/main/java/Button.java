import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Dimension;

public class Button extends JButton implements Shape{
    public void paintShape(Graphics2D g2)
    {
        //this.paint(g2);
        Insets insets = Canvas.getInstance().getCanvasComponent().getInsets();
        Dimension size = this.getSize();
        this.setBounds(this.getX() + insets.left, this.getY() + insets.top,
                size.width, size.height);
    }

    public void draw()
    {
        Canvas.getInstance().show(this);
        Canvas.getInstance().getCanvasComponent().add(this);
    }


    public Button(Icon icon)
    {
        super(icon);
    }

    public Button(String text)
    {
        super(text);
    }

    public Button(String text, Icon icon)
    {
        super(text, icon);
    }
}
