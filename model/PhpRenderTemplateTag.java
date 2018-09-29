package model;

import io.parser.HtmlParser;

import java.io.IOException;

import config.TemplateConfig;

public class PhpRenderTemplateTag extends HtmlTag {

	public static final String TAG_NAME = "renderSection" ;

	PhpTemplateTag renderTmpl;
	
	public PhpRenderTemplateTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.PHP_TPL_NS);
	}

	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		if (this.renderTmpl == null) {
			throw new RuntimeException("illegal state");
		}
		renderTmpl.toPhp(out,directTextOutputBuffer,cfg);
	}
	
	public void setRenderTmpl(PhpTemplateTag renderTmpl) {
		this.renderTmpl = renderTmpl;
	}

}
