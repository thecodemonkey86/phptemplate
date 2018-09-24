package model;

import io.parser.HtmlParser;

import java.io.IOException;

import config.TemplateConfig;

public class PhpCommentTag extends HtmlTag {

	public static final String TAG_NAME = "comment" ;

	
	public PhpCommentTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.PHP_TPL_NS);
	}

	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
//		out.append("/*");
//		super.toCpp(out);
//		out.append("*/");
	}
	

}
