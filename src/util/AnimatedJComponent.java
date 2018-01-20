package util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Definiert einen doppelt gepufferten, animierten und sich automatisch
 * aktualisierenden JComponent.
 */
public abstract class AnimatedJComponent extends JComponent {
    private final Timer timer;
    
    public AnimatedJComponent() {
        setDoubleBuffered(true);
        
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateAnimation();
                repaint();
            }
        });
        timer.start();
    }
    
    protected abstract void calculateAnimation();
}
