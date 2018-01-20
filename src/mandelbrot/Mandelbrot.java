package mandelbrot;

import javax.swing.JFrame;
import mandelbrot.view.MandelbrotWindow;
import util.Vector;
import util.VectorRectangle;

/**
 * Definiert die Hauptklasse, das Model des Programms, welches zur Laufzeit
 * genau einmal existiert.
 */
public class Mandelbrot {
    public static void main(String[] args) {
        Mandelbrot m = new Mandelbrot();
        MandelbrotWindow mw = new MandelbrotWindow(m);
        mw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public final int threads = 8;
    
    public final VectorRectangle firstInterval = new VectorRectangle(
        new Vector(-2.2, -1.501),
        new Vector(0.8, 1.499)
    );
    
    public final int frame = 100; // Vorausberechnungsrahmen in Pixeln
    public final int lowPriorityFrame = 300; // in Pixeln
    public final double scaleup = 2; // Auflösungsskalierungsfaktor
    
    public int width = 400;
    public int height = 300;
    
    public double zoomFactor = 0.03; // 1 -/+ Intervallstreckungsfaktor je Frame
    
    public int iterations = 100;
    
    public ColorProjection colorProjection = ColorProjection.colorProjections[0];
    public ColorProjection pointsColorProjection = ColorProjection.colorProjections[1];
    
    public int pointsAmount = 1000;
    public boolean linesNotPoints = false;
    
    public Vector mousePoint = null; // Mauspunkt in der komplexen Zahlenebene
    
    /**
     * Erzeugt ein neues Mandelbrotdatenmodell.
     */
    public Mandelbrot() {
        cache1 = new TiledRendering(width, height, frame, lowPriorityFrame, scaleup,
            firstInterval, colorProjection, iterations);
        buildCache2();
        for (int i = 0; i < threads; ++i) new Thread(new Worker(i)).start();
    }
    
    /*** caching ***/
    
    public TiledRendering cache1;
    public TiledRendering cache2;
    
    /**
     * Baut den zweiten Cache auf Grund der aktuellen Datenlage neu auf.
     */
    public void buildCache2() {
        cache2 = new TiledRendering(width, height, frame, lowPriorityFrame,
            scaleup, cache1.interval.scale(1 / scaleup), colorProjection, iterations
        );
    }
    
    /**
     * Baut den gesamten Cache auf Grund der aktuellen Datenlage neu auf.
     */
    public void refresh() {
        cache1 = new TiledRendering(width, height, frame, lowPriorityFrame, scaleup,
            cache1.interval, colorProjection, iterations);
        buildCache2();
    }
    
    /*** scheduling ***/
    
    /**
     * Sucht die nächste zu berechnende Aufgabe und reserviert diese.
     * 
     * @param seed gleich verteilter Wert, der für jeden Thread unterschiedlich
     * sein sollte, keine wesentliche Rolle spielt und nur zu
     * Optimierungszwecken der Lastverteilung benötigt wird
     * @return ein ausführbares Objekt oder null falls es nichts zu tun gibt
     */
    public Runnable getWork(int seed) {
        Runnable r;
        
        if ((r = cache1.getWork(seed, true)) != null) return r;
        if ((r = cache2.getWork(seed, true)) != null) return r;
        if ((r = cache1.getWork(seed, false)) != null) return r;
        if ((r = cache2.getWork(seed, false)) != null) return r;
        
        return null;
    }
    
    /*** working ***/
    
    class Worker implements Runnable {
        private final int seed;
        
        public Worker(int seed) {
            this.seed = seed;
        }
        
        @Override
        public void run() {
            while (true) {
                Runnable r;
                while ((r = getWork(seed)) != null) r.run();
                
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
        }
    }
    
    /*** dragging ***/
    
    public Vector dragStart;
    public VectorRectangle dragStartInterval;
    
    /**
     * Beginnt einen Verschiebevorgang an dem angegebenen Punkt.
     * 
     * @param v Startpunkt der komplexen Zahlenebene des Verschiebevorganges
     */
    public void dragStart(Vector v) {
        this.dragStart = v;
        this.dragStartInterval = cache1.interval;
    }
    
    /**
     * Führt die Verschiebung des aktuellen Verschiebevorganges bis zu dem
     * angegebenen punkt durch. Diese Methode beendet den Verschiebevorgang
     * nicht und kann immer wieder aufgerufen werden, um das Ziehen zu einem
     * anderen Punkt auszuführen.
     * 
     * @param v vorläufiger Endpunkt der komplexen Zahlen Ebene des Verschiebevorganges
     * @param reverse Angabe, ob der Verschiebevorgang in umgekehrter Richtung
     * ausgeführt werden soll
     */
    public void drag(Vector v, boolean reverse) {
        Vector move = v.minus(dragStart);
        cache1.interval = reverse ? dragStartInterval.minus(move) : dragStartInterval.plus(move);
        cache1.recenter();
    }
    
    /*** zooming ***/
    
    /**
     * Zoomt den gewählten Bildausschnitt größer oder kleiner.
     * 
     * @param origin Punkt in den bzw. aus dem herein bzw. herausgezoomt werden soll
     * @param factor anzuwendender Skalierungsfaktor des Bildausschnittes
     */
    public void zoom(Vector origin, double factor) {
        cache1.interval = cache1.interval.scale(origin, factor);
        
        if (cache1.interval.getWidth() < cache1.nativeSize.getX()
            || cache1.interval.getHeight() < cache1.nativeSize.getY()) {
            VectorRectangle cr = cache1.interval;
            cache1 = cache2;
            cache1.interval = cr;
            cache1.recenter();
            buildCache2();
        }
        
        if (cache1.interval.getWidth() > cache1.totalInterval.getSize().multiply(0.9).getX()
            || cache1.interval.getHeight() > cache1.totalInterval.getSize().multiply(0.9).getY()) {
            cache1 = new TiledRendering(width, height, frame, lowPriorityFrame, scaleup,
                cache1.interval, colorProjection, iterations);
            buildCache2();
        }
    }
}
