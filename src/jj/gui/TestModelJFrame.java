/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jj.controller.ArticulosJpaController;
import jj.util.datamodels.rows.FilaArticulo;
import jj.util.NumbersUtil;
import jj.util.datamodels.DefGSVCol;
import jj.util.datamodels.JTableColumn;
import jj.util.datamodels.PruebaDataModel;
import jj.util.datamodels.TableHeadMouseAdapter;

/**
 *
 * @author manuel.japon
 */
public class TestModelJFrame extends BaseFrame {

    private ArticulosJpaController articulosController;
    private PruebaDataModel pruebaDataModel;
    
    public TestModelJFrame() {
        initComponents();
        
        articulosController = new ArticulosJpaController(em);
        
        List<JTableColumn> columns = new ArrayList<>();
                
        columns.add(
                new JTableColumn<FilaArticulo>(
                        "ID", 
                        0, 
                        "art_id", 
                        Integer.class,
                        new DefGSVCol<FilaArticulo>() {
                            public Object getValueAt(FilaArticulo row, int rowIndex) {
                                return row.getArtId();
                            }
                        }
                )
        );
        columns.add(
                new JTableColumn<FilaArticulo>(
                        "Nro", 
                        1, 
                        "art_codbar", 
                        String.class,
                        new DefGSVCol<FilaArticulo>() {
                            public Object getValueAt(FilaArticulo row, int rowIndex) {
                                return row.getCodBarra();
                            }
                        }
                )
        );
        
        columns.add(
                new JTableColumn<FilaArticulo>(
                        "Articulo", 
                        2, 
                        "art_nombre", 
                        String.class,
                        new DefGSVCol<FilaArticulo>() {
                            public Object getValueAt(FilaArticulo row, int rowIndex) {
                                return row.getNombre();
                            }
                        }
                )
        );
        
        columns.add(
                new JTableColumn<FilaArticulo>(
                        "Prec. Compra sin Iva", 
                        3, 
                        "art_preciocompra", 
                        BigDecimal.class,
                        new DefGSVCol<FilaArticulo>() {
                            public Object getValueAt(FilaArticulo row, int rowIndex) {
                                return NumbersUtil.round2(row.getPrecioCompraSinIva());
                            }
                        }
                )
        );
        
        columns.add(
                new JTableColumn<FilaArticulo>(
                        "Prec. Venta", 
                        4, 
                        "art_precio", 
                        BigDecimal.class,
                        new DefGSVCol<FilaArticulo>() {
                            public Object getValueAt(FilaArticulo row, int rowIndex) {
                                return NumbersUtil.round2(row.getPrecioVenta());
                            }
                        }
                )
        );
        
        columns.add(
                new JTableColumn<FilaArticulo>(
                        "Precio Mínimo", 
                        5, 
                        "art_preciomin", 
                        BigDecimal.class,
                        new DefGSVCol<FilaArticulo>() {
                            public Object getValueAt(FilaArticulo row, int rowIndex) {
                                return NumbersUtil.round2(row.getPrecioMin());
                            }
                        }
                )
        );
        
        columns.add(
                new JTableColumn<FilaArticulo>(
                        "IVA", 
                        6, 
                        "art_iva", 
                        Boolean.class,
                        new DefGSVCol<FilaArticulo>() {
                            public Boolean getValueAt(FilaArticulo row, int rowIndex) {
                                return row.isIva();
                            }                            
                            public boolean isCellEditable(FilaArticulo row) {
                                return true;
                            }
                        }
                )
        );
        
        columns.add(
                new JTableColumn<FilaArticulo>(
                        "Inventario", 
                        7, 
                        "art_inv", 
                        BigDecimal.class,
                        new DefGSVCol<FilaArticulo>() {
                            public Object getValueAt(FilaArticulo row, int rowIndex) {
                                return NumbersUtil.round2(row.getInventario());
                            }
                        }
                )
        );
        
        columns.add(
                new JTableColumn<FilaArticulo>(
                        "Categoría", 
                        8, 
                        "cat_name", 
                        String.class,
                        new DefGSVCol<FilaArticulo>() {
                            public Object getValueAt(FilaArticulo row, int rowIndex) {
                                return row.getCategoria();
                            }
                        }
                )
        );
        
        
        pruebaDataModel = new PruebaDataModel(columns, articulosController);
        pruebaDataModel.loadFromDataBase();
        pruebaDataModel.fireTableDataChanged();
        
        jTable1.setModel(pruebaDataModel);        
        jTable1.getTableHeader().addMouseListener(new TableHeadMouseAdapter(jTable1, pruebaDataModel));
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());

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
        jScrollPane1.setViewportView(jTable1);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jLabel1.setText("prueba");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TestModelJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TestModelJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TestModelJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TestModelJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestModelJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
