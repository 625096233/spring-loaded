package io.spring.loaded.monitor;

import java.io.File;
import java.util.List;


public interface FilesChangedListener {

	void onFilesChanged(List<File> files);

}
