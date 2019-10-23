package com.ricoh.cidemo.report.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.google.common.io.Files;
import com.liferay.arquillian.portal.annotation.PortalURL;
import com.liferay.portal.kernel.exception.PortalException;
import com.ricoh.cidemo.report.portlet.constants.CiDemoReportPortletKeys;

@RunAsClient
@RunWith(Arquillian.class)
public class CiDemoReportPortletFunctionalTest {

	private static Logger log = LogManager.getRootLogger();
	
	public CiDemoReportPortletFunctionalTest() {
		super();		
		log.info("CiDemoReportPortletFunctionalTest constructor");
	}
	
	/**
	 * Deploy current project building it from processBuilder
	 * @return
	 * @throws Exception
	 */
	@Deployment(name = "com.ricoh.cidemo.report", order = 1)
	public static JavaArchive create() throws Exception {
		final File tempDir = Files.createTempDir();
		log.info(">>>> CIDemoReportPortletFunctionalTest.create()");
		String gradlew = "./gradlew";
		String osName = System.getProperty("os.name", "");
		if (osName.toLowerCase().contains("windows")) {
			gradlew = "..\\..\\gradlew.bat";
		} else {
		    gradlew = "gradle";
		}
		final ProcessBuilder processBuilder = new ProcessBuilder(gradlew, "jar", "-Pdir=" + tempDir.getAbsolutePath());
		final Process process = processBuilder.start();
		process.waitFor();
		String fileName = tempDir.getAbsolutePath() + "/com.ricoh.cidemo.report.portlet-1.0.0.jar";
		final File jarFile = new File(fileName);
		log.info("Generated file: "+fileName);
		return ShrinkWrap.createFromZipFile(JavaArchive.class, jarFile);
	}


	@Test
	@OperateOnDeployment("com.ricoh.cidemo.report")
	public void testInstallPortlet() throws IOException, PortalException {
		log.info(">>>> CiDemoReportPortletFunctionalTest.testInstallPortlet()");
		String url = getPortletURL();
		_browser.get(url);
		String bodyText = _browser.getPageSource();
		Assert.assertTrue("The portlet is not well deployed", bodyText.contains("CI-DEMO-REPORT PORTLET"));
	}

	
	

	@Test
	@OperateOnDeployment("com.ricoh.cidemo.report")
	public void createReport() throws IOException, PortalException, InterruptedException {
		log.info(">>>> CiDemoReportPortletFunctionalTest.createReport()");
		log.info(">>>> Inicializando llamada Ajax");
		String url = getPortletURL();
		_browser.get(url);
		Graphene.guardAjax(_add).click();
		Thread.sleep(5000);
		String bodyText = _browser.getPageSource();
		Assert.assertTrue("The report PDF of users has failed", bodyText.contains("SUCCESS"));
		
	}
	
	/**
	 * Returns portlet URL, uses the environment vars TARGET_LIFERAY_TESTING_HOST and 
	 * TARGET_LIFERAY_TESTING_PORT to override the hardcoded localhost:8080 in @PortalURL annotation
	 * @return
	 */
	private String getPortletURL() {
		String targetHost = System.getenv("TARGET_LIFERAY_TESTING_HOST");
		String targetPort = System.getenv("TARGET_LIFERAY_TESTING_PORT");
		
		
		// Hardcoded IP, used to test in my windows develop machine against my local liferay
//		String osName = System.getProperty("os.name", "");
//		if (osName.toLowerCase().contains("windows")) {
//			targetHost="192.168.56.1";
//		}
		
		String url = _portletURL.toExternalForm();
		if (targetHost!=null && !"".equals(targetHost)) {
			url = url.replaceAll("localhost", targetHost);
		}
		if (targetPort!=null && !"".equals(targetPort)) {
			url = url.replaceAll("8080", targetPort);
		}
		log.info("portletURL: "+url);
		return url; 
	}	
	
		
	/*public void closeBrowser() throws Exception {
		log.info(">>>> CiDemoReportPortletFunctionalTest.closeBrowser()");
		_browser.close();
	}
	*/

	@Drone
	private WebDriver _browser;

	@PortalURL(CiDemoReportPortletKeys.CIDEMOREPORT)
	private URL _portletURL;

	@FindBy(css = "button[id$='buttonAdd']")
	private WebElement _add;
	
	@FindBy(id = "_callResponse")
	private WebElement _callResponse;
	
	@FindBy(id = "fm")
	private WebElement _fm;

	
}
