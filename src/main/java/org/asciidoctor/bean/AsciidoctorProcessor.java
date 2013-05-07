package org.asciidoctor.bean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

@ApplicationScoped
public class AsciidoctorProcessor {
	private Asciidoctor delegate;
	
    // tag::render[]
	public String renderAsDocument(String source, String baseDir) {
		return delegate.render(source, OptionsBuilder.options()
				.safe(SafeMode.SAFE).backend("html5").headerFooter(true).eruby("erubis")
				.option("base_dir", baseDir)
				.attributes(AttributesBuilder.attributes()
						.attribute("icons!", "")
						.attribute("copycss!", "").asMap()).asMap());
	}
    // end::render[]
	
	public Asciidoctor getDelegate() {
		return delegate;
	}
	
	@PostConstruct
	public void init() {
		delegate = Asciidoctor.Factory.create();
	}
}
