# Lexical Analyzer using Java

A Swing-based, modular **Lexical Analyzer** built in Java for academic and educational purposes, demonstrating a comprehensive lexical analysis workflow in compilers. This project supports multi-language tokenization, symbol table generation, theme toggling, and basic error reporting.


## 🚀 Features

- 📄 Load `.txt` source files for lexical analysis  
- 🌐 **Multi-Language Tokenization**: configurable support for languages like C, Java, and Python-style syntax  
- 🧩 **Token Categories**:
  - Keywords
  - Identifiers
  - Operators
  - Separators
  - Literals (integers, decimals, strings)
- 🗂️ **Symbol Table Generator**: tracks identifiers, types, and occurrences  
- 🎨 **Toggle Theme**: switch between light and dark UI themes via GUI  
- 🚨 **Error Checking & Reporting**:
  - Detects unrecognized tokens
  - Highlights malformed literals/comments
  - Line-numbered error logs in output
- 🔁 Duplicate token elimination from final listings  
- 🖱️ Tokenization triggered by GUI button – easy to operate


## 🛠️ Technologies Used

- **Java SE 8+**
- **Swing** for UI components and theme switching
- **Regular expressions** for token pattern matching
- **File I/O** for reading input files and logging errors
