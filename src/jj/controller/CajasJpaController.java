/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jj.controller.exceptions.NonexistentEntityException;
import jj.entity.Cajas;
import jj.entity.Facturas;
import jj.util.ErrorValidException;
import jj.util.FechasUtil;

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
    
    public void crearCaja(Date fechaApertura, BigDecimal saldoAnterior, String obs){
        
        if (existeCajaAbierta(fechaApertura)){
           throw new ErrorValidException("Ya ha sido registrado la apertura de caja");
        }
        
        beginTrans();
        
        Cajas cajas = new Cajas();
        
        cajas.setCjFecaper(fechaApertura);
        cajas.setCjSaldoant(saldoAnterior);
        cajas.setCjObsaper(obs);
        cajas.setCjEstado(0);
        
        em.persist(cajas);
        
        commitTrans();
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
        EntityManager em = null;
        try {
            em = getEntityManager();
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
            if (em != null) {
                em.close();
            }
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
    
}
