JC = javac
JFLAGS = -g
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java
CLASSES =\
	NaiveBayesClassifier.java\
	NaiveBayesTrainer.java
default:classes
classes: $(CLASSES:.java=.class)
clean:
	rm -f  *.class