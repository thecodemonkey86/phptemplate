package io;

import java.io.IOException;
import java.nio.file.Path;

public interface Compressor {
	public void compress(Path input, Path output) throws IOException;
}
