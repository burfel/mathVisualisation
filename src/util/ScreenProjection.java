package util;

import java.awt.Point;

/**
 * Definiert eine Projektion zwischen einem Intervall der zweidimensionalen
 * Vektorebene und Bildschirmkoordinaten.
 * Dabei werden die Koordinaten der Vektorebene wie bei Darstellungen üblich so
 * interpretiert, dass die x-Koordinate nach rechts wächst und die y-Koordinate
 * nach oben wächst. Die Bildschirmkoordinaten werden wie in Java üblich so
 * interpretiert, dass die x-Koordinate ebenfalls nach recths wächst, jedoch die
 * y-Koordinate nach unten wächst.
 * Die Bildschirmkoordinaten gehen jeweils von 0 bis zur angegebenen Breite
 * bzw. Höhe.
 */
public class ScreenProjection {
    private final VectorRectangle interval;
    
    private final int screenWidth;
    private final int screenHeight;
    
	/**
	 * Erzeugt eine neue Bildschirmprojektion.
	 * @param interval Intervall der zweidimensionalen Vektorebene.
	 * @param screenWidth Breite des Bildschirm-Koordinatensystems
	 * @param screenHeight Höhe des Bildschirm-Koordinatensystems
	 */
    public ScreenProjection(VectorRectangle interval, int screenWidth, int screenHeight) {
        this.interval = interval;
        
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }
    
	/**
	 * Projiziert den angegebenen Punkt von Bildschirmkoordinaten in
	 * Vektorkoordinaten.
	 * @param x x-Koordinate auf dem Bildschirm
	 * @param y y-Koordinate auf dem Bildschirm
	 * @return Punkt als Vektor
	 */
    public Vector projectFromScreen(int x, int y) {
        return new Vector(
            (x / (double)screenWidth) * interval.getWidth() + interval.getLeft(),
            ((screenHeight - y) / (double)screenHeight) * interval.getHeight() + interval.getBottom()
        );
    }
    
	/**
	 * Projiziert den angegebenen Punkt von Bildschirmkoordinaten in
	 * Vektorkoordinaten.
	 * @param p Punkt in Bildschirmkoordinaten
	 * @return Punkt als Vektor
	 */
    public Vector projectFromScreen(Point p) {
        return projectFromScreen((int)p.getX(), (int)p.getY());
    }
    
	/**
	 * Projiziert den angegebenen Punkt von Vektorkoordinaten in
	 * Bildschirmkoordinaten.
	 * @param v Punkt als Vektor
	 * @return Punkt in Bildschirmkoordinaten
	 */
    public Point projectToScreen(Vector v) {
        return new Point(
            (int)((v.getX() - interval.getLeft()) / (double)interval.getWidth() * screenWidth),
            (int)(screenHeight - (v.getY() - interval.getBottom()) / (double)interval.getHeight() * screenHeight)
        );
    }
}
