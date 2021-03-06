package io.toast.tk.runtime.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.toast.tk.core.runtime.IWebAutoElement;
import io.toast.tk.runtime.IRepository;

public class WebComponentRepository implements IRepository<IWebAutoElement<?>> {

	Map<String, IWebAutoElement<?>> components = new HashMap<>();
	
	@Override
	public IWebAutoElement<?> get(final String entityName) {
		return components.get(entityName);
	}

	@Override
	public Collection<IWebAutoElement<?>> getAll() {
		return components.values();
	}

	@Override
	public void add(
		final String entityName, 
		final IWebAutoElement<?> entity
	) {
		components.put(entityName, entity);
	}

	@Override
	public Map<String, IWebAutoElement<?>> getMap() {
		return components;
	}

	@Override
	public void setMap(final Map<String, IWebAutoElement<?>> components) {
		this.components = components;
	}

	@Override
	public void clear() {
		components.clear();		
	}
}