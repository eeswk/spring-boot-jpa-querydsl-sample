package com.zerock.persistence;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.zerock.domain.Board;
//CrudRepository
//QuerydslPredicateExecutor<T>
public interface BoardRepository extends CrudRepository<Board, Long>, QuerydslPredicateExecutor<Board> {
	
	public List<Board> findBoardByTitle(String title);
	
	public Collection<Board> findByWriter(String writer);
	
	public Collection<Board> findByWriterContaining(String writer);
	
	public Collection<Board> findByTitleContainingOrContentContaining(String title, String content);
	
	public Collection<Board> findByTitleContainingAndBnoGreaterThan(String title, Long num);
	
	public Collection<Board> findByBnoGreaterThanOrderByBnoDesc(Long bno);
	
	public List<Board> findByBnoGreaterThanOrderByBnoDesc(Long bno, Pageable paging);
	
	public List<Board> findByBnoGreaterThan(Long bno, Pageable paging);
	
	//페이징 처리
	public Page<Board> findByBnoLessThan(Long bno, Pageable paging);
	
	@Query("select b from Board b where b.title like %?1% and b.bno > 0 order by b.bno desc")
	public List<Board> findByTitle(String title);
	
	
	@Query("select b from Board b where b.content like %:content% and b.bno > 0 order by b.bno desc")
	public List<Board> findByContent(@Param("content") String content);

	@Query("select b from #{#entityName} b where b.writer like %?1% and b.bno > 0 order by b.bno desc")
	public List<Board> findByWriter2(String writer);
	
	@Query("select b.bno, b.title, b.writer "
			+ "from Board b where b.writer like %?1% and b.bno > 0 order by b.bno desc")
	public List<Object []> findByTitle2(String title);
	
	//nativeQuery
	@Query(value="select bno, title, writer from tbl_boards where title like CONCAT('%', ?1, '%') and bno > 0 order by bno desc", nativeQuery=true )
	public List<Object []> findByTitle3(String title);
	
	@Query("select b from Board b where b.bno > 0 order by b.bno desc")
	public List<Board> findByPage(Pageable paging);

		
}
