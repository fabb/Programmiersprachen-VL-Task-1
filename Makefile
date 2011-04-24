TARGET := VirtualMachine.class
CLASSES := Type.class Operation.class Scanner.class Parser.class $(TARGET)
SRC := $(addprefix taschenrechner/,$(addsuffix .java,$(basename $(CLASSES))))
CLASSTARGET := taschenrechner/$(basename $(TARGET))
DIST := dist
DOCS := docs/
LIB := lib/*

all: $(DIST) $(DIST)/taschenrechner/$(TARGET)

$(SRC): $(DIST) $(DOCS)

$(DIST)/taschenrechner/$(TARGET): $(SRC)
	@echo "COMPILE  $^"
	javac -cp $(LIB) -d $(DIST)/ $^

$(DIST):
	@mkdir -p $@/

exec: $(DIST)/taschenrechner/$(TARGET)
	java -cp $(LIB):$(DIST) $(CLASSTARGET) -d

$(DOCS):
	@mkdir -p $@/
	javadoc taschenrechner -private -sourcepath taschenrechner \
		-d $@ $(SRC)
	
tst: all
	@make -C tst/
	for i in `ls tst/*.tr`; do echo $$i:; java -cp $(LIB):$(DIST) $(CLASSTARGET) -f $$i; done

.PHONY: clean tst exec
clean:
	rm -Rf dist/ docs/
	make -C tst/ clean
