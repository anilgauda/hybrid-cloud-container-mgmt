package ie.ncirl.container.manager.common.domain.enums;

import lombok.Getter;

public enum Role {

    USER(100), GUEST(200), AGENT(300);

    @Getter
    private int code;

    Role(int code) {
        this.code = code;
    }

    public static Role fromCode(int code) {
        switch (code) {
            case 100:
                return Role.USER;
            case 200:
                return Role.GUEST;
            case 300:
                return Role.AGENT;

            default:
                throw new IllegalArgumentException("Code [" + code + "] not supported.");
        }
    }

}
