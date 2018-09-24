package io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import settings.Settings;
import util.Util;

public class SettingsIO {
	public static Settings loadSettings(Path settingsPath, Path execPath) throws IOException {
		Charset utf8 = Charset.forName("UTF-8");
		Settings s=new Settings();
		if (Files.exists(settingsPath)) {
			List<String> linesSettings = Files.readAllLines(settingsPath, utf8);
			for (String line : linesSettings) {
				String[] lineParts = line.split("=");
				if (lineParts.length==2){
					String key = lineParts[0].trim();
					String val = lineParts[1].trim();
					
					if (key.equals("closureCompiler")) {
						
						s.setClosureCompilerExecutable(Util.getConfigurablePath(val,execPath));
					} else if (key.equals("yuiCompressor")) {
						s.setYuiCompressorExecutable(Util.getConfigurablePath(val, execPath));
					}
				}
			}
		}
		return s;
	}
}
