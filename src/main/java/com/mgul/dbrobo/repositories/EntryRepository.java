package com.mgul.dbrobo.repositories;

import com.mgul.dbrobo.models.Entry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntryRepository extends MongoRepository<Entry,String> {
}
