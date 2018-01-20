package util;

/**
 * Definiert Hilfsmethoden.
 */
public class Util {
	/**
	 * Erzeugt eine möglichst gut lesbare String-Darstellung der angegebenen
	 * Fließkommazahl.
	 * @param d darzustellende Fließkommazahl
	 * @return gut lesbare String-Darstellung
	 */
    public static String format(double d) {
        return format(d, 5);
    }
    
	/**
	 * Erzeugt eine möglichst gut lesbare String-Darstellung der angegebenen
	 * Fließkommazahl.
	 * @param d darzustellende Fließkommazahl
	 * @param precision Anzahl der darzustellenden Stellen
	 * @return gut lesbare String-Darstellung
	 */
    public static String format(double d, int precision) {
        int e = (int)Math.log10(d);
        return (d * Math.pow(10, -e) + "0000000000").substring(0, precision + 2) + "e" + e;
    }    
}
