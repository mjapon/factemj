/*     */ package mupi.af.print;
/*     */ 
//import com.itextpdf.text.pdf.Barcode128;
/*     */ import com.serviestudios.print.util.CadsUtil;
/*     */ import com.serviestudios.print.util.LinePart;
/*     */ import com.serviestudios.xml.util.ProcesaMsgXmlUtil;
/*     */ import com.serviestudios.xml.util.XmlBarcodePart;
/*     */ import com.serviestudios.xml.util.XmlGenericPart;
/*     */ import com.serviestudios.xml.util.XmlMsgPart;
/*     */ import com.serviestudios.xml.util.XmlTextPart;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.font.TextAttribute;
/*     */ import java.awt.print.PageFormat;
/*     */ import java.awt.print.Printable;
/*     */ import java.awt.print.PrinterException;
/*     */ import java.io.PrintStream;
/*     */ import java.text.AttributedString;
/*     */ import java.util.List;
/*     */ 
/*     */ public class BarCodeServiestudios
/*     */   implements Printable
/*     */ {
/*     */   private Font plainFont;
/*     */   private int currenty;
/*  29 */   private int altoLinea = 10;
/*     */   private String cadenaImprimir;
/*     */   private List<XmlMsgPart> partesDocList;
/*     */   private ProcesaMsgXmlUtil msgXmlUtil;
/*     */   private boolean isXml;
/*     */   
/*     */   public BarCodeServiestudios(String cadenaImprimir, String tipo) throws Exception
/*     */   {
/*  37 */     this.plainFont = new Font("Serif", 0, 10);
/*  38 */     this.cadenaImprimir = cadenaImprimir;
/*  39 */     this.isXml = false;
/*     */     
/*  41 */     if ((tipo != null) && ("xml".equalsIgnoreCase(tipo))) {
/*     */       try {
/*  43 */         this.msgXmlUtil = new ProcesaMsgXmlUtil(this.cadenaImprimir);
/*  44 */         this.msgXmlUtil.procesar();
/*  45 */         this.partesDocList = this.msgXmlUtil.getPartesDocumento();
/*  46 */         System.out.println(CadsUtil.getFechaHoraActual() + ":BarCodeServiestudios:Finalizo procesar partes xml-->");
/*  47 */         this.isXml = true;
/*     */       }
/*     */       catch (Throwable ex) {
/*  50 */         System.out.println("ERROR:Se produjo un error al procesar el xml:" + ex.getMessage());
/*  51 */         throw new Exception("Se produjo un error al procesar el xml:" + ex.getMessage());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void avanzarCordY(int lineas) {
/*  57 */     this.currenty += this.altoLinea * lineas;
/*     */   }
/*     */   
/*     */   public AttributedString getNewLine(String texto) {
/*  61 */     AttributedString attributedString = new AttributedString(texto);
/*  62 */     if (texto.length() > 0) {
/*  63 */       attributedString.addAttribute(TextAttribute.FONT, this.plainFont);
/*     */     }
/*     */     
/*  66 */     return attributedString;
/*     */   }
/*     */   
/*     */   public AttributedString getNewLineFormated(String texto, Font font)
/*     */   {
/*  71 */     AttributedString attributedString = new AttributedString(texto);
/*  72 */     if (texto.length() > 0) {
/*  73 */       attributedString.addAttribute(TextAttribute.FONT, font);
/*     */     }
/*  75 */     return attributedString;
/*     */   }
/*     */   
/*     */   private Image getBarCodeImage(String barcode) {
            
    /*
                 Barcode128 code128 = new Barcode128();
                code128.setCode(barcode);
                return code128.createAwtImage(Color.BLACK, Color.WHITE);
    */
    return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void auxLogicaGenericPart(Graphics graphics, int margenx, XmlGenericPart genericPart)
/*     */   {
/*  87 */     String txt = genericPart.getContenido();
/*     */     
/*  89 */     String[] lineas = txt.split("\n");
/*  90 */     Font font = this.plainFont;
/*     */     
/*  92 */     FontMetrics metrics = graphics.getFontMetrics(font);
/*  93 */     this.altoLinea = metrics.getHeight();
/*     */     String[] arrayOfString1;
/*  95 */     int j = (arrayOfString1 = lineas).length; for (int i = 0; i < j; i++) { String linea = arrayOfString1[i];
/*     */       
/*  97 */       if (linea.trim().length() > 0) {
/*  98 */         graphics.drawString(getNewLineFormated(linea, font).getIterator(), margenx, this.currenty);
/*     */       }
/* 100 */       avanzarCordY(1);
/*     */     }
/*     */   }
/*     */   
/*     */   public void auxLogicaTxtPart(Graphics graphics, int margenx, XmlTextPart textPart) {
/* 105 */     String txt = textPart.getContenido();
/*     */     
/* 107 */     String[] lineas = txt.split("\n");
/* 108 */     Font font = textPart.getFont();
/*     */     
/* 110 */     FontMetrics metrics = graphics.getFontMetrics(font);
/* 111 */     this.altoLinea = metrics.getHeight();
/*     */     String[] arrayOfString1;
/* 113 */     int j = (arrayOfString1 = lineas).length; for (int i = 0; i < j; i++) { String linea = arrayOfString1[i];
/*     */       
/* 115 */       if (linea.trim().length() > 0) {
/* 116 */         graphics.drawString(getNewLineFormated(linea, font).getIterator(), margenx, this.currenty);
/*     */       }
/* 118 */       avanzarCordY(1);
/*     */     }
/*     */   }
/*     */   
/*     */   public void logicaTxt(Graphics graphics, int margenx) {
/* 123 */     int widthBarcode = 80;
/* 124 */     int heightBarcode = 40;
/*     */     
/* 126 */     String[] lineas = this.cadenaImprimir.split("\n");
/*     */     String[] arrayOfString1;
/* 128 */     int j = (arrayOfString1 = lineas).length; for (int i = 0; i < j; i++) { String linea = arrayOfString1[i];
/*     */       
/* 130 */       LinePart linePart = new LinePart(linea);
/* 131 */       if (linePart.isBarcode()) {
/* 132 */         Image barcodeImage = getBarCodeImage(linePart.getPart());
/* 133 */         graphics.drawImage(barcodeImage, margenx + 30, this.currenty, widthBarcode, heightBarcode, null);
/* 134 */         this.currenty += heightBarcode;
/*     */       }
/*     */       else {
/* 137 */         if (linea.trim().length() > 0) {
/* 138 */           graphics.drawString(getNewLine(linea).getIterator(), margenx, this.currenty);
/*     */         }
/* 140 */         avanzarCordY(1);
/*     */       }
/*     */     }
/* 143 */     System.out.println("Culmino logica txt--->");
/*     */   }
/*     */   
/*     */   public void logicaXml(Graphics graphics, int margenx) {
/* 147 */     for (XmlMsgPart parteDoc : this.partesDocList) {
/* 148 */       String tipo = parteDoc.getTipo();
/* 149 */       String contenido = parteDoc.getContenido();
/*     */       
/* 151 */       if ("texto".equalsIgnoreCase(tipo)) {
/* 152 */         System.out.println(CadsUtil.getFechaHoraActual() + ":BarCodeServiestudios: Se procesa nodo texto-------------->");
/*     */         
/* 154 */         XmlTextPart textPart = (XmlTextPart)parteDoc;
/* 155 */         auxLogicaTxtPart(graphics, margenx, textPart);
/*     */       }
/* 157 */       else if ("barcode".equalsIgnoreCase(tipo)) {
/* 158 */         System.out.println(CadsUtil.getFechaHoraActual() + ":BarCodeServiestudios: Se procesa nodo barcode-------------->");
/*     */         
/* 160 */         XmlBarcodePart barcodePart = (XmlBarcodePart)parteDoc;
/* 161 */         Image barcodeImage = getBarCodeImage(contenido.trim());
/*     */         
/* 163 */         int ancho = barcodePart.getBarcodeWidth();
/* 164 */         int alto = barcodePart.getBarcodeHeigh();
/*     */         
/* 166 */         graphics.drawImage(barcodeImage, margenx + 30, this.currenty, ancho, alto, null);
/* 167 */         this.currenty += barcodePart.getBarcodeHeigh();
/*     */       }
/*     */       else {
/* 170 */         System.out.println(CadsUtil.getFechaHoraActual() + ":BarCodeServiestudios: Se procesa nodo generic-------------->");
/* 171 */         XmlGenericPart textPart = (XmlGenericPart)parteDoc;
/* 172 */         auxLogicaGenericPart(graphics, margenx, textPart);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException
/*     */   {
/* 179 */     int xo = (int)pageFormat.getImageableX();
/* 180 */     int yo = (int)pageFormat.getImageableY();
/*     */     
/* 182 */     int margenx = xo + 10;
/* 183 */     this.currenty = (yo + 15);
/*     */     
/* 185 */     FontMetrics metrics = graphics.getFontMetrics(this.plainFont);
/* 186 */     this.altoLinea = metrics.getHeight();
/*     */     
/* 188 */     if (this.isXml) {
/* 189 */       System.out.println(CadsUtil.getFechaHoraActual() + ":BarCodeServiestudios:print Se ejecuta logica xml-->");
/* 190 */       logicaXml(graphics, margenx);
/*     */     }
/*     */     else {
/* 193 */       logicaTxt(graphics, margenx);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 360 */     return 0;
/*     */   }
/*     */ }


/* Location:              /Users/mjapon/Documents/jarisyplusprint/IsyplusPrint/jar/printws.jar!/mupi/af/print/BarCodeServiestudios.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */