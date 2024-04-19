package com.springproject.core;

import com.springproject.core.model.data.BookScore;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;


//@SpringBootTest
class CoreApplicationTests {

	@Test
	void contextLoads(){
		TreeSet<BookScore> scores = new TreeSet<>(Comparator.comparing(BookScore::getBookId));
		List<Integer> adding = new ArrayList<>();
		BookScore query = new BookScore(1, 0);
		scores.add(new BookScore(1, 1));
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			adding.add(1);
		}
		adding.parallelStream().forEach(add -> {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			synchronized (scores) {
				BookScore find = scores.ceiling(query);
				find.setScore(find.getScore() + add);
			}
		});
		System.out.println(scores.ceiling(query).getScore());
		System.out.println(System.currentTimeMillis() - start);
	}

}
