all: grammar prog

grammar:
	cd ../ && ./sablecc MiniJavaAluno/grammar.sablecc

prog:
	cat -n ../Minijava/testes/small/1/06
	javac main/Main.java && java main/Main ../Minijava/testes/small/1/06

clean:
	rm -rf "*.class"
