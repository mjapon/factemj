/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import jj.controller.ArticulosJpaController;
import jj.controller.CategoriasJpaController;
import jj.entity.Categorias;
import jj.util.ArticulosModelListener;
import jj.util.EntityManagerUtil;
import jj.util.IVAComboBoxEditor;
import jj.util.IVAComboBoxRenderer;
import jj.util.datamodels.ArticulosDataModel;
import jj.util.datamodels.CatsListModel;

/**
 *
 * @author manuel.japon
 */
public class MercaderiaFrame extends javax.swing.JFrame {
    
    private ArticulosDataModel articulosDataModel;
    private ArticulosModelListener articulosModelListener;
    private EntityManager em;
    private ArticulosJpaController articulosController;
    private CategoriasJpaController catsController;
    private List<Categorias> catsList;
    private CatsListModel catsModelList;
    private JFrame root;

    /**
     * Creates new form MercaderiaFrame
     */
    public MercaderiaFrame() {
        initComponents();
        this.em = EntityManagerUtil.createEntintyManagerFactory();
        articulosController = new ArticulosJpaController(em);
        catsController = new CategoriasJpaController(em);
        
        boolean forSelect = false;
        
        articulosDataModel = new ArticulosDataModel(1);
        articulosDataModel.setController(articulosController);
        //articulosDataModel.setArticulosFrame(this);
        
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
        
        jListCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        jListCategorias.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedValue = jListCategorias.getSelectedValue();                    
                    System.out.println("valueChangedEvent:"+selectedValue);
                }
            }
        });
        
        loadCats();
        //tfCodBarra.requestFocus();        
    }
    
    public void updateLabelEstado(String label){
        this.jStatusLabel.setText(label);
    }
    
    public void filtroFocus(){
        
        this.filtroTF.requestFocus();
        
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
    
    public void doFilter(){
        //System.out.println("Filtro key pressed--->");        
        //Se debe filtrar todos los articulos del model                
        String filtro = this.filtroTF.getText().trim();
        if (filtro.length()>0){
            //System.out.println("Se aplica el filtro:"+filtro);
            try{
                this.articulosDataModel.loadFromDataBaseFilter(filtro);
                if (this.articulosDataModel.getItems() != null){
                    jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelNorth = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jCrearArtBtn = new javax.swing.JButton();
        jPanelCenter = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jCrearCatBtn = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListCategorias = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        filtroTF = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableArts = new javax.swing.JTable();
        jPanelBtns = new javax.swing.JPanel();
        jGuardarBtn = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        jMoverBtn = new javax.swing.JButton();
        jCerrarBtn = new javax.swing.JButton();
        jPanelSouth = new javax.swing.JPanel();
        jStatusLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanelNorth.setPreferredSize(new java.awt.Dimension(100, 60));
        jPanelNorth.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setText("MERCADERÍA");
        jPanelNorth.add(jLabel1, java.awt.BorderLayout.CENTER);

        jCrearArtBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/Plus_48px.png"))); // NOI18N
        jCrearArtBtn.setText("Crear");
        jCrearArtBtn.setToolTipText("Crear nuevo artículo de venta");
        jPanelNorth.add(jCrearArtBtn, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelCenter.setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(150, 504));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.BorderLayout());

        jLabel2.setText("Categorías");
        jPanel4.add(jLabel2, java.awt.BorderLayout.CENTER);

        jCrearCatBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/Plus_25px.png"))); // NOI18N
        jCrearCatBtn.setToolTipText("Crear nueva categoría");
        jCrearCatBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCrearCatBtnActionPerformed(evt);
            }
        });
        jPanel4.add(jCrearCatBtn, java.awt.BorderLayout.EAST);

        jPanel1.add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jListCategorias.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jScrollPane1.setViewportView(jListCategorias);

        jPanel5.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanelCenter.add(jPanel1, java.awt.BorderLayout.WEST);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        filtroTF.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        filtroTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtroTFActionPerformed(evt);
            }
        });
        filtroTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtroTFKeyReleased(evt);
            }
        });
        jPanel3.add(filtroTF, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel3, java.awt.BorderLayout.NORTH);

        jTableArts.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
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
        jScrollPane2.setViewportView(jTableArts);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanelCenter.add(jPanel2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelBtns.setLayout(new java.awt.GridLayout(6, 1));

        jGuardarBtn.setText("Guardar");
        jPanelBtns.add(jGuardarBtn);

        btnBorrar.setText("Borrar");
        jPanelBtns.add(btnBorrar);

        jMoverBtn.setText("Mover");
        jPanelBtns.add(jMoverBtn);

        jCerrarBtn.setText("Cerrar");
        jCerrarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCerrarBtnActionPerformed(evt);
            }
        });
        jPanelBtns.add(jCerrarBtn);

        getContentPane().add(jPanelBtns, java.awt.BorderLayout.EAST);

        jPanelSouth.setLayout(new java.awt.BorderLayout());
        jPanelSouth.add(jStatusLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCrearCatBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCrearCatBtnActionPerformed
        String nombrecat = JOptionPane.showInputDialog(this," Ingrese el nombre de la categoria ");
        try{
            if (nombrecat != null && nombrecat.trim().length()>0){
                jStatusLabel.setText("");
                catsController.crear(nombrecat.trim().toUpperCase());
                jStatusLabel.setText("Categoría creada");
                loadCats();
            }
        }
        catch(Throwable ex){
            showMsgError(ex);
        }        
    }//GEN-LAST:event_jCrearCatBtnActionPerformed

    private void jCerrarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCerrarBtnActionPerformed
        FarmaAppMain farmaApp = (FarmaAppMain)this.root;
        farmaApp.logicaClosePane(this.getClass().getName());
        
    }//GEN-LAST:event_jCerrarBtnActionPerformed

    private void filtroTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtroTFActionPerformed
        
        //doFilter();
        
    }//GEN-LAST:event_filtroTFActionPerformed

    private void filtroTFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filtroTFKeyReleased
        
        doFilter();
        
    }//GEN-LAST:event_filtroTFKeyReleased
    
    public void loadCats(){
        try{
            catsList = catsController.listar();
            catsModelList = new CatsListModel();
            catsModelList.setItems(catsList);
            jListCategorias.setModel(catsModelList);
        }
        catch(Throwable ex){
            showMsgError(ex);
        }
    }
    
    private void showMsgError(Throwable ex){
        System.out.println("Error:"+ex.getMessage());
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error:"+ex.getMessage());
        jStatusLabel.setText("Error:"+ex.getMessage());
    }
    
    public JFrame getRoot() {
           return root;
    }

    public void setRoot(JFrame root) {
         this.root = root;
    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBorrar;
    private javax.swing.JTextField filtroTF;
    private javax.swing.JButton jCerrarBtn;
    private javax.swing.JButton jCrearArtBtn;
    private javax.swing.JButton jCrearCatBtn;
    private javax.swing.JButton jGuardarBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList<String> jListCategorias;
    private javax.swing.JButton jMoverBtn;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelBtns;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel jStatusLabel;
    private javax.swing.JTable jTableArts;
    // End of variables declaration//GEN-END:variables
}
