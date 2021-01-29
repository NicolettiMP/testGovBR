package com.nicoletti.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nicoletti.model.Arquivo;

@Repository
public interface FileRepository extends JpaRepository<Arquivo, Long>{
	
	
	@Query("SELECT new Arquivo(a.id, a.name, a.size) FROM Arquivo a ORDER BY a.uploadTime DESC")
	List<Arquivo> findAll();
	
	
}
