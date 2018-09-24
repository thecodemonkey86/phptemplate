package model;

import java.io.IOException;

public interface WalkTreeAction {
	void currentNode(AbstractNode node, ParserResult parserResult) throws IOException;
}
