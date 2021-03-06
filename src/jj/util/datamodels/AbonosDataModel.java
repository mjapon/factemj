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
import jj.controller.MovtransaccJpaController;
import jj.entity.Movtransacc;
import jj.util.FechasUtil;
import jj.util.datamodels.rows.FilaAbonos;
import jj.util.NumbersUtil;
import jj.util.TotalesCuentasXPC;

/**
 *
 * @author mjapon
 */
public class AbonosDataModel  extends AbstractTableModel{
    
    protected List<FilaAbonos> items = new ArrayList<>();
    protected TotalesCuentasXPC totales;
    protected JTable jtable;
    
    private Map<Integer, Integer> mapSort;     
    private MovtransaccJpaController controller;    
    private Integer pgfId;
    //private ParamBusquedaCXCP params;
    
    //pgfFecreg
    
    public enum ColumnaAbonosEnum{        
        FECHAABONO(0, "Fecha Abono", String.class, "movFechareg"),
        MONTO(1, "Monto", BigDecimal.class, "movMonto"),
        OBS(2, "Observacion", String.class, "pgfMonto"),
        ESTADO(3, "Estado", String.class, "pgfSaldo");
        
        public final int index;
        public String desc;
        public Class klass;
        public String entityCol;
        
        private ColumnaAbonosEnum(int index, String desc, Class cclass, String entityColumn){
            this.index = index; 
            this.desc = desc;
            this.klass = cclass;
            this.entityCol = entityColumn;
        }
        public static String GET_NAME(int index){            
            ColumnaAbonosEnum[] values = ColumnaAbonosEnum.values();            
            for (ColumnaAbonosEnum col:values){
                if (col.index == index){
                    return col.desc;
                }
            }
            return "NODEF_INDEX"+index;
        }
        public static Class GET_CLASS(int index){
            ColumnaAbonosEnum[] values = ColumnaAbonosEnum.values();
            for (ColumnaAbonosEnum col:values){
                if (col.index == index){
                    return col.klass;
                }
            }
            return null;
        }        
        
        public static String GET_ENTITY(int index){
            AbonosDataModel.ColumnaAbonosEnum[] values = AbonosDataModel.ColumnaAbonosEnum.values();
            for (AbonosDataModel.ColumnaAbonosEnum col:values){
                if (col.index == index){
                    return col.entityCol;
                }
            }
            return null;            
        }
    };
    
    public AbonosDataModel(  ){
        super();
        totales = new TotalesCuentasXPC();
        mapSort = new HashMap<>();
        mapSort.put(0, 1);
        mapSort.put(1, 0);
        mapSort.put(2, 0);
        mapSort.put(3, 0);
        
        //this.pgfId = pagoId;
    }
    
    public void removeItem(int rowIndex){
        
    }
    
    public void addItem(FilaAbonos item){        
        items.add(item);
        totalizar();
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        if (column>=0 && column<ColumnaAbonosEnum.values().length){            
            String colName = ColumnaAbonosEnum.GET_NAME(column);
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
        return ColumnaAbonosEnum.values().length;
    }

     @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {   
        
       return false;
        
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        
        super.setValueAt(aValue, rowIndex, columnIndex); //To change body of generated methods, choose Tools | Templates.
    }
        
    public FilaAbonos getValueAt(int rowIndex) {        
        FilaAbonos fila = items.get(rowIndex);
        return fila;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {        
        if (rowIndex>=0 && rowIndex<items.size()){
            FilaAbonos fila = items.get(rowIndex);
            /*
            if (columnIndex==ColumnaFacturaEnum.NRO.index){
                return filafactura.getNumFila();
            }
            else 
            */
            if (columnIndex==ColumnaAbonosEnum.FECHAABONO.index){
                return fila.getFechaAbono();
            }
            else if (columnIndex==ColumnaAbonosEnum.MONTO.index){
                return NumbersUtil.round(fila.getMonto(), 2).toPlainString();
            }
            else if (columnIndex==ColumnaAbonosEnum.OBS.index){
                return fila.getObservacion();
            }
            else if (columnIndex==ColumnaAbonosEnum.ESTADO.index){
                return fila.getEstado();
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
            FilaCXCP fila = new FilaCXCP(facturaId, numFactura, monto, deuda, referente, obs, FechasUtil.format(fechafactura), codPago);
            addItem(fila);
        }
        
        fireTableDataChanged();
        
    }
*/

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Class klass = ColumnaAbonosEnum.GET_CLASS(columnIndex);
        if (klass != null){
            return klass;
        }
        return Object.class;
        
    }    
    
    public void switchSortColumn(Integer column) throws Exception{    
        
        for (int i = 0; i<AbonosDataModel.ColumnaAbonosEnum.values().length;i++){
            
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
        for (int i=0; i<AbonosDataModel.ColumnaAbonosEnum.values().length; i++){
            if (mapSort.containsKey(i)){
                if (mapSort.get(i)!= 0){
                    index = i;
                }
            }
        }        
        return index;
    }
    
    public String getSortColumn(Integer column){       
        String colName = AbonosDataModel.ColumnaAbonosEnum.GET_ENTITY(column);
        return colName;       
    }
    
    public void loadFromDataBase() throws Exception{
        int sortIndex = getSorIndex();
        int sortorder = mapSort.get(sortIndex);
        
        String sortord = sortorder==-1?"desc":"asc";
        
        
        List<Movtransacc> abonosList = controller.listarAbonos(pgfId);        
        items.clear();
        
        for(Movtransacc item: abonosList){
            
            String fechaAbono = FechasUtil.formatDateHour(item.getMovFechareg());
            BigDecimal monto = NumbersUtil.round(item.getMovMonto(), 2);
            String estado = item.getMovValido()==0?"VALIDO":"ANULADO";
            String observacion = item.getMovObserv();
            
            FilaAbonos fila = new FilaAbonos(item.getMovId(),fechaAbono, monto, estado, observacion);
            items.add(fila);
        }
        
        totalizar();                
        fireTableDataChanged();
    }
    
    
    public void totalizar(){
        
    }

    public List<FilaAbonos> getItems() {
        return items;
    }

    public void setItems(List<FilaAbonos> items) {
        this.items = items;
    }

    public TotalesCuentasXPC getTotalesFactura() {
        return totales;
    }

    public void setTotalesFactura(TotalesCuentasXPC totales) {
        this.totales = totales;
    }
    
    public JTable getJtable() {
        return jtable;
    }

    public void setJtable(JTable jtable) {
        this.jtable = jtable;
    }

    public MovtransaccJpaController getController() {
        return controller;
    }

    public void setController(MovtransaccJpaController controller) {
        this.controller = controller;
    }

    public Integer getPgfId() {
        return pgfId;
    }

    public void setPgfId(Integer pgfId) {
        this.pgfId = pgfId;
    }
    
    
}
