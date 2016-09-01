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

	public void execute(CrawlDatum datum, String from) throws Exception {
		HttpResponse response = getResponse(datum);
		Page page = new Page(datum, response);
		if (flag == 1) {
			CrawlInfo crawlInfo = new CrawlDoctorInfo();
			crawlInfo.setFrom(from);
			crawlInfo.visit(page);
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

		long startTime = System.currentTimeMillis();

		try {
			execute(crawlDatum, from);
			crawlDatum.setStatus(CrawlDatum.STATUS_DB_SUCCESS);
		} catch (Exception ex) {
			crawlDatum.setStatus(CrawlDatum.STATUS_DB_FAILED);
		}

		long endTime = System.currentTimeMillis();
		long costTime = (endTime - startTime);
		System.out.println("costTime: " + costTime + "ms");
	}

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
			url = "http://www.haodf.com" + url;
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

	public static void main(String[] args) {
		try {
			startCrawling();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
