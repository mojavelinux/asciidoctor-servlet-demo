package org.asciidoctor.test.bean;

import java.io.File;

import javax.inject.Inject;

import org.asciidoctor.bean.AsciidoctorProcessor;
import org.asciidoctor.servlet.AsciidoctorServlet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AsciidoctorProcessorInContainerTest {
    @Inject
    AsciidoctorProcessor processor;

    @Deployment
    public static WebArchive createDeployment() {
        MavenDependencyResolver resolver = DependencyResolvers
                .use(MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
        File[] deps = resolver
                .artifacts("org.asciidoctor:asciidoctor-java-integration")
                .exclusion("com.beust:jcommander:*")
                .resolveAsFiles();

        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
                .addClass(AsciidoctorServlet.class)
                .addClass(AsciidoctorProcessor.class).addAsLibraries(deps)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(war.toString(true));
        return war;
    }

    @Test
    public void testIsDeployed() {
        Assert.assertNotNull(processor);
        String result = processor.renderAsDocument(
                "http://asciidoc.org[AsciiDoc]", null);
        System.out.println(result);
        Assert.assertNotNull(result);
        Assert.assertTrue(result
                .contains("<a href=\"http://asciidoc.org\">AsciiDoc</a>"));
    }
}
