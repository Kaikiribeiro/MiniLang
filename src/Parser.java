import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Token> listaToken = new ArrayList<>();
    private int pos = 0;

    public Parser(List<Token> listaToken) {
        this.listaToken = listaToken;
    }

    public int getPos() {
        return pos;
    }

    public Token peek() {
        return listaToken.get(pos);
    }

    public Token avancar() {
        Token atual = peek();
        if (atual.getTipo() != TokenType.EOF) {
            pos++;
        }
        return atual;
    }

    public boolean verificar(TokenType tokenType) {
        return peek().getTipo() == tokenType;
    }

    private void erro(String esperado) {
        Token token = peek();

        System.out.printf("Erro na linha %d, coluna %d: esperado '%s', mas encontrado '%s'%n",
                token.getLinha(), token.getColuna(), esperado, token.getTexto());
        System.exit(1);
    }

    public void consumir(TokenType tipo, String esperado) {
        if (verificar(tipo)) {
            avancar();
        } else {
            erro(esperado);
        }
    }

    public void declaracao() {
        if (verificar(TokenType.INTEIRO) || verificar(TokenType.REAL)) {
            avancar();
        } else {
            erro("int ou double");
        }

        consumir(TokenType.IDENTIFICADOR, "nome da variavel");

        if (verificar(TokenType.ATRIBUICAO)) {
            consumir(TokenType.ATRIBUICAO, "=");
            expressao();
        }

        consumir(TokenType.PONTO_VIRGULA, ";");
    }

    public void declaracoes() {
        while (verificar(TokenType.INTEIRO) || verificar(TokenType.REAL)) {
            declaracao();
        }
    }

    public void fator() {
        if (verificar(TokenType.NUMERO)) {
            avancar();
        } else if (verificar(TokenType.IDENTIFICADOR)) {
            avancar();
        } else {
            erro("numero ou variavel");
        }
    }

    public void termo() {
        fator();
        while (verificar(TokenType.VEZES) || verificar(TokenType.DIVISAO)) {
            avancar();
            fator();
        }
    }

    public void expressao() {
        termo();
        while (verificar(TokenType.MAIS) || verificar(TokenType.MENOS)) {
            avancar();
            termo();
        }
    }

    public void condicao() {
        if (verificar(TokenType.LOGICO)) {
            avancar();
        } else {
            expressao();
            if (verificar(TokenType.IGUAL) || verificar(TokenType.DIFERENTE)
                    || verificar(TokenType.MENOR_QUE) || verificar(TokenType.MAIOR_QUE)
                    || verificar(TokenType.MAIOR_OU_IGUAL_QUE) || verificar(TokenType.MENOR_OU_IGUAL_QUE)) {
                avancar();
            } else {
                erro("operador relacional");
            }

            expressao();
        }
    }

    public void atribuicao() {
        consumir(TokenType.IDENTIFICADOR, "nome da variavel");
        consumir(TokenType.ATRIBUICAO, "=");
        expressao();
        consumir(TokenType.PONTO_VIRGULA, ";");
    }

    public void bloco() {
        consumir(TokenType.ABRE_CHAVE, "{");
        while (verificar(TokenType.IDENTIFICADOR) || verificar(TokenType.SE) || verificar(TokenType.ENQUANTO)
                || verificar(TokenType.REAL) || verificar(TokenType.INTEIRO)) {
            if (verificar(TokenType.INTEIRO) || verificar(TokenType.REAL)) {
                declaracoes();
            } else if (verificar(TokenType.IDENTIFICADOR)) {
                atribuicao();
            } else {
                comando();
            }
        }
        consumir(TokenType.FECHA_CHAVE, "}");
    }

    public void iF() {
        consumir(TokenType.SE, "if");
        consumir(TokenType.ABRE_PARENTESE, "(");
        condicao();
        consumir(TokenType.FECHA_PARENTESE, ")");
        bloco();
    }

    public void ifElse() {
        iF();
        if (verificar(TokenType.SENAO)) {
            consumir(TokenType.SENAO, "else");
            bloco();
        }
    }

    public void programa() {
        consumir(TokenType.ALGORITMO, "algoritmo");
        consumir(TokenType.ABRE_CHAVE, "{");
        declaracoes();
        comando();
        comandos();
        consumir(TokenType.FECHA_CHAVE, "}");
        consumir(TokenType.FIM_ALGORITMO, "fim_algoritmo");
        consumir(TokenType.EOF, "fim do arquivo");
    }

    public void enquanto() {
        consumir(TokenType.ENQUANTO, "while");
        consumir(TokenType.ABRE_PARENTESE, "(");
        condicao();
        consumir(TokenType.FECHA_PARENTESE, ")");
        bloco();
    }

    public void comando() {
        if (verificar(TokenType.SE)) {
            ifElse();
        } else if (verificar(TokenType.ENQUANTO)) {
            enquanto();
        } else {
            erro("if ou while");
        }
    }

    public void comandos() {
        while (verificar(TokenType.ENQUANTO) || verificar(TokenType.SE)) {
            comando();
        }
    }

}
