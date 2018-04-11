/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui;

import java.util.Optional;
import java.util.stream.Stream;

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
        
        
        //System.out.println(result);
        
//        System.out.println( buscar().isPresent() );        
        
        
        
    }
    
}
