/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  mjapon
 * Created: 14/05/2018
 */

--Cambios del 14 de mayo de 2018
ALTER TABLE public.articulos ADD cat_id INT DEFAULT 0 NOT NULL;
ALTER TABLE public.articulos ADD art_tipo VARCHAR(1) DEFAULT 'B' NOT NULL;


--Cambios 16 de mayo de 2018
ALTER TABLE public.facturas ADD fact_descg NUMERIC(15,4) DEFAULT 0.0 NULL;

--Cambios 19 de mayo de 2018

-- auto-generated definition
CREATE TABLE cajas
(
  cj_id        SERIAL NOT NULL
    CONSTRAINT cajas_pkey
    PRIMARY KEY,
  cj_user      INTEGER        DEFAULT 0,
  cj_saldoant  NUMERIC(15, 4) DEFAULT 0.0,
  cj_ventas    NUMERIC(15, 4) DEFAULT 0.0,
  cj_abonoscxc NUMERIC(15, 4) DEFAULT 0.0,
  cj_abonoscxp NUMERIC(15, 4) DEFAULT 0.0,
  cj_anulados  NUMERIC(15, 4) DEFAULT 0.0,
  cj_obsaper   VARCHAR(1000),
  cj_obscierre VARCHAR(1000),
  cj_fecaper   TIMESTAMP,
  cj_feccierre TIMESTAMP,
  cj_estado    INTEGER        DEFAULT 0,
  cj_obsanul   VARCHAR(1000),
  cj_useranul  INTEGER,
  cj_saldo     NUMERIC(15, 4) DEFAULT 0.0
);

CREATE UNIQUE INDEX cajas_cj_id_uindex
  ON cajas (cj_id);

COMMENT ON COLUMN cajas.cj_estado IS '0:abierto, 1:cerrado, 2:anulado';

CREATE TABLE categorias
(
  cat_id   SERIAL NOT NULL
    CONSTRAINT categorias_pkey
    PRIMARY KEY,
  cat_name VARCHAR(80)
);

CREATE UNIQUE INDEX categorias_cat_id_uindex
  ON categorias (cat_id);

insert into categorias(cat_id, cat_name) values (-1, 'SIN CATEGORIA');

ALTER TABLE public.transacciones ADD tra_mask TEXT NULL
COMMENT ON COLUMN public.transacciones.tra_mask IS 'configuracion para las columnas que se visualizan en la factura'


INSERT INTO public.secuencias (sec_id, sec_clave, sec_valor) VALUES (3, 'SEC_SERVS', 1);

--Se agrega clave foranea en tabla de detalles de factura para impedir que se pueda borrar el articulo
ALTER TABLE public.detallesfact
ADD CONSTRAINT detallesfact_articulos_art_id_fk
FOREIGN KEY (art_id) REFERENCES articulos (art_id);


--Creacion de tabla para el registro de kardex de un articulo
CREATE TABLE public.kardexart
(
    ka_id SERIAL PRIMARY KEY NOT NULL,
    ka_artid INT NOT NULL,
    ka_fechareg TIMESTAMP NOT NULL,
    ka_user INT DEFAULT 0,
    ka_accion VARCHAR(100),
    ka_valorant VARCHAR(80),
    ka_valordesp VARCHAR(80),
    CONSTRAINT kardexart_articulos_art_id_fk FOREIGN KEY (ka_artid) REFERENCES articulos (art_id)
);
CREATE UNIQUE INDEX kardexart_ka_id_uindex ON public.kardexart (ka_id);

--Creacion de la tabla de unidades
CREATE TABLE unidades
(
  uni_id      SERIAL NOT NULL
    CONSTRAINT unidades_pkey
    PRIMARY KEY,
  uni_name    VARCHAR(80),
  uni_simbolo VARCHAR(10)
);

CREATE UNIQUE INDEX unidades_uni_id_uindex
  ON unidades (uni_id);

--Creacino de tabla de precios por unidad
CREATE TABLE public.unidadesprecio
(
    unidp_id SERIAL PRIMARY KEY NOT NULL,
    unidp_artid INT NOT NULL,
    unidp_precioventa DECIMAL(15,4) DEFAULT 0.0,
    unidp_preciomin DECIMAL(15,4) DEFAULT 0.0,
    CONSTRAINT unidadesprecio_articulos_art_id_fk FOREIGN KEY (unidp_artid) REFERENCES articulos (art_id)
);
CREATE UNIQUE INDEX unidadesprecio_unidp_id_uindex ON public.unidadesprecio (unidp_id);


ALTER TABLE public.unidadesprecio ADD unidp_unid INT NOT NULL;
ALTER TABLE public.unidadesprecio
ADD CONSTRAINT unidadesprecio_unidades_uni_id_fk
FOREIGN KEY (unidp_unid) REFERENCES unidades (uni_id);

--Miercoles 23 de mayo noche
INSERT INTO unidades(uni_id, uni_name, uni_simbolo) VALUES (1,'UNIDAD', 'un');
update articulos set unid_id = 1;

ALTER SEQUENCE public.unidades_uni_id_seq RESTART WITH 2;

--cambios viernes 25de mayo
ALTER TABLE public.facturas ADD fact_utilidad NUMERIC(15,4) DEFAULT 0.0 NULL;


update facturas set fact_utilidad = subquery.utl from
(
  SELECT child.fact_id, sum((child.detf_precio - child.detf_preciocm) * child.detf_cant) AS utl
  FROM detallesfact child
  GROUP BY child.fact_id
) as subquery
where facturas.fact_id = subquery.fact_id

--http://calculo.cc/temas/temas_e.s.o/sucesiones/teoria/formulas.html