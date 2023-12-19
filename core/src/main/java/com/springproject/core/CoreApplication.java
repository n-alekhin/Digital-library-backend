package com.springproject.core;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoreApplication {
	public static void main(String[] args) {
/*		EpubService epubService = new EpubService();

		try (InputStream epubStream = new FileInputStream("C:/Users/1/Desktop/Digital Library/Digital-library-backend/core/src/main/resources/Dorothy_Dixon_-_The_Night_of_the_Green_Dragon.epub")) {
			EpubDto info = epubService.extractInfoFromEpub(epubStream);
			System.out.println("/n");
			System.out.println(info.getTitle());
			System.out.println(info.getAuthors());
			System.out.println(info.getChapterContents().get(1));
		} catch (IOException e) {
			e.printStackTrace();
		}*/


		SpringApplication.run(CoreApplication .class,args);
}

}
