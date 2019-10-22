package com.ricoh.cidemo.report.portlet.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.ricoh.cidemo.report.portlet.constants.CiDemoReportPortletKeys;
import com.ricoh.cidemo.report.portlet.dto.UserDto;
import com.ricoh.cidemo.report.portlet.util.PDFUtil;
import com.ricoh.cidemo.report.portlet.util.UserDtoUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 * @author Paula.V.Restrepo
 */

@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.display-name=ReportePDF",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + CiDemoReportPortletKeys.CIDEMOREPORT,
		"javax.portlet.resource-bundle=content.Language",
		"com.liferay.portlet.requires-namespaced-parameters=false", 
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class CiDemoReportPortlet extends MVCPortlet {
	private static final Log log = LogFactoryUtil.getLog(CiDemoReportPortlet.class);
	
	@Override
	public void init() throws PortletException {		
		log.info("CiDemoReportPortlet.init()");
		super.init();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws IOException, PortletException {
		
		Role adminRole;
		try {
			adminRole = RoleLocalServiceUtil.getRole(CompanyThreadLocal.getCompanyId(), "Administrator");
			List<User> adminUsers = UserLocalServiceUtil.getRoleUsers(adminRole.getRoleId());
			PrincipalThreadLocal.setName(adminUsers.iterator().next().getUserId());
			PermissionChecker checker =PermissionCheckerFactoryUtil.create(adminUsers.iterator().next());
			PermissionThreadLocal.setPermissionChecker(checker);
		} catch (PortalException e) {
			log.error("CiDemoReportPortlet.serveResource(): "+e.getMessage(),e);
		
		} catch (Exception e1) {
			log.error("CiDemoReportPortlet.serveResource(): "+e1.getMessage(),e1);
		}
		
		
		PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();
		if (permissionChecker.isOmniadmin()) {
		
		List<UserDto> userDtolist = new ArrayList<>();
		final ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		userDtolist= UserDtoUtil.getListUserDtoFromDB(themeDisplay.getCompanyId(), 0, 1000);
		
		try {
			if((userDtolist!=null) && (!userDtolist.isEmpty())) {
				JasperDesign jasperDesign;
				jasperDesign = JRXmlLoader.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("QueryReport.jrxml"));
				JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
				String pdfApplicationFormName = "UserReport.pdf";
				resourceResponse.setContentType("application/pdf");
				resourceResponse.setProperty(HttpHeaders.CONTENT_DISPOSITION,"attachement;filename="+pdfApplicationFormName.replaceAll(" ", ""));
				OutputStream os = PDFUtil.generatePdf(userDtolist, jasperReport, resourceResponse.getPortletOutputStream());
				os.flush();
				os.close();
			}
			
		} catch (JRException e) {
			log.error("CiDemoReportPortlet.serveResource(): "+e.getMessage(),e);
		} 
		
		} else {
			resourceResponse.addProperty(ResourceResponse.HTTP_STATUS_CODE, "401");
			resourceResponse.addProperty("Location", resourceResponse.createRenderURL().toString());
		}
		super.serveResource(resourceRequest, resourceResponse);
	}
	
	
			
		
}