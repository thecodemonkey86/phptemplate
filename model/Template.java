package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import config.TemplateConfig;
import io.PhpOutput;

public class Template implements ITemplateItem {
	protected List<ITemplateItem> tags;
	
	public Template() {
		tags = new ArrayList<>();
	}
	
	public void addTag(ITemplateItem tag) {
		this.tags.add(tag);
	}
	
//	public void replaceRenderTags(Template tmpl, String name) throws IOException {
//		for(int i=0;i<tags.size();i++) {
//			if (tags.get(i) instanceof CppRenderTemplateTag) {
//				if ( ((CppRenderTemplateTag)tags.get(i)).getAttrByName("name").equals(name)) {
//					tags.set(i, tmpl);
//					return;
//				}
//				//
//			}
//		}
//	}
	
	public void walkTree(WalkTreeAction action,ParserResult parserResult) throws IOException {
		if (this.tags != null) {
			for(ITemplateItem n:tags) {
				n.walkTree(action, parserResult);
			}
		}
	}

	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		if (this.tags != null) {
			for(ITemplateItem n:tags) {
				n.toPhp(out,directTextOutputBuffer,cfg);
			}
			PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
		}
		
	}
}
