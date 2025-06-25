package gui;

import lexer.Tokenizer;
import lexer.SymbolTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.*;

public class LexicalAnalyzerGUI extends JFrame {
    private JTextArea codeInput, resultArea, symbolTableArea;
    private JButton analyzeButton, importFileButton, themeToggleButton;

    private boolean isDarkMode = true;

    private final Color darkBg = new Color(30, 30, 30);
    private final Color darkFg = new Color(220, 220, 220);
    private final Color darkBtn = new Color(50, 50, 50);

    private final Color lightBg = Color.WHITE;
    private final Color lightFg = Color.BLACK;
    private final Color lightBtn = new Color(230, 230, 230);

    private final Font textFont = new Font("Consolas", Font.PLAIN, 14);

    public LexicalAnalyzerGUI() {
        setTitle("Lexical Analyzer");
        setSize(1200, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new BorderLayout());
        JLabel inputLabel = new JLabel("Enter Code:");
        codeInput = new JTextArea(15, 30);
        JScrollPane scrollPane = new JScrollPane(codeInput);
        leftPanel.add(inputLabel, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel middlePanel = new JPanel(new BorderLayout());
        JLabel outputLabel = new JLabel("Lexical Analysis Result:");
        resultArea = new JTextArea(15, 30);
        resultArea.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        middlePanel.add(outputLabel, BorderLayout.NORTH);
        middlePanel.add(resultScrollPane, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        JLabel symbolLabel = new JLabel("Symbol Table:");
        symbolTableArea = new JTextArea(15, 30);
        symbolTableArea.setEditable(false);
        JScrollPane symbolScrollPane = new JScrollPane(symbolTableArea);
        rightPanel.add(symbolLabel, BorderLayout.NORTH);
        rightPanel.add(symbolScrollPane, BorderLayout.CENTER);

        mainPanel.add(leftPanel);
        mainPanel.add(middlePanel);
        mainPanel.add(rightPanel);

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        analyzeButton = new JButton("Analyze");
        importFileButton = new JButton("Import File");
        themeToggleButton = new JButton("Toggle Theme");

        buttonPanel.add(analyzeButton);
        buttonPanel.add(importFileButton);
        buttonPanel.add(themeToggleButton);
        add(buttonPanel, BorderLayout.SOUTH);

        analyzeButton.addActionListener(e -> analyzeCode(codeInput.getText()));
        importFileButton.addActionListener(e -> importAndStoreFile());
        themeToggleButton.addActionListener(e -> toggleTheme());

        applyTheme();
    }

    private void customizeButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void applyTheme() {
        Color bg = isDarkMode ? darkBg : lightBg;
        Color fg = isDarkMode ? darkFg : lightFg;
        Color btnColor = isDarkMode ? darkBtn : lightBtn;

        getContentPane().setBackground(bg);
        codeInput.setBackground(bg);
        codeInput.setForeground(fg);
        codeInput.setCaretColor(fg);

        resultArea.setBackground(bg);
        resultArea.setForeground(fg);
        resultArea.setCaretColor(fg);

        symbolTableArea.setBackground(bg);
        symbolTableArea.setForeground(fg);
        symbolTableArea.setCaretColor(fg);

        customizeButton(analyzeButton, btnColor, fg);
        customizeButton(importFileButton, btnColor, fg);
        customizeButton(themeToggleButton, btnColor, fg);

        repaint();
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        applyTheme();
    }

    private void analyzeCode(String code) {
        SymbolTable symbolTable = new SymbolTable();
        Map<String, Set<String>> tokens = Tokenizer.tokenize(code, symbolTable);

        StringBuilder output = new StringBuilder();
        output.append("------------------------------------\n");
        output.append("|        LEXICAL ANALYSIS         |\n");
        output.append("------------------------------------\n\n");

        for (Map.Entry<String, Set<String>> entry : tokens.entrySet()) {
            if (!entry.getKey().equals("LexicalErrors") && !entry.getValue().isEmpty()) {
                output.append(String.format("%-15s : ", entry.getKey().toUpperCase()));
                output.append(String.join(", ", entry.getValue())).append("\n");
            }
        }

        output.append("\n------------------------------------\n");
        output.append("|           LEXICAL ERRORS         |\n");
        output.append("------------------------------------\n\n");

        Set<String> errors = tokens.get("LexicalErrors");
        if (errors.isEmpty()) {
            output.append("No lexical errors found.\n");
        } else {
            for (String err : errors) {
                output.append(err).append("\n");
            }
        }

        resultArea.setText(output.toString());

        StringBuilder symbolText = new StringBuilder();
        for (SymbolTable.Entry entry : symbolTable.getAll()) {
            symbolText.append(entry).append("\n");
        }
        symbolTableArea.setText(symbolText.toString());
    }

    private void importAndStoreFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line).append("\n");
                }
                codeInput.setText(content.toString());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading file.");
            }
        }
    }
}
