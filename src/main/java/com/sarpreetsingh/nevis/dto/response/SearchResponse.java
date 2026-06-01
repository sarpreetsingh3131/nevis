package com.sarpreetsingh.nevis.dto.response;

import com.sarpreetsingh.nevis.dto.response.ClientResponse.ClientSearchDto;
import com.sarpreetsingh.nevis.dto.response.DocumentResponse.DocumentSearchDto;

import java.util.List;

public class SearchResponse {

    public record SearchListDto(List<ClientSearchDto> clients, List<DocumentSearchDto> documents) { }
}
