package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import config.TemplateConfig;

public abstract class AbstractNode implements ITemplateItem{

	protected List<AbstractNode> childNodes;
	
	public AbstractNode() {
		this.childNodes = null;
	}
	

	public void addChildNode(AbstractNode node) {
		if (this.childNodes == null) {
			this.childNodes = new ArrayList<>();
		}
		if (!(node instanceof PhpCommentTag)) {
			this.childNodes.add(node);
		}
	}

	public List<AbstractNode> getChildNodes() {
		return childNodes;
	}
	
	/**
	 * @param out
	 * @throws IOException 
	 */
	public abstract void toPhp(StringBuilder out, StringBuilder directTextOutputBuffer,TemplateConfig cfg); ;

	public void walkTree(WalkTreeAction action,ParserResult parserResult) throws IOException {
		action.currentNode(this, parserResult);
		if (this.childNodes != null) {
			for(AbstractNode n:childNodes) {
				n.walkTree(action, parserResult);
			}
		}
	}
}
