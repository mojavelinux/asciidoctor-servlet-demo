package org.asciidoctor.test.servlet;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.asciidoctor.bean.AsciidoctorProcessor;
import org.asciidoctor.servlet.AsciidoctorServlet;
import org.asciidoctor.test.servlet.helper.RequestHolderFilter;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Activity;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.servlet.AfterServlet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@WarpTest
@RunWith(Arquillian.class)
public class AsciidoctorServletWarpTest {
    @Drone
    WebDriver browser;
    
    @ArquillianResource
    URL deploymentUrl;
    
    @Deployment
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
                .addClass(RequestHolderFilter.class)
                .addAsLibraries(deps)
                .addAsWebResource(new File("src/main/webapp/asciidoctor.css"), "asciidoctor.css")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test @RunAsClient
    public void testCanRenderAsciiDoc() throws MalformedURLException {
        browser.navigate().to(deploymentUrl + "asciidoctor");
        WebElement textarea = browser.findElement(By.cssSelector("textarea"));
        textarea.sendKeys("= AsciiDoc Invasion!\n:linkcss!:\n\nIke has invaded AsciiDoc.");
        
        Warp.initiate(new Activity() {
            @Override
            public void perform() {
                browser.findElement(By.name("submit")).click();
            }
        }).inspect(new VerifyResult());

        
        browser.findElement(By.id("header"));
    }
    
    public static class VerifyResult extends Inspection {

        private static final long serialVersionUID = 1L;

        @Inject
        AsciidoctorProcessor processor;
        
        @AfterServlet
        public void wasCalled() {
            HttpServletRequest request = RequestHolderFilter.currentInstance();
            Object result = request.getAttribute("result");
            Assert.assertNotNull(result);
            Assert.assertNotNull(processor);
            Assert.assertNotNull(processor.getDelegate());
            //System.out.println(result);
        }
    }
}
