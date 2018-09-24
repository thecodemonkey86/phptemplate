package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ParserResult {

	protected List<TplPreprocessorTag> preprocessorTags;
	protected List<PhpTemplateTag> templateTags; // if layout template referencing multiple templates
	protected Template simpleTemplate;  // if simple template
	
	protected ParserResult parentParserResult;
	
	public ParserResult() {
		preprocessorTags = new ArrayList<>();
		templateTags = null;
	}
	
	public void addPreprocessorTag(TplPreprocessorTag ppTag) {
		this.preprocessorTags.add(ppTag);
	}
	
	public List<TplPreprocessorTag> getPreprocessorTags() {
		return preprocessorTags;
	}
	
	public List<PhpTemplateTag> getTemplateTags() {
		return templateTags;
	}
	
	public void addTemplateTag(PhpTemplateTag tpl) throws IOException {
		if (simpleTemplate != null) {
			throw new IOException("illegal state");
		}
		this.templateTags.add(tpl);
	}
	
	public void setSimpleTemplate(Template simpleTemplate) throws IOException {
		if (templateTags != null) {
			throw new IOException("illegal state");
		}
		this.simpleTemplate = simpleTemplate;
	}
	
	public Template getSimpleTemplate() {
		return simpleTemplate;
	}
	
	public boolean isSimpleTemplate() {
		return simpleTemplate != null;
	}
	
	public boolean isMultiTemplate() {
		return templateTags != null;
	}

	public void setMultiTemplate() throws IOException {
		if (simpleTemplate !=null ) {
			throw new IOException("illegal state");
		}
		if (this.templateTags == null) {
			this.templateTags = new ArrayList<>();
		}
		
	}
	
	public void addNode(AbstractNode node) throws IOException {
		if (isSimpleTemplate()) {
			this.simpleTemplate.addTag(node);
		} else {
			if (this.templateTags == null || this.templateTags.size() == 0) {
				throw new IOException("illegal state");
			}
			this.templateTags.get(this.templateTags.size()-1).addChildNode(node);
		}
	}
	
	public void setParentParserResult(ParserResult parentParserResult) {
		this.parentParserResult = parentParserResult;
	}
	
	public ParserResult getParentParserResult() {
		return parentParserResult;
	}

	public PhpTemplateTag getTemplateByName(String name) throws IOException {
		for(PhpTemplateTag t : templateTags) {
			if (t.getAttrByName("name").getStringValue().equals(name)) {
				return t;
			}
		}
		throw new IOException("no such attribute: "+name);
	}

	
//	public void addNode(AbstractNode tag) throws IOException {
//		if (templateTags != null) {
//			throw new IOException("illegal state");
//		}
//		if (this.nodes == null) {
//			this.nodes = new ArrayList<>();
//		}
//		this.nodes.add(tag);
//	}
//
//	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
//		if(isSimpleTemplate()) {
//			simpleTemplate.toPhp(out,directTextOutputBuffer,cfg);
//			PhpOutput.clearDirectTextOutputBuffer(out, directTextOutputBuffer, cfg);
//		} else {
//			for (Template templateTag : templateTags) {
//				
//			}
//		}
//		
//	}

	public LinkedHashSet<String> getAllCssIncludes() {
		LinkedHashSet<String> includeCss = null;
		if(parentParserResult == null) {
			includeCss = new LinkedHashSet<>();
		} else {
			includeCss = parentParserResult.getAllCssIncludes();
		}
		for(TplPreprocessorTag t : preprocessorTags) {
			for(String css : t.getIncludeCss()) {
				includeCss.add(css);
			}
		}
		return includeCss;
	}
	
	public LinkedHashSet<String> getAllJsIncludes() {
		LinkedHashSet<String> includeJs = null;
		if(parentParserResult == null) {
			includeJs = new LinkedHashSet<>();
		} else {
			includeJs = parentParserResult.getAllJsIncludes();
		}
		for(TplPreprocessorTag t : preprocessorTags) {
			for(String js : t.getIncludeJs()) {
				includeJs.add(js);
			}
		}
		return includeJs;
	}
	
	public LinkedHashSet<String> getAllUsePhpClassesIncludes() {
		LinkedHashSet<String> usePhpClasses = null;
		if(parentParserResult == null) {
			usePhpClasses = new LinkedHashSet<>();
		} else {
			usePhpClasses = parentParserResult.getAllUsePhpClassesIncludes();
		}
		for(TplPreprocessorTag t : preprocessorTags) {
			for(String use : t.getUsePhpClasses()) {
				usePhpClasses.add(use);
			}
		}
		return usePhpClasses;
	}
}
