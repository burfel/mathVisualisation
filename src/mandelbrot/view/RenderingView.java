package mandelbrot.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import mandelbrot.Mandelbrot;
import mandelbrot.ProgressionCalculator;
import util.AnimatedJComponent;
import util.ScreenProjection;
import util.Vector;
import util.VectorRectangle;

/**
 * Definiert eine Darstellung des aktuelle gerenderten Bildes.
 */
public class RenderingView extends AnimatedJComponent {
    private final Mandelbrot mandelbrot;
    
    private boolean isDragging = false;
    private boolean isZoomingIn = false;
    private boolean isZoomingOut = false;
    
    private ScreenProjection dragProjection;
    
    private ScreenProjection getScreenProjection() {
        return new ScreenProjection(mandelbrot.cache1.interval, getWidth(), getHeight());
    }
    
    private Vector getOrigin() {
        Point p = getMousePosition();
        if (p == null) return null;
        return getScreenProjection().projectFromScreen(p);
    }
    
    public RenderingView(final Mandelbrot mandelbrot) {
        this.mandelbrot = mandelbrot;
        //calculateAspectRatio();
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isShiftDown()) {
                    isDragging = true;
                    dragProjection = getScreenProjection();
                    mandelbrot.dragStart(dragProjection.projectFromScreen(e.getPoint()));
                }
                else if (e.getButton() == MouseEvent.BUTTON1) isZoomingIn = true;
                else if (e.getButton() == MouseEvent.BUTTON3) isZoomingOut = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
                isZoomingIn = false;
                isZoomingOut = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
            
        });
        
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) mandelbrot.drag(dragProjection.projectFromScreen(e.getPoint()), true);
                mandelbrot.mousePoint = getOrigin();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mandelbrot.mousePoint = getOrigin();
            }
        });
        
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                onResize();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
    }
    
    private void onResize() {
        mandelbrot.width = getWidth();
        mandelbrot.height = getHeight();
        
        mandelbrot.cache1.interval = new VectorRectangle(
            mandelbrot.cache1.interval.getCenter(),
            mandelbrot.cache1.interval.getHeight() * (getWidth() / (double)getHeight()),
            mandelbrot.cache1.interval.getHeight()
        );
        
        mandelbrot.refresh();
    }
    
    @Override
    protected void calculateAnimation() {
        if (isZoomingIn || isZoomingOut) {
            Vector v = getOrigin();
            if (v != null) mandelbrot.zoom(v, isZoomingIn ? 1-mandelbrot.zoomFactor : 1+mandelbrot.zoomFactor);
        }
    }

    @Override
    public void paint(Graphics g) {
        mandelbrot.cache1.draw((Graphics2D)g, 0, 0, getWidth(), getHeight());
        
        g.setColor(Color.WHITE);
        
        Vector v = getOrigin();
        
        if (v != null) {
            ProgressionCalculator m = new ProgressionCalculator();
            m.begin(v);
            m.calculate();
            
            Point lastPoint = null;
            
            for (int i = 0; i < mandelbrot.pointsAmount; ++i) {
                Point p = getScreenProjection().projectToScreen(m.currentValue);
                g.setColor(mandelbrot.pointsColorProjection.project(i, mandelbrot.pointsAmount));
                
                if (0 <= p.x && p.x < getWidth() && 0 <= p.y && p.y < getHeight()) {
                    if (mandelbrot.linesNotPoints) g.drawLine(lastPoint.x, lastPoint.y, p.x, p.y);
                    else g.drawRect((int)p.getX(), (int)p.getY(), 1, 1);
                }
                
                m.calculate();
                
                lastPoint = p;
            }
        }
    }
}
