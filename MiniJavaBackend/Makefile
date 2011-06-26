all: java prog

java:
	javac ./x86/*.java
	javac ./reg_alloc/*.java
	javac ./temp/*.java
	javac ./symbol/*.java
	javac ./flow_graph/*.java
	javac ./syntaxtree/*.java
	javac ./errors/*.java
	javac ./main/*.java
	javac ./visitor/*.java
	javac ./util/*.java
	javac ./util/conversor/*.java
	javac ./translate/*.java
	javac ./assem/*.java
	javac ./graph/*.java
	javac ./frame/*.java
	javac ./tree/*.java
	javac ./canon/*.java

prog:
	cat -n ../Minijava/testes/small/$(TEST)
	javac main/Main.java && java main/Main ../Minijava/testes/small/$(TEST)

