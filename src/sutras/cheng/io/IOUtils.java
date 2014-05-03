package sutras.cheng.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * 用于处理直接与流相关的数据
 * 
 * @author chengkai
 * 
 */
public class IOUtils {
	// 电子书后缀
	public static final String TXTEND = ".txt";
	// pdf格式后缀
	public static final String PDFEND = ".pdf";
	// doc格式后缀
	public static final String DOCEND = ".doc";
	// xml格式后缀
	public static final String XMLEND = ".xml";

	private static final String CHARSET = "utf-8"; // 设置编码
	private static final int TIME_OUT = 10 * 1000; // 超时时间
	private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
	private static final String BOUNDARY = UUID.randomUUID().toString();

	public static void openFile(Context context, File file) {
		try {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// 设置intent的Action属性
			intent.setAction(Intent.ACTION_VIEW);
			// 获取文件file的MIME类型
			String type = getMIMEType(file);
			// 设置intent的data和Type属性。
			intent.setDataAndType(/* uri */Uri.fromFile(file), type);
			// 跳转
			context.startActivity(intent);
			// Intent.createChooser(intent, "请选择对应的软件打开该附件！");
		} catch (ActivityNotFoundException e) {
			Toast.makeText(context, "sorry附件不能打开，请下载相关软件！", 500).show();
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param file
	 * @param serverUrl
	 * @return
	 */
	public static String uploadFile(File file, String serverUrl) {
		HttpURLConnection conn = getConnect(serverUrl);
		String result = null;
		// 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";

		try {
			// conn.connect();//建立连接
			if (file != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				// 采用数据流包装文件上传
				DataOutputStream dos = new DataOutputStream(
						conn.getOutputStream());
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */

				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int res = conn.getResponseCode();
				System.out.println("响应码--->" + res);
				if (res == 200) {
					InputStream input = conn.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(input));
					StringBuilder sb1 = new StringBuilder();
					String s = "";
					while ((s = reader.readLine()) != null) {
						sb1.append(s);
					}
					result = sb1.toString();
					System.out.println(result);
				} else {
					System.out.println("上传文件失败！");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将指定路径的文件解析为字符串
	 * 
	 * @param path
	 * @param charset
	 * @return
	 */
	public static String parseFile(String path, String charset) {
		String info = "";
		try {
			InputStream input = new FileInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input, "utf-8"));
			String temp = "";
			while ((temp = reader.readLine()) != null) {
				info += temp;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return info;
	}

	/**
	 * @param link
	 *            上传自己所在的地理位置
	 * @return 返回响应的内容
	 */
	public static String uploadLoc(String link) {
		String result = null;
		try {
			HttpURLConnection conn = getConnect(link);
			conn.connect();// 建立连接
			// 返回响应码
			int res = conn.getResponseCode();
			System.out.println("返回响应参数" + res);
			if (res == 200) {
				InputStream input = conn.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input));
				StringBuilder sb = new StringBuilder();
				String s = "";
				while ((s = reader.readLine()) != null) {
					sb.append(s);
				}
				result = sb.toString();
				System.out.println("响应结果-->" + result);
			} else {
				System.out.println("没有响应！");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 根据link返回HttpURLConnection连接实例
	 * 
	 * @param link
	 * @return
	 */
	public static HttpURLConnection getConnect(String link) {
		// 打开链接
		HttpURLConnection conn = null;
		try {
			// 创建URL
			URL url = new URL(link);
			conn = (HttpURLConnection) url.openConnection();
			// 设置读取时间
			conn.setReadTimeout(TIME_OUT);
			// 设置连接超时时间
			conn.setConnectTimeout(TIME_OUT);
			// 允许输入流
			conn.setDoInput(true);
			// 允许输出流
			conn.setDoOutput(true);
			// 不允许使用缓存
			conn.setUseCaches(false);
			// 请求方式---post请求
			conn.setRequestMethod("POST");
			// 设置字符编码--相关参数设置
			conn.setRequestProperty("Charset", CHARSET);
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 获取链接的输入流
	 * 
	 * @param str
	 * @return
	 */
	public static InputStream getInputStream(String url) {
		HttpClient client = new DefaultHttpClient();
		try {
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return entity.getContent();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取目录的全部文件
	 * 
	 * @param dir
	 * @return
	 */
	public static List<File> listFile(File dir) {
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile();
			}
		});
		return new ArrayList<File>(Arrays.asList(files));
	}

	/**
	 * 获取目录的全部文件, 指定扩展名的文件
	 * 
	 * @param dir
	 * @return
	 */
	public static List<File> listFile(File dir, final String ext) {

		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(ext);
			}
		});
		return new ArrayList<File>(Arrays.asList(files));
	}

	/**
	 * 递归获取目录的全部文件
	 * 
	 * @param dir
	 * @return
	 */
	public static List<File> listAll(File dir) {
		List<File> all = listFile(dir);
		File[] subs = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		for (File sub : subs) {
			all.addAll(listAll(sub));
		}
		return all;
	}

	/**
	 * 递归获取目录的全部文件, 指定扩展名的文件
	 * 
	 * @param dir
	 * @return
	 */
	public static List<File> listAll(File dir, String ext) {
		List<File> all = listFile(dir, ext);
		File[] subs = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		for (File sub : subs) {
			all.addAll(listAll(sub, ext));
		}
		return all;
	}

	/**
	 * 复制文件
	 */
	public static void cp(String from, String to) throws IOException {
		cp(new File(from), new File(to));
	}

	/**
	 * 复制文件
	 */
	public static void cp(File from, File to) throws IOException {
		FileInputStream in = new FileInputStream(from);
		OutputStream out = new FileOutputStream(to);
		cp(in, out);
		in.close();
		out.close();
	}

	/**
	 * 复制文件
	 */
	public static void cp(InputStream in, OutputStream out) throws IOException {
		// 1K byte 的缓冲区!
		byte[] buf = new byte[1024];
		int count;
		while ((count = in.read(buf)) != -1) {
			// System.out.println(count);
			out.write(buf, 0, count);
		}
		in.close();
		out.close();
	}

	public static String readLine(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b;
		while (true) {
			b = in.read();
			if (b == '\n' || b == '\r' || b == -1) {// 编码是否是回车换行
				break;
			}
			out.write(b);
		}
		return new String(out.toByteArray());
	}

	/**
	 * 从流中读取一行文本, 读取到一行的结束为止
	 * 
	 * @param in
	 * @return 一行文本
	 */
	public static String readLine(InputStream in, String charset)
			throws IOException {
		byte[] buf = new byte[0];
		int b = 0;
		while (true) {
			b = in.read();
			if (b == '\n' || b == '\r' || b == -1) {// 编码是否是回车换行
				break;
			}
			buf = Arrays.copyOf(buf, buf.length + 1);
			buf[buf.length - 1] = (byte) b;
		}
		if (buf.length == 0 && b == -1) {
			return null;
		}
		return new String(buf, charset);
	}

	/**
	 * 读取文件的全部内容到一个byte数组 可以缓存一个"小"文件到堆内存中
	 */
	public static byte[] read(String filename) throws IOException {
		return read(new File(filename));
	}

	/**
	 * 读取文件的全部内容到一个byte数组 可以缓存一个"小"文件到堆内存中
	 */
	public static byte[] read(File file) throws IOException {
		// 用RAF打开文件
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		// 安装文件的长度开辟 缓冲区数组(byte数组)
		byte[] buf = new byte[(int) raf.length()];
		// 读取文件的缓冲区
		raf.read(buf);
		// 关闭文件(RAF)
		raf.close();
		// 返回缓冲区数组引用.
		return buf;
	}

	/**
	 * 读取文件的全部内容到一个byte数组 可以缓存一个"小"文件到堆内存中 如: 文件内容: ABC中 读取为: {41, 42, 43, d6,
	 * d0}
	 */
	public static byte[] read(InputStream in) throws IOException {
		byte[] ary = new byte[in.available()];
		in.read(ary);
		in.close();
		return ary;
	}

	/**
	 * 连接byte 数组的全部内容为字符串, 以hex(十六进制)形式连接 如: 数组{0x41, 0x42, 0x43, 0xd6, 0xd0}
	 * 结果: "[41, 42, 43, d6, d0]"
	 */
	public static String join(byte[] ary) {
		if (ary == null || ary.length == 0)
			return "[]";
		StringBuilder buf = new StringBuilder();
		buf.append("[").append(
				leftPad(Integer.toHexString(ary[0] & 0xff), '0', 2));
		for (int i = 1; i < ary.length; i++) {
			String hex = Integer.toHexString(ary[i] & 0xff);
			buf.append(",").append(leftPad(hex, '0', 2));
		}
		buf.append("]");
		return buf.toString();
	}

	public static String toBinString(byte[] ary) {
		if (ary == null || ary.length == 0)
			return "[]";
		StringBuilder buf = new StringBuilder();
		buf.append("[").append(
				leftPad(Integer.toBinaryString(ary[0] & 0xff), '0', 8));
		for (int i = 1; i < ary.length; i++) {
			String hex = Integer.toBinaryString(ary[i] & 0xff);
			buf.append(",").append(leftPad(hex, '0', 8));
		}
		buf.append("]");
		return buf.toString();
	}

	/**
	 * 实现leftPad功能, 对字符串实现左填充
	 * 
	 * @param str
	 *            被填充字符串: 5
	 * @param ch
	 *            填充字符: #
	 * @param length
	 *            填充以后的长度: 8
	 * @return "#######5"
	 */
	public static String leftPad(String str, char ch, int length) {
		if (str.length() == length) {
			return str;
		}
		char[] chs = new char[length];
		Arrays.fill(chs, ch);
		System.arraycopy(str.toCharArray(), 0, chs, length - str.length(),
				str.length());
		return new String(chs);
	}

	/**
	 * 将text追加到文件 filename的尾部 使用系统默认文本编码
	 */
	public static void println(String filename, String text) throws IOException {
		println(new File(filename), text);
	}

	public static void println(File file, String text) throws IOException {
		OutputStream out = new FileOutputStream(file, true);
		println(out, text);
		out.close();
	}

	/**
	 * 向流中输出一行字符串, 使用默认编码 不关闭流
	 * 
	 * @param out
	 *            目标流
	 * @param text
	 *            文本
	 * @throws IOException
	 */
	public static void println(OutputStream out, String text)
			throws IOException {
		out.write(text.getBytes());
		out.write('\n');
		out.flush();
	}

	/**
	 * 向流中输出一行字符串, 使用指定的编码 不关闭流
	 * 
	 * @param out
	 *            目标流
	 * @param text
	 *            文本
	 * @param charset
	 *            指定的编码
	 * @throws IOException
	 */
	public static void println(OutputStream out, String text, String charset)
			throws IOException {
		out.write(text.getBytes(charset));
		out.write('\n');
	}

	/**
	 * 切分文件, 如: file.dat 切分为 file.dat.0, file.dat.1 ...
	 * 
	 * @param file
	 * @param size
	 *            大小, 以KByte为单位
	 */
	public static void split(String file, int size) throws IOException {
		if (size <= 0) {
			throw new IllegalArgumentException("搞啥呀!");
		}
		int idx = 0;// 文件的序号
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file
				+ "." + idx++));
		int b;
		int count = 0;
		while ((b = in.read()) != -1) {
			out.write(b);
			count++;
			if (count % (size * 1024) == 0) {
				out.close();
				out = new BufferedOutputStream(new FileOutputStream(file + "."
						+ idx++));
			}
		}
		in.close();
		out.close();
	}

	/**
	 * 将文件进行连接
	 * 
	 * @param filename
	 *            是第一个文件名,如:file.dat.0
	 */
	public static void join(String file) throws IOException {
		String filename = file.substring(0, file.lastIndexOf("."));
		String num = file.substring(file.lastIndexOf(".") + 1);
		int idx = Integer.parseInt(num);
		OutputStream out = new FileOutputStream(filename);
		File f = new File(filename + "." + idx++);
		while (f.exists()) {
			InputStream in = new FileInputStream(f);
			cp(in, out);
			in.close();
			f = new File(filename + "." + idx++);
		}
		out.close();
	}

	/**
	 * 序列化对象
	 */
	public static byte[] Serialize(Serializable obj) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(os);

		out.writeObject(obj);// 对象序列化, foo
		out.close();
		byte[] ary = os.toByteArray();
		return ary;
	}

	public static Object Unserialize(byte[] data) throws IOException,
			ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
				data));
		Object o = in.readObject();// 反序列化
		in.close();
		return o;
	}

	/**
	 * 对象的深层赋值(克隆)
	 * 
	 * @param obj
	 * @return 对象的副本
	 * @throws ClassNotFoundException
	 */
	public static Object clone(Serializable obj) throws IOException,
			ClassNotFoundException {
		return Unserialize(Serialize(obj));
	}

	/**
	 * 读取流中到字符数组
	 * 
	 * @param in
	 * @return
	 */
	public static char[] readChar(Reader in) throws IOException {
		StringBuilder buf = new StringBuilder();
		int c;
		while ((c = in.read()) != -1) {
			buf.append((char) c);
		}
		in.close();
		return buf.toString().toCharArray();
	}

	public static char[] readChar(String filename, String encoding)
			throws IOException {
		return readChar(new File(filename), encoding);
	}

	public static char[] readChar(File file, String encoding)
			throws IOException {
		return readChar(new FileInputStream(file), encoding);
	}

	public static char[] readChar(InputStream in, String encoding)
			throws IOException {
		return readChar(new InputStreamReader(in, encoding));
	}

	private static String getMIMEType(File file) {

		String type = "*/*";
		String fName = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MapTable.length; i++) {

			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	// 可以自己随意添加
	private static String[][] MIME_MapTable = {
			// {后缀名，MIME类型}
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.Android.package-archive" },
			{ ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" },
			{ ".c", "text/plain" },
			{ ".class", "application/octet-stream" },
			{ ".conf", "text/plain" },
			{ ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{
					".docx",
					"application/vnd.openxmlformats-officedocument"
							+ ".wordprocessingml.document" },
			{ ".xls", "application/vnd.ms-excel" },
			{
					".xlsx",
					"application/vnd.openxmlformats-officedocument"
							+ "spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" },
			{ ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" },
			{ ".gz", "application/x-gzip" },
			{ ".h", "text/plain" },
			{ ".htm", "text/html" },
			{ ".html", "text/html" },
			{ ".jar", "application/java-archive" },
			{ ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" },
			{ ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" },
			{ ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" },
			{ ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" },
			{ ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" },
			{ ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" },
			{ ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" },
			{ ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{
					".pptx",
					"application/vnd.openxmlformats-officedocument"
							+ ".presentationml.presentation" },
			{ ".prop", "text/plain" }, { ".rc", "text/plain" },
			{ ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" },
			{ ".sh", "text/plain" }, { ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
			{ ".z", "application/x-compress" },
			{ ".zip", "application/x-zip-compressed" }, { "", "*/*" } };
}
