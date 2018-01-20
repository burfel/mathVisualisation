package mandelbrot.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import mandelbrot.Mandelbrot;
import util.AnimatedJComponent;
import util.Util;
import util.Vector;

/**
 * Definiert eine Text-Darstellung aktueller Metadaten.
 */
public class MetaView extends AnimatedJComponent {
    private final Mandelbrot mandelbrot;
    
    public MetaView(Mandelbrot mandelbrot) {
        this.mandelbrot = mandelbrot;
        setPreferredSize(new Dimension(250, 200));
    }
    
    @Override
    protected void calculateAnimation() {
    }
    
    @Override
    public void paint(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.WHITE);
        
        int lineHeight = 18;
        
        int ox1 = 10;
        int ox2 = 75;
        int oy = 20 - lineHeight;
        
        oy += lineHeight;
        g.drawString("USER INTERFACE", ox1, oy);
        oy += 5;
        
        oy += lineHeight;
        g.drawString("zoom in", ox1, oy);
        g.drawString("hold left mouse button", ox2, oy);
        
        oy += lineHeight;
        g.drawString("zoom out", ox1, oy);
        g.drawString("hold right mouse button", ox2, oy);
        
        oy += lineHeight;
        g.drawString("drag", ox1, oy);
        g.drawString("shift + move pressed mouse", ox2, oy);
        
        oy += 5;
        oy += lineHeight;
        g.drawString("mouse:", ox1, oy);
        Vector v = mandelbrot.mousePoint;
        g.drawString((v == null ? "n/a" : v.complexToString()), ox2, oy);
        
        oy += 15;
        oy += lineHeight;
        g.drawString("INTERVAL", ox1, oy);
        oy += 5;
        
        oy += lineHeight;
        g.drawString("real:", ox1, oy);
        g.drawString("[" + Util.format(mandelbrot.cache1.interval.getLeft()) + ", " + Util.format(mandelbrot.cache1.interval.getRight()) + "]", ox2, oy);
        
        oy += lineHeight;
        g.drawString("imaginary:", ox1, oy);
        g.drawString("[" + Util.format(mandelbrot.cache1.interval.getBottom()) + ", " + Util.format(mandelbrot.cache1.interval.getTop()) + "]", ox2, oy);
        
        oy += lineHeight;
        g.drawString("width:", ox1, oy);
        g.drawString(Util.format(mandelbrot.cache1.interval.getWidth()), ox2, oy);
        
        oy += lineHeight;
        g.drawString("height:", ox1, oy);
        g.drawString(Util.format(mandelbrot.cache1.interval.getHeight()), ox2, oy);
        
        oy += 15;
        oy += lineHeight;
        g.drawString("RENDERING", ox1, oy);
        oy += 5;
        
        oy += lineHeight;
        g.drawString("resolution:", ox1, oy);
        g.drawString(mandelbrot.width + "x" + mandelbrot.height, ox2, oy);
        
        oy += lineHeight;
        g.drawString("iterations:", ox1, oy);
        g.drawString(mandelbrot.iterations + "", ox2, oy);
        
        oy += 5;
        oy += lineHeight;
        g.drawString("cache1", ox1, oy);
        g.drawString(mandelbrot.cache1.totalWidth + "x" + mandelbrot.cache1.totalHeight, ox2, oy);
        g.drawString(String.format("%.0f", mandelbrot.cache1.getCompleteness() * 100) + "%", 150, oy);
        g.drawString(String.format("%.3f", mandelbrot.cache1.getTotalTime() / mandelbrot.threads / 1000d) + "s", 195, oy);
        
        oy += lineHeight;
        g.drawString("cache2", ox1, oy);
        g.drawString(mandelbrot.cache2.totalWidth + "x" + mandelbrot.cache2.totalHeight, ox2, oy);
        g.drawString(String.format("%.0f", mandelbrot.cache2.getCompleteness() * 100) + "%", 150, oy);
        g.drawString(String.format("%.3f", mandelbrot.cache2.getTotalTime() / mandelbrot.threads / 1000d) + "s", 195, oy);
    }
}
