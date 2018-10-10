package com.jd.idea.plugin.mybatis.generator.generate;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.ui.popup.Balloon;
import org.mybatis.generator.api.ProgressCallback;

public class GenerateCallback implements ProgressCallback {

	private ProgressIndicator indicator;

	private Balloon balloon;

	public GenerateCallback(ProgressIndicator indicator, Balloon balloon) {
		this.indicator = indicator;
		this.balloon = balloon;
	}

	@Override
	public void introspectionStarted(int i) {
	}

	@Override
	public void generationStarted(int i) {
	}

	@Override
	public void saveStarted(int i) {
	}

	@Override
	public void startTask(String s) {
		System.out.println("Start Task: " + s);
		indicator.setText(s);
		indicator.setFraction(indicator.getFraction() + 0.1);
	}

	@Override
	public void done() {
		indicator.setText("Generate Finished");
		indicator.setFraction(1);
		this.balloon.hide();
	}

	@Override
	public void checkCancel() throws InterruptedException {

	}
}
