/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui.cajas;

import java.math.BigDecimal;
import java.util.Date;
import jj.controller.CajasJpaController;
import jj.entity.Cajas;
import jj.gui.BaseFrame;
import jj.util.FechasUtil;
import jj.util.NumbersUtil;

/**
 *
 * @author mjapon
 */
public class AperturaCajaFrame extends BaseFrame {
    
    private Date dia;
    private BigDecimal saldoInicial;
    
    CajasJpaController cajasController;    
    
    /**
     * Creates new form AperturaCajaFrame
     */
    public AperturaCajaFrame() {
        super();
        initComponents();        
        cajasController = new CajasJpaController(em);        
        initForm();
    }
    
    public void initForm(){
        try{
            dia = new Date();
            dayName.setText(FechasUtil.getDayNameOfDate(dia));
            jTFFecha.setText( FechasUtil.format(dia) );
            saldoInicial = BigDecimal.ZERO;
            Cajas cajaAyer = cajasController.getCajaCerradaMenorFechaCierre(dia);
            if (cajaAyer != null){
                saldoInicial = cajaAyer.getCjSaldo();
            }
            jTFSaldoAnterior.setText( NumbersUtil.round2(saldoInicial).toPlainString() );
            jTAObs.setText("");
        }
        catch(Throwable ex){
            showMsgError(ex);
        }
    }   
    
    public void verificarCajaAperturada(){
        if (cajasController.existeCajaAbierta(dia)){
            showMsg("Ya ha sido aperturada la caja para el día de hoy:"+FechasUtil.getDayNameOfDate(dia));
            setVisible(false);
        }
        else{
            if (cajasController.existeCajaAbiertaMenorFecha(dia)){
                showMsg("No se puede abrir Caja, existe una caja anterior que no ha sido cerrada aún, cierre esa caja para poder aperturar otra" );
            }
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTFFecha = new javax.swing.JFormattedTextField();
        jTFSaldoAnterior = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTAObs = new javax.swing.JTextArea();
        dayName = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButtonGuardar = new javax.swing.JButton();
        jButtonCerrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setText("Apertura de Caja");
        jPanel1.add(jLabel1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jLabel2.setText("Fecha de apertura:");

        jLabel3.setText("Saldo Inicial (anterior):");

        jLabel4.setText("Observación:");

        jTFFecha.setEditable(false);
        try {
            jTFFecha.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jTFFecha.setFont(new java.awt.Font("Arial", 0, 20)); // NOI18N
        jTFFecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFFechaActionPerformed(evt);
            }
        });

        jTFSaldoAnterior.setFont(new java.awt.Font("Arial", 0, 20)); // NOI18N

        jTAObs.setColumns(20);
        jTAObs.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jTAObs.setRows(5);
        jScrollPane1.setViewportView(jTAObs);

        dayName.setFont(new java.awt.Font("Arial", 0, 20)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTFSaldoAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTFFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dayName)))
                .addGap(65, 65, 65))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dayName))
                .addGap(35, 35, 35)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTFSaldoAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel4.setLayout(new java.awt.GridLayout(4, 1));

        jButtonGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-save.png"))); // NOI18N
        jButtonGuardar.setText("Guardar");
        jButtonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGuardarActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonGuardar);

        jButtonCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-close_pane_filled.png"))); // NOI18N
        jButtonCerrar.setText("Cerrar");
        jButtonCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCerrarActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonCerrar);

        getContentPane().add(jPanel4, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTFFechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFFechaActionPerformed

    }//GEN-LAST:event_jTFFechaActionPerformed

    private void jButtonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGuardarActionPerformed
        try{
            saldoInicial = new BigDecimal(jTFSaldoAnterior.getText());
            cajasController.crearCaja(dia, saldoInicial, jTAObs.getText());
            String dayName = FechasUtil.getDayNameOfDate(dia);
            showMsg("CAJA APERTURA PARA EL DIA DE HOY:" + dayName);
            setVisible(false);
        }
        catch(Throwable ex){
            showMsgError(ex);
        }
    }//GEN-LAST:event_jButtonGuardarActionPerformed

    private void jButtonCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCerrarActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButtonCerrarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
        verificarCajaAperturada();
        
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel dayName;
    private javax.swing.JButton jButtonCerrar;
    private javax.swing.JButton jButtonGuardar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTAObs;
    private javax.swing.JFormattedTextField jTFFecha;
    private javax.swing.JTextField jTFSaldoAnterior;
    // End of variables declaration//GEN-END:variables
}
