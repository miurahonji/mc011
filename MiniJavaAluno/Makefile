all: grammar prog

grammar:
	cd ../ && ./sablecc MiniJavaAluno/grammar.sablecc

prog:
	javac main/Main.java && java main/Main ../Minijava/testes/small/1/01
