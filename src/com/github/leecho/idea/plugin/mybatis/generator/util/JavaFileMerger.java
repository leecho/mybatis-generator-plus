package com.github.leecho.idea.plugin.mybatis.generator.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LIQIU
 */
public class JavaFileMerger {

	public String getNewJavaFile(String newFileSource, String existingFileFullPath) throws FileNotFoundException {
		CompilationUnit newCompilationUnit = JavaParser.parse(newFileSource);
		CompilationUnit existingCompilationUnit = JavaParser.parse(new File(existingFileFullPath));
		return mergerFile(newCompilationUnit, existingCompilationUnit, newFileSource);
	}

	public String mergerFile(CompilationUnit newCompilationUnit, CompilationUnit existingCompilationUnit, String newFileSource) {

		NodeList<ImportDeclaration> newsImports = newCompilationUnit.getImports();
		NodeList<ImportDeclaration> existsImports = existingCompilationUnit.getImports();
		HashSet<ImportDeclaration> allImports = new HashSet<>();
		allImports.addAll(newsImports);
		allImports.addAll(existsImports);
		allImports.removeAll(newsImports);

		List<String> sources = Arrays.stream(newFileSource.split("\n")).collect(Collectors.toList());

		sources.addAll(newsImports.size() + 2, allImports.stream().map(importDeclaration -> importDeclaration.toString().replace("\r\n","")).collect(Collectors.toList()));


		List<MethodDeclaration> newMethods = newCompilationUnit.getTypes().get(0).getMethods();
		List<MethodDeclaration> oldMethods = existingCompilationUnit.getTypes().get(0).getMethods();
		HashSet<MethodDeclaration> allMethods = new HashSet<>();
		allMethods.addAll(newMethods);
		allMethods.addAll(oldMethods);
		allMethods.removeAll(newMethods);

		sources.addAll(sources.size() - 1, allMethods.stream().map(methodDeclaration -> "\n" + "    " + methodDeclaration.toString()).collect(Collectors.toList()));
		return String.join("\n", sources);
	}
}
