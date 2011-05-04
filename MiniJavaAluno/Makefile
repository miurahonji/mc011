all: grammar prog

grammar:
	cd ../ && ./sablecc MiniJavaAluno/grammar.sablecc

prog:
	cat ../Minijava/testes/small/1/01
	javac main/Main.java && java main/Main ../Minijava/testes/small/1/01
