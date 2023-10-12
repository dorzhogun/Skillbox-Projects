package main.dto;

import main.model.User;

public class UserMapper
{
    public static DtoUser list(User user)
    {
        DtoUser dtoUser = new DtoUser();
        dtoUser.setId(user.getId());
        dtoUser.setName(user.getName());
        return dtoUser;
    }
}
