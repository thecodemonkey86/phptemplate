package util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {
	public static Path getConfigurablePath(String path, Path execPath) {
		if (path.startsWith("%EXEC_PATH%")) {
			String s = StringUtil.dropFirst(path,"%EXEC_PATH%" );
			if(s.startsWith("/") || s.startsWith("\\")) {
				s = s.substring(1);
			}
			
			if(s.length()>0)
				return execPath.resolve(s);
			return execPath;
		} else {
			return Paths.get(path);
		}
	}
}
