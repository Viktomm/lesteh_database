package com.mgul.dbrobo.repositories;

import com.mgul.dbrobo.models.Place;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlaceRepository extends MongoRepository<Place,Long> {
}
