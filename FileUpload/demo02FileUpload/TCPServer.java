package demo02FileUpload;
/*
文件上传案例服务器端
3. 【服务端】输入流，读取文件数据到服务端程序。
4. 【服务端】输出流，写出文件数据到服务器硬盘中。

数据源：客户端上传的文件
目的地：服务器硬盘

实现步骤：
1.创建服务器对象
2.获取请求的客户端对象 accept()
3.用客户端对象获取网络字节输入流对象
4.创建本地字节输出流对象（需要先判断文件夹是否存在）
5.使用网络字节输入流读取客户端上传文件 read()
6.使用本地字节输流对象读取文件
7.获取网络字节输出流对象
8.给客户端回写数据 write()
10.释放资源
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String[] args) throws IOException {
        //1.创建服务器对象
        ServerSocket server = new ServerSocket(8888);

        //2.获取请求的客户端对象 accept()
        Socket socket = server.accept();

        //3.用客户端对象获取网络字节输入流对象
        InputStream is = socket.getInputStream();

        //4.创建本地字节输出流对象（需要先判断文件夹是否存在）
        File file = new File("upload");
        if(!file.exists() || !file.isDirectory()){
            file.mkdir();
        }

        FileOutputStream fos = new FileOutputStream(file+"\\1.jpg");

        //5.使用网络字节输入流读取客户端上传文件 read()
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = is.read(bytes)) != -1 ){
            //6.使用本地字节输流对象读取文件
            fos.write(bytes,0,len);
        }

        //7.获取网络字节输出流对象
        //8.给客户端回写数据 write()
        socket.getOutputStream().write("上传成功".getBytes());

        //10.释放资源
        fos.close();
        socket.close();
        server.close();
    }
}
