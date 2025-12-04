import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static class ASTNode {
        public String type;
        public String value;
        public List<ASTNode> children = new ArrayList<>();
        public Lexer.Token token;
        
        public ASTNode(String type, String value, Lexer.Token token) {
            this.type = type; this.value = value; this.token = token;
        }
        public void add(ASTNode node) { if (node != null) children.add(node); }
    }

    private final List<Lexer.Token> tokens;
    private int pos = 0;

    public Parser(List<Lexer.Token> tokens) { this.tokens = tokens; }

    private Lexer.Token peek(int offset) {
        if (pos + offset >= tokens.size()) return new Lexer.Token(null, "", -1);
        return tokens.get(pos + offset);
    }
    private Lexer.Token current() { return peek(0); }
    private boolean match(String val) { if (current().value.equals(val)) { pos++; return true; } return false; }
    private boolean match(Lexer.TokenType type) { if (current().type == type) { pos++; return true; } return false; }
    private void expect(String val) { if (!match(val)) error("Expected '" + val + "'"); }
    private void expect(Lexer.TokenType type) { if (!match(type)) error("Expected " + type); }
    private void error(String msg) { throw new RuntimeException("Syntax Error at pos " + current().position + ": " + msg); }

    public ASTNode parse() {
        ASTNode root = new ASTNode("PROGRAM", "", null);
        
        boolean headerParsed = true;
        while (headerParsed && pos < tokens.size()) {
            if (match("package")) {
                expect(Lexer.TokenType.IDENTIFIER);
                while (match(".")) { expect(Lexer.TokenType.IDENTIFIER); }
                expect(";");
            } else if (match("import")) {
                expect(Lexer.TokenType.IDENTIFIER);
                while (match(".")) { if (match("*")) break; expect(Lexer.TokenType.IDENTIFIER); }
                expect(";");
            } else {
                headerParsed = false;
            }
        }

        boolean insideClass = false;
        while (pos < tokens.size()) {
             if (match("class")) {
                 expect(Lexer.TokenType.IDENTIFIER);
                 expect("{");
                 insideClass = true;
                 break;
             }
             if (match("public") || match("final") || match("abstract")) continue;
             break;
        }

        while (pos < tokens.size()) {
            if (insideClass && current().value.equals("}")) break;
            root.add(parseFunctionDecl());
        }

        if (insideClass) expect("}");
        return root;
    }

    private ASTNode parseFunctionDecl() {
        while (match("public") || match("static") || match("private") || match("protected")) {}
        
        String returnType = "void";
        if (current().type == Lexer.TokenType.KEYWORD || current().type == Lexer.TokenType.IDENTIFIER) {
            returnType = current().value;
            pos++;
        }
        while (match("[") && match("]")) { returnType += "[]"; }

        Lexer.Token nameToken = current();
        expect(Lexer.TokenType.IDENTIFIER);
        ASTNode funcNode = new ASTNode("FUNCTION", nameToken.value, nameToken);
        funcNode.add(new ASTNode("TYPE", returnType, null));

        expect("(");
        ASTNode params = new ASTNode("PARAMS", "", null);
        if (!current().value.equals(")")) {
            do {
                String pType = current().value;
                if (current().type == Lexer.TokenType.KEYWORD || current().type == Lexer.TokenType.IDENTIFIER) pos++;
                else error("Expected parameter type");
                while (match("[") && match("]")) { pType += "[]"; }

                String pName = current().value;
                match(Lexer.TokenType.IDENTIFIER);
                
                ASTNode param = new ASTNode("PARAM", pName, peek(-1));
                param.add(new ASTNode("TYPE", pType, null));
                params.add(param);
            } while (match(","));
        }
        funcNode.add(params);
        expect(")");
        funcNode.add(parseBlock());
        return funcNode;
    }

    private ASTNode parseBlock() {
        expect("{");
        ASTNode block = new ASTNode("BLOCK", "", current());
        while (!current().value.equals("}") && pos < tokens.size()) block.add(parseStatement());
        expect("}");
        return block;
    }

    private ASTNode parseStatement() {
        if (match("if")) return parseIf();
        if (match("return")) return parseReturn();
        if (match("while")) return parseWhile();
        if (current().value.equals("{")) return parseBlock();
        if (isType(current().value)) return parseVarDecl();
        if (current().type == Lexer.TokenType.IDENTIFIER && peek(1).value.equals("=")) return parseAssignment();
        return parseExprStmt();
    }
    
    private ASTNode parseAssignment() {
        Lexer.Token varToken = current();
        match(Lexer.TokenType.IDENTIFIER);
        expect("=");
        ASTNode assign = new ASTNode("ASSIGN", varToken.value, varToken);
        assign.add(parseExpression());
        expect(";");
        return assign;
    }

    private boolean isType(String s) { 
        return s.matches("int|float|boolean|void|String|char|double|long") || 
               (current().type == Lexer.TokenType.IDENTIFIER && peek(1).type == Lexer.TokenType.IDENTIFIER);
    }

    private ASTNode parseIf() {
        ASTNode node = new ASTNode("IF", "", peek(-1));
        expect("("); node.add(parseExpression()); expect(")");
        node.add(parseBlock());
        if (match("else")) node.add(parseBlock());
        return node;
    }

    private ASTNode parseWhile() {
        ASTNode node = new ASTNode("WHILE", "", peek(-1));
        expect("("); node.add(parseExpression()); expect(")");
        node.add(parseBlock());
        return node;
    }

    private ASTNode parseReturn() {
        ASTNode node = new ASTNode("RETURN", "", peek(-1));
        if (!current().value.equals(";")) node.add(parseExpression());
        expect(";");
        return node;
    }

    private ASTNode parseVarDecl() {
        String type = current().value;
        pos++; 
        while (match("[") && match("]")) { type += "[]"; } 
        
        Lexer.Token nameTok = current();
        expect(Lexer.TokenType.IDENTIFIER);
        ASTNode node = new ASTNode("VAR_DECL", nameTok.value, nameTok);
        node.add(new ASTNode("TYPE", type, null));
        if (match("=")) node.add(parseExpression());
        expect(";");
        return node;
    }

    private ASTNode parseExprStmt() { ASTNode node = parseExpression(); expect(";"); return node; }
    private ASTNode parseExpression() { return parseComparison(); }

    private ASTNode parseComparison() {
        ASTNode left = parseAdditive();
        while (current().value.matches("==|!=|<|>|<=|>=")) {
            Lexer.Token op = current();
            match(Lexer.TokenType.OPERATOR);
            ASTNode binOp = new ASTNode("BINARY_OP", op.value, op);
            binOp.add(left); binOp.add(parseAdditive());
            left = binOp;
        }
        return left;
    }

    private ASTNode parseAdditive() {
        ASTNode left = parseTerm();
        while (current().value.matches("\\+|-")) {
            Lexer.Token op = current();
            match(Lexer.TokenType.OPERATOR);
            ASTNode binOp = new ASTNode("BINARY_OP", op.value, op);
            binOp.add(left); binOp.add(parseTerm());
            left = binOp;
        }
        return left;
    }

    private ASTNode parseTerm() {
        if (match(Lexer.TokenType.NUMBER)) return new ASTNode("LITERAL_NUM", peek(-1).value, peek(-1));
        if (match(Lexer.TokenType.BOOLEAN)) return new ASTNode("LITERAL_BOOL", peek(-1).value, peek(-1));
        
        // FIXED: Explicitly handle STRING tokens from Lexer
        if (match(Lexer.TokenType.STRING)) {
             return new ASTNode("LITERAL_STRING", peek(-1).value, peek(-1));
        }

        if (current().type == Lexer.TokenType.IDENTIFIER) {
            String name = current().value;
            Lexer.Token token = current();
            pos++;
            while (match(".")) {
                name += "." + current().value;
                match(Lexer.TokenType.IDENTIFIER);
            }
            if (match("(")) {
                ASTNode call = new ASTNode("CALL", name, token);
                if (!current().value.equals(")")) {
                    do { call.add(parseExpression()); } while (match(","));
                }
                expect(")");
                return call;
            }
            return new ASTNode("VAR_REF", name, token);
        }
        
        if (match("(")) { ASTNode expr = parseExpression(); expect(")"); return expr; }
        
        throw new RuntimeException("Unexpected token: " + current());
    }
}