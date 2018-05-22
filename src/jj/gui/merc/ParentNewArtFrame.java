/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui.merc;

import jj.util.FilaArticulo;

/**
 *
 * @author manuel.japon
 */
public interface ParentNewArtFrame {
    
    public void afterSuccessfulSaved();
    public void afterClose();
    public boolean validateNewRow(FilaArticulo filaArt);
}
