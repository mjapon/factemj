/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.util.datamodels;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author mjapon
 */
public class JTCList {
    
    private List<JTableColumn> columnsList; 
    
    private JTableColumn<BigDecimal> column = new JTableColumn("Iva", 0, "iva");
    
    private List<FilaCajas> items;
    
    public void init(){
        column.setGsValueDlg(new IGetSetValueJC() {
            @Override
            public Object getValueAt(int rowIndex) {
                FilaCajas fila = items;
            }

            @Override
            public void setValueAt(int rowIndex, Object value) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
    }
    
    
    
}
