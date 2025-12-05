public class Utils {

    public static String classifyValue(String v) {
        v = v.trim();

        if (v.startsWith("\"") && v.endsWith("\"")) {
            String content = v.substring(1, v.length()-1);
            if (isValidStringContent(content)) return "String";
        }

        if (v.startsWith("'") && v.endsWith("'") && v.length() >= 2) {
            String content = v.substring(1, v.length()-1);
            if (content.length() == 1 || isValidEscape(content)) return "char";
        }

        if (v.equals("true") || v.equals("false")) return "boolean";

        if (v.matches("-?\\d+[lL]")) return "long";

        if (v.matches("-?\\d*\\.?\\d+[fFdD]") || v.matches("-?\\d+\\.\\d*[fFdD]?")) {
            String lower = v.toLowerCase();
            return lower.endsWith("f") ? "float" : "double";
        }

        if (v.matches("-?\\d*\\.\\d+")) return "double";

        if (v.matches("-?\\d+")) return "int";

        return null;
    }

    public static boolean isValidStringContent(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\\') {
                if (i + 1 >= s.length()) return false;
                char next = s.charAt(i+1);
                if ("btnfr\"'\\".indexOf(next) == -1) return false;
                i++;
            }
        }
        return true;
    }

    public static boolean isValidEscape(String s) {
        return s.length() == 1 || (s.length() == 2 && "btnfr\"'\\".indexOf(s.charAt(1)) != -1);
    }

    public static boolean isCompatible(String declared, String actual, String literal) {
        if (declared.equals(actual)) return true;
        if (declared.equals("String") || actual.equals("String")) return declared.equals(actual);
        if (declared.equals("char") || actual.equals("char")) return declared.equals(actual);
        if (declared.equals("boolean") || actual.equals("boolean")) return declared.equals(actual);

        if (actual.equals("int")) {
            try {
                long val = Long.parseLong(literal);
                return switch (declared) {
                    case "byte"  -> val >= Byte.MIN_VALUE && val <= Byte.MAX_VALUE;
                    case "short" -> val >= Short.MIN_VALUE && val <= Short.MAX_VALUE;
                    case "int", "long", "float", "double" -> true;
                    default -> false;
                };
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return (declared.equals("long")   && actual.equals("int")) ||
               (declared.equals("float")  && actual.equals("int")) ||
               (declared.equals("double") && (actual.equals("int") || actual.equals("long") || actual.equals("float")));
    }

    public static boolean isValidId(String s) {
        return s != null && s.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }
}