/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jj.entity.Facturas;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import jj.controller.exceptions.IllegalOrphanException;
import jj.controller.exceptions.NonexistentEntityException;
import jj.entity.Clientes;

/**
 *
 * @author mjapon
 */
public class ClientesJpaController extends BaseJpaController<Clientes> implements Serializable {

    public ClientesJpaController(EntityManager em) {
        super(em);
    }    

    
    public Clientes findByCi(String ci){        
        String querystr = "from Clientes a where a.cliCi = '"+ci.trim()+"'";      
        Query query = em.createQuery(querystr);
        
        List<Clientes> clientes = query.getResultList();
        if (clientes.size()>0){
            return clientes.get(0);
        }
        else{
            return null;
        }
    }
    
    public Clientes findById(Integer clienteId){
        Query query = newQuery("from Clientes a where a.cliId = "+ clienteId);        
        return getResultFirst(query);
        
    }
    
    public void create(Clientes clientes) {
        if (clientes.getFacturasList() == null) {
            clientes.setFacturasList(new ArrayList<Facturas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Facturas> attachedFacturasList = new ArrayList<Facturas>();
            for (Facturas facturasListFacturasToAttach : clientes.getFacturasList()) {
                facturasListFacturasToAttach = em.getReference(facturasListFacturasToAttach.getClass(), facturasListFacturasToAttach.getFactId());
                attachedFacturasList.add(facturasListFacturasToAttach);
            }
            clientes.setFacturasList(attachedFacturasList);
            em.persist(clientes);
            for (Facturas facturasListFacturas : clientes.getFacturasList()) {
                Clientes oldCliIdOfFacturasListFacturas = facturasListFacturas.getCliId();
                facturasListFacturas.setCliId(clientes);
                facturasListFacturas = em.merge(facturasListFacturas);
                if (oldCliIdOfFacturasListFacturas != null) {
                    oldCliIdOfFacturasListFacturas.getFacturasList().remove(facturasListFacturas);
                    oldCliIdOfFacturasListFacturas = em.merge(oldCliIdOfFacturasListFacturas);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Clientes clientes) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Clientes persistentClientes = em.find(Clientes.class, clientes.getCliId());
            List<Facturas> facturasListOld = persistentClientes.getFacturasList();
            List<Facturas> facturasListNew = clientes.getFacturasList();
            List<String> illegalOrphanMessages = null;
            for (Facturas facturasListOldFacturas : facturasListOld) {
                if (!facturasListNew.contains(facturasListOldFacturas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Facturas " + facturasListOldFacturas + " since its cliId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Facturas> attachedFacturasListNew = new ArrayList<Facturas>();
            for (Facturas facturasListNewFacturasToAttach : facturasListNew) {
                facturasListNewFacturasToAttach = em.getReference(facturasListNewFacturasToAttach.getClass(), facturasListNewFacturasToAttach.getFactId());
                attachedFacturasListNew.add(facturasListNewFacturasToAttach);
            }
            facturasListNew = attachedFacturasListNew;
            clientes.setFacturasList(facturasListNew);
            clientes = em.merge(clientes);
            for (Facturas facturasListNewFacturas : facturasListNew) {
                if (!facturasListOld.contains(facturasListNewFacturas)) {
                    Clientes oldCliIdOfFacturasListNewFacturas = facturasListNewFacturas.getCliId();
                    facturasListNewFacturas.setCliId(clientes);
                    facturasListNewFacturas = em.merge(facturasListNewFacturas);
                    if (oldCliIdOfFacturasListNewFacturas != null && !oldCliIdOfFacturasListNewFacturas.equals(clientes)) {
                        oldCliIdOfFacturasListNewFacturas.getFacturasList().remove(facturasListNewFacturas);
                        oldCliIdOfFacturasListNewFacturas = em.merge(oldCliIdOfFacturasListNewFacturas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = clientes.getCliId();
                if (findClientes(id) == null) {
                    throw new NonexistentEntityException("The clientes with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Clientes clientes;
            try {
                clientes = em.getReference(Clientes.class, id);
                clientes.getCliId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The clientes with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Facturas> facturasListOrphanCheck = clientes.getFacturasList();
            for (Facturas facturasListOrphanCheckFacturas : facturasListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Clientes (" + clientes + ") cannot be destroyed since the Facturas " + facturasListOrphanCheckFacturas + " in its facturasList field has a non-nullable cliId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(clientes);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Clientes> findClientesEntities() {
        return findClientesEntities(true, -1, -1);
    }

    public List<Clientes> findClientesEntities(int maxResults, int firstResult) {
        return findClientesEntities(false, maxResults, firstResult);
    }

    private List<Clientes> findClientesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Clientes.class));
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

    public Clientes findClientes(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Clientes.class, id);
        } finally {
            em.close();
        }
    }

    public int getClientesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Clientes> rt = cq.from(Clientes.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    
    
}
