package ie.ncirl.container.manager.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VMDTO implements DTO {
    private Long id;
    private String name;

    private byte[] privateKey;
    private String username;
    private String host;

    private Long providerId;
}
