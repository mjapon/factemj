/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui;

import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
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
import jj.util.datamodels.rows.FilaArticulo;
import jj.util.datamodels.rows.FilaFactura;
import jj.util.datamodels.rows.FilaPago;
import jj.util.IVAComboBoxEditor;
import jj.util.IVAComboBoxRenderer;
import jj.util.NumbersUtil;
import jj.util.PrintFactUtil;
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
    
    /**
     * Creates new form FacturaVentaFrame
     */
    public FacturaVentaFrame(Integer tra_codigo) {
        initComponents();
        
        this.tra_codigo = tra_codigo;  
        
        if (this.tra_codigo == 1){
            this.jLabelTitulo.setText("FACTURA DE VENTA");
        }
        else if (this.tra_codigo == 2){
            this.jLabelTitulo.setText("FACTURA DE COMPRA");
        }
        
        facturaDataModel = new FacturaDataModel(this.tra_codigo);
        facturaDataModel.setFrame(this);
        facturaModelListener = new FacturaModelListener();
        facturaDataModel.addTableModelListener(facturaModelListener);        
        jTableFactura.setModel(facturaDataModel); 
        facturaDataModel.setJtable(jTableFactura);

        this.em = EntityManagerUtil.createEntintyManagerFactory();
       
        articulosController = new ArticulosJpaController(em);
        clientesController = new ClientesJpaController(em);
        facturaController = new FacturasJpaController(em);
        secuenciasController = new SecuenciasJpaController(em);
        ctesController = new CtesJpaController(em);   
        
        consFinal = clientesController.findById(-1); 
        if (consFinal == null){
            JOptionPane.showMessageDialog(null, "EL CONSUMIDOR FINAL NO HA SIDO REGISTRADO");
        }
        
        //Configurar Tables
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
        
        //EStablecer los anchos de las columnas
        jTableFactura.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);        
        jTableFactura.getColumnModel().getColumn(FacturaDataModel.ColumnaFacturaEnum.CODBAR.index).setPreferredWidth(80);
        jTableFactura.getColumnModel().getColumn(FacturaDataModel.ColumnaFacturaEnum.ARTICULO.index).setPreferredWidth(200);
        jTableFactura.getColumnModel().getColumn(FacturaDataModel.ColumnaFacturaEnum.CANTIDAD.index).setPreferredWidth(40);        
        jTableFactura.getColumnModel().getColumn(FacturaDataModel.ColumnaFacturaEnum.IVA.index).setPreferredWidth(80);
        
        /*
        jTableFactura.updateUI();
        facturaDataModel.fireTableDataChanged();
        */
        
        pagosMap = new HashMap<Integer, FilaPago>();
        pagosMap.put(1, new FilaPago(1, "EFECTIVO", BigDecimal.ZERO, ""));
        pagosMap.put(2, new FilaPago(2, "CRÉDITO", BigDecimal.ZERO, ""));
                
        //DataModels
        //Para el tipo de precio que se debe mostrar, columnas de articulos
        int modelType = 2;
        if (this.tra_codigo == 2){//Factura de compra
            modelType = 3;
        }
        
        articulosDataModel = new ArticulosDataModel(modelType);
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
        
        float[] columnWidthPercentage = {20.0f, 55.0f, 10.0f, 5.0f, 5.0f, 5.0f};
        
        resizeColumns();
        jTableArts.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeColumns();
            }
        });
    
        /*
        jTableArts.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);        
        jTableArts.getColumnModel().getColumn(0).setPreferredWidth(210);
        jTableArts.getColumnModel().getColumn(1).setPreferredWidth(80);
        jTableArts.getColumnModel().getColumn(2).setPreferredWidth(60);
        */
        
        
                
        /*
        try{
            articulosDataModel.loadFromDataBase();
        }
        catch(Throwable ex){
            System.out.println("Error al traer de base de datos:"+ex.getMessage());
            JOptionPane.showMessageDialog(null, "Error al traer de base de datos:"+ex.getMessage());
        }
        articulosDataModel.fireTableDataChanged(); 
        */
        jTableArts.updateUI();
        
        
        //Se establece el parent para el frame de pagos
        pagosFrame = new PagosFrame(this);
        
        initNewFactura();              
    }
    
    float[] columnWidthPercentage = {50.0f, 25.0f, 25.0f};
    
    private void resizeColumns() {
        int tW = jTableArts.getWidth();
        TableColumn column;
        TableColumnModel jTableColumnModel = jTableArts.getColumnModel();
        int cantCols = jTableColumnModel.getColumnCount();
        for (int i = 0; i < cantCols; i++) {
            column = jTableColumnModel.getColumn(i);
                int pWidth = Math.round(columnWidthPercentage[i] * tW);
            column.setPreferredWidth(pWidth);
        }
}
    
    public void updateArticulos(){
        //Cargar articulos en venta de factura
        try{
            articulosDataModel.loadFromDataBase();
        }
        catch(Throwable ex){
            System.out.println("Error al traer de base de datos:"+ex.getMessage());
            JOptionPane.showMessageDialog(null, "Error al traer de base de datos:"+ex.getMessage());
        }
        articulosDataModel.fireTableDataChanged(); 
        jTableArts.updateUI();
        
        System.out.println("Articulos updated----->");
    }
    
    public void initNewFactura(){        
        
        this.em = EntityManagerUtil.createEntintyManagerFactory();        
        
        if (this.tra_codigo == 1)//Factura de venta:
        {   
            this.jLabelRef.setText("Cliente");
                
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
            
            
            Secuencias secuencia = secuenciasController.getSecuencia("EST001");
            if (secuencia == null){
                JOptionPane.showMessageDialog(this, "ERROR:No se ha registrado la secuencia de facturas:EST001, favor registrar", "ERROR SECUENCIAS", JOptionPane.ERROR_MESSAGE);
            }
            else{
                jTFNumFact.setText( String.valueOf( secuencia.getSecValor() ) );
            }
        
        }        
        else if (this.tra_codigo == 2){
            
            this.jLabelRef.setText("Proveedor");
            this.jLabelEstPtoEmi.setText("");
            this.jTFNumFact.setText("");
            
        }
        
        //fecha de emsion
        String fechaActual = FechasUtil.getFechaActual();
        //System.out.println("jTFFecha.setValue------>");
        //jTFFecha.setValue(new Date());
        this.jTFFecha.setText( fechaActual );            
        this.cliCodigo = 0;
        
        //Limpiar items vendidos y encerear totales        
        facturaDataModel.getItems().clear();
        facturaDataModel.fireTableDataChanged();
        facturaDataModel.encerarTotales();
        jTableFactura.updateUI();
        updateLabelsTotales();
        
        this.jTFCI.setText("");
        this.jTFCliente.setText("");
        this.jTFDireccion.setText("");
        this.jTFTelf.setText("");
        this.jTFEmail.setText("");
        
        if (this.tra_codigo ==1 ){
            this.jCBConsFinal.setSelected(true);
            this.loadDatosConsFinal();
            enableDisableCamposCli(false);
        }
        else if (this.tra_codigo == 2){
            this.jCBConsFinal.setSelected(false);  
            this.jCBConsFinal.setEnabled(false);
            this.clearCliente();
            enableDisableCamposCli(true);
        }        
        
        this.jTFVuelto.setText("");
        this.jLabelVuelto.setText("");        
        
        filtroTF.setText("");
        filtroTF.requestFocus();
        
        doFilter();
        
        pagosMap = new HashMap<Integer, FilaPago>();
        pagosMap.put(1, new FilaPago(1, "EFECTIVO", BigDecimal.ZERO, ""));
        pagosMap.put(2, new FilaPago(2, "CRÉDITO", BigDecimal.ZERO, ""));
        
        this.jTFEfectivo.setText(BigDecimal.ZERO.toPlainString());
        this.jTFCredito.setText(BigDecimal.ZERO.toPlainString());
        this.jTFTotalPagos.setText(BigDecimal.ZERO.toPlainString());
        this.jTextAreaObs.setText("");
    }

    public void setPagosMap(){
        Map<Integer, FilaPago> mapPagos = new HashMap<Integer, FilaPago>();
        mapPagos.put(1, new FilaPago(1, "EFECTIVO", new BigDecimal(this.jTFEfectivo.getText()), ""));
        mapPagos.put(2, new FilaPago(2, "CRÉDITO", new BigDecimal(this.jTFCredito.getText()), this.jTextAreaObs.getText()));
        pagosMap = mapPagos;
    }
    
    public void syncPagos(Boolean isToPagos){
        if (isToPagos){
            this.jTFEfectivo.setText(pagosMap.get(1).getMonto().toPlainString());
            this.jTFCredito.setText(pagosMap.get(2).getMonto().toPlainString());
        }
        else{            
            Map<Integer, FilaPago> mapPagos = new HashMap<Integer, FilaPago>();
            mapPagos.put(1, new FilaPago(1, "EFECTIVO", new BigDecimal(this.jTFEfectivo.getText()), ""));
            mapPagos.put(2, new FilaPago(2, "CRÉDITO", new BigDecimal(this.jTFCredito.getText()), this.jTextAreaObs.getText()));
            pagosMap = mapPagos;
        }
    }    
    
    public void restoreDefaultsDividerLocation() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("restoreDefaultsDividerLocation --->");
                jSplitPane1.setDividerLocation( 0.75 );
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
        jButtonSalir = new javax.swing.JButton();
        jPanelCenter = new javax.swing.JPanel();
        jPanelDetallesFact = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableFactura = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jPanelSubT = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabelSubTotal = new javax.swing.JLabel();
        jPanelTotal1 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabelDescuento = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jTFDescGlobal = new javax.swing.JTextField();
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
        jPanel1FormasPago = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTFEfectivo = new javax.swing.JTextField();
        jTFCredito = new javax.swing.JTextField();
        jTFTotalPagos = new javax.swing.JTextField();
        jPanelObs = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaObs = new javax.swing.JTextArea();
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
        jLabelRef = new javax.swing.JLabel();
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
        jPanelNorth = new javax.swing.JPanel();
        jLabelTitulo = new javax.swing.JLabel();
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

        jButtonBorrar.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-trash.png"))); // NOI18N
        jButtonBorrar.setText("Quitar");
        jButtonBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBorrarActionPerformed(evt);
            }
        });
        jPanel9.add(jButtonBorrar);

        jButtonGuardar.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-save.png"))); // NOI18N
        jButtonGuardar.setText("Guardar");
        jButtonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGuardarActionPerformed(evt);
            }
        });
        jPanel9.add(jButtonGuardar);

        jButtonSalir.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-close_pane_filled.png"))); // NOI18N
        jButtonSalir.setText("Cerrar");
        jButtonSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSalirActionPerformed(evt);
            }
        });
        jPanel9.add(jButtonSalir);

        jPanelEst.add(jPanel9, java.awt.BorderLayout.CENTER);

        jPanel11.add(jPanelEst, java.awt.BorderLayout.EAST);

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

        jPanel8.setLayout(new java.awt.GridLayout(3, 1));

        jPanelSouth.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Totales Factura", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 13))); // NOI18N
        jPanelSouth.setPreferredSize(new java.awt.Dimension(467, 60));
        jPanelSouth.setLayout(new java.awt.GridLayout(1, 5));

        jPanelSubT.setLayout(new java.awt.GridLayout(2, 1));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("SUBTOTAL:");
        jPanelSubT.add(jLabel2);

        jLabelSubTotal.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jPanelSubT.add(jLabelSubTotal);

        jPanelSouth.add(jPanelSubT);

        jPanelTotal1.setLayout(new java.awt.GridLayout(2, 2));

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText("DESC. X FILA:");
        jLabel12.setToolTipText("");
        jPanelTotal1.add(jLabel12);

        jLabelDescuento.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jPanelTotal1.add(jLabelDescuento);

        jLabel17.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel17.setText("DESC. FACTURA:");
        jLabel17.setToolTipText("");
        jPanelTotal1.add(jLabel17);

        jTFDescGlobal.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jTFDescGlobal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFDescGlobalKeyReleased(evt);
            }
        });
        jPanelTotal1.add(jTFDescGlobal);

        jPanelSouth.add(jPanelTotal1);

        jPanelTotal.setLayout(new java.awt.GridLayout(2, 1));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText("IVA:");
        jPanelTotal.add(jLabel4);

        jLabelIVA.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jPanelTotal.add(jLabelIVA);

        jPanelSouth.add(jPanelTotal);

        jPanelIva.setLayout(new java.awt.GridLayout(2, 1));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("TOTAL:");
        jPanelIva.add(jLabel6);

        jLabelTOTAL.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jLabelTOTAL.setForeground(new java.awt.Color(0, 204, 51));
        jPanelIva.add(jLabelTOTAL);

        jPanelSouth.add(jPanelIva);

        jPanel10.setLayout(new java.awt.GridLayout(2, 1));

        jLabel13.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel13.setText("Cambio:");
        jPanel10.add(jLabel13);

        jTFVuelto.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jTFVuelto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFVueltoKeyReleased(evt);
            }
        });
        jPanel10.add(jTFVuelto);

        jLabelVuelto.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        jPanel10.add(jLabelVuelto);

        jPanelSouth.add(jPanel10);

        jPanel8.add(jPanelSouth);

        jPanel1FormasPago.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Formas de Pago", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 13))); // NOI18N
        jPanel1FormasPago.setLayout(new java.awt.GridLayout(2, 3));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("EFECTIVO:");
        jPanel1FormasPago.add(jLabel5);

        jLabel15.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel15.setText("CRÉDITO:");
        jPanel1FormasPago.add(jLabel15);

        jLabel16.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel16.setText("TOTAL:");
        jPanel1FormasPago.add(jLabel16);

        jTFEfectivo.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jTFEfectivo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFEfectivoFocusLost(evt);
            }
        });
        jTFEfectivo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFEfectivoKeyReleased(evt);
            }
        });
        jPanel1FormasPago.add(jTFEfectivo);

        jTFCredito.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jTFCredito.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFCreditoFocusLost(evt);
            }
        });
        jTFCredito.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFCreditoKeyReleased(evt);
            }
        });
        jPanel1FormasPago.add(jTFCredito);

        jTFTotalPagos.setEditable(false);
        jTFTotalPagos.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jTFTotalPagos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFTotalPagosKeyReleased(evt);
            }
        });
        jPanel1FormasPago.add(jTFTotalPagos);

        jPanel8.add(jPanel1FormasPago);

        jPanelObs.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Observación", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 13))); // NOI18N
        jPanelObs.setLayout(new java.awt.BorderLayout());

        jTextAreaObs.setColumns(20);
        jTextAreaObs.setRows(5);
        jScrollPane3.setViewportView(jTextAreaObs);

        jPanelObs.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel8.add(jPanelObs);

        jPanel6.add(jPanel8, java.awt.BorderLayout.SOUTH);

        jPanelDetallesFact.add(jPanel6, java.awt.BorderLayout.CENTER);

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText("Nro:");

        jLabelEstPtoEmi.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabelEstPtoEmi.setText("001002");

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
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
                .addGap(159, 159, 159)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelEstPtoEmi)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFNumFact, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(77, 77, 77)
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

        jCBConsFinal.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
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

        jLabel3.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
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

        jLabelRef.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabelRef.setText("Cliente:");
        jPanel2.add(jLabelRef);
        jPanel2.add(jTFCliente);

        jPanelDatosCli.add(jPanel2);

        jPanel3.setLayout(new java.awt.GridLayout(2, 1));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel7.setText("Direccion:");
        jPanel3.add(jLabel7);
        jPanel3.add(jTFDireccion);

        jPanelDatosCli.add(jPanel3);

        jPanel4.setLayout(new java.awt.GridLayout(2, 1));

        jLabel8.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel8.setText("Telf:");
        jPanel4.add(jLabel8);
        jPanel4.add(jTFTelf);

        jPanelDatosCli.add(jPanel4);

        jPanel5.setLayout(new java.awt.GridLayout(2, 1));

        jLabel9.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel9.setText("Email:");
        jPanel5.add(jLabel9);
        jPanel5.add(jTFEmail);

        jPanelDatosCli.add(jPanel5);

        jPanelCenter.add(jPanelDatosCli, java.awt.BorderLayout.WEST);

        jPanel11.add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jLabelTitulo.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabelTitulo.setText("Registrar venta");

        javax.swing.GroupLayout jPanelNorthLayout = new javax.swing.GroupLayout(jPanelNorth);
        jPanelNorth.setLayout(jPanelNorthLayout);
        jPanelNorthLayout.setHorizontalGroup(
            jPanelNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNorthLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitulo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelNorthLayout.setVerticalGroup(
            jPanelNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNorthLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitulo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.add(jPanelNorth, java.awt.BorderLayout.NORTH);

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
        farmaApp.logicaClosePane(this.getClass().getName()+this.tra_codigo);
    }//GEN-LAST:event_jButtonSalirActionPerformed
    
    public void addArticulo(Articulos articulo){
        facturaDataModel.addItem(articulo);
    }
    
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        //System.out.println("Window opened----->");        
        //barCodeInput.requestFocus();
        
    }//GEN-LAST:event_formWindowOpened
    
    //PrintFactUtil
    public void logicaImpresion(DatosCabeceraFactura cabecera, 
            TotalesFactura totales, 
            List<FilaFactura> detalles){
        PrintFactUtil.imprimir(ctesController, cabecera, totales, detalles);
    }    
   
    
    private void jButtonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGuardarActionPerformed
        try{
            List<FilaFactura> detalles = facturaDataModel.getItems();
            
            //Verificar que se haya ingresado articulos
            if (detalles.size()==0){
                JOptionPane.showMessageDialog(null, "Debe agregar items!");
                return;
            }
            
            DatosCabeceraFactura cabeceraFactura = new DatosCabeceraFactura();
            cabeceraFactura.setNroEstFact(jLabelEstPtoEmi.getText());
            cabeceraFactura.setNumFactura(jTFNumFact.getText());
            cabeceraFactura.setTraCodigo(this.tra_codigo);
            
            boolean permitConsFinal = this.tra_codigo == 1;
            
            //Verificar que haya ingresado datos para el cliente            
            String refName = "cliente";
            if (this.tra_codigo == 2){//Compras
                refName = "proveedor";
            }
            
            if (!permitConsFinal){                
                //Validar el ingreso del cliente                
                if (StringUtil.isEmpty(this.jTFCI.getText())){
                    JOptionPane.showMessageDialog(null, "Debes ingresar el número de cédula o ruc del "+refName+"!");
                    return;
                }
                else if (StringUtil.isEmpty(this.jTFCliente.getText())){
                    JOptionPane.showMessageDialog(null, "Debes ingresar el nombre del "+refName+"!");
                    return;
                }
            }
            
            if (StringUtil.isEmpty(jTFNumFact.getText())){
                JOptionPane.showMessageDialog(null, "Debes ingresar el numero de la factura");
                return;
            }
                
            cabeceraFactura.setCliId(this.cliCodigo);                     
            cabeceraFactura.setCi(this.jTFCI.getText());
            cabeceraFactura.setCliente(this.jTFCliente.getText());
            cabeceraFactura.setDireccion(this.jTFDireccion.getText());
            cabeceraFactura.setTelf(this.jTFTelf.getText());
            cabeceraFactura.setEmail(this.jTFEmail.getText());
            cabeceraFactura.setFechaFactura(this.jTFFecha.getText());
            cabeceraFactura.setTraCodigo(this.tra_codigo);
            
            TotalesFactura totalesFactura = facturaDataModel.getTotalesFactura();
            
            setPagosMap();
            
            BigDecimal sumaPagos = pagosMap.get(1).getMonto().add( pagosMap.get(2).getMonto() );
            
            if (NumbersUtil.round(sumaPagos, 2).compareTo( NumbersUtil.round(totalesFactura.getTotal(),2) )!=0){
                JOptionPane.showMessageDialog(null, "La suma de pagos:"+NumbersUtil.round(sumaPagos, 2).toPlainString()+", no coincide con el total de la factura:"+NumbersUtil.round(totalesFactura.getTotal(),2).toPlainString());
                return;
            }
            
            facturaController.crearFactura(cabeceraFactura, totalesFactura, detalles, pagosMap);
            
            int res = JOptionPane.showConfirmDialog(this, "Registrado Satisfactoriamente, Imprimir?", "Comprobante", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION){
                logicaImpresion(cabeceraFactura, totalesFactura, detalles);
            }
            
            initNewFactura();            
        }
        catch(Throwable ex){
            JOptionPane.showMessageDialog(null, "Error al registrar factura:"+ ex.getMessage(), "NO SE PUDO REGISTRAR", JOptionPane.ERROR_MESSAGE);
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
       
               
    }//GEN-LAST:event_jCBConsFinalPropertyChange

    private void jCBConsFinalStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCBConsFinalStateChanged
       
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
        
        if (jTableArts != null){
            currentRow =-1;
            jTableArts.clearSelection();
        }
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
        }        
        else if (evt.getKeyCode() == KeyEvent.VK_UP){
            if (currentRow>0){
                currentRow -=1;
                System.out.println("Current row:" + currentRow);
            }            
            System.out.println("Arrow up key pressed --->");
            jTableArts.setRowSelectionInterval(currentRow, currentRow);
        }    
        else if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            
            //Verificar si el filtro ingresado solo son numeros entonces se procede con busqueda por codigo de barra
            //Caso contrario se filtra
            String filtro = this.filtroTF.getText().trim();
            boolean esBarcode = false;
            if (filtro.length()>3){
                esBarcode = filtro.chars().allMatch( Character::isDigit );
            }            
            if (esBarcode){
                currentRow =-1;
                List<Articulos> arts = this.articulosController.findByBarcode(filtro);        
                if (arts != null && arts.size()>0){
                    Articulos articulo =  arts.get(0);   
                    addArticulo(articulo);            
                    focusFiltro();
                }
                else{
                    JOptionPane.showMessageDialog(this, "No se encontró el código de barra:"+filtro, "Facturado", JOptionPane.WARNING_MESSAGE);
                }
            }
            else{
                int row = this.jTableArts.getSelectedRow();            
                if (row>-1){
                    System.out.println("Enter seleccion row-->");
                    doSelectionAction();
                }
                else{
                    System.out.println("Enter barcode sel-->");
                }
            }
        }
        else{
            currentRow =-1;
            jTableArts.clearSelection();
            doFilter();
        }

    }//GEN-LAST:event_filtroTFKeyReleased

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        
        System.out.println("Window activated----->");        
        //barCodeInput.requestFocus();
        
    }//GEN-LAST:event_formWindowActivated

    private void jTFCIFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCIFocusLost
       findCliente();        
    }//GEN-LAST:event_jTFCIFocusLost

    private void jTFEfectivoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFEfectivoKeyReleased
        onMontoEfectivoChange();
    }//GEN-LAST:event_jTFEfectivoKeyReleased

    private void jTFCreditoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFCreditoKeyReleased
        onMontoCreditoChange();
    }//GEN-LAST:event_jTFCreditoKeyReleased

    private void jTFTotalPagosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFTotalPagosKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFTotalPagosKeyReleased

    public void sumaEfectivoCredito(){
        
        BigDecimal efectivo = new BigDecimal(this.jTFEfectivo.getText());
        BigDecimal credito = new BigDecimal(this.jTFCredito.getText());
        
        BigDecimal montoTotal = efectivo.add(credito);
        
        this.jTFTotalPagos.setText( NumbersUtil.round(montoTotal, 2).toPlainString() );
    }
    
    public void onMontoCreditoChange(){
        try{            
            BigDecimal montoTotal = this.facturaDataModel.getTotalesFactura().getTotal();            
            BigDecimal montoCredito = new BigDecimal(jTFCredito.getText());
            
            if (montoCredito.compareTo(montoTotal)<=0){
                BigDecimal montoEfectivo = NumbersUtil.round(montoTotal.subtract(montoCredito), 2);
                this.jTFEfectivo.setText(montoEfectivo.toPlainString());
            }
            
            sumaEfectivoCredito();
        }
        catch(Throwable ex){
            System.out.println("Error al calcular montos:"+ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void onMontoEfectivoChange(){
        try{            
            BigDecimal montoTotal = this.facturaDataModel.getTotalesFactura().getTotal();            
            BigDecimal montoEfectivo = new BigDecimal(jTFEfectivo.getText());
            
            if (montoEfectivo.compareTo(montoTotal)<=0){
                BigDecimal montoCredito = NumbersUtil.round(montoTotal.subtract(montoEfectivo), 2);
                this.jTFCredito.setText(montoCredito.toPlainString());
            }
            
            sumaEfectivoCredito();
        }
        catch(Throwable ex){
            System.out.println("Error al calcular montos:"+ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void jTFCreditoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFCreditoFocusLost
       onMontoCreditoChange();
        
    }//GEN-LAST:event_jTFCreditoFocusLost

    private void jTFEfectivoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFEfectivoFocusLost
       onMontoEfectivoChange();
    }//GEN-LAST:event_jTFEfectivoFocusLost

    private void jTFDescGlobalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDescGlobalKeyReleased
        //Se debe realizar el calcula de la factura
        
    }//GEN-LAST:event_jTFDescGlobalKeyReleased
    
    public void doFilter(){
        String filtro = this.filtroTF.getText().trim();
        if (filtro.length()>=0){
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
            
            this.jTFEfectivo.setText(pagoEfectivo.getMonto().toPlainString());
            this.jTFCredito.setText(pagoCredito.getMonto().toPlainString());
        }
        
        jLabelSubTotal.setText( totalesFactura.getSubtotal().toPlainString() );
        jLabelIVA.setText( totalesFactura.getIva().toPlainString() );
        jLabelTOTAL.setText( totalesFactura.getTotal().toPlainString() );
        jLabelDescuento.setText( totalesFactura.getDescuento().toPlainString() );
    }
    
    public JFrame getRoot() {
           return root;
    }

    public void setRoot(JFrame root) {
         this.root = root;
    }         
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField filtroTF;
    private javax.swing.JButton jButtonBorrar;
    private javax.swing.JButton jButtonGuardar;
    private javax.swing.JButton jButtonSalir;
    private javax.swing.JCheckBox jCBConsFinal;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
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
    private javax.swing.JLabel jLabelRef;
    private javax.swing.JLabel jLabelSubTotal;
    private javax.swing.JLabel jLabelTOTAL;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JLabel jLabelVuelto;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel1FormasPago;
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
    private javax.swing.JPanel jPanelObs;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelSubT;
    private javax.swing.JPanel jPanelTotal;
    private javax.swing.JPanel jPanelTotal1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField jTFCI;
    private javax.swing.JTextField jTFCliente;
    private javax.swing.JTextField jTFCredito;
    private javax.swing.JTextField jTFDescGlobal;
    private javax.swing.JTextField jTFDireccion;
    private javax.swing.JTextField jTFEfectivo;
    private javax.swing.JTextField jTFEmail;
    private javax.swing.JFormattedTextField jTFFecha;
    private javax.swing.JTextField jTFNumFact;
    private javax.swing.JTextField jTFTelf;
    private javax.swing.JTextField jTFTotalPagos;
    private javax.swing.JTextField jTFVuelto;
    private javax.swing.JTable jTableArts;
    private javax.swing.JTable jTableFactura;
    private javax.swing.JTextArea jTextAreaObs;
    // End of variables declaration//GEN-END:variables
}
