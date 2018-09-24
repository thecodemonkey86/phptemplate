package model;

import java.io.IOException;

import config.TemplateConfig;
import io.PhpOutput;

public class RawOutputSection extends AbstractNode implements IAttrValueElement {

	String expression;
	
	public RawOutputSection(String expression) throws IOException {
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
		return String.format("{{%s}}", expression) ;
	}
	

	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		out.append( String.format("echo %s;\n",expression));
		
	}

	@Override
	public boolean stringOutput() {
		return false;
	}
}
