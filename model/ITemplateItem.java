package model;

import java.io.IOException;

import config.TemplateConfig;

public interface ITemplateItem {
	void toPhp(StringBuilder out, StringBuilder directTextOutputBuffer,TemplateConfig cfg); ;
	void walkTree(WalkTreeAction action,ParserResult parserResult) throws IOException;
}
