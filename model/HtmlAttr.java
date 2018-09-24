package model;

import config.TemplateConfig;
import io.PhpOutput;

public class HtmlAttr implements ITemplateItem{

	String name;
	AttrValue value;
	char valueSeparatorChar;
	
	
	public HtmlAttr(String name, AttrValue value, char valueSeparatorChar) {
		super();
		this.name = name;
		this.value = value;
		this.valueSeparatorChar = valueSeparatorChar;
	}
	
	public String getName() {
		return name;
	}
	
	public AttrValue getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder(name);
		sb.append('=');
		sb.append(valueSeparatorChar);
		if (value!=null)
			sb.append(value.toString());
		sb.append(valueSeparatorChar);
		return sb.toString();
	}

	public char getValueSeparatorChar() {
		return valueSeparatorChar;
	}

	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		directTextOutputBuffer.append(" ");
		directTextOutputBuffer.append(name)
			.append('=')
			.append(valueSeparatorChar);
		
		for(IAttrValueElement e: value.getElements()) {
			if (e.stringOutput()) {
				directTextOutputBuffer.append(e.toString() );
			} else {
//				ParseUtil.addOutChunks(out, ParseUtil.dropWhitespaces(sb.toString()), HtmlParser.LINE_WIDTH);
//				sb = new StringBuilder();
				PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
				e.toPhp(out,directTextOutputBuffer,cfg);
			}
		}
		directTextOutputBuffer.append(valueSeparatorChar);
	}

	@Override
	public void walkTree(WalkTreeAction action, ParserResult parserResult) {
		// TODO Auto-generated method stub
		
	}

	public String getStringValue() {
		StringBuilder sb=new StringBuilder();
		for(IAttrValueElement e: value.getElements()) {
			sb.append(e.toString());
		}
		return sb.toString();
	}
}
