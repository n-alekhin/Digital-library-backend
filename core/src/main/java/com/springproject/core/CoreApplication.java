package com.springproject.core;

import com.springproject.core.Services.VectorService;
import com.springproject.core.Services.VectorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CoreApplication {
	private static final VectorService vectorService = new VectorServiceImpl();
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

		System.out.println(vectorService.getNounChunks("Non-fiction books about climate change"));
		//System.out.println("gggg");
		//SpringApplication.run(CoreApplication .class,args);
}

}
