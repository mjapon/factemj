/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jj.entity.Clientes;
import jj.entity.Detallesfact;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import jj.controller.exceptions.IllegalOrphanException;
import jj.controller.exceptions.NonexistentEntityException;
import jj.entity.Facturas;
import jj.entity.Pagosfact;
import jj.entity.Transacciones;
import jj.util.DatosCabeceraFactura;
import jj.util.ErrorValidException;
import jj.util.FechasUtil;
import jj.util.FilaFactura;
import jj.util.FilaPago;
import jj.util.ParamBusquedaCXCP;
import jj.util.ParamsBusquedaTransacc;
import jj.util.StringUtil;
import jj.util.TotalesFactura;

/**
 *
 * @author mjapon
 */
public class FacturasJpaController extends BaseJpaController<Facturas> implements Serializable {

    public FacturasJpaController(EntityManager em) {
        super(em);
    }

    public void create(Facturas facturas) {
        if (facturas.getDetallesfactList() == null) {
            facturas.setDetallesfactList(new ArrayList<Detallesfact>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Clientes cliId = facturas.getCliId();
            if (cliId != null) {
                cliId = em.getReference(cliId.getClass(), cliId.getCliId());
                facturas.setCliId(cliId);
            }
            List<Detallesfact> attachedDetallesfactList = new ArrayList<Detallesfact>();
            for (Detallesfact detallesfactListDetallesfactToAttach : facturas.getDetallesfactList()) {
                detallesfactListDetallesfactToAttach = em.getReference(detallesfactListDetallesfactToAttach.getClass(), detallesfactListDetallesfactToAttach.getDetfId());
                attachedDetallesfactList.add(detallesfactListDetallesfactToAttach);
            }
            facturas.setDetallesfactList(attachedDetallesfactList);
            em.persist(facturas);
            if (cliId != null) {
                cliId.getFacturasList().add(facturas);
                cliId = em.merge(cliId);
            }
            for (Detallesfact detallesfactListDetallesfact : facturas.getDetallesfactList()) {
                Facturas oldFactIdOfDetallesfactListDetallesfact = detallesfactListDetallesfact.getFactId();
                detallesfactListDetallesfact.setFactId(facturas);
                detallesfactListDetallesfact = em.merge(detallesfactListDetallesfact);
                if (oldFactIdOfDetallesfactListDetallesfact != null) {
                    oldFactIdOfDetallesfactListDetallesfact.getDetallesfactList().remove(detallesfactListDetallesfact);
                    oldFactIdOfDetallesfactListDetallesfact = em.merge(oldFactIdOfDetallesfactListDetallesfact);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Facturas facturas) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Facturas persistentFacturas = em.find(Facturas.class, facturas.getFactId());
            Clientes cliIdOld = persistentFacturas.getCliId();
            Clientes cliIdNew = facturas.getCliId();
            List<Detallesfact> detallesfactListOld = persistentFacturas.getDetallesfactList();
            List<Detallesfact> detallesfactListNew = facturas.getDetallesfactList();
            List<String> illegalOrphanMessages = null;
            for (Detallesfact detallesfactListOldDetallesfact : detallesfactListOld) {
                if (!detallesfactListNew.contains(detallesfactListOldDetallesfact)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detallesfact " + detallesfactListOldDetallesfact + " since its factId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (cliIdNew != null) {
                cliIdNew = em.getReference(cliIdNew.getClass(), cliIdNew.getCliId());
                facturas.setCliId(cliIdNew);
            }
            List<Detallesfact> attachedDetallesfactListNew = new ArrayList<Detallesfact>();
            for (Detallesfact detallesfactListNewDetallesfactToAttach : detallesfactListNew) {
                detallesfactListNewDetallesfactToAttach = em.getReference(detallesfactListNewDetallesfactToAttach.getClass(), detallesfactListNewDetallesfactToAttach.getDetfId());
                attachedDetallesfactListNew.add(detallesfactListNewDetallesfactToAttach);
            }
            detallesfactListNew = attachedDetallesfactListNew;
            facturas.setDetallesfactList(detallesfactListNew);
            facturas = em.merge(facturas);
            if (cliIdOld != null && !cliIdOld.equals(cliIdNew)) {
                cliIdOld.getFacturasList().remove(facturas);
                cliIdOld = em.merge(cliIdOld);
            }
            if (cliIdNew != null && !cliIdNew.equals(cliIdOld)) {
                cliIdNew.getFacturasList().add(facturas);
                cliIdNew = em.merge(cliIdNew);
            }
            for (Detallesfact detallesfactListNewDetallesfact : detallesfactListNew) {
                if (!detallesfactListOld.contains(detallesfactListNewDetallesfact)) {
                    Facturas oldFactIdOfDetallesfactListNewDetallesfact = detallesfactListNewDetallesfact.getFactId();
                    detallesfactListNewDetallesfact.setFactId(facturas);
                    detallesfactListNewDetallesfact = em.merge(detallesfactListNewDetallesfact);
                    if (oldFactIdOfDetallesfactListNewDetallesfact != null && !oldFactIdOfDetallesfactListNewDetallesfact.equals(facturas)) {
                        oldFactIdOfDetallesfactListNewDetallesfact.getDetallesfactList().remove(detallesfactListNewDetallesfact);
                        oldFactIdOfDetallesfactListNewDetallesfact = em.merge(oldFactIdOfDetallesfactListNewDetallesfact);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = facturas.getFactId();
                if (findFacturas(id) == null) {
                    throw new NonexistentEntityException("The facturas with id " + id + " no longer exists.");
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
            Facturas facturas;
            try {
                facturas = em.getReference(Facturas.class, id);
                facturas.getFactId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The facturas with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Detallesfact> detallesfactListOrphanCheck = facturas.getDetallesfactList();
            for (Detallesfact detallesfactListOrphanCheckDetallesfact : detallesfactListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Facturas (" + facturas + ") cannot be destroyed since the Detallesfact " + detallesfactListOrphanCheckDetallesfact + " in its detallesfactList field has a non-nullable factId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Clientes cliId = facturas.getCliId();
            if (cliId != null) {
                cliId.getFacturasList().remove(facturas);
                cliId = em.merge(cliId);
            }
            em.remove(facturas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Facturas> findFacturasEntities() {
        return findFacturasEntities(true, -1, -1);
    }

    public List<Facturas> findFacturasEntities(int maxResults, int firstResult) {
        return findFacturasEntities(false, maxResults, firstResult);
    }

    private List<Facturas> findFacturasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Facturas.class));
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

    public Facturas findFacturas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Facturas.class, id);
        } finally {
            em.close();
        }
    }
    
    public Facturas findById(Integer factId){
        
        
        return em.find(Facturas.class, factId);
        
    }

    public int getFacturasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Facturas> rt = cq.from(Facturas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public Map<String, Object> getDetallesFactura(Integer factId){
        Facturas factura = buscar(factId);
        
        if (factura == null){
            throw new ErrorValidException("No existe la factura con el id:"+factId);
        }
        
        DatosCabeceraFactura cabecera = new DatosCabeceraFactura();
        cabecera.setNumFactura( factura.getFactNum() );
        cabecera.setNroEstFact("");
        cabecera.setCliId(factura.getCliId().getCliId());
        cabecera.setCliente(factura.getCliId().getCliNombres());
        cabecera.setDireccion(factura.getCliId().getCliDir());
        cabecera.setTelf(factura.getCliId().getCliTelf());
        cabecera.setEmail(factura.getCliId().getCliEmail());
        cabecera.setFechaFactura( FechasUtil.format(factura.getFactFecha()));
        cabecera.setTraCodigo(factura.getTraId().getTraId());
                
        TotalesFactura totalesFactura = new TotalesFactura();
        totalesFactura.setSubtotal(factura.getFactSubt()  );
        totalesFactura.setIva(factura.getFactIva());
        totalesFactura.setTotal(factura.getFactTotal());
        totalesFactura.setDescuento(factura.getFactDesc());
        
        
        List<Object[]> detalles = listarDetalles(factId);
        
        List<FilaFactura> detallesList = new ArrayList<>();

        for (Object[] item: detalles){
            
            FilaFactura filafactura = new FilaFactura(1,
                (Integer)item[1],
                (String)item[7],
                (String)item[6],
                ((BigDecimal)item[3]).doubleValue(),
                (BigDecimal)item[2],
                    (Boolean)item[4],
                (BigDecimal)item[8],
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
            );
        
            filafactura.updateTotales();
            detallesList.add(filafactura);            
        }         
        
        
        Map<String, Object> datosFactura = new HashMap<String, Object>();
        datosFactura.put("cabecera", cabecera);
        datosFactura.put("totales", totalesFactura);
        datosFactura.put("detalles", detallesList);
        
        return datosFactura;
        
    }
    
    public List<Object[]> listarMovsAbonos(ParamBusquedaCXCP params){
        
        /*
        StringBuilder builder = new StringBuilder("select " +
                        " m.movFechareg as movFechareg, " +
                        " m.factIdRel.factNum as factNum, " +
                        " m.factIdRel.factTotal as factTotal, " +
                        " m.movMonto as movMonto, " +
                        " p.pgfSaldo as pgfSaldo, " +                        
                        " m.factIdRel.factId as factId " +                
                        " from Movtransacc m " +
                        " join Pagosfact p on m.pgfId = p.pgfId ");
        
        List<String> paramsList = new ArrayList<String>();
        
        paramsList.add("m.factIdRel.factValido = 0");
        paramsList.add("m.movValido = 0");        
        
        if (params.getDesde() != null){
            paramsList.add("year(m.movFechareg) >= year(:paramDesde) ");
            paramsList.add("month(m.movFechareg) >= month(:paramDesde) ");
            paramsList.add("day(m.movFechareg) >= day(:paramDesde) ");
        }
        if (params.getHasta() != null){
            //paramsList.add("DATE(m.movFechareg) <= :paramHasta ");
            
            paramsList.add("year(m.movFechareg) <= year(:paramHasta) ");
            paramsList.add("month(m.movFechareg) <= month(:paramHasta) ");
            paramsList.add("day(m.movFechareg) <= day(:paramHasta) ");
        }
        
        paramsList.add("m.traId.traId = "+params.getTra_codigo());
        
        String delimiter = " and ";
        String where = "";
            
        if (paramsList.size()>0){
            StringJoiner joiner = new StringJoiner(delimiter);
            for(String param: paramsList){
                joiner.add(param);
            }

            where = " where " + joiner.toString();
        }
        
        String baseQuery = builder.toString();
        StringBuilder orderSB = new StringBuilder("order by ");
        orderSB.append(params.getSortColumn());
        orderSB.append(" ");
        orderSB.append(params.getSortOrder());

        String queryStr = String.format("%s %s %s",  baseQuery, where, orderSB);
        
        System.out.println("Query movs:");
        System.out.println(queryStr);

        Query query = this.newQuery(queryStr.toString());        
            
        if (params.getDesde() != null){
            query = query.setParameter("paramDesde", params.getDesde(), TemporalType.DATE);
        }

        if (params.getHasta() != null){
            query = query.setParameter("paramHasta", params.getHasta(), TemporalType.DATE);
        }        
        
        return query.getResultList();
        */
        
        
        StringBuilder builder = new StringBuilder("select\n" +
        " date(m.mov_fechareg) as movFechareg,\n" +
        " f.fact_num as factNum,\n" +
        " f.fact_total as factTotal,\n" +
        " m.mov_monto as movMonto,\n" +
        " p.pgf_saldo as pgfSaldo,\n" +
        " f.fact_id as factId\n" +
        " from movtransacc m\n" +
        " join facturas f on f.fact_id = m.fact_id_rel\n" +
        " join pagosfact p on m.pgf_id = p.pgf_id");
        
        List<String> paramsList = new ArrayList<String>();
        
        paramsList.add("f.fact_valido = 0");
        paramsList.add("m.mov_valido = 0");        
        
        if (params.getDesde() != null){
            paramsList.add("date(m.mov_fechareg) >= date(?paramDesde) ");            
        }
        if (params.getHasta() != null){
            paramsList.add("date(m.mov_fechareg) <= date(?paramHasta) ");
        }
        
        paramsList.add("m.tra_id = "+params.getTra_codigo());
        
        String delimiter = " and ";
        String where = "";
            
        if (paramsList.size()>0){
            StringJoiner joiner = new StringJoiner(delimiter);
            for(String param: paramsList){
                joiner.add(param);
            }

            where = " where " + joiner.toString();
        }
        
        String baseQuery = builder.toString();
        StringBuilder orderSB = new StringBuilder("order by ");
        orderSB.append(params.getSortColumn());
        orderSB.append(" ");
        orderSB.append(params.getSortOrder());

        String queryStr = String.format("%s %s %s",  baseQuery, where, orderSB);
        
        System.out.println("Query movs:");
        System.out.println(queryStr);

        Query query = this.newNativeQuery(queryStr.toString());        
            
        if (params.getDesde() != null){
            query = query.setParameter("paramDesde", params.getDesde(), TemporalType.DATE);
        }

        if (params.getHasta() != null){
            query = query.setParameter("paramHasta", params.getHasta(), TemporalType.DATE);
        }        
        
        return query.getResultList();
        
        
    }
    
    
    public List<Object[]> listarCuentasXCP(ParamBusquedaCXCP params){
        
        StringBuilder baseQuery = new StringBuilder(" select "
                + " f.factId as factId, "
                + " f.factNum as factNum, "
                + " f.factFecreg as factFecreg,"
                + " f.factTotal as factTotal,"
                + " f.cliId.cliNombres as cliNombres,"
                + " f.cliId.cliCi as cliCi,"
                + " p.pgfFecreg as pgfFecreg,"
                + " p.pgfMonto as pgfMonto,"
                + " p.pgfSaldo as pgfSaldo,"
                + " p.pgfObs as pgfObs, "
                + " p.pgfId as pgfId, "
                + " case when p.pgfSaldo > 0.0 then 'PENDIENTE DE PAGO' else 'FACTURA CANCELADA' end as estadoDesc "
                + "  from Facturas f join Pagosfact p on p.factId.factId = f.factId and f.traId.traId= "+params.getTra_codigo());
        
        List<String> paramsList = new ArrayList<String>();
        
        paramsList.add("f.factValido = 0");
        
        if (params.getEstadoPago() == 1){//Cancelado
            paramsList.add("p.pgfSaldo = 0.0 and p.pgfMonto>0 and p.fpId.fpId = 2");
        }        
        else if (params.getEstadoPago() == 2){//Pendiente de pago
            paramsList.add("p.pgfSaldo > 0.0");
        }
        
        boolean searchByDate = true;
        
        if (params.getFiltro()!= null && params.getFiltro().trim().length()>4){
            searchByDate = false;
        }
        
        if (searchByDate){
            if (params.getDesde() != null){
                paramsList.add("f.factFecha >= :paramDesde ");
            }
            if (params.getHasta() != null){
                paramsList.add("f.factFecha <= :paramHasta ");
            }
        }
        else{
            paramsList.add(" (f.cliId.cliNombres like '%"+params.getFiltro().toUpperCase().trim()+"%' or f.cliId.cliCi like '%"+params.getFiltro().toUpperCase().trim()+"%') ");
        }
        
        if (params.getCliId() == 0){
            
        }
        else{
            
        }        
        
        String delimiter = " and ";
        String where = "";
            
        if (paramsList.size()>0){
            StringJoiner joiner = new StringJoiner(delimiter);
            for(String param: paramsList){
                joiner.add(param);
            }

            where = " where " + joiner.toString();
        }

        StringBuilder orderSB = new StringBuilder("order by ");
        orderSB.append(params.getSortColumn());
        orderSB.append(" ");
        orderSB.append(params.getSortOrder());

        String queryStr = String.format("%s %s %s",  baseQuery, where, orderSB);

        Query query = this.newQuery(queryStr.toString());
        
        if (searchByDate){
            if (params.getDesde() != null){
                query = query.setParameter("paramDesde", params.getDesde(), TemporalType.DATE);
            }

            if (params.getHasta() != null){
                query = query.setParameter("paramHasta", params.getHasta(), TemporalType.DATE);
            }
        }

        return query.getResultList();
    }
    
    public Map<Integer, BigDecimal> getEfectCreditVal(Integer factId){
        
        String query = new String("select o from Pagosfact o where o.factId.factId =  "+factId);
        
        List<Pagosfact> pagos = newQuery(query).getResultList();
        
        BigDecimal montoEfectivo = BigDecimal.ZERO;
        BigDecimal montoCredito = BigDecimal.ZERO;
        BigDecimal montoSaldo = BigDecimal.ZERO;
        
        if (pagos != null){
            
            for (Pagosfact pago: pagos){
                if (pago.getFpId().getFpId() == 1){
                    montoEfectivo = pago.getPgfMonto();
                }
                else if (pago.getFpId().getFpId() == 2){
                    montoCredito = pago.getPgfMonto();
                    montoSaldo = pago.getPgfSaldo();
                }
            }
        }
        
        Map<Integer, BigDecimal> mapResult = new HashMap<Integer, BigDecimal>();
        
        mapResult.put(1, montoEfectivo);
        mapResult.put(2, montoCredito);
        mapResult.put(3, montoSaldo);
        
        return mapResult;
        
    }
     
    public List<Object[]> listar(ParamsBusquedaTransacc params){
        
        boolean searchByArt = params.getArtId() != null && params.getArtId()>0;        
        
        
            StringBuilder baseQueryArts = new StringBuilder("select f.factId.factId,\n" +
                                                            "f.factId.factNum,\n" +
                                                            "f.detfCant*f.detfPrecio as subtotal,\n" +
                                                            "case  when f.detfIva=true then (f.detfCant*f.detfPrecio)*0.12 else 0.0 end as iva,\n" +
                                                            "f.detfCant*f.detfPrecio*f.detfDesc as descuento,\n" +
                                                            "0.0 as total,"+
                                                            "f.factId.factFecreg,\n" +
                                                            "f.factId.cliId.cliNombres,\n" +
                                                            "f.factId.cliId.cliCi,"+
                                                            "f.detfPreciocm,"+
                                                            "0.0 as efectivo, 0.0 as credito, 0.0 as saldo from Detallesfact f");

            //Columnas de la consulta
            StringBuilder baseQueryFact = new StringBuilder("select f.factId, \n" +
                                                        "f.factNum,\n" +
                                                        "f.factSubt,  \n" +
                                                        "f.factIva,\n" +
                                                        "f.factDesc,   \n" +
                                                        "f.factTotal,\n" +
                                                        "f.factFecreg,\n" +
                                                        "f.cliId.cliNombres,\n" +
                                                        "f.cliId.cliCi,"+
                                                        "0.0 as pc, 0.0 as efectivo, 0.0 as credito, 0.0 as saldo from Facturas f ");     
            
            String baseQuery = baseQueryFact.toString();
            String prefijo = "f";
            if (searchByArt){
                prefijo = "f.factId";
                baseQuery = baseQueryArts.toString();
            }
            
            //Parametros            
            List<String> paramsList = new ArrayList<String>();                        
            
            paramsList.add(prefijo+".factValido=0 ");
            
            paramsList.add(prefijo+".traId.traId= "+params.getTraCodigo());
            
            if (params.getDesde() != null){
                paramsList.add(prefijo+".factFecha >= :paramDesde ");
            }
            if (params.getHasta() != null){
                paramsList.add(prefijo+".factFecha <= :paramHasta ");
            }
            
            if (params.getCliId() != null && params.getCliId()>0){
                paramsList.add(prefijo+".cliId.cliId = :paramCliId");
            }
            if (params.getArtId() != null && params.getArtId()>0){
                //Se debe realizar la busqueda por el codigo del articulo
                paramsList.add("f.artId = :paramArtId");
            }       
            
            
            String delimiter = " and ";
            String where = "";
            
            if (paramsList.size()>0){
                StringJoiner joiner = new StringJoiner(delimiter);
                for(String param: paramsList){
                    joiner.add(param);
                }
                
                where = " where " + joiner.toString();
            }
            
            StringBuilder orderSB = new StringBuilder("order by ");
            orderSB.append(prefijo + "."+params.getSortColumn());
            orderSB.append(" ");
            orderSB.append(params.getSortOrder());
            
            String queryStr = String.format("%s %s %s",  baseQuery, where, orderSB);
            
            System.out.println("query--->listar:"+queryStr);
            
            Query query = this.newQuery(queryStr.toString());
            
            if (params.getDesde() != null){
                query = query.setParameter("paramDesde", params.getDesde(), TemporalType.DATE);
            }
            
            if (params.getHasta() != null){
                query = query.setParameter("paramHasta", params.getHasta(), TemporalType.DATE);
            }
            
            if (params.getCliId() != null && params.getCliId()>0){
                query = query.setParameter("paramCliId", params.getCliId());
            }
            
            if (params.getArtId() != null && params.getArtId()>0){
                query = query.setParameter("paramArtId", params.getArtId());
            }
            
            List<Object[]> resultList = query.getResultList();
            
            for (Object[] fila:  resultList){      
                Map<Integer, BigDecimal> efeccredmap =getEfectCreditVal((Integer)fila[0]);
                
                fila[10] = efeccredmap.get(1);
                fila[11] = efeccredmap.get(2);
                fila[12] = efeccredmap.get(3);
            }
            
            return resultList;
            
    }
    
    public BigDecimal getUtilidades(ParamsBusquedaTransacc params){
        
         StringBuilder baseQuery = new StringBuilder("select "
                 + "f.detfCant,"
                 + "f.detfPrecio, "
                 + "f.detfPreciocm, "                 
                 + "f.detfCant*f.detfPrecio as subtotal,\n"
                 + "case when f.detfIva=true then (f.detfCant*f.detfPrecio)*0.12 else 0.0 end as iva,\n"
                 + "f.detfCant*f.detfPrecio*f.detfDesc as descuento from Detallesfact f");
         
         
            List<String> paramsList = new ArrayList<String>();
            
            String prefijo = "f";
            
            paramsList.add(prefijo+".factId.factValido=0 ");
            
            if (params.getDesde() != null){
                paramsList.add(prefijo+".factId.factFecha >= :paramDesde ");
            }
            if (params.getHasta() != null){
                paramsList.add(prefijo+".factId.factFecha <= :paramHasta ");
            }
            
            if (params.getCliId() != null && params.getCliId()>0){
                paramsList.add(prefijo+".factId.cliId.cliId = :paramCliId");
            }
            if (params.getArtId() != null && params.getArtId()>0){
                //Se debe realizar la busqueda por el codigo del articulo
                paramsList.add("f.artId = :paramArtId");
            }            
            
            String delimiter = " and ";
            String where = "";
            
            if (paramsList.size()>0){
                StringJoiner joiner = new StringJoiner(delimiter);
                for(String param: paramsList){
                    joiner.add(param);
                }
                
                where = " where " + joiner.toString();
            }
            
            String orderSB = "";
            
            String queryStr = String.format("%s %s %s",  baseQuery, where, orderSB);
            
            Query query = this.newQuery(queryStr.toString());
            
            if (params.getDesde() != null){
                query = query.setParameter("paramDesde", params.getDesde(), TemporalType.DATE);
            }
            
            if (params.getHasta() != null){
                query = query.setParameter("paramHasta", params.getHasta(), TemporalType.DATE);
            }
            
            if (params.getCliId() != null && params.getCliId()>0){
                query = query.setParameter("paramCliId", params.getCliId());
            }
            
            if (params.getArtId() != null && params.getArtId()>0){
                query = query.setParameter("paramArtId", params.getArtId());
            }
            
            List<Object[]> result = query.getResultList();
            
            BigDecimal utilidades = BigDecimal.ZERO;
            
            for (Object[] fila: result){
                BigDecimal cant = (BigDecimal)fila[0];
                BigDecimal precio = (BigDecimal)fila[1];
                BigDecimal preciocm = (BigDecimal)fila[2];
                BigDecimal subtotal = (BigDecimal)fila[3];
                Double iva = (Double)fila[4];
                BigDecimal descuento = (BigDecimal)fila[5];
                BigDecimal costoFila = (cant.multiply(preciocm)).subtract(descuento);
                BigDecimal utilidadFila = subtotal.add(new BigDecimal(iva)).subtract(descuento).subtract(costoFila);
                
                utilidades = utilidades.add(utilidadFila);
            }
            
            System.out.println("Utilidades es:");
            System.out.println(utilidades.toPlainString());
            
            return utilidades.setScale(2, RoundingMode.HALF_UP);        
    }
    
    public List<Object[]> listarDetalles(Integer factId){
        
        try{
            StringBuilder builder = new StringBuilder();
            
            builder.append("select d.detfId," +
                            " d.artId," +
                            " d.detfPrecio," +
                            " d.detfCant," +
                            " d.detfIva," +
                            " d.detfDesc, " +
                            " art.artNombre, "+
                            " art.artCodbar, "+
                            " d.detfCant*d.detfPrecio as subt, "+
                            " (d.detfCant*d.detfPrecio)*d.detfDesc as descv, "+                            
                            " case when d.detfIva = 1 then ((d.detfCant*d.detfPrecio) - ((d.detfCant*d.detfPrecio)*d.detfDesc))*0.12 else 0.0 end as ivaval "+
                            "  from Detallesfact d join Articulos art on d.artId = art.artId where d.factId.factId= "+factId);
            
            
            Query query = this.newQuery(builder.toString());
            //query = query.setParameter("paramfecha", dia, TemporalType.DATE);
            
            System.out.println("query parameter res:");
            System.out.println(query);
            
            return query.getResultList();
            
        }
        catch(Throwable ex){
            System.out.println("Error al tratar de obtener detalles fact:"+ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
    
    public List<Object[]> getPagos(Integer facturaId){
        
        StringBuilder builder = new StringBuilder("select p.pgfId, "
                + "p.pgfMonto, "
                + "p.pgfSaldo, "
                + "p.pgfObs,"
                + "p.spId.spId,"
                + "p.fpId.fpId, "                
                + "p.fpId.fpNombre, "
                + "p.spId.spNombre from Pagosfact p where p.factId.factId = "+ facturaId);
        
        
        Query query = this.newQuery(builder.toString());
        //query = query.setParameter("paramfecha", dia, TemporalType.DATE);

        System.out.println("query parameter res:");
        System.out.println(query);

        return query.getResultList();
            
        
    }
    
    public List<Object[]> listar(Date dia, String sortColumn, String sortOrder) throws Exception{        
        
        try{            
            Calendar cal = Calendar.getInstance();
            cal.setTime(dia);

            //String diaStr = FechasUtil.format(dia);
            StringBuilder prequery = new StringBuilder("select f.factId, \n" +
                                                        "f.factNum,\n" +
                                                        "f.factSubt,  \n" +
                                                        "f.factIva,\n" +
                                                        "f.factDesc,   \n" +
                                                        "f.factTotal,\n" +
                                                        "f.factFecreg,\n" +
                                                        "f.cliId.cliNombres,\n" +
                                                        "f.cliId.cliCi from Facturas f where f.factValido = 0 ");
            
            //prequery = prequery.append("where f.factFecha = :paramfecha ")
            
            prequery =prequery.append(" order by ").append(sortColumn)
                    .append(" ")
                    .append(sortOrder);            
            //prequery = prequery.append(" order by ").append(sortColumn).append(" ").append(sortOrder);
            
            System.out.println("query");
            System.out.println(prequery.toString());            
            
            Query query = this.newQuery(prequery.toString());
            //query = query.setParameter("paramfecha", dia, TemporalType.DATE);
            
            System.out.println("query parameter res:");
            System.out.println(query);
            
            return query.getResultList();
        }
        catch(Throwable ex){
            System.out.println("Error al listar facturas:"+ex.getMessage());
            ex.printStackTrace();
            throw  new Exception("Error al listar facturas:"+ex.getMessage());
        }        
    }
    
    public void anularFactura(Integer factId){
        try{
            em.getTransaction().begin();
            Facturas factura = findById(factId);
            
            ArticulosJpaController articulosController = new ArticulosJpaController(em);
            
            boolean esFacturaVenta = false;
            boolean esFacturaCompra = false;
            
            if (factura != null){
                factura.setFactValido(1);//1-->anulado
                
                esFacturaVenta = factura.getTraId().getTraId() == 1;
                esFacturaCompra = factura.getTraId().getTraId() == 2;

                if (esFacturaCompra || esFacturaVenta){
                    //Se debe revertir los inventarios                
                    List<Detallesfact> detalles = getDetallesFact(factId);
                    for (Detallesfact det: detalles){
                        if (esFacturaVenta){
                            articulosController.incrementInv(det.getArtId(), det.getDetfCant());
                        }
                        else if (esFacturaCompra){
                            articulosController.decrementInv(det.getArtId(), det.getDetfCant());
                        }
                    }
                }
            }
            
            em.getTransaction().commit();
        }
        catch(Throwable ex){
            System.out.println("Error al tratar de anular la factura");
        }
    }
    
    public Integer getTraCodigo(Integer factId){
        try{
            //em.getTransaction().begin();
            Facturas factura = findById(factId);
            return factura.getTraId().getTraId();
            
            //em.getTransaction().commit();
        }
        catch(Throwable ex){
            System.out.println("Error al tratar de anular la factura");
        }
        return null;
    }
    
    /**
     * Verifia si un numero de factura válido ya ha sido utilizado
     */
    public boolean isNumFactReg(String numFactura){
        
        try{
            
            String query = String.format("from Facturas o where o.factNum = '%s' and o.factValido =0 ",numFactura);            
            Integer cuenta = countResultList(query);
            return cuenta>0;
            
        }
        catch(Throwable ex){
            System.out.println("Error al buscar factura:"+ ex.getMessage());
            ex.printStackTrace();
        }
        
        return false;
    }
    
    public Facturas buscar(Integer factId){        
        try{            
            return em.find(Facturas.class, factId);
        }
        catch(Throwable ex){
            System.out.println("Error al tratar de obtener los datos de la factura-->"+ex.getMessage());
            ex.printStackTrace();
        }
        
        return null;
    }
    
    public List<Detallesfact> getDetallesFact(Integer factId){
        try{            
            Query query = newQuery("from Detallesfact o where o.factId.factId = "+factId);            
            return query.getResultList();
        }
        catch(Throwable ex){
            System.out.println("Error al tratar de obtener los detalles de la factura:"+ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
    
    public void crearFactura(
            DatosCabeceraFactura datosCabecera, 
            TotalesFactura totalesFact, 
            List<FilaFactura> detalles,
            Map<Integer, FilaPago> pagosMap
            ) throws Exception{        
        
        try{
            em.getTransaction().begin();
            
            boolean esFacturaCompra = datosCabecera.getTraCodigo() == 2;
            boolean esFacturaVenta = datosCabecera.getTraCodigo() == 1;
            
            Integer tipoCliente = 1;//1-cliente, 2-proveedor
            if (esFacturaCompra){
                tipoCliente = 2;//Proveedor
            }
        
            ClientesJpaController clientesController = new ClientesJpaController(em);
            ArticulosJpaController articulosController = new ArticulosJpaController(em);

            Integer cli_codigo = datosCabecera.getCliId();
            Clientes clienteFactura = null;
            if (cli_codigo == null || cli_codigo == 0){                        //Se debe crear el cliente

                //Verificar si la cedula ingresada ya ha sido registrada:            
                String ci = datosCabecera.getCi();

                Clientes clientefind = clientesController.findByCi(ci);
                if (clientefind == null){
                    clienteFactura =  new Clientes();
                    clienteFactura.setCliNombres( datosCabecera.getCliente().trim().toUpperCase() );
                    clienteFactura.setCliCi(datosCabecera.getCi());
                    clienteFactura.setCliFechareg( new Date());
                    clienteFactura.setCliDir( datosCabecera.getDireccion() );
                    clienteFactura.setCliTelf( datosCabecera.getTelf());
                    clienteFactura.setCliEmail( datosCabecera.getEmail());
                    clienteFactura.setCliMovil( "" );    
                    clienteFactura.setCliTipo(tipoCliente);

                    em.persist(clienteFactura);
                    em.flush();
                }
                else{
                    clienteFactura = clientefind;
                    clienteFactura.setCliNombres( datosCabecera.getCliente().trim().toUpperCase() );
                    clienteFactura.setCliDir( datosCabecera.getDireccion() );
                    clienteFactura.setCliTelf( datosCabecera.getTelf());
                    clienteFactura.setCliEmail( datosCabecera.getEmail());
                }
            }
            else{
                //Buscar cliente registrado por codigo
                clienteFactura = clientesController.findById(cli_codigo);
                if (clienteFactura != null){
                    clienteFactura.setCliNombres( datosCabecera.getCliente().trim().toUpperCase() );
                    clienteFactura.setCliDir( datosCabecera.getDireccion() );
                    clienteFactura.setCliTelf( datosCabecera.getTelf());
                    clienteFactura.setCliEmail( datosCabecera.getEmail()); 
                }
            }

            Facturas factura = new Facturas();
            factura.setCliId(clienteFactura);

            String numeroFactura = "";
            if (esFacturaVenta){
                String secFactura = StringUtil.zfill(new Integer(datosCabecera.getNumFactura()), 9);
                numeroFactura = StringUtil.format("{0}{1}", 
                    datosCabecera.getNroEstFact(),
                    secFactura);
            }            
            else if (esFacturaCompra){
                numeroFactura = datosCabecera.getNumFactura();
            }
            

            factura.setFactNum(numeroFactura);
            factura.setFactEstab(1);
            factura.setFactPtoemi(1);
            factura.setFactSubt(totalesFact.getSubtotal());
            factura.setFactIva(totalesFact.getIva());
            factura.setFactTotal(totalesFact.getTotal());
            factura.setFactDesc(totalesFact.getDescuento());

            factura.setFactFecha(new Date());
            factura.setFactFecreg(new Date());
            factura.setFactValido(0);
            
            TransaccionesJpaController transaccionesJpaController = new TransaccionesJpaController(em);
            
            Transacciones transacc = transaccionesJpaController.findTransaccByTraId( datosCabecera.getTraCodigo() );
            factura.setTraId(transacc);

            factura.setUserId(1);
            em.persist(factura);

            //Registro de detalles de factura
            for (FilaFactura fila: detalles ){
                System.out.println("Registro de fila de la facrura");

                Detallesfact detalle = new Detallesfact();

                detalle.setFactId(factura);
                detalle.setArtId( fila.getCodigoArt() );
                detalle.setDetfPrecio(fila.getPrecioUnitario());
                detalle.setDetfCant(new BigDecimal(fila.getCantidad()));
                detalle.setDetfIva(fila.isIsIva());
                detalle.setDetfDesc(fila.getDescuento());
                detalle.setDetfPreciocm(fila.getPrecioCompra());

                //Se debe actualizar el inventario del articulo                
                if ( esFacturaVenta ){//factura de venta
                    articulosController.decrementInv(fila.getCodigoArt(), new BigDecimal(fila.getCantidad()));
                }
                else if (esFacturaCompra){//factura de compra
                    articulosController.incrementInv(fila.getCodigoArt(), new BigDecimal(fila.getCantidad()));
                    //Actualizar el precio de compra
                    articulosController.updatePrecioCompra(fila.getCodigoArt(), fila.getPrecioUnitario());
                }
                
                em.persist(detalle);
            }            
            //Crear pagos
            
            //Se registra pago en efectivo
            Pagosfact pagoEfectivo = new Pagosfact();            
            Pagosfact pagoCredito = new Pagosfact();
            
            EstadospagoJpaController estadoCtntrl = new EstadospagoJpaController(em);
            FormaspagoJpaController formaPagoCntrl = new FormaspagoJpaController(em);
            
            //Pago efectivo            
            if (pagosMap.get(1).getMonto().compareTo(BigDecimal.ZERO)>=0){                
                pagoEfectivo.setFpId( formaPagoCntrl.findFormaspago(1) );//efectivo
                pagoEfectivo.setSpId( estadoCtntrl.findEstadospago(1) );//estado pago cancelado
                pagoEfectivo.setFactId(factura);
                pagoEfectivo.setPgfMonto( pagosMap.get(1).getMonto() );
                pagoEfectivo.setPgfSaldo(BigDecimal.ZERO);                
                pagoEfectivo.setPgfObs( pagosMap.get(1).getObservacion() );
                pagoEfectivo.setPgfFecreg(new Date());
                em.persist(pagoEfectivo);
            }
            
            //Pago credito
            if (pagosMap.get(2).getMonto().compareTo(BigDecimal.ZERO)>=0){
                pagoCredito.setFpId( formaPagoCntrl.findFormaspago(2) );//credito
                pagoCredito.setSpId( estadoCtntrl.findEstadospago(2) );//Estado pago pendiente
                pagoCredito.setFactId(factura);
                pagoCredito.setPgfMonto( pagosMap.get(2).getMonto() );
                pagoCredito.setPgfObs( pagosMap.get(2).getObservacion() );
                pagoCredito.setPgfFecreg(new Date());
                pagoCredito.setPgfSaldo( pagosMap.get(2).getMonto() );
                em.persist(pagoCredito);
            }

            //Se debe actualizar el numero de secuencia de la factura
            SecuenciasJpaController secuenciasController = new SecuenciasJpaController(em);
            secuenciasController.genSecuencia("EST001");

            em.getTransaction().commit();            
        }
        catch(Throwable ex){
            System.out.println("Erro al tratar de registrar factura:"+ ex.getMessage());
            ex.printStackTrace();
            throw  new Exception("Erro al tratar de registrar factura:"+ ex.getMessage());
        }
        finally{
            if (em != null) {
                //em.close();
            }
        }
    }
}
