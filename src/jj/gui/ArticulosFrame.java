/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.EntityManager;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import jj.controller.ArticulosJpaController;

import jj.util.datamodels.ArticulosDataModel;
import jj.util.ArticulosModelListener;
import jj.util.CtesU;
import jj.util.EntityManagerUtil;
import jj.util.ErrorValidException;
import jj.util.FilaArticulo;
import jj.util.IVAComboBoxEditor;
import jj.util.IVAComboBoxRenderer;

/**
 *
 * @author manuel.japon
 */
public class ArticulosFrame extends javax.swing.JFrame implements ILabelEstado{
    
    private ArticulosDataModel articulosDataModel;
    private ArticulosModelListener articulosModelListener;
    private EntityManager em;
    private ArticulosJpaController articulosController;
    
    private JFrame root;
 public JFrame getRoot() {
        return root;
 }

 public void setRoot(JFrame root) {
      this.root = root;
 }
    
    /**
     * Creates new form ArticulosFrame
     */
    public ArticulosFrame() {
        initComponents();
        
        this.em = EntityManagerUtil.createEntintyManagerFactory();
        articulosController = new ArticulosJpaController(em);
        
        boolean forSelect = false;
        
        articulosDataModel = new ArticulosDataModel(1);
        articulosDataModel.setController(articulosController);
        articulosDataModel.setArticulosFrame(this);
        
        articulosModelListener = new ArticulosModelListener();
        articulosDataModel.addTableModelListener(articulosModelListener);
        
        jTableArts.setModel(articulosDataModel);
        String[] values = new String[] { "SI", "NO"};
        
        TableColumn col = jTableArts.getColumnModel().getColumn(6);
        col.setCellEditor(new IVAComboBoxEditor(values));
        
        IVAComboBoxRenderer ivaCombo =  new IVAComboBoxRenderer(values);
        
        ivaCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Action performed Combo value:" + e.getSource());
            }
        });
        
        col.setCellRenderer(ivaCombo);
                
        jTableArts.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTableArts.columnAtPoint(e.getPoint());
                String name = jTableArts.getColumnName(col);
                //System.out.println("TableHeader column index selected " + col + " " + name);
                
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
                if (e.getFirstIndex()>=0){
                    btnBorrar.setEnabled(true);
                }
                else{
                    btnBorrar.setEnabled(false);                    
                }
            }
        });
        
        jTableArts.updateUI();
        articulosDataModel.fireTableDataChanged();        
        tfCodBarra.requestFocus();        
    }
    
    public void updateLabelEstado(String label){
        this.jLabelEstado.setText(label);
    }
    
    public void clearForms(){
        tfCodBarra.setText("");
        tfNombre.setText("");
        tfPrecioCompra.setText("");
        tfPrecioVenta.setText("");
        tfPrecioMin.setText("");
        tfInventario.setText("");
        tfIVAPC.setText("");
    }
    
    public void updateArticulos(){
        
        try{
            articulosDataModel.loadFromDataBase();
            
           if (this.articulosDataModel.getItems() != null){
                    jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                        String.valueOf( "("+ this.articulosDataModel.getItems().size() + ")" )    
                    ));
                } 
            
        }
        catch(Throwable ex){
            System.out.println("Error al traer de base de datos:"+ex.getMessage());
            updateLabelEstado("Error al traer de base de datos:"+ex.getMessage());
            JOptionPane.showMessageDialog(null, "Error al traer de base de datos:"+ex.getMessage());
        }
        
        articulosDataModel.fireTableDataChanged();        
        
    }
    
    public void logicaAdd(){
        try{
            Integer artId = 0;
            String codBarra =  this.tfCodBarra.getText();
            String nombre = this.tfNombre.getText();
            BigDecimal precioCompraIva = new BigDecimal(this.tfPrecioCompra.getText().trim()).add(new BigDecimal(this.tfIVAPC.getText().trim()));
            BigDecimal precioCompraConIva = precioCompraIva;
            BigDecimal precioVentaConIva = new BigDecimal(this.tfPrecioVenta.getText().trim());
            BigDecimal precioMinConIva = new BigDecimal(this.tfPrecioMin.getText().trim());
            BigDecimal inventario = new BigDecimal(this.tfInventario.getText().trim());
            
            String valueOfIva = this.cbIvaInput.getSelectedItem().toString();
            
            boolean IVA = "SI".equalsIgnoreCase(valueOfIva);
            
            boolean genCodbar = this.jCBGenCodbar.isSelected();
            if (genCodbar){
                codBarra = "GEN";
            }
        
            FilaArticulo auxfilaArt = new FilaArticulo(artId, codBarra, nombre, precioCompraConIva, precioVentaConIva, precioMinConIva, IVA, inventario);            
            FilaArticulo filaArt  = new FilaArticulo(artId, 
                    codBarra, 
                    nombre, 
                    auxfilaArt.getPrecioCompraSinIva(),
                    auxfilaArt.getPrecioVentaSinIva(),
                    auxfilaArt.getPrecioMinSinIva(),
                    IVA, 
                    inventario);            
            boolean valid = this.articulosDataModel.validar(filaArt);            
            if (valid){
                //Verificar codigo de barra y nombre de articulo
                if (!genCodbar && articulosController.yaExisteBarcode(codBarra)){
                    throw  new ErrorValidException("El código de barra:"+codBarra+" ya esta registrado");
                }
                else if (articulosController.yaExisteNombre(nombre)){
                    throw  new ErrorValidException("El artículo de nombre:"+nombre+" ya esta registrado");
                }                
                articulosController.crearArticulo(filaArt, genCodbar);
                articulosDataModel.loadFromDataBase();
                this.clearForms();
                
                if (genCodbar){
                    this.tfNombre.requestFocus();
                }
                else{
                    this.tfCodBarra.requestFocus();
                }
                
            }
            else{
                JOptionPane.showMessageDialog(null,"Los datos son incorrectos, verifica el codigo de barra, nombre del articulo ..");
            }
        }
        catch(Throwable ex){
            updateLabelEstado("Error:"+ex.getMessage());
            JOptionPane.showMessageDialog(null, "Error:"+ex.getMessage());
            ex.printStackTrace();
            System.out.println("Error:"+ex.getMessage());
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

        jPanelNorth = new javax.swing.JPanel();
        jPanelTitulo = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jCBCalPrecio = new javax.swing.JCheckBox();
        jCBGenCodbar = new javax.swing.JCheckBox();
        jPanelInputs = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tfCodBarra = new javax.swing.JTextField();
        tfNombre = new javax.swing.JTextField();
        cbIvaInput = new javax.swing.JComboBox<>();
        tfPrecioCompra = new javax.swing.JTextField();
        tfIVAPC = new javax.swing.JTextField();
        tfPrecioVenta = new javax.swing.JTextField();
        tfPrecioMin = new javax.swing.JTextField();
        tfInventario = new javax.swing.JTextField();
        jPanelCenter = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        filtroTF = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableArts = new javax.swing.JTable();
        jPanelSouth = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabelEstado = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(900, 400));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanelNorth.setLayout(new java.awt.BorderLayout());

        jPanelTitulo.setLayout(new java.awt.BorderLayout());

        jLabel7.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel7.setText("LISTA DE ARTíCULOS");
        jPanelTitulo.add(jLabel7, java.awt.BorderLayout.CENTER);

        jPanel4.setLayout(new java.awt.GridLayout(1, 2));

        jCBCalPrecio.setText("Calcular Precios");
        jPanel4.add(jCBCalPrecio);

        jCBGenCodbar.setText("Generar Código");
        jCBGenCodbar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBGenCodbarActionPerformed(evt);
            }
        });
        jPanel4.add(jCBGenCodbar);

        jPanelTitulo.add(jPanel4, java.awt.BorderLayout.EAST);

        jPanelNorth.add(jPanelTitulo, java.awt.BorderLayout.NORTH);

        jPanelInputs.setLayout(new java.awt.GridLayout(2, 8));

        jLabel1.setText("Codigo Barra:");
        jPanelInputs.add(jLabel1);

        jLabel2.setText("Nombre:");
        jPanelInputs.add(jLabel2);

        jLabel8.setText("IVA:");
        jPanelInputs.add(jLabel8);

        jLabel3.setText("Pre Compra Sin Iva:");
        jPanelInputs.add(jLabel3);

        jLabel10.setText("IVA PC");
        jPanelInputs.add(jLabel10);

        jLabel4.setText("Precio Venta:");
        jPanelInputs.add(jLabel4);

        jLabel5.setText("Precio Min:");
        jPanelInputs.add(jLabel5);

        jLabel6.setText("Inventario:");
        jPanelInputs.add(jLabel6);

        tfCodBarra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tfCodBarraKeyPressed(evt);
            }
        });
        jPanelInputs.add(tfCodBarra);

        tfNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tfNombreKeyPressed(evt);
            }
        });
        jPanelInputs.add(tfNombre);

        cbIvaInput.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SI", "NO" }));
        cbIvaInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbIvaInputActionPerformed(evt);
            }
        });
        cbIvaInput.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cbIvaInputPropertyChange(evt);
            }
        });
        jPanelInputs.add(cbIvaInput);

        tfPrecioCompra.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfPrecioCompraFocusLost(evt);
            }
        });
        tfPrecioCompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tfPrecioCompraKeyPressed(evt);
            }
        });
        jPanelInputs.add(tfPrecioCompra);

        tfIVAPC.setEnabled(false);
        tfIVAPC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tfIVAPCKeyPressed(evt);
            }
        });
        jPanelInputs.add(tfIVAPC);

        tfPrecioVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tfPrecioVentaKeyPressed(evt);
            }
        });
        jPanelInputs.add(tfPrecioVenta);

        tfPrecioMin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tfPrecioMinKeyPressed(evt);
            }
        });
        jPanelInputs.add(tfPrecioMin);

        tfInventario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tfInventarioKeyPressed(evt);
            }
        });
        jPanelInputs.add(tfInventario);

        jPanelNorth.add(jPanelInputs, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelCenter.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel9.setText("Filtro");
        jPanel3.add(jLabel9, java.awt.BorderLayout.WEST);

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
        jPanel3.add(filtroTF, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel3, java.awt.BorderLayout.NORTH);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Count"));
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        jTableArts.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
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
        jScrollPane1.setViewportView(jTableArts);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanelCenter.add(jPanel2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setLayout(new java.awt.GridLayout(6, 1));

        btnAdd.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnAdd.setText("Agregar");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        jPanelSouth.add(btnAdd);

        btnSave.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnSave.setText("Guardar");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanelSouth.add(btnSave);

        btnBorrar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnBorrar.setText("Borrar");
        btnBorrar.setEnabled(false);
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });
        jPanelSouth.add(btnBorrar);

        btnClose.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnClose.setText("Cerrar");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanelSouth.add(btnClose);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.EAST);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabelEstado.setText("jLabel8");
        jPanel1.add(jLabelEstado, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        
        logicaAdd();
       
        
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        //this.setVisible(false);
        FarmaAppMain farmaApp = (FarmaAppMain)this.root;
        farmaApp.logicaClosePane(this.getClass().getName());
        
    }//GEN-LAST:event_btnCloseActionPerformed

    public void filtroFocus(){
        
        this.filtroTF.requestFocus();
        
    }
    
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        this.articulosDataModel.saveAllRecords();                
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tfCodBarraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfCodBarraKeyPressed
      if (evt.getKeyCode() == KeyEvent.VK_ENTER){
          String barcode = tfCodBarra.getText();
          if (barcode.trim().length()>0){
              if (this.articulosController.yaExisteBarcode(barcode)){
                  JOptionPane.showMessageDialog(null, "Ya esta registrado el artículo con código de barra:"+ barcode+", ingrese otro");
              }
              else{
                  tfNombre.requestFocus();
              }
          }
      }
    }//GEN-LAST:event_tfCodBarraKeyPressed

    private void tfNombreKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfNombreKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            tfPrecioCompra.requestFocus();
        }
    }//GEN-LAST:event_tfNombreKeyPressed

    private void tfPrecioCompraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfPrecioCompraKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            tfPrecioVenta.requestFocus();
        }
    }//GEN-LAST:event_tfPrecioCompraKeyPressed

    private void tfPrecioVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfPrecioVentaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            tfPrecioMin.requestFocus();
        }
    }//GEN-LAST:event_tfPrecioVentaKeyPressed

    private void tfPrecioMinKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfPrecioMinKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            tfInventario.requestFocus();
        }
    }//GEN-LAST:event_tfPrecioMinKeyPressed

    private void tfInventarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfInventarioKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            logicaAdd();
        }
    }//GEN-LAST:event_tfInventarioKeyPressed

    public void disableBtnBorar(){
        this.btnBorrar.setEnabled(false);
    }
    
    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        int row = this.jTableArts.getSelectedRow();
        if (row>-1){
            FilaArticulo filart = this.articulosDataModel.getValueAt(row);        
            if (filart != null){
                try {
                    this.articulosController.destroy(filart.getArtId());                    
                    JOptionPane.showMessageDialog( null, "Artículo borrado");
                    this.articulosDataModel.loadFromDataBase();
                } catch (Throwable ex) {
                    System.out.println("No se pudo borrar el articulo "+ filart.getNombre()+":"+ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "No se pudo borrar el articulo "+ filart.getNombre()+":"+ex.getMessage());
                    updateLabelEstado("No se pudo borrar el articulo "+ filart.getNombre()+":"+ex.getMessage());
                }
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "Elija un articulo para borrar");
        }        
    }//GEN-LAST:event_btnBorrarActionPerformed

    
    public void doFilter(){
        //System.out.println("Filtro key pressed--->");        
        //Se debe filtrar todos los articulos del model                
        String filtro = this.filtroTF.getText().trim();
        if (filtro.length()>0){
            //System.out.println("Se aplica el filtro:"+filtro);
            try{
                this.articulosDataModel.loadFromDataBaseFilter(filtro);
                
                if (this.articulosDataModel.getItems() != null){
                    jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                        "("+ String.valueOf( this.articulosDataModel.getItems().size() ) + ")"    
                    ));
                } 
                
            }
            catch(Throwable ex){
                System.out.println("Error al cargar datos:"+ ex.getMessage());
                ex.printStackTrace();
            }
        }
        
    }
    
    private void filtroTFKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filtroTFKeyPressed
       
        
    }//GEN-LAST:event_filtroTFKeyPressed

    private void filtroTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtroTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtroTFActionPerformed

    private void filtroTFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filtroTFKeyReleased
        
        doFilter();
        
    }//GEN-LAST:event_filtroTFKeyReleased

    private void tfIVAPCKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfIVAPCKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfIVAPCKeyPressed

    private void tfPrecioCompraFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfPrecioCompraFocusLost
      //Se debe calcular el iva del precio de compra
      logicaIvaPrecioCompra(jCBCalPrecio.isSelected());
     
      
        
    }//GEN-LAST:event_tfPrecioCompraFocusLost

    
    private void logicaIvaPrecioCompra(boolean calPrecio){
         BigDecimal valorIvaPC = BigDecimal.ZERO;      
      try{
          BigDecimal precioCompra = new BigDecimal(this.tfPrecioCompra.getText());          
          String valueIva = cbIvaInput.getSelectedItem().toString();
          
          if ("SI".equalsIgnoreCase(valueIva)){
              valorIvaPC = precioCompra.multiply(new BigDecimal(CtesU.IVA)).setScale(2, RoundingMode.HALF_UP);
          }
          else{
              
          }                  
          
          if (calPrecio){
                BigDecimal precioVenta = BigDecimal.ZERO;
                BigDecimal porcGanancia = new BigDecimal("0.20");
                BigDecimal porcPrecMin = new BigDecimal("0.05");
            
                BigDecimal factorGanacia = BigDecimal.ONE.add(porcGanancia);
                BigDecimal factorPrecioMin = BigDecimal.ONE.subtract(porcPrecMin);
      
                BigDecimal posiblePreVenta = (precioCompra.add(valorIvaPC)).multiply( factorGanacia ).setScale(2, RoundingMode.HALF_UP);
                BigDecimal posiblePreMin = posiblePreVenta.multiply( factorPrecioMin ).setScale(2, RoundingMode.HALF_UP);
            
                this.tfPrecioVenta.setText(posiblePreVenta.toPlainString());
                this.tfPrecioMin.setText(posiblePreMin.toPlainString());
          }          
              
              
      }
      catch(Throwable ex){
          System.out.println("Error al calcular el iva:"+ ex.getMessage());
          ex.printStackTrace();
      }
      
      this.tfIVAPC.setText(valorIvaPC.toPlainString());    
    }
    
    private void cbIvaInputPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cbIvaInputPropertyChange
        
        //logicaIvaPrecioCompra(false);
        
    }//GEN-LAST:event_cbIvaInputPropertyChange

    private void cbIvaInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbIvaInputActionPerformed
        logicaIvaPrecioCompra(false);
    }//GEN-LAST:event_cbIvaInputActionPerformed

    private void jCBGenCodbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBGenCodbarActionPerformed
        
        if (this.jCBGenCodbar.isSelected()){
            this.tfCodBarra.setEnabled(false);
        }
        else{
            this.tfCodBarra.setEnabled(true);
        }
        
    }//GEN-LAST:event_jCBGenCodbarActionPerformed
    
    private boolean filtroInRow(String filtro, FilaArticulo filaArt){
        boolean result = false;
        String codBarra = filaArt.getCodBarra();
        String nombreArt = filaArt.getNombre();
        return codBarra.toUpperCase().contains(filtro.toUpperCase()) ||
                nombreArt.toUpperCase().contains(filtro.toUpperCase());
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cbIvaInput;
    private javax.swing.JTextField filtroTF;
    private javax.swing.JCheckBox jCBCalPrecio;
    private javax.swing.JCheckBox jCBGenCodbar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelEstado;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelInputs;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelTitulo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableArts;
    private javax.swing.JTextField tfCodBarra;
    private javax.swing.JTextField tfIVAPC;
    private javax.swing.JTextField tfInventario;
    private javax.swing.JTextField tfNombre;
    private javax.swing.JTextField tfPrecioCompra;
    private javax.swing.JTextField tfPrecioMin;
    private javax.swing.JTextField tfPrecioVenta;
    // End of variables declaration//GEN-END:variables
}
