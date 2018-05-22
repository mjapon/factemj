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
 * @author manuel.japon
 */
public class FilaArticulo {
    
    private Integer artId;
    private Integer secuencial;
    private String codBarra;
    private String nombre;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal precioMin;
    private boolean iva;
    private BigDecimal inventario;
    private String tipo;
    private String categoria;
    private Integer catId;
    
    public FilaArticulo(Integer artId, String codBarra, String nombre, 
            BigDecimal precioCompra, BigDecimal precioVenta, BigDecimal precioMin, 
            boolean iva, BigDecimal inventario, String categoria, String tipo, 
            Integer catid ) {
        this.artId = artId;
        this.secuencial = 0;
        this.codBarra = codBarra;
        this.nombre = nombre;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.precioMin = precioMin;
        this.iva = iva;
        this.inventario = inventario;
        this.tipo = tipo;
        this.categoria = categoria;
        this.catId = catid;
    }
    
    public BigDecimal getPrecioSinIva(BigDecimal value){
        if (iva){
            BigDecimal div = new BigDecimal(1).add(new BigDecimal(CtesU.IVA));
            BigDecimal factor = new BigDecimal(1).divide( div, 6, RoundingMode.HALF_UP );            
            return value.multiply(factor);
        }
        return value;
    }
    
    public BigDecimal getPrecioConIva(BigDecimal value){    
        if (iva){
            BigDecimal factor = new BigDecimal(1).add( new BigDecimal(CtesU.IVA) );
            //System.out.println("getPrecioConIva factor:" + factor);
            return value.multiply(factor);
        }
        return value;
    }
    
    public BigDecimal getPrecioCompraSinIva(){
        return getPrecioSinIva(this.precioCompra);
    }
    
    public BigDecimal getPrecioVentaSinIva(){
        return getPrecioSinIva(this.precioVenta);
    }
    
    public BigDecimal getPrecioMinSinIva(){
        return getPrecioSinIva(this.precioMin);
    }
    
    
    
    public BigDecimal getPrecioCompraConIva(){
        return getPrecioConIva(this.precioCompra);
    }
    
    public BigDecimal getPrecioVentaConIva(){
        return getPrecioConIva(this.precioVenta);
    }
    
    public BigDecimal getPrecioMinConIva(){
        return getPrecioConIva(this.precioMin);
    }
    
    
    public Integer getSecuencial() {
        return secuencial;
    }

    public void setSecuencial(Integer secuencial) {
        this.secuencial = secuencial;
    }

    public String getCodBarra() {
        return codBarra;
    }

    public void setCodBarra(String codBarra) {
        this.codBarra = codBarra;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getPrecioMin() {
        return precioMin;
    }

    public void setPrecioMin(BigDecimal precioMin) {
        this.precioMin = precioMin;
    }

    public boolean isIva() {
        return iva;
    }

    public void setIva(boolean iva) {
        this.iva = iva;
    }

    public BigDecimal getInventario() {
        return inventario;
    }

    public void setInventario(BigDecimal inventario) {
        this.inventario = inventario;
    }

    public Integer getArtId() {
        return artId;
    }

    public void setArtId(Integer artId) {
        this.artId = artId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }    

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }
    
    
    
}