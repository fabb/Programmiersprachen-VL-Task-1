SHELL := bash

all: prime.tr ifbsp.tr fac.tr

# .tr : inputformat according to the assignment (generated)
# .trp: input with preprocessor statements
%.tr: %.trp
	@echo "  PRE    $<"
	@#just remove comments (required as workaround)
	@gcc -x c -fpreprocessed -dD -E $< > $@.tmp
	@#do the actual preprocessing here
	@#sed stuff: delete empty lines. $$ -> $ (escape sequence for make)
	@gcc -x c -E -P $@.tmp | sed '/^$$/d' > $@
	@rm $@.tmp

.PHONY: clean buildtools tst
clean:
	rm -rf *.tr

buildtools:
	make -C ../ all

tst:
	make -C ../ tst
