package io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class JsCompressor implements Compressor{

	String closureCompilerExecutable;
	
	public JsCompressor( Path closureCompilerExecutable) {
		this.closureCompilerExecutable = closureCompilerExecutable.toString();
	}
	
	@Override
	public void compress(Path input, Path output) throws IOException {
		//ProcessBuilder proc = new ProcessBuilder("java","-jar", settings.getClosureCompilerExecutable().toString(),tempJsFile.toAbsolutePath().toString(),"--js_output_file",repositoryPath.resolve("js").resolve(minJsFileName).toAbsolutePath().toString(),"--jscomp_off=uselessCode");
		ProcessBuilder proc = new ProcessBuilder("java","-jar", this.closureCompilerExecutable,input.toAbsolutePath().toString(),"--js_output_file",output.toAbsolutePath().toString(),"--jscomp_off=uselessCode");
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
