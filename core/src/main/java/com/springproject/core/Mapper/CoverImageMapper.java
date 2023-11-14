package com.springproject.core.Mapper;

import com.springproject.core.Entity.CoverImage;
import com.springproject.core.model.ExtractBookInfo;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class CoverImageMapper implements Converter<ExtractBookInfo, CoverImage> {

    @Override
    public CoverImage convert(MappingContext<ExtractBookInfo, CoverImage> context) {
        CoverImage destination = context.getDestination();
        ExtractBookInfo source = context.getSource();
        destination.setMediaType(source.getMediaType());
        destination.setCoverImage(source.getCoverImage());
        return destination;
    }
}
