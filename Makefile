SHELL := bash

TARGET := VirtualMachine.class
PKG := taschenrechner
CLASSES := Type.class Operation.class Scanner.class Parser.class $(TARGET)
SRC := $(addprefix $(PKG)/,$(addsuffix .java,$(basename $(CLASSES))))
DIST := dist
DOCS := docs
LIB := lib/*
RTARGET := $(DIST)/$(PKG)/$(TARGET)
CLASSTARGET := $(PKG)/$(basename $(TARGET))

all: $(RTARGET) $(DOCS)

$(RTARGET): $(DIST) $(SRC)
	@echo "COMPILE  $(basename $(notdir $(SRC)))"
	@javac -cp $(LIB) -d $(DIST)/ $(SRC)

$(DIST):
	@mkdir -p $@/

exec: $(RTARGET)
	java -cp $(LIB):$(DIST) $(CLASSTARGET) -d

$(DOCS): $(SRC)
	@echo "GEN      JAVADOC"
	@mkdir -p $@/
	@javadoc taschenrechner -private -sourcepath taschenrechner \
		-d $@ $(SRC)
	
progs: all
	@make -C $@/
	for i in `ls $@/*.tr`; do echo $$i:; java -cp $(LIB):$(DIST) \
		$(CLASSTARGET) -f $$i; done

.PHONY: clean progs exec
clean:
	rm -Rf dist/ docs/
	make -C progs/ clean
