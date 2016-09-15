package io.toast.tk.dao.domain.impl.test.block;

import java.util.List;


public interface ITestPlan {

	List<ICampaign> getCampaigns();

	String getName();

	IProject getProject();

	void setId(String id);

	void setIteration(short iteration);

	void setProject(IProject project);
}