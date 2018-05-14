/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.util.datamodels;

import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import jj.entity.Categorias;

/**
 *
 * @author manuel.japon
 */
public class CatsListModel extends DefaultListModel implements ListModel{
    
    private List<Categorias> items;
    
    @Override
    public int getSize() {
        return items.size();
    }

    public Categorias getElementoAt(int index){
        return items.get(index);
    }
    
    @Override
    public Object getElementAt(int index) {
        return items.get(index).getCatName();
    }

    public List<Categorias> getItems() {
        return items;
    }

    public void setItems(List<Categorias> items) {
        this.items = items;
    }
    
}
