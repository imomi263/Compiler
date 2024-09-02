本次实验实现了翻译函数调用和控制流语句，该实现未经系统测试，预计会存在一些考虑不周的地方，目前主要是对于条件的翻译不够明确，比如说AND命令、OR命令如何翻译比较困惑，同时目前对于一个表达式真假的表示还不够明确，比如下面这种：

```c++
if(a){
    
}
```

or指令短路理论上实现了，可能还存在许多bug。同时对于LLVM的大多数指令还是不够了解，比如我不太明确如何处理嵌套if语句的中间代码生成。

```sysy
if(a){
	if(b){
	
	}
}

中间代码：
a.true
b.true
b.false
a.false
```

这里由于LLVMAppendBasicBlock会把a.false直接append到a.true的后面，同时我尝试先new一个LLVMBasicBlockRef，然后append上去，但是由于对api不够熟悉于是失败了。目前的方法是先append上a.false，然后remove下来，visit(a.stmt())之后在append a.false。

