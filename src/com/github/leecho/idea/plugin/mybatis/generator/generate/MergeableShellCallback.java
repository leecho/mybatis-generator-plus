package com.github.leecho.idea.plugin.mybatis.generator.generate;

import com.github.leecho.idea.plugin.mybatis.generator.util.JavaFileMerger;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.FileNotFoundException;

/**
 * @author LIQIU
 */
public class MergeableShellCallback extends DefaultShellCallback {

	public MergeableShellCallback(boolean overwrite) {
		super(overwrite);
	}

	@Override
	public boolean isMergeSupported() {
		return true;
	}

	@Override
	public String mergeJavaFile(String newFileSource, String existingFileFullPath, String[] javadocTags, String fileEncoding) throws ShellException {
		String filePath = existingFileFullPath.replace(".java", "");
		if (filePath.endsWith("Mapper")) {
			try {
				return new JavaFileMerger().getNewJavaFile(newFileSource, existingFileFullPath);
			} catch (FileNotFoundException e) {
				throw new ShellException(e);
			}
		} else {
			return newFileSource;
		}
	}
}
