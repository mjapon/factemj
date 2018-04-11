/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mjapon
 */
public class FechasUtil {
    
    public static String formato = "dd/MM/yyyy";
    public static String formatoFechaHora = "dd/MM/yyyy hh:mm";
    public static SimpleDateFormat dateFormat;
    public static SimpleDateFormat dateHourFormat;
    public static Map<Integer, String> mesesMap;
    
    static{
        dateFormat = new SimpleDateFormat(formato);
        dateHourFormat= new SimpleDateFormat(formatoFechaHora);
        
        mesesMap = new HashMap<Integer, String>();
        mesesMap.put(0, "ENERO");
        mesesMap.put(1, "FEBRERO");
        mesesMap.put(2, "MARZO");
        mesesMap.put(3, "ABRIL");
        mesesMap.put(4, "MAYO");
        mesesMap.put(5, "JUNIO");
        mesesMap.put(6, "JULIO");
        mesesMap.put(7, "AGOSTO");
        mesesMap.put(8, "SEPTIEMBRE");
        mesesMap.put(9, "OCTUBRE");
        mesesMap.put(10, "NOMVIEMBRE");
        mesesMap.put(11, "DICIEMBRE");
    }
    
    public static Date parse(String fecha) throws ParseException{        
        return dateFormat.parse(fecha);
    }
    
    public static String format(Date date){
        return dateFormat.format(date);
    }
    public static String formatDateHour(Date date){
        return dateHourFormat.format(date);
    }
    
    public static String getFechaActual(){
        return format(new Date());
    }
    
    public static String getMonthName(Integer index){
        if (mesesMap.containsKey(index)){
            return mesesMap.get(index);
        }
        return null;
    }
    
    public static Integer getMonthIndex(String name){
        if (mesesMap.containsValue(name)){
            for (Map.Entry<Integer, String> entry : mesesMap.entrySet()) {
                if (name.equalsIgnoreCase(entry.getValue())) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
}
