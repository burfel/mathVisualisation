package util;

import java.awt.Point;

/**
 * Definiert einen zweidimensionalen Vektor, der als Punkt, Bewegung oder
 * komplexe Zahl interpretiert werden kann.
 */
public class Vector {
    private final double x;
    private final double y;
    
	/**
	 * @return x-Komponente des Vektors.
	 */
    public double getX() {
        return x;
    }
    
	/**
	 * @return y-Komponente des Vektors.
	 */
    public double getY() {
        return y;
    }
    
	/**
	 * Erzeugt einen neuen Vektor.
	 * @param x x-Komponente
	 * @param y y-Komponente
	 */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
	/**
	 * Erzeugt einen neuen Vektor.
	 * @param p Punkt, dessen Koordinaten als Komponenten verwendet werden
	 * sollen
	 */
    public Vector(Point p) {
        this(p.x, p.y);
    }
    
	/**
	 * @return Länge des Vektors
	 */
    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }
    
	/**
	 * Erzeugt eine verschobene Kopie des Vektors.
	 * @param v positive Verschiebung
	 * @return verschobene Kopie des Vektors
	 */
    public Vector plus(Vector v) {
        return new Vector(x + v.x, y + v.y);
    }
    
	/**
	 * Erzeugt eine verschobene Kopie des Vektors.
	 * @param v negative Verschiebung
	 * @return verschobene Kopie des Vektors
	 */
    public Vector minus(Vector v) {
        return new Vector(x - v.x, y - v.y);
    }
    
	/**
	 * Erzeugt eine skalierte Kopie des Vektors.
	 * @param factor Skalierungsfaktor
	 * @return skalierte Kopie
	 */
    public Vector multiply(double factor) {
        return new Vector(x * factor, y * factor);
    }
    
	/**
	 * Erzeugt einen Vektor, der als komplexe Zahl interpretiert das Produkt
	 * des aktuellen Vektors als komplexe Zahl interpretiert mit dem angegebenen
	 * Vektor als komplexe Zahl interpretiert ist.
	 * @param v komplexer Faktor
	 * @return komplexes Produkt
	 */
    public Vector complexMultiply(Vector v) {
        return new Vector(x * v.x - y * v.y, x * v.y + y * v.x);
    }
    
	/**
	 * Erzeugt eine skalierte Kopie des Vektors.
	 * @param divisor Divisor
	 * @return skalierte Kopie
	 */
    public Vector divide(double divisor) {
        return new Vector(x / divisor, y / divisor);
    }
    
	/**
	 * Erzeugt eine skalierte Kopie des Vektors.
	 * @param origin Ursprung der Skalierung
	 * @param factor Skalierungsfaktor
	 * @return skalierte Kopie
	 */
    public Vector scale(Vector origin, double factor) {
        return this.minus(origin).multiply(factor).plus(origin);
    }
    
    @Override
    public String toString() {
        return "Vector(" + x + ", " + y + ")";
    }
    
	/**
	 * @return möglichst gut lesbare String-Darstellung des als komplexe Zahl
	 * interpretierten Vektors.
	 */
    public String complexToString() {
        return Util.format(getX()) + " " + (getY() >= 0 ? "+" : "-") + " " + Util.format(Math.abs(getY())) + "i";
    }
    
	/**
	 * @return Punkt, der die auf ganze Zahlen gerundeten Komponenten des
	 * aktuellen Vektors als Koordinaten hat.
	 */
    public Point toPoint() {
        return new Point((int)x, (int)y);
    }
}
