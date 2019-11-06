package ie.ncirl.container.manager.app.converters;

import ie.ncirl.container.manager.common.domain.enums.Role;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RoleEnumConverter implements AttributeConverter<Role, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Role role) {
        return role.getCode();
    }

    @Override
    public Role convertToEntityAttribute(Integer code) {
        return Role.fromCode(code);
    }
}
