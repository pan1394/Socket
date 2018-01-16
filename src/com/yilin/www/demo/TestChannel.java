package com.yilin.www.demo;

 
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

/**
 * 一、Channel:用于源节点与目标节点之间的连接。在Java NIO中，负责缓冲区中数据传输，Channel本身不存储数据，因此需要配合缓冲区进行传输。
 *
 * 二、Channel的实现类：
 *     java.nio.channels.Channel 接口：
 *     |-- FileChannel
 *     |-- SocketChannel
 *     |-- ServerSocketChannel
 *     |-- DatagramChannel
 *
 * 三、获取通道Channel
 * 1.Java针对支持通道的类提供了getChannel()方法
 *   本地IO
 *   FileInputStream/FileOutputStream
 *   RandomAccessFile
 *
 *   网络IO:
 *   Socket
 *   ServerSocket
 *   DatagramSocket
 *
 * 2.在jdk1.7中的NIO.2针对各个通道提供了静态方法open()
 *
 * 3.在jdk1.7中的NIO.2的Files工具类的newByteChannel()
 *
 * 四、通道之间的数据传输
 * transferFrom()
 * transferTo()
 *
 * 五、分散(scatter)与聚集(gather)
 * 分散读取(scattering Reads)：将通道中的数据分散到多个缓冲区中
 * 聚集写入(gathering Writes)：将多个缓冲区的数据聚集到通道中
 *
 * 六、字符集Charset
 * 编码：字符串->字节数组
 * 解码：字节数组 -> 字符串
 *
 */
public class TestChannel {

    public static void main(String[] args) throws IOException {

        /*
         * 1.利用通道完成文件的复制（非直接缓冲区）
         */
        FileInputStream fis = null;
        FileOutputStream fos = null;

        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            fis = new FileInputStream("1.jpg");
            fos = new FileOutputStream("2.jpg");
            //1.获取通道
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            //2.分配指定大小的缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            //3.将通道中的数据缓冲区中
            while (inChannel.read(buffer) != -1) {

                buffer.flip();//切换成都数据模式

                //4.将缓冲区中的数据写入通道中
                outChannel.write(buffer);
                buffer.clear();//清空缓冲区
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        /*
         * 2.利用（直接缓冲区）通道完成文件的复制(内存映射文件的方式)
         */

        long start = System.currentTimeMillis();
        FileChannel inChannel2 = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel2 = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

        //内存映射文件
        MappedByteBuffer inMappedBuf = inChannel2.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedBuf = outChannel2.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        //直接对缓冲区进行数据读写操作
        byte[] dst = new byte[inMappedBuf.limit()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);

        inChannel2.close();
        outChannel2.close();

        long end = System.currentTimeMillis();
        System.out.println("耗费的时间为：" + (end - start));

        /*
         * 通道之间的数据传输（直接缓冲区）
         */
        FileChannel inChannel3 = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel3 = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

        inChannel3.transferTo(0, inChannel3.size(), outChannel3);
        //等价于
//        outChannel3.transferFrom(inChannel3, 0, inChannel3.size());

        inChannel3.close();
        outChannel3.close();

        /*
         * 分散和聚集
         */
        RandomAccessFile randomAccessFile1 = new RandomAccessFile("1.txt", "rw");

        //1.获取通道
        FileChannel fileChannel1 = randomAccessFile1.getChannel();

        //2.分配指定大小的缓冲区
        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer buf2 = ByteBuffer.allocate(2014);

        //3.分散读取
        ByteBuffer[] bufs = {buf1, buf2};
        fileChannel1.read(bufs);

        for (ByteBuffer byteBuffer : bufs) {
            byteBuffer.flip();
        }
        System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
        System.out.println("----------------------------");
        System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));

        //4.聚集写入
        RandomAccessFile randomAccessFile2 = new RandomAccessFile("2.txt", "rw");
        FileChannel fileChannel2 = randomAccessFile2.getChannel();
        fileChannel2.write(bufs);


        /*
         * 字符集
         */
        Map<String, Charset> map = Charset.availableCharsets();

        Set<Map.Entry<String, Charset>> set = map.entrySet();

        for (Map.Entry<String, Charset> entry : set) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }



        Charset cs1 = Charset.forName("GBK");
        //获取编码器和解码器
        CharsetEncoder ce = cs1.newEncoder();

        //获取解码器
        CharsetDecoder cd = cs1.newDecoder();

        CharBuffer cBuf = CharBuffer.allocate(1024);
        cBuf.put("hello world!");
        cBuf.flip();

        //编码
        ByteBuffer bBuf = ce.encode(cBuf);

        for (int i = 0; i < 12; i++) {
            System.out.println(bBuf.get());
        }

        //解码
        bBuf.flip();
        CharBuffer cBuf2 = cd.decode(bBuf);
        System.out.println(cBuf2.toString());

        System.out.println("----------------------------");
        Charset cs2 = Charset.forName("UTF-8"); //"GBK"
        bBuf.flip();
        CharBuffer cBuf3 = cs2.decode(bBuf);
        System.out.println(cBuf3);
    }
}