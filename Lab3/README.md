没有完全按照实验来写，而且十分不完善，预计会存在许多bug。

```
// 样例输入
int a(){
    return 1;
}

int a(){
    return 0;
}

int main(){
    b = c();
    int test1;
    int test1;
    int array[2] = {0,0};
    test1 = array;
    test1 = a + array;
    test1 = a(1);
    test1 = test1[0];
    test1();
    a = 1;
    return a;
}

// 我的输出
Error type 4 at Line 5: Redefined function: a
Error type 1 at Line 10: Undefined variable: b
Error type 2 at Line 10: Undefined function: c
Error type 3 at Line 12: Redefined variable: test1
Error type 6 at Line 15: type.Type mismatched for operands
Error type 6 at Line 15: type.Type mismatched for operands
Error type 8 at Line 16: Function is not applicable for arguments.
Error type 9 at Line 17: Not an array: test1
Error type 10 at Line 18: Not an function: test1
Error type 11 at Line 19: The left-hand side of an assignment must be a variable.
Error type 7 at Line 20: type.Type mismatched for return.
```

这里我不是很明白实验帮助中让我自己设计一个Type抽象类之后的操作，于是写了一个比较垃圾的实现。