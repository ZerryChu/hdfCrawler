package zerry.hdfWebCollector;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import zerry.hdfWebCollector.dao.PublicDao;
import zerry.hdfWebCollector.model.Page;

public abstract class CrawlInfo {
	private PublicDao publicDao;
	
	//@Autowired
	public void setPublicDao(PublicDao publicDao) {
		this.publicDao = publicDao;
	}
	
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
	
	public abstract void visit(Page page);
	
}
