module ProjektVerwaltung {

	// Java FX
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;

	// JDBC API
	requires java.sql;

	// F�r Java FX
	opens controller;
	opens test;

	//
	opens model;
}