package io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

import settings.Settings;
import shoshone.httpclient.ShoshoneClient;
import shoshone.httpclient.UrlUtil;
import util.FileUtil;
import util.exception.CancelException;

public class CssJsProcessor {
	
	protected static boolean noCache;
	protected static boolean debugMode;
	protected static Settings settings;
	protected static Path repositoryPath,basePath;
	
	public static void setDebugMode(boolean debugMode) {
		CssJsProcessor.debugMode = debugMode;
	}
	
	public static void setBasePath(Path basePath) {
		CssJsProcessor.basePath = basePath;
	}
	
	public static void setNoCache(boolean noCache) {
		CssJsProcessor.noCache = noCache;
	}
	
	public static void setRepositoryPath(Path repositoryPath) {
		CssJsProcessor.repositoryPath = repositoryPath;
	}
	
	public static void setSettings(Settings settings) {
		CssJsProcessor.settings = settings;
	}
	
	protected static Path load(String src, String ext, Compressor compressor) throws IOException, CancelException {
		String fileName = null;
		Path file = null;
		String minfileName = null;
		Path tempFile = null;
		Path originalFile = null;
		boolean isPreMinified = false;
		try {
			boolean localSrcCssNewer = false;
			if (src.startsWith("http")) {
				fileName = UrlUtil.getFileName(src);
				
				isPreMinified = fileName.endsWith(".min."+ext);
				minfileName = !debugMode ? ( isPreMinified ? fileName : FileUtil.dropExtension(fileName)+".min." + ext ) : fileName;
				if (noCache || !Files.exists(repositoryPath.resolve(ext).resolve(minfileName))) {
					tempFile = Files.createTempFile("cpptmpl", ext );
					if (!Files.exists(repositoryPath.resolve(ext))) {
						Files.createDirectories(repositoryPath.resolve(ext));
					}
					ShoshoneClient sc=new ShoshoneClient(src);
					sc.applyGetRequest(false);
					sc.download(tempFile);
					sc.closeConnection();
					originalFile = tempFile;
				} else {
					originalFile = repositoryPath.resolve(ext).resolve(minfileName);
				}
				file = repositoryPath.resolve(ext).resolve(minfileName);
			} else {
				originalFile = basePath.resolve(src);
				minfileName = FileUtil.dropExtension(originalFile.getFileName().toString())+".min." + ext;
				file = repositoryPath.resolve(ext).resolve(minfileName);
				localSrcCssNewer = Files.exists(file) ? Files.getLastModifiedTime(originalFile).compareTo(Files.getLastModifiedTime(file)) == 1 : false;
			}
			
			if(!debugMode) {
				
				if (noCache || localSrcCssNewer || !Files.exists(file)) {
					if(isPreMinified) {
						Files.move(originalFile, file, StandardCopyOption.REPLACE_EXISTING);
					} else {
						compressor.compress(originalFile, file );
					}
					
				} else if (src.startsWith("http") && isPreMinified) {
					if(tempFile != null) {
						Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);
					}
				}
			} else {
				if (src.startsWith("http")) {
					if(tempFile != null)
						Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);
					else
						System.out.println();
				} else {
					file = originalFile;
				}
			}
			
			if (!Files.exists(file)) {
				throw new IOException(String.format("error %s processing", ext));
			}
			return file;
		} catch (IOException e ) {
			throw e;
		} finally {
			if(tempFile != null) {
				Files.deleteIfExists(tempFile);
			}
		}
		
		
	}
	
	public static String getInlineCss(String cssSrc) throws IOException, CancelException {
		return replaceCssUrlsForInline(cssSrc, new String(Files.readAllBytes(loadCss(cssSrc)), StandardCharsets.UTF_8 ));
		//return new String(Files.readAllBytes(loadCss(cssSrc)), StandardCharsets.UTF_8 );
	}
	
	private static String replaceCssUrlsForInline(String path, String css) throws IOException, CancelException {
		String substr = "background-image";
		for(int i=0;i<css.length();i++) {
			if(css.regionMatches(i, substr, 0, substr.length())) {
				boolean skip = false;
				for(int j=i+substr.length();j<css.length();j++) {
					if(css.charAt(j)==':') {
						for(int k=j+1;k<css.length();k++) {
							
							switch (css.charAt(k)) {
							case ' ':
							case '\t':
							case '\n':
							case '\r':
								continue;
							default:
								String substrUrlTag = "url";
								if(css.regionMatches(k, substrUrlTag, 0, substrUrlTag.length())) {
									int urlStart = css.indexOf('"',k+substrUrlTag.length());
									int urlEnd = css.indexOf('"',urlStart+1);
									if(urlStart>-1 && urlEnd>-1) {
										String url = css.substring(urlStart+1,urlEnd);
										
										if(url.contains("://")) {
											//ShoshoneClient sc = new ShoshoneClient(url);
											//sc.applyGetRequest();
										} else {
											Path p = basePath.resolve( Paths.get( path) .getParent().resolve( url));
											System.out.println(p);
											String mime = Files.probeContentType(p);
											css = String.format("%s\"data:%s;base64,%s\"%s", css.substring(0, urlStart),mime,Base64.getEncoder().encodeToString(Files.readAllBytes(p)),css.substring(urlEnd+1));
										}
										
									} else {
										throw new IOException();
									}
									
								} else {
									skip = true;
									break;
								}
							}
							if(skip) {
								break;
							}
						}
					}
					if(skip) {
						break;
					}
				}
			}
		}
		return css;
	}
	
	public static Path loadCss(String cssSrc) throws IOException, CancelException {
		return load(cssSrc, "css", new CssCompressor(settings.getYuiCompressorExecutable()));
	}
	
	public static Path loadJs(String jsSrc) throws IOException, CancelException {
		return load(jsSrc, "js", new JsCompressor(settings.getClosureCompilerExecutable()));
	}
}
