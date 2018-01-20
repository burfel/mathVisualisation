package mandelbrot;

import util.Vector;

/**
 * Definiert das Werkzeug zur Berechnung von Mandelbrot-Folgen.
 */
public class ProgressionCalculator {
    public Vector characteristicValue;
    public Vector currentValue;
    
    /**
     * Beginnt eine neue Berechnung.
     * 
     * @param characteristicValue charakteristischer Wert, der für die
     * Berechnung der Folge verwendet werden soll
     */
    public void begin(Vector characteristicValue) {
        this.characteristicValue = characteristicValue;
        currentValue = new Vector(0, 0);
    }
    
    /**
     * Berechnet das nächste Glied der Folge.
     */
    public void calculate() {
        currentValue = currentValue
            .complexMultiply(currentValue)
            .plus(characteristicValue);
    }
    
    /**
     * Berechnet Folgeglieder, bis ein Konvergenzverhalten erkennbar ist.
     * 
     * @param iterations Anzahl der maximal zu berechnenden Folgeglieder
     * @return Anzahl der berechneten Folgeglieder, bis ein Konvergenzverhalten
     * erkennbar wurde oder 0 sofern bis zur maximalen Anzahl der zu
     * berechnenden Folgeglieder kein Konvergenzverhalten erkannt wurde.
     */
    public int calculate(int iterations) {
        for (int i = 0; i < iterations; ++i) {
            calculate();
            if (currentValue.getLength() > 2) return i + 1;
        }
        
        return 0;
    }
}
