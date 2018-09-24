package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import config.TemplateConfig;

public class HtmlTag extends AbstractNode{
	public static final String INPUT_TAG = "input";
	public static final String IMG_TAG = "img";
	public static final String HR_TAG = "hr";
	protected String ns;
	protected String tagName;
	protected List<HtmlAttr> attrs;
	protected boolean selfClosing;
	public static final Set<String> VOID_TAGS;
	
	static {
		VOID_TAGS = new HashSet<>();
		VOID_TAGS.add(INPUT_TAG);
		VOID_TAGS.add(HR_TAG);
		VOID_TAGS.add(IMG_TAG);
		VOID_TAGS.add(HtmlBr.TAG_NAME);
		
	};
	
	public HtmlTag(String tagName) {
		this.tagName = tagName;
		this.selfClosing = false;
	}
	
	public void setSelfClosing() throws IOException {
		this.selfClosing = true;
		if (selfClosing && childNodes != null) {
			throw new IOException("illegal state: self closing tags cannot have content");
		}
	}
	
	public String getTagName() {
		return tagName;
	}
	
	
	
	public void setNs(String ns) {
		this.ns = ns;
	}
	
	public String getNs() {
		return ns;
	}
	
	public void addAttr(HtmlAttr a) {
		if (attrs == null) {
			attrs = new ArrayList<>();
		}
		attrs.add(a);
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder("<");
		sb.append(getNamespaceAndTagName());
		if (attrs!=null) {
			for(HtmlAttr a:attrs) {
				sb.append(' ').append(a);
			}
		}
		if (selfClosing) {
			sb.append("/>");
			return sb.toString();
		}
		sb.append('>');
		if(childNodes!=null) {
			for(AbstractNode n:childNodes) {
				sb.append(n);
			}
		}
		sb.append("</").append(getNamespaceAndTagName()).append('>');
		return sb.toString();
	}


	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		directTextOutputBuffer.append("<");
		directTextOutputBuffer.append(tagName);
		
		if (attrs!=null) {
			for(HtmlAttr a:attrs) {
				a.toPhp(out,directTextOutputBuffer,cfg);
			}
		}
		
		directTextOutputBuffer.append(">");
		if (childNodes != null) { 
			for(AbstractNode n:childNodes) {
				n.toPhp(out,directTextOutputBuffer,cfg);
			}
		}
		
		if (!isVoidTag(tagName) ) {
			directTextOutputBuffer.append("</").append(tagName).append('>');
		}
	}
	
	public String getNamespaceAndTagName() {
		if (ns != null) {
			return String.format("%s:%s", ns, tagName);
		} else {
			return tagName; 
		}
	}
	
	public HtmlAttr getAttrByName(String name) {
		for(HtmlAttr a:attrs) {
			if(a.getName().equals(name)) {
				return a;
			}
		}
		throw new RuntimeException("no such attribute: "+name);
	}

	public static boolean isVoidTag(String tagName) {
		return VOID_TAGS.contains(tagName);
	}

//	protected String toCppString() {
//		StringBuilder sb=new StringBuilder("<");
//		sb.append(tagName);
//		if (attrs!=null) {
//			for(HtmlAttr a:attrs) {
//				sb.append(' ').append(a.toCpp());
//			}
//		}
//		sb.append('>');
//		if(childNodes!=null) {
//			for(AbstractNode n:childNodes) {
//				sb.append(n);
//			}
//		}
//		sb.append("</").append(tagName).append('>');
//		return sb.toString();
//	}
//	
//	public String toCpp(StringBuilder out) {
//		StringBuilder sb = new StringBuilder();
//		Util.addOutChunks(sb, toString(), HtmlParser.LINE_WIDTH);
//		return sb.toString();
//	}
}
