smash:
	../jflex-1.9.1/bin/jflex SmashFlex.jflex
	java -jar /usr/share/java/cup/java-cup-11b.jar -locations -interface SmashParse.cup
	javac -cp /usr/share/java/cup/java-cup-11b.jar *.java
