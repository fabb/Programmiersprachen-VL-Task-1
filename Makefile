TARGET := TRVM.class
CLASSTARGET := $(basename $(TARGET))
DIST := dist

all: $(DIST)/$(TARGET)

$(DIST)/%.class: %.java $(DIST)
	@echo "COMPILE  $<"
	@javac -d $(DIST)/ $<

$(DIST):
	@mkdir -p $(DIST)/

exec: $(DIST)/$(TARGET)
	java -cp $(DIST) $(CLASSTARGET)

tst: all
	@make -C tst/
	for i in `ls tst/*.tr`; do echo $$i:; java -cp $(DIST) $(CLASSTARGET) -f $$i; done

.PHONY: clean tst exec
clean:
	rm -Rf dist/
	make -C tst/ clean
