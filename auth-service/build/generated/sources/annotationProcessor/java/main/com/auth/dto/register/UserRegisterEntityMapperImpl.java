package com.auth.dto.register;

import com.auth.entity.UserEntity;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-19T19:37:17+0400",
    comments = "version: 1.6.0, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.5.jar, environment: Java 17.0.15 (Eclipse Adoptium)"
)
@Component
public class UserRegisterEntityMapperImpl implements UserRegisterEntityMapper {

    @Override
    public UserEntity toEntity(UserRegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setUsername( request.username() );
        userEntity.setPassword( request.password() );
        userEntity.setEmail( request.email() );

        return userEntity;
    }

    @Override
    public UserRegisterResponse toResponse(UserEntity user) {
        if ( user == null ) {
            return null;
        }

        UUID uuid = null;
        String username = null;
        String email = null;

        uuid = user.getUuid();
        username = user.getUsername();
        email = user.getEmail();

        UserRegisterResponse userRegisterResponse = new UserRegisterResponse( uuid, username, email );

        return userRegisterResponse;
    }
}
