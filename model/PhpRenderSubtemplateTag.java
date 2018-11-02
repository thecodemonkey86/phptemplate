package model;

import io.parser.HtmlParser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import config.TemplateConfig;

public class PhpRenderSubtemplateTag extends HtmlTag {

	public static final String TAG_NAME = "renderSubtemplate" ;

	protected static Path basePath;
	protected static final Charset UTF8 = Charset.forName("UTF-8");	
	
	public PhpRenderSubtemplateTag() throws IOException {
		super(TAG_NAME);
		setNs(HtmlParser.PHP_TPL_NS);
	}

	public static void setBasePath(Path basePath) {
		PhpRenderSubtemplateTag.basePath = basePath;
	}
	
	
	@Override
	public void toPhp(StringBuilder out,StringBuilder directTextOutputBuffer,TemplateConfig cfg) {
		HtmlParser p = new HtmlParser();
		try {
			String html = new String( Files.readAllBytes(basePath.resolve("Subtemplates").resolve(getAttrByName("name").getStringValue() +".html")),UTF8 );
			ParserResult result = p.parse(html);
			result.getSimpleTemplate().toPhp(out, directTextOutputBuffer, cfg);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
}
