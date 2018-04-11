/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.util;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 *
 * @author mjapon
 */
public class IVAComboBoxEditor extends DefaultCellEditor {
    
  public IVAComboBoxEditor(String[] items) {
    super(new JComboBox(items));
  }
}