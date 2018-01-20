package mandelbrot;

import java.awt.Color;

/**
 * Beschreibt eine Abbildung eines beliebig aber fest gewählten natürlichen,
 * nullbasiertem Intervall in den Farbraum.
 */
public class ColorProjection {
    public static final ColorProjection[] colorProjections
        = new ColorProjection[10];
    
    static {
        for (int i = 0; i < colorProjections.length; ++i) {
            colorProjections[i] = new ColorProjection(i);
        }
    }
    
    private final int mode;
    
    private ColorProjection(int mode) {
        this.mode = mode;
    }
    
    /**
     * Projiziert eine natürliche Zahl n aus dem Interval 0 bis max
     * (beides einschließlich) in den Farbraum.
     * 
     * @param n zu projizierende Zahl
     * @param max Obergrenze des Intervalls
     * @return erzeugte Farbe
     */
    public Color project(int n, int max) {
    	if (n == 0) return Color.BLACK;
    	
        double v = n / (double)max;
    	
        switch (mode) {
            case 0: return new Color(0, 0, (int)(Math.sqrt(v)* 255));
            case 1: return Color.getHSBColor((float)(v), 1, 1);
            case 2: return Color.getHSBColor((float)(v*v), 1, 1);
            case 3: return Color.getHSBColor((float)(3*v), 1, 1);
            case 4: return Color.getHSBColor((float)(8*v), 1, 1);
            case 5: return Color.getHSBColor((float)(max/20*v), 1, 1);
            case 6: return Color.getHSBColor((float)(max/2*v), 1, 1);
            case 7: return Color.getHSBColor((float)((max/2+1.1*max/100)*v), 1, 1);
            case 8: return Color.getHSBColor((float)(Math.log(v)), 1, 1);
            case 9: return Color.getHSBColor((float)(1), 0, 1);
    	}
        
        return Color.WHITE;
    }

    @Override
    public String toString() {
        return new String[] {
            "RGB-Blau: Wurzel-Projektion",
            "HSB: Linear",
            "HSB: Quadratisch",
            "HSB: Linear x3",
            "HSB: Linear x8",
            "HSB: Linear (dynam. Faktor)",
            "HSB: Alternierend 2 Farben",
            "HSB: Alternierender Gradient",
            "HSB: Logarithmisch",
            "Schwarz-Weiss"
        }[mode];
    }
}
