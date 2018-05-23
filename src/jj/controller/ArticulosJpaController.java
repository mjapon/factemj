/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jj.controller.exceptions.NonexistentEntityException;
import jj.entity.Articulos;
import jj.entity.Secuencias;
import jj.util.ErrorValidException;
import jj.util.datamodels.rows.FilaArticulo;
import sun.security.pkcs11.Secmod;

/**
 *
 * @author mjapon
 */
public class ArticulosJpaController extends BaseJpaController implements Serializable {
    
    private String baseSqlArts = "select "+
                "o.art_id,"+
                "o.art_nombre,"+
                "o.art_codbar,"+
                "o.art_preciocompra,"+
                "o.art_precio,"+
                "o.art_preciomin,"+
                "o.art_inv,"+
                "o.prov_id,"+
                "o.art_iva,"+
                "o.unid_id,"+
                "c.cat_name, "+
                "o.art_tipo "+
                "from articulos o left join categorias c on o.cat_id = c.cat_id ";
    
    public ArticulosJpaController(EntityManager em){
        super(em);
    }

    public void create(Articulos articulos) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(articulos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public boolean articuloEnUso(Integer artId){
        Integer result = runCountQuery("select count(*) from detallesfact where art_id= "+artId);
        return result>0;
    }

    public void edit(Articulos articulos) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            articulos = em.merge(articulos);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = articulos.getArtId();
                if (findArticulos(id) == null) {
                    throw new NonexistentEntityException("The articulos with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws Throwable {    
        try {
            beginTrans();
            Articulos articulos;
            try {
                articulos = em.getReference(Articulos.class, id);                
                //Verificar articulo en uso
                if (articuloEnUso(id)){
                    throw  new ErrorValidException("El artículo "+ articulos.getArtNombre()+" esta siendo usado en alguna transacción, no se puede eliminar");
                }
                
                articulos.getArtId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The articulos with id " + id + " no longer exists.", enfe);
            }
            em.remove(articulos);
            commitTrans();
        }
        catch(Throwable ex){
            logErrorWithThrow(ex);
        }
        
    }

    public List<Articulos> findArticulosEntities() {
        return findArticulosEntities(true, -1, -1);
    }

    public List<Articulos> findArticulosEntities(int maxResults, int firstResult) {
        return findArticulosEntities(false, maxResults, firstResult);
    }

    private List<Articulos> findArticulosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Articulos.class));
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

    public Articulos findArticulos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Articulos.class, id);
        } finally {
            //em.close();
        }
    }

    public int getArticulosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Articulos> rt = cq.from(Articulos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<Articulos> findByBarcode(String barcode){
        String querystr = "from Articulos a where a.artCodbar = '"+barcode.trim().toUpperCase()+"'";
        Query query = em.createQuery(querystr);
        return query.getResultList();
        
    }
    
    public void decrementInv(Integer artCodigo, BigDecimal cant){
        Articulos articulo = em.find(Articulos.class, artCodigo);        
        if (articulo != null){
            System.out.println("El articulo es distinto de null-->");
            
            BigDecimal artInv = articulo.getArtInv();
            BigDecimal newInv = BigDecimal.ZERO;
            if (artInv.compareTo(BigDecimal.ZERO)>0){
                newInv = artInv.subtract(cant);
            }            
            
            articulo.setArtInv(newInv);            
            em.persist(articulo);
        }
    }
    
    public void incrementInv(Integer artCodigo, BigDecimal cant){
        Articulos articulo = em.find(Articulos.class, artCodigo);        
        if (articulo != null){            
            BigDecimal artInv = articulo.getArtInv();
            BigDecimal newInv = BigDecimal.ZERO;
            if (artInv.compareTo(BigDecimal.ZERO)>=0){
                System.out.println("El inventario es >= cero se suma al invetario actual");
                newInv = artInv.add(cant);
            }
            else{
                System.out.println("El inventario es menor ha cero se establece el inventario en el monto que viene en la compra");
                newInv = artInv;
            }
            
            System.out.println("El nuevo inventario es-->" + newInv);
            
            articulo.setArtInv(newInv);            
            em.persist(articulo);
        }
    }
    
    public void updatePrecioCompra(Integer artCodigo, BigDecimal newPrecioCompra){
        Articulos articulo = em.find(Articulos.class, artCodigo);
        if (articulo != null){
            System.out.println("El articulo es distinto de null-->");
            if (newPrecioCompra != null){
                articulo.setArtPrecioCompra(newPrecioCompra);
                em.persist(articulo);
            }
        }
    }
    
    /*
    public Integer crearArticulo(FilaArticulo filaArt, boolean genCodBarra) throws Exception{
        if (genCodBarra){
            SecuenciasJpaController secuenciasJpaController = new SecuenciasJpaController(em);
            
            Secuencias secuencia = secuenciasJpaController.getSecuencia("ART_CODBAR");
            if (secuencia != null){                
                filaArt.setCodBarra(String.valueOf(secuencia.getSecValor()));
                Integer res = crearArticulo(filaArt);                
                secuenciasJpaController.genSecuencia(secuencia.getSecId());
                return res;
            }            
        }
        else{
            return crearArticulo(filaArt);
        }
        
        return null;
    }
    */
    
    public Integer crearArticulo(FilaArticulo filaArt, boolean genCodBarra) throws Exception{
        try{
            em.getTransaction().begin();
            
            Articulos articulo = new Articulos();
            
            SecuenciasJpaController secuenciasJpaController = new SecuenciasJpaController(em);
            Secuencias secuencia = secuenciasJpaController.getSecuencia("ART_CODBAR");
            if (genCodBarra){                                
                filaArt.setCodBarra(String.valueOf(secuencia.getSecValor()));
            }            
            
            if (yaExisteBarcode(filaArt.getCodBarra())){
                throw new ErrorValidException("El código de barra:"+filaArt.getCodBarra()+" ya ha sido registrado, no se puede actualizar");
            }
            
            articulo.setArtCodbar(filaArt.getCodBarra().trim().toUpperCase());
            articulo.setArtInv(filaArt.getInventario());
            articulo.setArtIva(filaArt.isIva());
            articulo.setArtNombre(filaArt.getNombre().trim().toUpperCase());
            articulo.setProvId(1);
            articulo.setArtPrecio(filaArt.getPrecioVenta());
            articulo.setArtPrecioCompra(filaArt.getPrecioCompra());
            articulo.setArtPreciomin(filaArt.getPrecioMin());
            articulo.setUnidId(1);
            articulo.setArtTipo(filaArt.getTipo());
            articulo.setCatId(filaArt.getCatId());

            em.persist(articulo);
                        
            if (genCodBarra){
                secuenciasJpaController.genSecuencia("ART_CODBAR");
            }
            
            if ("S".equalsIgnoreCase(filaArt.getTipo())){
                SecuenciasJpaController secuenciasController = new SecuenciasJpaController(em);
                secuenciasController.genSecuencia("SEC_SERVS");
            }
            
            em.flush();
            
            em.getTransaction().commit();
            return articulo.getArtId();
        }
        catch(Throwable ex){
            System.out.println("Error al registrar articulo:"+ex.getMessage());
            ex.printStackTrace();
            throw  new Exception("Error al registrar articulo:"+ex.getMessage());
        }        
    }
    
    public void cambiarCategoria(Integer artId, Integer newCatId){
        
        try{
            beginTrans();
            
            Articulos art = em.find(Articulos.class, artId);
            if (art!= null){
                art.setCatId(newCatId);
            }
            
            em.persist(art);
            //em.flush();
            commitTrans();
            
        }
        catch(Throwable ex){
            logError(ex);
            throw  new ErrorValidException(ex.getMessage());
        }
        
        
    }
    
    public void actualizarArticulo(FilaArticulo filaArt) throws Throwable{
        
         try{
            em.getTransaction().begin();
            if (filaArt.getArtId()>0){
                Articulos articulo = findArticulos(filaArt.getArtId());
                if (articulo != null){
                    
                    if (!filaArt.getCodBarra().trim().toUpperCase().equalsIgnoreCase(articulo.getArtCodbar().trim().toUpperCase())){
                        if (yaExisteBarcode(filaArt.getCodBarra())){
                            throw new ErrorValidException("El código de barra:"+filaArt.getCodBarra()+" ya ha sido registrado, no se puede actualizar");
                        }
                    }
                    
                    articulo.setArtCodbar(filaArt.getCodBarra().trim().toUpperCase());
                    articulo.setArtInv(filaArt.getInventario());
                    articulo.setArtIva(filaArt.isIva());
                    articulo.setArtNombre(filaArt.getNombre().toUpperCase());
                    articulo.setProvId(1);
                    articulo.setArtPrecio(filaArt.getPrecioVenta());
                    articulo.setArtPrecioCompra(filaArt.getPrecioCompra());
                    articulo.setArtPreciomin(filaArt.getPrecioMin());
                    articulo.setUnidId(1);

                    em.persist(articulo);
                    em.flush();
                    em.getTransaction().commit();
                }
                else{                      
                    //em.getTransaction().rollback();
                    throw  new Exception("No se encuentra registradoe la articulo con codigo:"+filaArt.getArtId());
                }
            }
            else{                
                throw new Exception("Este articulo aun no ha sido registrado");
            }
        }
        catch(Throwable ex){
            logErrorWithThrow(ex);            
            //throw  new Exception("Error al registrar articulo:"+ex.getMessage());
        }
    }
    
    public List<Object[]> listarRaw(String sortBy, String sortOrder) throws Exception{        
        String queryStr = baseSqlArts +" order by "+sortBy+" "+sortOrder;
        Query query = this.newNativeQuery(queryStr);
        return query.getResultList();
    }
    
     public List<Object[]> listarRawCat(String sortBy, String sortOrder, Integer catId) throws Exception{
        String whereCatId = " where o.cat_id = "+catId;
        if (catId == 0){//PARA EL CASO DE TODAS LAS CATEGORIAS
            whereCatId = "";
        }         
        String queryStr = baseSqlArts + whereCatId+" order by "+sortBy+" "+sortOrder;
        Query query = this.newNativeQuery(queryStr);
        return query.getResultList();
    }
    
    public List<Articulos> listar(String sortBy, String sortOrder) throws Exception{
        try{
            //artPrecioCompra
            String thequery = "from Articulos o order by "+sortBy+" "+sortOrder;            
            Query query = this.newQuery(thequery);
            return query.getResultList();
        }
        catch(Throwable ex){
            System.out.println("Error al listar los articulos:"+ex.getMessage());
            ex.printStackTrace();
            throw  new Exception("Error al listar los articulos:"+ex.getMessage());
        }
    }
    
    public List<Object[]> listarRaw(String sortBy, String sortOrder, String filtro) throws Exception{
        String queryStr = baseSqlArts+" where o.art_nombre like '%"+filtro.toUpperCase()+"%' or o.art_codbar like '%"+ filtro.toUpperCase() +"%' order by o."+sortBy+" "+sortOrder;
        
        System.out.println("listarRaw query->");
        System.out.println(queryStr);        
        Query query = this.newNativeQuery(queryStr);
        return query.getResultList();
    }
    
    public List<Articulos> listar(String sortBy, String sortOrder, String filtro) throws Exception{
        try{
            String thequery = "from Articulos o where o.artNombre like '%"+filtro.toUpperCase()+"%' or o.artCodbar like '%"+ filtro.toUpperCase() +"%' order by o."+sortBy+" "+sortOrder;
            Query query = this.newQuery(thequery);
            return query.getResultList();
        }
        catch(Throwable ex){
            logError(ex);
            throw  new Exception("Error al listar los articulos:"+ex.getMessage());
        }
    }
    
    public boolean yaExisteBarcode(String barcode){
        Query query = newQuery("from Articulos o where o.artCodbar = '"+barcode.trim().toUpperCase()+"'");
        List resultList = query.getResultList();        
        return resultList.size()>0;
    }
    
    public boolean yaExisteNombre(String nombre){
        Query query = newQuery("from Articulos o where o.artNombre = '"+nombre.trim().toUpperCase()+"'");
        List resultList = query.getResultList();        
        return resultList.size()>0;
    }
}
