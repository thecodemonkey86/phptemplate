package model;

import io.PhpOutput;
import io.parser.HtmlParser;

import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;

public class PhpIfTag extends HtmlTag {

	public static final String TAG_NAME = "if" ;

	private boolean hasThenTag() {
		boolean result = false;
		for(AbstractNode n : childNodes) {
			if (n instanceof TextNode) {
				continue;
			} else if (n instanceof PhpThenTag) {
				result = true;
			} else if (result && !(n instanceof PhpElseTag)){
				throw new RuntimeException("syntax error");
			}
		}
		return result;
	}
	
	public PhpIfTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.PHP_TPL_NS);
	}

	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("if ").append(CodeUtil.parentheses(getAttrByName("cond").getStringValue()));
	
		if (childNodes != null && childNodes.size() > 0) { 
			if (!hasThenTag()) {
				out.append("{\n");
			}
			for(AbstractNode n:childNodes) {
				n.toPhp(out,directTextOutputBuffer,cfg);
			}
		} else {
			out.append("{\n");
		}
		PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		if (!hasThenTag()) {
			out.append("}\n");
		}
	}
	

}
