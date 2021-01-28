package com.nicoletti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nicoletti.model.Arquivo;

@Repository
public interface FileRepository extends JpaRepository<Arquivo, Long>{

	
	
}
