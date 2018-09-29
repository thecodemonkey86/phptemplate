package model;

import config.TemplateConfig;
import io.parser.HtmlParser;

public class PhpTemplateTag extends HtmlTag {

	public static final String TAG_NAME = "section" ;
	
	public PhpTemplateTag() {
		super(TAG_NAME);
		setNs(HtmlParser.PHP_TPL_NS);
	}
	
	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toPhp(out,directTextOutputBuffer,cfg);
			}
		}
	}

}
