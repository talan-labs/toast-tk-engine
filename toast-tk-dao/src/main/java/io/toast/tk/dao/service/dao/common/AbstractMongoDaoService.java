package io.toast.tk.dao.service.dao.common;

import java.util.List;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Key;
import com.github.jmkgreen.morphia.dao.BasicDAO;
import com.google.inject.Inject;

import io.toast.tk.dao.domain.impl.common.TagImpl;
import io.toast.tk.dao.domain.impl.test.block.ITaggable;

public abstract class AbstractMongoDaoService<E extends ITaggable> 
extends
		BasicDAO<E, ObjectId> 
implements 
		ICrudDaoService<E> {

	@Inject
	protected EntityCollectionManager entityManager;

	protected CommonMongoDaoService commonService;

	Class<E> clazz;

	@Inject
	protected AbstractMongoDaoService(
		final Class<E> clazz, 
		final Datastore ds,
		final CommonMongoDaoService service
	) {
		super(ds);
		this.commonService = service;
		this.clazz = clazz;
	}

	/**
	 * collect any element flagged with this tag
	 * 
	 * @param tag
	 * @return
	 */
	public List<E> getByTag(final TagImpl tag) {
		return commonService.getTaggedItems(getDatastore(), clazz, tag);
	}

	@Override
	public Key<E> saveAndIndex(final E entity) {
		final Key<E> save = super.save(entity);
		final String collection = entityManager.getCollection(clazz);
		if (collection != null) {
			commonService.indexEntity(ds, save.getKind(), save.getId().toString(), entity);
		}
		return save;
	}
}