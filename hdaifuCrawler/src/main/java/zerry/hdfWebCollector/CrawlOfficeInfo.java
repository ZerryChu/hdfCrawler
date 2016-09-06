package zerry.hdfWebCollector;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import zerry.hdfWebCollector.dao.PublicDao;
import zerry.hdfWebCollector.entity.Office;
import zerry.hdfWebCollector.model.Page;

public class CrawlOfficeInfo extends CrawlInfo {
	
	public void save(Office office) {
		Transaction tx = null;
		Session session = null;
		try {
			 session = sessionFactory.getCurrentSession();
		     tx = (Transaction) session.beginTransaction();
		     session.save(office);
		     tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int visit(Page page) {
		// String keyword = page.meta("keyword");
		Elements results = page.select(".jblb .blue");
		// System.out.println(results.size());

		for (int rank = 0; rank < results.size(); rank++) {
			Element result = results.get(rank);
			System.out.println(result.text());
			
			Office office = ctx.getBean(Office.class);
			office.setFrom(from);
			office.setName(result.text());
			try {
				//publicDao.save(office);
				save(office);
			} catch (Exception e) {
				e.printStackTrace();
			}
			/*
			 * 无后续节点 CrawlDatum datum = new CrawlDatum(result.attr("abs:href"))
			 * .meta("keyword", keyword) .meta("pageNum", pageNum + "")
			 * .meta("rank", rank + "") .meta("pageType", "outlink")
			 * .meta("depth", (depth + 1) + "") .meta("referer", page.getUrl());
			 * next.add(datum);
			 */
		}
		return results.size();
	}
	
}
