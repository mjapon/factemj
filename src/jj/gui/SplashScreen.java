/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jj.gui;

import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author manuel.japon
 */
public class SplashScreen extends javax.swing.JFrame {

    /**
     * Creates new form SplashScreen
     */
    public SplashScreen() {
        initComponents();
        /*
        BufferedImage img = null;        
        try {
            URL resource = FarmaAppMain.class.getResource("MarcaVG.png");
            //URL resource = SplashScreen.class.getResource("MarcaVG.png");
            img = ImageIO.read(resource);
            ImageIcon imageIcon = new ImageIcon(img);
            setContentPane(new JLabel(imageIcon));
            System.out.println("Se cargo la imagen-->");
        } catch (Throwable e) {
            System.out.println("Error al cargar la image-->");
            e.printStackTrace();
        } 
        */
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.FlowLayout());

        jLabel1.setText("Iniciando el Sistema....");
        getContentPane().add(jLabel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
