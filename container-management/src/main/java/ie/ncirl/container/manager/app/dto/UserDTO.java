package ie.ncirl.container.manager.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {

    private String username;

    private String password;

    private String confirmPassword;

    private Integer roleCode;
}
