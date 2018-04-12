/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.text.MaskFormatter;
import jj.controller.FacturasJpaController;
import jj.controller.MovtransaccJpaController;
import jj.util.EntityManagerUtil;
import jj.util.FechasUtil;
import jj.util.FilaAbonos;
import jj.util.FilaCXCP;
import jj.util.FilaVenta;
import jj.util.ParamBusquedaCXCP;
import jj.util.datamodels.AbonosDataModel;
import jj.util.datamodels.CuentaXCPDataModel;

/**
 *
 * @author mjapon
 */
public class CuentasXCBPFrame extends javax.swing.JFrame {    
   
    private JFormattedTextField desdeTF;
    private JFormattedTextField hastaTF;
    private CuentaXCPDataModel cPDataModel;  
    private AbonosDataModel abonosDataModel;
    private Integer clpCodigo;
    private Integer estadoCobro;    
    private EntityManager em;
    private FacturasJpaController facturaController;
    private MovtransaccJpaController movsController;
    
    private JFrame root;
    private Integer tra_codigo;//3-cuentas x cobrar, 4-cuentas por pagar
    
    public CuentasXCBPFrame(Integer tra_codigo) {
        initComponents();
        
        this.tra_codigo = tra_codigo;
        
        desdeTF = new JFormattedTextField(createFormatter("##/##/####"));
        hastaTF = new JFormattedTextField(createFormatter("##/##/####"));
        jPanel12.add( desdeTF );
        jPanel12.add( hastaTF );
        
        this.em = EntityManagerUtil.createEntintyManagerFactory();
        this.facturaController = new FacturasJpaController(em);
        this.movsController = new MovtransaccJpaController(em);        
        
        abonosDataModel = new AbonosDataModel();
        abonosDataModel.setController(movsController);
        jTable2.setModel(abonosDataModel);
        
        cPDataModel = new CuentaXCPDataModel();
        cPDataModel.setController(facturaController); 
        
        setupTransacc();
        
        jTable1.setModel(cPDataModel);
        try{
            //ventasDataModel.loadFromDataBase();
            Integer numItems = cPDataModel.getItems().size();
            jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("("+numItems+")"));
            updateLabelTotales();
        }
        catch(Throwable ex){
            JOptionPane.showMessageDialog(null, "Error al listar:"+ ex.getMessage());
            System.out.println("Error;"+ex.getMessage());
            ex.printStackTrace();
        }        
        
        jTable1.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTable1.columnAtPoint(e.getPoint());
                String name = jTable1.getColumnName(col);
                System.out.println("TableHeader column index selected " + col + " " + name);                
                try{
                    for (int i=0; i<cPDataModel.getColumnCount();i++){
                        jTable1.getColumnModel().getColumn(i).setHeaderValue( cPDataModel.getColumnName(i) );
                    }                    
                    cPDataModel.switchSortColumn(col);                    
                }
                catch(Throwable ex){
                    JOptionPane.showMessageDialog(null, "Error en sort:"+ex.getMessage());
                    System.out.println("Error en sort:"+ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        
        jTable1.updateUI();
        cPDataModel.fireTableDataChanged();        
        
        jTable1.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                
                if (mouseEvent.getClickCount() == 1) {
                    //Load Movimientos
                    try{                        
                        int rowCXCP = jTable1.getSelectedRow();
                        if (rowCXCP>-1){
                            FilaCXCP filaCXCP = cPDataModel.getValueAt(row);
                            abonosDataModel.setPgfId(filaCXCP.getCodPago());
                            jLabelHeadAbon.setText("LISTA DE ABONOS DE LA FACTURA:"+filaCXCP.getNumFactura());
                            abonosDataModel.loadFromDataBase(); 
                        }
                    }
                    catch(Throwable ex){
                        System.out.println("Error al listar abonos:"+ex.getMessage());
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error al listar abonos:"+ex.getMessage());
                    }
                    abonosDataModel.fireTableDataChanged();
                }
                
                if (mouseEvent.getClickCount() == 2) {
                    //showDetallesFrame();
                    System.out.println("dbl clic event-->");
                }
            }
        });
        
        this.desdeTF.setText( FechasUtil.getFechaActual() );
        this.hastaTF.setText( FechasUtil.getFechaActual() ); 
        
        jTable2.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTable2.columnAtPoint(e.getPoint());
                String name = jTable2.getColumnName(col);
                System.out.println("TableHeader column index selected " + col + " " + name);
                try{
                    for (int i=0; i<abonosDataModel.getColumnCount();i++){
                        jTable2.getColumnModel().getColumn(i).setHeaderValue( abonosDataModel.getColumnName(i) );
                    }                    
                    abonosDataModel.switchSortColumn(col);                    
                }
                catch(Throwable ex){
                    JOptionPane.showMessageDialog(null, "Error en sort:"+ex.getMessage());
                    System.out.println("Error en sort:"+ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        
        jTable2.updateUI();
        abonosDataModel.fireTableDataChanged();        
    }
    
    public void setupTransacc(){
        if(this.tra_codigo == 3){
            this.jLabelTitulo.setText("CUENTAS X COBRAR");
        }
        else if (this.tra_codigo == 4){
            this.jLabelTitulo.setText("CUENTAS X PAGAR");
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
    
    
    public void updateLabelTotales(){
        
        /*
        TotalesVentasModel totalesVentasModel =  this.ventasDataModel.getTotalesVentasModel();
        if (totalesVentasModel != null){
            this.jLabelSumaDesc.setText("SUMA DESC="+totalesVentasModel.getSumaDesc().setScale(2, RoundingMode.HALF_UP).toPlainString());
            this.jLabelSumaIva.setText("SUMA IVA="+totalesVentasModel.getSumaIva().setScale(2, RoundingMode.HALF_UP).toPlainString());
            this.jLabelSumaTot.setText("SUMA TOTAL="+totalesVentasModel.getSumaTotal().setScale(2, RoundingMode.HALF_UP).toPlainString());
            this.jLabelSumaUtilidades.setText("SUMA UTILIDADES="+totalesVentasModel.getUtilidades().setScale(2, RoundingMode.HALF_UP).toPlainString());
        }
        */
    }
    
    public void showDetallesFrame(){
        System.out.println("show detalles--->");
        
        int row = this.jTable1.getSelectedRow();
        if (row>-1){
            FilaCXCP filart = this.cPDataModel.getValueAt(row);            
            DetallesFacturaFrame detallesFacturaFrame = new DetallesFacturaFrame(filart.getCodFactura());
            
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            detallesFacturaFrame.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
            
            detallesFacturaFrame.setSize(900, 800);

            detallesFacturaFrame.setVisible(true);
        }    
        else{
            JOptionPane.showMessageDialog(this, "Debe seleccionar la factura");
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

        jPanel1 = new javax.swing.JPanel();
        jLabelTitulo = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        btnBuscar = new javax.swing.JButton();
        jCmbEstado = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabelHeadAbon = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButtonVerFact = new javax.swing.JButton();
        jButtonAbonar = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabelTitulo.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabelTitulo.setText("CUENTAS X COBRAR");

        jPanel12.setLayout(new java.awt.GridLayout(2, 2));

        jLabel3.setText("Desde:");
        jPanel12.add(jLabel3);

        jLabel4.setText("Hasta:");
        jPanel12.add(jLabel4);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hoy", "Ayer", "Esta Semana", "Este Mes", "Mes Anterior", "Este Año" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        jCmbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pendiente de Pago", "Cancelado", "Todos" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTitulo)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(jCmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(146, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jTable1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable1.setRowHeight(20);
        jScrollPane1.setViewportView(jTable1);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel6.setLayout(new java.awt.GridLayout(1, 1));

        jLabelHeadAbon.setText("LISTA DE ABONOS:");
        jPanel6.add(jLabelHeadAbon);

        jPanel5.add(jPanel6, java.awt.BorderLayout.NORTH);

        jTable2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable2.setRowHeight(20);
        jScrollPane2.setViewportView(jTable2);

        jPanel5.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setBottomComponent(jPanel5);

        jPanel2.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jLabel1.setText("totales");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 854, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

        jPanel4.setLayout(new java.awt.GridLayout(5, 1));

        jButtonVerFact.setText("Ver Factura");
        jButtonVerFact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVerFactActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonVerFact);

        jButtonAbonar.setText("Abonar");
        jButtonAbonar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbonarActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonAbonar);

        jButtonClose.setText("Cerrar");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonClose);

        getContentPane().add(jPanel4, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed

        Integer fechaIndex = this.jComboBox1.getSelectedIndex();
        switch (fechaIndex){
            case 0: //hoy
            {
                this.desdeTF.setText(FechasUtil.getFechaActual());
                this.hastaTF.setText(FechasUtil.getFechaActual());
                break;
            }
            case 1:{//ayer
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)-1 );
                this.desdeTF.setText( FechasUtil.format(cal.getTime()) );
                this.hastaTF.setText( FechasUtil.format(cal.getTime()) );
                break;
            }
            case 2:{//Esta semana
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                this.desdeTF.setText( FechasUtil.format(cal.getTime()));
                cal.add(Calendar.DAY_OF_WEEK, 6);
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                break;
            }
            case 3:{//Este mes
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, 1);
                this.desdeTF.setText( FechasUtil.format(cal.getTime()));

                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                this.hastaTF.setText( FechasUtil.format(cal.getTime()));
                break;
            }
            case 4:{//MES ANTERIOR
                Calendar cal = Calendar.getInstance();

                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                this.desdeTF.setText( FechasUtil.format(cal.getTime()));

                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                this.hastaTF.setText( FechasUtil.format(cal.getTime()));
                break;
            }
            case 5:{//ESTE AÑO
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.MONTH, cal.getActualMinimum(Calendar.MONTH));
                cal.set(Calendar.DAY_OF_MONTH, 1);
                this.desdeTF.setText( FechasUtil.format(cal.getTime()));

                cal.set(Calendar.MONTH, cal.getActualMaximum(Calendar.MONTH));
                cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
                this.hastaTF.setText( FechasUtil.format(cal.getTime()));
                break;
            }
        }

        logicaBuscar();

    }//GEN-LAST:event_jComboBox1ActionPerformed

    
    public void restoreDefaultsDividerLocation() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("restoreDefaultsDividerLocation --->");
                jSplitPane1.setDividerLocation( 0.6 );
            }
        });
    }
    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        if (cPDataModel != null){
            logicaBuscar();
        }
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        
        FarmaAppMain farmaApp = (FarmaAppMain)this.root;        
        farmaApp.logicaClosePane(this.getClass().getName()+this.tra_codigo);
        
        
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonAbonarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbonarActionPerformed
        
        int row = this.jTable1.getSelectedRow();
        if (row>-1){
            FilaCXCP fila = (FilaCXCP)this.cPDataModel.getValueAt(row);           
            abonosFrame = new AbonosFrame(fila, this.tra_codigo);
            abonosFrame.setParentFrame(this);
            
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            abonosFrame.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
            
            abonosFrame.setVisible(true);
        }
        
        
        
    }//GEN-LAST:event_jButtonAbonarActionPerformed

    private void jButtonVerFactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVerFactActionPerformed
        
        showDetallesFrame();
        
    }//GEN-LAST:event_jButtonVerFactActionPerformed
    
     
     
    public void logicaBuscar(){
        ParamBusquedaCXCP paramsBusqueda = new ParamBusquedaCXCP();
        
        paramsBusqueda.setEstadoPago(2);
        paramsBusqueda.setCliId(0);
        
        Integer tra_codigo = 1;
        if (this.tra_codigo == 4){
            tra_codigo = 2;
        }
        paramsBusqueda.setTra_codigo(tra_codigo);
        
        try{
            paramsBusqueda.setDesde( FechasUtil.parse(this.desdeTF.getText()) );
            paramsBusqueda.setHasta( FechasUtil.parse(this.hastaTF.getText()) );
            
            int statePagoIndex = this.jCmbEstado.getSelectedIndex();
            int statePago = 0;
            if (statePagoIndex == 0){
                statePago = 2;
            }
            else if (statePagoIndex == 1){
                statePago = 1;
            }
            paramsBusqueda.setEstadoPago(statePago);
        }
        catch(Throwable ex){
            System.out.println("Error al parsear:"+ex.getMessage());
            ex.printStackTrace();
        }
        
        try{
            cPDataModel.setParams(paramsBusqueda);
            cPDataModel.loadFromDataBase();            
                        
            Integer numItems = cPDataModel.getItems().size();
            jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("("+numItems+")"));            
            updateLabelTotales();            
        }
        catch(Throwable ex){
            System.out.println("Error al leer de base de datos:"+ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al leer de base de datos:"+ex.getMessage());
        }
        
    }

    public JFrame getRoot() {
        return root;
    }

    public void setRoot(JFrame root) {
        this.root = root;
    }
    
    
    private AbonosFrame abonosFrame;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton jButtonAbonar;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonVerFact;
    private javax.swing.JComboBox<String> jCmbEstado;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelHeadAbon;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
