SHELL := bash

DIST := dist
DOCS := docs
LIB := lib/*
PKG := taschenrechner

TARGET := VirtualMachine.class
RTARGET := $(DIST)/$(PKG)/$(TARGET)
CLASSTARGET := $(PKG).$(basename $(TARGET))
CLASSES := Type.class Operation.class Scanner.class Parser.class $(TARGET)
SRC := $(addprefix $(PKG)/,$(addsuffix .java,$(basename $(CLASSES))))

TESTTARGET := AllTests.class
TESTRTARGET := $(DIST)/$(PKG)/$(TESTTARGET)
TESTCLASSTARGET := $(PKG).$(basename $(TESTTARGET))
TESTCLASSES := AdditionTest.class $(TESTTARGET)
TESTSRC := $(addprefix test/$(PKG)/,$(addsuffix .java,$(basename $(TESTCLASSES))))

all: $(RTARGET) $(DOCS)

$(RTARGET): $(DIST) $(SRC) $(TESTSRC)
	@echo "COMPILE  $(basename $(notdir $(SRC)))"
	@echo "COMPILE  $(basename $(notdir $(TESTSRC)))"
	@javac -cp '$(LIB)' -d $(DIST)/ $(SRC) $(TESTSRC)

$(DIST):
	@mkdir -p $@/

exec: $(RTARGET)
	java -cacao -cp $(LIB):$(DIST) $(CLASSTARGET) -d

test: $(RTARGET)
	java -cacao -cp $(LIB):$(DIST) junit.textui.TestRunner $(TESTCLASSTARGET)

$(DOCS): $(SRC)
	@echo "GEN      JAVADOC"
	@mkdir -p $@/
	@javadoc taschenrechner -private -sourcepath taschenrechner \
		-d $@ $(SRC)
	
progs: all
	@make -C $@/
	for i in `ls $@/*.tr`; do echo $$i:; java -cacao -cp $(LIB):$(DIST) \
		$(CLASSTARGET) -f $$i; done

.PHONY: clean progs exec test
clean:
	rm -Rf dist/ docs/
	make -C progs/ clean
