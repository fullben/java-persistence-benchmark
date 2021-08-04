package de.uniba.dsg.jpb.server.repositories;

import de.uniba.dsg.jpb.model.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {}
