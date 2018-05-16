/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.util.datamodels;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import jj.controller.CajasJpaController;
import jj.util.ParamBusquedaCXCP;
import jj.util.TotalesCajas;

/**
 *
 * @author mjapon
 */
public class CajasDataModel extends AbstractTableModel{
    
    protected List<FilaCajas> items = new ArrayList<>();
    protected TotalesCajas totales;
    protected JTable jtable;
    
    protected Map<Integer, Integer> mapSort;     
    protected CajasJpaController controller;    
    protected ParamBusquedaCXCP params;
    
    //pgfFecreg
    
    public enum ColumnaCAJASEnum{        
        NRO(0, "Nro", Integer.class, "cj_id"),
        SALDOANT(1, "Saldo Anterior", BigDecimal.class, "cj_saldoant"),
        FECHAAPER(2, "Fecha Apertura", String.class, "cj_fecaper"),
        ESTADO(3, "Estado", String.class, "cj_estado"),
        FECHACIERRE(4, "Fecha Cierre", String.class, "cj_feccierre"),
        OBSAPER(5, "Obs Apertura", String.class, "cj_obsaper"),
        TOTVENTAS(6, "Total Ventas", BigDecimal.class, "cj_ventas"),
        ABCOBRA(7, "Abonos Cobrados", BigDecimal.class, "cj_abonoscxc"),
        ABPAGA(8, "Abonos Pagados", BigDecimal.class, "cj_abonoscxp"),
        SALDO(9, "Saldo", BigDecimal.class, "cj_saldo");
        
        public final int index;
        public String desc;
        public Class klass;
        public String entityCol;
        
        private ColumnaCAJASEnum(int index, String desc, Class cclass, String entityColumn){
            this.index = index; 
            this.desc = desc;
            this.klass = cclass;
            this.entityCol = entityColumn;
        }
        public static String GET_NAME(int index){            
            ColumnaCAJASEnum[] values = ColumnaCAJASEnum.values();            
            for (ColumnaCAJASEnum col:values){
                if (col.index == index){
                    return col.desc;
                }
            }
            return "NODEF_INDEX"+index;
        }
        public static Class GET_CLASS(int index){
            ColumnaCAJASEnum[] values = ColumnaCAJASEnum.values();
            for (ColumnaCAJASEnum col:values){
                if (col.index == index){
                    return col.klass;
                }
            }
            return null;
        }        
        
        public static String GET_ENTITY(int index){
            CajasDataModel.ColumnaCAJASEnum[] values = CajasDataModel.ColumnaCAJASEnum.values();
            for (CajasDataModel.ColumnaCAJASEnum col:values){
                if (col.index == index){
                    return col.entityCol;
                }
            }
            return null;            
        }
    };
    
    public CajasDataModel(){
        super();
        totales = new TotalesCajas();
        mapSort = new HashMap<>();
        mapSort.put(0, 1);
        mapSort.put(1, 0);
        mapSort.put(2, 0);
        mapSort.put(3, 0);
        mapSort.put(4, 0);        
        mapSort.put(5, 0);        
    }
    
    public void removeItem(int rowIndex){
        
    }
    
    public void addItem(FilaCajas item){        
        items.add(item);
        totalizar();
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        if (column>=0 && column<ColumnaCAJASEnum.values().length){            
            String colName = ColumnaCAJASEnum.GET_NAME(column);
            return colName;
            
        }
        return super.getColumnName(column); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public int getRowCount() {
        return items.size();
    }   
    
    @Override
    public int getColumnCount() {
        return ColumnaCAJASEnum.values().length;
    }

     @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {   
        
       return false;
        
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        
        super.setValueAt(aValue, rowIndex, columnIndex); //To change body of generated methods, choose Tools | Templates.
    }
        
    public FilaCajas getValueAt(int rowIndex) {        
        FilaCajas fila = items.get(rowIndex);
        return fila;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {        
        if (rowIndex>=0 && rowIndex<items.size()){
            FilaCajas fila = items.get(rowIndex);
            /*
            if (columnIndex==ColumnaFacturaEnum.NRO.index){
                return filafactura.getNumFila();
            }
            else 
            */
            if (columnIndex==ColumnaCAJASEnum.NRO.index){
                return fila.getCj_id();
            }
            else if (columnIndex==ColumnaCAJASEnum.SALDOANT.index){
                return fila.getCj_saldoant();
            }
            else if (columnIndex==ColumnaCAJASEnum.FECHAAPER.index){
                return fila.getCj_fecaper();
            }
            else if (columnIndex==ColumnaCAJASEnum.ESTADO.index){
                return fila.getEstado();
            }
            else if (columnIndex==ColumnaCAJASEnum.FECHACIERRE.index){
                return fila.getCj_feccierre();
            }
            else if (columnIndex==ColumnaCAJASEnum.OBSAPER.index){
                return fila.getCj_obsaper();
            }
            else if (columnIndex==ColumnaCAJASEnum.TOTVENTAS.index){
                return fila.getCj_ventas();
            }
            else if (columnIndex==ColumnaCAJASEnum.ABCOBRA.index){
                return fila.getCj_abonoscxc();
            }
            else if (columnIndex==ColumnaCAJASEnum.ABPAGA.index){
                return fila.getCj_abonoscxp();
            }
            else if (columnIndex==ColumnaCAJASEnum.SALDO.index){
                return fila.getCj_saldo();
            }
            else{
                return "";
            }
        }
        else{
            return "";
        }
    }
    
    /*
    public void loadItems(List<Object[]> items){        
        for (Object[] item: items){
            
            Integer facturaId = (Integer)item[0];
            String numFactura = (String)item[1];
            Date fechafactura = (Date)item[2];
            BigDecimal monto = (BigDecimal)item[3];
            BigDecimal deuda = (BigDecimal)item[8];
            String referente = (String)item[4];
            String obs = (String)item[9];   
            Integer codPago = (Integer)item[0]; 
            String estadoDesc = (String)item[11];
            
            
            
            Integer cj_id, 
            BigDecimal cj_saldoant, 
            BigDecimal cj_ventas, 
            BigDecimal cj_abonoscxc, 
            BigDecimal cj_abonoscxp, 
            String cj_obsaper, 
            String cj_obscierre, 
            String cj_fecaper, 
            String cj_feccierre, 
            Integer cj_estado, 
            String estado, 
            String cj_obsanul, 
            BigDecimal cj_saldo
            
            FilaCajas fila = new FilaCajas(facturaId, numFactura, monto, deuda, referente, 
                    obs, FechasUtil.format(fechafactura), codPago, estadoDesc, BigDecimal.ZERO);
            addItem(fila);
        }
        
        fireTableDataChanged();
        
    }
    */  

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Class klass = ColumnaCAJASEnum.GET_CLASS(columnIndex);
        if (klass != null){
            return klass;
        }
        return Object.class;
        
    }    
    
    public void switchSortColumn(Integer column) throws Exception{    
        
        for (int i = 0; i<CajasDataModel.ColumnaCAJASEnum.values().length;i++){
            
            if (i == column){
                Integer sortValue = mapSort.get(column);
                if (sortValue == -1){
                    mapSort.put(column, 1);
                }
                else{
                    mapSort.put(column, -1);
                }
            }
            else{
                mapSort.put(i, 0);
            }
        }
        
        this.loadFromDataBase();
        
    }
    
    public Integer getSorIndex(){
        int index = 0;//Por defecto se ordena por nombre
        for (int i=0; i<CajasDataModel.ColumnaCAJASEnum.values().length; i++){
            if (mapSort.containsKey(i)){
                if (mapSort.get(i)!= 0){
                    index = i;
                }
            }
        }        
        return index;
    }
    
    public String getSortColumn(Integer column){       
        String colName = CajasDataModel.ColumnaCAJASEnum.GET_ENTITY(column);
        return colName;       
    }
    
    public void loadFromDataBase() throws Exception{
        int sortIndex = getSorIndex();
        int sortorder = mapSort.get(sortIndex);
        
        String sortord = sortorder==-1?"desc":"asc";
        String sortcolumn = getSortColumn(sortIndex);
        
        this.params.setSortColumn(sortcolumn);
        this.params.setSortOrder(sortord);
        
        List<FilaCajas> cajasList = controller.listar(this.params.getDesde(),this.params.getHasta());        
        items.clear();
        items = cajasList;
        totalizar();
        fireTableDataChanged();
    }    
    
    public void totalizar(){
        BigDecimal sumaSaldos = BigDecimal.ZERO;        
        for (FilaCajas fila: this.items){
            sumaSaldos = sumaSaldos.add( fila.getCj_saldo()!=null?fila.getCj_saldo():BigDecimal.ZERO );
        }
        
        if (this.getTotalesCajas() == null){
            this.setTotalesCajas(new TotalesCajas());
        }
        
        TotalesCajas totales = this.getTotalesCajas();
        totales.setSumaSaldo(sumaSaldos);
    }

    public List<FilaCajas> getItems() {
        return items;
    }

    public void setItems(List<FilaCajas> items) {
        this.items = items;
    }

    public TotalesCajas getTotalesCajas() {
        return totales;
    }

    public void setTotalesCajas(TotalesCajas totales) {
        this.totales = totales;
    }
    
    public JTable getJtable() {
        return jtable;
    }

    public void setJtable(JTable jtable) {
        this.jtable = jtable;
    }

    public ParamBusquedaCXCP getParams() {
        return params;
    }

    public void setParams(ParamBusquedaCXCP params) {
        this.params = params;
    }

    public CajasJpaController getController() {
        return controller;
    }

    public void setController(CajasJpaController controller) {
        this.controller = controller;
    }
    
}
