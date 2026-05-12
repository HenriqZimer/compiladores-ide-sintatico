# Alinhamento: trabalho.gals ↔ Semantico.java

## Ações Semânticas Implementadas

| Ação | Localização em trabalho.gals | Implementação em Semantico.java | Status |
|------|------------------------------|--------------------------------|--------|
| **1** | `<tipo> ::= INT_KW #1 \| ...` | Captura tipo: `tipoAtual = token.getLexeme()` | ✅ |
| **2** | `<id_array> ::= ID_TK #2` | Inserir variável simples | ✅ |
| **5** | `<id_array> ::= ID_TK #5 ABRE_COL ...` | Inserir vetor | ✅ |
| **10** | `<fator> ::= ... \| ID_TK #10 <fator_id>` | Verificar em expressão + inicialização | ✅ |
| **11** | `<cmd_atrib_chamada> ::= ID_TK #11 ...` | Verificar em atribuição/chamada | ✅ |
| **20** | `<id_array> ::= ID_TK #20 ATRIB <expressao>` | Marcar como inicializado | ✅ |
| **21** | `<acesso> ::= ID_TK #21 <compl_acesso>` | Verificar em leitura (cmd_leia) | ✅ |
| **41** | `<termo> ::= <termo> <op_mult> #41 <fator>` | Operações em expressões aritméticas | ✅ |

## Fluxo de Execução

### 1️⃣ Declaração de Variável
```
Código: int x;
         ↓
Ação 1: Tipo capturado ("int") → tipoAtual = "int"
         ↓
Ação 2: Variável inserida → tabela.inserir("x", "int", "variavel", "global")
```

### 2️⃣ Declaração de Vetor
```
Código: int v[10];
         ↓
Ação 1: Tipo capturado ("int")
         ↓
Ação 5: Vetor inserido → tabela.inserir("v", "int", "vetor", "global")
```

### 3️⃣ Atribuição com Inicialização
```
Código: x = 5;
         ↓
Ação 11: Verificar se "x" existe
         ↓
Ação 20: Marcar como inicializado → s.inicializado = true
```

### 4️⃣ Uso em Expressão
```
Código: y = x + 1;
         ↓
Ação 10: Verificar "x" + inicialização + marcar como usado
         ↓
Ação 41: Verificar operação aritmética
```

### 5️⃣ Leitura (cmd_leia)
```
Código: leia(x);
         ↓
Ação 21: Verificar "x" + marcar como inicializado (recebe valor de leitura)
```

## Estrutura da Tabela de Símbolos

```
Simbolo {
  - nome: String          // "x", "y", "v"
  - tipo: String          // "int", "float", "char", "string", "bool"
  - modalidade: String    // "variavel", "vetor", "parametro", "funcao"
  - escopo: String        // "global" ou nome da função
  - inicializado: boolean // false, true após atribuição ou leitura
  - usado: boolean        // false, true quando acessado
}
```

## Avisos Semânticos Gerados

### ✓ Aviso 1: Variável não inicializada
```
Quando: Ação 10 sobre variável com inicializado = false
Mensagem: "Aviso: a variável 'X' está sendo usada sem ter sido inicializada."
```

### ✓ Aviso 2: Identificador não declarado
```
Quando: Ações 10, 11, 21 tentam acessar identificador inexistente
Mensagem: "Erro: identificador 'X' não foi declarado no escopo."
Tipo: ERRO (paralisa compilação)
```

### ✓ Aviso 3: Variável não usada
```
Quando: Ao final, verificar symblos com usado = false
Implementado: Em IDECompilador.java após compilação
```

## Interface IDE

A interface `IDECompilador.java` exibe:

1. **Editor de Código** (esquerda)
2. **3 Abas de Saída** (direita):
   - Erros e Mensagens
   - Tabela de Símbolos (com coluna "Inicializado" e "Usado")
   - Avisos Semânticos

## Alinhamento Verificado

- ✅ Sem ações não utilizadas na gramática
- ✅ Sem ações faltando na implementação
- ✅ Sem métodos desnecessários
- ✅ Compilação sem erros
- ✅ Interface atualizada com dados da tabela
- ✅ Avisos integrados

---

**Data de Alinhamento:** 12 de maio de 2026  
**Status:** COMPLETO E SINCRONIZADO COM trabalho.gals
