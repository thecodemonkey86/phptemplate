package settings;
import java.nio.file.Path;




public class Settings {
	public static final int LINE_WIDTH = 1024*1024;
	Path  closureCompilerExecutable, yuiCompressorExecutable;

	public Path getClosureCompilerExecutable() {
		return closureCompilerExecutable;
	}

	public void setClosureCompilerExecutable(Path closureCompilerExecutable) {
		this.closureCompilerExecutable = closureCompilerExecutable;
	}

	public Path getYuiCompressorExecutable() {
		return yuiCompressorExecutable;
	}

	public void setYuiCompressorExecutable(Path yuiCompressorExecutable) {
		this.yuiCompressorExecutable = yuiCompressorExecutable;
	}
	
	@Override
	public String toString() {
		return closureCompilerExecutable+"|"+yuiCompressorExecutable;
	}
	
	
	
}
