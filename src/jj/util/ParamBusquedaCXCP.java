/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.util;

import java.util.Date;

/**
 *
 * @author mjapon
 */
public class ParamBusquedaCXCP {
    
    private Date desde;
    private Date hasta;
    private Integer cliId;
    private Integer artId;
    private Integer estadoPago;
    
    private String sortColumn;
    private String sortOrder;

    public ParamBusquedaCXCP() {
        
    }

    public ParamBusquedaCXCP(Date desde, Date hasta, Integer cliId, Integer artId, 
            String sortColumn, String sortOrder, Integer estadoPago) {
        this.desde = desde;
        this.hasta = hasta;
        this.cliId = cliId;
        this.artId = artId;
        this.sortColumn = sortColumn;
        this.sortOrder = sortOrder;
        this.estadoPago = estadoPago;
    }
    

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public Integer getCliId() {
        return cliId;
    }

    public void setCliId(Integer cliId) {
        this.cliId = cliId;
    }

    public Integer getArtId() {
        return artId;
    }

    public void setArtId(Integer artId) {
        this.artId = artId;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(Integer estadoPago) {
        this.estadoPago = estadoPago;
    }
}