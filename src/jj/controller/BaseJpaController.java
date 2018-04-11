/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.controller;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author mjapon
 */
public class BaseJpaController<T> {

    protected EntityManager em;

    public BaseJpaController(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return this.em;
    }

    public Query newQuery(String queryStr) {
        Query query = em.createQuery(queryStr);
        return query;
    }

    public List<T> getResultList(String queryStr) {
        Query query = newQuery(queryStr);
        return query.getResultList();
    }
    
    public Integer countResultList(String queryStr){
        List<T> result = getResultList(queryStr);
        return result!=null? result.size():0;
    }

    public T getResultFirst(Query query) {
        List<T> resultList = query.getResultList();
        if (resultList.size() > 0) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
    
    public T getResultFirst(String queryStr){
        Query query = newQuery(queryStr);
        List<T> resultList = query.getResultList();
        if (resultList.size() > 0) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
    
    /**
     * Imprimir la descripcion de la excepcion generada
     * @param ex 
     */
    public void logError(Throwable ex){
        System.out.println("Error en jpaController:"+ this.getClass().getName()+":"+ex.getMessage());
        ex.printStackTrace();
    }
    
    /**
     * Imprimir el mensaje de error enviado, seguido del mensaje de la expepcion
     * @param ex
     * @param logMessage 
     */
    public void logError(Throwable ex, String logMessage){
        System.out.println(logMessage+"->"+ex.getMessage());
        ex.printStackTrace();
    }
}
