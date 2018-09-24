package config;
import java.nio.file.Path;


public class TemplateConfig {
	private String clsName; 
	private Path tplClsFile, tmplPath;
	private static Path srcPath, destPath;
	private static String namespace;
	private boolean renderToString;
	private String renderToStringVariableName;
	
	public String getClsName() {
		return clsName;
	}

	public void setClsName(String clsName) {
		this.clsName = clsName;
	}

	public Path getTplClsFile() {
		return tplClsFile;
	}

	public void setTplClsFile(Path tplClsFile) {
		this.tplClsFile = tplClsFile;
	}

	public Path getTmplPath() {
		return tmplPath;
	}

	public void setTmplPath(Path tmplPath) {
		this.tmplPath = tmplPath;
	}

	public static Path getSrcPath() {
		return srcPath;
	}

	public static void setSrcPath(Path srcPath) {
		TemplateConfig.srcPath = srcPath;
	}

	public static Path getDestPath() {
		return destPath;
	}

	public static void setDestPath(Path destPath) {
		TemplateConfig.destPath = destPath;
	}
	
	public static String getNamespace() {
		return namespace;
	}
	
	public static void setNamespace(String namespace) {
		TemplateConfig.namespace = namespace;
	}
	
	public String getRenderToStringVariableName() {
		return renderToStringVariableName;
	}
	
	public boolean isRenderToString() {
		return renderToString;
	}
	
	public void setRenderToString(boolean renderToString) {
		this.renderToString = renderToString;
	}
	
	public void setRenderToStringVariableName(String renderToStringVariableName) {
		this.renderToStringVariableName = renderToStringVariableName;
	}
	
}
