import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class LexicalAnalyzerGUI extends JFrame {
    private JTextArea codeInput, resultArea;
    private JButton analyzeButton, importFileButton;

    private static final Set<String> cKeywords = new HashSet<>(Arrays.asList(
            "int", "float", "if", "else", "while", "for", "return", "break", "continue",
            "switch", "case", "default", "void", "char", "double", "long", "short"
    ));

    private static final Set<String> cppKeywords = new HashSet<>(Arrays.asList(
            "int", "float", "if", "else", "while", "for", "return", "break", "continue",
            "switch", "case", "default", "void", "char", "double", "long", "short",
            "using", "namespace", "class", "public", "private", "protected", "static",
            "bool", "const", "friend", "inline", "template", "virtual"
    ));

    private static final Set<String> javaKeywords = new HashSet<>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
            "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
            "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
            "interface", "long", "native", "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
            "throw", "throws", "transient", "try", "void", "volatile", "while"
    ));

    private static final Set<String> operators = new HashSet<>(Arrays.asList(
            "+", "-", "*", "/", "=", "==", "!=", "<", ">", "<=", ">=", "&&", "||", "++", "--",
            "&", "|", "^", "~", "<<", ">>", ">>>", "!", "%", "+=", "-=", "*=", "/=", "&=",
            "|=", "^=", "%=", "<<=", ">>=", ">>>="
    ));

    private static final Set<String> punctuation = new HashSet<>(Arrays.asList(
            "(", ")", "{", "}", "[", "]", ";", ",", "."
    ));

    private static final Set<String> cppSpecific = new HashSet<>(Arrays.asList(
            "cout", "cin", "std", "endl"
    ));

    public LexicalAnalyzerGUI() {
        setTitle("Lexical Analyzer - No Duplicates");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Color bgColor = new Color(30, 30, 30);
        Color fgColor = new Color(220, 220, 220);
        Color btnColor = new Color(50, 50, 50);
        Font textFont = new Font("Consolas", Font.PLAIN, 14);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(bgColor);
        JLabel inputLabel = new JLabel("Enter Code:");
        inputLabel.setForeground(fgColor);
        codeInput = new JTextArea(15, 30);
        codeInput.setBackground(bgColor);
        codeInput.setForeground(fgColor);
        codeInput.setFont(textFont);
        codeInput.setCaretColor(fgColor);
        JScrollPane scrollPane = new JScrollPane(codeInput);
        leftPanel.add(inputLabel, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(bgColor);
        JLabel outputLabel = new JLabel("Lexical Analysis Result:");
        outputLabel.setForeground(fgColor);
        resultArea = new JTextArea(15, 30);
        resultArea.setEditable(false);
        resultArea.setBackground(bgColor);
        resultArea.setForeground(fgColor);
        resultArea.setFont(textFont);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        rightPanel.add(outputLabel, BorderLayout.NORTH);
        rightPanel.add(resultScrollPane, BorderLayout.CENTER);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(bgColor);
        analyzeButton = new JButton("Analyze");
        importFileButton = new JButton("Import File");

        customizeButton(analyzeButton, btnColor, fgColor);
        customizeButton(importFileButton, btnColor, fgColor);

        buttonPanel.add(analyzeButton);
        buttonPanel.add(importFileButton);
        add(buttonPanel, BorderLayout.SOUTH);

        analyzeButton.addActionListener(e -> analyzeCode(codeInput.getText()));
        importFileButton.addActionListener(e -> importAndStoreFile());
    }

    private void customizeButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void analyzeCode(String code) {
        Map<String, Set<String>> tokens = tokenize(code);

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

    public static Map<String, Set<String>> tokenize(String code) {
        Map<String, Set<String>> tokenMap = new LinkedHashMap<>();
        tokenMap.put("Preprocessor", new HashSet<>());
        tokenMap.put("Keywords", new HashSet<>());
        tokenMap.put("Identifiers", new HashSet<>());
        tokenMap.put("Operators", new HashSet<>());
        tokenMap.put("Numbers", new HashSet<>());
        tokenMap.put("Punctuation", new HashSet<>());
        tokenMap.put("StringLiterals", new HashSet<>());
        tokenMap.put("LexicalErrors", new HashSet<>());

        // Store original code for context detection
        String originalCode = code;

        // Determine language context
        boolean isJava = Pattern.compile("\\b(import|class|public)\\b", Pattern.CASE_INSENSITIVE).matcher(originalCode).find();
        boolean isCpp = Pattern.compile("#include\\s*<\\s*iostream\\s*>", Pattern.CASE_INSENSITIVE).matcher(originalCode).find();
        Set<String> activeKeywords = isJava ? javaKeywords : (isCpp ? cppKeywords : cKeywords);

        // Handle preprocessor directives (C/C++ only)
        if (!isJava) {
            Matcher includeMatcher = Pattern.compile("#include\\s*<[^>]+>").matcher(originalCode);
            while (includeMatcher.find()) {
                tokenMap.get("Preprocessor").add(includeMatcher.group().trim());
            }
            code = code.replaceAll("#include\\s*<[^>]+>", "");
        }

        // Remove comments
        code = code.replaceAll("//.*", "");
        code = code.replaceAll("(?s)/\\*.*?\\*/", "");


        if (code.contains("/*") && !code.contains("*/")) {
            tokenMap.get("LexicalErrors").add("Unclosed multi-line comment");
        }


        int i = 0;
        String lastToken = "";
        boolean inString = false;
        while (i < code.length()) {
            char c = code.charAt(i);

            // Skip whitespace
            if (Character.isWhitespace(c) && !inString) {
                i++;
                continue;
            }

            // String literals
            if (c == '"') {
                StringBuilder str = new StringBuilder();
                str.append(c);
                i++;
                inString = !inString;
                boolean escaped = false;
                while (i < code.length()) {
                    c = code.charAt(i);
                    str.append(c);
                    if (c == '\\') escaped = !escaped;
                    else if (c == '"' && !escaped) {
                        inString = false;
                        break;
                    } else {
                        escaped = false;
                    }
                    i++;
                }
                if (i == code.length() && str.charAt(str.length() - 1) != '"') {
                    tokenMap.get("LexicalErrors").add("Unterminated string: " + str);
                } else {
                    tokenMap.get("StringLiterals").add(str.toString());
                    i++;
                }
                lastToken = str.toString();
                continue;
            }

            // Multi-character operators
            String[] multiOps = {"==", "!=", "<=", ">=", "&&", "||", "++", "--", "&=", "|=", "^=", "%=", "<<=", ">>=", ">>>=", "<<", ">>", ">>>"};
            boolean matched = false;
            for (String op : multiOps) {
                if (i + op.length() <= code.length() && code.substring(i, i + op.length()).equals(op)) {
                    tokenMap.get("Operators").add(op);
                    i += op.length();
                    lastToken = op;
                    matched = true;
                    break;
                }
            }
            if (matched) continue;

            // Single-character operators and punctuation
            String singleChar = String.valueOf(c);
            if (operators.contains(singleChar)) {
                tokenMap.get("Operators").add(singleChar);
                i++;
                lastToken = singleChar;
                continue;
            }
            if (punctuation.contains(singleChar)) {
                // Skip commas in string literals
                if (singleChar.equals(",") && inString) {
                    i++;
                    continue;
                }
                tokenMap.get("Punctuation").add(singleChar);
                i++;
                lastToken = singleChar;
                continue;
            }

            // Numbers and potential identifiers
            if (!inString) {
                // Identifiers and Keywords
                if (Character.isLetter(c) || c == '_') {
                    StringBuilder token = new StringBuilder();
                    token.append(c);
                    i++;
                    while (i < code.length() && (Character.isLetterOrDigit(code.charAt(i)) || code.charAt(i) == '_')) {
                        token.append(code.charAt(i));
                        i++;
                    }
                    String tokenStr = token.toString();
                    if (activeKeywords.contains(tokenStr)) {
                        tokenMap.get("Keywords").add(tokenStr);
                    } else if (!isJava && !isCpp && cppSpecific.contains(tokenStr)) {
                        tokenMap.get("LexicalErrors").add("Invalid identifier in C context: " + tokenStr);
                    } else if (lastToken.equals("class") && activeKeywords.contains(tokenStr)) {
                        tokenMap.get("LexicalErrors").add("Invalid use of keyword as identifier: " + tokenStr);
                    } else {
                        tokenMap.get("Identifiers").add(tokenStr);
                    }
                    lastToken = tokenStr;
                    continue;
                }

                // Numbers
                if (Character.isDigit(c) || (c == '-' && i + 1 < code.length() && Character.isDigit(code.charAt(i + 1)))) {
                    StringBuilder number = new StringBuilder();
                    number.append(c);
                    i++;
                    boolean hasDot = false;
                    while (i < code.length()) {
                        char nc = code.charAt(i);
                        if (Character.isDigit(nc)) {
                            number.append(nc);
                        } else if (nc == '.' && !hasDot) {
                            hasDot = true;
                            number.append(nc);
                        } else if (Character.isLetter(nc)) {
                            // Invalid: digit followed by letter
                            number.append(nc);
                            i++;
                            while (i < code.length() && (Character.isLetterOrDigit(code.charAt(i)) || code.charAt(i) == '_')) {
                                number.append(code.charAt(i));
                                i++;
                            }
                            tokenMap.get("LexicalErrors").add("Invalid identifier: " + number.toString());
                            lastToken = number.toString();
                            break;
                        } else {
                            break;
                        }
                        i++;
                    }

                    // If no error, add to numbers
                    if (!tokenMap.get("LexicalErrors").contains("Invalid identifier: " + number.toString())) {
                        tokenMap.get("Numbers").add(number.toString());
                        lastToken = number.toString();
                    }
                    continue;
                }

        }

            i++;
            lastToken = singleChar;
        }

        return tokenMap;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LexicalAnalyzerGUI().setVisible(true));
    }
}
