/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui.facte;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.text.MaskFormatter;
import jj.controller.ArticulosJpaController;
import jj.controller.CtesJpaController;
import jj.controller.FacturasJpaController;
import jj.entity.Articulos;
import jj.gui.BusquedaArtsFrame;
import jj.gui.FarmaAppMain;
import jj.util.EntityManagerUtil;
import jj.util.FechasUtil;
import jj.util.datamodels.rows.FilaVenta;
import jj.util.ISearchArt;
import jj.util.NumbersUtil;
import jj.util.ParamsBusquedaTransacc;
import jj.util.datamodels.TotalesVentasModel;
import jj.util.datamodels.VentasDataModel;

/**
 *
 * @author mjapon
 */
public class AdminVentasFrame extends javax.swing.JFrame implements ISearchArt{
    
     private EntityManager em;
     private FacturasJpaController facturasJpaController;
     private ArticulosJpaController articulosJpaController;
     private VentasDataModel ventasDataModel;
     private JFrame root;
     
     private List<Articulos> articulosList;
     private JFormattedTextField desdeTF;
     private JFormattedTextField hastaTF;
     private Articulos articuloSel;
     private Integer tra_codigo;
     
     private Integer codCatChanca;
     private Integer codCatSP;
     private Integer codCatOtros = -1;
     private CtesJpaController ctesCntrl;
     private Map<Integer, BigDecimal> mapUtilidades;
    /**
     * Creates new form AdminVentasFrame
     */
    public AdminVentasFrame(Integer tra_codigo) {
        initComponents();
        
        this.tra_codigo = tra_codigo;
        if (this.tra_codigo == 1){
            this.jLabel1.setText("ADMINISTRAR VENTAS");
            this.jLabelSumaUtilidades.setVisible(true);
        }
        else if(this.tra_codigo == 2){
            this.jLabel1.setText("ADMINISTRAR COMPRAS");
            this.jLabelSumaUtilidades.setVisible(false);
        }
        
        desdeTF = new JFormattedTextField(createFormatter("##/##/####"));
        hastaTF = new JFormattedTextField(createFormatter("##/##/####"));
        jPanel12.add( desdeTF );
        jPanel12.add( hastaTF );
         
        this.em = EntityManagerUtil.createEntintyManagerFactory();
        
        ventasDataModel = new VentasDataModel();
        facturasJpaController = new FacturasJpaController(em);
        articulosJpaController = new ArticulosJpaController(em);
        ctesCntrl = new CtesJpaController(em);
        ventasDataModel.setController(facturasJpaController);
        
        jTable2.setModel(ventasDataModel);
        try{
            //ventasDataModel.loadFromDataBase();
            Integer numItems = ventasDataModel.getItems().size();
            jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("("+numItems+")"));
            updateLabelTotales();
        }
        catch(Throwable ex){
            JOptionPane.showMessageDialog(null, "Error al listar:"+ ex.getMessage());
            System.out.println("Error;"+ex.getMessage());
            ex.printStackTrace();
        }        
        
        jTable2.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTable2.columnAtPoint(e.getPoint());
                String name = jTable2.getColumnName(col);
                System.out.println("TableHeader column index selected " + col + " " + name);                
                try{
                    for (int i=0; i<ventasDataModel.getColumnCount();i++){
                        jTable2.getColumnModel().getColumn(i).setHeaderValue( ventasDataModel.getColumnName(i) );
                    }                    
                    ventasDataModel.switchSortColumn(col);                    
                }
                catch(Throwable ex){
                    JOptionPane.showMessageDialog(null, "Error en sort:"+ex.getMessage());
                    System.out.println("Error en sort:"+ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        
        jTable2.updateUI();
        ventasDataModel.fireTableDataChanged();        
        
        jTable2.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2) {
                    showDetallesFrame();
                    System.out.println("dbl clic event-->");
                }
            }
        });
        
        jTable2.getSelectionModel().addListSelectionListener((e) -> {
            if (!e.getValueIsAdjusting()) {
                checkStatusBtn();
            }
        });
        
        this.desdeTF.setText( FechasUtil.getFechaActual() );
        this.hastaTF.setText( FechasUtil.getFechaActual() );

        codCatChanca = Integer.valueOf(ctesCntrl.findValueByClave("COD_CAT_CHANCADO"));
        codCatSP = Integer.valueOf(ctesCntrl.findValueByClave("COD_CAT_SP"));
        
    }
    
    public void checkStatusBtn(){
        boolean isEnabled =this.jTable2.getSelectedRows().length>0;
        btnAnular.setEnabled(isEnabled);
        btnDetalles.setEnabled(isEnabled);
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
    
    public Date buildAndGetDate(){
        Date fechaActual = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaActual);
        return null;
    }
    
    public void updateLabelTotales(){
        TotalesVentasModel totalesVentasModel =  this.ventasDataModel.getTotalesVentasModel();
        if (totalesVentasModel != null){
            jTFSumaDesc.setText( NumbersUtil.round2ToStr(totalesVentasModel.getSumaDesc()) );
            jTFSumaIva.setText( NumbersUtil.round2ToStr(totalesVentasModel.getSumaIva()) );
            jTFSumaTotal.setText( NumbersUtil.round2ToStr(totalesVentasModel.getSumaTotal()) );
            jTFSumaEfectivo.setText( NumbersUtil.round2ToStr( totalesVentasModel.getSumaEfectivo() ) );
            jTFSumaCredito.setText(NumbersUtil.round2ToStr(totalesVentasModel.getSumaCredito()) );
            jTFSumaSaldoPend.setText(NumbersUtil.round2ToStr(totalesVentasModel.getSumaSaldo()));
            jTFSumaUtilidades.setText(NumbersUtil.round2ToStr(totalesVentasModel.getUtilidades()));
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
        jPanel4 = new javax.swing.JPanel();
        articuloSelBtn = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        articuloSelLabel = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jBtnQuitarArt = new javax.swing.JButton();
        jTextFieldFiltro = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabelSumaIva = new javax.swing.JLabel();
        jTFSumaIva = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabelSumaDesc = new javax.swing.JLabel();
        jTFSumaDesc = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        jLabelSumaTot = new javax.swing.JLabel();
        jTFSumaTotal = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabelSumaEfectivo = new javax.swing.JLabel();
        jTFSumaEfectivo = new javax.swing.JTextField();
        jPanel15 = new javax.swing.JPanel();
        jLabelSumaCredito = new javax.swing.JLabel();
        jTFSumaCredito = new javax.swing.JTextField();
        jPanel16 = new javax.swing.JPanel();
        jLabelSumaSaldo = new javax.swing.JLabel();
        jTFSumaSaldoPend = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        jLabelSumaUtilidades = new javax.swing.JLabel();
        jTFSumaUtilidades = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        btnAnular = new javax.swing.JButton();
        btnDetalles = new javax.swing.JButton();
        btnUtilidades = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setText("ADMINISTRAR VENTAS");
        jPanel1.add(jLabel1, java.awt.BorderLayout.NORTH);

        jPanel4.setMinimumSize(new java.awt.Dimension(0, 200));
        jPanel4.setPreferredSize(new java.awt.Dimension(200, 100));

        articuloSelBtn.setText("Articulo...");
        articuloSelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                articuloSelBtnActionPerformed(evt);
            }
        });

        btnBuscar.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-search.png"))); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        articuloSelLabel.setText(".");

        jPanel12.setLayout(new java.awt.GridLayout(2, 2));

        jLabel2.setText("Desde:");
        jPanel12.add(jLabel2);

        jLabel3.setText("Hasta:");
        jPanel12.add(jLabel3);

        jComboBox1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hoy", "Ayer", "Esta Semana", "Este Mes", "Mes Anterior", "Este Año" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jBtnQuitarArt.setText("Quitar art");
        jBtnQuitarArt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnQuitarArtActionPerformed(evt);
            }
        });

        jTextFieldFiltro.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldFiltroFocusGained(evt);
            }
        });
        jTextFieldFiltro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTextFieldFiltroMouseReleased(evt);
            }
        });
        jTextFieldFiltro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldFiltroKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(114, 114, 114)
                        .addComponent(articuloSelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jBtnQuitarArt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(articuloSelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(195, 195, 195))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(17, 17, 17)
                        .addComponent(articuloSelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(articuloSelBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBtnQuitarArt)))))
                .addContainerGap(88, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("(Count)"));
        jScrollPane1.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N

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
        jTable2.setRowHeight(25);
        jScrollPane1.setViewportView(jTable2);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel7.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jPanel7.setLayout(new java.awt.GridLayout(1, 7));

        jPanel8.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jPanel8.setLayout(new java.awt.GridLayout(1, 2));

        jLabelSumaIva.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelSumaIva.setText("IVA:");
        jPanel8.add(jLabelSumaIva);

        jTFSumaIva.setEditable(false);
        jTFSumaIva.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jPanel8.add(jTFSumaIva);

        jPanel7.add(jPanel8);

        jPanel9.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jPanel9.setLayout(new java.awt.GridLayout(1, 2));

        jLabelSumaDesc.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelSumaDesc.setText("DESCUENTO:");
        jPanel9.add(jLabelSumaDesc);

        jTFSumaDesc.setEditable(false);
        jTFSumaDesc.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jPanel9.add(jTFSumaDesc);

        jPanel7.add(jPanel9);

        jPanel10.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jPanel10.setLayout(new java.awt.GridLayout(1, 2));

        jLabelSumaTot.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelSumaTot.setText("TOTAL:");
        jPanel10.add(jLabelSumaTot);

        jTFSumaTotal.setEditable(false);
        jTFSumaTotal.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jPanel10.add(jTFSumaTotal);

        jPanel7.add(jPanel10);

        jPanel14.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jPanel14.setLayout(new java.awt.GridLayout(1, 2));

        jLabelSumaEfectivo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelSumaEfectivo.setText("EFECTIVO:");
        jPanel14.add(jLabelSumaEfectivo);

        jTFSumaEfectivo.setEditable(false);
        jTFSumaEfectivo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jPanel14.add(jTFSumaEfectivo);

        jPanel7.add(jPanel14);

        jPanel15.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jPanel15.setLayout(new java.awt.GridLayout(1, 2));

        jLabelSumaCredito.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelSumaCredito.setText("CRÉDITO:");
        jPanel15.add(jLabelSumaCredito);

        jTFSumaCredito.setEditable(false);
        jTFSumaCredito.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jPanel15.add(jTFSumaCredito);

        jPanel7.add(jPanel15);

        jPanel16.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jPanel16.setLayout(new java.awt.GridLayout(1, 2));

        jLabelSumaSaldo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelSumaSaldo.setText("SALDO PEND:");
        jPanel16.add(jLabelSumaSaldo);

        jTFSumaSaldoPend.setEditable(false);
        jTFSumaSaldoPend.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jPanel16.add(jTFSumaSaldoPend);

        jPanel7.add(jPanel16);

        jPanel13.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jPanel13.setLayout(new java.awt.GridLayout(1, 2));

        jLabelSumaUtilidades.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelSumaUtilidades.setText("UTILIDADES:");
        jPanel13.add(jLabelSumaUtilidades);

        jTFSumaUtilidades.setEditable(false);
        jTFSumaUtilidades.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jPanel13.add(jTFSumaUtilidades);

        jPanel7.add(jPanel13);

        jPanel5.add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel5, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel11.setLayout(new java.awt.GridLayout(7, 1));

        btnAnular.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnAnular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-cancel_subscription.png"))); // NOI18N
        btnAnular.setText("Anular");
        btnAnular.setEnabled(false);
        btnAnular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnularActionPerformed(evt);
            }
        });
        jPanel11.add(btnAnular);

        btnDetalles.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnDetalles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-view_details.png"))); // NOI18N
        btnDetalles.setText("Detalles");
        btnDetalles.setEnabled(false);
        btnDetalles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetallesActionPerformed(evt);
            }
        });
        jPanel11.add(btnDetalles);

        btnUtilidades.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnUtilidades.setText("Utilidades");
        btnUtilidades.setEnabled(false);
        btnUtilidades.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUtilidadesActionPerformed(evt);
            }
        });
        jPanel11.add(btnUtilidades);

        btnClose.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-close_pane_filled.png"))); // NOI18N
        btnClose.setText("Cerrar");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel11.add(btnClose);

        getContentPane().add(jPanel11, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        //this.setVisible(false);
        FarmaAppMain farmaApp = (FarmaAppMain)this.root;
        farmaApp.logicaClosePane(this.getClass().getName()+this.tra_codigo);
        
    }//GEN-LAST:event_btnCloseActionPerformed
    
    public void logicaBuscar(){
        
        try{
            Date desde = FechasUtil.parse(this.desdeTF.getText());
            Date hasta = FechasUtil.parse(this.hastaTF.getText());
            
            ParamsBusquedaTransacc params = new ParamsBusquedaTransacc();
            params.setDesde(desde);
            params.setHasta(hasta);
            params.setTraCodigo(tra_codigo);
            params.setFiltro(jTextFieldFiltro.getText());
            
            params.setArtId(0);
            if (this.articuloSel != null){
                params.setArtId( this.articuloSel.getArtId() );
            }            
            
            params.setCliId(0);
            
            ventasDataModel.setParams(params);
            ventasDataModel.loadFromDataBase();
            
            mapUtilidades = facturasJpaController.getUtilidadesByCat(codCatChanca, codCatSP, ventasDataModel.getUtilidadesList());
                        
            Integer numItems = ventasDataModel.getItems().size();
            jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("("+numItems+")"));
            
            updateLabelTotales();
            
            this.btnUtilidades.setEnabled(true);
            
        }
        catch(Throwable ex){
            System.out.println("Error al buscar:"+ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void btnAnularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnularActionPerformed
        
        System.out.println("Select action");
        int row = this.jTable2.getSelectedRow();
        if (row>-1){
            FilaVenta filart = this.ventasDataModel.getValueAt(row);
            if (JOptionPane.showConfirmDialog(this, "Seguro que desea anular esta factura?") == 0){
                facturasJpaController.anularFactura(filart.getVentaId());
                JOptionPane.showMessageDialog(null, "La factura ha sido anulada");
                
                if (ventasDataModel != null){
                    logicaBuscar();
                }
            }
        }    
        else{
            JOptionPane.showMessageDialog(this, "Debe seleccionar la factura");
        }
        
        
    }//GEN-LAST:event_btnAnularActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed

        if (ventasDataModel != null){
            logicaBuscar();
        }

    }//GEN-LAST:event_btnBuscarActionPerformed

    private void articuloSelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_articuloSelBtnActionPerformed
        
        BusquedaArtsFrame artsFrame = new BusquedaArtsFrame();
        artsFrame.setSearchArtFrame(this);
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        artsFrame.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        artsFrame.setVisible(true);
        
        
    }//GEN-LAST:event_articuloSelBtnActionPerformed

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

    private void jBtnQuitarArtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnQuitarArtActionPerformed
        
        this.articuloSel = null;
        this.articuloSelLabel.setText("");
    }//GEN-LAST:event_jBtnQuitarArtActionPerformed

    private void btnDetallesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetallesActionPerformed
        showDetallesFrame();
    }//GEN-LAST:event_btnDetallesActionPerformed

    private void jTextFieldFiltroMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldFiltroMouseReleased
        
    }//GEN-LAST:event_jTextFieldFiltroMouseReleased

    private void jTextFieldFiltroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldFiltroKeyReleased
        System.out.println("key released");
    }//GEN-LAST:event_jTextFieldFiltroKeyReleased

    private void jTextFieldFiltroFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldFiltroFocusGained
        
        System.out.println("Se ejecuta el evento focus gained----------->");
        this.jTextFieldFiltro.selectAll();
        
    }//GEN-LAST:event_jTextFieldFiltroFocusGained

    private void btnUtilidadesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUtilidadesActionPerformed
        UtilidadesFrame frame = new UtilidadesFrame(mapUtilidades, codCatChanca, codCatSP);
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(900, 500);
        frame.setLocation((dim.width/2)-(frame.getSize().width/2), (dim.height/2)-(frame.getSize().height/2));
            
        frame.setVisible(true);        
    }//GEN-LAST:event_btnUtilidadesActionPerformed

    
    public void showDetallesFrame(){
        System.out.println("Select action");
        int row = this.jTable2.getSelectedRow();
        if (row>-1){
            FilaVenta filart = this.ventasDataModel.getValueAt(row);            
            DetallesFacturaFrame detallesFacturaFrame = new DetallesFacturaFrame(filart.getVentaId());
            
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            detallesFacturaFrame.setSize(900, 500);
            detallesFacturaFrame.setLocation((dim.width/2)-(detallesFacturaFrame.getSize().width/2), (dim.height/2)-(detallesFacturaFrame.getSize().height/2));            
            detallesFacturaFrame.setVisible(true);
        }    
        else{
            JOptionPane.showMessageDialog(this, "Debe seleccionar la factura");
        }
    }
    
    public JFrame getRoot() {
        return root;
    }

    public void setRoot(JFrame root) {
        this.root = root;
    }
    
    @Override
    public void setFilaArticulo(Articulos filaArticulo) {        
        System.out.println("Set fila articulo");
        this.articuloSel = filaArticulo;
        this.articuloSelLabel.setText( this.articuloSel.getArtNombre() + "(" + this.articuloSel.getArtCodbar()+")" );
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton articuloSelBtn;
    private javax.swing.JLabel articuloSelLabel;
    private javax.swing.JButton btnAnular;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDetalles;
    private javax.swing.JButton btnUtilidades;
    private javax.swing.JButton jBtnQuitarArt;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelSumaCredito;
    private javax.swing.JLabel jLabelSumaDesc;
    private javax.swing.JLabel jLabelSumaEfectivo;
    private javax.swing.JLabel jLabelSumaIva;
    private javax.swing.JLabel jLabelSumaSaldo;
    private javax.swing.JLabel jLabelSumaTot;
    private javax.swing.JLabel jLabelSumaUtilidades;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTFSumaCredito;
    private javax.swing.JTextField jTFSumaDesc;
    private javax.swing.JTextField jTFSumaEfectivo;
    private javax.swing.JTextField jTFSumaIva;
    private javax.swing.JTextField jTFSumaSaldoPend;
    private javax.swing.JTextField jTFSumaTotal;
    private javax.swing.JTextField jTFSumaUtilidades;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextFieldFiltro;
    // End of variables declaration//GEN-END:variables

}
