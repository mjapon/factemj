/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.util.datamodels;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import jj.controller.FacturasJpaController;
import jj.util.CtesU;
import jj.util.FechasUtil;
import jj.util.FilaVenta;
import jj.util.ParamsBusquedaTransacc;

/**
 *
 * @author mjapon
 */
public class VentasDataModel extends AbstractTableModel{
    
    private TotalesVentasModel totalesVentasModel;
    
    public enum ColVentasEnum{        
        NROFACT(0, "Nro", String.class, "factNum"),
        FECHAREG(1, "Fecha", String.class, "factFecreg"),
        CLIENTE(2, "Cliente", String.class, "cliId.cliNombres"),
        CIRUC(3, "CI/RUC", String.class, "cliId.cliCi"),
        SUBTOTAL(4, "Subtotal", BigDecimal.class, "factSubt"),
        IVA(5, "IVA", BigDecimal.class, "factIva"),
        DESCUENTO(6, "Descuento", BigDecimal.class,"factDesc"),
        TOTAL(7, "Total", BigDecimal.class, "factTotal"),      
        EFECTIVO(8, "Efectivo", BigDecimal.class, "factTotal"),
        CREDITO(9, "Crédito", BigDecimal.class, "factTotal");
        
        public final int index;
        public String desc;
        public Class klass;
        public String entityCol;
        private ColVentasEnum(int index, String desc, Class cclass, String entityColumn){
            this.index = index; 
            this.desc = desc;
            this.klass = cclass;
            this.entityCol = entityColumn;
        }
        public static String GET_NAME(int index){            
            ColVentasEnum[] values = ColVentasEnum.values();            
            for (ColVentasEnum col:values){
                if (col.index == index){
                    return col.desc;
                }
            }
            return "NODEF_INDEX"+index;
        }
        public static Class GET_CLASS(int index){
            ColVentasEnum[] values = ColVentasEnum.values();
            for (ColVentasEnum col:values){
                if (col.index == index){
                    return col.klass;
                }
            }
            return null;
        }
        public static String GET_ENTITY(int index){
            ColVentasEnum[] values = ColVentasEnum.values();
            for (ColVentasEnum col:values){
                if (col.index == index){
                    return col.entityCol;
                }
            }
            return null;            
        }
    };
    
    private Date fecha;
    private Map<Integer, Integer> mapSort;
    private List<FilaVenta> items = new ArrayList<>();
    private FacturasJpaController controller;    
    private ParamsBusquedaTransacc params;
    
    public void initForAdmin(){
         mapSort = new HashMap<>();
         mapSort.put(0, 1);
         mapSort.put(1, 0);
         mapSort.put(2, 0);
         mapSort.put(3, 0);
         mapSort.put(4, 0);
         mapSort.put(5, 0);
         mapSort.put(6, 0);
         mapSort.put(7, 0);
         mapSort.put(8, 0);
         mapSort.put(9, 0);
    }

    public ParamsBusquedaTransacc getParams() {
        return params;
    }

    public void setParams(ParamsBusquedaTransacc params) {
        this.params = params;
    }
    
    public void totalizar(){
        BigDecimal sumaIva = BigDecimal.ZERO;
        BigDecimal sumaDesc = BigDecimal.ZERO;
        BigDecimal sumaTotal = BigDecimal.ZERO;
        BigDecimal sumaEfectivo = BigDecimal.ZERO;
        BigDecimal sumaCredito = BigDecimal.ZERO;
        
        for (FilaVenta filaVenta: this.items){            
            sumaIva = sumaIva.add(filaVenta.getIva());
            sumaDesc = sumaDesc.add(filaVenta.getDescuento());
            sumaTotal = sumaTotal.add(filaVenta.getTotal());
            sumaEfectivo = sumaEfectivo.add(filaVenta.getEfectivo());
            sumaCredito = sumaCredito.add(filaVenta.getCredito());
        }
        
        if (this.totalesVentasModel == null){
            totalesVentasModel = new TotalesVentasModel();
        }
        
        totalesVentasModel.setSumaIva(sumaIva);
        totalesVentasModel.setSumaDesc(sumaDesc);
        totalesVentasModel.setSumaTotal(sumaTotal);
        totalesVentasModel.setSumaEfectivo(sumaEfectivo);
        totalesVentasModel.setSumaCredito(sumaCredito);
    }
    
    public VentasDataModel(Date fecha){
        super();
        initForAdmin();
        totalesVentasModel  = new TotalesVentasModel();
        this.fecha = fecha;
    }
    

    @Override
    public String getColumnName(int column) { 
        if (column>=0 && column<VentasDataModel.ColVentasEnum.values().length){  
            
            String colName = VentasDataModel.ColVentasEnum.GET_NAME(column);
            
            if (mapSort.containsKey(column)){
                Integer sortValue = mapSort.get(column);
                switch(sortValue){
                    //case 0:{break;}
                    case 1:{colName = colName+" (+)"; break;}
                    case -1:{colName = colName+" (-)";break;}
                }
            }            
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
        return ColVentasEnum.values().length;
    }
    
    public Integer getSorIndex(){
        int index = 0;//Por defecto se ordena por nombre
        for (int i=0; i<ColVentasEnum.values().length; i++){
            if (mapSort.containsKey(i)){
                if (mapSort.get(i)!= 0){
                    index = i;
                }
            }
        }        
        return index;
    }
    
    public String getSortColumn(Integer column){        
        String colName = ColVentasEnum.GET_ENTITY(column);
        return colName;       
    }
    
    public void switchSortColumn(Integer column) throws Exception{    
        
        for (int i = 0; i<ColVentasEnum.values().length;i++){
            
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
    
    public void loadFromDataBase() throws Exception{
        int sortIndex = getSorIndex();
        int sortorder = mapSort.get(sortIndex);
        
        String sortord = sortorder==-1?"desc":"asc";
        String sortcolumn = getSortColumn(sortIndex);
        
        this.params.setSortColumn(sortcolumn);
        this.params.setSortOrder(sortord);
        
        List<Object[]> ventas = controller.listar(this.params);
        
        items.clear();
        
        for(Object[] art: ventas){
            Integer ventaId = (Integer)art[0];
            String nro = (String)art[1];
            BigDecimal subtotal = (BigDecimal)art[2];
            String cliente = (String)art[7];
            String ciruc = (String)art[8];
            BigDecimal iva = new BigDecimal(art[3].toString()); 
            BigDecimal descuento = new BigDecimal(art[4].toString());
            BigDecimal total = new BigDecimal(art[5].toString());
            Date fechaReg = (Date)art[6];
            if (this.params.getArtId()!=null && this.params.getArtId()>0){
                total =  subtotal.subtract(descuento).add(iva);
            }            
            
            BigDecimal efectivo = (BigDecimal)art[10];
            BigDecimal credito = (BigDecimal)art[11];
            
            if (efectivo.compareTo(BigDecimal.ZERO)==0 && credito.compareTo(BigDecimal.ZERO)==0){
                efectivo = total;
            }
            
            FilaVenta fila = new FilaVenta(ventaId, nro, cliente, ciruc, iva, 
                    descuento, total, subtotal, 
                    FechasUtil.format(fechaReg), efectivo, credito);
            items.add(fila);
        }
        
        totalizar();        
        totalesVentasModel.setUtilidades(BigDecimal.ZERO);
        try{
            BigDecimal utilidades = controller.getUtilidades(params);
            totalesVentasModel.setUtilidades(utilidades);
        }
        catch(Throwable ex){
            System.out.println("Error al obtener las utilidades:"+ex.getMessage());
            ex.printStackTrace();
        }
        
        fireTableDataChanged();
    }
    
    
    public FilaVenta getValueAt(int rowIndex){
        if (rowIndex>=0 && rowIndex<items.size()){
            return items.get(rowIndex);            
        }
        return null;
    }
    
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex>=0 && rowIndex<items.size()){
            FilaVenta filaVenta = items.get(rowIndex);
            if (columnIndex==ColVentasEnum.NROFACT.index){
                return filaVenta.getNro();
            }
            else if (columnIndex==ColVentasEnum.CLIENTE.index){
                return filaVenta.getCliente();
            }
            else if (columnIndex==ColVentasEnum.CIRUC.index){
                return filaVenta.getCiruc();
            }
            else if (columnIndex==ColVentasEnum.SUBTOTAL.index){
                
                BigDecimal subtotal = filaVenta.getSubtotal();
                subtotal.setScale(CtesU.NUM_DECIM_VIEW, BigDecimal.ROUND_HALF_UP);
                return subtotal.toPlainString();
            }
            else if (columnIndex==ColVentasEnum.IVA.index){
                BigDecimal valorIva = filaVenta.getIva();
                valorIva.setScale(CtesU.NUM_DECIM_VIEW, BigDecimal.ROUND_HALF_UP);
                return valorIva.toPlainString();
            }
            else if (columnIndex==ColVentasEnum.DESCUENTO.index){
                BigDecimal descuento = filaVenta.getDescuento();
                descuento.setScale(CtesU.NUM_DECIM_VIEW, BigDecimal.ROUND_HALF_UP);
                return descuento.toPlainString();
            }
            else if (columnIndex==ColVentasEnum.TOTAL.index){
                BigDecimal total = filaVenta.getTotal();
                total.setScale(CtesU.NUM_DECIM_VIEW, BigDecimal.ROUND_HALF_UP);
                return total.toPlainString();
            }
            else if (columnIndex==ColVentasEnum.FECHAREG.index){
                return filaVenta.getFechaReg();
            }
            else if (columnIndex==ColVentasEnum.EFECTIVO.index){
                BigDecimal efectivo = filaVenta.getEfectivo();
                efectivo.setScale(CtesU.NUM_DECIM_VIEW, BigDecimal.ROUND_HALF_UP);
                return efectivo.toPlainString();
            }
            else if (columnIndex==ColVentasEnum.CREDITO.index){
                BigDecimal credito = filaVenta.getCredito();
                credito.setScale(CtesU.NUM_DECIM_VIEW, BigDecimal.ROUND_HALF_UP);
                return credito.toPlainString();
            }
            else{
                return "";
            }
        }
        else{
            return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) { 
        return false;
    }
    

    @Override
    public Class<?> getColumnClass(int columnIndex) {        
         Class klass = ColVentasEnum.GET_CLASS(columnIndex);
        if (klass != null){
            return klass;
        }
        return Object.class;
    }

    public List<FilaVenta> getItems() {
        return items;
    }

    public void setItems(List<FilaVenta> items) {
        this.items = items;
    }

    public FacturasJpaController getController() {
        return controller;
    }

    public void setController(FacturasJpaController controller) {
        this.controller = controller;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public TotalesVentasModel getTotalesVentasModel() {
        return totalesVentasModel;
    }

    public void setTotalesVentasModel(TotalesVentasModel totalesVentasModel) {
        this.totalesVentasModel = totalesVentasModel;
    }
}