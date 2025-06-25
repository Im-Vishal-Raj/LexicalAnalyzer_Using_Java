package lexer;

import java.util.*;

public class SymbolTable {
    public static class Entry {
        public String name;
        public String type;
        public int line;

        public Entry(String name, String type, int line) {
            this.name = name;
            this.type = type;
            this.line = line;
        }

        @Override
        public String toString() {
            return String.format("Name: %-15s Type: %-10s Line: %d", name, type, line);
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    public void add(String name, String type, int line) {
        for (Entry e : entries) {
            if (e.name.equals(name) && e.line == line) return;
        }
        entries.add(new Entry(name, type, line));
    }

    public List<Entry> getAll() {
        return entries;
    }
}
