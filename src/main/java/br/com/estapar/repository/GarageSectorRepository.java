package br.com.estapar.repository;

import br.com.estapar.domain.entity.GarageSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GarageSectorRepository extends JpaRepository<GarageSector, String> { }
