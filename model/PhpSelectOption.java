package model;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.PhpOutput;
import io.parser.HtmlParser;

public class PhpSelectOption extends HtmlTag{

	public static final String TAG_NAME = "option" ;
	private String selectedValueExpression;
	
	public void setSelectedValueExpression(String selectedValueExpression) {
		this.selectedValueExpression = selectedValueExpression;
	}
	
	public PhpSelectOption() {
		super(TAG_NAME);
		setNs(HtmlParser.PHP_TPL_NS);
	}
	
	@Override
	public void toPhp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg) {
		
		directTextOutputBuffer.append("<");
		directTextOutputBuffer.append(tagName);
		
		if (attrs!=null) {
			for(HtmlAttr a:attrs) {
				
				a.toPhp(out,directTextOutputBuffer,cfg);
				PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
				if(this.selectedValueExpression != null) {
					HtmlAttr optionValue = getAttrByName("value");
					out.append("if ").append(CodeUtil.parentheses(selectedValueExpression+" == '"+ optionValue.getStringValue()+'\'')).append(" {\n");
					CodeUtil.writeLine(out,"echo ' selected=\"selected\"';");
					out.append('\n');
					CodeUtil.writeLine(out, "}");
				}
			}
			
		}
		
		directTextOutputBuffer.append(">");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toPhp(out,directTextOutputBuffer,cfg);
			}
		}
		
		if (!isVoidTag(tagName) ) {
			directTextOutputBuffer.append("</").append(tagName).append('>');
		}
	}
}
