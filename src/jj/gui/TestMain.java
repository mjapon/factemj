/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui;

import java.util.Optional;
import java.util.stream.Stream;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author manuel.japon
 */
public class TestMain {
    
    public static Optional<String> buscar(){
        //return new Optional<String>(null);
        
        Optional<String> result = null;
        return result;
        
    }
    
    public static void main(String[] args){
        
        System.out.println("--->");
        
        Stream<String> streamOfArray = Stream.of("a", "b", "c", "2", "a2d", "2dfas");
        
        //Stream<String> result = streamOfArray.filter(a -> "a".equalsIgnoreCase(a));
        
        
        
        Long stream = streamOfArray.filter(element -> {
            System.out.println("filter() was called");
            return element.contains("2");
        }).map(element -> {
            System.out.println("map() was called");
            return element.toUpperCase();
        }).count();
        
        //System.out.println(stream.isPresent()?stream.get():"null");
        System.out.println("count:"+ stream);
        
        
        
        JTextField xField = new JTextField(5);
      JTextField yField = new JTextField(5);

        JPanel myPanel = new JPanel();
      myPanel.add(new JLabel("x:"));
      myPanel.add(xField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("y:"));
      myPanel.add(yField);

      int result = JOptionPane.showConfirmDialog(null, myPanel, 
               "Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
         System.out.println("x value: " + xField.getText());
         System.out.println("y value: " + yField.getText());
      }
        
        
        //System.out.println(result);
        
//        System.out.println( buscar().isPresent() );        
        
        
        
    }
    
}
