/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.util.datamodels;

import java.math.BigDecimal;

/**
 *
 * @author mjapon
 */
public class TotalesVentasModel {
    
    private BigDecimal sumaIva = BigDecimal.ZERO;
    private BigDecimal sumaDesc= BigDecimal.ZERO;
    private BigDecimal sumaTotal= BigDecimal.ZERO;    
    private BigDecimal utilidades = BigDecimal.ZERO;
    private BigDecimal sumaEfectivo = BigDecimal.ZERO;
    private BigDecimal sumaCredito = BigDecimal.ZERO;
    private BigDecimal sumaSaldo = BigDecimal.ZERO;

    public TotalesVentasModel() {
       
    }

    public BigDecimal getSumaIva() {
        return sumaIva;
    }

    public void setSumaIva(BigDecimal sumaIva) {
        this.sumaIva = sumaIva;
    }

    public BigDecimal getSumaDesc() {
        return sumaDesc;
    }

    public void setSumaDesc(BigDecimal sumaDesc) {
        this.sumaDesc = sumaDesc;
    }

    public BigDecimal getSumaTotal() {
        return sumaTotal;
    }

    public void setSumaTotal(BigDecimal sumaTotal) {
        this.sumaTotal = sumaTotal;
    }

    public BigDecimal getUtilidades() {
        return utilidades;
    }

    public void setUtilidades(BigDecimal utilidades) {
        this.utilidades = utilidades;
    }

    public BigDecimal getSumaEfectivo() {
        return sumaEfectivo;
    }

    public void setSumaEfectivo(BigDecimal sumaEfectivo) {
        this.sumaEfectivo = sumaEfectivo;
    }

    public BigDecimal getSumaCredito() {
        return sumaCredito;
    }

    public void setSumaCredito(BigDecimal sumaCredito) {
        this.sumaCredito = sumaCredito;
    }
    public BigDecimal getSumaSaldo() {
        return sumaSaldo;
    }
    public void setSumaSaldo(BigDecimal sumaSaldo) {
        this.sumaSaldo = sumaSaldo;
    }
}
