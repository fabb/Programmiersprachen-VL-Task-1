TARGET := TRVM.class

all: $(TARGET)

dist/%.class: %.java dist
	@echo "COMPILE  $<"
	@javac -d dist/ $<

dist:
	@mkdir -p dist/

exec: dist/$(TARGET)
	@java -cp dist $(basename $(notdir $<))

.PHONY:
clean:
	rm -Rf dist/
