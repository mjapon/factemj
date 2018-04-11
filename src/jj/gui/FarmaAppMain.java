/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jj.util.EstadoAPP;

/**
 *
 * @author manuel.japon
 */
public class FarmaAppMain extends javax.swing.JFrame {
    
    private SplashScreen splashScreen;
    
    public static Map<String, EstadoAPP> estadosApp;
    

    /**
     * Creates new form FarmaAppMain
     */
    public FarmaAppMain() {
        
        splashScreen = new SplashScreen();
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        splashScreen.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        
        splashScreen.setVisible(true);
        
        initComponents();        
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        
        initEstados();
        /*
        BufferedImage img = null;
        try {            
            URL resource = FarmaAppMain.class.getResource("MarcaVG.png");            
            img = ImageIO.read(resource);            
            ImageIcon imageIcon = new ImageIcon(img);
            setContentPane(new JLabel(imageIcon));           
        
        } catch (Throwable e) {
            System.out.println("Error al cargar la image-->");
            e.printStackTrace();
        }
        */                
        
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                
                String title = sourceTabbedPane.getTitleAt(index);
                if ("Facturar".equalsIgnoreCase(title)){                    
                    EstadoAPP estadoApp = estadosApp.get(FacturaVentaFrame.class.getName());
                    FacturaVentaFrame facturaFrame = (FacturaVentaFrame)estadoApp.getFrame();
                    //facturaFrame.focusBarCode();  
                    facturaFrame.focusFiltro();
                    facturaFrame.restoreDefaultsDividerLocation();
                    System.out.println(" facturaFrame.focusBarCode();---------> ");
                }      
                else if ("Administrar Artículos".equalsIgnoreCase(title)){
                    EstadoAPP estadoApp = estadosApp.get(ArticulosFrame.class.getName());
                    ArticulosFrame facturaFrame = (ArticulosFrame)estadoApp.getFrame();
                    facturaFrame.filtroFocus();
                    System.out.println(" Administrar Artículos focusBarCode();---------> ");
                }
                else if ("Cuentas X Cobrar".equalsIgnoreCase(title)){
                    EstadoAPP estadoApp = estadosApp.get(CuentasXCBPFrame.class.getName());
                    CuentasXCBPFrame facturaFrame = (CuentasXCBPFrame)estadoApp.getFrame();
                    facturaFrame.restoreDefaultsDividerLocation();
                    System.out.println(" Administrar Artículos focusBarCode();---------> ");
                }
                
                System.out.println("Tab changed to------>: " + sourceTabbedPane.getTitleAt(index));                
            }
          };
        
        this.tabbedPaneMain.addChangeListener(changeListener);
        
        
        splashScreen.setVisible(false);
        
    
    }
    
    public static void realoadArtsFactura(){
        EstadoAPP facturaEstadoApp = estadosApp.get(FacturaVentaFrame.class.getName());        
        FacturaVentaFrame facturaFrame = (FacturaVentaFrame)facturaEstadoApp.getFrame();
        facturaFrame.reloadArts();
    }
    
    
    public EstadoAPP createEstado(String className){
        
        ImageIcon icon = createImageIcon("iconpet.png");        
        
        if (FacturaVentaFrame.class.getName().equalsIgnoreCase(className)){
            //Facturacion
            FacturaVentaFrame newFacturaFrame = new FacturaVentaFrame(1);                
            newFacturaFrame.setRoot(this);
            EstadoAPP facturaEstadoAPP = new EstadoAPP(newFacturaFrame.getContentPane(), icon, "Facturar", newFacturaFrame);        
            
            return facturaEstadoAPP;
        }
        else if (ArticulosFrame.class.getName().equalsIgnoreCase(className)){
            
            //Inventario de articulos
            ArticulosFrame articulosFrame = new ArticulosFrame(); 
            articulosFrame.setRoot(this);
            EstadoAPP inventarioEstadoAPP = new EstadoAPP(articulosFrame.getContentPane(), icon, "Administrar Artículos", articulosFrame);
            
            return inventarioEstadoAPP;
        }
        else if (AdminVentasFrame.class.getName().equalsIgnoreCase(className)){
            
            //Administrar ventas
            AdminVentasFrame adminVentasFrame = new AdminVentasFrame();
            adminVentasFrame.setRoot(this);
            EstadoAPP adminVentasEstadoAPP = new EstadoAPP(adminVentasFrame.getContentPane(), icon, "Administrar Ventas", adminVentasFrame);                
        
            return adminVentasEstadoAPP;
            
        }
        else if (CuentasXCBPFrame.class.getName().equalsIgnoreCase(className)){
            CuentasXCBPFrame cBPFrame = new CuentasXCBPFrame();
            cBPFrame.setRoot(this);
            EstadoAPP adminCXCPAPP = new EstadoAPP(cBPFrame.getContentPane(), icon, "Cuentas X Cobrar", cBPFrame);                
        
            return adminCXCPAPP;            
        }
        
        return null;
        
    }
    
    public void initEstados(){
        estadosApp = new HashMap<>();
        estadosApp.put(FacturaVentaFrame.class.getName(), createEstado(FacturaVentaFrame.class.getName()));
        estadosApp.put(ArticulosFrame.class.getName(), createEstado(ArticulosFrame.class.getName()));
        estadosApp.put(AdminVentasFrame.class.getName(), createEstado(AdminVentasFrame.class.getName()));    
        estadosApp.put(CuentasXCBPFrame.class.getName(), createEstado(CuentasXCBPFrame.class.getName()));    
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPaneMain = new javax.swing.JTabbedPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        facturarMenuItem = new javax.swing.JMenuItem();
        adminMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        adminArtsMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        fileMenu1 = new javax.swing.JMenu();
        facturarMenuItem1 = new javax.swing.JMenuItem();
        fileMenu2 = new javax.swing.JMenu();
        facturarMenuItem2 = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SISTEMA DE FACTURACION JAPON");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().add(tabbedPaneMain, java.awt.BorderLayout.CENTER);

        menuBar.setBorderPainted(false);
        menuBar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        menuBar.setMargin(new java.awt.Insets(10, 10, 10, 10));

        fileMenu.setMnemonic('f');
        fileMenu.setText("Ventas");
        fileMenu.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        facturarMenuItem.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        facturarMenuItem.setMnemonic('o');
        facturarMenuItem.setText("Facturar");
        facturarMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                facturarMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(facturarMenuItem);

        adminMenuItem.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        adminMenuItem.setMnemonic('o');
        adminMenuItem.setText("Administrar");
        adminMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(adminMenuItem);

        exitMenuItem.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Salir");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        adminArtsMenu.setMnemonic('e');
        adminArtsMenu.setText("Inventarios");
        adminArtsMenu.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        cutMenuItem.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        cutMenuItem.setMnemonic('t');
        cutMenuItem.setText("Administrar");
        cutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutMenuItemActionPerformed(evt);
            }
        });
        adminArtsMenu.add(cutMenuItem);

        menuBar.add(adminArtsMenu);

        fileMenu1.setMnemonic('f');
        fileMenu1.setText("Cuentas x Cobrar");
        fileMenu1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        facturarMenuItem1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        facturarMenuItem1.setMnemonic('o');
        facturarMenuItem1.setText("Administrar");
        facturarMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                facturarMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu1.add(facturarMenuItem1);

        menuBar.add(fileMenu1);

        fileMenu2.setMnemonic('f');
        fileMenu2.setText("Cuentas x Pagar");
        fileMenu2.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        facturarMenuItem2.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        facturarMenuItem2.setMnemonic('o');
        facturarMenuItem2.setText("Administrar");
        facturarMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                facturarMenuItem2ActionPerformed(evt);
            }
        });
        fileMenu2.add(facturarMenuItem2);

        menuBar.add(fileMenu2);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Ayuda");
        helpMenu.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        contentMenuItem.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        contentMenuItem.setMnemonic('c');
        contentMenuItem.setText("Contents");
        helpMenu.add(contentMenuItem);

        aboutMenuItem.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        int response = JOptionPane.showConfirmDialog(null, "¿Seguro que desea salir? ");
        if (response == JOptionPane.YES_OPTION){
            System.exit(0);
        }
    }//GEN-LAST:event_exitMenuItemActionPerformed

    
    static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = FarmaAppMain.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    
    private void facturarMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_facturarMenuItemActionPerformed
                
        EstadoAPP estadoApp = estadosApp.get(FacturaVentaFrame.class.getName());
        
        if (estadoApp == null){
            estadoApp = createEstado(FacturaVentaFrame.class.getName());
            estadosApp.put(FacturaVentaFrame.class.getName(), estadoApp);
        }
        
        logicaOpenPane(estadoApp);
        
    }//GEN-LAST:event_facturarMenuItemActionPerformed

    
    public void logicaOpenPane(EstadoAPP estadoAPP){
        //EstadoAPP estadoApp = estadosApp.get("INVENTARIOS");
        Integer indexOfComp =  tabbedPaneMain.indexOfComponent(estadoAPP.getPane());
        
        if (indexOfComp  == -1){
            tabbedPaneMain.addTab(estadoAPP.getName(), estadoAPP.getIcon(),  estadoAPP.getPane());
        }
        
        tabbedPaneMain.setSelectedComponent(estadoAPP.getPane());
        
    }
    
    public void logicaClosePane(String statusName){
        
        EstadoAPP estadoApp = estadosApp.get(statusName);
        if (estadoApp != null){
            logicaClosePane(estadoApp);
            estadosApp.put(statusName, null);
        }
        
    }
    
    public void logicaClosePane(EstadoAPP estadoAPP){
        
        Integer indexOfComp =  tabbedPaneMain.indexOfComponent(estadoAPP.getPane());
        
        if (indexOfComp  == -1){
            
        }
        else{
            tabbedPaneMain.remove(estadoAPP.getPane());          
            
        }                
    }
    
    
    
    
    private void cutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutMenuItemActionPerformed
        
        EstadoAPP estadoApp = estadosApp.get(ArticulosFrame.class.getName());        
        if (estadoApp == null){
            estadoApp = createEstado(ArticulosFrame.class.getName());
            estadosApp.put(ArticulosFrame.class.getName(), estadoApp);
        }
        
        logicaOpenPane(estadoApp);
        
        
    }//GEN-LAST:event_cutMenuItemActionPerformed

    private void adminMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminMenuItemActionPerformed
       
        EstadoAPP estadoApp = estadosApp.get(AdminVentasFrame.class.getName());        
        if (estadoApp == null){
            estadoApp = createEstado(AdminVentasFrame.class.getName());
            estadosApp.put(AdminVentasFrame.class.getName(), estadoApp);
        }

        logicaOpenPane(estadoApp);
        
        
    }//GEN-LAST:event_adminMenuItemActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try{
            URL resourceIco = FarmaAppMain.class.getResource("Icono_Farmaplus.png");
            ImageIcon imgIco = new ImageIcon(resourceIco);
            setIconImage(imgIco.getImage());
            System.out.println("seteado------------->");
        }
        catch(Throwable ex){
            System.out.println("Error al tratar de setear ico->");
        }        
    }//GEN-LAST:event_formWindowOpened

    private void facturarMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_facturarMenuItem1ActionPerformed
        
        EstadoAPP estadoApp = estadosApp.get(CuentasXCBPFrame.class.getName());        
        if (estadoApp == null){
            estadoApp = createEstado(CuentasXCBPFrame.class.getName());
            estadosApp.put(CuentasXCBPFrame.class.getName(), estadoApp);
        }

        logicaOpenPane(estadoApp);
        
        
    }//GEN-LAST:event_facturarMenuItem1ActionPerformed

    private void facturarMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_facturarMenuItem2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_facturarMenuItem2ActionPerformed

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
            java.util.logging.Logger.getLogger(FarmaAppMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FarmaAppMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FarmaAppMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FarmaAppMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try{
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                catch(Throwable ex){
                    System.out.println("Error al establecer look and fell:"+ ex.getMessage());
                    ex.printStackTrace();
                }
                 
                FarmaAppMain app = new FarmaAppMain();
                
                /*
                URL resourceIco = FarmaAppMain.class.getResource("Icono_Farmaplus.png"); 
                ImageIcon imgIco = new ImageIcon(resourceIco);
                app.setIconImage(imgIco.getImage());
                */
                
                app.setVisible(true);
            
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu adminArtsMenu;
    private javax.swing.JMenuItem adminMenuItem;
    private javax.swing.JMenuItem contentMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenuItem facturarMenuItem;
    private javax.swing.JMenuItem facturarMenuItem1;
    private javax.swing.JMenuItem facturarMenuItem2;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu fileMenu1;
    private javax.swing.JMenu fileMenu2;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTabbedPane tabbedPaneMain;
    // End of variables declaration//GEN-END:variables
    
    //private AdminVentasFrame adminVentasFrame;
    //private FacturaVentaFrame facturaVentaFrame;
    //private ArticulosFrame articulosFrame;
    
}
