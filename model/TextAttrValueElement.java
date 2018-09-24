package model;

import config.TemplateConfig;
import io.PhpOutput;
import io.parser.HtmlParser;
import util.ParseUtil;

public class TextAttrValueElement implements IAttrValueElement{
	String chars;
	
	public TextAttrValueElement(String chars) {
		this.chars = chars;
	}
	
	@Override
	public String toString() {
		return chars;
	}

	@Override
	public boolean stringOutput() {
		return true;
	}

	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		PhpOutput.addOutChunks(out, ParseUtil.dropWhitespaces(chars), HtmlParser.LINE_WIDTH,cfg);
	}

	@Override
	public void walkTree(WalkTreeAction action, ParserResult parserResult) {
		// TODO Auto-generated method stub
		
	}

}
