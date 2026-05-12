# Análise Semântica - Documentação de Implementação

## Resumo dos Requisitos Atendidos

### 1. ✅ (2 pontos) Inserir identificadores na tabela de símbolos

**Implementado em:**
- `Semantico.java` - Ações semânticas 2, 3, 4, 5
- `TabelaSimbolos.java` - Método `inserir()`

**Características:**
- Suporta inserção de: **variáveis**, **funções**, **parâmetros** e **vetores**
- Cada identificador armazena: nome, tipo, modalidade e escopo
- Realizado na gramática (trabalho.gals) com ações semânticas nas produções

**Ações Semânticas:**
- Ação 1: Captura o tipo atual (int, float, char, string, bool)
- Ação 2: Insere variável no escopo atual
- Ação 3: Insere função no escopo global
- Ação 4: Insere parâmetro no escopo da função
- Ação 5: Insere vetor no escopo atual

---

### 2. ✅ (2 pontos) Verificar se um identificador está declarado no escopo em que é usado

**Implementado em:**
- `TabelaSimbolos.java` - Método `buscar()`
- `Semantico.java` - Ações 10 e 11

**Características:**
- Busca em escopo local primeiro
- Se não encontrar, busca em escopo global (herança de escopo)
- Lança exceção `SemanticError` se não declarado
- Marca identificador como "usado"

**Ações Semânticas:**
- Ação 10: Verifica declaração no uso (leitura) de identificador
- Ação 11: Verifica declaração em atribuição/chamada

---

### 3. ✅ (1 ponto) Garantir a unicidade dos identificadores em um escopo

**Implementado em:**
- `TabelaSimbolos.java` - Método `existe()` + `inserir()`

**Características:**
- Verifica se já existe identific com mesmo nome no mesmo escopo
- Lança `SemanticError` se duplicado: "Erro: identificador 'X' já declarado no escopo 'Y'"
- Permite mesmo nome em escopos diferentes

---

### 4. ✅ (1 ponto) Avisar se os identificadores são declarados e não usados

**Implementado em:**
- `Simbolo.java` - Campo booleano `usado`
- `TabelaSimbolos.java` - Método `verificarNaoUsados()`
- `Semantico.java` - Método `sairEscopo()`

**Características:**
- Rastreia quais identificadores foram usados
- Ao sair do escopo, verifica variáveis não usadas
- Avisos são armazenados em lista e exibidos na interface

**Exemplo de Aviso:**
```
Aviso: a variável 'x' foi declarada mas nunca usada.
```

---

### 5. ✅ (1 ponto) Avisar se os identificadores estão sendo usados sem estar inicializados

**Implementado em:**
- `Simbolo.java` - Campo booleano `inicializado`
- `Semantico.java` - Ação 21
- `TabelaSimbolos.java` - Método `marcarInicializado()`

**Características:**
- Parâmetros são considerados inicializados por padrão
- Variáveis começam como não inicializadas
- Ao usar uma variável não inicializada, gera aviso
- **NÃO RESTRINGE** - apenas avisa (conforme requisito)

**Exemplo de Aviso:**
```
Aviso: a variável 'x' está sendo usada sem ter sido inicializada.
```

---

### 6. ✅ (1 ponto) Acrescentar na interface da IDE um componente para visualização da tabela

**Implementado em:**
- `IDECompilador.java` - Novos componentes:
  - `JTable tabelaSimbolo` com modelo dinâmico
  - `JTabbedPane abas` para organização
  - Método `atualizarTabelaSimbolo()`

**Características:**
- Tabela com colunas: Nome, Tipo, Modalidade, Escopo, Inicializado, Usado
- Atualiza automaticamente após compilação
- Integrada em aba dedicada na interface
- Usa 3 abas: "Erros e Mensagens", "Tabela de Símbolos", "Avisos Semânticos"
- Visualização clara com ✓ e ✗ para status

**Interface Melhorada:**
- Tamanho: 1200x800
- Split pane com código e saída
- Syntax highlighting visual com cores

---

### 7. ✅ (2 pontos) Verificar compatibilidade de tipos em expressões e atribuições

**Implementado em:**
- `Semantico.java` - Ações 40 e 41
- `TabelaSimbolos.java` - Método `getTipo()`

**Características:**
- Rastreia tipo de cada identificador
- Verifica operações inválidas (ex: multiplicação de strings)
- Monitora tipos em atribuições
- Avisos de incompatibilidade sem restrição (conforme preferência)

**Ações Semânticas:**
- Ação 40: Verifica compatibilidade em atribuição
- Ação 41: Verifica operações válidas com tipo

**Exemplos:**
```
Aviso: atribuição entre tipos diferentes: int e string.
Erro: operação '*' não permitida com tipo string.
```

---

## Estrutura de Classes

### `Simbolo.java`
```
- nome: String
- tipo: String (int, float, char, string, bool)
- modalidade: String (variável, vetor, parâmetro, função)
- escopo: String
- inicializado: boolean
- usado: boolean
- tamanho: int (para vetores)
```

### `TabelaSimbolos.java`
```
Métodos principais:
- existe(nome, escopo): boolean
- inserir(nome, tipo, modalidade, escopo): void
- buscar(nome, escopo): Simbolo
- marcarUsado(nome, escopo): void
- marcarInicializado(nome, escopo): void
- getTipo(nome, escopo): String
- verificarNaoUsados(): List<Simbolo>
- getTabela(): List<Simbolo>
```

### `Semantico.java`
```
Ações Semânticas:
1  - Capturar tipo
2  - Inserir variável
3  - Inserir função
4  - Inserir parâmetro
5  - Inserir vetor
10 - Verificar uso (leitura)
11 - Verificar em atribuição
20 - Marcar como inicializado
21 - Verificar inicialização
30 - Entrar em novo escopo
31 - Retornar ao escopo global
40 - Verificar compatibilidade tipos
41 - Verificar operação válida
99 - Exibir tabela
```

---

## Integração na Gramática

As ações semânticas foram integradas no arquivo `trabalho.gals` com o formato GALS:

```gals
<tipo> ::= INT_KW #1 | FLOAT_KW #1 | CHAR_KW #1 | STRING_KW #1 | BOOL_KW #1 ;
<id_array> ::= ID_TK #2 | ID_TK #5 ABRE_COL <expressao> FECHA_COL | ID_TK #20 ATRIB <expressao> ;
<acesso> ::= ID_TK #21 <compl_acesso> ;
<fator> ::= ... | ID_TK #10 <fator_id> | ... ;
```

---

## Testes

Uma série de testes está incluída no método `main()` de `Semantico.java`:

```java
public static void main(String[] args) throws Exception {
    Semantico s = new Semantico();
    
    // Teste: inserir variáveis
    s.tipoAtual = "int";
    s.executeAction(2, new Token(0, "x", 0));
    
    // Teste: usar sem inicializar
    s.executeAction(21, new Token(0, "x", 0));
    
    // Teste: marcar como inicializado
    s.executeAction(20, new Token(0, "x", 0));
    
    // Exibir tabela
    s.executeAction(99, new Token(0, "", 0));
}
```

---

## Como Usar

1. **Compilar o projeto:**
   ```bash
   javac *.java
   ```

2. **Executar a IDE:**
   ```bash
   java IDECompilador
   ```

3. **Usar a interface:**
   - Cole código no editor esquerdo
   - Clique em "▶ Realizar Análise"
   - Veja resultados nas abas
   - Consulte tabela de símbolos na aba "Tabela de Símbolos"
   - Verifique avisos na aba "Avisos Semânticos"

---

## Exemplo de Código de Teste

```c
int x;
int y;

x = 10;
y = 20;

se (x > 5) entao
    y = x + 5;
end

// Avisos gerados:
// - Se alguma variável for declarada mas não usada
// - Se alguma variável for usada sem inicialização
```

---

## Notas Importantes

- **Escopos:** Global por padrão, mudam ao entrar em funções/procedimentos
- **Herança de Escopo:** Variáveis globais são acessíveis de escopos locais
- **Parâmetros:** Considerados inicializados ao serem inseridos
- **Vetores:** Suportam dimensão e inicialização na declaração
- **Tipos:** Suportados: int, float, char, string, bool
- **Avisos não Restringem:** Incompatibilidades geram avisos mas não travam compilação

---

## Referências

- GALS - Gerador de Analisadores Léxicos e Sintáticos
- Análise Semântica: Verificação de tipos, escopos e declarações
- Tabela de Símbolos: Estrutura central para análise semântica
