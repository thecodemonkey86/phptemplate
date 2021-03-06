package model;

import io.PhpOutput;
import io.parser.HtmlParser;

import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;

public class PhpForTag extends HtmlTag {

	public static final String TAG_NAME = "for" ;

	
	public PhpForTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.PHP_TPL_NS);
	}

	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg ) {
		PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		String def;
		if(hasAttr("each")) {
			def =  getAttrByName("each").getStringValue() + " as "+(hasAttr("key") ? getAttrByName("key").getStringValue() + " => " +getAttrByName("value").getStringValue() : getAttrByName("as").getStringValue() );
		} else {
			def = getAttrByName("def").getStringValue();
		}
		
	
		
		out.append(def.contains(";") ? "for " : "foreach ").append(CodeUtil.parentheses(def))
		.append("{\n");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toPhp(out,directTextOutputBuffer,cfg);
			}
		}
		PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append("}\n");
	}
	

}
