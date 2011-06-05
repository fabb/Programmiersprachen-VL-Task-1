" diese datei nach ~/.vim/syntax/trp.vim kopieren und
" folgendes in die ~/.vimrc schmeissen:
" > syntax on
" > filetype on
" > augroup filetypedetect
" > 	au BufNewFile,BufRead *.trp setf trp
" > augroup END
"
" have fun :)

if version < 600
	syntax clear
elseif exists("b:current_syntax")
	finish
endif

syn case match
syn sync lines=250

syn match Statement /c/
syn match Statement /d/
syn match Statement /=/
syn match Statement /-/
syn match Statement /+/
syn match Statement "a"
syn match Statement "<"
syn match Statement ">"
syn match Statement "+"
syn match Statement "*"
syn match Statement "%"
syn match Statement "\~"
syn match Statement "&"
syn match Statement "|"

syn match Special "\["
syn match Special "\]"
syn match Type "\\"

syn match Identifier "\u\+"
syn match Statement "[0-9]\+"

let b:current_syntax = "trp"

" from /usr/share/vim/vim7?/syntax/c.vim
syn region	cCppOut		start="^\s*\(%:\|#\)\s*if\s\+0\+\>" end=".\@=\|$" contains=cCppOut2
syn region	cCppOut2	contained start="0" end="^\s*\(%:\|#\)\s*\(endif\>\|else\>\|elif\>\)" contains=cSpaceError,cCppSkip
syn region	cCppSkip	contained start="^\s*\(%:\|#\)\s*\(if\>\|ifdef\>\|ifndef\>\)" skip="\\$" end="^\s*\(%:\|#\)\s*endif\>" contains=cSpaceError,cCppSkip

syn region	cIncluded	display contained start=+"+ skip=+\\\\\|\\"+ end=+"+
syn match	cIncluded	display contained "<[^>]*>"
syn match	cInclude	display "^\s*\(%:\|#\)\s*include\>\s*["<]" contains=cIncluded
"syn region	cDefine		start="^\s*\(%:\|#\)\s*\(define\|undef\)\>" skip="\\$" end="$" keepend
syn match cDefine "#define"
syn region	cCommentL	start="//" skip="\\$" end="$" keepend
syn region	cComment	matchgroup=cCommentStart start="/\*" end="\*/"
syntax match	cCommentError	display "\*/"
syntax match	cCommentStartError display "/\*"me=e-1 contained

hi def link cCommentStart cComment
hi def link cComment Comment
hi def link cCommentL Comment
hi def link cIncluded String
hi def link cCppSkip cCppOut
hi def link cCppOut2 cCppOut
hi def link cCppOut	Comment
hi def link cInclude	Include
hi def link cDefine Macro
hi def link cSpaceError		cError
