import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SemanticAnalyzer {
    private final Stack<Map<String, String>> scopes = new Stack<>();
    private String currentFunctionReturnType = "void";

    public void analyze(Parser.ASTNode root) {
        scopes.push(new HashMap<>()); // Global Scope
        visit(root);
        scopes.pop();
    }

    private void visit(Parser.ASTNode node) {
        if (node == null) return;
        
        String type = node.type;
        
        if (type.equals("PROGRAM")) {
            for (Parser.ASTNode child : node.children) visit(child);
            
        } else if (type.equals("FUNCTION")) {
            String funcName = node.value;
            String retType = node.children.get(0).value;
            declare(funcName, "function", node.token);
            currentFunctionReturnType = retType;
            enterScope();
            for (Parser.ASTNode param : node.children.get(1).children) {
                String pName = param.value;
                String pType = param.children.get(0).value;
                declare(pName, pType, param.token);
            }
            visit(node.children.get(2));
            exitScope();
            
        } else if (type.equals("BLOCK")) {
            for (Parser.ASTNode stmt : node.children) visit(stmt);
            
        } else if (type.equals("VAR_DECL")) {
            String varName = node.value;
            String varType = node.children.get(0).value;
            declare(varName, varType, node.token);
            if (node.children.size() > 1) {
                String exprType = getExpressionType(node.children.get(1));
                checkTypeMatch(varType, exprType, node.token);
            }
            
        } else if (type.equals("IF") || type.equals("WHILE")) {
            String condType = getExpressionType(node.children.get(0));
            if (!condType.equals("boolean")) error("Condition must be boolean, found: " + condType, node.token);
            visit(node.children.get(1));
            if (node.children.size() > 2) visit(node.children.get(2));
            
        } else if (type.equals("RETURN")) {
            if (node.children.isEmpty()) {
                if (!currentFunctionReturnType.equals("void"))
                    error("Return value expected for " + currentFunctionReturnType, node.token);
            } else {
                String returnValType = getExpressionType(node.children.get(0));
                checkTypeMatch(currentFunctionReturnType, returnValType, node.token);
            }
            
        } else if (type.equals("BINARY_OP")) {
            getExpressionType(node);
        }
    }

    private String getExpressionType(Parser.ASTNode node) {
        String type = node.type;
        if (type.equals("LITERAL_NUM")) {
            return node.value.contains(".") || node.value.endsWith("f") ? "float" : "int";
        } else if (type.equals("LITERAL_BOOL")) {
            return "boolean";
        } else if (type.equals("LITERAL_STRING")) {
            return "String"; // FIXED: Now recognizes String literals
        } else if (type.equals("VAR_REF")) {
            return lookup(node.value, node.token);
        } else if (type.equals("BINARY_OP")) {
            String left = getExpressionType(node.children.get(0));
            String right = getExpressionType(node.children.get(1));
            return checkOpCompatibility(left, right, node.value, node.token);
        } else if (type.equals("CALL")) {
            return "unknown"; // Skip strict checking for function calls to avoid false positives
        }
        return "unknown";
    }

    private String checkOpCompatibility(String t1, String t2, String op, Lexer.Token token) {
        // FIXED: Handle String Concatenation
        if (op.equals("+")) {
            if (t1.equals("String") || t2.equals("String")) return "String";
        }

        if (op.matches("==|!=")) {
            if (t1.equals("String") && t2.equals("String")) return "boolean";
            if (!t1.equals(t2)) error("Type mismatch in comparison: " + t1 + " vs " + t2, token);
            return "boolean";
        }
        if (op.matches("<|>|<=|>=")) {
            if (!isNumber(t1) || !isNumber(t2)) error("Comparison requires numbers", token);
            return "boolean";
        }
        if (op.matches("\\+|-|\\*|/")) {
            if (!isNumber(t1) || !isNumber(t2)) error("Math op requires numbers", token);
            return (t1.equals("float") || t2.equals("float")) ? "float" : "int";
        }
        return "unknown";
    }

    private void checkTypeMatch(String expected, String actual, Lexer.Token token) {
        if (actual.equals("unknown")) return; // Allow unknown types (like from function calls)
        if (expected.equals("float") && actual.equals("int")) return;
        if (!expected.equals(actual)) error("Type Mismatch: Expected " + expected + " but got " + actual, token);
    }

    private boolean isNumber(String type) { return type.equals("int") || type.equals("float"); }
    private void enterScope() { scopes.push(new HashMap<>()); }
    private void exitScope() { scopes.pop(); }

    private void declare(String name, String type, Lexer.Token token) {
        if (scopes.peek().containsKey(name)) error("Variable '" + name + "' already declared", token);
        scopes.peek().put(name, type);
    }

    private String lookup(String name, Lexer.Token token) {
        for (int i = scopes.size() - 1; i >= 0; i--) if (scopes.get(i).containsKey(name)) return scopes.get(i).get(name);
        error("Undeclared variable '" + name + "'", token);
        return "unknown";
    }

    private void error(String msg, Lexer.Token token) {
        throw new RuntimeException("Semantic Error at pos " + (token != null ? token.position : "?") + ": " + msg);
    }
}