import java.util.List;
import java.util.ArrayList;

public class AnalisadorLexico {

    private String codigo;
    private int pos = 0;
    private int linha = 1;
    private int coluna = 1;

    public AnalisadorLexico(String codigo) {
        this.codigo = codigo;
    }

    private boolean fim() {
        return pos >= codigo.length();
    }

    private char atual() {
        return codigo.charAt(pos);
    }

    private char proximoChar() {
        char c = codigo.charAt(pos);
        pos++;
        if (c == '\n') {
            linha++;
            coluna = 1;
        } else {
            coluna++;
        }
        return c;
    }

    public void pularEspaco() {
        while (!fim() && Character.isWhitespace(atual())) {
            proximoChar();
        }
    }

    private boolean casar(char esperado) {
        if (fim() || atual() != esperado) {
            return false;
        }
        proximoChar();
        return true;
    }

    private TipoToken tipoPalavra(String texto) {
        switch (texto) {
            case "int":
                return TipoToken.INTEIRO;
            case "double":
                return TipoToken.REAL;
            case "if":
                return TipoToken.SE;
            case "else":
                return TipoToken.SENAO;
            case "while":
                return TipoToken.ENQUANTO;
            case "algoritmo":
                return TipoToken.ALGORITMO;
            case "fim_algoritmo":
                return TipoToken.FIM_ALGORITMO;
            case "true":
                return TipoToken.LOGICO;
            case "false":
                return TipoToken.LOGICO;
            default:
                return TipoToken.IDENTIFICADOR;
        }
    }

    public List<Token> tokenizar() {
        List<Token> tokens = new ArrayList<>();

        while (true) {
            pularEspaco();
            if (fim()) {
                break;
            }

            int c = coluna;
            int l = linha;
            char ch = proximoChar();

            switch (ch) {
                case '(':
                    tokens.add(new Token(TipoToken.ABRE_PARENTESE, "(", l, c));
                    break;

                case ')':
                    tokens.add(new Token(TipoToken.FECHA_PARENTESE, ")", l, c));
                    break;
                case '{':
                    tokens.add(new Token(TipoToken.ABRE_CHAVE, "{", l, c));
                    break;
                case '}':
                    tokens.add(new Token(TipoToken.FECHA_CHAVE, "}", l, c));
                    break;
                case ';':
                    tokens.add(new Token(TipoToken.PONTO_VIRGULA, ";", l, c));
                    break;
                case '+':
                    tokens.add(new Token(TipoToken.MAIS, "+", l, c));
                    break;
                case '-':
                    tokens.add(new Token(TipoToken.MENOS, "-", l, c));
                    break;
                case '*':
                    tokens.add(new Token(TipoToken.VEZES, "*", l, c));
                    break;
                case '/':
                    tokens.add(new Token(TipoToken.DIVISAO, "/", l, c));
                    break;
                case '=':
                    if (casar('=')) {
                        tokens.add(new Token(TipoToken.IGUAL, "==", l, c));
                    } else {
                        tokens.add(new Token(TipoToken.ATRIBUICAO, "=", l, c));
                    }
                    break;
                case '<':
                    if (casar('=')) {
                        tokens.add(new Token(TipoToken.MENOR_OU_IGUAL_QUE, "<=", l, c));
                    } else {
                        tokens.add(new Token(TipoToken.MENOR_QUE, "<", l, c));
                    }
                    break;
                case '>':
                    if (casar('=')) {
                        tokens.add(new Token(TipoToken.MAIOR_OU_IGUAL_QUE, ">=", l, c));
                    } else {
                        tokens.add(new Token(TipoToken.MAIOR_QUE, ">", l, c));
                    }
                    break;
                case '!':
                    if (casar('=')) {
                        tokens.add(new Token(TipoToken.DIFERENTE, "!=", l, c));
                    } else {
                        System.out.printf("Erro na linha %d, coluna %d: esperado '=' depois de '!'%n", l, c);
                        System.exit(1);
                    }
                    break;

                default:
                    if (Character.isDigit(ch)) {
                        StringBuilder num = new StringBuilder();
                        num.append(ch);

                        while (!fim() && Character.isDigit(atual())) {
                            num.append(proximoChar());
                        }

                        if (casar('.')) {
                            num.append('.');
                            while (!fim() && Character.isDigit(atual())) {
                                num.append(proximoChar());
                            }
                        }

                        tokens.add(new Token(TipoToken.NUMERO, num.toString(), l, c));
                    } else if (Character.isLetter(ch) || ch == '_') {
                        StringBuilder palavra = new StringBuilder();
                        palavra.append(ch);

                        while (!fim() && (Character.isLetterOrDigit(atual()) || atual() == '_')) {
                            palavra.append(proximoChar());
                        }

                        String texto = palavra.toString();
                        tokens.add(new Token(tipoPalavra(texto), texto, l, c));
                    } else {
                        System.out.printf("Erro na linha %d, coluna %d: caractere invalido '%c'%n", l, c, ch);
                        System.exit(1);
                    }
                    break;
            }
        }
        tokens.add(new Token(TipoToken.EOF, "", linha, coluna));
        return tokens;
    }
}