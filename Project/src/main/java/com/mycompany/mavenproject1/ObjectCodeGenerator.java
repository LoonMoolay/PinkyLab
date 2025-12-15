package com.mycompany.mavenproject1;

import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ObjectCodeGenerator extends javax.swing.JFrame {

    private String objectCode;

    public ObjectCodeGenerator(String intermediateCode) {
        initComponents();
        this.objectCode = generateObjectCode(intermediateCode);
        this.jTextArea1.setText(this.objectCode);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();

        setTitle("Object Code Generator");

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Assembly Code");

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setBackground(new java.awt.Color(70, 177, 204));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Save to File");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        saveObjectCodeToFile();
    }//GEN-LAST:event_jButton1ActionPerformed

    private String generateObjectCode(String intermediateCode) {
        StringBuilder asmCode = new StringBuilder();

        // Track variable values for constant folding and optimization
        java.util.Map<String, String> varValues = new java.util.HashMap<>();
        java.util.Map<String, Integer> constantValues = new java.util.HashMap<>();

        // Add assembly header (without comments)
        asmCode.append(".data\n\n");
        asmCode.append(".text\n");
        asmCode.append("    .globl main\n");
        asmCode.append("main:\n");
        asmCode.append("    PUSH BP\n");
        asmCode.append("    MOV BP, SP\n\n");

        // Process each line of intermediate code
        String[] lines = intermediateCode.split("\n");
        String currentRegister = null; // Track what's currently in AX

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("{") || line.startsWith("}")) {
                // Skip empty lines and braces
                continue;
            }

            // Convert intermediate code to assembly
            if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String leftSide = parts[0].trim();
                    String rightSide = parts[1].trim();

                    // Extract variable name (remove data type if present)
                    String[] leftParts = leftSide.split("\\s+");
                    String varName = leftParts[leftParts.length - 1];

                    // Try constant folding first
                    Integer foldedValue = tryConstantFold(rightSide, constantValues);
                    if (foldedValue != null) {
                        asmCode.append("    MOV ").append(varName).append(", ").append(foldedValue).append("\n");
                        constantValues.put(varName, foldedValue);
                        currentRegister = varName;
                        continue;
                    }

                    // Check if it's an arithmetic operation
                    if (rightSide.contains("+")) {
                        generateOptimizedArithmetic(asmCode, line, varName, rightSide, "+", "ADD", currentRegister, varValues);
                    } else if (rightSide.contains("-") && !rightSide.matches("^-?\\d+$")) {
                        generateOptimizedArithmetic(asmCode, line, varName, rightSide, "-", "SUB", currentRegister, varValues);
                    } else if (rightSide.contains("*")) {
                        generateOptimizedMultiply(asmCode, line, varName, rightSide, currentRegister);
                    } else if (rightSide.contains("/") && !rightSide.contains("//")) {
                        generateOptimizedDivide(asmCode, line, varName, rightSide, "DIV", currentRegister);
                    } else if (rightSide.contains("%")) {
                        generateOptimizedDivide(asmCode, line, varName, rightSide, "MOD", currentRegister);
                    } else {
                        // Simple assignment - optimize if possible
                        // If assigning to the variable that's already in AX, skip (redundant)
                        if (!(rightSide.equals(currentRegister) && varName.equals(currentRegister))) {
                            asmCode.append("    MOV ").append(varName).append(", ").append(rightSide).append("\n");
                            varValues.put(varName, rightSide);
                        }
                    }
                    currentRegister = varName;
                }
            }
        }

        // Add assembly footer (without comments)
        asmCode.append("\n    MOV SP, BP\n");
        asmCode.append("    POP BP\n");
        asmCode.append("    MOV AX, 4C00h\n");
        asmCode.append("    INT 21h\n");
        asmCode.append("END main\n");

        return asmCode.toString();
    }

    private Integer tryConstantFold(String expr, java.util.Map<String, Integer> constants) {
        try {
            // Check for simple arithmetic with constants
            if (expr.contains("+")) {
                String[] ops = expr.split("\\+");
                if (ops.length == 2) {
                    Integer v1 = getConstantValue(ops[0].trim(), constants);
                    Integer v2 = getConstantValue(ops[1].trim(), constants);
                    if (v1 != null && v2 != null) return v1 + v2;
                }
            } else if (expr.contains("-") && !expr.matches("^-?\\d+$")) {
                String[] ops = expr.split("-");
                if (ops.length == 2) {
                    Integer v1 = getConstantValue(ops[0].trim(), constants);
                    Integer v2 = getConstantValue(ops[1].trim(), constants);
                    if (v1 != null && v2 != null) return v1 - v2;
                }
            } else if (expr.contains("*")) {
                String[] ops = expr.split("\\*");
                if (ops.length == 2) {
                    Integer v1 = getConstantValue(ops[0].trim(), constants);
                    Integer v2 = getConstantValue(ops[1].trim(), constants);
                    if (v1 != null && v2 != null) return v1 * v2;
                }
            } else if (expr.contains("/")) {
                String[] ops = expr.split("/");
                if (ops.length == 2) {
                    Integer v1 = getConstantValue(ops[0].trim(), constants);
                    Integer v2 = getConstantValue(ops[1].trim(), constants);
                    if (v1 != null && v2 != null && v2 != 0) return v1 / v2;
                }
            } else if (expr.contains("%")) {
                String[] ops = expr.split("%");
                if (ops.length == 2) {
                    Integer v1 = getConstantValue(ops[0].trim(), constants);
                    Integer v2 = getConstantValue(ops[1].trim(), constants);
                    if (v1 != null && v2 != null && v2 != 0) return v1 % v2;
                }
            }
        } catch (Exception e) {
            // Not foldable
        }
        return null;
    }

    private Integer getConstantValue(String operand, java.util.Map<String, Integer> constants) {
        try {
            return Integer.parseInt(operand);
        } catch (NumberFormatException e) {
            return constants.get(operand);
        }
    }

    private void generateOptimizedArithmetic(StringBuilder asmCode, String line, String varName,
                                             String rightSide, String op, String instruction,
                                             String currentRegister, java.util.Map<String, String> varValues) {
        String[] operands = rightSide.split("\\" + (op.equals("+") ? "\\+" : op));
        if (operands.length != 2) return;

        String op1 = operands[0].trim();
        String op2 = operands[1].trim();

        // Optimization: if op1 is already in AX (currentRegister), skip the MOV
        if (!op1.equals(currentRegister)) {
            asmCode.append("    MOV AX, ").append(op1).append("\n");
        }

        // Use direct ADD/SUB instruction
        asmCode.append("    ").append(instruction).append(" AX, ").append(op2).append("\n");

        // Optimization: if result is stored back to op1, we can skip the MOV
        if (!varName.equals(op1)) {
            asmCode.append("    MOV ").append(varName).append(", AX\n");
        }
    }

    private void generateOptimizedMultiply(StringBuilder asmCode, String line, String varName,
                                           String rightSide, String currentRegister) {
        String[] operands = rightSide.split("\\*");
        if (operands.length != 2) return;

        String op1 = operands[0].trim();
        String op2 = operands[1].trim();

        // Check for power-of-2 multiplication (can use shift)
        try {
            int val = Integer.parseInt(op2);
            if (isPowerOfTwo(val)) {
                int shift = (int)(Math.log(val) / Math.log(2));
                if (!op1.equals(currentRegister)) {
                    asmCode.append("    MOV AX, ").append(op1).append("\n");
                }
                asmCode.append("    SHL AX, ").append(shift).append("\n");
                asmCode.append("    MOV ").append(varName).append(", AX\n");
                return;
            }
        } catch (NumberFormatException e) {
            // Not a constant, use normal multiply
        }

        // Standard multiply
        if (!op1.equals(currentRegister)) {
            asmCode.append("    MOV AX, ").append(op1).append("\n");
        }
        asmCode.append("    MOV BX, ").append(op2).append("\n");
        asmCode.append("    IMUL BX\n");
        asmCode.append("    MOV ").append(varName).append(", AX\n");
    }

    private void generateOptimizedDivide(StringBuilder asmCode, String line, String varName,
                                         String rightSide, String operation, String currentRegister) {
        String[] operands = rightSide.split(operation.equals("MOD") ? "%" : "/");
        if (operands.length != 2) return;

        String op1 = operands[0].trim();
        String op2 = operands[1].trim();

        // Check for power-of-2 division (can use shift)
        if (operation.equals("DIV")) {
            try {
                int val = Integer.parseInt(op2);
                if (isPowerOfTwo(val)) {
                    int shift = (int)(Math.log(val) / Math.log(2));
                    if (!op1.equals(currentRegister)) {
                        asmCode.append("    MOV AX, ").append(op1).append("\n");
                    }
                    asmCode.append("    SAR AX, ").append(shift).append("\n");
                    asmCode.append("    MOV ").append(varName).append(", AX\n");
                    return;
                }
            } catch (NumberFormatException e) {
                // Not a constant, use normal divide
            }
        }

        // Standard divide
        if (!op1.equals(currentRegister)) {
            asmCode.append("    MOV AX, ").append(op1).append("\n");
        }
        asmCode.append("    XOR DX, DX\n");
        asmCode.append("    MOV BX, ").append(op2).append("\n");
        asmCode.append("    IDIV BX\n");

        // For modulo, result is in DX; for division, in AX
        if (operation.equals("MOD")) {
            asmCode.append("    MOV ").append(varName).append(", DX\n");
        } else {
            asmCode.append("    MOV ").append(varName).append(", AX\n");
        }
    }

    private boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    private void saveObjectCodeToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Código Objeto");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Assembly Files (*.asm)", "asm");
        fileChooser.setFileFilter(filter);
        fileChooser.setSelectedFile(new java.io.File("output.asm"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            // Add .asm extension if not present
            if (!filePath.toLowerCase().endsWith(".asm")) {
                filePath += ".asm";
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(this.objectCode);
                JOptionPane.showMessageDialog(this,
                        "Código objeto guardado en:\n" + filePath,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar archivo: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
