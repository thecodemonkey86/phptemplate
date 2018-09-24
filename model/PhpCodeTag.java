package model;

import config.TemplateConfig;
import io.PhpOutput;
import io.parser.HtmlParser;

public class PhpCodeTag extends AbstractNode implements IAttrValueElement {
	protected String code;
	
	public PhpCodeTag(String code) {
		setCode(code);
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(HtmlParser.PHP_CODE_TAG);
		sb.append(' ')
			.append(this.code)
			.append(HtmlParser.PHP_CODE_END_TAG);
		return sb.toString();
	}

	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		String[] lines = code.split("\n");
		StringBuilder sbTrimmed = new StringBuilder(code.length());
		if(lines.length > 0) {
			for(int i = 0; i < lines.length; i++) {
				String trim = lines[i].trim();
				if (trim.length() > 0) {
					sbTrimmed.append(trim).append('\n');
				}
			}			
		}
		PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		String trimmed = sbTrimmed.toString().trim();
		if(trimmed.length()>0)
			out.append(trimmed).append('\n');
	}

	@Override
	public boolean stringOutput() {
		return false;
	}

}
