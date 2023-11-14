package com.springproject.core.Mapper;

import com.springproject.core.Entity.BookFullInfo;
import com.springproject.core.model.ExtractBookInfo;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class FullBookMapper implements Converter<ExtractBookInfo, BookFullInfo> {
    @Override
    public BookFullInfo convert(MappingContext<ExtractBookInfo, BookFullInfo> context) {
        BookFullInfo destination = context.getDestination();
        ExtractBookInfo source = context.getSource();
        destination.setSize(source.getSize());
        destination.setLanguage(source.getLanguage());
        destination.setGenres(source.getGenres());
        destination.setDescription(source.getDescription());
        destination.setPublisher(source.getPublisher());
        return destination;
    }
}
