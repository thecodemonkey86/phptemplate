package model;

import java.io.IOException;

import config.TemplateConfig;
import io.PhpOutput;

public class HtmlEscapedOutputSection extends AbstractNode implements IAttrValueElement {

	String expression;
	
	public HtmlEscapedOutputSection(String expression) throws IOException {
		this.expression = expression.trim();
		if (this.expression.length() == 0) {
			throw new IOException("syntax error");
		}
	}
	
	@Override
	public void addChildNode(AbstractNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return String.format("{%s}", expression) ;
	}
	

	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		//if(cfg.isRenderToString()) {
		//	out.append( String.format("echo htmlentities(%s,ENT_COMPAT | ENT_HTML5);\n",expression));
		//} else {
			out.append( String.format("echo htmlentities(%s,ENT_COMPAT | ENT_HTML5);\n",expression));
		//}
		
	}

	@Override
	public boolean stringOutput() {
		return false;
	}
}
