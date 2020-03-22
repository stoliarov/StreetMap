package ru.nsu.stoliarov.streetmap.service.file;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BZipFileUncompressor {
	
	private final PipedOutputStream pipedOutputStream;
	
	private final BZip2CompressorInputStream bzIn;
	
	private final PipedInputStream resultInputStream;
	
	public BZipFileUncompressor(String compressedFileName) throws IOException {
		
		InputStream fileInputStream = Files.newInputStream(Paths.get(compressedFileName));
		BufferedInputStream archiveInputStream = new BufferedInputStream(fileInputStream);
		
		resultInputStream = new PipedInputStream();
		pipedOutputStream = new PipedOutputStream(resultInputStream);
		bzIn = new BZip2CompressorInputStream(archiveInputStream);
	}
	
	/**
	 * Асинхронно начинает процесс разархивирования.
	 * Возвращает InputStream, из которого можно будет читать разархивированные данные.
	 */
	public InputStream startUncompressing() {
		
		Thread thread = new Thread(new UncompressingThread());
		thread.start();
		
		return resultInputStream;
	}
	
	private class UncompressingThread implements Runnable {
		
		private static final int BUFFER_SIZE = 8192;
		
		@Override
		public void run() {
			
			try {
				
				int n = 0;
				final byte[] buffer = new byte[BUFFER_SIZE];
				
				while(-1 != (n = bzIn.read(buffer))) {
					pipedOutputStream.write(buffer, 0, n);
				}
				
				pipedOutputStream.close();
				bzIn.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
