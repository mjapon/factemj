/*     */ package mupi.af.print;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.font.TextAttribute;
/*     */ import java.awt.print.PageFormat;
/*     */ import java.awt.print.Printable;
/*     */ import java.awt.print.PrinterException;
/*     */ import java.text.AttributedString;
/*     */ 
/*     */ 
/*     */ public class BarCodeMupi
/*     */   implements Printable
/*     */ {
/*     */   private String codigoActivo;
/*     */   private String nombreEmpresa;
/*     */   private String nombreActivo;
/*     */   private String serieActivo;
/*     */   private String msgFin;
/*     */   private boolean sc;
/*     */   private Font plainFont;
/*     */   private int currenty;
/*  27 */   private int altoLinea = 10;
/*     */   
/*     */   public BarCodeMupi(String codigoActivo, String nombreActivo, String serie, boolean sc) {
/*  30 */     this.codigoActivo = codigoActivo;
/*  31 */     this.nombreEmpresa = "MUTUALISTA PICHINCHA";
/*  32 */     this.nombreActivo = nombreActivo;
/*  33 */     this.msgFin = "Cuida tus activos fijos, no desprendas este ticket";
/*  34 */     this.serieActivo = (serie != null ? serie : "");
/*  35 */     this.sc = sc;
/*     */     
/*     */ 
/*     */ 
/*  39 */     setFonts();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  45 */     this.currenty = 0;
/*     */   }
/*     */   
/*     */   public void setFonts() {
/*  49 */     this.plainFont = new Font("Serif", 0, 10);
/*     */   }
/*     */   
/*     */ 
/*     */   public BarCodeMupi(String codigoActivo, String nombreActivo, String nomEmpresa, String msgFinal, String serie, boolean sc)
/*     */   {
/*  55 */     this.codigoActivo = codigoActivo;
/*  56 */     this.nombreEmpresa = (nomEmpresa != null ? nomEmpresa : "MUTUALISTA PICHINCHA");
/*  57 */     this.nombreActivo = nombreActivo;
/*  58 */     this.msgFin = (msgFinal != null ? msgFinal : "Cuida tus activos fijos, no desprendas este ticket");
/*  59 */     this.serieActivo = (serie != null ? serie : "");
/*  60 */     this.sc = sc;
/*     */     
/*  62 */     setFonts();
/*     */     
/*     */ 
/*     */ 
/*  66 */     this.currenty = 0;
/*     */   }
/*     */   
/*     */   public AttributedString getNewLine(String texto) {
/*  70 */     AttributedString attributedString = new AttributedString(texto);
/*  71 */     attributedString.addAttribute(TextAttribute.FONT, this.plainFont);
/*  72 */     return attributedString;
/*     */   }
/*     */   
/*     */   public void avanzarCordY(int lineas) {
/*  76 */     this.currenty += this.altoLinea * lineas;
/*     */   }
/*     */   
/*     */   public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
/*  80 */     int xo = (int)pageFormat.getImageableX();
/*  81 */     int yo = (int)pageFormat.getImageableY();
/*     */     
/*  83 */     FontMetrics metrics = graphics.getFontMetrics(this.plainFont);
/*  84 */     this.altoLinea = metrics.getHeight();
/*     */     
/*  86 */     int margenx = xo + 10;
/*  87 */     this.currenty = (yo + 20);
/*     */     
/*  89 */     int widthBarcode = 80;
/*  90 */     int heightBarcode = 40;
/*     */     
/*  92 */     graphics.drawString(getNewLine("Fundacion Terminal Terrestre Guayaquil").getIterator(), margenx, this.currenty);
/*  93 */     avanzarCordY(1);
/*  94 */     graphics.drawString(getNewLine("MATRIZ: Av Rumiñahui Sn y Avenida de los ").getIterator(), margenx, this.currenty);
/*  95 */     avanzarCordY(1);
/*  96 */     graphics.drawString(getNewLine("RUC# 09922236574001 ").getIterator(), margenx, this.currenty);
/*  97 */     avanzarCordY(3);
/*  98 */     graphics.drawString(getNewLine("CLAVE DE ACCESO").getIterator(), margenx, this.currenty);
/*  99 */     avanzarCordY(1);
/* 100 */     graphics.drawString(getNewLine("0308201601119008248900120010030000071726730402110").getIterator(), margenx, this.currenty);
/* 101 */     avanzarCordY(2);
/* 102 */     graphics.drawString(getNewLine("Emision:  03/08/2016 16:30").getIterator(), margenx, this.currenty);
/* 103 */     avanzarCordY(1);
/* 104 */     graphics.drawString(getNewLine("Factura:  001-019-00000123").getIterator(), margenx, this.currenty);
/* 105 */     avanzarCordY(1);
/* 106 */     graphics.drawString(getNewLine("CI/RUC:  1104450877001").getIterator(), margenx, this.currenty);
/* 107 */     avanzarCordY(1);
/* 108 */     graphics.drawString(getNewLine("CLIENTE:  JAPON GUALAN MANUEL EFRAIN").getIterator(), margenx, this.currenty);
/* 109 */     avanzarCordY(3);
/* 110 */     graphics.drawString(getNewLine("CANT  DESC            PRECIO UNI.  TOTAL").getIterator(), margenx, this.currenty);
/* 111 */     avanzarCordY(1);
/* 112 */     graphics.drawString(getNewLine("01    TASA TORNIQUETE       0.25    0.25").getIterator(), margenx, this.currenty);
/* 113 */     avanzarCordY(3);
/* 114 */     graphics.drawString(getNewLine("Subtotal              0.25").getIterator(), margenx, this.currenty);
/* 115 */     avanzarCordY(1);
/* 116 */     graphics.drawString(getNewLine("0.00% IVA             0.00").getIterator(), margenx, this.currenty);
/* 117 */     avanzarCordY(1);
/* 118 */     graphics.drawString(getNewLine("TOTAL GENERAL         0.25").getIterator(), margenx, this.currenty);
/* 119 */     avanzarCordY(1);
/* 120 */     graphics.drawString(getNewLine("        ORIGINAL ADQUIRENTE").getIterator(), margenx, this.currenty);
/* 121 */     avanzarCordY(1);
/* 122 */     graphics.drawString(getNewLine("Cooperativa de Transportes Rutas Orenses").getIterator(), margenx, this.currenty);
/* 123 */     avanzarCordY(1);
/* 124 */     graphics.drawString(getNewLine("Salida:   03/08/2016 16:00").getIterator(), margenx, this.currenty);
/* 125 */     avanzarCordY(1);
/* 126 */     graphics.drawString(getNewLine("Anden:    72-73").getIterator(), margenx, this.currenty);
/* 127 */     avanzarCordY(1);
/* 128 */     graphics.drawString(getNewLine("Usuario:  mjapon").getIterator(), margenx, this.currenty);
/*     */     
/* 130 */     avanzarCordY(2);
/* 131 */     graphics.drawString(getNewLine("La FTTG en cumplimiento de la Resolucion No").getIterator(), margenx, this.currenty);
/* 132 */     avanzarCordY(1);
/* 133 */     graphics.drawString(getNewLine("NAC-OGEROGC1200106 emitidos por el SRI Se ha emitido").getIterator(), margenx, this.currenty);
/* 134 */     avanzarCordY(1);
/* 135 */     graphics.drawString(getNewLine("esta factura electronica. Para descargar este comprobante").getIterator(), margenx, this.currenty);
/* 136 */     avanzarCordY(1);
/* 137 */     graphics.drawString(getNewLine("electronico el cual esta disponible las 24 horas de").getIterator(), margenx, this.currenty);
/* 138 */     avanzarCordY(1);
/* 139 */     graphics.drawString(getNewLine("dia al ingresar a la pagina web http://www.ttg.ec").getIterator(), margenx, this.currenty);
/* 140 */     avanzarCordY(1);
/* 141 */     graphics.drawString(getNewLine("opcion Facturacion Electronica. Si desea el RIDE impreso").getIterator(), margenx, this.currenty);
/* 142 */     avanzarCordY(3);
/*     */     
/* 144 */     Image barcodeImage = getBarCodeImage();
/* 145 */     graphics.drawImage(barcodeImage, margenx + 30, this.currenty, widthBarcode, heightBarcode, null);
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
/* 228 */     return 0;
/*     */   }
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
/*     */   private Image getBarCodeImage()
/*     */   {
/* 295 */     //Barcode128 code128 = new Barcode128();
/* 296 */     //code128.setCode(this.codigoActivo);
/* 297 */     //return code128.createAwtImage(Color.BLACK, Color.WHITE);
                return null;
/*     */   }
/*     */ }


/* Location:              /Users/mjapon/Documents/jarisyplusprint/IsyplusPrint/jar/printws.jar!/mupi/af/print/BarCodeMupi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */