/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.util;

import jj.util.datamodels.rows.FilaFactura;
import com.serviestudios.print.util.TextPrinterUtil;
import java.util.List;
import javax.swing.JOptionPane;
import jj.controller.CtesJpaController;

/**
 *
 * @author manuel.japon
 */
public class PrintFactUtil {
    
    public static void imprimir(CtesJpaController ctesController, DatosCabeceraFactura cabecera, 
            TotalesFactura totales, 
            List<FilaFactura> detalles){
        try{
            System.out.println("Logica de impresion-->"); 
            
            String templateCab = ctesController.findValueByClave("TEMPLATE_CAB");
            
            if (templateCab == null){
                System.out.println("Templatecab no definido");
                templateCab = "";
            }
            
            String templateFila = ctesController.findValueByClave("TEMPLATE_DET");
            if (templateFila == null){
                System.out.println("Templatedet no definido");
                templateFila = "";
            }
            
            String templatePie = ctesController.findValueByClave("TEMPLATE_PIE");
            if (templatePie == null){
                System.out.println("Templatepie no definido");
                templatePie = "";
            }
            
            try{                
                String textToPrint = GenTxtFactura.getTxt(
                        cabecera,
                        detalles, 
                        totales, 
                        templateCab, 
                        templateFila, 
                        templatePie
                );
                
                //Verificar el numero de copias ha imprimir
                Integer copysToPrint = 1;
                try{
                    String auxNroCopys = ctesController.findValueByClave("NUMBER_COPYS_PRINT");
                    if (auxNroCopys!=null){
                        copysToPrint = Integer.valueOf(auxNroCopys);
                    }
                }
                catch(Throwable ex){
                    System.out.println("Error al tratar de obtener el numero de copias ha imprimir: "+ex.getMessage());
                    ex.printStackTrace();
                }
                                
                if (copysToPrint>1){
                    textToPrint = GenTxtFactura.getCopias(textToPrint, copysToPrint);
                }
                
                System.out.println("texto ha imprimir");
                System.out.println(textToPrint);                    
                
                //IMPRESORA
                String impresora = ctesController.findValueByClave("IMPRESORA");
                TextPrinterUtil printerUtil = new TextPrinterUtil();
                printerUtil.imprimir(impresora, textToPrint);                            
            }
            catch(Throwable ex){
                System.out.println("Error al generar txt:"+ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al imprimir:"+ex.getMessage());
            }            
        }
        catch(Throwable ex){
            System.out.println("Error al tratar de imprimir:"+ex.getMessage());
            ex.printStackTrace();
        }
    }
}