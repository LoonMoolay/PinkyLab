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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class MainPanel extends javax.swing.JFrame {

    LexemeGenerator lex;
    String stream;
    TokenTable tokenTable;
    SymbolTable symbolTable;
    IntermediateCodeGenerator intermediateFrame;
    ObjectCodeGenerator objectCodeFrame;
    private JTextArea terminalOutput;
    private JButton runButton;
    private Process currentRunningProcess;
    private java.io.BufferedWriter processInputWriter;
    
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
            File imageFile = new File("C:\\Users\\miner\\Documents\\#PROYECTOS\\PinkyLab\\PinkyLab.png");
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

        // Panel de botones en grid layout (top right)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(COLOR_FONDO_PRINCIPAL);
        buttonPanel.setLayout(new GridLayout(4, 2, 10, 10));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        scanBtn = createPinkyButton("Analizar");
        scanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanBtnActionPerformed(evt);
            }
        });

        runButton = createPinkyButton("Ejecutar");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
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

        JButton clearTerminalBtn = createPinkyButton("Limpiar Terminal");
        clearTerminalBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                terminalOutput.setText("");
            }
        });

        buttonPanel.add(scanBtn);
        buttonPanel.add(runButton);
        buttonPanel.add(jButton5);
        buttonPanel.add(jButton6);
        buttonPanel.add(jButton4);
        buttonPanel.add(jButton1);
        buttonPanel.add(jButton7);
        buttonPanel.add(clearTerminalBtn);

        // Terminal output panel
        terminalOutput = new JTextArea();
        terminalOutput.setEditable(true);  // Enable editing for Scanner input
        terminalOutput.setFont(new Font("Consolas", Font.PLAIN, 13));
        terminalOutput.setBackground(new Color(30, 30, 30));
        terminalOutput.setForeground(new Color(0, 255, 0));
        terminalOutput.setCaretColor(Color.WHITE);
        terminalOutput.setText("Terminal output will appear here...\n");

        // Add Enter key listener to send input to Scanner
        terminalOutput.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && processInputWriter != null) {
                    evt.consume(); // Prevent default newline behavior
                    try {
                        // Extract the last line typed by user
                        String text = terminalOutput.getText();
                        int lastNewline = text.lastIndexOf('\n', text.length() - 2);
                        String input = text.substring(lastNewline + 1).trim();

                        // Send input to the running process
                        processInputWriter.write(input);
                        processInputWriter.newLine();
                        processInputWriter.flush();

                        // Add newline to terminal display
                        terminalOutput.append("\n");
                        terminalOutput.setCaretPosition(terminalOutput.getDocument().getLength());
                    } catch (java.io.IOException e) {
                        terminalOutput.append("\nError sending input: " + e.getMessage() + "\n");
                    }
                }
            }
        });

        JScrollPane terminalScrollPane = new JScrollPane(terminalOutput);
        terminalScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_BOTON_BORDE, 2), 
            "Terminal de Ejecución",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            COLOR_BOTON_BORDE));
        terminalScrollPane.setPreferredSize(new Dimension(0, 200));

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

        // Create a panel for code editor and terminal
        JSplitPane editorTerminalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
            jScrollPane2, terminalScrollPane);
        editorTerminalSplit.setResizeWeight(0.7);
        editorTerminalSplit.setDividerSize(5);

        // Create top panel with welcome message and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_FONDO_PRINCIPAL);

        // Button panel wrapper to position it on the right
        JPanel buttonPanelWrapper = new JPanel(new BorderLayout());
        buttonPanelWrapper.setBackground(COLOR_FONDO_PRINCIPAL);
        buttonPanelWrapper.add(buttonPanel, BorderLayout.EAST);

        topPanel.add(centerPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanelWrapper, BorderLayout.EAST);

        // Agregar componentes al panel principal
        jPanel1.add(headerPanel, BorderLayout.NORTH);
        jPanel1.add(editorTerminalSplit, BorderLayout.CENTER);
        jPanel1.add(topPanel, BorderLayout.SOUTH);

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

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String code = sourceStream.getText();
        if (code.isEmpty()) {
            terminalOutput.setText("Error: No code to run!\n");
            return;
        }

        terminalOutput.setText("Compiling and running...\n");
        runButton.setEnabled(false);
        terminalOutput.setEditable(false);

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Create temporary directory
                    File tempDir = new File(System.getProperty("java.io.tmpdir"), "pinkylab_temp");
                    tempDir.mkdirs();

                    // Extract class name from code
                    String className = extractClassName(code);
                    if (className == null) {
                        publish("Error: No public class found in code!\n");
                        return null;
                    }

                    // Write code to file
                    File javaFile = new File(tempDir, className + ".java");
                    try (FileWriter writer = new FileWriter(javaFile)) {
                        writer.write(code);
                    }

                    publish("Compiling " + className + ".java...\n");

                    // Compile with increased memory and stack size for complex code (e.g., matrices)
                    ProcessBuilder compileBuilder = new ProcessBuilder(
                        "javac",
                        "-J-Xmx2048m",      // Increase heap memory to 2GB
                        "-J-Xss10m",        // Increase stack size to 10MB for deep recursion
                        "-encoding", "UTF-8", // Ensure UTF-8 encoding
                        javaFile.getAbsolutePath()
                    );
                    compileBuilder.directory(tempDir);
                    compileBuilder.redirectErrorStream(true);
                    Process compileProcess = compileBuilder.start();

                    StringBuilder compileOutput = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(compileProcess.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            compileOutput.append(line).append("\n");
                        }
                    }

                    // Wait for compilation with timeout (2 minutes for complex code)
                    boolean compiledInTime = compileProcess.waitFor(120, java.util.concurrent.TimeUnit.SECONDS);

                    if (!compiledInTime) {
                        compileProcess.destroyForcibly();
                        publish("Compilation timeout: Code is too complex or has infinite loop in compilation.\n");
                        publish("Try simplifying the code or breaking it into smaller parts.\n");
                        return null;
                    }

                    int compileExitCode = compileProcess.exitValue();
                    if (compileExitCode != 0) {
                        publish("Compilation failed:\n" + compileOutput.toString());
                        return null;
                    }

                    publish("Compilation successful!\n");
                    publish("Running " + className + "...\n");
                    publish("----------------------------------------\n");
                    publish("[Program started - Type input and press Enter when prompted]\n");

                    // Run with increased memory for matrix operations
                    ProcessBuilder runBuilder = new ProcessBuilder(
                        "java",
                        "-Xmx2048m",        // Max heap memory 2GB
                        "-Xss10m",          // Stack size 10MB
                        className
                    );
                    runBuilder.directory(tempDir);
                    runBuilder.redirectErrorStream(true);
                    currentRunningProcess = runBuilder.start();

                    // Setup input writer for Scanner support
                    processInputWriter = new java.io.BufferedWriter(
                        new java.io.OutputStreamWriter(currentRunningProcess.getOutputStream()));

                    // Enable terminal for user input
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        terminalOutput.setEditable(true);
                        terminalOutput.requestFocus();
                    });

                    // Read process output in separate thread
                    Thread outputThread = new Thread(() -> {
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(currentRunningProcess.getInputStream()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                final String outputLine = line;
                                publish(outputLine + "\n");
                            }
                        } catch (IOException e) {
                            if (!e.getMessage().contains("Stream closed")) {
                                publish("Error reading output: " + e.getMessage() + "\n");
                            }
                        }
                    });
                    outputThread.start();

                    // Wait for process with timeout (5 minutes)
                    boolean finishedInTime = currentRunningProcess.waitFor(300, java.util.concurrent.TimeUnit.SECONDS);

                    // Wait for output thread to complete
                    outputThread.join(1000);

                    if (!finishedInTime) {
                        currentRunningProcess.destroyForcibly();
                        publish("----------------------------------------\n");
                        publish("Execution timeout: Program took too long (possible infinite loop).\n");
                        return null;
                    }

                    int runExitCode = currentRunningProcess.exitValue();
                    publish("----------------------------------------\n");
                    publish("Program exited with code: " + runExitCode + "\n");

                    // Cleanup
                    javaFile.delete();
                    new File(tempDir, className + ".class").delete();

                } catch (Exception e) {
                    publish("Error: " + e.getMessage() + "\n");
                    e.printStackTrace();
                } finally {
                    // Cleanup process resources
                    if (processInputWriter != null) {
                        try {
                            processInputWriter.close();
                        } catch (IOException e) {
                            // Ignore
                        }
                        processInputWriter = null;
                    }
                    currentRunningProcess = null;
                }
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String text : chunks) {
                    terminalOutput.append(text);
                }
                terminalOutput.setCaretPosition(terminalOutput.getDocument().getLength());
            }

            @Override
            protected void done() {
                runButton.setEnabled(true);
                terminalOutput.setEditable(false);
                processInputWriter = null;
                currentRunningProcess = null;
            }
        };

        worker.execute();
    }

    private String extractClassName(String code) {
        // Try to find public class first
        java.util.regex.Pattern publicPattern = java.util.regex.Pattern.compile(
            "public\\s+class\\s+(\\w+)");
        java.util.regex.Matcher publicMatcher = publicPattern.matcher(code);
        if (publicMatcher.find()) {
            return publicMatcher.group(1);
        }

        // Try to find any class (with or without public modifier)
        java.util.regex.Pattern anyClassPattern = java.util.regex.Pattern.compile(
            "(?:public\\s+)?class\\s+(\\w+)");
        java.util.regex.Matcher anyClassMatcher = anyClassPattern.matcher(code);
        if (anyClassMatcher.find()) {
            return anyClassMatcher.group(1);
        }

        return null;
    }

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
