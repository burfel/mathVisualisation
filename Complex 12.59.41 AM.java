package mandelbrot3;

public class Complex {
	// version 0.1
	
    double r;
    double i;
    
    public Complex(double r, double i) {
        this.r = r;
        this.i = i;
    }
    
    public Complex plus(Complex c) {
        return new Complex(r + c.r, i + c.i);
    }
    
    public Complex multiply(Complex c) {
        return new Complex(r * c.r - i * c.i, r * c.i + i * c.r);
    }

    public double absoluteValue() {
        return Math.sqrt(r * r + i * i);
    }

    @Override
    public String toString() {
        return r + " + i" + i;
    } 
}
