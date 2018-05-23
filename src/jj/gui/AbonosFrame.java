/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.EntityManager;
import javax.swing.JOptionPane;
import jj.controller.MovtransaccJpaController;
import jj.util.EntityManagerUtil;
import jj.util.datamodels.rows.FilaCXCP;
import jj.util.NumbersUtil;
import jj.util.form.AbonoForm;

/**
 *
 * @author mjapon
 */
public class AbonosFrame extends javax.swing.JFrame {

    
    private FilaCXCP fila;
    private CuentasXCBPFrame parentFrame;
    private EntityManager em;
    private MovtransaccJpaController controller;
    private Integer tra_codigo;//3 cuentas x cobrar, 4-cuentas x pagar
    
    /**
     * Creates new form AbonosFrame
     */
    public AbonosFrame(FilaCXCP fila, Integer tra_codigo) {
        initComponents();
        this.tra_codigo = tra_codigo;
        this.fila = fila;        
        this.jTFValorPend.setText( NumbersUtil.round(fila.getDeuda(), 2).toPlainString() );
        this.jTFValorPend.setEditable(false);
        this.em = EntityManagerUtil.createEntintyManagerFactory();
        
        controller = new MovtransaccJpaController(em);
        
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
        jTFValorPend = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTFValorAbono = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaObs = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jButtonGuardar = new javax.swing.JButton();
        jButtonCerrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setText("REGISTRAR ABONO");
        jPanel1.add(jLabel1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setText("VALOR PENDIENTE:");

        jLabel3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel3.setText("MONTO DEL ABONO:");

        jLabel4.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel4.setText("OBSERVACIÓN:");

        jTextAreaObs.setColumns(20);
        jTextAreaObs.setRows(5);
        jScrollPane1.setViewportView(jTextAreaObs);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addGap(31, 31, 31)
                            .addComponent(jTFValorAbono, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(31, 31, 31)
                            .addComponent(jTFValorPend, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFValorPend, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTFValorAbono, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.GridLayout(4, 1));

        jButtonGuardar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButtonGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-save.png"))); // NOI18N
        jButtonGuardar.setText("Guardar");
        jButtonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGuardarActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonGuardar);

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

    private void jButtonCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCerrarActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonCerrarActionPerformed

    private void jButtonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGuardarActionPerformed
        
        try{            
            Integer pagoId = this.fila.getCodPago();        
            BigDecimal monto = new BigDecimal(this.jTFValorAbono.getText());
            String observacion = this.jTextAreaObs.getText();
            Integer factId = this.fila.getCodFactura();
            AbonoForm form = new AbonoForm(pagoId, monto, observacion, factId, this.tra_codigo);
        
            controller.guardarAbono(form);
            this.parentFrame.logicaBuscar();
            
            BigDecimal deuda = new BigDecimal(this.jTFValorPend.getText());
            
            if (monto.compareTo(deuda)==0){
                JOptionPane.showMessageDialog(null, "Abono Registrado, La Factura ha sido CANCELADA en su totalidad");
            }
            else{
                JOptionPane.showMessageDialog(null, "Abono Registrado, queda un saldo pendiente aún por cancelar");
            }            
            setVisible(false);
        }
        catch(Throwable ex){
            System.out.println("Error al tratar de registrar el abono:"+ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al registrar el abono:"+ex.getMessage());
        }
        
        
    }//GEN-LAST:event_jButtonGuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        jTFValorAbono.requestFocus();
        
    }//GEN-LAST:event_formWindowOpened

    public CuentasXCBPFrame getParentFrame() {
        return parentFrame;
    }

    public void setParentFrame(CuentasXCBPFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCerrar;
    private javax.swing.JButton jButtonGuardar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTFValorAbono;
    private javax.swing.JTextField jTFValorPend;
    private javax.swing.JTextArea jTextAreaObs;
    // End of variables declaration//GEN-END:variables
}
