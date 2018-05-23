/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.util.datamodels;

/**
 *
 * @author manuel.japon
 */
public class JTableColumn<T> {
    
    private String name;
    private Integer index;
    private T klassType;
    private String dbName;
    
    private IGetSetValueJC<T> gsValueDlg;
    

    public JTableColumn(String name, Integer index, String dbName) {
        this.name = name;
        this.index = index;
        //this.klass =;
        this.dbName = dbName;
    }
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public T getKlassType() {
        return klassType;
    }

    public void setKlassType(T klassType) {
        this.klassType = klassType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    
    public T getValueAt(int rowIndex){
        return gsValueDlg.getValueAt(rowIndex);
    }
    
    public void setValueAt(int rowIndex, T value){
        gsValueDlg.setValueAt(rowIndex, value);
    }

    public void setGsValueDlg(IGetSetValueJC gsValueDlg) {
        this.gsValueDlg = gsValueDlg;
    }
    
}
