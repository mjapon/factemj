/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Usuario
 */
public class FilaFactura {

    private Integer numFila;
    private Integer codigoArt;
    private String codBarra;
    private String nombreArt;
    private Double cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal precioCompra;
    private boolean isIva;
    private BigDecimal subtotal;
    private BigDecimal total;
    private BigDecimal valorIva;
    private BigDecimal descuento;
    private BigDecimal descuentoPorc;
    private BigDecimal precioMinimo;

    public FilaFactura(
            Integer numFila,
            Integer codigoArt,
            String codBarra,
            String nombreArt,
            Double cantidad,
            BigDecimal precioUnitario,
            boolean isIva,
            BigDecimal subtotal,
            BigDecimal total,
            BigDecimal precioMinimo,
            BigDecimal precioCompra) {
        this.numFila = numFila;
        this.codigoArt = codigoArt;
        this.codBarra = codBarra;
        this.nombreArt = nombreArt;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.isIva = isIva;
        this.subtotal = subtotal;
        this.total = total;
        this.valorIva = BigDecimal.ZERO;
        this.precioMinimo = precioMinimo;
        this.descuento = BigDecimal.ZERO;
        this.descuentoPorc = BigDecimal.ZERO;
        this.precioCompra = precioCompra;
    }

    public FilaFactura() {

    }

    public Integer getNumFila() {
        return numFila;
    }

    public void setNumFila(Integer numFila) {
        this.numFila = numFila;
    }

    public Integer getCodigoArt() {
        return codigoArt;
    }

    public void setCodigoArt(Integer codigoArt) {
        this.codigoArt = codigoArt;
    }

    public String getCodBarra() {
        return codBarra;
    }

    public void setCodBarra(String codBarra) {
        this.codBarra = codBarra;
    }

    public String getNombreArt() {
        return nombreArt;
    }

    public void setNombreArt(String nombreArt) {
        this.nombreArt = nombreArt;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public boolean isIsIva() {
        return isIva;
    }

    public void setIsIva(boolean isIva) {
        this.isIva = isIva;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void updateTotales() {
        //Se calcula el total de cada fila        
        this.subtotal = new BigDecimal(this.cantidad).multiply(this.precioUnitario);
        BigDecimal subtMinDesc = this.subtotal.subtract(this.descuento);
        this.subtotal.setScale(CtesU.NUM_DECIM_VIEW, BigDecimal.ROUND_HALF_UP);

        if (this.isIva) {
            this.valorIva = subtMinDesc.multiply(new BigDecimal(CtesU.IVA));
        } else {
            this.valorIva = BigDecimal.ZERO;
        }

        this.total = this.subtotal.add(this.valorIva).subtract(this.descuento);        
        this.descuento.setScale(CtesU.NUM_DECIM_VIEW, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getValorIva() {
        return valorIva;
    }

    public void setValorIva(BigDecimal valorIva) {
        this.valorIva = valorIva;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }
    
    //Calcula el descuento ha partir del porcentaje
    public void calcDescFromPorc(){   
        
        System.out.println("calcDescFromPorc------->");
        
        BigDecimal theSubtotal = this.subtotal;
        if (this.subtotal == null){
            theSubtotal = BigDecimal.ZERO;
        }        
        BigDecimal theDescValue = BigDecimal.ZERO;
        
        BigDecimal factorDesc = this.descuentoPorc.divide(new BigDecimal(100.0));
        if (factorDesc.compareTo(BigDecimal.ZERO)>=0){
            theDescValue = theSubtotal.multiply(factorDesc);
        }        
        theDescValue = theDescValue.setScale(4, RoundingMode.HALF_UP);        
        this.descuento = theDescValue;
    }
    
    public void calcPorcDescFromValue(){        
        
        System.out.println("calcPorcDescFromValue------->");
        BigDecimal theSubtotal = new BigDecimal(this.cantidad).multiply(this.precioUnitario);        
        theSubtotal.setScale(CtesU.NUM_DECIM_VIEW, BigDecimal.ROUND_HALF_UP);
        
        BigDecimal theDescPorc = BigDecimal.ZERO;
        
        BigDecimal descValue = this.descuento;
        if (descValue == null){
            descValue = BigDecimal.ZERO;
        }
        
        if (theSubtotal.compareTo(BigDecimal.ZERO)>0){            
            System.out.println("Valor de descvalue es:"+descValue.toPlainString());
            
            BigDecimal descPorc = descValue.divide(theSubtotal, 4, RoundingMode.HALF_UP);
            System.out.println("porcentaje descuento = "+ descPorc.toPlainString());
            theDescPorc = descPorc.multiply(new BigDecimal("100.00"));
            theDescPorc = theDescPorc.setScale(2, RoundingMode.HALF_UP);
            
            System.out.println("porcentaje des calc es:"+ theDescPorc.toPlainString());
        }        
        
        this.descuentoPorc = theDescPorc;        
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;        
        System.out.println("Se calcula el porcentaje en funcion del valor");
        this.calcPorcDescFromValue();
    }

    public BigDecimal getPrecioMinimo() {
        return precioMinimo;
    }

    public void setPrecioMinimo(BigDecimal precioMinimo) {
        this.precioMinimo = precioMinimo;
    }

    public BigDecimal getDescuentoPorc() {
        return descuentoPorc;
    }

    public void setDescuentoPorc(BigDecimal descuentoPorc) {
        this.descuentoPorc = descuentoPorc;
        System.out.println("Se calcula el valor en funcion del porcentaje");
        this.calcDescFromPorc();
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

}
