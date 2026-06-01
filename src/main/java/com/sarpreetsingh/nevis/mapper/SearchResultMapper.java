package com.sarpreetsingh.nevis.mapper;

import com.sarpreetsingh.nevis.dto.response.SearchResponse.SearchListDto;
import com.sarpreetsingh.nevis.util.SearchResultWrapper;

public class SearchResultMapper {

    public static SearchListDto toDto(SearchResultWrapper searchResultWrapper) {
        return new SearchListDto(
                ClientMapper.toListSearchDto(searchResultWrapper.getClients()),
                DocumentMapper.toListSearchDto(searchResultWrapper.getDocuments()));
    }
}
