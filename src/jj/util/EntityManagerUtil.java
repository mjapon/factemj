/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author mjapon
 */
public class EntityManagerUtil {
    
    public static EntityManager createEntintyManagerFactory(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FARMAJJPU");
        EntityManager em = emf.createEntityManager();
        return em;
    }
    
}
