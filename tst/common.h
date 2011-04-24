/* IF Then Else
 * 1. arg: else
 * 2. arg: then
 * 3. arg: expr */
#define IFTE [4c 5d 2+ d a]a

/* IF Else Then //cf. forth
 * TODO
 */
#define IFET [4c 5d 2+ d a]

/* swap the two upper elements */
#define SWP 3c 4d

/* duplicate top of stack */
#define DUP 2c

/* calcuate absolute value */
/* pre: input */
#define ABS DUP \
	0 < \
	[0 1 - * ] \
	[] \
	IFTE
/* post: |input| */
