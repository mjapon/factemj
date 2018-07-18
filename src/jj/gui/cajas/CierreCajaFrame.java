/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui.cajas;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.swing.JOptionPane;
import jj.controller.CajasJpaController;
import jj.controller.CtesJpaController;
import jj.controller.FacturasJpaController;
import jj.entity.Cajas;
import jj.gui.BaseFrame;
import jj.gui.facte.DetallesFacturaFrame;
import jj.util.FechasUtil;
import jj.util.datamodels.rows.FilaCXCP;
import jj.util.datamodels.rows.FilaVenta;
import jj.util.NumbersUtil;
import jj.util.ParamBusquedaCXCP;
import jj.util.ParamsBusquedaTransacc;
import jj.util.TotalesCuentasXPC;
import jj.util.datamodels.MovsCVDataModel;
import jj.util.datamodels.MovsCXCPDataModel;
import jj.util.datamodels.TotalesVentasModel;
import jj.util.datamodels.VentasDataModel;

/**
 *
 * @author mjapon
 */
public class CierreCajaFrame extends BaseFrame {
    
    private Cajas caja;
    private Date dia;
    private CajasJpaController cajasController;
    private CtesJpaController ctesCntrl;
    
    private MovsCVDataModel movsVentasDataModel;    
    private MovsCVDataModel movsComprasDataModel;    
    private MovsCXCPDataModel movsCXCDataModel;
    private MovsCXCPDataModel movsCXPDataModel;
    private FacturasJpaController facturaController;
    
    private BigDecimal totalVentas;
    private BigDecimal totalVentasChanca;
    private BigDecimal totalVentasSP;
    
    private BigDecimal abonosCobrados;
    private BigDecimal abonosCobradosChk;
    private BigDecimal abonosCobradosSP;
    
    private BigDecimal abonosPagados;
    private BigDecimal abonosPagadosChk;
    private BigDecimal abonosPagadosSP;
    
    private BigDecimal saldoInicial;
    private BigDecimal saldoInicialChanca;
    private BigDecimal saldoInicialSP;
    
    private BigDecimal saldoCaja;
    private BigDecimal saldoCajaChanca;
    private BigDecimal saldoCajaSP;
    
    private BigDecimal auxSaldoCaja;
    private BigDecimal auxSaldoCajaChanca;
    private BigDecimal auxSaldoCajaSP;
    
    /**
     * Creates new form CierreCajaFrame
     */
    public CierreCajaFrame() {
        super();
        initComponents();
        
        cajasController = new CajasJpaController(em);
        facturaController = new FacturasJpaController(em);
        ctesCntrl = new CtesJpaController(em);        
        
        movsVentasDataModel = new MovsCVDataModel();
        movsVentasDataModel.setController(facturaController);
        jTableVentas.setModel(movsVentasDataModel);        
        jTableVentas.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTableVentas.columnAtPoint(e.getPoint());
                try{
                    for (int i=0; i<movsVentasDataModel.getColumnCount();i++){
                        jTableVentas.getColumnModel().getColumn(i).setHeaderValue( movsVentasDataModel.getColumnName(i) );
                    }                    
                    movsVentasDataModel.switchSortColumn(col);                    
                }
                catch(Throwable ex){
                    JOptionPane.showMessageDialog(null, "Error en sort:"+ex.getMessage());
                    System.out.println("Error en sort:"+ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        jTableVentas.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    showDetallesFacturaFrame(1);
                }
            }
        });
        jTableVentas.updateUI();
        
         //Configuracion de movimientos de compra
        
        //Configuracion de cuentas x cobrar
        movsCXCDataModel = new MovsCXCPDataModel(3);
        movsCXCDataModel.setController(facturaController);
        jTableCxC.setModel(movsCXCDataModel);
        jTableCxC.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTableCxC.columnAtPoint(e.getPoint());
                try{
                    for (int i=0; i<movsCXCDataModel.getColumnCount();i++){
                        jTableCxC.getColumnModel().getColumn(i).setHeaderValue( movsCXCDataModel.getColumnName(i) );
                    }
                    movsCXCDataModel.switchSortColumn(col);
                }
                catch(Throwable ex){
                    JOptionPane.showMessageDialog(null, "Error en sort:"+ex.getMessage());
                    System.out.println("Error en sort:"+ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        jTableCxC.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    showDetallesFacturaFrame(3);
                }
            }
        });
        jTableCxC.updateUI();        
        
        //Configuracion de cuentas x pagar
        movsCXPDataModel = new MovsCXCPDataModel(4);
        movsCXPDataModel.setController(facturaController);
        jTableCxP.setModel(movsCXPDataModel);
        jTableCxP.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTableCxP.columnAtPoint(e.getPoint());
                try{
                    for (int i=0; i<movsCXPDataModel.getColumnCount();i++){
                        jTableCxP.getColumnModel().getColumn(i).setHeaderValue( movsCXPDataModel.getColumnName(i) );
                    }                    
                    movsCXPDataModel.switchSortColumn(col);
                }
                catch(Throwable ex){
                    JOptionPane.showMessageDialog(null, "Error en sort:"+ex.getMessage());
                    System.out.println("Error en sort:"+ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        jTableCxP.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    showDetallesFacturaFrame(4);
                }
            }
        });
        jTableCxP.updateUI();
        
        saldoInicial = BigDecimal.ZERO;
        saldoInicialChanca= BigDecimal.ZERO;
        saldoInicialSP= BigDecimal.ZERO;
        
        totalVentas = BigDecimal.ZERO;
        totalVentasChanca= BigDecimal.ZERO;
        totalVentasSP = BigDecimal.ZERO;
        
        abonosCobrados = BigDecimal.ZERO;
        abonosCobradosChk = BigDecimal.ZERO;
        abonosCobradosSP = BigDecimal.ZERO;        
        
        abonosPagados = BigDecimal.ZERO;
        abonosPagadosChk= BigDecimal.ZERO;
        abonosPagadosSP= BigDecimal.ZERO;        
        
        saldoCaja = BigDecimal.ZERO;
        saldoCajaChanca = BigDecimal.ZERO;
        saldoCajaSP = BigDecimal.ZERO;
        
        valAJSP = BigDecimal.ZERO;
        valAjChanca = BigDecimal.ZERO;
        valAjOtros = BigDecimal.ZERO;
        
        jTFAjChanca.setText(valAjChanca.toPlainString());
        jTFAjSP.setText(valAJSP.toPlainString());
        jTFAjOtros.setText(valAjOtros.toPlainString());
        
        this.jTFFechaApertura.setText( FechasUtil.getFechaActual() );
    }
    
    public void loadInfoCaja(){
        try{
            dia = FechasUtil.parse(jTFFechaApertura.getText());
            
            caja = null;
            boolean hayCajaAbierta = false;
            
            if (cajasController.existeCajaAbierta(dia)){
                caja = cajasController.getCajaDia(dia);
                hayCajaAbierta = true;
            }
            else{
                if (cajasController.existeCajaAbiertaMenorFecha(dia)){
                    caja = cajasController.getCajaAbiertaMenorFecha(dia);
                    hayCajaAbierta = true;
                }
                else{
                    hayCajaAbierta = false;
                }
            }
            
            if (!hayCajaAbierta){
                showMsg("No existe caja abierta");
            }
            else{
                this.jTFFechaApertura.setText( FechasUtil.formatDateHour(caja.getCjFecaper()) );
                saldoInicial = caja.getCjSaldoant()!=null?caja.getCjSaldoant():BigDecimal.ZERO;
                saldoInicialChanca = caja.getCjSaldoantchanca()!=null?caja.getCjSaldoantchanca():BigDecimal.ZERO;
                saldoInicialSP = caja.getCjSaldoantsp()!=null?caja.getCjSaldoantsp():BigDecimal.ZERO;

                this.jTFFechaApertura.setText( FechasUtil.formatDateHour(caja.getCjFecaper()) );
                this.jTFSaldoInicial.setText( NumbersUtil.round2(saldoInicial).toPlainString() );
                this.jTFSaldoInicialChanca.setText( NumbersUtil.round2(saldoInicialChanca).toPlainString() );
                this.jTFSaldoInicialSP.setText( NumbersUtil.round2(saldoInicialSP).toPlainString() );
                this.jTAObsApertura.setText( caja.getCjObsaper() );

                Date desde = caja.getCjFecaper();
                Date hasta = new Date();

                //Ventas
                ParamsBusquedaTransacc paramsVentas = new ParamsBusquedaTransacc();
                paramsVentas.setDesde(desde);
                paramsVentas.setHasta(hasta);
                paramsVentas.setTraCodigo(1);            
                paramsVentas.setArtId(0);
                paramsVentas.setCliId(0);
                paramsVentas.setUsarFechaHora(true);                
                
                Integer codCatChanca = Integer.valueOf(ctesCntrl.findValueByClave("COD_CAT_CHANCADO"));
                ParamsBusquedaTransacc paramsVentasChanca = new ParamsBusquedaTransacc();
                paramsVentasChanca.setDesde(desde);
                paramsVentasChanca.setHasta(hasta);
                paramsVentasChanca.setTraCodigo(1);            
                paramsVentasChanca.setArtId(codCatChanca);
                paramsVentasChanca.setCliId(0);
                paramsVentasChanca.setUsarFechaHora(true);
                paramsVentasChanca.setByCat(true);
                
                Integer codCatSP = Integer.valueOf(ctesCntrl.findValueByClave("COD_CAT_SP"));
                ParamsBusquedaTransacc paramsVentasSP = new ParamsBusquedaTransacc();
                paramsVentasSP.setDesde(desde);
                paramsVentasSP.setHasta(hasta);
                paramsVentasSP.setTraCodigo(1);            
                paramsVentasSP.setArtId(codCatSP);
                paramsVentasSP.setCliId(0);
                paramsVentasSP.setUsarFechaHora(true);
                paramsVentasSP.setByCat(true);
                
                //facturaController.listarByCat(paramsVentasChanca);
                VentasDataModel ventasChancado = new VentasDataModel();
                ventasChancado.setController(facturaController);
                ventasChancado.setParams(paramsVentasChanca);
                ventasChancado.loadFromDataBase();
                
                VentasDataModel ventasSP = new VentasDataModel();
                ventasSP.setController(facturaController);
                ventasSP.setParams(paramsVentasSP);
                ventasSP.loadFromDataBase();
                

                movsVentasDataModel.setParams(paramsVentas);
                movsVentasDataModel.loadFromDataBase();
                jTableVentas.updateUI();
                movsVentasDataModel.fireTableDataChanged();      
                jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ventas("+movsVentasDataModel.getItems().size() +")"  , javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N

                //Compras
                ParamsBusquedaTransacc paramsCompras = new ParamsBusquedaTransacc();
                paramsCompras.setDesde(desde);
                paramsCompras.setHasta(hasta);
                paramsCompras.setTraCodigo(2);
                paramsCompras.setArtId(0);
                paramsCompras.setCliId(0);

                /*
                movsComprasDataModel.setParams(paramsCompras);
                movsComprasDataModel.loadFromDataBase();
                jTableCompras.updateUI();
                movsComprasDataModel.fireTableDataChanged();            
                jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Compras("+movsComprasDataModel.getItems().size() +")" , javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N
                */

                //Cuentas x cobrar
                ParamBusquedaCXCP paramsCXC = new ParamBusquedaCXCP();
                paramsCXC.setDesde(desde);
                paramsCXC.setHasta(hasta);
                paramsCXC.setTra_codigo(3);
                paramsCXC.setArtId(0);
                paramsCXC.setCliId(0);
                movsCXCDataModel.setParams(paramsCXC);
                movsCXCDataModel.loadFromDataBase();
                
                //Cuentas x cobrar chancado
                MovsCXCPDataModel movsCXCChacaDM = new MovsCXCPDataModel(3);
                ParamBusquedaCXCP paramsCXCChanca = new ParamBusquedaCXCP();
                paramsCXCChanca.setDesde(desde);
                paramsCXCChanca.setHasta(hasta);
                paramsCXCChanca.setTra_codigo(3);
                paramsCXCChanca.setArtId(0);
                paramsCXCChanca.setCliId(0);
                paramsCXCChanca.setFindByCaja(true);
                paramsCXCChanca.setCajaId(1);
                movsCXCChacaDM.setParams(paramsCXCChanca);
                movsCXCChacaDM.setController(facturaController);
                movsCXCChacaDM.loadFromDataBase();
                
                MovsCXCPDataModel movsCXPChacaDM = new MovsCXCPDataModel(4);
                ParamBusquedaCXCP paramsCXPChanca = new ParamBusquedaCXCP();
                paramsCXPChanca.setDesde(desde);
                paramsCXPChanca.setHasta(hasta);
                paramsCXPChanca.setTra_codigo(4);
                paramsCXPChanca.setArtId(0);
                paramsCXPChanca.setCliId(0);
                paramsCXPChanca.setFindByCaja(true);
                paramsCXPChanca.setCajaId(1);
                movsCXPChacaDM.setController(facturaController);
                movsCXPChacaDM.setParams(paramsCXPChanca);
                movsCXPChacaDM.loadFromDataBase();
                
                
                
                jTableCxC.updateUI();
                movsCXCDataModel.fireTableDataChanged();
                jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Abonos Cobrados("+movsCXCDataModel.getItems().size() +")" , javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N


                //Cuentas x pagar
                ParamBusquedaCXCP paramsCXP = new ParamBusquedaCXCP();
                paramsCXP.setDesde(desde);
                paramsCXP.setHasta(hasta);
                paramsCXP.setTra_codigo(4);
                paramsCXP.setArtId(0);
                paramsCXP.setCliId(0);
                movsCXPDataModel.setParams(paramsCXP);
                movsCXPDataModel.loadFromDataBase();
                
                //CUENTAS POR PAGAR CHANCA, SERVICIOS PROF
                MovsCXCPDataModel movsCXCSPDM = new MovsCXCPDataModel(3);
                ParamBusquedaCXCP paramsCXCSP = new ParamBusquedaCXCP();
                paramsCXCSP.setDesde(desde);
                paramsCXCSP.setHasta(hasta);
                paramsCXCSP.setTra_codigo(3);
                paramsCXCSP.setArtId(0);
                paramsCXCSP.setCliId(0);
                paramsCXCSP.setFindByCaja(true);
                paramsCXCSP.setCajaId(2);
                movsCXCSPDM.setController(facturaController);
                movsCXCSPDM.setParams(paramsCXCSP);
                movsCXCSPDM.loadFromDataBase();
                
                MovsCXCPDataModel movsCXPSPDM = new MovsCXCPDataModel(4);
                ParamBusquedaCXCP paramsCXPSP = new ParamBusquedaCXCP();
                paramsCXPSP.setDesde(desde);
                paramsCXPSP.setHasta(hasta);
                paramsCXPSP.setTra_codigo(4);
                paramsCXPSP.setArtId(0);
                paramsCXPSP.setCliId(0);
                paramsCXPSP.setFindByCaja(true);
                paramsCXPSP.setCajaId(2);
                movsCXPSPDM.setController(facturaController);
                movsCXPSPDM.setParams(paramsCXPSP);
                movsCXPSPDM.loadFromDataBase();
                
                
                jTableCxP.updateUI();
                movsCXPDataModel.fireTableDataChanged();
                jPanelCxP.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Abonos Pagados("+movsCXPDataModel.getItems().size() +")" , javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N

                //Sumatora de ventas
                TotalesVentasModel totalesVentas =  movsVentasDataModel.getTotalesVentasModel();
                TotalesVentasModel totalesVentasChanca =  ventasChancado.getTotalesVentasModel();
                TotalesVentasModel totalesVentasSP =  ventasSP.getTotalesVentasModel();
                
                TotalesCuentasXPC totalesCXP = movsCXPDataModel.getTotalesFactura();
                TotalesCuentasXPC totalesCXC = movsCXCDataModel.getTotalesFactura();
                
                TotalesCuentasXPC totalesCXPChanca = movsCXPChacaDM.getTotalesFactura();
                TotalesCuentasXPC totalesCXPSP = movsCXPSPDM.getTotalesFactura();
                
                TotalesCuentasXPC totalesCXCChanca = movsCXCChacaDM.getTotalesFactura();
                TotalesCuentasXPC totalesCXCSP = movsCXCSPDM.getTotalesFactura();
                
                
                BigDecimal  auxTotalVentas = totalesVentas.getSumaEfectivo();
                
                totalVentasChanca = totalesVentasChanca.getSumaEfectivo();
                totalVentasSP = totalesVentasSP.getSumaEfectivo();
                
                BigDecimal auxAbonosCobrados = totalesCXC.getSumaMonto();
                BigDecimal auxAbonosPagados = totalesCXP.getSumaMonto();
                
                abonosCobradosChk = totalesCXCChanca.getSumaMonto();
                abonosPagadosChk = totalesCXPChanca.getSumaMonto();
                
                abonosCobradosSP = totalesCXCSP.getSumaMonto();
                abonosPagadosSP = totalesCXPSP.getSumaMonto();
                
                abonosCobrados = auxAbonosCobrados.subtract(abonosCobradosChk.add(abonosCobradosSP)); 
                abonosPagados = auxAbonosPagados.subtract(abonosPagadosChk.add(abonosPagadosSP));
                
                totalVentas = auxTotalVentas.subtract(totalVentasChanca.add(totalVentasSP));

                auxSaldoCaja =  saldoInicial.add(totalVentas).add(abonosCobrados).subtract(abonosPagados);
                auxSaldoCajaChanca = saldoInicialChanca.add(totalVentasChanca).add(abonosCobradosChk).subtract(abonosPagadosChk);
                auxSaldoCajaSP = saldoInicialSP.add(totalVentasSP).add(abonosCobradosSP).subtract(abonosPagadosSP);

                jTFVentas.setText( NumbersUtil.round2(totalVentas).toPlainString() );
                jTFVentasChancado.setText( NumbersUtil.round2(totalVentasChanca).toPlainString() );
                jTFVentasSP.setText(NumbersUtil.round2(totalVentasSP).toPlainString() );
                
                jTFCuentasXCobrar.setText( NumbersUtil.round2(abonosCobrados).toPlainString() );
                jTFCXCChanca.setText( NumbersUtil.round2(abonosCobradosChk).toPlainString() );
                jTFCXCSP.setText( NumbersUtil.round2(abonosCobradosSP).toPlainString() );                
                
                jTFCuentasXPagar.setText( NumbersUtil.round2(abonosPagados).toPlainString() );
                jTFCXPChanca.setText( NumbersUtil.round2(abonosPagadosChk).toPlainString() );
                jTFCXPSP.setText( NumbersUtil.round2(abonosPagadosSP).toPlainString() );                
                
                /*
                jTFSaldo.setText(NumbersUtil.round2(saldoCaja).toPlainString());
                jTFSaldoChanca.setText(NumbersUtil.round2(saldoCajaChanca).toPlainString());
                jTFSaldoSP.setText(NumbersUtil.round2(saldoCajaSP).toPlainString());
                */
                updateSaldoAjustes();

                jTFTotalVGrid.setText(NumbersUtil.round2(auxTotalVentas).toPlainString());
                jTFTotalACGrid.setText(NumbersUtil.round2(auxAbonosCobrados).toPlainString());
                jTFTotalAPGrid.setText(NumbersUtil.round2(auxAbonosPagados).toPlainString());
            }
        }
        catch(Throwable ex){
            showMsgError(ex);
        }
    }
    
    public void showDetallesFacturaFrame(Integer traCodigo){
        System.out.println("Select action");
        int row = this.jTableVentas.getSelectedRow();
        if (traCodigo == 2){
            //row = this.jTableCompras.getSelectedRow();
        }
        else if (traCodigo == 3){
            row = this.jTableCxC.getSelectedRow();
        }
        else if (traCodigo == 4){
            row = this.jTableCxP.getSelectedRow();
        }
        
        if (row>-1){            
            if (traCodigo == 1 || traCodigo ==2){                
                FilaVenta filart = null;                
                if (traCodigo == 1){
                    filart = this.movsVentasDataModel.getValueAt(row);
                }
                else if (traCodigo == 2){
                    filart = this.movsComprasDataModel.getValueAt(row);
                }                
                DetallesFacturaFrame detallesFacturaFrame = new DetallesFacturaFrame(filart.getVentaId());            
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                detallesFacturaFrame.setLocation((dim.width/2)-(this.getSize().width/2), (dim.height/2)-(this.getSize().height/2));            
                detallesFacturaFrame.setSize(900, 500);
                detallesFacturaFrame.setVisible(true);
            }
            else if (traCodigo == 3 || traCodigo ==4){                
                FilaCXCP filaCXCP = null;
                if (traCodigo == 3){
                    filaCXCP = this.movsCXCDataModel.getValueAt(row);
                }
                else if (traCodigo == 4){
                    filaCXCP = this.movsCXPDataModel.getValueAt(row);
                }
                
                DetallesFacturaFrame detallesFacturaFrame = new DetallesFacturaFrame(filaCXCP.getCodFactura());
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                detallesFacturaFrame.setLocation((dim.width/2)-(this.getSize().width/2), (dim.height/2)-(this.getSize().height/2));            
                detallesFacturaFrame.setSize(900, 500);
                detallesFacturaFrame.setVisible(true);
            }
        }    
        else{
            JOptionPane.showMessageDialog(this, "Debe seleccionar la factura");
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
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTFFechaApertura = new javax.swing.JTextField();
        jTFSaldoInicial = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTAObsCierre = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTFCuentasXCobrar = new javax.swing.JTextField();
        jTFVentas = new javax.swing.JTextField();
        jTFCuentasXPagar = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTFSaldo = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTAObsApertura = new javax.swing.JTextArea();
        jLabel14 = new javax.swing.JLabel();
        jTFSaldoInicialChanca = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTFVentasChancado = new javax.swing.JTextField();
        jTFSaldoChanca = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jTFSaldoInicialSP = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jTFVentasSP = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jTFCXCSP = new javax.swing.JTextField();
        jTFCXCChanca = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jTFCXPSP = new javax.swing.JTextField();
        jTFCXPChanca = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jTFSaldoSP = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTFAjChanca = new javax.swing.JTextField();
        jTFAjSP = new javax.swing.JTextField();
        jTFAjOtros = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableVentas = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jTFTotalVGrid = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableCxC = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jTFTotalACGrid = new javax.swing.JTextField();
        jPanelCxP = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCxP = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jTFTotalAPGrid = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jButtonUpdVal = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1200, 800));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setText("Cierre de Caja");
        jPanel1.add(jLabel1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.GridLayout(1, 2));

        jLabel2.setText("Fecha de Apertura:");

        jLabel3.setText("Saldo Inicial (Anterior):");

        jLabel4.setText("Obs Apertura:");

        jTFFechaApertura.setEditable(false);

        jTFSaldoInicial.setEditable(false);

        jTAObsCierre.setColumns(20);
        jTAObsCierre.setRows(5);
        jScrollPane4.setViewportView(jTAObsCierre);

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel5.setText("Resumen del día:");

        jLabel6.setText("Total Ventas Válidas en Efectivo:");

        jLabel7.setText("Total Abonos Cobrados:");

        jLabel8.setText("Total Abonos Pagados:");

        jTFCuentasXCobrar.setEditable(false);

        jTFVentas.setEditable(false);

        jTFCuentasXPagar.setEditable(false);

        jLabel9.setText("Saldos en CAJA:");

        jTFSaldo.setEditable(false);
        jTFSaldo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTFSaldo.setForeground(new java.awt.Color(0, 153, 51));

        jLabel10.setText("Observaciones para el cierre:");

        jTAObsApertura.setEditable(false);
        jTAObsApertura.setColumns(20);
        jTAObsApertura.setRows(5);
        jScrollPane5.setViewportView(jTAObsApertura);

        jLabel14.setText("Chancado:");

        jTFSaldoInicialChanca.setEditable(false);

        jLabel15.setText("Chancado:");

        jTFVentasChancado.setEditable(false);

        jTFSaldoChanca.setEditable(false);
        jTFSaldoChanca.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTFSaldoChanca.setForeground(new java.awt.Color(0, 153, 51));

        jLabel17.setText("Servicios Prof.:");

        jLabel18.setText("Otros:");

        jTFSaldoInicialSP.setEditable(false);

        jLabel19.setText("Servicios Prof:");

        jLabel20.setText("Otros:");

        jTFVentasSP.setEditable(false);

        jLabel21.setText("Chancado:");

        jLabel22.setText("Servicios Prof:");

        jLabel23.setText("Otros:");

        jTFCXCSP.setEditable(false);

        jTFCXCChanca.setEditable(false);

        jLabel24.setText("Chancado:");

        jLabel25.setText("Servicios Prof:");

        jLabel26.setText("Otros:");

        jTFCXPSP.setEditable(false);

        jTFCXPChanca.setEditable(false);

        jLabel27.setText("Chancado:");

        jLabel28.setText("Servicios Prof:");

        jLabel29.setText("Otros:");

        jTFSaldoSP.setEditable(false);
        jTFSaldoSP.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTFSaldoSP.setForeground(new java.awt.Color(0, 153, 51));

        jLabel16.setText("Ajustes de Caja (+/-)");

        jTFAjChanca.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTFAjChanca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFAjChancaKeyReleased(evt);
            }
        });

        jTFAjSP.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTFAjSP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFAjSPKeyReleased(evt);
            }
        });

        jTFAjOtros.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTFAjOtros.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTFAjOtrosKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jTFCXCChanca, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTFCXCSP, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTFCuentasXCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(41, 41, 41)
                                .addComponent(jTFFechaApertura, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel5)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(82, 82, 82)
                                .addComponent(jLabel19)
                                .addGap(51, 51, 51)
                                .addComponent(jLabel20))
                            .addComponent(jLabel3)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addGap(77, 77, 77)
                                        .addComponent(jLabel17))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jTFSaldoInicialChanca, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTFSaldoInicialSP, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(23, 23, 23)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel18)
                                    .addComponent(jTFSaldoInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane5))
                        .addComponent(jLabel6)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jTFVentasChancado, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(22, 22, 22)
                            .addComponent(jTFVentasSP, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTFVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel7)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel21)
                            .addGap(82, 82, 82)
                            .addComponent(jLabel22)
                            .addGap(51, 51, 51)
                            .addComponent(jLabel23)))
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addGap(82, 82, 82)
                        .addComponent(jLabel25)
                        .addGap(51, 51, 51)
                        .addComponent(jLabel26))
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16)
                                    .addComponent(jTFAjChanca, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(3, 3, 3))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel27)
                                    .addComponent(jTFSaldoChanca, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTFSaldoSP, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel28)
                                    .addComponent(jTFAjSP))))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel29)
                                .addComponent(jTFSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTFAjOtros, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jTFCXPChanca, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTFCXPSP)
                        .addGap(18, 18, 18)
                        .addComponent(jTFCuentasXPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTFFechaApertura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addGap(2, 2, 2)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFSaldoInicialSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFSaldoInicialChanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFSaldoInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFVentasChancado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFVentasSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFCuentasXCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFCXCChanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFCXCSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFCuentasXPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFCXPSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFCXPChanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFAjChanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFAjSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFAjOtros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel29))
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFSaldoChanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFSaldoSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.add(jPanel4);

        jPanel5.setLayout(new java.awt.GridLayout(3, 1));

        jPanel7.setPreferredSize(new java.awt.Dimension(454, 500));
        jPanel7.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(454, 200));

        jTableVentas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTableVentas);

        jPanel7.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jLabel11.setText("TOTAL:");
        jPanel8.add(jLabel11);

        jTFTotalVGrid.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTFTotalVGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFTotalVGridActionPerformed(evt);
            }
        });
        jPanel8.add(jTFTotalVGrid);

        jPanel7.add(jPanel8, java.awt.BorderLayout.SOUTH);

        jPanel5.add(jPanel7);

        jPanel6.setPreferredSize(new java.awt.Dimension(454, 200));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setPreferredSize(new java.awt.Dimension(454, 200));

        jTableCxC.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTableCxC);

        jPanel6.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jLabel12.setText("TOTAL:");
        jPanel9.add(jLabel12);

        jTFTotalACGrid.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTFTotalACGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFTotalACGridActionPerformed(evt);
            }
        });
        jPanel9.add(jTFTotalACGrid);

        jPanel6.add(jPanel9, java.awt.BorderLayout.SOUTH);

        jPanel5.add(jPanel6);

        jPanelCxP.setPreferredSize(new java.awt.Dimension(454, 500));
        jPanelCxP.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setPreferredSize(new java.awt.Dimension(454, 200));

        jTableCxP.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(jTableCxP);

        jPanelCxP.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jLabel13.setText("TOTAL:");
        jPanel10.add(jLabel13);

        jTFTotalAPGrid.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTFTotalAPGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFTotalAPGridActionPerformed(evt);
            }
        });
        jPanel10.add(jTFTotalAPGrid);

        jPanelCxP.add(jPanel10, java.awt.BorderLayout.SOUTH);

        jPanel5.add(jPanelCxP);

        jPanel2.add(jPanel5);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.GridLayout(8, 1));

        jButtonUpdVal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-connection_sync.png"))); // NOI18N
        jButtonUpdVal.setText("Actualizar Valores");
        jButtonUpdVal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdValActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonUpdVal);

        jButton1.setText("Cerrar Caja");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jj/gui/icons/icons8-close_pane_filled.png"))); // NOI18N
        jButton2.setText("Cancelar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2);

        getContentPane().add(jPanel3, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
        loadInfoCaja();
        
    }//GEN-LAST:event_formWindowOpened

    private void jButtonUpdValActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdValActionPerformed
        
        loadInfoCaja();        
        showMsg("Valores actualizados");
        
    }//GEN-LAST:event_jButtonUpdValActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try{
            int response = JOptionPane.showConfirmDialog(null, "¿Seguro que desea registrar el cierre de caja, ya no podra registrar ventas? ");
            if (response == JOptionPane.YES_OPTION){
                Integer cjId = caja.getCjId();
                BigDecimal ventasAnuladas = BigDecimal.ZERO;
                String obsCierre = jTAObsCierre.getText();
                cajasController.cerrarCaja(cjId, 
                        totalVentas, 
                        totalVentasChanca, 
                        totalVentasSP,
                        abonosCobrados, 
                        abonosPagados, 
                        ventasAnuladas, 
                        saldoCaja, 
                        saldoCajaChanca, 
                        saldoCajaSP, 
                        obsCierre,
                        abonosCobradosChk,
                        abonosCobradosSP,
                        abonosPagadosChk,
                        abonosPagadosSP,
                        valAjOtros,
                        valAJSP,
                        valAjChanca
                        );
                showMsg(" La caja ha sido cerrada satisfactoriamente ");                
                setVisible(false);
            }
        }
        catch(Throwable ex){
            showMsgError(ex);
        }
        
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTFTotalVGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFTotalVGridActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFTotalVGridActionPerformed

    private void jTFTotalACGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFTotalACGridActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFTotalACGridActionPerformed

    private void jTFTotalAPGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFTotalAPGridActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFTotalAPGridActionPerformed

    private void jTFAjChancaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFAjChancaKeyReleased
        try{
             valAjChanca = BigDecimal.ZERO;
             valAjChanca = new BigDecimal( jTFAjChanca.getText() );                          
        }
        catch(Throwable ex){
            valAjChanca = BigDecimal.ZERO;
        }
        updateSaldoAjustes();
        
    }//GEN-LAST:event_jTFAjChancaKeyReleased

    private void jTFAjSPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFAjSPKeyReleased
        try{
             valAJSP = BigDecimal.ZERO;
             valAJSP = new BigDecimal( jTFAjSP.getText() );                          
        }
        catch(Throwable ex){
            valAJSP = BigDecimal.ZERO;
        }
        updateSaldoAjustes();
        
    }//GEN-LAST:event_jTFAjSPKeyReleased

    private void jTFAjOtrosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFAjOtrosKeyReleased
        try{
             valAjOtros = BigDecimal.ZERO;
             valAjOtros = new BigDecimal( jTFAjOtros.getText() );                          
        }
        catch(Throwable ex){
            valAjOtros = BigDecimal.ZERO;
        }
        updateSaldoAjustes();
    }//GEN-LAST:event_jTFAjOtrosKeyReleased
    
    public void updateSaldoAjustes(){
        try{
            saldoCaja =  auxSaldoCaja.add(valAjOtros);
            saldoCajaChanca =  auxSaldoCajaChanca.add(valAjChanca);
            saldoCajaSP = auxSaldoCajaSP.add(valAJSP);

            jTFSaldo.setText(NumbersUtil.round2(saldoCaja).toPlainString());
            jTFSaldoChanca.setText(NumbersUtil.round2(saldoCajaChanca).toPlainString());
            jTFSaldoSP.setText(NumbersUtil.round2(saldoCajaSP).toPlainString());
        }
        catch(Throwable ex){
            System.out.println("Error al actualizar saldos de caja:"+ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private BigDecimal valAjChanca = BigDecimal.ZERO;
    private BigDecimal valAJSP= BigDecimal.ZERO;
    private BigDecimal valAjOtros= BigDecimal.ZERO;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonUpdVal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelCxP;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextArea jTAObsApertura;
    private javax.swing.JTextArea jTAObsCierre;
    private javax.swing.JTextField jTFAjChanca;
    private javax.swing.JTextField jTFAjOtros;
    private javax.swing.JTextField jTFAjSP;
    private javax.swing.JTextField jTFCXCChanca;
    private javax.swing.JTextField jTFCXCSP;
    private javax.swing.JTextField jTFCXPChanca;
    private javax.swing.JTextField jTFCXPSP;
    private javax.swing.JTextField jTFCuentasXCobrar;
    private javax.swing.JTextField jTFCuentasXPagar;
    private javax.swing.JTextField jTFFechaApertura;
    private javax.swing.JTextField jTFSaldo;
    private javax.swing.JTextField jTFSaldoChanca;
    private javax.swing.JTextField jTFSaldoInicial;
    private javax.swing.JTextField jTFSaldoInicialChanca;
    private javax.swing.JTextField jTFSaldoInicialSP;
    private javax.swing.JTextField jTFSaldoSP;
    private javax.swing.JTextField jTFTotalACGrid;
    private javax.swing.JTextField jTFTotalAPGrid;
    private javax.swing.JTextField jTFTotalVGrid;
    private javax.swing.JTextField jTFVentas;
    private javax.swing.JTextField jTFVentasChancado;
    private javax.swing.JTextField jTFVentasSP;
    private javax.swing.JTable jTableCxC;
    private javax.swing.JTable jTableCxP;
    private javax.swing.JTable jTableVentas;
    // End of variables declaration//GEN-END:variables
}
