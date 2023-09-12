package pl.jakup1998.rental.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jakup1998.rental.dto.UserDto;
import pl.jakup1998.rental.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomUserMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public CustomUserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public List<UserDto> convertToDtoList(List<User> users) {
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

}
