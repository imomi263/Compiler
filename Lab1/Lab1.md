### Part1 词法分析

- 本次实验你需要完成一个词法分析器对使用SysY语言书写的源代码进行词法分析，要求如下
  - 本次实验要求通过**标准错误输出（stderr, 如System.err 等）**， 打印程序的 **所有** 运行结果。
  - 当**包含词法错误时**：对于包含词法错误的文件，你需要打印**所有**错误信息，格式为：`Error type A at Line [lineNo]:[errorMessage]`，其中`lineNo`为出错的`token`首个字符所在行的行号，`errorMessage`可自行定义，本实验不做要求，只要冒号前的信息正确即可。
  - 当**不包含词法错误时**：对于没有任何词法错误的文件，你需要打印所有识别到的`Tokens`信息，具体输出格式可以参见**样例一**。特别要求：输出时忽略所有注释，对十六进制和八进制数字常量输出`token`文本时需输出其十进制的值



##### 词法结构

```
CONST -> 'const';
INT -> 'int';
VOID -> 'void';
IF -> 'if';
ELSE -> 'else';
WHILE -> 'while';
BREAK -> 'break';
CONTINUE -> 'continue';
RETURN -> 'return';
PLUS -> '+';
MINUS -> '-';
MUL -> '*';
DIV -> '/';
MOD -> '%';
ASSIGN -> '=';
EQ -> '==';
NEQ -> '!=';
LT -> '<';
GT -> '>';
LE -> '<=';
GE -> '>=';
NOT -> '!';
AND -> '&&';
OR -> '||';
L_PAREN -> '(';
R_PAREN -> ')';
L_BRACE -> '{';
R_BRACE -> '}';
L_BRACKT -> '[';
R_BRACKT -> ']';
COMMA -> ',';
SEMICOLON -> ';';
IDENT : 以下划线或字母开头，仅包含下划线、英文字母大小写、阿拉伯数字
   ;
INTEGER_CONST : 数字常量，包含十进制数，0开头的八进制数，0x或0X开头的十六进制数
   ;
WS
   -> [ \r\n\t]+
   ;
LINE_COMMENT
   -> '//' .*? '\n'
   ;
MULTILINE_COMMENT
   -> '/*' .*? '*/'
   ;
```

