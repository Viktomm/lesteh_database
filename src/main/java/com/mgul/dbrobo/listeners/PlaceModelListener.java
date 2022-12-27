package com.mgul.dbrobo.listeners;

import com.mgul.dbrobo.models.Place;
import com.mgul.dbrobo.services.generators.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

public class PlaceModelListener extends AbstractMongoEventListener<Place> {
    private final SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    public PlaceModelListener(SequenceGeneratorService sequenceGeneratorService) {
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Place> event) {
        super.onBeforeConvert(event);
//        if (event.getSource().getId()<1) {
//            event.getSource().setId(sequenceGeneratorService.generateSequence(Place.SEQUENCE_NAME));
//        }
    }
}
