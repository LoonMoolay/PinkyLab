package com.mycompany.mavenproject1;

import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ObjectCodeGenerator extends javax.swing.JFrame {

    private String objectCode;
    private int labelCounter = 0;

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
        java.util.Stack<String> loopStartLabels = new java.util.Stack<>();
        java.util.Stack<String> loopEndLabels = new java.util.Stack<>();

        // Add assembly header
        asmCode.append(".data\n\n");
        asmCode.append(".text\n");
        asmCode.append("    .globl main\n");
        asmCode.append("main:\n");
        asmCode.append("    PUSH BP\n");
        asmCode.append("    MOV BP, SP\n\n");

        // Process each line of intermediate code
        String[] lines = intermediateCode.split("\n");
        String currentRegister = null;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty() || line.startsWith("{") || line.startsWith("}")) {
                continue;
            }

            // Handle while loops
            if (line.startsWith("while")) {
                String startLabel = "WHILE_START_" + (labelCounter++);
                String endLabel = "WHILE_END_" + (labelCounter++);
                loopStartLabels.push(startLabel);
                loopEndLabels.push(endLabel);

                asmCode.append(startLabel).append(":\n");

                // Extract condition
                String condition = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).trim();
                generateConditionCheck(asmCode, condition, endLabel, false);
                continue;
            }

            // Handle for loops
            if (line.startsWith("for")) {
                String startLabel = "FOR_START_" + (labelCounter++);
                String endLabel = "FOR_END_" + (labelCounter++);
                loopStartLabels.push(startLabel);
                loopEndLabels.push(endLabel);

                asmCode.append(startLabel).append(":\n");
                // For loops typically need condition checking - simplified version
                continue;
            }

            // Handle do-while loops
            if (line.startsWith("do")) {
                String startLabel = "DO_START_" + (labelCounter++);
                String endLabel = "DO_END_" + (labelCounter++);
                loopStartLabels.push(startLabel);
                loopEndLabels.push(endLabel);

                asmCode.append(startLabel).append(":\n");
                continue;
            }

            // Handle end of do-while with condition
            if (line.contains("while") && !line.startsWith("while") && !loopStartLabels.isEmpty()) {
                String startLabel = loopStartLabels.pop();
                String endLabel = loopEndLabels.pop();

                String condition = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).trim();
                generateConditionCheck(asmCode, condition, endLabel, false);
                asmCode.append("    JMP ").append(startLabel).append("\n");
                asmCode.append(endLabel).append(":\n");
                continue;
            }

            // Handle end of while/for loops (implicit from closing brace in source)
            if (line.equals("endloop") || (line.startsWith("}") && !loopStartLabels.isEmpty())) {
                if (!loopStartLabels.isEmpty()) {
                    String startLabel = loopStartLabels.pop();
                    String endLabel = loopEndLabels.pop();
                    asmCode.append("    JMP ").append(startLabel).append("\n");
                    asmCode.append(endLabel).append(":\n");
                }
                continue;
            }

            // Handle if statements
            if (line.startsWith("if")) {
                String elseLabel = "ELSE_" + (labelCounter++);
                String endLabel = "ENDIF_" + (labelCounter++);

                String condition = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).trim();
                generateConditionCheck(asmCode, condition, elseLabel, true);
                continue;
            }

            // Handle break statement
            if (line.equals("break")) {
                if (!loopEndLabels.isEmpty()) {
                    asmCode.append("    JMP ").append(loopEndLabels.peek()).append("\n");
                }
                continue;
            }

            // Handle continue statement
            if (line.equals("continue")) {
                if (!loopStartLabels.isEmpty()) {
                    asmCode.append("    JMP ").append(loopStartLabels.peek()).append("\n");
                }
                continue;
            }

            // Handle return statement
            if (line.startsWith("return")) {
                String returnValue = line.substring(6).trim();
                if (!returnValue.isEmpty()) {
                    asmCode.append("    MOV AX, ").append(returnValue).append("\n");
                }
                asmCode.append("    JMP END_MAIN\n");
                continue;
            }

            // Handle increment operators (++)
            if (line.contains("++")) {
                String varName = line.replace("++", "").trim();
                asmCode.append("    INC ").append(varName).append("\n");
                continue;
            }

            // Handle decrement operators (--)
            if (line.contains("--")) {
                String varName = line.replace("--", "").trim();
                asmCode.append("    DEC ").append(varName).append("\n");
                continue;
            }

            // Handle compound assignments
            if (line.contains("+=")) {
                String[] parts = line.split("\\+=");
                if (parts.length == 2) {
                    String var = parts[0].trim();
                    String value = parts[1].trim();
                    asmCode.append("    MOV AX, ").append(var).append("\n");
                    asmCode.append("    ADD AX, ").append(value).append("\n");
                    asmCode.append("    MOV ").append(var).append(", AX\n");
                }
                continue;
            }

            if (line.contains("-=")) {
                String[] parts = line.split("-=");
                if (parts.length == 2) {
                    String var = parts[0].trim();
                    String value = parts[1].trim();
                    asmCode.append("    MOV AX, ").append(var).append("\n");
                    asmCode.append("    SUB AX, ").append(value).append("\n");
                    asmCode.append("    MOV ").append(var).append(", AX\n");
                }
                continue;
            }

            if (line.contains("*=")) {
                String[] parts = line.split("\\*=");
                if (parts.length == 2) {
                    String var = parts[0].trim();
                    String value = parts[1].trim();
                    asmCode.append("    MOV AX, ").append(var).append("\n");
                    asmCode.append("    MOV BX, ").append(value).append("\n");
                    asmCode.append("    IMUL BX\n");
                    asmCode.append("    MOV ").append(var).append(", AX\n");
                }
                continue;
            }

            if (line.contains("/=")) {
                String[] parts = line.split("/=");
                if (parts.length == 2) {
                    String var = parts[0].trim();
                    String value = parts[1].trim();
                    asmCode.append("    MOV AX, ").append(var).append("\n");
                    asmCode.append("    XOR DX, DX\n");
                    asmCode.append("    MOV BX, ").append(value).append("\n");
                    asmCode.append("    IDIV BX\n");
                    asmCode.append("    MOV ").append(var).append(", AX\n");
                }
                continue;
            }

            if (line.contains("%=")) {
                String[] parts = line.split("%=");
                if (parts.length == 2) {
                    String var = parts[0].trim();
                    String value = parts[1].trim();
                    asmCode.append("    MOV AX, ").append(var).append("\n");
                    asmCode.append("    XOR DX, DX\n");
                    asmCode.append("    MOV BX, ").append(value).append("\n");
                    asmCode.append("    IDIV BX\n");
                    asmCode.append("    MOV ").append(var).append(", DX\n");
                }
                continue;
            }

            // Handle assignments
            if (line.contains("=") && !line.contains("==")) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String leftSide = parts[0].trim();
                    String rightSide = parts[1].trim();

                    String[] leftParts = leftSide.split("\\s+");
                    String varName = leftParts[leftParts.length - 1];

                    Integer foldedValue = tryConstantFold(rightSide, constantValues);
                    if (foldedValue != null) {
                        asmCode.append("    MOV ").append(varName).append(", ").append(foldedValue).append("\n");
                        constantValues.put(varName, foldedValue);
                        currentRegister = varName;
                        continue;
                    }

                    if (rightSide.contains("+") && !rightSide.contains("++")) {
                        generateOptimizedArithmetic(asmCode, line, varName, rightSide, "+", "ADD", currentRegister, varValues);
                    } else if (rightSide.contains("-") && !rightSide.matches("^-?\\d+$") && !rightSide.contains("--")) {
                        generateOptimizedArithmetic(asmCode, line, varName, rightSide, "-", "SUB", currentRegister, varValues);
                    } else if (rightSide.contains("*") && !rightSide.contains("*=")) {
                        generateOptimizedMultiply(asmCode, line, varName, rightSide, currentRegister);
                    } else if (rightSide.contains("/") && !rightSide.contains("//") && !rightSide.contains("/=")) {
                        generateOptimizedDivide(asmCode, line, varName, rightSide, "DIV", currentRegister);
                    } else if (rightSide.contains("%") && !rightSide.contains("%=")) {
                        generateOptimizedDivide(asmCode, line, varName, rightSide, "MOD", currentRegister);
                    } else {
                        if (!(rightSide.equals(currentRegister) && varName.equals(currentRegister))) {
                            asmCode.append("    MOV ").append(varName).append(", ").append(rightSide).append("\n");
                            varValues.put(varName, rightSide);
                        }
                    }
                    currentRegister = varName;
                }
            }
        }

        // Add assembly footer
        asmCode.append("\nEND_MAIN:\n");
        asmCode.append("    MOV SP, BP\n");
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

    private void generateConditionCheck(StringBuilder asmCode, String condition, String jumpLabel, boolean jumpIfFalse) {
        // Handle logical operators
        if (condition.contains("&&")) {
            // For AND: both conditions must be true
            String[] conditions = condition.split("&&");
            String tempLabel = "TEMP_" + (labelCounter++);

            for (int i = 0; i < conditions.length; i++) {
                String cond = conditions[i].trim();
                if (i < conditions.length - 1) {
                    generateSingleCondition(asmCode, cond, jumpLabel, true);
                } else {
                    generateSingleCondition(asmCode, cond, jumpLabel, jumpIfFalse);
                }
            }
            return;
        }

        if (condition.contains("||")) {
            // For OR: at least one condition must be true
            String[] conditions = condition.split("\\|\\|");
            String successLabel = "SUCCESS_" + (labelCounter++);

            for (int i = 0; i < conditions.length; i++) {
                String cond = conditions[i].trim();
                if (i < conditions.length - 1) {
                    generateSingleCondition(asmCode, cond, successLabel, false);
                } else {
                    generateSingleCondition(asmCode, cond, jumpLabel, jumpIfFalse);
                }
            }
            if (!jumpIfFalse) {
                asmCode.append(successLabel).append(":\n");
            }
            return;
        }

        // Handle NOT operator
        if (condition.startsWith("!")) {
            String innerCondition = condition.substring(1).trim();
            generateSingleCondition(asmCode, innerCondition, jumpLabel, !jumpIfFalse);
            return;
        }

        generateSingleCondition(asmCode, condition, jumpLabel, jumpIfFalse);
    }

    private void generateSingleCondition(StringBuilder asmCode, String condition, String jumpLabel, boolean jumpIfFalse) {
        // Parse comparison operators
        String operator = "";
        String[] operands = null;

        if (condition.contains("==")) {
            operator = "==";
            operands = condition.split("==");
        } else if (condition.contains("!=")) {
            operator = "!=";
            operands = condition.split("!=");
        } else if (condition.contains("<=")) {
            operator = "<=";
            operands = condition.split("<=");
        } else if (condition.contains(">=")) {
            operator = ">=";
            operands = condition.split(">=");
        } else if (condition.contains("<")) {
            operator = "<";
            operands = condition.split("<");
        } else if (condition.contains(">")) {
            operator = ">";
            operands = condition.split(">");
        }

        if (operands != null && operands.length == 2) {
            String op1 = operands[0].trim();
            String op2 = operands[1].trim();

            asmCode.append("    MOV AX, ").append(op1).append("\n");
            asmCode.append("    CMP AX, ").append(op2).append("\n");

            // Generate appropriate jump based on operator
            String jumpInstruction = "";
            if (jumpIfFalse) {
                // Jump when condition is FALSE
                switch (operator) {
                    case "==": jumpInstruction = "JNE"; break;
                    case "!=": jumpInstruction = "JE"; break;
                    case "<": jumpInstruction = "JGE"; break;
                    case ">": jumpInstruction = "JLE"; break;
                    case "<=": jumpInstruction = "JG"; break;
                    case ">=": jumpInstruction = "JL"; break;
                }
            } else {
                // Jump when condition is TRUE
                switch (operator) {
                    case "==": jumpInstruction = "JE"; break;
                    case "!=": jumpInstruction = "JNE"; break;
                    case "<": jumpInstruction = "JL"; break;
                    case ">": jumpInstruction = "JG"; break;
                    case "<=": jumpInstruction = "JLE"; break;
                    case ">=": jumpInstruction = "JGE"; break;
                }
            }

            asmCode.append("    ").append(jumpInstruction).append(" ").append(jumpLabel).append("\n");
        }
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
