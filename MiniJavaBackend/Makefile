all: grammar prog

grammar:
	cd ../ && ./sablecc MiniJavaAluno/grammar.sablecc

prog:
	cat -n ../Minijava/testes/small/$(TEST)
	javac main/Main.java && java main/Main ../Minijava/testes/small/$(TEST)

