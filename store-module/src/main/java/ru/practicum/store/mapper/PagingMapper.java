package ru.practicum.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Slice;
import ru.practicum.store.dto.response.Paging;

@Mapper(componentModel = "spring")
public interface PagingMapper {

    @Mapping(target = "pageNumber", source = "number")
    @Mapping(target = "pageSize", source = "size")
    @Mapping(target = "isLast", source = "last")
    Paging toDTO(Slice<?> slice);
}
