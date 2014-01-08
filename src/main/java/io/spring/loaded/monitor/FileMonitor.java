package io.spring.loaded.monitor;

import java.io.File;


public interface FileMonitor {

	void monitor(File file);

	void addFilesChangedListener(FilesChangedListener fileChangeListener);

}
