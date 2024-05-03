package com.mgul.dbrobo.repositories;

import com.mgul.dbrobo.models.Entry;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EntryRepository extends MongoRepository<Entry,String> {

    Page<Entry> findAll(Pageable pageable);

    List<Entry> findByDateForCalculationBetween(LocalDateTime start,LocalDateTime end);

    Entry findFirstByuNameAndSerial(String uName,String serial);
    List<Entry> findByuNameAndSerialAndDateForCalculationBetween(String uName, String serial, LocalDateTime start, LocalDateTime end);

    Page<Entry> findAllByuNameContainingIgnoreCaseAndSerialContainingIgnoreCase(String uName, String serial, Pageable pageable);
}
