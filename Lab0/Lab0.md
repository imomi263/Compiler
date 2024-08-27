你需要在`src/Main.java`实现`cat`的基础功能，即读取一个**文本文件**并输出其所有内容到标准输出流。


```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main
{
    public static void main(String[] args){
        String filename=args[0];
        try(BufferedReader reader=new BufferedReader(new FileReader(filename))){
            String line;
            while((line=reader.readLine())!=null){
                System.out.println(line);
            }
        }catch(IOException e){
            System.out.println("error!\n");
        }
    }
}
```

