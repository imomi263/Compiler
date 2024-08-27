grammar SysY;

program : compUnit;


compUnit : (decl | funcDef)+EOF;
decl : constDecl | varDecl;
constDecl : 'const' bType constDef (',' constDef)* ';';
bType : 'int';
constDef : IDENT ('[' constExp ']')* '=' constInitVal;
constInitVal : constExp
            | '{'( constInitVal  (',' constInitVal)*)?'}';

varDecl : bType varDef (',' varDef)* ';';
varDef : IDENT ('['constExp']')*
        | IDENT ('['constExp']')* '=' initVal;
initVal : exp
        | '{' (initVal  (',' initVal)*)? '}';
funcDef : funcType IDENT '(' (funcFParams)? ')' block;
funcType : 'void' | 'int';
funcFParams : funcFParam (',' funcFParam)*;
funcFParam : bType IDENT ( '['  ']'  ('[' exp']')* )?;
block : '{' (blockItem)* '}';
blockItem : decl
          | stmt;

stmt: lVal '=' exp ';'                          #stmt_assign
            | exp? ';'                          #stmt2
            | block                             #stmtblock
            |'if' '(' cond ')' stmt             #stmt_if
            | 'if' '(' cond ')' stmt 'else' stmt#stmt_if_else
            | 'while' '(' cond ')' stmt         #stmt_while
            | 'break' ';'                       #stmt_break
            | 'continue' ';'                    #stmt_continue
            | 'return' (exp)? ';'               #stmt_return
            ;

exp
   : L_PAREN exp R_PAREN                  #exp1
   | lVal                                 #exp2
   | number                               #exp3
   | IDENT L_PAREN (funcRParams)? R_PAREN #exp4
   | unaryOp exp                          #exp5
   | exp (MUL | DIV | MOD) exp            #exp6
   | exp (PLUS | MINUS) exp               #exp7
   ;

cond
   : exp
   | cond (LT | GT | LE | GE) cond
   | cond (EQ | NEQ) cond
   | cond AND cond
   | cond OR cond
   ;

lVal
   : IDENT (L_BRACKT exp R_BRACKT)*
   ;

number
   : INTCONST
   ;

unaryOp
   : PLUS
   | MINUS
   | NOT
   ;

funcRParams
   : param (COMMA param)*
   ;

param
   : exp
   ;

constExp
   : exp
   ;





CONST : 'const';
INT : 'int';
VOID : 'void';
IF : 'if';
ELSE : 'else';
WHILE : 'while';
BREAK : 'break';
CONTINUE : 'continue';
RETURN: 'return';
PLUS : '+';
MINUS: '-';
MUL: '*';
DIV: '/';
MOD:'%';
ASSIGN : '=';
EQ :'==';
NEQ :'!=';
LT :'<';
GT :'>';
LE : '<=';
GE : '>=';
NOT : '!';
AND :'&&';
OR : '||';
L_PAREN:'(';
R_PAREN:')';
L_BRACE:'{';
R_BRACE:'}';
L_BRACKT:'[';
R_BRACKT:']';
COMMA:',';
SEMICOLON:';';

IDENT : ('_' | [a-zA-Z])('_'|[0-9a-zA-Z])*;
WS : [ \r\n\t]+ ->skip;
LINE_COMMENT : '//' .*? '\n' ->skip;
MULTILINE_COMMENT: '/*' .*? '*/' ->skip;
INTCONST : ('0' | [1-9][0-9]*) | ('0'[1-9][0-9]*) | (('0x'|'0X')[1-9a-eA-E][0-9a-eA-E]*);






















