/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui.merc.unid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import jj.controller.UnidadesJpaController;
import jj.gui.BaseFrame;
import jj.gui.FarmaAppMain;
import jj.util.NumbersUtil;
import jj.util.StringUtil;
import jj.util.datamodels.DefGSVCol;
import jj.util.datamodels.JTableColumn;
import jj.util.datamodels.PreciosUnidadDataModel;
import jj.util.datamodels.TableHeadMouseAdapter;
import jj.util.datamodels.rows.FilaArticulo;
import jj.util.datamodels.rows.FilaUnidad;
import jj.util.datamodels.rows.FilaUnidadPrecio;

/**
 *
 * @author manuel.japon
 */
public class PreciosXUnidadFrame extends BaseFrame {
    
    private List<FilaUnidad> unidadesList;
    private List<FilaUnidadPrecio> precios;
    private UnidadesJpaController unidadesController;
    private FilaArticulo selectedArt;
    private PreciosUnidadDataModel dataModel;
    private List<JTableColumn> columns;
    
    /**
     * Creates new form PreciosXUnidadFrame
     */
    public PreciosXUnidadFrame( FilaArticulo selectedArt ) {
        initComponents();
        unidadesController = new UnidadesJpaController(em);
        this.selectedArt = selectedArt;
        loadUnidades();
        initColumns();
        dataModel = new PreciosUnidadDataModel(columns, unidadesController, this.selectedArt.getArtId());
        jTable1.setModel(dataModel);
        loadPrecios();
        
        jTable1.getTableHeader().addMouseListener(new TableHeadMouseAdapter(jTable1, dataModel));
        
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()) {
                    endisBtns();
                }
            }
        });
    }    
    
    public void endisBtns(){
        boolean isEnable = jTable1.getSelectedRows().length>0;
        jBtnBorrar.setEnabled(isEnable);
    }
    
    public void initColumns(){
        //a.uni_id, a.uni_name, a.uni_simbolo
        columns = new ArrayList<JTableColumn>();
        columns.add(
                new JTableColumn<FilaUnidadPrecio>(
                        0, 
                        "Unidad", 
                        "uni_name", 
                        Integer.class,
                        new DefGSVCol<FilaUnidadPrecio>() {
                            public Object getValueAt(FilaUnidadPrecio row, int rowIndex) {
                                return row.getUnidad();
                            }
                        }
                )
        );
        
        columns.add(
                new JTableColumn<FilaUnidadPrecio>(
                        1, 
                        "Precio Normal", 
                        "unidp_precioventa", 
                        String.class,
                        new DefGSVCol<FilaUnidadPrecio>() {
                            public Object getValueAt(FilaUnidadPrecio row, int rowIndex) {
                                if (row.isIsIva()){
                                    return NumbersUtil.round2(row.getPrecioNormalConIva());
                                }
                                return NumbersUtil.round2(row.getPrecioNormal());
                            }
                            public boolean isCellEditable(FilaUnidadPrecio row) {
                                return true;
                            }
                            public void setValueAt(FilaUnidadPrecio row, int rowIndex, Object value) {
                                if (row.isIsIva()){
                                    row.setPrecioNormal(FilaArticulo.getPrecioSinIvaUtil(new BigDecimal(value.toString())) );
                                }
                                else{
                                    row.setPrecioNormal(new BigDecimal(String.valueOf(value)));
                                }
                            }
                        }
                )
        );
        
        columns.add(
                new JTableColumn<FilaUnidadPrecio>(
                        2, 
                        "Precio Mínimo",
                        "unidp_preciomin", 
                        String.class,
                        new DefGSVCol<FilaUnidadPrecio>() {
                            public Object getValueAt(FilaUnidadPrecio row, int rowIndex) {
                                if (row.isIsIva()){
                                    return NumbersUtil.round2(row.getPrecioMinConIva());
                                }
                                return NumbersUtil.round2(row.getPrecioMin());
                            }
                            public boolean isCellEditable(FilaUnidadPrecio row) {
                                return true;
                            }
                            public void setValueAt(FilaUnidadPrecio row, int rowIndex, Object value) {
                                if (row.isIsIva()){
                                    row.setPrecioMin(FilaArticulo.getPrecioSinIvaUtil(new BigDecimal(value.toString())) );
                                }
                                else{
                                    row.setPrecioMin(new BigDecimal(String.valueOf(value)));
                                }
                            }
                        }
                )
        );
    }
    
    public void loadUnidades(){
        unidadesList = unidadesController.listar();
        unidadesList.stream().forEach(filaUnidad -> jCBUnidad.addItem(filaUnidad.getUnidName()) );
    }
    
    public void filterUnidades(){
        precios.stream().forEach( filaPrecio -> { 
            unidadesList.stream().filter(filaUnidad -> filaUnidad.getUnidId()!= filaPrecio.getUnidId() );
        });
    }
    
    public void loadPrecios(){
        dataModel.loadFromDataBase();
        dataModel.fireTableDataChanged();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jBtnBorrar = new javax.swing.JButton();
        jBtnSalir = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jCBUnidad = new javax.swing.JComboBox<>();
        jTFPrecioVenta = new javax.swing.JTextField();
        jTFPrecioMin = new javax.swing.JTextField();
        jBtnCrear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jTable1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
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
        jTable1.setRowHeight(25);
        jScrollPane1.setViewportView(jTable1);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.GridLayout(5, 1));

        jBtnBorrar.setText("Borrar");
        jBtnBorrar.setEnabled(false);
        jBtnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBorrarActionPerformed(evt);
            }
        });
        jPanel3.add(jBtnBorrar);

        jBtnSalir.setText("Cerrar");
        jBtnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnSalirActionPerformed(evt);
            }
        });
        jPanel3.add(jBtnSalir);

        getContentPane().add(jPanel3, java.awt.BorderLayout.EAST);

        jPanel4.setLayout(new java.awt.GridLayout(2, 1));

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setText("Precios x Unidad");
        jPanel1.add(jLabel1);

        jPanel4.add(jPanel1);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel6.setLayout(new java.awt.GridLayout(2, 3));

        jLabel2.setText("Unidad");
        jPanel6.add(jLabel2);

        jLabel3.setText("Precio Venta");
        jPanel6.add(jLabel3);

        jLabel4.setText("Precio Min.");
        jPanel6.add(jLabel4);

        jPanel6.add(jCBUnidad);
        jPanel6.add(jTFPrecioVenta);
        jPanel6.add(jTFPrecioMin);

        jPanel5.add(jPanel6, java.awt.BorderLayout.CENTER);

        jBtnCrear.setText("Agregar");
        jBtnCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnCrearActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnCrear, java.awt.BorderLayout.EAST);

        jPanel4.add(jPanel5);

        getContentPane().add(jPanel4, java.awt.BorderLayout.NORTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnSalirActionPerformed
        
        setVisible(false);
        
    }//GEN-LAST:event_jBtnSalirActionPerformed

    private void jBtnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnCrearActionPerformed
        try{
            String unityName = (String)jCBUnidad.getSelectedItem();
            Optional<FilaUnidad> optionalResult = unidadesList.stream().filter(e -> e.getUnidName().equalsIgnoreCase(unityName)).findFirst();
            if (optionalResult.isPresent()){
                FilaUnidad selectedUnity = optionalResult.get();
                
                if (StringUtil.isEmpty(jTFPrecioVenta.getText()) || StringUtil.isEmpty(jTFPrecioMin.getText())){
                    showMsg("Debe ingresar los precios");
                    return;
                }
                
                BigDecimal precioNormalInput = new BigDecimal(jTFPrecioVenta.getText());
                BigDecimal precioMinInput = new BigDecimal(jTFPrecioMin.getText());
                
                //Verificar si el producto graba iva
                BigDecimal precioNormal = precioNormalInput;
                BigDecimal precioMin = precioMinInput;
                if (selectedArt.isIva()){
                    precioNormal = FilaArticulo.getPrecioSinIvaUtil(precioNormalInput);
                    precioMin = FilaArticulo.getPrecioSinIvaUtil(precioMinInput);
                }
                
                if (unidadesController.yaExistePrecio(selectedArt.getArtId(), selectedUnity.getUnidId())){
                    showMsg("Ya esta registrado el precio para esta unidad");
                    return;
                }
                unidadesController.registrarPrecio( 
                        new FilaUnidadPrecio(selectedArt.getArtId(), 
                                selectedUnity.getUnidId(), 
                                precioNormal, 
                                precioMin,
                                selectedArt.isIva()
                        )
                );
                loadPrecios();
                FarmaAppMain.showSystemTrayMsg("El precio ha sido registrado");
            }
            else{
                showMsg("No pude recuperar unidad seleccionada");
            }
        }
        catch(Throwable ex){
            showMsgError(ex);
        }
    }//GEN-LAST:event_jBtnCrearActionPerformed

    private void jBtnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBorrarActionPerformed
        try{
            if (showConfirmMsg("¿Esta segur@?")){
                for (int row=0;row<jTable1.getSelectedRows().length;row++){
                    int rowindex = jTable1.getSelectedRows()[row];
                    FilaUnidadPrecio fila = dataModel.getValueAt(rowindex);
                    unidadesController.deletePrecio( fila.getFilaId() );
                }
            }
            loadPrecios();
            FarmaAppMain.showSystemTrayMsg(" Los registros han sido borrados");
        }
        catch(Throwable ex){
            showMsgError(ex);
        }
        
    }//GEN-LAST:event_jBtnBorrarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnBorrar;
    private javax.swing.JButton jBtnCrear;
    private javax.swing.JButton jBtnSalir;
    private javax.swing.JComboBox<String> jCBUnidad;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTFPrecioMin;
    private javax.swing.JTextField jTFPrecioVenta;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
