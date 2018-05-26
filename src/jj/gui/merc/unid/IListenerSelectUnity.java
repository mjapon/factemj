/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui.merc.unid;

import jj.util.datamodels.rows.FilaFactura;
import jj.util.datamodels.rows.FilaUnidadPrecio;

/**
 *
 * @author mjapon
 */
public interface IListenerSelectUnity {
    
    public void doSelect(FilaUnidadPrecio filaUnidadPrecio);
    
    public void doChangeDescuento(FilaFactura filaFactura);
    
}
