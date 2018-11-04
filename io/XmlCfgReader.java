package io;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import config.TemplateConfig;


public class XmlCfgReader implements ContentHandler {

	protected List<TemplateConfig> xmlConfigs;
	enum Section {
		templateconfig,
		template;
	}
	TemplateConfig currentCfg;
	Section section;
	Path xmlDir;
	public XmlCfgReader(Path xmlDir) {
		xmlConfigs = new ArrayList<>();
		this.xmlDir = xmlDir;
	}
	
	@Override
	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		try {
			section = Section.valueOf(localName);
		} catch (Exception e) {
			return; 
		}
		switch (section) {
		case templateconfig:
			String srcPath = atts.getValue("src");
			if(srcPath != null) {
				
				if(srcPath.startsWith(".") || (!srcPath.contains(":/") && !srcPath.startsWith("/"))) {
					Path path = xmlDir;
					while(srcPath.startsWith("../") || srcPath.startsWith("..\\")) {
						path = path.getParent();
						srcPath = srcPath.substring(3);
					}
					TemplateConfig.setSrcPath( path.resolve(srcPath));
				} else {
					TemplateConfig.setSrcPath( Paths.get(srcPath));
				}
			} else {
				TemplateConfig.setSrcPath(this.xmlDir);
			}
			
			String destPath = atts.getValue("dest");
			if(destPath != null) {
				if(destPath.startsWith(".") || (!destPath.contains(":/") && !destPath.startsWith("/"))) {
					Path path = xmlDir;
					while(destPath.startsWith("../") || destPath.startsWith("..\\")) {
						path = path.getParent();
						destPath = destPath.substring(3);
					}
					TemplateConfig.setDestPath( path.resolve(destPath));
				} else {
					TemplateConfig.setDestPath( Paths.get(destPath));
				}
				
			} else {
				TemplateConfig.setDestPath( this.xmlDir.resolve("view"));
			}
			TemplateConfig.setNamespace(atts.getValue("namespace"));
			
			String enableBase64CssImage = atts.getValue("enabledBase64CssImages");
			if(enableBase64CssImage != null) {
				TemplateConfig.setOptionInlineCssImages(enableBase64CssImage.equals("true")||enableBase64CssImage.equals("1"));
			}
			
			break;
		case template:
			currentCfg = new TemplateConfig(); 
			
			String overrideClassname=atts.getValue("class");
			String tplName = atts.getValue("name");
			currentCfg.setClsName(overrideClassname != null ? overrideClassname : tplName+"View");
			String tplClsFile = atts.getValue("tplClsFile");
			String tplSubDir = null;
			Path tplPath = TemplateConfig.getSrcPath().resolve("Templates");
			if (tplClsFile == null) {
				tplSubDir = atts.getValue("path");
				Path tplClsFileDirectory =  TemplateConfig.getDestPath();
				if (tplSubDir != null && !tplSubDir.isEmpty()) {
					tplClsFileDirectory = tplClsFileDirectory.resolve(tplSubDir);
					tplPath = tplPath.resolve(tplSubDir);
				}
				String clsFileName = overrideClassname != null ? overrideClassname +".php" :currentCfg.getClsName() +".php"; 
				currentCfg.setTplClsFile(tplClsFileDirectory.resolve(clsFileName));
			} else {
				currentCfg.setTplClsFile(Paths.get(tplClsFile));
			}
			currentCfg.setTmplPath(tplPath.resolve(tplName+".html"));
		default:
			break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		try {
			Section section = Section.valueOf(localName);
			if (section == Section.template) {
				xmlConfigs.add(currentCfg);
			}
		} catch (Exception e) {
			return; 
		}
	
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub

	}

	public List<TemplateConfig> getXmlConfigs() {
		return xmlConfigs;
	}
}
