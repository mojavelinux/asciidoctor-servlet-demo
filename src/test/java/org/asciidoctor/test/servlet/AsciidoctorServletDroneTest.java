package org.asciidoctor.test.servlet;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.asciidoctor.bean.AsciidoctorProcessor;
import org.asciidoctor.servlet.AsciidoctorServlet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RunWith(Arquillian.class)
public class AsciidoctorServletDroneTest {
	@Drone
	WebDriver browser;
	
	@ArquillianResource
	URL deploymentUrl;
	
	@Deployment(testable = false)
	public static WebArchive createDeployment() {
		MavenDependencyResolver resolver = DependencyResolvers
				.use(MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
		File[] deps = resolver
				.artifacts("org.asciidoctor:asciidoctor-java-integration")
				.exclusion("com.beust:jcommander:*")
				.resolveAsFiles();
		
		return ShrinkWrap.create(WebArchive.class, "test.war")
				.addClass(AsciidoctorServlet.class)
				.addClass(AsciidoctorProcessor.class)
				.addAsLibraries(deps)
				.addAsWebResource(new File("src/main/webapp/asciidoctor.css"), "asciidoctor.css")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Test
	public void testViewInputForm() throws MalformedURLException {
		browser.navigate().to(deploymentUrl + "asciidoctor");
		WebElement textarea = browser.findElement(By.cssSelector("textarea"));
		textarea.sendKeys("= AsciiDoc Invasion!\n\nIke has invaded AsciiDoc.");
		browser.findElement(By.name("submit"));
	}

	@Test
	public void testCanRenderAsciiDoc() throws MalformedURLException {
		browser.navigate().to(deploymentUrl + "asciidoctor");
		WebElement textarea = browser.findElement(By.cssSelector("textarea"));
		textarea.sendKeys("= AsciiDoc Invasion!\n\nIke has invaded AsciiDoc.");
		WebElement submit = browser.findElement(By.name("submit"));
		submit.click();
		browser.findElement(By.id("header"));
	}
}
