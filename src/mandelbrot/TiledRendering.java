package mandelbrot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import util.ScreenProjection;
import util.Vector;
import util.VectorRectangle;

/**
 * Definiert ein übergeordnetes Rendering fester Pixelgröße, das aus Kacheln
 * von kleineren Rendering-Objekten besteht, die in verschiedener Hinsicht
 * mehr Informationen enthalten, als für die Berechnung des angegebenen
 * Bildausschnittes erforderlich ist, jedoch die schnelle Berechnung ähnlicher
 * Bildausschnitte ermöglichen.
 * Die Kacheln werden 1. in höherer als der benötigten Auflösung berechnet
 * und 2. werden Kacheln über die Ränder des gewählten Bildausschnittes hinaus
 * berechnet.
 * Diese Klassendefinition teilt sich in mehrere Bereiche.
 *  + Im Abschnitt *rearanging* werden Operationen zum Verschieben der Kacheln
 *    definiert, was bis zu gewissen Grenzen im gewählten Bildausschnitt nicht
 *    auffällt, wenn Kacheln nämlich nur in dem Maße verschoben werden, dass
 *    keine noch nicht berechneten Kacheln ins Bild wandern oder sogar der
 *    Bildausschnitt außerhalb der Kacheln liegt.
 *  + Unter *scheduling* wird eine Methode implementiert, die die noch nicht
 *    zur Berechnung reservierte Kachel auswählt, die als nächstes berechnet
 *    werden soll.
 *  + Der Vollständigkeit halber ist auch TiledRendering genauso wie Rendering
 *    ausführbar. Unter *rendering* wird der Rendering-Prozess eines
 *    TiledRenderings defniert. Dieser sollte in der Regel nicht verwendet
 *    werden, da er nicht parallelisiert und nicht dauerhaft arbeitet.
 *  + Unter *statistics* werden Methoden definiert, die Auskunft über den
 *    Status und Dauer der Berechnung geben.
 *  + Der Abschnitt *drawing* definiert Methoden, die den gewählten
 *    Bildausschnitt auf ein Graphics2D Objekt zeichnen. Dies verbraucht wenig
 *    Rechenzeit und kann in einer kontinuierlichen Darstellung für jeden Frame
 *    aufgerufen werden.
 * Zur intendierten Verwendung der Klasse TiledRendering ist ein Thread-Pool
 * erforderlich, der Worker-Threads enthält, die immer wieder getWork()
 * aufrufen und ggf. zurückgelieferte ausführbare Objekte ausführen. Es spielt
 * dabei keinerlei Rolle wie viele Threads der Thread-Pool umfasst.
 */
public class TiledRendering implements Runnable {
    public VectorRectangle interval; // aktueller Bildausschnitt
    
    public final Vector nativeSize; // kleinstes durch die aktuelle Datenlage
        // in gegebener Auflösung darstellbares Interval
    
    public final int tileWidth = 100;
    public final int tileHeight = 100;
    
    public final int tilesU; // Kacheln in U-Richtung (Realteil-Achse)
    public final int tilesV; // Kacheln in V-Richtung (Imaginärteil-Achse)
    private Rendering[][] tiles;
    public final int lowPriorityFrameU; // in tiles
    public final int lowPriorityFrameV; // in tiles
    
    public final int totalWidth;
    public final int totalHeight;
    
    public VectorRectangle totalInterval; // aktuell potenziell vorhandener
        // Bildausschnitt
    
    public final int iterations;
    public final ColorProjection colorProjection;
    
    /**
     * Erzeugt ein neues TiledRendering.
     * 
     * @param width Breite in Pixeln
     * @param height Höhe in Pixeln
     * @param frame Rahmendicke in Pixeln
     * @param lowPriorityFrame Niedrigprioritätsrahmendicke in Pixeln
     * @param scale Skalierungsfaktor, der angibt eine wie viel höhere
     * Auflösung berechnet werden soll.
     * @param interval gewünschter Bildausschnitt als Intervall der komplexen
     * Zahlenebene
     * @param colorProjection zu verwendende Farbprojektion
     * @param iterations zur Berechnung zu verwendende Iterationstiefe
     */
    public TiledRendering(int width, int height, int frame, int lowPriorityFrame,
        double scale, VectorRectangle interval,
        ColorProjection colorProjection, int iterations) {
        this.interval = interval;
        
        this.nativeSize = interval.getSize().divide(scale);
        
        this.tilesU = (int)(width * scale) /
            tileWidth + ((int)((frame + lowPriorityFrame) * scale) / tileWidth) * 2;
        this.tilesV = (int)(height * scale) /
            tileHeight + ((int)((frame + lowPriorityFrame) * scale) / tileHeight) * 2;
        this.tiles = new Rendering[tilesU][tilesV];
        
        this.lowPriorityFrameU = (int)Math.floor(lowPriorityFrame / (double)tileWidth);
        this.lowPriorityFrameV = (int)Math.floor(lowPriorityFrame / (double)tileHeight);
        
        this.totalWidth = tilesU * tileWidth;
        this.totalHeight = tilesV * tileHeight;
        
        ScreenProjection projection = new ScreenProjection(interval,
            (int)(width * scale), (int)(height * scale));
        
        this.totalInterval = new VectorRectangle(
            projection.projectFromScreen(
                new Point(
                    (int)(width * scale) / 2 - totalWidth / 2,
                    (int)(height * scale) / 2 + totalHeight / 2
                )
            ),
            projection.projectFromScreen(
                new Point(
                    (int)(width * scale) / 2 + totalWidth / 2,
                    (int)(height * scale) / 2 - totalHeight / 2
                )
            )
        );
        
        this.iterations = iterations;
        this.colorProjection = colorProjection;
        
        ScreenProjection totalProjection = new ScreenProjection(totalInterval,
            totalWidth, totalHeight);
        
        for (int u = 0; u < tilesU; ++u) {
            for (int v = 0; v < tilesV; ++v) {
                tiles[u][v] = new Rendering(
                                        tileWidth,
                    tileHeight, new VectorRectangle(
                            totalProjection.projectFromScreen(
                                new Point(u * tileWidth, (v + 1) * tileHeight)
                            ),
                            totalProjection.projectFromScreen(
                                new Point((u + 1) * tileWidth, v * tileHeight)
                            )
                    ),
                    colorProjection, iterations
                );
                
            }
        }
    }
    
    /*** rearanging ***/
    
    /**
     * Verschiebt das Kachelgitter ohne den Bildausschnitt zu verschieben.
     * 
     * @param du Verschiebung in U-Richtung. Positive Werte führen zur
     * Neuberechnung von Kacheln auf der rechten Seite.
     * @param dv Verschiebung in V-Richtung. Positive Werte führen zur
     * Neuberechnung von Kacheln auf der unteren Seite.
     */
    public void shift(int du, int dv) {
        if (du == 0 && dv == 0) return;
        
        Rendering[][] newTiles = new Rendering[tilesU][tilesV];
        
        totalInterval = new VectorRectangle(
            totalInterval.getBottomLeft().plus(
                new Vector(
                    du * tiles[0][0].interval.getWidth(),
                    -dv * tiles[0][0].interval.getHeight()
                )
            ),
            totalInterval.getTopRight().plus(
                new Vector(
                    du * tiles[0][0].interval.getWidth(),
                    -dv * tiles[0][0].interval.getHeight()
                )
            )
        );
        
        ScreenProjection totalProjection = new ScreenProjection(totalInterval,
            totalWidth, totalHeight);
        
        for (int u = 0; u < tilesU; ++u) {
            for (int v = 0; v < tilesV; ++v) {
                if (u + du >= 0 && u + du < tilesU && v + dv >= 0 && v + dv < tilesV) {
                    newTiles[u][v] = tiles[u + du][v + dv];
                }
                else {
                    newTiles[u][v] = new Rendering(
                                                tileWidth,
                        tileHeight, new VectorRectangle(
                                totalProjection.projectFromScreen(
                                    new Point(
                                        u * tileWidth,
                                        (v + 1) * tileHeight
                                    )
                                ),
                                totalProjection.projectFromScreen(
                                    new Point(
                                        (u + 1) * tileWidth,
                                        v * tileHeight
                                    )
                                )
                        ),
                        colorProjection, iterations
                    );
                }
            }
        }
        
        tiles = newTiles;
    }
    
    /**
     * Verschiebt das Kachelgitter in der Weise, dass der gewählte
     * Bildausschnitt möglichst in der mitte des Gitters liegt.
     */
    public void recenter() {
        double paddingTop = (totalInterval.getTop() - interval.getTop())
            / tiles[0][0].interval.getHeight();
        double paddingBottom = (interval.getBottom() - totalInterval.getBottom())
            / tiles[0][0].interval.getHeight();
        
        if (Math.abs(Math.floor(paddingTop) - Math.floor(paddingBottom)) >= 2) {
            shift(0, (int)Math.floor((paddingTop - paddingBottom) / 2));
        }
        
        double paddingLeft = (interval.getLeft() - totalInterval.getLeft())
            / tiles[0][0].interval.getWidth();
        double paddingRight = (totalInterval.getRight() - interval.getRight())
            / tiles[0][0].interval.getWidth();
        
        if (Math.abs(Math.floor(paddingLeft) - Math.floor(paddingRight)) >= 2) {
            shift((int)Math.floor((paddingLeft - paddingRight) / 2), 0);
        }
    }
    
    /*** scheduling ***/
    
    /**
     * Sucht die nächste zu berechnende Aufgabe und reserviert diese.
     * 
     * @param seed gleich verteilter Wert, der für jeden Thread unterschiedlich
     * sein sollte, keine wesentliche Rolle spielt und nur zu
     * Optimierungszwecken der Lastverteilung benötigt wird
     * @param skipLowPriority Angabe, ob der niedrigprioritäre Bereich als
     * mögliche Arbeitsquelle mit einbezogen werden soll
     * @return ein ausführbares Objekt oder null falls es nichts zu tun gibt
     */
    public Runnable getWork(int seed, boolean skipLowPriority) {
        int ou = skipLowPriority ? lowPriorityFrameU : 0;
        int ov = skipLowPriority ? lowPriorityFrameV : 0;
        int tu = skipLowPriority ? tilesU - 2 * lowPriorityFrameU : tilesU;
        int tv = skipLowPriority ? tilesV - 2 * lowPriorityFrameV : tilesV;
        
        int u = tu / 2;
        int v = tv / 2;
        int w = 1;
        int h = 1;
        
        while (true) {
            for (int c = 0 + seed; c < 4 + seed; ++c) {
                switch (c % 4) {
                    case 0:
                        for (int i = 0; i < w; ++i) {
                            if (tiles[ou + u + i][ov + v].tryToReserve()) {
                                return tiles[ou + u + i][ov + v];
                            }
                        }
                        break;
                        
                    case 1:
                        for (int i = 1; i < h; ++i) {
                            if (tiles[ou + u + w - 1][ov + v + i].tryToReserve()) {
                                return tiles[ou + u + w - 1][ov + v + i];
                            }
                        }
                        break;
                        
                    case 2:
                        for (int i = w - 2; i >= 0; --i) {
                            if (tiles[ou + u + i][ov + v + h - 1].tryToReserve()) {
                                return tiles[ou + u + i][ov + v + h - 1];
                            }
                        }
                        break;
                        
                    case 3:
                        for (int i = h - 2; i >= 0; --i) {
                            if (tiles[ou + u][ov + v + i].tryToReserve()) {
                                return tiles[ou + u][ov + v + i];
                            }
                        }
                        break;
                }
            }
            
            if (u == 0 && v == 0 && w == tu && h == tv) return null;
            
            u = Math.max(0, u - 1);
            v = Math.max(0, v - 1);
            w = Math.min(tu, w + 2);
            h = Math.min(tv, h + 2);
        }
    }
    
    /*** rendering ***/
    
    @Override
    public void run() {
        Runnable r;
        while ((r = getWork(0, false)) != null) r.run();
    }
    
    /*** statistics ***/
    
    /**
     * Berechnet die gesamte für alle aktuellen schon berechneten Kacheln
     * verwendete Rechenzeit.
     * 
     * @return Rechenzeit in Millisekunden.
     */
    public int getTotalTime() {
        int t = 0;
        
        for (int u = 0; u < tilesU; ++u) {
            for (int v = 0; v < tilesV; ++v) {
                t += tiles[u][v].time;
            }
        }
        
        return t;
    }
    
    /**
     * Berechnet den Anteil der aktuellen fertig berechneten Kacheln an der
     * Gesamtheit der Kacheln dieses TiledRenderings.
     * 
     * @return Fertigkeit zwischen 0 und 1
     */
    public double getCompleteness() {
        int c = 0;
        
        for (int u = 0; u < tilesU; ++u) {
            for (int v = 0; v < tilesV; ++v) {
                if (tiles[u][v].time != 0) ++c;
            }
        }
        
        return c / (double)(tilesU * tilesV);
    }
    
    /*** drawing ***/
    
    private void draw(Graphics2D g, int x, int y, int width, int height,
        VectorRectangle interval) {
        ScreenProjection projection = new ScreenProjection(interval, width, height);
        
        for (int u = 0; u < tilesU; ++u) {
            for (int v = 0; v < tilesV; ++v) {
                Point tl = projection.projectToScreen(
                    tiles[u][v].interval.getTopLeft()
                );
                Point br = projection.projectToScreen(
                    tiles[u][v].interval.getBottomRight()
                );
                
                if (br.getX() < 0) continue;
                if (br.getY() < 0) continue;
                if (tl.getX() > width) continue;
                if (tl.getY() > height) continue;
                
                g.drawImage(tiles[u][v],
                    x + (int)tl.getX(),
                    y + (int)tl.getY(),
                    (int)br.getX() - (int)tl.getX(),
                    (int)br.getY() - (int)tl.getY(),
                    null
                );
            }
        }
    }
    
    /**
     * Zeichnet den gewählten Bildausschnitt.
     * 
     * @param g das Graphics2D-Objekt, auf das gezeichnet werden soll
     * @param x x-Koordinate, an der das Bild gezeichnet werden soll in Pixeln
     * @param y y-Koordinate, an der das Bild gezeichnet werden soll in Pixeln
     * @param width Breite des zu zeichnenden Bildes in Pixeln
     * @param height Höhe des zu zeichnenden Bildes in Pixeln
     */
    public void draw(Graphics2D g, int x, int y, int width, int height) {
        draw(g, x, y, width, height, interval);
    }
    
    /**
     * Zeichnet den kompletten vorhandenen Bildausschnitt und Debuginformationen.
     * 
     * @param g das Graphics2D-Objekt, auf das gezeichnet werden soll
     * @param x x-Koordinate, an der das Bild gezeichnet werden soll in Pixeln
     * @param y y-Koordinate, an der das Bild gezeichnet werden soll in Pixeln
     * @param width Breite des zu zeichnenden Bildes in Pixeln
     * @param height Höhe des zu zeichnenden Bildes in Pixeln
     */
    public void drawDebug(Graphics2D g, int x, int y, int width, int height) {
        draw(g, x, y, width, height, totalInterval);

        ScreenProjection totalProjection = new ScreenProjection(totalInterval,
            width, height);
        
        {
            Point tl = totalProjection.projectToScreen(interval.getTopLeft());
            Point br = totalProjection.projectToScreen(interval.getBottomRight());

            g.setColor(Color.WHITE);
            g.drawRect(
                (int)tl.getX(),
                (int)tl.getY(),
                x + (int)br.getX() - (int)tl.getX(),
                y + (int)br.getY() - (int)tl.getY()
            );
            g.drawString("camera", (int)tl.getX() + 5, (int)br.getY() - 5);
        }
        
        {
            VectorRectangle cr = new VectorRectangle(
                interval.getCenter().minus(nativeSize.multiply(0.5)),
                interval.getCenter().plus(nativeSize.multiply(0.5))
            );
            
            Point tl = totalProjection.projectToScreen(cr.getTopLeft());
            Point br = totalProjection.projectToScreen(cr.getBottomRight());

            g.setColor(Color.GRAY);
            g.drawRect(
                (int)tl.getX(),
                (int)tl.getY(),
                x + (int)br.getX() - (int)tl.getX(),
                y + (int)br.getY() - (int)tl.getY()
            );
            g.drawString("native", (int)tl.getX() + 5, (int)br.getY() - 5);
        }
    }
}
