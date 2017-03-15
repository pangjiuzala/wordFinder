package cn.zju.edu.crawlerweb;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.zju.edu.util.FileUtil;
import cn.zju.edu.util.HtmlUtil;

/*Jsoup使用方法:http://www.cnblogs.com/xiaoMzjm/p/3899366.html */

public class WordSpider {
	public static String savehtml = ".html";
	public static int count = 1;// 统计定时器执行次数

	public static void crawlerword() throws IOException {

		// 写文件前清空上一次文件
		FileUtil.clearFile();

		TimerTask task = new TimerTask() {
			int number = 0;
			List<String> result = FileUtil.readfile("word.txt");

			@Override
			public void run() {

				// 保存首页html，此处不能保存，问题定位中

				try {
					synchronized (this) {
						if (number <= result.size() - 1) {
							getNativeWord(result.get(number));
							number++;
						} else {
							cancel();
							FileUtil.resultCut();
						}
					}

				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		};
		Timer timer = new Timer();
		long delay = 0;
		// 每隔intevalPeriod爬取下一个词
		long intevalPeriod = 1 * 20000 * 20;
		System.out.println("每隔" + intevalPeriod / (1000 * 60) + "分钟爬取下一个词"
				+ "\n");
		timer.scheduleAtFixedRate(task, delay, intevalPeriod);

	}

	// 获取url中的cookie
	public static String getCookie(final String Url) {
		StringBuffer sb = new StringBuffer();
		try {
			CookieManager manager = new CookieManager();
			manager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
			CookieHandler.setDefault(manager);
			URL url = new URL(Url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.getHeaderFields();
			CookieStore store = manager.getCookieStore();
			List<HttpCookie> lCookies = store.getCookies();
			for (HttpCookie cookie : lCookies) {
				sb.append(URLDecoder.decode(cookie.getValue(), "UTF8"));
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
		return sb.toString();
	}

	// 从本地html选出标注语料

	private static synchronized void getNativeWord(final String word)
			throws IOException {
		final String encondeword = java.net.URLDecoder.decode(word, "utf-8");
		final String logpath = encondeword + ".html";
		// 首页html保存路径
		final String filefind = "./htmlfind/" + encondeword + savehtml;
		// System.out.println("每隔20s爬取一次数据" + "\n");
		// 首页Url
		final String findUrl = "http://www.cncorpus.org/CnCindex.aspx?"
				+ "__VIEWSTATE=%2FwEPDwUKMTk4MDQ0MTE5OA9kFgICAw9kFgQCKQ8PFgIeB1Zpc2libGVoZBYIAgMPDxYCHgRUZXh0BTrnrKwx5YiwMTAw5p2h77yM5YWx5p%2Bl6K%2Bi5YiwNTI3MjjmnaHnrKblkIjopoHmsYLnmoTkvovlj6UhZGQCBQ8PFgIfAGhkZAIHDw8WAh8AaGRkAg0PDxYCHwBnZGQCLw8PFgIfAGhkFgoCAQ8PFgIfAGhkZAIDDw8WAh8AaGRkAgkPDxYCHwEFATFkZAILDw8WAh8BBQM1MjhkZAINDw8WAh8BBQU1MjcyOGRkGAEFHl9fQ29udHJvbHNSZXF1aXJlUG9zdEJhY2tLZXlfXxYKBQtSQmluZGV4d29yZAUKUkJsaWtlbW9kZQUKUkJsaWtlbW9kZQUOUkJmdWxsdGV4dG1vZGUFDlJCZnVsbHRleHRtb2RlBQxSYWRpb0J1dHRvbjMFDFJhZGlvQnV0dG9uMwUMUmFkaW9CdXR0b240BQ5DaGVja0JveENodWNodQUQQ2hlY2tCb3hLV0lDbW9kZf9jlvtMb1%2FyXrpEQQLtIFyLoPLGND86N0hSq0CED%2Brk"
				+ "&__VIEWSTATEGENERATOR=3A0BE18D"
				+ "&__EVENTVALIDATION=%2FwEWDgK3wKfCCgLYiuv%2FCwLzuO7zDQL3uO7zDQLV%2BYmkCgLZ%2BYmkCgKM54rGBgK8u9naBwKJlM7DBwKAg8rcDgKWzvT1CAKWzuCuBwK2q5qHDgK%2FxfDTAXWmVvcYknI3MwjcfE48IiMijAq3WW044PF7g9pBhtfu"
				+ "&TextBoxCCkeywords="
				+ word
				+ "&DropDownListPsize=500&Button1=%E6%A3%80++%E7%B4%A2&1=RBindexword&2=RadioButton4";
		System.out
				.println("正在爬取" + "词:[" + encondeword + "]" + "首页数据⋯⋯" + "\n");
		// 保存首页html
		HtmlUtil.urlToHtm(encondeword, findUrl, filefind);
		File in = new File(filefind);
		Document doc = Jsoup.parse(in, "UTF-8", "");
		Elements spanPoint = doc
				.select("span[style=display:inline-block;font-family:宋体;font-size:11pt;width:1080px;]");

		// 获取页数，从而确定定时器执行最大次数

		final Elements pageNumber = doc.select("span[id=LabelPageCount]");
		final int number;
		// 判断查询结果是否为空
		if (pageNumber.text().toString().equals("")) {

			number = 0;
			System.out.println("对不起，关键词:[" + encondeword + "]"
					+ "未被索引，请使用模糊检索方式查询" + "\n");
		} else {
			number = Integer.parseInt(pageNumber.text().toString());
			for (Element e : spanPoint) {
				FileUtil.writefile(e.text() + "\n", logpath);
				FileUtil.writefile(e.text() + "\n", FileUtil.crawlerpath);
			}
			System.out.println("词:[" + encondeword + "]" + "首页数据爬取成功" + "\n");
		}
		// final int number = Integer.parseInt(pageNumber.text().toString());
		// 下一页
		final String nextUrl = "http://www.cncorpus.org/CnCindex.aspx?__EVENTTARGET=LBnextpage&__EVENTARGUMENT=&__VIEWSTATE=%2FwEPDwUKMTk4MDQ0MTE5OA9kFgICAw9kFgQCKQ8PFgIeB1Zpc2libGVnZBYIAgMPDxYCHgRUZXh0BTrnrKwx5YiwNTAw5p2h77yM5YWx5p%2Bl6K%2Bi5YiwNTI3MjjmnaHnrKblkIjopoHmsYLnmoTkvovlj6UhZGQCBQ8PFgIfAGhkZAIHDw8WAh8AaGRkAg0PDxYCHwBnZGQCLw8PFgIfAGdkFgoCAQ8PFgIfAGhkZAIDDw8WAh8AaGRkAgkPDxYCHwEFATFkZAILDw8WAh8BBQMxMDZkZAINDw8WAh8BBQU1MjcyOGRkGAEFHl9fQ29udHJvbHNSZXF1aXJlUG9zdEJhY2tLZXlfXxYKBQtSQmluZGV4d29yZAUKUkJsaWtlbW9kZQUKUkJsaWtlbW9kZQUOUkJmdWxsdGV4dG1vZGUFDlJCZnVsbHRleHRtb2RlBQxSYWRpb0J1dHRvbjMFDFJhZGlvQnV0dG9uMwUMUmFkaW9CdXR0b240BQ5DaGVja0JveENodWNodQUQQ2hlY2tCb3hLV0lDbW9kZeDFB%2FOXKuors7kNSBQvXV5bn9EPHGNvJgT94fUsjIhu&__VIEWSTATEGENERATOR=3A0BE18D&__EVENTVALIDATION=%2FwEWFQKNm9KcBQLYiuv%2FCwLzuO7zDQL3uO7zDQLV%2BYmkCgLZ%2BYmkCgKM54rGBgK8u9naBwKJlM7DBwKAg8rcDgKWzvT1CAKWzuCuBwK2q5qHDgK%2FxfDTAQLxqL%2BhAgLCpJSTBQKKn9X3AwKLlOLCBgLc%2F9LTBQL3t9jyBALZu%2BPjB6rMBlDgd9II8LdS4y%2BzUaXaUcHAjVptZHdcvx89wEPp"
				+ "&TextBoxCCkeywords="
				+ word
				+ "&DropDownListPsize=500&1=RBindexword&2=RadioButton4&txtTopage=";
		// 获取findurl的cookie，进入下一页url需要
		getCookie(findUrl);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {

				try {
					// 保存首页html，此处不能保存，问题定位中
					++count;
					if (count <= number) {
						// 保存每一次的nexturl值
						System.out.println("正在爬取" + "词:[" + encondeword + "]"
								+ "第" + count + "页数据⋯⋯" + "\n");
						// 下一页html保存路径
						String filenext = "./htmlnext/" + encondeword + count
								+ savehtml;

						HtmlUtil.urlToHtm(encondeword, nextUrl, filenext);
						File innext = new File(filenext);
						Document docnext = Jsoup.parse(innext, "UTF-8", "");
						Elements spannext = docnext
								.select("span[style=display:inline-block;font-family:宋体;font-size:11pt;width:1080px;]");
						System.out.println("词:[" + encondeword + "]" + "第"
								+ count + "页据爬取成功" + "\n");
						for (Element e : spannext) {
							FileUtil.writefile(e.text() + "\n", logpath);
							FileUtil.writefile(e.text() + "\n",
									FileUtil.crawlerpath);
						}
					} else if (count > number) {
						count = 1;
						if (number != 0) {
							System.out.println("词:[" + encondeword + "]"
									+ "标注语料已经抓取完成，结果保存在" + FileUtil.crawlerpath
									+ "中" + "\n");
							FileUtil.extractedWord(encondeword, logpath,
									FileUtil.countpath);
						}
						cancel();
					}

				} catch (IOException e) {

					e.printStackTrace();
				}

			}
		};
		Timer timer = new Timer();
		long delay = 0;
		long intevalPeriod = 1 * 20000;
		timer.scheduleAtFixedRate(task, delay, intevalPeriod);

	}
}
