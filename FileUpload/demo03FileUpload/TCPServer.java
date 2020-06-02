package demo03FileUpload;
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

优化：
1. **文件名称写死的问题**
  服务端，保存文件的名称如果写死，那么最终导致服务器硬盘，只会保留一个文件。

  处理方式:
  自定义文件命名规则，保证文件名称唯一。例如：毫秒值+随机数

String filename = System.currentTimeMillis() + new Random().nextInt(9999) + ".jpg";
FileOutputStream fos = new FileOutputStream(file+"\\" + filename);

2. **循环接收的问题**
    服务器每次接受用户一个文件就关闭，这样是不现实的。

    处理方式：
        使用循环改进，让服务器一直处于监听状态：

// 每次接收新的连接,创建一个Socket
while（true）{
    Socket accept = serverSocket.accept();
    ......
}

3. **效率问题**

   服务端，在接收大文件时，可能耗费几秒钟的时间，此时不能接收其他用户上传，所以，使用多线程技术优化，代码如下：

   while（true）{
    Socket accept = serverSocket.accept();
    // accept 交给子线程处理.
    //可选择lambda表达式
    new Thread(() -> {
      	......
        InputStream bis = accept.getInputStream();
      	......
    }).start();
}

 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TCPServer {
    public static void main(String[] args) throws IOException {
        //1.创建服务器对象
        ServerSocket server = new ServerSocket(8888);

        /*
        循环接收:让服务器一直处于监听状态（死循环）
            有一个客户端上传文件，就保存一个文件
         */
        //2.获取请求的客户端对象 accept()
        while(true){
            /*
            可使用多线程技术，提高程序的效率
                每来一个客户，就开启一个新线程
             */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Socket socket = null;
                    FileOutputStream fos = null;

                    try{
                        socket = server.accept();

                        //3.用客户端对象获取网络字节输入流对象
                        InputStream is = socket.getInputStream();

                        //4.创建本地字节输出流对象（需要先判断文件夹是否存在）
                        File file = new File("upload");
                        if(!file.exists() || !file.isDirectory()){
                            file.mkdir();
                        }

                        /*
                        自定义一个文件的命名规则：防止同名文件被覆盖掉
                        规则：毫秒值+随机数
                         */
                        String filename = System.currentTimeMillis() + new Random().nextInt(9999) + ".jpg";
                        fos = new FileOutputStream(file+"\\" + filename);

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
                    }
                    catch (IOException e){
                        System.out.println(e);
                    }
                    finally {
                        try{
                            if(socket != null) {
                                socket.close();
                            }
                            if(fos != null){
                                fos.close();
                            }
                        }
                        catch (IOException e){
                            System.out.println(e);
                        }
                    }
                }
            }).start();
        }
    }
}
