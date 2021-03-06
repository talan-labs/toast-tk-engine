package io.toast.tk.runtime.result;

import io.toast.tk.dao.domain.api.test.ITestResult;
import io.toast.tk.dao.domain.api.test.ITestResult.ResultKind;
import io.toast.tk.runtime.IActionItemRepository;

public abstract class AbstractResultHandler<T> implements IResultHandler<T> {

    protected IActionItemRepository objectRepository;

    public AbstractResultHandler(IActionItemRepository objectRepository) {
        this.objectRepository = objectRepository;
    }
    
	@Override
    public ITestResult result(T value, final String expected) {
		ITestResult result = buildResult(value, expected);
		if (shouldStoreResult(expected)) {
			objectRepository.getUserVariables().put(expected, value);
			return result;
		}
		
		Object expectedValue = null;
		if(expected != null  && isVar(expected)) {
			objectRepository.getUserVariables().get(expected);
		} else {
			if(expected != null) {
				expectedValue = expected.trim();
			} else {
				expectedValue = expected;
			}
		}

		if(value == null){
			if(expectedValue != null){
				markResultAsFailure(result);
			}
		}
		else if(expectedValue != null && !expectedValue.equals("") && !value.toString().equals(expectedValue)
					&& !shouldStoreResult(expected) // The comparison of stored result is not well executed
													// If the user want to compare result, he has to use the phrase
				){
			markResultAsFailure(result);
		}
		return result;
    }

	private boolean shouldStoreResult(String expected) {
		return expected != null && isVar(expected);
	}

	private void markResultAsFailure(ITestResult result) {
		result.setIsSuccess(false);
		result.setIsFailure(true);
		result.setResultKind(ResultKind.FAILURE);
	}
	
	private boolean isVar(String expected){
		return expected.startsWith("$");
	}
	
	protected abstract ITestResult buildResult(T value, String expected);
}
