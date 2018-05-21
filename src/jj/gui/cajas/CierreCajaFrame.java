/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui.cajas;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.Date;
import javax.swing.JOptionPane;
import jj.controller.CajasJpaController;
import jj.controller.FacturasJpaController;
import jj.entity.Cajas;
import jj.gui.BaseFrame;
import jj.gui.DetallesFacturaFrame;
import jj.util.FechasUtil;
import jj.util.FilaCXCP;
import jj.util.FilaVenta;
import jj.util.NumbersUtil;
import jj.util.ParamBusquedaCXCP;
import jj.util.ParamsBusquedaTransacc;
import jj.util.TotalesCuentasXPC;
import jj.util.datamodels.MovsCVDataModel;
import jj.util.datamodels.MovsCXCPDataModel;
import jj.util.datamodels.TotalesVentasModel;

/**
 *
 * @author mjapon
 */
public class CierreCajaFrame extends BaseFrame {
    
    private Cajas caja;
    private Date dia;
    private CajasJpaController cajasController;
    
    private MovsCVDataModel movsVentasDataModel;    
    private MovsCVDataModel movsComprasDataModel;    
    private MovsCXCPDataModel movsCXCDataModel;
    private MovsCXCPDataModel movsCXPDataModel;
    private FacturasJpaController facturaController;
    
    private BigDecimal totalVentas;
    private BigDecimal abonosCobrados;
    private BigDecimal abonosPagados;
    private BigDecimal saldoInicial;
    private BigDecimal saldoCaja;
    
    /**
     * Creates new form CierreCajaFrame
     */
    public CierreCajaFrame() {
        super();
        initComponents();
        
        cajasController = new CajasJpaController(em);
        facturaController = new FacturasJpaController(em);
        
        
        movsVentasDataModel = new MovsCVDataModel();
        movsVentasDataModel.setController(facturaController);
        jTableVentas.setModel(movsVentasDataModel);        
        jTableVentas.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTableVentas.columnAtPoint(e.getPoint());
                try{
                    for (int i=0; i<movsVentasDataModel.getColumnCount();i++){
                        jTableVentas.getColumnModel().getColumn(i).setHeaderValue( movsVentasDataModel.getColumnName(i) );
                    }                    
                    movsVentasDataModel.switchSortColumn(col);                    
                }
                catch(Throwable ex){
                    JOptionPane.showMessageDialog(null, "Error en sort:"+ex.getMessage());
                    System.out.println("Error en sort:"+ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        jTableVentas.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    showDetallesFacturaFrame(1);
                }
            }
        });
        jTableVentas.updateUI();
        
         //Configuracion de movimientos de compra
        
        //Configuracion de cuentas x cobrar
        movsCXCDataModel = new MovsCXCPDataModel(3);
        movsCXCDataModel.setController(facturaController);
        jTableCxC.setModel(movsCXCDataModel);
        jTableCxC.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTableCxC.columnAtPoint(e.getPoint());
                try{
                    for (int i=0; i<movsCXCDataModel.getColumnCount();i++){
                        jTableCxC.getColumnModel().getColumn(i).setHeaderValue( movsCXCDataModel.getColumnName(i) );
                    }
                    movsCXCDataModel.switchSortColumn(col);
                }
                catch(Throwable ex){
                    JOptionPane.showMessageDialog(null, "Error en sort:"+ex.getMessage());
                    System.out.println("Error en sort:"+ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        jTableCxC.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    showDetallesFacturaFrame(3);
                }
            }
        });
        jTableCxC.updateUI();        
        
        //Configuracion de cuentas x pagar
        movsCXPDataModel = new MovsCXCPDataModel(4);
        movsCXPDataModel.setController(facturaController);
        jTableCxP.setModel(movsCXPDataModel);
        jTableCxP.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTableCxP.columnAtPoint(e.getPoint());
                try{
                    for (int i=0; i<movsCXPDataModel.getColumnCount();i++){
                        jTableCxP.getColumnModel().getColumn(i).setHeaderValue( movsCXPDataModel.getColumnName(i) );
                    }                    
                    movsCXPDataModel.switchSortColumn(col);
                }
                catch(Throwable ex){
                    JOptionPane.showMessageDialog(null, "Error en sort:"+ex.getMessage());
                    System.out.println("Error en sort:"+ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        jTableCxP.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    showDetallesFacturaFrame(4);
                }
            }
        });
        jTableCxP.updateUI();
        
        saldoInicial = BigDecimal.ZERO;
        totalVentas = BigDecimal.ZERO;
        abonosCobrados = BigDecimal.ZERO;
        abonosPagados = BigDecimal.ZERO;
        saldoCaja = BigDecimal.ZERO;
    }
    
    public void loadInfoCaja(){
        try{
            dia = new Date();
            if (!cajasController.existeCajaAbierta(dia)){
                showMsg("No existe apertura de caja para hoy:"+ FechasUtil.getDayNameOfDate(dia));
                this.setVisible(false);
                return;
            }
            
            caja = cajasController.getCajaDia(dia);
            saldoInicial = caja.getCjSaldoant()!=null?caja.getCjSaldoant():BigDecimal.ZERO;
            
            this.jTFFechaApertura.setText( FechasUtil.formatDateHour(caja.getCjFecaper()) );
            this.jTFSaldoInicial.setText( NumbersUtil.round2(saldoInicial).toPlainString() );
            this.jTAObsApertura.setText( caja.getCjObsaper() );
            
            Date desde = caja.getCjFecaper();
            Date hasta = new Date();
            
            //Ventas
            ParamsBusquedaTransacc paramsVentas = new ParamsBusquedaTransacc();
            paramsVentas.setDesde(desde);
            paramsVentas.setHasta(hasta);
            paramsVentas.setTraCodigo(1);            
            paramsVentas.setArtId(0);
            paramsVentas.setCliId(0);
            
            movsVentasDataModel.setParams(paramsVentas);
            movsVentasDataModel.loadFromDataBase();
            jTableVentas.updateUI();
            movsVentasDataModel.fireTableDataChanged();      
            jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ventas("+movsVentasDataModel.getItems().size() +")"  , javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N
                        
            //Compras
            ParamsBusquedaTransacc paramsCompras = new ParamsBusquedaTransacc();
            paramsCompras.setDesde(desde);
            paramsCompras.setHasta(hasta);
            paramsCompras.setTraCodigo(2);
            paramsCompras.setArtId(0);
            paramsCompras.setCliId(0);
            
            /*
            movsComprasDataModel.setParams(paramsCompras);
            movsComprasDataModel.loadFromDataBase();
            jTableCompras.updateUI();
            movsComprasDataModel.fireTableDataChanged();            
            jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Compras("+movsComprasDataModel.getItems().size() +")" , javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N
            */
            
            //Cuentas x cobrar
            ParamBusquedaCXCP paramsCXC = new ParamBusquedaCXCP();
            paramsCXC.setDesde(desde);
            paramsCXC.setHasta(hasta);
            paramsCXC.setTra_codigo(3);
            paramsCXC.setArtId(0);
            paramsCXC.setCliId(0);
            movsCXCDataModel.setParams(paramsCXC);
            movsCXCDataModel.loadFromDataBase();
            jTableCxC.updateUI();
            movsCXCDataModel.fireTableDataChanged();
            jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Abonos Cobrados("+movsCXCDataModel.getItems().size() +")" , javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N
            
            
            //Cuentas x pagar
            ParamBusquedaCXCP paramsCXP = new ParamBusquedaCXCP();
            paramsCXP.setDesde(desde);
            paramsCXP.setHasta(hasta);
            paramsCXP.setTra_codigo(4);
            paramsCXP.setArtId(0);
            paramsCXP.setCliId(0);
            movsCXPDataModel.setParams(paramsCXP);
            movsCXPDataModel.loadFromDataBase();
            jTableCxP.updateUI();
            movsCXPDataModel.fireTableDataChanged();
            jPanelCxP.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Abonos Pagados("+movsCXPDataModel.getItems().size() +")" , javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N
            
            //updateLabelTotales();         
            
            //Sumatora de ventas
            TotalesVentasModel totalesVentas =  movsVentasDataModel.getTotalesVentasModel();
            TotalesCuentasXPC totalesCXP = movsCXPDataModel.getTotalesFactura();
            TotalesCuentasXPC totalesCXC = movsCXCDataModel.getTotalesFactura();
            
            totalVentas = totalesVentas.getSumaEfectivo();
            abonosCobrados = totalesCXC.getSumaMonto();
            abonosPagados = totalesCXP.getSumaMonto();
            
            saldoCaja =  saldoInicial.add(totalVentas).add(abonosCobrados).subtract(abonosPagados);
            
            jTFVentas.setText( NumbersUtil.round2(totalVentas).toPlainString() );
            jTFCuentasXCobrar.setText( NumbersUtil.round2(abonosCobrados).toPlainString() );
            jTFCuentasXPagar.setText( NumbersUtil.round2(abonosPagados).toPlainString() );
            jTFSaldo.setText(NumbersUtil.round2(saldoCaja).toPlainString());
            
            jTFTotalVGrid.setText(NumbersUtil.round2(totalVentas).toPlainString());
            jTFTotalACGrid.setText(NumbersUtil.round2(abonosCobrados).toPlainString());
            jTFTotalAPGrid.setText(NumbersUtil.round2(abonosPagados).toPlainString());
        
        }
        catch(Throwable ex){
            showMsgError(ex);
        }
    }
    
    public void showDetallesFacturaFrame(Integer traCodigo){
        System.out.println("Select action");
        int row = this.jTableVentas.getSelectedRow();
        if (traCodigo == 2){
            //row = this.jTableCompras.getSelectedRow();
        }
        else if (traCodigo == 3){
            row = this.jTableCxC.getSelectedRow();
        }
        else if (traCodigo == 4){
            row = this.jTableCxP.getSelectedRow();
        }
        
        if (row>-1){            
            if (traCodigo == 1 || traCodigo ==2){                
                FilaVenta filart = null;                
                if (traCodigo == 1){
                    filart = this.movsVentasDataModel.getValueAt(row);
                }
                else if (traCodigo == 2){
                    filart = this.movsComprasDataModel.getValueAt(row);
                }                
                DetallesFacturaFrame detallesFacturaFrame = new DetallesFacturaFrame(filart.getVentaId());            
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                detallesFacturaFrame.setLocation((dim.width/2)-(this.getSize().width/2), (dim.height/2)-(this.getSize().height/2));            
                detallesFacturaFrame.setSize(900, 500);
                detallesFacturaFrame.setVisible(true);
            }
            else if (traCodigo == 3 || traCodigo ==4){                
                FilaCXCP filaCXCP = null;
                if (traCodigo == 3){
                    filaCXCP = this.movsCXCDataModel.getValueAt(row);
                }
                else if (traCodigo == 4){
                    filaCXCP = this.movsCXPDataModel.getValueAt(row);
                }
                
                DetallesFacturaFrame detallesFacturaFrame = new DetallesFacturaFrame(filaCXCP.getCodFactura());
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                detallesFacturaFrame.setLocation((dim.width/2)-(this.getSize().width/2), (dim.height/2)-(this.getSize().height/2));            
                detallesFacturaFrame.setSize(900, 500);
                detallesFacturaFrame.setVisible(true);
            }
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
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTFFechaApertura = new javax.swing.JTextField();
        jTFSaldoInicial = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTAObsCierre = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTFCuentasXCobrar = new javax.swing.JTextField();
        jTFVentas = new javax.swing.JTextField();
        jTFCuentasXPagar = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTFSaldo = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTAObsApertura = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableVentas = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jTFTotalVGrid = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableCxC = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jTFTotalACGrid = new javax.swing.JTextField();
        jPanelCxP = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCxP = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jTFTotalAPGrid = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jButtonUpdVal = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1200, 700));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setText("Cierre de Caja");
        jPanel1.add(jLabel1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.GridLayout(1, 2));

        jLabel2.setText("Fecha de Apertura:");

        jLabel3.setText("Saldo Inicial (Anterior):");

        jLabel4.setText("Obs Apertura:");

        jTFFechaApertura.setEditable(false);

        jTFSaldoInicial.setEditable(false);

        jTAObsCierre.setColumns(20);
        jTAObsCierre.setRows(5);
        jScrollPane4.setViewportView(jTAObsCierre);

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel5.setText("Resumen del día:");

        jLabel6.setText("Total Ventas Válidas en Efectivo:");

        jLabel7.setText("Total Abonos Cobrados:");

        jLabel8.setText("Total Abonos Pagados:");

        jTFCuentasXCobrar.setEditable(false);

        jTFVentas.setEditable(false);

        jTFCuentasXPagar.setEditable(false);

        jLabel9.setText("Saldo en CAJA:");

        jTFSaldo.setEditable(false);
        jTFSaldo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTFSaldo.setForeground(new java.awt.Color(0, 153, 51));

        jLabel10.setText("Observaciones para el cierre:");

        jTAObsApertura.setEditable(false);
        jTAObsApertura.setColumns(20);
        jTAObsApertura.setRows(5);
        jScrollPane5.setViewportView(jTAObsApertura);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(42, 42, 42)
                        .addComponent(jTFFechaApertura, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFSaldoInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel5)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTFVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTFCuentasXCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel10)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel9)
                            .addGap(18, 18, 18)
                            .addComponent(jTFSaldo))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addGap(18, 18, 18)
                            .addComponent(jTFCuentasXPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFFechaApertura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTFSaldoInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(22, 22, 22)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTFVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTFCuentasXCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jTFCuentasXPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jTFSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel4);

        jPanel5.setLayout(new java.awt.GridLayout(3, 1));

        jPanel7.setPreferredSize(new java.awt.Dimension(454, 500));
        jPanel7.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(454, 200));

        jTableVentas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTableVentas);

        jPanel7.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jLabel11.setText("TOTAL:");
        jPanel8.add(jLabel11);

        jTFTotalVGrid.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTFTotalVGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFTotalVGridActionPerformed(evt);
            }
        });
        jPanel8.add(jTFTotalVGrid);

        jPanel7.add(jPanel8, java.awt.BorderLayout.SOUTH);

        jPanel5.add(jPanel7);

        jPanel6.setPreferredSize(new java.awt.Dimension(454, 200));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setPreferredSize(new java.awt.Dimension(454, 200));

        jTableCxC.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTableCxC);

        jPanel6.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jLabel12.setText("TOTAL:");
        jPanel9.add(jLabel12);

        jTFTotalACGrid.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTFTotalACGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFTotalACGridActionPerformed(evt);
            }
        });
        jPanel9.add(jTFTotalACGrid);

        jPanel6.add(jPanel9, java.awt.BorderLayout.SOUTH);

        jPanel5.add(jPanel6);

        jPanelCxP.setPreferredSize(new java.awt.Dimension(454, 500));
        jPanelCxP.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setPreferredSize(new java.awt.Dimension(454, 200));

        jTableCxP.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(jTableCxP);

        jPanelCxP.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jLabel13.setText("TOTAL:");
        jPanel10.add(jLabel13);

        jTFTotalAPGrid.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTFTotalAPGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFTotalAPGridActionPerformed(evt);
            }
        });
        jPanel10.add(jTFTotalAPGrid);

        jPanelCxP.add(jPanel10, java.awt.BorderLayout.SOUTH);

        jPanel5.add(jPanelCxP);

        jPanel2.add(jPanel5);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.GridLayout(8, 1));

        jButtonUpdVal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-connection_sync.png"))); // NOI18N
        jButtonUpdVal.setText("Actualizar Valores");
        jButtonUpdVal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdValActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonUpdVal);

        jButton1.setText("Cerrar Caja");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-close_pane_filled.png"))); // NOI18N
        jButton2.setText("Cancelar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2);

        getContentPane().add(jPanel3, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
        loadInfoCaja();
        
    }//GEN-LAST:event_formWindowOpened

    private void jButtonUpdValActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdValActionPerformed
        
        loadInfoCaja();        
        showMsg("Valores actualizados");
        
    }//GEN-LAST:event_jButtonUpdValActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        try{
            int response = JOptionPane.showConfirmDialog(null, "¿Seguro que desea registrar el cierre de caja, ya no podra registrar ventas? ");
            if (response == JOptionPane.YES_OPTION){

                Integer cjId = caja.getCjId();
                BigDecimal ventasAnuladas = BigDecimal.ZERO;
                String obsCierre = jTAObsCierre.getText();

                cajasController.cerrarCaja(cjId, totalVentas, abonosCobrados, abonosPagados, ventasAnuladas, saldoCaja, obsCierre);
                showMsg(" La caja ha sido cerrada satisfactoriamente ");
                
                setVisible(false);
            }
        }
        catch(Throwable ex){
            showMsgError(ex);
        }
        
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTFTotalVGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFTotalVGridActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFTotalVGridActionPerformed

    private void jTFTotalACGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFTotalACGridActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFTotalACGridActionPerformed

    private void jTFTotalAPGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFTotalAPGridActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFTotalAPGridActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonUpdVal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelCxP;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextArea jTAObsApertura;
    private javax.swing.JTextArea jTAObsCierre;
    private javax.swing.JTextField jTFCuentasXCobrar;
    private javax.swing.JTextField jTFCuentasXPagar;
    private javax.swing.JTextField jTFFechaApertura;
    private javax.swing.JTextField jTFSaldo;
    private javax.swing.JTextField jTFSaldoInicial;
    private javax.swing.JTextField jTFTotalACGrid;
    private javax.swing.JTextField jTFTotalAPGrid;
    private javax.swing.JTextField jTFTotalVGrid;
    private javax.swing.JTextField jTFVentas;
    private javax.swing.JTable jTableCxC;
    private javax.swing.JTable jTableCxP;
    private javax.swing.JTable jTableVentas;
    // End of variables declaration//GEN-END:variables
}
