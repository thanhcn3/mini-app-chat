package com.example.user_service.Mapper.User;

import com.example.user_service.dto.User.Friend.ProfileResponse;
import com.example.user_service.enity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileResponse profileToProfileResponse(User user);
}
