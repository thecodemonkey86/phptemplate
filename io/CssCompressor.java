package io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class CssCompressor implements Compressor{

	String yuiCompressorExecutable;
	
	public CssCompressor(Path yuiCompressorExecutable) throws IOException {
		if(yuiCompressorExecutable == null) {
			throw new IOException("yui compressor file path missing");
		}
		this.yuiCompressorExecutable = yuiCompressorExecutable.toString();
	}
	
	@Override
	public void compress(Path input, Path output) throws IOException {
		ProcessBuilder proc = new ProcessBuilder("java","-jar", yuiCompressorExecutable,"--type","css", "-o" ,output.toAbsolutePath().toString(),input.toAbsolutePath().toString());
		Process p = proc.start();
		try {
			InputStream errorStream = p.getErrorStream();
			int r;
			while((r=errorStream.read())>-1) {
				System.out.print((char)r);
			}
			InputStream inputStream = p.getInputStream();
			while((r=inputStream.read())>-1) {
				System.out.print((char)r);
			}
			p.waitFor(30, TimeUnit.SECONDS);
			

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
