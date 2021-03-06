package com.mongo.test;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

import com.github.jmkgreen.morphia.Key;

import io.toast.tk.dao.domain.impl.report.TestPlanImpl;
import io.toast.tk.dao.domain.impl.repository.ProjectImpl;
import io.toast.tk.dao.service.dao.access.plan.TestPlanDaoService;
import io.toast.tk.dao.service.dao.access.repository.ProjectDaoService;

public class TestPlanTestCase extends EmbeddedMongoDBTestCase {
	
	static TestPlanDaoService service;
	static ProjectDaoService pService;
	
	@Test
	public void shouldFindNoTestPlanInEmbeddedMongoDb() {
		TestPlanDaoService.Factory repoFactory = injector.getInstance(TestPlanDaoService.Factory.class);
		service = repoFactory.create("play_db");				
		ProjectDaoService.Factory projectFactory = injector.getInstance(ProjectDaoService.Factory.class);
		pService = projectFactory.create("play_db");	
		List<TestPlanImpl> testplans = service.findAllReferenceProjects(new ObjectId().toString());
		Assert.assertEquals(0, testplans.size());
	}

	
	@Test
	public void shouldFind1TestPlanInEmbeddedMongoDb() {	
		ProjectImpl p = new ProjectImpl();
		p.setName("default");
		Key<ProjectImpl> keyProject = pService.save(p);
		
		TestPlanImpl plan = new TestPlanImpl();
		plan.setProject(p);
		service.save(plan);
		
		List<TestPlanImpl> testplans = service.findAllReferenceProjects(keyProject.getId().toString());
		Assert.assertEquals(1, testplans.size());
	}
	
	
	@Test
	public void shouldUpdateTestPlanBale() throws IllegalAccessException {	
		ProjectImpl p = new ProjectImpl();
		p.setName("default");
		Key<ProjectImpl> keyProject = pService.save(p);
		
		TestPlanImpl plan = new TestPlanImpl();
		plan.setName("Plan_1");
		plan.setIteration(Short.parseShort("0"));
		plan.setProject(p);
		service.save(plan);
		
		plan.setName("Plan2");
		service.updateTemplateFromTestPlan(plan);
		
		List<TestPlanImpl> testplans = service.findAllReferenceProjects(keyProject.getId().toString());
		Assert.assertEquals(1, testplans.size());
		Assert.assertEquals("Plan2", testplans.get(0).getName());
	}
}
