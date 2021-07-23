module ProjektVerwaltung {

	// Java FX
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;

	// JDBC API
	requires java.sql;

	// Für Java FX
	opens controller;
	opens test;

	//
	opens model;
}