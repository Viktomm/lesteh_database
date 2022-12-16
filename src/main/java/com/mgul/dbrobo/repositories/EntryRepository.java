package com.mgul.dbrobo.repositories;

import com.mgul.dbrobo.models.Entry;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EntryRepository extends MongoRepository<Entry,String> {
    //Возможно не рабочая заготовка под дебаг монитор
    Page<Entry> findAll(Pageable pageable);

    //Возможно рабочая заготовка под отображение
    List<Entry> findByCreatedAtBetween(LocalDateTime start,LocalDateTime end);
}
