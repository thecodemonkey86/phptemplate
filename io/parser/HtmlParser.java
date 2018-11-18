package io.parser;

import java.io.IOException;

import settings.Settings;
import util.Pair;
import util.ParseUtil;
import model.AbstractNode;
import model.AttrValue;
import model.EmptyHtmlAttr;
import model.PhpCodeTag;
import model.PhpCommentTag;
import model.PhpDefaultCaseTag;
import model.PhpElseTag;
import model.PhpForTag;
import model.PhpIfTag;
import model.PhpRenderSubtemplateTag;
import model.PhpRenderTemplateTag;
import model.PhpSelect;
import model.PhpSelectOption;
import model.PhpSwitchTag;
import model.PhpTemplateTag;
import model.PhpThenTag;
import model.HtmlAttr;
import model.HtmlBr;
import model.HtmlTag;
import model.ParserResult;
import model.PhpCaseTag;
import model.HtmlEscapedOutputSection;
import model.RawOutputSection;
import model.Template;
import model.TextAttrValueElement;
import model.TextNode;
import model.TplPreprocessorTag;

public class HtmlParser {

	public final static String PHP_CODE_TAG = "<?php";
	public final static String PHP_CODE_END_TAG = "?>";
	public final static String HTML_END_TAG = "</";
	public final static char PHP_INLINE_END = '}';
	public final static char PHP_INLINE_START = '{';
	public final static String PHP_INLINE_RAW_END = "}}";
	public final static String PHP_INLINE_RAW_START = "{{";
	public static final int LINE_WIDTH = Settings.LINE_WIDTH;
	public final static String PHP_TPL_NS = "p";
	public final static String HTML_COMMENT_START = "<!--";
	public final static String HTML_COMMENT_END = "-->";

	protected String html;
	protected int currentPos;
	protected ParserResult result;
	
	public ParserResult parse(String html) throws IOException {
		this.currentPos = 0;
		this.html = html;
		this.result=new ParserResult();
		parseRoot();
		
		return this.result;
	}
	
	protected boolean atEnd() {
		return currentPos >= html.length();
	}
	
	protected void next()  {
		currentPos++;
	}
	protected void next(int offset)  {
		currentPos+=offset;
	}
	protected void setPos(int pos) {
		currentPos = pos;
	}
	protected char currChar() throws IOException {
		if(atEnd()) {
			throw new IOException("syntax error");
		}
		return html.charAt(currentPos);
	}
	protected boolean currSubstrEquals(String substr) throws IOException {
		if(atEnd()) {
			throw new IOException("syntax error");
		}
		return html.regionMatches(currentPos, substr, 0, substr.length());
	}
	
	
	
	private void addTextNode(int startIndex,ParserResult result) throws IOException {
		String text = html.substring(startIndex,currentPos);
		
		if (!text.isEmpty())
			result.addNode(new TextNode(text));
		
	}
	
	private void addTextNodeToEnd(int startIndex,ParserResult result) throws IOException {
		String text = html.substring(startIndex);
		
		if (!text.isEmpty())
			result.addNode(new TextNode(text));
		
	}
	
	private void addTextNode(HtmlTag tag, int startIndex) throws IOException {
		String text = html.substring(startIndex,currentPos);
		if (!text.isEmpty())
			tag.addChildNode(new TextNode(html.substring(startIndex,currentPos)));
		
	}
	
	private void checkInitSimpleTemplate() throws IOException {
		if(!result.isSimpleTemplate() && !result.isMultiTemplate()) {
			result.setSimpleTemplate(new Template());
		} else if(result.isMultiTemplate()) {
			throw new IOException();
		}
	}
	
	protected void parseRoot() throws IOException {
		int startIndex = 0;
		while(!atEnd()) {
			if(currSubstrEquals("<p:include")) {
				addTextNode(startIndex, result);
				result.addPreprocessorTag(parsePhpIncludeTag());
				startIndex = currentPos; 
			} else 	if (currSubstrEquals(HtmlParser.PHP_CODE_TAG)) {
				
				//next();
				/*if(isCurrTagPhpBeginPreprocessor()) {
					addTextNode(startIndex, result);
					result.addPreprocessorTag(parsePhpPreprocessorCodeSection());
					startIndex = currentPos; 
				} else {*/
					checkInitSimpleTemplate();
					addTextNode(startIndex, result);
					result.addNode(parseCodeTag());
					startIndex = currentPos+HtmlParser.PHP_CODE_END_TAG.length(); 
				//}
			
			} else if(currSubstrEquals( HtmlParser.PHP_INLINE_RAW_START )) {
				checkInitSimpleTemplate();
				addTextNode( startIndex, result);
				result.addNode(parseRawOutputSection());
				
				startIndex = currentPos+PHP_INLINE_RAW_START.length();			
			} else if(currChar() == HtmlParser.PHP_INLINE_START ) {
				checkInitSimpleTemplate();
				addTextNode( startIndex, result);
				result.addNode(parseHtmlEscapedOutputSection());
				
				startIndex = currentPos+1;			
			} else if(currChar() == '<') {
				if (!currSubstrEquals(String.format("<%s:%s", HtmlParser.PHP_TPL_NS, PhpTemplateTag.TAG_NAME))) {
					if (!checkSkipHtmlComment() ) {
						checkInitSimpleTemplate();
						addTextNode(startIndex, result);
						next();
						result.addNode(parseNode());
					}
				} else {
					String text = html.substring(startIndex,currentPos).trim();
					
					if (!text.isEmpty()) {
						throw new IOException("invalid characters");
					}
					result.setMultiTemplate();
					next();
					AbstractNode node =  parseNode();
					if (!(node instanceof PhpTemplateTag)) {
						throw new IOException("expected cpp:template tag");
					}
					result.addTemplateTag((PhpTemplateTag)node);
				}
				
				
				startIndex = currentPos+1; 
				
			}
			next();
		}
		if(startIndex < html.length() && !html.substring(startIndex).trim().isEmpty()) {
			checkInitSimpleTemplate();
			addTextNodeToEnd(startIndex, result);
			
		}
	}

	private boolean checkSkipHtmlComment() throws IOException {
		if (currSubstrEquals(HTML_COMMENT_START)) {
			currentPos = html.indexOf(HTML_COMMENT_END, currentPos + HTML_COMMENT_START.length()) + HTML_COMMENT_END.length() - 1;
			return true;
		}
		return false;
	}
	
	protected boolean isCurrTagPhpBeginPreprocessor() {
		int startIndexCodeSection = currentPos + HtmlParser.PHP_CODE_TAG.length();
		for (int k = startIndexCodeSection; k < html.length(); k++) {
			switch (html.charAt(k)) {
			case ' ':
			case '\t':
			case '\n':
			case '\r':
			case '/':
				continue;
			case '#':
				return true;
			default:
				return false;
			}
		}
		return false;
	}

	private void addTextNode(AttrValue val,int startIndex) {
		String text = html.substring(startIndex,currentPos);
		if (!text.isEmpty())
			val.addElement(new TextAttrValueElement(text));
	}
	
	protected HtmlAttr parseAttr() throws IOException {
		Pair<String, Integer> pEq = ParseUtil.getIndexAndSubstrToNextChar(html, currentPos, '=');
		int indexEq = pEq.getValue2();
		String attrName = pEq.getValue1().trim();
		if (!attrName.matches("[a-zA-Z-]+")) {
			String[] arr = attrName.split("\\s");
			
			if(arr[0].matches("[a-zA-Z-]+")) {
				currentPos += arr[0].length();
				return new EmptyHtmlAttr(arr[0]);
			}
			
			throw new IOException(String.format("syntax error. Attr name [%s] must match [a-zA-Z-]+", attrName));
		}
		Pair<Integer, Character> pQuot = ParseUtil.firstIndexOf(html, '\"', '\'', indexEq);
		int indexQuot = pQuot.getValue1();
		if (indexQuot == -1) {
			throw new IOException("syntax error. Expected single or double quote");
		}
		setPos(indexQuot + 1);
		AttrValue val = new AttrValue();
		int startIndex = currentPos;
		while(!atEnd()) {
			if(currSubstrEquals(HtmlParser.PHP_CODE_TAG)) {
				addTextNode(val, startIndex);
				
				val.addElement(parseCodeTag());
				
				startIndex = currentPos + HtmlParser.PHP_CODE_END_TAG.length();
			} else if(currSubstrEquals( HtmlParser.PHP_INLINE_RAW_START )) {
				addTextNode(val, startIndex);
				
				val.addElement(parseRawOutputSection());
				
				startIndex = currentPos+PHP_INLINE_RAW_START.length();
			} else if(currChar() == HtmlParser.PHP_INLINE_START ) {
				addTextNode(val, startIndex);
				
				val.addElement(parseHtmlEscapedOutputSection());
				
				startIndex = currentPos+1;
			} else if(currChar() == pQuot.getValue2()) {
				addTextNode(val, startIndex);
				
				HtmlAttr attr = new HtmlAttr(attrName, val, pQuot.getValue2());
				setPos(currentPos);
				return attr ;
			}
			next();
		}
		throw new IOException("syntax error. missing closing quote | "+attrName);
		
	}

	protected HtmlTag parseNode() throws IOException {
		String namespaceTagName = ParseUtil.substrToNextChar(html, new char[] {' ', '\r', '\t', '\n', '>'}, currentPos);
		String ns = null;
		String tagName = null;
		HtmlTag tag = null;
		
		if (namespaceTagName.contains(":")) {
			String[] parts = namespaceTagName.split(":");
			ns = parts[0];
			tagName = parts[1];
			if (ns.equals(HtmlParser.PHP_TPL_NS)) {
				if (tagName.equals(PhpTemplateTag.TAG_NAME)) {
					tag = new PhpTemplateTag();
				} else if (tagName.equals(PhpRenderTemplateTag.TAG_NAME)) {
					tag = new PhpRenderTemplateTag();
				} else if (tagName.equals(PhpForTag.TAG_NAME)) {
					tag = new PhpForTag();
				} else if (tagName.equals(PhpIfTag.TAG_NAME)) {
					tag = new PhpIfTag();
				} else if (tagName.equals(PhpThenTag.TAG_NAME)) {
					tag = new PhpThenTag();
				} else if (tagName.equals(PhpElseTag.TAG_NAME)) {
					tag = new PhpElseTag();
				} else if (tagName.equals(PhpSwitchTag.TAG_NAME)) {
					tag = new PhpSwitchTag();
				} else if (tagName.equals(PhpCaseTag.TAG_NAME)) {
					tag = new PhpCaseTag();
				} else if (tagName.equals(PhpDefaultCaseTag.TAG_NAME)) {
					tag = new PhpDefaultCaseTag();
				} else if (tagName.equals(PhpCommentTag.TAG_NAME)) {
					tag = new PhpCommentTag();
				} else if (tagName.equals(PhpRenderSubtemplateTag.TAG_NAME)) {
					tag = new PhpRenderSubtemplateTag();
				} else if (tagName.equals(PhpSelect.TAG_NAME)) {
					tag = new PhpSelect();
				} else if (tagName.equals(PhpSelectOption.TAG_NAME)) {
					tag = new PhpSelectOption();
				} 
			}
		} else {
			tagName = namespaceTagName;
			if (tagName.equals(HtmlBr.TAG_NAME)) {
				tag = new HtmlBr();
				next(HtmlBr.TAG_NAME.length());
				while(currChar() != '>') {
					switch (currChar()) {
					case '/':
					case '\n':
					case '\r':
					case ' ':
					case '\t':
						break;
					default:
						throw new IOException(String.format("illegal character %s", currChar()));
					}
					next();
				}
				return tag;
			} else {
				tag=new HtmlTag(tagName);
			}
		}
		
		if (tag == null ) {
			throw new IOException("tag " +namespaceTagName + " not supported");
		}
		
		next(namespaceTagName.length());
		while(!atEnd() && currChar() != '>') {
			if(Character.isAlphabetic(currChar()) || currChar() == '-') {
				tag.addAttr(parseAttr());
			} else if (currChar() == '/') {
				tag.setSelfClosing();
				while(!atEnd() && currChar() != '>') {
					next();
				}
				return tag;
			}
			next();
		}
		
		if (HtmlTag.isVoidTag( namespaceTagName)) {
			return tag;
		}
		next();
		parseTagContent(tag,currentPos);
		return tag;
	}
	
	protected PhpCodeTag parseCodeTag() throws IOException {
		next(HtmlParser.PHP_CODE_TAG.length() + 1);
		int startIndex = currentPos;
		
		boolean quot = false;
		boolean escape = false;
		while(!atEnd()) {
			if (!quot && currSubstrEquals(HtmlParser.PHP_CODE_END_TAG)) {
				PhpCodeTag tag = new PhpCodeTag(html.substring(startIndex,currentPos));
				return tag;
			} else if(!escape && currChar() == '\"') {
				quot = !quot;
			} else if (!escape && currChar() == '\\') {
				escape = true;
			} else if (escape) {
				escape = false;
			}
			next();
		}
		PhpCodeTag tag = new PhpCodeTag(html.substring(startIndex));
		return tag;
	}
	
	protected HtmlEscapedOutputSection parseHtmlEscapedOutputSection() throws IOException {
		next();
		int startIndex = currentPos;
		
		boolean quot = false;
		boolean escape = false;
		int leftBraceCount = 0;
		while(!atEnd()) {
			if (!quot && leftBraceCount == 0 &&  currChar() == HtmlParser.PHP_INLINE_END) {
				HtmlEscapedOutputSection section = new HtmlEscapedOutputSection(html.substring(startIndex,currentPos));
				return section;
			} else if(!escape && currChar() == '\"') {
				quot = !quot;
			} else if (!escape && currChar() == '\\') {
				escape = true;
			} else if (escape) {
				escape = false;
			} else if (!quot && currChar() == HtmlParser.PHP_INLINE_START) {
				leftBraceCount++;
			} else if (!quot && currChar() == HtmlParser.PHP_INLINE_END) {
				leftBraceCount--;
			}
			next();
		}
		HtmlEscapedOutputSection section = new HtmlEscapedOutputSection(html.substring(startIndex));
		return section;
	}
	
	protected RawOutputSection parseRawOutputSection() throws IOException {
		next(HtmlParser.PHP_INLINE_RAW_START.length());
		int startIndex = currentPos;
		
		boolean quot = false;
		boolean escape = false;
		while(!atEnd()) {
			if (!quot &&  currSubstrEquals(HtmlParser.PHP_INLINE_RAW_END)) {
				RawOutputSection section = new RawOutputSection(html.substring(startIndex,currentPos));
				return section;
			} else if(!escape && currChar() == '\"') {
				quot = !quot;
			} else if (!escape && currChar() == '\\') {
				escape = true;
			} else if (escape) {
				escape = false;
			}
			next();
		}
		RawOutputSection section = new RawOutputSection(html.substring(startIndex));
		return section;
	}
	
	protected void parseTagContent(HtmlTag tag, int startIndex) throws IOException {
		while(!atEnd()) {
			if(currSubstrEquals("<php:includetemplate")) {
				
			} else if (currSubstrEquals(HtmlParser.HTML_END_TAG)) {
				addTextNode(tag,startIndex);
				startIndex = currentPos; 
				next(HtmlParser.HTML_END_TAG.length());
				if(!currSubstrEquals(tag.getNamespaceAndTagName())) {
					//System.out.println(html.substring(currentPos,currentPos+10));
					throw new IOException("end tag does not match: " + tag.getNamespaceAndTagName()+", pos "+currentPos);
				}
				next(tag.getNamespaceAndTagName().length());
				while(currChar() != '>') {
					next();
				}
				return;
			} else if(currSubstrEquals(HtmlParser.PHP_CODE_TAG)) {
				addTextNode(tag,startIndex);
				startIndex = currentPos; 
				//next();
				/*if(isCurrTagPhpBeginPreprocessor()) {
					result.addPreprocessorTag(parsePhpPreprocessorCodeSection());
				} else {*/
					tag.addChildNode(parseCodeTag());
				//}
				startIndex = currentPos+HtmlParser.PHP_CODE_END_TAG.length(); 
			} else if(currSubstrEquals( HtmlParser.PHP_INLINE_RAW_START )) {
				addTextNode( tag,startIndex);
				startIndex = currentPos; 
				tag.addChildNode(parseRawOutputSection());
				
				startIndex = currentPos+PHP_INLINE_RAW_START.length();	
			} else if(currChar() == HtmlParser.PHP_INLINE_START ) {
				addTextNode( tag,startIndex);
				startIndex = currentPos; 
				tag.addChildNode(parseHtmlEscapedOutputSection());
				
				startIndex = currentPos+1;
			} else if(currChar() == '<') {
				if (!checkSkipHtmlComment()) {
					addTextNode(tag,startIndex);
					startIndex = currentPos; 
					next();
					
					tag.addChildNode(parseNode());
				}
				startIndex = currentPos+1; 
				
			} 
			next();
		} 
	}
	
	protected TplPreprocessorTag parsePhpIncludeTag() throws IOException {
		next(TplPreprocessorTag.PHP_TPl_INCLUDE_END_TAG.length() + 1);
		int startIndex = currentPos;
		
		boolean quot = false;
		boolean escape = false;
		while(!atEnd()) {
			if (!quot && currSubstrEquals(TplPreprocessorTag.PHP_TPl_INCLUDE_END_TAG)) {
				TplPreprocessorTag tag = new TplPreprocessorTag(html.substring(startIndex,currentPos));
				next(TplPreprocessorTag.PHP_TPl_INCLUDE_END_TAG.length());
				while(!atEnd()&&currChar()!='>') {
					next();
				}
				next();
				return tag;
			} else if(!escape && currChar() == '\"') {
				quot = !quot;
			} else if (!escape && currChar() == '\\') {
				escape = true;
			} else if (escape) {
				escape = false;
			}
			next();
		}
		TplPreprocessorTag tag = new TplPreprocessorTag(html.substring(startIndex));
		return tag;
	}
	
	/*protected TplPreprocessorTag parsePhpPreprocessorCodeSection() throws IOException {
		next(HtmlParser.PHP_CODE_TAG.length() + 1);
		int startIndex = currentPos;
		
		boolean quot = false;
		boolean escape = false;
		while(!atEnd()) {
			if (!quot && currSubstrEquals(HtmlParser.PHP_CODE_END_TAG)) {
				TplPreprocessorTag tag = new TplPreprocessorTag(html.substring(startIndex,currentPos));
				next(HtmlParser.PHP_CODE_END_TAG.length());
				return tag;
			} else if(!escape && currChar() == '\"') {
				quot = !quot;
			} else if (!escape && currChar() == '\\') {
				escape = true;
			} else if (escape) {
				escape = false;
			}
			next();
		}
		TplPreprocessorTag tag = new TplPreprocessorTag(html.substring(startIndex));
		return tag;
	}*/
		
}
