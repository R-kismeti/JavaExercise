package demo03FileUpload;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/*
文件上传案例客户端
1. 【客户端】输入流，从硬盘读取文件数据到程序中。
2. 【客户端】输出流，写出文件数据到服务端。

数据源：E:\baiduDisk\02-Java语言进阶\day11_网络编程\img\1_cs.jpg
目的地：服务器

实现步骤
1.创建本地字节输入流对象
2.创建客户端对象
3.用客户端对象获取网络字节输出流对象
4.使用本地字节输入流对象读取文件
5.将读取到的文件上传 write()
6.用客户端对象获取网络字节输入对象
7.读取服务器回写的数据 read()
8.释放资源
 */
public class TCPClient {
    public static void main(String[] args) throws IOException {
        //1.创建本地字节输入流对象
        FileInputStream fis = new FileInputStream("1.jpg");

        //2.创建客户端对象
        Socket socket = new Socket("127.0.0.1",8888);

        //3.用客户端对象获取网络字节输出流对象
        OutputStream os = socket.getOutputStream();

        //4.使用本地字节输入流对象读取文件
        //5.将读取到的文件上传 write()
        byte[] bytes = new byte[1024];
        int len = 0;
        while((len = fis.read(bytes)) != -1){
            os.write(bytes,0,len);
        }

        /*此处，因为文件的结束标记并没有写入到输入流中
          因此，服务器端的读取文件内容会进入到阻塞状态，无法继续执行
        */
        //关闭输出流,通知服务端,写出数据完毕
        socket.shutdownOutput();
        
        //6.用客户端对象获取网络字节输入对象
        InputStream is = socket.getInputStream();

        //7.读取服务器回写的数据 rea`d()
        while((len = is.read(bytes)) != -1){
            System.out.println(new String(bytes,0,len));
        }

        //8.释放资源
        fis.close();
        socket.close();
    }
}
