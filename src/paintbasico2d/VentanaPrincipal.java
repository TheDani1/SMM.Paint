/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package paintbasico2d;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import sm.dgs.graficos.Arco2D;
import sm.dgs.graficos.Curva2D;
import sm.dgs.graficos.CurvaCubica;
import sm.dgs.graficos.Elipse2D;
import sm.dgs.graficos.Linea2D;
import sm.dgs.graficos.MiFigura;
import sm.dgs.graficos.Poligono;
import sm.dgs.graficos.Rectangulo2D;
import sm.dgs.graficos.RoundRectangulo2D;
import sm.dgs.graficos.TrazoLibre2D;
import sm.dgs.imagen.ColorBordeOp;
import sm.dgs.imagen.MiOp;
import sm.dgs.imagen.PosterizarOp;
import sm.dgs.imagen.RojoOp;
import sm.dgs.iu.Lienzo2D.Figura;
import sm.dgs.iu.LienzoAdapter;
import sm.dgs.iu.LienzoEvent;
import sm.image.EqualizationOp;
import sm.image.Histogram;
import sm.image.KernelProducer;
import sm.image.LookupTableProducer;
import sm.image.SepiaOp;
import sm.image.TintOp;
import sm.image.color.GreyColorSpace;

/**
 *
 * @author danielgs
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    private BufferedImage imgFuente;
    private JFileChooser chooser;

    public LookupTable cuadratica(double m) {

        double Max;

        if (m >= 128.0) {
            Max = (double) ((1.0 / 100.0) * Math.pow((float) 0.0 - m, 2));
        } else {
            Max = (double) (1.0 / 100.0) * Math.pow((float) 255.0 - m, 2);
        }
        double K = 255.0 / Max;

        byte lt[] = new byte[256];

        for (int l = 0; l < 256; l++) {
            lt[l] = (byte) (K * ((1.0 / 100.0) * Math.pow((float) l - m, 2)));
        }

        ByteLookupTable slt = new ByteLookupTable(0, lt);
        return slt;

    }

    public LookupTable trapezoide(double a, double b) {

//        double Max = (m >= 128) ? 0 : 255;
        double K = 1.0 / 255.0;

        byte lt[] = new byte[256];
        for (int l = 1; l < 256; l++) {

            if (l >= 0) {
                lt[l] = 0;
                lt[l] = (byte) (K * lt[l]);
            } else if (0 < l && l < a) {
                lt[l] = (byte) (l / a);
                lt[l] = (byte) (K * lt[l]);
            } else if (a <= l && l <= b) {
                lt[l] = (byte) 1;
                lt[l] = (byte) (K * lt[l]);
            } else if (b < l && l < 255) {
                lt[l] = (byte) (255.0 - (double) l / 255.0 - b);
                lt[l] = (byte) (K * lt[l]);
            } else if (l >= 255) {
                lt[l] = 0;
                lt[l] = (byte) (K * lt[l]);
            }
            //lt[l] = (byte) (K * (1.0 / 100.0f * Math.pow(l - (double) m, 2)));
        }

        ByteLookupTable slt = new ByteLookupTable(0, lt);
        return slt;

    }

    protected JFileChooser getFileChooser() {
        if (chooser == null) {
            chooser = new JFileChooser();
            FileFilter jpg = new FileNameExtensionFilter("JPG (.jpg)", "jpg");
            FileFilter png = new FileNameExtensionFilter("PNG (.png)", "png");
            FileFilter jpeg = new FileNameExtensionFilter("JPEG (.jpeg)", "jpeg");
            FileFilter gif = new FileNameExtensionFilter("GIF (.gif)", "gif");
            chooser.addChoosableFileFilter(jpg);
            chooser.addChoosableFileFilter(png);
            chooser.addChoosableFileFilter(jpeg);
            chooser.addChoosableFileFilter(gif);
            chooser.setFileFilter(jpg);
//            chooser.setFileFilter(png);
            chooser.setAcceptAllFileFilterUsed(false);
        }
        return chooser;
    }

    // 1) Definir la clase manejadora y sobrecargar los metodos que sean necesarios
    private class ManejadorVentanaInterna extends InternalFrameAdapter {

        @Override
        public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {

            VentanaInterna vi = (VentanaInterna) evt.getInternalFrame();
            Color lcolor = vi.getLienzo().getColor();
            Figura sfigura = vi.getLienzo().getFigura();

            rellenoToggleButton.setSelected(vi.getLienzo().getRelleno());
            transparenciaToggleButton.setSelected(vi.getLienzo().getTransparencia());
            alisarToggleButton.setSelected(vi.getLienzo().getAntialiasing());

            if (lcolor == Color.BLACK) {

                ComboBoxColors.setSelectedIndex(0);

            } else if (lcolor == Color.RED) {

                ComboBoxColors.setSelectedIndex(1);

            } else if (lcolor == Color.BLUE) {

                ComboBoxColors.setSelectedIndex(2);

            } else if (lcolor == Color.WHITE) {

                ComboBoxColors.setSelectedIndex(3);

            } else if (lcolor == Color.YELLOW) {

                ComboBoxColors.setSelectedIndex(4);

            } else if (lcolor == Color.GREEN) {

                ComboBoxColors.setSelectedIndex(5);

            }

            if (null != sfigura) {
                switch (sfigura) {
                    case TRAZO_LIBRE:
                        botonTrazoLibre.setSelected(true);
                        break;
                    case LINEA:
                        botonLinea.setSelected(true);
                        break;
                    case RECTANGULO:
                        botonRectan.setSelected(true);
                        break;
                    case ELIPSE:
                        botonElipse.setSelected(true);
                        break;
                    default:
                        break;
                }
            }

            ToggleButtonMover.setSelected(vi.getLienzo().getMover());

        }

    }

    /**
     * Creates new form VentanaPrincipal
     */
    public VentanaPrincipal() {
        initComponents();
    }

    public class MiManejadorLienzo extends LienzoAdapter {

        public void shapeAdded(LienzoEvent evt) {

            VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();

            labelEstado.setText("[EVT] Figura " + " x " + "añadida");
            
            // TODO: SOBRECARGAR MÉTODO STRING EN CADA FIGURA
            
            ArrayList<MiFigura> listaFiguras = vi.getLienzo().getvShape();

            DefaultComboBoxModel modelo= new DefaultComboBoxModel();
            
            for (int i = 0; i < listaFiguras.size(); i++) {
                
                Shape figura = listaFiguras.get(i).getTipoFigura();
               
                if(figura instanceof Rectangulo2D){
                    modelo.addElement(modelo.getSize()+1 +". Rectángulo2D");
                }else if (figura instanceof TrazoLibre2D){
                    modelo.addElement(modelo.getSize()+1 +". TrazoLibre2D");
                }else if(figura instanceof Linea2D){
                    modelo.addElement(modelo.getSize()+1 +". Linea2D");
                }else if(figura instanceof Elipse2D){
                    modelo.addElement(modelo.getSize()+1 +". Elipse2D");
                }else if(figura instanceof Curva2D){
                    modelo.addElement(modelo.getSize()+1 +". Curva2D");
                }else if(figura instanceof RoundRectangulo2D){
                    modelo.addElement(modelo.getSize()+1 +". Rectángulo Redondo");
                }else if(figura instanceof Arco2D){
                    modelo.addElement(modelo.getSize()+1 +". Arco2D");
                }else if(figura instanceof CurvaCubica){
                    modelo.addElement(modelo.getSize()+1 +". CurvaCubica");
                }else if(figura instanceof Poligono){
                    modelo.addElement(modelo.getSize()+1 +". Poligono");
                }else if(figura instanceof Area){
                    modelo.addElement(modelo.getSize()+1 +". Area");
                }else{
                    modelo.addElement(modelo.getSize()+1 +". Figura");
                }
                
            }

            comboBoxObjetos.setModel(modelo);
        }

        public void propertyChange(LienzoEvent evt) {

            labelEstado.setText("Figura" + " x " + "seleccionada" + "Transparencia: " + evt.getfigura().getTransparencia() + " Alisar: " + evt.getfigura().getAntialiasing());

            Color lcolor = evt.getfigura().getColor();
            Shape sfigura = evt.getfigura().getTipoFigura();

            rellenoToggleButton.setSelected(evt.getfigura().getRelleno());
            transparenciaToggleButton.setSelected(evt.getfigura().getTransparencia());
            alisarToggleButton.setSelected(evt.getfigura().getAntialiasing());

            if (lcolor == Color.BLACK) {

                ComboBoxColors.setSelectedIndex(0);

            } else if (lcolor == Color.RED) {

                ComboBoxColors.setSelectedIndex(1);

            } else if (lcolor == Color.BLUE) {

                ComboBoxColors.setSelectedIndex(2);

            } else if (lcolor == Color.WHITE) {

                ComboBoxColors.setSelectedIndex(3);

            } else if (lcolor == Color.YELLOW) {

                ComboBoxColors.setSelectedIndex(4);

            } else if (lcolor == Color.GREEN) {

                ComboBoxColors.setSelectedIndex(5);

            }

            if (sfigura instanceof Linea2D) {
                botonLinea.setSelected(true);
            } else if (sfigura instanceof Rectangulo2D) {
                botonRectan.setSelected(true);
            } else if (sfigura instanceof Elipse2D) {
                botonElipse.setSelected(true);
            } else if (sfigura instanceof TrazoLibre2D) {
                botonTrazoLibre.setSelected(true);
            }

            int a = evt.getfigura().getGrosor_trazo().intValue();
            spinnerGrosor.setValue(a);

            Composite composicion = evt.getfigura().getComposicion();

            //sliderTransparencia.setValue();
        }

    }

    public static float getFloat(JSpinner spinner) {
        float rv = 0;
        Object o = spinner.getValue();
        if (o != null) {
            if (o instanceof Number) {
                rv = ((Number) o).floatValue();
            }//  w ww .j a  v  a2s.  c om
        }
        return rv;
    }

    private BufferedImage getImageBand(BufferedImage img, int banda) {
        //Creamos el modelo de color de la nueva imagen basado en un espcio de color GRAY
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ComponentColorModel cm = new ComponentColorModel(cs, false, false,
                Transparency.OPAQUE,
                DataBuffer.TYPE_BYTE);

        //Creamos el nuevo raster a partir del raster de la imagen original
        int vband[] = {banda};
        WritableRaster bRaster = (WritableRaster) img.getRaster().createWritableChild(0, 0,
                img.getWidth(), img.getHeight(), 0, 0, vband);

        //Creamos una nueva imagen que contiene como raster el correspondiente a la banda
        return new BufferedImage(cm, bRaster, false, null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        figuras = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jPanel12 = new javax.swing.JPanel();
        botonNuevo = new javax.swing.JButton();
        botonAbrir = new javax.swing.JButton();
        botonGuardar = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        botonTrazoLibre = new javax.swing.JToggleButton();
        botonLinea = new javax.swing.JToggleButton();
        botonRectan = new javax.swing.JToggleButton();
        botonElipse = new javax.swing.JToggleButton();
        botonCurva = new javax.swing.JToggleButton();
        bRoundRectangle = new javax.swing.JToggleButton();
        bArc2D = new javax.swing.JToggleButton();
        bCubicCurve = new javax.swing.JToggleButton();
        bPolygon = new javax.swing.JToggleButton();
        bArea = new javax.swing.JToggleButton();
        comboBoxObjetos = new javax.swing.JComboBox<>();
        jPanel15 = new javax.swing.JPanel();
        Color colores[] = {Color.BLACK, Color.RED, Color.BLUE, Color.WHITE, Color.YELLOW, Color.GREEN};
        ComboBoxColors = new javax.swing.JComboBox<>(colores);
        alisarToggleButton = new javax.swing.JToggleButton();
        jPanel16 = new javax.swing.JPanel();
        Color coloresRelleno[] = {Color.BLACK, Color.RED, Color.BLUE, Color.WHITE, Color.YELLOW, Color.GREEN};
        ComboBoxColorsRelleno = new javax.swing.JComboBox<>(coloresRelleno);
        rellenoToggleButton = new javax.swing.JToggleButton();
        jPanel19 = new javax.swing.JPanel();
        sliderTransparencia = new javax.swing.JSlider();
        transparenciaToggleButton = new javax.swing.JToggleButton();
        jPanel18 = new javax.swing.JPanel();
        spinnerGrosor = new javax.swing.JSpinner();
        Color coloresTrazo[] = {Color.BLACK, Color.RED, Color.BLUE, Color.WHITE, Color.YELLOW, Color.GREEN};
        ComboBoxColorsTrazo = new javax.swing.JComboBox<>(coloresTrazo);
        jPanel14 = new javax.swing.JPanel();
        ToggleButtonMover = new javax.swing.JToggleButton();
        botonSeleccionador = new javax.swing.JToggleButton();
        bVolcado = new javax.swing.JToggleButton();
        jPanel20 = new javax.swing.JPanel();
        bMi = new javax.swing.JButton();
        bHistograma = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        escritorio = new javax.swing.JDesktopPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        sliderBrillo = new javax.swing.JSlider();
        sliderContraste = new javax.swing.JSlider();
        jPanel9 = new javax.swing.JPanel();
        sliderFiltroMedia = new javax.swing.JSlider();
        jPanel7 = new javax.swing.JPanel();
        ComboBoxFiltros = new javax.swing.JComboBox<>();
        jPanel11 = new javax.swing.JPanel();
        botonContraste = new javax.swing.JButton();
        bIluminar = new javax.swing.JButton();
        bOscurecer = new javax.swing.JButton();
        bCuadratica = new javax.swing.JButton();
        bTrapezoide = new javax.swing.JToggleButton();
        jPanel10 = new javax.swing.JPanel();
        slider360 = new javax.swing.JSlider();
        b90 = new javax.swing.JButton();
        b180 = new javax.swing.JButton();
        b270 = new javax.swing.JButton();
        bAumentar = new javax.swing.JButton();
        bDisminuir = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        bExtraccionBandas = new javax.swing.JButton();
        comboBoxEspaciosColor = new javax.swing.JComboBox<>();
        jPanel17 = new javax.swing.JPanel();
        bCombinacion = new javax.swing.JButton();
        bTintar = new javax.swing.JButton();
        bSepia = new javax.swing.JButton();
        bEcualizador = new javax.swing.JButton();
        bRojo = new javax.swing.JButton();
        jNumColors = new javax.swing.JSlider();
        sliderColorBorde = new javax.swing.JSlider();
        jPanel6 = new javax.swing.JPanel();
        labelEstado = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuArchivo = new javax.swing.JMenu();
        menuNuevo = new javax.swing.JMenuItem();
        menuAbrir = new javax.swing.JMenuItem();
        menuGuardar = new javax.swing.JMenuItem();
        menuEdicion = new javax.swing.JMenu();
        menuImagen = new javax.swing.JMenu();
        menuRescaleOp = new javax.swing.JMenuItem();
        menuConvolveOp = new javax.swing.JMenuItem();
        menuAyuda = new javax.swing.JMenu();
        itemAcercade = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jToolBar1.setRollover(true);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Fichero"));
        jPanel12.setAlignmentY(0.7F);
        jPanel12.setPreferredSize(new java.awt.Dimension(135, 70));
        jPanel12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));

        botonNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevo.png"))); // NOI18N
        botonNuevo.setToolTipText("Nuevo");
        botonNuevo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonNuevo.setPreferredSize(new java.awt.Dimension(35, 35));
        botonNuevo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonNuevoActionPerformed(evt);
            }
        });
        jPanel12.add(botonNuevo);

        botonAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/abrir.png"))); // NOI18N
        botonAbrir.setToolTipText("Abrir");
        botonAbrir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonAbrir.setPreferredSize(new java.awt.Dimension(35, 35));
        botonAbrir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAbrirActionPerformed(evt);
            }
        });
        jPanel12.add(botonAbrir);

        botonGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/guardar.png"))); // NOI18N
        botonGuardar.setToolTipText("Guardar");
        botonGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonGuardar.setPreferredSize(new java.awt.Dimension(35, 35));
        botonGuardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGuardarActionPerformed(evt);
            }
        });
        jPanel12.add(botonGuardar);

        jToolBar1.add(jPanel12);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Figuras"));
        jPanel13.setPreferredSize(new java.awt.Dimension(600, 50));
        jPanel13.setRequestFocusEnabled(false);
        jPanel13.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));

        figuras.add(botonTrazoLibre);
        botonTrazoLibre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/trazo.png"))); // NOI18N
        botonTrazoLibre.setToolTipText("Trazo Libre");
        botonTrazoLibre.setPreferredSize(new java.awt.Dimension(35, 35));
        botonTrazoLibre.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonTrazoLibre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonTrazoLibreActionPerformed(evt);
            }
        });
        jPanel13.add(botonTrazoLibre);

        figuras.add(botonLinea);
        botonLinea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/linea.png"))); // NOI18N
        botonLinea.setToolTipText("Línea");
        botonLinea.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonLinea.setPreferredSize(new java.awt.Dimension(35, 35));
        botonLinea.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonLinea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonLineaActionPerformed(evt);
            }
        });
        jPanel13.add(botonLinea);

        figuras.add(botonRectan);
        botonRectan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rectangulo.png"))); // NOI18N
        botonRectan.setToolTipText("Rectángulo");
        botonRectan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonRectan.setPreferredSize(new java.awt.Dimension(35, 35));
        botonRectan.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonRectan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRectanActionPerformed(evt);
            }
        });
        jPanel13.add(botonRectan);

        figuras.add(botonElipse);
        botonElipse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/elipse.png"))); // NOI18N
        botonElipse.setToolTipText("Elipse");
        botonElipse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonElipse.setPreferredSize(new java.awt.Dimension(35, 35));
        botonElipse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonElipse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonElipseActionPerformed(evt);
            }
        });
        jPanel13.add(botonElipse);

        figuras.add(botonCurva);
        botonCurva.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/curva.png"))); // NOI18N
        botonCurva.setToolTipText("Curva");
        botonCurva.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonCurva.setPreferredSize(new java.awt.Dimension(35, 35));
        botonCurva.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonCurva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCurvaActionPerformed(evt);
            }
        });
        jPanel13.add(botonCurva);

        figuras.add(bRoundRectangle);
        bRoundRectangle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/7968882_rectangle_adobe illustrator tool_rectangle tool_icon.png"))); // NOI18N
        bRoundRectangle.setToolTipText("Rectángulo Redondo");
        bRoundRectangle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bRoundRectangle.setPreferredSize(new java.awt.Dimension(35, 35));
        bRoundRectangle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bRoundRectangle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRoundRectangleActionPerformed(evt);
            }
        });
        jPanel13.add(bRoundRectangle);

        figuras.add(bArc2D);
        bArc2D.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/7832925_arc_design tools_tool_tools_icon.png"))); // NOI18N
        bArc2D.setToolTipText("Arco");
        bArc2D.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bArc2D.setPreferredSize(new java.awt.Dimension(35, 35));
        bArc2D.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bArc2D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bArc2DActionPerformed(evt);
            }
        });
        jPanel13.add(bArc2D);

        figuras.add(bCubicCurve);
        bCubicCurve.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/9043763_3d_curve_auto_colon_icon.png"))); // NOI18N
        bCubicCurve.setToolTipText("Curva Cúbica");
        bCubicCurve.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bCubicCurve.setPreferredSize(new java.awt.Dimension(35, 35));
        bCubicCurve.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bCubicCurve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCubicCurveActionPerformed(evt);
            }
        });
        jPanel13.add(bCubicCurve);

        figuras.add(bPolygon);
        bPolygon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/9054997_bx_polygon_icon.png"))); // NOI18N
        bPolygon.setToolTipText("Polígono");
        bPolygon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bPolygon.setPreferredSize(new java.awt.Dimension(35, 35));
        bPolygon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bPolygon.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                bPolygonStateChanged(evt);
            }
        });
        bPolygon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPolygonActionPerformed(evt);
            }
        });
        jPanel13.add(bPolygon);

        figuras.add(bArea);
        bArea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/216130_area_chart_icon.png"))); // NOI18N
        bArea.setToolTipText("Área");
        bArea.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bArea.setPreferredSize(new java.awt.Dimension(35, 35));
        bArea.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAreaActionPerformed(evt);
            }
        });
        jPanel13.add(bArea);

        comboBoxObjetos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ninguna figura" }));
        comboBoxObjetos.setPreferredSize(new java.awt.Dimension(170, 25));
        comboBoxObjetos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxObjetosActionPerformed(evt);
            }
        });
        jPanel13.add(comboBoxObjetos);

        jToolBar1.add(jPanel13);

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Atributos"));
        jPanel15.setPreferredSize(new java.awt.Dimension(555, 50));

        ComboBoxColors.setToolTipText("Color Figura");
        ComboBoxColors.setPreferredSize(new java.awt.Dimension(35, 35));
        ComboBoxColors.setRenderer(new ColorCellRender());
        ComboBoxColors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBoxColorsActionPerformed(evt);
            }
        });
        jPanel15.add(ComboBoxColors);

        alisarToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/alisar.png"))); // NOI18N
        alisarToggleButton.setToolTipText("Alisado");
        alisarToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        alisarToggleButton.setPreferredSize(new java.awt.Dimension(35, 35));
        alisarToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        alisarToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alisarToggleButtonActionPerformed(evt);
            }
        });
        jPanel15.add(alisarToggleButton);

        jPanel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        ComboBoxColorsRelleno.setToolTipText("Color Relleno");
        ComboBoxColorsRelleno.setPreferredSize(new java.awt.Dimension(35, 35));
        ComboBoxColorsRelleno.setRenderer(new ColorCellRender());
        ComboBoxColorsRelleno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBoxColorsRellenoActionPerformed(evt);
            }
        });
        jPanel16.add(ComboBoxColorsRelleno);

        rellenoToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rellenar.png"))); // NOI18N
        rellenoToggleButton.setToolTipText("Relleno");
        rellenoToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rellenoToggleButton.setPreferredSize(new java.awt.Dimension(35, 35));
        rellenoToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rellenoToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rellenoToggleButtonActionPerformed(evt);
            }
        });
        jPanel16.add(rellenoToggleButton);

        jPanel15.add(jPanel16);

        jPanel19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        sliderTransparencia.setMaximum(10);
        sliderTransparencia.setToolTipText("Deslizador Transparencia");
        sliderTransparencia.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderTransparenciaStateChanged(evt);
            }
        });
        jPanel19.add(sliderTransparencia);

        transparenciaToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/transparencia.png"))); // NOI18N
        transparenciaToggleButton.setToolTipText("Transparencia");
        transparenciaToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        transparenciaToggleButton.setPreferredSize(new java.awt.Dimension(35, 35));
        transparenciaToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        transparenciaToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transparenciaToggleButtonActionPerformed(evt);
            }
        });
        jPanel19.add(transparenciaToggleButton);

        jPanel15.add(jPanel19);

        jPanel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        spinnerGrosor.setToolTipText("Grosor Trazo");
        spinnerGrosor.setPreferredSize(new java.awt.Dimension(50, 25));
        spinnerGrosor.setValue(5.0f);
        spinnerGrosor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerGrosorStateChanged(evt);
            }
        });
        jPanel18.add(spinnerGrosor);

        ComboBoxColorsTrazo.setToolTipText("Color Trazo");
        ComboBoxColorsTrazo.setPreferredSize(new java.awt.Dimension(35, 35));
        ComboBoxColorsTrazo.setRenderer(new ColorCellRender());
        ComboBoxColorsTrazo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBoxColorsTrazoActionPerformed(evt);
            }
        });
        jPanel18.add(ComboBoxColorsTrazo);

        jPanel15.add(jPanel18);

        jToolBar1.add(jPanel15);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Tools"));
        jPanel14.setPreferredSize(new java.awt.Dimension(160, 50));

        ToggleButtonMover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/seleccion.png"))); // NOI18N
        ToggleButtonMover.setToolTipText("Mover");
        ToggleButtonMover.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ToggleButtonMover.setPreferredSize(new java.awt.Dimension(35, 35));
        ToggleButtonMover.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ToggleButtonMover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ToggleButtonMoverActionPerformed(evt);
            }
        });
        jPanel14.add(ToggleButtonMover);

        botonSeleccionador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/9111124_select_icon (1).png"))); // NOI18N
        botonSeleccionador.setToolTipText("Selección");
        botonSeleccionador.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonSeleccionador.setPreferredSize(new java.awt.Dimension(35, 35));
        botonSeleccionador.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonSeleccionador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSeleccionadorActionPerformed(evt);
            }
        });
        jPanel14.add(botonSeleccionador);

        bVolcado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/image77.png"))); // NOI18N
        bVolcado.setPreferredSize(new java.awt.Dimension(35, 35));
        bVolcado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bVolcadoActionPerformed(evt);
            }
        });
        jPanel14.add(bVolcado);

        jToolBar1.add(jPanel14);

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Mi Operación"));
        jPanel20.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel20.setPreferredSize(new java.awt.Dimension(110, 50));

        bMi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imágenes ejemplo-20220512/image133.png"))); // NOI18N
        bMi.setToolTipText("Operación Propia");
        bMi.setPreferredSize(new java.awt.Dimension(35, 35));
        bMi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bMiActionPerformed(evt);
            }
        });
        jPanel20.add(bMi);

        bHistograma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/image63.png"))); // NOI18N
        bHistograma.setToolTipText("Histograma");
        bHistograma.setPreferredSize(new java.awt.Dimension(35, 35));
        bHistograma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bHistogramaActionPerformed(evt);
            }
        });
        jPanel20.add(bHistograma);

        jToolBar1.add(jPanel20);

        jPanel1.add(jToolBar1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout escritorioLayout = new javax.swing.GroupLayout(escritorio);
        escritorio.setLayout(escritorioLayout);
        escritorioLayout.setHorizontalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2364, Short.MAX_VALUE)
        );
        escritorioLayout.setVerticalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
        );

        jPanel2.add(escritorio, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Brillo y contraste"));
        jPanel8.setLayout(new java.awt.GridLayout(1, 2));

        sliderBrillo.setMaximum(255);
        sliderBrillo.setMinimum(-255);
        sliderBrillo.setToolTipText("Deslizador Brillo");
        sliderBrillo.setValue(0);
        sliderBrillo.setPreferredSize(new java.awt.Dimension(200, 35));
        sliderBrillo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderBrilloStateChanged(evt);
            }
        });
        sliderBrillo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderBrilloFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderBrilloFocusLost(evt);
            }
        });
        jPanel8.add(sliderBrillo);

        sliderContraste.setMaximum(20);
        sliderContraste.setToolTipText("Deslizador Contraste");
        sliderContraste.setValue(10);
        sliderContraste.setPreferredSize(new java.awt.Dimension(200, 35));
        sliderContraste.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderContrasteStateChanged(evt);
            }
        });
        sliderContraste.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderContrasteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderContrasteFocusLost(evt);
            }
        });
        jPanel8.add(sliderContraste);

        jPanel4.add(jPanel8);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Filtros"));
        jPanel9.setLayout(new java.awt.BorderLayout());

        sliderFiltroMedia.setMaximum(31);
        sliderFiltroMedia.setMinimum(1);
        sliderFiltroMedia.setToolTipText("Deslizador Filtro");
        sliderFiltroMedia.setValue(0);
        sliderFiltroMedia.setPreferredSize(new java.awt.Dimension(200, 35));
        sliderFiltroMedia.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderFiltroMediaStateChanged(evt);
            }
        });
        sliderFiltroMedia.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderFiltroMediaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderFiltroMediaFocusLost(evt);
            }
        });
        jPanel9.add(sliderFiltroMedia, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel9);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Filtros"));
        jPanel7.setLayout(new java.awt.BorderLayout());

        ComboBoxFiltros.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Emborronamiento media", "Emborronamiento binomial", "Enfoque", "Relieve", "Detector de fronteras laplaciano", "Emborronamiento media5x5", "Emborronamiento media7x7" }));
        ComboBoxFiltros.setToolTipText("Filtros");
        ComboBoxFiltros.setPreferredSize(new java.awt.Dimension(247, 35));
        ComboBoxFiltros.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ComboBoxFiltrosFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ComboBoxFiltrosFocusLost(evt);
            }
        });
        ComboBoxFiltros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBoxFiltrosActionPerformed(evt);
            }
        });
        jPanel7.add(ComboBoxFiltros, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel7);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Transformaciones"));

        botonContraste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/contraste.png"))); // NOI18N
        botonContraste.setToolTipText("Contraste");
        botonContraste.setPreferredSize(new java.awt.Dimension(35, 35));
        botonContraste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonContrasteActionPerformed(evt);
            }
        });
        jPanel11.add(botonContraste);

        bIluminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iluminar.png"))); // NOI18N
        bIluminar.setToolTipText("Iluminar");
        bIluminar.setPreferredSize(new java.awt.Dimension(35, 35));
        bIluminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bIluminarActionPerformed(evt);
            }
        });
        jPanel11.add(bIluminar);

        bOscurecer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/oscurecer.png"))); // NOI18N
        bOscurecer.setToolTipText("Oscurecer");
        bOscurecer.setPreferredSize(new java.awt.Dimension(35, 35));
        bOscurecer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOscurecerActionPerformed(evt);
            }
        });
        jPanel11.add(bOscurecer);

        bCuadratica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cuadratica.png"))); // NOI18N
        bCuadratica.setToolTipText("Función Cuadrática");
        bCuadratica.setPreferredSize(new java.awt.Dimension(35, 35));
        bCuadratica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCuadraticaActionPerformed(evt);
            }
        });
        jPanel11.add(bCuadratica);

        bTrapezoide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/trapezoid.png"))); // NOI18N
        bTrapezoide.setToolTipText("Función Trapezoidal");
        bTrapezoide.setPreferredSize(new java.awt.Dimension(35, 35));
        bTrapezoide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bTrapezoideActionPerformed(evt);
            }
        });
        jPanel11.add(bTrapezoide);

        jPanel4.add(jPanel11);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Rotación y escalado"));

        slider360.setMajorTickSpacing(72);
        slider360.setMaximum(360);
        slider360.setPaintTicks(true);
        slider360.setSnapToTicks(true);
        slider360.setToolTipText("Slider Rotación");
        slider360.setValue(0);
        slider360.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider360StateChanged(evt);
            }
        });
        slider360.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                slider360FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                slider360FocusLost(evt);
            }
        });
        jPanel10.add(slider360);

        b90.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rotacion90.png"))); // NOI18N
        b90.setToolTipText("Rotación 90º");
        b90.setPreferredSize(new java.awt.Dimension(35, 35));
        b90.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b90ActionPerformed(evt);
            }
        });
        jPanel10.add(b90);

        b180.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rotacion180.png"))); // NOI18N
        b180.setToolTipText("Rotación 180º");
        b180.setPreferredSize(new java.awt.Dimension(35, 35));
        b180.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b180ActionPerformed(evt);
            }
        });
        jPanel10.add(b180);

        b270.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rotacion270.png"))); // NOI18N
        b270.setToolTipText("Rotación 270º");
        b270.setPreferredSize(new java.awt.Dimension(35, 35));
        b270.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b270ActionPerformed(evt);
            }
        });
        jPanel10.add(b270);

        bAumentar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/aumentar.png"))); // NOI18N
        bAumentar.setToolTipText("Aumentar");
        bAumentar.setPreferredSize(new java.awt.Dimension(35, 35));
        bAumentar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAumentarActionPerformed(evt);
            }
        });
        jPanel10.add(bAumentar);

        bDisminuir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/disminuir.png"))); // NOI18N
        bDisminuir.setToolTipText("Disminuir");
        bDisminuir.setPreferredSize(new java.awt.Dimension(35, 35));
        bDisminuir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDisminuirActionPerformed(evt);
            }
        });
        jPanel10.add(bDisminuir);

        jPanel4.add(jPanel10);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Color"));

        bExtraccionBandas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/bandas.png"))); // NOI18N
        bExtraccionBandas.setToolTipText("Extracción de Bandas");
        bExtraccionBandas.setPreferredSize(new java.awt.Dimension(35, 35));
        bExtraccionBandas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bExtraccionBandasActionPerformed(evt);
            }
        });
        jPanel5.add(bExtraccionBandas);

        comboBoxEspaciosColor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "RGB", "YCC", "GREY", "Grey++" }));
        comboBoxEspaciosColor.setToolTipText("Elección Espacio Color");
        comboBoxEspaciosColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxEspaciosColorActionPerformed(evt);
            }
        });
        jPanel5.add(comboBoxEspaciosColor);

        jPanel4.add(jPanel5);

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), "Operadores"));

        bCombinacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/combinar.png"))); // NOI18N
        bCombinacion.setToolTipText("Combinación");
        bCombinacion.setPreferredSize(new java.awt.Dimension(35, 35));
        jPanel17.add(bCombinacion);

        bTintar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/tintar.png"))); // NOI18N
        bTintar.setToolTipText("Tintado");
        bTintar.setPreferredSize(new java.awt.Dimension(35, 35));
        bTintar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bTintarActionPerformed(evt);
            }
        });
        jPanel17.add(bTintar);

        bSepia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/sepia.png"))); // NOI18N
        bSepia.setToolTipText("Sepia");
        bSepia.setPreferredSize(new java.awt.Dimension(35, 35));
        bSepia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSepiaActionPerformed(evt);
            }
        });
        jPanel17.add(bSepia);

        bEcualizador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/ecualizar.png"))); // NOI18N
        bEcualizador.setToolTipText("Ecualizar");
        bEcualizador.setPreferredSize(new java.awt.Dimension(35, 35));
        bEcualizador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bEcualizadorActionPerformed(evt);
            }
        });
        jPanel17.add(bEcualizador);

        bRojo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rojo.png"))); // NOI18N
        bRojo.setToolTipText("Aumento Rojo");
        bRojo.setPreferredSize(new java.awt.Dimension(35, 35));
        bRojo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRojoActionPerformed(evt);
            }
        });
        jPanel17.add(bRojo);

        jNumColors.setMaximum(20);
        jNumColors.setMinimum(2);
        jNumColors.setToolTipText("Deslizador Número de Colores");
        jNumColors.setValue(2);
        jNumColors.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jNumColorsStateChanged(evt);
            }
        });
        jNumColors.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jNumColorsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jNumColorsFocusLost(evt);
            }
        });
        jPanel17.add(jNumColors);

        sliderColorBorde.setMaximum(255);
        sliderColorBorde.setToolTipText("Deslizador Borde Color");
        sliderColorBorde.setValue(0);
        sliderColorBorde.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderColorBordeStateChanged(evt);
            }
        });
        sliderColorBorde.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderColorBordeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderColorBordeFocusLost(evt);
            }
        });
        jPanel17.add(sliderColorBorde);

        jPanel4.add(jPanel17);

        jPanel3.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        labelEstado.setText("Barra de Estado");
        jPanel6.add(labelEstado);

        jPanel3.add(jPanel6, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

        menuArchivo.setText("Archivo");
        menuArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuArchivoActionPerformed(evt);
            }
        });

        menuNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevo.png"))); // NOI18N
        menuNuevo.setText("Nuevo");
        menuNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNuevoActionPerformed(evt);
            }
        });
        menuArchivo.add(menuNuevo);

        menuAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/abrir.png"))); // NOI18N
        menuAbrir.setText("Abrir");
        menuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAbrirActionPerformed(evt);
            }
        });
        menuArchivo.add(menuAbrir);

        menuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/guardar.png"))); // NOI18N
        menuGuardar.setText("Guardar");
        menuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuGuardarActionPerformed(evt);
            }
        });
        menuArchivo.add(menuGuardar);

        jMenuBar1.add(menuArchivo);

        menuEdicion.setText("Edición");
        jMenuBar1.add(menuEdicion);

        menuImagen.setText("Imagen");

        menuRescaleOp.setText("Reescalar imagen");
        menuRescaleOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRescaleOpActionPerformed(evt);
            }
        });
        menuImagen.add(menuRescaleOp);

        menuConvolveOp.setText("ConvolveOp");
        menuConvolveOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuConvolveOpActionPerformed(evt);
            }
        });
        menuImagen.add(menuConvolveOp);

        jMenuBar1.add(menuImagen);

        menuAyuda.setText("Ayuda");

        itemAcercade.setText("Acerca de");
        itemAcercade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAcercadeActionPerformed(evt);
            }
        });
        menuAyuda.add(itemAcercade);

        jMenuBar1.add(menuAyuda);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNuevoActionPerformed
        VentanaInterna vi = new VentanaInterna();
        escritorio.add(vi);
        vi.setVisible(true);

        //IMAGEN
        BufferedImage img;
        img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        vi.getLienzo().setImage(img);

        vi.addInternalFrameListener(new ManejadorVentanaInterna());
        vi.getLienzo().addLienzoListener(new MiManejadorLienzo());
    }//GEN-LAST:event_menuNuevoActionPerformed

    private void botonTrazoLibreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonTrazoLibreActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.TRAZO_LIBRE);
        }
    }//GEN-LAST:event_botonTrazoLibreActionPerformed

    private void botonLineaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLineaActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.LINEA);
        }
    }//GEN-LAST:event_botonLineaActionPerformed

    private void botonRectanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRectanActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.RECTANGULO);
        }
    }//GEN-LAST:event_botonRectanActionPerformed

    private void botonElipseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonElipseActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.ELIPSE);
        }
    }//GEN-LAST:event_botonElipseActionPerformed

    private void botonCurvaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCurvaActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.CURVA);
        }
    }//GEN-LAST:event_botonCurvaActionPerformed

    private void spinnerGrosorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerGrosorStateChanged
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {

            if (vi.getLienzo().getS_selected() != null) {
                vi.getLienzo().getFiguraSeleccionada().setStroke(new BasicStroke(vi.getLienzo().getFiguraSeleccionada().getGrosor_trazo()));
                vi.getLienzo().setGrosor_trazo(getFloat(spinnerGrosor));

                vi.getLienzo().getFiguraSeleccionada().setStroke(new BasicStroke(vi.getLienzo().getGrosor_trazo()));
                vi.getLienzo().getFiguraSeleccionada().setGrosor_trazo(getFloat(spinnerGrosor));

            } else {

                vi.getLienzo().setGrosor_trazo(getFloat(spinnerGrosor));
                vi.getLienzo().setStroke(new BasicStroke(vi.getLienzo().getGrosor_trazo()));

            }

        }

        this.repaint();
    }//GEN-LAST:event_spinnerGrosorStateChanged

    private void menuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAbrirActionPerformed
        JFileChooser chooser = getFileChooser();
        int resp = chooser.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                File f = chooser.getSelectedFile();
                BufferedImage img = ImageIO.read(f);
                VentanaInterna vi = new VentanaInterna(img);

                this.escritorio.add(vi);
                vi.setTitle(f.getName());
                vi.setVisible(true);

                vi.addInternalFrameListener(new ManejadorVentanaInterna());
            } catch (Exception ex) {
                System.err.println("Error al leer la imagen");
            }
        }
    }//GEN-LAST:event_menuAbrirActionPerformed

    private void menuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuGuardarActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage(true);
            if (img != null) {
                JFileChooser dlg = new JFileChooser();
                int resp = dlg.showSaveDialog(this);
                if (resp == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = dlg.getSelectedFile();
                        ImageIO.write(img, "jpg", f);
                        vi.setTitle(f.getName());
                    } catch (Exception ex) {
                        System.err.println("Error al guardar la imagen");
                    }
                }
            }
        }
    }//GEN-LAST:event_menuGuardarActionPerformed

    private void menuRescaleOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRescaleOpActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (img != null) {
                try {
                    RescaleOp rop = new RescaleOp(1.0F, 100.0F, null);
                    rop.filter(img, img);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_menuRescaleOpActionPerformed

    private void menuConvolveOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuConvolveOpActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (img != null) {
                try {
                    float filtro[] = {0.1f, 0.1f, 0.1f, 0.1f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f};
                    Kernel k = new Kernel(3, 3, filtro);
                    ConvolveOp cop = new ConvolveOp(k);

                    BufferedImage imgdest = cop.filter(img, null);
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_menuConvolveOpActionPerformed

    private void sliderBrilloFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderBrilloFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_sliderBrilloFocusGained

    private void sliderBrilloFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderBrilloFocusLost
        imgFuente = null;

    }//GEN-LAST:event_sliderBrilloFocusLost

    private void sliderBrilloStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderBrilloStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            escritorio.repaint();
            BufferedImage img = vi.getLienzo().getImage();
            if (imgFuente != null) {
                try {
                    RescaleOp rop = new RescaleOp(1.0F, sliderBrillo.getValue(), null);
                    rop.filter(imgFuente, img);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderBrilloStateChanged

    private void sliderContrasteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderContrasteFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_sliderContrasteFocusGained

    private void sliderContrasteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderContrasteFocusLost
        imgFuente = null;
    }//GEN-LAST:event_sliderContrasteFocusLost

    private void sliderContrasteStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderContrasteStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (imgFuente != null) {
                try {

                    float num = sliderContraste.getValue() * 0.1f;
                    float filtro[] = {num, num, num,
                        num, num, num,
                        num, num, num};
                    Kernel k = new Kernel(3, 3, filtro);
                    ConvolveOp cop = new ConvolveOp(k);

                    BufferedImage imgdest = cop.filter(imgFuente, img);

                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderContrasteStateChanged

    private void ComboBoxFiltrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBoxFiltrosActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());

        switch (ComboBoxFiltros.getSelectedIndex()) {
            case 0:
                if (vi != null) {
                    BufferedImage img = vi.getLienzo().getImage();
                    if (imgFuente != null) {
                        try {

                            Kernel k = KernelProducer.createKernel(KernelProducer.TYPE_MEDIA_3x3);
                            ConvolveOp cop2 = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);

                            BufferedImage imgdest = cop2.filter(img, null);
                            vi.getLienzo().setImage(imgdest);
                            vi.getLienzo().repaint();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
                break;
            case 1:
                if (vi != null) {
                    BufferedImage img = vi.getLienzo().getImage();
                    if (imgFuente != null) {
                        try {

                            Kernel k = KernelProducer.createKernel(KernelProducer.TYPE_BINOMIAL_3x3);
                            ConvolveOp cop2 = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);

                            BufferedImage imgdest = cop2.filter(img, null);
                            vi.getLienzo().setImage(imgdest);
                            vi.getLienzo().repaint();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
                break;
            case 2:
                if (vi != null) {
                    BufferedImage img = vi.getLienzo().getImage();
                    if (imgFuente != null) {
                        try {

                            Kernel k = KernelProducer.createKernel(KernelProducer.TYPE_ENFOQUE_3x3);
                            ConvolveOp cop2 = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);

                            BufferedImage imgdest = cop2.filter(img, null);
                            vi.getLienzo().setImage(imgdest);
                            vi.getLienzo().repaint();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
                break;
            case 3:
                if (vi != null) {
                    BufferedImage img = vi.getLienzo().getImage();
                    if (imgFuente != null) {
                        try {

                            Kernel k = KernelProducer.createKernel(KernelProducer.TYPE_RELIEVE_3x3);
                            ConvolveOp cop2 = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);

                            BufferedImage imgdest = cop2.filter(img, null);
                            vi.getLienzo().setImage(imgdest);
                            vi.getLienzo().repaint();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
                break;
            case 4:
                if (vi != null) {
                    BufferedImage img = vi.getLienzo().getImage();
                    if (imgFuente != null) {
                        try {

                            Kernel k = KernelProducer.createKernel(KernelProducer.TYPE_LAPLACIANA_3x3);
                            ConvolveOp cop2 = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);

                            BufferedImage imgdest = cop2.filter(img, null);
                            vi.getLienzo().setImage(imgdest);
                            vi.getLienzo().repaint();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
                break;

            case 5:
                if (vi != null) {
                    BufferedImage img = vi.getLienzo().getImage();
                    if (imgFuente != null) {
                        try {

                            float num = 1 / 25.0f;
                            float filtro[] = {num, num, num, num, num,
                                num, num, num, num, num,
                                num, num, num, num, num,
                                num, num, num, num, num,
                                num, num, num, num, num,};
                            Kernel k = new Kernel(5, 5, filtro);
                            ConvolveOp cop2 = new ConvolveOp(k);

                            BufferedImage imgdest = cop2.filter(img, null);
                            vi.getLienzo().setImage(imgdest);
                            vi.getLienzo().repaint();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
                break;
            case 6:
                if (vi != null) {
                    BufferedImage img = vi.getLienzo().getImage();
                    if (imgFuente != null) {
                        try {

                            float num = 1 / 49.0f;
                            float filtro[] = {num, num, num, num, num, num, num,
                                num, num, num, num, num, num, num,
                                num, num, num, num, num, num, num,
                                num, num, num, num, num, num, num,
                                num, num, num, num, num, num, num,};
                            Kernel k = new Kernel(5, 5, filtro);
                            ConvolveOp cop2 = new ConvolveOp(k);

                            BufferedImage imgdest = cop2.filter(img, null);
                            vi.getLienzo().setImage(imgdest);
                            vi.getLienzo().repaint();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
                break;
        }
    }//GEN-LAST:event_ComboBoxFiltrosActionPerformed

    private void ComboBoxFiltrosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ComboBoxFiltrosFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_ComboBoxFiltrosFocusGained

    private void ComboBoxFiltrosFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ComboBoxFiltrosFocusLost
        imgFuente = null;
    }//GEN-LAST:event_ComboBoxFiltrosFocusLost

    private void ToggleButtonMoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ToggleButtonMoverActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (ToggleButtonMover.isSelected()) {
            vi.getLienzo().setMover(true);
        } else {
            vi.getLienzo().setMover(false);
        }
    }//GEN-LAST:event_ToggleButtonMoverActionPerformed

    private void botonAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAbrirActionPerformed
        JFileChooser dlg = getFileChooser();
        int resp = dlg.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                File f = dlg.getSelectedFile();
                BufferedImage img = ImageIO.read(f);
                VentanaInterna vi = new VentanaInterna(img);
                //vi.getLienzo().setImage(img);
                this.escritorio.add(vi);
                vi.setTitle(f.getName());
                vi.setVisible(true);
            } catch (Exception ex) {
                System.err.println("Error al leer la imagen");
            }
        }
    }//GEN-LAST:event_botonAbrirActionPerformed

    private void botonNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonNuevoActionPerformed
        VentanaInterna vi = new VentanaInterna();
        escritorio.add(vi);
        vi.setVisible(true);

        //IMAGEN
        BufferedImage img;
        img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        vi.getLienzo().setImage(img);

        // 2) Crear el objeto manejador (hacer el "new" de la clase anterior)
        vi.addInternalFrameListener(new ManejadorVentanaInterna());
        vi.getLienzo().addLienzoListener(new MiManejadorLienzo());
    }//GEN-LAST:event_botonNuevoActionPerformed

    private void botonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGuardarActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage(true);
            if (img != null) {
                JFileChooser dlg = new JFileChooser();
                int resp = dlg.showSaveDialog(this);
                if (resp == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = dlg.getSelectedFile();
                        ImageIO.write(img, "jpg", f);
                        vi.setTitle(f.getName());
                    } catch (Exception ex) {
                        System.err.println("Error al guardar la imagen");
                    }
                }
            }
        }
    }//GEN-LAST:event_botonGuardarActionPerformed
    
    private void ComboBoxColorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBoxColorsActionPerformed

        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();

        switch (ComboBoxColors.getSelectedIndex()) {
            case 0:

                if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
                    vi.getLienzo().getFiguraSeleccionada().setColor(Color.BLACK);
                } else {
                    vi.getLienzo().setColor(Color.BLACK);
                }

                this.repaint();

                break;

            case 1:

                if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
                    vi.getLienzo().getFiguraSeleccionada().setColor(Color.RED);
                } else {
                    vi.getLienzo().setColor(Color.RED);
                }

                this.repaint();

                break;

            case 2:

                if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
                    vi.getLienzo().getFiguraSeleccionada().setColor(Color.BLUE);
                } else {
                    vi.getLienzo().setColor(Color.BLUE);
                }

                this.repaint();

                break;

            case 3:

                if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
                    vi.getLienzo().getFiguraSeleccionada().setColor(Color.WHITE);
                } else {
                    vi.getLienzo().setColor(Color.WHITE);
                }

                this.repaint();

                break;
            case 4:

                if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
                    vi.getLienzo().getFiguraSeleccionada().setColor(Color.YELLOW);
                } else {
                    vi.getLienzo().setColor(Color.YELLOW);
                }

                this.repaint();

                break;

            case 5:

                if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
                    vi.getLienzo().getFiguraSeleccionada().setColor(Color.GREEN);
                } else {
                    vi.getLienzo().setColor(Color.GREEN);
                }

                this.repaint();

                break;
        }
    }//GEN-LAST:event_ComboBoxColorsActionPerformed

    private void transparenciaToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transparenciaToggleButtonActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();

        if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
            if (transparenciaToggleButton.isSelected()) {
                vi.getLienzo().getFiguraSeleccionada().setTransparencia(true);
            } else {
                vi.getLienzo().getFiguraSeleccionada().setTransparencia(false);
            }
        } else {
            if (transparenciaToggleButton.isSelected()) {
                vi.getLienzo().setTransparencia(true);
            } else {
                vi.getLienzo().setTransparencia(false);
            }
        }

        this.repaint();
    }//GEN-LAST:event_transparenciaToggleButtonActionPerformed

    private void alisarToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alisarToggleButtonActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();

        if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
            if (alisarToggleButton.isSelected()) {
                vi.getLienzo().getFiguraSeleccionada().setAntialiasing(true);
            } else {
                vi.getLienzo().getFiguraSeleccionada().setAntialiasing(false);
            }
        } else {
            if (alisarToggleButton.isSelected()) {
                vi.getLienzo().setAntialiasing(true);
            } else {
                vi.getLienzo().setAntialiasing(false);
            }
        }

        this.repaint();
    }//GEN-LAST:event_alisarToggleButtonActionPerformed

    private void rellenoToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rellenoToggleButtonActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();

        if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {

            System.out.print("Relleno Figura Seleccionada");

            if (rellenoToggleButton.isSelected()) {
                vi.getLienzo().getFiguraSeleccionada().setRelleno(true);
            } else {
                vi.getLienzo().getFiguraSeleccionada().setRelleno(false);
            }
        } else {

            System.out.print("Relleno Figura Lienzo");

            if (rellenoToggleButton.isSelected()) {
                vi.getLienzo().setRelleno(true);
            } else {
                vi.getLienzo().setRelleno(false);
            }
        }

        this.repaint();
    }//GEN-LAST:event_rellenoToggleButtonActionPerformed

    private void sliderFiltroMediaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderFiltroMediaStateChanged
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (imgFuente != null) {
                try {

                    float num = 1 / (float) (sliderFiltroMedia.getValue() * sliderFiltroMedia.getValue());

                    float filtro[] = new float[sliderFiltroMedia.getValue() * sliderFiltroMedia.getValue()];

                    for (int i = 0; i < sliderFiltroMedia.getValue() * sliderFiltroMedia.getValue(); i++) {
                        filtro[i] = num;
                    }

                    Kernel k = new Kernel(sliderFiltroMedia.getValue(), sliderFiltroMedia.getValue(), filtro);
                    ConvolveOp cop2 = new ConvolveOp(k);

                    BufferedImage imgdest = cop2.filter(imgFuente, img);

                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderFiltroMediaStateChanged

    private void sliderFiltroMediaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderFiltroMediaFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_sliderFiltroMediaFocusGained

    private void sliderFiltroMediaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderFiltroMediaFocusLost
        imgFuente = null;
        sliderFiltroMedia.setValue(0);
    }//GEN-LAST:event_sliderFiltroMediaFocusLost

    private void botonContrasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonContrasteActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (img != null) {
                try {
                    LookupTable lt = LookupTableProducer.createLookupTable(LookupTableProducer.TYPE_SFUNCION);
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(img, img); // Imagen origen y destino iguales
                    vi.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }

    }//GEN-LAST:event_botonContrasteActionPerformed

    private void bIluminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bIluminarActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (img != null) {
                try {
                    LookupTable lt = LookupTableProducer.createLookupTable(LookupTableProducer.TYPE_ROOT);
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(img, img); // Imagen origen y destino iguales
                    vi.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_bIluminarActionPerformed

    private void bOscurecerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOscurecerActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (img != null) {
                try {
                    LookupTable lt = LookupTableProducer.createLookupTable(LookupTableProducer.TYPE_POWER);
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(img, img); // Imagen origen y destino iguales
                    vi.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_bOscurecerActionPerformed

    private void bCuadraticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCuadraticaActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (img != null) {
                try {
                    LookupTable lt = cuadratica(128.0);
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(img, img); // Imagen origen y destino iguales
                    vi.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_bCuadraticaActionPerformed

    private void slider360FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider360FocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_slider360FocusGained

    private void slider360FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_slider360FocusLost
        imgFuente = null;
        slider360.setValue(0);
    }//GEN-LAST:event_slider360FocusLost

    private void slider360StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider360StateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (imgFuente != null) {
                try {

                    double r = Math.toRadians(slider360.getValue());
                    Point c = new Point(imgFuente.getWidth() / 2, imgFuente.getHeight() / 2);

                    AffineTransform at = AffineTransform.getRotateInstance(r, c.x, c.y);
                    AffineTransformOp atop;
                    atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

                    BufferedImage imgdest = atop.filter(imgFuente, null);

                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_slider360StateChanged

    private void b90ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b90ActionPerformed

        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        BufferedImage img = vi.getLienzo().getImage();

        try {

            double r = Math.toRadians(90);
            Point c = new Point(img.getWidth() / 2, img.getHeight() / 2);
            AffineTransform at = AffineTransform.getRotateInstance(r, c.x, c.y);
            AffineTransformOp atop;
            atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            BufferedImage imgdest = atop.filter(img, null);

            vi.getLienzo().setImage(imgdest);
            vi.getLienzo().repaint();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getLocalizedMessage());
        }

    }//GEN-LAST:event_b90ActionPerformed

    private void b180ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b180ActionPerformed

        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        BufferedImage img = vi.getLienzo().getImage();

        try {

            double r = Math.toRadians(180);
            Point c = new Point(img.getWidth() / 2, img.getHeight() / 2);
            AffineTransform at = AffineTransform.getRotateInstance(r, c.x, c.y);
            AffineTransformOp atop;
            atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            BufferedImage imgdest = atop.filter(img, null);

            vi.getLienzo().setImage(imgdest);
            vi.getLienzo().repaint();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }//GEN-LAST:event_b180ActionPerformed

    private void b270ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b270ActionPerformed

        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        BufferedImage img = vi.getLienzo().getImage();

        try {

            double r = Math.toRadians(270);
            Point c = new Point(img.getWidth() / 2, img.getHeight() / 2);
            AffineTransform at = AffineTransform.getRotateInstance(r, c.x, c.y);
            AffineTransformOp atop;
            atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            BufferedImage imgdest = atop.filter(img, null);

            vi.getLienzo().setImage(imgdest);
            vi.getLienzo().repaint();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }//GEN-LAST:event_b270ActionPerformed

    private void bAumentarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAumentarActionPerformed

        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        BufferedImage img = vi.getLienzo().getImage();

        try {
            AffineTransform at = AffineTransform.getScaleInstance(1.25, 1.25);
            AffineTransformOp atop = new AffineTransformOp(at,
                    AffineTransformOp.TYPE_BILINEAR);
            BufferedImage imgdest = atop.filter(img, null);

            vi.getLienzo().setImage(imgdest);
            vi.getLienzo().repaint();
        } catch (Exception e) {
            System.err.println("Error");
        }

    }//GEN-LAST:event_bAumentarActionPerformed

    private void bDisminuirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDisminuirActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        BufferedImage img = vi.getLienzo().getImage();

        try {
            AffineTransform at = AffineTransform.getScaleInstance(0.75, 0.75);
            AffineTransformOp atop = new AffineTransformOp(at,
                    AffineTransformOp.TYPE_BILINEAR);
            BufferedImage imgdest = atop.filter(img, null);

            vi.getLienzo().setImage(imgdest);
            vi.getLienzo().repaint();
        } catch (Exception e) {
            System.err.println("Error");
        }
    }//GEN-LAST:event_bDisminuirActionPerformed

    private void bTrapezoideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTrapezoideActionPerformed

        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (img != null) {
                try {
                    LookupTable lt = trapezoide(56, 56);
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(img, img); // Imagen origen y destino iguales
                    vi.getLienzo().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_bTrapezoideActionPerformed

    private void menuArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuArchivoActionPerformed
        JFileChooser dlg = getFileChooser();
        int resp = dlg.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                File f = dlg.getSelectedFile();
                BufferedImage img = ImageIO.read(f);
                VentanaInterna vi = new VentanaInterna();
                vi.getLienzo().setImage(img);
                this.escritorio.add(vi);
                vi.setTitle(f.getName());
                vi.setVisible(true);
            } catch (Exception ex) {
                System.err.println("Error al leer la imagen");
            }
        }
    }//GEN-LAST:event_menuArchivoActionPerformed

    private void botonSeleccionadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSeleccionadorActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (botonSeleccionador.isSelected()) {
            vi.getLienzo().setSeleccionar(true);
        } else {
            vi.getLienzo().setSeleccionar(false);
            vi.getLienzo().setGrosor_trazo(5.0f);
            vi.getLienzo().setStroke(new BasicStroke(vi.getLienzo().getGrosor_trazo()));
            vi.getLienzo().setColor(Color.BLACK);
            ComboBoxColors.setSelectedIndex(0);
            botonRectan.setSelected(true);
            spinnerGrosor.setValue(5);
        }
    }//GEN-LAST:event_botonSeleccionadorActionPerformed

    private void bRoundRectangleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRoundRectangleActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.ROUNDRECTANGULO);
        }
    }//GEN-LAST:event_bRoundRectangleActionPerformed

    private void bArc2DActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bArc2DActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.ARCO);
        }
    }//GEN-LAST:event_bArc2DActionPerformed

    private void bCubicCurveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCubicCurveActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.CURVACUBICA);
        }
    }//GEN-LAST:event_bCubicCurveActionPerformed

    private void bPolygonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPolygonActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.POLIGONO);
        }

        if (!bPolygon.isSelected()) {
            vi.getLienzo().setPaso_poligono(0);
        }
    }//GEN-LAST:event_bPolygonActionPerformed

    private void bAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAreaActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (!(vi == null)) {
            vi.getLienzo().setFigura(Figura.AREA);
        }
    }//GEN-LAST:event_bAreaActionPerformed

    private void bPolygonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_bPolygonStateChanged
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();

        if (!bPolygon.isSelected()) {
            vi.getLienzo().setPaso_poligono(0);
        }
    }//GEN-LAST:event_bPolygonStateChanged

    private void bExtraccionBandasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bExtraccionBandasActionPerformed

        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();

        BufferedImage imgS = vi.getLienzo().getImage();
        WritableRaster rasterS = imgS.getRaster();

//VentanaInterna vi = new VentanaInterna();
        //escritorio.add(vi);
        for (int i = 0; i < rasterS.getNumBands(); i++) {

            BufferedImage img = getImageBand(vi.getLienzo().getImage(), i);

            VentanaInterna n_vi = new VentanaInterna();
            escritorio.add(n_vi);
            n_vi.getLienzo().setImage(img);
            n_vi.setTitle("[" + i + "]");
            n_vi.setVisible(true);

        }

        //IMAGEN
        /*BufferedImage img;
        img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        vi.getLienzo().setImage(img);
        vi.setTitle("[1]");
        vi.setVisible(true);*/
        // 2) Crear el objeto manejador (hacer el "new" de la clase anterior)
        vi.addInternalFrameListener(new ManejadorVentanaInterna());
        vi.getLienzo().addLienzoListener(new MiManejadorLienzo());
    }//GEN-LAST:event_bExtraccionBandasActionPerformed

    private void comboBoxEspaciosColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxEspaciosColorActionPerformed

        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        BufferedImage imgSource = vi.getLienzo().getImage();

        ColorSpace cs;

        switch (comboBoxEspaciosColor.getSelectedIndex()) {
            case 1: {
                cs = ColorSpace.getInstance(ColorSpace.CS_PYCC);

                break;
            }
            case 0: {
                cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);

                break;
            }
            case 2: {
                cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);

                break;
            }
            case 3: {
                cs = new GreyColorSpace();

                break;
            }
            default:
                cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);

                break;
        }

        ColorConvertOp cop = new ColorConvertOp(cs, null);
        BufferedImage imgOut = cop.filter(imgSource, null);
        vi.getLienzo().setImage(imgOut);
    }//GEN-LAST:event_comboBoxEspaciosColorActionPerformed

    private void bTintarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTintarActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        BufferedImage imgSource = vi.getLienzo().getImage();

        TintOp tintado = new TintOp(vi.getLienzo().getColor(), 0.5f);
        BufferedImage imgOut = tintado.filter(imgSource, null);
        vi.getLienzo().setImage(imgOut);

        vi.getLienzo().repaint();
    }//GEN-LAST:event_bTintarActionPerformed

    private void bSepiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSepiaActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        BufferedImage imgSource = vi.getLienzo().getImage();

        SepiaOp sepia = new SepiaOp();
        BufferedImage imgOut = sepia.filter(imgSource, null);
        vi.getLienzo().setImage(imgOut);

        vi.getLienzo().repaint();
    }//GEN-LAST:event_bSepiaActionPerformed

    private void bEcualizadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bEcualizadorActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        BufferedImage imgSource = vi.getLienzo().getImage();

        EqualizationOp ecualizacion = new EqualizationOp();
        BufferedImage imgOut = ecualizacion.filter(imgSource, null);
        vi.getLienzo().setImage(imgOut);

        vi.getLienzo().repaint();
    }//GEN-LAST:event_bEcualizadorActionPerformed

    private void jNumColorsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jNumColorsFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_jNumColorsFocusGained

    private void jNumColorsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jNumColorsFocusLost
        imgFuente = null;
        jNumColors.setValue(2);
    }//GEN-LAST:event_jNumColorsFocusLost

    private void jNumColorsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jNumColorsStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (imgFuente != null) {
                try {

                    int num_c = jNumColors.getValue();
                    PosterizarOp post = new PosterizarOp(num_c);

                    BufferedImage imgdest = post.filter(imgFuente, img);

                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_jNumColorsStateChanged

    private void sliderTransparenciaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderTransparenciaStateChanged
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();

        float nivel_t = sliderTransparencia.getValue() / 10.0f;

        if (vi.getLienzo().getSeleccionar() && vi.getLienzo().getFiguraSeleccionada() != null) {
            if (alisarToggleButton.isSelected()) {
                vi.getLienzo().getFiguraSeleccionada().setComposicion(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, nivel_t));
            } else {
                vi.getLienzo().getFiguraSeleccionada().setComposicion(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, nivel_t));
            }
        } else {
            if (alisarToggleButton.isSelected()) {
                vi.getLienzo().setComposicion(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, nivel_t));
            } else {
                vi.getLienzo().setComposicion(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, nivel_t));
            }
        }

        this.repaint();
    }//GEN-LAST:event_sliderTransparenciaStateChanged

    private void ComboBoxColorsTrazoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBoxColorsTrazoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ComboBoxColorsTrazoActionPerformed

    private void ComboBoxColorsRellenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBoxColorsRellenoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ComboBoxColorsRellenoActionPerformed

    private void bRojoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRojoActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        BufferedImage imgSource = vi.getLienzo().getImage();

        RojoOp rojo = new RojoOp(30);
        BufferedImage imgOut = rojo.filter(imgSource, null);
        vi.getLienzo().setImage(imgOut);

        vi.getLienzo().repaint();
    }//GEN-LAST:event_bRojoActionPerformed

    private void sliderColorBordeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderColorBordeStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo().getImage();
            if (imgFuente != null) {
                try {

                    ColorBordeOp bordeop = new ColorBordeOp(vi.getLienzo().getColor(), sliderColorBorde.getValue());

                    BufferedImage imgdest = bordeop.filter(imgFuente, img);

                    vi.getLienzo().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderColorBordeStateChanged

    private void sliderColorBordeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderColorBordeFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_sliderColorBordeFocusGained

    private void sliderColorBordeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderColorBordeFocusLost
        imgFuente = null;
        jNumColors.setValue(0);
    }//GEN-LAST:event_sliderColorBordeFocusLost

    private void bMiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bMiActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        BufferedImage imgSource = vi.getLienzo().getImage();

        MiOp miop = new MiOp(vi.getLienzo().getColor(), 30);
        BufferedImage imgOut = miop.filter(imgSource, null);
        vi.getLienzo().setImage(imgOut);

        vi.getLienzo().repaint();
    }//GEN-LAST:event_bMiActionPerformed

    private void bHistogramaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bHistogramaActionPerformed
        VentanaInterna vi2 = (VentanaInterna) escritorio.getSelectedFrame();
        BufferedImage imgSource = vi2.getLienzo().getImage();

        /*InternalForHistogram vi = new InternalForHistogram();
        vi.setTitle("[Histograma] " + vi2.getTitle());
        escritorio.add(vi);
        vi.setVisible(true);*/
        VentanaInterna vi = new VentanaInterna();
        vi.setTitle("[Histograma] " + vi2.getTitle());

        Histogram histograma = new Histogram(imgSource);
        vi.getLienzo().setHistogramaLienzo(histograma);
        vi.setSize(histograma.getNumBins() * histograma.getNumBands() + 10, 300);

        escritorio.add(vi);
        vi.setVisible(true);

    }//GEN-LAST:event_bHistogramaActionPerformed

    private void itemAcercadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAcercadeActionPerformed
        AcercaDeFrame acercade = new AcercaDeFrame();
        acercade.setTitle("Acerca de");
        acercade.setVisible(true);
    }//GEN-LAST:event_itemAcercadeActionPerformed

    private void comboBoxObjetosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxObjetosActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
         
        if(vi.getLienzo().getSeleccionar()){
            System.out.print(comboBoxObjetos.getSelectedIndex());
            vi.getLienzo().seleccionarFigura(vi.getLienzo().getvShape().get(comboBoxObjetos.getSelectedIndex()));
        }
    }//GEN-LAST:event_comboBoxObjetosActionPerformed

    private void bVolcadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bVolcadoActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        
        if (bVolcado.isSelected()) {
            vi.getLienzo().setVolcar(true);
            
            if(!vi.getLienzo().getvShape().isEmpty()){
                
                vi.getLienzo().volcarVector();
                
                DefaultComboBoxModel modelo= new DefaultComboBoxModel();
                modelo.addElement("Ninguna figura");
                comboBoxObjetos.setModel(modelo);
                
                escritorio.repaint();
            }
            
           
        } else {
            vi.getLienzo().setVolcar(false);
        }
        
        vi.getLienzo().repaint();
    }//GEN-LAST:event_bVolcadoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Color> ComboBoxColors;
    private javax.swing.JComboBox<Color> ComboBoxColorsRelleno;
    private javax.swing.JComboBox<Color> ComboBoxColorsTrazo;
    private javax.swing.JComboBox<String> ComboBoxFiltros;
    private javax.swing.JToggleButton ToggleButtonMover;
    private javax.swing.JToggleButton alisarToggleButton;
    private javax.swing.JButton b180;
    private javax.swing.JButton b270;
    private javax.swing.JButton b90;
    private javax.swing.JToggleButton bArc2D;
    private javax.swing.JToggleButton bArea;
    private javax.swing.JButton bAumentar;
    private javax.swing.JButton bCombinacion;
    private javax.swing.JButton bCuadratica;
    private javax.swing.JToggleButton bCubicCurve;
    private javax.swing.JButton bDisminuir;
    private javax.swing.JButton bEcualizador;
    private javax.swing.JButton bExtraccionBandas;
    private javax.swing.JButton bHistograma;
    private javax.swing.JButton bIluminar;
    private javax.swing.JButton bMi;
    private javax.swing.JButton bOscurecer;
    private javax.swing.JToggleButton bPolygon;
    private javax.swing.JButton bRojo;
    private javax.swing.JToggleButton bRoundRectangle;
    private javax.swing.JButton bSepia;
    private javax.swing.JButton bTintar;
    private javax.swing.JToggleButton bTrapezoide;
    private javax.swing.JToggleButton bVolcado;
    private javax.swing.JButton botonAbrir;
    private javax.swing.JButton botonContraste;
    private javax.swing.JToggleButton botonCurva;
    private javax.swing.JToggleButton botonElipse;
    private javax.swing.JButton botonGuardar;
    private javax.swing.JToggleButton botonLinea;
    private javax.swing.JButton botonNuevo;
    private javax.swing.JToggleButton botonRectan;
    private javax.swing.JToggleButton botonSeleccionador;
    private javax.swing.JToggleButton botonTrazoLibre;
    private javax.swing.JComboBox<String> comboBoxEspaciosColor;
    private javax.swing.JComboBox<String> comboBoxObjetos;
    private javax.swing.JDesktopPane escritorio;
    private javax.swing.ButtonGroup figuras;
    private javax.swing.JMenuItem itemAcercade;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSlider jNumColors;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel labelEstado;
    private javax.swing.JMenuItem menuAbrir;
    private javax.swing.JMenu menuArchivo;
    private javax.swing.JMenu menuAyuda;
    private javax.swing.JMenuItem menuConvolveOp;
    private javax.swing.JMenu menuEdicion;
    private javax.swing.JMenuItem menuGuardar;
    private javax.swing.JMenu menuImagen;
    private javax.swing.JMenuItem menuNuevo;
    private javax.swing.JMenuItem menuRescaleOp;
    private javax.swing.JToggleButton rellenoToggleButton;
    private javax.swing.JSlider slider360;
    private javax.swing.JSlider sliderBrillo;
    private javax.swing.JSlider sliderColorBorde;
    private javax.swing.JSlider sliderContraste;
    private javax.swing.JSlider sliderFiltroMedia;
    private javax.swing.JSlider sliderTransparencia;
    private javax.swing.JSpinner spinnerGrosor;
    private javax.swing.JToggleButton transparenciaToggleButton;
    // End of variables declaration//GEN-END:variables
}
