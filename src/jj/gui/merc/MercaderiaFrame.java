/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui.merc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import jj.controller.ArticulosJpaController;
import jj.controller.CategoriasJpaController;
import jj.entity.Categorias;
import jj.gui.BaseFrame;
import jj.gui.FarmaAppMain;
import jj.util.ArrayUtil;
import jj.util.FilaArticulo;
import jj.util.IVACheckBoxEditor;
import jj.util.IVAComboBoxEditor;
import jj.util.IVAComboBoxRenderer;
import jj.util.IvaCheckBoxRenderer;
import jj.util.datamodels.CatRow;
import jj.util.datamodels.CatsListModel;
import jj.util.datamodels.MercaderiaDataModel;
import jj.util.datamodels.TotalesMercaderia;

/**
 *
 * @author manuel.japon
 */
public class MercaderiaFrame extends BaseFrame implements ParentNewArtFrame {
    
    private MercaderiaDataModel mercaderiaDataModel;    
    private ArticulosJpaController articulosController;
    private CategoriasJpaController catsController;
    private List<CatRow> catsList;
    private CatsListModel catsModelList;
    private Integer selectedCatIndex;    
    /**
     * Creates new form MercaderiaFrame
     */
    public MercaderiaFrame() {
        super();
        initComponents();
        
        articulosController = new ArticulosJpaController(em);
        catsController = new CategoriasJpaController(em);
        mercaderiaDataModel = new MercaderiaDataModel();
        mercaderiaDataModel.setController(articulosController);
        
        jTableArts.setModel(mercaderiaDataModel);
                
        jTableArts.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTableArts.columnAtPoint(e.getPoint());
                try{
                    for (int i=0; i<mercaderiaDataModel.getColumnCount();i++){
                        jTableArts.getColumnModel().getColumn(i).setHeaderValue( mercaderiaDataModel.getColumnName(i) );
                    }                    
                    mercaderiaDataModel.switchSortColumn(col);
                }
                catch(Throwable ex){
                    showMsgError(ex);
                }
            }
        });
        
        jTableArts.getModel().addTableModelListener( new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                System.out.println("Listening tableChanged event-->");
                updateTotales();
            }
        } );
        
        jTableArts.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getFirstIndex()>=0){
                    btnBorrar.setEnabled(true);
                    jMoverBtn.setEnabled(true);
                }
                else{
                    btnBorrar.setEnabled(false);
                    jMoverBtn.setEnabled(false);
                }
            }
        });        
        
        reloadArts();
        
        jTableArts.updateUI();
        mercaderiaDataModel.fireTableDataChanged();
        
        jListCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);        
        jListCategorias.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    e.getFirstIndex();
                    String selectedValue = jListCategorias.getSelectedValue();                    
                }
            }
        });
        
        jListCategorias.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 1) {
                    try{                        
                        int index = list.locationToIndex(evt.getPoint());
                        selectedCatIndex =  index;
                        CatRow selectedCat = catsList.get(index);
                        jEditCatBtn.setEnabled(selectedCat.getCatId()>0);
                        mercaderiaDataModel.loadFromDataBaseCat(selectedCat.getCatId());
                        updateTotales();
                        updateLabelBorder();
                    }
                    catch(Throwable ex){
                        showMsgError(ex);
                    }                   
                }
            }
        });
        
        //JTableFooter        
        loadCats();
    }
    
    public void updateTotales(){
        jTableFooter.setEnabled(false);        
        /*
        "Nro", //0 artId
        "Codbar",//1 artCodbar
        "Articulo",//2  artNombre
        "Prec. Compra sin Iva",//3   artPrecioCompra
        "Prec. Venta",//4  artPrecio
        "Precio Mínimo",//5  artPreciomin
        "IVA",//6   artIva
        "Inventario",//7    artInv
        "Categoría"//8    
        */
        TotalesMercaderia totalesMerc = mercaderiaDataModel.getTotales();
        jTableFooter.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"TOTALES", 
                    "", 
                    "", 
                    totalesMerc.getSumaPrecioCompra(),
                    totalesMerc.getSumaPrecioVenta(),
                    totalesMerc.getSumaPrecioVentaMin(),
                    "",
                    totalesMerc.getSumaInv(),
                    ""}
            },
            new String [] {
                "", "", "", "PRECIO COMPRA","PRECIO VENTA","PRECIO VENTA MIN","","INVENTARIO",""
            }
        ));
    }
    
    public void reloadArts(){
        try{
            mercaderiaDataModel.loadFromDataBase();            
            updateTotales();
            btnBorrar.setEnabled(false);
            jMoverBtn.setEnabled(false);
        }
        catch(Throwable ex){
            showMsgError(ex);
        }
    }
    
    public void updateLabelEstado(String label){
        this.jStatusLabel.setText(label);
    }
    
    public void filtroFocus(){
        this.filtroTF.requestFocus();
    }
    
    public void updateLabelBorder(){
        if (mercaderiaDataModel.getItems() != null){
            jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(
                String.valueOf( "Nro Registros("+ mercaderiaDataModel.getItems().size() + ")" )
            ));
        }
    }
    
    public void updateArticulos(){
        try{
            mercaderiaDataModel.loadFromDataBase();
            updateTotales();
            updateLabelBorder();
        }
        catch(Throwable ex){
            showMsgError(ex);
        }
        mercaderiaDataModel.fireTableDataChanged();
    }
    
    public void doFilter(){
        String filtro = this.filtroTF.getText().trim();
        if (filtro.length()>0){
            try{
                mercaderiaDataModel.loadFromDataBaseFilter(filtro);
                updateTotales();
                updateLabelBorder();
            }
            catch(Throwable ex){
                showMsgError(ex);
            }
        }
    }
    
    @Override
    public void afterSuccessfulSaved() {
        updateArticulos();
    }

    @Override
    public void afterClose() {
        //Do nothing
    }

    @Override
    public boolean validateNewRow(FilaArticulo filaArt) {
        return mercaderiaDataModel.validar(filaArt);
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
        jPanel6 = new javax.swing.JPanel();
        jEditCatBtn = new javax.swing.JButton();
        jCrearCatBtn = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListCategorias = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        filtroTF = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableArts = new javax.swing.JTable();
        jTableFooter = new javax.swing.JTable();
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
        jCrearArtBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCrearArtBtnActionPerformed(evt);
            }
        });
        jPanelNorth.add(jCrearArtBtn, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelCenter.setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(150, 504));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.BorderLayout());

        jLabel2.setText("Categorías");
        jPanel4.add(jLabel2, java.awt.BorderLayout.CENTER);

        jPanel6.setMinimumSize(new java.awt.Dimension(80, 33));
        jPanel6.setPreferredSize(new java.awt.Dimension(80, 33));
        jPanel6.setLayout(new java.awt.GridLayout(1, 2));

        jEditCatBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/edit_25px.png"))); // NOI18N
        jEditCatBtn.setToolTipText("Crear nueva categoría");
        jEditCatBtn.setEnabled(false);
        jEditCatBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEditCatBtnActionPerformed(evt);
            }
        });
        jPanel6.add(jEditCatBtn);

        jCrearCatBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/Plus_25px.png"))); // NOI18N
        jCrearCatBtn.setToolTipText("Crear nueva categoría");
        jCrearCatBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCrearCatBtnActionPerformed(evt);
            }
        });
        jPanel6.add(jCrearCatBtn);

        jPanel4.add(jPanel6, java.awt.BorderLayout.EAST);

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

        jPanel7.setLayout(new java.awt.BorderLayout());

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
        jTableArts.setRowHeight(25);
        jScrollPane2.setViewportView(jTableArts);

        jPanel7.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jTableFooter.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jTableFooter.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableFooter.setRowHeight(30);
        jPanel7.add(jTableFooter, java.awt.BorderLayout.PAGE_END);

        jPanel2.add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanelCenter.add(jPanel2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelBtns.setLayout(new java.awt.GridLayout(6, 1));

        jGuardarBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-save.png"))); // NOI18N
        jGuardarBtn.setText("Guardar");
        jGuardarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGuardarBtnActionPerformed(evt);
            }
        });
        jPanelBtns.add(jGuardarBtn);

        btnBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-trash.png"))); // NOI18N
        btnBorrar.setText("Borrar");
        btnBorrar.setEnabled(false);
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });
        jPanelBtns.add(btnBorrar);

        jMoverBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-change.png"))); // NOI18N
        jMoverBtn.setText("Mover");
        jMoverBtn.setEnabled(false);
        jMoverBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMoverBtnActionPerformed(evt);
            }
        });
        jPanelBtns.add(jMoverBtn);

        jCerrarBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-close_pane_filled.png"))); // NOI18N
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
        String nombrecat = JOptionPane.showInputDialog(this," Ingrese el nombre de la categoría ");
        try{
            if (nombrecat != null){
                if (nombrecat.trim().length()>0){
                    jStatusLabel.setText("");
                    catsController.crear(nombrecat.trim().toUpperCase());
                    jStatusLabel.setText("Categoría creada");
                    loadCats();
                }
                else{
                    showMsg("Nombre incorrecto");
                }
            }
        }
        catch(Throwable ex){
            showMsgError(ex);
        }        
    }//GEN-LAST:event_jCrearCatBtnActionPerformed
    
    public void showEditCatName(Integer catIndex){
        try{
            CatRow selectedCat = catsList.get(catIndex);
            if (selectedCat.getCatId()>0){
                String currentName = selectedCat.getCatName().trim().toUpperCase();
                String newName = JOptionPane.showInputDialog("Ingrese el nuevo nombre de la categoría", currentName);
                if (newName!=null){
                    if (newName.trim().length()>0){
                        if (!newName.trim().toUpperCase().equalsIgnoreCase(currentName)){                        
                            catsController.actualizar(selectedCat.getCatId(), newName);                    
                            showMsg("Actualizado correctamente");
                            loadCats();
                        }
                    }
                    else{
                        showMsg("Nombre incorrecto");
                    }
                }
            }
        }
        catch(Throwable ex){
            showMsgError(ex);
        }
    }
    
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
    public void doDelete(){
        int[] rows = this.jTableArts.getSelectedRows();
        if (rows.length>0){
            int resp = JOptionPane.showConfirmDialog(null, "¿Seguro que desea borrar este(os) artículo(s)?");
            if (resp == JOptionPane.YES_OPTION){
                try{
                    for ( int row: rows ){
                        FilaArticulo filart = this.mercaderiaDataModel.getValueAt(row);
                        if (filart != null){
                            this.articulosController.destroy(filart.getArtId());
                        }
                    }                    
                    reloadArts();
                    
                    showMsg("Artículo(s) borrado");
                }
                catch(Throwable ex){
                    showMsgError(ex);
                }
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "Elija un articulo para borrar");
        }
    }
    
    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        doDelete();
    }//GEN-LAST:event_btnBorrarActionPerformed

    private void jMoverBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMoverBtnActionPerformed
        try{
            Stream stream = catsList.stream().filter(cat->cat.getCatId()>0).map(cat -> cat.getCatName());
            String[] catsArray = ArrayUtil.streamToStrArray(stream);

            String catSelected = (String)JOptionPane.showInputDialog(null, "Selecione la nueva categoría",
                "Cambio de categoría", JOptionPane.QUESTION_MESSAGE, null, catsArray, catsArray[0]);
            
            if (catSelected !=null){
                Optional<CatRow> catRowFinded = catsList.stream().filter(cat->cat.getCatName().equalsIgnoreCase(catSelected)).findFirst();
                if (catRowFinded.isPresent()){
                    CatRow newCatSelected = catRowFinded.get();
                    int[] rows = this.jTableArts.getSelectedRows();
                    for(int rowIndexSel: rows){
                        FilaArticulo filaArt = mercaderiaDataModel.getFila(rowIndexSel);
                        articulosController.cambiarCategoria(filaArt.getArtId(), newCatSelected.getCatId());
                    }
                }
                reloadArts();
                FarmaAppMain.showSystemTrayMsg("Operación Exitosa");
           }
        }
        catch(Throwable ex){
            showMsgError(ex);
        }
    }//GEN-LAST:event_jMoverBtnActionPerformed

    private void jGuardarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGuardarBtnActionPerformed
        mercaderiaDataModel.saveAllRecords();
    }//GEN-LAST:event_jGuardarBtnActionPerformed

    private void jEditCatBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEditCatBtnActionPerformed
        if (selectedCatIndex!=null){
            showEditCatName(selectedCatIndex);
        }
    }//GEN-LAST:event_jEditCatBtnActionPerformed

    private void jCrearArtBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCrearArtBtnActionPerformed
        NewArtFrame newArtFrame = new NewArtFrame(this);
        newArtFrame.centerOnScreen();
        newArtFrame.setVisible(true);
    }//GEN-LAST:event_jCrearArtBtnActionPerformed
    public void loadCats(){
        try{
            List<Categorias> auxCatsList = catsController.listar();
            catsList = catsController.parseCatList(auxCatsList);
            catsModelList = new CatsListModel();
            catsModelList.setItems(catsList);
            jListCategorias.setModel(catsModelList);
        }
        catch(Throwable ex){
            showMsgError(ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBorrar;
    private javax.swing.JTextField filtroTF;
    private javax.swing.JButton jCerrarBtn;
    private javax.swing.JButton jCrearArtBtn;
    private javax.swing.JButton jCrearCatBtn;
    private javax.swing.JButton jEditCatBtn;
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
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanelBtns;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel jStatusLabel;
    private javax.swing.JTable jTableArts;
    private javax.swing.JTable jTableFooter;
    // End of variables declaration//GEN-END:variables

   
}
