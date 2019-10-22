package com.ricoh.cidemo.report.portlet.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.ricoh.cidemo.report.portlet.dto.UserDto;
import com.ricoh.cidemo.report.portlet.portlet.CiDemoReportPortlet;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;

public class PDFUtil {
	private static final Log log = LogFactoryUtil.getLog(CiDemoReportPortlet.class);

	/**
	 * @param parameters
	 * @param dataSource
	 * @param inputJRXML
	 * @param outPDF
	 * @throws JRException
	 */
 
	@SuppressWarnings("deprecation")
	public static OutputStream generatePdf(List<UserDto>userlist, JasperReport jasperReport, OutputStream os) {
		List<String> resultados = new ArrayList<>();
		ByteArrayInputStream responseStream = null;
		JRExporter exporterPDF = new JRPdfExporter();
		for(UserDto u:userlist){
			resultados.add(UserDtoUtil.isUserToStringGenerate(u).toString());
			System.out.println(resultados);
		}
		
		try {
		JasperDesign jasperDesign;
		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Users Report");
		JasperPrint print = JasperFillManager.fillReport(jasperReport,parameters, new JRBeanCollectionDataSource(resultados));
		exporterPDF.setParameter(JRExporterParameter.JASPER_PRINT, print);
		exporterPDF.setParameter(JRExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporterPDF.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
		exporterPDF.exportReport();
		responseStream=new ByteArrayInputStream(JasperExportManager.exportReportToPdf(print));
	} catch (JRException e) {
		log.error("CiDemoReportPortlet.generatePdf(): "+e.getMessage(),e);
	}
		return os;
	}

}
