package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TplPreprocessorTag {

	
	public static final String PP_TEMPLATE = "<p:baseTemplate";
	public static final String PP_JAVASCRIPT = "<p:js";
	public static final String PP_CSS = "<p:css";
	public static final String PHP_TPl_INCLUDE_END_TAG = "</p:include";
	public static final String PP_USE = "<p:use";
	
	protected String includeLayoutTemplatePath;
	protected List<String> includeJs;
	protected List<String> includeCss;
	protected List<String> usePhpClasses;
	
	public TplPreprocessorTag(String code) throws IOException {
		this.includeJs = new ArrayList<>();
		this.includeCss = new ArrayList<>();
		this.usePhpClasses = new ArrayList<>();
		parse(code);
	}

	private void parse(String code) throws IOException {
		String[] lines = code.split("\\r?\\n");
		
		for(String l:lines) {
			String l0=l.trim();
			if (l0.startsWith(PP_TEMPLATE)) {
				int quotStart=l0.indexOf("src=\"",PP_TEMPLATE.length());
				int quotEnd=l0.indexOf('"',quotStart+5);
				
				if (quotStart > PP_TEMPLATE.length() && quotEnd > quotStart && includeLayoutTemplatePath == null) {
					includeLayoutTemplatePath = l0.substring(quotStart+5,quotEnd) ;
				} else {
					throw new IOException("syntax error");
				}
				
			} else if (l0.startsWith(PP_JAVASCRIPT)) {
				int quotStart=l0.indexOf("src=\"",PP_JAVASCRIPT.length());
				int quotEnd=l0.indexOf('"',quotStart+5);
				
				if (quotStart > PP_JAVASCRIPT.length() && quotEnd > quotStart) {
					includeJs.add( l0.substring(quotStart+5,quotEnd) );
				} else {
					throw new IOException("syntax error");
				}
			} else if (l0.startsWith(PP_CSS)) {
				int quotStart=l0.indexOf("src=\"",PP_CSS.length());
				int quotEnd=l0.indexOf('"',quotStart+5);
				
				if (quotStart > PP_CSS.length() && quotEnd > quotStart) {
					includeCss.add( l0.substring(quotStart+5,quotEnd) );
				} else {
					throw new IOException("syntax error");
				}
			} else if (l0.startsWith(PP_USE)) {
				int quotStart=l0.indexOf("class=\"",PP_USE.length());
				int quotEnd=l0.indexOf('"',quotStart+7);
				
				if (quotStart > PP_USE.length() && quotEnd > quotStart) {
					usePhpClasses.add( l0.substring(quotStart+7,quotEnd) );
				} else {
					throw new IOException("syntax error");
				}
			}
		}
	}

	public String getIncludeLayoutTemplatePath() {
		return includeLayoutTemplatePath;
	}
	
	public List<String> getIncludeCss() {
		return includeCss;
	}
	
	public List<String> getIncludeJs() {
		return includeJs;
	}
	
	public List<String> getUsePhpClasses() {
		return usePhpClasses;
	}

}
