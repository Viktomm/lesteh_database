package com.mgul.dbrobo.services;

import com.mgul.dbrobo.models.Entry;
import com.mgul.dbrobo.repositories.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntryService {
    private final EntryRepository entryRepository;

    @Autowired
    public EntryService(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    public List<Entry> findAll(){
        return entryRepository.findAll();
    }

    public void insertOne(Entry entry){
        entryRepository.insert(entry);
    }

    //TODO:Тут запрос клоунский, его бы переписать через SQL
    public List<Entry> firstTenEntries() {
        return entryRepository.findAll(Sort.by(Sort.Order.desc("createdAt"))).subList(0,2);
    }
}
