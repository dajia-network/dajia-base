package com.dajia.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.dajia.util.CommonUtils;
import com.dajia.vo.OrderVO;

public class ExportService extends AbstractXlsView {
	Logger logger = LoggerFactory.getLogger(ExportService.class);

	@Override
	public void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String resource = (String) model.get("resource");

		if (resource.equalsIgnoreCase("order")) {
			Sheet sheet = workbook.createSheet("订单数据");
			List<String> headers = new ArrayList<String>();
			headers.add("ID");
			headers.add("订单编号");
			headers.add("产品名");
			headers.add("购买数量");
			headers.add("购买单价");
			headers.add("邮费");
			headers.add("总金额");
			headers.add("订单状态");
			headers.add("购买时间");
			headers.add("付款方式");
			headers.add("用户");
			headers.add("收货信息");
			headers.add("用户留言");
			headers.add("商家留言");
			headers.add("内部备注");
			headers.add("快递公司");
			headers.add("快递单号");

			// create header row
			Row header = sheet.createRow(0);
			for (int i = 0; i < headers.size(); i++) {
				header.createCell(i).setCellValue(headers.get(i));
			}

			List<OrderVO> orders = (List<OrderVO>) model.get("data");
			for (int i = 0; i < orders.size(); i++) {
				OrderVO ov = orders.get(i);
				Row row = sheet.createRow(i + 1);
				int j = 0;
				row.createCell(j++).setCellValue(ov.orderId);
				row.createCell(j++).setCellValue(ov.trackingId);
				row.createCell(j++).setCellValue(ov.productDesc);
				row.createCell(j++).setCellValue(CommonUtils.displayInteger(ov.quantity));
				row.createCell(j++).setCellValue(CommonUtils.displayBigDecimal(ov.unitPrice));
				row.createCell(j++).setCellValue(CommonUtils.displayBigDecimal(ov.postFee));
				row.createCell(j++).setCellValue(CommonUtils.displayBigDecimal(ov.totalPrice));
				row.createCell(j++).setCellValue(ov.orderStatus4Show);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				row.createCell(j++).setCellValue(sdf.format(ov.orderDate));
				row.createCell(j++).setCellValue(ov.payType4Show);
				row.createCell(j++).setCellValue(ov.userName);
				row.createCell(j++).setCellValue(ov.contactName + " " + ov.contactMobile + " " + ov.address);
				row.createCell(j++).setCellValue(ov.userComments);
				row.createCell(j++).setCellValue(ov.comments);
				row.createCell(j++).setCellValue(ov.adminComments);
				row.createCell(j++).setCellValue(ov.logisticAgent4Show);
				row.createCell(j++).setCellValue(ov.logisticTrackingId);
			}
		}
	}
}
