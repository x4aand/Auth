package com.auth.dto.role;

import com.auth.entity.UserEntity;
import com.auth.entity.UserRoleEntity;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-19T19:37:17+0400",
    comments = "version: 1.6.0, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.5.jar, environment: Java 17.0.15 (Eclipse Adoptium)"
)
@Component
public class UserRoleMapperImpl implements UserRoleMapper {

    @Override
    public UserRoleSetResponse toResponse(UserEntity user) {
        if ( user == null ) {
            return null;
        }

        Set<UserRoleResponse> rolesResponse = null;

        rolesResponse = userRoleEntitySetToUserRoleResponseSet( user.getRoles() );

        UserRoleSetResponse userRoleSetResponse = new UserRoleSetResponse( rolesResponse );

        return userRoleSetResponse;
    }

    @Override
    public UserRoleResponse toRoleResponse(UserRoleEntity userRoleEntity) {
        if ( userRoleEntity == null ) {
            return null;
        }

        String roleName = null;
        String displayName = null;
        String description = null;

        roleName = userRoleEntity.getRoleName();
        displayName = userRoleEntity.getDisplayName();
        description = userRoleEntity.getDescription();

        UserRoleResponse userRoleResponse = new UserRoleResponse( roleName, displayName, description );

        return userRoleResponse;
    }

    protected Set<UserRoleResponse> userRoleEntitySetToUserRoleResponseSet(Set<UserRoleEntity> set) {
        if ( set == null ) {
            return null;
        }

        Set<UserRoleResponse> set1 = new LinkedHashSet<UserRoleResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( UserRoleEntity userRoleEntity : set ) {
            set1.add( toRoleResponse( userRoleEntity ) );
        }

        return set1;
    }
}
