package com.example.testEnums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AccessRuleIssuedEnum {

    EXTERN_OUT(0, 0, 0, "走读-出校"),
    EXTERN_IN (1, 1, 0, "走读-入校"),
    BOARDER_OUT (2, 0, 1, "住宿-出校"),
    BOARDER_IN (3, 1, 1, "住宿-入校"),
    HALF_EXTERN_OUT (4, 0, 2, "半走读-出校"),
    HALF_EXTERN_IN (5, 1, 2, "半走读-入校"),
    ARTS_OUT (6, 0, 3, "体艺生-出校"),
    ARTS_IN (7, 1, 3, "体艺生-入校");

    /**
     * code
     */
    Integer code;

    /**
     * 出入类型：
     * 0:出校；
     * 1:入校
     */
    private Integer type;

    /**
     * 住校信息：
     * 0:走读；
     * 1:住宿；
     * 2:半走读；
     * 3:体艺生
     */
    private Integer accommodation;

    /**
     * desc
     */
    String desc;

    public static Integer getDescByCode(Integer type, Integer accommodation) {
        return Arrays.stream(AccessRuleIssuedEnum.values())
                .filter(value -> Objects.equals(value.getType(), type) &&  Objects.equals(value.getAccommodation(), accommodation))
                .map(AccessRuleIssuedEnum::getCode)
                .findFirst().orElse(null);
    }
}
