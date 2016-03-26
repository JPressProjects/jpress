package io.jpress.core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;

public class JBaseCRUDController<M extends JModel<? extends JModel<?>>> extends
		JBaseController {

	private static final Log logger = Log.getLog(JBaseCRUDController.class);;
	private final Class<M> mClazz;
	protected final M mDao;

	@SuppressWarnings("unchecked")
	public JBaseCRUDController() {
		ParameterizedType type = getParameterizedType(getClass());
		mClazz = (Class<M>) type.getActualTypeArguments()[0];
		mDao = getDao();
	}

	private ParameterizedType getParameterizedType(Class<?> clazz) {
		if (clazz == Object.class) {
			logger.error("get ParameterizedType error in _BaseController.class");
			return null;
		}

		Type genericSuperclass = clazz.getGenericSuperclass();
		if (genericSuperclass instanceof ParameterizedType) {
			return (ParameterizedType) genericSuperclass;
		} else {
			return getParameterizedType(clazz.getSuperclass());
		}
	}

	@SuppressWarnings("unchecked")
	public void index() {
		Page<M> page = onPageLoad(getPageNumbere(), getPageSize());
		if (null == page) {
			page = (Page<M>) mDao.doPaginate(getPageNumbere(), getPageSize());
		}
		keepPara();
		setAttr("page", page);
		render("index.html");
	}

	
	public void edit() {
		keepPara();
		render("edit.html");
	}

	public void save() {
		getModel(mClazz).save();
		renderAjaxResultForSuccess("ok");
	}

	public void delete() {
		mDao.deleteById(getParaToLong());
		renderAjaxResultForSuccess("删除成功");
	}

	@SuppressWarnings("unchecked")
	private M getDao() {
		M m = null;
		try {
			Field field = mClazz.getDeclaredField("DAO");
			m = (M) field.get(null);
		} catch (Exception e) {
			logger.error("get DAO error.", e);
		}
		return m;
	}

	public Page<M> onPageLoad(int pageNumber, int pageSize) {
		return null;
	}

}
