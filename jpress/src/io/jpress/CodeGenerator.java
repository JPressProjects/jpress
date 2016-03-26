package io.jpress;

import io.jpress.core.generator.JGenerator;

public class CodeGenerator {

	public static void main(String[] args) {
		
		String modelPackage = "io.jpress";
		
		String dbHost = "127.0.0.1";
		String dbName = "ccpress";
		String dbUser = "root";
		String dbPassword = "";
		
		new JGenerator(modelPackage, dbHost, dbName, dbUser, dbPassword).doGenerate();

	}

}
