package model;

import java.io.IOException;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.PhpOutput;
import io.parser.HtmlParser;

public class PhpSelect extends HtmlTag{
	public static final String TAG_NAME = "select" ;
	public PhpSelect() {
		super(TAG_NAME);
		setNs(HtmlParser.PHP_TPL_NS);
	}
	
	@Override
	public void toPhp(StringBuilder out, StringBuilder directTextOutputBuffer, TemplateConfig cfg) {
		directTextOutputBuffer.append("<");
		directTextOutputBuffer.append(tagName);
		
		if (attrs!=null) {
			for(HtmlAttr a:attrs) {
				if(!a.getName().equals("value") && !a.getName().equals("options")) {
					a.toPhp(out,directTextOutputBuffer,cfg);	
				}
				
			}
		}
		
		directTextOutputBuffer.append(">");
		HtmlAttr value = getAttrByName("value");
		PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		if(childNodes != null && !childNodes.isEmpty()) {
			
			for (AbstractNode node : childNodes) {
				try {
					node.walkTree(new WalkTreeAction() {
						
						@Override
						public void currentNode(AbstractNode node, ParserResult parserResult) throws IOException {
							if(node instanceof PhpSelectOption) {
								PhpSelectOption opt = (PhpSelectOption) node;
								opt.setSelectedValueExpression(value.getStringValue());
							}
							
						}
					}, null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			for (AbstractNode node : childNodes) {
				if(node instanceof TextNode) {
					continue;
				}
				node.toPhp(out, directTextOutputBuffer, cfg);
			}
			
		
		} else {
			HtmlAttr options = getAttrByName("options");
			out.append("foreach ").append(CodeUtil.parentheses(options.getStringValue()+" as $__key => $__value")).append("{\n");
			
			directTextOutputBuffer.append("<option name=\"");
			PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
			CodeUtil.writeLine(out,"echo $__key;");
			CodeUtil.writeLine(out,"echo '\"';");
			out.append("if ").append(CodeUtil.parentheses(value.getStringValue()+" == $__key")).append("{\n");
			CodeUtil.writeLine(out,"echo ' selected=\"selected\"';");
			CodeUtil.writeLine(out, "}");
			CodeUtil.writeLine(out,"echo '>';");
			CodeUtil.writeLine(out,"echo $__value;");
			CodeUtil.writeLine(out,"echo '</option>';");
			out.append('\n');
			CodeUtil.writeLine(out, "}");
		}
		
		directTextOutputBuffer.append("</").append(tagName).append('>');
		
		
		
		
	}

}
