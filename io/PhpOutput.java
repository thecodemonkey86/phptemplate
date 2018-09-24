package io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import codegen.CodeUtil;
import config.TemplateConfig;
import io.parser.HtmlParser;
import settings.Settings;
import util.Pair;
import util.ParseUtil;
import util.StringUtil;
import util.exception.CancelException;
import model.ParserResult;

public class PhpOutput {
	
	/*public static final String BEGIN_COMPILED_TEMPLATE_INLINE_CSS = "//BEGIN_COMPILED_TEMPLATE_INLINE_CSS//";
	public static final String END_COMPILED_TEMPLATE_INLINE_CSS = "//END_COMPILED_TEMPLATE_INLINE_CSS//";
	public static final String BEGIN_COMPILED_TEMPLATE_INLINE_JS = "//BEGIN_COMPILED_TEMPLATE_INLINE_JS//";
	public static final String END_COMPILED_TEMPLATE_INLINE_JS = "//END_COMPILED_TEMPLATE_INLINE_JS//";
	public static final String BEGIN_COMPILED_TEMPLATE = "//BEGIN_COMPILED_TEMPLATE//";
	public static final String END_COMPILED_TEMPLATE = "//END_COMPILED_TEMPLATE//";
	*/
	public static final Charset UTF8 = Charset.forName("UTF-8");
	
	protected static String getJsOrCssMethodName(String include) throws IOException {
		/*int start = 0;
		
		if (jsInclude.startsWith("https://")) {
			start = "https://".length();
		} else if  (jsInclude.startsWith("http://")) {
			start = "http://".length();
		}*/
		
		int start = include.lastIndexOf('/');
		if(start == -1) {
			start = include.lastIndexOf('\\');
		}
		if(start == -1) {
			if (include.startsWith("https://")) {
				start = "https://".length();
			} else if  (include.startsWith("http://")) {
				start = "http://".length();
			} else {
				start=0;
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i = start; i < include.length(); i++) {
			if (include.charAt(i) >= 'a' && include.charAt(i) <= 'z' ||
					include.charAt(i) >= 'A' && include.charAt(i) <= 'Z' ||
					include.charAt(i) >= '0' && include.charAt(i) <= '9'
					) {
				sb.append(include.charAt(i));
			} else if (include.charAt(i) == '.') {
				sb.append("dot");
			}
		}
		if (sb.length() <= 256)
			return sb.toString();
		else {
			try {
				MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
				return "js"+ DatatypeConverter.printHexBinary( sha256.digest(sb.toString().getBytes()));
			} catch (Exception e) {
				throw new IOException(e);
			}
			
		}
	}
	
	/*protected static String insertCss(String cppCode, String clsName, Set<String> inlineCss) throws IOException, CancelException {
		StringBuilder sbInlineCss = new StringBuilder();
		for(String cssSrc : inlineCss) {
			String css = new String(Files.readAllBytes(CssJsProcessor.loadCss(cssSrc)), UTF8 );
			ParseUtil.addOutChunks(sbInlineCss, css, Settings.LINE_WIDTH);
			sbInlineCss.append('\n');
		}
		String templateClass = cppCode;
		int markerRenderInlineCssBeginIndex = templateClass.indexOf(BEGIN_COMPILED_TEMPLATE_INLINE_CSS)+BEGIN_COMPILED_TEMPLATE_INLINE_CSS.length();
		int markerRenderInlineCssEndIndex = templateClass.indexOf(END_COMPILED_TEMPLATE_INLINE_CSS);
		
		
		if (markerRenderInlineCssBeginIndex > -1 && markerRenderInlineCssEndIndex > -1) {
			templateClass = templateClass.substring(0,markerRenderInlineCssBeginIndex)+"\nprotected function renderInlineCss() {\n" + sbInlineCss.toString() +"\n}\n"+ templateClass.substring(markerRenderInlineCssEndIndex);
			
		}
		return templateClass;
	}
	
	protected static String insertTemplate(String cppCode, String clsName, ParserResult layoutParserResult) {
		StringBuilder out = new StringBuilder();
		StringBuilder directTextOutputBuffer = new StringBuilder();
		layoutParserResult.toCpp(out,directTextOutputBuffer);
		String templateClass = cppCode;
		int markerRenderInlineCssBeginIndex = templateClass.indexOf(BEGIN_COMPILED_TEMPLATE)+BEGIN_COMPILED_TEMPLATE.length();
		int markerRenderInlineCssEndIndex = templateClass.indexOf(END_COMPILED_TEMPLATE);
		
		
		if (markerRenderInlineCssBeginIndex > -1 && markerRenderInlineCssEndIndex > -1) {
			templateClass = templateClass.substring(0,markerRenderInlineCssBeginIndex)+"\nprotected function renderBody(ViewData" + clsName.substring(0,clsName.length()-4) +" $data) {\n" + out.toString() +"\n}\n"+ templateClass.substring(markerRenderInlineCssEndIndex);
			
		}
		return templateClass;
	}*/
	
	protected static String getJsAsPhp(String jsSrc) throws IOException, CancelException {
		StringBuilder sbInlineJs = new StringBuilder();
		String js = new String(Files.readAllBytes(CssJsProcessor.loadJs(jsSrc)), UTF8 );
		addOutChunks(sbInlineJs, js, Settings.LINE_WIDTH,null);
		sbInlineJs.append('\n');
		return sbInlineJs.toString();
	}
	
	protected static String getCssAsPhp(Set<String> inlineCss) throws IOException, CancelException {
		StringBuilder sbInlineCss = new StringBuilder();
		for(String cssSrc : inlineCss) {
			String css = CssJsProcessor.getInlineCss(cssSrc);;
			addOutChunks(sbInlineCss, css, Settings.LINE_WIDTH,null);
			sbInlineCss.append('\n');
		}
		return sbInlineCss.toString();
	}
	
	public static void writeCompiledTemplateFile(ParserResult layoutResult,ParserResult result, Path directory, String namespace, String clsName,TemplateConfig cfg) throws IOException, CancelException {
		if (!Files.exists(directory))
			Files.createDirectories(directory);
		
		StringBuilder sb = new StringBuilder();
		CodeUtil.writeLine(sb, "<?php");
		CodeUtil.writeLine(sb, "namespace " + namespace +";");
		
		for(String use : result.getAllUsePhpClassesIncludes()) {
			CodeUtil.writeLine(sb, "use " + use +";");
		}
		
		CodeUtil.writeLine(sb, "class "+clsName +"CompiledTemplate {");
		
		LinkedHashSet<String> inlineJs = result.getAllJsIncludes();
		CodeUtil.writeLine(sb, "public static function renderInlineJs() {");
		for(String jsSrc : inlineJs) {
			CodeUtil.writeLine(sb, "InlineJsRenderer::"+getJsOrCssMethodName(jsSrc)+"();");
		}
		CodeUtil.writeLine(sb,"}");
		LinkedHashSet<String> inlineCss = result.getAllCssIncludes();
		CodeUtil.writeLine(sb, "public static function renderInlineCss() {");
		for(String CssSrc : inlineCss) {
			CodeUtil.writeLine(sb, "InlineCssRenderer::"+getJsOrCssMethodName(CssSrc)+"();");
		}
		CodeUtil.writeLine(sb,"}");
		StringBuilder out = new StringBuilder();
		StringBuilder directTextOutputBuffer = new StringBuilder();
		layoutResult.getSimpleTemplate().toPhp(out,directTextOutputBuffer,cfg);
		CodeUtil.writeLine(sb, "public static function renderBody($data) {");
		CodeUtil.writeLine(sb,out.toString());
		CodeUtil.writeLine(sb,"}");
		
		
		CodeUtil.writeLine(sb,"}");
		
			
		
		Files.write(directory.resolve(clsName+ "CompiledTemplate.php"), sb.toString().getBytes(UTF8), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		
	}
	
	/*protected static String insertJs(String cppCode, String clsName, Set<String> inlineJs) throws IOException, CancelException {
		
		String templateClass = cppCode;
		int markerRenderInlineCssBeginIndex = templateClass.indexOf(BEGIN_COMPILED_TEMPLATE_INLINE_JS)+BEGIN_COMPILED_TEMPLATE_INLINE_JS.length();
		int markerRenderInlineCssEndIndex = templateClass.indexOf(END_COMPILED_TEMPLATE_INLINE_JS);
		
		
		if (markerRenderInlineCssBeginIndex > -1 && markerRenderInlineCssEndIndex > -1) {
			templateClass = templateClass.substring(0,markerRenderInlineCssBeginIndex)+"\nprotected function renderInlineJs() {\n" + getJsAsPhp(inlineJs) +"\n}\n"+ templateClass.substring(markerRenderInlineCssEndIndex);
			
		}
		return templateClass;
	}
	
	protected static String insertJsAsMethodCall(String cppCode, String clsName, Set<String> inlineJs) throws IOException {
		String templateClass = cppCode;
		int markerRenderInlineCssBeginIndex = templateClass.indexOf(BEGIN_COMPILED_TEMPLATE_INLINE_JS)+BEGIN_COMPILED_TEMPLATE_INLINE_JS.length();
		int markerRenderInlineCssEndIndex = templateClass.indexOf(END_COMPILED_TEMPLATE_INLINE_JS);
		StringBuilder sbInlineJs = new StringBuilder();
		for(String jsSrc : inlineJs) {
			sbInlineJs.append("InlineJsRenderer::")
			.append(getJsOrCssMethodName(jsSrc)).append("();");
			sbInlineJs.append('\n');
		}
		
		if (markerRenderInlineCssBeginIndex > -1 && markerRenderInlineCssEndIndex > -1) {
			templateClass = templateClass.substring(0,markerRenderInlineCssBeginIndex)+"\nfunction renderInlineJs() {\n" + sbInlineJs.toString() +"\n}\n"+ templateClass.substring(markerRenderInlineCssEndIndex);
			
		}
		return templateClass;
	}
	*/
	public static void writeJsPhpFile(Path directory, String namespace, Set<String> inlineJs) throws IOException, CancelException {
		
		if (!Files.exists(directory))
			Files.createDirectories(directory);
		
		StringBuilder sb = new StringBuilder();
		CodeUtil.writeLine(sb, "<?php");
		CodeUtil.writeLine(sb, "namespace " + namespace +";");
		CodeUtil.writeLine(sb, "class InlineJsRenderer {");
		

		for(String jsSrc : inlineJs) {
			CodeUtil.writeLine(sb, "public static function " + getJsOrCssMethodName(jsSrc) + "() {");
			CodeUtil.writeLine(sb, getJsAsPhp(jsSrc));
			CodeUtil.writeLine(sb,"}");
		}
		
		CodeUtil.writeLine(sb,"}");
		
			
		
		Files.write(directory.resolve("InlineJsRenderer.php"), sb.toString().getBytes(UTF8), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		
	}
	
	public static void writeCssPhpFile(Path directory, String namespace, Set<String> inlineCss) throws IOException, CancelException {
		
		if (!Files.exists(directory))
			Files.createDirectories(directory);
		
		StringBuilder sb = new StringBuilder();
		CodeUtil.writeLine(sb, "<?php");
		CodeUtil.writeLine(sb, "namespace " + namespace +";");
		CodeUtil.writeLine(sb, "class InlineCssRenderer {");
		

		for(String cssSrc : inlineCss) {
			CodeUtil.writeLine(sb, "public static function " + getJsOrCssMethodName(cssSrc) + "() {");
			CodeUtil.writeLine(sb, getCssAsPhp(inlineCss));
			CodeUtil.writeLine(sb,"}");
		}
		
		CodeUtil.writeLine(sb,"}");
		
			
		
		Files.write(directory.resolve("InlineCssRenderer.php"), sb.toString().getBytes(UTF8), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		
	}
	
	public static String getPhpEscapedString(String s) {
		return StringUtil.replaceAll( s.replace( "\\", "\\\\").replace("\'", "\\\'").replace("\"", "\\\""),Arrays.asList(
				new Pair<String, String>("\r", "\\r"),
				new Pair<String, String>("\n", "\\n"),
				new Pair<String, String>("\t", "\\t")
			));
	}
	
	public static void addOutChunks(StringBuilder out,String outLine,int lineWidth,TemplateConfig cfg ) {
		if (outLine.length()>lineWidth) {
			for(int i=0;i<=outLine.length()-lineWidth;i+=lineWidth) {
				String phpEscapedString = getPhpEscapedString(outLine.substring(i,i+lineWidth));
				
				/*if(cfg != null && cfg.isRenderToString()) {
					if(phpEscapedString.contains("\\")) {
						out.append(cfg.getRenderToStringVariableName()).append(" .= \""+ phpEscapedString.replace("$", "\\$")  + "\";");
					} else {
						out.append(cfg.getRenderToStringVariableName()).append(" .= \'"+ phpEscapedString  + "\';");	
					}
				} else {*/
					if(phpEscapedString.contains("\\")) {
						out.append("echo \""+ phpEscapedString.replace("$", "\\$")  + "\";");
					} else {
						out.append("echo \'"+ phpEscapedString  + "\';");	
					}
				//}
				out.append('\n');
			}
			if (outLine.length() % lineWidth != 0) {
				String phpEscapedString = getPhpEscapedString(outLine.substring((outLine.length()/lineWidth)*lineWidth));
				
				/*if(cfg != null && cfg.isRenderToString()) {
					if(phpEscapedString.contains("\\")) {
						out.append(cfg.getRenderToStringVariableName()).append(" .= \""+ phpEscapedString.replace("$", "\\$")  + "\";");
					} else {
						out.append(cfg.getRenderToStringVariableName()).append(" .= \'"+ phpEscapedString  + "\';");					
					}
				} else {*/
					if(phpEscapedString.contains("\\")) {
						out.append("echo \""+ phpEscapedString.replace("$", "\\$")  + "\";");
					} else {
						out.append("echo \'"+ phpEscapedString  + "\';");					
					}
				//}
				out.append('\n');
			}
		} else if (!outLine.isEmpty()){
			String phpEscapedString = getPhpEscapedString(outLine);
			/*if(cfg != null && cfg.isRenderToString()) {
				if(phpEscapedString.contains("\\")) {
					out.append(cfg.getRenderToStringVariableName()).append(" = \""+ phpEscapedString.replace("$", "\\$")  + "\";");
				} else {
					out.append(cfg.getRenderToStringVariableName()).append(" = \'"+ phpEscapedString  + "\';");
				}
			} else {*/
				if(phpEscapedString.contains("\\")) {
					out.append("echo \""+ phpEscapedString.replace("$", "\\$")  + "\";");
				} else {
					out.append("echo \'"+ phpEscapedString  + "\';");
				}
			//}
			
			
			out.append('\n');
		}
		
	}
	
	public static void clearDirectTextOutputBuffer(StringBuilder out, StringBuilder buffer, TemplateConfig cfg) {
		addOutChunks(out, ParseUtil.dropWhitespaces(buffer.toString()), HtmlParser.LINE_WIDTH,cfg);
		buffer.setLength(0);
	}
	
	/*public static void insertCode(String clsName, Path phpFile, ParserResult layoutParserResult,  Set<String> includeCss,  Set<String> includeJs) throws IOException, CancelException {
		String cppCode = new String(Files.readAllBytes(phpFile), UTF8);
		
		
		cppCode = insertCss(cppCode, clsName, includeCss);
		cppCode = insertJsAsMethodCall(cppCode, clsName, includeJs);
		cppCode = insertTemplate(cppCode, clsName, layoutParserResult);
		
		
		
		Files.write(phpFile,cppCode.getBytes(UTF8)  , StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
	}*/
}
