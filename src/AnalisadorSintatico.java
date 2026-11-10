import java.util.ArrayList;
import java.util.List;

public class AnalisadorSintatico {
    private List<Token> listaToken = new ArrayList<>();
    private int pos = 0;

    public AnalisadorSintatico(List<Token> listaToken) {
        this.listaToken = listaToken;
    }

    public int getPos() {
        return pos;
    }

    public Token atual() {
        return listaToken.get(pos);
    }

    public Token proximoToken() {
        Token tokenAtual = atual();
        if (tokenAtual.getTipo() != TipoToken.EOF) {
            pos++;
        }
        return tokenAtual;
    }

    public boolean verificar(TipoToken tipo) {
        return atual().getTipo() == tipo;
    }

    private void erro(String esperado) {
        Token token = atual();

        System.out.printf("Erro na linha %d, coluna %d: esperado '%s', mas encontrado '%s'%n",
                token.getLinha(), token.getColuna(), esperado, token.getTexto());
        System.exit(1);
    }

    public void consumir(TipoToken tipo, String esperado) {
        if (verificar(tipo)) {
            proximoToken();
        } else {
            erro(esperado);
        }
    }

    public void declaracao() {
        if (verificar(TipoToken.INTEIRO) || verificar(TipoToken.REAL)) {
            proximoToken();
        } else {
            erro("int ou double");
        }

        consumir(TipoToken.IDENTIFICADOR, "nome da variavel");

        if (verificar(TipoToken.ATRIBUICAO)) {
            consumir(TipoToken.ATRIBUICAO, "=");
            expressao();
        }

        consumir(TipoToken.PONTO_VIRGULA, ";");
    }

    public void declaracoes() {
        while (verificar(TipoToken.INTEIRO) || verificar(TipoToken.REAL)) {
            declaracao();
        }
    }

    public void fator() {
        if (verificar(TipoToken.NUMERO)) {
            proximoToken();
        } else if (verificar(TipoToken.IDENTIFICADOR)) {
            proximoToken();
        } else {
            erro("numero ou variavel");
        }
    }

    public void termo() {
        fator();
        while (verificar(TipoToken.VEZES) || verificar(TipoToken.DIVISAO)) {
            proximoToken();
            fator();
        }
    }

    public void expressao() {
        termo();
        while (verificar(TipoToken.MAIS) || verificar(TipoToken.MENOS)) {
            proximoToken();
            termo();
        }
    }

    public void condicao() {
        if (verificar(TipoToken.LOGICO)) {
            proximoToken();
        } else {
            expressao();
            if (verificar(TipoToken.IGUAL) || verificar(TipoToken.DIFERENTE)
                    || verificar(TipoToken.MENOR_QUE) || verificar(TipoToken.MAIOR_QUE)
                    || verificar(TipoToken.MAIOR_OU_IGUAL_QUE) || verificar(TipoToken.MENOR_OU_IGUAL_QUE)) {
                proximoToken();
            } else {
                erro("operador relacional");
            }

            expressao();
        }
    }

    public void atribuicao() {
        consumir(TipoToken.IDENTIFICADOR, "nome da variavel");
        consumir(TipoToken.ATRIBUICAO, "=");
        expressao();
        consumir(TipoToken.PONTO_VIRGULA, ";");
    }

    public void bloco() {
        consumir(TipoToken.ABRE_CHAVE, "{");
        while (verificar(TipoToken.IDENTIFICADOR) || verificar(TipoToken.SE) || verificar(TipoToken.ENQUANTO)
                || verificar(TipoToken.REAL) || verificar(TipoToken.INTEIRO)) {
            if (verificar(TipoToken.INTEIRO) || verificar(TipoToken.REAL)) {
                declaracoes();
            } else if (verificar(TipoToken.IDENTIFICADOR)) {
                atribuicao();
            } else {
                comando();
            }
        }
        consumir(TipoToken.FECHA_CHAVE, "}");
    }

    public void iF() {
        consumir(TipoToken.SE, "if");
        consumir(TipoToken.ABRE_PARENTESE, "(");
        condicao();
        consumir(TipoToken.FECHA_PARENTESE, ")");
        bloco();
    }

    public void ifElse() {
        iF();
        if (verificar(TipoToken.SENAO)) {
            consumir(TipoToken.SENAO, "else");
            bloco();
        }
    }

    public void programa() {
        consumir(TipoToken.ALGORITMO, "algoritmo");
        consumir(TipoToken.ABRE_CHAVE, "{");
        declaracoes();
        comando();
        comandos();
        consumir(TipoToken.FECHA_CHAVE, "}");
        consumir(TipoToken.FIM_ALGORITMO, "fim_algoritmo");
        consumir(TipoToken.EOF, "fim do arquivo");
    }

    public void enquanto() {
        consumir(TipoToken.ENQUANTO, "while");
        consumir(TipoToken.ABRE_PARENTESE, "(");
        condicao();
        consumir(TipoToken.FECHA_PARENTESE, ")");
        bloco();
    }

    public void comando() {
        if (verificar(TipoToken.SE)) {
            ifElse();
        } else if (verificar(TipoToken.ENQUANTO)) {
            enquanto();
        } else {
            erro("if ou while");
        }
    }

    public void comandos() {
        while (verificar(TipoToken.ENQUANTO) || verificar(TipoToken.SE)) {
            comando();
        }
    }

}
