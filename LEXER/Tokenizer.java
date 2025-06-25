package lexer;

import java.util.*;
import java.util.regex.*;

public class Tokenizer {
    private static final Set<String> cKeywords = Set.of(
        "int", "float", "if", "else", "while", "for", "return", "break", "continue",
        "switch", "case", "default", "void", "char", "double", "long", "short"
    );

    private static final Set<String> cppKeywords = Set.of(
        "int", "float", "if", "else", "while", "for", "return", "break", "continue",
        "switch", "case", "default", "void", "char", "double", "long", "short",
        "using", "namespace", "class", "public", "private", "protected", "static",
        "bool", "const", "friend", "inline", "template", "virtual"
    );

    private static final Set<String> javaKeywords = Set.of(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
        "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
        "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
        "interface", "long", "native", "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
        "throw", "throws", "transient", "try", "void", "volatile", "while"
    );

    private static final Set<String> operators = Set.of(
        "+", "-", "*", "/", "=", "==", "!=", "<", ">", "<=", ">=", "&&", "||", "++", "--",
        "&", "|", "^", "~", "<<", ">>", ">>>", "!", "%", "+=", "-=", "*=", "/=", "&=",
        "|=", "^=", "%=", "<<=", ">>=", ">>>="
    );

    private static final Set<String> punctuation = Set.of(
        "(", ")", "{", "}", "[", "]", ";", ",", "."
    );

    private static final Set<String> cppSpecific = Set.of(
        "cout", "cin", "std", "endl"
    );

    public static Map<String, Set<String>> tokenize(String code, SymbolTable symbolTable) {
        Map<String, Set<String>> tokenMap = new LinkedHashMap<>();
        tokenMap.put("Preprocessor", new HashSet<>());
        tokenMap.put("Keywords", new HashSet<>());
        tokenMap.put("Identifiers", new HashSet<>());
        tokenMap.put("Operators", new HashSet<>());
        tokenMap.put("Numbers", new HashSet<>());
        tokenMap.put("Punctuation", new HashSet<>());
        tokenMap.put("StringLiterals", new HashSet<>());
        tokenMap.put("CharacterLiterals", new HashSet<>());
        tokenMap.put("LexicalErrors", new HashSet<>());

        boolean isJava = code.contains("class") || code.contains("import");
        boolean isCpp = code.contains("#include <iostream>");
        Set<String> activeKeywords = isJava ? javaKeywords : (isCpp ? cppKeywords : cKeywords);

        code = code.replaceAll("//.*", "");
        code = code.replaceAll("(?s)/\\*.*?\\*/", "");
        if (Pattern.compile("/\\*[^\\*]*(?!\\*/)").matcher(code).find()) {
            tokenMap.get("LexicalErrors").add("Unclosed multi-line comment");
        }

        String keywordPattern = "\\b(" + String.join("|", activeKeywords) + ")\\b";
        String regex = String.join("|",
            "(#include\\s*<[^>]+>)",
            "\"(\\\\.|[^\\\"])*\"|\"[^\"]*",
            "'(\\\\.|[^\\'])'|'{1}[^']*[']?",
            "\\d+\\.\\d+\\.\\d*",
            keywordPattern,
            "\\b[a-zA-Z_][a-zA-Z0-9_]*\\b",
            "\\b\\d+[a-zA-Z_][a-zA-Z0-9_]*\\b",
            "-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?",
            "\\+\\+|--|&&|\\|\\||==|!=|<=|>=|<<|>>|>>>|::|[+\\-*/%=&|^~!<>]",
            "[(){}\\[\\];,.]",
            "[^\\s\"'a-zA-Z0-9_+\\-*/%=&|^~!<>{}\\[\\];,.]"
        );

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);

        int lineNumber = 1;
        int lastPos = 0;
        String lastToken = "";

        while (matcher.find()) {
            String token = matcher.group();
            int start = matcher.start();

            for (int i = lastPos; i < start; i++) {
                if (code.charAt(i) == '\n') lineNumber++;
            }
            lastPos = matcher.end();

            if (matcher.group(1) != null) {
                tokenMap.get("Preprocessor").add(token);
            } else if (token.startsWith("\"") && !token.endsWith("\"")) {
                tokenMap.get("LexicalErrors").add("Unterminated string at line " + lineNumber + ": " + token);
            } else if (token.startsWith("\"")) {
                tokenMap.get("StringLiterals").add(token);
                symbolTable.add(token, "StringLiteral", lineNumber);
            } else if (token.startsWith("'") && !token.matches("'(\\\\.|[^\\'])'")) {
                tokenMap.get("LexicalErrors").add("Invalid character literal at line " + lineNumber + ": " + token);
            } else if (token.startsWith("'")) {
                tokenMap.get("CharacterLiterals").add(token);
                symbolTable.add(token, "CharLiteral", lineNumber);
            } else if (activeKeywords.contains(token)) {
                tokenMap.get("Keywords").add(token);
            } else if (token.matches("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b")) {
                tokenMap.get("Identifiers").add(token);
                symbolTable.add(token, "Identifier", lineNumber);
            } else if (token.matches("\\b\\d+[a-zA-Z_][a-zA-Z0-9_]*\\b")) {
                tokenMap.get("LexicalErrors").add("Invalid identifier at line " + lineNumber + ": " + token);
            } else if (token.matches("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?")) {
                tokenMap.get("Numbers").add(token);
                symbolTable.add(token, "Number", lineNumber);
            } else if (token.matches("\\d+\\.\\d+\\.\\d*")) {
                tokenMap.get("LexicalErrors").add("Malformed number at line " + lineNumber + ": " + token);
            } else if (operators.contains(token) || token.equals("::")) {
                tokenMap.get("Operators").add(token);
            } else if (punctuation.contains(token)) {
                tokenMap.get("Punctuation").add(token);
            } else {
                tokenMap.get("LexicalErrors").add("Illegal character at line " + lineNumber + ": " + token);
            }

            lastToken = token;
        }

        for (int i = lastPos; i < code.length(); i++) {
            if (code.charAt(i) == '\n') lineNumber++;
            else if (!Character.isWhitespace(code.charAt(i))) {
                tokenMap.get("LexicalErrors").add("Illegal character at line " + lineNumber + ": " + code.charAt(i));
            }
        }

        return tokenMap;
    }
}
