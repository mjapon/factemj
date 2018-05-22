/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;
/**
 *
 * @author manuel.japon
 */
public class Testing 
{
  public void buildGUI()
  {
    String colNames[] = {"Col 0", "Col 1","Col 2", "Col 3"};
    DefaultTableModel dtm = new DefaultTableModel(null,colNames);
    dtm.setRowCount(100);
    JTable table = new JTable(dtm);
    for(int x = 0, r = table.getRowCount(); x < r; x++) table.setValueAt(""+x,x,0);
    
    JTable footer = new JTable(1,4);
    
    JPanel holdingPanel = new JPanel(new BorderLayout());
    JScrollPane sp = new JScrollPane(table);
    sp.setPreferredSize(new Dimension(300,200));
    holdingPanel.add(sp,BorderLayout.CENTER);
    holdingPanel.add(footer,BorderLayout.SOUTH);
    JFrame f = new JFrame();
    f.getContentPane().add(holdingPanel,BorderLayout.CENTER);
    f.getContentPane().add(sp.getVerticalScrollBar(),BorderLayout.EAST);
    f.pack();
    f.setLocationRelativeTo(null);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);
  }
  public static void main(String[] args)
  {
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        new Testing().buildGUI();
      }
    });
  }
}