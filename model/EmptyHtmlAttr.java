package model;

import config.TemplateConfig;

public class EmptyHtmlAttr extends HtmlAttr{

	public EmptyHtmlAttr(String name) {
		super(name, null, ' ');
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		directTextOutputBuffer.append(" ");
		directTextOutputBuffer.append(name) ;
	}
}
