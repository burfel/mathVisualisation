package mandelbrot;

import java.awt.image.BufferedImage;
import util.ScreenProjection;
import util.VectorRectangle;

/**
 * Beschreibt ein Pixelbild fester Größe, das eine unter Umständen noch nicht
 * fertig berechnete Darstellung eines festen Intervalls der durch
 * ProgressionCalculator definierten Funktion ist und für die eine fest
 * gewählte Farbprojektion, sowie eine festgewählte Iterationszahl verwendet
 * wird.
 * Objekte der Klasse sind ausführbar, wobei bei der Ausführung die
 * entsprechende Darstellung berechnet wird.
 * Ein Objekt der Klasse sollte immer nur genau einmal ausgeführt werden. Dazu
 * gibt es einen synchronisierten Reservierungsmechanismus, sodass im Falle der
 * Verwendung eines Threadpools ein thread threadsafe die Reservierung eines
 * Rendering-Objektes mit der Methode boolean tryToReserve() versuchen kann,
 * die nur bei ihrem ersten Aufruf überhaupt true liefern wird, was dann als
 * Übertragung der exklusiven Ausführungszuständigkeit an den reservierenden
 * Thread interpretiert werden soll.
 */
public class Rendering extends BufferedImage implements Runnable {
    public final VectorRectangle interval;
    public final ColorProjection colorProjection;
    public final int iterations;
    
    public int time = 0; // Ausführungsdauer in Millisekunden
    
    /**
     * Erzeugt ein neues Rendering.
     * 
     * @param width Breite des Renderings in Pixeln
     * @param height Höhe des Renderings in Pixeln
     * @param interval darzustellendes Interval
     * @param colorProjection zu verwendende Farbprojektion
     * @param iterations zur Berechnung zu verwendende Iterationstiefe
     */
    public Rendering(int width, int height, VectorRectangle interval,
        ColorProjection colorProjection, int iterations) {
        super(width, height, BufferedImage.TYPE_INT_RGB);
        this.interval = interval;
        this.colorProjection = colorProjection;
        this.iterations = iterations;
    }
    
    private boolean isReserved = false;
    
    /**
     * Versucht das Objekt zu reservieren und somit die exklusive
     * Ausführungszuständigkeit zu erlangen.
     * @return true sofern die Reservierung geglückt ist, ansonsten false
     */
    public synchronized boolean tryToReserve() {
        if (isReserved) return false;
        return isReserved = true;
    }
    
    @Override
    public void run() {
        long t0 = System.currentTimeMillis();
        
        ScreenProjection sp = new ScreenProjection(interval,
            getWidth(), getHeight());
        ProgressionCalculator pc = new ProgressionCalculator();
        
        for (int x = 0; x < getWidth(); ++x) {
            for (int y = 0; y < getHeight(); ++y) {
                pc.begin(sp.projectFromScreen(x, y));
                setRGB(x, y, colorProjection
                    .project(pc.calculate(iterations), iterations).getRGB());
            }
        }
        
        time = Math.max(1, (int)(System.currentTimeMillis() - t0));
    }
}
