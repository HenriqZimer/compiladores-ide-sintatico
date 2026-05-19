# Variáveis de configuração
JC = javac
JVM = java
SRC_DIR = .
BIN_DIR = bin
MAIN = IDECompilador

# Coleta todos os arquivos .java do diretório
SOURCES = $(wildcard $(SRC_DIR)/*.java)

# Define o alvo padrão (compilar tudo)
all: compile run

# Cria a pasta bin se não existir e compila os arquivos
compile:
	@mkdir -p $(BIN_DIR)
	$(JC) -d $(BIN_DIR) $(SOURCES)
	@echo "Compilação concluída! Arquivos em: $(BIN_DIR)/"

# Executa o programa apontando o classpath para a pasta bin
run: compile
	$(JVM) -cp $(BIN_DIR) $(MAIN)

# Limpa os arquivos compilados
clean:
	rm -rf $(BIN_DIR)
	@echo "Diretório $(BIN_DIR) removido."