TARGET := VirtualMachine.class
CLASSES := Type.class Operation.class Scanner.class Parser.class $(TARGET)
SRC := $(addprefix taschenrechner/,$(addsuffix .java,$(basename $(CLASSES))))
CLASSTARGET := taschenrechner/$(basename $(TARGET))
DIST := dist
LIB := lib/*

all: $(DIST) $(DIST)/taschenrechner/$(TARGET)

$(SRC): $(DIST)

$(DIST)/taschenrechner/$(TARGET): $(SRC)
	@echo "COMPILE  $^"
	javac -cp $(LIB) -d $(DIST)/ $^

$(DIST):
	@mkdir -p $(DIST)/

exec: $(DIST)/taschenrechner/$(TARGET)
	java -cp $(LIB):$(DIST) $(CLASSTARGET) -d

tst: all
	@make -C tst/
	for i in `ls tst/*.tr`; do echo $$i:; java -cp $(LIB):$(DIST) $(CLASSTARGET) -f $$i; done

.PHONY: clean tst exec
clean:
	rm -Rf dist/
	make -C tst/ clean
