package init;
import io.PhpOutput;
import io.CssJsProcessor;
import io.SettingsIO;
import io.XmlCfgReader;
import io.parser.HtmlParser;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import config.TemplateConfig;
import settings.Settings;
import util.Util;
import util.exception.CancelException;
import xml.reader.DefaultXMLReader;
import model.AbstractNode;
import model.PhpRenderSubtemplateTag;
import model.PhpRenderTemplateTag;
import model.ParserResult;
import model.TplPreprocessorTag;
import model.WalkTreeAction;

public class PhpTpl2 {

	private static String readUtf8(Path p) throws IOException {
		return new String(Files.readAllBytes(p),Charset.forName("UTF-8"));
	}
	
	private static void compileTemplate(Path basePath, Path repositoryPath,Settings settings, String clsName, Path templatePath, Path cppFile, Path destBasePath, Set<String> collectInlineJs, Set<String> collectInlineCss,TemplateConfig cfg) throws IOException, CancelException {
		CssJsProcessor.setBasePath(basePath);
		CssJsProcessor.setRepositoryPath(repositoryPath);
		CssJsProcessor.setSettings(settings);
		CssJsProcessor.setNoCache(false);
		PhpRenderSubtemplateTag.setBasePath(basePath);
		
		HtmlParser p=new HtmlParser();
		ParserResult result = p.parse(readUtf8(templatePath));
		
		
		if (result.isMultiTemplate()) {
			for(TplPreprocessorTag pp: result.getPreprocessorTags()) {
				ParserResult layoutResult = p.parse(readUtf8(basePath.resolve(pp.getIncludeLayoutTemplatePath())));
				result.setParentParserResult(layoutResult);
				System.out.println(layoutResult);
				layoutResult.getSimpleTemplate().walkTree(new WalkTreeAction() {
					
					@Override
					public void currentNode(AbstractNode node, ParserResult parserResult) throws IOException {
						if (node instanceof PhpRenderTemplateTag) {
							PhpRenderTemplateTag tpl = (PhpRenderTemplateTag) node;
							tpl.setRenderTmpl(result.getTemplateByName(((PhpRenderTemplateTag) node).getAttrByName("name").getStringValue()));
						}
						
					}
				}, layoutResult);				
				
				collectInlineJs.addAll(result.getAllJsIncludes());
				collectInlineCss.addAll(result.getAllCssIncludes());
			//	PhpOutput.insertCode(clsName, cppFile, layoutResult, result.getAllCssIncludes(), allJsIncludes);
				PhpOutput.writeCompiledTemplateFile(layoutResult,result, TemplateConfig.getDestPath().resolve("CompiledTemplate"), TemplateConfig.getNamespace()+"\\CompiledTemplate", clsName,cfg);
			}
			
		} else {
			collectInlineJs.addAll(result.getAllJsIncludes());
			collectInlineCss.addAll(result.getAllCssIncludes());
			//PhpOutput.insertCode(clsName, cppFile, result,  result.getAllCssIncludes(), result.getAllJsIncludes());
			PhpOutput.writeCompiledTemplateFile(result,result, TemplateConfig.getDestPath().resolve("CompiledTemplate"), TemplateConfig.getNamespace()+"\\CompiledTemplate", clsName, cfg);
		}
	}
	
	public static void main(String[] args) {
		try {
			
			//Path basePath = Paths.get("D:\\Bernhard\\netbeans_workspace\\marketplace\\public_html");
			//Path templatePath = basePath.resolve("templates\\product\\ProductList.html");
			//Path templatePath = basePath.resolve("templates\\product\\Test.html");
			//Path cppFile = Paths.get("D:\\Temp\\test.cpp");
			
			URL u=ClassLoader.getSystemClassLoader().getResource("manifest.dat");
			if (u == null) {
				throw new Exception("missing manifest.dat");
			}
			Path manifestPath = Paths.get(u.toURI());
			Path execPath = manifestPath.getParent();
			Settings settings = null;
			List<String> linesManifest = Files.readAllLines(manifestPath);
			
			//String clsName = "ProductList";
			
			for (String line : linesManifest) {
				String[] lineParts = line.split("=");
				if (lineParts.length==2){
					String key = lineParts[0].trim();
					String val = lineParts[1].trim();
					if (key.equals("settingsPath")) {
						settings = SettingsIO.loadSettings(Util.getConfigurablePath(val, execPath).resolve("settings.dat"), execPath);
					}
				}
				
			}
			if (settings == null) {
				throw new IOException("settings not loaded");
			}
			Path repositoryPath= execPath.resolve("repository");
			
			
			if (args.length >= 1) {
				
				String xmlFilePath = args[args.length - 1];
				for(int i=0;i<args.length - 1;i++) {
					if(args[i].equals("--debug")) {
						CssJsProcessor.setDebugMode(true);
					} else if(args[i].equals("--nocache")) {
						CssJsProcessor.setNoCache(true);
					}
				}
				
				Path xmlFile = Paths.get(xmlFilePath);
				XmlCfgReader handler = new XmlCfgReader(xmlFile.getParent());
				DefaultXMLReader.read(xmlFile, handler);
				
				List<TemplateConfig> xmlConfigs = handler.getXmlConfigs();
				LinkedHashSet<String> collectInlineJs = new LinkedHashSet<>();
				LinkedHashSet<String> collectInlineCss = new LinkedHashSet<>();
				for (TemplateConfig xmlConfig : xmlConfigs) {
					Path basePath = TemplateConfig.getSrcPath();
					String clsName = xmlConfig.getClsName();
					Path templatePath = xmlConfig.getTmplPath();
					Path cppFile = xmlConfig.getTplClsFile();
					compileTemplate(basePath, repositoryPath, settings, clsName, templatePath, cppFile, TemplateConfig.getDestPath(), collectInlineJs, collectInlineCss, xmlConfig );
				}
				//PhpOutput.writeCompiledTemplateFile(result, directory, namespace, clsName);
				PhpOutput.writeJsPhpFile(TemplateConfig.getDestPath().resolve("CompiledTemplate"), TemplateConfig.getNamespace()+"\\CompiledTemplate", collectInlineJs);
				PhpOutput.writeCssPhpFile(TemplateConfig.getDestPath().resolve("CompiledTemplate"), TemplateConfig.getNamespace()+"\\CompiledTemplate", collectInlineCss, TemplateConfig.isOptionInlineCssImages());
			} 
			
			
//			
//			result.toCpp(out); 

			
			
//			Files.write(Paths.get("D:\\Temp\\test.cpp"),out.toString().getBytes(Charset.forName("UTF-8"))  , StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
