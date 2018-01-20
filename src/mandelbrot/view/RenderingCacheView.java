package mandelbrot.view;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import mandelbrot.Mandelbrot;
import mandelbrot.TiledRendering;
import util.AnimatedJComponent;
import util.ScreenProjection;
import util.Vector;

/**
 * Definiert eine Darstellung des aktuelle gerenderten Caches.
 */
public class RenderingCacheView extends AnimatedJComponent {
    private final Mandelbrot mandelbrot;
    private final boolean displaySecond;
    
    private TiledRendering getCache() {
        return displaySecond ? mandelbrot.cache2 : mandelbrot.cache1;
    }
    
    private boolean isDragging = false;
    private boolean isZoomingIn = false;
    private boolean isZoomingOut = false;
    
    private ScreenProjection dragProjection;
    
    private ScreenProjection getScreenProjection() {
        return new ScreenProjection(getCache().totalInterval, getWidth(), getHeight());
    }
    
    private Vector getOrigin() {
        return getScreenProjection().projectFromScreen(getMousePosition());
    }
    
    public RenderingCacheView(final Mandelbrot mandelbrot, boolean displaySecond) {
        this.mandelbrot = mandelbrot;
        this.displaySecond = displaySecond;
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
                if (isDragging) mandelbrot.drag(dragProjection.projectFromScreen(e.getPoint()), false);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
    }
    
    @Override
    protected void calculateAnimation() {
        if (isZoomingIn || isZoomingOut) mandelbrot.zoom(getOrigin(), isZoomingIn ? 1-mandelbrot.zoomFactor : 1+mandelbrot.zoomFactor);
    }

    @Override
    public void paint(Graphics g) {
        getCache().drawDebug((Graphics2D)g, 0, 0, getWidth(), getHeight());
    }
}
