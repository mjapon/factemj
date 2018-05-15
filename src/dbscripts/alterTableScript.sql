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
