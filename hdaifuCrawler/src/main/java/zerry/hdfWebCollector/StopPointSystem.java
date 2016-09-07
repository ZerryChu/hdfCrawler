package zerry.hdfWebCollector;

import java.util.List;

import zerry.hdfWebCollector.model.CrawlDatum;
import zerry.hdfWebCollector.model.StopPoint;

public class StopPointSystem {

	protected static boolean is_on = false;

	// 待执行的断点
	protected static List<StopPoint> stopPoints;

	public static void run() {
		for (int i = 0;i < stopPoints.size(); i++) {
			new StopPointCrawler(stopPoints.get(i)).run();
			// while(num > ... ) sleep()...
			// 线程数量统计, 控制并发量
		}
	}

}

class StopPointCrawler extends Thread {

	private StopPoint stopPoint;

	public StopPointCrawler(StopPoint stopPoint) {
		// TODO Auto-generated constructor stub
		this.stopPoint = stopPoint;
	}
	
	public void takeAction() {
		String url = stopPoint.getUrl();
		String from = stopPoint.getFrom();
		int pageNum = stopPoint.getPageNum();
		
		if (url != null) {
			CrawlDatum newCrawlDatum = new CrawlDatum(url);
			try {
				Crawler crawler = new Crawler();
				crawler.setFlag(1);
				crawler.execute(newCrawlDatum, from, pageNum);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// send into stopPointSystem
			}
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		takeAction();
	}
}