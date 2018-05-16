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
import jj.gui.cajas.AdminCajasFrame;
import jj.gui.cajas.AperturaCajaFrame;
import jj.gui.cajas.CierreCajaFrame;
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
        initComponents();
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        initEstados();
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                
                String title = sourceTabbedPane.getTitleAt(index);
                if ("Factura de Venta".equalsIgnoreCase(title)){                    
                    EstadoAPP estadoApp = estadosApp.get(FacturaVentaFrame.class.getName()+"1");
                    FacturaVentaFrame facturaFrame = (FacturaVentaFrame)estadoApp.getFrame();
                    //facturaFrame.focusBarCode();  
                    facturaFrame.focusFiltro();
                    facturaFrame.restoreDefaultsDividerLocation();
                    facturaFrame.updateArticulos();
                    System.out.println(" facturaFrame.focusBarCode();---------> ");
                }
                else if ("Factura de Compra".equalsIgnoreCase(title)){
                    EstadoAPP estadoApp = estadosApp.get(FacturaVentaFrame.class.getName()+"2");
                    FacturaVentaFrame facturaFrame = (FacturaVentaFrame)estadoApp.getFrame();
                    //facturaFrame.focusBarCode();  
                    facturaFrame.focusFiltro();
                    facturaFrame.restoreDefaultsDividerLocation();
                    facturaFrame.updateArticulos();
                    System.out.println(" facturaFrame.focusBarCode();---------> ");
                }
                else if ("Administrar Artículos".equalsIgnoreCase(title)){
                    EstadoAPP estadoApp = estadosApp.get(ArticulosFrame.class.getName());
                    ArticulosFrame artsFrame = (ArticulosFrame)estadoApp.getFrame();
                    artsFrame.filtroFocus();
                    artsFrame.updateArticulos();
                    System.out.println(" Administrar Artículos focusBarCode();---------> ");
                }
                else if ("Administrar Productos".equalsIgnoreCase(title)){
                    EstadoAPP estadoApp = estadosApp.get(MercaderiaFrame.class.getName());
                    MercaderiaFrame mercsFrame = (MercaderiaFrame)estadoApp.getFrame();
                    mercsFrame.updateArticulos();
                    mercsFrame.filtroFocus();                    
                    System.out.println(" Administrar Productos focusBarCode();---------> ");
                }
                else if ("Cuentas X Cobrar".equalsIgnoreCase(title)){
                    EstadoAPP estadoApp = estadosApp.get(CuentasXCBPFrame.class.getName()+"3");
                    CuentasXCBPFrame facturaFrame = (CuentasXCBPFrame)estadoApp.getFrame();
                    facturaFrame.restoreDefaultsDividerLocation();
                    System.out.println(" Administrar Artículos focusBarCode();---------> ");
                }
                else if ("Cuentas X Pagar".equalsIgnoreCase(title)){
                    EstadoAPP estadoApp = estadosApp.get(CuentasXCBPFrame.class.getName()+"4");
                    CuentasXCBPFrame facturaFrame = (CuentasXCBPFrame)estadoApp.getFrame();
                    facturaFrame.restoreDefaultsDividerLocation();
                    System.out.println(" Administrar Artículos focusBarCode();---------> ");
                }
                
                System.out.println("Tab changed to------>: " + sourceTabbedPane.getTitleAt(index));                
            }
          };
        
        this.tabbedPaneMain.addChangeListener(changeListener);
    }    
    
    public EstadoAPP createEstado(String className, Integer tra_codigo){
        
        ImageIcon icon = createImageIcon("iconpet.png");        
        
        if (FacturaVentaFrame.class.getName().equalsIgnoreCase(className)){
            
            if (tra_codigo == 1){
                //Facturacion
                FacturaVentaFrame newFacturaFrame = new FacturaVentaFrame(1);                
                newFacturaFrame.setRoot(this);
                EstadoAPP facturaEstadoAPP = new EstadoAPP(newFacturaFrame.getContentPane(), icon, "Factura de Venta", newFacturaFrame);        

                return facturaEstadoAPP;
            }
            else if (tra_codigo == 2){                
                //Factura de compras
                FacturaVentaFrame newFacturaFrame = new FacturaVentaFrame(2);                
                newFacturaFrame.setRoot(this);
                EstadoAPP facturaEstadoAPP = new EstadoAPP(newFacturaFrame.getContentPane(), icon, "Factura de Compra", newFacturaFrame);        

                return facturaEstadoAPP;
            }            
        }
        else if (ArticulosFrame.class.getName().equalsIgnoreCase(className)){            
            //Inventario de articulos
            ArticulosFrame articulosFrame = new ArticulosFrame(); 
            articulosFrame.setRoot(this);
            EstadoAPP inventarioEstadoAPP = new EstadoAPP(articulosFrame.getContentPane(), icon, "Administrar Artículos", articulosFrame);
            
            return inventarioEstadoAPP;
        }
        else if (MercaderiaFrame.class.getName().equalsIgnoreCase(className)){
            MercaderiaFrame mercaderiaFrame = new MercaderiaFrame();
            mercaderiaFrame.setRoot(this);
            
            EstadoAPP mercaderiaEstadoAPP = new EstadoAPP(mercaderiaFrame.getContentPane(), icon, "Administrar Productos", mercaderiaFrame);
            return mercaderiaEstadoAPP;
        }
        else if (AdminVentasFrame.class.getName().equalsIgnoreCase(className)){
            if (tra_codigo == 1){                
                //Administrar ventas
                AdminVentasFrame adminVentasFrame = new AdminVentasFrame(1);//Ventas
                adminVentasFrame.setRoot(this);
                EstadoAPP adminVentasEstadoAPP = new EstadoAPP(adminVentasFrame.getContentPane(), icon, "Administrar Ventas", adminVentasFrame);                

                return adminVentasEstadoAPP;
            }
            else if (tra_codigo == 2){                
                //Administrar compras              
                AdminVentasFrame adminComprasFrame = new AdminVentasFrame(2);//Ventas
                adminComprasFrame.setRoot(this);
                EstadoAPP adminComprasEstadoAPP = new EstadoAPP(adminComprasFrame.getContentPane(), icon, "Administrar Compras", adminComprasFrame);                

                return adminComprasEstadoAPP;
            }
        }
        else if (CuentasXCBPFrame.class.getName().equalsIgnoreCase(className)){            
            if (tra_codigo == 3){                
                CuentasXCBPFrame cBPFrame = new CuentasXCBPFrame(3);
                cBPFrame.setRoot(this);
                EstadoAPP adminCXCPAPP = new EstadoAPP(cBPFrame.getContentPane(), icon, "Cuentas X Cobrar", cBPFrame);               

                return adminCXCPAPP;            
                
            }
            else if(tra_codigo == 4){                
                CuentasXCBPFrame cBPFrame = new CuentasXCBPFrame(4);
                cBPFrame.setRoot(this);
                EstadoAPP adminCXCPAPP = new EstadoAPP(cBPFrame.getContentPane(), icon, "Cuentas X Pagar", cBPFrame);                

                return adminCXCPAPP;           
            }
        }
        else if (MovsCajaFrame.class.getName().equalsIgnoreCase(className)){
            MovsCajaFrame movsCajaFrame = new MovsCajaFrame();
            movsCajaFrame.setRoot(this);
            EstadoAPP cajasAPP = new EstadoAPP(movsCajaFrame.getContentPane(), icon, "Movimientos Caja", movsCajaFrame);
            return cajasAPP;
        }
        else if (PlanCuentasFrame.class.getName().equalsIgnoreCase(className)){
            PlanCuentasFrame planCuentasFrame = new PlanCuentasFrame();
            planCuentasFrame.setRoot(this);
            EstadoAPP planCuentasAPP = new EstadoAPP(planCuentasFrame.getContentPane(), icon, "Plan Cuentas", planCuentasFrame);
            return planCuentasAPP;
        }
        else if (AdminCajasFrame.class.getName().equalsIgnoreCase(className)){
            AdminCajasFrame adminCajasFrame = new AdminCajasFrame();
            adminCajasFrame.setRoot(this);
            EstadoAPP planCuentasAPP = new EstadoAPP(adminCajasFrame.getContentPane(), icon, "Listado de Cajas", adminCajasFrame);
            return planCuentasAPP;
        }
        
        return null;
        
    }
    
    public void initEstados(){
        estadosApp = new HashMap<>();        
        estadosApp.put(ArticulosFrame.class.getName(), createEstado(ArticulosFrame.class.getName(), 0));
        
        estadosApp.put(FacturaVentaFrame.class.getName()+"1", createEstado(FacturaVentaFrame.class.getName(), 1));        
        estadosApp.put(AdminVentasFrame.class.getName()+"1", createEstado(AdminVentasFrame.class.getName(), 1));    
        estadosApp.put(CuentasXCBPFrame.class.getName()+"3", createEstado(CuentasXCBPFrame.class.getName(), 3));    
        
        
        estadosApp.put(FacturaVentaFrame.class.getName()+"2", createEstado(FacturaVentaFrame.class.getName(), 2));
        estadosApp.put(AdminVentasFrame.class.getName()+"2", createEstado(AdminVentasFrame.class.getName(), 2));
        estadosApp.put(CuentasXCBPFrame.class.getName()+"4", createEstado(CuentasXCBPFrame.class.getName(), 4));
        
        estadosApp.put(MovsCajaFrame.class.getName(), createEstado(MovsCajaFrame.class.getName(),0));        
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
        salirMI = new javax.swing.JMenu();
        exitMenuItem1 = new javax.swing.JMenuItem();
        fileMenu = new javax.swing.JMenu();
        facturarMenuItem = new javax.swing.JMenuItem();
        adminMenuItem = new javax.swing.JMenuItem();
        facturarMenuItem1 = new javax.swing.JMenuItem();
        fileMenu3 = new javax.swing.JMenu();
        regFacturaComprarMI = new javax.swing.JMenuItem();
        adminComprasMenuItem = new javax.swing.JMenuItem();
        facturarMenuItem2 = new javax.swing.JMenuItem();
        adminArtsMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        cutMenuItem1 = new javax.swing.JMenuItem();
        cajasMenu = new javax.swing.JMenu();
        movsCajaMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        contabMenu = new javax.swing.JMenu();
        planCuentasMenuItem = new javax.swing.JMenuItem();
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
        menuBar.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        menuBar.setMargin(new java.awt.Insets(10, 10, 10, 10));

        salirMI.setMnemonic('f');
        salirMI.setText("Sistema");
        salirMI.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        exitMenuItem1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        exitMenuItem1.setMnemonic('x');
        exitMenuItem1.setText("Salir");
        exitMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItem1ActionPerformed(evt);
            }
        });
        salirMI.add(exitMenuItem1);

        menuBar.add(salirMI);

        fileMenu.setMnemonic('f');
        fileMenu.setText("Ventas");
        fileMenu.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        facturarMenuItem.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        facturarMenuItem.setMnemonic('o');
        facturarMenuItem.setText("Facturar");
        facturarMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                facturarMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(facturarMenuItem);

        adminMenuItem.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        adminMenuItem.setMnemonic('o');
        adminMenuItem.setText("Administrar");
        adminMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(adminMenuItem);

        facturarMenuItem1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        facturarMenuItem1.setMnemonic('o');
        facturarMenuItem1.setText("Cuentas x cobrar");
        facturarMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                facturarMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(facturarMenuItem1);

        menuBar.add(fileMenu);

        fileMenu3.setMnemonic('f');
        fileMenu3.setText("Compras");
        fileMenu3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        regFacturaComprarMI.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        regFacturaComprarMI.setMnemonic('o');
        regFacturaComprarMI.setText("Registrar Factura");
        regFacturaComprarMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regFacturaComprarMIActionPerformed(evt);
            }
        });
        fileMenu3.add(regFacturaComprarMI);

        adminComprasMenuItem.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        adminComprasMenuItem.setMnemonic('o');
        adminComprasMenuItem.setText("Administrar");
        adminComprasMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminComprasMenuItemActionPerformed(evt);
            }
        });
        fileMenu3.add(adminComprasMenuItem);

        facturarMenuItem2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        facturarMenuItem2.setMnemonic('o');
        facturarMenuItem2.setText("Cuentas x Pagar");
        facturarMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                facturarMenuItem2ActionPerformed(evt);
            }
        });
        fileMenu3.add(facturarMenuItem2);

        menuBar.add(fileMenu3);

        adminArtsMenu.setMnemonic('e');
        adminArtsMenu.setText("Inventarios");
        adminArtsMenu.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        cutMenuItem.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cutMenuItem.setMnemonic('t');
        cutMenuItem.setText("Administrar");
        cutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutMenuItemActionPerformed(evt);
            }
        });
        adminArtsMenu.add(cutMenuItem);

        cutMenuItem1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cutMenuItem1.setMnemonic('t');
        cutMenuItem1.setText("A1dministrar");
        cutMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutMenuItem1ActionPerformed(evt);
            }
        });
        adminArtsMenu.add(cutMenuItem1);

        menuBar.add(adminArtsMenu);

        cajasMenu.setMnemonic('e');
        cajasMenu.setText("Cajas");
        cajasMenu.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        movsCajaMenuItem.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        movsCajaMenuItem.setMnemonic('t');
        movsCajaMenuItem.setText("Movimientos");
        movsCajaMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                movsCajaMenuItemActionPerformed(evt);
            }
        });
        cajasMenu.add(movsCajaMenuItem);

        jMenuItem1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jMenuItem1.setText("Apertura");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        cajasMenu.add(jMenuItem1);

        jMenuItem2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jMenuItem2.setText("Cierre");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        cajasMenu.add(jMenuItem2);

        jMenuItem3.setText("Administrar");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        cajasMenu.add(jMenuItem3);

        menuBar.add(cajasMenu);

        contabMenu.setMnemonic('e');
        contabMenu.setText("Contabilidad");
        contabMenu.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        planCuentasMenuItem.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        planCuentasMenuItem.setMnemonic('t');
        planCuentasMenuItem.setText("Plan Cuentas");
        planCuentasMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planCuentasMenuItemActionPerformed(evt);
            }
        });
        contabMenu.add(planCuentasMenuItem);

        menuBar.add(contabMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Ayuda");
        helpMenu.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        contentMenuItem.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        contentMenuItem.setMnemonic('c');
        contentMenuItem.setText("Contents");
        helpMenu.add(contentMenuItem);

        aboutMenuItem.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
                
        EstadoAPP estadoApp = estadosApp.get(FacturaVentaFrame.class.getName()+"1");
        if (estadoApp == null){
            estadoApp = createEstado(FacturaVentaFrame.class.getName(), 1);
            estadosApp.put(FacturaVentaFrame.class.getName()+"1", estadoApp);
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
            estadoApp = createEstado(ArticulosFrame.class.getName(), 0);
            estadosApp.put(ArticulosFrame.class.getName(), estadoApp);
        }
        
        logicaOpenPane(estadoApp);
        
        
    }//GEN-LAST:event_cutMenuItemActionPerformed

    private void adminMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminMenuItemActionPerformed
       
        EstadoAPP estadoApp = estadosApp.get(AdminVentasFrame.class.getName()+"1");        
        if (estadoApp == null){
            estadoApp = createEstado(AdminVentasFrame.class.getName(), 1);
            estadosApp.put(AdminVentasFrame.class.getName()+"1", estadoApp);
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
        
        EstadoAPP estadoApp = estadosApp.get(CuentasXCBPFrame.class.getName()+"3");        
        if (estadoApp == null){
            estadoApp = createEstado(CuentasXCBPFrame.class.getName(), 3);
            estadosApp.put(CuentasXCBPFrame.class.getName()+"3", estadoApp);
        }

        logicaOpenPane(estadoApp);
        
        
    }//GEN-LAST:event_facturarMenuItem1ActionPerformed

    private void facturarMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_facturarMenuItem2ActionPerformed
        
         EstadoAPP estadoApp = estadosApp.get(CuentasXCBPFrame.class.getName()+"4");        
        if (estadoApp == null){
            estadoApp = createEstado(CuentasXCBPFrame.class.getName(), 4);
            estadosApp.put(CuentasXCBPFrame.class.getName()+"4", estadoApp);
        }

        logicaOpenPane(estadoApp);
        
    }//GEN-LAST:event_facturarMenuItem2ActionPerformed

    private void regFacturaComprarMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regFacturaComprarMIActionPerformed
        
        EstadoAPP estadoApp = estadosApp.get(FacturaVentaFrame.class.getName()+"2");
        
        if (estadoApp == null){
            estadoApp = createEstado(FacturaVentaFrame.class.getName(), 2);
            estadosApp.put(FacturaVentaFrame.class.getName()+"2", estadoApp);
        }
        
        logicaOpenPane(estadoApp);
        
    }//GEN-LAST:event_regFacturaComprarMIActionPerformed

    private void adminComprasMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminComprasMenuItemActionPerformed
        EstadoAPP estadoApp = estadosApp.get(AdminVentasFrame.class.getName()+"2");        
        if (estadoApp == null){
            estadoApp = createEstado(AdminVentasFrame.class.getName(), 2);
            estadosApp.put(AdminVentasFrame.class.getName()+"2", estadoApp);
        }

        logicaOpenPane(estadoApp);
        
    }//GEN-LAST:event_adminComprasMenuItemActionPerformed

    private void exitMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItem1ActionPerformed
        
        int response = JOptionPane.showConfirmDialog(null, "¿Seguro que desea salir? ");
        if (response == JOptionPane.YES_OPTION){
            System.exit(0);
        }
        
    }//GEN-LAST:event_exitMenuItem1ActionPerformed

    private void movsCajaMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_movsCajaMenuItemActionPerformed
        EstadoAPP estadoApp = estadosApp.get(MovsCajaFrame.class.getName());
        if (estadoApp == null){
            estadoApp = createEstado(MovsCajaFrame.class.getName(), 0);
            estadosApp.put(MovsCajaFrame.class.getName(), estadoApp);
        }
        logicaOpenPane(estadoApp);        
    }//GEN-LAST:event_movsCajaMenuItemActionPerformed

    private void planCuentasMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planCuentasMenuItemActionPerformed
        
        EstadoAPP estadoApp = estadosApp.get(PlanCuentasFrame.class.getName());
        if (estadoApp == null){
            estadoApp = createEstado(PlanCuentasFrame.class.getName(), 0);
            estadosApp.put(PlanCuentasFrame.class.getName(), estadoApp);
        }
        logicaOpenPane(estadoApp);        
        
        
    }//GEN-LAST:event_planCuentasMenuItemActionPerformed

    private void cutMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutMenuItem1ActionPerformed
        
        EstadoAPP estadoApp = estadosApp.get(MercaderiaFrame.class.getName());        
        if (estadoApp == null){
            estadoApp = createEstado(MercaderiaFrame.class.getName(), 0);
            estadosApp.put(MercaderiaFrame.class.getName(), estadoApp);
        }
        
        logicaOpenPane(estadoApp);
        
        
    }//GEN-LAST:event_cutMenuItem1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        
        AperturaCajaFrame    aperturaCajaFrame = new AperturaCajaFrame();
        aperturaCajaFrame.centerOnScreen();
        aperturaCajaFrame.setVisible(true);  
        
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        
        
         CierreCajaFrame cierreCajaFrame = new CierreCajaFrame();        
        cierreCajaFrame.centerOnScreen();
        cierreCajaFrame.setVisible(true);  
        
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        
        EstadoAPP estadoApp = estadosApp.get(AdminCajasFrame.class.getName());        
        if (estadoApp == null){
            estadoApp = createEstado(AdminCajasFrame.class.getName(), 0);
            estadosApp.put(AdminCajasFrame.class.getName(), estadoApp);
        }
        logicaOpenPane(estadoApp);
        
    }//GEN-LAST:event_jMenuItem3ActionPerformed
    
    public SplashScreen getSplashScreen() {
        return splashScreen;
    }

    public void setSplashScreen(SplashScreen splashScreen) {
        this.splashScreen = splashScreen;
    }
    
    public void hideSplashScreen(){
        this.splashScreen.setVisible(false);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.setSize(500, 450);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        splashScreen.setLocation(dim.width/2-splashScreen.getSize().width/2, dim.height/2-splashScreen.getSize().height/2);
        splashScreen.setVisible(true);                

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
                app.setSplashScreen(splashScreen);
                app.hideSplashScreen();
                app.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu adminArtsMenu;
    private javax.swing.JMenuItem adminComprasMenuItem;
    private javax.swing.JMenuItem adminMenuItem;
    private javax.swing.JMenu cajasMenu;
    private javax.swing.JMenu contabMenu;
    private javax.swing.JMenuItem contentMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem cutMenuItem1;
    private javax.swing.JMenuItem exitMenuItem1;
    private javax.swing.JMenuItem facturarMenuItem;
    private javax.swing.JMenuItem facturarMenuItem1;
    private javax.swing.JMenuItem facturarMenuItem2;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu fileMenu3;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem movsCajaMenuItem;
    private javax.swing.JMenuItem planCuentasMenuItem;
    private javax.swing.JMenuItem regFacturaComprarMI;
    private javax.swing.JMenu salirMI;
    private javax.swing.JTabbedPane tabbedPaneMain;
    // End of variables declaration//GEN-END:variables
    
    //private AdminVentasFrame adminVentasFrame;
    //private FacturaVentaFrame facturaVentaFrame;
    //private ArticulosFrame articulosFrame;
    
}
