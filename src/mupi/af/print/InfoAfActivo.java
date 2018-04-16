/*    */ package mupi.af.print;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class InfoAfActivo implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 7119117245097806518L;
/*    */   private String codigo;
/*    */   private String nombre;
/*    */   private String serie;
/*    */   private boolean sc;
/*    */   
/*    */   public InfoAfActivo(String codigo, String nombre, String serie, boolean sc)
/*    */   {
/* 15 */     this.codigo = codigo;
/* 16 */     this.nombre = nombre;
/* 17 */     this.serie = serie;
/* 18 */     this.sc = sc;
/*    */   }
/*    */   
/*    */   public String getCodigo() {
/* 22 */     return this.codigo;
/*    */   }
/*    */   
/* 25 */   public void setCodigo(String codigo) { this.codigo = codigo; }
/*    */   
/*    */   public String getNombre() {
/* 28 */     return this.nombre;
/*    */   }
/*    */   
/* 31 */   public void setNombre(String nombre) { this.nombre = nombre; }
/*    */   
/*    */   public String getSerie() {
/* 34 */     return this.serie;
/*    */   }
/*    */   
/* 37 */   public void setSerie(String serie) { this.serie = serie; }
/*    */   
/*    */   public boolean isSc() {
/* 40 */     return this.sc;
/*    */   }
/*    */   
/* 43 */   public void setSc(boolean sc) { this.sc = sc; }
/*    */ }


/* Location:              /Users/mjapon/Documents/jarisyplusprint/IsyplusPrint/jar/printws.jar!/mupi/af/print/InfoAfActivo.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */