/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.util.datamodels;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import jj.controller.ArticulosJpaController;
import jj.util.ErrorValidException;
import jj.util.FilaArticulo;
import jj.util.StringUtil;

/**
 *
 * @author manuel.japon
 */
public class MercaderiaDataModel extends AbstractTableModel{
    
    private String[] columNames={
        "Nro", //0 artId
        "Codbar",//1 artCodbar
        "Articulo",//2  artNombre
        "Prec. Compra sin Iva",//3   artPrecioCompra
        "Prec. Venta",//4  artPrecio
        "Precio Mínimo",//5  artPreciomin
        "IVA",//6   artIva
        "Inventario"//7    artInv
    };
    
    private Map<Integer, Integer> mapSort;
    private List<FilaArticulo> items = new ArrayList<>();
    private ArticulosJpaController controller;    
    
    
    public MercaderiaDataModel(){
        mapSort = new HashMap<>();
        mapSort.put(0, 0);
        mapSort.put(1, 0);
        mapSort.put(2, 1);
        mapSort.put(3, 0);
        mapSort.put(4, 0);
        mapSort.put(5, 0);
        mapSort.put(6, 0);
        mapSort.put(7, 0);
    }
    
    public void clearItems(){
        if (items != null){
            items.clear();
        }
    }
    
    public boolean validar(FilaArticulo filaArt){
        if (!StringUtil.isNotEmpty(filaArt.getCodBarra()) ){
            throw  new ErrorValidException("Debe ingresar el codigo de barra");
        }
        else if (!StringUtil.isNotEmpty(filaArt.getNombre())){
            throw  new ErrorValidException("Debe ingresar el nombre");
        }        
        else if (!StringUtil.isNotEmpty(filaArt.getPrecioCompra().toString())){
            throw  new ErrorValidException("Debe ingresar el precio de compra");
        }
        else if (!StringUtil.isNotEmpty(filaArt.getPrecioVenta().toString())){
            throw  new ErrorValidException("Debe ingresar el precio de venta");
        }
        
        return true;
    }
    
    public void addItem(FilaArticulo filaArt){
        this.validar(filaArt);
        items.add(filaArt);
        fireTableDataChanged();
    }    

    @Override
    public String getColumnName(int column) { 
        if (column>=0 && column<columNames.length){
            String colName = columNames[column];
            
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
        return this.columNames.length;
    }
    
    public Integer getSorIndex(){
        int index = 2;//Por defecto se ordena por nombre
        for (int i=0; i<columNames.length; i++){
            if (mapSort.containsKey(i)){
                if (mapSort.get(i)!= 0){
                    index = i;
                }
            }
        }        
        return index;
    }
    
    public String getSortColumnForAdmin(Integer column){
        String columnName = "artNombre";       
        switch(column){
            case 0:{
                columnName = "artId";
                break;
            }
            case 1:{
                columnName = "artCodbar";
                break;
            }
            case 2:{
                columnName = "artNombre";
                break;
            }
            case 3:{
                columnName = "artPrecioCompra";
                break;
            }
            case 4:{
                columnName = "artPrecio";
                break;
            }
            case 5:{
                columnName = "artPreciomin";
                break;
            }
            case 6:{
                columnName = "artIva";
                break;
            }
            case 7:{
                columnName = "artInv";
                break;
            }
        }
        return columnName;
    }
    
    public String getSortColumn(Integer column){           
        return getSortColumnForAdmin(column);
    }
    
    public void switchSortColumn(Integer column) throws Exception{    
        
        for (int i = 0; i<columNames.length;i++){
            
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
        
        List<Object[]> arts = controller.listarRaw(sortcolumn, sortord);
        
        items.clear();
        for(Object[] art: arts){            
           items.add(getNewFromRow(art));
        }
        
        fireTableDataChanged();
    }
    
    public FilaArticulo getNewFromRow(Object[] art){
         FilaArticulo filaArticulo = new FilaArticulo(
                    (Integer)art[0],
                    (String)art[2],
                    (String)art[1],
                    (BigDecimal)art[3],
                    (BigDecimal)art[4],
                    (BigDecimal)art[5],
                    (boolean)art[8],
                    (BigDecimal)art[6]
            );
         return filaArticulo;
    }
    
     public void loadFromDataBaseCat(Integer codCat)throws Exception{
        
        int sortIndex = getSorIndex();
        int sortorder = mapSort.get(sortIndex);
        
        String sortord = sortorder==-1?"desc":"asc";
        String sortcolumn = getSortColumn(sortIndex);
        
        List<Object[]> arts = controller.listarRawCat(sortcolumn, sortord, codCat);
        
        items.clear();
        for(Object[] art: arts){
            items.add(getNewFromRow(art));
        }
        fireTableDataChanged();        
    }
     
     public void loadFromDataBaseFilter(String filtro)throws Exception{
        int sortIndex = getSorIndex();
        int sortorder = mapSort.get(sortIndex);
        
        String sortord = sortorder==-1?"desc":"asc";
        String sortcolumn = getSortColumn(sortIndex);
        
        List<Object[]> arts = controller.listarRaw(sortcolumn, sortord, filtro);        
        items.clear();
        
        for(Object[] art: arts){            
           items.add(getNewFromRow(art));
        }            
        
        fireTableDataChanged();        
    }    
     
      public void saveAllRecords(){
        try{                //actualizar en la base de datos
            for(FilaArticulo filafactura: items){
                if (filafactura.getArtId()>0){
                    controller.actualizarArticulo(filafactura);
                }                
            }
            
            JOptionPane.showMessageDialog(null,"Guardado");            
            loadFromDataBase();

        }
        catch(Throwable ex){
            showInfoError(ex);
            JOptionPane.showMessageDialog(null, "ERRO:"+ex.getMessage());
        }
    }
      
      public void showInfoError(Throwable ex){
          System.out.println("Error:"+ex.getMessage());
          ex.printStackTrace();
      }
      
      
      @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex>=0 && rowIndex<items.size()){
            FilaArticulo filafactura = items.get(rowIndex);
            switch (columnIndex){                
                case 1: {
                    filafactura.setCodBarra(aValue.toString());
                    break;
                }
                case 2: {
                    filafactura.setNombre(aValue.toString());
                    break;
                }
                case 3: {
                    BigDecimal precioCompra = new BigDecimal(aValue.toString());
                    filafactura.setPrecioCompra( precioCompra );
                    
                    break;
                }
                case 4:{
                    BigDecimal precioVenta = new BigDecimal(aValue.toString());
                    BigDecimal precioVentaSinIva = filafactura.getPrecioSinIva(precioVenta);                    
                    filafactura.setPrecioVenta( precioVentaSinIva );
                    break;
                }
                case 5: {
                    BigDecimal precioMin = new BigDecimal(aValue.toString());
                    BigDecimal precioMinSinIva = filafactura.getPrecioSinIva(precioMin);
                    filafactura.setPrecioMin( precioMinSinIva );
                    break;
                }
                case 6: {
                    boolean isIva = "SI".equalsIgnoreCase(aValue.toString());
                    filafactura.setIva(isIva);
                    break;
                }
                    
                case 7: {
                    filafactura.setInventario(new BigDecimal(aValue.toString()));
                    break;
                }
            }      
            
            try{
                if (filafactura.getArtId()>0){
                    controller.actualizarArticulo(filafactura);
                }
            }
            catch(Throwable ex){
                showInfoError(ex);
                JOptionPane.showMessageDialog(null, "ERRO:"+ex.getMessage());
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }
        super.setValueAt(aValue, rowIndex, columnIndex); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public Object getValueAtForAdmin(int rowIndex, int columnIndex){
        if (rowIndex>=0 && rowIndex<items.size()){
            FilaArticulo filaArticulo = items.get(rowIndex);            
            switch (columnIndex){
                case 0: return filaArticulo.getArtId();
                case 1: return filaArticulo.getCodBarra();
                case 2: return filaArticulo.getNombre();
                case 3: {
                    BigDecimal precioCompra = filaArticulo.getPrecioCompra().setScale(4, RoundingMode.HALF_UP);
                    return precioCompra;
                }
                case 4:{                    
                    //BigDecimal precioVenta = filaArticulo.getPrecioVenta().setScale(4, RoundingMode.HALF_UP);
                    BigDecimal precioVentaIva = filaArticulo.getPrecioVentaConIva().setScale(4, RoundingMode.HALF_UP);
                    return precioVentaIva;
                }
                case 5: {                    
                    //BigDecimal precioMin = filaArticulo.getPrecioMin().setScale(4, RoundingMode.HALF_UP);
                    BigDecimal precioMinIva = filaArticulo.getPrecioMinConIva().setScale(4, RoundingMode.HALF_UP);
                    return precioMinIva;                    
                }
                case 6: return filaArticulo.isIva()?"SI":"NO";                
                case 7: return filaArticulo.getInventario();
                default: return "";
            }
        }
        else{
            return "";
        }        
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {        
        
        return getValueAtForAdmin(rowIndex, columnIndex);
        
    }
    
    
    public boolean isCellEditableForAdmin(int rowIndex, int columnIndex) {        
        switch(columnIndex){
            case 1: return false;
            case 2: return true;
            case 3: return true;
            case 4: return true;
            case 5: return true;
            case 6: return true;
            case 7: return true;
            default: return false;
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) { 
        
        
        return isCellEditableForAdmin(rowIndex, columnIndex);
        
        
    }
    
    public Class<?> getColumnClassForAdmin(int columnIndex) {        
        switch(columnIndex){
            case 0: return Integer.class;//cantidad
            case 1: return String.class;//iva
            case 2: return String.class;//precioUnitario            
            case 3: return BigDecimal.class;
            case 4: return BigDecimal.class;
            case 5: return BigDecimal.class;//precioUnitario            
            case 6: return String.class;
            case 7: return BigDecimal.class;//precioUnitario                        
            default: return Object.class;
        }
    }
    
     @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getColumnClassForAdmin(columnIndex);

    }
    
    public FilaArticulo getFila(int rowIndex){
        return items.get(rowIndex);
    }
    
     public List<FilaArticulo> getItems() {
        return items;
    }

    public void setItems(List<FilaArticulo> items) {
        this.items = items;
    }

    public ArticulosJpaController getController() {
        return controller;
    }

    public void setController(ArticulosJpaController controller) {
        this.controller = controller;
    }
    
    
}
