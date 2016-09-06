package zerry.hdfWebCollector;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import zerry.hdfWebCollector.dao.PublicDao;
import zerry.hdfWebCollector.model.Page;

public abstract class CrawlInfo {
	/*
	private PublicDao publicDao;
	
	
	//@Autowired
	public void setPublicDao(PublicDao publicDao) {
		this.publicDao = publicDao;
	}
	
	*/

	protected static ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

	protected static SessionFactory sessionFactory = null;

	static {
		Configuration cfg = new Configuration().configure();
		sessionFactory = cfg.buildSessionFactory();
	}
	
	// 来自哪个科室的信息
	protected String from;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	public abstract int visit(Page page);
	
}
