all: grammar main

test: main run

grammar:
	cd ../ && ./sablecc MiniJavaAluno/grammar.sablecc

main:
	javac -Xlint Action/*.java
	javac RunTeste.java

run:
	./atester.sh 1
