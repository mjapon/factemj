/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mjapon
 */
@Entity
@Table(name = "cajas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cajas.findAll", query = "SELECT c FROM Cajas c")
    , @NamedQuery(name = "Cajas.findByCjId", query = "SELECT c FROM Cajas c WHERE c.cjId = :cjId")
    , @NamedQuery(name = "Cajas.findByCjUser", query = "SELECT c FROM Cajas c WHERE c.cjUser = :cjUser")
    , @NamedQuery(name = "Cajas.findByCjSaldoant", query = "SELECT c FROM Cajas c WHERE c.cjSaldoant = :cjSaldoant")
    , @NamedQuery(name = "Cajas.findByCjVentas", query = "SELECT c FROM Cajas c WHERE c.cjVentas = :cjVentas")
    , @NamedQuery(name = "Cajas.findByCjAbonoscxc", query = "SELECT c FROM Cajas c WHERE c.cjAbonoscxc = :cjAbonoscxc")
    , @NamedQuery(name = "Cajas.findByCjAbonoscxp", query = "SELECT c FROM Cajas c WHERE c.cjAbonoscxp = :cjAbonoscxp")
    , @NamedQuery(name = "Cajas.findByCjAnulados", query = "SELECT c FROM Cajas c WHERE c.cjAnulados = :cjAnulados")
    , @NamedQuery(name = "Cajas.findByCjObsaper", query = "SELECT c FROM Cajas c WHERE c.cjObsaper = :cjObsaper")
    , @NamedQuery(name = "Cajas.findByCjObscierre", query = "SELECT c FROM Cajas c WHERE c.cjObscierre = :cjObscierre")
    , @NamedQuery(name = "Cajas.findByCjFecaper", query = "SELECT c FROM Cajas c WHERE c.cjFecaper = :cjFecaper")
    , @NamedQuery(name = "Cajas.findByCjFeccierre", query = "SELECT c FROM Cajas c WHERE c.cjFeccierre = :cjFeccierre")
    , @NamedQuery(name = "Cajas.findByCjEstado", query = "SELECT c FROM Cajas c WHERE c.cjEstado = :cjEstado")
    , @NamedQuery(name = "Cajas.findByCjObsanul", query = "SELECT c FROM Cajas c WHERE c.cjObsanul = :cjObsanul")
    , @NamedQuery(name = "Cajas.findByCjUseranul", query = "SELECT c FROM Cajas c WHERE c.cjUseranul = :cjUseranul")})
public class Cajas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "cj_id")
    private Integer cjId;
    @Column(name = "cj_user")
    private Integer cjUser;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cj_saldoant")
    private BigDecimal cjSaldoant;
    @Column(name = "cj_saldoantchanca")
    private BigDecimal cjSaldoantchanca;
    @Column(name = "cj_ventas")
    private BigDecimal cjVentas;
    @Column(name = "cj_ventchanca")
    private BigDecimal cjventchanca;
    @Column(name = "cj_abonoscxc")
    private BigDecimal cjAbonoscxc;
    @Column(name = "cj_abonoscxp")
    private BigDecimal cjAbonoscxp;
    @Column(name = "cj_anulados")
    private BigDecimal cjAnulados;
    @Column(name = "cj_obsaper")
    private String cjObsaper;
    @Column(name = "cj_obscierre")
    private String cjObscierre;
    @Column(name = "cj_fecaper")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cjFecaper;
    
    @Column(name = "cj_feccierre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cjFeccierre;
    
    @Column(name = "cj_estado")
    private Integer cjEstado;
    
    @Column(name = "cj_obsanul")
    private String cjObsanul;
    
    @Column(name = "cj_useranul")
    private Integer cjUseranul;  
    
    @Column(name = "cj_saldo")
    private BigDecimal cjSaldo;
    
    @Column(name = "cj_saldochanca")
    private BigDecimal cjSaldoChanca;
    
    @Column(name = "cj_saldosp")
    private BigDecimal cjSaldoSP;
    
    @Column(name = "cj_ventsp")
    private BigDecimal cjVentsp;
    
    @Column(name = "cj_saldoantsp")
    private BigDecimal cjSaldoantsp;
    
    @Column(name = "cj_abonoscxcchanca")
    private BigDecimal cjAbonoscxcchanca;
    
    @Column(name = "cj_abonoscxpchanca")
    private BigDecimal cjAbonoscxpchanca;
    
    @Column(name = "cj_abonoscxcsp")
    private BigDecimal cjAbonoscxcsp;
    
    @Column(name = "cj_abonoscxpsp")
    private BigDecimal cjAbonoscxpsp;
    
    @Column(name = "cj_ajgen")
    private BigDecimal cjAjgen;
    
    @Column(name = "cj_ajchanca")
    private BigDecimal cjAjchanca;
    
    @Column(name = "cj_ajsp")
    private BigDecimal cjAjsp;

    public Cajas() {
    }

    public Cajas(Integer cjId) {
        this.cjId = cjId;
    }

    public Integer getCjId() {
        return cjId;
    }

    public void setCjId(Integer cjId) {
        this.cjId = cjId;
    }

    public Integer getCjUser() {
        return cjUser;
    }

    public void setCjUser(Integer cjUser) {
        this.cjUser = cjUser;
    }

    public BigDecimal getCjSaldoant() {
        return cjSaldoant;
    }

    public void setCjSaldoant(BigDecimal cjSaldoant) {
        this.cjSaldoant = cjSaldoant;
    }

    public BigDecimal getCjVentas() {
        return cjVentas;
    }

    public void setCjVentas(BigDecimal cjVentas) {
        this.cjVentas = cjVentas;
    }

    public BigDecimal getCjAbonoscxc() {
        return cjAbonoscxc;
    }

    public void setCjAbonoscxc(BigDecimal cjAbonoscxc) {
        this.cjAbonoscxc = cjAbonoscxc;
    }

    public BigDecimal getCjAbonoscxp() {
        return cjAbonoscxp;
    }

    public void setCjAbonoscxp(BigDecimal cjAbonoscxp) {
        this.cjAbonoscxp = cjAbonoscxp;
    }

    public BigDecimal getCjAnulados() {
        return cjAnulados;
    }

    public void setCjAnulados(BigDecimal cjAnulados) {
        this.cjAnulados = cjAnulados;
    }

    public String getCjObsaper() {
        return cjObsaper;
    }

    public void setCjObsaper(String cjObsaper) {
        this.cjObsaper = cjObsaper;
    }

    public String getCjObscierre() {
        return cjObscierre;
    }

    public void setCjObscierre(String cjObscierre) {
        this.cjObscierre = cjObscierre;
    }

    public Date getCjFecaper() {
        return cjFecaper;
    }

    public void setCjFecaper(Date cjFecaper) {
        this.cjFecaper = cjFecaper;
    }

    public Date getCjFeccierre() {
        return cjFeccierre;
    }

    public void setCjFeccierre(Date cjFeccierre) {
        this.cjFeccierre = cjFeccierre;
    }

    public Integer getCjEstado() {
        return cjEstado;
    }

    public void setCjEstado(Integer cjEstado) {
        this.cjEstado = cjEstado;
    }

    public String getCjObsanul() {
        return cjObsanul;
    }

    public void setCjObsanul(String cjObsanul) {
        this.cjObsanul = cjObsanul;
    }

    public Integer getCjUseranul() {
        return cjUseranul;
    }

    public void setCjUseranul(Integer cjUseranul) {
        this.cjUseranul = cjUseranul;
    }

    public BigDecimal getCjSaldo() {
        return cjSaldo;
    }

    public void setCjSaldo(BigDecimal cjSaldo) {
        this.cjSaldo = cjSaldo;
    }    

    public BigDecimal getCjSaldoantchanca() {
        return cjSaldoantchanca;
    }

    public void setCjSaldoantchanca(BigDecimal cjSaldoantchanca) {
        this.cjSaldoantchanca = cjSaldoantchanca;
    }

    public BigDecimal getCjventchanca() {
        return cjventchanca;
    }

    public void setCjventchanca(BigDecimal cjventchanca) {
        this.cjventchanca = cjventchanca;
    }

    public BigDecimal getCjSaldoChanca() {
        return cjSaldoChanca;
    }

    public void setCjSaldoChanca(BigDecimal cjSaldoChanca) {
        this.cjSaldoChanca = cjSaldoChanca;
    }    

    public BigDecimal getCjVentsp() {
        return cjVentsp;
    }

    public void setCjVentsp(BigDecimal cjVentsp) {
        this.cjVentsp = cjVentsp;
    }

    public BigDecimal getCjSaldoantsp() {
        return cjSaldoantsp;
    }

    public void setCjSaldoantsp(BigDecimal cjSaldoantsp) {
        this.cjSaldoantsp = cjSaldoantsp;
    }

    public BigDecimal getCjSaldoSP() {
        return cjSaldoSP;
    }

    public void setCjSaldoSP(BigDecimal cjSaldoSP) {
        this.cjSaldoSP = cjSaldoSP;
    }

    public BigDecimal getCjAbonoscxcchanca() {
        return cjAbonoscxcchanca;
    }

    public void setCjAbonoscxcchanca(BigDecimal cjAbonoscxcchanca) {
        this.cjAbonoscxcchanca = cjAbonoscxcchanca;
    }

    public BigDecimal getCjAbonoscxpchanca() {
        return cjAbonoscxpchanca;
    }

    public void setCjAbonoscxpchanca(BigDecimal cjAbonoscxpchanca) {
        this.cjAbonoscxpchanca = cjAbonoscxpchanca;
    }

    public BigDecimal getCjAbonoscxcsp() {
        return cjAbonoscxcsp;
    }

    public void setCjAbonoscxcsp(BigDecimal cjAbonoscxcsp) {
        this.cjAbonoscxcsp = cjAbonoscxcsp;
    }

    public BigDecimal getCjAbonoscxpsp() {
        return cjAbonoscxpsp;
    }

    public void setCjAbonoscxpsp(BigDecimal cjAbonoscxpsp) {
        this.cjAbonoscxpsp = cjAbonoscxpsp;
    }

    public BigDecimal getCjAjgen() {
        return cjAjgen;
    }

    public void setCjAjgen(BigDecimal cjAjgen) {
        this.cjAjgen = cjAjgen;
    }

    public BigDecimal getCjAjchanca() {
        return cjAjchanca;
    }

    public void setCjAjchanca(BigDecimal cjAjchanca) {
        this.cjAjchanca = cjAjchanca;
    }

    public BigDecimal getCjAjsp() {
        return cjAjsp;
    }

    public void setCjAjsp(BigDecimal cjAjsp) {
        this.cjAjsp = cjAjsp;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cjId != null ? cjId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cajas)) {
            return false;
        }
        Cajas other = (Cajas) object;
        if ((this.cjId == null && other.cjId != null) || (this.cjId != null && !this.cjId.equals(other.cjId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jj.entity.Cajas[ cjId=" + cjId + " ]";
    }
    
}
