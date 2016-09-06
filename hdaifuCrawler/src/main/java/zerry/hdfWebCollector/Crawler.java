package zerry.hdfWebCollector;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import zerry.hdfWebCollector.model.CrawlDatum;
import zerry.hdfWebCollector.model.Page;
import zerry.hdfWebCollector.net.HttpRequest;
import zerry.hdfWebCollector.net.HttpResponse;
public class Crawler extends Thread {

	private String url = null;
	private String from = null;
	private int    flag = 0;

	public void execute(CrawlDatum datum, String from, int pageNum) throws Exception {
		HttpResponse response = getResponse(datum);
		Page page = new Page(datum, response);
		if (flag == 1) {
			CrawlInfo crawlInfo = new CrawlDoctorInfo();
			crawlInfo.setFrom(from);
			int num = crawlInfo.visit(page);
			String url = datum.getUrl();
			String newUrl = null;
			CrawlDatum newDatum = null;
			if (num > 0) {
				if (pageNum == 1) {
					newUrl = url + "?prov=&p=2";
				} else {
					int index = String.valueOf(pageNum).length();
					newUrl = url.substring(0, url.length() - index) + (pageNum+1);
				}
				newDatum = new CrawlDatum(newUrl);
				System.out.println(newUrl + ": " + num);
				execute(newDatum, from, pageNum+1);
			}
		}
		else if (flag == 2) {
			CrawlInfo crawlInfo = new CrawlOfficeInfo();
			crawlInfo.setFrom(from);
			crawlInfo.visit(page);
		}
		// regexRule 加入更多a的链接进入爬的队列
		/*
		 * regexRule.addPositive("[*].html"); if (autoParse &&
		 * !regexRule.isEmpty()) { parseLink(page, next); }
		 */
	}

	/*
	 * protected void parseLink(Page page) { String conteType =
	 * page.getResponse().getContentType(); if (conteType != null &&
	 * conteType.contains("text/html")) { Document doc = page.getDoc(); if (doc
	 * != null) { Links links = new Links().addByRegex(doc, regexRule);
	 * next.add(links); } } }
	 */

	public void run() {
		CrawlDatum crawlDatum = new CrawlDatum(url);
		// long startTime = System.currentTimeMillis();

		try {
			execute(crawlDatum, from, 1);
			crawlDatum.setStatus(CrawlDatum.STATUS_DB_SUCCESS);
		} catch (Exception ex) {
			crawlDatum.setStatus(CrawlDatum.STATUS_DB_FAILED);
			ex.printStackTrace();
		}

		// long endTime = System.currentTimeMillis();
		// long costTime = (endTime - startTime);
	}

	// http://haoping.haodf.com/keshi/DE4r0PiRvNoMFwIj8vHRbuflCII2Ip/daifu.htm?prov=&p=260: 104 心血管科timeout 
	public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
		HttpRequest request = new HttpRequest(crawlDatum);
		return request.getResponse();
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void crawlUrlBySubjects(CrawlDatum datum, ClassPathXmlApplicationContext ctx) throws Exception {
		HttpResponse response = getResponse(datum);
		Page page = new Page(datum, response);
		Elements results = page.select(".black_link");
		for (int rank = 0; rank < results.size(); rank++) {
			Element result = results.get(rank);

			String text = result.text();
			String url = result.attributes().get("href");
			
			//http://www.haodf.com/keshi/DE4r0PiRvNoMTC1OEy9lrSjP-pqBS2.htm
			//http://haoping.haodf.com/keshi/DE4r0PiRvNoMTC1OEy9lrSjP-pqBS2/daifu.htm
			url = "http://haoping.haodf.com" + url;
			url = url.substring(0, url.length() - 4) + "/daifu.htm";
			System.out.println(url + "   " + text);
			
			Crawler crawlerForDoctor = ctx.getBean(Crawler.class);
			Crawler crawlerForOffice = ctx.getBean(Crawler.class);
			crawlerForDoctor.setFlag(1);
			crawlerForDoctor.setFrom(text);
			crawlerForDoctor.setUrl(url);
			crawlerForOffice.setFlag(2);
			crawlerForOffice.setFrom(text);
			crawlerForOffice.setUrl(url);
			crawlerForDoctor.start();
			crawlerForOffice.start();
		}

	}

	public static void startCrawling() throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
		CrawlDatum crawlDatum = (CrawlDatum) ctx.getBean("startPageCrawlDatum");
		new Crawler().crawlUrlBySubjects(crawlDatum, ctx);
	}

	// 哼，掐掉我的爬虫，我从断点处继续爬！
	public static void takeAction2() {
		String url = "http://haoping.haodf.com/keshi/DE4r0PiRvNoMFwIj8vHRbuflCII2Ip/daifu.htm?prov=&p=265";
		CrawlDatum newCrawlDatum = new CrawlDatum(url);
		try {
			Crawler crawler = new Crawler();
			crawler.setFlag(1);
			crawler.execute(newCrawlDatum, "心血管内科", 265);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			// startCrawling();
			takeAction2();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}