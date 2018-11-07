package model;

import io.PhpOutput;
import io.parser.HtmlParser;

import java.io.IOException;

import config.TemplateConfig;

public class PhpCaseTag extends HtmlTag {

	public static final String TAG_NAME = "case" ;
	
	public PhpCaseTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.PHP_TPL_NS);
	}

	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("case ").append(getAttrByName("value").getStringValue()).append(":\n");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toPhp(out,directTextOutputBuffer,cfg);
			}
		}
		PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("break;\n");
	}
	

}
