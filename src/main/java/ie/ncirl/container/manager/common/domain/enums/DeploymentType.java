package ie.ncirl.container.manager.common.domain.enums;

import lombok.Getter;
import lombok.ToString;

@ToString
public enum DeploymentType {



    FILL(100), SPREAD(200);

    @Getter
    private int code;

    DeploymentType(int code) {
        this.code = code;
    }

    public static DeploymentType fromCode(int code) {
        switch (code) {
            case 100:
                return DeploymentType.FILL;
            case 200:
                return DeploymentType.SPREAD;

            default:
                throw new IllegalArgumentException("Code [" + code + "] not supported.");
        }
    }


}
