package com.zerock;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerock.domain.Board;
import com.zerock.domain.QBoard;
import com.zerock.persistence.BoardRepository;

import ch.qos.logback.core.net.SyslogOutputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Boot03ApplicationTests {

	@Test
	public void contextLoads() {
	}
	
	@Autowired
	private BoardRepository repo;
	
	//insert Test
	@Test
	public void testInsert200() {
		for (int i=1; i<=200; i++) {
			Board board = new Board();
			board.setTitle("제목 " + i);
			board.setContent("내용 " + i + " 채우기");
			board.setWriter("user0"+i);
			repo.save(board);
		}
	}
	
	@Test
	public void testFindBoardByTitle() {
		
		List<Board> list = repo.findBoardByTitle("제목 100");
		for(int i=0, len = list.size(); i < len; i++) {
			System.out.println(list.get(i));
		}
		
		repo.findBoardByTitle("제목 100").forEach(board -> System.out.println(board));
		
	}
	
	@Test
	public void testByWriter() {
		repo.findByWriter("user07").forEach(board -> System.out.println(board));
	}
	
	@Test
	public void testByWriterContaining() {
		repo.findByWriterContaining("user01").forEach(board -> System.out.println(board));
	}
	
	@Test
	public void testByTitleContainingOrContentContaining () {
		System.out.println("총 카운트: "+ repo.findByTitleContainingOrContentContaining("제목 19", "내용 180").size());
		repo.findByTitleContainingOrContentContaining("제목 19", "내용 180").forEach(board -> System.out.println(board));
	}
	
	@Test
	public void testByTitleContainingAndBnoGreaterThan() {
		repo.findByTitleContainingAndBnoGreaterThan("10", 50L ).forEach(board -> System.out.println(board));
	}
	
	@Test
	public void testByBnoGreaterThanOrderByBnoDesc() {
		repo.findByBnoGreaterThanOrderByBnoDesc(190L).forEach(board -> System.out.println(board));
	}
	
	@Test
	public void testByBnoGreaterThanOrderByBnoDescPaging() {
//		Pageable paging = new PageRequest(0, 10);
//		repo.findByBnoGreaterThanOrderByBnoDesc(100L, paging).forEach(board -> System.out.println(board));
		
		repo.findByBnoGreaterThanOrderByBnoDesc(100L, PageRequest.of(0,10)).forEach(board -> System.out.println(board));
	}
	
	@Test
	public void testByBnoGreaterThan() {
		repo.findByBnoGreaterThan(100L, PageRequest.of(2, 10, Sort.Direction.ASC, "bno")).forEach(board -> System.out.println(board));
	}
	
	@Test
	public void testByBnoLessThan () {
		Page<Board> result = repo.findByBnoLessThan(50L, PageRequest.of(0, 10, Sort.Direction.ASC, "bno"));
		
		System.out.println("Page Size: " + result.getSize());
		System.out.println("Total Size: " + result.getTotalPages());
		System.out.println("Total Count: " + result.getTotalElements());
		System.out.println("Next : " + result.nextPageable());
		
		List<Board> list = result.getContent();
		
		list.forEach(board -> System.out.println(board));
	}
	
	@Test
	public void testByTitle() {
		repo.findByTitle("17").forEach(board -> System.out.println(board));
	}
	
	@Test
	public void testByContent() {
		repo.findByContent("123").forEach(board -> System.out.println(board));
	}
	
	@Test
	public void testByWriter2() {
		repo.findByWriter2("user0130").forEach(board -> System.out.println(board));
	}
	
	@Test
	public void testByTitle17() {
		repo.findByTitle2("17").forEach(arr -> System.out.println(Arrays.toString(arr)));
	}
	
	@Test
	public void testByTitleNative19() {
		repo.findByTitle3("19").forEach(arr -> System.out.println(Arrays.toString(arr)));
	}
	
	@Test
	public void testByPaging() {
		repo.findByPage(PageRequest.of(0, 10)).forEach(board -> System.out.println(board));
	}
	
	@Test
	public void testPredicate() {
		
		String type = "t";
		String keyword = "17";
		
		BooleanBuilder builder = new BooleanBuilder();
		
		QBoard board = QBoard.board;
		
		if (type.equals("t")) {
			builder.and(board.title.like("%"+ keyword + "%"));
		}
		
		//bno > 0
		builder.and(board.bno.gt(0L));
		
		Page<Board> result = repo.findAll(builder, PageRequest.of(0, 10));
		
		System.out.println("Page size: " + result.getSize());
		System.out.println("Total Page: " + result.getTotalPages());
		System.out.println("Total count: " + result.getTotalElements());
		System.out.println("Next :" + result.nextPageable());
		
		List<Board> list = result.getContent();
		
		list.forEach(b -> System.out.println(b));
	}
	
    @PersistenceContext
    EntityManager em;
	
	@Test
	public void testQueryDsl() {
		String keyword = "17";
		
		JPAQueryFactory queryFactory  = new JPAQueryFactory(em);
		
		QBoard board = QBoard.board;
		queryFactory.selectFrom(board)
			.where(board.title.like("%" + 17 +"%").and(board.writer.contains("user01")).and(board.bno.gt(0L)))
			.orderBy(board.bno.asc()).fetch().forEach(b -> System.out.println(b));
	}
}

