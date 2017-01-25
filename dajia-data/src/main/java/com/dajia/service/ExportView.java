package com.dajia.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.document.AbstractXlsView;

public class ExportView extends AbstractXlsView {
	Logger logger = LoggerFactory.getLogger(ExportView.class);

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String fileName = (String) model.get("filename");
		String sheetName = (String) model.get("sheetname");
		List<String> headers = (List<String>) model.get("headers");
		List<String> results = (List) model.get("results");

		// change the file name
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		// create excel xls sheet
		Sheet sheet = workbook.createSheet(sheetName);

		// create header row
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("ID");
		header.createCell(1).setCellValue("Name");
		header.createCell(2).setCellValue("Date");

		// Create data cells
		int rowCount = 1;
		for (Object obj : results) {
			// Row courseRow = sheet.createRow(rowCount++);
			// courseRow.createCell(0).setCellValue(course.getId());
			System.out.println("");
		}
	}
}
