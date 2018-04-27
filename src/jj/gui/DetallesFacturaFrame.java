/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import jj.controller.CtesJpaController;
import jj.controller.FacturasJpaController;
import jj.entity.Facturas;
import jj.util.DatosCabeceraFactura;
import jj.util.EntityManagerUtil;
import jj.util.FechasUtil;
import jj.util.FilaFactura;
import jj.util.NumbersUtil;
import jj.util.PrintFactUtil;
import jj.util.TotalesFactura;
import jj.util.datamodels.DetallesFactDataModel;
/**
 *
 * @author mjapon
 */
public class DetallesFacturaFrame extends javax.swing.JFrame {

    private Integer factId;
    private EntityManager em;
    private final FacturasJpaController facturaController;
    private DetallesFactDataModel dataModel;
    private Facturas factura;
    private Integer tra_codigo;
    
    private final CtesJpaController ctesController;
    
    /**
     * Creates new form DetallesFacturaFrame
     */
    public DetallesFacturaFrame(Integer factId) {
        initComponents();
        this.factId = factId;
        em = EntityManagerUtil.createEntintyManagerFactory();
        facturaController = new FacturasJpaController(em);
        ctesController = new CtesJpaController(em);   
        
        try{
            this.tra_codigo = facturaController.getTraCodigo(factId);
        }catch(Throwable ex){
            System.out.println("Error:"+ex.getMessage());
            ex.printStackTrace();
        }
        dataModel = new DetallesFactDataModel(this.tra_codigo);
        List<Object[]> detalles = facturaController.listarDetalles(factId);
        factura = facturaController.buscar(factId);
        
        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("NUM ARTÍCULOS:"+detalles.size()));
        
        this.jLabel1.setText(" Detalles del Comprobante Nro: "+ factura.getFactNum());
        this.labelFecha.setText(FechasUtil.format(factura.getFactFecha()));
        this.labelNro.setText( factura.getFactNum() );
        this.labelSubtotal.setText( factura.getFactSubt().toPlainString() );
        this.labelDescuento.setText( factura.getFactDesc().toPlainString() );
        this.labelIva.setText( factura.getFactIva().toPlainString() );
        this.labelTotal.setText( factura.getFactTotal().toPlainString() );
        this.labelCliente.setText( factura.getCliId().getCliNombres() );
        this.labelCiruc.setText( factura.getCliId().getCliCi() );
        this.labelDireccion.setText( factura.getCliId().getCliDir() );
        this.labelTelf.setText( factura.getCliId().getCliTelf() );
        this.labelEmail.setText( factura.getCliId().getCliEmail() );
        
        dataModel.loadItems(detalles);
        
        jTable1.setModel(dataModel); 
        dataModel.setJtable(jTable1);
        
        dataModel.fireTableDataChanged(); 
        
        List<Object[]> pagosList  = facturaController.getPagos(factId);
        
        efectivoValLbl.setText("0.0");
        creditoValLbl.setText("0.0");
        
        if (pagosList != null){
            for (Object[] filaPago: pagosList){
                BigDecimal monto = (BigDecimal)filaPago[1]; 
                BigDecimal saldo = (BigDecimal)filaPago[2]; 
                //Integer spId =  (Integer)filaPago[4];
                Integer fpId =  (Integer)filaPago[5];
                String observ  = (String)filaPago[3];
                String estadoPago = (String)filaPago[7];

                if (fpId == 1){
                    efectivoValLbl.setText( NumbersUtil.round(monto, 2).toPlainString() );
                }
                else if (fpId == 2){
                   
                    creditoValLbl.setText(NumbersUtil.round(monto, 2).toPlainString());
                    
                    String texto = NumbersUtil.round(saldo, 2).toPlainString()+" "+ estadoPago;
                    /*
                    if (saldo.compareTo(BigDecimal.ZERO)>0){
                        
                    }
                    else{
                        
                    }
                    */
                    saldoValLbl.setText( texto );
                    jTextAreaObs.setText(observ);
                }
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
        jButtonPrint = new javax.swing.JButton();
        jButtonSalir = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        labelFecha = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelNro = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        labelSubtotal = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        labelDescuento = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        labelIva = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        labelTotal = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        labelCliente = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        labelCiruc = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        labelDireccion = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        labelTelf = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        labelEmail = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        efectivoValLbl = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        creditoValLbl = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        saldoValLbl = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaObs = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new java.awt.GridLayout(1, 1));

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel1.setText("Detalles del Comprobante Nro:");
        jPanel1.add(jLabel1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.GridLayout(6, 1));

        jButtonPrint.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButtonPrint.setText("Imprimir");
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonPrint);

        jButtonSalir.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButtonSalir.setText("Cerrar");
        jButtonSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSalirActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonSalir);

        getContentPane().add(jPanel2, java.awt.BorderLayout.EAST);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel4.setLayout(new java.awt.GridLayout(1, 3));

        jPanel6.setLayout(new java.awt.GridLayout(6, 2));

        jLabel2.setText("Fecha:");
        jPanel6.add(jLabel2);

        labelFecha.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelFecha.setText("fechaValue");
        jPanel6.add(labelFecha);

        jLabel3.setText("Nro:");
        jPanel6.add(jLabel3);

        labelNro.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelNro.setText("nroValue");
        jPanel6.add(labelNro);

        jLabel4.setText("SubTotal:");
        jPanel6.add(jLabel4);

        labelSubtotal.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelSubtotal.setText("subtotalValue");
        jPanel6.add(labelSubtotal);

        jLabel5.setText("Descuento:");
        jPanel6.add(jLabel5);

        labelDescuento.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelDescuento.setText("descuentoValue");
        jPanel6.add(labelDescuento);

        jLabel6.setText("IVA:");
        jPanel6.add(jLabel6);

        labelIva.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelIva.setText("ivaValue");
        jPanel6.add(labelIva);

        jLabel7.setText("Total:");
        jPanel6.add(jLabel7);

        labelTotal.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelTotal.setText("totalValue");
        jPanel6.add(labelTotal);

        jPanel4.add(jPanel6);

        jPanel7.setLayout(new java.awt.GridLayout(5, 2));

        jLabel8.setText("Cliente:");
        jPanel7.add(jLabel8);

        labelCliente.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelCliente.setText("clienteValue");
        jPanel7.add(labelCliente);

        jLabel9.setText("CI/RUC:");
        jPanel7.add(jLabel9);

        labelCiruc.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelCiruc.setText("cirucValue");
        jPanel7.add(labelCiruc);

        jLabel10.setText("Dirección:");
        jPanel7.add(jLabel10);

        labelDireccion.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelDireccion.setText("direccionValue");
        jPanel7.add(labelDireccion);

        jLabel11.setText("Telf:");
        jPanel7.add(jLabel11);

        labelTelf.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelTelf.setText("telfValue");
        jPanel7.add(labelTelf);

        jLabel12.setText("Email:");
        jPanel7.add(jLabel12);

        labelEmail.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelEmail.setText("emailValue");
        jPanel7.add(labelEmail);

        jPanel4.add(jPanel7);

        jPanel9.setLayout(new java.awt.BorderLayout());

        jPanel10.setLayout(new java.awt.GridLayout(3, 2));

        jLabel13.setText("Efectivo:");
        jPanel10.add(jLabel13);

        efectivoValLbl.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jPanel10.add(efectivoValLbl);

        jLabel14.setText("Crédito:");
        jPanel10.add(jLabel14);

        creditoValLbl.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jPanel10.add(creditoValLbl);

        jLabel15.setText("Saldo:");
        jPanel10.add(jLabel15);

        saldoValLbl.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jPanel10.add(saldoValLbl);

        jPanel9.add(jPanel10, java.awt.BorderLayout.NORTH);

        jPanel11.setLayout(new java.awt.GridLayout(1, 1));

        jTextAreaObs.setColumns(20);
        jTextAreaObs.setRows(5);
        jTextAreaObs.setEnabled(false);
        jScrollPane2.setViewportView(jTextAreaObs);

        jPanel11.add(jScrollPane2);

        jPanel9.add(jPanel11, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel9);

        jPanel3.add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "NumElements", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N

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

        jPanel5.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel8.setLayout(new java.awt.GridLayout(1, 0));
        jPanel3.add(jPanel8, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSalirActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonSalirActionPerformed

    private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintActionPerformed
        
        System.out.println("Logica de impresion");        
        Map<String,Object> datosFactura = facturaController.getDetallesFactura(factId);
        
        DatosCabeceraFactura cabecera = (DatosCabeceraFactura)datosFactura.get("cabecera");
        TotalesFactura totales = (TotalesFactura)datosFactura.get("totales");
        List<FilaFactura> detalles = (List<FilaFactura>)datosFactura.get("detalles");
        
        PrintFactUtil.imprimir(ctesController, cabecera, totales, detalles);        
    }//GEN-LAST:event_jButtonPrintActionPerformed

  
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel creditoValLbl;
    private javax.swing.JLabel efectivoValLbl;
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JButton jButtonSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextAreaObs;
    private javax.swing.JLabel labelCiruc;
    private javax.swing.JLabel labelCliente;
    private javax.swing.JLabel labelDescuento;
    private javax.swing.JLabel labelDireccion;
    private javax.swing.JLabel labelEmail;
    private javax.swing.JLabel labelFecha;
    private javax.swing.JLabel labelIva;
    private javax.swing.JLabel labelNro;
    private javax.swing.JLabel labelSubtotal;
    private javax.swing.JLabel labelTelf;
    private javax.swing.JLabel labelTotal;
    private javax.swing.JLabel saldoValLbl;
    // End of variables declaration//GEN-END:variables
}
