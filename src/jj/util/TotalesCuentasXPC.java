/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.util;

import java.math.BigDecimal;

/**
 *
 * @author mjapon
 */
public class TotalesCuentasXPC {
    
    private BigDecimal sumaMonto;
    private BigDecimal sumaSaldoPend;
    
    public TotalesCuentasXPC(){
        
    }

    public TotalesCuentasXPC(BigDecimal sumaMonto, BigDecimal sumaSaldoPend) {
        this.sumaMonto = sumaMonto;
        this.sumaSaldoPend = sumaSaldoPend;
    }

    public BigDecimal getSumaMonto() {
        return sumaMonto;
    }

    public void setSumaMonto(BigDecimal sumaMonto) {
        this.sumaMonto = sumaMonto;
    }

    public BigDecimal getSumaSaldoPend() {
        return sumaSaldoPend;
    }

    public void setSumaSaldoPend(BigDecimal sumaSaldoPend) {
        this.sumaSaldoPend = sumaSaldoPend;
    }
}
