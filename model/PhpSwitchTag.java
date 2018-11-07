package model;

import io.PhpOutput;
import io.parser.HtmlParser;

import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;

public class PhpSwitchTag extends HtmlTag {

	public static final String TAG_NAME = "switch" ;

	
	public PhpSwitchTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.PHP_TPL_NS);
	}

	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("switch ").append(CodeUtil.parentheses(getAttrByName("cond").getStringValue())).append("{\n");
	
		if (childNodes != null && childNodes.size() > 0) { 
			for(AbstractNode n:childNodes) {
				n.toPhp(out,directTextOutputBuffer,cfg);
			}
		}
		PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("}\n");
	}
	

}
