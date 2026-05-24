grammar Formula;
/*
	parser rules
*/

formula
: '(' f=formula ')'		#parens
| NEG f=formula			#negation
| l=formula CONJ r=formula	#conjunction
| l=formula DISJ r=formula	#disjunction
| a=ATOM					#atom
;

/*
	Lexer rules
*/

fragment LOWERCASE		: [a-z] ;
fragment UPPERCASE		: [A-Z] ;

ATOM					: (LOWERCASE | UPPERCASE)+ ;
CONJ                    : '&' ;
DISJ                    : '|' ;
NEG                     : '~' ;
WS         				: [ \r\t\u000C\n]+ -> skip;