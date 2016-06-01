package io.jpress.core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;

import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;

public class JBaseCRUDController<M extends JModel<? extends JModel<?>>> extends JBaseController {

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
		Page<M> page = onIndexDataLoad(getPageNumbere(), getPageSize());
		if (null == page) {
			page = (Page<M>) mDao.doPaginate(getPageNumbere(), getPageSize());
		}
		setAttr("page", page);
		render("index.html");
	}

	public void edit() {
		BigInteger id = getParaToBigInteger("id");
		if (id != null) {
			setAttr(StrKit.firstCharToLowerCase(mClazz.getSimpleName()), mDao.findById(id));
		}
		render("edit.html");
	}

	public void save() {
		M m = getModel(mClazz);

		if (isMultipartRequest()) {
			getFile();
		}

		if (!onModelSaveBefore(m)) {
			renderAjaxResultForError();
			return;
		}
		m.saveOrUpdate();

		if (!onModelSaveAfter(m)) {
			renderAjaxResultForError();
			return;
		}

		renderAjaxResultForSuccess("ok");
	}

	public void delete() {
		BigInteger id = getParaToBigInteger("id");
		if (id != null) {
			mDao.deleteById(id);
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError();
		}
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

	public Page<M> onIndexDataLoad(int pageNumber, int pageSize) {
		return null;
	}

	public boolean onModelSaveBefore(M m) {
		// do nothing
		return true;
	}

	public boolean onModelSaveAfter(M m) {
		// do nothing
		return true;
	}

}
