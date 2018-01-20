package util;

/**
 * Definiert ein Rechteck der zweidimensionalen Vektorebene.
 */
public class VectorRectangle {
    private final Vector min;
    private final Vector max;
    
	/**
	 * @return die obere linke Ecke
	 */
    public Vector getTopLeft() {
        return new Vector(min.getX(), max.getY());
    }
    
	/**
	 * @return die y-Koordinate der Oberkante
	 */
    public double getTop() {
        return max.getY();
    }
    
	/**
	 * @return die obere rechte Ecke
	 */
    public Vector getTopRight() {
        return max;
    }
    
	/**
	 * @return die x-Koordinate der rechten Kante
	 */
    public double getRight() {
        return max.getX();
    }
    
	/**
	 * @return die untere rechte Ecke
	 */
    public Vector getBottomRight() {
        return new Vector(max.getX(), min.getY());
    }
    
	/**
	 * @return die y-Koordinate der Unterkante
	 */
    public double getBottom() {
        return min.getY();
    }
    
	/**
	 * @return die untere linke Ecke
	 */
    public Vector getBottomLeft() {
        return min;
    }
    
	/**
	 * @return die x-Koordinate der linken Kante
	 */
    public double getLeft() {
        return min.getX();
    }
    
	/**
	 * @return der Mittelpunkt
	 */
    public Vector getCenter() {
        return new Vector((min.getX() + max.getX()) / 2, (min.getY() + max.getY()) / 2);
    }
    
	/**
	 * @return die Breite
	 */
    public double getWidth() {
        return max.getX() - min.getX();
    }
    
	/**
	 * @return die Höhe
	 */
    public double getHeight() {
        return max.getY() - min.getY();
    }
    
	/**
	 * @return die Diagonale von unten links nach oben rechts
	 */
    public Vector getSize() {
        return new Vector(getWidth(), getHeight());
    }
    
	/**
	 * Erzeugt das Rechteck, das durch die angegebenen Punkte aufgespannt wird.
	 * @param a erster Punkt
	 * @param b zweiter Punkt
	 */
    public VectorRectangle(Vector a, Vector b) {
        this.min = new Vector(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()));
        this.max = new Vector(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()));
    }
    
	/**
	 * Erzeugt das Rechteck, der angegebenen Breite und Höhe, das sich an dem
	 * angegebenen Mittelpunkt befindet.
	 * @param center Mittelpunkt
	 * @param width Breite
	 * @param height Höhe
	 */
    public VectorRectangle(Vector center, double width, double height) {
        Vector v = new Vector(width / 2, height / 2);
        this.min = center.minus(v);
        this.max = center.plus(v);
    }
    
	/**
	 * Erzeugt eine verschobene Kopie des Rechtecks.
	 * @param v positive Verschiebung
	 * @return verschobenes Rechteck
	 */
    public VectorRectangle plus(Vector v) {
        return new VectorRectangle(min.plus(v), max.plus(v));
    }
    
	/**
	 * Erzeugt eine verschobene Kopie des Rechtecks.
	 * @param v negative Verschiebung
	 * @return verschobenes Rechteck
	 */
    public VectorRectangle minus(Vector v) {
        return new VectorRectangle(min.minus(v), max.minus(v));
    }
    
	/**
	 * Erzeugt eine um den Mittelpunkt skalierte Kopie des Rechtecks.
	 * @param factor Skalierungsfaktor
	 * @return skaliertes Rechteck
	 */
    public VectorRectangle scale(double factor) {
        return scale(getCenter(), factor);
    }
    
	/**
	 * Erzeugt eine um einen Punkt skalierte Kopie des Rechtecks.
	 * @param origin Ursprung der Skalierung
	 * @param factor Skalierungsfaktor
	 * @return skaliertes Rechteck
	 */
    public VectorRectangle scale(Vector origin, double factor) {
        return new VectorRectangle(min.scale(origin, factor), max.scale(origin, factor));
    }

    @Override
    public String toString() {
        return "Rectangle(" + min + ", " + max + ")";
    }
}
