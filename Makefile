TARGET := VirtualMachine.class
CLASSTARGET := $(basename $(TARGET))
DIST := dist
LIB := lib/*

all: $(DIST)/$(TARGET)

$(DIST)/%.class: %.java $(DIST)
	@echo "COMPILE  $<"
	javac -cp $(LIB) -d $(DIST)/ $<

$(DIST):
	@mkdir -p $(DIST)/

exec: $(DIST)/$(TARGET)
	java -cp $(LIB):$(DIST) $(CLASSTARGET) -d

tst: all
	@make -C tst/
	for i in `ls tst/*.tr`; do echo $$i:; java -cp $(LIB):$(DIST) $(CLASSTARGET) -f $$i; done

.PHONY: clean tst exec
clean:
	rm -Rf dist/
	make -C tst/ clean
