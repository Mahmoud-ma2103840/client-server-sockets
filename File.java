package projectOS;

import java.io.Serializable;

public class File implements Serializable {
	private String fileName;
	private String filePath;
	private int wordCount;
	
	public File(String fileName, String filePath, int wordCount) {
		this.fileName = fileName;
		this.filePath = filePath;
		this.wordCount = wordCount;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	@Override
	public String toString() {
		return "File [fileName=" + fileName + ", filePath=" + filePath + ", wordCount=" + wordCount + "]";
	}
}
