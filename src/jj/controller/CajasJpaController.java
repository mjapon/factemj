/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TemporalType;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jj.controller.exceptions.NonexistentEntityException;
import jj.entity.Cajas;
import jj.entity.Facturas;
import jj.util.ErrorValidException;
import jj.util.FechasUtil;
import jj.util.datamodels.rows.FilaCajas;

/**
 *
 * @author mjapon
 */
public class CajasJpaController extends BaseJpaController<Facturas> implements Serializable {

    
    public CajasJpaController(EntityManager em) {
        super(em);
    }
    
    public boolean existeCajaAbierta(Date dia){
        String query = String.format("select count(*) from cajas where cj_fecaper::date = to_date('%s','DD/MM/YYYY') and cj_estado = 0", FechasUtil.format(dia));
        Integer count = runCountQuery(query);
        return count>0;
    }
    
    public boolean existeCajaAbiertaMenorFecha(Date dia){
        String query = String.format("select count(*) from cajas where cj_fecaper::date < to_date('%s','DD/MM/YYYY') and cj_estado = 0", FechasUtil.format(dia));
        Integer count = runCountQuery(query);
        return count>0;
    }
    
    public Cajas getCajaAbiertaMenorFecha(Date dia){
        String nativeQuery = String.format("select cj_id from cajas where cj_fecaper::date < to_date('%s','DD/MM/YYYY') and cj_estado = 0 ", FechasUtil.format(dia));
        Integer cajaId = (Integer)newNativeQuery(nativeQuery).getSingleResult();
        if (cajaId != null){
            return em.find(Cajas.class, cajaId);
        }
        return null;
    }
    
    public Cajas getCajaCerradaMenorFecha(Date dia){
        String nativeQuery = String.format("select cj_id from cajas where cj_fecaper::date < to_date('%s','DD/MM/YYYY') and cj_estado = 1 order by cj_feccierre desc ", FechasUtil.format(dia));
        List<Integer> rs = newNativeQuery(nativeQuery).getResultList();
        if (rs.size()>0){
            Integer cajaId = (Integer)rs.get(0);
            if (cajaId != null){
                return em.find(Cajas.class, cajaId);
            }
        }
        
        return null;
    }
    
    public boolean hayCajaNoCerradaAyer(Date hoy){
        Date ayer =FechasUtil.sumarDia(hoy, -1);
        return existeCajaAbierta(ayer);
    }
    
    public boolean existeCajaAbierta(){
        String query = String.format("select count(*) from cajas where cj_estado = 0");
        Integer count = runCountQuery(query);        
        return count>0;
    }
    
    public Cajas getUltimaCajaCerrada(Date dia){
        return getCajaCerradaMenorFecha(dia);
    }
    
    public Cajas getCajaDiaAnterior(Date dia){
         Calendar calendar = Calendar.getInstance();
         calendar.add(Calendar.DAY_OF_MONTH, -1);
         Date ayer = calendar.getTime();
         
         String nativeQuery = String.format("select cj_id, cj_fecaper from cajas where cj_fecaper::date = to_date('%s','DD/MM/YYYY') and cj_estado = 1", FechasUtil.format(ayer));
         
         Object[] resultObj = getResultFirstNQ(nativeQuery);
         if (resultObj != null){
             Integer cj_id = (Integer)resultObj[0];
             Cajas caja = em.find(Cajas.class, cj_id);
             return caja;
         }
         else{
             return null;
         }
    }
    
    
    public Cajas getCajaDia(Date dia){        
         String nativeQuery = String.format("select cj_id, cj_fecaper  from cajas where cj_fecaper::date = to_date('%s','DD/MM/YYYY') and cj_estado != 2", FechasUtil.format(dia));
         
         Object[] resultObj = getResultFirstNQ(nativeQuery);
         if (resultObj != null){
             Integer cj_id = (Integer)resultObj[0];
             Cajas caja = em.find(Cajas.class, cj_id);
             return caja;
         }
         else{
             return null;
         }        
    }
    
    public void crearCaja(Date fechaApertura, BigDecimal saldoAnterior, String obs) throws Throwable{
        
        if (existeCajaAbierta(fechaApertura)){
           throw new ErrorValidException("Ya ha sido registrado la apertura de caja");
        }
        
        try{
            beginTrans();
        
            Cajas cajas = new Cajas();

            cajas.setCjFecaper(fechaApertura);
            cajas.setCjSaldoant(saldoAnterior);
            cajas.setCjObsaper(obs);
            cajas.setCjEstado(0);

            em.persist(cajas);

            commitTrans();
            
        }
        catch(Throwable ex){
            logErrorWithThrow(ex);
        }
        
    }
    
    public void cerrarCaja(Integer cjId,
            BigDecimal ventas,
            BigDecimal abonosCxC,
            BigDecimal abonosCxP,
            BigDecimal ventasAnuladas,
            BigDecimal saldo,
            String obsCierre
            ){
        
        beginTrans();
        Cajas caja = em.find(Cajas.class, cjId);
        if (caja == null){
            throw new ErrorValidException("No existe un registro de caja con el id:"+ cjId);
        }
        
        Integer estado = caja.getCjEstado();
        if (estado == 1){
            throw  new ErrorValidException( String.format("La caja ya ha sido cerrada fecha de cierre: %s", FechasUtil.format(caja.getCjFeccierre()) ) );
        }
        
        caja.setCjFeccierre(new Date());
        caja.setCjVentas(ventas);
        caja.setCjAbonoscxc(abonosCxC);
        caja.setCjAbonoscxp(abonosCxP);
        caja.setCjAnulados(ventasAnuladas);
        caja.setCjSaldo(saldo);
        caja.setCjObscierre(obsCierre);
        caja.setCjEstado(1);
        
        commitTrans();
    }
    
    public List<FilaCajas> listar(Date desde, Date hasta){
        
        String sql = "select cj_id,"+
                        "cj_saldoant,"+
                        "cj_ventas,"+
                        "cj_abonoscxc,"+
                        "cj_abonoscxp,"+
                        "cj_obsaper,"+
                        "cj_obscierre,"+
                        "cj_fecaper,"+
                        "cj_feccierre,"+
                          "cj_estado,"+
                        "case cj_estado when 0 then 'Abierto'"+
                           "            when 1 then 'Cerrado'"+
                           "            when 2 then 'Anulado' end as estado,"+
                        "cj_obsanul,"+
                        "cj_saldo from cajas where date(cj_fecaper) >= date(?paramDesde)"+
                        "AND date(cj_fecaper) <= date(?paramHasta)";
        
        Query query = this.newNativeQuery(sql);
        query = query.setParameter("paramDesde", desde, TemporalType.DATE);
        query = query.setParameter("paramHasta", hasta, TemporalType.DATE);
        
        List<Object[]> tmpList =  query.getResultList();
        List<FilaCajas> resultList = new ArrayList<>();
        
        for (Object[] obj: tmpList){
            
            Integer cj_id = (Integer)obj[0];
            BigDecimal cj_saldoant = (BigDecimal)obj[1];
            BigDecimal cj_ventas = (BigDecimal)obj[2];
            BigDecimal cj_abonoscxc = (BigDecimal)obj[3];
            BigDecimal cj_abonoscxp = (BigDecimal)obj[4];
            String cj_obsaper = (String)obj[5];
            String cj_obscierre = (String)obj[6];
            Date cj_fecaper = (Date)obj[7];
            Date cj_feccierre = (Date)obj[8];
            Integer cj_estado = (Integer)obj[9];
            String estado = (String)obj[10];
            String cj_obsanul = (String)obj[11];
            BigDecimal cj_saldo = (BigDecimal)obj[12];
            
            String fecAper = cj_fecaper!=null?FechasUtil.format(cj_fecaper):"";
            String fecCierre = cj_feccierre!=null?FechasUtil.format(cj_feccierre):"";
         
            FilaCajas filaCaja = new FilaCajas(cj_id, cj_saldoant, cj_ventas, cj_abonoscxc, cj_abonoscxp, cj_obsaper, cj_obscierre, fecAper, fecCierre, cj_estado, estado, cj_obsanul, cj_saldo);
            
            resultList.add(filaCaja);
        }
        
        return resultList;
        
    }

    public void create(Cajas cajas) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(cajas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cajas cajas) throws NonexistentEntityException, Exception {
        try {
            em.getTransaction().begin();
            cajas = em.merge(cajas);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cajas.getCjId();
                if (findCajas(id) == null) {
                    throw new NonexistentEntityException("The cajas with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cajas cajas;
            try {
                cajas = em.getReference(Cajas.class, id);
                cajas.getCjId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cajas with id " + id + " no longer exists.", enfe);
            }
            em.remove(cajas);
            em.getTransaction().commit();
        } finally {
            /*
            if (em != null) {
                em.close();
            }
            */
        }
    }

    public List<Cajas> findCajasEntities() {
        return findCajasEntities(true, -1, -1);
    }

    public List<Cajas> findCajasEntities(int maxResults, int firstResult) {
        return findCajasEntities(false, maxResults, firstResult);
    }

    private List<Cajas> findCajasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cajas.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Cajas findCajas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cajas.class, id);
        } finally {
            em.close();
        }
    }

    public int getCajasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cajas> rt = cq.from(Cajas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public Cajas findById(Integer factId){
        
        
        return em.find(Cajas.class, factId);
        
    }
    
    public void anularCaja(Integer cjId){
        
        try{
            
            beginTrans();
            Cajas caja = findById(cjId);
            
            if (caja != null){
                caja.setCjEstado(2);//1-->anulado
            }
            
            commitTrans();
        }
        catch(Throwable ex){
            logError(ex);
            throw new ErrorValidException("Error al tratar de anular caja:"+ex.getMessage());
        }
        
        
        
    }
    
}
