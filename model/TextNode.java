package model;

import java.io.IOException;

import config.TemplateConfig;
import util.ParseUtil;

public class TextNode extends AbstractNode {

	protected String text;
	
	public TextNode(String text) throws IOException {
		if (text.contains("<")) {
			throw new IOException("unexpected char <");
		} else
		if (text.contains(">")) {
			throw new IOException("unexpected char >");
		}
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return text;
	}

	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		if (text.trim().length() > 0)
			directTextOutputBuffer.append(ParseUtil.dropWhitespaces(text) );
	}

}
