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
import zerry.hdfWebCollector.entity.Doctor;
import zerry.hdfWebCollector.model.Page;

public class CrawlDoctorInfo extends CrawlInfo {
	
	public void save(Doctor doctor) {
		Transaction tx = null;
		Session session = null;
		try {
			 session = sessionFactory.getCurrentSession();
		     tx = (Transaction) session.beginTransaction();
		     session.save(doctor);
		     tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void visit(Page page) {
		// String keyword = page.meta("keyword");
		Elements results = page.select(".good_doctor_list_td");
		// System.out.println(results.size());

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
		int iter = 0;
		Doctor doctor = null;
		for (int rank = 0; rank < results.size(); rank++) {
			Element result = results.get(rank);

			String text = result.text();
			System.out.println(text);

			if (iter == 0) {
				doctor = ctx.getBean(Doctor.class);
				doctor.setFrom(from);
				doctor.setName(text);
			} else if (iter == 1) {
				doctor.setOffice(text);
			} else if (iter == 2){
				doctor.setSubject(text);
				try {
					//publicDao.save(doctor);
					save(doctor);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			iter++;
			iter %= 4;

			/*
			 * 无后续节点 CrawlDatum datum = new CrawlDatum(result.attr("abs:href"))
			 * .meta("keyword", keyword) .meta("pageNum", pageNum + "")
			 * .meta("rank", rank + "") .meta("pageType", "outlink")
			 * .meta("depth", (depth + 1) + "") .meta("referer", page.getUrl());
			 * next.add(datum);
			 */
		}

	}
}
