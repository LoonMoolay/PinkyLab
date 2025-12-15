package com.mycompany.mavenproject1;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class MainPanel extends javax.swing.JFrame {

    LexemeGenerator lex;
    String stream;
    TokenTable tokenTable;
    SymbolTable symbolTable;
    IntermediateCodeGenerator intermediateFrame;
    ObjectCodeGenerator objectCodeFrame;
    
    // Colores PinkyLab
    private static final Color COLOR_FONDO_PRINCIPAL = new Color(255, 255, 255);//Blanco puro
    private static final Color COLOR_BOTON = new Color(255, 182, 210);// Rosa kawaii
    private static final Color COLOR_BOTON_BORDE = new Color(255, 150, 200);// Rosa fuerte
    
    public MainPanel() {
        initComponents();
        setupPinkyLabStyle();
        tokenTable = new TokenTable();
        symbolTable = new SymbolTable();
    }
    
    private void setupPinkyLabStyle() {
        setTitle("PinkyLab -Compilador bonito ");
        setBackground(COLOR_FONDO_PRINCIPAL);
        setForeground(new Color(100, 100, 100));
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(true);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel1.setBackground(COLOR_FONDO_PRINCIPAL);
        jPanel1.setLayout(new BorderLayout(10, 10));
        jPanel1.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel encabezado con logo
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(COLOR_FONDO_PRINCIPAL);
        
        try {
            File imageFile = new File("C:\\Users\\fatim\\Documents\\Compilador Automatas\\CompiladorBonito\\PinkyLab.png");
            if (imageFile.exists()) {
                BufferedImage originalImage = ImageIO.read(imageFile);
                Image scaledImage = originalImage.getScaledInstance(350, 120, Image.SCALE_SMOOTH);
                JLabel headerLabel = new JLabel(new ImageIcon(scaledImage));
                headerPanel.add(headerLabel);
            }
        } catch (IOException e) {
            // Si no encuentra la imagen, mostrar texto
            JLabel textHeader = new JLabel("PinkyLab Compilador");
            textHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
            textHeader.setForeground(new Color(220, 100, 180));
            headerPanel.add(textHeader);
        }

        // Panel central con mensaje de bienvenida
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(COLOR_FONDO_PRINCIPAL);
        centerPanel.setLayout(new GridBagLayout());
        
        JLabel welcomeLabel = new JLabel("Bienvenida a tu Compilador PinkyLab");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcomeLabel.setForeground(new Color(220, 100, 180));
        centerPanel.add(welcomeLabel);

        // Panel de botones inferior
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(COLOR_FONDO_PRINCIPAL);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        scanBtn = createPinkyButton("Analizar");
        scanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanBtnActionPerformed(evt);
            }
        });

        jButton5 = createPinkyButton("Código Intermedio");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6 = createPinkyButton("Formato");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton4 = createPinkyButton("Tabla de Símbolos");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton1 = createPinkyButton("Ver Tokens");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton7 = createPinkyButton("Código Objeto");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        buttonPanel.add(scanBtn);
        buttonPanel.add(jButton5);
        buttonPanel.add(jButton6);
        buttonPanel.add(jButton4);
        buttonPanel.add(jButton1);
        buttonPanel.add(jButton7);

        // Panel para el área de texto
        sourceStream = new javax.swing.JTextArea();
        sourceStream.setColumns(20);
        sourceStream.setFont(new java.awt.Font("Segoe UI", 0, 14));
        sourceStream.setRows(5);
        sourceStream.setBackground(new Color(255, 255, 255));
        sourceStream.setForeground(new Color(80, 80, 80));
        sourceStream.setTabSize(4);
        
        jScrollPane2 = new javax.swing.JScrollPane(sourceStream);
        
        // Crear el panel de números de línea
        LineNumberPanel lineNumberPanel = new LineNumberPanel(sourceStream);
        sourceStream.getDocument().addDocumentListener(lineNumberPanel);
        
        // Agregar el panel de números de línea al JScrollPane
        jScrollPane2.setRowHeaderView(lineNumberPanel);

        // Agregar componentes al panel principal
        jPanel1.add(headerPanel, BorderLayout.NORTH);
        jPanel1.add(jScrollPane2, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(COLOR_FONDO_PRINCIPAL);
        southPanel.add(centerPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        jPanel1.add(southPanel, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(jPanel1, BorderLayout.CENTER);

        // Menú
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        jMenu1.setText("Archivo");

        jMenuItem1.setText("Nuevo");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Abrir");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Salir");
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("Acerca de");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Editar");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private JButton createPinkyButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(COLOR_BOTON);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(COLOR_BOTON_BORDE, 2));
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        
        // Agregar relleno
        button.setPreferredSize(new Dimension(120, 40));
        
        return button;
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        this.sourceStream.setText("");
        this.stream = "";
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
        dialog.setDirectory("C://");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String file = dialog.getDirectory() + dialog.getFile();
        FileReader fr;
        try {
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String s = "";
            String text = "";
            while((s = br.readLine()) != null)
                text += s + "\n";
            this.sourceStream.setText(text);
        } catch(IOException e) {}
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void scanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanBtnActionPerformed
        // TODO add your handling code here:
        this.tokenTable = new TokenTable();
        this.symbolTable = new SymbolTable();
        String text = this.sourceStream.getText();

        if(text.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Please write something before scanning");
            return;
        }
        this.intermediateFrame = new IntermediateCodeGenerator(text);
        this.stream = text;
        this.lex = new LexemeGenerator(this.stream, this.tokenTable, this.symbolTable);
        new ScanProgressBar();
    }//GEN-LAST:event_scanBtnActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        intermediateFrame.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        sourceStream.setText(IntermediateCodeGenerator.performFormat(sourceStream.getText()));
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        this.symbolTable.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.tokenTable.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(null,"Developers:\nAmmar\nManaf\nNaveed"); 
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        if (this.intermediateFrame == null) {
            JOptionPane.showMessageDialog(this, "Please scan and generate intermediate code first!");
            return;
        }
        String intermediateCode = IntermediateCodeGenerator.ConvertCode(this.stream);
        this.objectCodeFrame = new ObjectCodeGenerator(intermediateCode);
        this.objectCodeFrame.setVisible(true);
    }//GEN-LAST:event_jButton7ActionPerformed


    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("FaltLaf Light".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MainPanel().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton scanBtn;
    private javax.swing.JTextArea sourceStream;
    // End of variables declaration//GEN-END:variables
    
    // Clase interna para mostrar números de línea
    private class LineNumberPanel extends JComponent implements DocumentListener {
        private javax.swing.JTextArea textArea;
        private int lineCount = 0;
        
        public LineNumberPanel(javax.swing.JTextArea textArea) {
            this.textArea = textArea;
            setBackground(new Color(240, 240, 240));
            setForeground(new Color(150, 150, 150));
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setPreferredSize(new Dimension(50, textArea.getHeight()));
            updateLineCount();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            g2d.setColor(getForeground());
            g2d.setFont(getFont());
            
            int lineHeight = g2d.getFontMetrics().getHeight();
            int baseline = g2d.getFontMetrics().getAscent();
            
            int y = baseline;
            for (int i = 1; i <= lineCount; i++) {
                g2d.drawString(String.valueOf(i), 10, y);
                y += lineHeight;
            }
        }
        
        private void updateLineCount() {
            int count = textArea.getLineCount();
            if (count != lineCount) {
                lineCount = count;
                setPreferredSize(new Dimension(50, textArea.getHeight()));
                repaint();
            }
        }
        
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLineCount();
        }
        
        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLineCount();
        }
        
        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLineCount();
        }
    }
}
