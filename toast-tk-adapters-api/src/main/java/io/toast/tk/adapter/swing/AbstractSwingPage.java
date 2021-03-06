package io.toast.tk.adapter.swing;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.toast.tk.adapter.swing.component.DefaultSwingElement;
import io.toast.tk.core.adapter.AutoSwingType;
import io.toast.tk.core.driver.IRemoteSwingAgentDriver;
import io.toast.tk.core.runtime.IFeedableSwingPage;
import io.toast.tk.core.runtime.ISwingAutoElement;
import io.toast.tk.core.runtime.ISwingElementDescriptor;

/**
 * Page fixture abstraction, initializes fixture elements's locators based on wiki definitions
 */
public abstract class AbstractSwingPage implements IFeedableSwingPage {

	private static final Logger LOG;

	private static final ServiceLoader<ISwingComponentFactory> FACTORY_LOADER;
	
	static {
		LOG = LogManager.getLogger(AbstractSwingPage.class);
		FACTORY_LOADER = ServiceLoader.load(ISwingComponentFactory.class);
	}
	
	protected final Map<String, ISwingElementDescriptor> elements;

	protected final Map<String, ISwingAutoElement> autoElements;
	
	private String pageName;

	private String beanClassName; 
	
	public AbstractSwingPage() {
		this.elements = new HashMap<>();
		this.autoElements = new HashMap<>();
	}
	

	@Override
	public void initElement(final ISwingElementDescriptor e) {
		initElement(e.getName(), e.getType().name(), e.getLocator());
	}

	public void initElement(
		final String name,
		final String type,
		final String locator
	) {
		final DefaultSwingElement defaultWebElement = new DefaultSwingElement(name, AutoSwingType.valueOf(type), locator);
		elements.put(name, defaultWebElement);
		try {
			final ISwingComponentFactory factory = FACTORY_LOADER.iterator().next();
			if(factory == null) {
				throw new IllegalAccessError("No Swing Component Factory declared !");
			}
			final ISwingElementDescriptor iSwingComponent = elements.get(name);
			if(iSwingComponent != null) {
				final ISwingAutoElement execAutoClass = factory.getElement(iSwingComponent);
				// for this abstract page, init fields (for java classes only
				initBeanFields(name, execAutoClass);
				autoElements.put(name, execAutoClass);
			}
			else {
				// throw something
			}
		}
		catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void initBeanFields(
		final String name,
		final ISwingAutoElement execAutoClass
	) {
		Arrays.stream(getClass().getFields()).forEach(field -> setFieldProperty(name, execAutoClass, field));
	}

	private void setFieldProperty(
		final String name,
		final ISwingAutoElement execAutoClass, 
		final Field f
	) {
		final Class<?> automationClass = f.getType();
		if(isPropertyToSet(name, f, automationClass)) {
			try {
				BeanUtils.setProperty(this, name, execAutoClass);
			}
			catch(Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	private static boolean isPropertyToSet(
		final String name, 
		final Field f,
		final Class<?> automationClass
	) {
		return ISwingAutoElement.class.isAssignableFrom(automationClass) && f.getName().equals(name);
	}

	/**
	 * Convenient method to call an element based on the page enclosed fields' enum
	 * 
	 * @param name the element name
	 * @return the element descriptor
	 */
	public ISwingElementDescriptor getElement(final String name) {
		return elements.get(name);
	}

	/**
	 * Convenient method to call an element based on the page enclosed fields' enum
	 * 
	 * @param name the element name
	 * @return the swing auto element
	 */
	public ISwingAutoElement getAutoElement(final String name) {
		return autoElements.get(name);
	}

	public String getBeanClassName() {
		return beanClassName;
	}

	public void setBeanClassName(final String beanClassName) {
		this.beanClassName = beanClassName;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(final String pageName) {
		this.pageName = pageName;
	}

	public List<ISwingElementDescriptor> getLocationElements() {
		return new ArrayList<>(elements.values());
	}

	/**
	 * set the driver that will be used by the automation elements
	 * 
	 * @param driver automation driver 
	 */
	@Override
	public void setDriver(final IRemoteSwingAgentDriver driver) {
		autoElements.values().stream().forEach(swingAutoElement -> swingAutoElement.setFrontEndDriver(driver));
	}
}