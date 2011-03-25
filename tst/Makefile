SHELL := bash

all: prime.tr

# .tr : inputformat according to the assignment (generated)
# .trp: input with preprocessor statements
%.tr: %.trp
	@echo "  PRE    $<"
	@gcc -x c -E -P $< > $@.tmp
	@#unfortunately we need a second pass, so we can use line breaks properly
	@#sed stuff: delete empty lines. $$ -> $ (escape sequence for make)
	@gcc -x c -E -P $@.tmp | sed '/^$$/d' > $@
	@rm $@.tmp

.PHONY: clean buildtools
clean:
	rm -rf *.tr

buildtools:
	@echo " TODO! "
