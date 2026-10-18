# MiniLang: Analisador Léxico e Sintático

Projeto em Java que valida se um código-fonte escrito em **MiniLang**, uma linguagem imperativa simplificada, está sintaticamente correto. O programa lê um arquivo de texto, separa o código em tokens (análise léxica) e confere se a ordem dos tokens segue a gramática (análise sintática). Quando encontra um erro, mostra a **linha**, a **coluna** e uma mensagem dizendo o que era esperado.

Autor: Kaiki Ribeiro Avelino

## A linguagem

Um programa MiniLang tem esta estrutura: a palavra de início, chaves, zero ou mais declarações de variáveis, um bloco if-else e a palavra de término.

```
algoritmo {
  int x;
  double y = 2.5;
  if (x < 10) {
    x = x + 1;
  } else {
    y = y * 2;
  }
}
fim_algoritmo
```

### Decisões de projeto

O enunciado deixou alguns pontos em aberto. Nesta implementação:

- **O corpo do programa fica entre chaves**, entre `algoritmo` e `fim_algoritmo`.
- **O `else` é opcional**, como em Java. Um `if` sem `else` também é válido.
- **Dentro das chaves do `if` e do `else` só entram atribuições**, como `x = x + 1;`.
- **A palavra `while` é reconhecida pelo Lexer**, mas não faz parte da gramática, porque a estrutura pede um único bloco if-else.

## Gramática (EBNF)

Chaves `{ }` significam "zero ou mais vezes" e colchetes `[ ]` significam "opcional".

```
programa    → "algoritmo" "{" { declaracao } ifElse "}" "fim_algoritmo" EOF
declaracao  → ( "int" | "double" ) ID [ "=" expressao ] ";"
ifElse      → "if" "(" condicao ")" bloco [ "else" bloco ]
bloco       → "{" { atribuicao } "}"
atribuicao  → ID "=" expressao ";"
condicao    → expressao opRel expressao
opRel       → "==" | "!=" | "<" | ">" | "<=" | ">="
expressao   → termo { ( "+" | "-" ) termo }
termo       → fator { ( "*" | "/" ) fator }
fator       → NUMERO | ID
```

A separação em `expressao`, `termo` e `fator` faz com que `*` e `/` tenham prioridade sobre `+` e `-`.

## Tokens

- **Palavras reservadas:** `algoritmo`, `fim_algoritmo`, `int`, `double`, `if`, `else`, `while`
- **Símbolos:** `(` `)` `{` `}` `;` `=` `+` `-` `*` `/` `==` `!=` `<` `>` `<=` `>=`
- **Identificador:** nome de variável. Começa com letra ou `_` e continua com letras, dígitos ou `_`.
- **Número:** inteiro (`10`) ou decimal (`2.5`).

## Como funciona

| Classe | Função |
|---|---|
| `TokenType` | `enum` com todas as categorias de token |
| `Token` | guarda o tipo, o texto, a linha e a coluna de um token |
| `Lexer` | lê o texto do código letra por letra e gera a lista de tokens, contando linha e coluna |
| `Parser` | recebe a lista de tokens e confere se ela segue a gramática. Tem um método para cada regra, usando descida recursiva |
| `Main` | lê o arquivo, executa o `Lexer`, depois o `Parser`, e mostra o resultado |

O `Parser` usa quatro métodos de apoio: `peek()` (olha o token atual), `avancar()` (passa para o próximo), `verificar(tipo)` (confere o tipo do token atual) e `consumir(tipo, esperado)` (exige um tipo e mostra o erro se não for ele).

O programa **para no primeiro erro** encontrado.

## Estrutura do repositório

```
├── src/
│   ├── Main.java
│   ├── TokenType.java
│   ├── Token.java
│   ├── Lexer.java
│   └── Parser.java
├── exemplos/
│   ├── valido.txt
│   └── erro-*.txt
└── README.md
```

## Como rodar

Requisito: Java 11 ou superior.

Na pasta raiz do projeto:

```bash
javac -d out src/*.java
java -cp out Main
```

O `Main` lê o arquivo indicado na linha com `Files.readString(...)`. Para testar outro exemplo, troque o caminho nessa linha (por exemplo, `exemplos/erro-sem-fim.txt`) e rode de novo.

No IntelliJ, basta abrir o projeto e executar a classe `Main`.

## Exemplos

### Código válido

Arquivo `exemplos/valido.txt`:

```
Codigo sintaticamente correto!
```

### Erros

**Falta o `;` depois de `int x`** (`exemplos/erro-sem-ponto-virgula.txt`):

```
Erro na linha 3, coluna 3: esperado ';', mas encontrado 'double'
```

**Falta o `)` no `if`** (`exemplos/erro-sem-parentese.txt`):

```
Erro na linha 4, coluna 14: esperado ')', mas encontrado '{'
```

**Caractere inválido** (`exemplos/erro-caractere-invalido.txt`):

```
Erro na linha 5, coluna 11: caractere invalido '@'
```

A pasta `exemplos/` tem outros arquivos, válidos e com erro, que cobrem as demais regras da gramática.
