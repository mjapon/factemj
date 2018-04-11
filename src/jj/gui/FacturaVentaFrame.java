/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui;

import com.serviestudios.print.util.TextPrinterUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.MaskFormatter;
import jj.controller.ArticulosJpaController;
import jj.controller.ClientesJpaController;
import jj.controller.CtesJpaController;
import jj.controller.FacturasJpaController;
import jj.controller.SecuenciasJpaController;
import jj.entity.Articulos;
import jj.entity.Clientes;
import jj.entity.Ctes;
import jj.entity.Secuencias;
import jj.util.ArticulosModelListener;
import jj.util.DatosCabeceraFactura;
import jj.util.DescComboBoxEditor;
import jj.util.DescComboBoxRenderer;
import jj.util.EntityManagerUtil;
import jj.util.datamodels.FacturaDataModel;
import jj.util.FacturaModelListener;
import jj.util.FechasUtil;
import jj.util.FilaArticulo;
import jj.util.FilaFactura;
import jj.util.FilaPago;
import jj.util.GenTxtFactura;
import jj.util.IVAComboBoxEditor;
import jj.util.IVAComboBoxRenderer;
import jj.util.NumbersUtil;
import jj.util.StringUtil;
import jj.util.TotalesFactura;
import jj.util.datamodels.ArticulosDataModel;


/**
 *
 * @author Usuario
 */
public class FacturaVentaFrame extends javax.swing.JFrame {
    
    private final FacturaDataModel facturaDataModel;
    private final FacturaModelListener facturaModelListener;
    private final ArticulosJpaController articulosController;
    private final ClientesJpaController clientesController;
    private final FacturasJpaController facturaController;
    private final SecuenciasJpaController secuenciasController;
    private final CtesJpaController ctesController;
    private EntityManager em;
    private Integer cliCodigo;
    private Clientes consFinal;
    
    private ArticulosDataModel articulosDataModel;
    private ArticulosModelListener articulosModelListener;
    
    private Map<Integer, FilaPago> pagosMap;
    private PagosFrame pagosFrame;
    private Integer tra_codigo;
    
    private JFrame root;
    public JFrame getRoot() {
           return root;
    }

    public void setRoot(JFrame root) {
         this.root = root;
    }            

    /**
     * Creates new form FacturaVentaFrame
     */
    public FacturaVentaFrame(Integer tra_codigo) {
        initComponents();
        
        //System.out.println("Se inicializa jtable-->");
        
        facturaDataModel = new FacturaDataModel();        
        facturaDataModel.setFrame(this);        
        facturaModelListener = new FacturaModelListener();
        facturaDataModel.addTableModelListener(facturaModelListener);        
        jTableFactura.setModel(facturaDataModel); 
        facturaDataModel.setJtable(jTableFactura);
        
        this.tra_codigo = tra_codigo;
        
        String[] values = new String[] { "SI", "NO"};
        TableColumn colIva = jTableFactura.getColumnModel().getColumn( FacturaDataModel.ColumnaFacturaEnum.IVA.index );
        colIva.setCellEditor(new IVAComboBoxEditor(values));
        colIva.setCellRenderer(new IVAComboBoxRenderer(values));
        
        
        String[] valuesDesc = { "0.0", "0.1", "0.2" };
        TableColumn colDesc = this.jTableFactura.getColumnModel().getColumn(FacturaDataModel.ColumnaFacturaEnum.VDESC.index);

        DescComboBoxEditor descuentoCmbEditor = new DescComboBoxEditor(valuesDesc);
        colDesc.setCellEditor(descuentoCmbEditor);

        DescComboBoxRenderer descuentoCmbRenderer = new DescComboBoxRenderer(valuesDesc);
        descuentoCmbRenderer.setEditable(true);
        colDesc.setCellRenderer(descuentoCmbRenderer);
        
        updateLabelsTotales();
        
        jTableFactura.updateUI();        
        facturaDataModel.fireTableDataChanged();
        
        this.em = EntityManagerUtil.createEntintyManagerFactory();
       
        articulosController = new ArticulosJpaController(em);
        clientesController = new ClientesJpaController(em);
        facturaController = new FacturasJpaController(em);
        secuenciasController = new SecuenciasJpaController(em);
        ctesController = new CtesJpaController(em);
        
        enableDisableCamposCli(!this.jCBConsFinal.isSelected());
        
        consFinal = clientesController.findById(-1);       
        this.loadDatosConsFinal();
        
        if (consFinal == null){
            JOptionPane.showMessageDialog(null, "EL CONSUMIDOR FINAL NO HA SIDO REGISTRADO");
        }
        
        //EStablecer los anchos de las columnas
        jTableFactura.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //jTableFactura.getColumnModel().getColumn(0).setPreferredWidth(10);
        jTableFactura.getColumnModel().getColumn(FacturaDataModel.ColumnaFacturaEnum.CODBAR.index).setPreferredWidth(80);
        jTableFactura.getColumnModel().getColumn(FacturaDataModel.ColumnaFacturaEnum.ARTICULO.index).setPreferredWidth(180);        
        jTableFactura.getColumnModel().getColumn(FacturaDataModel.ColumnaFacturaEnum.CANTIDAD.index).setPreferredWidth(40);        
        jTableFactura.getColumnModel().getColumn(FacturaDataModel.ColumnaFacturaEnum.IVA.index).setPreferredWidth(80);
        
        pagosMap = new HashMap<Integer, FilaPago>();
        pagosMap.put(1, new FilaPago(1, "EFECTIVO", BigDecimal.ZERO, ""));
        pagosMap.put(2, new FilaPago(2, "CRÉDITO", BigDecimal.ZERO, ""));
        
        initNewFactura();        
        
        /*
        Configurar articulos
        */
        articulosDataModel = new ArticulosDataModel(2);
        articulosDataModel.setController(articulosController);
        
        articulosModelListener = new ArticulosModelListener();
        articulosDataModel.addTableModelListener(articulosModelListener);
        
        jTableArts.setModel(articulosDataModel);
        
        jTableArts.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTableArts.columnAtPoint(e.getPoint());
                String name = jTableArts.getColumnName(col);
                try{
                    for (int i=0; i<articulosDataModel.getColumnCount();i++){
                        jTableArts.getColumnModel().getColumn(i).setHeaderValue( articulosDataModel.getColumnName(i) );
                    }
                    articulosDataModel.switchSortColumn(col);
                }
                catch(Throwable ex){
                    JOptionPane.showMessageDialog(null, "Error en sort:"+ex.getMessage());
                    System.out.println("Error en sort:"+ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        
        jTableArts.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                //System.out.println("List selection changed--->");
            }
        });
        
        jTableArts.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2) {
                    doSelectionAction();                    
                }
            }
        });
        
        
        jTableArts.updateUI();
        
        try{
            articulosDataModel.loadFromDataBase();
        }
        catch(Throwable ex){
            System.out.println("Error al traer de base de datos:"+ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al traer de base de datos:"+ex.getMessage());
        }
        
        articulosDataModel.fireTableDataChanged(); 
        
        TableCellRenderer renderer = new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, 
                    Object value, 
                    boolean isSelected,
                    boolean hasFocus, 
                    int row, 
                    int column) {
                Component rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                return rendererComponent;
            }
        };
        jTableArts.setDefaultRenderer(BigDecimal.class, renderer);        
        pagosFrame = new PagosFrame(this);
        
        filtroTF.requestFocus();
        
        setResizable(true);
        pack();
    }
    
    public void syncPagos(Boolean isToPagos){
        if (isToPagos){
            pagosFrame.loadDatosPagos(
                    pagosMap.get(1).getMonto(),
                    pagosMap.get(2).getMonto(),
                    pagosMap.get(2).getObservacion()
            );
        }
        else{
            pagosMap = pagosFrame.getDatosPagos();
        }
    }    
    
    public void restoreDefaultsDividerLocation() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("restoreDefaultsDividerLocation --->");
                jSplitPane1.setDividerLocation( 0.7 );
            }
        });
    }
    
    public void reloadArts(){
        try{
            articulosDataModel.loadFromDataBase();
            System.out.println("ReloadArts performed--->");
        }
        catch(Throwable ex){
            System.out.println("Error al traer arts de base de datos:"+ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al traer de base de datos:"+ex.getMessage());
        }
    }
    
    public void doSelectionAction(){
        System.out.println("Select action");
        int row = this.jTableArts.getSelectedRow();
        if (row>-1){
            FilaArticulo filart = this.articulosDataModel.getValueAt(row);
            String codBarra = filart.getCodBarra();
            List<Articulos> articulosList =  articulosController.findByBarcode(codBarra);
            if (articulosList != null && articulosList.size()>0){
                addArticulo(articulosList.get(0));
                focusFiltro();
                //setVisible(false);
            }
            else{
                System.out.println("NO se pudo localizar el articulo:"+codBarra+", en la base de datos");
            }
        }        
    }
    
    protected MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }
    
    public void loadDatosConsFinal(){
        System.out.println("SE carga datos de cons final-->");        
        if (consFinal != null){            
            System.out.println("cons final es distinto de null-->");            
            this.cliCodigo = this.consFinal.getCliId();
            this.jTFCI.setText(this.consFinal.getCliCi());
            this.jTFCliente.setText(this.consFinal.getCliNombres());    
            
            this.jTFDireccion.setText("");
            this.jTFTelf.setText("");
            this.jTFEmail.setText("");
        }
        else{
            System.out.println("Consfinal es null-->");
        }
    }
    
    public void initNewFactura(){
        
        this.em = EntityManagerUtil.createEntintyManagerFactory();
        
        Secuencias secuencia = secuenciasController.getSecuencia("EST001");
        if (secuencia == null){
            JOptionPane.showMessageDialog(this, "ERROR:No se ha registrado la secuencia de facturas:EST001, favor registrar", "ERROR SECUENCIAS", JOptionPane.ERROR_MESSAGE);
        }
        else{
            jTFNumFact.setText( String.valueOf( secuencia.getSecValor() ) );
        }
        
        String estabPtoEmi = "";
        Ctes ctesStab = ctesController.findByClave("ESTAB");
        
        if (ctesStab == null){
            JOptionPane.showMessageDialog(this, "ERROR:No se ha registrado ESTAB en ctes", "ERROR CONFIG", JOptionPane.ERROR_MESSAGE);
        }
        else{
            estabPtoEmi = ctesStab.getCtesValor();
        }
        
        Ctes ctesPtoEmi = ctesController.findByClave("PTOEMI");
        
        if (ctesPtoEmi == null){
            JOptionPane.showMessageDialog(this, "ERROR:No se ha registrado ESTAB en ctes", "ERROR CONFIG", JOptionPane.ERROR_MESSAGE);
        }
        else{
            estabPtoEmi = estabPtoEmi+ctesStab.getCtesValor();        
        }
        
        this.jLabelEstPtoEmi.setText(estabPtoEmi);
        
        //fecha de emsion
        String fechaActual = FechasUtil.getFechaActual();
        //System.out.println("jTFFecha.setValue------>");
        //jTFFecha.setValue(new Date());
        this.jTFFecha.setText( fechaActual );    
        
        this.cliCodigo = 0;
        
        jTableFactura.updateUI();
        facturaDataModel.getItems().clear();
        facturaDataModel.fireTableDataChanged();
        
        facturaDataModel.encerarTotales();
        updateLabelsTotales();
        
        this.jTFCI.setText("");
        this.jTFCliente.setText("");
        this.jTFDireccion.setText("");
        this.jTFTelf.setText("");
        this.jTFEmail.setText("");
        
        this.jCBConsFinal.setSelected(true);
        this.loadDatosConsFinal();
        
        this.jTFVuelto.setText("");
        this.jLabelVuelto.setText("");        
        
        //barCodeInput.setText("");
        
        filtroTF.setText("");
        doFilter();
        filtroTF.requestFocus();
        
        //barCodeInput.requestFocus();    
        
        pagosMap = new HashMap<Integer, FilaPago>();
        pagosMap.put(1, new FilaPago(1, "EFECTIVO", BigDecimal.ZERO, ""));
        pagosMap.put(2, new FilaPago(2, "CRÉDITO", BigDecimal.ZERO, ""));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel11 = new javax.swing.JPanel();
        jPanelEst = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jButtonBorrar = new javax.swing.JButton();
        jButtonGuardar = new javax.swing.JButton();
        jButtonPagos = new javax.swing.JButton();
        jButtonSalir = new javax.swing.JButton();
        jPanelSouth = new javax.swing.JPanel();
        jPanelSubT = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabelSubTotal = new javax.swing.JLabel();
        jPanelTotal1 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabelDescuento = new javax.swing.JLabel();
        jPanelTotal = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabelIVA = new javax.swing.JLabel();
        jPanelIva = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabelTOTAL = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jTFVuelto = new javax.swing.JTextField();
        jLabelVuelto = new javax.swing.JLabel();
        jPanelNorth = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelDetallesFact = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableFactura = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabelEstPtoEmi = new javax.swing.JLabel();
        jTFNumFact = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTFFecha = new javax.swing.JFormattedTextField();
        jPanelDatosCli = new javax.swing.JPanel();
        jCBConsFinal = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTFCI = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTFCliente = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTFDireccion = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jTFTelf = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jTFEmail = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        filtroTF = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableArts = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 566));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jSplitPane1.setDividerLocation(100);
        jSplitPane1.setDividerSize(10);

        jPanel11.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel11.setLayout(new java.awt.BorderLayout());

        jPanelEst.setLayout(new java.awt.BorderLayout());

        jPanel9.setLayout(new java.awt.GridLayout(7, 1));

        jButtonBorrar.setText("Quitar");
        jButtonBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBorrarActionPerformed(evt);
            }
        });
        jPanel9.add(jButtonBorrar);

        jButtonGuardar.setText("Guardar");
        jButtonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGuardarActionPerformed(evt);
            }
        });
        jPanel9.add(jButtonGuardar);

        jButtonPagos.setText("Pagos");
        jButtonPagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPagosActionPerformed(evt);
            }
        });
        jPanel9.add(jButtonPagos);

        jButtonSalir.setText("Cerrar");
        jButtonSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSalirActionPerformed(evt);
            }
        });
        jPanel9.add(jButtonSalir);

        jPanelEst.add(jPanel9, java.awt.BorderLayout.CENTER);

        jPanel11.add(jPanelEst, java.awt.BorderLayout.EAST);

        jPanelSouth.setLayout(new java.awt.GridLayout(1, 5));

        jPanelSubT.setLayout(new java.awt.GridLayout(2, 1));

        jLabel2.setText("SUBTOTAL:");
        jPanelSubT.add(jLabel2);

        jLabelSubTotal.setFont(new java.awt.Font("Dialog", 0, 48)); // NOI18N
        jPanelSubT.add(jLabelSubTotal);

        jPanelSouth.add(jPanelSubT);

        jPanelTotal1.setLayout(new java.awt.GridLayout(2, 1));

        jLabel12.setText("Descuento:");
        jPanelTotal1.add(jLabel12);

        jLabelDescuento.setFont(new java.awt.Font("Dialog", 0, 48)); // NOI18N
        jPanelTotal1.add(jLabelDescuento);

        jPanelSouth.add(jPanelTotal1);

        jPanelTotal.setLayout(new java.awt.GridLayout(2, 1));

        jLabel4.setText("IVA:");
        jPanelTotal.add(jLabel4);

        jLabelIVA.setFont(new java.awt.Font("Dialog", 0, 48)); // NOI18N
        jPanelTotal.add(jLabelIVA);

        jPanelSouth.add(jPanelTotal);

        jPanelIva.setLayout(new java.awt.GridLayout(2, 1));

        jLabel6.setText("TOTAL:");
        jPanelIva.add(jLabel6);

        jLabelTOTAL.setFont(new java.awt.Font("Dialog", 0, 48)); // NOI18N
        jPanelIva.add(jLabelTOTAL);

        jPanelSouth.add(jPanelIva);

        jPanel10.setLayout(new java.awt.GridLayout(3, 1));

        jLabel13.setText("Cambio:");
        jPanel10.add(jLabel13);

        jTFVuelto.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jTFVuelto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFVueltoKeyReleased(evt);
            }
        });
        jPanel10.add(jTFVuelto);

        jLabelVuelto.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jPanel10.add(jLabelVuelto);

        jPanelSouth.add(jPanel10);

        jPanel11.add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setText("Registrar venta");

        javax.swing.GroupLayout jPanelNorthLayout = new javax.swing.GroupLayout(jPanelNorth);
        jPanelNorth.setLayout(jPanelNorthLayout);
        jPanelNorthLayout.setHorizontalGroup(
            jPanelNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNorthLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(350, Short.MAX_VALUE))
        );
        jPanelNorthLayout.setVerticalGroup(
            jPanelNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNorthLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelCenter.setLayout(new java.awt.BorderLayout());

        jPanelDetallesFact.setLayout(new java.awt.BorderLayout());

        jPanel6.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jTableFactura.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTableFactura.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableFactura.setRowHeight(30);
        jScrollPane1.setViewportView(jTableFactura);

        jPanel6.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel8.setLayout(new java.awt.BorderLayout());
        jPanel6.add(jPanel8, java.awt.BorderLayout.NORTH);

        jPanelDetallesFact.add(jPanel6, java.awt.BorderLayout.CENTER);

        jLabel10.setText("Nro:");

        jLabelEstPtoEmi.setText("001002");

        jLabel11.setText("Fecha:");

        try {
            jTFFecha.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jTFFecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFFechaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(240, 240, 240)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelEstPtoEmi)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFNumFact, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTFFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelEstPtoEmi, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTFNumFact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelDetallesFact.add(jPanel7, java.awt.BorderLayout.NORTH);

        jPanelCenter.add(jPanelDetallesFact, java.awt.BorderLayout.CENTER);

        jPanelDatosCli.setMinimumSize(new java.awt.Dimension(600, 100));
        jPanelDatosCli.setLayout(new java.awt.GridLayout(8, 1));

        jCBConsFinal.setSelected(true);
        jCBConsFinal.setText("Consumidor Final");
        jCBConsFinal.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCBConsFinalStateChanged(evt);
            }
        });
        jCBConsFinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBConsFinalActionPerformed(evt);
            }
        });
        jCBConsFinal.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jCBConsFinalPropertyChange(evt);
            }
        });
        jPanelDatosCli.add(jCBConsFinal);

        jPanel1.setLayout(new java.awt.GridLayout(2, 1));

        jLabel3.setText("CI/RUC:");
        jPanel1.add(jLabel3);

        jTFCI.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jTFCI.setMinimumSize(new java.awt.Dimension(10, 20));
        jTFCI.setPreferredSize(new java.awt.Dimension(10, 20));
        jTFCI.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCIFocusLost(evt);
            }
        });
        jTFCI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFCIActionPerformed(evt);
            }
        });
        jTFCI.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFCIKeyPressed(evt);
            }
        });
        jPanel1.add(jTFCI);

        jPanelDatosCli.add(jPanel1);

        jPanel2.setLayout(new java.awt.GridLayout(2, 1));

        jLabel5.setText("Cliente:");
        jPanel2.add(jLabel5);
        jPanel2.add(jTFCliente);

        jPanelDatosCli.add(jPanel2);

        jPanel3.setLayout(new java.awt.GridLayout(2, 1));

        jLabel7.setText("Direccion:");
        jPanel3.add(jLabel7);
        jPanel3.add(jTFDireccion);

        jPanelDatosCli.add(jPanel3);

        jPanel4.setLayout(new java.awt.GridLayout(2, 1));

        jLabel8.setText("Telf:");
        jPanel4.add(jLabel8);
        jPanel4.add(jTFTelf);

        jPanelDatosCli.add(jPanel4);

        jPanel5.setLayout(new java.awt.GridLayout(2, 1));

        jLabel9.setText("Email:");
        jPanel5.add(jLabel9);
        jPanel5.add(jTFEmail);

        jPanelDatosCli.add(jPanel5);

        jPanelCenter.add(jPanelDatosCli, java.awt.BorderLayout.WEST);

        jPanel11.add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(jPanel11);

        jPanel12.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel12.setPreferredSize(new java.awt.Dimension(200, 436));
        jPanel12.setLayout(new java.awt.BorderLayout());

        jPanel14.setLayout(new java.awt.BorderLayout());

        jPanel15.setLayout(new java.awt.BorderLayout());

        jLabel14.setText("Filtro");
        jPanel15.add(jLabel14, java.awt.BorderLayout.WEST);

        filtroTF.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        filtroTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtroTFActionPerformed(evt);
            }
        });
        filtroTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                filtroTFKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtroTFKeyReleased(evt);
            }
        });
        jPanel15.add(filtroTF, java.awt.BorderLayout.CENTER);

        jPanel14.add(jPanel15, java.awt.BorderLayout.NORTH);

        jScrollPane2.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        jTableArts.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTableArts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableArts.setRowHeight(25);
        jScrollPane2.setViewportView(jTableArts);

        jPanel14.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel12.add(jPanel14, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel12);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSalirActionPerformed
        //setVisible(false);
        
        FarmaAppMain farmaApp = (FarmaAppMain)this.root;        
        farmaApp.logicaClosePane(this.getClass().getName());
        
        
    }//GEN-LAST:event_jButtonSalirActionPerformed
    
    public void addArticulo(Articulos articulo){
        facturaDataModel.addItem(articulo);
        //barCodeInput.setText("");
    }
    
    /*
    private void findArticulo(){
        String barcode = this.barCodeInput.getText();
        List<Articulos> arts = this.articulosController.findByBarcode(barcode);
        
        if (arts != null && arts.size()>0){
            Articulos articulo =  arts.get(0);   
            addArticulo(articulo);            
        }
        else{
            JOptionPane.showMessageDialog(this, "No se encontró el código de barra:"+barcode, "Farmacia", JOptionPane.WARNING_MESSAGE);
        }
        
        barCodeInput.requestFocus();
    }
    */
    
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
        System.out.println("Window opened----->");        
        //barCodeInput.requestFocus();
        
    }//GEN-LAST:event_formWindowOpened
    
    public void logicaImpresion(DatosCabeceraFactura cabecera, 
            TotalesFactura totales, 
            List<FilaFactura> detalles){
        try{
            System.out.println("Logica de impresion-->"); 
            
            String templateCab = ctesController.findValueByClave("TEMPLATE_CAB");
            
            if (templateCab == null){
                System.out.println("Templatecab no definido");
                templateCab = "";
            }
            
            String templateFila = ctesController.findValueByClave("TEMPLATE_DET");
            if (templateFila == null){
                System.out.println("Templatedet no definido");
                templateFila = "";
            }
            
            String templatePie = ctesController.findValueByClave("TEMPLATE_PIE");
            if (templatePie == null){
                System.out.println("Templatepie no definido");
                templatePie = "";
            }
            
            try{                
                String textToPrint = GenTxtFactura.getTxt(
                        cabecera,
                        detalles, 
                        totales, 
                        templateCab, 
                        templateFila, 
                        templatePie
                );
                
                //Verificar el numero de copias ha imprimir
                Integer copysToPrint = 1;
                try{
                    String auxNroCopys = ctesController.findValueByClave("NUMBER_COPYS_PRINT");
                    if (auxNroCopys!=null){
                        copysToPrint = Integer.valueOf(auxNroCopys);
                    }
                }
                catch(Throwable ex){
                    System.out.println("Error al tratar de obtener el numero de copias ha imprimir: "+ex.getMessage());
                    ex.printStackTrace();
                }
                                
                if (copysToPrint>1){
                    textToPrint = GenTxtFactura.getCopias(textToPrint, copysToPrint);
                }
                
                System.out.println("texto ha imprimir");
                System.out.println(textToPrint);                    
                
                //IMPRESORA
                String impresora = ctesController.findValueByClave("IMPRESORA");
                TextPrinterUtil printerUtil = new TextPrinterUtil();
                printerUtil.imprimir(impresora, textToPrint);                            
            }
            catch(Throwable ex){
                System.out.println("Error al generar txt:"+ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al imprimir:"+ex.getMessage());
            }            
        }
        catch(Throwable ex){
            System.out.println("Error al tratar de imprimir:"+ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void jButtonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGuardarActionPerformed
        
        try{
            List<FilaFactura> detalles = facturaDataModel.getItems();
            
            //Verificar que se haya ingresado articulos
            if (detalles.size()==0){
                JOptionPane.showMessageDialog(null, "Debe agregar artículos!");
                return;
            }
            
            DatosCabeceraFactura cabeceraFactura = new DatosCabeceraFactura();
            cabeceraFactura.setNroEstFact(jLabelEstPtoEmi.getText());
            cabeceraFactura.setNumFactura(jTFNumFact.getText());
            if (this.jCBConsFinal.isSelected()){
                cabeceraFactura.setCliId(-1);
            }
            else{
                //Verificar que haya ingresado datos para el cliente
                if (this.cliCodigo == null|| this.cliCodigo == 0 ){
                    //Es un nuevo cliente verificar que haya ingresado el nui y el nombre
                    if (!StringUtil.isNotEmpty(this.jTFCI.getText())){
                        JOptionPane.showMessageDialog(null, "Debes ingresar el número de cédula o ruc del cliente!");
                        return;
                    }
                    else if (!StringUtil.isNotEmpty(this.jTFCliente.getText())){
                        JOptionPane.showMessageDialog(null, "Debes ingresar el nombre del cliente!");
                        return;
                    }
                }
                
                cabeceraFactura.setCliId(this.cliCodigo);
            }            
            cabeceraFactura.setCi(this.jTFCI.getText());
            cabeceraFactura.setCliente(this.jTFCliente.getText());
            cabeceraFactura.setDireccion(this.jTFDireccion.getText());
            cabeceraFactura.setTelf(this.jTFTelf.getText());
            cabeceraFactura.setEmail(this.jTFEmail.getText());
            cabeceraFactura.setFechaFactura(this.jTFFecha.getText());
            cabeceraFactura.setTraCodigo(this.tra_codigo);
            
            TotalesFactura totalesFactura = facturaDataModel.getTotalesFactura();
            BigDecimal sumaPagos = pagosMap.get(1).getMonto().add( pagosMap.get(2).getMonto() );
            
            if (NumbersUtil.round(sumaPagos, 2).compareTo( NumbersUtil.round(totalesFactura.getTotal(),2) )!=0){
                JOptionPane.showMessageDialog(null, "La suma de pagos:"+NumbersUtil.round(sumaPagos, 2).toPlainString()+", no coincide con el total de la factura:"+NumbersUtil.round(totalesFactura.getTotal(),2).toPlainString());
                return;
            }
            
            facturaController.crearFactura(cabeceraFactura, totalesFactura, detalles, pagosMap);
            
            int res = JOptionPane.showConfirmDialog(this, "Registrado Satisfactoriamente, Imprimir?", "Factura", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION){
                logicaImpresion(cabeceraFactura, totalesFactura, detalles);
            }
            
            initNewFactura();            
            
        }
        catch(Throwable ex){
            JOptionPane.showMessageDialog(null, "Error al registrar factura:"+ ex.getMessage(), "NO SE PUDO REGISTRAR FACTURA", JOptionPane.ERROR_MESSAGE);
            System.out.println("Error al registrar factura:"+ ex.getMessage());
            ex.printStackTrace();
        }        
        
    }//GEN-LAST:event_jButtonGuardarActionPerformed

    public void enableDisableCamposCli(boolean enable){
        this.jTFCI.setEnabled(enable);
        this.jTFCliente.setEnabled(enable);
        this.jTFDireccion.setEnabled(enable);
        this.jTFTelf.setEnabled(enable);
        this.jTFEmail.setEnabled(enable);        
        if (enable){
            this.jTFCI.requestFocus();
        }
    }    
    
    private void jCBConsFinalPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jCBConsFinalPropertyChange
        
        //System.out.println("prop value changed-->"+ evt.getNewValue() );
        
    }//GEN-LAST:event_jCBConsFinalPropertyChange

    private void jCBConsFinalStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCBConsFinalStateChanged
        //System.out.println("jCBConsFinalStateChanged value changed-->" );
        /*
        enableDisableCamposCli(!this.jCBConsFinal.isSelected());
        if (this.jCBConsFinal.isSelected()){
            loadDatosConsFinal();
        }
        else{
            this.clearCliente();
        }
        */
    }//GEN-LAST:event_jCBConsFinalStateChanged

    public void clearCliente(){        
        this.cliCodigo = 0;
        this.jTFCI.setText("");
        this.jTFCliente.setText("");
        this.jTFDireccion.setText("");
        this.jTFTelf.setText("");
        this.jTFEmail.setText("");
    }
    
    private void jCBConsFinalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBConsFinalActionPerformed
        
        enableDisableCamposCli(!this.jCBConsFinal.isSelected());
        if (this.jCBConsFinal.isSelected()){
            loadDatosConsFinal();
        }
        else{
            this.clearCliente();
        }
        
        
    }//GEN-LAST:event_jCBConsFinalActionPerformed

    private void jTFCIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFCIActionPerformed
                
        
    }//GEN-LAST:event_jTFCIActionPerformed
    
    private void findCliente(){
        
        String CI = this.jTFCI.getText();
        if (StringUtil.isNotEmpty(CI)){
            
            Clientes cliente = clientesController.findByCi(CI.trim());
            if (cliente!=null){
                jTFCliente.setText(cliente.getCliNombres());
                jTFDireccion.setText(cliente.getCliDir());
                jTFTelf.setText(cliente.getCliTelf());
                jTFEmail.setText(cliente.getCliEmail());                
                this.cliCodigo = cliente.getCliId();
            }
            else{
                jTFCliente.requestFocus();
                this.cliCodigo = 0;
                jTFCliente.setText("");
                jTFDireccion.setText("");
                jTFTelf.setText("");
                jTFEmail.setText("");                                
                System.out.println("Cliente no registrado-->");
            }
        }
        else{
            System.out.println("Ingrese el numero de cedula-->");
            //JOptionPane.showMessageDialog(this, "No se encuentra registrado");
            //jTFCliente.requestFocus();
        }        
    }
    
    public void focusBarCode(){
        //this.barCodeInput.requestFocus();        
    }
    
    public void focusFiltro(){
        this.filtroTF.setText("");
        this.filtroTF.requestFocus();        
    }
    
    private void jTFCIKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCIKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            findCliente();
        }        
    }//GEN-LAST:event_jTFCIKeyPressed

    private void jTFVueltoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFVueltoKeyReleased
        
        BigDecimal cambio = BigDecimal.ZERO;
        try{
            BigDecimal efectivo = new BigDecimal(this.jTFVuelto.getText());            
            TotalesFactura totalesFactura = facturaDataModel.getTotalesFactura();            
            BigDecimal totalFactura = totalesFactura.getTotal();
            
            if (efectivo.compareTo(totalFactura)>0){
                cambio = efectivo.subtract(totalFactura);
                cambio = cambio.setScale(2, RoundingMode.HALF_UP);
            }
            
        }
        catch(Throwable ex){
            System.out.println("Error al calcular el vuelto:"+ ex.getMessage());
            ex.printStackTrace();
        }
        
        this.jLabelVuelto.setText(cambio.toPlainString());
        
    }//GEN-LAST:event_jTFVueltoKeyReleased

    private void jButtonBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBorrarActionPerformed
        
        try{
            if (this.jTableFactura.getSelectedRows().length>0){
                int rowSelected = this.jTableFactura.getSelectedRows()[0];
                this.facturaDataModel.removeItem(rowSelected);
            }
            else{
                JOptionPane.showMessageDialog(null, "Debe seleccionar la fila que desea quitar");
            }            
        }
        catch(Throwable ex){
            System.out.println("Error al tratar de quitar item de factura:"+ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al tratar de quitar item de factura:"+ex.getMessage());
        }
        
        
    }//GEN-LAST:event_jButtonBorrarActionPerformed

    private void jTFFechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFFechaActionPerformed
        
    }//GEN-LAST:event_jTFFechaActionPerformed

    private void filtroTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtroTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtroTFActionPerformed

    private int currentRow = -1;
    private void filtroTFKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filtroTFKeyPressed
        //Verificdar si tecla presionada es flecha hacia abajo        
            
    }//GEN-LAST:event_filtroTFKeyPressed

    private void filtroTFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filtroTFKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_DOWN){
            System.out.println("Arrow down key pressed --->");            
            if (currentRow< this.jTableArts.getModel().getRowCount()){
                currentRow +=1;
            }
            
            System.out.println("Current row:" + currentRow);
            
            jTableArts.setRowSelectionInterval(currentRow, currentRow);
            //jTableArts. getSelectionModel().addSelectionInterval(currentRow, currentRow);
            
            
        }        
        else if (evt.getKeyCode() == KeyEvent.VK_UP){
            if (currentRow>0)
            currentRow -=1;            
            System.out.println("Current row:" + currentRow);
            
            System.out.println("Arrow up key pressed --->");            
            jTableArts.setRowSelectionInterval(currentRow, currentRow);
        }    
        else if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            
            int row = this.jTableArts.getSelectedRow();            
            if (row>-1){
                System.out.println("Enter seleccion row-->");
                doSelectionAction();
            }
            else{
                System.out.println("Enter barcode sel-->");
            }
        }
        else{
            currentRow =-1;
            jTableArts.clearSelection();
            //jTableArts.setRowSelectionInterval(currentRow, currentRow);
            doFilter();            
        }

    }//GEN-LAST:event_filtroTFKeyReleased

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        
        System.out.println("Window activated----->");        
        //barCodeInput.requestFocus();
        
    }//GEN-LAST:event_formWindowActivated

    private void jButtonPagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPagosActionPerformed
        syncPagos(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        pagosFrame.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        pagosFrame.setVisible(true);
    }//GEN-LAST:event_jButtonPagosActionPerformed

    private void jTFCIFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCIFocusLost
       findCliente();
        
    }//GEN-LAST:event_jTFCIFocusLost
    
    
    public void doFilter(){
        //System.out.println("do filter--->");
        
        //System.out.println("Filtro key pressed--->");        
        //Se debe filtrar todos los articulos del model                
        String filtro = this.filtroTF.getText().trim();
        if (filtro.length()>=0){
            //System.out.println("Se aplica el filtro:"+filtro);
            try{
                if (this.articulosDataModel != null){
                    this.articulosDataModel.loadFromDataBaseFilter(filtro);
                }
            }
            catch(Throwable ex){
                System.out.println("Error al cargar datos:"+ ex.getMessage());
                ex.printStackTrace();
            }
        }
        
    }
    
    public void updateLabelsTotales(){
        TotalesFactura totalesFactura = facturaDataModel.getTotalesFactura();
        
        if (pagosMap != null){            
            FilaPago pagoEfectivo =  pagosMap.get(1);
            FilaPago pagoCredito =  pagosMap.get(2);

            BigDecimal pagoSubst = totalesFactura.getTotal().subtract(pagoCredito.getMonto());
            if (pagoSubst.compareTo(BigDecimal.ZERO)<0){
                pagoEfectivo.setMonto( BigDecimal.ZERO );
            }
            else{
                pagoEfectivo.setMonto( NumbersUtil.round(pagoSubst, 2) );
            }
        }
        
        jLabelSubTotal.setText( totalesFactura.getSubtotal().toPlainString() );
        jLabelIVA.setText( totalesFactura.getIva().toPlainString() );
        jLabelTOTAL.setText( totalesFactura.getTotal().toPlainString() );
        jLabelDescuento.setText( totalesFactura.getDescuento().toPlainString() );
    }
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField filtroTF;
    private javax.swing.JButton jButtonBorrar;
    private javax.swing.JButton jButtonGuardar;
    private javax.swing.JButton jButtonPagos;
    private javax.swing.JButton jButtonSalir;
    private javax.swing.JCheckBox jCBConsFinal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelDescuento;
    private javax.swing.JLabel jLabelEstPtoEmi;
    private javax.swing.JLabel jLabelIVA;
    private javax.swing.JLabel jLabelSubTotal;
    private javax.swing.JLabel jLabelTOTAL;
    private javax.swing.JLabel jLabelVuelto;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelDatosCli;
    private javax.swing.JPanel jPanelDetallesFact;
    private javax.swing.JPanel jPanelEst;
    private javax.swing.JPanel jPanelIva;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelSubT;
    private javax.swing.JPanel jPanelTotal;
    private javax.swing.JPanel jPanelTotal1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField jTFCI;
    private javax.swing.JTextField jTFCliente;
    private javax.swing.JTextField jTFDireccion;
    private javax.swing.JTextField jTFEmail;
    private javax.swing.JFormattedTextField jTFFecha;
    private javax.swing.JTextField jTFNumFact;
    private javax.swing.JTextField jTFTelf;
    private javax.swing.JTextField jTFVuelto;
    private javax.swing.JTable jTableArts;
    private javax.swing.JTable jTableFactura;
    // End of variables declaration//GEN-END:variables
}