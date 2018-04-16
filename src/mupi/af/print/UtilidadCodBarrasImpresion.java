 package mupi.af.print;
 
 import com.serviestudios.print.util.CadsUtil;
 import java.awt.print.Book;
 import java.awt.print.PageFormat;
 import java.awt.print.Paper;
 import java.awt.print.PrinterJob;
 import java.io.PrintStream;
 import java.util.List;
 import javax.print.Doc;
 import javax.print.DocPrintJob;
 import javax.print.PrintService;
 import javax.print.PrintServiceLookup;
 import javax.print.attribute.AttributeSet;
 import javax.print.attribute.HashAttributeSet;
 import javax.print.attribute.standard.PrinterName;
 import javax.swing.JOptionPane;
 import sun.print.PageableDoc;
 
 
 public class UtilidadCodBarrasImpresion
 {
/*  32 */   private final int margin = 70;
/*  33 */   private final double inchesPerMillimeter = 0.0394D;
   
   private int anchoEtiqueta;
   private int altoEtiqueta;
   private int A4_WIDTH;
   private int A4_HEIGHT;
   private String nombreImpresora;
   private String nombreEmpresa;
   private String mensajeFinal;
   private Paper paper;
   private PageFormat pageFormat;
   private PrintService printService;
   
   private void inicializar()
   {
     try
     {
/*  50 */       this.A4_WIDTH = ((int)(this.anchoEtiqueta * 0.0394D * 72.0D));
/*  51 */       this.A4_HEIGHT = ((int)(this.altoEtiqueta * 0.0394D * 72.0D));
       
/*  53 */       this.paper = new Paper();
/*  54 */       this.paper.setSize(this.A4_WIDTH, this.A4_HEIGHT);
/*  55 */       this.paper.setImageableArea(0.0D, 0.0D, this.A4_WIDTH, this.A4_HEIGHT);
       
 
 
 
/*  60 */       this.pageFormat = new PageFormat();
/*  61 */       this.pageFormat.setPaper(this.paper);
       
/*  63 */       AttributeSet aset = new HashAttributeSet();
/*  64 */       aset.add(new PrinterName(this.nombreImpresora, null));
       
/*  66 */       PrintService[] services = PrintServiceLookup.lookupPrintServices(null, aset);
       
/*  68 */       if ((services != null) && (services.length > 0)) { PrintService[] arrayOfPrintService1;
/*  69 */         int j = (arrayOfPrintService1 = services).length; for (int i = 0; i < j; i++) { PrintService ps = arrayOfPrintService1[i];
/*  70 */           if (ps.getName().equalsIgnoreCase(this.nombreImpresora)) {
/*  71 */             this.printService = ps;
/*  72 */             break;
 
           }
           
 
         }
         
 
       }
       
 
 
     }
     catch (Throwable ex)
     {
 
/*  88 */       System.out.println("Error en UtilidadCodBarrasImpresion.inicializar:" + ex.getMessage());
     }
   }
   
   public void elegirImpresoraManualmente() {
/*  93 */     if (this.printService == null) {
/*  94 */       PrinterJob job = PrinterJob.getPrinterJob();
/*  95 */       if (job.printDialog()) {
/*  96 */         this.printService = job.getPrintService();
       }
     }
   }
   
   public void imprimir(List<InfoAfActivo> listaActivos) throws Throwable {
/* 102 */     if (this.printService == null)
     {
/* 104 */       int confirmado = JOptionPane.showConfirmDialog(null, "La impresora " + this.nombreImpresora + " no se encuentra instalada en su equipo. ¿Desea elegir otra?");
       
/* 106 */       if (confirmado == 0) {
/* 107 */         elegirImpresoraManualmente();
/* 108 */         imprimir(listaActivos);
       }
       else {
/* 111 */         throw new Exception("No se puede imprimir, verifique que la impresora: " + this.nombreImpresora + " se encuentre instalada en su equipo");
       }
       
     }
/* 115 */     else if (listaActivos != null) {
/* 116 */       for (InfoAfActivo activo : listaActivos) {
/* 117 */         imprimir(activo);
       }
     }
   }
   
 
   public void imprimir(String lineas)
     throws Throwable
   {
/* 126 */     String[] paginas = lineas.split("\f");
     
 
/* 129 */     if (this.printService != null) {
/* 130 */       System.out.println(CadsUtil.getFechaHoraActual() + ":UtilidadCodBarrasImpresion:Imprimiendo documento en impresora: " + this.nombreImpresora);
       
 
       String[] arrayOfString1;
       
/* 135 */       int j = (arrayOfString1 = paginas).length; for (int i = 0; i < j; i++) { String pagina = arrayOfString1[i];
         
/* 137 */         String tipoDoc = "xml";
/* 138 */         Book book = new Book();
         
/* 140 */         BarCodeServiestudios barCodeServiestudios = new BarCodeServiestudios(pagina, tipoDoc);
/* 141 */         book.append(barCodeServiestudios, this.pageFormat);
/* 142 */         Doc doc = new PageableDoc(book);
         
/* 144 */         System.out.println(CadsUtil.getFechaHoraActual() + ":UtilidadCodBarrasImpresion:Se envia trabajo de impresion--->");
/* 145 */         DocPrintJob printJob = this.printService.createPrintJob();
/* 146 */         printJob.print(doc, null);
       }
       
 
 
/* 151 */       System.out.println(CadsUtil.getFechaHoraActual() + ":UtilidadCodBarrasImpresion:Imprimiendo:Finalizo impresion");
     }
     else {
/* 154 */       int confirmado = JOptionPane.showConfirmDialog(null, "La impresora " + this.nombreImpresora + " no se encuentra instalada en su equipo. ¿Desea elegir otra?");
/* 155 */       if (confirmado == 0) {
/* 156 */         elegirImpresoraManualmente();
/* 157 */         imprimir(lineas);
       }
       else {
/* 160 */         throw new Exception("No se pudo imprimir");
       }
     }
   }
   
   public void imprimir(InfoAfActivo activo) throws Throwable {
/* 166 */     if (activo != null)
     {
/* 168 */       BarCodeMupi barCodeMupi = new BarCodeMupi(activo.getCodigo(), activo.getNombre(), this.nombreEmpresa, this.mensajeFinal, activo.getSerie(), activo.isSc());
       
/* 170 */       if (this.printService != null)
       {
/* 172 */         System.out.println("------>UtilidadCodBarrasImpresion:Imprimiendo nuevo activo en impresora: " + this.nombreImpresora);
         
/* 174 */         DocPrintJob job2 = this.printService.createPrintJob();
         
/* 176 */         Book book = new Book();
/* 177 */         book.append(barCodeMupi, this.pageFormat);
         
/* 179 */         Doc doc = new PageableDoc(book);
         
/* 181 */         job2.print(doc, null);
         
/* 183 */         System.out.println("--------->UtilidadCodBarrasImpresion:Finalizo impresion de: " + activo.getCodigo());
 
       }
       else
       {
/* 188 */         int confirmado = JOptionPane.showConfirmDialog(null, "La impresora " + this.nombreImpresora + " no se encuentra instalada en su equipo. ¿Desea elegir otra?");
         
/* 190 */         if (confirmado == 0) {
/* 191 */           elegirImpresoraManualmente();
/* 192 */           imprimir(activo);
         }
         else {
/* 195 */           throw new Exception("No se pudo imprimir");
         }
       }
     }
     else
     {
/* 201 */       throw new Exception("La información del activo no ha sido especificada, no se puede imprimir");
     }
   }
   
 
 
 
 
 
 
 
   public UtilidadCodBarrasImpresion(int anchoEtiqueta, int altoEtiqueta, String nomImpresora)
   {
/* 214 */     this.nombreImpresora = nomImpresora;
/* 215 */     this.anchoEtiqueta = anchoEtiqueta;
/* 216 */     this.altoEtiqueta = altoEtiqueta;
     
/* 218 */     inicializar();
   }
   
 
 
 
 
 
   public UtilidadCodBarrasImpresion(String nombreImpresora, Integer anchoPagina, Integer altoPagina)
   {
/* 228 */     this.anchoEtiqueta = anchoPagina.intValue();
/* 229 */     this.altoEtiqueta = altoPagina.intValue();
/* 230 */     this.nombreImpresora = nombreImpresora;
/* 231 */     inicializar();
   }
   
 
 
 
 
 
   public UtilidadCodBarrasImpresion()
   {
/* 241 */     this.anchoEtiqueta = 90;
/* 242 */     this.altoEtiqueta = 250;
/* 243 */     this.nombreImpresora = "Datamax E-4203";
     
/* 245 */     inicializar();
   }
   
   public String getNombreEmpresa() {
/* 249 */     return this.nombreEmpresa;
   }
   
   public void setNombreEmpresa(String nombreEmpresa) {
/* 253 */     this.nombreEmpresa = nombreEmpresa;
   }
   
   public String getMensajeFinal() {
/* 257 */     return this.mensajeFinal;
   }
   
   public void setMensajeFinal(String mensajeFinal) {
/* 261 */     this.mensajeFinal = mensajeFinal;
   }
 }


