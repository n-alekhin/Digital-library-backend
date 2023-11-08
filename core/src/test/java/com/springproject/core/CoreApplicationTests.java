package com.springproject.core;

import com.springproject.core.model.Constants;
import jdk.internal.joptsimple.internal.Strings;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


//@SpringBootTest
class CoreApplicationTests {

	@Test
	void contextLoads() throws IOException {
		/*String root = System.getProperty("user.dir") + "\\";
		String path = root + Constants.storagePath + "pg59952-images-3.epub";
		Book book = (new EpubReader()).readEpub(Files.newInputStream(Paths.get(path)));
		Resource coverPage = book.getCoverImage();

		if (coverPage != null) {
			// Определите путь, по которому вы хотите сохранить страницу обложки на диск.
			String savePath = "./test.jpg"; // Пример пути и имени файла.

			try {
				// Откройте входящий поток для чтения содержимого страницы обложки.
				InputStream inputStream = coverPage.getInputStream();

				// Создайте выходной поток для записи содержимого на диск.
				OutputStream outputStream = new FileOutputStream(savePath);

				// Скопируйте данные из входящего потока в выходной поток.
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				// Закройте потоки после завершения операции.
				inputStream.close();
				outputStream.close();

				System.out.println("Страница обложки успешно сохранена на диск: " + savePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		//book.getTableOfContents().getTocReferences().forEach(r -> System.out.println(r.getTitle()));
	}

}
