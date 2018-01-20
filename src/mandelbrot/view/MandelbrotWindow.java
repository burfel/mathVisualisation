package mandelbrot.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mandelbrot.ColorProjection;
import mandelbrot.Mandelbrot;

/**
 * Definiert das Hauptfenster der Anwendung.
 */
public class MandelbrotWindow extends JFrame {
    public MandelbrotWindow(final Mandelbrot mandelbrot) {
        setSize(1000, 670);
        setLayout(new BorderLayout());
        setTitle("Mandelbrot");
        setVisible(true);
        
        {
            RenderingView rv = new RenderingView(mandelbrot);
            add(rv, BorderLayout.CENTER);
        }
        
        final JTextField iterationsTextField;
        final JComboBox colorProjectionComboBox;
        final JComboBox pointsColorProjectionComboBox;
        
        {
            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BorderLayout());
            add(sidebar, BorderLayout.EAST);
            
            {
                MetaView mv = new MetaView(mandelbrot);
                sidebar.add(mv, BorderLayout.CENTER);
            }
            
            {
                JPanel topright = new JPanel();
                topright.setLayout(new BorderLayout());
                sidebar.add(topright, BorderLayout.NORTH);
                
                {
                    JPanel inspector = new JPanel();
                    inspector.setBorder(BorderFactory.createTitledBorder("options"));
                    inspector.setLayout(new BoxLayout(inspector, BoxLayout.Y_AXIS));
                    topright.add(inspector, BorderLayout.NORTH);

                    {
                        final JSlider js = new JSlider();
                        js.setBorder(BorderFactory.createTitledBorder("zoom speed"));
                        js.setMinimum(1);
                        js.setMaximum(25);
                        js.setValue((int)(mandelbrot.zoomFactor * 100));
                        js.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                mandelbrot.zoomFactor = js.getValue() / 100d;
                            }
                        });
                        inspector.add(js);
                    }

                    {
                        iterationsTextField = new JTextField();
                        iterationsTextField.setBorder(BorderFactory.createTitledBorder("iterations"));
                        iterationsTextField.setText(mandelbrot.iterations + "");
                        inspector.add(iterationsTextField);
                    }

                    {
                        colorProjectionComboBox = new JComboBox();
                        colorProjectionComboBox.setBorder(BorderFactory.createTitledBorder("color projection"));
                        for (ColorProjection cp: ColorProjection.colorProjections) colorProjectionComboBox.addItem(cp);
                        colorProjectionComboBox.setSelectedItem(mandelbrot.colorProjection);
                        inspector.add(colorProjectionComboBox);
                    }

                    {
                        pointsColorProjectionComboBox = new JComboBox();
                        pointsColorProjectionComboBox.setBorder(BorderFactory.createTitledBorder("points color projection"));
                        for (ColorProjection cp: ColorProjection.colorProjections) pointsColorProjectionComboBox.addItem(cp);
                        pointsColorProjectionComboBox.setSelectedItem(mandelbrot.pointsColorProjection);
                        inspector.add(pointsColorProjectionComboBox);
                    }
                    
                    {
                        JButton jb = new JButton();
                        jb.setText("refresh");
                        jb.setAlignmentX(CENTER_ALIGNMENT);
                        jb.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                mandelbrot.iterations = Integer.parseInt(iterationsTextField.getText());
                                mandelbrot.colorProjection = (ColorProjection)colorProjectionComboBox.getSelectedItem();
                                mandelbrot.pointsColorProjection = (ColorProjection)pointsColorProjectionComboBox.getSelectedItem();
                                mandelbrot.refresh();
                            }
                        });
                        inspector.add(jb);
                    }
                }
                
                {
                    JPanel buttons = new JPanel();
                    buttons.setBorder(BorderFactory.createTitledBorder("buttons"));
                    buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
                    topright.add(buttons, BorderLayout.SOUTH);

                    {
                        JButton jb = new JButton();
                        jb.setText("cache1");
                        jb.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                JFrame jf = new JFrame();
                                jf.setLocation(0, 0);
                                jf.setSize(400, 300);
                                jf.setLayout(new BorderLayout());
                                jf.setTitle("cache1");
                                jf.setVisible(true);

                                {
                                    RenderingCacheView rcv = new RenderingCacheView(mandelbrot, false);
                                    jf.add(rcv, BorderLayout.CENTER);
                                }
                            }
                        });
                        buttons.add(jb);
                    }

                    {
                        JButton jb = new JButton();
                        jb.setText("cache2");
                        jb.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                JFrame jf = new JFrame();
                                jf.setLocation(400, 0);
                                jf.setSize(400, 300);
                                jf.setLayout(new BorderLayout());
                                jf.setTitle("cache2");
                                jf.setVisible(true);

                                {
                                    RenderingCacheView rcv = new RenderingCacheView(mandelbrot, true);
                                    jf.add(rcv, BorderLayout.CENTER);
                                }
                            }
                        });
                        buttons.add(jb);
                    }
                    
                    {
                        JButton jb = new JButton();
                        jb.setText("restart");
                        jb.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                mandelbrot.cache1.interval = mandelbrot.firstInterval;
                                mandelbrot.refresh();
                            }
                        });
                        buttons.add(jb);
                    }
                }
            }
        }
        
        revalidate();
    }
}
