package ie.ncirl.container.manager.common.domain.enums;

import lombok.Getter;
import lombok.ToString;

@ToString
public enum AppDeployStrategy {
    VMWeighted(100), ApplicationWeighted(200);

    @Getter
    private int code;

    AppDeployStrategy(int code) {
        this.code = code;
    }

    public static AppDeployStrategy fromCode(int code) {
        switch (code) {
            case 100:
                return AppDeployStrategy.VMWeighted;
            case 200:
                return AppDeployStrategy.ApplicationWeighted;

            default:
                throw new IllegalArgumentException("Code [" + code + "] not supported.");
        }
    }



}
