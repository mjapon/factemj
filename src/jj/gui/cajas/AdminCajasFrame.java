/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jj.gui.cajas;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import jj.controller.CajasJpaController;
import jj.gui.BaseFrame;
import jj.util.FechasUtil;
import jj.util.ParamBusquedaCXCP;
import jj.util.datamodels.CajasDataModel;
import jj.util.datamodels.rows.FilaCajas;
/**
 *
 * @author mjapon
 */
public class AdminCajasFrame extends BaseFrame {
    
    private JFormattedTextField desdeTF;
    private JFormattedTextField hastaTF;
    private CajasJpaController cajasController;
    
    private CajasDataModel cajasDataModel;

    /** Creates new form AdminCajasFrame */
    public AdminCajasFrame() {
        initComponents();
        cajasController = new CajasJpaController(em);
        setupFechas();
        init();
    }
    
    public void setupFechas(){
        desdeTF = new JFormattedTextField(createFormatter("##/##/####"));
        desdeTF.setFont(new java.awt.Font("Arial", 0, 16));
        
        hastaTF = new JFormattedTextField(createFormatter("##/##/####"));
        hastaTF.setFont(new java.awt.Font("Arial", 0, 16));
        
        jPanel12.add( desdeTF );
        jPanel12.add( hastaTF );
    }
    
    public void init(){
        desdeTF.setText(FechasUtil.getFechaActual());
        hastaTF.setText(FechasUtil.getFechaActual());
        
        cajasDataModel = new CajasDataModel();
        cajasDataModel.setController(cajasController);
        jTable1.setModel(cajasDataModel);
        
        
        jTable1.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTable1.columnAtPoint(e.getPoint());
                String name = jTable1.getColumnName(col);
                //System.out.println("TableHeader column index selected " + col + " " + name);
                
                try{
                    for (int i=0; i<cajasDataModel.getColumnCount();i++){
                        jTable1.getColumnModel().getColumn(i).setHeaderValue( cajasDataModel.getColumnName(i) );
                    }                    
                    cajasDataModel.switchSortColumn(col);                    
                }
                catch(Throwable ex){
                    JOptionPane.showMessageDialog(null, "Error en sort:"+ex.getMessage());
                    System.out.println("Error en sort:"+ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getFirstIndex()>=0){
                    jButtonAnular.setEnabled(false);
                    try{
                        int row = jTable1.getSelectedRow();
                        if (row>-1){
                            FilaCajas filart = cajasDataModel.getValueAt(row);
                            if (filart.getCj_estado() != 2){
                                jButtonAnular.setEnabled(true);
                            }
                        }
                    }
                    catch(Throwable ex){
                        System.out.println("Error en selection listener:"+ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                else{
                    jButtonAnular.setEnabled(false);
                }
            }
        });
        
        jTable1.updateUI();
        cajasDataModel.fireTableDataChanged();
    }
    
    public void doAnular(){
        int row = this.jTable1.getSelectedRow();
        if (row>-1){
            FilaCajas filart = this.cajasDataModel.getValueAt(row);        
            if (filart != null){
                try {
                    this.cajasController.anularCaja(filart.getCj_id());
                    showMsg("La caja ha sido anulada");
                    this.cajasDataModel.loadFromDataBase();
                } catch (Throwable ex) {
                    showMsgError(ex);
                }
            }
        }
        else{
            showMsg("Elija el registro que desea anular");
        }
    }
    
    public void logicaBuscar(){
        try{
            Date fechaDesde = FechasUtil.parse(desdeTF.getText());
            Date fechaHasta = FechasUtil.parse(hastaTF.getText());
            
            cajasController.listar(fechaDesde, fechaHasta);
            
            ParamBusquedaCXCP params = new ParamBusquedaCXCP();
            params.setDesde(fechaDesde);
            params.setHasta(fechaHasta);
            
            cajasDataModel.setParams(params);
            cajasDataModel.loadFromDataBase();            
        }
        catch(Throwable ex){
            showMsgError(ex);
        }    
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel12 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButtonBuscar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jButtonAnular = new javax.swing.JButton();
        jButtonCerrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setText("Administración de cajas");
        jPanel5.add(jLabel1);

        jPanel1.add(jPanel5, java.awt.BorderLayout.NORTH);

        jComboBox1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hoy", "Ayer", "Esta Semana", "Este Mes", "Mes Anterior", "Este Año" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jPanel12.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jPanel12.setLayout(new java.awt.GridLayout(2, 2));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jLabel2.setText("Desde:");
        jPanel12.add(jLabel2);

        jLabel3.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jLabel3.setText("Hasta:");
        jPanel12.add(jLabel3);

        jButtonBuscar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButtonBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-search.png"))); // NOI18N
        jButtonBuscar.setText("Buscar");
        jButtonBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63)
                .addComponent(jButtonBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(191, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButtonBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel6, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jTable1.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
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

        jButtonAnular.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButtonAnular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-cancel_subscription.png"))); // NOI18N
        jButtonAnular.setText("Anular");
        jButtonAnular.setEnabled(false);
        jButtonAnular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnularActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonAnular);

        jButtonCerrar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButtonCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-close_pane_filled.png"))); // NOI18N
        jButtonCerrar.setText("Cerrar");
        jButtonCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCerrarActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonCerrar);

        getContentPane().add(jPanel3, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        
        setupFechasEvent(jComboBox1, desdeTF, hastaTF);
        logicaBuscar();

    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButtonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBuscarActionPerformed
        
        logicaBuscar();
        
    }//GEN-LAST:event_jButtonBuscarActionPerformed

    private void jButtonCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCerrarActionPerformed
        
        this.closeFrame();
        
    }//GEN-LAST:event_jButtonCerrarActionPerformed

    private void jButtonAnularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnularActionPerformed
        if (showConfirmMsg("¿Seguro que desea anular este registro?")){
            doAnular();
        }
    }//GEN-LAST:event_jButtonAnularActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAnular;
    private javax.swing.JButton jButtonBuscar;
    private javax.swing.JButton jButtonCerrar;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}
