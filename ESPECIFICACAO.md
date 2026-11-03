# Especificação da linguagem MiniLang

MiniLang é uma linguagem imperativa simplificada, criada para o trabalho de analisador sintático. Este documento define a estrutura léxica (os tokens), a gramática e as regras da linguagem, conforme implementado no `Lexer` e no `Parser` deste repositório.

Autor: Kaiki Ribeiro Avelino

## 1. Visão geral

- Um programa é uma **sequência de declarações de variáveis** seguida de **um ou mais comandos**, tudo entre uma palavra de início e uma palavra de término.
- Os comandos são o **`if`/`else`** e o **`while`**, que podem ser combinados, encadeados e aninhados.
- **Variáveis podem aparecer dentro de cada bloco**, tanto declaradas quanto atribuídas.
- A linguagem tem dois tipos de variável: `int` e `double`.
- As condições aceitam comparações (`x < 10`) e os valores booleanos `true` e `false`.
- O analisador **valida apenas a sintaxe** (a ordem dos tokens). Ele não executa o programa e não verifica o significado do código (veja a seção 6).

## 2. Estrutura de um programa

```
algoritmo {
  <declarações: zero ou mais>
  <comandos if/else e while: um ou mais>
}
fim_algoritmo
```

Exemplo:

```
algoritmo {
  int x = 0;
  int y = 0;
  while (x < 10) {
    int z = x * 2;
    if (z > 5) {
      y = y + 1;
    } else {
      y = y + 2;
    }
    x = x + 1;
  }
}
fim_algoritmo
```

Ordem obrigatória: `algoritmo`, `{`, declarações, comandos, `}`, `fim_algoritmo`. Depois do `fim_algoritmo` não pode haver mais nada.

## 3. Estrutura léxica

### 3.1 Espaços em branco

Espaços, tabulações e quebras de linha **separam tokens e são ignorados**. A linguagem não tem comentários.

### 3.2 Maiúsculas e minúsculas

A linguagem diferencia maiúsculas de minúsculas. As palavras reservadas só são reconhecidas em **minúsculas**: `If`, `INT` ou `True` são identificadores comuns.

### 3.3 Palavras reservadas

| Palavra | Token |
|---|---|
| `algoritmo` | `ALGORITMO` |
| `fim_algoritmo` | `FIM_ALGORITMO` |
| `int` | `INTEIRO` |
| `double` | `REAL` |
| `if` | `SE` |
| `else` | `SENAO` |
| `while` | `ENQUANTO` |
| `true` | `LOGICO` |
| `false` | `LOGICO` |

`true` e `false` geram o mesmo tipo de token (`LOGICO`), e o **texto** do token diferencia um do outro. Como são palavras reservadas, não podem ser usadas como nome de variável.

### 3.4 Identificadores

Nome de variável. Começa com uma **letra** ou `_`, seguida de letras, dígitos ou `_`. Uma palavra reservada não pode ser usada como identificador.

```
ID → ( LETRA | "_" ) { LETRA | DIGITO | "_" }
```

Exemplos válidos: `x`, `total`, `valor_1`, `_aux`.

### 3.5 Números

Número inteiro ou decimal, sem sinal.

```
NUMERO → DIGITO { DIGITO } [ "." DIGITO { DIGITO } ]
```

Exemplos válidos: `7`, `100`, `2.5`.

Observação: a implementação atual também aceita `2.` (ponto sem dígitos depois) como número.

### 3.6 Símbolos

| Símbolo | Token | Símbolo | Token |
|---|---|---|---|
| `(` | `ABRE_PARENTESE` | `==` | `IGUAL` |
| `)` | `FECHA_PARENTESE` | `!=` | `DIFERENTE` |
| `{` | `ABRE_CHAVE` | `<` | `MENOR_QUE` |
| `}` | `FECHA_CHAVE` | `>` | `MAIOR_QUE` |
| `;` | `PONTO_VIRGULA` | `<=` | `MENOR_OU_IGUAL_QUE` |
| `=` | `ATRIBUICAO` | `>=` | `MAIOR_OU_IGUAL_QUE` |
| `+` | `MAIS` | `*` | `VEZES` |
| `-` | `MENOS` | `/` | `DIVISAO` |

Os símbolos de dois caracteres (`==`, `!=`, `<=`, `>=`) são lidos como **um único token**. O `!` sozinho não existe na linguagem.

Além desses tokens, o analisador léxico gera um token `EOF`, que marca o fim do arquivo.

## 4. Gramática (EBNF)

Notação: `{ ... }` significa zero ou mais repetições, `[ ... ]` significa opcional, `|` significa alternativa, e o texto entre aspas é um token literal.

```
programa    → "algoritmo" "{" { declaracao } comando { comando } "}" "fim_algoritmo" EOF

declaracao  → ( "int" | "double" ) ID [ "=" expressao ] ";"

comando     → ifElse | enquanto
ifElse      → "if" "(" condicao ")" bloco [ "else" bloco ]
enquanto    → "while" "(" condicao ")" bloco
bloco       → "{" { instrucao } "}"
instrucao   → declaracao | atribuicao | comando
atribuicao  → ID "=" expressao ";"

condicao    → "true" | "false" | expressao opRel expressao
opRel       → "==" | "!=" | "<" | ">" | "<=" | ">="

expressao   → termo { ( "+" | "-" ) termo }
termo       → fator { ( "*" | "/" ) fator }
fator       → NUMERO | ID
```

As regras `comando` e `bloco` se chamam mutuamente: um comando contém blocos, e um bloco contém comandos. Essa **recursão** é o que permite aninhar `if` e `while` em qualquer profundidade.

## 5. Regras da linguagem

**Estrutura do programa**
- O programa tem **pelo menos um comando** (`if` ou `while`). Um programa sem nenhum comando gera erro.
- Os comandos do programa podem aparecer **em sequência**: por exemplo, um `if` seguido de um `while`.
- No corpo do programa, as declarações vêm **antes** dos comandos. Declarar uma variável depois de um comando, fora de um bloco, gera erro.
- Pode haver **zero declarações** no corpo do programa.
- O corpo do programa fica entre chaves, entre `algoritmo` e `fim_algoritmo`.

**Declarações**
- Uma declaração tem o tipo, o nome e, opcionalmente, um valor inicial: `int x;` ou `int x = 5;`.
- O valor inicial pode ser uma expressão: `int x = 2 + 3 * y;`.
- Declarações também podem aparecer **dentro de blocos**, junto com as atribuições e os outros comandos.

**Comandos**
- O `if` leva uma condição entre parênteses e um bloco. O `else` e o seu bloco são **opcionais**.
- O `while` leva uma condição entre parênteses e um bloco.
- Dentro de um bloco cabem declarações, atribuições, `if` e `while`, em qualquer ordem e quantidade. Isso permite `while` sozinho, `if` com `while` dentro e `while` com `if` dentro.
- Um bloco vazio (`{ }`) é válido.

**Atribuições**
- Uma atribuição tem a forma `nome = expressao;` e só pode aparecer **dentro de blocos**.

**Expressões**
- Operadores aritméticos: `+`, `-`, `*`, `/`.
- `*` e `/` têm **prioridade** sobre `+` e `-`. Operadores do mesmo nível são lidos da esquerda para a direita. Por exemplo, em `2 + 3 * 4`, o `3 * 4` é agrupado primeiro.
- Os operandos são números ou nomes de variáveis.

**Condições**
- Uma condição é `true`, `false` ou uma comparação entre **duas expressões** com **um** operador relacional, como `x + 1 >= y * 2`.
- Depois de `true` ou `false` só pode vir o `)` que fecha a condição.
- Não existem operadores lógicos (`&&`, `||`, `!`).

## 6. O que a linguagem não possui

A gramática não prevê:

- parênteses dentro de expressões (por exemplo, `(2 + 3) * 4` é inválido);
- números negativos escritos com sinal (`-5`) e o operador unário `-`;
- operadores lógicos (`&&`, `||`, `!`) e variáveis booleanas: `true` e `false` só aparecem em condições, e `x = true;` é inválido;
- `else if` sem chaves (para encadear, é preciso escrever `else { if ... }`);
- outros laços (`for`, `do-while`), funções, comentários e textos (strings).

**Análise semântica não é feita.** O analisador não verifica, por exemplo:

- se uma variável foi declarada antes do uso;
- se uma variável foi declarada mais de uma vez;
- o escopo das variáveis declaradas dentro de blocos;
- se os tipos combinam (`int x = 2.5;` é aceito);
- divisão por zero;
- laços infinitos (`while (true)` é aceito).

## 7. Tratamento de erros

O analisador **para no primeiro erro** encontrado e mostra a linha, a coluna e uma mensagem descritiva. Linhas e colunas começam em **1**, e cada caractere (inclusive o espaço e a tabulação) ocupa uma coluna.

**Erro sintático**

```
Erro na linha <L>, coluna <C>: esperado '<o que era esperado>', mas encontrado '<texto do token>'
```

Quando o token encontrado é o fim do arquivo, o texto fica vazio (`encontrado ''`).

**Erros léxicos**

```
Erro na linha <L>, coluna <C>: caractere invalido '<c>'
Erro na linha <L>, coluna <C>: esperado '=' depois de '!'
```

O analisador léxico lê o arquivo inteiro antes de o analisador sintático começar. Por isso, um erro léxico é sempre mostrado antes de qualquer erro sintático, mesmo que o erro sintático esteja em uma linha anterior.

**Mensagens "esperado" usadas pelo analisador sintático**

| Mensagem | Quando aparece |
|---|---|
| `algoritmo` | o programa não começa com a palavra de início |
| `{` | falta a chave que abre o programa ou um bloco |
| `}` | falta a chave que fecha um bloco ou o programa. Também aparece quando surge algo que não é declaração nem comando no corpo do programa, como uma declaração depois de um comando |
| `if ou while` | o programa não tem nenhum comando |
| `nome da variavel` | falta o nome em uma declaração |
| `=` | falta o `=` em uma atribuição |
| `;` | falta o ponto e vírgula |
| `numero ou variavel` | uma expressão está incompleta |
| `operador relacional` | falta o operador em uma comparação |
| `(` / `)` | falta um parêntese na condição do `if` ou do `while` |
| `fim_algoritmo` | falta a palavra de término |
| `fim do arquivo` | existe código depois do `fim_algoritmo` |

## 8. Exemplos válidos

**`while` sozinho**

```
algoritmo {
  int x = 0;
  while (x < 10) {
    x = x + 1;
  }
}
fim_algoritmo
```

**`if` com `while` dentro**

```
algoritmo {
  int x = 0;
  if (x < 10) {
    while (x < 5) {
      x = x + 1;
    }
  } else {
    x = 0;
  }
}
fim_algoritmo
```

**`while` com `if` dentro**

```
algoritmo {
  int x = 0;
  int y = 0;
  while (x < 10) {
    if (x == 5) {
      y = y + 1;
    } else {
      y = y + 2;
    }
    x = x + 1;
  }
}
fim_algoritmo
```

**Declarações dentro dos blocos**

```
algoritmo {
  int x = 0;
  while (x < 10) {
    int y = x * 2;
    if (y > 5) {
      double z = 1.5;
      x = x + 1;
    }
    x = x + 1;
  }
}
fim_algoritmo
```

**Condições booleanas**

```
algoritmo {
  int x = 0;
  while (true) {
    if (false) {
      x = 1;
    } else {
      x = 2;
    }
  }
}
fim_algoritmo
```

**Vários comandos em sequência**

```
algoritmo {
  int x = 0;
  if (x < 10) {
    x = 1;
  }
  while (x < 5) {
    x = x + 1;
  }
}
fim_algoritmo
```

**`if` sem `else`**

```
algoritmo {
  int x;
  if (x < 10) {
    x = x + 1;
  }
}
fim_algoritmo
```

**Com contas e decimais**

```
algoritmo {
  int a = 2 + 3 * 4;
  double b = a / 2 - 1;
  if (a + 1 >= b * 2) {
    a = a - 1;
    b = b + 0.5;
  } else {
    a = a * 2;
  }
}
fim_algoritmo
```

## 9. Exemplos inválidos

**Falta o `;` em uma declaração**

```
algoritmo {
  int x
  double y;
  if (x < 10) {
  }
}
fim_algoritmo
```

```
Erro na linha 3, coluna 3: esperado ';', mas encontrado 'double'
```

**Falta o `)` no `while`**

```
algoritmo {
  int x = 0;
  while (x < 10 {
    x = x + 1;
  }
}
fim_algoritmo
```

```
Erro na linha 3, coluna 17: esperado ')', mas encontrado '{'
```

**Programa sem nenhum comando**

```
algoritmo {
  int x;
}
fim_algoritmo
```

```
Erro na linha 3, coluna 1: esperado 'if ou while', mas encontrado '}'
```

**Booleano seguido de operador**

```
algoritmo {
  if (true < 1) {
  }
}
fim_algoritmo
```

```
Erro na linha 2, coluna 12: esperado ')', mas encontrado '<'
```

**Parênteses em uma expressão**

```
algoritmo {
  int x = (2 + 3) * 4;
  if (x < 10) {
  }
}
fim_algoritmo
```

```
Erro na linha 2, coluna 11: esperado 'numero ou variavel', mas encontrado '('
```

**Declaração depois de um comando, fora de um bloco**

```
algoritmo {
  if (x < 10) {
  }
  int y;
}
fim_algoritmo
```

```
Erro na linha 4, coluna 3: esperado '}', mas encontrado 'int'
```

**Caractere inválido**

```
algoritmo {
  if (x < 10) {
    x = x @ 1;
  }
}
fim_algoritmo
```

```
Erro na linha 3, coluna 11: caractere invalido '@'
```

## 10. Decisões de projeto

O enunciado deixou alguns pontos em aberto. Nesta implementação:

| Ponto | Decisão |
|---|---|
| Palavras de início e término | `algoritmo` e `fim_algoritmo` |
| Corpo do programa | entre chaves, dentro das palavras de início e término |
| Comandos do programa | um ou mais, `if`/`else` ou `while`, em sequência |
| `else` | opcional, como em Java |
| Conteúdo dos blocos | declarações, atribuições, `if` e `while`, com aninhamento livre |
| Valores booleanos | `true` e `false`, como em Java, aceitos apenas em condições |
| Ordem no corpo do programa | declarações antes dos comandos |

## 11. Relação com a implementação

Cada regra da gramática corresponde a um método da classe `Parser`:

| Regra | Método |
|---|---|
| `programa` | `programa()` |
| `declaracao` | `declaracao()` (e `declaracoes()` para a repetição) |
| `comando` | `comando()` (e `comandos()` para a repetição) |
| `ifElse` | `ifElse()` (que usa `iF()` para a parte do `if`) |
| `enquanto` | `enquanto()` |
| `bloco` e `instrucao` | `bloco()` |
| `atribuicao` | `atribuicao()` |
| `condicao` | `condicao()` |
| `expressao` | `expressao()` |
| `termo` | `termo()` |
| `fator` | `fator()` |

A análise léxica é feita pela classe `Lexer`, e os tokens são descritos pelas classes `Token` e `TokenType`.