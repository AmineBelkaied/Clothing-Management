

package com.clothing.management.dto.DeliveryCompanyDTOs;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.minidev.json.annotate.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class DeliveryResponseFirst extends DeliveryResponse {

    private int status;
    private boolean isError;
    private String message;
    private Result result;

    @JsonIgnore
    public int responseCode;
    @JsonIgnore
    public String responseMessage;

    public static class Result {
        private String barCode;
        private String link;
        private String state;

        public Result() {
        }

        public String getBarCode() {
            return barCode;
        }

        public String getLink() {
            return link;
        }

        public String getState() {
            return state;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "barCode='" + barCode + '\'' +
                    ", link='" + link + '\'' +
                    ", state='" + state + '\'' +
                    '}';
        }
    }
}